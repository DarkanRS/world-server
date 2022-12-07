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
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.ReturnTheFlowRoom.FlowBuilder.FlowPiece;
import com.rs.game.content.skills.dungeoneering.skills.DungPickaxe;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class ReturnTheFlowRoom extends PuzzleRoom {

	//private static final int[] PEDESTAL =
	//{ 54110, 54111, 54112, 54113, 37202 };
	private static final int[] PEDESTAL_FLOW =
		{ 54114, 54115, 54116, 54117, 37203 };

	//private static final int[] PILLAR =
	//{ 54118, 54119, 54120, 54121, 37204 };
	private static final int[] PILLAR_BROKEN =
		{ 54122, 54123, 54124, 54125, 37207 };
	private static final int[] PILLAR_REPAIRED =
		{ 54126, 54127, 54128, 54129, 37219 };

	//Rubble pieces receiving no flow (each rooms seems to have a few of these prespawned)
	private static final int[] RUBBLE_PIECE =
		{ 54130, 54131, 54132, 54133, 37220 };
	private static final int[] RUBBLE_PIECE_CLEARED =
		{ 54134, 54135, 54136, 54137, 37229 };
	//Cleared rubble pieces receiving flow (they still have remnants on them)
	private static final int[] RUBBLE_PIECE_FLOW =
		{ 54138, 54139, 54140, 54141, 37232 };
	private static final int[] RUBBLE_PIECE_CLEARED_FLOW =
		{ 54142, 54143, 54144, 54145, 37249 };

	//Clean straight pieces
	private static final int[] STRAIGHT_PIECE_PATH =
			//This must also be used as a base for the rubble pieces to make a groove in the ground
		{ 54146, 54147, 54148, 54149, 37250 };
	private static final int[] STRAIGHT_PIECE_PATH_FLOW =
		{ 54150, 54151, 54152, 54153, 37251 };

	//Clean corner pieces
	private static final int[] CORNER_PIECE_PATH =
		{ 54154, 54155, 54156, 54157, 37252 };
	private static final int[] CORNER_PIECE_PATH_FLOW =
		{ 54158, 54159, 54160, 54161, 37253 };

	private static final int[][] pillars = {
			{ 5, 5 },
			{ 5, 10 },
			{ 10, 10 },
			{ 10, 5 } };

	private int roomRotation;
	private Flow[] flows;
	private int tasks;

	@Override
	public void openRoom() {
		flows = new Flow[4];
		roomRotation = manager.getRoom(reference).getRotation();
		for (int x = 5; x <= 10; x++)
			for (int y = 5; y <= 10; y++) {
				GameObject object = manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, x, y);
				if (object != null && object.getId() == RUBBLE_PIECE[type])
					World.removeObject(object);
			}

		boolean counterClockwise = coinFlip();
		int pillarPtr = counterClockwise ? 1 : 0;

		for (int i = 0; i < 4; i++) {
			int pillarX = pillars[pillarPtr][0];
			int pillarY = pillars[pillarPtr][1];
			GameObject pillar = new GameObject(manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, pillarX, pillarY));
			if (counterClockwise)
				pillar.setRotation(roomRotation + 1 + i);
			else
				pillar.setRotation(roomRotation + 3 + i);
			boolean broken = false;
			if (coinFlip()) {
				tasks++;
				broken = true;
				pillar.setId(PILLAR_BROKEN[type]);
			}
			World.spawnObject(pillar);

			flows[i] = new Flow(pillarX, pillarY);
			if (counterClockwise)
				flows[i].flow.mirror();
			FlowPiece node = flows[i].flow.start;
			while (node.next != null) {
				if (i == 1) {
					int tmp = node.x;
					node.x = 1 + node.y;
					node.y = 4 - tmp;
				} else if (i == 2) {
					node.y = 3 - node.y;
					node.x = 5 - node.x;
				} else if (i == 3) {
					int tmp = node.x;
					node.x = 4 - node.y;
					node.y = tmp - 1;
				}
				node.rotation += i;
				if (!broken && !node.blocked)
					manager.spawnObject(reference, node.corner ? CORNER_PIECE_PATH_FLOW[type] : STRAIGHT_PIECE_PATH_FLOW[type], ObjectType.GROUND_DECORATION, node.rotation, 5 + node.x, 6 + node.y);
				else
					manager.spawnObject(reference, node.corner ? CORNER_PIECE_PATH[type] : STRAIGHT_PIECE_PATH[type], ObjectType.GROUND_DECORATION, node.rotation, 5 + node.x, 6 + node.y);
				if (node.blocked) {
					if (!broken)
						manager.spawnObject(reference, RUBBLE_PIECE_FLOW[type], ObjectType.SCENERY_INTERACT, node.rotation, 5 + node.x, 6 + node.y);
					else
						manager.spawnObject(reference, RUBBLE_PIECE[type], ObjectType.SCENERY_INTERACT, node.rotation, 5 + node.x, 6 + node.y);
					broken = true;
				}
				node = node.next;
			}
			pillarPtr++;
			pillarPtr &= 0x3;
		}
		//very small chance to have it not generate anything at the start
		if (tasks == 0) {
			setComplete();
			manager.spawnObject(reference, PEDESTAL_FLOW[type], ObjectType.SCENERY_INTERACT, 0, 7, 7);
		}
		manager.spawnRandomNPCS(reference);
	}

	@Override
	public boolean processObjectClick1(final Player player, final GameObject object) {
		if (object.getId() == RUBBLE_PIECE[type] || object.getId() == RUBBLE_PIECE_FLOW[type]) {
			if (!hasRequirement(player, Constants.MINING)) {
				player.sendMessage("You need a mining level of " + getRequirement(Constants.MINING) + " to mine this rock.");
				return false;
			}
			DungPickaxe pick = DungPickaxe.getBest(player);
			if (pick == null) {
				player.sendMessage("You do not have a pickaxe or do not have the required level to use the pickaxe.");
				return false;
			}
			player.setNextAnimation(pick.getAnimation());
			player.lock(4);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					final int[] coords = manager.getRoomPos(object.getTile());
					FlowPiece node = findFlowPiece(coords[0], coords[1]);
					if (node.blocked) { //players might click in same tick
						giveXP(player, Constants.MINING);
						player.setNextAnimation(new Animation(-1));
						GameObject rubble = new GameObject(object);
						rubble.setId(object.getId() == RUBBLE_PIECE[type] ? RUBBLE_PIECE_CLEARED[type] : RUBBLE_PIECE_CLEARED_FLOW[type]);
						World.spawnObject(rubble);
						node.blocked = false;
						advance();
						if (object.getId() == RUBBLE_PIECE_FLOW[type])
							startFlow(node);
					}
				}
			}, 3);
			return false;
		}
		if (object.getId() == PILLAR_BROKEN[type]) {
			if (!hasRequirement(player, Constants.CONSTRUCTION)) {
				player.sendMessage("You need a construction level of " + getRequirement(Constants.CONSTRUCTION) + " to repair this pillar.");
				return false;
			}
			int[] coords = manager.getRoomPos(object.getTile());
			for (Flow flow : flows)
				if (flow.pillarX == coords[0] && flow.pillarY == coords[1]) {
					giveXP(player, Constants.CONSTRUCTION);
					player.setNextAnimation(new Animation(14566));
					player.lock(2);
					replaceObject(object, PILLAR_REPAIRED[type]);
					advance();
					startFlow(flow.flow.start);
					return false;
				}
		}
		return true;
	}

	private void advance() {
		if (--tasks == 0) {
			setComplete();
			manager.spawnObject(reference, PEDESTAL_FLOW[type], ObjectType.SCENERY_INTERACT, 0, 7, 7);
		}

	}

	private void startFlow(FlowPiece node) {
		while (node.next != null) {
			if (node.corner)
				manager.spawnObject(reference, CORNER_PIECE_PATH_FLOW[type], ObjectType.GROUND_DECORATION, node.rotation, 5 + node.x, 6 + node.y);
			else if (!node.blocked)
				manager.spawnObject(reference, STRAIGHT_PIECE_PATH_FLOW[type], ObjectType.GROUND_DECORATION, node.rotation, 5 + node.x, 6 + node.y);
			else {
				manager.spawnObject(reference, RUBBLE_PIECE_FLOW[type], ObjectType.SCENERY_INTERACT, node.rotation, 5 + node.x, 6 + node.y);
				return;
			}
			node = node.next;
		}
	}

	private FlowPiece findFlowPiece(int x, int y) {
		for (Flow flow : flows) {
			FlowPiece node = flow.flow.start;
			while (node.next != null) {
				if (5 + node.x == x && 6 + node.y == y)
					return node;
				node = node.next;
			}
		}
		throw new RuntimeException();
	}

	private int endY = 1 + (int) (Math.random() * 2);

	public class Flow {

		int pillarX;
		int pillarY;
		FlowBuilder flow;

		public Flow(int x, int y) {
			pillarX = x;
			pillarY = y;
			flow = new FlowBuilder();
			flow.generate();
		}

		public GameObject getPillar() {
			return manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, pillarX, pillarY);
		}

	}

	public class FlowBuilder {

		public static final int NORTH = 0;
		public static final int EAST = 1;
		public static final int SOUTH = 2;
		public static final int WEST = 3;

		private static final int SIZE_X = 1;
		private static final int SIZE_Y = 2;
		private FlowPiece start;
		private FlowPiece curr;
		boolean[][] used = new boolean[2][3];

		public void generate() {
			start = curr = new FlowPiece(0, 0, false, 0, 0, false);
			while (curr.x != SIZE_X || curr.y != endY) {
				if (coinFlip()) {
					if (curr.x == SIZE_X && !used[curr.x - 1][curr.y]) {
						move(WEST);
						continue;
					}
					if (curr.x == 0 && !used[SIZE_X][curr.y]) {
						move(EAST);
						continue;
					}
				}
				//Else can't change X
				if (curr.y == SIZE_Y) {
					if (!used[curr.x][curr.y - 1])
						move(SOUTH);
					else
						move(EAST); //rest is impossible
				} else
					move(NORTH);
			}
			move(EAST);
		}

		public void move(int dir) {
			used[curr.x][curr.y] = true;
			if (curr.fromDir == NORTH && dir == NORTH)
				new FlowPiece(curr.x, curr.y + 1, false, 1, dir, coinFlip());
			else if (curr.fromDir == EAST && dir == EAST)
				new FlowPiece(curr.x + 1, curr.y, false, 2, dir, coinFlip());
			else if (curr.fromDir == NORTH && dir == EAST)
				new FlowPiece(curr.x + 1, curr.y, true, 3, dir, false);
			else if (curr.fromDir == SOUTH && dir == EAST)
				new FlowPiece(curr.x + 1, curr.y, true, 2, dir, false);
			else if (curr.fromDir == NORTH && dir == WEST)
				new FlowPiece(curr.x - 1, curr.y, true, 0, dir, false);
			else if (curr.fromDir == EAST && dir == SOUTH)
				new FlowPiece(curr.x, curr.y - 1, true, 0, dir, false);
			else if (curr.fromDir == EAST && dir == NORTH)
				new FlowPiece(curr.x, curr.y + 1, true, 1, dir, false);
			else if (curr.fromDir == WEST && dir == NORTH)
				new FlowPiece(curr.x, curr.y + 1, true, 2, dir, false);
			else
				throw new RuntimeException();
		}

		public void mirror() {
			FlowPiece node = start;
			while (node.next != null) {
				node.y = SIZE_Y - node.y + 1;
				if (node.corner)
					node.rotation = 1 - node.rotation;
				else if (node.rotation == 1)
					//turn straight pieces in Y direction
					node.rotation += 2;
				node = node.next;
			}
		}

		private boolean hasRock;

		public class FlowPiece {

			FlowPiece next;
			int rotation;
			int fromDir;
			boolean blocked;
			boolean corner;
			int x;
			int y;

			public FlowPiece(int x, int y, boolean corner, int rotation, int dir, boolean blocked) {
				this.x = x;
				this.y = y;
				fromDir = dir;
				if (curr != null) {
					curr.corner = corner;
					if (!hasRock) {
						hasRock = true;
						curr.blocked = blocked;
						if (blocked)
							tasks++;
					}
					curr.next = this;
					curr.rotation = rotation;
				}
				curr = this;
			}

		}
	}

	private static boolean coinFlip() {
		return Math.random() > 0.5;
	}

}
