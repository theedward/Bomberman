package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class GetNextActionNameCommand implements PlayerCommand {
	@Override
	public void execute(final Player player, final InputStream in, final OutputStream out) {
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(out);
			objectOut.writeUTF(player.getController().getNextActionName());
			objectOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
