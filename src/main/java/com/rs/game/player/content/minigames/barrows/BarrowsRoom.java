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
package com.rs.game.player.content.minigames.barrows;

import java.util.ArrayList;

public class BarrowsRoom {

	public enum RoomType {
		CORNER, EDGE, TREASURE;
	}

	protected ArrayList<Link> links;

	private RoomType type;

	private String name;

	public BarrowsRoom(RoomType type, String name) {
		this.name = name;
		this.type = type;
		links = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public RoomType getType() {
		return type;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void addLink(BarrowsRoom roomB) {
		Link roomLink = new Link(this, roomB);
		links.add(roomLink);
		roomB.links.add(roomLink);
	}
}