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
package com.rs.game.content.dialogues_matrix;

public class ClanVex extends MatrixDialogue {

	boolean rightClick;

	@Override
	public void start() {
		rightClick = (boolean) parameters[0];

		if (rightClick) {
			sendNPCDialogue(5915, HAPPY_TALKING, "Why of course you can have a vexillum.");
			stage = 100;
		} else {
			sendNPCDialogue(5915, DRUNK_HAPPY_TIRED, "Right click 'get vexillum' on me for a clan vexillum.");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 100) {
			player.getInventory().addItem(20709, 1);
			end();
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
