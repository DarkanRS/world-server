package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.KeyPress;

public class KeyPressHandler implements PacketHandler<Player, KeyPress> {

	@Override
	public void handle(Player player, KeyPress packet) {
		player.refreshIdleTime();
		if (packet.getKeyCode() == 13) {
            player.closeInterfaces();
            if (player.getInterfaceManager().containsInterface(755)) {//World map
                //Send window pane
                player.getPackets().sendWindowsPane(player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, 2);

                //Reset top of interface stack on client
                player.getInterfaceManager().setDefaultTopInterface();
            }
        }
	}

}
