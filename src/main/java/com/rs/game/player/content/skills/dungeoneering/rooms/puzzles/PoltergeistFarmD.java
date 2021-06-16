package com.rs.game.player.content.skills.dungeoneering.rooms.puzzles;

import com.rs.game.object.GameObject;
import com.rs.game.player.dialogues.Dialogue;

public class PoltergeistFarmD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Pick corianger.", "Pick explosemary.", "Pick parslay.", "More herbs.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		PoltergeistRoom room = (PoltergeistRoom) parameters[0];
		GameObject object = (GameObject) parameters[1];
		if (!room.canTakeHerb()) {
			// another player took all the herbs while dialogue was open
			end();
		} else if (stage == -1) {
			if (componentId == OPTION_1) {
				room.takeHerb(player, object, 0);
				end();
			} else if (componentId == OPTION_2) {
				room.takeHerb(player, object, 1);
				end();
			} else if (componentId == OPTION_3) {
				room.takeHerb(player, object, 2);
				end();
			} else if (componentId == OPTION_4) {
				stage = 0;
				sendOptionsDialogue("Select an Option", "Pick cardamaim.", "Pick papreaper.", "Pick slaughtercress.", "More herbs.");

			}
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				room.takeHerb(player, object, 3);
				end();
			} else if (componentId == OPTION_2) {
				room.takeHerb(player, object, 4);
				end();
			} else if (componentId == OPTION_3) {
				room.takeHerb(player, object, 5);
				end();
			} else if (componentId == OPTION_4) {
				stage = -1;
				sendOptionsDialogue("Select an Option", "Pick corianger.", "Pick explosemary.", "Pick parslay.", "More herbs.");
			}
		}
	}

	@Override
	public void finish() {

	}

}
