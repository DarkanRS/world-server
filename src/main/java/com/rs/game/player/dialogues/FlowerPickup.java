package com.rs.game.player.dialogues;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.lib.game.Animation;

public class FlowerPickup extends Dialogue {

	GameObject flowerObject;
	int flowerId;

	public int getFlowerId(int objectId) {
		return 2460 + ((objectId - 2980) * 2);
	}

	@Override
	public void start() {
		flowerObject = (GameObject) parameters[0];
		flowerId = (int) parameters[1];
		sendOptionsDialogue("What do you want to do with the flowers?", "Pick", "Leave them");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == 11) {
				player.setNextAnimation(new Animation(827));
				player.getInventory().addItem(getFlowerId(flowerId), 1);
				player.getInventory().refresh();
				World.removeObject(flowerObject);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}
}