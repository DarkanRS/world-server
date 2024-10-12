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
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.dungeoneering.npcs.Gravecreeper;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;
import kotlin.Pair;

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
			WorldTasks.scheduleTimer(1, (ticks) -> {
				boss.createBurnTiles(Tile.of(boss.getTile()));
				return false;
			});
			boss.setSpecialDelay(World.getServerTicks() + Gravecreeper.BURN_DELAY);
			if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
				boss.setForceFollowClose(true);
				WorldTasks.scheduleTimer(7, (ticks) -> {
					boss.setForceFollowClose(false);
					return false;
				});
			}
			return 4;
		}

		boolean atDistance = !WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		int attack = Utils.random(!atDistance ? 2 : 1);
		switch (attack) {
		case 0:// range
			npc.setNextAnimation(new Animation(14504));
			World.sendProjectile(npc, target, 2753, new Pair<>(65, 65), 30, 5, 0);
			delayHit(npc, 1, target, Hit.range(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.RANGE, target)));
			break;
		case 1:// melee
			npc.setNextAnimation(new Animation(14503));
			delayHit(npc, 0, target, Hit.melee(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MELEE, target)));
			break;
		}
		return 4;
	}
}
