package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class DreadFowlCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6825, 6824 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7810));
			npc.setNextSpotAnim(new SpotAnim(1318));
			delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, 40, AttackStyle.MAGE, target)));
			World.sendProjectile(npc, target, 1376, 34, 16, 30, 35, 16, 0);
		} else {
			if (Utils.getRandomInclusive(10) == 0) {// 1/10 chance of random special
											// (weaker)
				npc.setNextAnimation(new Animation(7810));
				npc.setNextSpotAnim(new SpotAnim(1318));
				delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, 30, AttackStyle.MAGE, target)));
				World.sendProjectile(npc, target, 1376, 34, 16, 30, 35, 16, 0);
			} else {
				npc.setNextAnimation(new Animation(7810));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 30, AttackStyle.MELEE, target)));
			}
		}
		return npc.getAttackSpeed();
	}
}
