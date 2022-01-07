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
package com.rs.game.player.content.minigames.ectofuntus;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;

public class EctoBones extends Action {

	public EctoBones(int itemId) {
		this.itemId = itemId;
	}

	int itemId;
	int step = 0;

	@Override
	public boolean start(Player player) {
		step = 0;
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (step == -1)
			return false;
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		switch (step) {
		case 0:
			if (!Ectofuntus.hopper(player, itemId))
				step = -1;
			else
				step++;
			return 2;
		case 1:
			if (!Ectofuntus.grinder(player))
				step = -1;
			else
				step++;
			return 2;
		case 2:
			if (!Ectofuntus.bin(player))
				step = -1;
			else
				step = 0;
			return 2;
		default:
			stop(player);
		}
		return 4;
	}

	@Override
	public void stop(Player player) {
	}
}
