package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class IronTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7376, 7375 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7954));
			npc.setNextSpotAnim(new SpotAnim(1450));
			if (!npc.inMeleeRange(target)) {// range hit
				delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, 220, AttackStyle.MAGE, target, 2.0)).setSource(familiar.getOwner()));
				delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, 220, AttackStyle.MAGE, target, 2.0)).setSource(familiar.getOwner()));
				delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, 220, AttackStyle.MAGE, target, 2.0)).setSource(familiar.getOwner()));
			} else {// melee hit
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 230, AttackStyle.MELEE, target, 2.0)).setSource(familiar.getOwner()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 230, AttackStyle.MELEE, target, 2.0)).setSource(familiar.getOwner()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 230, AttackStyle.MELEE, target, 2.0)).setSource(familiar.getOwner()));
			}
		} else {
			if (!npc.inMeleeRange(target)) {
				damage = getMaxHit(npc, 255, AttackStyle.MAGE, target);
				npc.setNextAnimation(new Animation(7694));
				delayHit(npc, World.sendProjectile(npc, target, 1452, 34, 16, 30, 35, 16, 0).getTaskDelay(), target, getMagicHit(npc, damage).setSource(familiar.getOwner()));
			} else {// melee
				damage = getMaxHit(npc, 244, AttackStyle.MELEE, target);
				npc.setNextAnimation(new Animation(7946));
				npc.setNextSpotAnim(new SpotAnim(1447));
				delayHit(npc, 1, target, getMeleeHit(npc, damage).setSource(familiar.getOwner()));
			}
		}
		return npc.getAttackSpeed();
	}

}
