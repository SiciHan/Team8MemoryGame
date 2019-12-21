package com.example.team8memorygame;

import android.os.AsyncTask;

import com.example.team8memorygame.Model.Command;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncToServer extends AsyncTask<Command, Void, JSONObject> {
    IServerResponse callback;

    @Override
    protected void onPreExecute(){}

    @Override
    protected JSONObject doInBackground(Command... cmds){
        Command cmd = cmds[0];
        this.callback = cmd.getCallBack();

        JSONObject jsonObj = null;
        StringBuilder response = new StringBuilder();

        try{
            URL url = new URL(cmd.getServerPt());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            // send data
            if (cmd.getData() != null){
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
                outStream.writeBytes(cmd.getData().toString());
                outStream.flush();
                outStream.close();
            }

            // receive response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null ;){
                response.append(line).append('\n');
            }

            try{
                jsonObj = new JSONObject(response.toString());
                jsonObj.put("context", cmd.getContext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonObj;


    }

//    @Override
//    protected void onProgressUpdate(Void v){}

    @Override
    protected void onPostExecute(JSONObject jsonObj){
        if (jsonObj != null){
            this.callback.onServerResponse(jsonObj);
        }
    }

    public interface IServerResponse{
        void onServerResponse(JSONObject jsonObj);
    }
}
