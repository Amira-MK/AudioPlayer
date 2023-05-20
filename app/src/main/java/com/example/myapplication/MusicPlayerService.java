package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.List;

public class MusicPlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private String audioFilePath;

    public static final String ACTION_PLAY = "com.example.myapplication.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.myapplication.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.myapplication.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.myapplication.ACTION_NEXT";
    public static final String CHANNEL_ID = "com.example.myapplication.CHANNEL_ID";
    public static final int NOTIFICATION_ID = 1;

    private List<String> audioFilesList;
    private int currentAudioIndex;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            showNotification();
            stopSelf();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                    if (!isPlaying) {
                        String audioFilePath = intent.getStringExtra("audioFilePath");
                        playAudio(audioFilePath);
                    }
                    break;
                case ACTION_PAUSE:
                    if (isPlaying) {
                        pauseAudio();
                    }
                    break;
                case ACTION_PREVIOUS:
                    if (currentAudioIndex > 0) {
                        currentAudioIndex--;
                    } else {
                        currentAudioIndex = audioFilesList.size() - 1;
                    }
                    String previousAudioFilePath = getAudioFilePath(audioFilesList.get(currentAudioIndex));
                    if (previousAudioFilePath != null) {
                        playAudio(previousAudioFilePath);
                    }
                    break;
                case ACTION_NEXT:
                    if (currentAudioIndex < audioFilesList.size() - 1) {
                        currentAudioIndex++;
                    } else {
                        currentAudioIndex = 0;
                    }
                    String nextAudioFilePath = getAudioFilePath(audioFilesList.get(currentAudioIndex));
                    if (nextAudioFilePath != null) {
                        playAudio(nextAudioFilePath);
                    }
                    break;
            }
        }

        return START_NOT_STICKY;
    }

    private void playAudio(String audioFilePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            showNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            showNotification();
        }
    }

    private String getAudioFilePath(String audioFileName) {



        return null;
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Intent activityIntent = new Intent(this, MusicPlayer.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayerService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent pauseIntent = new Intent(this, MusicPlayerService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent previousIntent = new Intent(this, MusicPlayerNotificationReceiver.class);
        previousIntent.setAction("previous");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, 0);

        Intent nextIntent = new Intent(this, MusicPlayerNotificationReceiver.class);
        nextIntent.setAction("next");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.audioo)
                .setContentTitle("Music Player")
                .setContentText("Playing audio")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.previous, "Previous", previousPendingIntent)
                .addAction(isPlaying ? R.drawable.pause : R.drawable.play, isPlaying ? "Pause" : "Play", isPlaying ? pausePendingIntent : playPendingIntent)
                .addAction(R.drawable.next, "Next", nextPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
