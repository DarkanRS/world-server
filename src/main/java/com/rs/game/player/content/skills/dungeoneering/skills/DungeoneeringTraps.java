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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.npc.NPC;
import com.rs.game.npc.dungeoneering.MastyxTrap;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class DungeoneeringTraps {

	public static final int[] ITEM_TRAPS =
		{ 17756, 17758, 17760, 17762, 17764, 17766, 17768, 17770, 17772, 17774 };
	private static final int[] MASTRYX_HIDES =
		{ 17424, 17426, 17428, 17430, 17432, 17434, 17436, 17438, 17440, 17442 };
	private static final int[] HUNTER_LEVELS =
		{ 1, 10, 20, 30, 40, 50, 60, 70, 80, 90 };

	public static void placeTrap(final Player player, final DungeonManager manager, final int index) {
		int levelRequired = HUNTER_LEVELS[index];
		if (manager.getMastyxTraps().size() > 5) {
			player.sendMessage("Your party has already placed the maximum amount of traps allowed.");
			return;
		}
		if (player.getSkills().getLevel(Constants.HUNTER) < levelRequired) {
			player.sendMessage("You need a Hunter level of " + levelRequired + " in order to place this trap.");
			return;
		}
		player.lock(2);
		player.setNextAnimation(new Animation(827));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				manager.addMastyxTrap(new MastyxTrap(player.getDisplayName(), 11076 + index, player, -1, false));
				player.getInventory().deleteItem(new Item(ITEM_TRAPS[index], 1));
				player.sendMessage("You lay the trap onto the floor.");
			}
		}, 2);
	}

	public static void removeTrap(Player player, MastyxTrap trap, DungeonManager manager) {
		if (player.getDisplayName().equals(trap.getPlayerName())) {
			player.setNextAnimation(new Animation(827));
			player.sendMessage("You dismantle the trap.");
			player.getInventory().addItem(ITEM_TRAPS[trap.getTier()], 1);
			manager.removeMastyxTrap(trap);
		} else
			player.sendMessage("This trap is not yours to remove!");
	}

	public static void skinMastyx(Player player, NPC npc) {
		player.setNextAnimation(new Animation(827));
		player.getInventory().addItemDrop(MASTRYX_HIDES[getNPCTier(npc.getId() - 10)], Utils.random(2, 5));
		npc.finish();
	}

	public static int getNPCTier(int id) {
		return id - 11086;
	}
}
