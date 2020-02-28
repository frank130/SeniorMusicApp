package com.example.ziad.algorithmicmusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import nl.bravobit.ffmpeg.FFmpeg;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 5;

    /**
     * Set the directory of the piano notes downloaded
     */
    private File mPianoNotesDir = new File(Environment.getExternalStorageDirectory(), "Download");

    /**
     * Set the out file of the generated music
     */
    private File mOutputFile = new File(Environment.getExternalStorageDirectory(), "song.wav");

    private MediaPlayer mMediaPlayer;

    private Button mOutputButton, mPlayButton, mBuildSong, mMainTrack;
    private TextView mMessageTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputButton = findViewById(R.id.bt_output);
        mPlayButton = findViewById(R.id.bt_play);
        mBuildSong = findViewById(R.id.button4);
        mMainTrack = findViewById(R.id.btn_MainTrack);

        mMessageTextView = findViewById(R.id.tv_message);
        mProgressBar = findViewById(R.id.progress_bar);
        mOutputButton.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mMainTrack.setOnClickListener(this);

        mBuildSong.setOnClickListener(this);


        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            mMessageTextView.setText("FFMPEG is supported");
        } else {
            // ffmpeg is not supported
            mMessageTextView.setText("FFMPEG is NOT supported :(");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

        } else {
            mOutputButton.setEnabled(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mOutputButton.setEnabled(true);
                } else {
                    mMessageTextView.setText("Write External Permission is required in order to save the output music file");
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_output:
                outputMusicFile();
                break;
            case R.id.bt_play:
                playMusicFile();
                break;
            case R.id.button4:
                Intent intent = new Intent(this, CreateBaseSong.class);

                startActivity(intent);
                break;
            case R.id.btn_MainTrack:
                Intent track_intent = new Intent(this, MainTrackHome.class);

                startActivity(track_intent);
                break;

        }
    }

    private void outputMusicFile() {
        mProgressBar.setVisibility(View.VISIBLE);

        Instrument piano_right = new Instrument("piano_right");
        piano_right.setPath_to_notes(mPianoNotesDir.getAbsolutePath());
        //piano_right.setBar("G#5h G#6w F6w A5q C6q F6q C6w G#5q C#6h D#6h C#6w F6h C#6h");

        Instrument piano_left = new Instrument("piano_left");
        piano_left.setPath_to_notes(mPianoNotesDir.getAbsolutePath());
      //  piano_left.setBar("F5h F6w C#6w F#5q D#6q C#6q D#6w F5q A#5h C6h A#5h Rh Rh");



        piano_right.setBar("A5i C6i D6h rq A6i C6i G6i F6i E6i A5i D6i C6i A5i C6i D6h rq A6i C6i G6i F6i E6i A5i D6i C6i A5i C6i D6i A5i C6i D6i C6i A5i A6i C6i G6i F6i E6i A5i D6i C6i D5i F5i A5i F5i A5i D6i A5i D6i F6i D6i F6i D7w");


piano_left.setBar("D4w F4h C4h D4w A#3h A3h D4w F4h C4h D4w Rw");
        Instrument piano_new = new Instrument("piano_new");
        piano_new.setPath_to_notes(mPianoNotesDir.getAbsolutePath());
        //piano_new.setBar("Rw Rw Rw Rw Ri D5i D5i E5i E5i D5i D5i E5i E5i D5i D5i E5i E5i D5i E5i A5q F#5h");



        Player p = new Player(this.getApplicationContext());
        p.addInstrument(piano_left);
        p.addInstrument(piano_right);
        //p.addInstrument(piano_new);

        p.produceMusic(mOutputFile.getAbsolutePath(), new MusicGeneratingCallback() {
            @Override
            public void onError(String message) {
                mProgressBar.setVisibility(View.GONE);
                mMessageTextView.setText("Error: " + message);
            }

            @Override
            public void onSuccess(String message) {
                mProgressBar.setVisibility(View.GONE);
                mMessageTextView.setText("Music file is generated successfully\n" + mOutputFile.getAbsoluteFile());
                mPlayButton.setEnabled(true);
            }
        });
    }

    private void playMusicFile() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, Uri.parse(mOutputFile.getAbsolutePath()));
        }
        mMediaPlayer.start();
    }
}
