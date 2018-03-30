package de.smac.smaccloud.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michael.easydialog.EasyDialog;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.LanguageListViewAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;

/**
 * Show intro screen
 */
public class IntroScreenActivity extends Activity implements View.OnClickListener
{
    public EasyDialog dialog;
    RelativeLayout relativeLayout;
    Spanned Text;
    MenuInflater inflater;
    TextView btnLogin, btnSignUp;
    Button buttonLogin, buttonSignUp;
    TextView textViewTitle, textViewSmacSoftwareLink;
    Activity activity;
    ImageView imgInfo;
    PreferenceHelper preManager;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private int pagePosition;
    private ImageView languageChange;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
    {

        @Override
        public void onPageSelected(int position)
        {
            addBottomDots(position);
            pagePosition = position;
            if (position == layouts.length - 1)
            {
                btnSkip.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.GONE);
            }
            else
            {
                btnSkip.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                btnSignUp.setVisibility(View.VISIBLE);
            }
            if (pagePosition == 8)
            {
                View slide9View = viewPager.getChildAt(2);
                TextView txt_admin_link = null;
                if (slide9View != null)
                {
                    txt_admin_link = (TextView) slide9View.findViewById(R.id.txt_admin_link);
                }
                else
                {
                    slide9View = viewPager.getChildAt(1);
                    if (slide9View != null)
                    {
                        txt_admin_link = (TextView) slide9View.findViewById(R.id.txt_admin_link);
                    }
                }
                if (txt_admin_link != null)
                {
                    Text = Html.fromHtml(getString(R.string.slide_9_desc) +
                            "<a href='https://smaccloud.smacsoftwares.de:2020/SMACAdmin/app/#/login/EN/'> https://smaccloud.smacsoftwares.de:2020</a>" + (")"));

                    txt_admin_link.setMovementMethod(LinkMovementMethod.getInstance());
                    txt_admin_link.setText(Text);
                }
            }
            if (position == layouts.length - 1)
            {
                if (viewPager.getChildCount() > 0)
                {
                    View slide10View = viewPager;

                    if (slide10View != null)
                    {

                        buttonLogin = (Button) slide10View.findViewById(R.id.btn_login);
                        preManager = new PreferenceHelper(getApplicationContext());
                        //Helper.retainOrientation(IntroScreenActivity.this);
                        textViewTitle = (TextView) slide10View.findViewById(R.id.demo_title);
                        textViewSmacSoftwareLink = (TextView) slide10View.findViewById(R.id.txt_smac_link);
                        languageChange = (ImageView) slide10View.findViewById(R.id.language_english);
                        buttonSignUp = (Button) slide10View.findViewById(R.id.btn_sign_up);
                        imgInfo = (ImageView) slide10View.findViewById(R.id.img_info);
                        buttonLogin.setOnClickListener(IntroScreenActivity.this);
                        buttonSignUp.setOnClickListener(IntroScreenActivity.this);
                        languageChange.setOnClickListener(IntroScreenActivity.this);
                        textViewSmacSoftwareLink.setOnClickListener(IntroScreenActivity.this);
                        imgInfo.setOnClickListener(IntroScreenActivity.this);
                        if (getSupportActionBar() != null)
                        {
                            getSupportActionBar().setTitle(getString(R.string.app_name));
                            actionBar.setHomeButtonEnabled(false);
                            actionBar.setDisplayHomeAsUpEnabled(false);
                            actionBar.setDisplayShowHomeEnabled(false);
                        }
                        updateLanguage();

                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    };
    private PreferenceHelper prefManager;

    @Override
    protected void onResume()
    {
        super.onResume();
        if (PreferenceHelper.hasUserContext(context))
        {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_information, menu);
        applyThemeColor();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_information:
                String url = "https://www.smaccloud.com/help/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void applyThemeColor()
    {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_info, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.orange_color));
    }

    public void updateLanguage()
    {
        if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("en"))
        {
            if (PreferenceHelper.getSelectedLanguage(context).equals(""))
                PreferenceHelper.storeSelectedLanguage(context, "en");

            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
            languageChange.setImageResource(R.drawable.ic_flag_english);
        }
        else if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("de"))
        {
            PreferenceHelper.storeSelectedLanguage(context, "de");
            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
            languageChange.setImageResource(R.drawable.ic_flag_german);
        }
        buttonLogin.setText(getResources().getString(R.string.login));
        buttonSignUp.setText(getResources().getString(R.string.sign_up));
        btnLogin.setText(getResources().getString(R.string.login));
        btnSignUp.setText(getResources().getString(R.string.sign_up));
        textViewSmacSoftwareLink.setText(getResources().getString(R.string.smac_link));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PreferenceHelper(this);
        if (!prefManager.isFirstTimeLaunch())
        {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_introduction);
        PreferenceHelper.getSelectedLanguage(context);
        Helper.retainOrientation(IntroScreenActivity.this);



        viewPager = (ViewPager) findViewById(R.id.view_pager);
        relativeLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnLogin = (TextView) findViewById(R.id.btn_login);
        btnSignUp = (TextView) findViewById(R.id.btn_sign_up);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentLogin);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentSignUp = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intentSignUp);
            }
        });

        layouts = new int[]{
                R.layout.activity_introduction_slide1,
                R.layout.activity_introduction_slide2,
                R.layout.activity_introduction_slide3,
                R.layout.activity_introduction_slide4,
                R.layout.activity_introduction_slide5,
                R.layout.activity_introduction_slide6,
                R.layout.activity_introduction_slide7,
                R.layout.activity_introduction_slide8,
                R.layout.activity_introduction_slide9,
                R.layout.activity_introduction_slide10,
                R.layout.activity_demo,


        };



        Helper.setupTypeface(relativeLayout, Helper.robotoRegularTypeface);

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchHomeScreen();
            }
        });
        btnNext.setOnClickListener(

                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // checking for last page
                        // if last page home screen will be launched
                        int current = getItem(+1);
                        if (current < layouts.length)
                        {
                            // move to next screen
                            viewPager.setCurrentItem(current);
                        }
                        else
                        {
                            launchHomeScreen();
                        }
                    }
                });
    }

    public void showDialogLikeTooltip()
    {
        dialog = new EasyDialog(IntroScreenActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_language_list, null);
        if (Helper.isTablet(IntroScreenActivity.this))
        {
            view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(this) / 4, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else
        {
            view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(this) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        final ListView listLanguage = (ListView) view.findViewById(R.id.listLanguage);
        LanguageListViewAdapter languageListViewAdapter = new LanguageListViewAdapter(IntroScreenActivity.this);
        listLanguage.setAdapter(languageListViewAdapter);
        listLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (position == 0)
                {
                    PreferenceHelper.storeSelectedLanguage(IntroScreenActivity.this, "en");
                    Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
                    languageChange.setImageResource(R.drawable.ic_flag_english);
                }
                else if (position == 1)
                {
                    PreferenceHelper.storeSelectedLanguage(IntroScreenActivity.this, "de");
                    Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
                    languageChange.setImageResource(R.drawable.ic_flag_german);
                }
                updateLanguage();
                dialog.dismiss();
            }
        });
        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(IntroScreenActivity.this.getResources().getColor(R.color.white_color))
                .setLocationByAttachedView(languageChange)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        updateLanguage();
    }


    private void addBottomDots(int currentPage)
    {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++)
        {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i)
    {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen()
    {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(IntroScreenActivity.this, DemoActivity.class));
        finish();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_login:
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                break;

            case R.id.btn_sign_up:
                Intent i1 = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i1);
                break;

            case R.id.language_english:
                showDialogLikeTooltip();
                break;

            case R.id.txt_smac_link:
                String url = "https://www.smacsoftwares.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.img_info:
                String url1 = "https://www.smaccloud.com/help/";
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(url1));
                startActivity(intent1);
                break;

        }

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter
    {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter()
        {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount()
        {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj)
        {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
