package com.piedpiper1337.pickwhich.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.callbacks.NavigationCallback;
import com.piedpiper1337.pickwhich.callbacks.PhotoInteractionCallback;
import com.piedpiper1337.pickwhich.callbacks.RESTApiBroadcastReceiver;
import com.piedpiper1337.pickwhich.callbacks.RESTApiProcessorCallback;
import com.piedpiper1337.pickwhich.fragments.HomeFragment;
import com.piedpiper1337.pickwhich.fragments.InboxFragment;
import com.piedpiper1337.pickwhich.fragments.PhotoFragment;
import com.piedpiper1337.pickwhich.fragments.dummy.DummyContent;
import com.piedpiper1337.pickwhich.service.ServiceHelper;
import com.piedpiper1337.pickwhich.utils.Constants;

public class HomeActivity extends BaseActivity implements
        RESTApiProcessorCallback,
        InboxFragment.OnListFragmentInteractionListener,
        NavigationCallback, PhotoInteractionCallback {

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
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @Override
    public void onHttpResponseError(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        int requestId = intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1);
        String message = intent.getStringExtra(Constants.IntentExtras.MESSAGE);

        if (requestId == Constants.ApiRequestId.LOGOUT) {
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }

        Log.e(getTag(), message);
        showErrorDialog(message);
    }

    @Override
    public void onHttpRequestComplete(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        int requestId = intent.getIntExtra(Constants.IntentExtras.REQUEST_ID, -1);

        if (requestId == Constants.ApiRequestId.LOGOUT) {
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else {
            Log.e(getTag(), "Unhandled HttpRequestComplete from " + intent.getAction());
        }
    }

    private void initUI() {

        HomeFragment homeFragment = HomeFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.home_coordinator_layout, homeFragment, "homeFragment")
                .commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            ServiceHelper.getInstance(HomeActivity.this).doLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void goFullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void returnFromFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public void startNewPick() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.home_coordinator_layout, PhotoFragment.newInstance())
                .addToBackStack(null)
                .commit();
        //goFullScreen();
    }

    @Override
    public void goToInbox() {
        // TODO: Maybe clear the backstack
        returnFromFullScreen();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.home_coordinator_layout, PhotoFragment.newInstance())
                .commit();
    }

    @Override
    public void nextPhoto() {

    }

    @Override
    public void finishedPhoto() {

    }
}
