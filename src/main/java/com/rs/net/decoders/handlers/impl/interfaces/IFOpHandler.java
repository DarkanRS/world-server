package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOp;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ButtonClickEvent;

public class IFOpHandler implements PacketHandler<Player, IFOp> {

	@Override
	public void handle(Player player, IFOp packet) {
		if (packet.getInterfaceId() >= Utils.getInterfaceDefinitionsSize())
			return;
		if (player.isLocked() || player.isDead() || !player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;
		if (packet.getComponentId() != 65535 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId())
			return;
		if (!player.getControllerManager().processButtonClick(packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), packet.getOpcode()))
			return;
		PluginManager.handle(new ButtonClickEvent(player, packet.getInterfaceId(), packet.getComponentId(), packet.getSlotId(), packet.getItemId(), packet.getOpcode()));
	}

}
