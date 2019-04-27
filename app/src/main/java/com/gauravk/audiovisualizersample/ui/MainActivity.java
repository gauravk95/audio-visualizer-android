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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.gauravk.audiovisualizer.visualizer.HiFiVisualizer;
import com.gauravk.audiovisualizersample.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERM_REQ_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.v_blob_btn).setOnClickListener(this);
        findViewById(R.id.v_blast_btn).setOnClickListener(this);
        findViewById(R.id.v_wave_btn).setOnClickListener(this);
        findViewById(R.id.v_bar_btn).setOnClickListener(this);
        findViewById(R.id.v_stream_btn).setOnClickListener(this);
        findViewById(R.id.v_circle_line_btn).setOnClickListener(this);
        findViewById(R.id.v_hifi_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.v_blob_btn:
                if (checkAudioPermission())
                    launchBlobActivity();
                else
                    requestAudioPermission();
                break;
            case R.id.v_blast_btn:
                if (checkAudioPermission())
                    launchBlastActivity();
                else
                    requestAudioPermission();
                break;
            case R.id.v_wave_btn:
                if (checkAudioPermission())
                    launchWaveActivity();
                else
                    requestAudioPermission();
                break;
            case R.id.v_bar_btn:
                if (checkAudioPermission())
                    launchSpikyWaveActivity();
                else
                    requestAudioPermission();
                break;
            case R.id.v_stream_btn:
                if (checkAudioPermission())
                    launchMusicStreamActivity();
                else
                    requestAudioPermission();
                break;
            case R.id.v_circle_line_btn:
                if (checkAudioPermission()){
                    launchCircleLinectivity();
                }

                else
                    requestAudioPermission();
                break;
            case R.id.v_hifi_btn:
                if (checkAudioPermission()){
                    Intent intent = new Intent(MainActivity.this, HiFiActivity.class);
                    startActivity(intent);
                }

                else
                    requestAudioPermission();
                break;
        }
    }

    private boolean checkAudioPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERM_REQ_CODE);
    }

    private void launchBlobActivity() {
        Intent intent = new Intent(MainActivity.this, BlobActivity.class);
        startActivity(intent);
    }

    private void launchBlastActivity() {
        Intent intent = new Intent(MainActivity.this, BlastActivity.class);
        startActivity(intent);
    }

    private void launchWaveActivity() {
        Intent intent = new Intent(MainActivity.this, WaveActivity.class);
        startActivity(intent);
    }

    private void launchSpikyWaveActivity() {
        Intent intent = new Intent(MainActivity.this, BarActivity.class);
        startActivity(intent);
    }

    private void launchMusicStreamActivity() {
        Intent intent = new Intent(MainActivity.this, MusicStreamActivity.class);
        startActivity(intent);
    }
    private void launchCircleLinectivity() {
        Intent intent = new Intent(MainActivity.this, CircleLineActivity.class);
        startActivity(intent);
    }

}
