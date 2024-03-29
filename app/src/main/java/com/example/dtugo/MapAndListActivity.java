package com.example.dtugo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MapAndListActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get rid of notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_mapandlist);
        getSupportActionBar().hide();

        //Initialize fragment
        Fragment fragment = new DtuMapFragment();

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