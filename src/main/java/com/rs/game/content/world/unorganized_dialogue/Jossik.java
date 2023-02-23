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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.prayer.PrayerBooks;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Jossik extends Conversation {
	
	private static final int JOSSIK = 1334;
	
	public static NPCClickHandler handle = new NPCClickHandler(new Object[] { 1334 }, e -> {
		if (e.getOption().contains("Talk"))
			e.getPlayer().startConversation(new Jossik(e.getPlayer()));
		else if (e.getOption().contains("Trade"))
			ShopsHandler.openShop(e.getPlayer(), "book_shop");
	});

	public Jossik(Player player) {
		super(player);
		
		addNPC(JOSSIK, HeadE.CHEERFUL, "Hello again, adventurer. What brings you this way?");
		addOptions(ops -> {
			ops.add("Can I see your wares?")
				.addPlayer(HeadE.CONFUSED, "Can I see your wares?")
				.addNPC(JOSSIK, HeadE.CHEERFUL_EXPOSITION, "Sure thing! I think you'll agree, my prices are remarkable.")
				.addNext(() -> ShopsHandler.openShop(player, "the_lighthouse_store"));
			ops.add("Have you found any new prayer books?")
				.addNext(() -> {
					for (int i = 0; i < player.getPrayerBook().length; i++) {
						if (player.getPrayerBook()[i] && !player.containsOneItem(PrayerBooks.BOOKS[i] + 1)) {
							player.npcDialogue(JOSSIK, HeadE.CHEERFUL, "As a matter of fact I did! I found a " + ItemDefinitions.getDefs(PrayerBooks.BOOKS[i] + 1).getName() + ", and I recognised it as yours immediately! Here you go!");
							player.getInventory().addItem(PrayerBooks.BOOKS[i] + 1, 1);
							return;
						}
					}
					ShopsHandler.openShop(player, "book_shop");
				});
			ops.add("Nevermind.");
		});
	}
}
