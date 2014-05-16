package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HandleEventCommand implements PlayerCommand {
	@Override
	public void execute(final String username, final Player player, final ObjectInputStream in,
						final ObjectOutputStream out) {
		try {
			Event e = Event.valueOf(in.readUTF());
			player.getController().handleEvent(e);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
