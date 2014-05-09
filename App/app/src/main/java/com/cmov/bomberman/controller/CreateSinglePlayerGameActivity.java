package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import com.cmov.bomberman.R;

public class CreateSinglePlayerGameActivity extends Activity {
	private static final String DEFAULT_USERNAME = "Bomberman";
	private static final int MIN_LEVEL = 1;
	private static final int MAX_LEVEL = 3;

	private NumberPicker levelPicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

		levelPicker = (NumberPicker) findViewById(R.id.levelPicker);
		levelPicker.setMinValue(MIN_LEVEL);
		levelPicker.setMaxValue(MAX_LEVEL);
		levelPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(final NumberPicker numberPicker, final int oldVal, final int newVal) {
				final ImageView mapPreview = (ImageView) findViewById(R.id.imageView1);
				mapPreview.setImageDrawable(getResources().getDrawable(R.drawable.first_level));
			}
		});
	}

	public void startGame(View v) {
		// Go to the GameActivity
		final Intent intent = new Intent(this, SinglePlayerGameActivity.class);
		intent.putExtra("level", levelPicker.getValue());
		intent.putExtra("username", DEFAULT_USERNAME);
		startActivity(intent);
	}
}
