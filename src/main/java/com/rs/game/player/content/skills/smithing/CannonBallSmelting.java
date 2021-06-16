package com.rs.game.player.content.skills.smithing;

import com.rs.game.player.dialogues.CreateActionD;
import com.rs.game.player.quests.Quest;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class CannonBallSmelting  {

	public static int AMMO_MOULD = 4;
	public static int STEEL_BAR = 2353;

	private static Item[][] mats = { { new Item(2353) } };
	private static Item[][] prods = { { new Item(2, 4) } };
	private static int[] reqs = { 35 };
	private static double[] xp = { 25.6 };
	private static int[] anims = { 3243 }; //827, 899

	public static ItemOnObjectHandler handleCreate = new ItemOnObjectHandler(new Object[] { "Furnace" }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if ((e.getItem().getId() == AMMO_MOULD || e.getItem().getId() == STEEL_BAR) && e.getPlayer().getInventory().containsItem(AMMO_MOULD)) {
				if (e.getPlayer().getQuestManager().isComplete(Quest.DWARF_CANNON)) {
					e.getPlayer().getDialogueManager().execute(new CreateActionD(mats, prods, xp, anims, reqs, Constants.SMITHING, 7));
				} else {
					e.getPlayer().sendMessage("You must complete the Dwarf Cannon quest before smithing cannonballs.");
				}
			}
		}
	};

}
