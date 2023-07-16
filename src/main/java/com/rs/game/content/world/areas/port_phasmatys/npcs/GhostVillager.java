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
package com.rs.game.content.world.areas.port_phasmatys.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GhostVillager extends Conversation {
	private static final int npcId = 1697;

	public static NPCClickHandler GhostVillager = new NPCClickHandler(new Object[] { npcId }, e -> {
		switch (e.getOption()) {
		case "Talk-To" -> e.getPlayer().startConversation(new GhostVillager(e.getPlayer()));
		}
	});

	public GhostVillager(Player player) {
		super(player);
		if (player.getEquipment().getNeckId() != 552) {
			addNPC(npcId,HeadE.FRUSTRATED,"Woooo wooo wooooo woooo");
			create();
			player.sendMessage("You cannot understand the ghost.");
			return;
		}
		switch (Utils.random(1, 5)) {
		case 1 -> addNPC(npcId,HeadE.CALM, "What do you want, mortal?");
		case 2 -> addNPC(npcId,HeadE.CALM, "We do not talk to the warm-bloods.");
		case 3 -> addNPC(npcId,HeadE.CALM, "Why did we have to listen to that maniacal priest?");
		case 4 -> addNPC(npcId,HeadE.CALM, "This cold wind blows right through you, doesn't it?");
		case 5 -> addNPC(npcId,HeadE.CALM, "Worship the Ectofuntus all you want, but don't bother us, human.");
		}
		create();
	}
}

