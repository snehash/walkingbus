package com.ut.walkingbus.walkingbus;


import android.media.Image;
import android.net.Uri;

public class Child {
    private String mName;
    private Uri mPicture;
    private String mStatus;
    private String mChaperoneNumber;
    private String mChaperoneName;

    public Child (String name, Uri picture, String status, String chaperoneNumber, String chaperoneName) {
        mName = name;
        mPicture = picture;
        mStatus = status;
        mChaperoneNumber = chaperoneNumber;
        mChaperoneName = chaperoneName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPicture(Uri image) {
        mPicture = image;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public void setChaperoneNumber(String number) {
        mChaperoneNumber = number;
    }

    public void setChaperoneName(String name) {
        mChaperoneName = name;
    }

    public String getName() {
        return mName;
    }

    public String getStatus() {
        return mStatus;
    }

    public Uri getPicture() {
        return mPicture;
    }

    public String getChaperoneNumber() {
        return mChaperoneNumber;
    }

    public String getChaperoneName() {
        return mChaperoneName;
    }
}
