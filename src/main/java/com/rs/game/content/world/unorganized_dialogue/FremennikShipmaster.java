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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.dungeoneering.DamonheimController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

public class FremennikShipmaster extends Conversation {

	public FremennikShipmaster(Player player, int npcId, boolean backing) {
		super(player);
		
		addNPC(npcId, HeadE.CONFUSED, backing ? "Do you want a lift back to the south?" : "You want passage to Daemonheim?");
		addOptions(ops -> {
			ops.add("Yes, please.", () -> sail(player, backing));
			ops.add("Not right now, thanks.");
		});
		create();
	}
	
	public static void sail(Player player, boolean backing) {
		player.useStairs(-1, backing ? Tile.of(3254, 3171, 0) : Tile.of(3511, 3692, 0), 2, 3);
		if (backing)
			player.getControllerManager().forceStop();
		else
			player.getControllerManager().startController(new DamonheimController());
	}
}
