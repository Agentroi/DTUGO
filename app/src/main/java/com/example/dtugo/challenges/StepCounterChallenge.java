package com.example.dtugo.challenges;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
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

import static com.example.dtugo.Notifications.CHANNEL_CHALLENGE_ID;

public class StepCounterChallenge extends AppCompatActivity {
    private boolean counterIsRunning = false;

    private TextView textViewStepCounter;

    private SensorManager sensorManager;
    private Sensor sensorStepCounter;
    private SensorEventListener sensorEventListenerStepCounter;

    private boolean isPaused = false;
    private boolean resultReady = false;
    private Intent savedIntent;
    private NotificationManagerCompat notificationManager;

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

        notificationManager = NotificationManagerCompat.from(this);

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

                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        counterIsRunning = false;

                        Intent intent = new Intent(StepCounterChallenge.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", "" + stepCounter);
                        if (!isPaused) {
                            startActivity(intent);
                        } else {
                            sendNotification(view);
                            resultReady = true;
                            savedIntent = intent;
                        }
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
        isPaused = false;

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

        if (resultReady) {
            resultReady = false;
            startActivity(savedIntent);
        }

        if (!counterIsRunning) {
            addListenerOnButton();
        }
    }

    @Override
    public void onBackPressed () {
    }

    @Override
    protected void onPause () {
        super.onPause();
        isPaused = true;
    }

    public void sendNotification(View view) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, StepCounterChallenge.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_CHALLENGE_ID)
                .setSmallIcon(R.drawable.ic_smiley)
                .setContentTitle("Udfordring afsluttet")
                .setContentText("Se dit resultat her")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(1, notification);
    }
}
