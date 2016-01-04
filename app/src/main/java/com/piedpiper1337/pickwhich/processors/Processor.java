package com.piedpiper1337.pickwhich.processors;

import android.content.Context;
import android.content.Intent;

import com.piedpiper1337.pickwhich.utils.Constants;

import org.json.JSONObject;

/**
 * Created by cary on 1/4/16.
 */
public abstract class Processor {

    protected Context mContext;

    public abstract int getRequestId();

    protected String get() {
        return "";
    }

    protected String post() {
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
}
