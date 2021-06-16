package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class InfernalMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Infernal Mage" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));

		World.sendProjectile(npc, target, defs.getAttackProjectile(), 30, 30, 50, 2, Utils.random(5), 5);
		delayHit(npc, 3, target, getMagicHit(npc, getMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
		target.setNextSpotAnim(new SpotAnim(2739, 3, 100));
		return npc.getAttackSpeed();
	}
}
