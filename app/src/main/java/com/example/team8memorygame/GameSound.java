package com.example.team8memorygame;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

public class GameSound {
    private AudioAttributes audioAttributes;
    final int soundStreamMax=3;

    private static SoundPool soundhit;
    private static int correct; // if two matches
    private static int wrong; // if mismatch
    private static int win;
    private static int click;
    private static int download;

    public GameSound(Context context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            audioAttributes=new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundhit=new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(soundStreamMax)
                    .build();
        }
        else{
            soundhit=new SoundPool(soundStreamMax,AudioManager.STREAM_MUSIC,0);
        }
        correct =soundhit.load(context,R.raw.correct,1);
        wrong=soundhit.load(context,R.raw.wrong,1);
        win=soundhit.load(context,R.raw.win,1);
        click=soundhit.load(context,R.raw.coin,1);
        download=soundhit.load(context,R.raw.download,1);
    }
    public void playCorrectSound(){
        soundhit.play(correct,1.0f,1.0f,1,0,1.0f);
    }
    public void playWrongSound(){
        soundhit.play(wrong,1.0f,1.0f,1,0,1.0f);
    }
    public void playWinSound(){ soundhit.play(win,1.0f,1.0f,1,0,1.0f);}
    public void playClickSound(){ soundhit.play(click,1.0f,1.0f,1,0,1.0f);}
    public void playDownloadSound(){ soundhit.play(download,1.0f,1.0f,1,0,1.0f);}
}


