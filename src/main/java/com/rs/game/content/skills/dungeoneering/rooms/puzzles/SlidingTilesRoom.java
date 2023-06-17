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

import com.rs.game.content.skills.dungeoneering.*;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingTilesRoom extends PuzzleRoom {

	private static final int[] BASE_TILE =
		{ 12125, 12133, 12141, 12149, 12963 };

	private static final int[][] TILE_COORDS =
		{
				{ 5, 9 },
				{ 7, 9 },
				{ 9, 9 },
				{ 5, 7 },
				{ 7, 7 },
				{ 9, 7 },
				{ 5, 5 },
				{ 7, 5 },
				{ 9, 5 }, };

	private static final int[][] VALID_MOVES =
		{
				{ 1, 3 },
				{ 0, 2, 4 },
				{ 1, 5 },
				{ 0, 4, 6 },
				{ 1, 3, 5, 7 },
				{ 2, 4, 8 },
				{ 3, 7 },
				{ 4, 6, 8 },
				{ 5, 7 } };

	private DungeonNPC[] tiles;
	private int freeIndex = 8;
	private int[] shuffledNpcOrder;
	private int[] solveOrder;
	private int solveIndex;

	@Override
	public void openRoom() {
		List<Integer> monsters = DungeonUtils.generateRandomMonsters(manager.getParty().getFloor(), (int) (manager.getParty().getAverageCombatLevel()*1.3), manager.getParty().getCombatLevel(), Utils.random(4));
		for (int i : monsters) {
			GuardianMonster m = GuardianMonster.forId(i);
			int x = Utils.random(2) + 1;
			int y = Utils.random(2) + 1;
			if (Utils.random(2) == 0) { //vertical spawn
				x += Utils.random(2) == 0 ? 0 : 11; //left or right side
				y = Utils.random(14) + 1;
			} else { //horizontal spawn
				y += Utils.random(2) == 0 ? 0 : 11; //bottom or top
				x = Utils.random(14) + 1;
			}

			manager.spawnNPC(reference, i, x, y, true, m.name().contains("FORGOTTEN_") ? DungeonConstants.FORGOTTEN_WARRIOR : DungeonConstants.GUARDIAN_NPC);
		}

		shuffle();
		tiles = new SlidingTile[9];
		for (int i = 0; i < 9; i++)
			if (shuffledNpcOrder[i] != 0) {
				int[] coords = DungeonManager.translate(TILE_COORDS[i][0], TILE_COORDS[i][1], 0, 2, 2, 0);
				Tile base = manager.getRoomBaseTile(reference);
				tiles[i] = new SlidingTile(shuffledNpcOrder[i], Tile.of(base.getX() + coords[0], base.getY() + coords[1], 0), manager);
			}
	}

	public static class SlidingTile extends DungeonNPC {

		public SlidingTile(int id, Tile tile, DungeonManager manager) {
			super(id, tile, manager);
		}

		@Override
		public int getFaceAngle() {
			return 0;
		}
	}

	public void shuffle() {
		int type = manager.getParty().getFloorType();
		shuffledNpcOrder = new int[9];
		solveOrder = new int[8];
		for (int i = 0; i < 8; i++)
			shuffledNpcOrder[i] = BASE_TILE[type] + i;
		List<Integer> set = new ArrayList<>();
		boolean[] used = new boolean[9];
		while (true) {
			for (int i = 0; i < VALID_MOVES[freeIndex].length; i++)
				if (!used[VALID_MOVES[freeIndex][i]])
					set.add(VALID_MOVES[freeIndex][i]);
			if (set.isEmpty())
				break;
			Collections.shuffle(set);
			int next = set.get(0);
			set.clear();
			used[freeIndex] = true;
			solveOrder[solveIndex++] = freeIndex;
			shuffledNpcOrder[freeIndex] = shuffledNpcOrder[next];
			shuffledNpcOrder[next] = 0;
			freeIndex = next;

		}
	}

	public static boolean handleSlidingBlock(Player player, NPC npc) {
		if (!npc.getDefinitions().getName().equals("Sliding block") || player.getControllerManager().getController() == null || !(player.getControllerManager().getController() instanceof DungeonController))
			return false;
		DungeonManager manager = player.getDungManager().getParty().getDungeon();
		VisibleRoom room = manager.getVisibleRoom(manager.getCurrentRoomReference(player.getTile()));
		if ((room == null) || !(room instanceof SlidingTilesRoom puzzle))
			return false;
		for (int i = 0; i < puzzle.tiles.length; i++)
			if (puzzle.tiles[i] == npc) {
				player.lock(1);
				if (i != puzzle.solveOrder[puzzle.solveIndex - 1]) {
					player.sendMessage("You strain your powers of telekenesis, but the tile just doesn't want to go there.");
					player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * .2), HitLook.TRUE_DAMAGE));
					return true;
				}
				puzzle.solveIndex--;
				if (puzzle.solveIndex == 0) {
					puzzle.setComplete();
					//players can keep clicking after it's done but will take damage
					puzzle.solveIndex = 1;
					puzzle.solveOrder[0] = -1;
				}
				int[] coords = DungeonManager.translate(TILE_COORDS[puzzle.freeIndex][0], TILE_COORDS[puzzle.freeIndex][1], 0, 2, 2, 0);
				Tile base = puzzle.manager.getRoomBaseTile(puzzle.reference);
				npc.addWalkSteps(base.getX() + coords[0], base.getY() + coords[1]);

				puzzle.tiles[puzzle.freeIndex] = puzzle.tiles[i];
				puzzle.tiles[i] = null;
				puzzle.freeIndex = i;
				return true;
			}

		return true;
	}

	@Override
	public String getCompleteMessage() {
		return "You hear a click as a nearby door unlocks.";//This is correct
	}

}
