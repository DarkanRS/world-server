package com.rs.game.player.content.skills.construction;

import com.rs.game.player.dialogues.Dialogue;

public class BuildD extends Dialogue {

	@Override
	public void start() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 55) {
			player.closeInterfaces();
			return;
		}
		player.getHouse().build((componentId - 8) / 7);
	}

	@Override
	public void finish() {
	}

}
