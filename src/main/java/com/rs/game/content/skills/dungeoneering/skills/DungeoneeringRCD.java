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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.content.SkillsDialogue;
import com.rs.game.content.dialogues_matrix.MatrixDialogue;

public class DungeoneeringRCD extends MatrixDialogue {

	public static final int[][] RUNES = {
			{ 17780, 17781, 17782, 17783 }, //Elemental 0
			{ 17784, 17785, 17786, 17787 }, //Combat 1
			{ 17788, 17789, 17790, 17791, 17792 }, //Other 2
			{ 16997, 17001, 17005, 17009, 17013, 16999, 17003, 17007, 17011, 17015 } //Staves 3
	};

	@Override
	public void start() {
		int type = (int) parameters[0];
		sendRCOptions(type);
	}

	private void sendRCOptions(int type) {
		if (type == 0)
			sendOptionsDialogue("What would you like to make?", "Runes", "Staves");
		else
			SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE_INTERVAL, "Which item would you like to make?", 0, RUNES[type-1], null);
		stage = (byte) (type + 1);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1)
			sendRCOptions(componentId == OPTION_1 ? 1 : 4);
		else if (stage >= 2 && stage <= 5) {
			int option = SkillsDialogue.getItemSlot(componentId);
			int quantity = SkillsDialogue.getQuantity(player);
			if (stage == 2) {
				if (option == 0)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[0][option], 1, .10, 11, 2, 22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 8, 88, 9, 99, 10));
				else if (option == 1)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[0][option], 5, .12, 19, 2, 38, 3, 57, 4, 76, 5, 95, 6));
				else if (option == 2)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[0][option], 9, .13, 26, 2, 52, 3, 78, 4));
				else if (option == 3)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[0][option], 14, .14, 35, 2, 70, 3));
			} else if (stage == 3) {
				if (option == 0)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[1][option], 2, .11, 14, 2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98, 8));
				else if (option == 1)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[1][option], 35, .17, 74, 2));
				else if (option == 2)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[1][option], 35, .20, 74, 2));
				else if (option == 3)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[1][option], 77, .21));
			} else if (stage == 4) {
				if (option == 0)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[2][option], 20, .15, 46, 2, 92, 3));
				else if (option == 1)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[2][option], 27, .16, 59, 2));
				else if (option == 2)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[2][option], 40, .174, 82, 2));
				else if (option == 3)
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[2][option], 45, .18, 91, 2));
				else
					player.getActionManager().setAction(new DungeoneeringRunecrafting(quantity, RUNES[2][option], 50, .19));
			} else if (stage == 5)
				player.getActionManager().setAction(new DungeoneeringStaves(option, quantity));
			end();
		}

	}

	@Override
	public void finish() {

	}
}
