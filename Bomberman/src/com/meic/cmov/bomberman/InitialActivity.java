package com.meic.cmov.bomberman;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class InitialActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.initial, menu);
		return true;
	}
	
	public void newGame(View v){
		Intent intent = new Intent (InitialActivity.this, NewGameActivity.class);
		startActivity(intent);
	}
	
	public void multiplayerGame(View v){
		Intent intent = new Intent (InitialActivity.this, MultiplayerActivity.class);
		startActivity(intent);
	}

	public void about(View v){
		Intent intent = new Intent (InitialActivity.this, AboutActivity.class);
		startActivity(intent);
	}
}
