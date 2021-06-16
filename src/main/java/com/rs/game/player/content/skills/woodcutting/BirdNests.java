package com.rs.game.player.content.skills.woodcutting;

import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class BirdNests {
	
	public static ItemClickHandler handleBirdNests = new ItemClickHandler(5070, 5071, 5072, 11966, 5073, 5074, 7413) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You don't have enough inventory space.");
				return;
			}
			e.getPlayer().incrementCount("Nests searched");
			e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
			e.getPlayer().getInventory().addItem(5075, 1);
			switch(e.getItem().getId()) {
			case 5070:
				e.getPlayer().getInventory().addItem(5076, 1, true);
				break;
			case 5071:
				e.getPlayer().getInventory().addItem(5078, 1, true);
				break;
			case 5072:
				e.getPlayer().getInventory().addItem(5077, 1, true);
				break;
			case 11966:
				e.getPlayer().getInventory().addItem(11964, 1, true);
				break;
			case 5073:
				for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_tree_seed")))
					e.getPlayer().getInventory().addItem(rew);
				break;
			case 5074:
				for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_ring")))
					e.getPlayer().getInventory().addItem(rew);
				break;
			case 7413:
				for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_shit_seed")))
					e.getPlayer().getInventory().addItem(rew);
				break;
			}
		}
	};

}
