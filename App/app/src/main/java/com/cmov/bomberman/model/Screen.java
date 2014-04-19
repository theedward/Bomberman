package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.util.LinkedList;
import java.util.List;

// This class is responsible to draw everything in the canvas.
public class Screen {
	Canvas canvas;
	//List<Drawing> objects;

	public Screen(Canvas canvas) {
		this.canvas = canvas;
		//objects = new LinkedList<Drawing>();
	}

	public void addObject(Drawing object) {
		//objects.add(object);
	}

	// Calls the method draw for all the objects
	public void drawAll(List<Drawing> objects) {
		for (Drawing obj : objects) {
			obj.draw(canvas);
		}
	}
}
