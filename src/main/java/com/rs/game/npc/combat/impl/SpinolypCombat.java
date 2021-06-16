package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class SpinolypCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Spinolyp" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		switch (Utils.random(2)) {
		case 0:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			WorldProjectile projectile = World.sendProjectile(npc, target, 2705, 34, 16, 35, 2, 10, 0);
			delayHit(npc, projectile.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			projectile = World.sendProjectile(npc, target, 473, 34, 16, 35, 2, 10, 0);
			delayHit(npc, projectile.getTaskDelay(), target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			break;
		}
		if (Utils.random(10) == 0)
			target.getPoison().makePoisoned(68);
		return npc.getAttackSpeed();
	}
}
