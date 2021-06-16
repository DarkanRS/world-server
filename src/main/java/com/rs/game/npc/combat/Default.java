package com.rs.game.npc.combat;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class Default extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Default" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		AttackStyle attackStyle = defs.getAttackStyle();
		if (attackStyle == AttackStyle.MELEE) {
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, npc.getMaxHit(), attackStyle, target)));
		} else {
			int damage = getMaxHit(npc, npc.getMaxHit(), attackStyle, target);
			WorldProjectile p = World.sendProjectile(npc, target, defs.getAttackProjectile(), 32, 32, 50, 2, 2, 0);
			delayHit(npc, p.getTaskDelay(), target, attackStyle == AttackStyle.RANGE ? getRangeHit(npc, damage) : getMagicHit(npc, damage));
		}
		if (defs.getAttackGfx() != -1)
			npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
