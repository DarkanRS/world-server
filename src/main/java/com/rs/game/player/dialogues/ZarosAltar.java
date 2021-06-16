package com.rs.game.player.dialogues;

public class ZarosAltar extends Dialogue {

	@Override
	public void start() {
		if (!player.getPrayer().isCurses())
			sendOptionsDialogue("Change from prayers to curses?", "Yes, replace my prayers with curses.", "Never mind.");
		else
			sendOptionsDialogue("Change from curses to prayers?", "Yes, replace my curses with prayers.", "Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			if (!player.getPrayer().isCurses()) {
				sendDialogue("The altar fills your head with dark thoughts, purging the", "prayers from your memory and leaving only curses in", " their place.");
				player.getPrayer().setPrayerBook(true);
			} else {
				sendDialogue("The altar eases its grip on your mid. The curses slip from", "your memory and you recall the prayers you used to know.");
				player.getPrayer().setPrayerBook(false);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
