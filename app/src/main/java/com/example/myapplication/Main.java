package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ListView listView;
    private List<String> audioFilesList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.favorites) {
            Intent intent = new Intent(this, Favorit.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.download) {
            Intent intent = new Intent(this, Download.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);


                listView = findViewById(R.id.list_view);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {

                fetchAudioFiles();
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedAudioTitle = audioFilesList.get(position);
                    String selectedAudioFilePath = getAudioFilePath(selectedAudioTitle);

                    if (selectedAudioFilePath != null) {
                        Intent intent = new Intent(Main.this, MusicPlayer.class);
                        intent.putExtra("audioTitle", selectedAudioTitle);
                        intent.putExtra("audioFilePath", selectedAudioFilePath);
                        intent.putStringArrayListExtra("audioFilesList", (ArrayList<String>) audioFilesList);
                        intent.putExtra("currentAudioIndex", position);
                        startActivity(intent);
                    } else {
                        // Handle the case when the audio file path is not available or invalid
                        Toast.makeText(Main.this, "Invalid audio file path", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }

    private String getAudioFilePath(String audioTitle) {
        String[] projection = {MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media.TITLE + "=?";
        String[] selectionArgs = {audioTitle};
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            int filePathColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            String audioFilePath = cursor.getString(filePathColumnIndex);
            cursor.close();
            return audioFilePath;
        }

        return null;
    }

    private void fetchAudioFiles() {
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = getContentResolver().query(audioUri, projection, null, null, sortOrder);

        audioFilesList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            do {

                if (titleColumnIndex != -1) {
                    String audioTitle = cursor.getString(titleColumnIndex);
                    audioFilesList.add(audioTitle);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                audioFilesList
        );
        listView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAudioFiles();
            } else {

            }
        }
    }





}







