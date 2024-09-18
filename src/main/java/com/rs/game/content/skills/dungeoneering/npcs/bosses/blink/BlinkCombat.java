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
package com.rs.game.content.skills.dungeoneering.npcs.bosses.blink;

import com.rs.game.World;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;
import kotlin.Pair;

import java.util.LinkedList;
import java.util.List;

public class BlinkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Blink" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Blink boss = (Blink) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0 || boss.isSpecialRequired()) {
			boss.setSpecialRequired(false);
			boss.setNextForceTalk(new ForceTalk("H...Here it comes..."));
			// boss.playSoundEffect(2989);
			WorldTasks.delay(5, () -> {
				boss.setNextAnimation(new Animation(14956));
				boss.setNextForceTalk(new ForceTalk("Kapow!!"));
				// boss.playSoundEffect(3002);
				for (Entity t : boss.getPossibleTargets()) {
					if (t instanceof Player player)
						player.sendMessage("You are hit by a powerful magical blast.");
					t.setNextSpotAnim(new SpotAnim(2855, 0, 50));
					delayHit(boss, 0, t, new Hit(boss, (int) Utils.random(boss.getMaxHit() * .6D, boss.getMaxHit()), HitLook.MAGIC_DAMAGE));
				}
			});
			return 8;
		}

		boolean atDistance = !WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		if (!atDistance && (Utils.random(3) != 0)) {
			boss.setNextAnimation(new Animation(12310));
			delayHit(boss, 0, target, Hit.melee(boss, getMaxHit(boss, boss.getMaxHit(), CombatStyle.MELEE, target)));
			return 4;
		}
		boolean rangeAttack = Utils.random(3) == 0;

		if (rangeAttack) {
			if (manager.getParty().getTeam().size() > 1 || Utils.random(3) == 0) {
				Tile beginningTile = boss.getNextPath();
				boss.setNextAnimation(new Animation(14949));
				boss.resetCombat();
				boss.setNextFaceEntity(null);
				boss.setNextFaceTile(beginningTile);// Faces the direction it throws into
				World.sendProjectile(boss, beginningTile, 2853, new Pair<>(18, 18), 50, 5, 0);
				WorldTasks.scheduleLooping(new Task() {

					private List<Tile> knifeTargets;
					private int cycles;

					@Override
					public void run() {
						cycles++;
						if (cycles == 1) {
							knifeTargets = new LinkedList<>();
							for (Entity t : boss.getPossibleTargets()) {
								Tile center = Tile.of(t.getTile());
								for (int i = 0; i < 3; i++)
									knifeTargets.add(i == 0 ? center : World.getFreeTile(center, 1));
							}
						} else if (cycles == 2) {
							for (Tile tile : knifeTargets) {
								// outdated method projectile
								int delay = 3;
								for (Entity t : boss.getPossibleTargets()) {
									if (!t.matches(tile))
										continue;
									delayHit(boss, delay, t, Hit.range(boss, getMaxHit(boss, boss.getMaxHit(), CombatStyle.RANGE, t)));
								}
							}
							stop();
							return;
						}
					}
				}, 0, 0);
			} else {
				boss.setNextAnimation(new Animation(14949));
				World.sendProjectile(boss, target, 2853, new Pair<>(18, 18), 50, 5, 0);
				delayHit(boss, 1, target, Hit.range(boss, getMaxHit(boss, boss.getMaxHit(), CombatStyle.RANGE, target)));
			}
		} else {
			if (Utils.random(7) == 0)
				boss.setNextForceTalk(new ForceTalk("Magicinyaface!"));
			// boss.playSoundEffect(3022); //MAGIC IN YA FACE
			boss.setNextAnimation(new Animation(14956));
			boss.setNextSpotAnim(new SpotAnim(2854));
			target.setNextSpotAnim(new SpotAnim(2854, 5, 0));
			int damage = getMaxHit(boss, boss.getMaxHit(), CombatStyle.MAGE, target);
			if (target instanceof Player player)
				if (player.getPrayer().isProtectingMage())
					damage *= 0.5D;
			delayHit(boss, 1, target, Hit.magic(boss, damage));
		}
		return 5;
	}
}
