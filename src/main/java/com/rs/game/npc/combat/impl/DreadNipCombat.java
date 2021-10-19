package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.others.DreadNip;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class DreadNipCombat extends CombatScript {

	private String[] DREADNIP_ATTACK_MESSAGE = { "Your dreadnip stunned its target!", "Your dreadnip poisened its target!" };

	@Override
	public Object[] getKeys() {
		return new Object[] { 14416 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		DreadNip dreadNip = (DreadNip) npc;
		if (dreadNip.getTicks() <= 3)
			return 0;
		npc.setNextAnimation(new Animation(-1));
		int attackStyle = Utils.random(2);
		switch (attackStyle) {
		case 0:
			break;
		case 1:
			int tickDelay = 5 + Utils.getRandomInclusive(3);
			target.freeze(tickDelay);
			if (target instanceof Player player) {
				player.getActionManager().addActionDelay(tickDelay);
			} else {
				NPC npcTarget = (NPC) target;
				npcTarget.getCombat().setCombatDelay(npcTarget.getCombat().getCombatDelay() + tickDelay);
			}
			break;
		case 2:
			target.getPoison().makePoisoned(108);
			break;
		}
		if (attackStyle != 0)
			dreadNip.getOwner().sendMessage(DREADNIP_ATTACK_MESSAGE[attackStyle - 1]);
		delayHit(npc, 0, target, new Hit(npc, getMaxHit(npc, 550, AttackStyle.MELEE, target), HitLook.TRUE_DAMAGE));
		return 5;
	}
}
