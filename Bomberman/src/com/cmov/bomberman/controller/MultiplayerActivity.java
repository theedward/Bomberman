package com.cmov.bomberman.controller;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.meic.cmov.bomberman.R;

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

}
