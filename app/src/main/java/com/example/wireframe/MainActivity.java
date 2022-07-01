package com.example.wireframe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerView musicListView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    SearchView searchView;
    //NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);
        searchView = findViewById(R.id.search_song);

        searchView.clearFocus();


        if(!checkPermission()){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        if(songsList.size()==0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }else{
            musicListView.setLayoutManager(new LinearLayoutManager(this));
            musicListView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

       /* if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }*/

    }

 /*   @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"Wireframe",NotificationManager.IMPORTANCE_LOW);
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager!=null){
            notificationManager.createNotificationChannel(channel);
        }
    }*/


    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this,"PERMISSION TO READ FILES HAS NOT BEEN ALLOWED",Toast.LENGTH_SHORT).show();
        }else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(musicListView!=null){
            musicListView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }
    }

   private void filterList(String text){
        ArrayList<AudioModel> filteredList = new ArrayList<>();
        for (AudioModel songs: songsList){
            if (songs.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(songs);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this,"No song found", Toast.LENGTH_SHORT).show();
        }
        else {
            musicListView.setLayoutManager(new LinearLayoutManager(this));
            musicListView.setAdapter(new MusicListAdapter(filteredList,getApplicationContext()));
        }
    }

}