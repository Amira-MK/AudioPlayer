package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends AppCompatActivity {

    private TextView audioTitleTextView;
    private ImageButton playPauseButton;

    private ImageButton previousButton;
    private ImageButton nextButton;

    private List<String> audioFilesList;
    private int currentAudioIndex;


    private MediaPlayer mediaPlayer;


    private String audioTitle;
    private String audioFilePath;

    private boolean isPlaying = false;

    private ImageButton addToFavoritesButton;
    private ListView favoritesListView;
    private ArrayAdapter<String> favoritesAdapter;

    private DatabaseHelper databaseHelper;


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
        setContentView(R.layout.activity_music_player);

        audioTitleTextView = findViewById(R.id.textView);
        playPauseButton = findViewById(R.id.imageButton3);
        previousButton = findViewById(R.id.imageButton);
        nextButton = findViewById(R.id.imageButton2);

        // Get audio file details from the intent
        audioTitle = getIntent().getStringExtra("audioTitle");
        audioFilePath = getIntent().getStringExtra("audioFilePath");
        audioFilesList = getIntent().getStringArrayListExtra("audioFilesList");
        currentAudioIndex = getIntent().getIntExtra("currentAudioIndex", 0);

        audioTitleTextView.setText(audioTitle);


        databaseHelper = new DatabaseHelper(this);


        addToFavoritesButton = findViewById(R.id.imageButton4);



        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedAudio = audioFilesList.get(currentAudioIndex);
                addToFavorites(selectedAudio);
            }
        });



        // Set up favorites ListView and adapter
//        favoritesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFavorites());
//        favoritesListView.setAdapter(favoritesAdapter);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.play);
        });

        playPauseButton.setOnClickListener(view -> {
            if (isPlaying) {
                pauseAudio();
            } else {
                playAudio();
            }
        });

        previousButton.setOnClickListener(view -> {
            previousAudio();
        });

        nextButton.setOnClickListener(view -> {
            nextAudio();
        });

    }

    private void addToFavorites(String audio) {
        boolean success = databaseHelper.addFavorite(audio);

        if (success) {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
        }
    }
//
//    private List<String> getFavorites() {
//        // Retrieve the list of favorite audio items from the SQLite database
//        return databaseHelper.getFavorites();
//    }
//
//    private void updateFavoritesList() {
//        // Update the favorites list and notify the adapter
//        List<String> favorites = getFavorites();
//        favoritesAdapter.clear();
//        favoritesAdapter.addAll(favorites);
//        favoritesAdapter.notifyDataSetChanged();
//    }


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

    private void nextAudio(){

        if (currentAudioIndex < audioFilesList.size() - 1) {
            currentAudioIndex++;
        } else {
            currentAudioIndex = 0;
        }
        audioFilePath = getAudioFilePath(audioFilesList.get(currentAudioIndex));
        audioTitleTextView.setText(audioFilesList.get(currentAudioIndex));
        mediaPlayer.reset();
        playAudio();

    }

    private void previousAudio(){
        if (currentAudioIndex > 0) {
            currentAudioIndex--;
        } else {
            currentAudioIndex = audioFilesList.size() - 1;
        }
        audioFilePath = getAudioFilePath(audioFilesList.get(currentAudioIndex));
        audioTitleTextView.setText(audioFilesList.get(currentAudioIndex));
        mediaPlayer.reset();
        playAudio();
    }


    private void playAudio() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.pause);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel("audio_channel", "Audio Channel", NotificationManager.IMPORTANCE_LOW);
//                NotificationManager notificationManager = getSystemService(NotificationManager.class);
//                notificationManager.createNotificationChannel(channel);
//            }

            Intent playIntent = new Intent(this, MusicPlayerNotificationReceiver.class);
            playIntent.setAction("play");
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent previousIntent = new Intent(this, MusicPlayerNotificationReceiver.class);
            previousIntent.setAction("previous");
            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent(this, MusicPlayerNotificationReceiver.class);
            nextIntent.setAction("next");
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent closeIntent = new Intent(this, MusicPlayerNotificationReceiver.class);
            closeIntent.setAction("close");
            PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "audio_channel")
                    .setSmallIcon(R.drawable.audioo)
                    .setContentTitle("Audio Player")
                    .setContentText(audioTitle)
                    .setContentIntent(playPendingIntent)
                    .addAction(R.drawable.previous, "Previous", previousPendingIntent)
                    .addAction(isPlaying ? R.drawable.pause : R.drawable.play, isPlaying ? "Pause" : "Play", playPendingIntent)
                    .addAction(R.drawable.next, "Next", nextPendingIntent)
                    .addAction(R.drawable.audioo, "Close", closePendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setAutoCancel(false);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.play);
        }
    }

    private String getAudioTitleFromPath(String filePath) {
        int separatorIndex = filePath.lastIndexOf(File.separator);
        if (separatorIndex != -1) {
            return filePath.substring(separatorIndex + 1);
        }
        return filePath;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }




}