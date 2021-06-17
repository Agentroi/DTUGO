package com.example.dtugo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SpinningGame extends AppCompatActivity {

    private ImageView imageView;
    private Button resetButton;

    private TextView xPar;
    private TextView yPar;
    private TextView zPar;

    private int count = 0;
    private boolean bool = false;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinning_game);

        imageView = findViewById(R.id.imageView);

        xPar = findViewById(R.id.xPar);
        yPar = findViewById(R.id.yPar);
        zPar = findViewById(R.id.zPar);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListenerAccelrometer = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGravity = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

                imageView.setRotation((float) (-floatOrientation[0]*180/3.14159));

                xPar.setText(""+Math.round(floatOrientation[0]*180/3.14159));
                yPar.setText(""+Math.round(floatOrientation[1]*180/3.14159));
                //zPar.setText(""+Math.round(floatOrientation[2]*180/3.14159));

                if (Math.round(floatOrientation[1]*180/3.14159) < -45 || Math.round(floatOrientation[1]*180/3.14159) > 30 || Math.round(floatOrientation[2]*180/3.14159) < -45 || Math.round(floatOrientation[2]*180/3.14159) > 45) {
                    imageView.setBackgroundColor(Color.RED);
                } else {
                    imageView.setBackgroundColor(Color.GREEN);

                    if (Math.round(floatOrientation[0] * 180 / 3.14159) < 0) {
                        imageView.setBackgroundColor(Color.RED);
                    } else {
                        imageView.setBackgroundColor(Color.GREEN);
                    }

                    if (Math.round(floatOrientation[0] * 180 / 3.14159) < 0 && bool == true) {
                        bool = false;
                        count++;
                    } else if (Math.round(floatOrientation[0] * 180 / 3.14159) < 150 && Math.round(floatOrientation[0] * 180 / 3.14159) > 30) {
                        bool = true;
                    }
                }

                zPar.setText("" + count);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGeoMagnetic = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

                imageView.setRotation((float) (-floatOrientation[0]*180/3.14159));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListenerAccelrometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void ResetButton(View view){
        imageView.setRotation(180);
    }
}