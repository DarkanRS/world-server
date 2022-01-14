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
package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.glacors.Glacor;
import com.rs.game.npc.glacors.Glacor.InheritedType;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class GlacorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14301 };
	}

	final int MAGE_PROJECTILE = 634;
	final int RANGE_PROJECTILE = 962;
	final int EXPLODE_GFX = 739;
	final int SPECIAL_PROJECTILE = 2314;

	int attackType = 0;

	@Override
	public int attack(NPC npc, final Entity target) {
		if (target instanceof NPC) {
			npc.setNextAnimation(new Animation(9968));
			npc.setNextSpotAnim(new SpotAnim(905));
			WorldProjectile p = World.sendProjectile(npc, target, SPECIAL_PROJECTILE, 60, 32, 50, 2, 0, 0);
			final WorldTile targetPosition = new WorldTile(target.getX(), target.getY(), target.getPlane());
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if ((target.getX() == targetPosition.getX()) && (target.getY() == targetPosition.getY()))
						target.applyHit(new Hit(target, 500, HitLook.TRUE_DAMAGE));
					World.sendSpotAnim(null, new SpotAnim(2315), targetPosition);
				}
			}, p.getTaskDelay());
			return 0;
		}
		if (target instanceof Player player) {
			final Glacor glacor = (Glacor) npc;

			glacor.lastAttacked = player;

			attackType = Utils.random(1, 3);

			if (glacor.getMinionType() != null && glacor.getMinionType() == InheritedType.SAPPING)
				player.getPrayer().drainPrayer(50);

			if (Utils.random(100) < 10)
				attackType = 3;

			if (attackType == 1) {
				npc.setNextAnimation(new Animation(9967));
				npc.setNextSpotAnim(new SpotAnim(902));
				WorldProjectile p = World.sendProjectile(npc, target, MAGE_PROJECTILE, 60, 32, 50, 2, 0, 0);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						delayHit(npc, -1, target, getMagicHit(npc, getMaxHit(npc, 255, AttackStyle.MAGE, player)));
					}
				}, p.getTaskDelay());
				if ((Utils.getRandomInclusive(100) > 80) && !player.hasEffect(Effect.FREEZE)) {
					player.setNextSpotAnim(new SpotAnim(369));
					player.freeze(Ticks.fromSeconds(10));
				}
			} else if (attackType == 2) {
				npc.setNextAnimation(new Animation(9968));
				npc.setNextSpotAnim(new SpotAnim(905));
				WorldProjectile p = World.sendProjectile(npc, target, RANGE_PROJECTILE, 60, 32, 50, 2, 0, 0);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						delayHit(npc, -1, target, getRangeHit(npc, getMaxHit(npc, 255, AttackStyle.RANGE, player)));
					}
				}, p.getTaskDelay());
			} else if (attackType == 3) {
				npc.setNextAnimation(new Animation(9955));
				npc.setNextSpotAnim(new SpotAnim(905));
				WorldProjectile p = World.sendProjectile(npc, target, SPECIAL_PROJECTILE, 60, 32, 50, 1, 0, 0);
				final WorldTile targetPosition = new WorldTile(player.getX(), player.getY(), player.getPlane());
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if ((player.getX() == targetPosition.getX()) && (player.getY() == targetPosition.getY()))
							player.applyHit(new Hit(player, player.getHitpoints() / 2, HitLook.TRUE_DAMAGE));
						World.sendSpotAnim(null, new SpotAnim(2315), targetPosition);
					}
				}, p.getTaskDelay());
			}
		}
		return 5;
	}

}
