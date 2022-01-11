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
package com.rs.game.npc.qbd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.WorldProjectile;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

/**
 * Represents the Queen Black Dragon's fire wall attack.
 *
 * @author Emperor
 *
 */
public final class FireWallAttack implements QueenAttack {

	/**
	 * The wall graphic ids.
	 */
	private static final int[] WALL_GRAPHIC_IDS = { 3158, 3159, 3160 };

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16746);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		int waves = npc.getPhase();
		if (waves > 3)
			waves = 3;
		npc.setNextAnimation(ANIMATION);
		final List<Integer> wallIds = new ArrayList<>();
		for (int id : WALL_GRAPHIC_IDS)
			wallIds.add(id);
		Collections.shuffle(wallIds);
		victim.sendMessage("<col=FF9900>The Queen Black Dragon takes a huge breath.</col>");
		final int wallCount = waves;
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int i = 0; i < wallCount; i++) {
					final int wallId = wallIds.get(i);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							for (int j = 0; j < 2; j++) {
								final boolean second = j == 1;
								WorldTasks.schedule(new WorldTask() {
									int y = 37 + (second ? 1 : 0);
									@Override
									public void run() {
										if (npc == null || npc.getPhase() >= 5 || npc.hasFinished() || !npc.withinDistance(victim) || npc.isDead())
											return;
										if (!((wallId == 3158 && victim.getX() == npc.getBase().getX() + 28) || (wallId == 3159 && victim.getX() == npc.getBase().getX() + 37) || (wallId == 3160 && victim.getX() == npc.getBase().getX() + 32)))
											if (victim.getY() == npc.getBase().getY() + y) {
												int hit;
												int protection = PlayerCombat.getAntifireLevel(victim, true);
												if (protection == 1)
													hit = Utils.random(350, 360);
												else if (protection == 2)
													hit = Utils.random(200, 210);
												else
													hit = Utils.random(500, 510);
												victim.applyHit(new Hit(npc, hit, HitLook.TRUE_DAMAGE));
											}
										if (--y == 19)
											stop();
									}
								}, 0, 0);
							}
							victim.getPackets().sendProjectile(new WorldProjectile(npc.getBase().transform(33, 38, 0), npc.getBase().transform(33, 19, 0), wallId, 0, 0, 18, 600, 0, 0, null));
						}

					}, (i * 7) + 1);
				}
			}
		}, 1);
		npc.getTempAttribs().setI("fire_wall_tick_", npc.getTicks() + Utils.random((waves * 7) + 5, 60));
		return 8 + (waves * 2);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		int tick = npc.getTempAttribs().getI("fire_wall_tick_");
		return tick == -1 || tick < npc.getTicks();
	}

}