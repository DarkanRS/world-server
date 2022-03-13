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

public class ZarosAltar extends MatrixDialogue {

	@Override
	public void start() {
		if (!player.getPrayer().isCurses())
			sendOptionsDialogue("Change from prayers to curses?", "Yes, replace my prayers with curses.", "Never mind.");
		else
			sendOptionsDialogue("Change from curses to prayers?", "Yes, replace my curses with prayers.", "Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			if (!player.getPrayer().isCurses()) {
				sendDialogue("The altar fills your head with dark thoughts, purging the", "prayers from your memory and leaving only curses in", " their place.");
				player.getPrayer().setPrayerBook(true);
			} else {
				sendDialogue("The altar eases its grip on your mid. The curses slip from", "your memory and you recall the prayers you used to know.");
				player.getPrayer().setPrayerBook(false);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
