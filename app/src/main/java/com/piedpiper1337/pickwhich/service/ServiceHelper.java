package com.piedpiper1337.pickwhich.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.piedpiper1337.pickwhich.utils.CommonUtils;
import com.piedpiper1337.pickwhich.utils.Constants;

/**
 * Singleton Service Helper
 * Manages the starting of the ApiService
 * <p/>
 * Created by cary on 1/7/16.
 */
public class ServiceHelper {

    private static String TAG = ServiceHelper.class.getSimpleName();

    private Context mContext;
    private static ServiceHelper mServiceHelper;

    private ServiceHelper(Context context) {
        mContext = context;
    }

    public static ServiceHelper getInstance(Context context) {
        if (mServiceHelper == null) {
            mServiceHelper = new ServiceHelper(context);
        } else {
            mServiceHelper.setContext(context);
        }

        return mServiceHelper;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private boolean startService(Intent intent) {
        if (CommonUtils.isOnline(mContext)) {
            intent.setClass(mContext, RESTApiService.class);
            mContext.startService(intent);
            Log.d(TAG, "Apparently we started the service");
            return true;
        } else {
            // Error, we're not online!
            Intent errorIntent = new Intent();
            errorIntent.setAction(Constants.IntentActions.ACTION_ERROR);
            errorIntent.putExtra(Constants.IntentExtras.MESSAGE, Constants.IntentExtras.ERROR_NO_NETWORK);
            mContext.sendBroadcast(errorIntent); // Let the Activity/Fragment know we have no Internet Access
            return false;
        }
    }

    public void doLogin(String username, String password) {
        Intent loginIntent = new Intent();
        loginIntent.setAction(Constants.IntentActions.AUTHENTICATION); // Specifies which Processor to use (LoginProcessor)
        loginIntent.putExtra(Constants.IntentExtras.REQUEST_ID, Constants.ApiRequestId.LOGIN);
        loginIntent.putExtra(Constants.IntentExtras.USERNAME, username);
        loginIntent.putExtra(Constants.IntentExtras.PASSWORD, password);
        startService(loginIntent);
    }

    public void doSignUp(String username, String password, String email, String phoneNumber) {
        Intent signUpIntent = new Intent();
        signUpIntent.setAction(Constants.IntentActions.AUTHENTICATION);
        signUpIntent.putExtra(Constants.IntentExtras.REQUEST_ID, Constants.ApiRequestId.SIGN_UP);
        signUpIntent.putExtra(Constants.IntentExtras.USERNAME, username);
        signUpIntent.putExtra(Constants.IntentExtras.PASSWORD, password);
        signUpIntent.putExtra(Constants.IntentExtras.EMAIL, email);
        signUpIntent.putExtra(Constants.IntentExtras.PHONE_NUMBER, phoneNumber);
        startService(signUpIntent);
    }

    public void doLogout() {
        Intent logoutIntent = new Intent();
        logoutIntent.setAction(Constants.IntentActions.AUTHENTICATION);
        logoutIntent.putExtra(Constants.IntentExtras.REQUEST_ID, Constants.ApiRequestId.LOGOUT);
        startService(logoutIntent);
    }
}
