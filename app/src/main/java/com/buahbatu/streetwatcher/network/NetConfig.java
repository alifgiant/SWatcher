package com.buahbatu.streetwatcher.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.buahbatu.streetwatcher.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by maaakbar on 11/8/15.
 */
public class NetConfig {
    public final static String preferenceName = "streetWatcher";
    public final static String preference_domain = "domain";
    public final static String preference_logged = "loggedIn";

    String domain;
    Context context;
    private SharedPreferences local_setting;

    public NetConfig(Context context) {
        this.context = context;
        local_setting = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        this.domain = local_setting.getString(preference_domain,
                context.getResources().getString(R.string.default_domain));
    }

    public void storeDomain(String domain){
        SharedPreferences.Editor editor = local_setting.edit();
        editor.putString(preference_domain, domain);
        editor.apply();
    }

    public URL getLoginURL() throws MalformedURLException{
        try {
            return new URL("http://"+domain+context.getResources().getString(R.string.login_url));
        }catch (MalformedURLException e){
            throw e;
        }
    }

    public String getAlertURL(){
        return "http://"+domain+context.getResources().getString(R.string.alert_url);
    }

    public boolean setLoggedInStatus(boolean status){
        SharedPreferences.Editor editor = local_setting.edit();
        editor.putBoolean(preference_logged, status);
        editor.apply();
        return status;
    }

    public boolean getLoggedInStatus(){
        return local_setting.getBoolean(preference_logged, false);
    }
}
