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
package com.rs.game.content.minigames.wguild;

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class WGCyclops extends NPC {

	public WGCyclops(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void drop(Player killer) {
		super.drop(killer);
		WarriorsGuild.killedCyclopses++;
		if (killer.getControllerManager().getController() != null && killer.getControllerManager().getController() instanceof WarriorsGuild wguild)
			if (wguild.inCyclopse) {
				if (Utils.random(50) == 0)
					sendDrop(killer, new Item(WarriorsGuild.getBestDefender(killer)));
			} else
				killer.sendMessage("Your time has expired and the cyclops will no longer drop defenders.");
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 4291, 4292, 6078, 6079, 6080, 6081 }, (npcId, tile) -> new WGCyclops(npcId, tile));
}
