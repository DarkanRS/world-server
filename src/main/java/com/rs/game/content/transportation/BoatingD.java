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
package com.rs.game.content.transportation;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.KLARENSE;

import com.rs.game.content.quests.dragonslayer.DragonSlayer;
import com.rs.game.content.quests.dragonslayer.KlarenseDragonSlayerD;
import com.rs.game.content.quests.piratestreasure.CustomsOfficerPiratesTreasureD;
import com.rs.game.content.quests.piratestreasure.PiratesTreasure;
import com.rs.game.content.transportation.TravelMethods.Carrier;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

public class BoatingD extends Conversation {
	
	private int cost;
	private boolean returning;
	private Carrier ship;

	public BoatingD(Player player, int npcId) {
		super(player);

		if (npcId == 380 && player.getQuestManager().getStage(Quest.PIRATES_TREASURE) == PiratesTreasure.SMUGGLE_RUM) {
			player.startConversation(new CustomsOfficerPiratesTreasureD(player).getStart());
			return;
		}
		if (npcId == 744 && !player.isQuestComplete(Quest.DRAGON_SLAYER)) {
			player.startConversation(new KlarenseDragonSlayerD(player).getStart());
			return;
		}
		if (npcId == 744 && !player.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.IS_BOAT_FIXED_ATTR)) {
			player.startConversation(new Dialogue().addNPC(KLARENSE, HeadE.CALM_TALK, "Wow! You sure are lucky! Seems the Lady Lumbridge just washed right up into the dock by herself! She's pretty badly damaged, though ..."));
			return;
		}
		
		Object[] attributes = getBoatForShip(player, npcId);
		if (attributes == null)
			return;
		
		ship = (Carrier) attributes[0];
		returning = (Boolean) attributes[1];
		cost = -1;
		if (ship.getFares() != null)
			cost = ship.getFares()[0];
		
		addNPC(npcId, HeadE.CHEERFUL, "Hello adventurer, how can I help you today?");
		addOptions(ops -> {
			ops.add("Where does this boat take me?", new Dialogue()
					.addNPC(npcId, HeadE.CHEERFUL, "This boat? Why this boat takes you to " + ship.getFixedName(returning) + ((cost == -1) ? "." : ", for a small fee of " + cost + " coins."))
					.addOptions(cost == -1 ? "Board the ship?" : "Pay the price of " + cost + " coins?", conf -> {
						conf.add("Yes, board the ship.", () -> TravelMethods.sendCarrier(player, ship, returning));
						conf.add(cost == -1 ? "No, sometime later." : "I can't afford that!");
					}));
			ops.add("Nevermind.");
		});
	}

	public static Object[] getBoatForShip(Player player, int npcId) {
		switch (npcId) {
		case 376:
		case 377:
		case 378:
			return new Object[] { Carrier.KARAMJA_FARE, false };
		case 380:
			return player.withinDistance(Tile.of(2772, 3227, 0), 30) ?
					new Object[] { Carrier.BRIMHAVEN_FARE, true } : new Object[] { Carrier.KARAMJA_FARE, true };

		case 381:
			return new Object[] { Carrier.BRIMHAVEN_FARE, true };
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
			return new Object[] { Carrier.TEACH_MOS_LE_HARMLESS, player.withinDistance(Tile.of(3714, 3499, 1)) ? false : true };
		}
		return null;
	}
}
