package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Favorit extends AppCompatActivity {

    private ListView favoritesListView;
    private ArrayAdapter<String> favoritesAdapter;
    private List<String> favoritesList;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorit);

        favoritesListView = findViewById(R.id.listView);
        favoritesList = new ArrayList<>();
        favoritesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoritesList);
        favoritesListView.setAdapter(favoritesAdapter);

        databaseHelper = new DatabaseHelper(this);
        getFavorites();

        favoritesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAudio = favoritesList.get(position);
            String audioFilePath = getAudioFilePath(selectedAudio);

            // Create an intent to navigate back to the MusicPlayer activity
            Intent intent = new Intent(Favorit.this, MusicPlayer.class);
            intent.putExtra("audioTitle", selectedAudio);
            intent.putExtra("audioFilePath", audioFilePath);
            intent.putStringArrayListExtra("audioFilesList", (ArrayList<String>) favoritesList);
            intent.putExtra("currentAudioIndex", position);
            startActivity(intent);
        });
    }

    private String getAudioFilePath(String audioTitle) {
        // Retrieve audio file path based on the audio title
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

    private void getFavorites() {
        favoritesList.clear();
        favoritesList.addAll(databaseHelper.getFavorites());
        favoritesAdapter.notifyDataSetChanged();
    }
}