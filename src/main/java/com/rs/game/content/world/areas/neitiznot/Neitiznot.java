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
package com.rs.game.content.world.areas.neitiznot;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Neitiznot  {

	static class MawnisBurowgarD extends Conversation {

		private static final int MAWNIS = 5503;

		public MawnisBurowgarD(Player player) {
			super(player);

			addNPC(MAWNIS, HeadE.HAPPY_TALKING, "It makes me proud to know that the helm of my ancestors will be worn in battle.");
			addNPC(MAWNIS, HeadE.HAPPY_TALKING, "I thank you on behalf of all my kinsmen Dallim Far-strider.");
			addPlayer(HeadE.WORRIED, "Ah yes, about that beautiful helmet.");
			addNPC(MAWNIS, HeadE.CONFUSED, "You mean the priceless heirloom that I gave to you as a sign of my trust and gratitude?");
			addPlayer(HeadE.WORRIED, "Err yes, that one. I may have mislaid it.");
			addNPC(MAWNIS, HeadE.CONFUSED, "It's a good job I have alert and loyal men who notice when something like this is left lying around and picks it up.");
			addNPC(MAWNIS, HeadE.CONFUSED, "I'm afraid I'm going to have to charge you a 50,000GP handling cost.");
			Dialogue op = addOption("Pay 50,000GP to recover your helmet?", "Yes, that would be fine.", "No, that's too much.");
			if (player.getInventory().hasCoins(50000))
				op.addNPC(MAWNIS, HeadE.HAPPY_TALKING, "Please be more careful with it in the future.", () -> {
					if (player.getInventory().hasCoins(50000)) {
						player.getInventory().removeCoins(50000);
						player.getInventory().addItem(10828, 1, true);
					}
				});
			else
				op.addNPC(MAWNIS, HeadE.HAPPY_TALKING, "You don't have enough gold right now.");
			op.addNPC(MAWNIS, HeadE.HAPPY_TALKING, "Okay. Come back later if you change your mind.");

			create();
		}
	}

	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 21512, 21513, 21514, 21515 }, e -> {
		if (e.getObjectId() == 21512 || e.getObjectId() == 21513)
			e.getPlayer().useLadder(e.getObjectId() == 21512 ? e.getPlayer().transform(2, 0, 2) : e.getPlayer().transform(-2, 0, -2));
		else if (e.getObjectId() == 21514 || e.getObjectId() == 21515)
			e.getPlayer().useLadder(e.getObjectId() == 21514 ? e.getPlayer().transform(-2, 0, 1) : e.getPlayer().transform(2, 0, -1));
	});

	public static NPCClickHandler handleMawnis = new NPCClickHandler(new Object[] { 5503 }, e -> {
		e.getPlayer().startConversation(new MawnisBurowgarD(e.getPlayer()));
	});

	public static NPCClickHandler handleShops = new NPCClickHandler(new Object[] { 5509, 5487, 5484, 5486, 5485, 5483, 5495 }, e -> {
		switch(e.getNPC().getId()) {
		case 5509:
			ShopsHandler.openShop(e.getPlayer(), "neitiznot_supplies");
			break;
		case 5487:
			ShopsHandler.openShop(e.getPlayer(), "keepa_kettilons_store");
			break;
		case 5484:
			ShopsHandler.openShop(e.getPlayer(), "flosis_fishmongers");
			break;
		case 5486:
			ShopsHandler.openShop(e.getPlayer(), "weapons_galore");
			break;
		case 5485:
			ShopsHandler.openShop(e.getPlayer(), "armour_shop");
			break;
		case 5483:
			ShopsHandler.openShop(e.getPlayer(), "ore_store");
			break;
		case 5495:
			ShopsHandler.openShop(e.getPlayer(), "contraband_yak_produce");
			break;
		}
	});

	public static NPCClickHandler handleCureHide = new NPCClickHandler(new Object[] { 5506 }, e -> {
		e.getPlayer().sendOptionDialogue("What can I help you with?", ops -> {
			ops.add("Cure my yak-hide, please.", () -> {
				if (e.getPlayer().getInventory().containsItem(10818, 1)) {
					int number = e.getPlayer().getInventory().getAmountOf(10818);
					e.getPlayer().getInventory().deleteItem(10818, number);
					e.getPlayer().getInventory().addItem(10820, number);
				}
			});
			ops.add("Nothing, thanks.");
		});
	});

	public static NPCClickHandler handleNeitzTravel = new NPCClickHandler(new Object[] { 5507, 5508 }, e -> {
		e.getPlayer().setNextTile(e.getNPC().getId() == 5507 ? Tile.of(2644, 3709, 0) : Tile.of(2310, 3781, 0));
	});

	public static NPCClickHandler handleJatizoTravel = new NPCClickHandler(new Object[] { 5482, 5481 }, e -> {
		e.getPlayer().setNextTile(e.getNPC().getId() == 5482 ? Tile.of(2644, 3709, 0) : Tile.of(2420, 3781, 0));
	});

	public static NPCClickHandler handleMagnusBanker = new NPCClickHandler(new Object[] { 5488 }, e -> {
		e.getPlayer().getBank().open();
	});
}
