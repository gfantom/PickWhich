package com.piedpiper1337.pickwhich.callbacks;

import android.content.Intent;

/**
 * Created by cary on 1/4/16.
 */
public interface ApiProcessorCallback {

    /**
     * Called from the broadcast receiver on an error
     *
     * @param intent
     */
    public void onHttpResponseError(Intent intent);

    /**
     * Called from the broadcast receiver on success
     *
     * @param intent
     */
    public void onHttpRequestComplete(Intent intent);
}
