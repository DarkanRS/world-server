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

public class ClanCloak extends Dialogue {

	boolean rightClick;

	@Override
	public void start() {
		rightClick = (boolean) parameters[0];

		if (rightClick) {
			sendNPCDialogue(13633, HAPPY_TALKING, "Why of course you can have a clan cape.");
			stage = 100;
		} else {
			sendNPCDialogue(13633, DRUNK_HAPPY_TIRED, "Right click 'get cloak' on me if you would like a clan cape.");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 100) {
			player.getInventory().addItem(20708, 1);
			end();
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
