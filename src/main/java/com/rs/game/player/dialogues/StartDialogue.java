package com.rs.game.player.dialogues;

public class StartDialogue extends Dialogue {

	int accType = 0;
	boolean ironMan = false;

	@Override
	public void start() {
		sendOptionsDialogue("Would you like this account to be an ironman?", "Yes (No trading, picking up items that aren't yours etc.)", "No");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_2) {
				ironMan = false;
			} else {
				ironMan = true;
			}
			stage = 3;
			sendOptionsDialogue("Is a " + (ironMan ? "ironman" : "normal") + " account alright for you?", "Yes. Create my account.", "No. Let me choose again.");
		} else if (stage == 3) {
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Would you like this account to be an ironman?", "Yes (No trading, picking up items that aren't yours etc.)", "No");
				stage = 1;
			} else {
				player.setIronMan(ironMan);
				player.setChosenAccountType(true);
				player.getAppearance().generateAppearanceData();
				end();
			}
		}
	}

	@Override
	public void finish() {

	}

}
