package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.graphics.Color;

import com.cmov.bomberman.model.drawing.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible to draw everything in the canvas.
 */
public class Screen {
	List<WallDrawing> wallDrawings; // all the walls
    Map<Integer, Drawing> drawings;

	public Screen() {
        drawings = new HashMap<Integer, Drawing>();
	}

    public void setWallDrawings(List<WallDrawing> wallDrawings) {
        this.wallDrawings = wallDrawings;
    }

    public void setDrawings(Map<Integer, Drawing> drawings) {
        this.drawings = drawings;
    }

    // tests if the object with the given id exists
    // if yes updates it
    // if not , instantiates a new one it is a bomb
    public void updateDrawing(String type, int id, Position pos, String currentAction, String lastAction,
                                 int step, int lastStep, int range, boolean isDestroyed) {
        if (isDestroyed) {
            drawings.remove(id);
        } else if (!drawings.containsKey(id)) {
            if (type.equals("Bomb")) {
                drawings.put(id, new BombDrawing(pos, step, range, currentAction));
            }
        } else {
            updateObject(id, pos, currentAction, lastAction, step, lastStep);
        }
    }

    // updates the object with the given id
    private void updateObject(int id, Position pos, String currentAction, String lastAction,
                              int step, int lastStep) {
        drawings.get(id).setPosition(pos);
        drawings.get(id).setLastAction(lastAction);
        drawings.get(id).setCurrentAction(currentAction);
        drawings.get(id).setStep(step);
        drawings.get(id).setLastStep(lastStep);
    }

    /**
	 * Draws the walls after the rest of the objects because when an explosion occurs,
	 * it doesn't know what is in the other position.
	 * @param canvas
	 */
	public void drawAll(Canvas canvas) {
		// set background color
		canvas.drawColor(Color.GREEN);

        for (WallDrawing wall : wallDrawings) {
            wall.draw(canvas);
        }

        for (Drawing drawing : drawings.values()) {
            drawing.draw(canvas);
        }
	}
}
