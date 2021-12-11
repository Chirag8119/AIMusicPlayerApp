package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {
//purpose of this activity is to extract audio files from external source and displays on app screen

    private String[] itemAll; //String array
    private ListView mSongsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        mSongsList = findViewById(R.id.songsList);
        externalStoragePermission();

    }

    private void externalStoragePermission(){
        //giving the permission to fetch audio files from phone(using allow)
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
displayAudioSongsName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        displayAudioSongsName();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
token.continuePermissionRequest();
                    }
                }).check();


    }


    //read file from the storage and store it to array List
    //read only audio songs
    public ArrayList<File> readOnlyAudioSongs(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] allFiles=file.listFiles();
        for(File individualFile : allFiles){
            if(individualFile.isDirectory() && !individualFile.isHidden()){
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            }
            else{
                if(individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wav")|| individualFile.getName().endsWith(".wma") ){
                    //fetch all types of audio files like .mp3, .aac, .wav, .wma
                    arrayList.add(individualFile);
                }
            }
        }


        return arrayList;
    }

    private void displayAudioSongsName(){

        final ArrayList<File> audioSongs=readOnlyAudioSongs(Environment.getExternalStorageDirectory());
        itemAll=new String[audioSongs.size()];
        for(int songCounter=0; songCounter<audioSongs.size(); songCounter++){
            itemAll[songCounter]=audioSongs.get(songCounter).getName();//In this way we get the song name and store it into the itemAll string array
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(MainActivity2.this,android.R.layout.simple_list_item_1,itemAll);
            mSongsList.setAdapter(arrayAdapter);


         //if user click on any song thn we must play that song
          mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 String songName=mSongsList.getItemAtPosition(i).toString();
                  Intent intent= new Intent(MainActivity2.this,MainActivity.class);
                  intent.putExtra("song",audioSongs);
                  intent.putExtra("name",songName);
                  intent.putExtra("position",i);
                  startActivity(intent);


              }
          });

        }

    }
}