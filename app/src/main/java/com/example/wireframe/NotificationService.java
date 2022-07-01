package com.example.wireframe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("SONGS_SONGS")
        .putExtra("actionname",intent.getAction()));
    }
}
