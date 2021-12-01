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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.crafting.urns;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXActionD;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXItem;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class MakeUnfOp extends MakeXActionD {

	public MakeUnfOp(Player player, Urn... urns) {
		for (int i = 0;i < urns.length;i++)
			addOption(new MakeXItem(player, new Item(1761, 2), new Item(urns[i].unfId()), urns[i].getUnfXp(), 899, urns[i].getLevel(), Constants.CRAFTING, 4));
	}

}
