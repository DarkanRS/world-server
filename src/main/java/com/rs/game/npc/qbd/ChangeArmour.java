package com.rs.game.npc.qbd;

import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.util.Utils;

/**
 * Handles the Queen Black Dragon's change armour "attack".
 * 
 * @author Emperor
 * 
 */
public final class ChangeArmour implements QueenAttack {

	@Override
	public int attack(final QueenBlackDragon npc, Player victim) {
		npc.switchState(Utils.random(2) < 1 ? QueenState.CRYSTAL_ARMOUR : QueenState.HARDEN);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				npc.switchState(QueenState.DEFAULT);
			}
		}, 40);
		npc.getTempAttribs().put("_last_armour_change", npc.getTicks() + Utils.random(41, 100));
		return Utils.random(4, 10);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		Integer last = (Integer) npc.getTempAttribs().get("_last_armour_change");
		return last == null || last < npc.getTicks();
	}

}