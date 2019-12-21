package com.example.team8memorygame.Model;

import com.example.team8memorygame.AsyncToServer;

import org.json.JSONObject;

public class Command {
    private AsyncToServer.IServerResponse callBack;
    private String context;
    private String serverPt;
    private JSONObject data;

    public Command(AsyncToServer.IServerResponse callBack, String context, String serverPt, JSONObject data) {
        this.callBack = callBack;
        this.context = context;
        this.serverPt = serverPt;
        this.data = data;
    }

    public AsyncToServer.IServerResponse getCallBack() {
        return callBack;
    }

    public void setCallBack(AsyncToServer.IServerResponse callBack) {
        this.callBack = callBack;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getServerPt() {
        return serverPt;
    }

    public void setServerPt(String serverPt) {
        this.serverPt = serverPt;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
