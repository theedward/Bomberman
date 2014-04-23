package com.cmov.bomberman.model;

import android.graphics.Canvas;

import java.util.LinkedList;
import java.util.List;

// This class is responsible to draw everything in the canvas.
public class Screen {
	Canvas canvas;
	List<Drawing> walls; // with wallDrawings

	public Screen(Canvas canvas) {
		this.canvas = canvas;
		walls = new LinkedList<Drawing>();
	}

	public void setWalls(List<Drawing> wallDrawings) {
		this.walls = wallDrawings;
	}

	// Calls the method draw for all the objects
	public void drawAll(List<Drawing> objects) {
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
