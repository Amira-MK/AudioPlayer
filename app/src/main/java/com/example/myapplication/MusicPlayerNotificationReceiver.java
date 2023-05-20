package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicPlayerNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(MusicPlayerService.ACTION_PREVIOUS)) {
            Intent previousIntent = new Intent(context, MusicPlayerService.class);
            previousIntent.setAction(MusicPlayerService.ACTION_PREVIOUS);
            context.startService(previousIntent);
        }

        if (action != null && action.equals(MusicPlayerService.ACTION_NEXT)) {
            Intent nextIntent = new Intent(context, MusicPlayerService.class);
            nextIntent.setAction(MusicPlayerService.ACTION_NEXT);
            context.startService(nextIntent);
        }
    }
}
