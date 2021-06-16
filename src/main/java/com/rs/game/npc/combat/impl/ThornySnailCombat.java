package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class ThornySnailCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6807, 6806 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(8148));
			npc.setNextSpotAnim(new SpotAnim(1385));
			World.sendProjectile(npc, target, 1386, 34, 16, 30, 35, 16, 0);
			delayHit(npc, 1, target, getRangeHit(npc, getMaxHit(npc, 80, AttackStyle.RANGE, target)));
			npc.setNextSpotAnim(new SpotAnim(1387));
		} else {
			npc.setNextAnimation(new Animation(8143));
			delayHit(npc, 1, target, getRangeHit(npc, getMaxHit(npc, 40, AttackStyle.RANGE, target)));
		}
		return npc.getAttackSpeed();
	}

}
