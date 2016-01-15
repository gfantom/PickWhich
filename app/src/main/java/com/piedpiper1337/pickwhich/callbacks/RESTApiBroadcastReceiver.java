package com.piedpiper1337.pickwhich.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.piedpiper1337.pickwhich.utils.Constants;

/**
 * BroadcastReceiver to handle API Intents
 * <p/>
 * Activities and Fragments that interact
 * with the API should use one of these
 * <p/>
 * Created by cary on 1/4/16.
 */
public class RESTApiBroadcastReceiver extends BroadcastReceiver {

    private RESTApiProcessorCallback mCallback;

    public RESTApiBroadcastReceiver(RESTApiProcessorCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.IntentActions.ACTION_SUCCESS)) {
            mCallback.onHttpRequestComplete(intent);
        } else if (intent.getAction().equals(Constants.IntentActions.ACTION_ERROR)) {
            mCallback.onHttpResponseError(intent);
        }
    }
}
