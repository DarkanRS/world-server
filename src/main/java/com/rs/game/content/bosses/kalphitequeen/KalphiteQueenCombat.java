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
package com.rs.game.content.bosses.kalphitequeen;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KalphiteQueenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kalphite Queen" };
	}
	
	//278, 279, 280, 281 spotanims

	public static void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final NPC startTile, Entity t, final int projectile, final int gfx) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile.getTile()) : t;
		if (fromEntity instanceof NPC npc) {
			if (target == null)
				return;
			if (target instanceof Player player)
				arrayList.add(player);
			World.sendProjectile(fromEntity, target, projectile, fromEntity == startTile ? 70 : 20, 20, 30, 6, 0, 0);
			int endTime = 3;
			delayHit(startTile, endTime, target, getMagicHit(startTile, getMaxHit(startTile, npc.getMaxHit(), AttackStyle.MAGE, target)));
			WorldTasks.schedule(new Task() {
				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(gfx));
					attackMageTarget(arrayList, target, startTile, null, projectile, gfx);
				}
			}, endTime);
		}
	}

	private void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final NPC startTile, Entity t) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile.getTile()) : t;
		if (target == null)
			return;
		if (target instanceof Player player)
			arrayList.add(player);
		World.sendProjectile(fromEntity, target, 280, fromEntity == startTile ? 70 : 20, 20, 60, 30, 0, 0);
		delayHit(startTile, 0, target, getMagicHit(startTile, getMaxHit(startTile, startTile.getMaxHit(), AttackStyle.MAGE, target)));
		WorldTasks.schedule(new Task() {

			@Override
			public void run() {
				target.setNextSpotAnim(new SpotAnim(281));
				attackMageTarget(arrayList, target, startTile, null);
			}
		});
	}

	private static Player getTarget(List<Player> list, final Entity fromEntity, Tile startTile) {
		if (fromEntity == null)
			return null;
		ArrayList<Player> added = new ArrayList<>();
		for (Player player : fromEntity.queryNearbyPlayersByTileRange(16, player -> !list.contains(player) && player.withinDistance(fromEntity.getTile()) && player.withinDistance(startTile)))
			added.add(player);
		if (added.isEmpty())
			return null;
		Collections.sort(added, (o1, o2) -> {
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;
			if (Utils.getDistance(o1.getTile(), fromEntity.getTile()) > Utils.getDistance(o2.getTile(), fromEntity.getTile()))
				return 1;
			if (Utils.getDistance(o1.getTile(), fromEntity.getTile()) < Utils.getDistance(o2.getTile(), fromEntity.getTile()))
				return -1;
			return 0;
		});
		return added.get(0);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(3);
		if (attackStyle == 0) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if ((distanceX <= size) && (distanceX >= -1) && (distanceY <= size) && (distanceY >= -1)) {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
			attackStyle = Utils.random(2);
		}
		npc.setNextAnimation(new Animation(npc.getId() == 1158 ? 6240 : 6234));
		if (attackStyle == 1)
			// range easy one
			for (final Entity t : npc.getPossibleTargets()) {
				delayHit(npc, 2, t, getRangeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, t)));
				World.sendProjectile(npc, t, 288, 46, 31, 50, 30, 16, 0);
			}
		else {
			npc.setNextSpotAnim(new SpotAnim(npc.getId() == 1158 ? 278 : 279));
			WorldTasks.schedule(new Task() {

				@Override
				public void run() {
					attackMageTarget(new ArrayList<Player>(), npc, npc, target);
				}

			});
		}
		return npc.getAttackSpeed();
	}
}
