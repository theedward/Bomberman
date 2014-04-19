package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import com.cmov.bomberman.R;

public class NewGameActivity extends Activity {

	private static final int MAP_MIN_LEVEL = 1;
	private static final int MAP_MAX_LEVEL = 5;
	private static final int NUM_LEVELS = 5;
	private static int[] mapPreviewId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);

		mapPreviewId = new int[] { R.drawable.level1, R.drawable.suicide_bomber, R.drawable.level1, R.drawable.level1,
								   R.drawable.level1, };

		final NumberPicker levelPicker = (NumberPicker) findViewById(R.id.levelPicker);
		levelPicker.setMinValue(MAP_MIN_LEVEL);
		levelPicker.setMaxValue(MAP_MAX_LEVEL);
		levelPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(final NumberPicker numberPicker, final int oldVal, final int newVal) {
				final ImageView mapPreview = (ImageView) findViewById(R.id.imageView1);
				mapPreview.setImageDrawable(getResources().getDrawable(mapPreviewId[newVal - 1]));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_game, menu);
		return true;
	}

	public void startGame(View v) {
		Intent intent = new Intent(NewGameActivity.this, GameActivity.class);
		startActivity(intent);
	}

}
