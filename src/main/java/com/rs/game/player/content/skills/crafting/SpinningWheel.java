package com.rs.game.player.content.skills.crafting;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class SpinningWheel {
	
	private static Item[][] materials = { { new Item(1779) }, { new Item(10814) }, { new Item(1737) }, { new Item(9436) }, { new Item(3693) } };
	private static Item[][] products = { { new Item(1777) }, { new Item(954) }, { new Item(1759) }, { new Item(9438) }, { new Item(3694) } };
	private static int[] reqs = { 1, 1, 1, 1, 1 };
	private static double[] xp = { 15, 15, 15, 1, 1 };
	private static int[] anims = { 883, 883, 883, 883, 883 };
	
	public static ObjectClickHandler onClick = new ObjectClickHandler(new Object[] { "Spinning wheel" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2)
				e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
		}
	};
	
	public static ItemOnObjectHandler handleItemOn = new ItemOnObjectHandler(new Object[] { "Spinning wheel" }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			for (int i = 0; i < materials.length; i++) {
				if (materials[i][0].getId() == e.getItem().getId()) {
					e.getPlayer().getDialogueManager().execute(new CreateActionD(new Item[][] { { materials[i][0] } }, new Item[][] { { products[i][0] } }, new double[] { xp[i] }, new int[] { anims[i] }, Constants.CRAFTING, 2));
				}
			}
		}
	};
}
