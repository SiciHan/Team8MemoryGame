package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;

public class MemoryGameActivity extends AppCompatActivity {

    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMilliseconds = 180000;
    TextView countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        countDownTimer = (TextView)findViewById(R.id.countDownTimer);
        startTimer();
    }

    protected void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMilliseconds,1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMilliseconds = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    void updateCountDownText(){
        int minutes = (int)(mTimeLeftInMilliseconds / 1000) / 60;
        int seconds = (int)(mTimeLeftInMilliseconds / 1000) % 60;

        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        countDownTimer.setText(timeLeft);
    }
}
