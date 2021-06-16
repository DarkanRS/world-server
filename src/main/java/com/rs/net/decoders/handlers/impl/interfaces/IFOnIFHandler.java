package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Alchemy;
import com.rs.game.player.content.skills.magic.Enchanting;
import com.rs.game.player.content.skills.magic.Lunars;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnIF;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;

public class IFOnIFHandler implements PacketHandler<Player, IFOnIF> {
	
	@Override
	public void handle(Player player, IFOnIF packet) {
		if (packet.getFromInter() == 192 && packet.getToInter() == 679) {
			Item item = player.getInventory().getItem(packet.getToSlot());
			if (item == null)
				return;
			switch (packet.getFromComp()) {
			case 59:
				Alchemy.handleAlchemy(player, item, false, true);
				break;
			case 38:
				Alchemy.handleAlchemy(player, item, true, true);
				break;
			case 50:
				Alchemy.handleSuperheat(player, item, true);
				break;
			case 29:
			case 41:
			case 53:
			case 61:
			case 76:
			case 88:
				Enchanting.handleEnchanting(player, item, packet.getFromComp());
			default:
				if (player.hasRights(Rights.DEVELOPER)) {
					player.sendMessage("Unhandled spell: fromComp: " + packet.getFromComp() + " slotId: " + packet.getToSlot());
				}
				break;
			}
			return;
		}

		if (packet.getFromInter() == 430 && packet.getToInter() == 679) {
			Item item = player.getInventory().getItem(packet.getToSlot());
			if (item == null)
				return;
			switch (packet.getFromComp()) {
			case 33:
				Lunars.handlePlankMake(player, item);
				break;
			case 50:
				Lunars.handleRestorePotionShare(player, item);
				break;
			case 72:
				Lunars.handleLeatherMake(player, item);
				break;
			case 49:
				Lunars.handleBoostPotionShare(player, item);
				break;
			default:
				if (player.hasRights(Rights.DEVELOPER)) {
					player.sendMessage("Unhandled lunar spell: fromComp: " + packet.getFromComp() + " slotId: " + packet.getToSlot());
				}
				break;
			}
			return;
		}

		if (packet.getFromInter() == 747 && packet.getToInter() == 679) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(packet.getToSlot());
				}
			}
			return;
		}
		if (packet.getFromInter() == Inventory.INVENTORY_INTERFACE && packet.getFromInter() == packet.getToInter() && !player.getInterfaceManager().containsInventoryInter()) {
			if (packet.getToSlot() >= 28 || packet.getFromSlot() >= 28)
				return;
			Item usedWith = player.getInventory().getItem(packet.getToSlot());
			Item itemUsed = player.getInventory().getItem(packet.getFromSlot());
			if (itemUsed == null || usedWith == null || itemUsed.getId() != packet.getFromItemId() || usedWith.getId() != packet.getToItemId())
				return;
			if (packet.getFromSlot() == packet.getToSlot())
				return;
			player.stopAll();
			if (!InventoryOptionsHandler.handleItemOnItem(player, itemUsed, usedWith, packet.getFromSlot(), packet.getToSlot()))
				player.sendMessage("Nothing interesting happens.");
		}
	}

}
