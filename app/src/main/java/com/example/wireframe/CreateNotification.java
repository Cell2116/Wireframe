package com.example.wireframe;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class CreateNotification extends AppCompatActivity {
    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;

    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x=0;


    public static void createNotification (Context context, AudioModel audioModel,int playbutton, int pos, int size){
     //   int playbutton = R.drawable.ic_baseline_play_circle_outline_24;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSession mediaSession = new MediaSession(context, "tag");
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.music_icon);
            PendingIntent pendingIntentPrevious;
           // size = size - 1;
            int drw_previous;
            if (pos == 0){
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context,NotificationService.class)
                        .setAction(ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context,0,intentPrevious,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.ic_baseline_skip_previous_24;
            }
            Intent intentPlay = new Intent(context,NotificationService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context,0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
      //      drw_previous = R.drawable.ic_baseline_play_circle_outline_24;

            PendingIntent pendingIntentNext;
            int drw_next;
            if (pos == 0){
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context,NotificationService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context,0,intentNext,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_baseline_skip_next_24;
            }

            //AudioModel songTitle = songList.get
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(audioModel.getTitle())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drw_previous,"Previous",pendingIntentPrevious)
                    .addAction(playbutton,"Play",pendingIntentPlay)
                    .addAction(drw_next,"Next",pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken())))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
            notificationManagerCompat.notify(1,notification);
        }
    }

}
