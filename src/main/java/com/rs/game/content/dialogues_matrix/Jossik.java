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
package com.rs.game.content.dialogues_matrix;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.prayer.PrayerBooks;
import com.rs.utils.shop.ShopsHandler;

public class Jossik extends MatrixDialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello again, adventurer. What brings you this way?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Can I see your wares?", "Have you found any new prayer books?");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(9827, "Can I see your wares?");
			} else {
				for (int i = 0; i < player.getPrayerBook().length; i++)
					if (player.getPrayerBook()[i] && !player.containsOneItem(PrayerBooks.BOOKS[i] + 1)) {
						sendNPCDialogue(npcId, 9827, "As a matter of fact I did! I found a " + ItemDefinitions.getDefs(PrayerBooks.BOOKS[i] + 1).getName() + ", and I recognised it as yours immediately! Here you go!");
						player.getInventory().addItem(PrayerBooks.BOOKS[i] + 1, 1);
						stage = -2;
						return;
					}
				ShopsHandler.openShop(player, "book_shop");
				end();
			}
		} else if (stage == 1) {
			stage = 2;
			sendNPCDialogue(npcId, 9827, "Sure thing! I think you'll agree, my prices are remarkable.");
		} else if (stage == 2) {
			ShopsHandler.openShop(player, "the_lighthouse_store");
			end();
		} else
			end();

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
