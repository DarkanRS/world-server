package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.Shop;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFDragOntoIF;
import com.rs.lib.util.Utils;

public class IFDragOntoIFHandler implements PacketHandler<Player, IFDragOntoIF> {

	@Override
	public void handle(Player player, IFDragOntoIF packet) {
		if (Utils.getInterfaceDefinitionsSize() <= packet.getFromInter() || Utils.getInterfaceDefinitionsSize() <= packet.getToInter())
			return;
		if (!player.getInterfaceManager().containsInterface(packet.getFromInter()) || !player.getInterfaceManager().containsInterface(packet.getToInter()))
			return;
		if (packet.getFromComp() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getFromInter()) <= packet.getFromComp())
			return;
		if (packet.getToComp() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getToInter()) <= packet.getToComp())
			return;
		
		if (packet.getFromInter() == Inventory.INVENTORY_INTERFACE && packet.getFromComp() == 0 && packet.getToInter() == Inventory.INVENTORY_INTERFACE && packet.getToComp() == 0) {
			if (packet.getToSlot()-28 < 0 || packet.getToSlot()-28 >= player.getInventory().getItemsContainerSize() || packet.getFromSlot() >= player.getInventory().getItemsContainerSize())
				return;
			player.getInventory().switchItem(packet.getFromSlot(), packet.getToSlot()-28);
		} else if (packet.getFromInter() == 763 && packet.getFromComp() == 0 && packet.getToInter() == 763 && packet.getToComp() == 0) {
			if (packet.getToSlot() >= player.getInventory().getItemsContainerSize() || packet.getFromSlot() >= player.getInventory().getItemsContainerSize())
				return;
			player.getInventory().switchItem(packet.getFromSlot(), packet.getToSlot());
		} else if (packet.getFromInter() == 762 && packet.getToInter() == 762) {
			player.getBank().switchItem(packet.getFromSlot(), packet.getToSlot(), packet.getFromComp(), packet.getToComp());
		} else if (packet.getFromInter() == 1265 && packet.getToInter() == 1266 && player.getTempAttribs().get("shop_buying") != null) {
			if ((boolean) player.getTempAttribs().get("shop_buying") == true) {
				Shop shop = (Shop) player.getTempAttribs().get("Shop");
				if (shop == null)
					return;
				shop.buy(player, packet.getFromSlot(), 1);
			}
		} else if (packet.getFromInter() == 34 && packet.getToInter() == 34)
			player.getNotes().switchNotes(packet.getFromSlot(), packet.getToSlot());
	}

}
