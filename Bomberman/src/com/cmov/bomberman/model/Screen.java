package com.cmov.bomberman.model;

import android.graphics.Canvas;

import java.util.LinkedList;
import java.util.List;

// This class is responsible to draw everything in the canvas.
public class Screen {
	Canvas canvas;
	List<Drawable> objects;

	public Screen(Canvas canvas) {
		this.canvas = canvas;
		objects = new LinkedList<>();
	}

	public void addObject(Drawable object) {
		objects.add(object);
	}

	// Calls the method draw for all the objects
	public void drawAll() {
		for (Drawable obj : objects) {
			obj.draw(canvas);
		}
	}
}
