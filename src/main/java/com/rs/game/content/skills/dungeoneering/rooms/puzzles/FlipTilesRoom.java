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
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;

import java.util.HashSet;
import java.util.Set;

public class FlipTilesRoom extends PuzzleRoom {

	private static final int BASE_TILE = 49637;
	private static final int YELLOW_TO_GREEN = 13781;
	private static final int GREEN_TO_YELLOW = 13782;
	private static final int GREEN = 49638;
	private static final int YELLOW = 49642;
	private GameObject[][] tiles;
	private int xOffset;
	private int yOffset;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
		tiles = new GameObject[5][5];
		outer: for (int x = 0; x < 15; x++)
			for (int y = 0; y < 15; y++) {
				GameObject object = manager.getObjectWithType(reference, ObjectType.GROUND_DECORATION, x, y);
				if (object != null && object.getId() == BASE_TILE) {
					xOffset = x;
					yOffset = y;
					break outer;
				}
			}
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++) {
				//Apparently not every configuration is solveable but eh, thats what the force option is for!
				tiles[x][y] = new GameObject(Math.random() > 0.5 ? GREEN : YELLOW, ObjectType.GROUND_DECORATION, 0, manager.getTile(reference, x + xOffset, y + yOffset));
				World.spawnObject(tiles[x][y]);
			}
	}

	@Override
	public boolean processObjectClick1(Player p, GameObject object) {
		String name = object.getDefinitions().getName();
		if (name.equals("Green tile") || name.equals("Yellow tile")) {
			p.lock(2);
			p.setNextAnimation(new Animation(7660));
			int[] pos = manager.getRoomPos(object.getTile());
			Set<GameObject> objects = getAdjacent(pos[0] - xOffset, pos[1] - yOffset);
			for (GameObject tile : objects)
				flipTile(tile);
			checkComplete();
			return false;
		}

		return true;
	}

	@Override
	public boolean processObjectClick2(Player p, GameObject object) {
		String name = object.getDefinitions().getName();
		if (name.equals("Green tile") || name.equals("Yellow tile")) {
			p.sendMessage("You force the tile without changing adjacent tiles, and the released energy harms you.");
			p.applyHit(new Hit(p, (int) (p.getMaxHitpoints() * .2), HitLook.TRUE_DAMAGE));
			p.lock(2);
			p.setNextAnimation(new Animation(13695));
			int[] pos = manager.getRoomPos(object.getTile());
			flipTile(tiles[pos[0] - xOffset][pos[1] - yOffset]);
			checkComplete();
			return false;
		}
		return true;
	}

	public void flipTile(final GameObject tile) {
		final int id = tile.getId();
		tile.setId(id == GREEN ? YELLOW : GREEN); //instantly update so 2 players pressing the same tiles at once will not bug it out, although visual may be weird, rs might lock the whole puzzle up for 1 sec, not sure tho
		for (Player team : manager.getParty().getTeam())
			team.getPackets().sendObjectAnimation(tile, new Animation(id == GREEN ? GREEN_TO_YELLOW : YELLOW_TO_GREEN));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				World.spawnObject(new GameObject(id == GREEN ? YELLOW : GREEN, ObjectType.GROUND_DECORATION, 0, tile.getTile()));
			}
		}, 1);
	}

	@Override
	public String getCompleteMessage() {
		return "You hear a click as the last tile flips. All the doors in the room are now unlocked.";
	}

	private void checkComplete() {
		if (isComplete())
			return; //You can still flip tiles after puzzle is complete, but don't do any checks
		int first = tiles[0][0].getId();
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
				if (first != tiles[x][y].getId())
					return;
		setComplete();
	}

	private Set<GameObject> getAdjacent(int x, int y) {
		Set<GameObject> set = new HashSet<>();

		set.add(tiles[x][y]);
		if (x > 0)
			set.add(tiles[x - 1][y]);
		if (x < 4)
			set.add(tiles[x + 1][y]);
		if (y < 4)
			set.add(tiles[x][y + 1]);
		if (y > 0)
			set.add(tiles[x][y - 1]);

		return set;
	}

}
