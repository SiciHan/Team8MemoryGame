package com.example.team8memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
    implements AsyncToServer.IServerResponse{
Button btn1;
    Button fetch;
    ProgressBar progressBar;
    MyTask mTask;
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
    EditText editText1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=findViewById(R.id.MoveToGameBtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MemoryGameActivity.class);
                startActivity(intent);
            }
        });
        editText1 = findViewById(R.id.editText1);
        editText1.setText("https://pixabay.com/");
        progressBar = findViewById(R.id.progressBar);
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
        fetch = findViewById(R.id.fetch);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask = new MyTask();
                mTask.execute(editText1.getText().toString());

            }
        });


        // For sending jsonObject to server
        // Create a jsonObject
        JSONObject jsonObj = new JSONObject();
        try{
            jsonObj.put("PlayerName", "Hongwei");
            jsonObj.put("Time",28);
        }catch (Exception e){
            e.printStackTrace();
        }
        // send jsonObj(data) to server
        sendData(jsonObj);
        // request for data from server
//        requestData();

    }
    private List<String> getImageUrls(String targetUrl){
        try {
            List<String> imageUrls = new ArrayList<>();
            final Connection connect = Jsoup.connect(targetUrl);
            connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
            final Document document = connect.get();
            Elements imgElements = document.select("img[src]");
            int count = 0;
            for (Element e:
                    imgElements) {
                if (Pattern.matches(".*?jpe?g|png|git|$", e.attr("src"))) {
                    imageUrls.add(e.attr("src"));
                }
                count++;
                if(count == 20) break;
            }
            return imageUrls;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            InputStream inputStream=connection.getInputStream();
            bitmap= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    protected void sendData(JSONObject data){
        // Need to set "Port: 65332" to your Visual Studio own port number
        Command cmd = new Command(this, "set",
                "http://10.0.2.2:65332/Home/setPlayer", data);

        new AsyncToServer().execute(cmd);
    }

    protected void requestData(){
        // Need to set "Port: 65332" to your Visual Studio own port number
        Command cmd = new Command(this, "get",
                "http://10.0.2.2:65332/Home/getPlayer", null);

        new AsyncToServer().execute(cmd);

    }

    public void onServerResponse(JSONObject jsonObj){
        int id = 0;
        String name = "";
        int time = 0;

        if (jsonObj == null){
            return;
        }
        try{
            String context = (String)jsonObj.get("context");
            if (context.compareTo("get") == 0){
                id = (int)jsonObj.get("PlayerId");
                name = (String)jsonObj.get("PlayerName");
                time = (int)jsonObj.get("Time");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private class MyTask extends AsyncTask<String, Integer, List<Bitmap>> {
        @Override
        protected void onPreExecute() {
            fetch.setText("fetching...");
            fetch.setEnabled(false);
        }


        @Override
        protected List<Bitmap> doInBackground(String... params) {
            try {
                List<String> urls = getImageUrls(params[0]);
                publishProgress(20);
                List<Bitmap> bitmapList = new ArrayList<>();
                int count = 0;
                for (String url:
                        urls) {
                    count++;
                    Bitmap bitmap = GetImageInputStream(url);
                    String fileName = "image" + count;
                    File publicPath = Environment.getExternalStorageDirectory();
                    File file = new File(publicPath,"/" + fileName + ".jpg");
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    bitmapList.add(bitmap);
                    out.flush();
                    out.close();
                    publishProgress(20 + count * 4);
                }
                publishProgress(100);
                return bitmapList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
//            Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
            progressBar.setProgress(progresses[0]);
        }

        @Override
        protected void onPostExecute(List<Bitmap> result) {
//            Log.i(TAG, "onPostExecute(Result result) called");
            fetch.setText("fetched");
            if(result.size() > 0) imageView1.setImageBitmap(result.get(0));
            if(result.size() > 1) imageView2.setImageBitmap(result.get(1));
            if(result.size() > 2) imageView3.setImageBitmap(result.get(2));
            if(result.size() > 3) imageView4.setImageBitmap(result.get(3));
            if(result.size() > 4) imageView5.setImageBitmap(result.get(4));
            if(result.size() > 5) imageView6.setImageBitmap(result.get(5));
            if(result.size() > 6) imageView7.setImageBitmap(result.get(6));
            if(result.size() > 7) imageView8.setImageBitmap(result.get(7));
            if(result.size() > 8) imageView9.setImageBitmap(result.get(8));
            if(result.size() > 9) imageView10.setImageBitmap(result.get(9));
            if(result.size() > 10) imageView11.setImageBitmap(result.get(10));
            if(result.size() > 11) imageView12.setImageBitmap(result.get(11));
            if(result.size() > 12) imageView13.setImageBitmap(result.get(12));
            if(result.size() > 13) imageView14.setImageBitmap(result.get(13));
            if(result.size() > 14) imageView15.setImageBitmap(result.get(14));
            if(result.size() > 15) imageView16.setImageBitmap(result.get(15));
            if(result.size() > 16) imageView17.setImageBitmap(result.get(16));
            if(result.size() > 17) imageView18.setImageBitmap(result.get(17));
            if(result.size() > 18) imageView19.setImageBitmap(result.get(18));
            if(result.size() > 19) imageView20.setImageBitmap(result.get(19));
            if(result.size() < 20){
                fetch.setText("fetch");
                fetch.setEnabled(true);
            }
        }

        @Override
        protected void onCancelled() {
//            Log.i(TAG, "onCancelled() called");
            fetch.setText("fetch");
            progressBar.setProgress(0);
        }
    }


}
