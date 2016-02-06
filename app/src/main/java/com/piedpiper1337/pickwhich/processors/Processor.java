package com.piedpiper1337.pickwhich.processors;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.piedpiper1337.pickwhich.utils.Constants;
import com.piedpiper1337.pickwhich.utils.SharedPreferenceUtil;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okio.Buffer;

/**
 * Base Processor class
 * Contains all the basic REST verbs
 * <p/>
 * Created by cary on 1/4/16.
 */
public abstract class Processor {

    public static final MediaType JSON = MediaType.parse("application/json");

    protected Context mContext;

    public abstract void execute(Intent intent);

    public abstract int getRequestId();

    public abstract String getTag();

    /**
     * Perform OAUTH steps and return the result (also broadcast results to subscribed BroadcastReceivers)
     *
     * TODO: ACTUALLY IMPLEMENT OAUTH
     * */
    protected Response postAuth(String url, String json) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
                //.header("Authorization", basicAuth)
                .build();
        JSONObject requestBody = bodyToString(request);
        Log.i(getTag(), getTag() + " Request Body " + requestBody.toString(4));

        Response response = client.newCall(request).execute();

        if(!response.isSuccessful()) {
            Log.e(getTag(), getTag() + " Error executing " + request.uri() + " response code " + response.code());
            broadcast(Constants.IntentActions.ACTION_ERROR, getTag() + " Error executing " + request.uri() + " response code " + response.code(), null);
        }

        return response;
    }

    protected String get(String url) throws IOException, JSONException, ApiException {
        String authToken = SharedPreferenceUtil.readPreference(mContext, Constants.SharedPreferenceKeys.OAUTH_TOKEN, "");
        String responseString = "";

        if (TextUtils.isEmpty(authToken)) {
            return null;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + authToken) // Assuming we have done postAuth
                .build();

        Response response = client.newCall(request).execute();
        responseString = response.body().string();

        if(!response.isSuccessful()) {
            handleError(response.code(), request.uri() + "", responseString);
        }

        return responseString;
    }

    protected String post(String url, String jsonData) throws JSONException, IOException, ApiException {
        String responseString = "";

        OkHttpClient client = new OkHttpClient();

        String authToken = SharedPreferenceUtil.readPreference(mContext, Constants.SharedPreferenceKeys.OAUTH_TOKEN, "");

        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + authToken)
                .build();
        JSONObject requestBody = bodyToString(request);
        Log.i(getTag(), getTag() + " Request Body " + requestBody.toString(4));

        Response response = client.newCall(request).execute();
        responseString = response.body().string();

        if(!response.isSuccessful()) {
            handleError(response.code(), request.uri()+"", responseString);
        }

        return responseString;
    }

    protected String put(String url, String jsonData) throws JSONException, IOException, ApiException {
        OkHttpClient client = new OkHttpClient();
        String responseString = "";

        String authToken = SharedPreferenceUtil.readPreference(mContext, Constants.SharedPreferenceKeys.OAUTH_TOKEN, "");

        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", "Bearer " + authToken)
                .build();
        JSONObject requestBody = bodyToString(request);
        Log.i(getTag(), getTag() + " Request Body " + requestBody.toString(4));

        Response response = client.newCall(request).execute();
        responseString = response.body().string();

        if(!response.isSuccessful()) {
            handleError(response.code(), request.uri()+"", responseString);
        }

        return responseString;
    }

    protected String delete(String url) throws IOException, JSONException, ApiException {
        String authToken = SharedPreferenceUtil.readPreference(mContext, Constants.SharedPreferenceKeys.OAUTH_TOKEN, "");
        String responseString = "";

        if (TextUtils.isEmpty(authToken)) {
            return null;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .delete()
                .url(url)
                .header("Authorization", "Bearer " + authToken) // Assuming we have done postAuth
                .build();

        Response response = client.newCall(request).execute();
        responseString = response.body().string();

        if(!response.isSuccessful()) {
            handleError(response.code(), request.uri() + "", responseString);
        }

        return responseString;
    }

    private static JSONObject bodyToString(Request request) throws JSONException {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            JSONObject obj = new JSONObject(buffer.readUtf8());
            return obj;
        } catch (final IOException e) {
            return null;
        }
    }

    private void handleError(int responseCode, String uri, String responseString) throws JSONException, ApiException {
        String msg = "";

        if(!TextUtils.isEmpty(responseString)){
            JSONObject errorObj = new JSONObject(responseString);
            if(responseCode == 500){
                JSONObject srvErrorObj = (JSONObject) errorObj.getJSONArray(Constants.JsonKeys.ERRORS).get(0);
                msg = srvErrorObj.getString(Constants.JsonKeys.MESSAGE);
            }
        }

        Log.e(getTag(), getTag() + " Error executing " + uri + " \nresponse code " + responseCode + " \nMessage: " + msg);

        broadcast(Constants.IntentActions.ACTION_ERROR, getTag() + " <p><b>Error executing:</b> " + uri + " </p><p><b>Response code:</b> " + responseCode + " </p><b>Message:</b> " + msg, null);

        throw new ApiException("Response Code: " + responseCode + " Message : " + msg);
    }

    /**
     * Broadcast the result of a request to subscribed Activities and Fragments
     *
     * @param action       Intent Action (use Constants.IntentActions)
     * @param message      Message to broadcast
     * @param jsonResponse The Json Response DUH (can be null)
     */
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
     * @param tag       name of the Processor
     * @param requestId the request id
     */
    public void logExecute(String tag, int requestId) {
        Log.d(tag, "-_-_-_-_-_-_ " + tag + " calling request id " + requestId + " -_-_-_-_-_-_");
    }
}
