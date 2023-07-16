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
package com.rs.game.content.bosses.godwars.saradomin;

import com.rs.game.content.bosses.godwars.zaros.ZarosFactionNPC;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class SaradominFactionNPC extends NPC {

	public SaradominFactionNPC(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}

	@Override
	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof SaradominFactionNPC);
	}

	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			// using else as only one item should count
			if (name.contains("saradomin") || name.contains("holy symbol") || name.contains("holy book") || name.contains("monk") || name.contains("citharede") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 3839)
				return true;
		}
		return false;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6254, 6255, 6256, 6257, 6258, 6259 }, (npcId, tile) -> new SaradominFactionNPC(npcId, tile, false));
}
