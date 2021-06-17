package com.example.dtugo.challenges;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dtugo.R;

public class StepCounterChallenge extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private TextView textViewStepCounter;
    private Sensor mStepCounter;
    int stepCounter = 0;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                //ask for permission
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }

            super.onCreate(savedInstanceState);
            getSupportActionBar().hide(); //hide the title bar
            setContentView(R.layout.activity_stepcounterchallenge);
            addListenerOnButton();

            textViewStepCounter = findViewById(R.id.Parameter);

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }

    public void addListenerOnButton(){
        Button counterButton = (Button) findViewById(R.id.Counter1);
        TextView textView = (TextView) findViewById(R.id.Parameter);
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepCounter = 0;
                counterButton.setEnabled(false);
                onResume();

                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                       onPause();

                    }

                }.start();
            }
        });
    }

    @Override
    protected void onResume() {
         super.onResume();
         if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
             sensorManager.registerListener(this,mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
         }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER ) != null){
            sensorManager.unregisterListener(this, mStepCounter);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepCounter++;
        textViewStepCounter.setText(String.valueOf(stepCounter));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}