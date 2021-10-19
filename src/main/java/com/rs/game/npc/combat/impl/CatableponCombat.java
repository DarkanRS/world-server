package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class CatableponCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 4397, 4398, 4399 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (Utils.random(10) == 0 && target instanceof Player player) {
			int strLvl = player.getSkills().getLevelForXp(Constants.STRENGTH);
			if (strLvl - player.getSkills().getLevel(Constants.STRENGTH) < 8) {
				player.getSkills().drainLevel(Constants.STRENGTH, (int) (strLvl * 0.15));
				npc.setNextAnimation(new Animation(4272));
				delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, def.getMaxHit(), def.getAttackStyle(), target)));
				return npc.getAttackSpeed();
			}

		}
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, def.getMaxHit(), def.getAttackStyle(), target)));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
