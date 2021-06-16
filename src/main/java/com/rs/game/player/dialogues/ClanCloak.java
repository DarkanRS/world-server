package com.rs.game.player.dialogues;

public class ClanCloak extends Dialogue {

	boolean rightClick;

	@Override
	public void start() {
		rightClick = (boolean) parameters[0];

		if (rightClick) {
			sendNPCDialogue(13633, HAPPY_TALKING, "Why of course you can have a clan cape.");
			stage = 100;
		} else {
			sendNPCDialogue(13633, DRUNK_HAPPY_TIRED, "Right click 'get cloak' on me if you would like a clan cape.");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 100) {
			player.getInventory().addItem(20708, 1);
			end();
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
