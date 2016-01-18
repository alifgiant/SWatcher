package com.buahbatu.streetwatcher.network;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.buahbatu.streetwatcher.R;

import org.json.JSONObject;

import java.net.MalformedURLException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.EntityBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by maaakbar on 10/31/15.
 */
public class ServerConnection {
    private Context context;
    private NetConfig config;
    private final String TAG = "Connection";
    OnServerConnectionStateChange stateChange;

    public ServerConnection(Context context, OnServerConnectionStateChange stateChange) {
        this.context = context;
        this.stateChange = stateChange;
    }

    public void login(String username, String password){
        Log.d(TAG, "login clickked");
        config = new NetConfig(context);
        try{
            PostWebTask task = new PostWebTask(context, config.getLoginURL(), httpPostEvent);
            EntityBuilder builder = EntityBuilder.create();

            NameValuePair user = new BasicNameValuePair("user", username);
            NameValuePair pass = new BasicNameValuePair("pass", password);

            builder.setParameters(user, pass);
            task.execute(builder.build());

        }catch (MalformedURLException e){
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Wrong URL format, retype domain");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private boolean responseParser(String response){
        return true;
    }

    ProgressDialog dialog;
    PostWebTask.HttpPostEvent httpPostEvent = new PostWebTask.HttpPostEvent() {
        @Override
        public void preEvent() {
            dialog = ProgressDialog.show(context,"","Logging in", true);
        }

        @Override
        public void postEvent(String... result) {
            dialog.dismiss();
            if (result.length > 0){
                boolean isSuccess = config.setLoggedInStatus(responseParser(result[0]));
                Log.i(TAG, "postEvent "+Boolean.toString(isSuccess));
                if (isSuccess) stateChange.onLoginSuccess();
            }else{
                Log.i(TAG, "postEvent no data");
            }
        }
    };

    public interface OnServerConnectionStateChange{
        void onLoginSuccess();
        void onLoginFailed();
    }
}
