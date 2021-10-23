package com.rs.game.player.dialogues;

public class StealingCreationMagic extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select a magical weapon.", "Magic Staff", "Elemental Rune", "Catalyc Rune");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Select a class", "Class one", "Class two", "Class three", "Class four", "Class five");
				stage = 1;
			} else if (componentId == OPTION_2) {
				player.getTempAttribs().setI("sc_request", 12850);
				end();
				player.getPackets().sendInputIntegerScript("Enter Amount:");
			} else {
				player.getTempAttribs().setI("sc_request", 12851);
				end();
				player.getPackets().sendInputIntegerScript("Enter Amount:");
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				player.getTempAttribs().setI("sc_request", 14377);
			} else if (componentId == OPTION_2) {
				player.getTempAttribs().setI("sc_request", 14379);
			} else if (componentId == OPTION_3) {
				player.getTempAttribs().setI("sc_request", 14381);
			} else if (componentId == OPTION_4) {
				player.getTempAttribs().setI("sc_request", 14383);
			} else if (componentId == OPTION_5) {
				player.getTempAttribs().setI("sc_request", 14385);
			}
			end();
			player.getPackets().sendInputIntegerScript("Enter Amount:");
		}
	}

	@Override
	public void finish() {
	}
}
