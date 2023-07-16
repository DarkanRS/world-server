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
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class AkHaranu extends Conversation {
	private static final int npcId = 1687;

	public static NPCClickHandler AkHaranu = new NPCClickHandler(new Object[] { npcId }, e -> {
		switch (e.getOption()) {
			case "Talk-To" -> e.getPlayer().startConversation(new AkHaranu(e.getPlayer()));
			case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "akharanus_exotic_shop");
		}
	});

	public AkHaranu(Player player) {
		super(player);
		player.startConversation(new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "It's nice to see a human face around here.")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "My name is Ak-Haranu. I am trader, come from many far across sea in east.")
				.addPlayer(HeadE.HAPPY_TALKING, "You come from the lands of the East?")
				/* Ghosts Ahoy
				.addPlayer(HeadE.HAPPY_TALKING, "You come from the lands of the East? Do you have anything that can help me translate a book that is scribed in your language?")
				.addNPC(npcId, HeadE.SECRETIVE, "Ak-Haranu may help you. A translation manual I have. Much good for reading Eastern language.")
				.addPlayer(HeadE.HAPPY_TALKING, "How much do you want for it?")
				.addNPC(npcId, HeadE.SECRETIVE, "Ak-Haranu not want money for this book, as is such small thing. But there may be something you could do for Ak-Haranu.")
				.addNPC(npcId, HeadE.SECRETIVE, "I am big admirer of Robin, Master Bowman. He staying in village inn.")
				.addPlayer(HeadE.HAPPY_TALKING, "What would you like me to do?")
				.addNPC(npcId, HeadE.SECRETIVE, "Please get Master Bowman sign an oak longbow for me. So Ak-Haranu can show family and friends when returning home and become much admired. Then I give book in exchange.")
				.addPlayer(HeadE.HAPPY_TALKING, "Ok, wait here - I'll get you your bow.")
				 */
				.addNPC(npcId, HeadE.HAPPY_TALKING, "Yes, Would you like buy Eastern gifts?")
				.addOptions(ops -> {
					ops.add("I'd like to see what you have for sale.", () -> ShopsHandler.openShop(player, "akharanus_exotic_shop"));
					ops.add("No thanks.")
							.addPlayer(HeadE.CONFUSED, "No thanks.");
				}));
	}
}

