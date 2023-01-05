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
package com.rs.game.content.world.npcs.portPhasmatys;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;


@PluginEventHandler
public class GhostShopkeeper extends Conversation {
	private static int npcId = 1699;


	public static NPCClickHandler GhostShopkeeper = new NPCClickHandler(new Object[]{npcId}) {
		@Override
		//Handle Right-Click
		public void handle(NPCClickEvent e) {
			switch (e.getOption()) {
				//Start Conversation
				case "Talk-to" -> e.getPlayer().startConversation(new GhostShopkeeper(e.getPlayer()));
				case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "port_phasmatys_general_store");
			}
		}
	};

	public boolean GhostEquipped() {
		int neckId = player.getEquipment().getNeckId();
		if (neckId == -1)
			return false;
		return ItemDefinitions.getDefs(neckId).getName().contains("Ghostspeak");
	};

	public GhostShopkeeper(Player player) {
		super(player);
		if (GhostEquipped())
		{
		addNPC(npcId, HeadE.SECRETIVE, "Would you like to buy or sell anything?");
		addOptions(new Options() {
			@Override
			public void create() {

				option("I'd like to see what you have for sale.", new Dialogue()
						.addNext(() -> {
							ShopsHandler.openShop(player, "port_phasmatys_general_store");
						}));

				option("No thanks.", new Dialogue()
						.addPlayer(HeadE.CONFUSED, "No thanks.")
				);
			}


		});
	}
		else {
			addNPC(npcId,HeadE.FRUSTRATED,"Woooo wooo wooooo woooo");
			create();
			player.sendMessage("You cannot understand the ghost.");
		};
	}
}

