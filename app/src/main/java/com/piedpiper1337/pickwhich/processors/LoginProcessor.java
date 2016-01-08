package com.piedpiper1337.pickwhich.processors;

import android.content.Intent;
import android.text.TextUtils;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.piedpiper1337.pickwhich.utils.Constants;

/**
 * Created by cary on 1/6/16.
 */
public class LoginProcessor extends Processor {

    private int mRequestId = -1;

    @Override
    public void execute(Intent intent) {
        mRequestId = intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1); // Base class automatically includes this in
        String username = intent.getStringExtra(Constants.IntentExtras.USERNAME);
        String password = intent.getStringExtra(Constants.IntentExtras.PASSWORD);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            broadcast(Constants.IntentActions.ACTION_ERROR, "Empty username or password", null);
        }

        if (mRequestId == Constants.ApiRequestId.LOGIN) {
            // Log in
            broadcast(Constants.IntentActions.ACTION_SUCCESS, "", null); // Successful login, Parse handles login details (for now)
        } else if (mRequestId == Constants.ApiRequestId.SIGN_UP) {
            // Sign up
            String email = intent.getStringExtra(Constants.IntentExtras.EMAIL);

            if (TextUtils.isEmpty(email)) {
                broadcast(Constants.IntentActions.ACTION_ERROR, "Missing valid email", null); // Missing email
                return;
            }

            String phoneNumber = intent.getStringExtra(Constants.IntentExtras.PHONE_NUMBER);

            if (TextUtils.isEmpty(phoneNumber)) {
                // Ask the user to enter their phone number and then give an error if they don't comply
                // But for now, just return an error
                broadcast(Constants.IntentActions.ACTION_ERROR, "Unable to proceed without a phone number", null);
                return;
            }

            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.put("phone", phoneNumber);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        broadcast(Constants.IntentActions.ACTION_SUCCESS, "", null);
                    } else {
                        // Check error code e.g. e.CACHE_MISS
                        broadcast(Constants.IntentActions.ACTION_ERROR, "", null);
                    }
                }
            });
        }
    }

    @Override
    public int getRequestId() {
        return mRequestId;
    }

    @Override
    public String getTag() {
        return LoginProcessor.class.getSimpleName();
    }
}