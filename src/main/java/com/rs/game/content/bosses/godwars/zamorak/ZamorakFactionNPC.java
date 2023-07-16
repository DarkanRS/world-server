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
package com.rs.game.content.bosses.godwars.zamorak;

import com.rs.game.content.bosses.godwars.zaros.ZarosFactionNPC;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ZamorakFactionNPC extends NPC {

	public ZamorakFactionNPC(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}

	@Override
	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof ZamorakFactionNPC);
	}

	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null || item.getId() == -1)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak") || name.contains("unholy") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 3841)
				return true;
		}
		return false;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6210, 6211, 6212, 6213, 6214, 6215, 6216, 6217, 6218, 6219, 6220, 6221 }, 
			(npcId, tile) -> new ZamorakFactionNPC(npcId, tile, false));
}
