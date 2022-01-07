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
package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.lib.game.WorldTile;

public class MovePlayerAction extends CutsceneAction {

	private int x, y, plane, movementType;

	public MovePlayerAction(int x, int y, boolean run, int actionDelay) {
		this(x, y, -1, run ? Player.RUN_MOVE_TYPE : Player.WALK_MOVE_TYPE, actionDelay);
	}

	/**
	 * If you are moving to a location outside your current region you must use hard region codes to change camera angles and positions.
	 * Being inside or outside the region when starting the cutscene changes the result tremendously
	 */
	public MovePlayerAction(int x, int y, int plane, int movementType, int actionDelay) {
		super(-1, actionDelay);
		this.x = x;
		this.y = y;
		this.plane = plane;
		this.movementType = movementType;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		if (movementType == Player.TELE_MOVE_TYPE) {
			player.setNextWorldTile(new WorldTile(scene.getBaseX() + x, scene.getBaseY() + y, plane));
			return;
		}
		player.setRun(movementType == Player.RUN_MOVE_TYPE);
		player.addWalkSteps(scene.getBaseX() + x, scene.getBaseY() + y);
	}

}
