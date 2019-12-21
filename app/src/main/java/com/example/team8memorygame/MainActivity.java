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

import com.example.team8memorygame.Model.Command;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
    implements AsyncToServer.IServerResponse{
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

        // For sending jsonObject to server
        // Create a jsonObject
        JSONObject jsonObj = new JSONObject();
        try{
            jsonObj.put("PlayerName", "Hongwei");
            jsonObj.put("Time",28);
        }catch (Exception e){
            e.printStackTrace();
        }
        // send jsonObj(data) to server
        sendData(jsonObj);
        // request for data from server
//        requestData();

    }

    protected void sendData(JSONObject data){
        // Need to set "Port: 65332" to your Visual Studio own port number
        Command cmd = new Command(this, "set",
                "http://10.0.2.2:65332/Home/setPlayer", data);

        new AsyncToServer().execute(cmd);
    }

    protected void requestData(){
        // Need to set "Port: 65332" to your Visual Studio own port number
        Command cmd = new Command(this, "get",
                "http://10.0.2.2:65332/Home/getPlayer", null);

        new AsyncToServer().execute(cmd);

    }

    public void onServerResponse(JSONObject jsonObj){
        int id = 0;
        String name = "";
        int time = 0;

        if (jsonObj == null){
            return;
        }
        try{
            String context = (String)jsonObj.get("context");
            if (context.compareTo("get") == 0){
                id = (int)jsonObj.get("PlayerId");
                name = (String)jsonObj.get("PlayerName");
                time = (int)jsonObj.get("Time");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
