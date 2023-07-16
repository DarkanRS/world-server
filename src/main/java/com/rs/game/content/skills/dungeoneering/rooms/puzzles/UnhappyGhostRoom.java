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
package com.rs.game.content.skills.dungeoneering.rooms.puzzles;

import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class UnhappyGhostRoom extends PuzzleRoom {

	public static final int RING = 19879;
	public static final int GHOST = 11246;

	private static final int[] COFFIN =
		{ 54582, 54571, 40181, 54593, 55465 };
	private static final int[] COFFIN_OPEN =
		{ 54583, 54572, 55451, 54594, 55466 };
	private static final int[] COFFIN_BLESSED =
		{ 40172, 40175, 55452, 55463, 55467 };
	private static final int[] JEWELLERY_BOX_EMPTY =
		{ 54587, 54576, 55453, 54598, 55468 };
	private static final int[] JEWELLERY_BOX =
		{ 40173, 40180, 55454, 55464, 55469 };
	private static final int[] BROKEN_POT =
		{ 54588, 54577, 55455, 54599, 55470 };
	private static final int[] FIXED_POT =
		{ 54589, 54578, 55456, 54600, 55471 };
	private static final int[] DAMAGED_PILLAR =
		{ 54591, 54580, 55457, 54602, 55472 };
	private static final int[] REPAIRED_PILLAR =
		{ 54592, 54581, 55458, 54603, 55473 };

	private int stage = 0;

	@Override
	public void openRoom() {
		manager.spawnItem(reference, new Item(RING, 1), 5, 9);
		new UnhappyGhost(manager.getTile(reference, 6, 9), manager);
		manager.spawnRandomNPCS(reference);
	}

	@Override
	public boolean processObjectClick1(Player player, GameObject object) {
		//TODO: You can fail skill related tasks
		if (object.getId() == DAMAGED_PILLAR[type]) {
			if (!hasRequirement(player, Constants.CONSTRUCTION)) {
				player.sendMessage("You need a construction level of " + getRequirement(Constants.CONSTRUCTION) + " to repair this pillar.");
				return false;
			}
			giveXP(player, Constants.CONSTRUCTION);
			player.lock(4);
			player.setNextAnimation(new Animation(14566));
			replaceObject(object, REPAIRED_PILLAR[type]);
			advance(player);
			return false;
		}
		if (object.getId() == JEWELLERY_BOX_EMPTY[type]) {
			if (!player.getInventory().containsItem(RING, 1)) {
				player.sendMessage("You don't have any jewellery to put in here.");
				return false;
			}
			player.lock(3);
			player.setNextAnimation(new Animation(833));
			player.getInventory().deleteItem(RING, 1);
			replaceObject(object, JEWELLERY_BOX[type]);
			advance(player);
			return false;
		}
		if (object.getId() == BROKEN_POT[type]) {
			if (!hasRequirement(player, Constants.CONSTRUCTION)) {
				player.sendMessage("You need a construction level of " + getRequirement(Constants.CONSTRUCTION) + " to repair this pillar.");
				return false;
			}
			giveXP(player, Constants.CONSTRUCTION);
			player.lock(4);
			player.setNextAnimation(new Animation(14566));
			replaceObject(object, FIXED_POT[type]);
			advance(player);
			return false;
		} else if (object.getId() == COFFIN[type]) {
			if (!hasRequirement(player, Constants.THIEVING)) {
				player.sendMessage("You need a thieving level of " + getRequirement(Constants.THIEVING) + " to unlock this coffin.");
				return false;
			}
			giveXP(player, Constants.THIEVING);
			player.lock(3);
			player.setNextAnimation(new Animation(833));
			replaceObject(object, COFFIN_OPEN[type]);
			advance(player);
			return false;
		} else if (object.getId() == COFFIN_OPEN[type]) {
			if (!hasRequirement(player, Constants.PRAYER)) {
				player.sendMessage("You need a prayer level of " + getRequirement(Constants.PRAYER) + " to bless these remains.");
				return false;
			}
			//TODO: failing drains prayer
			giveXP(player, Constants.PRAYER);
			player.lock(3);
			player.setNextAnimation(new Animation(833));
			replaceObject(object, COFFIN_BLESSED[type]);
			advance(player);
			return false;
		}
		return true;
	}

	public void advance(Player player) {
		if (++stage == 5)
			setComplete();
	}

	@Override
	public boolean processNPCClick1(Player player, NPC npc) {
		if (npc.getId() == GHOST) {
			player.npcDialogue(GHOST, isComplete() ? HeadE.CHEERFUL : HeadE.SAD, "Woooo wooooo woooooooo wooo");
			return false;
		}
		return true;
	}

	public class UnhappyGhost extends DungeonNPC {

		public UnhappyGhost(Tile tile, DungeonManager manager) {
			super(GHOST, tile, manager);

		}

		@Override
		public void processNPC() {
			if (!isComplete())
				if (Utils.random(5) == 0)
					setNextAnimation(new Animation(860));
			super.processNPC();
		}

	}

}
