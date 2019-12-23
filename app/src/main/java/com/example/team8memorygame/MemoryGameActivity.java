package com.example.team8memorygame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.team8memorygame.Model.Command;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGameActivity extends AppCompatActivity
    implements AsyncToServerMultiplayer.IServerResponse{
    private GameSound gameSound;
    int watchAdCount=1;
    int advPoints;
    int clicked = 0;
    boolean faceUp = false;
    int lastClicked = -1;
    int matched = 5;
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running=true;//once the activity starts, the timer will start
    private boolean wasRunning;
    ArrayList<Integer> images=null;
    ImageButton[] buttons=null;
    ArrayList<String> files = null;
    TextView picMatch = null;
    int score = 0;
    int tries = 0;
    //variable needed for multiplayer parts
    boolean enablePause=true;
    CommandForMultiplayers cmd;
    JSONObject jsonObject=new JSONObject();
    String playername=null;
    boolean hasWinner=false;
    boolean isWinner=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        //get the intent from PlayerModeActivity, images in byte[] are also passed through intent
        enablePause=getIntent().getBooleanExtra("enablePause",true);
        playername=(String)getIntent().getSerializableExtra("playername");
        gameWinnerListener();
        // Initialise gameSound
        gameSound=new GameSound(this);
         //do bind service
        doBindService();
//        Intent music=new Intent();
//        music.setClass(this,MusicService.class);
        //startService(music);

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTimer();
        initUI();
        picMatch = findViewById(R.id.picmatches);
        memoryLogic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
        RemoveGame();
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
        gameSound.playClickSound();
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
        gameSound.playClickSound();
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
        if(enablePause==false){
            findViewById(R.id.pause).setVisibility(View.GONE);
            findViewById(R.id.resume).setVisibility(View.GONE);
        }
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
                        tries++;
                        if (buttons[finalI].getTag().toString().equalsIgnoreCase(buttons[lastClicked].getTag().toString())){
                            buttons[finalI].setClickable(false);
                            buttons[lastClicked].setClickable(false);
                            matched++;
                            gameSound.playCorrectSound();

                            faceUp = false;
                            clicked = 0;
                            picMatch.setText(matched + "/6 matches");

                            //if the matches equals 6 and it is the single player mode
                            if(matched == 6 && enablePause==true){
                                gameSound.playWinSound();
                                System.out.println("so smart, you matched 6 pairs in " + seconds + " seconds!");
                                running = false;
                                // without the handler below and if we just run resultDialog directly,
                                // there seems to be a noticeable lag when making the final matching pair
                                Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        calculateScore();
                                        resultDialog();
                                    }
                                });
                            }
                            //if the matches equals 6 and it is the 2-player mode
                            else if(matched==6 && enablePause==false){
                                FinishGame();
                            }
                        } else {
                            gameSound.playWrongSound();
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

    public void resultDialog(){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the custom layout
        // pass null as the parent view because it's going in the dialog layout
        View resultView = getLayoutInflater().inflate(R.layout.result_dialog,null);
        builder.setView(resultView);

        // prevents user from dismissing the dialog
        builder.setCancelable(false);

        // gets the timeScore TextView and sets it according to the seconds int (which has to be parsed into a String or else TextView breaks)
        TextView timeScore = resultView.findViewById(R.id.timeScore);
        System.out.println(timeScore.getText());
        if (advPoints == 0)
            timeScore.setText(String.valueOf(score));
        else
            timeScore.setText(score + " + " + advPoints);

        final EditText playerName = resultView.findViewById(R.id.playerName);
        System.out.println("current name: " + playerName.getText());

        // sets the player's name in result page to whatever name's in the shared preference
        SharedPreferences playerPref =  getSharedPreferences("player", MODE_PRIVATE);
        if(playerPref.contains("name")){
            playerName.setText(playerPref.getString("name",null));
        }

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Left empty as this will be overriden below for positive button
                // validation logic goes below
                gameSound.playClickSound();
            }
        });

        builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameSound.playClickSound();
                Toast.makeText(MemoryGameActivity.this, "It's okay, we all give up now and then.." , Toast.LENGTH_SHORT).show();
                reset();
            }
        });

        // creates and shows the result dialog
        final AlertDialog resultDialog = builder.create();
        resultDialog.show();

        // Overrides the setPositiveButton behaviour. Instead of dismissing it immediately, it goes through a check for EditText's playerName
        resultDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Boolean isError = false;

                //checking for input name
                String name = playerName.getText().toString().trim();

                if(name.isEmpty()){
                    isError = true;
                    playerName.setError("No seriously, what's your name?");
                }

                // if no errors, this block below will save all the necessary details into Shared Preference / Send to database
                if(!isError){
                    SharedPreferences playerPref =  getSharedPreferences("player", MODE_PRIVATE);
                    SharedPreferences.Editor editor = playerPref.edit();
                    editor.putString("name", name);
                    editor.putInt("score", score+advPoints);
                    // Consider using `apply()` instead; `commit` writes its data to persistent storage immediately,
                    // whereas `apply` will handle it in the background
                    editor.apply();

                    resultDialog.dismiss();
                    reset();

                    Intent intent = new Intent(MemoryGameActivity.this, LeaderBoardActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void calculateScore(){
        int timebonus = (30 - seconds) * 5; // (deduct) 5 points deducted per second
        int trybonus = (30 - tries) * 10; // (deduct) 10 points per try
        if(timebonus < 0 ) timebonus = 0;
        if(trybonus < 0 ) trybonus = 0;

        score = timebonus + trybonus;
    }

    private void reset(){
        clicked = 0;
        faceUp = false;
        lastClicked = -1;
        matched = 0;
        //Number of seconds displayed on the stopwatch.
        seconds = 0;
        //Is the stopwatch running?
        running=true;//once the activity starts, the timer will start
        initUI();
        score = 0;
        tries = 0;
        picMatch.setText(matched + "/6 matches");
        if(buttons!=null){
            for(ImageButton button:buttons){
                button.setClickable(true);
                button.setImageResource(R.drawable.code);
                button.setTag("cardBack");
            }
        }
    }

    //MusicService
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the game_menu; adds items to the action bar if it's present
        getMenuInflater().inflate(R.menu.game_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.resetBtn:
                gameSound.playClickSound();
                reset();
                Toast.makeText(MemoryGameActivity.this, "When life gets hard, reset" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.musicPause:
                gameSound.playClickSound();
                onPauseMusic();
                break;
            case R.id.musicResume:
                gameSound.playClickSound();
                onResumeMusic();
                break;
            case R.id.watchAdv:
                gameSound.playClickSound();
                mServ.pauseMusic();
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);

                File file=new File(getFilesDir()+"/videos/adv.mp4");
                Uri uri= FileProvider.getUriForFile(MemoryGameActivity.this,"com.example.team8memorygame.provider",file);
                intent.setDataAndType(uri,"video/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if(watchAdCount<2){
                    advPoints=50;
                    watchAdCount=2;
                    Toast.makeText(MemoryGameActivity.this,"Thanks for watching adv,you have earned "+advPoints+" pts",Toast.LENGTH_LONG).show();
                    // use part to add score pts to ur method
                }
                startActivity(intent);
                break;
            case R.id.watchTut:
                gameSound.playClickSound();
                mServ.pauseMusic();
                intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);

                file=new File(getFilesDir()+"/videos/demo.mp4");
                uri= FileProvider.getUriForFile(MemoryGameActivity.this,"com.example.team8memorygame.provider",file);
                intent.setDataAndType(uri,"video/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void FinishGame(){
        //once move to next page
        //need to remove the game played

        try{
            jsonObject.put("name",playername);//this playername is ""
            System.out.println("start game"+jsonObject.toString());//
        }catch (Exception e){
            e.printStackTrace();
        }
        cmd=new CommandForMultiplayers(this,"finishGame","http://10.0.2.2:65332/Home/FinishGame",jsonObject);
        new AsyncToServerMultiplayer().execute(cmd);
    }
    public void RemoveGame(){
        //once click on finish
        //need to set the winner in the server

        try{
            jsonObject.put("name",playername);//this playername is ""
            System.out.println("start game"+jsonObject.toString());//
        }catch (Exception e){
            e.printStackTrace();
        }
        cmd=new CommandForMultiplayers(this,"removeGame","http://10.0.2.2:65332/Home/RemoveGame",jsonObject);
        new AsyncToServerMultiplayer().execute(cmd);
    }
    public void gameWinnerListener(){
        //constantly check if the winner is set, if set display message
        try{
            jsonObject.put("name",playername);//this playername is ""
        }catch (Exception e){
            e.printStackTrace();
        }
        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                cmd = new CommandForMultiplayers(MemoryGameActivity.this, "GetWinner", "http://10.0.2.2:65332/Home/GetWinner", jsonObject);
                new AsyncToServerMultiplayer().execute(cmd);
                if (hasWinner == false) {
                    handler.postDelayed(this, 1000);
                }else{
                    Intent intent=new Intent(MemoryGameActivity.this,ResultActivity.class);
                    intent.putExtra("isWinner",isWinner);
                    startActivity(intent);
                    handler.removeCallbacks(this);
                }
            }
        });

    }
    @Override
    public void onServerResponse(JSONObject jsonObject){
        if(jsonObject==null){
            return;
        }

        String context="";
        try{
            context=(String) jsonObject.get("context");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(context.equals("finishGame")){
            //no need

        }
        if(context.equals("removeGame")){
            //no need action

        }
        else if (context.equals("GetWinner")){
            //set the isWinner=false/true
            //set has winner to false/true
            String status="";
            String winner="";
            try{
                status=(String)jsonObject.get("status");
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                winner=(String)jsonObject.get("name");
            }catch (Exception e){
                e.printStackTrace();
            }
            if(winner.equals("")){
                hasWinner=false;
            }
            else{
                hasWinner=true;
            }
            if(winner.equals(playername)){
                isWinner=true;
            }else{
                isWinner=false;
            }

        }


    }

}
