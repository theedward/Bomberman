package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.cmov.bomberman.R;

import org.w3c.dom.Text;

public class CreateSinglePlayerGameActivity extends Activity {
	private static final String DEFAULT_USERNAME = "Bomberman";
	private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 2;
    private static int[] mapPreviewId;

    private NumberPicker levelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
        TextView bomberman = (TextView) findViewById(R.id.bombermantxt);
        TextView level = (TextView) findViewById(R.id.level);
        level.setTypeface(blockFonts);
        Button startbtn = (Button) findViewById(R.id.startbtn);
        startbtn.setTypeface(blockFonts);
        bomberman.setTypeface(blockFonts);

        levelPicker = (NumberPicker) findViewById(R.id.levelPicker);
        levelPicker.setMinValue(MIN_LEVEL);
        levelPicker.setMaxValue(MAX_LEVEL);
    }

    public void startGame(View v) {
		// Go to the GameActivity
		final Intent intent = new Intent(this, SinglePlayerGameActivity.class);
		intent.putExtra("level", levelPicker.getValue());
		intent.putExtra("username", DEFAULT_USERNAME);
        startActivity(intent);
    }
}
