package com.rs.game.player.content.skills.crafting;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class GlassBlowing  {

	public static int MOLTEN_GLASS = 1775;
	public static int ROBUST_GLASS = 23193;
	public static int GLASSBLOWING_PIPE = 1785;

	private static Item[][] materials = { { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(MOLTEN_GLASS) }, { new Item(ROBUST_GLASS) } };
	private static Item[][] products = { { new Item(1919) }, { new Item(4527) }, { new Item(4522) }, { new Item(229) }, { new Item(6667) }, { new Item(567) }, { new Item(4542) }, { new Item(10980) }, { new Item(23191) } };
	private static int[] reqs = { 1, 4, 12, 33, 42, 46, 49, 87, 89 };
	private static double[] xp = { 17.5, 19, 25, 35, 42.5, 52.5, 55, 70, 100 };
	private static int[] anims = { 884, 884, 884, 884, 884, 884, 884, 884, 884 };
	
	public static ItemOnItemHandler blowGlass = new ItemOnItemHandler(GLASSBLOWING_PIPE, new int[] { MOLTEN_GLASS, ROBUST_GLASS }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
		}
	};

}
