package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ResumeCountDialogue;
import com.rs.plugin.events.InputIntegerEvent;

public class ResumeCountDialogueHandler implements PacketHandler<Player, ResumeCountDialogue> {

	@Override
	public void handle(Player player, ResumeCountDialogue packet) {
		if (!player.isRunning() || player.isDead())
			return;
		if (player.getInterfaceManager().containsInterface(671) && player.getInterfaceManager().containsInterface(665)) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
				return;
			if (packet.getValue() < 0)
				return;
			if (player.getTempAttribs().removeB("bob_isRemove"))
				player.getFamiliar().getBob().removeItem(player.getTempAttribs().removeI("bob_item_X_Slot", 0), packet.getValue());
			else
				player.getFamiliar().getBob().addItem(player.getTempAttribs().removeI("bob_item_X_Slot", 0), packet.getValue());
		} else if (player.getInterfaceManager().containsInterface(335) && player.getInterfaceManager().containsInterface(336)) {
			if (packet.getValue() < 0)
				return;
			if (player.getTempAttribs().removeB("trade_isRemove"))
				player.getTrade().removeItem(player.getTempAttribs().removeI("trade_item_X_Slot", 0), packet.getValue());
			else
				player.getTrade().addItem(player.getTempAttribs().removeI("trade_item_X_Slot", 0), packet.getValue());
		} else {
			if (player.getTempAttribs().getO("pluginInteger") != null && player.getTempAttribs().removeO("pluginInteger") instanceof InputIntegerEvent iie)
				iie.run(packet.getValue());
			return;
		}
	}

}
