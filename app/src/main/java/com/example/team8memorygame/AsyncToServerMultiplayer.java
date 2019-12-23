package com.example.team8memorygame;


import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncToServerMultiplayer extends AsyncTask<CommandForMultiplayers,Void, JSONObject> {
    //This task will take CommandForMultiplayers as input and Jsonobject as output
    IServerResponse callback;
    protected JSONObject doInBackground(CommandForMultiplayers...cmds){
        CommandForMultiplayers cmd=cmds[0];
        this.callback=cmd.callback;
        JSONObject jsonObject=null;
        StringBuilder response=new StringBuilder();
        try{
            URL url=new URL(cmd.endPt);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            //send data
            if(cmd.data!=null){
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json; charset=utf-8");
                DataOutputStream outputStream=new DataOutputStream(conn.getOutputStream());
                //here write the data and flush
                System.out.println(cmd.data.toString());
                outputStream.writeBytes(cmd.data.toString());
                outputStream.flush();
                outputStream.close();
            }
            //receive data
            InputStream inputStream=new BufferedInputStream(conn.getInputStream());
            BufferedReader r =new BufferedReader(new InputStreamReader(inputStream));
            for(String line; (line=r.readLine())!=null;){
                response.append(line).append('\n');
                System.out.println(line);
            }
            try{
                //try to catch the return data from server
                jsonObject=new JSONObject(response.toString());
                jsonObject.put("context",cmd.context);
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
    protected void onPostExecute(JSONObject jsonObject){
        //when this task is executed, will put the the jsonObject response as output and so something aboutit.
        if(jsonObject!=null){
            this.callback.onServerResponse(jsonObject);
        }
    }
    public interface IServerResponse{
        void onServerResponse(JSONObject jsonObject);//do something about the jsonObject returned.
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
