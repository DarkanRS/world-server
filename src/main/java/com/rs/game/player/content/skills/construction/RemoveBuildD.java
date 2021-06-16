package com.rs.game.player.content.skills.construction;

import com.rs.game.object.GameObject;
import com.rs.game.player.dialogues.Dialogue;

public class RemoveBuildD extends Dialogue {

	GameObject object;

	@Override
	public void start() {
		this.object = (GameObject) parameters[0];
		sendOptionsDialogue("Really remove it?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			player.getHouse().removeBuild(object);
		}
		end();
	}

	@Override
	public void finish() {

	}

}
