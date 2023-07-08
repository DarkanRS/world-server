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
package com.rs.game.content.world.areas.camp_tyras;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Quatermaster extends Conversation {
	private static final int npcId = 1208;

	public static NPCClickHandler Quatermaster = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
		
		case "Talk-to" -> e.getPlayer().startConversation(new Quatermaster(e.getPlayer()));
		}
	});

	public Quatermaster(Player player) {
		super(player);
		addNPC(npcId, HeadE.HAPPY_TALKING, "Good day " + player.getPronoun("Sir","Miss") +". I'm the quartermaster for King Tyras's camp. We have a little we could trade here. " +
				"We have a new stock of dragon halberds. Would you like a look at what we have now?");
		addOptions(new Options() {
			@Override
			public void create() {

				option("Yes please. What are you selling?", new Dialogue()
						.addNext(() -> {
							ShopsHandler.openShop(player, "quartermasters_stores");
						}));
				option("No, thank you.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "No, thank you.")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "Okay, come back soon.")
				);
			}
		});
	}
}
