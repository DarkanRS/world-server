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
package com.rs.game.content.world.areas.rimmington.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Rommik extends Conversation {
	private static final int npcId = 585;

	public static NPCClickHandler Rommik = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
		//Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new Rommik(e.getPlayer()));
		case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "rommiks_crafting_shop");
		}
	});

	public Rommik(Player player) {
		super(player);
		addNPC(npcId, HeadE.HAPPY_TALKING, "Would you like to buy some Crafting equipment?");
		addOptions(new Options() {
			@Override
			public void create() {

				option("Let's see what you've got then..", new Dialogue()
						.addNext(() -> {
							ShopsHandler.openShop(player, "rommiks_crafting_shop");
						}));
				option("No thanks, I've got all the crafting equipment I need.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "No thanks, I've got all the crafting equipment I need.")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "Okay. Fare well on your travels.")
				);

			}


		});
	}
}
