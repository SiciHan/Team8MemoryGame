package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.team8memorygame.Model.Command;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity
    implements AsyncToServer.IServerResponse{

    TextView[] leader = null;
    TextView[] score = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);



        initUI();
        requestData();

    }

    //music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection sConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class), sConn, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(sConn);
            mIsBound = false;
        }
    }

    protected void onResumeMusic() {
        super.onResume();
        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    protected void onPauseMusic() {
        super.onPause();
        mServ.pauseMusic();
    }

    protected void onDestroyMusic() {
        super.onDestroy();
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);
    }

    protected void initUI(){
        leader = new TextView[]{
                findViewById(R.id.leader1),
                findViewById(R.id.leader2),
                findViewById(R.id.leader3),
                findViewById(R.id.leader4),
                findViewById(R.id.leader5),
        };
        score = new TextView[]{
                findViewById(R.id.score1),
                findViewById(R.id.score2),
                findViewById(R.id.score3),
                findViewById(R.id.score4),
                findViewById(R.id.score5),
        };
    }



    protected void requestData(){
        // Need to set "Port: 65332" to your Visual Studio own port number
        Command cmd = new Command(this, "get",
                "http://10.0.2.2:65332/Home/getPlayer", null);

        new AsyncToServer().execute(cmd);

    }

    public void onServerResponse(JSONArray jsonArr){

        if (jsonArr == null){
            return;
        }

        try {
            for (int i = 0; i<jsonArr.length(); i++){

                String name = null;
                int time = 0;
                JSONObject obj = jsonArr.getJSONObject(i);
                name = obj.getString("PlayerName");
                time = obj.getInt("Time");
                leader[i].setText(name);
                score[i].setText(Integer.toString(time));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
