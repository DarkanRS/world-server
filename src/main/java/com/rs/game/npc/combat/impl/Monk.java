package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class Monk extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Monk" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (Utils.random(100) < 70) {
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
			npc.setNextAnimation(new Animation(def.getAttackEmote()));
		} else {
			npc.heal(20);
			npc.setNextSpotAnim(new SpotAnim(84));
		}
		return npc.getAttackSpeed();
	}
}
