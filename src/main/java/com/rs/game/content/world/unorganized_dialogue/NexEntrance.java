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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.game.content.bosses.godwars.zaros.NexArena;
import com.rs.game.content.bosses.godwars.zaros.NexController;
import com.rs.engine.dialogue.Conversation;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

public class NexEntrance extends Conversation {

	public NexEntrance(NexArena arena, Player player) {
		super(player);

		addSimple("The room beyond this point is a prison! There is no way out other than death or teleport. Only those who endure dangerous encounters should proceed.");
		addOption("There are currently " + arena.getPlayersCount() + " people fighting.<br>Do you wish to join them?", "Climb down.", "Stay here.");
		addNext(() -> {
			player.setNextTile(Tile.of(2911, 5204, 0));
			player.getControllerManager().startController(new NexController(arena));
		});
		create();
	}
}
