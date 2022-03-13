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
package com.rs.game.content.skills.dungeoneering.dialogues;

import com.rs.game.content.dialogues_matrix.MatrixDialogue;

public class DungeonDifficulty extends MatrixDialogue {

	@Override
	public void start() {
		int partySize = (int) parameters[0];
		String[] options = new String[partySize];
		for (int i = 0; i < options.length; i++)
			options[i] = "" + (i + 1);
		options[partySize / 2] += " (recommended)";
		sendOptionsDialogue("What difficulty of dungeon would you like?", options);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			player.getDungManager().setDificulty(1);
		else if (componentId == OPTION_2)
			player.getDungManager().setDificulty(2);
		else if (componentId == OPTION_3)
			player.getDungManager().setDificulty(3);
		else if (componentId == OPTION_4)
			player.getDungManager().setDificulty(4);
		else if (componentId == OPTION_5)
			player.getDungManager().setDificulty(5);
		player.getDungManager().enterDungeon(true);
		end();
	}

	@Override
	public void finish() {

	}

}
