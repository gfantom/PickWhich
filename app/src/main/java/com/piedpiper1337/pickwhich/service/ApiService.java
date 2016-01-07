package com.piedpiper1337.pickwhich.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by cary on 1/6/16.
 */
public class ApiService extends IntentService {

    public ApiService() {
        super("ApiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO: Use ProcessorFactory to create appropriate Processor
    }
}
