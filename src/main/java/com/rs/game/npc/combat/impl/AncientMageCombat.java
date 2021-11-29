package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.content.Effect;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class AncientMageCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13459 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int spellType = Utils.getRandomInclusive(3);
		Hit hit = getMagicHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target));

		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		if (spellType == 0) {
			World.sendProjectile(npc, target, 386, 18, 18, 50, 50, 0, 0);
			delayHit(npc, 2, target, hit);
		} else if (spellType == 1) {
			World.sendProjectile(npc, target, 380, 18, 18, 50, 50, 0, 0);
			delayHit(npc, 2, target, hit);
		} else if (spellType == 2) {
			World.sendProjectile(npc, target, 374, 18, 18, 50, 50, 0, 0);// blood
			delayHit(npc, 2, target, hit);
			npc.heal(hit.getDamage() / 3);
		} else if (spellType == 3) {
			World.sendProjectile(npc, target, 362, 18, 18, 50, 50, 0, 0); // ice
			delayHit(npc, 2, target, hit);
			if (hit.getDamage() > 0 && !target.hasEffect(Effect.FREEZE)) {
				target.setNextSpotAnim(new SpotAnim(369));
				target.freeze(Ticks.fromSeconds(10));
			}
		} else {
			World.sendProjectile(npc, target, 386, 18, 18, 50, 50, 0, 0); // ice
			delayHit(npc, 2, target, hit);
		}

		return npc.getAttackSpeed();
	}
}
