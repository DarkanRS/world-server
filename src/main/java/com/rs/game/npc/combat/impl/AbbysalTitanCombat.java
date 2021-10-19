package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class AbbysalTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7350, 7349 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int damage = 0;
		damage = getMaxHit(npc, 140, AttackStyle.MELEE, target);
		npc.setNextAnimation(new Animation(7980));
		npc.setNextSpotAnim(new SpotAnim(1490));

		if (target instanceof Player player) {
			if (damage > 0 && player.getPrayer().getPoints() > 0)
				player.getPrayer().drainPrayer(damage / 2);
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
