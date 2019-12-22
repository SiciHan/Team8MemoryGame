package com.example.team8memorygame;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.team8memorygame.R;

public class MusicService extends Service implements MediaPlayer.OnErrorListener {
    private final IBinder mbinder=new ServiceBinder();
    MediaPlayer mPlayer;
    private int length=0;
    public MusicService(){

    }
    public class ServiceBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // if got error, try change intent to arg0
        return mbinder;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        mPlayer=MediaPlayer.create(this, R.raw.music);
        mPlayer.setOnErrorListener(this);
        if(mPlayer!=null){
            mPlayer.setLooping(true);
            mPlayer.setVolume(1.0f,1.0f);
        }

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                onError(mediaPlayer, i, i1);
                return true;
            }
        });
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        if(mPlayer!=null){
            mPlayer.start();
        }
        return START_NOT_STICKY;
    }
    public void pauseMusic(){
        if(mPlayer!=null){
            if(mPlayer.isPlaying()){
                mPlayer.pause();
                length=mPlayer.getCurrentPosition();
            }
        }
    }
    public void resumeMusic(){
        if(mPlayer!=null){
            if(!mPlayer.isPlaying()){
                mPlayer.seekTo(length);
                mPlayer.start();
            }
        }
    }
    public void startMusic(){
        mPlayer=MediaPlayer.create(this,R.raw.music);
        mPlayer.setOnErrorListener(this);
        if(mPlayer!=null){
            if(!mPlayer.isPlaying()){
                mPlayer.setLooping(true);
                mPlayer.setVolume(1.0f,1.0f);
                mPlayer.start();
            }
        }
    }
    public void stopMusic(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer=null;
        }
    }
    public void onDestroy(){
        super.onDestroy();
        if(mPlayer!=null){
            try{
                mPlayer.stop();
                mPlayer.release();
            }
            finally {
                mPlayer=null;
            }
        }
    }
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(this,"Music Player has failed",Toast.LENGTH_SHORT).show();
        if(mPlayer!=null){
            try{
                mPlayer.stop();
                mPlayer.release();
            }
            finally{
                mPlayer=null;
            }
        }
        return false;
    }
}
