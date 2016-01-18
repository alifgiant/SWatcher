package com.buahbatu.streetwatcher;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.buahbatu.streetwatcher.network.NetConfig;
import com.buahbatu.streetwatcher.services.CameraService;
import com.buahbatu.streetwatcher.services.SoundAlert;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    Map<Integer,Fragment> listFrag;
    final static String TAG = "Home";
    final static int mRequestStatus = 2;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.setup_button).setOnClickListener(moveToSetup);
        findViewById(R.id.status_button).setOnClickListener(moveToStatus);
        checkLoggedStatus();
    }

    void checkLoggedStatus(){
        if (!new NetConfig(getApplicationContext()).getLoggedInStatus()){
            Intent move = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(move);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    View.OnClickListener moveToSetup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent move = new Intent(getApplicationContext(), SetupActivity.class);
            startActivity(move);
        }
    };

    View.OnClickListener moveToStatus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent move = new Intent(getApplicationContext(), StatusActivity.class);
            startActivityForResult(move, mRequestStatus);
        }
    };

    SoundAlert soundAlert = null;
    void runServices(){
        running = true;
        // alert
        Log.i(TAG, "runServices");
        soundAlert = new SoundAlert(getApplicationContext(), ((TextureView)findViewById(R.id.camera_view)));
        soundAlert.start();
    }

    void stopServices(){
        running = false;
        if(soundAlert!=null){
            soundAlert.stop();
            soundAlert = null;
            Log.i(TAG, "stopServices");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==mRequestStatus && resultCode==RESULT_OK){
            if (data!=null){
                boolean isShouldStart = data.getBooleanExtra("status", false);
                Log.i(TAG, "onActivityResult "+Boolean.toString(isShouldStart));
                if (!running && isShouldStart)
                    runServices();
                else if (running && !isShouldStart)
                    stopServices();
            }
        }
    }
}
