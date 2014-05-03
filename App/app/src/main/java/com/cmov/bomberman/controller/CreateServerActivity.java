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

        EditText usernameBox = (EditText)findViewById(R.id.chose_username_editable_join);
        username = usernameBox.getText().toString();
    }

    public void accept(){
         /*final Intent intent = new Intent(this, LoadingGameActivity.class);
        intent.putExtra("isOwner", true);
        intent.putExtra("username", username);
        startActivity(intent);
        */
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
