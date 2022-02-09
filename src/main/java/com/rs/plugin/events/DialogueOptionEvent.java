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
package com.rs.plugin.events;

import com.rs.game.player.Player;

public abstract class DialogueOptionEvent {

	protected int option;
	private String[] options;
	private String optionStr = "";

	public abstract void run(Player player);

	public int getOption() {
		return option;
	}

	public String getOptionString() {
		return optionStr;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public void setOption(int integer) {
		option = integer;
		if ((integer-1) >= 0)
			optionStr = options[integer-1];
	}
}
