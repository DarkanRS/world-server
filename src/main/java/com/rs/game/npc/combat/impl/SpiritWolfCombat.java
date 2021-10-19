package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class SpiritWolfCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6829, 6828 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			familiar.submitSpecial(familiar.getOwner());
			npc.setNextAnimation(new Animation(8293));
			npc.setNextSpotAnim(new SpotAnim(1334));
			World.sendProjectile(npc, target, 1333, 34, 16, 30, 35, 16, 0);
			if (target instanceof NPC targN) {
				if (targN.getCombatDefinitions().getAttackStyle() != AttackStyle.SPECIAL)
					target.setAttackedByDelay(3000);// three seconds
				else
					familiar.getOwner().sendMessage("Your familiar cannot scare that monster.");
			} else if (target instanceof Player)
				familiar.getOwner().sendMessage("Your familiar cannot scare a player.");
			else if (target instanceof Familiar)
				familiar.getOwner().sendMessage("Your familiar cannot scare other familiars.");
		} else {
			npc.setNextAnimation(new Animation(6829));
			delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, 40, AttackStyle.MAGE, target)));
		}
		return npc.getAttackSpeed();
	}

}
