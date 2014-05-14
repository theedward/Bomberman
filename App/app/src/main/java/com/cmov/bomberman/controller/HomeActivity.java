package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cmov.bomberman.R;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
		TextView bomberman = (TextView) findViewById(R.id.bombermantxt);
		Button singlePlayer = (Button) findViewById(R.id.singleplayerbtn);
		Button multiPlayer = (Button) findViewById(R.id.multiplayerbtn);
		Button about = (Button) findViewById(R.id.aboutbtn);
		singlePlayer.setTypeface(blockFonts);
		multiPlayer.setTypeface(blockFonts);
		bomberman.setTypeface(blockFonts);
		about.setTypeface(blockFonts);


	}

	public void newGame(View v) {
		Intent intent = new Intent(HomeActivity.this, CreateSinglePlayerGameActivity.class);
		startActivity(intent);
	}

	public void multiplayerGame(View v) {
		//        Intent intent = new Intent(HomeActivity.this, MultiplayerActivity.class);
		//        startActivity(intent);
		Intent intent = new Intent(HomeActivity.this, SimpleChatActivity.class);
		startActivity(intent);
	}

	public void about(View v) {
		Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
		startActivity(intent);
	}
}
