package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MemoryGameActivity extends AppCompatActivity {

/*    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMilliseconds = 180000;*/
    TextView countDownTimer;
    int clicked = 0;
    boolean turnOver = false;
    int lastClicked = -1;
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running=true;//once the activity starts, the timer will start
    private boolean wasRunning;
    ImageButton[] buttons=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTimer();
        //countDownTimer = (TextView)findViewById(R.id.countDownTimer);

        final ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.camel);
        images.add(R.drawable.fox);
        images.add(R.drawable.koala);
        images.add(R.drawable.lion);
        images.add(R.drawable.monkey);
        images.add(R.drawable.wolf);
        images.add(R.drawable.camel);
        images.add(R.drawable.fox);
        images.add(R.drawable.koala);
        images.add(R.drawable.lion);
        images.add(R.drawable.monkey);
        images.add(R.drawable.wolf);

        buttons = new ImageButton[]{
                findViewById(R.id.Image1),
                findViewById(R.id.Image2),
                findViewById(R.id.Image3),
                findViewById(R.id.Image4),
                findViewById(R.id.Image5),
                findViewById(R.id.Image6),
                findViewById(R.id.Image7),
                findViewById(R.id.Image8),
                findViewById(R.id.Image9),
                findViewById(R.id.Image10),
                findViewById(R.id.Image11),
                findViewById(R.id.Image12)
        };

        Collections.shuffle(images);
        for (int i = 0; i < images.size(); i++) {
            System.out.println("Button: " + (i+1) + ", Tag: " + images.get(i));
        }

        for (int i=0;i<buttons.length;i++){
            final int finalI = i;
            buttons[i].setTag("cardBack");
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Tag: " + buttons[finalI].getTag());
                    String imageName = (String) buttons[finalI].getTag();
                    if (imageName.equals("cardBack") && !turnOver){
                        buttons[finalI].setImageResource(images.get(finalI));
                        buttons[finalI].setTag(images.get(finalI).toString());
                        if (clicked == 0) {
                            lastClicked = finalI;
                            System.out.println("Lastclicked tag: " + lastClicked + ", i: " + finalI);
                        }
                        System.out.println("Flipped the card, setting tag to: " + buttons[finalI].getTag().toString());
                        clicked++;
                    } else if (!imageName.contains("cardBack")){
                        buttons[finalI].setImageResource(R.drawable.code);
                        buttons[finalI].setTag("cardBack");
                        System.out.println("WHY YOU CLICK ME");
                        clicked--;
                    }
                    if (clicked == 2){
                        System.out.println("Max clicked reached");
                        turnOver = true;
                        if (buttons[finalI].getTag().toString().equalsIgnoreCase(buttons[lastClicked].getTag().toString())){
                            buttons[finalI].setClickable(false);
                            buttons[lastClicked].setClickable(false);
                            turnOver = false;
                            clicked = 0;
                            //if the matches equals 6
                            //onClickPause();
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    turnOver = false;
                                    clicked = 0;
                                    buttons[finalI].setImageResource(R.drawable.code);
                                    buttons[finalI].setTag("cardBack");
                                    buttons[lastClicked].setImageResource(R.drawable.code);
                                    buttons[lastClicked].setTag("cardBack");
                                }
                            }, 1000);
                        }
                    } else if (clicked == 0){
                        turnOver = false;
                    }
                }
            });
        }

        Button reset = findViewById(R.id.resetBtn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (int i = 0; i < buttons.length; i++) {
//                    buttons[i].setTag("cardBack");
//                    buttons[i].setImageResource(R.drawable.code);
//                    buttons[i].setClickable(true);
//                }
//                turnOver = false;
//                clicked = 0;
//                Collections.shuffle(images);
//                mTimeLeftInMilliseconds = 180000;
//                startTimer();
//                updateCountDownText();
                finish();
                startActivity(getIntent());
            }
        });
    }

    /*protected void startTimer(){
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
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }
    //Start the stopwatch running when the resume button is clicked.
    public void onClickStart(View view) {
        running = true;
        if(buttons!=null){
            for(ImageButton button:buttons){
                button.setClickable(true);
            }
        }
    }
    //Stop the stopwatch running when the pause button is clicked.
    public void onClickStop(View view) {
        running = false;
        //need to freeze the users from clicking on the imagebuttons
        if(buttons!=null){
            for(ImageButton button:buttons){
                button.setClickable(false);
            }
        }
        //grey out the pause button
        //enable to the resume button
    }
    //Sets the number of seconds on the timer.
    private void runTimer() {
        final TextView timeView = (TextView)findViewById(R.id.stopwatch);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format("%d:%02d:%02d",
                        hours, minutes, secs);
                timeView.setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
