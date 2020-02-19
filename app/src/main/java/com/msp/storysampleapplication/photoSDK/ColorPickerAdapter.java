package com.msp.storysampleapplication.photoSDK;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.msp.storysampleapplication.R;

import java.util.ArrayList;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Integer> colorPickerColorsArrayList;
    private OnColorPickerClickListener onColorPickerClickListener;

    public ColorPickerAdapter(Context context, ArrayList<Integer> colorPickerColorsArrrayList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.colorPickerColorsArrayList = colorPickerColorsArrrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_color_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        buildColorPickerView(holder.viewColorPicker, colorPickerColorsArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorPickerColorsArrayList.size();
    }

    private void buildColorPickerView(View view, int colorCode) {
        view.setVisibility(View.VISIBLE);

        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(20);
        biggerCircle.setIntrinsicWidth(20);
        biggerCircle.setBounds(new Rect(0, 0, 20, 20));
        biggerCircle.getPaint().setColor(colorCode);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(5);
        smallerCircle.setIntrinsicWidth(5);
        smallerCircle.setBounds(new Rect(0, 0, 5, 5));
        smallerCircle.getPaint().setColor(Color.WHITE);
        smallerCircle.setPadding(5, 5, 5, 5);
        Drawable[] drawables = {smallerCircle, biggerCircle};

        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        view.setBackground(layerDrawable);
    }

    public void setOnColorPickerClickListener(OnColorPickerClickListener onColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewColorPicker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewColorPicker = itemView.findViewById(R.id.view_color_picker);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onColorPickerClickListener != null)
                        onColorPickerClickListener.onColorPickerClickListener(colorPickerColorsArrayList.get(ViewHolder.this.getAdapterPosition()));
                }
            });
        }
    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode);
    }
}
