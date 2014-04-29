package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.cmov.bomberman.R;

public class MultiplayerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
    }

    public void joinServer(View v) {
        Intent intent = new Intent(MultiplayerActivity.this, JoinServerActivity.class);
        startActivity(intent);
    }

    public void createServer(View v) {
        Intent intent = new Intent(MultiplayerActivity.this, CreateServerActivity.class);
        startActivity(intent);
    }
}

