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
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.*;

public class ToxinMaze extends PuzzleRoom {

	private static final int TICK_SPEED = 5;
	private static final int TOTAL_TICKS = 80; //48 secs

	private static final int BARRIER = 49341;
	private static final int BARRIER_ENTRANCE = 49344;

	private static final int[] SWITCH_DOWN =
		{ 49384, 49385, 49386, 49386, 49386 //TODO: down of 54333, 33675
		};

	private static final int[][] OBSTACLES =
		{ //
				{ 49370, 49371, 49372, 49373, 49374 }, //Frozen
				{ 49360, 49361, 49362, 49363, 49364 }, //Aba?
				{ 49365, 49366, 49367, 49368, 49369 }, //Furnished?
				{ 54412, 54413, 54414, 54415, 54416 }, //Occult?
				{ 55853, 55854, 55855, 55856, 55857 }, //Warped?
		};

	private boolean expired;
	private WorldTask toxinTask;

	@Override
	public void openRoom() {
		generate();
	}

	@Override
	public boolean processObjectClick1(final Player player, GameObject object) {
		if (object.getDefinitions().getName().equals("Switch")) {
			GameObject down = new GameObject(object);
			down.setId(SWITCH_DOWN[type]);
			World.spawnObject(down);
			player.lock(1);
			player.setNextAnimation(new Animation(832));
			toxinTask = new ToxinTask();
			WorldTasks.schedule(toxinTask, 0, TICK_SPEED);
			setComplete(); //doors are unlocked instantly
			return false;
		}
		if (object.getDefinitions().getName().equals("Locked chest")) {
			destroy();
			for (Connector connector : connectors)
				if (connector.blocked)
					World.removeObject(World.getSpawnedObject(manager.getTile(reference, connector.x, connector.y)));
			if (!expired) {
				manager.getRoom(reference).setThiefChest(0); //Chest with lvl 1 requirement
				//fall through to default chest handler
				return true;
			}
			replaceObject(object, DungeonConstants.THIEF_CHEST_OPEN[type]);
			player.setNextAnimation(new Animation(536));
			player.lock(2);
			player.sendMessage("You open the chest, but it appears to be empty.");
			return false;
		}
		if (object.getId() == BARRIER_ENTRANCE && toxinTask == null && !isComplete()) {
			player.sendMessage("The barrier won't let you pass, I wonder what that lever is for...");
			return false;
		}
		if (object.getId() == BARRIER || object.getId() == BARRIER_ENTRANCE) {
			Tile in = Tile.of(object.getX() + Utils.ROTATION_DIR_X[object.getRotation()], object.getY() + Utils.ROTATION_DIR_Y[object.getRotation()], 0);
			Tile out = Tile.of(object.getX(), object.getY(), 0);
			Tile target = null;
			int delay = 0;
			if (player.matches(out))
				target = in;
			else if (player.matches(in))
				target = out;
			else {
				player.addWalkSteps(object.getX(), object.getY(), 1, false);
				target = in;
				delay = 1;
			}
			final Tile target_ = target;
			player.lock();
			WorldTasks.schedule(delay, () -> {
				player.forceMove(target_, 9516, 5, 30);
				player.setNextSpotAnim(new SpotAnim(2609));
			});
			return false;
		}

		return true;
	}

	private class ToxinTask extends WorldTask {

		private int ticks = TOTAL_TICKS / TICK_SPEED;

		@Override
		public void run() {
			if (toxinTask == null) {
				if (ticks > 0) {
					//Maze finished before timer ran out
					manager.message(reference, "The ticking sound from beneath the floor is suddenly silenced.");
					manager.hideBar(reference);
				} else
					//Maze finished after timer ran out
					manager.message(reference, "The toxin in the room seems to have dispersed.");
				return;
			}
			ticks--;
			if (ticks > 0)
				manager.showBar(reference, "Time remaining", (ticks * TICK_SPEED * 100) / TOTAL_TICKS);
			else {
				if (ticks == 0) {
					expired = true;
					manager.message(reference, "A toxin starts to fill the room...");
					manager.hideBar(reference);
				}
				for (Player player : manager.getParty().getTeam())
					if (manager.getCurrentRoomReference(player.getTile()).equals(reference)) {
						player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * .1), HitLook.TRUE_DAMAGE));
						player.sendMessage("You take damage from the toxin in the room.");
					}
			}
		}
	}

	@Override
	public String getCompleteMessage() {
		return "You press the switch, and both the chest in the center of the room and the door next to you unlock. A strange ticking sound comes from beneath the floor.";
	}

	@Override
	public void destroy() {
		if (toxinTask != null) {
			toxinTask.stop();
			toxinTask = null;
		}
	}

	/**
	 * Amount of unused barriers at each depth level (for generation only)
	 */
	private int[] availableDoors;

	/**
	 * The barriers by depth (for linking only)
	 */
	private Barrier[][] layers;

	private Set<Connector> connectors = new HashSet<>();

	public void generate() {

		//MAZE GRAPH DO NOT CHANGE, YOU WILL HORRIBLY FUCK THINGS UP

		int[] doorsPerLayer =
			{ 6, 3, 3, 2, 2 };

		layers = new Barrier[doorsPerLayer.length][];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = new Barrier[doorsPerLayer[i]];
			for (int j = 0; j < layers[i].length; j++)
				layers[i][j] = new Barrier(i);
		}

		BarrierSide south = new Barrier(0).outer; //Not counting the entrance bit
		BarrierSide east = new Barrier(-1).outer;
		BarrierSide north = layers[0][2].outer; //directly connected
		BarrierSide west = new Barrier(-1).outer;

		//Visual maze graph
		//_________|______|___
		//______|_______|_____
		//____|____|__|_______
		//_____|_|_______|____
		//_|_S__|__|_|_|___|__
		//        E  N   W
		//Connect everything with its clockwise neighbor
		//Exits
		new Connector(south, layers[1][0].outer, 9, 2);
		new Connector(east, layers[0][1].outer, 14, 9);
		new Connector(west, layers[0][4].outer, 1, 5);

		//Ring 1
		new Connector(layers[0][0].outer, east, 14, 5);
		new Connector(layers[0][0].inner, layers[1][1].outer, 13, 5);
		new Connector(layers[0][1].outer, layers[0][2].outer, 13, 14);
		new Connector(layers[0][1].inner, layers[0][2].inner, 12, 13);
		new Connector(layers[0][2].outer, layers[0][3].outer, 3, 14);
		new Connector(layers[0][2].inner, layers[0][3].inner, 4, 13);
		new Connector(layers[0][3].outer, west, 1, 10);
		new Connector(layers[0][3].inner, layers[1][2].outer, 2, 10);
		new Connector(layers[0][4].outer, layers[0][5].outer, 1, 2);
		new Connector(layers[0][4].inner, layers[0][5].inner, 3, 2);
		new Connector(layers[0][5].inner, south, 6, 2);

		//Ring 2
		new Connector(layers[1][0].outer, layers[0][0].inner, 12, 2);
		new Connector(layers[1][0].inner, layers[1][1].inner, 12, 4);
		new Connector(layers[1][1].outer, layers[0][1].inner, 13, 8);
		new Connector(layers[1][1].inner, layers[2][1].outer, 12, 11);
		new Connector(layers[1][2].outer, layers[0][4].inner, 2, 6);
		new Connector(layers[1][2].inner, layers[2][0].outer, 3, 4);

		//Ring 3
		new Connector(layers[2][0].outer, layers[1][0].inner, 7, 3);
		new Connector(layers[2][0].inner, layers[3][0].outer, 11, 6);
		new Connector(layers[2][1].outer, layers[2][2].outer, 8, 12);
		new Connector(layers[2][1].inner, layers[2][2].inner, 7, 11);
		new Connector(layers[2][2].outer, layers[1][2].inner, 3, 11);
		new Connector(layers[2][2].inner, layers[3][1].outer, 4, 9);

		//Ring 4
		new Connector(layers[3][0].outer, layers[2][1].inner, 11, 10);
		new Connector(layers[3][0].inner, layers[4][0].outer, 9, 10);
		new Connector(layers[3][1].outer, layers[2][0].inner, 4, 5);
		new Connector(layers[3][1].inner, layers[4][1].outer, 6, 5);

		//Ring 5
		new Connector(layers[4][0].outer, layers[3][1].inner, 6, 10);
		new Connector(layers[4][1].outer, layers[3][0].inner, 9, 5);

		availableDoors = doorsPerLayer.clone();

		south.door.visited = true;
		generatePath(south, 1, Math.random() > 0.5);
		//Make sure exits are reachable
		if (!north.door.visited)
			connectExit(north);
		if (!east.door.visited)
			connectExit(east);
		if (!south.door.visited)
			connectExit(south);
		if (!west.door.visited)
			connectExit(west);

		//Block ALL non critical connectors
		for (Connector connector : connectors)
			if (!connector.critical)
				connector.blocked = true;
		layers[4][0].visited = true;
		layers[4][1].visited = true;

		//Determine which ones can be unblocked without connecting to crit path
		boolean progress = true;
		while (progress) {
			progress = false;
			for (Connector connector : connectors)
				if (connector.blocked)
					if (connector.left != null && connector.right != null)
						if (connector.left.door.visited ^ connector.right.door.visited) {
							connector.blocked = false;
							connector.left.door.visited = true;
							connector.right.door.visited = true;
							progress = true;
						}
		}

		for (Connector connector : connectors)
			if (connector.blocked)
				World.spawnObject(new GameObject(OBSTACLES[type][Utils.random(5)], ObjectType.SCENERY_INTERACT, 0, manager.getTile(reference, connector.x, connector.y)));

	}

	public boolean generatePath(BarrierSide current, int layer, boolean goRight) {
		int choicesDown = 0;
		int choicesUp = 0;
		List<BarrierSide> options = new LinkedList<>();
		BarrierSide next;
		if (goRight)
			next = current.right;
		else
			next = current.left;
		//next can be null due to the default lever obstacles etc
		while (next != null) {
			if (next.door.layer != -1) { //Skip exits
				if (next.door.visited)
					//can't go further or paths will connect
					break;
				if (layer > next.door.layer) {
					//Door goes back to the outer ring
					choicesDown++;
					if (availableDoors[next.door.layer] - choicesDown > 0)
						options.add(next);
				} else {
					choicesUp++;
					options.add(next);
					if ((layer == 4) || (availableDoors[next.door.layer] - choicesUp == 0))
						//last door, going further is useless
						break;
				}
			}
			if (goRight)
				next = next.right;
			else
				next = next.left;
		}
		if (options.isEmpty())
			//backtrack condition
			return false;
		Collections.shuffle(options);
		//Priorize going down (makes mazes longer crit path although going down as much as possible will make the maze have very few dead ends)
		/*Collections.sort(options, new Comparator<BarrierSide>() {
			@Override
			public int compare(BarrierSide o1, BarrierSide o2) {
				return o1.door.layer - o2.door.layer;
			}
		});*/
		for (BarrierSide option : options) {
			next = current;
			//Apply new maze state
			do {
				if (goRight) {
					next.rightCon.critical = true;
					next = next.right;
				} else {
					next.leftCon.critical = true;
					next = next.left;
				}
				next.door.visited = true;
				if (next.door.layer != -1)
					availableDoors[next.door.layer]--;
			} while (next != option);
			if (layer == 4)
				return true;
			//Recursive calculation
			boolean rightFirst = Math.random() > 0.5;
			//Check both directions
			if (generatePath(next.otherSide, layer + (layer > next.door.layer ? -1 : 1), rightFirst) || generatePath(next.otherSide, layer + (layer > next.door.layer ? -1 : 1), !rightFirst))
				return true;
			//Backtrack, undo state changes
			next = current;
			do {
				if (goRight) {
					next.rightCon.critical = false;
					next = next.right;
				} else {
					next.leftCon.critical = false;
					next = next.left;
				}
				next.door.visited = false;
				if (next.door.layer != -1)
					availableDoors[next.door.layer]++;
			} while (next != option);
		}
		return false;
	}

	public void connectExit(BarrierSide curr) {
		while (true) {
			//Keep moving to the right until we find something connected
			curr.door.visited = true;
			curr.rightCon.critical = true;
			curr = curr.right;
			if (curr.door.visited)
				break;
			if (curr.layer() != 1 && curr.otherSide.layer() > curr.layer())
				//Attempt to move up to layer 1
				curr = curr.otherSide;
		}
	}

	/**
	 * Represents a barrier in the maze
	 */
	public class Barrier {

		/**
		 * Flag set if the barrier is currently being used in the critical path
		 */
		public boolean visited;

		/**
		 * Depth level of this barrier
		 */
		public int layer;

		public Barrier(int layer) {
			this.layer = layer;
			outer = new BarrierSide(this);
			inner = new BarrierSide(this);
			outer.otherSide = inner;
			inner.otherSide = outer;
		}

		/**
		 * Outer side of the barrier
		 */
		public BarrierSide outer;

		/**
		 * Inner side of the barrier
		 */
		public BarrierSide inner;

	}

	/**
	 * Represent a single side of a barrier
	 */
	public class BarrierSide {

		/**
		 * The actual barrier
		 */
		public Barrier door;

		public BarrierSide(Barrier door) {
			this.door = door;
		}

		/**
		 * The counter clockwise path on this side of the barrier
		 */
		public Connector leftCon;

		/**
		 * The clockwise path on this side of the barrier
		 */
		public Connector rightCon;

		/**
		 * The other side of this barrier
		 */
		public BarrierSide otherSide;

		/**
		 * Shortcut to leftCon.left
		 */
		public BarrierSide left;

		/**
		 * Shortcut to rightCon.right
		 */
		public BarrierSide right;

		public int layer() {
			return door.outer == this ? door.layer : door.layer + 1;
		}

	}

	/**
	 * The path between one side of a door to another side of a door
	 */
	public class Connector {

		/**
		 * Coordinates of where an object can be placed on this path to block it
		 */
		private int x;
		private int y;

		/**
		 * Flag set if this connector is a critical part of the generated path
		 */
		public boolean critical;

		public boolean blocked;

		/**
		 * The door on the connectors counter clockwise path
		 */
		public BarrierSide left;

		/**
		 * The door on the connectors clockwise path
		 */
		public BarrierSide right;

		/**
		 * Creates a connector and sets the links between objects so we can
		 * traverse them
		 */
		public Connector(BarrierSide left, BarrierSide right, int x, int y) {
			this.left = left;
			this.right = right;
			this.x = x;
			this.y = y;
			left.right = right;
			left.rightCon = this;
			right.left = left;
			right.leftCon = this;
			connectors.add(this);
		}

	}

}
