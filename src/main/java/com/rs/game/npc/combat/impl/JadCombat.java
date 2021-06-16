package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class JadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 2745, 15208 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(3);
		if (attackStyle == 2) { // melee
			if (!npc.inMeleeRange(target))
				attackStyle = Utils.random(2); // set mage
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 1) { // range
			npc.setNextAnimation(new Animation(16202));
			npc.setNextSpotAnim(new SpotAnim(2994));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							target.setNextSpotAnim(new SpotAnim(3000));
						}
					}, 0);
					delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, defs.getMaxHit() - 2, AttackStyle.RANGE, target)));
				}
			}, 2);
		} else {
			npc.setNextAnimation(new Animation(16195));
			npc.setNextSpotAnim(new SpotAnim(2995));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					WorldProjectile p = World.sendProjectile(npc, target, 2996, 80, 30, 40, 5, 5, 0);
					target.setNextSpotAnim(new SpotAnim(2741, 0, 100));
					delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, defs.getMaxHit() - 2, AttackStyle.MAGE, target)));
				}
			}, 2);
		}

		return 8;
	}

}
