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
package com.rs.game.content.skills.agility;

import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class ApeAtollAgility {

	public static void ClimbDownTropicalTree(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.forceMove(Tile.of(2769, 2746, 1), 30, 7*30, () -> {
			player.getSkills().addXp(Constants.AGILITY, 55);
			player.getAppearance().setBAS(-1);
		});
	}

	public static void ClimbDownVine(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		WorldTasks.schedule(1, () -> {
			player.setNextAnimation(new Animation(1381));
			player.setNextTile(Tile.of(player.getX(), player.getY(), 0));
			player.getSkills().addXp(Constants.AGILITY, 36);
		});
	}

	public static void ClimbUpSkullSlope(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.forceMove(Tile.of(2743, 2741, 0), 10, 3*30, () -> {
			player.getSkills().addXp(Constants.AGILITY, 45);
			player.getAppearance().setBAS(-1);
		});
	}

	public static void ClimbUpTropicalTree(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		WorldTasks.schedule(1, () -> {
			player.setNextAnimation(new Animation(1382));
			player.setNextTile(Tile.of(2752, 2742, 2));
			player.getSkills().addXp(Constants.AGILITY, 25);
		});
	}

	public static void crossMonkeyBars(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		player.getAppearance().setBAS(744);
		player.forceMove(Tile.of(2747, 2741, 2), 10, 4*30, () -> {
			player.setNextTile(Tile.of(2747, 2741, 0));
			player.getSkills().addXp(Constants.AGILITY, 35);
			player.getAppearance().setBAS(-1);
		});
	}

	public static void JumpToSteppingStone(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final Tile toTile = Tile.of(object.getX(), object.getY(), object.getPlane());
		final Tile toTile2 = Tile.of(player.getX() == 2755 ? 2753 : 2755, 2742, object.getPlane());
		final Tile WaterTile = Tile.of(2756, 2746, object.getPlane());
		final Tile Land = Tile.of(2757, 2746, object.getPlane());
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(1381));
				player.setNextTile(toTile);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						if (Utils.random(5) == 0) {
							player.setNextAnimation(new Animation(1381));
							player.applyHit(new Hit(player, Utils.random(200), HitLook.TRUE_DAMAGE));
							player.getAppearance().setBAS(741);
							player.forceMove(WaterTile, 10, 90, () -> {
								player.getAppearance().setBAS(-1);
								player.setNextTile(Land);
							});
							stop();
						} else {
							player.setNextAnimation(new Animation(1381));
							player.setNextTile(toTile2);
							player.getSkills().addXp(Constants.AGILITY, 15);
							stop();
						}
					}
				}, 1);
				stop();
			}
		}, 1);
	}

	public static void swingRope(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48) || (player.getX() == 2756))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to be a ninja monkey to be able to do this agility.");
			return;
		}
		player.lock(4);
		player.setNextAnimation(new Animation(1392));
		World.sendObjectAnimation(object, new Animation(497));
		player.forceMove(Tile.of(2756, 2731, object.getPlane()), 20, 90, () -> player.getSkills().addXp(Constants.AGILITY, 22));
		player.getPackets().sendGameMessage("You skilfully swing across.", true);
	}
}
