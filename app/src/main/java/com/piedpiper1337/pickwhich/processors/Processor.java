package com.piedpiper1337.pickwhich.processors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.piedpiper1337.pickwhich.utils.Constants;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONObject;

/**
 * Base Processor class
 * Contains all the basic REST verbs
 *
 * Created by cary on 1/4/16.
 */
public abstract class Processor {

    protected Context mContext;

    public abstract void execute(Intent intent);

    public abstract int getRequestId();

    public abstract String getTag();

    protected String get() {
        return "";
    }

    protected String post(String url, String json) {
        OkHttpClient client = new OkHttpClient();

        return "";
    }

    protected String put() {
        return "";
    }

    protected String delete() {
        return "";
    }

    private void handleError() {

    }

    /**
     * Broadcast the result of a request to subscribed Activities and Fragments
     *
     * @param action Intent Action (use Constants.IntentActions)
     * @param message Message to broadcast
     * @param jsonResponse The Json Response DUH (can be null)
     * */
    protected void broadcast(String action, String message, JSONObject jsonResponse) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(Constants.IntentExtras.MESSAGE, message);

        if (jsonResponse != null) {
            intent.putExtra(Constants.IntentExtras.JSON_RESPONSE, jsonResponse.toString());
        }

        intent.putExtra(Constants.IntentExtras.REQUEST_ID, getRequestId());
        mContext.sendBroadcast(intent); // FIRE ZE INTENT PEW
    }

    /**
     * Log the execution given name of executing processor (tag) and request id
     *
     * @param tag name of the Processor
     * @param requestId the request id
     * */
    public void logExecute(String tag, int requestId) {
        Log.d(tag, "-_-_-_-_-_-_ " + tag + " calling request id " + requestId + " -_-_-_-_-_-_");
    }
}
