package com.rs.game.npc.combat.impl;

import com.rs.cache.loaders.Bonus;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class SteelTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7344, 7343 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(8190));
			target.setNextSpotAnim(new SpotAnim(1449));
			if (!npc.inMeleeRange(target)) {// range hit
				delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, 244, Bonus.RANGE_ATT, AttackStyle.RANGE, target, 3.0)).setSource(familiar.getOwner()));
				delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, 244, Bonus.RANGE_ATT, AttackStyle.RANGE, target, 3.0)).setSource(familiar.getOwner()));
				delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, 244, Bonus.RANGE_ATT, AttackStyle.RANGE, target, 3.0)).setSource(familiar.getOwner()));
				delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, 244, Bonus.RANGE_ATT, AttackStyle.RANGE, target, 3.0)).setSource(familiar.getOwner()));
			} else {// melee hit
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 244, Bonus.CRUSH_ATT, AttackStyle.MELEE, target, 3.0)).setSource(familiar.getOwner()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 244, Bonus.CRUSH_ATT, AttackStyle.MELEE, target, 3.0)).setSource(familiar.getOwner()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 244, Bonus.CRUSH_ATT, AttackStyle.MELEE, target, 3.0)).setSource(familiar.getOwner()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 244, Bonus.CRUSH_ATT, AttackStyle.MELEE, target, 3.0)).setSource(familiar.getOwner()));
			}
		} else {
			if (!npc.inMeleeRange(target)) {
				int attackStage = Utils.getRandomInclusive(1);// 2
				switch (attackStage) {
				case 0:// magic
					damage = getMaxHit(npc, 255, AttackStyle.MAGE, target);
					npc.setNextAnimation(new Animation(7694));
					delayHit(npc, World.sendProjectile(npc, target, 1451, 34, 16, 30, 35, 16, 0).getTaskDelay(), target, getMagicHit(npc, damage).setSource(familiar.getOwner()));
					break;
				case 1:// range
					damage = getMaxHit(npc, 244, AttackStyle.RANGE, target);
					npc.setNextAnimation(new Animation(8190));
					delayHit(npc, World.sendProjectile(npc, target, 1445, 34, 16, 30, 35, 16, 0).getTaskDelay(), target, getRangeHit(npc, damage).setSource(familiar.getOwner()));
					break;
				}
			} else {// melee
				damage = getMaxHit(npc, 244, AttackStyle.MELEE, target);
				npc.setNextAnimation(new Animation(8183));
				delayHit(npc, 1, target, getMeleeHit(npc, damage).setSource(familiar.getOwner()));
			}
		}
		return npc.getAttackSpeed();
	}
}
