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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class PoltergeistRoom extends PuzzleRoom {

	public static final int POLTERGEIST_ID = 11245;

	public static final int CONSECRATED_HERB = 19659;
	public static final int[] HERBS =
		{ 19653, 19654, 19655, 19656, 19657, 19658 };

	private static final int[][] CENSERS =
		{
				{ -1, -1, -1 },
				{ 54095, 54099, 54103 },
				{ 54096, 54100, 54104 },
				{ 54097, 54101, 54105 },
				{ 39847, 39850, 39851 }, };

	private static final int[][] SARCOPHAGUS =
		{
				{ -1, -1, -1 },
				{ 54079, 54083 },
				{ 54080, 54084 },
				{ 54081, 54085 },
				{ 39526, 39840 }, };

	private NPC poltergeist;
	private Item requiredHerb;
	private int censersLit;
	private int herbsAvailable = 4;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
		poltergeist = manager.spawnNPC(reference, POLTERGEIST_ID, 5, 5, false, DungeonConstants.PUZZLE_NPC);
		requiredHerb = new Item(HERBS[Utils.random(HERBS.length)]);
	}

	public boolean canTakeHerb() {
		return herbsAvailable > 0;
	}

	public void takeHerb(Player player, GameObject object, int index) {
		if (requiredHerb.getId() == HERBS[index]) {
			if (player.getInventory().addItem(HERBS[index], 1)) {
				player.lock(1);
				player.sendMessage("With great care, you pick a clump of the herb.");
				giveXP(player, Constants.HERBLORE);
				herbsAvailable--;
				if (herbsAvailable == 0) {
					GameObject o = new GameObject(object);
					o.setId(DungeonConstants.EMPTY_FARMING_PATCH);
					World.spawnObject(o);
				}
			}
		} else
			player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * .3), HitLook.TRUE_DAMAGE));
	}

	@Override
	public boolean processObjectClick1(Player player, GameObject object) {
		String name = object.getDefinitions().getName();
		if (name.equals("Sarcophagus")) {
			player.simpleDialogue("The inscription reads: 'Here lies Leif, posthumously honoured with the discovery of " + requiredHerb.getName() + ".");
			return false;
		}
		if (name.equals("Censer") && object.getDefinitions().containsOption("Light")) {
			int requiredFiremaking = getRequirement(Constants.FIREMAKING);
			if (!player.getInventory().containsOneItem(DungeonConstants.TINDERBOX)) {
				player.sendMessage("You need a tinderbox in order to light a censer.");
				return false;
			} else if (requiredFiremaking > player.getSkills().getLevel(Constants.FIREMAKING)) {
				player.sendMessage("You need a firemaking level of " + requiredFiremaking + " to light this.");
				return false;
			}
			giveXP(player, Constants.FIREMAKING);
			player.lock(1);
			censersLit++;
			if (censersLit == 4)
				poltergeist.finish();
			World.spawnObject(new GameObject(CENSERS[manager.getParty().getFloorType()][2], object.getType(), object.getRotation(), object.getTile()));
			return false;
		}
		if (name.equals("Censer") && object.getDefinitions().containsOption("Inspect")) {
			player.sendMessage("This censer would be ideal for burning something in.");
			return false;
		} else if (name.equals("Herb patch") && object.getDefinitions().containsOption("Harvest")) {
			int requiredHerblore = getRequirement(Constants.HERBLORE);
			if (requiredHerblore > player.getSkills().getLevel(Constants.HERBLORE)) {
				player.sendMessage("You need a herblore level of " + requiredHerblore + " to harvest these herbs.");
				return false;
			}
			player.startConversation(new Dialogue().addOptions(ops -> {
				for (int i = 0;i < HERBS.length;i++) {
					final int index = i;
					ops.add(ItemDefinitions.getDefs(HERBS[i]).name, () -> takeHerb(player, object, index));
				}
			}));
			return false;
		}
		return true;
	}

	@Override
	public boolean handleItemOnObject(Player player, GameObject object, Item item) {
		if (object.getDefinitions().getName().equals("Censer") && object.getDefinitions().containsOption("Inspect"))
			if (item.getId() == CONSECRATED_HERB) {
				player.lock(1);
				player.sendMessage("You pile the herbs into the censer.");
				player.getInventory().deleteItem(item);
				World.spawnObject(new GameObject(CENSERS[manager.getParty().getFloorType()][1], object.getType(), object.getRotation(), object.getTile()));
				return false;
			}
		return true;
	}

	@Override
	public boolean processObjectClick2(Player player, GameObject object) {
		if (object.getDefinitions().getName().equals("Sarcophagus")) {
			if (censersLit != 4) {
				player.sendMessage("lit: " + censersLit + "/4");
				return false;
			}
			int requiredThieving = getRequirement(Constants.THIEVING);
			if (requiredThieving > player.getSkills().getLevel(Constants.THIEVING)) {
				player.sendMessage("You need a thieving level of " + requiredThieving + " to open the sarcophagus.");
				return false;
			}
			giveXP(player, Constants.THIEVING);
			player.sendMessage("You successfully open the sarcophagus.");
			World.spawnObject(new GameObject(SARCOPHAGUS[manager.getParty().getFloorType()][1], object.getType(), object.getRotation(), object.getTile()));
			setComplete();
			return false;
		}
		return true;
	}

	public static class Poltergeist extends DungeonNPC {

		private Tile[] corners;
		private int ptr;

		public Poltergeist(int id, Tile tile, DungeonManager manager, RoomReference reference) {
			super(id, tile, manager);
			corners = new Tile[4];
			corners[0] = manager.getTile(reference, 5, 5);
			corners[1] = manager.getTile(reference, 5, 10);
			corners[2] = manager.getTile(reference, 10, 10);
			corners[3] = manager.getTile(reference, 10, 5);
		}

		@Override
		public void processNPC() {
			if (getWalkSteps().isEmpty()) {
				addWalkSteps(corners[ptr].getX(), corners[ptr].getY());
				if (++ptr == corners.length)
					ptr = 0;
			}
			super.processNPC();
		}

	}

	public void consecrateHerbs(Player player, int id) {
		int requiredPrayer = getRequirement(Constants.PRAYER);
		if (requiredPrayer > player.getSkills().getLevel(Constants.PRAYER)) {
			player.sendMessage("You need a prayer level of " + requiredPrayer + " to consecrate the herbs.");
			return;
		}
		giveXP(player, Constants.PRAYER);
		player.lock(2);
		player.getInventory().deleteItem(id, 1);
		player.getInventory().addItem(CONSECRATED_HERB, 1);
		player.sendMessage("You consecrate the herbs.");
	}

	@Override
	public String getCompleteMessage() {
		return "You hear a clunk as the doors unlock.";
	}

	@Override
	public String getLockMessage() {
		return "The door is locked. You can't see any obvious keyhole or mechanism.";
	}

}
