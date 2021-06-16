package com.rs.game.player.content.skills.crafting;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Weaving  {

	private static Item[][] materials = { { new Item(1759, 4) }, { new Item(5931, 4) }, { new Item(5933, 6) }, { new Item(401, 5), new Item(1794) } };
	private static Item[][] products = { { new Item(3224) }, { new Item(5418) }, { new Item(5376) }, { new Item(14859) } };
	private static int[] reqs = { 10, 21, 36, 52 };
	private static double[] xp = { 12, 38, 56, 83 };
	private static int[] anims = { -1, -1, -1, -1 };
	
	public static ObjectClickHandler onClick = new ObjectClickHandler(new Object[] { "Loom" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
		}
	};
}
