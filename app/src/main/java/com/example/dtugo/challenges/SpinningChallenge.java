package com.example.dtugo.challenges;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dtugo.R;

import static com.example.dtugo.Notifications.CHANNEL_CHALLENGE_ID;

public class SpinningChallenge extends AppCompatActivity {
    private boolean counterIsRunning = false;

    private TextView locationTitle;
    private TextView challengeTitle;
    private TextView counter;

    private int count = 0;
    private boolean bool = false;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    private SensorEventListener sensorEventListenerAccelerometer;
    private SensorEventListener sensorEventListenerMagneticField;

    private CountDownTimer challengeCounter;

    private boolean isPaused = false;
    private boolean resultReady = false;
    private Intent savedIntent;
    private NotificationManagerCompat notificationManager;

        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide(); //hide the title bar
            setContentView(R.layout.activity_spinning_challenge);

            //Set TextViews

            locationTitle = (TextView) findViewById(R.id.location_title);
            challengeTitle = (TextView) findViewById(R.id.challenge_title);
            counter = (TextView) findViewById(R.id.Parameter);

            locationTitle.setText("Biblioteket");
            challengeTitle.setText("Spinning Challenge");
            counter.setText("" + 0);

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

                challengeCounter = new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);

                    }

                    public void onFinish() {
                        counterIsRunning = false;
                        //counterButton.setText("done!");
                        Intent intent = new Intent(SpinningChallenge.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", ""+count+" spins!");
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
        //register your sensorListener here
        count = 0;
        counter.setText("" + 0);
        if (counterIsRunning) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            sensorEventListenerAccelerometer = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    floatGravity = event.values;

                    SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                    SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

                    if (!(Math.round(floatOrientation[1] * 180 / 3.14159) < -30 || Math.round(floatOrientation[1] * 180 / 3.14159) > 30 || Math.round(floatOrientation[2] * 180 / 3.14159) < -30 || Math.round(floatOrientation[2] * 180 / 3.14159) > 30)) {

                        if (Math.round(floatOrientation[0] * 180 / 3.14159) < 0 && bool == true) {
                            bool = false;
                            count++;
                        } else if (Math.round(floatOrientation[0] * 180 / 3.14159) < 170 && Math.round(floatOrientation[0] * 180 / 3.14159) > 10) {
                            bool = true;
                        }
                    }

                    counter.setText("" + count);

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

            sensorEventListenerMagneticField = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    floatGeoMagnetic = event.values;

                    SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                    SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
/*
                counter.setText("" + Math.round(floatOrientation[0] * 180 / 3.14159));

                if (!(Math.round(floatOrientation[1]*180/3.14159) < -30 || Math.round(floatOrientation[1]*180/3.14159) > 30 || Math.round(floatOrientation[2]*180/3.14159) < -45 || Math.round(floatOrientation[2]*180/3.14159) > 45)) {

                    if (Math.round(floatOrientation[0] * 180 / 3.14159) < 0 && bool == true) {
                        bool = false;
                        count++;
                    } else if (Math.round(floatOrientation[0] * 180 / 3.14159) < 150 && Math.round(floatOrientation[0] * 180 / 3.14159) > 30) {
                        bool = true;
                    }
                }

 */
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };


            sensorManager.registerListener(sensorEventListenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
        }

        isPaused = false;

        if (resultReady) {
            resultReady = false;
            startActivity(savedIntent);
        }

        if (!counterIsRunning) {
            addListenerOnButton();
        }
    }

    @Override
    public void onBackPressed() {
    }



    @Override
    protected void onPause() {
        super.onPause();
        //Unregister your sensorListener here
        isPaused = true;
        //sensorManager.unregisterListener(sensorEventListenerAccelerometer,sensorAccelerometer);
        //sensorManager.unregisterListener(sensorEventListenerMagneticField,sensorMagneticField);
    }

    public void sendNotification(View view) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SpinningChallenge.class), PendingIntent.FLAG_UPDATE_CURRENT);

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