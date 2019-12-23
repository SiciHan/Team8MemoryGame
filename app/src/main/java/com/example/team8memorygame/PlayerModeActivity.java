package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayerModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_mode);
    }
    public void onClickSinglePlayer(View v){

        Intent intent=new Intent(this,MemoryGameActivity.class);
        intent.putExtra("img6",(byte[])getIntent().getSerializableExtra("img6"));
        intent.putExtra("enablePause",true);
        startActivity(intent);
    }
    public void onClickMultiPlayer(View v){
        Intent intent=new Intent(this, MatchActivity.class);
        intent.putExtra("img6",(byte[])getIntent().getSerializableExtra("img6"));
        intent.putExtra("enablePause",false);
        startActivity(intent);
    }
}
