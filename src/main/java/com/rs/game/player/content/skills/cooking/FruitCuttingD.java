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
package com.rs.game.player.content.skills.cooking;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.cooking.FruitCutting.CuttableFruit;
import com.rs.game.player.dialogues.Dialogue;

public class FruitCuttingD extends Dialogue {

	CuttableFruit fruit;

	@Override
	public void start() {
		fruit = (CuttableFruit) parameters[0];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "Choose how many you wish to cut,<br>then click on the item to begin.", player.getInventory().getItems().getNumberOf(fruit.getFruitId()), fruit.getProductIds(), null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);
		if (option > fruit.getProductIds().length) {
			end();
			return;
		}
		int quantity = SkillsDialogue.getQuantity(player);
		int invQuantity = player.getInventory().getItems().getNumberOf(fruit.getFruitId());
		if (quantity > invQuantity)
			quantity = invQuantity;
		end();
		player.getActionManager().setAction(new FruitCutting(fruit, option, quantity));
	}

	@Override
	public void finish() {

	}

}
