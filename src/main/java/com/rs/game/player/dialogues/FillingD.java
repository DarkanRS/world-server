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

import com.rs.game.player.actions.FillAction;
import com.rs.game.player.actions.FillAction.Filler;
import com.rs.game.player.content.SkillsDialogue;

public class FillingD extends Dialogue {

	private Filler filler;

	@Override
	public void start() {
		filler = (Filler) parameters[0];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "Choose how many you wish to fill,<br>then click on the item to begin.", player.getInventory().getItems().getNumberOf(filler.getEmptyItem()), new int[] { filler.getFilledItem()
				.getId() }, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(new FillAction(SkillsDialogue.getQuantity(player), filler));
		end();
	}

	@Override
	public void finish() {

	}

}