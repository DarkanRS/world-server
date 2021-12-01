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
package com.rs.game.player.dialogues;

import com.rs.game.object.GameObject;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.cooking.Cooking;
import com.rs.game.player.content.skills.cooking.Cooking.Cookables;

public class CookingD extends Dialogue {

	private Cookables cooking;
	private GameObject object;

	@Override
	public void start() {
		this.cooking = (Cookables) parameters[0];
		this.object = (GameObject) parameters[1];

		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.COOK, "Choose how many you wish to cook,<br>then click on the item to begin.", player.getInventory().getItems().getNumberOf(cooking.getRawItem()), new int[] { cooking.getProduct()
				.getId() }, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
		player.getActionManager().setAction(new Cooking(object, cooking.getRawItem(), SkillsDialogue.getQuantity(player)));
	}

	@Override
	public void finish() {

	}

}
