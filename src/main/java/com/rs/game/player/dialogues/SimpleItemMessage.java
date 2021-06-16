package com.rs.game.player.dialogues;

public class SimpleItemMessage extends Dialogue {

	@Override
	public void start() {
		String[] messages = new String[parameters.length - 1];
		for (int i = 0; i < messages.length; i++)
			messages[i] = (String) parameters[i + 1];
		sendEntityDialogue(Dialogue.IS_ITEM, "", (Integer) parameters[0], -1, messages);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}

