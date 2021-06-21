package com.example.dtugo.challenges;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dtugo.R;

import static com.example.dtugo.Notifications.CHANNEL_CHALLENGE_ID;

public class RunActivity extends AppCompatActivity {
    private static final long POLLING_FREQ = 500 ;
    private static final float MIN_DISTANCE = 0.01f;

    private boolean counterIsRunning = false;
    private Location myStartLocation;
    private Location myCurrentLocation;
    private LocationListener locationListener;
    LocationManager locationManager;
    private double distance;
    private CountDownTimer challengeCounter;

    private boolean isPaused = false;
    private boolean resultReady = false;
    private Intent savedIntent;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_run);

        //We check for permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //Initialize location manager:
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        //Initialize start location:
        myStartLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

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

                startSensor();

                challengeCounter = new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);

                    }

                    public void onFinish() {
                        if (myStartLocation == null) {
                            Log.i("startLocation", "null");
                        }
                        if (myCurrentLocation == null) {
                            Log.i("currentLocation", "null");
                        }
                        distance = myStartLocation.distanceTo(myCurrentLocation);
                        String result = "" + (int) distance + " meter!";

                        //counterButton.setText("done!");
                        Intent intent = new Intent(RunActivity.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", result);
                        counterIsRunning = false;
                        stopSensor();
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
    }

    public void startSensor() {
        //We check for permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //Initialize start location:
        myStartLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Initialize location listener:
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myCurrentLocation = location;
            }
        };

        if (null != locationManager.getProvider(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLLING_FREQ, MIN_DISTANCE, locationListener);
        }

    }

    public void stopSensor() {
        //We check for permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //Initialize start location:
        //myStartLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Unregister your sensorListener here
        locationManager.removeUpdates(locationListener);
    }

    public void sendNotification(View view) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, RunActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

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