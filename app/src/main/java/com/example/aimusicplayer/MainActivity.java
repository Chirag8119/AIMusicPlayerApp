package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout parentLayout; // when touching the screen of music player
    private SpeechRecognizer speechRecognizer;
    private  Intent speechIntent;
    private String keeper="";

    private ImageView pausePlayBtn , nextBtn, previousBtn;
    private TextView songNameTxt;
    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode="ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoicePermission();

        pausePlayBtn=findViewById(R.id.play_pause_btn);
        nextBtn=findViewById(R.id.next_btn);
        previousBtn=findViewById(R.id.previous_btn);
        imageView=findViewById(R.id.logo);
        lowerRelativeLayout=findViewById(R.id.lower);
        voiceEnabledBtn=findViewById(R.id.voice_enabled_btn);
        songNameTxt=findViewById(R.id.songName);

        speechRecognizer=speechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceiveValuesAndStartPlaying();//calling the method
        imageView.setBackgroundResource(R.drawable.logo);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override //we will use only onResults
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesFound =bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matchesFound!=null){
                   if(mode.equals("ON")){
                       keeper=matchesFound.get(0);

                       //fixing the command for playing and pause
                       if(keeper.equals("pause the song")){
                           playPauseSong();
                           Toast.makeText(MainActivity.this,"Command :"+keeper,Toast.LENGTH_LONG).show();

                       }
                       else   if(keeper.equals("play the song")){
                           playPauseSong();
                           Toast.makeText(MainActivity.this,"Command : "+keeper,Toast.LENGTH_LONG).show();

                       }
                       else   if(keeper.equals("play next song")){
                           playNextSong();
                           Toast.makeText(MainActivity.this,"Command : "+keeper,Toast.LENGTH_LONG).show();

                       }
                       else   if(keeper.equals("play previous song")){
                           playPreviousSong();
                           Toast.makeText(MainActivity.this,"Command : "+keeper,Toast.LENGTH_LONG).show();

                       }
                   }


                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        parentLayout=findViewById(R.id.parentLayout);

        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //action up(when leave the finger up) and action down(press long on screen) features
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechIntent);
                        keeper="";
                    break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                    break;
                }
                return false;
            }
        });

        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("ON")){
                    mode="OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else{
                    mode="ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

pausePlayBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        playPauseSong();
    }
});

//playing through button
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myMediaPlayer.getCurrentPosition()>0){
                    playPreviousSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myMediaPlayer.getCurrentPosition()>0){
                    playNextSong();
                }
            }
        });

    }

    private void validateReceiveValuesAndStartPlaying(){
        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName=mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);
        position=bundle.getInt("position",0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(MainActivity.this,uri);
        myMediaPlayer.start();
    }

    //speech to text
    private void checkVoicePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {//giving permission to latest phones  m=marshmello
            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent =new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong(){
        imageView.setBackgroundResource(R.drawable.four);
        if(myMediaPlayer.isPlaying()){
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }
        else{
            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    //for play next songs
    private void playNextSong(){
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position=((position+1)%mySongs.size()); //to move to next song
        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=myMediaPlayer.create(MainActivity.this,uri);
        mSongName=mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();
        imageView.setBackgroundResource(R.drawable.three);
        if(myMediaPlayer.isPlaying()){
            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else{
            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }

    }

    //to play previous song
    private void playPreviousSong(){
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position=((position-1)<0 ? (mySongs.size()-1):(position-1));// to goto previous song
        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=myMediaPlayer.create(MainActivity.this,uri);
        mSongName=mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();
        imageView.setBackgroundResource(R.drawable.two);
        if(myMediaPlayer.isPlaying()){
            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else{
            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }
}