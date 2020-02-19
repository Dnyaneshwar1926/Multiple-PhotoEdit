package com.msp.storysampleapplication.photoSDK;

public interface OnPhotoEditorSDKListener {

    void onEditTextChangeListener(String text, int colorCode);

    void onAddViewListener(ViewType viewType, int numberOfAddedViews);

    void onRemoveViewListener(int numberOfAddedViews);

    void onStartViewChangeListener(ViewType viewType);

    void onStopViewChangeListener(ViewType viewType);
}
