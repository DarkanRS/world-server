package com.rs.game.player.dialogues;

public class ItemMessage extends Dialogue {

	@Override
	public void start() {
		send1ItemDialogue((Integer) parameters[1], "", (String) parameters[0]);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
