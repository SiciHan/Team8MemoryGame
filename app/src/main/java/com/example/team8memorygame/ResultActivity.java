package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        init();
    }
    public void init(){
        Intent intent=getIntent();
        boolean isWinner=(Boolean)intent.getSerializableExtra("isWinner");
        textView=(TextView)findViewById(R.id.textView);
        if(isWinner==true){
            textView.setText("Congratulations! You Won!");
        }
        else {
            textView.setText("Good Game.Your opponent won.");
        }
    }
    public void onClickToMain(View v){
        Intent intent=new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
    }
}
