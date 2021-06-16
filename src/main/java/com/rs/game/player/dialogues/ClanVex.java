package com.rs.game.player.dialogues;

public class ClanVex extends Dialogue {

	boolean rightClick;

	@Override
	public void start() {
		rightClick = (boolean) parameters[0];

		if (rightClick) {
			sendNPCDialogue(5915, HAPPY_TALKING, "Why of course you can have a vexillum.");
			stage = 100;
		} else {
			sendNPCDialogue(5915, DRUNK_HAPPY_TIRED, "Right click 'get vexillum' on me for a clan vexillum.");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 100) {
			player.getInventory().addItem(20709, 1);
			end();
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
