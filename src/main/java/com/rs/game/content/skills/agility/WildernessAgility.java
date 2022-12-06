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
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class WildernessAgility {

	/*
	 * Author Bandoswhips
	 */

	public static void GateWalk(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		if (player.getY() != object.getY()) {
			player.addWalkSteps(2998, 3916, 0, false);
			player.lock(2);
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					GateWalkEnd(player, object);
				}
			}, 1);
		} else
			GateWalkEnd(player, object);
	}

	public static void GateWalk2(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		if (player.getY() != object.getY()) {
			player.addWalkSteps(2998, 3931, 0, false);
			player.lock(2);
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					GateWalkEnd2(player, object);
				}
			}, 1);
		} else
			GateWalkEnd2(player, object);
	}

	private static void GateWalkEnd(final Player player, GameObject object) {
		player.sendMessage("You walk carefully across the path...", true);
		player.lock();
		player.setNextAnimation(new Animation(9908));
		final WorldTile toTile = WorldTile.of(object.getX(), 3931, object.getPlane());
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 16, Direction.NORTH));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				player.sendMessage("... and make it safely to the other side.", true);
				player.unlock();
			}

		}, 15);
	}

	private static void GateWalkEnd2(final Player player, GameObject object) {
		player.sendMessage("You walk carefully across the path...", true);
		player.lock();
		player.setNextAnimation(new Animation(9908));
		final WorldTile toTile = WorldTile.of(object.getX() + 1, 3916, object.getPlane());
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 16, Direction.SOUTH));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				player.sendMessage("... and make it safely to the other side.", true);
				player.unlock();
			}

		}, 15);
	}

	public static void enterObstaclePipe(final Player player, int objectX, int objectY) {
		if (!Agility.hasLevel(player, 52))
			return;
		final boolean running = player.getRun();
		final WorldTile toTile = WorldTile.of(objectX, objectY == 3938 ? 3950 : 3937, 0);
		player.setRunHidden(false);
		player.lock(7);
		player.addWalkSteps(objectX, objectY == 3938 ? 3950 : 3937, -1, false);
		player.sendMessage("You pulled yourself through the pipes.", true);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearance().setBAS(295);
				} else {
					player.setNextWorldTile(toTile);
					player.getAppearance().setBAS(-1);
					setWildernessStage(player, 0);
					player.setRunHidden(running);
					player.getSkills().addXp(Constants.AGILITY, 7);
					stop();
				}
			}
		}, 0, 6);
	}

	public static void swingOnRopeSwing(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 52))
			return;
		if (player.getY() > 3953) {
			player.sendMessage("You can't see a good way to jump from here.");
			return;
		}
		player.lock(4);
		player.setNextAnimation(new Animation(751));
		World.sendObjectAnimation(player, object, new Animation(497));
		final WorldTile toTile = WorldTile.of(object.getX(), 3958, object.getPlane());
		player.setNextForceMovement(new ForceMovement(player.getTile(), 1, toTile, 3, Direction.NORTH));
		player.getSkills().addXp(Constants.AGILITY, 22);
		player.sendMessage("You skilfully swing across.", true);
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				if (getWildernessStage(player) == 0)
					setWildernessStage(player, 1);
				player.getSkills().addXp(Constants.AGILITY, 15.5);
			}

		}, 1);
	}

	/*
	 * Stepping Stone Method by Cjay0091
	 */
	public static void crossSteppingPalletes(final Player player, final GameObject object) {
		if (player.getY() != object.getY())
			return;
		player.lock();
		WorldTasks.schedule(new WorldTask() {

			int x;

			@Override
			public void run() {
				if (x++ == 6) {
					player.unlock();
					stop();
					return;
				}
				final WorldTile toTile = WorldTile.of(3002 - x, player.getY(), player.getPlane());
				player.setNextForceMovement(new ForceMovement(toTile, 1, Direction.WEST));
				player.setNextAnimation(new Animation(741));
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						player.setNextWorldTile(toTile);
					}
				}, 0);
			}
		}, 2, 1);
		player.getSkills().addXp(Constants.AGILITY, 20);
		if (getWildernessStage(player) == 1)
			setWildernessStage(player, 2);
	}

	public static void walkLog(final Player player) {
		if (player.getX() != 3002 || player.getY() != 3945)
			player.addWalkSteps(3002, 3945, -1, false);
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(2994, 3945, -1, false);
		player.sendMessage("You walk carefully across the log...", true);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearance().setBAS(155);
				} else {
					player.unlock();
					player.getAppearance().setBAS(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Constants.AGILITY, 7.5);
					player.sendMessage("... and make it safely to the other side.", true);
					stop();
					if (getWildernessStage(player) == 2)
						setWildernessStage(player, 3);
				}
			}
		}, 0, 6);
	}

	public static void climbCliff(final Player player, GameObject object) {
		if (player.getY() != 3939)
			return;
		player.setNextAnimation(new Animation(3378));
		final WorldTile toTile = WorldTile.of(player.getX(), 3935, 0);

		player.sendMessage("You climb up the rock.", true);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(toTile);
				player.setNextAnimation(new Animation(-1));
				player.getAppearance().setBAS(-1);
				stop();
				if (getWildernessStage(player) == 3) {
					player.incrementCount("Wilderness laps");
					removeWildernessStage(player);
					player.getSkills().addXp(Constants.AGILITY, 499);

				}
			}
		}, 5);
	}

	public static void removeWildernessStage(Player player) {
		player.getTempAttribs().removeI("WildernessCourse");
	}

	public static void setWildernessStage(Player player, int stage) {
		player.getTempAttribs().setI("WildernessCourse", stage);
	}

	public static int getWildernessStage(Player player) {
		return player.getTempAttribs().getI("WildernessCourse");
	}
}