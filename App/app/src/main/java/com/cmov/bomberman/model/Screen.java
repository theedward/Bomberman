package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import com.cmov.bomberman.model.drawing.*;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is responsible to draw everything in the canvas.
 */
public class Screen {
	private final String TAG = this.getClass().getSimpleName();
	private final SparseArray<Drawing> drawings;
	private final List<WallDrawing> wallDrawings; // contains all the walls

	public Screen() {
		drawings = new SparseArray<Drawing>();
		wallDrawings = new LinkedList<WallDrawing>();
	}

	public boolean hasDrawing(final int id) {
		return drawings.get(id) != null;
	}

	// tests if the object with the given id exists
	// if yes, updates it
	// if not, creates it
	public void updateDrawing(String type, int id, Position pos, String currentAction, String lastAction, int step,
							  int lastStep, int rangeRight, int rangeLeft, int rangeUp, int rangeDown,
							  boolean isDestroyed) {
		final Drawing d = drawings.get(id);
		if (drawings.get(id) != null) {
			// update drawing
			d.setPosition(pos);
			d.setLastAction(lastAction);
			d.setCurrentAction(currentAction);
			d.setStep(step);
			d.setLastStep(lastStep);

			if (type.equals("Bomb")) {
				BombDrawing b = (BombDrawing) d;
				b.setRangeRight(rangeRight);
				b.setRangeLeft(rangeLeft);
				b.setRangeUp(rangeUp);
				b.setRangeDown(rangeDown);
			}
		}

		if (isDestroyed) {
			drawings.remove(id);
		}
	}

	public void createDrawing(boolean isMine, String type, int id, Position pos, int rangeRight, int rangeLeft,
							  int rangeUp, int rangeDown) {
		final int initialStep = 0;
		final String initialAction = "";

		// create object and insert
		if (type.equals("Bomberman")) {
			drawings.put(id, new BombermanDrawing(isMine, pos, initialStep, initialAction));
		} else if (type.equals("Robot")) {
			drawings.put(id, new RobotDrawing(pos, initialStep, initialAction));
		} else if (type.equals("Obstacle")) {
			drawings.put(id, new ObstacleDrawing(pos, initialStep));
		} else if (type.equals("Bomb")) {
			drawings.put(id, new BombDrawing(pos, rangeRight, rangeLeft, rangeUp, rangeDown));
		} else {
			Log.e(TAG, "Invalid object type: " + type);
		}
	}

	public void createWallDrawing(Position pos) {
		wallDrawings.add(new WallDrawing(pos));
	}

	public void removeDrawing(int id) {
		drawings.remove(id);
	}

	public List<Integer> currentDrawingIds() {
		LinkedList<Integer> drawingIds = new LinkedList<Integer>();
		for (int i = 0; i < drawings.size(); i++) {
			drawingIds.add(drawings.keyAt(i));
		}

		return drawingIds;
	}

	/**
	 * Draws the walls after the rest of the objects because when an explosion occurs,
	 * it doesn't know what is in the other position.
	 *
	 * @param canvas the canvas where all the drawings are drawn
	 */
	public void drawAll(Canvas canvas) {
		// set background color
		canvas.drawColor(Color.GREEN);

		for (WallDrawing wall : wallDrawings) {
			wall.draw(canvas);
		}

		final int size = drawings.size();
		for (int i = 0; i < size; i++) {
			drawings.valueAt(i).draw(canvas);
		}
	}
}
