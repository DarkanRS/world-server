package com.rs.game.player.content.transportation;

import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
		if (startDelay > 0) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					try {
						unfade(player, event);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, leftTime);
		} else
			unfade(player, event);
	}

	public static void unfade(final Player player, Runnable event) {
		event.run();
		player.getInterfaceManager().setFadingInterface(170);
		WorldTasksManager.schedule(new WorldTask() {
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
