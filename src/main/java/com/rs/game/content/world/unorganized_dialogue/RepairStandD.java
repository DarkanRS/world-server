package com.rs.game.content.world.unorganized_dialogue;

import com.rs.game.content.ItemConstants;
import com.rs.engine.dialogue.Conversation;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class RepairStandD extends Conversation {
	public RepairStandD(Player player, ItemConstants.ItemDegrade details, Item item, final boolean stand, final int slot) {
		super(player);
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().setIFText(1183, 12, "It will cost " + (stand ? details.getRepairStandCost(player) : details.getCost(item)) + " to repair your " + item.getDefinitions().getName() + ".");
		player.getPackets().setIFItem(1183, 13, details.getItemId(), 1);
		player.getPackets().setIFText(1183, 7, "Repair this item fully for " + (stand ? details.getRepairStandCost(player) : details.getCost(item)) + " coins?");
		player.getPackets().setIFText(1183, 22, "Confirm repair");
		player.save("repairDetails", details);
		player.save("repairItem", item);
		player.save("repairStand", stand);
		player.save("repairSlot", slot);
	}

	public static ButtonClickHandler handleRepair = new ButtonClickHandler(1183, e -> {
		ItemConstants.ItemDegrade details = e.getPlayer().getO("repairDetails");
		Item item = e.getPlayer().getO("repairItem");
		boolean stand = e.getPlayer().getO("repairStand");
		int slot = e.getPlayer().getO("repairSlot");
		if (e.getComponentId() == 9) {
			if (e.getPlayer().getInventory().hasCoins(stand ? details.getRepairStandCost(e.getPlayer()) : details.getCost(item))) {
				if (e.getPlayer().getInventory().getItem(slot) == null || e.getPlayer().getInventory().getItem(slot).getId() != item.getId())
					return;
				e.getPlayer().getInventory().getItems().set(slot, new Item(details.getItemId(), 1));
				e.getPlayer().getInventory().removeCoins(stand ? details.getRepairStandCost(e.getPlayer()) : details.getCost(item));
				e.getPlayer().getInventory().refresh();
			} else
				e.getPlayer().sendMessage("You don't have enough coins.");
		}
		e.getPlayer().closeInterfaces();
	});
}
