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
package com.rs.game.player.content.transportation;

import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;

public final class FadingScreen {

	public static void fade(final Player player, int ticks, final Runnable event) {
		unfade(player, fade(player, ticks), event);
	}

	public static void fade(final Player player, final Runnable event) {
		unfade(player, fade(player), event);
	}

	public static void unfade(final Player player, int ticks, final Runnable event) {
		unfade(player, 4, ticks, event);
	}

	public static void unfade(final Player player, int startDelay, int delay, final Runnable event) {
		int leftTime = startDelay + delay;
		if (startDelay > 0)
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					try {
						unfade(player, event);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, leftTime);
		else
			unfade(player, event);
	}

	public static void unfade(final Player player, Runnable event) {
		event.run();
		player.getInterfaceManager().setFadingInterface(170);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					player.getInterfaceManager().closeFadingInterface();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 4);
	}

	public static int fade(Player player, int fadeTicks) {
		player.getInterfaceManager().setFadingInterface(115);
		return fadeTicks;
	}

	public static int fade(Player player) {
		return fade(player, 0);
	}
}
