package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.team8memorygame.Model.Command;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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
    EditText downloading;
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


        editText1 = findViewById(R.id.editText1);
        editText1.setText("https://stocksnap.io");
        downloading = findViewById(R.id.imageDownloading);
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

    // Create Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the game_menu; adds items to the action bar if it's present
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.musicPause3:
                gameSound.playClickSound();
                onPauseMusic();
                break;
            case R.id.musicResume3:
                gameSound.playClickSound();
                onResumeMusic();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

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

                /*Document doc = Jsoup.connect(params[0]).get();
                Elements img = doc.getElementsByTag("img");
                for(int i=2; i<22; i++){
                    Element e = img.get(i);
                    src = e.absUrl("src");
                    imageUrls.add(src);
                }*/

                ArrayList<String> list_temp = returnImageUrlsFromHtml(URLString(params[0]));
                for(int i = 6; i<26; i++){
                    imageUrls.add(list_temp.get(i));
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
                    downloading.setText("downloading... " + imgNum + "/20");
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
            downloading.setText("Completed !");
            gameSound.playDownloadSound();
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

        private String URLString(String url) {
            String str = "";
            String result = "";
            try {
                URL ur = new URL(url);
                URLConnection conn = ur.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                while (null != (str = br.readLine())) {
                    result += str;
                }
                br.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private ArrayList<String> returnImageUrlsFromHtml(String html) {
            ArrayList<String> imageSrcList = new ArrayList<String>();
            String htmlCode = html;
            Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(htmlCode);
            String quote = null;
            String src = null;
            while (m.find()) {
                quote = m.group(1);
                src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
                imageSrcList.add(src);
            }
            if (imageSrcList == null || imageSrcList.size() == 0) {
                Log.e("imageSrcList", "CAN not find !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return null;
            }
            return imageSrcList;
        }
    }


}
