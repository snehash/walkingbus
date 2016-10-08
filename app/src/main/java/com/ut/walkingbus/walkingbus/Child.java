package com.ut.walkingbus.walkingbus;


import android.media.Image;

public class Child {
    private String mName;
    private Image mPicture;
    private String mStatus;

    public Child (String name, Image picture, String status) {
        mName = name;
        mPicture = picture;
        mStatus = status;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPicture(Image image) {
        mPicture = image;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getName() {
        return mName;
    }

    public String getStatus() {
        return mStatus;
    }

    public Image getPicture() {
        return mPicture;
    }
}
