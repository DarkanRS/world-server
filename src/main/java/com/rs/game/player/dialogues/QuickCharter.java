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

import com.rs.game.player.content.transportation.TravelMethods;
import com.rs.game.player.content.transportation.TravelMethods.Carrier;

public class QuickCharter extends Dialogue {

	private Carrier ship;
	private int stage;

	@Override
	public void start() {
		ship = (Carrier) parameters[0];
		if (ship == null) // shouldn't happen but just incase
			end();
		sendDialogue("Sailing to " + ship.getFixedName(false) + " will cost you " + ship.getFares()[(int) parameters[1]] + " gold.");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Are you sure?", "Okay", "Choose Again", "No");
			stage = 0;
		} else {
			if (componentId == OPTION_1)
				TravelMethods.sendCarrier(player, ship, (int) parameters[1], false);
			else if (componentId == OPTION_2)
				TravelMethods.openCharterInterface(player);
			end();
		}
	}

	@Override
	public void finish() {

	}
}
