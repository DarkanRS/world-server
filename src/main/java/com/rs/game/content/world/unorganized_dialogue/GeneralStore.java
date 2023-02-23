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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.utils.shop.ShopsHandler;

public class GeneralStore extends Conversation {

	public GeneralStore(Player player, NPC npc, String shopName) {
		super(player);
		
		addNPC(npc.getId(), HeadE.CONFUSED, "Can I help you at all?");
		addOptions(ops -> {
			ops.add("Yes, please. What are you selling?", () -> ShopsHandler.openShop(player, shopName));
			
			ops.add("How should I use your shop?")
				.addNPC(npc.getId(), HeadE.CHEERFUL, "I'm glad you ask! You can buy as many of the items stocked as you wish. The price of these items changes based on the amount in stock.")
				.addNPC(npc.getId(), HeadE.CHEERFUL, "You can also sell most items to the shop and the price given will be based on the amount in stock.");
				
			ops.add("No, thanks.");
		});
	}
}
