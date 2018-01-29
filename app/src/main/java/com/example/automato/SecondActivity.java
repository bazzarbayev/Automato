package com.example.automato;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {

    private TextView coor;
    private Button play;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent i = this.getIntent();
        final String audioPath = i.getStringExtra(FirstActivity.AUDIO_PATH);
        final double latCoor = i.getDoubleExtra(FirstActivity.LAT,0);
        final double longCoor = i.getDoubleExtra(FirstActivity.LONG,0);

        coor = findViewById(R.id.txtCoor);
        SpannableString content = new SpannableString(String.format(Locale.ENGLISH,"Coordinates: %.4f, %.4f", latCoor,longCoor));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        coor.setText(content);
        coor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latCoor, longCoor);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                SecondActivity.this.startActivity(intent);
            }
        });

        play = findViewById(R.id.btnPlay);
        play.setText(audioPath.substring(audioPath.lastIndexOf('/')+1));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepare();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                Toast.makeText(SecondActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
