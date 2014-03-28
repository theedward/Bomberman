package com.meic.cmov.bomberman;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.NumberPicker;

public class NewGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		
		final NumberPicker levelPicker = (NumberPicker) findViewById(R.id.levelPicker);
		levelPicker.setMinValue(1);
		levelPicker.setMaxValue(5);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_game, menu);
		return true;
	}
	
	public void startGame(View v){
		Intent intent = new Intent (NewGameActivity.this, GameActivity.class);
		startActivity(intent);
	}

}
