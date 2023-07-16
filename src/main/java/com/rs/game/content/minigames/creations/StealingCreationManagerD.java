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
package com.rs.game.content.minigames.creations;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

public class StealingCreationManagerD extends Conversation {

	public StealingCreationManagerD(Player player, StealingCreationGameController game, NPC npc) {
		super(player);
		player.heal(player.getMaxHitpoints());
		player.getPoison().reset();
		addNPC(npc.getId(), HeadE.ANGRY, Utils.random(3) == 0 ? "It's close. It could be anyone's game." : "Get a move on! Gather some clay before the other team takes it all!");
		addOptions(ops -> {
			ops.add("Show me the remaining clay.", () -> Helper.displayClayStatus(game.getArea(), player));
			ops.add("I want to quit!")
				.addNPC(npc.getId(), HeadE.CONFUSED, "Are you sure you want to quit? You will not score any points if you leave.")
				.addOption("Are you sure you want to quit?", "Yes, I want to quit.", "No, I want to stay.")
				.addNext(() -> player.getControllerManager().forceStop());
			ops.add("Nevermind.");
		});
	}
}
