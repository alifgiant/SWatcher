package com.buahbatu.streetwatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.buahbatu.streetwatcher.network.NetConfig;
import com.buahbatu.streetwatcher.network.ServerConnection;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_button).setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ServerConnection connection = new ServerConnection(LoginActivity.this, stateChangeListener);
            String domain = ((EditText)findViewById(R.id.server_name_text)).getText().toString();
            String auth = ((EditText)findViewById(R.id.auth_text)).getText().toString();
            connection.login(domain, auth);
        }
    };

    ServerConnection.OnServerConnectionStateChange stateChangeListener = new ServerConnection.OnServerConnectionStateChange() {
        @Override
        public void onLoginSuccess() {
//            String domain = ((EditText)findViewById(R.id.server_name_text)).getText().toString();
//            NetConfig config = new NetConfig(getApplicationContext());
//            config.storeDomain(domain);
//            Log.i(TAG, "onLoginSuccess "+domain);

            Intent move = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(move);
            finish();
        }

        @Override
        public void onLoginFailed() {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
