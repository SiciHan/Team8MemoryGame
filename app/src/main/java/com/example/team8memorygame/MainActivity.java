package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.team8memorygame.Model.Command;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
    implements AsyncToServer.IServerResponse{
Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=findViewById(R.id.MoveToGameBtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MemoryGameActivity.class);
                startActivity(intent);
            }
        });


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
