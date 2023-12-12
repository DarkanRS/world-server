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

import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

public class AgilityShortcuts {
	public static void climbOver(Player player, Tile toTile) {
		climbOver(player, toTile, 839);
	}

	public static void climbOver(Player player, Tile toTile, int animId) {
		player.forceMove(toTile, animId, 33, AnimationDefinitions.getDefs(animId).getEmoteTime() / 20 - 5);
	}

	public static void sidestep(final Player player, Tile toTile) {
		player.lock();
		WorldTasks.schedule(1, () -> player.forceMove(toTile, 3844, 0, 120));
	}

	public static void crawlUnder(final Player player, Tile toTile) {
		player.lock();
		WorldTasks.schedule(1, () -> player.forceMove(toTile, 2589, 35, 130));
		WorldTasks.schedule(4, () -> player.setNextAnimation(new Animation(2591)));
	}

	public static void walkLog(final Player player, Tile toTile, int delay) {
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock(delay);
		player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
		player.sendMessage("You walk carefully across the slippery log...", true);
		WorldTasks.schedule(new Task() {
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
