package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class MetalDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze dragon", "Iron dragon", "Steel dragon", "Pit iron dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0) && Utils.random(2) == 0) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
		} else {
			int damage = 100 + Utils.getRandomInclusive(500);
			final Player player = target instanceof Player p ? p : null;
			if (player != null) {
				int protection = PlayerCombat.getAntifireLevel(target, false);
				if (protection == 1) {
					damage = Utils.getRandomInclusive(50);
				} else if (protection == 2) {
					damage = 0;
				}
			}

			npc.setNextAnimation(new Animation(13160));
			World.sendProjectile(npc, target, 393, 28, 16, 35, 35, 16, 0);
			delayHit(npc, 1, target, getRegularHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}

}
