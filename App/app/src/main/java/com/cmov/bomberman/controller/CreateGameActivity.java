package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.GameService;

public class CreateGameActivity extends Activity {
	private static final String DEFAULT_USERNAME = "Bomberman";
	private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 2;
    private static int[] mapPreviewId;

    private NumberPicker levelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        mapPreviewId = new int[]{R.drawable.first_level, R.drawable.second_level};

        levelPicker = (NumberPicker) findViewById(R.id.levelPicker);
        levelPicker.setMinValue(MIN_LEVEL);
        levelPicker.setMaxValue(MAX_LEVEL);
        levelPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(final NumberPicker numberPicker, final int oldVal, final int newVal) {
                final ImageView mapPreview = (ImageView) findViewById(R.id.imageView1);
                mapPreview.setImageDrawable(getResources().getDrawable(mapPreviewId[newVal - 1]));
            }
        });
    }

    public void startGame(View v) {
		// Create the game service
		final Intent serviceIntent = new Intent(CreateGameActivity.this, GameService.class);
		serviceIntent.putExtra("level", levelPicker.getValue());
		serviceIntent.putExtra("isMultiplayer", false);
		startService(serviceIntent);

		// Go to the GameActivity
		final Intent intent = new Intent(CreateGameActivity.this, GameActivity.class);
		intent.putExtra("username", DEFAULT_USERNAME);
		intent.putExtra("isMultiplayer", false);
        startActivity(intent);
    }
}
