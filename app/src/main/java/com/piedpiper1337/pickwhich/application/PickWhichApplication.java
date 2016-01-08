package com.piedpiper1337.pickwhich.application;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.piedpiper1337.pickwhich.R;

/**
 * Created by cary on 1/7/16.
 */
public class PickWhichApplication extends Application {

    private static String TAG = PickWhichApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Initializing parse");
        Parse.initialize(this, getString(R.string.application_id), getString(R.string.client_id));
    }
}
