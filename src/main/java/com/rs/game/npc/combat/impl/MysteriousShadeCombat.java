package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class MysteriousShadeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Mysterious shade" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final boolean rangeAttack = Utils.random(1) == 0;

		npc.setNextAnimation(new Animation(rangeAttack ? 13396 : 13398));
		npc.setNextSpotAnim(new SpotAnim(rangeAttack ? 2514 : 2515));
		WorldProjectile projectile = World.sendProjectile(npc, target, rangeAttack ? 2510 : 2511, 18, 18, 25, 3, 0, 0);
		if (rangeAttack)
			delayHit(npc, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)), () -> {
				target.setNextSpotAnim(new SpotAnim(2512));
			});
		else
			delayHit(npc, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)), () -> {
				target.setNextSpotAnim(new SpotAnim(2513));
			});
		target.setNextSpotAnim(new SpotAnim(rangeAttack ? 2512 : 2513, projectile.getEndTime(), 0));
		return npc.getAttackSpeed();
	}
}
