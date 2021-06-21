package com.example.dtugo.challenges;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
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

public class ChallengeTemplate extends AppCompatActivity {
    private boolean counterIsRunning = false;
    //new
    private boolean isPaused = false;
    private boolean resultReady = false;
    private Intent savedIntent;
    private NotificationManagerCompat notificationManager;

    private CountDownTimer challengeCounter;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide(); //hide the title bar
            setContentView(R.layout.activity_challengetemplate);

            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish();
            }

            //new
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

                challengeCounter = new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);

                    }

                    public void onFinish() {
                        //counterButton.setText("done!");
                        Intent intent = new Intent(ChallengeTemplate.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", "sensor_data");
                        counterIsRunning = false;
                        //new
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
        //new
        isPaused = false;

        if (!counterIsRunning) {
            addListenerOnButton();
        }

        //new
        if (resultReady) {
            resultReady = false;
            startActivity(savedIntent);
        }
    }


    @Override
    public void onBackPressed() {
            //new (empty)
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Unregister your sensorListener here

        //new
        isPaused = true;
    }

    //new
    public void sendNotification(View view) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ChallengeTemplate.class), PendingIntent.FLAG_UPDATE_CURRENT);

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