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

public class StartDialogue extends Dialogue {

	int accType = 0;
	boolean ironMan = false;

	@Override
	public void start() {
		sendOptionsDialogue("Would you like this account to be an ironman?", "Yes (No trading, picking up items that aren't yours etc.)", "No");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_2) {
				ironMan = false;
			} else {
				ironMan = true;
			}
			stage = 3;
			sendOptionsDialogue("Is a " + (ironMan ? "ironman" : "normal") + " account alright for you?", "Yes. Create my account.", "No. Let me choose again.");
		} else if (stage == 3) {
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Would you like this account to be an ironman?", "Yes (No trading, picking up items that aren't yours etc.)", "No");
				stage = 1;
			} else {
				player.setIronMan(ironMan);
				player.setChosenAccountType(true);
				player.getAppearance().generateAppearanceData();
				end();
			}
		}
	}

	@Override
	public void finish() {

	}

}
