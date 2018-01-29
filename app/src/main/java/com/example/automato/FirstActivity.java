package com.example.automato;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class FirstActivity extends AppCompatActivity {

    private final static int REQUEST_PERMISSION_CODE = 1000;
    private final static int RECORD_LENGTH = 3000;
    private final static int TIME_DURATION = 30000;

    public static String AUDIO_PATH = "audio_path";
    public static String LAT = "lat";
    public static String LONG = "long";

    Button btnStop;
    private String pathSave;
    private MediaRecorder mediaRecorder;
    private double locationLat;
    private double locationLong;
    private TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        timerText = findViewById(R.id.tv_timer);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setEnabled(false);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                intent.putExtra(AUDIO_PATH, pathSave);
                intent.putExtra(LAT, locationLat);
                intent.putExtra(LONG, locationLong);
                startActivity(intent);
            }
        });

        MyLocListener loc = new MyLocListener();
        LocationManager myManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Checks the permission, requests
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MyLocListener.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, loc);
        initRecord();

        //Timer
        new CountDownTimer(TIME_DURATION, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                timerText.setText("done!");
            }

        }.start();

    }

    class MyLocListener implements LocationListener {
        public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1200;

        @Override
        public void onLocationChanged(Location location){
            if(location != null) {
                locationLat = location.getLatitude();
                locationLong = location.getLongitude();

                ((TextView)findViewById(R.id.txtLocation))
                        .setText(String.format("Coordinates: %.4f, %.4f", locationLat,locationLong));
            }
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private void initRecord(){
        if (!checkPermissionFromDevice())
            requestPermission();
        Random r = new Random();
        int startTime = r.nextInt(TIME_DURATION-RECORD_LENGTH);


        //This method is for to start the record. It starts randomly
        Handler handler = new Handler(this.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkPermissionFromDevice()) {
                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/"
                            + UUID.randomUUID().toString() + "_audio_record.mp3";
                    setupMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(FirstActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }

            }
        }, startTime);

        //This is for stop the record. It stops randomly
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaRecorder.stop();
                Toast.makeText(FirstActivity.this, "Recorded", Toast.LENGTH_SHORT).show();

            }
        }, startTime+RECORD_LENGTH);

        //This is for setting visibility for button stop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnStop.setEnabled(true);

            }
        }, TIME_DURATION);

    }
    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }


    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        }, REQUEST_PERMISSION_CODE);
    }


}
