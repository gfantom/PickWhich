package com.piedpiper1337.pickwhich.service;

import android.content.Context;
import android.content.Intent;

import com.piedpiper1337.pickwhich.utils.CommonUtils;
import com.piedpiper1337.pickwhich.utils.Constants;

/**
 * Singleton Service Helper
 * Manages the starting of the ApiService
 *
 * Created by cary on 1/7/16.
 */
public class ServiceHelper {

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
            intent.setClass(mContext, ApiService.class);
            mContext.startService(intent);
            return true;
        } else {
            // Error we're not online!
            Intent errorIntent = new Intent();
            errorIntent.setAction(Constants.IntentActions.ACTION_ERROR);
            errorIntent.putExtra(Constants.IntentExtras.MESSAGE, Constants.IntentExtras.ERROR_NO_NETWORK);
            mContext.sendBroadcast(errorIntent); // Let the Activity/Fragment know we have no Internet Access
            return false;
        }
    }
}
