package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFContinue;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;

public class IFContinueHandler implements PacketHandler<Player, IFContinue> {

	@Override
	public void handle(Player player, IFContinue packet) {
		if (Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId() || !player.isRunning() || !player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;
		if (player.getTempAttribs().get("pluginOption") != null && player.getTempAttribs().remove("pluginOption") instanceof DialogueOptionEvent doe) {
			doe.setOption(packet.getComponentId() == 11 ? 1 : packet.getComponentId()-11);
			if (player.getInterfaceManager().containsChatBoxInter())
				player.getInterfaceManager().closeChatBoxInterface();
			doe.run(player);
			return;
		}
		if (player.getConversation() != null) {
			player.getConversation().process(packet.getInterfaceId(), packet.getComponentId());
			return;
		}
		player.getDialogueManager().continueDialogue(packet.getInterfaceId(), packet.getComponentId());
		PluginManager.handle(new ButtonClickEvent(player, packet.getInterfaceId(), packet.getComponentId(), -1, -1, ClientPacket.IF_CONTINUE));
	}

}
