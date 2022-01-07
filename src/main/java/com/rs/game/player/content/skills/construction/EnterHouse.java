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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.construction;

import com.rs.game.player.dialogues.Dialogue;

/**
 *
 * @author Jonathan
 * @since January 22th, 2014
 */
public class EnterHouse extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("What would you like to do?", "Go to your house.", "Go to your house (building mode).", "Go to a friend's house.", "Never mind.");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			switch (option) {
			case OPTION_1:
				player.getHouse().setBuildMode(false);
				player.getHouse().enterMyHouse();
				end();
				break;
			case OPTION_2:
				player.getHouse().kickGuests();
				player.getHouse().setBuildMode(true);
				player.getHouse().enterMyHouse();
				end();
				break;
			case OPTION_3:
				if (player.isIronMan()) {
					player.sendMessage("You cannot enter another player's house as an ironman.");
					end();
					break;
				}
				player.sendInputName("Enter name of the person who's house you'd like to join:", (name) -> House.enterHouse(player, name));
				end();
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}