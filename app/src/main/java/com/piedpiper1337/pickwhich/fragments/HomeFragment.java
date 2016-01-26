package com.piedpiper1337.pickwhich.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;
import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.activities.HomeActivity;
import com.piedpiper1337.pickwhich.adapters.InboxFragmentPagerAdapter;
import com.piedpiper1337.pickwhich.callbacks.NavigationCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BaseFragment {

    private static String TAG = HomeFragment.class.getSimpleName();

    private NavigationCallback mNavigationCallback;
    private Context mContext;

    private ViewPager mInboxViewPager;
    private InboxFragmentPagerAdapter mInboxFragmentPagerAdapter;
    private TabLayout mTabLayout;
    private ParseUser mCurrentUser;
    private Toolbar mToolbar;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof NavigationCallback) {
            mNavigationCallback = (NavigationCallback) activity;
            mContext = activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement NavigationCallbacks!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentUser = ParseUser.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((HomeActivity) mContext).setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.photo_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationCallback.startNewPick();
            }
        });

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        mInboxViewPager = (ViewPager) view.findViewById(R.id.inbox_viewpager);
        mInboxFragmentPagerAdapter = new InboxFragmentPagerAdapter(getFragmentManager(), mContext);
        mInboxFragmentPagerAdapter.addFragment(InboxFragment.newInstance(1), "Inbox", R.drawable.ic_inbox_white_24dp);
        mInboxFragmentPagerAdapter.addFragment(InboxFragment.newInstance(1), "Sent", R.drawable.ic_inbox_white_24dp);
        mInboxViewPager.setAdapter(mInboxFragmentPagerAdapter);

        mTabLayout.setupWithViewPager(mInboxViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_inbox_white_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_send_white_24dp);

        if (mCurrentUser != null) {
            ((HomeActivity) mContext).setTitle(mCurrentUser.getUsername() + "'s Picks");
        }

        return view;
    }

    @Override
    public String getTagName() {
        return TAG;
    }
}
