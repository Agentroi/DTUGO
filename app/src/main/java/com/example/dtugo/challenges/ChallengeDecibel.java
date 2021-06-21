package com.example.dtugo.challenges;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.dtugo.R;

import org.w3c.dom.Text;

import java.io.IOException;

import static com.example.dtugo.Notifications.CHANNEL_CHALLENGE_ID;

public class ChallengeDecibel extends AppCompatActivity {
    private boolean counterIsRunning = false;
    private CountDownTimer challengeCounter;
    TextView mDecibelView;

    private boolean isPaused = false;
    private boolean resultReady = false;
    private Intent savedIntent;
    private NotificationManagerCompat notificationManager;

    MediaRecorder mediaRecorder;
    Thread textRunner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    final Handler textHandler = new Handler();
    private double maxdB = 0.0;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide(); //hide the title bar
            setContentView(R.layout.activity_decibel);

            mDecibelView = (TextView) findViewById(R.id.Parameter);
            mDecibelView.setText("0.0 dB");

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);

            }

            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish();
            }

            notificationManager = NotificationManagerCompat.from(this);

            addListenerOnButton();
        }

    private void startThread() {
            textRunner = new Thread() {
                public void run() {
                    while (textRunner != null) {
                        if(counterIsRunning) {
                            try {
                                Thread.sleep(100);
                                Log.i("Noise", "Tock");
                            } catch (InterruptedException e) {
                            }
                            textHandler.post(textUpdater);
                        } else { return;}
                    }
                }
            };
            textRunner.start();
            Log.d("Noise", "start runner()");
    }

    public void addListenerOnButton(){
        Button counterButton = (Button) findViewById(R.id.Counter1);

        counterButton.setEnabled(true);
        counterButton.setText("START");

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterButton.setEnabled(false);
                counterIsRunning = true;
                startThread();

                startRecording();

                challengeCounter = new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        counterButton.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        //counterButton.setText("done!");
                        Intent intent = new Intent(ChallengeDecibel.this, ResultActivity.class);

                        //Add sensor data in the putExtra method's value field
                        intent.putExtra("result_key", maxdB + " dB");

                        counterIsRunning = false;
                        stopRecording();
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

    public void startRecording() {

            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile("/dev/null");

                try {
                    mediaRecorder.prepare();
                } catch (IOException ioe) {
                    android.util.Log.e("[PrepareRecorder]","IOException: " + android.util.Log.getStackTraceString(ioe));
                } catch (java.lang.SecurityException e) {
                    android.util.Log.e("[PrepareRecorder]","SecurityException: " + android.util.Log.getStackTraceString(e));
                }

                try {
                    Thread.sleep(100);
                    mediaRecorder.start();
                } catch (IllegalStateException ise) {
                    android.util.Log.e("[StartRecorder]","ISEception: " + android.util.Log.getStackTraceString(ise));
                } catch (java.lang.SecurityException e) {
                    android.util.Log.e("[StartRecorder]","SecurityException: " + android.util.Log.getStackTraceString(e));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    public void stopRecording() {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
    }

    final Runnable textUpdater = new Runnable() {
            public void run() {
                updater();
            }
    };

    public void updater() {
        double amp = getAmplitudeEMA();
        mDecibelView.setText(amp+" dB");
        if (amp > maxdB) {
            maxdB = amp;
        }
    }

    public double getAmplitude() {
            if (mediaRecorder != null) {
                return (mediaRecorder.getMaxAmplitude());
            } else {
                return 0;
            }
    }

    public double getAmplitudeEMA() {
            double amplitude = getAmplitude();
            mEMA = EMA_FILTER * amplitude + (1.0 - EMA_FILTER) * mEMA;
            return Math.round(mEMA)/220;
    }



    @Override
    protected void onResume() {
        super.onResume();
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

    public void sendNotification(View view) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ChallengeDecibel.class), PendingIntent.FLAG_UPDATE_CURRENT);

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