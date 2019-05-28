package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

public class MainActivity extends Activity implements
        VoiceView.OnRecordListener {
    private static final int MY_PERMISSION_REQUEST_RECORD_AUDIO = 20;

    private static final String TAG = MainActivity.class.getName();

    private TextView mTextView;
    private VoiceView mVoiceView;
    private MediaRecorder mMediaRecorder;
    private Handler mHandler;
    private Button btnListCommands;
    private Button btnPrivacyPolicy;
    private boolean mIsRecording = false;
    int witchActivityToOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        mVoiceView = (VoiceView) findViewById(R.id.voiceview);
        mVoiceView.setOnRecordListener(this);

        //

        mHandler = new Handler(Looper.getMainLooper());


    }

    @Override
    public void onRecordStart() {
        Log.d(TAG, "onRecordStart");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            try {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mMediaRecorder.setOutputFile(new File(getFilesDir(),
                        "audio.amr").getAbsolutePath());


                mMediaRecorder.prepare();
                mMediaRecorder.start();
                mIsRecording = true;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        float radius = (float) Math.log10(Math.max(1,
                                mMediaRecorder.getMaxAmplitude() - 500))
                                * ScreenUtils.dp2px(MainActivity.this, 20);
                        mTextView.setText(String.valueOf(radius));
                        mVoiceView.animateRadius(radius);
                        if (mIsRecording) {
                            mHandler.postDelayed(this, 50);
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "MediaRecorder prepare failed!",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Needs micro record permission",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSION_REQUEST_RECORD_AUDIO);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_REQUEST_RECORD_AUDIO);
            }
        }


    }

    @Override
    public void onRecordFinish() {
        Log.d(TAG, "onRecordFinish");
        try {
            mIsRecording = false;
            mMediaRecorder.stop();
            witchActivityToOpen = 0;
        } catch (RuntimeException e) {
        }

    }

    @Override
    protected void onDestroy() {
        try {
            if (mIsRecording) {
                mMediaRecorder.stop();
                mIsRecording = false;
            }
            mMediaRecorder.release();


        } catch (RuntimeException e) {
        }finally {
            super.onDestroy();
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
}
