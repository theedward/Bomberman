package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.net.GameClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GroupOwnerChangedCommand implements PlayerCommand {
	private GameClient game;

	public GroupOwnerChangedCommand(final GameClient game) {
		this.game = game;
	}

	@Override
	public void execute(final String username, final Player player, final ObjectInputStream in,
						final ObjectOutputStream out) {
		game.serverOwnerChanged();
	}
}
