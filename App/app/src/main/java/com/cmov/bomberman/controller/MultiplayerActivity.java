package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.cmov.bomberman.R;

public class MultiplayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multiplayer, menu);
        return true;
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

