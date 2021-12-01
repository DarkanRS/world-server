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

import com.rs.game.player.Player;
import com.rs.game.player.content.transportation.TravelMethods;
import com.rs.game.player.content.transportation.TravelMethods.Carrier;
import com.rs.lib.game.WorldTile;

public class BoatingDialogue extends Dialogue {

	private int npcId, cost;
	private boolean returning;
	private Carrier ship;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello adventurer, how can I help you today?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Where does this boat take me?", "Nothing, nevermind.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(9827, "Where does this boat take me?");
			} else {
				sendPlayerDialogue(9827, "Nothing, nevermind.");
				stage = 4;
			}
		} else if (stage == 1) {
			stage = 2;
			Object[] attributes = getBoatForShip(player, npcId);
			if (attributes == null) {
				end();
				return;
			}
			ship = (Carrier) attributes[0];
			returning = (Boolean) attributes[1];
			cost = -1;
			if (ship.getFares() != null)
				cost = ship.getFares()[0];
			if (cost == -1)
				sendNPCDialogue(npcId, 9827, "This boat? Why this boat takes you to " + ship.getFixedName(returning) + ".");
			else
				sendNPCDialogue(npcId, 9827, "This boat? Why this boat takes you to " + ship.getFixedName(returning) + ", for a small fee of " + cost + " coins.");
		} else if (stage == 2) {
			if (cost != -1)
				sendOptionsDialogue("Pay the price of " + cost + " coins?", "Yes, board the ship.", "I can't affored that!");
			else
				sendOptionsDialogue("Board the ship?", "Yes, board the ship.", "No, sometime later.");
			stage = 3;
		} else if (stage == 3) {
			if (componentId == OPTION_1)
				TravelMethods.sendCarrier(player, ship, returning);
			end();
		} else if (stage == 4) {
			sendNPCDialogue(npcId, 9827, "Oh alright then, have a splendid day.");
			stage = 5;
		} else if (stage == 5) {
			end();
		}
	}

	public static Object[] getBoatForShip(Player player, int npcId) {
		switch (npcId) {
		case 376:
		case 377:
		case 378:
			return new Object[] { Carrier.KARAMJA_FARE, false };
		case 381:
			return new Object[] { Carrier.KARAMJA_FARE, true };
		case 744:
			return new Object[] { Carrier.CRANDOR_FARE, false };
		case 2728:
		case 2729:
		case 657:
			return new Object[] { Carrier.ENTRANA_FARE, false };
		case 2730:
			return new Object[] { Carrier.ENTRANA_FARE, true };
		case 3801:
			return new Object[] { Carrier.VOID_OUTPOST_FARE, false };
		case 3800:
			return new Object[] { Carrier.VOID_OUTPOST_FARE, true };
		case 4962:
			return new Object[] { Carrier.BRIMHAVEN_FARE, false };
		case 380:
			return new Object[] { Carrier.BRIMHAVEN_FARE, true };
		case 5482:
			return new Object[] { Carrier.JATIZO, true };
		case 5481:
			return new Object[] { Carrier.JATIZO, false };
		case 5508:
			return new Object[] { Carrier.NEITZNOT, false };
		case 5507:
			return new Object[] { Carrier.NEITZNOT, true };
		case 1304:
			return new Object[] { Carrier.MISCELLENIA, false };
		case 1385:
			return new Object[] { Carrier.MISCELLENIA, true };
		case 413:
			return new Object[] { Carrier.PIRATES_COVE, false };
		case 4537:
			return new Object[] { Carrier.PIRATES_COVE, true };
		case 407:
			return new Object[] { Carrier.LUNAR_ISLE, false };
		case 408:
			return new Object[] { Carrier.LUNAR_ISLE, true };
		case 2435:
			return new Object[] { Carrier.WATERBIRTH, false };
		case 2438:
			return new Object[] { Carrier.WATERBIRTH, true };
		case 3160:
			return new Object[] { Carrier.TEACH_MOS_LE_HARMLESS, player.withinDistance(new WorldTile(3714, 3499, 1)) ? false : true };
		}
		return null;
	}

	@Override
	public void finish() {

	}
}
