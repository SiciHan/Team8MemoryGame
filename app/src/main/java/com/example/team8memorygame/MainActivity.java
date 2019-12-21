package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
Button btn1;
private GameSound gameSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameSound=new GameSound(this);
        // do bind service
        doBindService();
        Intent music=new Intent();
        music.setClass(this,MusicService.class);
        startService(music);
        Button resumeMusic=findViewById(R.id.musicResume0);
        resumeMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResumeMusic();
            }
        });
        Button pauseMusic=findViewById(R.id.musicPause0);
        pauseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPauseMusic();
            }
        });
        btn1=findViewById(R.id.MoveToGameBtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSound.playCorrectSound();
                Intent intent=new Intent(MainActivity.this,MemoryGameActivity.class);
                startActivity(intent);
            }
        });

    }

    //music service
    private boolean mIsBound=false;
    private MusicService mServ;
    private ServiceConnection sConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mServ=((MusicService.ServiceBinder)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ=null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),sConn, Context.BIND_AUTO_CREATE);
        mIsBound=true;
    }
    void doUnbindService(){
        if(mIsBound){
            unbindService(sConn);
            mIsBound=false;
        }
    }
    protected void onResumeMusic(){
        super.onResume();
        if(mServ!=null){
            mServ.resumeMusic();
        }
    }
    protected void onPauseMusic(){
        super.onPause();
        mServ.pauseMusic();
    }

    protected void onDestroyMusic(){
        super.onDestroy();
        doUnbindService();
        Intent music=new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }
}
