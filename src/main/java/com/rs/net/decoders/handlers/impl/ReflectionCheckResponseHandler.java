package com.rs.net.decoders.handlers.impl;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ReflectionCheckResponse;
import com.rs.lib.util.ReflectionCheck;

public class ReflectionCheckResponseHandler implements PacketHandler<Player, ReflectionCheckResponse> {

	@Override
	public void handle(Player player, ReflectionCheckResponse packet) {
		ReflectionCheck check = player.getReflectionCheck(packet.getId());
		if (check == null) {
			World.sendWorldMessage("<col=FF0000>" + player.getDisplayName() + " failed reflection check. Reason: Check id not found.", true);
			return;
		}
		if (packet.exists()) {
			if (check.exists()) {
				if (packet.getModifiers().equals(check.getModifiers()))
					World.sendWorldMessage("<col=00FF00>" + player.getDisplayName() + " passed reflection check.", true);
				else {
					World.sendWorldMessage("<col=FF0000>" + player.getDisplayName() + " failed reflection check. Reason: Method modifiers don't match.", true);
					World.sendWorldMessage("<col=FF0000>" + "Expected: \"" + check.getModifiers() + "\" but found: \"" + packet.getModifiers() + "\"", true);
				}
			} else {
				World.sendWorldMessage("<col=FF0000>" + player.getDisplayName() + " failed reflection check. Reason: Method modifiers don't match.", true);
				World.sendWorldMessage("<col=FF0000>" + "Expected: \"" + check.getModifiers() + "\" but found: \"" + packet.getModifiers() + "\"", true);
			}
		} else {
			if (!check.exists())
				World.sendWorldMessage("<col=00FF00>" + player.getDisplayName() + " passed reflection check.", true);
			else
				World.sendWorldMessage("<col=FF0000>" + player.getDisplayName() + " failed reflection check. Reason: Method not found when it should have been.", true);
		}
	}

}
