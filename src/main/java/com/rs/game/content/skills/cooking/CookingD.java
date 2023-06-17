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
package com.rs.game.content.skills.cooking;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.engine.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.content.skills.cooking.Cooking.Cookables;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;

public class CookingD extends Conversation {

	public CookingD(Player player, Cookables cook, GameObject object) {
		super(player);
		addNext(new MakeXStatement(MakeXType.COOK, player.getInventory().getAmountOf(cook.getRawItem().getId()), "Choose how many you wish to cook,<br>then click on the item to begin.", new int[] { cook.getProduct().getId() }, null));
		addNext(() -> player.getActionManager().setAction(new Cooking(object, cook.getRawItem(), MakeXStatement.getQuantity(player))));
		create();
	}
}
