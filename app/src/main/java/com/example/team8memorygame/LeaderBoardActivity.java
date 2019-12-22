package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

                JSONObject obj = jsonArr.getJSONObject(i);
                String name = obj.getString("PlayerName");
                int time = obj.getInt("Time");
                leader[0].setText(name);
                score[0].setText(time);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
