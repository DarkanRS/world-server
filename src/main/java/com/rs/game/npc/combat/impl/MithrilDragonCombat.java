package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class MithrilDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Mithril dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage;
		switch (Utils.getRandomInclusive(3)) {
		case 0: // Melee
			if (npc.withinDistance(target, 3)) {
				damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, damage));
			} else {
				damage = Utils.getRandomInclusive(500);
				int protection = PlayerCombat.getAntifireLevel(target, true);
				if (protection == 1) {
					damage = Utils.getRandomInclusive(50);
				} else if (protection == 2) {
					damage = 0;
				}
				npc.setNextAnimation(new Animation(14252));
				WorldProjectile p = World.sendProjectile(npc, target, 393, 28, 32, 50, 2, 16, 0);
				delayHit(npc, p.getTaskDelay(), target, getRegularHit(npc, damage));
			}
			break;
		case 1: // Dragon breath
			if (npc.withinDistance(target, 3)) {
				damage = Utils.getRandomInclusive(650);
				int protection = PlayerCombat.getAntifireLevel(target, true);
				if (protection == 1) {
					damage = Utils.getRandomInclusive(300);
				} else if (protection == 2) {
					damage = 0;
				}
				npc.setNextAnimation(new Animation(14252));
				npc.setNextSpotAnim(new SpotAnim(2465));
				delayHit(npc, 1, target, getRegularHit(npc, damage));
			} else {
				damage = Utils.getRandomInclusive(650);
				int protection = PlayerCombat.getAntifireLevel(target, true);
				if (protection == 1) {
					damage = Utils.getRandomInclusive(40);
				} else if (protection == 2) {
					damage = 0;
				}
				npc.setNextAnimation(new Animation(14252));
				WorldProjectile p = World.sendProjectile(npc, target, 393, 28, 32, 50, 2, 16, 0);
				delayHit(npc, p.getTaskDelay(), target, getRegularHit(npc, damage));
			}
			break;
		case 2: // Range
			damage = Utils.getRandomInclusive(250);
			npc.setNextAnimation(new Animation(14252));
			World.sendProjectile(npc, target, 2707, 28, 16, 35, 35, 16, 0);
			delayHit(npc, 1, target, getRangeHit(npc, damage));
			break;
		case 3: // Ice arrows range
			damage = Utils.getRandomInclusive(250);
			npc.setNextAnimation(new Animation(14252));
			WorldProjectile p = World.sendProjectile(npc, target, 2705, 28, 32, 50, 2, 16, 0);
			delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, damage));
			break;
		}
		return npc.getAttackSpeed();
	}

}
