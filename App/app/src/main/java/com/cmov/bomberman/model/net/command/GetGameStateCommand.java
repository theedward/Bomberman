package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.GameImpl;
import com.cmov.bomberman.model.agent.Bomberman;
import com.cmov.bomberman.model.agent.NullAlgorithm;
import com.cmov.bomberman.model.net.GameServer;
import com.cmov.bomberman.model.net.dto.GameDto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

public class GetGameStateCommand implements GameCommand {
	@Override
	public void execute(final GameServer gameServer, final ObjectInputStream in, final ObjectOutputStream out) {
		try {
			GameImpl game = gameServer.getGame();
			GameDto dto = new GameDto();

			dto.setLevel(game.getLevel());
			// clean possible controllable proxy
			Map<String, Bomberman> playersAgent = game.getPlayersAgent();
			for (Bomberman b : playersAgent.values()) {
				b.setAlgorithm(new NullAlgorithm());
			}

			dto.setPlayersAgent(playersAgent);
			dto.setPlayerAgentIdx(game.getPlayerAgentIdx());
			dto.setGameState(game.getGameState());
			dto.setBombermanIds(game.getBombermanIds());
			dto.setBombermanPos(game.getBombermanPos());
			dto.setBombermanUsed(game.getBombermanUsed());
			dto.setGameConfiguration(game.getGameConfiguration());
			dto.setStarted(game.isStarted());
			dto.setPaused(game.isPaused());
			dto.setWallPositions(game.getWallPositions());
			dto.setNumRoundsLeft(game.getNumRoundsLeft());

			Map<String, Boolean> playersState = new TreeMap<String, Boolean>();
			for (String s : game.getPlayers().keySet()) {
				playersState.put(s, true);
			}
			for (String s : game.getPlayersOnPause().keySet()) {
				playersState.put(s, false);
			}

			dto.setPlayersState(playersState);

			out.writeUTF("gameState");
			out.writeObject(dto);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
