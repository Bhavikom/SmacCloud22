package de.smac.smaccloud.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.michael.easydialog.EasyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.MediaDetailActivity;
import de.smac.smaccloud.activity.ShareActivity;
import de.smac.smaccloud.activity.ShowGalleryFolderListActivity;
import de.smac.smaccloud.activity.ShowGalleryItemListActivity;
import de.smac.smaccloud.activity.UserCommentViewActivity;
import de.smac.smaccloud.adapter.MediaAdapter;
import de.smac.smaccloud.adapter.MenuDialogListViewAdapter;
import de.smac.smaccloud.adapter.SelectedMediaAdapter;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.GenericHelperForRetrofit;
import de.smac.smaccloud.helper.GridSpacingItemDecoration;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelFiles;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.SelectedMediaFromGalleryModel;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.service.DownloadFileFromURL;
import de.smac.smaccloud.service.FCMInstanceIdService;
import de.smac.smaccloud.service.FCMMessagingService;
import de.smac.smaccloud.service.SMACCloudApplication;
import de.smac.smaccloud.widgets.UserCommentDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static de.smac.smaccloud.activity.MediaActivity.REQUEST_COMMENT;
import static de.smac.smaccloud.activity.MediaActivity.REQUEST_LIKE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;


/**
 * Show arrayListMedia data
 */
public class MediaFragment extends Fragment implements DownloadFileFromURL.interfaceAsyncResponse, MediaAdapter.OnItemClickOfAdapter
{
    CharSequence[] mediaSelectionItems;
    private int position;
    ArrayList<SelectedMediaFromGalleryModel> arrayListSelectedMediaTemp;
    boolean flagToShowPopup = false;
    /* newly added code*/
    SelectedMediaAdapter adapterSelectedMedia;
    RecyclerView recyclerViewGallery;
    ImageView imgBackground;
    Bitmap bitmapSelected = null;
    String imagePathFromGalleryOrCamera ="",imagePathDefault="";
    String imageName ="";
    private PopupWindow mPopupWindow;
    public Menu mMenu;
    /* newly added code*/

    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PARENT = "extra_parent";
    public static final String EXTRA_VIEW = "extra_view";
    public static final String EXTRA_MEDIA = "extra_media";
    public static final String KEY_MEDIA_DETAIL_IS_DELETED = "isDeleted";
    public static final int REQ_IS_MEDIA_DELETED = 4001;
    public static final String FILETYPE_IMAGE = "image";
    public static final String FILETYPE_PDF = "application/pdf";
    public static final String FILETYPE_FOLDER = "folder";
    public static final String FILETYPE_AUDIO = "audio";
    public static final String FILETYPE_MP3 = "audio/mp3";
    public static final String FILETYPE_VIDEO = "video";
    public static final String FILETYPE_VIDEO_MP4 = "video/mp4";
    public static final String BROADCAST_MEDIA_DOWNLOAD_COMPLETE = "BROADCAST_MEDIA_DOWNLOAD_COMPLETE";
    public static int COMMENT_ACTIVITY_REQUEST_CODE = 1001;
    public PreferenceHelper prefManager;
    public String deviceId = "00000-00000-00000-00000-00000";
    public EasyDialog dialog;
    int mScrollState;
    DownloadFileFromURL.interfaceAsyncResponse interfaceResponse = null;
    UserCommentDialog commentDialog;
    Handler handler;
    private BroadcastReceiver broadcastReceiverToHandleDownload;
    private Media media1;
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private ArrayList<Media> arrayListMedia;
    private Channel channel;
    private User user;
    private Media mediaItem;
    private boolean isGrid = false;
    private int parentId = -1;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(getActivity());
        setHasOptionsMenu(true);
        interfaceResponse = this;
        handler = new Handler();

        broadcastReceiverToHandleDownload = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, final Intent intent)
            {
                final Intent intentLocal = intent;
                if (intent.getAction().equals(Helper.DOWNLOAD_ACTION))
                {
                    final Media mediaReceived = intentLocal.getParcelableExtra("media_object");
                    final String position = intentLocal.getStringExtra("position");
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (isAdded())
                            {
                                for (Media object : arrayListMedia)
                                {
                                    if (object.id == mediaReceived.id)
                                    {
                                        arrayListMedia.set(arrayListMedia.indexOf(object), mediaReceived);
                                        if (mediaAdapter != null)
                                        {
                                            if (mScrollState == RecyclerView.SCROLL_STATE_IDLE)
                                            {
                                                mediaAdapter.notifyItemChanged(Integer.parseInt(position), mediaReceived);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }, 0);
                }
            }
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiverToHandleDownload),
                new IntentFilter(Helper.DOWNLOAD_ACTION)
        );
        try
        {
            receiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    if (intent.hasExtra("MEDIA"))
                    {
                        final Media tempMedia = intent.getParcelableExtra("MEDIA");
                        if (checkMediaId(tempMedia))
                        {
                            updateMediaList();
                            mediaAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        updateMediaList();
                        mediaAdapter.notifyDataSetChanged();
                    }
                }
            };
            activity.registerReceiver(receiver, new IntentFilter(BROADCAST_MEDIA_DOWNLOAD_COMPLETE));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean checkMediaId(Media tempMedia)
    {
        for (int i = 0; i < arrayListMedia.size(); i++)
        {
            if (arrayListMedia.get(i).id == tempMedia.id)
                return true;
        }
        return false;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiverToHandleDownload);

    }

    @Override
    public void onDestroy()
    {
        try
        {
            context.unregisterReceiver(receiver);
        }
        catch (IllegalArgumentException iaex)
        {
            iaex.printStackTrace();
        }
        super.onDestroy();
        mediaAdapter.onPauseIsCalled();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_media, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mediaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Helper.IS_DIALOG_SHOW = true;
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        MediaAdapter.OnClickListener clickListener = new MediaAdapter.OnClickListener()
        {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(final int position, View view) throws ParseException
            {
                media1 = arrayListMedia.get(position);

                if (activity.getSupportActionBar() != null)
                {
                    activity.getSupportActionBar().setTitle(media1.name);
                }

                if ((arrayListMedia.get(position).type.equals(FILETYPE_FOLDER)))
                {
                    Fragment mediaFragment = new MediaFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(EXTRA_CHANNEL, channel);
                    arguments.putParcelable(EXTRA_MEDIA, media1);
                    arguments.putBoolean(EXTRA_VIEW, isGrid);
                    arguments.putInt(EXTRA_PARENT, media1.id);
                    mediaFragment.setArguments(arguments);
                    navigateToFragment(R.id.layoutDynamicFrame, mediaFragment, true);
                }
                else
                {
                    int[] resIds;
                    String[] menuNames;
                    if (arrayListMedia.get(position).isDownloaded == 1)
                    {
                        TypedArray ar = activity.getResources().obtainTypedArray(R.array.arr_menu_img_res_ids);
                        int len = ar.length();
                        resIds = new int[len];
                        for (int i = 0; i < len; i++)
                            resIds[i] = ar.getResourceId(i, 0);
                        ar.recycle();
                        menuNames = activity.getResources().getStringArray(R.array.arr_menu_names);
                    }
                    else
                    {
                        TypedArray ar = activity.getResources().obtainTypedArray(R.array.arr_menu_img_res_ids);
                        int len = ar.length();
                        resIds = new int[1];
                        for (int i = 0; i < 1; i++)
                            resIds[i] = ar.getResourceId(i, 0);
                        ar.recycle();
                        menuNames = new String[]{activity.getResources().getStringArray(R.array.arr_menu_names)[0]};
                    }
                    dialog = new EasyDialog(getActivity());
                    final View view1 = getActivity().getLayoutInflater().inflate(R.layout.activity_menu_dialog_list, null);
                    view1.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(getActivity()) / 2
                            , ViewGroup.LayoutParams.WRAP_CONTENT));
                    final ListView menuList = (ListView) view1.findViewById(R.id.menuList);
                    MenuDialogListViewAdapter menuListViewAdapter = new MenuDialogListViewAdapter(getActivity(), DataHelper.checkLike(activity, media1.id, user.id), resIds, menuNames);
                    menuList.setAdapter(menuListViewAdapter);

                    menuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int dialogPosition, long id)
                        {
                            if (dialogPosition == 0)
                            {
                                // info
                                Intent mediaDetails = new Intent(getActivity(), MediaDetailActivity.class);
                                mediaDetails.putExtra(EXTRA_CHANNEL, channel);
                                mediaDetails.putExtra(EXTRA_MEDIA, media1);
                                mediaDetails.putExtra(EXTRA_VIEW, isGrid);
                                mediaDetails.putExtra(EXTRA_PARENT, parentId);
                                // TODO: 10-Jan-17 Transmission Animation
                                Pair<View, String> pair1 = Pair.create(recyclerView.findViewHolderForLayoutPosition(position).itemView.findViewById(R.id.imageIcon), getString(R.string.text_transition_animation_media_image));
                                Pair<View, String> pair2 = Pair.create(recyclerView.findViewHolderForLayoutPosition(position).itemView.findViewById(R.id.labelName), getString(R.string.text_transition_animation_media_title));
                                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair1, pair2);
                                startActivityForResult(mediaDetails, REQ_IS_MEDIA_DELETED, optionsCompat.toBundle());

                            }
                            else if (dialogPosition == 1)
                            {
                                // rate
                                if (prefManager.isDemoLogin())
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(getString(R.string.disable_like_title));
                                    builder.setMessage(getString(R.string.disable_like_message));
                                    builder.setPositiveButton(getString(R.string.ok),
                                            new DialogInterface.OnClickListener()
                                            {
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    dialog.dismiss();
                                                }
                                            });

                                    AlertDialog dialog = builder.create();

                                    dialog.show();

                                }
                                else
                                {
                                    if (DataHelper.checkLike(activity, media1.id, user.id))
                                    {
                                        notifySimple(getString(R.string.msg_it_already_like_by_you));
                                    }
                                    else
                                    {
                                        if (Helper.isNetworkAvailable(context))
                                        {
                                            Helper.IS_DIALOG_SHOW = false;
                                            postNetworkRequest(REQUEST_LIKE, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_LIKE,
                                                    RequestParameter.urlEncoded("ChannelId", String.valueOf(channel.id)),
                                                    RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                                    RequestParameter.urlEncoded("MediaId", String.valueOf(media1.id)), RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))), RequestParameter.urlEncoded("DeviceId",  PreferenceHelper.getFCMTokenId(context)));
                                        }
                                        else
                                        {
                                            Helper.storeLikeOffline(activity, media1);
                                        }
                                    }
                                }
                            }
                            else if (dialogPosition == 2)
                            {
                                // comment
                                Intent userCommentIntent = new Intent(context, UserCommentViewActivity.class);
                                userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, media1);
                                userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
                                startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);

                            }
                            else if (dialogPosition == 3)
                            {
                                // share
                                user = new User();
                                user.id = PreferenceHelper.getUserContext(context);
                                Intent sharingDetails = new Intent(getActivity(), ShareActivity.class);
                                sharingDetails.putExtra(EXTRA_CHANNEL, channel);
                                sharingDetails.putExtra(EXTRA_MEDIA, media1);
                                sharingDetails.putExtra(EXTRA_VIEW, isGrid);
                                sharingDetails.putExtra(EXTRA_PARENT, parentId);
                                startActivity(sharingDetails);
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.setLayout(view1)
                            .setGravity(EasyDialog.GRAVITY_BOTTOM)
                            .setBackgroundColor(getActivity().getResources().getColor(R.color.white))
                            .setLocationByAttachedView(view)
                            .setTouchOutsideDismiss(true)
                            .setMatchParent(false)
                            .show();
                    DataHelper.updateMedia(context, arrayListMedia.get(position));
                    media1.isDownloading = arrayListMedia.get(position).isDownloading;
                    media1.isDownloaded = arrayListMedia.get(position).isDownloaded;
                }
            }

        };
        mediaAdapter.setClickListener(clickListener);
    }


    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        Bundle arguments = getArguments();

        if (arguments != null)
        {
            channel = arguments.getParcelable(EXTRA_CHANNEL);
            isGrid = arguments.getBoolean(EXTRA_VIEW);
            parentId = arguments.getInt(EXTRA_PARENT);
            if (!(parentId == -1))
            {
                mediaItem = arguments.getParcelable(EXTRA_MEDIA);

            }
            else
            {
                Media channelAsMedia = new Media();
                channelAsMedia.id = channel.id;
                channelAsMedia.name = channel.name;
                channelAsMedia.icon = channel.thumbnail;

            }

        }
        recyclerView = (RecyclerView) findViewById(R.id.listChannels);
        Glide.with(activity)
                .load(channel.thumbnail)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        recyclerView.setBackground(new BitmapDrawable(getResources(), bitmap));

                    }
                });

        if (Helper.getScreenOrientation(activity) == 1)
        {
            // Portrait Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }


        }
        else
        {
            // Landscape Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }

        }


        arrayListMedia = new ArrayList<>();
        mediaAdapter = new MediaAdapter(activity, arrayListMedia, this, recyclerView);
        mediaAdapter.setGrid(isGrid);
        recyclerView.setAdapter(mediaAdapter);
        //applyThemeColor();
        updateMediaList();

        user = new User();
        user.id = PreferenceHelper.getUserContext(context);
        applyThemeColor();
        Helper.GCM.getCloudMessagingId(activity, new Helper.GCM.RegistrationComplete()
        {
            @Override
            public void onRegistrationComplete(String registrationId)
            {
                deviceId = registrationId;
            }
        });
        new FCMInstanceIdService(context).onTokenRefresh();

        FCMMessagingService.themeChangeNotificationListener = new FCMMessagingService.ThemeChangeNotificationListener()
        {
            @Override
            public void onThemeChangeNotificationReceived()
            {
                applyThemeColor();
            }
        };
        mediaSelectionItems = new CharSequence[]{ getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel_captial)};
    }


    public void applyThemeColor()
    {

        try
        {
            activity.updateParentThemeColor();
            if (activity.getSupportActionBar() != null)
            {

                activity.getSupportActionBar().setTitle(channel.name);
                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));

                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
                if (upArrow != null)
                {
                    upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
                    activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
                }
                if (toolbar != null)
                {
                    toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
                }
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // Portrait Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
        else
        {
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    public void updateMediaList()
    {
        try
        {
            arrayListMedia.clear();
            if (parentId == -1)
            {
                DataHelper.getMediaListFromParent(context, channel.id, arrayListMedia);
                Log.e("", " arrayListMedia size : " + arrayListMedia.size());
            }
            else
            {
                DataHelper.getMediaListFromChannelId(context, parentId, arrayListMedia);
                Log.e("", " arrayListMedia size : " + arrayListMedia.size());
            }
            if (((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.size() > 0)
            {
                for (int i = 0; i < ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.size(); i++)
                {
                    for (int j = 0; j < arrayListMedia.size(); j++)
                    {
                        if (((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.get(i).isDownloading == 1 &&
                                ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.get(i).id == arrayListMedia.get(j).id)
                        {
                            arrayListMedia.set(j, ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.get(i));
                        }
                    }
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            if (activity.getSupportActionBar() != null)
            {
                if (fragmentManager.getBackStackEntryCount() == 1)
                {
                    activity.getSupportActionBar().setTitle(channel.name);
                }
                else
                {
                    activity.getSupportActionBar().setTitle(mediaItem.name);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        applyThemeColor();
    }

    public void callCommentService(String commentText)
    {
        postNetworkRequest(REQUEST_COMMENT, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_COMMENT,
                RequestParameter.urlEncoded("ChannelId", String.valueOf(channel.id)),
                RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                RequestParameter.urlEncoded("MediaId", String.valueOf(media1.id)),
                RequestParameter.urlEncoded("Comment", commentText), RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))));
    }


    @Override
    public void onItemClick(int pos, int itemPos)
    {

        media1 = arrayListMedia.get(itemPos);
        if (pos == 1)
        {
            // info
            Intent mediaDetails = new Intent(getActivity(), MediaDetailActivity.class);
            mediaDetails.putExtra(EXTRA_CHANNEL, channel);
            mediaDetails.putExtra(EXTRA_MEDIA, media1);
            mediaDetails.putExtra(EXTRA_VIEW, isGrid);
            mediaDetails.putExtra(EXTRA_PARENT, parentId);
            // TODO: 10-Jan-17 Transmission Animation
            Pair<View, String> pair1 = Pair.create(recyclerView.findViewHolderForLayoutPosition(itemPos).itemView.findViewById(R.id.imageIcon), getString(R.string.text_transition_animation_media_image));
            Pair<View, String> pair2 = Pair.create(recyclerView.findViewHolderForLayoutPosition(itemPos).itemView.findViewById(R.id.labelName), getString(R.string.text_transition_animation_media_title));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair1, pair2);
            startActivityForResult(mediaDetails, REQ_IS_MEDIA_DELETED, optionsCompat.toBundle());
        }
        else if (pos == 2)
        {
            // share
            user = new User();
            user.id = PreferenceHelper.getUserContext(context);
            Intent sharingDetails = new Intent(getActivity(), ShareActivity.class);
            sharingDetails.putExtra(EXTRA_CHANNEL, channel);
            sharingDetails.putExtra(EXTRA_MEDIA, media1);
            sharingDetails.putExtra(EXTRA_VIEW, isGrid);
            sharingDetails.putExtra(EXTRA_PARENT, parentId);
            startActivity(sharingDetails);
        }
        else if (pos == 3)
        {
            startUserCommentViewActivity();
        }
        else if (pos == 4)
        {
            // rate
            if (prefManager.isDemoLogin())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.disable_like_title));
                builder.setMessage(getString(R.string.disable_like_message));
                builder.setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();

                dialog.show();

            }
            else
            {
                if (DataHelper.checkLike(activity, media1.id, user.id))
                {
                    notifySimple(getString(R.string.msg_it_already_like_by_you));
                }
                else
                {
                    if (Helper.isNetworkAvailable(context))
                    {
                        Helper.IS_DIALOG_SHOW = false;
                        postNetworkRequest(REQUEST_LIKE, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_LIKE,
                                RequestParameter.urlEncoded("ChannelId", String.valueOf(channel.id)),
                                RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                RequestParameter.urlEncoded("MediaId", String.valueOf(media1.id)), RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))), RequestParameter.urlEncoded("DeviceId",  PreferenceHelper.getFCMTokenId(context)));
                    }
                    else
                    {
                        Helper.storeLikeOffline(activity, media1);
                    }
                }
            }
        }
    }
    private void startUserCommentViewActivity()
    {
        Intent userCommentIntent = new Intent(context, UserCommentViewActivity.class);
        userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, media1);
        userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
        startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);
    }

    public void refreshAdapter()
    {
        if (mediaAdapter != null)
        {
            mediaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if (mediaAdapter != null)
            {
                mediaAdapter.notifyDataSetChanged();
            }
        }


    }
    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_LIKE)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        Helper.storeLikeOffline(activity, media1);
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.msg_please_try_again_later));
                        }
                    }
                    else
                    {

                        JSONObject userLikeJson = responseJson.optJSONObject("Payload");
                        UserLike userLike = new UserLike();
                        if (userLikeJson != null)
                        {
                            UserLike.parseFromJson(userLikeJson, userLike);
                            if (userLike.userId > 0)
                                userLike.add(context);
                        }
                        else if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                        {
                            if (responseJson.optString("Message").equalsIgnoreCase(DataProvider.Messages.USERLIKE_OBJECT_IS_EMPTY))
                            {
                                userLike.isSynced = 1;
                                userLike.associatedId = media1.id;
                                userLike.userId = PreferenceHelper.getUserContext(context);
                                DataHelper.addUserLikes(context, userLike);
                            }
                        }
                        Log.e("", " arrayListMedia size after change : " + arrayListMedia.size());
                        updateMediaList();
                        mediaAdapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
        }
        else if (requestCode == REQUEST_COMMENT)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        Helper.storeCommentOffline(activity, media1, commentDialog.edtMediaComment.getText().toString());
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.msg_please_try_again_later));
                        }
                        if (commentDialog != null && commentDialog.isShowing())
                            commentDialog.dismiss();
                    }
                    else
                    {
                        JSONObject userCommentJson = responseJson.optJSONObject("Payload");
                        UserComment userComment = new UserComment();
                        if (userCommentJson != null)
                        {
                            UserComment.parseFromJSon(userCommentJson, userComment);
                            if (userComment.userId > 0)
                                userComment.add(context);
                        }
                        else if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                        {

                        }
                        if (commentDialog != null && commentDialog.isShowing())
                            commentDialog.dismiss();
                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
        }
        Helper.IS_DIALOG_SHOW = true;
    }



    @Override
    public void processFinish(String output, Media media, int pos)
    {
        if (mediaAdapter.dialog != null && mediaAdapter.dialog.isShowing())
            mediaAdapter.dialog.dismiss();
        mediaAdapter.notifyDataSetChanged();
    }

    @Override
    public void statusOfDownload(Media media, int pos)
    {

    }
    /* newly added code*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:

                /* on click of add button showing popup window to create folder or upload media*/
                if(flagToShowPopup == false) {
                    View view = getActivity().findViewById(R.id.action_add);
                    showPopupToCreateFolderOrMedia(view);
                    flagToShowPopup = true;
                }else {
                    mPopupWindow.dismiss();
                    flagToShowPopup = false;
                }
                break;
        }
        return true;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_media_all_download, menu);
        inflater.inflate(R.menu.menu_fragment_media, menu);
        mMenu = menu;

        if(prefManager.isAddMediaRight()){
            menu.findItem(R.id.action_add).setVisible(true);
        }else {
            menu.findItem(R.id.action_add).setVisible(false);
        }
        applyThemeColor();
        // Do something that differs the Activity's menu here
        //super.onCreateOptionsMenu(menu, inflater);
    }
    private void showPopupToCreateFolderOrMedia(View view){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_mediaadd,null);
        mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
        // Get a reference for the custom view close button
        ImageView imageViewAddFolder = (ImageView) customView.findViewById(R.id.img_create_folder);
        ImageView imageViewAddMedia = (ImageView) customView.findViewById(R.id.img_add_media);

        // Set a click listener for the popup window close button
        imageViewAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
                flagToShowPopup = false;
                imagePathFromGalleryOrCamera="";
                showCreateFolderDialog();
            }
        });
        imageViewAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
                flagToShowPopup = false;
                imagePathFromGalleryOrCamera="";
                if(ShowGalleryItemListActivity.arrayListSelectedMedia != null) {
                    ShowGalleryItemListActivity.arrayListSelectedMedia.clear();
                }
                showUploadFileDialog();
            }
        });
        mPopupWindow.showAsDropDown(view);
    }
    private void showCreateFolderDialog(){
        // Create custom dialog object
        final Dialog customDialog = new Dialog(getActivity());
        // Include dialog.xml file
        customDialog.setContentView(R.layout.custom_dialog_add_folder);
        customDialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        final EditText editTextFolderName = (EditText) customDialog.findViewById(R.id.edittext_folder_name);
        Button btnChoose = (Button)customDialog.findViewById(R.id.btn_choose);
        Button btnCreateFolder = (Button)customDialog.findViewById(R.id.btn_create_folder);
        TextView textViewCancel = (TextView)customDialog.findViewById(R.id.txt_cancel);
        imgBackground = (ImageView)customDialog.findViewById(R.id.image_background);

        //Uri path = Uri.parse("android.resource://de.smac.smaccloud/" + R.drawable.icon_default+".png");
        //Uri path = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.icon_default);
        //imagePathDefault = path.toString();

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = 1;
                boolean isPermission=checkPermission();
                if(isPermission){
                    showDialogToChooseMedia(1);
                }

            }
        });
        btnCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCreateFolderService(editTextFolderName.getText().toString(),customDialog);
            }
        });
        customDialog.show();
    }
    private void showUploadFileDialog(){
        // Create custom dialog object
        final Dialog customDialog = new Dialog(getActivity());
        // Include dialog.xml file
        customDialog.setContentView(R.layout.custom_dialog_upload_file);
        customDialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        final EditText editTextDescription = (EditText) customDialog.findViewById(R.id.edittext_description);
        final TextView textViewUploadFile = (TextView) customDialog.findViewById(R.id.txt_upload_file);
        final TextView textViewCancel = (TextView) customDialog.findViewById(R.id.txt_cancel);
        recyclerViewGallery = (RecyclerView)customDialog.findViewById(R.id.recycler_view_gallery);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        //recyclerViewGallery.setLayoutManager(mLayoutManager);
        //recyclerViewGallery.getItemAnimator().setChangeDuration(0);
        //recyclerViewGallery.addItemDecoration(new GridSpacingItemDecoration(Helper.COLUMN_OF_GRIDVIEW,Helper.COLUMN_SPACE,Helper.IS_INCLUDE_EDGE));

        if (Helper.isTablet(activity))
        {
            recyclerViewGallery.setLayoutManager(new GridLayoutManager(context, 3));
        }
        else
        {
            recyclerViewGallery.setLayoutManager(new GridLayoutManager(context, 2));
        }


        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(context, R.dimen.padding_gridview_item);
        recyclerViewGallery.addItemDecoration(itemDecoration);

        adapterSelectedMedia = new SelectedMediaAdapter(ShowGalleryItemListActivity.arrayListSelectedMedia,context);
        recyclerViewGallery.setAdapter(adapterSelectedMedia);

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.cancel();
            }
        });
        textViewUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ShowGalleryItemListActivity.arrayListSelectedMedia.size() > 0){

                    arrayListSelectedMediaTemp = new ArrayList<>();
                    arrayListSelectedMediaTemp.addAll(ShowGalleryItemListActivity.arrayListSelectedMedia);
                    int count = 0;
                    for (int i=0;i<ShowGalleryItemListActivity.arrayListSelectedMedia.size();i++){

                        callCreateFileService(ShowGalleryItemListActivity.arrayListSelectedMedia.get(i).getMediaName(),
                                editTextDescription.getText().toString(),customDialog,
                                ShowGalleryItemListActivity.arrayListSelectedMedia.get(i).getMediaBitmapPath(),
                                ShowGalleryItemListActivity.arrayListSelectedMedia.get(i).getFileType(),i);
                    }


                }else {
                    notifySimple(getString(R.string.please_select_atleast_one_media));
                }

            }
        });
        Button btnChoose = (Button)customDialog.findViewById(R.id.btn_choose);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open dialog to choose media
                position = 2;
                boolean isPermission=checkPermission();
                if(isPermission){
                    showDialogToChooseMedia(2);
                }

            }
        });
        customDialog.show();
        Window window = customDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void callCreateFolderService(String strFolderName, final Dialog customDialog){
        if(!TextUtils.isEmpty(strFolderName)){
            try {
                File file;
                if(!TextUtils.isEmpty(imagePathFromGalleryOrCamera)) {
                    file = new File(imagePathFromGalleryOrCamera);
                }else {
                    file = Helper.createFileFromDrawable(context,R.drawable.icon_default);
                }
                GenericHelperForRetrofit helper = new GenericHelperForRetrofit(getActivity());
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("Name",strFolderName);
                hashMap.put("UserId", String.valueOf(PreferenceHelper.getUserContext(context)));
                hashMap.put("Org_Id",PreferenceHelper.getOrganizationId(context));
                hashMap.put("ChannelId", String.valueOf(channel.id));
                hashMap.put("ParentId", String.valueOf(parentId));
                JSONObject jsonObject = Helper.createJsonObject(DataProvider.Actions.ACTION_CREATE_FOLDER,hashMap);

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Call<String> call = helper.getRetrofit().createFolder(jsonObject.toString(),body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e(" success ", " response from retro : "+response);
                        try {
                            JSONObject jsonObjectMain = new JSONObject(response.body());
                            JSONObject jsonObjectInner = jsonObjectMain.getJSONObject("Payload");
                            ArrayList<Channel> arraylistChannels = new ArrayList<>();
                            JSONArray channelJsonArray = jsonObjectInner.optJSONArray("Channels");
                            Channel.parseListFromJson(channelJsonArray, arraylistChannels);
                            for (Channel channel : arraylistChannels) {
                                switch (channel.syncType) {
                                    case 1:
                                        channel.add(context);
                                        break;

                                    case 2:
                                        channel.saveChanges(context);
                                        break;

                                    case 3:
                                        channel.remove(context);
                                        break;
                                }
                                addCreator(channel.creator);
                            }
                            if (jsonObjectInner.has("Media") && !jsonObjectInner.isNull("Media")) {
                                JSONArray mediaJsonArray = jsonObjectInner.optJSONArray("Media");
                                ArrayList<Media> arraylistMediallist = new ArrayList<>();
                                Media.parseListFromJson(mediaJsonArray, arraylistMediallist);
                                for (Media media : arraylistMediallist) {
                                    switch (media.syncType) {
                                        case 1:
                                            media.add(context);

                                            break;
                                        case 2:
                                            media.saveChanges(context);

                                            break;

                                        case 3:
                                            media.remove(context);
                                            break;
                                    }
                                    Log.e("Media type", media.type + media.currentVersionId);
                                    if (!(media.type.equals("folder"))) {
                                        addMediaVersion(media.currentVersion);
                                    }
                                }
                            }
                            if (jsonObjectInner.has("ChannelFiles") && !jsonObjectInner.isNull("ChannelFiles")) {
                                JSONArray channelFilesJsonArray = jsonObjectInner.optJSONArray("ChannelFiles");
                                ArrayList<ChannelFiles> arraylistChhannelFiles = new ArrayList<>();
                                ChannelFiles.parseListFromJson(channelFilesJsonArray, arraylistChhannelFiles);
                                for (ChannelFiles channelFile : arraylistChhannelFiles) {
                                    switch (channelFile.syncType) {
                                        case 1:
                                            channelFile.add(context);
                                            break;

                                        case 2:
                                            channelFile.saveChanges(context);
                                            break;

                                        case 3:
                                            channelFile.remove(context);
                                            break;
                                    }
                                }
                            }
                            updateMediaList();
                            mediaAdapter.notifyDataSetChanged();
                            customDialog.dismiss();

                        }catch (Exception e){
                            customDialog.dismiss();
                            notifySimple(getString(R.string.msg_invalid_response_from_server));
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        customDialog.dismiss();
                        notifySimple(getString(R.string.msg_invalid_response_from_server));
                    }
                });
            }catch (Exception e){
                customDialog.dismiss();
                notifySimple(getString(R.string.msg_invalid_response_from_server));
            }

        }else {
            notifySimple(getString(R.string.please_add_folder_name));
        }
    }
    private void callCreateFileService(String fileName, String strDescription, final Dialog customDialog,
                                       final String filePath, final String fileType, final int posUploadingItem){
        try {
            GenericHelperForRetrofit helper = new GenericHelperForRetrofit(getActivity());
            HashMap<String,String> hashMap = new HashMap();
            hashMap.put("Name",fileName);
            hashMap.put("UserId", String.valueOf(PreferenceHelper.getUserContext(context)));
            hashMap.put("Org_Id",PreferenceHelper.getOrganizationId(context));
            hashMap.put("ChannelId", String.valueOf(channel.id));
            hashMap.put("ParentId", String.valueOf(parentId));
            hashMap.put("Description",strDescription);
            hashMap.put("Attachable","0");

            JSONObject jsonObject = Helper.createJsonObject(DataProvider.Actions.ACTION_CREATE_FILE,hashMap);
            Call<String> call = helper.getRetrofit().createFile(jsonObject.toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e(" success ", " response from retro : "+response);
                    try {
                        JSONObject jsonObjectMain = new JSONObject(response.body());
                        JSONObject jsonObjectPayload = jsonObjectMain.getJSONObject("Payload");

                        String mediaId = jsonObjectPayload.getString("Id");

                        callUploadFileService(mediaId,filePath,customDialog,fileType,posUploadingItem);

                    }catch (Exception e){
                        customDialog.dismiss();
                        notifySimple(getString(R.string.msg_invalid_response_from_server));
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customDialog.dismiss();
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            });
        }catch (Exception e){
            customDialog.dismiss();
            notifySimple(getString(R.string.msg_invalid_response_from_server));
        }
    }
    private void callUploadFileService(final String mediaId, String filePath, final Dialog customDialog, String fileType, final int posUploadingItem){
        try {

            final File sourceLocation = new File(filePath);

            GenericHelperForRetrofit helper = new GenericHelperForRetrofit(getActivity());
            HashMap<String,String> hashMap = new HashMap();

            hashMap.put("UserId", String.valueOf(PreferenceHelper.getUserContext(context)));
            hashMap.put("Org_Id",PreferenceHelper.getOrganizationId(context));
            hashMap.put("ChannelId", String.valueOf(channel.id));
            hashMap.put("MediaId", mediaId);

            RequestBody requestFile = RequestBody.create(MediaType.parse(fileType), sourceLocation);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", sourceLocation.getName(), requestFile);

            JSONObject jsonObject = Helper.createJsonObject(DataProvider.Actions.ACTION_ADD_MEDIA_CONTENT,hashMap);
            Call<String> call = helper.getRetrofit().addMediaContent(jsonObject.toString(),body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e(" success ", " response from retro : "+response);
                    try {
                        JSONObject jsonObjectMain = new JSONObject(response.body());
                        JSONObject jsonObjectPayload = jsonObjectMain.getJSONObject("Payload");

                        ArrayList<Channel> arraylistChannels = new ArrayList<>();
                        JSONArray channelJsonArray = jsonObjectPayload.optJSONArray("Channels");
                        Channel.parseListFromJson(channelJsonArray, arraylistChannels);
                        for (Channel channel : arraylistChannels) {
                            switch (channel.syncType) {
                                case 1:
                                    channel.add(context);
                                    break;

                                case 2:
                                    channel.saveChanges(context);
                                    break;

                                case 3:
                                    channel.remove(context);
                                    break;
                            }
                            addCreator(channel.creator);
                        }
                        if (jsonObjectPayload.has("ChannelFiles") && !jsonObjectPayload.isNull("ChannelFiles")) {
                            JSONArray channelFilesJsonArray = jsonObjectPayload.optJSONArray("ChannelFiles");
                            ArrayList<ChannelFiles> arraylistChhannelFiles = new ArrayList<>();
                            ChannelFiles.parseListFromJson(channelFilesJsonArray, arraylistChhannelFiles);
                            for (ChannelFiles channelFile : arraylistChhannelFiles) {
                                switch (channelFile.syncType) {
                                    case 1:
                                        channelFile.add(context);
                                        break;

                                    case 2:
                                        channelFile.saveChanges(context);
                                        break;

                                    case 3:
                                        channelFile.remove(context);
                                        break;
                                }
                            }
                        }
                        if (jsonObjectPayload.has("Media") && !jsonObjectPayload.isNull("Media")) {
                            JSONArray mediaJsonArray = jsonObjectPayload.optJSONArray("Media");
                            ArrayList<Media> arraylistMediallist = new ArrayList<>();
                            Media.parseListFromJson(mediaJsonArray, arraylistMediallist);
                            for (Media media : arraylistMediallist) {

                                media.isDownloaded = 1;
                                switch (media.syncType) {
                                    case 1:
                                        media.add(context);

                                        break;
                                    case 2:
                                        media.saveChanges(context);

                                        break;

                                    case 3:
                                        media.remove(context);
                                        break;
                                }
                                Log.e("Media type", media.type + media.currentVersionId);
                                if (!(media.type.equals("folder"))) {
                                    addMediaVersion(media.currentVersion);
                                }
                            }
                        }
                        SelectedMediaFromGalleryModel modelTemp = new SelectedMediaFromGalleryModel();
                        modelTemp = ShowGalleryItemListActivity.arrayListSelectedMedia.get(posUploadingItem);
                        modelTemp.setUploadedCompleted(true);

                        ShowGalleryItemListActivity.arrayListSelectedMedia.set(posUploadingItem,modelTemp);
                        adapterSelectedMedia.notifyDataSetChanged();

                        Helper.copyFileFromSourceToTrage(sourceLocation,Helper.getDownloadFilePath(context,mediaId));
                        updateMediaList();
                        mediaAdapter.notifyDataSetChanged();


                        if(arrayListSelectedMediaTemp.size() > 0) {
                            arrayListSelectedMediaTemp.remove(0);
                        }
                        if(arrayListSelectedMediaTemp.size() == 0){
                            customDialog.dismiss();
                        }

                    }catch (Exception e){
                        notifySimple(getString(R.string.msg_invalid_response_from_server));
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            });
        }catch (Exception e){

            notifySimple(getString(R.string.msg_invalid_response_from_server));
        }
    }
    private void onCaptureImageResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            imagePathFromGalleryOrCamera = cursor.getString(columnIndex);
        }
        if (data != null) {
            try {
                bitmapSelected = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgBackground.setImageBitmap(bitmapSelected);
    }
    private void onCaptureImageOrVideoResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        String[] fileNameColumn = {MediaStore.Images.Media.DISPLAY_NAME};

        Cursor cursor = context.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

        Cursor cursorName = context.getContentResolver().query(selectedImageUri, fileNameColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            cursorName.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            int columnIndexName = cursorName.getColumnIndex(fileNameColumn[0]);

            imagePathFromGalleryOrCamera = cursor.getString(columnIndex);
            imageName = cursorName.getString(columnIndexName);

            if(!TextUtils.isEmpty(imagePathFromGalleryOrCamera) && !TextUtils.isEmpty(imageName)){
                if(ShowGalleryItemListActivity.arrayListSelectedMedia == null ){
                    ShowGalleryItemListActivity.arrayListSelectedMedia = new ArrayList<>();

                    SelectedMediaFromGalleryModel mediaFromGalleryModel = new SelectedMediaFromGalleryModel();
                    mediaFromGalleryModel.setMediaBitmapPath(imagePathFromGalleryOrCamera);
                    mediaFromGalleryModel.setMediaName(imageName);
                    ShowGalleryItemListActivity.arrayListSelectedMedia.add(mediaFromGalleryModel);

                    refreshAdapter();
                }else {
                    SelectedMediaFromGalleryModel mediaFromGalleryModel = new SelectedMediaFromGalleryModel();
                    mediaFromGalleryModel.setMediaBitmapPath(imagePathFromGalleryOrCamera);
                    mediaFromGalleryModel.setMediaName(imageName);
                    ShowGalleryItemListActivity.arrayListSelectedMedia.add(mediaFromGalleryModel);

                    refreshAdapter();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePathFromGalleryOrCamera = cursor.getString(columnIndex);
        }
        //Bitmap bm=null;
        if (data != null) {
            try {
                bitmapSelected = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgBackground.setImageBitmap(bitmapSelected);
    }
    public void showDialogToChooseMedia(final int pos) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        //builder.setTitle("Add Photo!");
        builder.setItems(mediaSelectionItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                openGalleryOrCamera(mediaSelectionItems,item,pos);
            }
        });
        builder.show();
    }
    private void openGalleryOrCamera(CharSequence[] items,int item,int pos){
        if (items[item].equals(getString(R.string.camera))) {
            Helper.userChoosenTask = getString(R.string.camera);
            if(pos == 1) { // pos == 1 means user wants to create folder so just allow to capture image
                cameraIntentForImage();
            }else {
                cameraIntentForImageVideo(); // want to upload media so allow to capture image and video both
            }

        } else if (items[item].equals(getString(R.string.gallery))) {
            Helper.userChoosenTask = getString(R.string.gallery);

            if(pos == 2){
                // choose multi media from gallery
                openGalleryFolderListActivity();
            }else {
                // choose single image from gallery
                galleryIntentForImage();
            }
        } else if (items[item].equals(getString(R.string.cancel_captial))) {
            dialog.dismiss();
        }
    }
    public void galleryIntentForImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),Helper.SELECT_SINGLE_IMAGE_FROM_GALLERY);
    }
    private void cameraIntentForImageVideo(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Intent chooserIntent = Intent.createChooser(takePictureIntent, "Capture Image or Video");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takeVideoIntent});
        startActivityForResult(chooserIntent, Helper.REQUEST_CAMERA_IMAGE_VIDEO);
    }
    public void cameraIntentForImage()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Helper.REQUEST_CAMERA_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == Helper.SELECT_SINGLE_IMAGE_FROM_GALLERY) {
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == Helper.REQUEST_CAMERA_IMAGE) {
                onCaptureImageResult(data);
            }
            else if (requestCode == Helper.REQUEST_CAMERA_IMAGE_VIDEO) {
                onCaptureImageOrVideoResult(data);
            }
            else if(requestCode == Helper.OPEN_MEDIA_PICKER_FOR_SINGLE_OR_MULTIPLE){
                if(ShowGalleryItemListActivity.arrayListSelectedMedia != null && ShowGalleryItemListActivity.arrayListSelectedMedia.size() > 0 ){
                    adapterSelectedMedia.notifyDataSetChanged();
                }
            }
            else {
                updateMediaList();
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
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
    protected void addCreator(User creator)
    {
        switch (creator.syncType)
        {
            case 0:
                creator.saveChanges(context);
                break;
            case 1:
                creator.add(context);
                break;
            case 2:
                creator.saveChanges(context);
                break;
            case 3:
                creator.remove(context);
                break;
        }
    }
    protected void addMediaVersion(MediaVersion currentVersion)
    {
        if (currentVersion != null)
        {
            switch (currentVersion.syncType)
            {
                case 0:
                    currentVersion.saveChanges(context);
                    addCreator(currentVersion.creator);
                    break;
                case 1:
                    currentVersion.add(context);
                    addCreator(currentVersion.creator);
                    break;
                case 2:
                    currentVersion.saveChanges(context);
                    addCreator(currentVersion.creator);
                    break;
                case 3:
                    currentVersion.remove(context);
                    addCreator(currentVersion.creator);
                    break;
            }
        }

    }
    public void openGalleryFolderListActivity() {
        Intent intent= new Intent(getActivity(), ShowGalleryFolderListActivity.class);
        startActivityForResult(intent,Helper.OPEN_MEDIA_PICKER_FOR_SINGLE_OR_MULTIPLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Helper.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDialogToChooseMedia(position);
            }
        }
    }
    /* newly added code*/

}
