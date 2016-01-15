package com.piedpiper1337.pickwhich.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.callbacks.RESTApiBroadcastReceiver;
import com.piedpiper1337.pickwhich.callbacks.RESTApiProcessorCallback;
import com.piedpiper1337.pickwhich.utils.Constants;

public class HomeActivity extends BaseActivity implements RESTApiProcessorCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();

    // To subscribe to broadcasts
    private RESTApiBroadcastReceiver mReceiver;
    private IntentFilter mFilter;

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mReceiver = new RESTApiBroadcastReceiver(this);
        mFilter = new IntentFilter();
        mFilter.addAction(Constants.IntentActions.ACTION_ERROR);
        mFilter.addAction(Constants.IntentActions.ACTION_SUCCESS);

        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onHttpResponseError(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        String message = intent.getStringExtra(Constants.IntentExtras.MESSAGE);
        Log.e(getTag(), message);
        showErrorDialog(message);
    }

    @Override
    public void onHttpRequestComplete(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1) == Constants.ApiRequestId.LOGIN) {
            // Successfully logged in, continue to next screen
            startActivity(new Intent(this, HomeActivity.class)); // Start the main app Activity
            finish();
        }
    }

    private void initUI() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Transition to camera Fragment
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
