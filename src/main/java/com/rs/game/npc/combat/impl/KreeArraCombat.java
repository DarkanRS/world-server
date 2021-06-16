package com.rs.game.npc.combat.impl;

import com.rs.cores.CoresManager;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.pathing.Direction;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class KreeArraCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6222 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!npc.isUnderCombat()) {
			npc.setNextAnimation(new Animation(6997));
			delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 260, AttackStyle.MELEE, target)));
			return npc.getAttackSpeed();
		}
		npc.setNextAnimation(new Animation(6976));
		for (Entity t : npc.getPossibleTargets()) {
			if (Utils.getRandomInclusive(2) == 0) {
				WorldProjectile p = World.sendProjectile(npc, t, 1198, 60, 32, 50, 1, 0, 0);
				npc.setNextAnimation(new Animation(6976));
				delayHit(npc, p.getTaskDelay(), t, getMagicHit(npc, getMaxHit(npc, 210, AttackStyle.MAGE, t)));
				t.setNextSpotAnim(new SpotAnim(1196, p.getTaskDelay()));
			} else {
				WorldProjectile p = World.sendProjectile(npc, t, 1197, 60, 32, 50, 1, 0, 0);
				delayHit(npc, p.getTaskDelay(), t, getRangeHit(npc, getMaxHit(npc, 720, AttackStyle.RANGE, t)));
				CoresManager.schedule(() -> {
					Direction dir = WorldUtil.getDirectionTo(npc, target);
					if (dir != null) {
						if (World.checkWalkStep(target, dir, target.getSize())) {
							target.resetWalkSteps();
							target.setNextWorldTile(target.transform(dir.getDx(), dir.getDy()));
						}
					}
				}, p.getTaskDelay());
			}
		}
		return npc.getAttackSpeed();
	}
}
