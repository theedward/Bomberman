package com.cmov.bomberman.model;

import android.graphics.Canvas;

import java.util.LinkedList;
import java.util.List;

// This class is responsible to draw everything in the canvas.
public class Screen {
	List<Drawing> walls; // with wallDrawings
	List<Drawing> objects;

	public Screen() {
		walls = new LinkedList<Drawing>();
	}

	public void setWalls(final List<Drawing> walls) {
		this.walls = walls;
	}

	public void setObjects(final List<Drawing> objects) {
		this.objects = objects;
	}

	// Calls the method draw for all the objects
	public void drawAll(Canvas canvas) {
		// draw walls
		for (Drawing wall : walls) {
			wall.draw(canvas);
		}
		// draw objects
		for (Drawing obj : objects) {
			obj.draw(canvas);
		}
	}
}
