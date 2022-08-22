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
package com.rs.game.content.bosses.corp;

import com.rs.game.content.cutscenes.Cutscene;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.model.entity.player.Player;

public class CorporealBeastScene extends Cutscene {
	@Override
	public void construct(Player player) {
		camPos(2993, 4378, 1000);
		camLook(2984, 4383, 5000);
		dialogue(new Dialogue().addSimple("You peek through the door."), true);
	}
}
