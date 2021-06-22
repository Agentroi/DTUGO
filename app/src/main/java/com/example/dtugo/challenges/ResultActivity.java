package com.example.dtugo.challenges;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dtugo.MapAndListActivity;
import com.example.dtugo.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getFlags() == Intent.FLAG_FROM_BACKGROUND) {
            moveTaskToBack(true);
        }
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_result);
        TextView resultText = (TextView) findViewById(R.id.result_data);
        String result = getIntent().getStringExtra("result_key");
        resultText.setText(result);

        addListenerOnButton();
    }

    public void addListenerOnButton(){
        Button retryButton = (Button) findViewById(R.id.retry);
        Button returnButton = (Button) findViewById(R.id.return_button);

        retryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                finish();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Replace MainActivity with the MapActivity/Main activity in DTU-GO
                //https://stackoverflow.com/questions/14001963/finish-all-activities-at-a-time
                Intent intent = new Intent(getApplicationContext(), MapAndListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}