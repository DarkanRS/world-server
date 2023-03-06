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

import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

public class AnimatedArmor extends OwnedNPC {

	private transient Player player;

	public AnimatedArmor(Player player, int id, Tile tile) {
		super(player, id, tile, false);
		this.player = player;
	}

	public int[] getDroppedItems() {
		int index = getId() - 4278;
		int[] droppedItems = WarriorsGuild.ARMOUR_SETS[index];
		return droppedItems;
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		super.finish();
		if (player != null) {
			player.getTempAttribs().removeB("animator_spawned");
			player.setWarriorPoints(3, WarriorsGuild.ARMOR_POINTS[getId() - 4278]);
			if (!isDead())
				for (int item : getDroppedItems()) {
					if (item == -1)
						continue;
					player.getInventory().addItemDrop(item, 1);
				}
		}
	}
}
