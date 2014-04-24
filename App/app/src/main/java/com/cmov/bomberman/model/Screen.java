package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.graphics.Color;
import com.cmov.bomberman.model.drawing.Drawing;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is responsible to draw everything in the canvas.
 */
public class Screen {
	List<Drawing> fixedDrawings; // all the walls
	List<Drawing> objects;

	public Screen() {
		fixedDrawings = new LinkedList<Drawing>();
		objects = new LinkedList<Drawing>();
	}

	public void setFixedDrawings(final List<Drawing> fixedDrawings) {
		this.fixedDrawings = fixedDrawings;
	}

	public void setObjects(final List<Drawing> objects) {
		this.objects = objects;
	}

	public void drawAll(Canvas canvas) {
		// set background color
		canvas.drawColor(Color.GREEN);

		// draw fixedDrawings
		for (Drawing wall : fixedDrawings) {
			wall.draw(canvas);
		}

		// draw objects
		for (Drawing obj : objects) {
			obj.draw(canvas);
		}
	}
}
