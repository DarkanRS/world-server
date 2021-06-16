package com.rs.game.player.content.minigames.creations;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.controllers.StealingCreationGameController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.util.Logger;

public class StealingCreationManager {

	private static final List<StealingCreationGameController> running = new CopyOnWriteArrayList<StealingCreationGameController>();
	private static WorldTask watcher;

	public synchronized static void createGame(int size, List<Player> blueTeam, List<Player> redTeam) {
		running.add(new StealingCreationGameController(size, blueTeam, redTeam));
		if (watcher == null) {
			WorldTasksManager.schedule(watcher = new WorldTask() {
				@Override
				public void run() {
					try {
						processWatcher();
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 0, 1);
		}
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
