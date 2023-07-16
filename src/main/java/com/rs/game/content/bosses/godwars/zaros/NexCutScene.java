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
package com.rs.game.content.bosses.godwars.zaros;

import com.rs.engine.cutscene.Cutscene;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

public class NexCutScene extends Cutscene {
	
	private Tile dir;
	private int selected;

	public NexCutScene(Tile dir, int selected) {
		this.dir = dir;
		this.selected = selected;
	}

	@Override
	public void construct(Player player) {
		int xExtra = 0;
		int yExtra = 0;
		if (selected == 0)
			yExtra -= 7;
		else if (selected == 2)
			yExtra += 7;
		else if (selected == 1)
			xExtra -= 7;
		else
			xExtra += 7;
		camPos(2925 + xExtra, 5203 + yExtra, 2500, -1);
		camLook(dir.getX(), dir.getY(), 2500, 3);
	}
}
