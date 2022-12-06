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
package com.rs.game.content.world.areas.apeatoll;

import com.rs.game.World;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class ApeAtollAgility {

	public static void ClimbDownTropicalTree(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock();
		final WorldTile toTile = WorldTile.of(2769, 2746, 1);
		player.setNextForceMovement(new ForceMovement(player.getTile(), 1, toTile, 7, Direction.NORTH));
		player.getAppearance().setBAS(760);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 55);
				player.getAppearance().setBAS(-1);
				player.unlock();
				stop();
			}
		}, 2);
	}

	public static void ClimbDownVine(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final WorldTile toTile = WorldTile.of(player.getX(), player.getY(), 0);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(1381));
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 36);
				stop();
			}
		}, 1);
	}

	public static void ClimbUpSkullSlope(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		final WorldTile toTile = WorldTile.of(2743, 2741, 0);
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 3, Direction.WEST));
		player.getAppearance().setBAS(739);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 45);
				player.getAppearance().setBAS(-1);
				stop();
			}
		}, 2);
	}

	public static void ClimbUpTropicalTree(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final WorldTile toTile = WorldTile.of(2752, 2742, 2);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(1382));
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 25);
				stop();
			}
		}, 1);
	}

	public static void crossMonkeyBars(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		final WorldTile toTile = WorldTile.of(2747, 2741, 0);
		final WorldTile toTile2 = WorldTile.of(2747, 2741, 2);
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile2, 4, Direction.WEST));
		player.getAppearance().setBAS(744);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getAppearance().setBAS(-1);
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 35);
				stop();
			}
		}, 3);
	}

	public static void JumpToSteppingStone(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final WorldTile toTile = WorldTile.of(object.getX(), object.getY(), object.getPlane());
		final WorldTile toTile2 = WorldTile.of(player.getX() == 2755 ? 2753 : 2755, 2742, object.getPlane());
		final WorldTile WaterTile = WorldTile.of(2756, 2746, object.getPlane());
		final WorldTile Land = WorldTile.of(2757, 2746, object.getPlane());
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(1381));
				player.setNextWorldTile(toTile);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						if (Utils.random(5) == 0) {
							player.setNextAnimation(new Animation(1381));
							player.applyHit(new Hit(player, Utils.random(200), HitLook.TRUE_DAMAGE));
							player.setNextForceMovement(new ForceMovement(player.getTile(), 0, WaterTile, 3, Direction.NORTH));
							player.getAppearance().setBAS(741);
							WorldTasks.schedule(new WorldTask() {

								@Override
								public void run() {
									player.getAppearance().setBAS(-1);
									player.setNextWorldTile(Land);
									stop();
								}
							}, 1);
							stop();
						} else {
							player.setNextAnimation(new Animation(1381));
							player.setNextWorldTile(toTile2);
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
		World.sendObjectAnimation(player, object, new Animation(497));
		final WorldTile toTile = WorldTile.of(2756, 2731, object.getPlane());
		player.setNextForceMovement(new ForceMovement(player.getTile(), 1, toTile, 3, Direction.EAST));
		player.getSkills().addXp(Constants.AGILITY, 22);
		player.getPackets().sendGameMessage("You skilfully swing across.", true);
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				stop();
			}
		}, 1);
	}
}
