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
package com.rs.game.content.bosses.godwars.armadyl;

import com.rs.game.content.bosses.godwars.zaros.ZarosFactionNPC;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ArmadylFactionNPC extends NPC {

	public ArmadylFactionNPC(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}

	@Override
	public boolean canBeAttackedBy(Player player) {
		if (!PlayerCombat.isRanging(player)) {
			player.sendMessage("The aviansie is flying too high for you to attack using melee.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof ArmadylFactionNPC);
	}

	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	private boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null)
				continue;
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("armadyl") || name.contains("book of law") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 19614)
				return true;
		}
		return false;
	}

	public static NPCInstanceHandler toAbyssalDemon = new NPCInstanceHandler(new Object[] { 6228, 6229, 6230, 6231, 6232, 6233, 6234, 6235, 6236, 6237, 6238, 6239, 6240, 6241, 6242, 6243, 6244, 6245, 6246 }, 
			(npcId, tile) -> new ArmadylFactionNPC(npcId, tile, false));
}
