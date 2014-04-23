package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import com.cmov.bomberman.R;

public class NewGameActivity extends Activity {

	private static final int MAP_MIN_LEVEL = 1;
	private static final int MAP_MAX_LEVEL = 2;
	private static int[] mapPreviewId;

	private NumberPicker levelPicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);

		mapPreviewId = new int[] { R.drawable.first_level, R.drawable.second_level};

		levelPicker = (NumberPicker) findViewById(R.id.levelPicker);
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

	public void startGame(View v) {
		Intent intent = new Intent(NewGameActivity.this, GameActivity.class);
		intent.putExtra("level", levelPicker.getValue());
		startActivity(intent);
	}

}
