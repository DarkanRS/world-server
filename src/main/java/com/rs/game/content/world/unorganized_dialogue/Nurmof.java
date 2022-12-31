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

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.utils.shop.ShopsHandler;

public class Nurmof extends Conversation {

	public Nurmof(Player player, NPC npc) {
		super(player);
		addNPC(npc.getId(), HeadE.CHEERFUL, "Greetings and welcome to my pickaxe shop. Do you want to buy my premium quality pickaxes?");
		addOptions(ops -> {
			ops.add("Yes, please.", () -> ShopsHandler.openShop(player, "nurmofs_pickaxe_shop"));
			
			ops.add("Are your pickaxes better than other pickaxes, then?")
				.addPlayer(HeadE.CONFUSED, "Are your pickaxes better than other pickaxes, then?")
				.addNPC(npc.getId(), HeadE.CHEERFUL_EXPOSITION, "Of course they are! My pickaxes are made of higher grade metal than your ordinary bronze pickaxes, allowing you to mine ore just that little bit faster.");
			
			ops.add("No, thank you.")
				.addPlayer(HeadE.CHEERFUL, "No, thank you.");
		});
	}
}
