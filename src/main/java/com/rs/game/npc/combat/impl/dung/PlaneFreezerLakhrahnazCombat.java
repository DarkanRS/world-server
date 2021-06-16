package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.pathing.Direction;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class PlaneFreezerLakhrahnazCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Plane-freezer Lakhrahnaz" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(8) == 0) {
			npc.resetWalkSteps();
			npc.addWalkSteps(npc.getX() + Utils.random(3) - 2, npc.getY() + Utils.random(3) - 2);
		}
		if (Utils.random(3) == 0) {
			int attackStyle = Utils.random(2);
			if (attackStyle == 1 && !WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0))
				attackStyle = 0;
			switch (attackStyle) {
			case 0:
				npc.setNextAnimation(new Animation(13775));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendProjectile(npc, t, 2577, 16, 16, 41, 30, 0, 0);
					t.setNextSpotAnim(new SpotAnim(2578, 70, 0));
					delayHit(npc, 1, t, getMagicHit(npc, getMaxHit(npc, 100, AttackStyle.MAGE, target)));
				}
				break;
			case 1:
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				Direction dir = Direction.random();
				target.addWalkSteps(target.getX() + dir.getDx(), target.getY() + dir.getDy(), 1);
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, 100, AttackStyle.MELEE, target)));
				break;
			}
			return npc.getAttackSpeed();
		}
		npc.setNextAnimation(new Animation(13775));
		npc.setNextSpotAnim(new SpotAnim(2574));
		World.sendProjectile(npc, target, 2595, 16, 16, 41, 30, 0, 0);
		target.setNextSpotAnim(new SpotAnim(2576, 70, 0));
		delayHit(npc, 1, target, getRangeHit(npc, getMaxHit(npc, 100, AttackStyle.RANGE, target)));
		return npc.getAttackSpeed();
	}
}
