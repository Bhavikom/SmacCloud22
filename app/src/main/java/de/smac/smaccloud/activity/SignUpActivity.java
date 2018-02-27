package de.smac.smaccloud.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;

/**
 * Created by S Soft on 2/27/2018.
 */

public class SignUpActivity extends Activity implements View.OnClickListener
{
    EditText editUserName, editEmailId, editAddress, editContact, editMobileNo, editOragnization, editPassword, editConfirmPassword;
    Button buttonSignUp;
    LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editUserName = (EditText) findViewById(R.id.textUserName);
        editEmailId = (EditText) findViewById(R.id.textEmailId);
        editAddress = (EditText) findViewById(R.id.textAddress);
        editContact = (EditText) findViewById(R.id.textContact);
        editMobileNo = (EditText) findViewById(R.id.textMobileNo);
        editOragnization = (EditText) findViewById(R.id.textOrgnization);
        editPassword = (EditText) findViewById(R.id.textPassword);
        editConfirmPassword = (EditText) findViewById(R.id.textCPassword);
        buttonSignUp = (Button) findViewById(R.id.btn_signUp);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        buttonSignUp.setOnClickListener(this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.sign_up));

        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.btn_signUp:

                String name = editUserName.getText().toString().trim();
                String emailId = editEmailId.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String mobileNo = editMobileNo.getText().toString().trim();
                String organization = editOragnization.getText().toString().trim();
                String confirmPassword = editConfirmPassword.getText().toString().trim();

                if (name.isEmpty())
                {
                    Snackbar.make(parentLayout, getString(R.string.name_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (emailId.isEmpty())
                {
                    Snackbar.make(parentLayout, getString(R.string.emailid_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
                {
                    Snackbar.make(parentLayout, getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                }
                else if (mobileNo.isEmpty())
                {
                    Snackbar.make(parentLayout, getString(R.string.mobileno_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (organization.isEmpty())
                {
                    Snackbar.make(parentLayout, getString(R.string.oragnization_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (password.isEmpty())
                {
                    Snackbar.make(parentLayout, getString(R.string.password_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (!password.equals(confirmPassword))
                {
                    Snackbar.make(parentLayout, getString(R.string.password_mismatch_validation_message), Snackbar.LENGTH_LONG).show();
                }
                else
                {

                    Toast.makeText(context, "signup", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
