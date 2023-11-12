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
package com.rs.game.content.minigames.ectofuntus;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.Animation;

public class SlimeBucketFill extends PlayerAction {

	public SlimeBucketFill() {
	}

	@Override
	public boolean start(Player player) {
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getInventory().containsItem(Ectofuntus.EMPTY_BUCKET, 1))
			return true;
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		if (fillBucket(player))
			return 1;
		return 1;
	}

	@Override
	public void stop(Player player) {

	}

	public boolean fillBucket(Player player) {
		if (player.getInventory().containsItem(Ectofuntus.EMPTY_BUCKET, 1)) {
			player.setNextAnimation(new Animation(4471));
			player.getInventory().deleteItem(Ectofuntus.EMPTY_BUCKET, 1);
			player.soundEffect(1132);
			player.getInventory().addItem(Ectofuntus.BUCKET_OF_SLIME, 1);
			return true;
		}
		return false;
	}
}
