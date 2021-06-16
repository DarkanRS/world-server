package com.rs.game.player.dialogues;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.lib.game.Animation;

public class ColouredRecessShelvesD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Blue vial.", "Green Vial.", "Yellow Vial.", "Violet Vial.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (Math.random() < 0.2) {
			player.sendMessage("The vial reacts explosively as you pick it up.");
			player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * 0.25D), HitLook.TRUE_DAMAGE));
			end();
			return;
		} else if (componentId == OPTION_1) {
			player.getInventory().addItem(19869, 1);
		} else if (componentId == OPTION_2) {
			player.getInventory().addItem(19871, 1);
		} else if (componentId == OPTION_3) {
			player.getInventory().addItem(19873, 1);
		} else if (componentId == OPTION_4) {
			player.getInventory().addItem(19875, 1);
		}
		player.setNextAnimation(new Animation(832));
		end();
	}

	@Override
	public void finish() {

	}

}
