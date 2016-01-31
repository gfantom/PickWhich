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

import com.parse.ParseUser;
import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.callbacks.RESTApiBroadcastReceiver;
import com.piedpiper1337.pickwhich.callbacks.RESTApiProcessorCallback;
import com.piedpiper1337.pickwhich.service.ServiceHelper;
import com.piedpiper1337.pickwhich.utils.Constants;

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
    private AutoCompleteTextView mEmailEditText;
    private Button mEmailSignInButton;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mUsernameEditText;
    private EditText mPhoneNumberEditText;
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

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d(getTag(), "Logged in already: " + currentUser.getEmail() + " " + currentUser.get("username"));
            finish();
            startActivity(new Intent(this, HomeActivity.class)); // Move along, we're already logged in
        }

        ActivityCompat.requestPermissions(this, new String[] {
                android.Manifest.permission.READ_CONTACTS
        }, 0);

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

        int requestId = intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1);

        showProgress(false);

        if (requestId == Constants.ApiRequestId.LOGIN) {
            // Successfully logged in, continue to next screen
            startActivity(new Intent(this, HomeActivity.class)); // Start the main app Activity
            finish();
        } else if (requestId == Constants.ApiRequestId.SIGN_UP) {
            // Successfully signed up, now we can continue
            // TODO: start phone number verification process
            ParseUser currentuser = ParseUser.getCurrentUser();
            if (currentuser != null) {
                startActivity(new Intent(this, HomeActivity.class)); // Start the main app Activity
                finish();
            }
        } else {
            Log.e(getTag(), "Unhandled request received from " + intent.getAction());
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
        mEmailEditText = (AutoCompleteTextView) findViewById(R.id.email);
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
        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number);

        /**
         * First Password Box
         * */
        mPasswordEditText = (EditText) findViewById(R.id.password);
        mPasswordEditText.setOnEditorActionListener(mSignInOnEditorActionListener);

        /**
         * Confirm Password Box (for sign up)
         * */
        mConfirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.password_confirm_text_input_layout);
        mPasswordConfirmEditText = (EditText) findViewById(R.id.password_confirm);

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
                    mPasswordEditText.setOnEditorActionListener(mSignUpOnEditorActionListener);
                    mPasswordEditText.setImeActionLabel(getString(R.string.action_sign_up), R.id.login);
                } else {
                    mConfirmPasswordTextInputLayout.setVisibility(View.GONE);
                    mUsernameTextInputLayout.setVisibility(View.GONE);
                    mPhoneNumberTextInputLayout.setVisibility(View.GONE);
                    mEmailSignInButton.setOnClickListener(mSignInOnClickListener);
                    mEmailSignInButton.setText(R.string.action_sign_in);
                    mPasswordEditText.setOnEditorActionListener(mSignInOnEditorActionListener);
                    mPasswordEditText.setImeActionLabel(getString(R.string.action_sign_in), R.id.login);
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
            mPasswordEditText.setOnEditorActionListener(mSignInOnEditorActionListener);
            mPasswordEditText.setImeActionLabel(getString(R.string.action_sign_up), R.id.login);
        }

        /**
         * Views for toggling the Progress Bar
         * */
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptSignUp() {
        // Reset errors
        mEmailEditText.setError(null);
        mUsernameEditText.setError(null);
        mPhoneNumberEditText.setError(null);
        mPasswordEditText.setError(null);
        mPasswordConfirmEditText.setError(null);

        String email = mEmailEditText.getText().toString();
        String username = mUsernameEditText.getText().toString();
        String phoneNumber = mPhoneNumberEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mPasswordConfirmEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /**
         * Check that the email is not empty and has a valid format
         * */
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }

        /**
         * Check that the username is not empty and has a valid format
         * */
        if (!isUsernameValid(username)) {
            mUsernameEditText.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError(getString(R.string.error_field_required));
            focusView = mUsernameEditText;
            cancel = true;
        }

        /**
         * Check that the phone number is not empty and has a valid format
         * */
        if (!isPhoneNumberValid(phoneNumber)) {
            mPhoneNumberEditText.setError(getString(R.string.error_invalid_phone_number));
            focusView = mPhoneNumberEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberEditText.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberEditText;
            cancel = true;
        }

        /**
         * Check for a valid password, if the user entered one.
         * */
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        /**
         * Check that the confirmed password isn't empty
         * */
        if (TextUtils.isEmpty(confirmPassword)) {
            mPasswordConfirmEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        }

        /**
         * Check that passwords match
         * */
        if (!mPasswordEditText.getText().toString().equals(mPasswordConfirmEditText.getText().toString())) {
            mPasswordEditText.setError(getString(R.string.error_passwords_must_match));
            mPasswordConfirmEditText.setError(getString(R.string.error_passwords_must_match));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            ServiceHelper.getInstance(LoginActivity.this).doSignUp(username, password, email, phoneNumber);
        }
    }

    /**
     * Attempts to log in
     * Essentially check if fields are valid and start progress spinner and background login request
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
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
            ServiceHelper.getInstance(LoginActivity.this).doLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return true;
        //return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordValid(String password) {
        // TODO: Replace this with something better
        return password.length() >= 6 && password.matches(".*[0-9].*");
    }

    private boolean isUsernameValid(String email) {
        return email.length() > 2;
    }

    private boolean isPhoneNumberValid(String number) {
        // TODO: Replace this with something better
        return number.length() == 10;
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

        mEmailEditText.setAdapter(adapter);
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

