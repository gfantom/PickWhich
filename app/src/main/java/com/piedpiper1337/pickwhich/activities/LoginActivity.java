package com.piedpiper1337.pickwhich.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.callbacks.RESTApiBroadcastReceiver;
import com.piedpiper1337.pickwhich.callbacks.RESTApiProcessorCallback;
import com.piedpiper1337.pickwhich.service.ServiceHelper;
import com.piedpiper1337.pickwhich.utils.Constants;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;


/**
 * The login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>, RESTApiProcessorCallback {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // To subscribe to broadcasts
    private RESTApiBroadcastReceiver mReceiver;
    private IntentFilter mFilter;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private Button mEmailSignInButton;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private EditText mUsernameEditText;
    private EditText mPhoneNumber;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout mConfirmPasswordTextInputLayout;
    private TextInputLayout mUsernameTextInputLayout;
    private TextInputLayout mPhoneNumberTextInputLayout;
    private CheckBox mSignUpCheckBox;

    TextView.OnEditorActionListener mSignInOnEditorActionListener;
    TextView.OnEditorActionListener mSignUpOnEditorActionListener;

    View.OnClickListener mSignInOnClickListener;
    View.OnClickListener mSignUpOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int temp = 0;

        ActivityCompat.requestPermissions(this, new String[] {
                android.Manifest.permission.READ_CONTACTS
        }, temp);

        setContentView(R.layout.activity_login);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initUI(); // Set up the UI
    }

    @Override
    protected void onResume() {
        super.onResume();

        mReceiver = new RESTApiBroadcastReceiver(this);
        mFilter = new IntentFilter();
        mFilter.addAction(Constants.IntentActions.ACTION_ERROR);
        mFilter.addAction(Constants.IntentActions.ACTION_SUCCESS);

        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onHttpResponseError(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        showProgress(false);

        String message = intent.getStringExtra(Constants.IntentExtras.MESSAGE);
        Log.e(getTag(), message);
        showErrorDialog(message);
    }

    @Override
    public void onHttpRequestComplete(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        showProgress(false);

        if (intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1) == Constants.ApiRequestId.LOGIN) {
            // Successfully logged in, continue to next screen
            startActivity(new Intent(this, HomeActivity.class)); // Start the main app Activity
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    /**
     * Initialized the UI
     */
    private void initUI() {
        mSignInOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        };

        mSignUpOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        };

        mSignInOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        };

        mSignUpOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        };

        /**
         * Email Box
         * */
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        /**
         * Username Box (for sign up)
         * */
        mUsernameTextInputLayout = (TextInputLayout) findViewById(R.id.username_textinputlayout);
        mUsernameEditText = (EditText) findViewById(R.id.username);

        /**
         * Phone Number Box (for sign up)
         * */
        mPhoneNumberTextInputLayout = (TextInputLayout) findViewById(R.id.phone_number_textinputlayout);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);

        /**
         * First Password Box
         * */
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(mSignInOnEditorActionListener);

        /**
         * Confirm Password Box (for sign up)
         * */
        mConfirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.password_confirm_text_input_layout);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);

        /**
         * Dual purpose SUSI button
         * */
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(mSignInOnClickListener);

        /**
         * CheckBox for toggling SU/SI
         * */
        mSignUpCheckBox = (CheckBox) findViewById(R.id.sign_up_checkbox);
        mSignUpCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mConfirmPasswordTextInputLayout.setVisibility(View.VISIBLE);
                    mUsernameTextInputLayout.setVisibility(View.VISIBLE);
                    mPhoneNumberTextInputLayout.setVisibility(View.VISIBLE);
                    mEmailSignInButton.setOnClickListener(mSignUpOnClickListener);
                    mEmailSignInButton.setText(R.string.action_sign_up);
                    mPasswordView.setOnEditorActionListener(mSignUpOnEditorActionListener);
                    mPasswordView.setImeActionLabel(getString(R.string.action_sign_up), R.id.login);
                } else {
                    mConfirmPasswordTextInputLayout.setVisibility(View.GONE);
                    mUsernameTextInputLayout.setVisibility(View.GONE);
                    mPhoneNumberTextInputLayout.setVisibility(View.GONE);
                    mEmailSignInButton.setOnClickListener(mSignInOnClickListener);
                    mEmailSignInButton.setText(R.string.action_sign_in);
                    mPasswordView.setOnEditorActionListener(mSignInOnEditorActionListener);
                    mPasswordView.setImeActionLabel(getString(R.string.action_sign_in), R.id.login);
                }
            }
        });

        /**
         * When the screen is rotated this check prevents a UI bug
         * */
        if (mSignUpCheckBox.isChecked()) {
            mConfirmPasswordTextInputLayout.setVisibility(View.VISIBLE);
            mUsernameTextInputLayout.setVisibility(View.VISIBLE);
            mPhoneNumberTextInputLayout.setVisibility(View.VISIBLE);
            mEmailSignInButton.setOnClickListener(mSignInOnClickListener);
            mEmailSignInButton.setText(R.string.action_sign_up);
            mPasswordView.setOnEditorActionListener(mSignInOnEditorActionListener);
            mPasswordView.setImeActionLabel(getString(R.string.action_sign_up), R.id.login);
        }

        /**
         * Views for toggling the Progress Bar
         * */
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptSignUp() {

    }

    /**
     * Attempts to log in
     * Essentially check if fields are valid and start progress spinner and background login request
     */
    private void attemptLogin() {
        //mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging the human in", true);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ServiceHelper.getInstance(LoginActivity.this).doLogin("test", "Super Duper test!");
        }
    }

    private boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordValid(String password) {
        // TODO: Replace this with logic
        return password.length() >= 6 && password.matches(".*[0-9].*");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Crazy Loader stuff that was auto generated,
     * loads email addresses from contacts for the
     * AutoCompleteTextView
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

