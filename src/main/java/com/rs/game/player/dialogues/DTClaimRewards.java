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

public class DTClaimRewards extends Dialogue {

	@Override
	public void start() {
		sendDialogue("You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("If you claim your rewards your progress will be reset.", "Claim rewards", "Cancel");
		} else if (stage == 0) {
			if (componentId == 11)
				player.getDominionTower().openRewardsChest();
			end();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
