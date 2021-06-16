package com.rs.game.player.content.minigames.ectofuntus;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.game.Animation;

public class SlimeBucketFill extends Action {

	public SlimeBucketFill() {
	}

	@Override
	public boolean start(Player player) {
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getInventory().containsItem(Ectofuntus.EMPTY_BUCKET, 1)) {
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		if (fillBucket(player)) {
			return 1;
		}
		return 1;
	}

	@Override
	public void stop(Player player) {

	}

	public boolean fillBucket(Player player) {
		if (player.getInventory().containsItem(Ectofuntus.EMPTY_BUCKET, 1)) {
			player.setNextAnimation(new Animation(4471));
			player.getInventory().deleteItem(Ectofuntus.EMPTY_BUCKET, 1);
			player.getInventory().addItem(Ectofuntus.BUCKET_OF_SLIME, 1);
			return true;
		}
		return false;
	}
}
