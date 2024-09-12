package com.rs.game.content.world.areas.global;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Ladders {
	public static ObjectClickHandler world6400Up = new ObjectClickHandler(new Object[] { 4781, 29355 }, e -> e.getPlayer().useStairs(828, e.getPlayer().transform(0, -6400), 1, 1));
	public static ObjectClickHandler world6400Down = new ObjectClickHandler(new Object[] { 1754, 1759, 4780 }, e -> e.getPlayer().useStairs(827, e.getPlayer().transform(0, 6400), 1, 1));
	public static ObjectClickHandler world2UP = new ObjectClickHandler(new Object[] { 11739 }, e -> e.getPlayer().useStairs(828, e.getPlayer().transform(0, -2, 1)));
	public static ObjectClickHandler world2DOWN = new ObjectClickHandler(new Object[] { 11741 }, e -> e.getPlayer().useStairs(828, e.getPlayer().transform(0, 2, -1)));

	public static ObjectClickHandler ladders = new ObjectClickHandler(new Object[] { "Ladder", "Bamboo ladder" }, e -> handleLadder(e.getPlayer(), e.getObject(), e.getOption()));

	private static void handleLadder(Player player, GameObject object, String option) {
		switch (option.toLowerCase()) {
			case "climb-up", "climb up" -> {
				if (player.getPlane() == 3) return;
				player.useStairs(828, Tile.of(player.getX(), player.getY(), player.getPlane() + 1));
			}
			case "climb-down", "climb down" -> {
				if (player.getPlane() == 0) return;
				player.useStairs(828, Tile.of(player.getX(), player.getY(), player.getPlane() - 1));
			}
			case "climb" -> {
				if (player.getPlane() == 3 || player.getPlane() == 0) return;
				player.promptUpDown(
					828,
					"Climb up the ladder.",
					player.transform(0, 0, 1),
					"Climb down the ladder.",
					player.transform(0, 0, -1)
				);
			}
		}
	}
}
