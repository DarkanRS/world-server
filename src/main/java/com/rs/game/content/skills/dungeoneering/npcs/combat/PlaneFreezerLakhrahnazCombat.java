// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.pathing.Direction;
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
				case 0 -> {
					npc.setNextAnimation(new Animation(13775));
					for (Entity t : npc.getPossibleTargets()) {
						World.sendProjectile(npc, t, 2577, 16, 16, 41, 30, 0, 0);
						t.setNextSpotAnim(new SpotAnim(2578, 70, 0));
						delayHit(npc, 1, t, getMagicHit(npc, getMaxHit(npc, 100, AttackStyle.MAGE, target)));
					}
				}
				case 1 -> {
					npc.setNextAnimation(new Animation(defs.getAttackEmote()));
					Direction dir = Direction.random();
					target.addWalkSteps(target.getX() + dir.getDx(), target.getY() + dir.getDy(), 1);
					delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, 100, AttackStyle.MELEE, target)));
				}
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
