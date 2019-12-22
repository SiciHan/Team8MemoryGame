package com.example.team8memorygame;


import org.json.JSONObject;

public class CommandForMultiplayers {
    protected String context;
    protected String endPt;
    protected JSONObject data;
    protected AsyncToServerMultiplayer.IServerResponse callback;
    CommandForMultiplayers(AsyncToServerMultiplayer.IServerResponse callback, String context, String endPt, JSONObject data){
        this.callback=callback;
        this.context=context;
        this.data=data;
        this.endPt=endPt;
    }
}

