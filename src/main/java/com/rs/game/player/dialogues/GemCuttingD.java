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
package com.rs.game.player.dialogues;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.crafting.GemCutting;
import com.rs.game.player.content.skills.crafting.GemCutting.Gem;

public class GemCuttingD extends Dialogue {

	private Gem gem;

	@Override
	public void start() {
		gem = (Gem) parameters[0];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.CUT, "Choose how many you wish to cut,<br>then click on the item to begin.", player.getInventory().getItems().getNumberOf(gem.getUncut()), new int[] { gem.getUncut() }, null);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(new GemCutting(gem, SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {

	}

}
