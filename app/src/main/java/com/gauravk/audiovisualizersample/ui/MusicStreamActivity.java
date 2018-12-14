package com.gauravk.audiovisualizersample.ui;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizersample.R;

import java.io.IOException;

public class MusicStreamActivity extends AppCompatActivity {

    ImageButton imgButton;
    BlastVisualizer mVisualizer;
    MediaPlayer mediaPlayer;

    String stream = "http://stream.radioreklama.bg/radio1rock128";

    boolean prepared = false;
    boolean started = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_stream);

        imgButton = findViewById(R.id.playbtn);
        imgButton.setImageResource(R.drawable.playbtn);
        mVisualizer = findViewById(R.id.blast);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != AudioManager.ERROR) {
            mVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        new PlayerTask().execute(stream);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started) {
                    started = false;
                    mediaPlayer.pause();
                    imgButton.setImageResource(R.drawable.playbtn);
                } else {
                    started = true;
                    mediaPlayer.start();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    imgButton.setImageResource(R.drawable.pausebtn);
                }
            }
        });
    }

    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            imgButton.setImageResource(R.drawable.playbtn);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            //mediaPlayer.start();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared) {
            mediaPlayer.release();
        }
        if (mVisualizer != null)
            mVisualizer.release();
    }
}
