package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class WaterfiendCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 5361 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int spellType = Utils.getRandomInclusive(2);
		Hit hit;
		if (spellType > 1)
			hit = getMagicHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target));
		else
			hit = getRangeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, target));

		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		if (spellType == 0) {
			World.sendProjectile(npc, target, 11, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		} else if (spellType == 1) {
			World.sendProjectile(npc, target, 11, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		} else if (spellType == 2) {
			World.sendProjectile(npc, target, 2704, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		} else {
			World.sendProjectile(npc, target, 2704, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		}

		return npc.getAttackSpeed();
	}
}
