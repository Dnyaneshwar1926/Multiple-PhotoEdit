package com.msp.storysampleapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditingActivity extends AppCompatActivity implements SelectedStoryAdapter.ItemClickListener {
    private static final String TAG = "EditingActivity";

    ArrayList<String> multiImageList;

    ViewPager2 viewPager;
    RecyclerView recyclerView;
    ImageView ivDelete;

    private StoryViewPagerAdapter storyViewPagerAdapter;
    private SelectedStoryAdapter selectedStoryAdapter;

    public static int current_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        multiImageList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String jsonString = bundle.getString(MainActivity.SELECTED_DATA_FROM_GALLERY);
            Gson gson = new Gson();
            Type listOfSelectedImageType = new TypeToken<ArrayList<String>>() {
            }.getType();
            multiImageList = gson.fromJson(jsonString, listOfSelectedImageType);
        }

        Log.d(TAG, "onCreate: multiImageList " + multiImageList);

        initialiseUIElements();
    }

    private void initialiseUIElements() {
        viewPager = findViewById(R.id.viewPager);
        recyclerView = findViewById(R.id.recyclerView);
        ivDelete = findViewById(R.id.iv_delete);

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiImageList.remove(current_position);
                storyViewPagerAdapter.notifyDataSetChanged();
                selectedStoryAdapter.notifyItemRemoved(current_position);
            }
        });

        setSelectingImageAdapter();
        setStoryViewPageAdapter();
    }

    private void setSelectingImageAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        selectedStoryAdapter = new SelectedStoryAdapter(this, multiImageList, this);
        recyclerView.setAdapter(selectedStoryAdapter);
    }

    /**
     * This adapter sets the viewpager2 for editing
     */
    private void setStoryViewPageAdapter() {
        viewPager.setUserInputEnabled(false);
        storyViewPagerAdapter = new StoryViewPagerAdapter(this, multiImageList);
        viewPager.setAdapter(storyViewPagerAdapter);
        storyViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(String uri, int position, int prevIndex) {

        current_position = position;
        if (storyViewPagerAdapter == null) {
            setStoryViewPageAdapter();
        } else {
            viewPager.setCurrentItem(position, false);
            if (prevIndex != position)
                storyViewPagerAdapter.notifyItemChanged(position);
        }

    }
}
