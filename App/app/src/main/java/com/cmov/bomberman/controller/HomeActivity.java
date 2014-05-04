package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.cmov.bomberman.R;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void newGame(View v) {
        Intent intent = new Intent(HomeActivity.this, CreateSinglePlayerGameActivity.class);
        startActivity(intent);
    }

    public void multiplayerGame(View v) {
        Intent intent = new Intent(HomeActivity.this, MultiplayerActivity.class);
        startActivity(intent);
    }

    public void about(View v) {
        Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
        startActivity(intent);
    }
}
