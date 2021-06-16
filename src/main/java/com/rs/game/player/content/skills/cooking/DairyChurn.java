package com.rs.game.player.content.skills.cooking;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DairyChurn  {
	private static Item[][] materials = { { new Item(1927) }, { new Item(1927) }, { new Item(1927) } };
	private static Item[][] products = { { new Item(2130), new Item(3727) }, { new Item(6697), new Item(3727) }, { new Item(1985), new Item(3727) } };
	private static int[] reqs = { 21, 38, 48 };
	private static double[] xp = { 18, 40.5, 64 };
	private static int[] anims = { -1, -1, -1 };
	
	public static ObjectClickHandler handleChurns = new ObjectClickHandler(new Object[] { "Dairy churn", "Dairy Churn" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.COOKING, 8));
		}
	};
}
