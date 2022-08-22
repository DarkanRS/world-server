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
package com.rs.game.content.minigames.domtower;

import com.rs.game.content.cutscenes.Cutscene;
import com.rs.game.model.entity.player.Player;

public class DTPreview extends Cutscene {
	@Override
	public void construct(Player player) {
		camLook(3386, 3104, 1000, 6, 6, -1);
		camPos(3395, 3104, 5000, 7, 8, 5);
		camLook(3390, 3115, 6000, 6, 6, -1);
		camPos(3395, 3115, 6000, 7, 8, 5);
		camLook(3380, 3125, 6000, 6, 6, -1);
		camPos(3380, 3125, 6000, 7, 6, 5);
		camLook(3375, 3125, 6000, 6, 6, -1);
		camPos(3375, 3125, 6000, 7, 6, 5);
		camLook(3370, 3125, 6000, 6, 6, -1);
		camPos(3370, 3125, 6000, 7, 6, 5);
		camLook(3358, 3120, 5500, 6, 6, -1);
		camPos(3358, 3125, 5500, 7, 6, 5);
		camLook(3358, 3095, 4000, 6, 6, -1);
		camPos(3358, 3095, 4000, 7, 8, 5);
		camLook(3374, 3084, 2500, 6, 6, -1);
		camPos(3374, 3084, 2500, 7, 8, 5);
		camLook(3374, 3097, 2300, 9, 9, -1);
		camPos(3374, 3097, 2300, 7, 8, 5);
	}
}
