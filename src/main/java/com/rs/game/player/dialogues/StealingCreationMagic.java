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

public class StealingCreationMagic extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select a magical weapon.", "Magic Staff", "Elemental Rune", "Catalyc Rune");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Select a class", "Class one", "Class two", "Class three", "Class four", "Class five");
				stage = 1;
			} else if (componentId == OPTION_2) {
				player.getTempAttribs().setI("sc_request", 12850);
				end();
				player.getPackets().sendInputIntegerScript("Enter Amount:");
			} else {
				player.getTempAttribs().setI("sc_request", 12851);
				end();
				player.getPackets().sendInputIntegerScript("Enter Amount:");
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1)
				player.getTempAttribs().setI("sc_request", 14377);
			else if (componentId == OPTION_2)
				player.getTempAttribs().setI("sc_request", 14379);
			else if (componentId == OPTION_3)
				player.getTempAttribs().setI("sc_request", 14381);
			else if (componentId == OPTION_4)
				player.getTempAttribs().setI("sc_request", 14383);
			else if (componentId == OPTION_5)
				player.getTempAttribs().setI("sc_request", 14385);
			end();
			player.getPackets().sendInputIntegerScript("Enter Amount:");
		}
	}

	@Override
	public void finish() {
	}
}
