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
package com.rs.game.player.dialogues;

import com.rs.game.player.controllers.DuelArenaController;

public class ForfeitDialouge extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Forfeit Duel?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			if (!player.getLastDuelRules().getRule(7))
				((DuelArenaController) player.getControllerManager().getController()).endDuel(player.getLastDuelRules().getTarget(), player);
			else
				sendDialogue("You can't forfeit during this duel.");
			break;
		}
		end();
	}

	@Override
	public void finish() {

	}

}
