package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class FrostDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Frost dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage;
		int mageRange = npc.getTempI("frostAtkStyle", -1) == -1 ? Utils.getRandomInclusive(1) : npc.getTempI("frostAtkStyle", -1);
		npc.setTempI("frostAtkStyle", mageRange);
		
		if (Utils.random(3) == 0) {
			if (WorldUtil.isInRange(npc, target, 0)) {
				damage = Utils.getRandomInclusive(500);
				int protection = PlayerCombat.getAntifireLevel(target, true);
				if (protection == 1) {
					damage = Utils.getRandomInclusive(40);
				} else if (protection == 2) {
					damage = 0;
				}
				npc.setNextAnimation(new Animation(13152));
				npc.setNextSpotAnim(new SpotAnim(2465));
				delayHit(npc, 1, target, getRegularHit(npc, damage));
			} else {
				damage = Utils.getRandomInclusive(500);
				int protection = PlayerCombat.getAntifireLevel(target, true);
				if (protection == 1) {
					damage = Utils.getRandomInclusive(50);
				} else if (protection == 2) {
					damage = 0;
				}
				npc.setNextAnimation(new Animation(13155));
				delayHit(npc, World.sendProjectile(npc, target, 393, 28, 16, 35, 2, 16, 0).getTaskDelay(), target, getRegularHit(npc, damage));
			}
		} else {
			if (npc.withinDistance(target, 3) && Utils.random(2) == 0) {
				damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, damage));
			} else if (mageRange == 0) {
				damage = Utils.getRandomInclusive(250);
				npc.setNextAnimation(new Animation(13155));
				delayHit(npc, World.sendProjectile(npc, target, 2705, 28, 16, 35, 2, 16, 0).getTaskDelay(), target, getMagicHit(npc, damage), () -> {
					target.setNextSpotAnim(new SpotAnim(2711));
				});
			} else {
				damage = Utils.getRandomInclusive(250);
				npc.setNextAnimation(new Animation(13155));
				delayHit(npc, World.sendProjectile(npc, target, 11, 28, 16, 35, 2, 16, 0).getTaskDelay(), target, getRangeHit(npc, damage));
			}
		}
		return npc.getAttackSpeed();
	}

}
