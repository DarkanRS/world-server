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
package com.rs.game.content.transportation;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class SpiritTree {

	private static final int TREE_INTERFACE = 864;
	private static final Tile[] TELEPORTS = {
			Tile.of(2554, 3255, 0),
			Tile.of(3187, 3507, 0),
			Tile.of(2416, 2852, 0),
			Tile.of(2339, 3108, 0),
			Tile.of(2541, 3170, 0),
			Tile.of(2462, 3445, 0)
	};
	
	public static ObjectClickHandler handleTrees = new ObjectClickHandler(new Object[] { "Spirit Tree", "Spirit tree", 26723 }, e -> {
		String op = e.getOption().toLowerCase();
		if (op.contains("talk")) {
			e.getPlayer().startConversation(new Dialogue()
					.addNPC((e.getObjectId() == 68973 && e.getObjectId() == 68974) ? 3637 : 3636, HeadE.CALM_TALK, "If you are a friend of the gnome people, you are a friend of mine. Do you wish to travel?")
					.addOptions(ops -> {
						ops.add("Yes, please.", () -> SpiritTree.openInterface(e.getPlayer()));
						ops.add("No, thanks.");
					}));
		} else if (op.contains("travel") || op.contains("teleport")) {
			SpiritTree.openInterface(e.getPlayer());
		}
	});

	public static void openInterface(Player player) {
		player.getVars().setVarBit(3959, 3);
		player.getInterfaceManager().sendInterface(TREE_INTERFACE);
		player.getPackets().setIFRightClickOps(TREE_INTERFACE, 6, 0, 7, 0);
		if (player.getRegionId() == 10033 || player.getRegionId() == 12102)
			player.getVars().setVar(1469, 0x27b8c61);
		else if (player.getRegionId() == 9781)
			player.getVars().setVar(1469, 0x2678d74);
		else
			sendTeleport(player, TELEPORTS[4]);
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(864, e -> handleSpiritTree(e.getPlayer(), e.getSlotId()));

	private static void sendTeleport(Player player, Tile tile) {
		player.sendMessage("You place your hands on the dry tough bark of the spirit tree, and feel a surge of energy run through your veins.");
		Magic.sendTeleportSpell(player, 7082, 7084, 1229, 1229, 1, 0, tile, 4, true, Magic.OBJECT_TELEPORT, null);
	}

	public static void handleSpiritTree(Player player, int slot) {
		if (slot == 0)
			if (player.getRegionId() == 10033)
				slot = 6;
			else
				slot = 5;
		sendTeleport(player, TELEPORTS[slot - 1]);
	}
}
