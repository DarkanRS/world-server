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
package com.rs.game.content.dialogue.impl;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.utils.shop.ShopsHandler;

public class DrogoDwarf extends Conversation {

	public DrogoDwarf(Player player, NPC npc) {
		super(player);
		addNPC(npc.getId(), HeadE.CHEERFUL, "'Ello. Welcome to my Mining shop, friend.");
		addOptions(ops -> {
			ops.add("Do you want to trade?")
				.addPlayer(HeadE.CONFUSED, "Do you want to trade?")
				.addNext(() -> ShopsHandler.openShop(player, "drogos_mining_emporium"));
			
			ops.add("Hello, shorty.")
				.addPlayer(HeadE.CHEERFUL, "Hello, shorty.")
				.addNPC(npc.getId(), HeadE.ANGRY, "I may be short but at least I've got manners.");
			
			ops.add("Why don't you ever restock ores and bars?")
				.addPlayer(HeadE.CONFUSED, "Why don't you ever restock ores and bars?")
				.addNPC(npc.getId(), HeadE.CHEERFUL, "The only ores and bars I sell are those sold to me.");
		});
	}
}
