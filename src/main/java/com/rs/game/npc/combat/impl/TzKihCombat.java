package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;

public class TzKihCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tz-Kih", 7361, 7362 };
	}

	@Override
	public int attack(NPC npc, Entity target) {// yoa
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = 0;
		if (npc instanceof Familiar familiar) {// TODO get anim and gfx
			boolean usingSpecial = familiar.hasSpecialOn();
			if (usingSpecial) {
				for (Entity entity : npc.getPossibleTargets()) {
					damage = getMaxHit(npc, 70, AttackStyle.MELEE, target);
					if (target instanceof Player player)
						player.getPrayer().drainPrayer(damage);
					delayHit(npc, 0, entity, getMeleeHit(npc, damage));
				}
			}
			return npc.getAttackSpeed();
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
		if (target instanceof Player player)
			player.getPrayer().drainPrayer(damage + 10);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
