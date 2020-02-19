package com.msp.storysampleapplication;

import com.msp.storysampleapplication.photoSDK.PhotoEditorSDK;

public class StoryModel {

    private PhotoEditorSDK photoEditorSDK;
    private int position;

    public StoryModel(PhotoEditorSDK photoEditorSDK, int position) {
        this.photoEditorSDK = photoEditorSDK;
        this.position = position;
    }

    public PhotoEditorSDK getPhotoEditorSDK() {
        return photoEditorSDK;
    }

    public int getPosition() {
        return position;
    }
}
