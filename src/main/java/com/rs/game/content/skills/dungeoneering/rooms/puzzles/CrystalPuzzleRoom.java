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

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

public class CrystalPuzzleRoom extends PuzzleRoom {

	private static final int ANIM_CHARGE_LODESTONE = 833;

	// Lodestone base location
	private static final int[] POS_BASE_LODESTONE = { 10, 1 };

	// Large crystal in the center
	private static final int[] POS_CENTER = { 7, 8 };

	// Direction for each color
	private static final int[][] POS_DELTA = { { 1, 0 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, };

	// Pressure plate location for each color
	private static final int[][] POS_PLATE = { { 2, 8 }, { 7, 13 }, { 12, 8 }, { 7, 3 }, };

	// Large center crystal lighting
	private static final int[] LARGE_CRYSTAL_OFF = { 49507, 49508, 49509, 54275, 34866 };
	private static final int[] LARGE_CRYSTAL_FLASH = { 49510, 49511, 49512, 54276, 35070 };
	private static final int[] LARGE_CRYSTAL_ON = { 49513, 49514, 49515, 54277, 35231 };

	// Tile lighting
	private static final int[] TILE_INACTIVE = { 49465, 49466, 49467, 54261, 34317 };
	private static final int[][] TILE_ACTIVE = { { 49468, 49469, 49470, 54262, 34319 }, { 49477, 49478, 49479, 54265, 34848 }, { 49486, 49487, 49488, 54268, 34856 }, { 49495, 49496, 49497, 54271, 34862 } };

	// Pressure plates
	// private static final int[] PLATE = { 52206, 52206, 52206, 54282, 35232 };

	// Active lodestone id's
	private static final int[][] LODESTONE_ACTIVE = { { 49474, 49475, 49476, 54264, 34847 }, { 49483, 49484, 49485, 54267, 34855 }, { 49492, 49493, 49494, 54270, 34861 }, { 49501, 49502, 49503, 54273, 34864 }, };

	// Inactive lodestone id's
	private static final int[][] LODESTONE_INACTIVE = { { 49471, 49472, 49473, 54263, 34320 }, { 49480, 49481, 49482, 54266, 34852 }, { 49489, 49490, 49491, 54269, 34860 }, { 49498, 49499, 49500, 54272, 34863 }, };

	private boolean[] active;
	private int[][] energyTile;
	private TileTask task;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
		type = manager.getParty().getFloorType();
		active = new boolean[4];
		energyTile = new int[4][2];
		int activeCount = 0;
		for (int color = 0; color < 4; color++)
			if (Math.random() > 0.66 && activeCount++ != 3) {
				resetPosition(color);
				World.spawnObject(new GameObject(LODESTONE_ACTIVE[color][type], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, POS_BASE_LODESTONE[0] + color, POS_BASE_LODESTONE[1] + color)));
			} else
				World.spawnObject(new GameObject(LODESTONE_INACTIVE[color][type], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, POS_BASE_LODESTONE[0] + color, POS_BASE_LODESTONE[1] + color)));
		task = new TileTask();
		WorldTasks.schedule(task, 0, 0);
	}

	@Override
	public boolean processObjectClick1(Player p, GameObject object) {
		for (int color = 0; color < 4; color++)
			if (object.getId() == LODESTONE_INACTIVE[color][type]) {
				if (!hasRequirement(p, Constants.MAGIC)) {
					p.sendMessage("You need a magic level of " + getRequirement(Constants.MAGIC) + " to power this lodestone.");
					return false;
				}
				giveXP(p, Constants.MAGIC);
				p.setNextAnimation(new Animation(ANIM_CHARGE_LODESTONE));
				p.lock(1);
				resetPosition(color);
				World.spawnObject(new GameObject(LODESTONE_ACTIVE[color][type], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, POS_BASE_LODESTONE[0] + color, POS_BASE_LODESTONE[1] + color)));
				p.sendMessage("You reach out and find the lodestone's power source. You spark it into life.");
				return false;
			}
		return true;
	}

	private void resetPosition(int color) {
		active[color] = true;
		energyTile[color][0] = POS_PLATE[color][0] + POS_DELTA[color][0];
		energyTile[color][1] = POS_PLATE[color][1] + POS_DELTA[color][1];
		World.spawnObject(new GameObject(TILE_ACTIVE[color][type], ObjectType.GROUND_DECORATION, 0, manager.getTile(reference, energyTile[color][0], energyTile[color][1])));
	}

	@Override
	public String getCompleteMessage() {
		return "You hear a click. All the doors in the room are now unlocked.";
	}

	public class TileTask extends Task {
		@Override
		public void run() {
			synchronized (manager) {
				if (manager.isDestroyed() || reference == null) {
					stop();
					return;
				}
				outer: for (int color = 0; color < 4; color++) {
					// Make sure it's actually active
					if (!active[color])
						continue;
					// ... and nobody is standing on it
					for (Player player : manager.getParty().getTeam()) {
						Tile last = player.getTile();
						if (player.getLastTile() != null)
							last = player.getLastTile();
						Tile tile = manager.getTile(reference, POS_PLATE[color][0], POS_PLATE[color][1]);
						if (tile == null)
							return;
						if (last.withinDistance(tile, 0))
							continue outer;
					}

					// Remove current tile
					World.spawnObject(new GameObject(TILE_INACTIVE[type], ObjectType.GROUND_DECORATION, 0, manager.getTile(reference, energyTile[color][0], energyTile[color][1])));

					// Rewind position if it's in the center
					if (energyTile[color][0] == POS_CENTER[0] && energyTile[color][1] == POS_CENTER[1])
						resetPosition(color);
					else {
						// Actually move it
						energyTile[color][0] += POS_DELTA[color][0];
						energyTile[color][1] += POS_DELTA[color][1];
						World.spawnObject(new GameObject(TILE_ACTIVE[color][type], ObjectType.GROUND_DECORATION, 0, manager.getTile(reference, energyTile[color][0], energyTile[color][1])));
					}
				}

				// Check if puzzle is complete
				boolean complete = true;
				boolean any = false;
				for (int color = 0; color < 4; color++)
					if (energyTile[color][0] != POS_CENTER[0] || energyTile[color][1] != POS_CENTER[1])
						complete = false;
					else
						any = true;

				if (complete) {
					World.spawnObject(new GameObject(LARGE_CRYSTAL_ON[type], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, POS_CENTER[0], POS_CENTER[1])));
					setComplete();
					stop();
					task = null;
				} else if (any)
					World.spawnObject(new GameObject(LARGE_CRYSTAL_FLASH[type], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, POS_CENTER[0], POS_CENTER[1])));
				else
					World.spawnObject(new GameObject(LARGE_CRYSTAL_OFF[type], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, POS_CENTER[0], POS_CENTER[1])));
			}
		}

	}

	@Override
	public void destroy() {
		if (task != null) {
			task.stop();
			task = null;
		}
	}

}
