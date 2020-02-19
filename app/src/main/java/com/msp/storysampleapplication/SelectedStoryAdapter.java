package com.msp.storysampleapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SelectedStoryAdapter extends RecyclerView.Adapter<SelectedStoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> selectedImageArrayList;
    private ItemClickListener itemClickListener;

    private int currentSelected = 0;
    private int prevIndex = -1;

    public SelectedStoryAdapter(Context context, ArrayList<String> selectedImageModelArrayList,
                                ItemClickListener itemClickListener) {
        this.context = context;
        this.selectedImageArrayList = selectedImageModelArrayList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rooView = LayoutInflater.from(context).inflate(R.layout.item_selected_story, parent, false);
        return new ViewHolder(rooView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context)
                .load(selectedImageArrayList.get(position))
                .into(holder.ivStorySelecting);

        if (currentSelected == position) {
            itemClickListener.onItemSelected(selectedImageArrayList.get(position), position, prevIndex);
        }
    }

    @Override
    public int getItemCount() {
        return selectedImageArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivStorySelecting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivStorySelecting = itemView.findViewById(R.id.iv_story_selecting);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION && itemClickListener != null) {
                prevIndex = currentSelected;
                currentSelected = getAdapterPosition();
                notifyDataSetChanged();
            }
        }
    }

    public interface ItemClickListener {
        void onItemSelected(String uri, int position, int prevIndex);
    }
}
