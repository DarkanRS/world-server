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

public class StealingCreationRange extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select a range weapon.", "Bow", "Arrows");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1 || componentId == OPTION_2) {
				sendOptionsDialogue("Select a class", "Class one", "Class two", "Class three", "Class four", "Class five");
				stage = (byte) (componentId == OPTION_1 ? 1 : 2);
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				player.getTempAttribs().setI("sc_request", 14192);
			} else if (componentId == OPTION_2) {
				player.getTempAttribs().setI("sc_request", 14194);
			} else if (componentId == OPTION_3) {
				player.getTempAttribs().setI("sc_request", 14196);
			} else if (componentId == OPTION_4) {
				player.getTempAttribs().setI("sc_request", 14198);
			} else if (componentId == OPTION_5) {
				player.getTempAttribs().setI("sc_request", 14200);
			}
			end();
			player.getPackets().sendInputIntegerScript("Enter Amount:");
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				player.getTempAttribs().setI("sc_request", 14202);
			} else if (componentId == OPTION_2) {
				player.getTempAttribs().setI("sc_request", 14203);
			} else if (componentId == OPTION_3) {
				player.getTempAttribs().setI("sc_request", 14204);
			} else if (componentId == OPTION_4) {
				player.getTempAttribs().setI("sc_request", 14205);
			} else if (componentId == OPTION_5) {
				player.getTempAttribs().setI("sc_request", 14206);
			}
			end();
			player.getPackets().sendInputIntegerScript("Enter Amount:");
		}
	}

	@Override
	public void finish() {
	}
}
