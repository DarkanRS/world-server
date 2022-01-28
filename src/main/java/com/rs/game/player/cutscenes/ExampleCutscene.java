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
package com.rs.game.player.cutscenes;

import com.rs.game.Entity.MoveType;
import com.rs.game.player.Player;

public class ExampleCutscene extends Cutscene {
	
	@Override
	public void construct(Player player) {
		dynamicRegion(178, 554, 4, 4);
		playerMove(15, 20, 0, MoveType.TELE);
		spawnObj(67500, 2, 14, 23, 0);
		delay(20);
	}
}
