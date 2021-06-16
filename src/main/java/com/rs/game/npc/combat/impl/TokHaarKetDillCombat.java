package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class TokHaarKetDillCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "TokHaar-Ket-Dill" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(6) == 0) {
			delayHit(npc, 0, target, getRegularHit(npc, Utils.random(defs.getMaxHit() + 1)));
			target.setNextSpotAnim(new SpotAnim(2999));
			if (target instanceof Player) {
				Player playerTarget = (Player) target;
				playerTarget.sendMessage("The TokHaar-Ket-Dill slams it's tail to the ground.");
			}
		} else {
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
