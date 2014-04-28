package com.cmov.bomberman.model.drawing;

import android.graphics.Canvas;
import com.cmov.bomberman.model.Position;

// Every object that will be printed on the screen must implement this
// interface.
public abstract class Drawing {
	private Position position;
	private int id;
	private String lastAction;
	private String currentAction;
	private int step;
	private int lastStep;

	protected Drawing(final Position position) {
		this.position = position;
		this.lastAction = "";
		this.currentAction = "";
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) { this.position = position; }

	public int getId() { return id;}

	public void setId(int idDrawing) { this.id = idDrawing; }

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public String getCurrentAction() { return currentAction; }

	public void setCurrentAction(String currentAction) { this.currentAction = currentAction; }

	public int getStep() { return step; }

	public void setStep(int step) { this.step = step; }

	public int getLastStep() { return lastStep; }

	public void setLastStep(int lastStep) { this.lastStep = lastStep; }

	public abstract void draw(Canvas canvas);

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else {
			if (o instanceof Drawing) {
				Drawing drawing = (Drawing) o;
				return this.id == drawing.getId();
			}
			return false;
		}
	}
}
