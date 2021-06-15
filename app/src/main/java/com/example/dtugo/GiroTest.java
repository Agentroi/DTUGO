package com.example.dtugo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GiroTest extends Activity implements SensorEventListener {

    private TextView zText;
    private Sensor mySensor;
    private SensorManager SM;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_giro_test);

        //Create Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //Register Sensor Listener
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);

        //Assign text
        zText = (TextView) findViewById(R.id.textView);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        zText.setText("Z: " + sensorEvent.values[2]*180);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Not in use
    }
}