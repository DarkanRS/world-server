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

import com.rs.lib.game.WorldTile;

public class ClimbEmoteStairs extends MatrixDialogue {

	private WorldTile upTile;
	private WorldTile downTile;
	private int emoteId;

	// uptile, downtile, climbup message, climbdown message, emoteid
	@Override
	public void start() {
		upTile = (WorldTile) parameters[0];
		downTile = (WorldTile) parameters[1];
		emoteId = (Integer) parameters[4];
		sendOptionsDialogue("What would you like to do?", (String) parameters[2], (String) parameters[3], "Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			player.useStairs(emoteId, upTile, 2, 3);
		else if (componentId == OPTION_2)
			player.useStairs(emoteId, downTile, 2, 2);
		end();
	}

	@Override
	public void finish() {

	}

}
