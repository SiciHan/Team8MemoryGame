package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
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

    int clicked = 0;
    boolean faceUp = false;
    int lastClicked = -1;
    int matched = 0;
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running=true;//once the activity starts, the timer will start
    private boolean wasRunning;
    ArrayList<Integer> images=null;
    ImageButton[] buttons=null;
    ArrayList<String> files = null;
    TextView picMatch = null;

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
        initUI();
        picMatch = findViewById(R.id.picmatches);
        memoryLogic();

        Button reset = findViewById(R.id.resetBtn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

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
        view.setClickable(false);
        view.setVisibility(View.GONE);
        Button pauseBtn=(Button)findViewById(R.id.pause);
        pauseBtn.setClickable(true);
        pauseBtn.setVisibility(View.VISIBLE);
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
        view.setClickable(false);
        view.setVisibility(View.GONE);
        Button resumeBtn=(Button)findViewById(R.id.resume);
        resumeBtn.setClickable(true);
        resumeBtn.setVisibility(View.VISIBLE);
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

    protected void initUI(){
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

        //images list for testing until the 6 images are downloaded into the device from previous activity
        images = new ArrayList<>();
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

        Collections.shuffle(images);

        files = new ArrayList<>();
        files.add("image1.jpg");
        files.add("image2.jpg");
        files.add("image3.jpg");
        files.add("image4.jpg");
        files.add("image5.jpg");
        files.add("image6.jpg");
        files.add("image1.jpg");
        files.add("image2.jpg");
        files.add("image3.jpg");
        files.add("image4.jpg");
        files.add("image5.jpg");
        files.add("image6.jpg");

        Collections.shuffle(files);

    }

    protected void memoryLogic(){
        for (int i=0;i<buttons.length;i++){
            final int finalI = i;
            buttons[i].setTag("cardBack");
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Tag: " + buttons[finalI].getTag());
                    String imageName = (String) buttons[finalI].getTag();
                    if (imageName.equals("cardBack") && !faceUp){
                        buttons[finalI].setImageResource(images.get(finalI));
                        buttons[finalI].setTag(images.get(finalI).toString());
                        //uncomment code below to use bitmap images downloaded from previous activity from the phone's internal storage
                        /*buttons[finalI].setImageBitmap(BitmapFactory.decodeFile(getFilesDir()+"/"+files.get(finalI)));
                        buttons[finalI].setTag(files.get(finalI));*/
                        if (clicked == 0) {
                            lastClicked = finalI;
                            System.out.println("Lastclicked tag: " + lastClicked + ", i: " + finalI);
                        }
                        System.out.println("Flipped the card, setting tag to: " + buttons[finalI].getTag().toString());
                        clicked++;
                    }

                    //The second criteria is to ensure only 2 cards are clicked and flipped. Any further clicks on other cards won't mistakenly trigger the handler
                    if (clicked == 2 && !buttons[finalI].getTag().toString().equals("cardBack")){
                        buttons[finalI].setClickable(false);
                        faceUp = true;
                        if (buttons[finalI].getTag().toString().equalsIgnoreCase(buttons[lastClicked].getTag().toString())){
                            buttons[finalI].setClickable(false);
                            buttons[lastClicked].setClickable(false);
                            matched++;
                            faceUp = false;
                            clicked = 0;
                            picMatch.setText(matched + "/6 matches");
                            //if the matches equals 6
                            //onClickPause();
                            if(matched == 6){
                                System.out.println("so smart, you matched 6 pairs in " + seconds + " seconds!");
                                running = false;
                            }
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    faceUp = false;
                                    clicked = 0;
                                    buttons[finalI].setImageResource(R.drawable.code);
                                    buttons[finalI].setTag("cardBack");
                                    buttons[lastClicked].setImageResource(R.drawable.code);
                                    buttons[lastClicked].setTag("cardBack");
                                    buttons[finalI].setClickable(true);
                                    buttons[lastClicked].setClickable(true);
                                }
                            }, 1000);
                        }
                    } else if (clicked == 0){
                        faceUp = false;
                    } else if (clicked == 1){
                        buttons[lastClicked].setClickable(false);
                    }
                }
            });
        }
    }
}
