package com.cmov.bomberman.controller;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cmov.bomberman.R;
import com.cmov.bomberman.model.GameProxy;

public class CreateServerActivity extends WifiActivity {

    private String username;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_server);

        // must create a group
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "created p2p group");
            }

            @Override
            public void onFailure(int i) {
                Log.i(TAG, "could not create p2p group");
            }
        });
    }

    public void accept(View view){
        final EditText usernameBox = (EditText)findViewById(R.id.txtUsername);
        if (usernameBox.getText() != null) {
            username = usernameBox.getText().toString();
        }

        // create the game proxy
        // Create the game service
        final Intent serviceIntent = new Intent(this, GameProxy.class);
        // TODO create level
        serviceIntent.putExtra("level", 1);
        serviceIntent.putExtra("isMultiplayer", true);
        serviceIntent.putExtra("isServer", true);
        startService(serviceIntent);

        // go to the loading activity
        final Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("isOwner", true);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
