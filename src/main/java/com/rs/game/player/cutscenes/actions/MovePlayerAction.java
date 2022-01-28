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
package com.rs.game.player.cutscenes.actions;

import java.util.Map;

import com.rs.game.Entity.MoveType;
import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.lib.game.WorldTile;

public class MovePlayerAction extends CutsceneAction {

	private int x, y, plane;
	private MoveType movementType;

	public MovePlayerAction(int x, int y, int plane, MoveType movementType, int actionDelay) {
		super(null, actionDelay);
		this.x = x;
		this.y = y;
		this.plane = plane;
		this.movementType = movementType;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		Cutscene scene = (Cutscene) objects.get("cutscene");
		if (movementType == MoveType.TELE) {
			player.setNextWorldTile(new WorldTile(scene.getX(x), scene.getY(y), plane));
			return;
		}
		player.setRun(movementType == MoveType.RUN);
		player.addWalkSteps(scene.getX(x), scene.getY(y), 25, false);
	}

}
