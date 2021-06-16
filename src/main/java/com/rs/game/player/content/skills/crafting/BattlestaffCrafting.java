package com.rs.game.player.content.skills.crafting;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class BattlestaffCrafting  {

	private static Item[][] materials = { { new Item(1391), new Item(571) }, { new Item(1391), new Item(575) }, { new Item(1391), new Item(569) }, { new Item(1391), new Item(573) } };
	private static Item[][] products = { { new Item(1395) }, { new Item(1399) }, { new Item(1393) }, { new Item(1397) } };
	private static int[] reqs = { 54, 58, 62, 66 };
	private static double[] xp = { 100, 112.5, 125, 137.5 };
	private static int[] anims = { 16448, 16447, 16449, 16446 };
	
	public static ItemOnItemHandler craftStaves = new ItemOnItemHandler(1391, new int[] { 569, 571, 573, 575 }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
		}
	};
}