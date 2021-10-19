package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.others.Nechryael;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class NechryaelCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Nechryael" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (npc instanceof Nechryael n) {
			if (Utils.random(10) == 0 && !n.hasActiveSpawns())
				n.summonDeathSpawns();
		}
		npc.setNextAnimation(new Animation(npc.getCombatDefinitions().getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, def.getMaxHit(), def.getAttackStyle(), target)));
		return npc.getAttackSpeed();
	}
}
