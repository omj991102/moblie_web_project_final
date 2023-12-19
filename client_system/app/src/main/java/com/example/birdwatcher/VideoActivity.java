package com.example.birdwatcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.VideoView;
import android.widget.MediaController;
import android.net.Uri;

public class VideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        VideoView videoView = findViewById(R.id.vidView);
        Intent intent = getIntent();
        String vidURL = intent.getStringExtra("vidURL");
        vidURL = vidURL.replace("http", "https");
        Log.d("vid", Uri.parse(vidURL).toString());

        videoView.setVideoURI(Uri.parse(vidURL));
        MediaController controller = new MediaController(VideoActivity.this);
        videoView.setMediaController(controller);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { videoView.start(); }
        });

    }

}
