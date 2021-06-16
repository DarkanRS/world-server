package com.rs.game.player.content.skills.crafting;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class SnakeskinCrafting  {

	private static Item[][] materials = { { new Item(6287, 6), new Item(1734) }, { new Item(6287, 8), new Item(1734) }, { new Item(6287, 5), new Item(1734) }, { new Item(6287, 12), new Item(1734) }, { new Item(6287, 15), new Item(1734) } };
	private static Item[][] products = { { new Item(6328) }, { new Item(6330) }, { new Item(6326) }, { new Item(6324) }, { new Item(6322) } };
	private static int[] reqs = { 45, 47, 48, 51, 53 };
	private static double[] xp = { 30, 35, 45, 50, 55 };
	private static int[] anims = { -1, -1, -1, -1, -1 };
	
	public static ItemOnItemHandler craft = new ItemOnItemHandler(6287, 1733) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
		}
	};
}
