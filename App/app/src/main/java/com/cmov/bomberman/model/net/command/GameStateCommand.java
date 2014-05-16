package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.net.GameClient;
import com.cmov.bomberman.model.net.dto.GameDto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameStateCommand implements PlayerCommand {
	private final GameClient gameClient;

	public GameStateCommand(final GameClient gameClient) {
		this.gameClient = gameClient;
	}

	@Override
	public void execute(final String username, final Player player, final ObjectInputStream in,
						final ObjectOutputStream out) {
		try {
			GameDto dto = (GameDto) in.readObject();
			gameClient.setGameDto(dto);
			gameClient.notify();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
