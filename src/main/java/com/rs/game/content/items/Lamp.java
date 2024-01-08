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
package com.rs.game.content.items;

public class Lamp {
	private int slot;
	private int id;
	private int req;
	private int selectedSkill;
	private double xp;

	public Lamp(int id, int slot, int req) {
		this.slot = slot;
		this.id = id;
		this.req = req;
	}

	public int getSlot() {
		return slot;
	}

	public int getId() {
		return id;
	}

	public int getReq() {
		return req;
	}

	public int getSelectedSkill() {
		return selectedSkill;
	}

	public void setSelectedSkill(int selectedSkill) {
		this.selectedSkill = selectedSkill;
	}

	public double getXp() {
		return xp;
	}

	public void setXp(double xp) {
		this.xp = xp;
	}
}
