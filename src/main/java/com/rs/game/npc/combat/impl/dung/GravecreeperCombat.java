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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.Gravecreeper;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class GravecreeperCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Gravecreeper" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Gravecreeper boss = (Gravecreeper) npc;
		if (boss.getSpecialDelay() != -2 && (boss.getSpecialDelay() == -1 || (Utils.random(10) == 0 && boss.getSpecialDelay() <= World.getServerTicks()))) {
			if ((boss.getSpecialDelay() == -1) || (Utils.random(5) == 0)) {
				boss.useSpecial();
				return 4;
			}
			boss.setNextForceTalk(new ForceTalk("Burrnnn!"));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					boss.createBurnTiles(new WorldTile(boss));
				}
			}, 1);
			boss.setSpecialDelay(World.getServerTicks() + Gravecreeper.BURN_DELAY);
			if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
				boss.setForceFollowClose(true);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.setForceFollowClose(false);
					}
				}, 7);
			}
			return 4;
		}

		boolean atDistance = !WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		int attack = Utils.random(!atDistance ? 2 : 1);
		switch (attack) {
		case 0:// range
			npc.setNextAnimation(new Animation(14504));
			World.sendProjectile(npc, target, 2753, 65, 65, 30, 0, 0, 0);
			delayHit(npc, 1, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			break;
		case 1:// melee
			npc.setNextAnimation(new Animation(14503));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			break;
		}
		return 4;
	}
}
