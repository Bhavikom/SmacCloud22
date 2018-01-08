package de.smac.smaccloud.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

public class ChangePasswordActivity extends Activity implements View.OnClickListener
{

    private static final int REQUEST_CHANGE_PASSWORD = 4301;
    private static final String KEY_TEXT_VALUE = "textValue";
    Button btnChangePassword;
    EditText editCurrentPassword, editNewPassword, editConfirmPassword;
    TextView textViewPasswordTitle;
    ImageView imgVisible1, imgVisible2, imgVisible3;
    private LinearLayout parentLayout;
    private String deviceId = "00000-00000-00000-00000-00000";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        Helper.retainOrientation(ChangePasswordActivity.this);
        Helper.robotoBoldTypeface = Typeface.createFromAsset(this.getAssets(), Helper.fontPathBold);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        textViewPasswordTitle.setTypeface(Helper.robotoBoldTypeface);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.hint_password));
        }
        if (savedInstanceState != null)
        {
            CharSequence savedText = savedInstanceState.getCharSequence(KEY_TEXT_VALUE);
            editCurrentPassword.setText(savedText);
            editNewPassword.setText(savedText);
            editConfirmPassword.setText(savedText);
        }
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();

        imgVisible1 = (ImageView) findViewById(R.id.img_visible1);
        imgVisible2 = (ImageView) findViewById(R.id.img_visible2);
        imgVisible3 = (ImageView) findViewById(R.id.img_visible3);

        editCurrentPassword = (EditText) findViewById(R.id.edit_current_password);
        editNewPassword = (EditText) findViewById(R.id.edit_new_password);
        editConfirmPassword = (EditText) findViewById(R.id.edit_confirm_password);
        textViewPasswordTitle = (TextView) findViewById(R.id.txt_password_title);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.btn_change_password:
                        String currentPassword = editCurrentPassword.getText().toString();
                        String newPassword = editNewPassword.getText().toString();
                        String confirmPassword = editConfirmPassword.getText().toString();
                        if (currentPassword.isEmpty())
                        {
                            editCurrentPassword.setError(getString(R.string.current_password_validation_message));
                        }
                        else if (newPassword.isEmpty())
                        {
                            editNewPassword.setError(getString(R.string.new_password_validation_message));
                        }
                        else if (confirmPassword.isEmpty())
                        {
                            editConfirmPassword.setError(getString(R.string.confirm_password_validation_message));

                        }
                        else if (!newPassword.equals(confirmPassword))
                        {

                            notifySimple(getString(R.string.password_mismatch_validation_message));
                        }
                        else
                        {
                            if (Helper.isNetworkAvailable(context))
                            {
                                Helper.hideSoftKeyboard(ChangePasswordActivity.this);
                                postNetworkRequest(REQUEST_CHANGE_PASSWORD, DataProvider.ENDPOINT_USER, DataProvider.Actions.CHANGE_PASSWORD,
                                        RequestParameter.urlEncoded("Id", String.valueOf(PreferenceHelper.getUserContext(context))),
                                        RequestParameter.urlEncoded("CurrentPassword", currentPassword),
                                        RequestParameter.urlEncoded("Password", newPassword));
                            }
                            else
                            {
                                notifySimple(getString(R.string.msg_network_connection_not_available));
                            }
                        }
                        break;
                    case R.id.img_visible1:
                        if (editCurrentPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        {
                            editCurrentPassword.setInputType(129);
                            imgVisible1.setImageResource(R.drawable.ic_visibility_off);
                        }
                        else
                        {
                            editCurrentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imgVisible1.setImageResource(R.drawable.ic_visibility);
                        }
                        break;
                    case R.id.img_visible2:
                        if (editNewPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        {
                            editNewPassword.setInputType(129);
                            imgVisible2.setImageResource(R.drawable.ic_visibility_off);
                        }
                        else
                        {
                            editNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imgVisible2.setImageResource(R.drawable.ic_visibility);
                        }
                        break;
                    case R.id.img_visible3:
                        if (editConfirmPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        {
                            editConfirmPassword.setInputType(129);
                            imgVisible3.setImageResource(R.drawable.ic_visibility_off);
                        }
                        else
                        {
                            editConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imgVisible3.setImageResource(R.drawable.ic_visibility);
                        }
                        break;
                }
            }
        };
        btnChangePassword.setOnClickListener(clickListener);
        imgVisible1.setOnClickListener(clickListener);
        imgVisible2.setOnClickListener(clickListener);
        imgVisible3.setOnClickListener(clickListener);
    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_CHANGE_PASSWORD)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.change_password_database_message));
                        }
                    }
                    else
                    {
                        notifySimple(getString(R.string.change_password));
                    }
                }
                catch (JSONException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_please_try_again_later));
            }

        }
        else
        {
            notifySimple(getString(R.string.msg_cannot_complete_request));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE, editNewPassword.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, editConfirmPassword.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, editCurrentPassword.getText());
    }

    @Override

    public void onClick(View v)
    {

    }
}