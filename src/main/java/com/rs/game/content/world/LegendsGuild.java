package com.rs.game.content.world;

import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class LegendsGuild {

	public static ItemOnObjectHandler handleFountainOfHeroes = new ItemOnObjectHandler(new Object[] { 2938 }, null, e -> {
		if (isSkillsNeckOrCombatBrace(e.getItem().getId()) && e.getItem().getName().toLowerCase().indexOf("(4)") < 0) {
			for (Item item : e.getPlayer().getInventory().getItems().array())
				if (item != null)
					if (isSkillsNeckOrCombatBrace(item.getId())) {
						int fullyChargedItemId = getChargedId(item.getId());
						if (fullyChargedItemId != -1) {
							e.getPlayer().getInventory().replace(item, new Item(fullyChargedItemId));
							e.getPlayer().setNextAnimation(new Animation(899));
						}
					}
			e.getPlayer().sendMessage("You recharge some jewelry at the totem.");
		}
	});

	public static boolean isSkillsNeckOrCombatBrace(int itemId) {
		if ((itemId == 11126) || (itemId == 11124) || (itemId == 11122) || (itemId == 11120) || (itemId == 11118) || (itemId == 11113) || (itemId == 11111) || (itemId == 11109) || (itemId == 11107) || (itemId == 11105))
			return true;
		return false;
	}

	public static int getChargedId(int itemId) {
		//Combat Bracelet
		if ((itemId == 11126) || (itemId == 11124) || (itemId == 11122) || (itemId == 11120))
			return 11118;

		//Skills Necklace
		if ((itemId == 11113) || (itemId == 11111) || (itemId == 11109) || (itemId == 11107))
			return 11105;

		return -1;
	}
}
