package com.piedpiper1337.pickwhich.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cary on 1/25/16.
 */
public class PickInstance implements Serializable {

    private List<Bitmap> mImages;

    public PickInstance() {
        mImages = new ArrayList<>();
    }

    public void addImage(Bitmap image) {
        mImages.add(image);
    }

    public List<Bitmap> getImages() {
        return mImages;
    }
}
