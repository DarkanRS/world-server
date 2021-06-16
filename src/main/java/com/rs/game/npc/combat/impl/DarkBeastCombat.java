package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.utils.WorldUtil;

public class DarkBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2783 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(2731));
		if (WorldUtil.isInRange(target, npc, 3)) {
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, 170, def.getAttackStyle(), target)));
		} else {
			final int damage = getMaxHit(npc, 90, def.getAttackStyle(), target);
			World.sendProjectile(npc, target, 2181, 41, 16, 41, 35, 16, 0);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}
}
