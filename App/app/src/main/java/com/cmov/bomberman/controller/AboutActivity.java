package com.cmov.bomberman.controller;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import com.cmov.bomberman.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
		TextView bomberman = (TextView) findViewById(R.id.bombermantxt);
		TextView aboutGame = (TextView) findViewById(R.id.about_the_game);
		TextView aboutGameStory = (TextView) findViewById(R.id.about_the_game_story);
		TextView aboutUs = (TextView) findViewById(R.id.about_us);
		TextView aboutUsStory = (TextView) findViewById(R.id.about_us_story);
		aboutGame.setTypeface(blockFonts);
		aboutGameStory.setTypeface(blockFonts);
		aboutUs.setTypeface(blockFonts);
		aboutUsStory.setTypeface(blockFonts);
		bomberman.setTypeface(blockFonts);
	}
}
