package com.rs.game.player.dialogues;

public class StealingCreationClay extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select a class", "Class one", "Class two", "Class three", "Class four", "Class five");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				player.getTemporaryAttributes().put("sc_request", 14182);
			} else if (componentId == OPTION_2) {
				player.getTemporaryAttributes().put("sc_request", 14184);
			} else if (componentId == OPTION_3) {
				player.getTemporaryAttributes().put("sc_request", 14186);
			} else if (componentId == OPTION_4) {
				player.getTemporaryAttributes().put("sc_request", 14188);
			} else if (componentId == OPTION_5) {
				player.getTemporaryAttributes().put("sc_request", 14190);
			}
			end();
			player.getPackets().sendInputIntegerScript("Enter Amount:");
		}
	}

	@Override
	public void finish() {
	}
}
