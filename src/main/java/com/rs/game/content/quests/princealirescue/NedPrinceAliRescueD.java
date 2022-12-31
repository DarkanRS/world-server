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
package com.rs.game.content.quests.princealirescue;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class NedPrinceAliRescueD extends Conversation {
	Player p;
	public final static int NED = 918;
	final int CONVO1 = 0;
	final int CONVO2 = 1;
	final int CONVO3 = 2;

	public NedPrinceAliRescueD(Player p) {
		super(p);
		this.p = p;


	}

	public NedPrinceAliRescueD(Player p, int convoID) {
		super(p);
		this.p = p;

		switch(convoID) {
		case CONVO1:
			convo1(p);
			break;
		case CONVO2:
			convo2(p);
			break;
		case CONVO3:
			convo3(p);
			break;
		}

	}

	private void convo1(Player p) {

	}

	private void convo2(Player p) {

	}

	private void convo3(Player p) {

	}
}

