package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.team8memorygame.Model.Command;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{
    Button btn1;

    private GameSound gameSound;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;
    ImageView imageView10;
    ImageView imageView11;
    ImageView imageView12;
    ImageView imageView13;
    ImageView imageView14;
    ImageView imageView15;
    ImageView imageView16;
    ImageView imageView17;
    ImageView imageView18;
    ImageView imageView19;
    ImageView imageView20;
    ImageView imageView = null;
    EditText editText1;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    String src;
    ArrayList imageUrls = new ArrayList();
    ProgressBar progressBar;
    int imgNum = 0;
    int selected = 0;
    Intent intent;
    Button fetch;
    MyTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameSound = new GameSound(this);
        // do bind service
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
        Button resumeMusic = findViewById(R.id.musicResume0);
        resumeMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResumeMusic();
            }
        });
        Button pauseMusic = findViewById(R.id.musicPause0);
        pauseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPauseMusic();
            }
        });
        btn1 = findViewById(R.id.MoveToGameBtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSound.playCorrectSound();
                Intent intent = new Intent(MainActivity.this, MemoryGameActivity.class);
                startActivity(intent);
            }
        });
        editText1 = findViewById(R.id.editText1);
        editText1.setText("https://stocksnap.io");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageView10 = findViewById(R.id.imageView10);
        imageView11 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);
        imageView13 = findViewById(R.id.imageView13);
        imageView14 = findViewById(R.id.imageView14);
        imageView15 = findViewById(R.id.imageView15);
        imageView16 = findViewById(R.id.imageView16);
        imageView17 = findViewById(R.id.imageView17);
        imageView18 = findViewById(R.id.imageView18);
        imageView19 = findViewById(R.id.imageView19);
        imageView20 = findViewById(R.id.imageView20);
        intent = new Intent(MainActivity.this, PlayerModeActivity.class);
        fetch = findViewById(R.id.fetch);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText1.getText().toString();
                String saveToPath = getFilesDir() + "/relax.jpg";
                mTask = new MyTask();
                mTask.execute(url, saveToPath);
            }
        });

    }

    //music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection sConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class), sConn, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(sConn);
            mIsBound = false;
        }
    }

    protected void onResumeMusic() {
        super.onResume();
        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    protected void onPauseMusic() {
        super.onPause();
        mServ.pauseMusic();
    }

    protected void onDestroyMusic() {
        super.onDestroy();
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

        // For sending jsonObject to server
        // Create a jsonObject
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("PlayerName", "Hongwei");
            jsonObj.put("Time", 28);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*private List<String> getImageUrls(String targetUrl) {
        try {
            imageUrls = new ArrayList<>();
            final Connection connect = Jsoup.connect(targetUrl);
            connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
            final Document document = connect.get();
            Elements imgElements = document.select("img[src]");
            int count = 0;
            for (Element e :
                    imgElements) {
                if (Pattern.matches(".*?jpe?g|png|git|$", e.attr("src"))) {
                    imageUrls.add(e.attr("src"));
                }
                count++;
                if (count == 20) break;
            }
            return imageUrls;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }*/




    private class MyTask extends AsyncTask<String, Integer, ArrayList<Bitmap>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetch.setText("fetching...");
            fetch.setEnabled(false);
            imgNum = 0;
            bitmaps.clear();
            imageUrls.clear();
            selected = 0;
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            long imageLen = 0;
            long totalSoFar = 0;
            int readLen = 0;
            Bitmap bitmap = null;


            try {

                Document doc = Jsoup.connect(params[0]).get();
                Elements img = doc.getElementsByTag("img");
                for(int i=2; i<22; i++){
                    Element e = img.get(i);
                    src = e.absUrl("src");
                    imageUrls.add(src);
                }

                for(String ur: (ArrayList<String>)imageUrls){
                    URL single_url = new URL(ur);
                    HttpURLConnection conn = (HttpURLConnection)single_url.openConnection();
                    conn.connect();
                    imageLen += conn.getContentLength();
                }

                for(String ur: (ArrayList<String>)imageUrls){
                    URL single_url = new URL(ur);
                    HttpURLConnection conn = (HttpURLConnection)single_url.openConnection();
                    conn.connect();

                    byte[] data = new byte[1024];
                    InputStream in = single_url.openStream();
                    BufferedInputStream bufIn = new BufferedInputStream(in,2048);
                    OutputStream out = new FileOutputStream(params[1]);

                    while((readLen = bufIn.read(data)) != -1){
                        totalSoFar += readLen;
                        out.write(data, 0, readLen);

                        publishProgress((int)((totalSoFar * 100)/imageLen));
                    }

                    File file = new File(params[1]);
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    bitmaps.add(bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmaps;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
//            Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
            super.onProgressUpdate(progresses);
            progressBar.setProgress(Math.round(progresses[0]));

            if(bitmaps != null){
                if(bitmaps.size() >= imgNum+1){
                    imgNum++;
                    imageView = findViewById(getResources().getIdentifier(
                            "imageView" + imgNum, "id", getPackageName()
                    ));
                    imageView.setImageBitmap(bitmaps.get(imgNum-1));
                    imageView.setTag(R.id.tag_first, imgNum-1);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(view.getTag(R.id.tag_second) == null || view.getTag(R.id.tag_second).toString() != "clicked"){
                                view.setTag(R.id.tag_second,"clicked");
                                selected++;
                                byte[] bytes = bitmap2Bytes(bitmaps.get(Integer.parseInt(String.valueOf(view.getTag(R.id.tag_first)))));
                                intent.putExtra("img"+selected, bytes);
                                ImageView imgview = (ImageView)view;
                                imgview.setBackgroundResource(R.drawable.black);
                                if(selected >= 6){
                                    startActivity(intent);
                                    //finish();
                                }
                            }
                            else{
                                //view.setTag(null);
                                intent.removeExtra("img" + selected);
                                selected--;
                                ImageView imageview = (ImageView)view;
                                imageview.setImageBitmap(bitmaps.get(Integer.parseInt(String.valueOf(view.getTag(R.id.tag_first)))));
                                imageview.setBackground(null);
                                imageview.setTag(R.id.tag_second,"unclicked");
                            }
                        }
                    });
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
//            Log.i(TAG, "onPostExecute(Result result) called");
            super.onPostExecute(result);
            fetch.setText("fetched");
            if (result.size() < 20) {
                fetch.setText("fetch");
                fetch.setEnabled(true);
            }
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onCancelled() {
//            Log.i(TAG, "onCancelled() called");
            fetch.setText("fetch");
            progressBar.setProgress(0);
        }

        private byte[] bitmap2Bytes(Bitmap bm){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
    }


}
