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
package com.rs.game.content.world.areas.burthorpe.npcs;

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
public class Lidio extends Conversation {
	private static int npcId = 4293;


	public static NPCClickHandler Lidio = new NPCClickHandler(new Object[]{npcId}) {
		@Override
		//Handle Right-Click
		public void handle(NPCClickEvent e) {
			switch (e.getOption()) {
				//Start Conversation
				case "Talk-to" -> e.getPlayer().startConversation(new Lidio(e.getPlayer()));
				case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "warrior_guild_food_shop");
			}
		}
	};

	public Lidio(Player player) {
		super(player);
		addNPC(npcId, HeadE.HAPPY_TALKING, "Greetings, warrior, how can I fill your stomach today?");
		addOptions(new Options() {
			@Override
			public void create() {

				option("With food preferably.", new Dialogue()
						.addNext(() -> {
							ShopsHandler.openShop(player, "warrior_guild_food_shop");
						}));

				option("I think I'll give it a miss.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "I think I'll give it a miss.")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "Okay, come back soon!")
				);

			}


		});
	}
}
