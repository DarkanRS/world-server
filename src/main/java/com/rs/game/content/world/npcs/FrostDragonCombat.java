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
package com.rs.game.content.world.npcs;

import com.rs.game.World;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.combat.PlayerCombatKt;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
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
		int mageRange = npc.getTempAttribs().getI("frostAtkStyle", -1) == -1 ? Utils.getRandomInclusive(1) : npc.getTempAttribs().getI("frostAtkStyle", -1);
		npc.getTempAttribs().setI("frostAtkStyle", mageRange);

		if (Utils.random(3) == 0) {
			if (WorldUtil.isInRange(npc, target, 0)) {
				damage = Utils.getRandomInclusive(500);
				int protection = PlayerCombatKt.getAntifireLevel(target, true);
				if (protection == 1)
					damage = Utils.getRandomInclusive(40);
				else if (protection == 2)
					damage = 0;
				npc.setNextAnimation(new Animation(13152));
				npc.setNextSpotAnim(new SpotAnim(2465));
				delayHit(npc, 1, target, getRegularHit(npc, damage));
			} else {
				damage = Utils.getRandomInclusive(500);
				int protection = PlayerCombatKt.getAntifireLevel(target, true);
				if (protection == 1)
					damage = Utils.getRandomInclusive(50);
				else if (protection == 2)
					damage = 0;
				npc.setNextAnimation(new Animation(13155));
				delayHit(npc, World.sendProjectile(npc, target, 393, 28, 16, 35, 2, 16).getTaskDelay(), target, getRegularHit(npc, damage));
			}
		} else if (npc.withinDistance(target.getTile(), 3) && Utils.random(2) == 0) {
			damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
		} else if (mageRange == 0) {
			damage = Utils.getRandomInclusive(250);
			npc.setNextAnimation(new Animation(13155));
			delayHit(npc, World.sendProjectile(npc, target, 2705, 28, 16, 35, 2, 16).getTaskDelay(), target, getMagicHit(npc, damage), () -> target.setNextSpotAnim(new SpotAnim(2711)));
		} else {
			damage = Utils.getRandomInclusive(250);
			npc.setNextAnimation(new Animation(13155));
			delayHit(npc, World.sendProjectile(npc, target, 11, 28, 16, 35, 2, 16).getTaskDelay(), target, getRangeHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}

}
