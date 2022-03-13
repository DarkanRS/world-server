// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.net.decoders.handlers.impl;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
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
		} else if (!check.exists())
			World.sendWorldMessage("<col=00FF00>" + player.getDisplayName() + " passed reflection check.", true);
		else
			World.sendWorldMessage("<col=FF0000>" + player.getDisplayName() + " failed reflection check. Reason: Method not found when it should have been.", true);
	}

}
