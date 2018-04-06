package de.smac.smaccloud.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.michael.easydialog.EasyDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.AnnouncementListViewAdapter;
import de.smac.smaccloud.adapter.ChannelUserListAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.ChannelsFragment;
import de.smac.smaccloud.fragment.RecentActivitiesFragment;
import de.smac.smaccloud.fragment.SettingsFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.GenericHelperForRetrofit;
import de.smac.smaccloud.helper.InterfaceChannelCreated;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelUser;
import de.smac.smaccloud.model.CreateChannnelModel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.DownloadService;
import de.smac.smaccloud.service.FCMMessagingService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

/**
 * Main activity class
 */
public class DashboardActivity extends Activity implements SettingsFragment.InterfaceChangeLanguage
{
    /* newly added */
    Dialog customDialogCteateChannel;
    public InterfaceChannelCreated interfaceChannelCreated;
    /* newly added */
    private PreferenceHelper prefManager;
    private static ActionBarDrawerToggle drawerToggle;
    public LinearLayout parentLayout;
    public NavigationView navigationDashboard;
    public EasyDialog notificationDialog;
    private DrawerLayout drawerLayout;
    private TextView textviewNavigationHeader;
    private int currentNavigationItem = 0;
    private NavigationView.OnNavigationItemSelectedListener menuItemCallback;
    private ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void disableDrawerNavigation()
    {
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        prefManager = new PreferenceHelper(context);
        Helper.retainOrientation(DashboardActivity.this);
        User user = new User();
        UserPreference userPreference = new UserPreference();
        user.id = PreferenceHelper.getUserContext(context);
        try
        {
            user.populateUsingId(context);
            userPreference.userId = user.id;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        textviewNavigationHeader.setText(user.name + "\n" + user.email);
        navigate(R.id.menuChannels, false);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (getIntent() != null && getIntent().getExtras() != null && !getIntent().getExtras().isEmpty())
        {
            if (getIntent().getExtras().containsKey(FCMMessagingService.KEY_NOTIFICATION_DATA))
            {
                try
                {
                    JSONObject jsonNotificationData = new JSONObject(getIntent().getExtras().getString(FCMMessagingService.KEY_NOTIFICATION_DATA));
                    if (jsonNotificationData.has(FCMMessagingService.KEY_DATA_ACTION_TYPE) && jsonNotificationData.optString(FCMMessagingService.KEY_DATA_ACTION_TYPE).equals(String.valueOf(FCMMessagingService.ACTION_TYPE_CONTENT_UPDATED)))
                    {
                        askForSync();

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyTheme();
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDashboard = (NavigationView) findViewById(R.id.navigationDashboard);

        textviewNavigationHeader = (TextView) navigationDashboard.getHeaderView(0).findViewById(R.id.labelNavigationHeader);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, 0);
            }
        };

        applyTheme();
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();

        menuItemCallback = new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                navigate(item.getItemId(), false);
                return true;
            }
        };
        navigationDashboard.setNavigationItemSelectedListener(menuItemCallback);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void applyTheme()
    {
        updateParentThemeColor();
        //change actionbar color
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
        }
        if (toolbar != null)
        {
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }

        if (drawerToggle != null)
        {
            DrawerArrowDrawable drawerArrowDrawable = drawerToggle.getDrawerArrowDrawable();
            //drawerArrowDrawable.setColor(Color.GRAY);
            drawerArrowDrawable.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.MULTIPLY);
            drawerToggle.setDrawerArrowDrawable(drawerArrowDrawable);
        }

        if (navigationDashboard != null && navigationDashboard.getMenu() != null)
        {
            Helper.setupTypeface(textviewNavigationHeader, Helper.robotoRegularTypeface);
            applyTypefaceToNavigationDrawer();

            //change channel icon color
            navigationDashboard.getMenu()
                    .findItem(R.id.menuChannels)
                    .getIcon()
                    .setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);


            //change recent icon color
            navigationDashboard.getMenu()
                    .findItem(R.id.menuActivities)
                    .getIcon()
                    .setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);

            //change setting icon color
            navigationDashboard.getMenu()
                    .findItem(R.id.menuSettings)
                    .getIcon()
                    .setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * Redirect to navigation menu
     *
     * @param id
     * @param addToBackStack
     */
    private void navigate(int id, boolean addToBackStack)
    {
        if (id != currentNavigationItem)
        {
            switch (id)
            {
                case R.id.menuChannels:

                    navigateToFragment(R.id.layoutFrame, new ChannelsFragment(), getSupportFragmentManager().getBackStackEntryCount() != 0);
                    actionBar.setTitle(getString(R.string.label_channels));
                    drawerLayout.closeDrawer(navigationDashboard);
                    break;

                case R.id.menuActivities:
                    navigateToFragment(R.id.layoutFrame, new RecentActivitiesFragment(), true);
                    actionBar.setTitle(getString(R.string.label_recent));
                    drawerLayout.closeDrawer(navigationDashboard);
                    break;
                case R.id.menuAnnouncements:
                    actionBar.setTitle(getString(R.string.announcements));
                    break;
                case R.id.menuLogout:
                    drawerLayout.closeDrawer(navigationDashboard);
                    actionBar.setTitle(getString(R.string.lable_sign_out));
                    buildDialog(R.style.DialogAnimation, getString(R.string.sign_out_message));
                    break;

                case R.id.menuSettings:
                    navigateToFragment(R.id.layoutFrame, new SettingsFragment(), true);
                    actionBar.setTitle(getString(R.string.settings));
                    drawerLayout.closeDrawer(navigationDashboard);
                    break;
            }
            currentNavigationItem = id;
        }
        else
        {
            drawerLayout.closeDrawer(navigationDashboard);
        }
    }


    private void buildDialog(int animationSource, String type)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.label_sign_out));
        builder.setIcon(R.drawable.ic_signout);
        builder.setMessage(getString(R.string.sign_out_message));
        builder.setMessage(type);
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        PreferenceHelper.removeUserContext(context);
                        Intent loginActivity = new Intent(getApplicationContext(), IntroScreenActivity.class);
                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginActivity);
                        finish();
                    }
                });
        builder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = animationSource;

        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.download_All:
                Helper.downloadAllFiles(DashboardActivity.this, true);
                break;
            case R.id.action_notifications:

                View notificationListView = getLayoutInflater().inflate(R.layout.dialog_notification_list, null);
                notificationListView.setLayoutParams(new RelativeLayout.LayoutParams((int) (Helper.getDeviceWidth(this) / 1.5), ViewGroup.LayoutParams.WRAP_CONTENT));
                ListView lstNotifications = (ListView) notificationListView.findViewById(R.id.lstNotifications);
                try
                {
                    ArrayList<Announcement> announcements = new ArrayList<>();
                    DataHelper.getAnnouncementData(context, announcements);

                    if (!announcements.isEmpty())
                    {
                        AnnouncementListViewAdapter announcementListViewAdapter = new AnnouncementListViewAdapter(this, announcements);
                        lstNotifications.setAdapter(announcementListViewAdapter);

                        notificationDialog = new EasyDialog(this);
                        notificationDialog.setLayout(notificationListView)
                                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white1))
                                .setTouchOutsideDismiss(true)
                                .setMatchParent(false);
                        if (toolbar != null && toolbar.findViewById(R.id.action_notifications) != null)
                            notificationDialog.setLocationByAttachedView(toolbar.findViewById(R.id.action_notifications));
                        notificationDialog.show();
                    }
                    else
                    {
                        if (toolbar != null && toolbar.findViewById(R.id.action_notifications) != null)
                        {
                            View announcementView = toolbar.findViewById(R.id.action_notifications);
                            announcementView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_shaking));
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
            case R.id.action_search:
                startActivity(new Intent(DashboardActivity.this, MediaSearchActivity.class));
                break;
            case R.id.action_create_channel:
                showCreateChannelDialog();
                break;
        }
        return true;
    }

    /**
     * Start forgot activity
     */
    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            {
                //actionBar.setDisplayHomeAsUpEnabled(false);
                //drawerToggle.setDrawerIndicatorEnabled(true);

                navigationDashboard.getMenu().findItem(R.id.menuChannels).setCheckable(true).setChecked(true);
                currentNavigationItem = R.id.menuChannels;
                actionBar.setTitle(getString(R.string.channels));
            }
            else
            {
                Fragment fragment = getFragment();
                setTitleFromFragment(fragment);
            }
        }
        else
        {
            super.onBackPressed();
        }
        //}
    }

    public void setTitleFromFragment(Fragment fragment)
    {
        if (fragment.getClass().getName().equals(ChannelsFragment.class.getName()))
        {
            actionBar.setTitle(getString(R.string.channels));
            currentNavigationItem = R.id.menuChannels;
        }
        else if (fragment.getClass().getName().equals(SettingsFragment.class.getName()))
        {
            actionBar.setTitle(getString(R.string.settings));
            currentNavigationItem = R.id.menuSettings;
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_search) != null)
                toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_notifications) != null)
                toolbar.getMenu().findItem(R.id.action_notifications).setVisible(false);
        }
        else if (fragment.getClass().getName().equals(RecentActivitiesFragment.class.getName()))
        {
            actionBar.setTitle(getString(R.string.label_recent));
            currentNavigationItem = R.id.menuActivities;
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_search) != null)
                toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_notifications) != null)
                toolbar.getMenu().findItem(R.id.action_notifications).setVisible(false);
        }
    }

    private Fragment getFragment()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.layoutFrame);
        return fragment;
    }

    public void updateNavigationMenuString()
    {
        navigationDashboard.getMenu().removeGroup(R.id.menuDashboard);
        navigationDashboard.inflateMenu(R.menu.menu_activity_dashboard);
        navigationDashboard.getMenu().findItem(R.id.menuSettings).setCheckable(true).setChecked(true);

    }

    public void applyTypefaceToNavigationDrawer()
    {
        Menu m = navigationDashboard.getMenu();
        if (m != null)
        {
            for (int i = 0; i < m.size(); i++)
            {
                MenuItem mi = m.getItem(i);

                //for applying a font to subMenu ...
                SubMenu subMenu = mi.getSubMenu();
                if (subMenu != null && subMenu.size() > 0)
                {
                    for (int j = 0; j < subMenu.size(); j++)
                    {
                        MenuItem subMenuItem = subMenu.getItem(j);
                        applyFontToMenuItem(subMenuItem);
                    }
                }

                //the method we have create in activity
                applyFontToMenuItem(mi);
            }
        }
    }

    private void applyFontToMenuItem(MenuItem mi)
    {
        Typeface font = Helper.getCurrentTypeface();
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    public void changeLangaugeMenu(String lang)
    {
        Menu menu = navigationDashboard.getMenu();
        menu.findItem(R.id.menuChannels).setTitle(getString(R.string.channels));
        menu.findItem(R.id.menuActivities).setTitle(getString(R.string.activities));
        menu.findItem(R.id.menuAnnouncements).setTitle(getString(R.string.announcements));
        menu.findItem(R.id.menuSettings).setTitle(getString(R.string.settings));
        menu.findItem(R.id.menuLogout).setTitle(getString(R.string.label_sign_out));
        actionBar.setTitle(getString(R.string.settings));
    }


    @Override
    public void changeLanguage(String lang)
    {
        changeLangaugeMenu(lang);
    }

    public class CustomTypefaceSpan extends TypefaceSpan
    {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type)
        {
            super(family);
            newType = type;
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf)
        {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null)
            {
                oldStyle = 0;
            }
            else
            {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0)
            {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0)
            {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }

        @Override
        public void updateDrawState(TextPaint ds)
        {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint)
        {
            applyCustomTypeFace(paint, newType);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e(" @@@@@ "," on pause is called : ");
        final ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
        DataHelper.getAllDownloadList(context, arraylistDownloadList);
        if(arraylistDownloadList != null && arraylistDownloadList.size() > 0)
        {
            for (int i = 0; i < arraylistDownloadList.size(); i++)
            {
                try
                {
                    if (arraylistDownloadList.get(i).isDownloading == 1)
                    {
                        Media tempMedia = new Media();
                        tempMedia.id = arraylistDownloadList.get(i).mediaId;
                        DataHelper.getMedia(context, tempMedia);
                        tempMedia.isDownloading = 0;
                        DataHelper.updateMedia(context, tempMedia);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        DownloadService.isDownloading = false;
        prefManager.saveFullDownloadMedia(false);
    }

    @Override
    protected void onPause()
    {
        super.onPause();


    }
    TextView textviewCancel,textviewSave;
    EditText editTextChannelName;
    Button btnChoose,btnSelectAll;
    RecyclerView recyclerViewUser;
    ImageView imageViewChannelBackground;
    List<CreateChannnelModel> arrayListUser = new ArrayList<>();
    ChannelUserListAdapter adapterUserList;
    String strChannelName="";
    Bitmap bitmapSelected = null;
    String imagePathFromGalleryOrCamera ="";
    public static final int REQUEST_GET_USERLIST = 501;
    public static final int REQUEST_CREATE_CHANNEL = 502;
    public static final int REQUEST_ADD_CHANNEL_USER = 503;

    private RecyclerView.LayoutManager listManager;
    private void showCreateChannelDialog(){

        // Create custom dialog object
        customDialogCteateChannel = new Dialog(DashboardActivity.this);
        // Include dialog.xml file
        customDialogCteateChannel.setContentView(R.layout.activity_create_channel);
        customDialogCteateChannel.setCancelable(false);

        imagePathFromGalleryOrCamera="";
        // set values for custom dialog components - text, image and button
        textviewSave = (TextView) customDialogCteateChannel.findViewById(R.id.txt_save);
        textviewCancel = (TextView) customDialogCteateChannel.findViewById(R.id.txt_cancel);

        btnChoose = (Button) customDialogCteateChannel.findViewById(R.id.btn_choose);
        btnSelectAll = (Button) customDialogCteateChannel.findViewById(R.id.btn_select_all);
        recyclerViewUser = (RecyclerView) customDialogCteateChannel.findViewById(R.id.recycler_view_user);

        listManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerViewUser.setLayoutManager(listManager);

        imageViewChannelBackground = (ImageView) customDialogCteateChannel.findViewById(R.id.image_background);
        editTextChannelName = (EditText) customDialogCteateChannel.findViewById(R.id.edittext_channel);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result=checkPermission();
                if(result) {
                    selectImage(DashboardActivity.this);
                }
            }
        });
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListUser.size() > 0) {
                    for (int i = 0; i < arrayListUser.size(); i++) {
                        CreateChannnelModel modelTemp = arrayListUser.get(i);
                        modelTemp.setSelected(true);
                        arrayListUser.set(i,modelTemp);
                    }
                    adapterUserList.notifyDataSetChanged();
                }
            }
        });
        textviewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogCteateChannel.cancel();
            }
        });
        textviewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isNetworkAvailable(context))
                {
                    strChannelName = editTextChannelName.getText().toString();
                    if(!TextUtils.isEmpty(strChannelName)){
                        try
                        {
                            File file;
                            if(!TextUtils.isEmpty(imagePathFromGalleryOrCamera)) {
                                file = new File(imagePathFromGalleryOrCamera);
                            }else {
                                file = Helper.createFileFromDrawable(context,R.drawable.icon_default);
                            }

                            GenericHelperForRetrofit helper = new GenericHelperForRetrofit(DashboardActivity.this);
                            try {
                                String strPayload = "";

                                JSONObject main = new JSONObject();
                                main.put("Action",DataProvider.Actions.ACTION_CREATE_CHANNEL);

                                JSONObject payload = new JSONObject();

                                payload.put("Name",strChannelName);
                                payload.put("UserId",String.valueOf(PreferenceHelper.getUserContext(context)));
                                payload.put("Org_Id",PreferenceHelper.getOrganizationId(context));

                                main.put("Payload",payload);

                                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                                // MultipartBody.Part is used to send also the actual file name
                                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                                Call<String> call = helper.getRetrofit().createChannel(main.toString(),body);

                                call.enqueue(new Callback<String>() {

                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.e(" success ", " response from retro : "+response);

                                        //if(status){
                                        try {
                                            JSONObject jsonObjectMain = new JSONObject(response.body());

                                            int requestStatus = jsonObjectMain.optInt("Status");
                                            if (requestStatus > 0)
                                            {
                                                notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                                            }
                                            else {
                                                Channel modelChannel = new Channel();
                                                ArrayList<Channel> arraylistChannels = new ArrayList<>();

                                                JSONObject payload = jsonObjectMain.getJSONObject("Payload");
                                                String orgId = payload.getString("Org_Id");
                                                String channelId = payload.getString("Id");
                                                JSONObject objCreater = new JSONObject();
                                                objCreater = payload.getJSONObject("Creator");

                                                // getting channles list from service and storing in database
                                                Channel.parseListFromJsonObject(payload, arraylistChannels);
                                                for (Channel channel : arraylistChannels)
                                                {
                                                    channel.add(context);
                                                }


                                                JSONArray jsonArray = new JSONArray();
                                                JSONObject object = new JSONObject();
                                                object.put("Id",String.valueOf(PreferenceHelper.getUserContext(context)));
                                                jsonArray.put(object);
                                                if(arrayListUser != null && arrayListUser.size() > 0){
                                                    for (int i = 0; i < arrayListUser.size() ; i++) {
                                                        if(arrayListUser.get(i).isSelected()){
                                                            JSONObject objId = new JSONObject();
                                                            objId.put("Id",arrayListUser.get(i).getId());
                                                            jsonArray.put(objId);
                                                        }
                                                    }
                                                }
                                                postNetworkRequest(REQUEST_ADD_CHANNEL_USER, DataProvider.ENDPOINT_ADD_USER,
                                                        DataProvider.Actions.ACTION_ADD_CHANNEL_USER,
                                                        RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                                        RequestParameter.urlEncoded("ChannelId", channelId),
                                                        RequestParameter.jsonArray("Users", jsonArray),
                                                        RequestParameter.urlEncoded("Org_Id", orgId),
                                                        RequestParameter.urlEncoded("DeviceId", "00000-00000-00000-00000-00000"));


                                            }
                                        }catch (Exception e){
                                            notifySimple(getString(R.string.msg_invalid_response_from_server));
                                        }
                                        //}

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.e(" fail ", " response from retro : "+t.toString());
                                        notifySimple(getString(R.string.msg_invalid_response_from_server));
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                notifySimple(getString(R.string.msg_invalid_response_from_server));

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            notifySimple(getString(R.string.msg_invalid_response_from_server));
                        }
                    }else {
                        notifySimple(getString(R.string.msg_please_enter_channel_name));
                    }

                }else {
                    notifySimple(getString(R.string.msg_please_check_your_connection));
                }
            }
        });

        if(arrayListUser != null && arrayListUser.size() > 0){
            adapterUserList = new ChannelUserListAdapter(this,arrayListUser);
            recyclerViewUser.setAdapter(adapterUserList);
        }

        customDialogCteateChannel.show();
        Window window = customDialogCteateChannel.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @Override
    protected void onNetworkReady() {
        super.onNetworkReady();
        if (Helper.isNetworkAvailable(context))
        {
            try
            {
                postNetworkRequest(REQUEST_GET_USERLIST, DataProvider.ENDPOINT_GET_USER,
                        DataProvider.Actions.ACTION_GET_USER,
                        RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                        RequestParameter.urlEncoded("Keyword", "Bhavik"),
                        RequestParameter.urlEncoded("Offset", "1"),
                        RequestParameter.urlEncoded("Count", "1"),
                        RequestParameter.urlEncoded("SortBy", ""),
                        RequestParameter.urlEncoded("SortOrder", "0"),
                        RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void selectImage(final Activity activity) {
        final CharSequence[] items = { getString(R.string.camera), getString(R.string.gallery),
                getString(R.string.cancel_captial)};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        //builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.camera))) {
                    Helper.userChoosenTask = getString(R.string.camera);
                    cameraIntent();
                } else if (items[item].equals(getString(R.string.gallery))) {
                    Helper.userChoosenTask = getString(R.string.gallery);
                    galleryIntent();
                } else if (items[item].equals(getString(R.string.cancel_captial))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public  boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        Helper.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        //intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),Helper.SELECT_SINGLE_IMAGE_FROM_GALLERY);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Helper.REQUEST_CAMERA_IMAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Helper.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                selectImage(DashboardActivity.this);
                break;
        }
    }
    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePathFromGalleryOrCamera = cursor.getString(columnIndex);
        }
        //Bitmap bm=null;
        if (data != null) {
            try {
                selectedImageUri = data.getData();
                bitmapSelected = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageViewChannelBackground.setImageBitmap(bitmapSelected);
    }
    private void onCaptureImageResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePathFromGalleryOrCamera = cursor.getString(columnIndex);
        }
        //Bitmap bm=null;
        if (data != null) {
            try {
                selectedImageUri = data.getData();
                bitmapSelected = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageViewChannelBackground.setImageBitmap(bitmapSelected);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.SELECT_SINGLE_IMAGE_FROM_GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == Helper.REQUEST_CAMERA_IMAGE)
                onCaptureImageResult(data);
        }
    }
    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response) {
        super.onNetworkResponse(requestCode, status, response);

        if(requestCode == REQUEST_GET_USERLIST){
            if(status){
                try {
                    JSONObject jsonObjectMain = new JSONObject(response);

                    int requestStatus = jsonObjectMain.optInt("Status");
                    if (requestStatus > 0)
                    {
                        notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                    }
                    else {
                        // parse response here
                        JSONArray jsonArray = jsonObjectMain.getJSONArray("Payload");

                        Gson gson = new Gson();
                        String jsonOutput = jsonArray.toString();
                        Type listType = new TypeToken<List<CreateChannnelModel>>(){}.getType();
                        arrayListUser = (List<CreateChannnelModel>) gson.fromJson(jsonOutput, listType);

                    }
                }catch (Exception e){
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
        }
        else if(requestCode == REQUEST_ADD_CHANNEL_USER){
            if(status){
                try {
                    JSONObject jsonObjectMain = new JSONObject(response);

                    int requestStatus = jsonObjectMain.optInt("Status");
                    if (requestStatus > 0)
                    {
                        notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                    }
                    else {
                        JSONObject payload = jsonObjectMain.getJSONObject("Payload");

                        // getting json array of channels from service and updating in database
                        ArrayList<Channel> arraylistChannelsForUpdate = new ArrayList<>();
                        JSONArray jsonArrayChannels = payload.optJSONArray("Channels");
                        Channel.parseListFromJson(jsonArrayChannels, arraylistChannelsForUpdate);
                        for (Channel channel : arraylistChannelsForUpdate)
                        {
                            channel.saveChanges(context);
                        }

                        // getting json array of channels user and adding in database
                        JSONArray channelUsersJsonArray = payload.optJSONArray("ChannelUsers");
                        ArrayList<ChannelUser> arraylistChannelUsers = new ArrayList<>();
                        ChannelUser.parseListFromJson(channelUsersJsonArray, arraylistChannelUsers);
                        for (ChannelUser channelUser : arraylistChannelUsers)
                        {
                            channelUser.add(context);
                        }


                        interfaceChannelCreated.channelAdded();
                        if(customDialogCteateChannel.isShowing() && customDialogCteateChannel != null){
                            customDialogCteateChannel.dismiss();
                        }


                    }
                }catch (Exception e){
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
        }
    }
}