package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class SuqahCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Suqah" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandomInclusive(3) == 0) {// barrage
			boolean hit = Utils.getRandomInclusive(1) == 0;
			delayHit(npc, 2, target, getMagicHit(npc, hit ? 100 : 0));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(369));
					target.freeze(Ticks.fromSeconds(5));
				}
			});
		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
		}
		return npc.getAttackSpeed();
	}
}
