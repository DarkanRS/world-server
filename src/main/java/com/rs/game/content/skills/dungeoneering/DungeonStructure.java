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
package com.rs.game.content.skills.dungeoneering;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DungeonStructure {

	private RoomNode base;
	private RoomNode[][] rooms;
	private List<RoomNode> roomList;
	private List<Integer> availableKeys = IntStream.rangeClosed(0, 63).boxed().collect(Collectors.toList());
	private Random random;
	private int complexity;
	private int size;

	public DungeonStructure(int size, Random random, int complexity) {
		this.complexity = complexity;
		this.size = size;
		this.random = random;
		rooms = new RoomNode[DungeonConstants.DUNGEON_RATIO[size][0]][DungeonConstants.DUNGEON_RATIO[size][1]];
		Collections.shuffle(availableKeys, random);
		generate();
	}

	private void generate() {
		int x = random.nextInt(rooms.length);
		int y = random.nextInt(rooms[0].length);

		base = new RoomNode(null, x, y);
		roomList = new LinkedList<>();
		addRoom(base);

		List<Point> queue = new ArrayList<>();

		queue.add(new Point(getBase().x - 1, getBase().y));
		queue.add(new Point(getBase().x + 1, getBase().y));
		queue.add(new Point(getBase().x, getBase().y - 1));
		queue.add(new Point(getBase().x, getBase().y + 1));

		//Generate full dungeon
		while (!queue.isEmpty()) {
			Point next = random(queue);
			//Ensure the edge is within the dungeon boundary and it doesn't already exist
			if (next.x < 0 || next.y < 0 || next.x >= rooms.length || next.y >= rooms[0].length || getRoom(next.x, next.y) != null)
				continue;

			//Connect this edge to a random neighboring room
			RoomNode parent = randomParent(next.x, next.y);

			RoomNode room = new RoomNode(parent, next.x, next.y);
			addRoom(room);

			queue.add(new Point(next.x - 1, next.y));
			queue.add(new Point(next.x + 1, next.y));
			queue.add(new Point(next.x, next.y - 1));
			queue.add(new Point(next.x, next.y + 1));
		}

		int maxSize = rooms.length * rooms[0].length;
		int minSize = (int) (maxSize * 0.8);
		double multiplier = 1D - ((6D - complexity) * 0.06D);
		maxSize *= multiplier;
		minSize *= multiplier;
		//Create gaps by removing random DE's
		int remove = rooms.length * rooms[0].length - maxSize +  random.nextInt(maxSize - minSize);
		for (int i = 0; i < remove; i++)
			removeRoom(shuffledRooms().filter(r -> r.children.isEmpty()).findFirst().get());

		RoomNode boss;
		//Choose crit
		while (true) {
			//Sets only have distinct elements so no need to worry about overlapping paths
			Set<RoomNode> critPath = new HashSet<>();
			boss = shuffledRooms().filter(r -> r.children.isEmpty()).findFirst().get();
			critPath.addAll(boss.pathToBase());
			critPath.addAll(shuffledRooms().findAny().get().pathToBase());
			critPath.addAll(shuffledRooms().findAny().get().pathToBase());
			critPath.addAll(shuffledRooms().findAny().get().pathToBase());
			if (random.nextBoolean())
				critPath.addAll(shuffledRooms().findAny().get().pathToBase());

			if (critPath.size() >= DungeonConstants.MIN_CRIT_PATH[size] && critPath.size() <= DungeonConstants.MAX_CRIT_PATH[size]) {
				critPath.forEach(r -> r.isCritPath = true);
				boss.isBoss = true;
				break;
			}
		}

		//Move the base somewhere randomly on crit, base can't be a straight 2 way though
		setBase(shuffledRooms().filter(r -> !r.isBoss && r.isCritPath && !(r.west() && r.east() && !r.north() && !r.south())  && !(!r.west() && !r.east() && r.north() && r.south())).findFirst().get());

		//Crit DE locks, if we do these first these can't 'fail', and this mathematically ensures crit is actually crit because each branch will lock out another branch
		rooms().filter(r -> !r.isBoss && r.children.stream().noneMatch(c -> c.isCritPath) && r.isCritPath).forEach(r -> assignKey(r, true));

		//Some extra crit locks, to make things more interesting, these may 'fail' to add, but it doesn't matter
		shuffledRooms().filter(r -> !r.isBoss && r.isCritPath && r.key == -1).limit((size * 2) +1).forEach(r -> assignKey(r, true));

		if (boss.lock == -1) {
			//Should we force a lock on the boss? RS has a lock about 95% of the time
			//We could put the key anywhere on crit, because the lock doesn't actually lock out anything at all, and is redundant with keyshare on
		}

		//Bonus keys can be found anywhere that isn't the boss or has a key already
		long bonusLockCount = rooms().filter(r -> !r.isCritPath).count() / 4 + random.nextInt((size + 1) * 2);
		shuffledRooms().filter(r -> !r.isBoss && r.key == -1).limit(bonusLockCount).forEach(r -> assignKey(r, false));

	}

	RoomNode randomParent(int x, int y) {
		List<RoomNode> neighbors = new LinkedList<>();
		neighbors.add(getRoom(x - 1, y));
		neighbors.add(getRoom(x + 1, y));
		neighbors.add(getRoom(x, y - 1));
		neighbors.add(getRoom(x, y + 1));
		neighbors.removeIf(r -> r == null || r.isBoss); //These are not valid parents
		return (RoomNode) neighbors.toArray()[random.nextInt(neighbors.size())];
	}

	public RoomNode getBase() {
		return base;
	}

	public void setBase(RoomNode newBase) {
		if (base == newBase)
			return;
		base = newBase;
		swapTree(newBase);
	}

	private void swapTree(RoomNode child) {
		if (child.parent.parent != null)
			swapTree(child.parent);
		child.parent.parent = child;
		child.parent.children.remove(child);
		child.children.add(child.parent);
		child.parent = null;
	}

	public RoomNode getRoom(int x, int y) {
		if (x < 0 || y < 0 || x >= rooms.length || y >= rooms[x].length)
			return null;
		return rooms[x][y];
	}

	public void addRoom(RoomNode room) {
		rooms[room.x][room.y] = room;
		roomList.add(room);
	}

	public void removeRoom(RoomNode r) {
		r.parent.children.remove(r);
		roomList.remove(r);
		rooms[r.x][r.y] = null;
	}

	public Stream<RoomNode> rooms() {
		return roomList.stream();
	}

	public Stream<RoomNode> shuffledRooms() {
		Collections.shuffle(roomList, random);
		return roomList.stream();
	}

	public int getRoomCount() {
		return roomList.size();
	}

	public void assignKey(RoomNode keyRoom, boolean critLock) {
		List<RoomNode> unrelated = getUnrelatedRooms(keyRoom);
		List<RoomNode> children = keyRoom.getChildrenR();
		List<RoomNode> candidates = rooms().filter(r -> r.isCritPath == critLock && r.lock == -1).collect(Collectors.toList());
		candidates.retainAll(unrelated);
		candidates.removeAll(children);
		if (!candidates.isEmpty()) {
			RoomNode lockRoom = random(candidates);
			keyRoom.key = availableKeys.remove(0);
			lockRoom.lock = keyRoom.key;

		}
	}

	public List<RoomNode> getUnrelatedRooms(RoomNode room) {
		List<RoomNode> reachable = rooms().collect(Collectors.toList());
		Queue<RoomNode> encounteredLocks = new LinkedList<>();
		encounteredLocks.add(room);
		while ((room = encounteredLocks.poll()) != null) {
			reachable.remove(room);
			if (room.lock != -1) {
				int nextLock = room.lock;
				encounteredLocks.add(rooms().filter(r -> r.key == nextLock).findFirst().get());
			}
			if (room.parent != null)
				encounteredLocks.add(room.parent);
		}
		return reachable;
	}

	<T> T random(List<T> list) {
		return list.remove(random.nextInt(list.size()));
	}

}
