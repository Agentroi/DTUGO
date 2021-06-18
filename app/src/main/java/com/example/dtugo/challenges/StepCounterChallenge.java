package com.example.dtugo.challenges;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dtugo.R;

public class StepCounterChallenge extends AppCompatActivity {
    private boolean counterIsRunning = false;

    private TextView textViewStepCounter;

    private SensorManager sensorManager;
    private Sensor sensorStepCounter;
    private SensorEventListener sensorEventListenerStepCounter;

    private CountDownTimer challengeCounter;
    int stepCounter = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_stepcounterchallenge);


        textViewStepCounter = (TextView) findViewById(R.id.Parameter);
        textViewStepCounter.setText("" + 0);


        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        addListenerOnButton();
    }

    public void addListenerOnButton(){
        Button counterButton = (Button) findViewById(R.id.Counter1);
        TextView textView = (TextView) findViewById(R.id.Parameter);

        counterButton.setEnabled(true);
        counterButton.setText("START");

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterButton.setEnabled(false);
                counterIsRunning = true;

                onResume();

                new CountDownTimer(20000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        counterIsRunning = false;

                        Intent intent = new Intent(StepCounterChallenge.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", "" + stepCounter);
                        startActivity(intent);
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepCounter = 0;
        textViewStepCounter.setText("" + 0);

        if (counterIsRunning) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            sensorEventListenerStepCounter = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    stepCounter++;
                    textViewStepCounter.setText("" + stepCounter);
                }


                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

            sensorManager.registerListener(sensorEventListenerStepCounter, sensorStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (!counterIsRunning) {
            addListenerOnButton();
        }
    }

    @Override
    public void onBackPressed () {
        moveTaskToBack(false);

        if (challengeCounter != null) {
            challengeCounter.cancel();
        }
        finish();
    }

    @Override
    protected void onPause () {
        super.onPause();
        counterIsRunning = false;

    }


}
