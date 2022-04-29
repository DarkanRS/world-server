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
package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.skills.runecrafting.runespan.RunespanController;
import com.rs.lib.game.WorldTile;

public class RunespanPortalD extends MatrixDialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Where would you like to travel to?", "The Runecrafting Guild", "Low level entrance into the Runespan", "High level entrance into the Runespan");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1)
			if (componentId == OPTION_1) {
				player.useStairs(-1, new WorldTile(1696, 5460, 2), 0, 1);
				end();
			} else {
				RunespanController.enterRunespan(player, componentId == OPTION_3);
				end();
			}

	}

	@Override
	public void finish() {

	}

}
