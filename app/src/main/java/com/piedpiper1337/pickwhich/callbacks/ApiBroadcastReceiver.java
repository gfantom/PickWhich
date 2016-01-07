package com.piedpiper1337.pickwhich.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.piedpiper1337.pickwhich.utils.Constants;

/**
 * BroadcastReceiver to handle API Intents
 *
 * Activities and Fragments that interact
 * with the API should use one of these
 *
 * Created by cary on 1/4/16.
 */
public class ApiBroadcastReceiver extends BroadcastReceiver {

    private ApiProcessorCallback mCallback;

    public ApiBroadcastReceiver(ApiProcessorCallback callback) {
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
