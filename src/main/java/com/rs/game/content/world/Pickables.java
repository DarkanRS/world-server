package com.rs.game.content.world;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Pickables {

	public static ObjectClickHandler handle = new ObjectClickHandler(new Object[] { "Flax", "Cabbage", "Potato", "Wheat", "Onion" }, e -> {
		if (e.getOption().equals("Pick")) {
			switch (e.getObject().getDefinitions(e.getPlayer()).getName()) {
			case "Flax" -> pick(e.getPlayer(), e.getObject(), 1779);
			case "Cabbage" -> pick(e.getPlayer(), e.getObject(), 1965);
			case "Potato" -> pick(e.getPlayer(), e.getObject(), 1942);
			case "Wheat" -> pick(e.getPlayer(), e.getObject(), 1947);
			case "Onion" -> pick(e.getPlayer(), e.getObject(), 1957);
			}
		}
	});
	
	public static void pick(Player player, GameObject object, int itemId) {
		if (player.getInventory().addItem(itemId, 1)) {
			player.setNextAnimation(new Animation(827));
			player.lock(2);
			if (itemId != 1779 || Utils.random(5) == 0)
				World.removeObjectTemporary(object, Ticks.fromMinutes(1));
		}
	}
	
}
