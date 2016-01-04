package com.piedpiper1337.pickwhich.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by cary on 1/2/16.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * @return class name
     */
    public abstract String getTagName();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        logMethodName("onHiddenChanged()");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        logMethodName("onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethodName("onCreate()");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethodName("onViewCreated()");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logMethodName("onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        logMethodName("onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        logMethodName("onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        logMethodName("onResume()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        logMethodName("onConfigurationChanged()");
    }

    @Override
    public void onPause() {
        super.onPause();
        logMethodName("onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        logMethodName("onStop()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        logMethodName("onLowMemory()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethodName("onDestroy()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logMethodName("onDestroyView()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        logMethodName("onDetach()");
    }

    private void logMethodName(String methodName){
        Log.d(getTag(), ">>>>>>>>>> " + methodName + " in " + getTag() + " <<<<<<<<<<");
    }
}

