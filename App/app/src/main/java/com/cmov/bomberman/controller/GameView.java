package com.cmov.bomberman.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.cmov.bomberman.model.Screen;

public class GameView extends SurfaceView {
    Screen screen;

    public GameView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreen(final Screen screen) {
        this.screen = screen;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (screen != null) {
            screen.drawAll(canvas);
        }
    }
}
