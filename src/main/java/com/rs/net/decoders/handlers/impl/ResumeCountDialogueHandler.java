package com.rs.net.decoders.handlers.impl;

import com.rs.game.grandexchange.GrandExchange;
import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ResumeCountDialogue;
import com.rs.plugin.events.InputIntegerEvent;

public class ResumeCountDialogueHandler implements PacketHandler<Player, ResumeCountDialogue> {

	@Override
	public void handle(Player player, ResumeCountDialogue packet) {
		if (!player.isRunning() || player.isDead())
			return;
		if (player.getInterfaceManager().containsInterface(206) && player.getInterfaceManager().containsInterface(207)) {
			if (packet.getValue() < 0)
				return;
			Integer pc_item_X_Slot = (Integer) player.getTemporaryAttributes().remove("pc_item_X_Slot");
			if (pc_item_X_Slot == null)
				return;
			if (player.getTemporaryAttributes().remove("pc_isRemove") != null)
				player.getPriceCheckManager().removeItem(pc_item_X_Slot, packet.getValue());
			else
				player.getPriceCheckManager().addItem(pc_item_X_Slot, packet.getValue());
		} else if (player.getInterfaceManager().containsInterface(671) && player.getInterfaceManager().containsInterface(665)) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
				return;
			if (packet.getValue() < 0)
				return;
			Integer bob_item_X_Slot = (Integer) player.getTemporaryAttributes().remove("bob_item_X_Slot");
			if (bob_item_X_Slot == null)
				return;
			if (player.getTemporaryAttributes().remove("bob_isRemove") != null)
				player.getFamiliar().getBob().removeItem(bob_item_X_Slot, packet.getValue());
			else
				player.getFamiliar().getBob().addItem(bob_item_X_Slot, packet.getValue());
		} else if (player.getInterfaceManager().containsInterface(105)) {
			if (packet.getValue() < 0)
				return;

			if (player.getTemporaryAttributes().get("geCustomAmount") != null && (Boolean) player.getTemporaryAttributes().remove("geCustomAmount") == Boolean.TRUE) {
				player.geAmount = packet.getValue();
				GrandExchange.updatePrice(player);
			} else if (player.getTemporaryAttributes().get("geCustomPrice") != null && (Boolean) player.getTemporaryAttributes().remove("geCustomPrice") == Boolean.TRUE) {
				player.gePrice = packet.getValue();
				GrandExchange.updatePrice(player);
			}
		} else if (player.getInterfaceManager().containsInterface(335) && player.getInterfaceManager().containsInterface(336)) {
			if (packet.getValue() < 0)
				return;
			Integer trade_item_X_Slot = (Integer) player.getTemporaryAttributes().remove("trade_item_X_Slot");
			if (trade_item_X_Slot == null)
				return;
			if (player.getTemporaryAttributes().remove("trade_isRemove") != null)
				player.getTrade().removeItem(trade_item_X_Slot, packet.getValue());
			else
				player.getTrade().addItem(trade_item_X_Slot, packet.getValue());
		} else {
			if (player.getTemporaryAttributes().get("pluginInteger") != null && player.getTemporaryAttributes().get("pluginInteger") instanceof InputIntegerEvent) {
				Object event = player.getTemporaryAttributes().remove("pluginInteger");
				((InputIntegerEvent) event).run(packet.getValue());
			}
			return;
		}
	}

}
