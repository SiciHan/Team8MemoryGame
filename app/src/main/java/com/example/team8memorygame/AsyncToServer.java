package com.example.team8memorygame;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class AsyncToServer extends AsyncTask<Command, Boolean, JSONArray> {
    IServerResponse callback;

    @Override
    protected void onPreExecute(){}

    @Override
    protected JSONArray doInBackground(Command... cmds){
        System.out.println("background start");
        Command cmd = cmds[0];
        this.callback = cmd.getCallBack();

        JSONObject jsonObj = null;
        JSONArray jsonArr = null;
        StringBuilder response = new StringBuilder();
        boolean failToConnectToServer = false;

        try{
            URL url = new URL(cmd.getServerPt());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(5000);

            // send data
            if (cmd.getData() != null) {
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
            for (String line; (line = r.readLine()) != null; ) {
                response.append(line).append('\n');
            }

            try {
                // return jsonArr
                jsonArr = new JSONArray(response.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        catch(Exception e){
            System.out.println("Server not found");
            failToConnectToServer = true;
            publishProgress(failToConnectToServer);
            e.printStackTrace();
        }

        return jsonArr;


    }

    @Override
    protected void onProgressUpdate(Boolean... b){
        if (this.callback == null) {
            return;
        }
//        this.callback.serverNotFound();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArr){
        if (jsonArr != null){
            this.callback.onServerResponse(jsonArr);
        }
    }

    public interface IServerResponse{
        void onServerResponse(JSONArray jsonArr);
//        void serverNotFound();
    }
}
