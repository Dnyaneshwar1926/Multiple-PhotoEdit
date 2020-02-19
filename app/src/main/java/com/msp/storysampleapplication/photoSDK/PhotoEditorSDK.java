package com.msp.storysampleapplication.photoSDK;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msp.storysampleapplication.BrushDrawingView;
import com.msp.storysampleapplication.EditingActivity;
import com.msp.storysampleapplication.R;

import java.util.ArrayList;

public class PhotoEditorSDK implements MultiTouchListener.OnMultiTouchListener {

    private Context context;
    private RelativeLayout parentView;
    private ImageView imageView;
    private View deleteView;
    private BrushDrawingView brushDrawingView;
    private ArrayList<View> addedViews;
    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;
    private View addTextRootView;

    private PhotoEditorSDK(PhotoEditorSDKBuilder photoEditorSDKBuilder) {
        this.context = photoEditorSDKBuilder.context;
        this.parentView = photoEditorSDKBuilder.parentView;
        this.imageView = photoEditorSDKBuilder.imageView;
        this.deleteView = photoEditorSDKBuilder.deleteView;
        this.brushDrawingView = photoEditorSDKBuilder.brushDrawingView;
        addedViews = new ArrayList<>();
    }

    public void addText(String text, int colorCodeTextView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            addTextRootView = inflater.inflate(R.layout.photo_editor_sdk_text_item_list, null);
            TextView addTextView = addTextRootView.findViewById(R.id.photo_editor_sdk_text_tv);
            addTextView.setTag(EditingActivity.current_position);
//            addTextView.setGravity(Gravity.CENTER);
            addTextView.setText(text);
            if (colorCodeTextView != -1)
                addTextView.setTextColor(colorCodeTextView);
            MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                    parentView, this.imageView, onPhotoEditorSDKListener);
            multiTouchListener.setOnMultiTouchListener(this);
            addTextRootView.setOnTouchListener(multiTouchListener);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            parentView.addView(addTextRootView, params);
            parentView.setTag(EditingActivity.current_position);
            addedViews.add(addTextRootView);
            if (onPhotoEditorSDKListener != null)
                onPhotoEditorSDKListener.onAddViewListener(ViewType.TEXT, addedViews.size());
        } else {
            Toast.makeText(context, "Something went wrong inflating layout. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
        this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;
        brushDrawingView.setOnPhotoEditorSDKListener(onPhotoEditorSDKListener);
    }

    @Override
    public void onEditTextClickListener(View view, String text, int colorCode) {
        if (addTextRootView != null && addedViews.contains(view)) {
            parentView.removeView(view);
            addedViews.remove(view);
        }
    }

    @Override
    public void onRemoveViewListener(View removedView) {

    }

    public static class PhotoEditorSDKBuilder {

        private Context context;
        private RelativeLayout parentView;
        private ImageView imageView;
        private View deleteView;
        private BrushDrawingView brushDrawingView;

        public PhotoEditorSDKBuilder(Context context) {
            this.context = context;
        }

        public PhotoEditorSDKBuilder parentView(RelativeLayout parentView) {
            this.parentView = parentView;
            return this;
        }

        public PhotoEditorSDKBuilder childView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public PhotoEditorSDKBuilder deleteView(View deleteView) {
            this.deleteView = deleteView;
            return this;
        }

        public PhotoEditorSDKBuilder brushDrawingView(BrushDrawingView brushDrawingView) {
            this.brushDrawingView = brushDrawingView;
            return this;
        }

        public PhotoEditorSDK buildPhotoEditorSDK() {
            return new PhotoEditorSDK(this);
        }
    }
}
