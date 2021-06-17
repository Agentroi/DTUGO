package com.example.dtugo.challenges;

import androidx.appcompat.app.AppCompatActivity;

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

public class GforceChallenge extends AppCompatActivity implements SensorEventListener {

    private SensorManager SM;
    private TextView textViewGforcemeter;
    private Sensor mySensor;
    private boolean isGforceSensorPresent;
    double GforceCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gforcechallenge);
        getSupportActionBar().hide();
        addListenerOnButton();

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        textViewGforcemeter = (TextView) findViewById(R.id.ParameterGforce);
    }

    public void addListenerOnButton() {
        Button counterButton = (Button) findViewById(R.id.CounterGforce);
        TextView textView = (TextView) findViewById(R.id.ParameterGforce);

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int GforceCounter = 0;
                onResume();
                counterButton.setEnabled(false);


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
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            SM.unregisterListener(this, mySensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            double g = ((Math.sqrt((Math.pow(event.values[0],2)+(Math.pow(event.values[1],2)+(Math.pow(event.values[2],2))))))/(SensorManager.STANDARD_GRAVITY));
            if(g > GforceCounter){
                GforceCounter = g;
                textViewGforcemeter.setText("G " + Math.round((g*100.00)/100.00));
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}