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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Lilly extends Conversation {
	private static final int npcId = 4294;

	public static NPCClickHandler Lilly = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch (e.getOption()) {
		
		case "Talk-to" -> e.getPlayer().startConversation(new Lilly(e.getPlayer()));
		case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "warrior_guild_potion_shop");
		}
	});

	public Lilly(Player player) {
		super(player);
		addNPC(npcId, HeadE.SECRETIVE, "Uh..... hi... didn't see you there. Can.... I help?");
		addPlayer(HeadE.CONFUSED, "Umm... do you sell potions?");
		addNPC(npcId, HeadE.SECRETIVE, "Erm... yes. When I'm not drinking them.");
		addOptions(new Options() {
			@Override
			public void create() {

				option("I'd like to see what you have for sale.", new Dialogue()
						.addNext(() -> {
							ShopsHandler.openShop(player, "warrior_guild_potion_shop");
						}));

				option("That's a pretty wall hanging.", new Dialogue()
						.addPlayer(HeadE.CONFUSED, "That's a pretty wall hanging.")
						.addPlayer(HeadE.HAPPY_TALKING, "That's a pretty wall hanging.")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "Do you think so? I made it myself.")
						.addPlayer(HeadE.CONFUSED, "Really? Is that why there's all this cloth and dye around?")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "Yes, it's a hobby of mine when I'm.... relaxing.")
				);

				option("Bye.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Bye.")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "Have fun and come back soon!")
				);

			}


		});
	}
}
