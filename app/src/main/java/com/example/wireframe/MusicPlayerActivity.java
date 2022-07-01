package com.example.wireframe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleText,currentTimeText,totalTimeText;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    NotificationManager notificationManager;
    Context context;
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleText = findViewById(R.id.song_title);
        currentTimeText = findViewById(R.id.current_time);
        totalTimeText = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        titleText.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeText.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }
                 //   CreateNotification.createNotification(context,
                          //  MyMediaPlayer.currentIndex,
                           // songsList.size());

                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver,new IntentFilter("SONGS_SONGS"));
            startService(new Intent(getBaseContext(),OnClearFromRecentService.class));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"Wireframe", NotificationManager.IMPORTANCE_LOW);
        notificationManager = getSystemService(NotificationManager.class);
        if(notificationManager!=null){
            notificationManager.createNotificationChannel(channel);
        }
    }

    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleText.setText(currentSong.getTitle());

        totalTimeText.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());
        CreateNotification.createNotification(MusicPlayerActivity.this,currentSong,R.drawable.ic_baseline_play_circle_outline_24,
                MyMediaPlayer.currentIndex,
                songsList.size());
        playMusic();


    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    playPreviousSong();
                    break;
                case CreateNotification.ACTION_PLAY:
                    pausePlay();
                    break;
                case CreateNotification.ACTION_NEXT:
                    playNextSong();
                    break;

            }
        }
    };

    private void playMusic(){

        mediaPlayer.reset();
        CreateNotification.createNotification(MusicPlayerActivity.this, currentSong, R.drawable.ic_baseline_pause_circle_outline_24,
                MyMediaPlayer.currentIndex,
                songsList.size());
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void playNextSong(){

        if(MyMediaPlayer.currentIndex== songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying()) {
            CreateNotification.createNotification(MusicPlayerActivity.this, currentSong, R.drawable.ic_baseline_play_circle_outline_24,
                    MyMediaPlayer.currentIndex,
                    songsList.size());
            mediaPlayer.pause();

        }
        else {
            CreateNotification.createNotification(MusicPlayerActivity.this, currentSong, R.drawable.ic_baseline_pause_circle_outline_24,
                    MyMediaPlayer.currentIndex,
                    songsList.size());
            mediaPlayer.start();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }


    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}