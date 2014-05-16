package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cmov.bomberman.R;

public class MultiplayerActivity extends Activity {
	private String username;
	private boolean isServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer);

		Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");

		TextView bombermanTxt = (TextView) findViewById(R.id.bombermantxt);
		TextView usernameTxt = (TextView) findViewById(R.id.txtUsername);
		TextView choseUsername = (TextView) findViewById(R.id.choose_username);
		Button createServer = (Button) findViewById(R.id.create_server_button);
		Button joinServer = (Button) findViewById(R.id.join_server_button);

		bombermanTxt.setTypeface(blockFonts);
		usernameTxt.setTypeface(blockFonts);
		choseUsername.setTypeface(blockFonts);
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
		final EditText usernameBox = (EditText) findViewById(R.id.txtUsername);
		if (usernameBox.getText() != null && usernameBox.getText().length() != 0) {
			username = usernameBox.getText().toString();
			return true;
		} else {
			Toast.makeText(getApplicationContext(), R.string.must_enter_username, Toast.LENGTH_LONG).show();
			return false;
		}
	}
}
