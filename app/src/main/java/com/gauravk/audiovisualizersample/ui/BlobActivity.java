/*
        Copyright 2018 Gaurav Kumar

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.gauravk.audiovisualizersample.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gauravk.audiovisualizer.model.PaintStyle;
import com.gauravk.audiovisualizersample.R;
import com.gauravk.audiovisualizersample.utils.AudioPlayer;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;

public class BlobActivity extends AppCompatActivity {

    private BlobVisualizer mVisualizer;

    private AudioPlayer mAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blob);

        mVisualizer = findViewById(R.id.blob);
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
    }

}
