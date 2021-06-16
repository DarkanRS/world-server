package com.rs.game.player.content.skills.farming;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class RakeAction extends Action {
	
	private FarmPatch patch;
	
	public RakeAction(FarmPatch patch) {
		this.patch = patch;
	}

	@Override
	public boolean start(Player player) {
		if (patch.weeds <= 0)
			return false;
		player.setNextAnimation(FarmPatch.RAKING_ANIMATION);
		player.getActionManager().setActionDelay(4);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (patch.weeds <= 0)
			return false;
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(FarmPatch.RAKING_ANIMATION);
		if (patch.weeds > 0)
			patch.weeds--;
		patch.updateVars(player);
		player.putPatch(patch);
		player.incrementCount("Weeds raked");
		player.getInventory().addItemDrop(6055, 1);
		player.getSkills().addXp(Constants.FARMING, 4);
		return patch.weeds == 0 ? -1 : 3;
	}

	@Override
	public void stop(Player player) {
		player.setNextAnimation(new Animation(-1));
	}

}
