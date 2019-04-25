package com.gauravk.audiovisualizersample.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.gauravk.audiovisualizer.visualizer.HiFiVisualizer;
import com.gauravk.audiovisualizersample.R;
import com.gauravk.audiovisualizersample.utils.AudioPlayer;

public class HiFiActivity extends AppCompatActivity {
    private AudioPlayer mAudioPlayer;
    private HiFiVisualizer mVisualizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_fi);
        mVisualizer = findViewById(R.id.hifi);
        mAudioPlayer = new AudioPlayer();
    }
    @Override
    protected void onStart() {
        super.onStart();

        startPlayingAudio(R.raw.sample);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayingAudio();
    }

    private void startPlayingAudio(int resId) {
        mAudioPlayer.play(this, resId, new AudioPlayer.AudioPlayerEvent() {
            @Override
            public void onCompleted() {
                if (mVisualizer != null)
                    mVisualizer.hide();
            }
        });
        int audioSessionId = mAudioPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            mVisualizer.setAudioSessionId(audioSessionId);
    }

    private void stopPlayingAudio() {
        if (mAudioPlayer != null)
            mAudioPlayer.stop();
        if (mVisualizer != null)
            mVisualizer.release();
    }}
