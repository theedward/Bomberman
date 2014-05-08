package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Commands that are sent to the Player.
 */
public interface PlayerCommand {
	void execute(final Player player, InputStream in, OutputStream out);
}
