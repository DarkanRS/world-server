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
package com.rs.game.player.dialogues;

public class StealingCreationClay extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select a class", "Class one", "Class two", "Class three", "Class four", "Class five");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				player.getTempAttribs().setI("sc_request", 14182);
			} else if (componentId == OPTION_2) {
				player.getTempAttribs().setI("sc_request", 14184);
			} else if (componentId == OPTION_3) {
				player.getTempAttribs().setI("sc_request", 14186);
			} else if (componentId == OPTION_4) {
				player.getTempAttribs().setI("sc_request", 14188);
			} else if (componentId == OPTION_5) {
				player.getTempAttribs().setI("sc_request", 14190);
			}
			end();
			player.getPackets().sendInputIntegerScript("Enter Amount:");
		}
	}

	@Override
	public void finish() {
	}
}
