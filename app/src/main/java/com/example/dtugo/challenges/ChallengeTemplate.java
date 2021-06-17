package com.example.dtugo.challenges;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dtugo.R;

public class ChallengeTemplate extends AppCompatActivity {
    private boolean counterIsRunning = false;

    //new
    private CountDownTimer challengeCounter;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide(); //hide the title bar
            setContentView(R.layout.activity_challengetemplate);

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
                        startActivity(intent);
                    }

                }.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register your sensorListener here

        if (!counterIsRunning) {
            addListenerOnButton();
        }
    }


    //new
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
        challengeCounter.cancel();
        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();
        //Unregister your sensorListener here
    }
}