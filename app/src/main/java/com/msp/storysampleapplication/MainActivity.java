package com.msp.storysampleapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_EXT_STORAGE_PERMISSIONS = 1;
    public static final int PICK_IMAGE = 2;
    public static final String SELECTED_DATA_FROM_GALLERY = "SELECTED_DATA_FROM_GALLERY";

    ArrayList<String> multiImageList;
    Uri selectedMediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPickImage = findViewById(R.id.btn_pick_image);
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    accessPermission();
                }

                Intent multiPickIntent = new Intent();
                multiPickIntent.setType("*/*");
                multiPickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                multiPickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                multiPickIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(multiPickIntent, "Select Picture"), PICK_IMAGE);
            }
        });

    }

    // Ask permission from user to access gallery items/data.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void accessPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXT_STORAGE_PERMISSIONS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            multiImageList = new ArrayList<>();

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                Log.d(TAG, "onActivityResult: ClipData "+mClipData.getItemCount());
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    selectedMediaUri = item.getUri();
                    Log.d(TAG, "onActivityResult: selectedMediaUri " + selectedMediaUri);

                    multiImageList.add(selectedMediaUri.toString());
                }
                Log.d(TAG, "onActivityResult: multiImageList " + multiImageList);

                Intent intent = new Intent(this, EditingActivity.class);
                Gson gson = new Gson();
                intent.putExtra(SELECTED_DATA_FROM_GALLERY, gson.toJson(multiImageList));
                startActivity(intent);
                //setAdapter(multiImageList);
            }
        }
    }

}
