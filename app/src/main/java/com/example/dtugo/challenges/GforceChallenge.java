package com.example.dtugo.challenges;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

import com.example.dtugo.R;

import java.text.DecimalFormat;

import static com.example.dtugo.Notifications.CHANNEL_CHALLENGE_ID;

public class GforceChallenge extends AppCompatActivity {

    private TextView textViewGforcemeter;

    private boolean isPaused = false;
    private boolean resultReady = false;
    private Intent savedIntent;
    private NotificationManagerCompat notificationManager;


    private SensorManager SM;
    private Sensor sensorGforce;

    private boolean isGforceSensorPresent;
    private boolean counterIsRunning = false;

    double GforceCounter = 0;

    private CountDownTimer challengeCounter;

    private SensorEventListener sensorEventListenerGforce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gforcechallenge);
        getSupportActionBar().hide();


        textViewGforcemeter = (TextView) findViewById(R.id.ParameterGforce);

        textViewGforcemeter.setText("G " + 0);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        notificationManager = NotificationManagerCompat.from(this);

        addListenerOnButton();
    }

    public void addListenerOnButton() {
        Button counterButton = (Button) findViewById(R.id.CounterGforce);
        TextView textView = (TextView) findViewById(R.id.ParameterGforce);

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

                        Intent intent = new Intent(GforceChallenge.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", textViewGforcemeter.getText());
                        //counterIsRunning = false;
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
        GforceCounter = 0;
        textViewGforcemeter.setText(0 + " G");

       if(counterIsRunning) {
            SM = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorGforce = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

           sensorEventListenerGforce = new SensorEventListener() {

               @Override
               public void onSensorChanged (SensorEvent event){
                   double g = ((Math.sqrt((Math.pow(event.values[0], 2) + (Math.pow(event.values[1], 2) + (Math.pow(event.values[2], 2)))))) / (SensorManager.STANDARD_GRAVITY));
                   if (g > GforceCounter) {
                       GforceCounter = g;
                       DecimalFormat numberFormat = new DecimalFormat("#.00");
                       textViewGforcemeter.setText(numberFormat.format(GforceCounter)+ " G");
                   }
                   DecimalFormat numberFormat = new DecimalFormat("#.00");

               }

               @Override
               public void onAccuracyChanged(Sensor sensor,int accuracy){
               }
           };

           SM.registerListener(sensorEventListenerGforce, sensorGforce, SensorManager.SENSOR_DELAY_FASTEST);

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
        isPaused = true;
        counterIsRunning = false;
    }

    public void sendNotification(View view) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GforceChallenge.class), PendingIntent.FLAG_UPDATE_CURRENT);

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