package com.piedpiper1337.pickwhich.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseUser;
import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.adapters.InboxFragmentPagerAdapter;
import com.piedpiper1337.pickwhich.callbacks.NavigationCallback;
import com.piedpiper1337.pickwhich.callbacks.RESTApiBroadcastReceiver;
import com.piedpiper1337.pickwhich.callbacks.RESTApiProcessorCallback;
import com.piedpiper1337.pickwhich.fragments.InboxFragment;
import com.piedpiper1337.pickwhich.fragments.dummy.DummyContent;
import com.piedpiper1337.pickwhich.service.ServiceHelper;
import com.piedpiper1337.pickwhich.utils.Constants;

public class HomeActivity extends BaseActivity implements
        RESTApiProcessorCallback,
        InboxFragment.OnListFragmentInteractionListener,
        NavigationCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();

    // To subscribe to broadcasts
    private RESTApiBroadcastReceiver mReceiver;
    private IntentFilter mFilter;

    private ViewPager mInboxViewPager;
    private InboxFragmentPagerAdapter mInboxFragmentPagerAdapter;
    private TabLayout mTabLayout;
    private ParseUser mCurrentUser;
    private Toolbar mToolbar;

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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
        mCurrentUser = ParseUser.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Transition to camera Fragment
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        mInboxViewPager = (ViewPager) findViewById(R.id.inbox_viewpager);
        mInboxFragmentPagerAdapter = new InboxFragmentPagerAdapter(getSupportFragmentManager(), this);
        mInboxFragmentPagerAdapter.addFragment(InboxFragment.newInstance(1), "Inbox", R.drawable.ic_inbox_white_24dp);
        mInboxFragmentPagerAdapter.addFragment(InboxFragment.newInstance(1), "Sent", R.drawable.ic_inbox_white_24dp);
        mInboxViewPager.setAdapter(mInboxFragmentPagerAdapter);

        mTabLayout.setupWithViewPager(mInboxViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_inbox_white_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_send_white_24dp);

        if (mCurrentUser != null) {
            setTitle(mCurrentUser.getUsername() + "'s Picks");
        }
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

    }

    @Override
    public void goToInbox() {

    }

    @Override
    public void nextPhoto() {

    }
}
