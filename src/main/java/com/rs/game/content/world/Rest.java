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
package com.rs.game.content.world;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class Rest extends PlayerAction {

	private static int[][] REST_DEFS = { { 5713, 1549, 5748 }, { 11786, 1550, 11788 }, { 5713, 1551, 2921 } // TODO
	// First
	// emote

	};

	private int index;

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		index = Utils.random(REST_DEFS.length);
		player.setResting(true);
		player.setNextAnimation(new Animation(REST_DEFS[index][0]));
		player.getAppearance().setBAS(REST_DEFS[index][1]);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.sendMessage("You can't rest while you're poisoned.");
			return false;
		}
		if (player.inCombat(10000) || player.hasBeenHit(10000)) {
			player.sendMessage("You can't rest until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(Player player) {
		player.setResting(false);
		player.setNextAnimation(new Animation(REST_DEFS[index][2]));
		player.getEmotesManager().setNextEmoteEnd();
		player.getAppearance().setBAS(-1);
	}

}
