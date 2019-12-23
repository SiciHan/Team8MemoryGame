package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MatchActivity extends AppCompatActivity implements AsyncToServerMultiplayer.IServerResponse{
    CommandForMultiplayers cmd;
    JSONObject jsonObject=new JSONObject();

    String playername="";
    String opponentname="";
    boolean isMatched=false;
    boolean isStart=false;
    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        init();
        matchPlayer();
        startGameListener();
    }
    @Override
    public void onBackPressed(){
        deregister();
        super.onBackPressed();
        //de-register the player

    }
    protected void init(){
        //find the username or else randomly give a name like Guest1234 and display it
        //send the person's name to waitinglist, set the text to now you are in the waiting list
        cmd=new CommandForMultiplayers(this,"set","http://10.0.2.2:65332/Home/SetPerson",null);
        new AsyncToServerMultiplayer().execute(cmd);
    }

    public void matchPlayer(){
        //for each second//check if the person is still in the waitinglist,
        //if not, try to find the person in the matchinglist,//display the players
        //enable the start game button
        //if still in the waiting list: display waiting for your opponents

        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    jsonObject.put("name",playername);//this playername is ""
                    System.out.println("in matchactivity"+jsonObject.toString());//
                }catch (Exception e){
                    e.printStackTrace();
                }
                cmd=new CommandForMultiplayers(MatchActivity.this,"match","http://10.0.2.2:65332/Home/MatchPerson",jsonObject);
                new AsyncToServerMultiplayer().execute(cmd);
                if(opponentname.equals(""))
                    Toast.makeText(MatchActivity.this,"Waiting for your opponent",Toast.LENGTH_LONG).show();
                else{
                    System.out.println("opponent found");
                    isMatched=true;
                    //stop the run

                    startBtn=(Button)findViewById(R.id.startButton);
                    startBtn.setVisibility(View.VISIBLE);
                    startBtn.setClickable(true);
                    handler.removeCallbacks(this);

                }
                if(isMatched==false){
                    handler.postDelayed(this,1000);
                }
            }
        });
    }

    public void startGameListener(){
        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonObject.put("name", playername);//this playername is ""
                    System.out.println("in match player" + jsonObject.toString());//
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cmd = new CommandForMultiplayers(MatchActivity.this, "IsStart", "http://10.0.2.2:65332/Home/IsStart", jsonObject);
                new AsyncToServerMultiplayer().execute(cmd);
                if (isStart == true) {
                    Toast.makeText(MatchActivity.this, "Start your game now", Toast.LENGTH_LONG).show();
                    System.out.println("game started");
                    Intent intent = new Intent(MatchActivity.this, MemoryGameActivity.class);
                    intent.putExtra("playername",playername);
                    intent.putExtra("img6",(byte[])getIntent().getSerializableExtra("img6"));
                    intent.putExtra("enablePause",getIntent().getSerializableExtra("enablePause"));
                    startActivity(intent);
                    handler.removeCallbacks(this);
                } else if (isStart == false) {
                    handler.postDelayed(this, 1000);
                }
            }

        });

    }
    //When Start Game is clicked, tell the server should start the game
    public void onClickStartGame(View v){
        try{
            jsonObject.put("name",playername);//this playername is ""
            System.out.println("start game"+jsonObject.toString());//
        }catch (Exception e){
            e.printStackTrace();
        }
        cmd=new CommandForMultiplayers(this,"StartGame","http://10.0.2.2:65332/Home/StartGame",jsonObject);
        new AsyncToServerMultiplayer().execute(cmd);
    }
    public void deregister(){
        try{
            jsonObject.put("name",playername);//this playername is ""
            System.out.println("in deregister"+jsonObject.toString());//
        }catch (Exception e){
            e.printStackTrace();
        }
        cmd=new CommandForMultiplayers(this,"set","http://10.0.2.2:65332/Home/RemovePerson",jsonObject);
        new AsyncToServerMultiplayer().execute(cmd);
    }


    @Override
    public void onServerResponse(JSONObject jsonObject){
        //if the command is the set object command
        String context="";

        try{
            context=(String) jsonObject.get("context");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(jsonObject==null){
            return;
        }
        else if(context.equals("set")){
            //jsonObject is the person initialized
            try{
                System.out.println("after set"+jsonObject.toString());
                this.playername=(String)jsonObject.get("name");
                System.out.println("playername after set"+playername);
                TextView textView = (TextView) findViewById(R.id.playername);
                textView.setText(playername);
                Toast.makeText(this,"You are in the waiting list",Toast.LENGTH_LONG).show();
            }catch (Exception e){e.printStackTrace();}
        }
        else if(context.equals("match")){
            //jsonObject is the opponent person
            try {
                String status = (String) jsonObject.get("status");
                if (status.equals("no match")) {
                    Toast.makeText(this, "You are matched to no one!", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                try{
                    this.opponentname=(String)jsonObject.get("name");
                    TextView textView = (TextView) findViewById(R.id.opponentname);
                    textView.setText(this.opponentname);
                    Toast.makeText(this,"You are matched to "+ this.opponentname+"!",Toast.LENGTH_LONG).show();
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        }
        else if(context.equals("IsStart")){
            try{
                String status=(String) jsonObject.get("status");
                if(status.equals("true")){
                    isStart=true;
                }
                else if(status.equals("false")){
                    isStart=false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else if(context.equals("StartGame")){
            try{
                String status=(String) jsonObject.get("status");
                if(status.equals("started")){
                    Toast.makeText(this,"Initializing the game",Toast.LENGTH_LONG).show();
                }
                else if(status.equals("no such person in match")){
                    Toast.makeText(this,"No such person in match",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else{
            try {
                String status = (String) jsonObject.get("status");
                if (status.equals("ok")) {
                    System.out.println("set the text to ok");
                    TextView textView = (TextView) findViewById(R.id.message);
                    textView.setText("ok");
                }
            }catch (Exception e){e.printStackTrace();}
        }

    }
}

