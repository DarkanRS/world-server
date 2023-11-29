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

public class Door {

	private final int type, id, level;

	public Door(int type, int id, int level) {
		this.type = type;
		this.id = id;
		this.level = level;
	}

	public Door(int type, int id) {
		this(type, id, 0);
	}

	public Door(int type) {
		this(type, 0);
	}

	public int getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}
}
