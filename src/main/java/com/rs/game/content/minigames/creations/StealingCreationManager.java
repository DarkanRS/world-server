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
package com.rs.game.content.minigames.creations;

import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StealingCreationManager {

	private static final List<StealingCreationGameController> running = new CopyOnWriteArrayList<>();
	private static WorldTask watcher;

	public synchronized static void createGame(int size, List<Player> blueTeam, List<Player> redTeam) {
		running.add(new StealingCreationGameController(size, blueTeam, redTeam));
		if (watcher == null)
			WorldTasks.schedule(watcher = new WorldTask() {
				@Override
				public void run() {
					try {
						processWatcher();
					} catch (Throwable e) {
						Logger.handle(StealingCreationManager.class, "createGame", e);
					}
				}
			}, 0, 1);
	}

	/**
	 * Processes watcher thread.
	 */
	private static void processWatcher() {
		for (StealingCreationGameController game : running)
			game.run();
	}

	public static void removeGame(StealingCreationGameController game) {
		running.remove(game);
		if (running.size() == 0) {
			watcher.stop();
			watcher = null;
		}
	}
}
