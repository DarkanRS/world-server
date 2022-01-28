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
package com.rs.game.player.content.skills.dungeoneering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RoomNode {

	int x;
	int y;
	int key;
	int lock;
	boolean isCritPath;
	boolean isBoss;

	public RoomNode parent;
	public List<RoomNode> children;

	public RoomNode(RoomNode parent, int x, int y) {
		key = -1;
		lock = -1;
		children = new ArrayList<>();
		this.parent = parent;
		if (parent != null) //Base doesn't have a parent
			parent.children.add(this);
		this.x = x;
		this.y = y;
	}

	public boolean north() {
		return children.stream().anyMatch(c -> c.y - 1 == y) || (parent != null && parent.y - 1 == y);
	}

	public boolean east() {
		return children.stream().anyMatch(c -> c.x - 1 == x) || (parent != null && parent.x - 1 == x);
	}

	public boolean south() {
		return children.stream().anyMatch(c -> c.y + 1 == y) || (parent != null && parent.y + 1 == y);
	}

	public boolean west() {
		return children.stream().anyMatch(c -> c.x + 1 == x) || (parent != null && parent.x + 1 == x);
	}

	public int rotation() {
		if(parent == null)
			throw new RuntimeException("Cannot calculate rotation for base, no parent");
		if(parent.y - y == -1)
			return 0;
		if(parent.x - x == -1)
			return 1;
		if(parent.y - y == 1)
			return 2;
		if(parent.x - x == 1)
			return 3;
		throw new RuntimeException("Cannot calculate rotation, parent is not adjacent");
	}

	public List<RoomNode> pathToBase() {
		List<RoomNode> path = new LinkedList<>();
		RoomNode p = this;
		while(p != null) {
			path.add(p);
			p = p.parent;
		}
		return path;
	}

	public List<RoomNode> getChildrenR() {
		return Stream.concat(children.stream(), children.stream().flatMap(r -> r.getChildrenR().stream())).collect(Collectors.toList());
	}

}