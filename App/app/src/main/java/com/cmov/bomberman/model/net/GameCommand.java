package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Commands that are sent to the Game.
 */
public interface GameCommand {
	void execute(Game game, ObjectInputStream in, ObjectOutputStream out);
}
