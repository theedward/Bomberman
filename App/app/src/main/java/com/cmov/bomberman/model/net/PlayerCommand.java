package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Commands that are sent to the Player.
 */
public interface PlayerCommand {
	void execute(final Player player, ObjectInputStream in, ObjectOutputStream out);
}
