package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cmov.bomberman.R;

public class MultiplayerActivity extends Activity {

    private String username;
    private boolean isServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
    }

    public void joinServer(View v) {
        boolean canProceed = obtainUsername();
        isServer = false;

        if (canProceed) {
            goToActivity();
        }
    }

    public void createServer(View v) {
        boolean canProceed = obtainUsername();
        isServer = true;

        if (canProceed) {
            goToActivity();
        }
    }

    private void goToActivity() {
        Intent intent;

        if (isServer) {
            intent = new Intent(MultiplayerActivity.this, CreateServerActivity.class);
        } else {
            intent = new Intent(MultiplayerActivity.this, JoinServerActivity.class);
        }

        intent.putExtra("username", username);
        startActivity(intent);
    }

    private boolean obtainUsername() {
        final EditText usernameBox = (EditText)findViewById(R.id.txtUsername);
        if (usernameBox.getText() != null) {
            username = usernameBox.getText().toString();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), R.string.must_enter_username, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}