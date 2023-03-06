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
package com.rs.game.content.world;

import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class AgilityShortcuts {

	public static void forceMovement(Player player, Tile tile, int animation, int delay) {
		forceMovement(player, tile, animation, 0, delay);
	}

	public static void forceMovement(Player player, Tile tile, int animation, int useDelay, int delay) {
		player.setNextAnimation(new Animation(animation));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 1, tile, delay+1+useDelay));
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextTile(tile);
				player.unlock();
			}
		}, delay+useDelay);
	}

	public static void forceMovement(Player player, Tile tile, int animation, int useDelay, int delay, Direction direction) {
		player.setNextAnimation(new Animation(animation));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 1, tile, delay+1+useDelay, direction));
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextTile(tile);
				player.unlock();
			}
		}, delay+useDelay);
	}

	public static void forceMovementInstant(Player player, Tile tile, int animation, int delay) {
		forceMovementInstant(player, tile, animation, 0, delay);
	}

	public static void forceMovementInstant(Player player, Tile tile, int animation, int useDelay, int delay) {
		player.setNextAnimation(new Animation(animation));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, tile, delay+1+useDelay));
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextTile(tile);
				player.unlock();
			}
		}, delay+useDelay);
	}

	public static void forceMovementInstant(Player player, Tile tile, int animation, int useDelay, int delay, Direction direction) {
		player.setNextAnimation(new Animation(animation));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, tile, delay+1+useDelay, direction));
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextTile(tile);
				player.unlock();
			}
		}, delay+useDelay);
	}

	public static void climbOver(Player player, Tile toTile) {
		climbOver(player, toTile, 1560);
	}

	public static void climbOver(Player player, Tile toTile, int animId) {
		Direction direction = null;
		if (player.getX() < toTile.getX())
			direction = Direction.EAST;
		else if (player.getX() > toTile.getX())
			direction = Direction.WEST;
		else if (player.getY() < toTile.getY())
			direction = Direction.NORTH;
		else if (player.getY() > toTile.getY())
			direction = Direction.SOUTH;
		player.lock();
		player.setNextAnimation(new Animation(animId));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 2, direction));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextTile(toTile);
				player.unlock();
			}
		}, 1);
	}

	public static void sidestep(final Player player, Tile toTile) {
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				player.lock();
				ticks++;
				if (ticks == 1) {
					player.setNextAnimation(new Animation(3844));
					player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 3, Utils.getAngleTo(toTile.getX() - player.getX(), toTile.getY() - player.getY())));
				} else if (ticks == 4) {
					player.setNextTile(toTile);
					player.unlock();
					stop();
				}
			}
		}, 0, 0);
	}

	public static void crawlUnder(final Player player, Tile toTile) {
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				player.lock();
				ticks++;
				if (ticks == 1) {
					player.setNextAnimation(new Animation(2589));
					player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 4, Utils.getAngleTo(toTile.getX() - player.getX(), toTile.getY() - player.getY())));
				} else if (ticks == 4) {
					player.setNextAnimation(new Animation(2591));
					player.setNextTile(toTile);
					player.unlock();
					stop();
				}
			}
		}, 0, 0);
	}

	public static void walkLog(final Player player, Tile toTile, int delay) {
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock(delay);
		player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
		player.sendMessage("You walk carefully across the slippery log...", true);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearance().setBAS(155);
				} else {
					player.getAppearance().setBAS(-1);
					player.setRunHidden(running);
					player.getSkills().addXp(Skills.AGILITY, 7.5);
					player.sendMessage("... and make it safely to the other side.", true);
					stop();
				}
			}
		}, 0, delay);
	}
}
