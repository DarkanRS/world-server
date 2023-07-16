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
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.engine.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.content.skills.crafting.GemCutting;
import com.rs.game.content.skills.crafting.GemCutting.Gem;
import com.rs.game.model.entity.player.Player;

public class GemCuttingD extends Conversation {

	public GemCuttingD(Player player, Gem gem) {
		super(player);
		
		addNext(new MakeXStatement(MakeXType.CUT, "Choose how many you wish to cut,<br>then click on the item to begin.", new int[] { gem.getUncut() }, player.getInventory().getAmountOf(gem.getUncut())));
		addNext(() -> player.getActionManager().setAction(new GemCutting(gem, MakeXStatement.getQuantity(player))));
	}

}
