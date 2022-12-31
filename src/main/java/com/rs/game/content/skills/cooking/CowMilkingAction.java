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
package com.rs.game.content.skills.cooking;

import com.rs.game.content.world.unorganized_dialogue.GrilleGoatsDialogue;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class CowMilkingAction extends PlayerAction {

	public static final int EMPTY_BUCKET = 1925;

	public CowMilkingAction() {

	}

	@Override
	public boolean start(Player player) {
		if (!player.getInventory().containsItem(EMPTY_BUCKET, 1)) {
			player.startConversation(new GrilleGoatsDialogue(player));
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return player.getInventory().hasFreeSlots() && player.getInventory().containsItem(EMPTY_BUCKET, 1);
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(2305));
		player.getInventory().deleteItem(new Item(EMPTY_BUCKET, 1));
		player.getInventory().addItem(new Item(1927));
		player.sendMessage("You milk the cow.");
		return 5;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

}
