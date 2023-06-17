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
package com.rs.engine.dialogue.statements;

import com.rs.game.model.entity.player.Player;

import java.security.InvalidParameterException;

public class OptionStatement implements Statement {

	private String title;
	private String[] options;

	public OptionStatement(String title, String... options) {
		this.title = title;
		if (options.length > 5)
			throw new InvalidParameterException("The max options length is 5.");
		this.options = options;
	}

	@Override
	public void send(Player player) {
		String[] optionArray = new String[5];
		for (int i = 0; i < 5; i++)
			optionArray[i] = "";
		int ptr = 0;
		for (String s : options)
			if (s != null)
				optionArray[ptr++] = s;
		player.getInterfaceManager().sendChatBoxInterface(1188);
		player.getPackets().setIFText(1188, 20, title != null ? title : "Select an Option");
		player.getPackets().sendRunScriptReverse(5589, optionArray[4], optionArray[3], optionArray[2], optionArray[1], optionArray[0], options.length);
	}

	@Override
	public int getOptionId(int componentId) {
		return componentId == 11 ? 0 : componentId-12;
	}

	@Override
	public void close(Player player) {
		
	}
}
