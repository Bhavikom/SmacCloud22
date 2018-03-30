package de.smac.smaccloud.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.SettingsFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.LocalizationData;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;


public class SplashActivity extends de.smac.smaccloud.base.Activity
{
    public static final int REQUEST_GETLOCALIZATION = 5001;
    public static final int REQUEST_GET_VERSION = 5002;
    private static int SPLASH_TIME_OUT = 3000;
    String strLatestVersionCodeFromService = "";
    String strVersionName = "";
    String strVersionNo = "";
    ArrayList<LocalizationData> arrayListLocalization = new ArrayList<>();
    Activity activity;
    ImageView img_app_icon;
    private PreferenceHelper prefManager;
    private Handler splashHandler;
    private Runnable splashRunnable;
    private RelativeLayout parentLayout;
    private boolean flag;
    private TextView txtCopyRight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splace);
        prefManager = new PreferenceHelper(context);
        Helper.retainOrientation(SplashActivity.this);
        activity = this;
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        Helper.robotoRegularTypeface = Typeface.createFromAsset(this.getAssets(), Helper.fontPathRoboto);

        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);

        img_app_icon = (ImageView) findViewById(R.id.img_app_icon);
        txtCopyRight = (TextView) findViewById(R.id.txt_copyRight_text);

        String iconPath = PreferenceHelper.getAppIcon(context);
        if (iconPath.isEmpty())
        {
            img_app_icon.setImageResource(R.drawable.ic_logo);
        }
        else
        {
            Glide.with(context)
                    .load(iconPath)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_app_icon);
        }

        String lang = PreferenceHelper.getSelectedLanguage(SplashActivity.this);
        if (lang != null && !lang.isEmpty())
        {
            Helper.changeLanguage(SplashActivity.this, lang);
        }
        else
        {
            lang = "en";
            Helper.changeLanguage(SplashActivity.this, lang);
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        txtCopyRight.setText(getString(R.string.copyright).concat(" ").concat(String.valueOf(year).concat(" ").concat(getString(R.string.copyright1))));

    }

    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onNetworkReady()
    {
        super.onNetworkReady();
        if (Helper.isNetworkAvailable(activity))
        {
            try
            {
                strVersionNo = String.valueOf(Helper.getVersionNo(context));
                strVersionName = String.valueOf(Helper.getVersionName(context));
                Helper.IS_DIALOG_SHOW = false;
                postNetworkRequest(REQUEST_GET_VERSION, DataProvider.ENDPOINT_GET_VERSION,
                        DataProvider.Actions.ACTION_GET_VERSION,
                        RequestParameter.urlEncoded("VersionName", strVersionName),
                        RequestParameter.urlEncoded("VersionCode", strVersionNo));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        else
        {
            if (PreferenceHelper.hasUserContext(SplashActivity.this))
            {
                if (PreferenceHelper.getSyncStatus(SplashActivity.this))
                    startDashboardActivity();
                else
                    startSyncActivity();
            }
            else
            {
                Intent i = new Intent(SplashActivity.this, IntroScreenActivity.class);
                startActivity(i);
                finish();
            }

        }


    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        Log.e("TEST>>", response);
        Helper.IS_DIALOG_SHOW = true;
        if (requestCode == REQUEST_GET_VERSION)
        {
            if (status)
            {
                try
                {
                    JSONObject jsonObjectMain = new JSONObject(response);

                    int requestStatus = jsonObjectMain.optInt("Status");
                    if (requestStatus > 0)
                    {
                        notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                    }
                    else
                    {
                        // parse response here
                        JSONObject userJson = jsonObjectMain.optJSONObject("Payload");

                        String strForceUpdate = userJson.getString("IsNeedForceUpdate");
                        if (strForceUpdate.equals("false"))
                        {
                            // no need force update
                            strLatestVersionCodeFromService = userJson.getString("VersionCode");
                            if (Integer.parseInt(strLatestVersionCodeFromService) > Integer.parseInt(strVersionNo))
                            {

                                if (prefManager.getUpdatedVersionNo() == 0 || prefManager.getUpdatedVersionNo() < Integer.parseInt(strLatestVersionCodeFromService))
                                {

                                    showAlertDialogToUpdateVersion();
                                }
                                else
                                {
                                    // user already has given remind me later for this version code so call getlocalization and navigate to next
                                    try
                                    {
                                        Helper.IS_DIALOG_SHOW = false;
                                        postNetworkRequest(REQUEST_GETLOCALIZATION, DataProvider.ENDPOINT_GET_LOCALIZATION, DataProvider.Actions.ACTION_LOCALIZATION,
                                                RequestParameter.urlEncoded("LastSyncDate", PreferenceHelper.getLastSychDate(context)));
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }


                            }
                            else
                            {
                                try
                                {
                                    Helper.IS_DIALOG_SHOW = false;
                                    postNetworkRequest(REQUEST_GETLOCALIZATION, DataProvider.ENDPOINT_GET_LOCALIZATION, DataProvider.Actions.ACTION_LOCALIZATION,
                                            RequestParameter.urlEncoded("LastSyncDate", PreferenceHelper.getLastSychDate(context)));
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {
                            // force update is true that's why compulsory for user to update app-
                            // show alert dialog to force update app and prohibit user to enter in application
                            showAlertDialogForForceUpdate();
                        }

                    }
                }
                catch (Exception e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
        }
        else if (requestCode == REQUEST_GETLOCALIZATION)
        {
            if (status)
            {
                try
                {
                    JSONObject jsonObjectMain = new JSONObject(response);

                    int requestStatus = jsonObjectMain.optInt("Status");
                    if (requestStatus > 0)
                    {
                        notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                    }
                    else
                    {

                        //PreferenceHelper.saveLastSyncDate(this, jsonObjectMain.getString("Message"));
                        JSONObject userJson = jsonObjectMain.optJSONObject("Payload");
                        PreferenceHelper.saveLastSyncDate(this, userJson.optString("LastSyncDate"));
                        if (userJson.has("LocalizationList"))
                        {
                            JSONArray jsonArray = userJson.getJSONArray("LocalizationList");
                            arrayListLocalization = new ArrayList<>();
                            arrayListLocalization = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<LocalizationData>>()
                            {

                            }.getType());

                            if (arrayListLocalization.size() > 0)
                            {
                                DataHelper.fillLocalizationTable(this, arrayListLocalization);
                            }
                            //Log.e("TEST>>"," get localization : " + DataHelper.getLocalizationMessageFromCode(this,"2101","en-en","3"));
                            if (PreferenceHelper.hasUserContext(SplashActivity.this))
                            {
                                if (PreferenceHelper.getSyncStatus(SplashActivity.this))
                                    startDashboardActivity();
                                else
                                    startSyncActivity();
                            }
                            else
                            {
                                Intent i = new Intent(SplashActivity.this, IntroScreenActivity.class);
                                startActivity(i);
                                finish();
                            }

                        }

                    }
                }
                catch (Exception e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                if (PreferenceHelper.hasUserContext(SplashActivity.this))
                {
                    if (PreferenceHelper.getSyncStatus(SplashActivity.this))
                        startDashboardActivity();
                    else
                        startSyncActivity();
                }
                else
                {
                    Intent i = new Intent(SplashActivity.this, IntroScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
        else
        {
            notifySimple(getString(R.string.msg_cannot_complete_request));
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        flag = false;
        Helper.IS_DIALOG_SHOW = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        flag = true;
        //splashHandler.postDelayed(splashRunnable, SPLASH_TIME_OUT);
    }

    private void startDashboardActivity()
    {
        Intent dashboardIntent = new Intent(SplashActivity.this, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    private void startSyncActivity()
    {
        Intent dashboardIntent = new Intent(SplashActivity.this, SyncActivity.class);
        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
        startActivity(dashboardIntent);
        finish();
    }

    private void showAlertDialogToUpdateVersion()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(context.getString(R.string.version_updation_title));
        builder.setMessage(context.getString(R.string.version_update_message));
        builder.setPositiveButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                        final String appPackageName = getPackageName();
                        Helper.openPlayStoreUrl(context, appPackageName);
                        // redirect to play store url
                    }
                });
        builder.setNeutralButton(context.getString(R.string.label_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                        // call get localization service and navigate to next screen
                        Helper.IS_DIALOG_SHOW = false;
                        postNetworkRequest(REQUEST_GETLOCALIZATION, DataProvider.ENDPOINT_GET_LOCALIZATION, DataProvider.Actions.ACTION_LOCALIZATION,
                                RequestParameter.urlEncoded("LastSyncDate", PreferenceHelper.getLastSychDate(context)));
                    }
                });

        builder.setNegativeButton(context.getString(R.string.remind_me_later),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                        // save latest version code to preferences and don't remind user to show dialog again for this version code
                        prefManager.saveUpdatedVersionNo(Integer.parseInt(strLatestVersionCodeFromService));
                        // call localization service here
                        Helper.IS_DIALOG_SHOW = false;
                        postNetworkRequest(REQUEST_GETLOCALIZATION, DataProvider.ENDPOINT_GET_LOCALIZATION, DataProvider.Actions.ACTION_LOCALIZATION,
                                RequestParameter.urlEncoded("LastSyncDate", PreferenceHelper.getLastSychDate(context)));
                    }
                });
        builder.create().show();
    }

    private void showAlertDialogForForceUpdate()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.version_updation_title));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(context.getString(R.string.version_force_update_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        // redirect to play store url
                        final String appPackageName = getPackageName();
                        Helper.openPlayStoreUrl(context, appPackageName);

                    }
                });
        alertDialog.show();
    }
}
