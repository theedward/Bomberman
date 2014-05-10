package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmov.bomberman.R;
import com.cmov.bomberman.controller.net.MultiplayerGameActivity;

import org.w3c.dom.Text;

public class MultiplayerActivity extends Activity {

    private String username;
    private boolean isServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
        TextView bomberman = (TextView) findViewById(R.id.bombermantxt);
        TextView chooseUsername = (TextView) findViewById(R.id.choose_username);
        TextView editUsername = (TextView) findViewById(R.id.txtUsername);
        TextView createServer = (TextView) findViewById(R.id.create_server_button);
        TextView joinServer = (TextView) findViewById(R.id.join_server_button);

        bomberman.setTypeface(blockFonts);
        chooseUsername.setTypeface(blockFonts);
        editUsername.setTypeface(blockFonts);
        createServer.setTypeface(blockFonts);
        joinServer.setTypeface(blockFonts);
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
        Intent intent = new Intent(MultiplayerActivity.this, MultiplayerGameActivity.class);
        intent.putExtra("isServer", isServer);
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

