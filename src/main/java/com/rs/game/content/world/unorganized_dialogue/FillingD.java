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

import com.rs.game.content.items.liquid_containers.FillAction;
import com.rs.game.content.items.liquid_containers.FillAction.Filler;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;

public class FillingD extends Conversation {
	public FillingD(Player player, Filler filler) {
		super(player);
		
		addNext(new MakeXStatement(new int[] { filler.getFilledItem().getId() }, player.getInventory().getAmountOf(filler.getEmptyItem().getId())));
		addNext(() -> player.getActionManager().setAction(new FillAction(MakeXStatement.getQuantity(player), filler)));
	}
}