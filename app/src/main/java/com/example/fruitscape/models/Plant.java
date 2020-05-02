package com.example.fruitscape.models;

public class Plant {

    private int mImageResourceId, mClassLabelId;

    public Plant(int imageResourceId, int classLabelId) {
        mImageResourceId = imageResourceId;
        mClassLabelId = classLabelId;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public int getClassLabelId() {
        return mClassLabelId;
    }

}
