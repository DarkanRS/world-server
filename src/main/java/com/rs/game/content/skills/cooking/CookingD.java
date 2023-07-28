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
import com.rs.lib.Constants;
import com.rs.plugin.handlers.ItemOnItemHandler;

public class CookingD extends Conversation {

	public CookingD(Player player, Cookables cookable, GameObject gameObject) {
		super(player);
		generateConversation(player, cookable, gameObject);
		create();
	}

	private void generateConversation(Player player, Cookables cookable, GameObject gameObject)
	{
		String objectName = gameObject.getDefinitions(player).getName().toLowerCase();
		boolean isGameObjectFire = objectName.contains("fire");
		if (cookable == null) {
			if (isGameObjectFire)
				addSimple("You can't cook that on a fire.");
			else if (objectName.contains("range"))
				addSimple("You can't cook that on a range.");
			else
				addSimple("You can't cook that.");
			return;
		}
		if (cookable.isFireOnly() && !isGameObjectFire) {
			addSimple("You may only cook this on a fire.");
			return;
		}
		if (cookable.isSpitRoastRequired() && gameObject.getId() != 11363) {
			addSimple("You may only cook this on an iron spit.");
			return;
		}
		if (player.getSkills().getLevel(Constants.COOKING) < cookable.getLevel()) {
			addSimple("You need a cooking level of " + cookable.getLevel() + " to cook this food.");
			return;
		}

		addNext(new MakeXStatement(
				MakeXType.COOK,
				player.getInventory().getAmountOf(cookable.getRawItem().getId()),
				"Choose how many you wish to cook,<br>then click on the item to begin.",
				new int[] { cookable.getProductItem().getId() },
				null));
		addNext(() -> player.getActionManager().setAction(new Cooking(player, gameObject, cookable, MakeXStatement.getQuantity(player))));
	}

}
