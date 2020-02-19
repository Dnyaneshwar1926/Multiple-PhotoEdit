package com.msp.storysampleapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.msp.storysampleapplication.photoSDK.ColorPickerAdapter;
import com.msp.storysampleapplication.photoSDK.OnPhotoEditorSDKListener;
import com.msp.storysampleapplication.photoSDK.PhotoEditorSDK;
import com.msp.storysampleapplication.photoSDK.ViewType;

import java.io.IOException;
import java.util.ArrayList;

public class StoryViewPagerAdapter extends RecyclerView.Adapter<StoryViewPagerAdapter.ViewPager> implements OnPhotoEditorSDKListener {
    private static final String TAG = "StoryViewPagerAdapter";

    private Context context;
    private ArrayList<String> selectedImageArrayList;

    private ArrayList<Integer> colorPickerColorsArrayList;
    private int colorCodeTextView = -1;

    private PhotoEditorSDK photoEditorSDK;

    public StoryViewPagerAdapter(Context context, ArrayList<String> selectedImageModelArrayList) {
        this.context = context;
        selectedImageArrayList = selectedImageModelArrayList;
    }

    @NonNull
    @Override
    public ViewPager onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_story_view_pager, parent, false);
        return new ViewPager(rootView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewPager holder, int position) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(selectedImageArrayList.get(position)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Generate the palette and get the vibrant swatch
        // This will pick the colors from the bitmap
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = p.getLightMutedSwatch();
        if (vibrantSwatch != null) {
            holder.constraintLayout.setBackgroundColor(vibrantSwatch.getRgb());
        }

        Glide.with(context)
                .load(bitmap)
                .into(holder.ivStoryImage);

        //This will set the text, drawing with listeners for the view PhotoEditorSDK.
        photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(context)
                .parentView(holder.relativeLayoutParentImage) // add parent image view
                .childView(holder.ivStoryImage) // add the desired image view
                .deleteView(holder.ivDelete) // add the deleted view that will appear during the movement of the views
                .brushDrawingView(holder.viewBrushDrawing) // add the brush drawing view that is responsible for drawing on the image view
                .buildPhotoEditorSDK(); // build photo editor sdk

        photoEditorSDK.setOnPhotoEditorSDKListener(this);
    }

    // Generate palette synchronously and return it
    private Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }

    void sPosition(int pos) {
    }

    /**
     * This open the overlay to write text
     *
     * @param text,      The text written by user
     * @param colorCode, The text color selected by user. Default color will be white
     */
    void openAddTextPopupWindow(String text, int colorCode) {
        Log.d(TAG, "openAddTextPopupWindow: text " + text + " colorCode " + colorCode);
        colorCodeTextView = colorCode;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View addTextPopupWindowRootView = inflater.inflate(R.layout.add_text_popup_window, null);
            final EditText etAddText = addTextPopupWindowRootView.findViewById(R.id.et_add_text);
            TextView tvDone = addTextPopupWindowRootView.findViewById(R.id.tv_done);
            RecyclerView rvColors = addTextPopupWindowRootView.findViewById(R.id.rv_colors);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvColors.setLayoutManager(layoutManager);
            rvColors.setHasFixedSize(true);
            ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(context, colorPickerColorsArrayList);
            colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
                @Override
                public void onColorPickerClickListener(int colorCode) {
                    etAddText.setTextColor(colorCode);
                    colorCodeTextView = colorCode;
                }
            });
            rvColors.setAdapter(colorPickerAdapter);
            if (!text.isEmpty()) {
                etAddText.setText(text);
                etAddText.setTextColor(colorCode == -1 ? context.getResources().getColor(android.R.color.white) : colorCode);
            }
            final PopupWindow pop = new PopupWindow(context);
            pop.setContentView(addTextPopupWindowRootView);
            pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            pop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            pop.setFocusable(true);
            pop.setBackgroundDrawable(null);
            pop.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            tvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addText(etAddText.getText().toString().trim(), colorCodeTextView);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    pop.dismiss();
                }
            });
        }
    }

    /**
     * This will add the text written by user on image
     *
     * @param text,              Written by user
     * @param colorCodeTextView, color selected by user for the text
     */
    private void addText(String text, int colorCodeTextView) {
        Log.d(TAG, "addText: text " + text);
        photoEditorSDK.addText(text, colorCodeTextView);
    }

    @Override
    public int getItemCount() {
        return selectedImageArrayList.size();
    }

    @Override
    public void onEditTextChangeListener(String text, int colorCode) {
        openAddTextPopupWindow(text, colorCode);
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    public class ViewPager extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivStoryImage;
        RelativeLayout relativeLayoutParentImage;
        ImageView ivDelete;
        BrushDrawingView viewBrushDrawing;
        ConstraintLayout constraintLayout;

        ViewPager(@NonNull View itemView) {
            super(itemView);

            relativeLayoutParentImage = itemView.findViewById(R.id.rl_parent_image);

            ivStoryImage = itemView.findViewById(R.id.iv_story_image);
            viewBrushDrawing = itemView.findViewById(R.id.view_drawing);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            initialiseUiElements();
        }

        private void initialiseUiElements() {
            ivStoryImage.setOnClickListener(this);

            //Colors arraylist for changing the color of text, drawing etc
            colorPickerColorsArrayList = new ArrayList<>();
            colorPickerColorsArrayList.add(context.getResources().getColor(android.R.color.white));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.blue_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.brown_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(android.R.color.holo_green_dark));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.orange_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(android.R.color.holo_red_dark));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.red_orange_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.sky_blue_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.violet_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(android.R.color.black));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.yellow_color_picker));
            colorPickerColorsArrayList.add(context.getResources().getColor(R.color.yellow_green_color_picker));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_story_image:
                    openAddTextPopupWindow("", -1);
                    break;

                case R.id.rv_drawing_color_picker:

                    break;

                default:
                    Log.d(TAG, "onClick: WrongClick");
            }
        }


    }
}
