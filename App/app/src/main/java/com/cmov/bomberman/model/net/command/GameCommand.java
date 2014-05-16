package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.net.GameServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Commands that are sent to the Game.
 */
public interface GameCommand {
	void execute(GameServer game, ObjectInputStream in, ObjectOutputStream out);
}
