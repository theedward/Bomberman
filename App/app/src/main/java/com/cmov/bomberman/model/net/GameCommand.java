package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Commands that are sent to the Game.
 */
public interface GameCommand {
	void execute(Game game, InputStream in, OutputStream out);
}
