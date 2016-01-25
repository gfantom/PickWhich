package com.piedpiper1337.pickwhich.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.piedpiper1337.pickwhich.R;
import com.piedpiper1337.pickwhich.callbacks.NavigationCallback;
import com.piedpiper1337.pickwhich.callbacks.PhotoInteractionCallback;

/**
 * PhotoFragment is an instance of
 *
 * created by cary on 01/24/2016
 * */
public class PhotoFragment extends BaseFragment {
    private static String TAG = PhotoFragment.class.getSimpleName();

    private NavigationCallback mNavigationCallback;
    private PhotoInteractionCallback mPhotoInteractionCallback;


    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoFragment.
     */
    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationCallback && activity instanceof PhotoInteractionCallback) {
            mNavigationCallback = (NavigationCallback) activity;
            mPhotoInteractionCallback = (PhotoInteractionCallback) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + "must implement NavigationCallback and PhotoInteractionCallback");
        }
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

}
