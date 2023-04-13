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

import java.util.LinkedList;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.World.DropMethod;
import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.VisibleRoom;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.npcs.misc.DungeonFishSpot;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringFishing;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringFishing.Fish;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class FishingFerretRoom extends PuzzleRoom {

	private static final int FERRET_ID = 11007, VILE_FISH = 17375;
	private static final int[] PRESSURE_PLATE =
		{ 49555, 49557, 49559, 54296, 54297 };
	private static final int[] EMPTY_PLATE =
		{ 49546, 49547, 49548, 54293, 35293 };

	private Tile pressurePlate;
	private List<GroundItem> vileFishes;
	private DungeonFishSpot psuedoFishingSpot;// Cheap hax
	private int fished = 3;
	private int cooked = 3;

	public class Ferret extends DungeonNPC {

		public Ferret(int id, Tile tile, DungeonManager manager) {
			super(id, tile, manager);
		}

		@Override
		public void processNPC() {
			if (isComplete())//We will keep it spawned but it won't do shit :D
				return;
			if (getWalkSteps().isEmpty()) {
				if (getX() == pressurePlate.getX() && getY() == pressurePlate.getY()) {
					setComplete();
					psuedoFishingSpot.finish();
					psuedoFishingSpot = null;
					removeAllVileFish();
					return;
				}
				if (vileFishes.size() > 0) {
					GroundItem item = vileFishes.get(0);//Goes in chronological order
					Tile tile = item.getTile();
					if (matches(tile)) {
						removeVileFish();
						return;
					}
					addWalkSteps(tile.getX(), tile.getY(), -1, false);
				}
			} else {// Should be fine won't be checked often anyways
				GameObject o = World.getObjectWithType(getTile(), ObjectType.GROUND_DECORATION);
				if (o != null && o.getDefinitions().getName().equals("Hole")) {
					setNextAnimation(new Animation(13797));
					WorldTasks.schedule(new WorldTask() {

						@Override
						public void run() {
							resetWalkSteps();
							setNextTile(getRespawnTile());
							setNextAnimation(new Animation(-1));
							removeAllVileFish();
						}
					});
				}
			}
			super.processNPC();
		}
	}

	private void removeVileFish() {
		World.removeGroundItem(vileFishes.remove(0));
	}

	public void removeAllVileFish() {
		for (GroundItem fish : vileFishes)
			World.removeGroundItem(fish);
		vileFishes.clear();
	}

	@Override
	public void openRoom() {
		int[][] possibleCorners = null;
		outer: for (int x = 0; x < 15; x++)
			for (int y = 0; y < 15; y++) {
				GameObject object = manager.getObjectWithType(reference, ObjectType.GROUND_DECORATION, x, y);
				if (object != null && (object.getDefinitions().getName().equals("Tile") || object.getDefinitions().getName().equals("Hole"))) {
					possibleCorners = new int[][]
							{
						{ x + 6, y, x, y + 6 },
						{ x, y, x + 6, y + 6 } };
						break outer;
				}
			}

		vileFishes = new LinkedList<>();
		boolean invertChunks = Utils.random(2) == 0;
		int[] cornerChunks = possibleCorners[Utils.random(possibleCorners.length)];
		pressurePlate = manager.getRotatedTile(reference, cornerChunks[invertChunks ? 2 : 0], cornerChunks[invertChunks ? 3 : 1]);
		Ferret puzzleNPC = new Ferret(FERRET_ID, manager.getRotatedTile(reference, cornerChunks[invertChunks ? 0 : 2], cornerChunks[invertChunks ? 1 : 3]), manager);
		psuedoFishingSpot = new DungeonFishSpot(1957, manager.getRotatedTile(reference, 7, 13), manager, Fish.VILE_FISH);
		int floorType = manager.getParty().getFloorType();
		World.spawnObject(new GameObject(PRESSURE_PLATE[floorType], ObjectType.GROUND_DECORATION, 0, pressurePlate));
		World.spawnObject(new GameObject(EMPTY_PLATE[floorType], ObjectType.GROUND_DECORATION, 0, puzzleNPC.getTile()));
	}

	@Override
	public boolean processObjectClick1(Player player, GameObject object) {
		if (object.getDefinitions().getName().equals("Fishing spot")) {
			int requiredFishing = getRequirement(Constants.FISHING);
			if (psuedoFishingSpot == null)
				return true;
			if (requiredFishing > player.getSkills().getLevel(Constants.FISHING)) {
				player.sendMessage("You need a Fishing level of " + requiredFishing + " to catch a raw vile fish.");
				return false;
			}
			if (fished-- > 0)
				giveXP(player, Constants.FISHING);
			player.getActionManager().setAction(new DungeoneeringFishing(psuedoFishingSpot));
			return false;
		}
		return true;
	}

	public static boolean handleFerretThrow(final Player player, final GameObject object, final Item item) {
		if ((!object.getDefinitions().getName().equals("Tile") && !object.getDefinitions().getName().equals("Pressure plate")) || item.getId() != VILE_FISH || player.getControllerManager().getController() == null || !(player.getControllerManager().getController() instanceof DungeonController))
			return false;
		DungeonManager manager = player.getDungManager().getParty().getDungeon();
		VisibleRoom room = manager.getVisibleRoom(manager.getCurrentRoomReference(player.getTile()));
		if ((room == null) || !(room instanceof FishingFerretRoom puzzle))
			return false;
		if (puzzle.isComplete()) {
			player.sendMessage("I know it smells, but littering is wrong!");
			return false;
		}
		int requiredRange = puzzle.getRequirement(Constants.RANGE);
		if (player.getSkills().getLevel(Constants.RANGE) < requiredRange) {
			player.sendMessage("You need a Range level of " + requiredRange + " to throw a vile fish.");
			return false;
		}
		player.lock(2);
		player.setNextAnimation(new Animation(13325));
		player.setNextSpotAnim(new SpotAnim(2521));
		player.getInventory().deleteItem(item);
		player.faceObject(object);
		player.sendMessage("You throw the fish.");
		WorldProjectile p = World.sendProjectile(player, object, 2522, 32, 0, 25, 1, 15, 0);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				World.sendSpotAnim(object.getTile(), new SpotAnim(2523));
				World.addGroundItem(item, object.getTile(), null, false, 0, DropMethod.NORMAL, 40);
				puzzle.getVileFishes().add(ChunkManager.getChunk(object.getTile().getChunkId()).getGroundItem(item.getId(), object.getTile(), player));
			}
		}, p.getTaskDelay());
		return true;
	}

	@Override
	public boolean handleItemOnObject(Player player, GameObject object, Item item) {
		if (object.getDefinitions().getName().equals("Fire") && item.getId() == 17374) {
			int requiredCooking = getRequirement(Constants.COOKING);
			if (player.getSkills().getLevel(Constants.COOKING) < requiredCooking) {
				player.sendMessage("You need a Cooking level of " + requiredCooking + " to cook a raw vile fish.");
				return false;
			}
			if (cooked-- > 0)
				giveXP(player, Constants.COOKING);
			player.getInventory().deleteItem(17374, 1);
			player.getInventory().addItem(VILE_FISH, 1);
			return false;
		}
		return true;
	}

	protected List<GroundItem> getVileFishes() {
		return vileFishes;
	}

	@Override
	public String getCompleteMessage() {
		return "You hear a click as the ferret steps on the pressure plate. All the doors in the room are now unlocked.";
	}

}
