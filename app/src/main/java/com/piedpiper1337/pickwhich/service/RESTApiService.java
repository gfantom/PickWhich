package com.piedpiper1337.pickwhich.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.piedpiper1337.pickwhich.processors.Processor;
import com.piedpiper1337.pickwhich.processors.ProcessorFactory;
import com.piedpiper1337.pickwhich.utils.Constants;

/**
 * Created by cary on 1/6/16.
 */
public class RESTApiService extends IntentService {

    private static String TAG = RESTApiService.class.getSimpleName();

    public RESTApiService() {
        super("ApiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Use ProcessorFactory to create appropriate Processor
        Log.d(TAG, "ApiService starting " + intent.getAction());
        Processor processor = ProcessorFactory.createProcessor(intent.getAction(), this);
        processor.logExecute(processor.getClass().getSimpleName(),
                intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1));
        processor.execute(intent);
    }
}
