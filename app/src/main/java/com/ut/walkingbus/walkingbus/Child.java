package com.ut.walkingbus.walkingbus;


import android.net.Uri;

public class Child {
    private String mName;
    private Uri mPicture;
    private String mStatus;
    private String mChaperoneNumber;
    private String mChaperoneName;
    private String mId;
    // TODO: Make status based on R.string.status_x ids instead?
    public Child (String id, String name, Uri picture, String status, String chaperoneNumber, String chaperoneName) {
        mId = id;
        mName = name;
        mPicture = picture;
        mStatus = status;
        mChaperoneNumber = chaperoneNumber;
        mChaperoneName = chaperoneName;
    }

    public Child (Child c) {
        mId = c.getId();
        mName = c.getName();
        mPicture = c.getPicture();
        mStatus = c.getStatus();
        mChaperoneNumber = c.getChaperoneNumber();
        mChaperoneName = c.getChaperoneName();
    }

    public void setId(String id) {mId = id;}

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

    public String getId() { return mId; }

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
