package com.example.team8memorygame;

import android.os.AsyncTask;

import com.example.team8memorygame.Model.Command;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncToServer extends AsyncTask<Command, Void, JSONArray> {
    IServerResponse callback;

    @Override
    protected void onPreExecute(){}

    @Override
    protected JSONArray doInBackground(Command... cmds){
        Command cmd = cmds[0];
        this.callback = cmd.getCallBack();

        JSONObject jsonObj = null;
        JSONArray jsonArr = null;
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

                // return jsonArr
                jsonArr = new JSONArray(response.toString());
//                for (int i = 0; i < jsonArr.length(); i++){
//                    // get individual jsonObj
//                    jsonObj = jsonArr.getJSONObject(i);
//                    jsonObj.put("context", cmd.getContext());
//                    // publish jsonObj to list view
////                    publishProgress(jsonObj);
//
//                }

//                jsonObj = new JSONObject(response.toString());
//                jsonObj.put("context", cmd.getContext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonArr;


    }

//    @Override
//    protected void onProgressUpdate(Void v){}

    @Override
    protected void onPostExecute(JSONArray jsonArr){
        if (jsonArr != null){
            this.callback.onServerResponse(jsonArr);
        }
    }

    public interface IServerResponse{
        void onServerResponse(JSONArray jsonArr);
    }
}
