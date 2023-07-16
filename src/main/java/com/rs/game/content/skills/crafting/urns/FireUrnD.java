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
package com.rs.game.content.skills.crafting.urns;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.impl.MakeXActionD;
import com.rs.engine.dialogue.impl.MakeXItem;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class FireUrnD extends Conversation {

	public FireUrnD(Player player) {
		super(player);
		MakeXActionD makeX = new MakeXActionD();
		for (Urn urn : Urn.values())
			if (player.getInventory().containsItem(urn.unfId()))
				makeX.addOption(new MakeXItem(player, new Item(urn.unfId()), new Item(urn.nrId()), urn.getFireXp(), 899, urn.getLevel(), Constants.CRAFTING, 2));
		if (!makeX.isEmpty())
			addNext(makeX);
		create();
	}

}
