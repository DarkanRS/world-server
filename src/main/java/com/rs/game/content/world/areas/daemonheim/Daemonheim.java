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
package com.rs.game.content.world.areas.daemonheim;

import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.dungeoneering.DungeonRewards;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Daemonheim {
	public static NPCClickHandler handleFremmyBanker = new NPCClickHandler(new Object[] { 9710 }, e -> {
		Player p = e.getPlayer();
		if(e.getOption().equalsIgnoreCase("bank"))
			p.getBank().open();
		if(e.getOption().equalsIgnoreCase("collect"))
			GE.openCollection(p);
		if(e.getOption().equalsIgnoreCase("talk-to"))
			p.startConversation(new Dialogue()
					.addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Good day. How may I help you?")
					.addOptions("Select an option", ops -> {
						ops.add("I'd like to access my bank account, please.", new Dialogue().addNext(()->{p.getBank().open();}));
						ops.add("I'd like to check my PIN settings.", new Dialogue().addNext(()->{p.getBank().openPinSettings();}));
						ops.add("I'd like to see my collection box", new Dialogue().addNext(()->{GE.openCollection(p);}));
					}));
	});

	public static NPCClickHandler handleRewardsTrader = new NPCClickHandler(new Object[] { 9711 }, e -> {
		Player p = e.getPlayer();
		int NPC = e.getNPCId();
		if(e.getOption().equalsIgnoreCase("talk-to"))
			p.startConversation(new Conversation(p) {
				{
					addNPC(NPC, HeadE.CALM_TALK, "Oh, hello, I didn't see...");
					addPlayer(HeadE.HAPPY_TALKING, "Hey. I was wondering if you could help me?");
					addNPC(NPC, HeadE.CALM_TALK, "Help? Uh... I'm not sure that I can... uh...");
					create();
				}
			});
		if(e.getOption().equalsIgnoreCase("shop"))
			DungeonRewards.openRewardsShop(p);
		if(e.getOption().equalsIgnoreCase("recharge"))
			p.startConversation(new Conversation(p) {
				{
					addNPC(NPC, HeadE.HAPPY_TALKING, "Did you want to recharge your gear?");
					addPlayer(HeadE.HAPPY_TALKING, "Yes.");
					addNPC(NPC, HeadE.CALM_TALK, "Give me the item.");
					addPlayer(HeadE.HAPPY_TALKING, "Okay.");
					addSimple("He looks blankly at you...");
					addPlayer(HeadE.SECRETIVE, "Umm...");
					addPlayer(HeadE.SECRETIVE, "How do I give it to you?");
					addNPC(NPC, HeadE.CALM_TALK, "Use it on me, blimey, how else?");
					addPlayer(HeadE.HAPPY_TALKING, "That makes sense.");
					addSimple("He rolls his eyes...");
					create();
				}
			});
	});

	public static ItemOnNPCHandler handleChaoticsRecharge = new ItemOnNPCHandler(new Object[] { 9711 }, e -> {
		if (e.getItem().getId() < 18349 || e.getItem().getId() > 18374)
			return;
		ItemConstants.ItemDegrade deg = ItemConstants.ItemDegrade.forId(e.getItem().getId());
		if (deg == null)
			return;
		int cost = deg.getCost(e.getItem());
		e.getPlayer().startConversation(new Conversation(e.getPlayer())
				.addNPC(9711, HeadE.SCARED, "I can repair that for either " + Utils.formatNumber(cost) + " coins or " + Utils.formatNumber(cost / 10) + " coins and " + Utils.formatNumber(cost / 100) + " dungeoneering tokens.")
				.addOptions("Which repair option would you like to use?", ops -> {
					ops.add(Utils.formatNumber(cost) + " coins", () -> {
						Item item = e.getPlayer().getInventory().getItem(e.getItem().getSlot());
						if (item == null || item.getId() != e.getItem().getId())
							return;
						if (!e.getPlayer().getInventory().hasCoins(cost)) {
							e.getPlayer().sendMessage("You don't have enough coins.");
							return;
						}
						e.getPlayer().getInventory().removeCoins(cost);
						item.setId(deg.getItemId());
						item.deleteMetaData();
						e.getPlayer().getInventory().refresh(e.getItem().getSlot());
					});
					ops.add(Utils.formatNumber(cost / 10) + " coins and " + Utils.formatNumber(cost / 100) + " dungeoneering tokens", () -> {
						Item item = e.getPlayer().getInventory().getItem(e.getItem().getSlot());
						if (item == null || item.getId() != e.getItem().getId())
							return;
						int coinCost = cost / 10;
						int tokenCost = cost / 100;
						if (!e.getPlayer().getInventory().hasCoins(coinCost)) {
							e.getPlayer().sendMessage("You don't have enough coins.");
							return;
						}
						if (e.getPlayer().getDungManager().getTokens() < tokenCost) {
							e.getPlayer().sendMessage("You don't have enough dungeoneering tokens.");
							return;
						}
						e.getPlayer().getInventory().removeCoins(coinCost);
						e.getPlayer().getDungManager().removeTokens(tokenCost);
						item.setId(deg.getItemId());
						item.deleteMetaData();
						e.getPlayer().getInventory().refresh(e.getItem().getSlot());
					});
					ops.add("Nevermind.");
				}));
	});
}
