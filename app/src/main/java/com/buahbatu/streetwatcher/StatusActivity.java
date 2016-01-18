package com.buahbatu.streetwatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.buahbatu.streetwatcher.network.NetConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StatusActivity extends AppCompatActivity {
    int statOff = R.drawable.power_buttons_off;
    int statOn = R.drawable.power_buttons_on;

    private final String TAG = "status act";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        findViewById(R.id.back_button).setOnClickListener(backListener);
        findViewById(R.id.status_button).setOnClickListener(buttonStatus);
        changeImage();
    }

    void changeImage(){
        SharedPreferences sp = getSharedPreferences(NetConfig.preferenceName, MODE_PRIVATE);
        TextView statusText = (TextView)findViewById(R.id.status_text);
        boolean isActive = sp.getBoolean("status", false);

        if (isActive) {
            ((ImageButton) findViewById(R.id.status_button)).setImageResource(statOn);
            statusText.setText(getResources().getText(R.string.status_active));
        } else {

            ((ImageButton) findViewById(R.id.status_button)).setImageResource(statOff);
            statusText.setText(getResources().getText(R.string.status_NONACTIVE));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_button:
                    finishActivity();
                    break;
            }
        }
    };

    View.OnClickListener buttonStatus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isNetworkAvailable()) {
                SharedPreferences sp = getSharedPreferences(NetConfig.preferenceName, MODE_PRIVATE);
                TextView statusText = (TextView) findViewById(R.id.status_text);
                boolean isActive = sp.getBoolean("status", false);
                if (isActive) {
                    ((ImageButton) v).setImageResource(statOff);
                    statusText.setText(getResources().getText(R.string.status_NONACTIVE));
                    enableServices(false);
                    Toast.makeText(StatusActivity.this, "Service Disabled", Toast.LENGTH_SHORT).show();
                } else {
                    ((ImageButton) v).setImageResource(statOn);
                    statusText.setText(getResources().getText(R.string.status_active));
                    enableServices(true);
                    Toast.makeText(StatusActivity.this, "Service Enabled", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(StatusActivity.this, "Please Connect To Network", Toast.LENGTH_SHORT).show();
            }
        }
    };

    void enableServices(boolean isEnable){
        SharedPreferences.Editor editor = getSharedPreferences(NetConfig.preferenceName, MODE_PRIVATE).edit();
        editor.putBoolean("status", isEnable);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void finishActivity(){
        SharedPreferences sp = getSharedPreferences(NetConfig.preferenceName, MODE_PRIVATE);
        Intent result= new Intent();
        result.putExtra("status", sp.getBoolean("status", false));
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }
}
