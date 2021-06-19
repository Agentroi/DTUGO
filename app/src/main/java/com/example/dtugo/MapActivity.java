package com.example.dtugo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaExtractor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

public class MapActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Get rid of notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();

        //Initialize fragment
        Fragment fragment = new MapFragment();

        //open fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .commit();

        //Add start music
        mediaPlayer = MediaPlayer.create(this,R.raw.start);
        mediaPlayer.start();

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(this,R.raw.start);
    }

    @Override
    protected void onResume() {
        if(mediaPlayer != null && !mediaPlayer.isPlaying())
            mediaPlayer.start();
        super.onResume();
    }

}