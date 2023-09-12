// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option.add) any later version.
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
package com.rs.game.content.minigames.pyramidplunder;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SimonTempleton extends Conversation {
	private static final int npcId = 3123;
	private static final int[] ARTEFACT_IDS = {
			PPArtefact.IVORY_COMB.getArtefactId(),
			PPArtefact.POTTERY_SCARAB.getArtefactId(),
			PPArtefact.POTTERY_STATUETTE.getArtefactId(),
			PPArtefact.STONE_SEAL.getArtefactId(),
			PPArtefact.STONE_SCARAB.getArtefactId(),
			PPArtefact.STONE_STATUETTE.getArtefactId(),
			PPArtefact.GOLD_SEAL.getArtefactId(),
			PPArtefact.GOLD_SCARAB.getArtefactId(),
			PPArtefact.GOLD_STATUETTE.getArtefactId(),
			PPArtefact.JEWELLED_GOLDEN.getArtefactId(),
			PPArtefact.JEWELLED_DIAMOND.getArtefactId(),
			PPArtefact.NOTED_IVORY_COMB.getArtefactId(),
			PPArtefact.NOTED_POTTERY_SCARAB.getArtefactId(),
			PPArtefact.NOTED_POTTERY_STATUETTE.getArtefactId(),
			PPArtefact.NOTED_STONE_SEAL.getArtefactId(),
			PPArtefact.NOTED_STONE_SCARAB.getArtefactId(),
			PPArtefact.NOTED_STONE_STATUETTE.getArtefactId(),
			PPArtefact.NOTED_GOLD_SEAL.getArtefactId(),
			PPArtefact.NOTED_GOLD_SCARAB.getArtefactId(),
			PPArtefact.NOTED_GOLD_STATUETTE.getArtefactId(),
	};

	private boolean playerHasArtefacts(Player player) {
		for (int artefactId : ARTEFACT_IDS) {
			if (player.getInventory().containsItem(artefactId)) {
				return true;
			}
		}
		return false;
	}


	public static NPCClickHandler SimonTempletonD = new NPCClickHandler(new Object[]{ npcId }, new String[] { "Talk-to" }, e -> {
		e.getPlayer().startConversation(new SimonTempleton(e.getPlayer()));
	});

	public SimonTempleton(Player player) {
		super(player);
		if (player.getInventory().containsItem(6970)) {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "I have a pyramid top I can sell you!"));
			addNext(new Dialogue(new NPCStatement(npcId, HeadE.CHEERFUL, "Excellent job mate! Here's your money."), () -> {
				int totalMoney = player.getInventory().getAmountOf(6970) * 10000;
				player.getInventory().deleteItem(6970, Integer.MAX_VALUE);
				player.getInventory().addCoins(totalMoney);
			}));
			return;
		}
		if (playerHasArtefacts(player)) {
			addNext(new Dialogue()
					.addPlayer(HeadE.CHEERFUL, "I have some interesting artefacts I'd like you to look at."))
					.addNPC(npcId, HeadE.HAPPY_TALKING, "Bonzer! Let's have a Butcher's mate.")
					.addNPC(npcId, HeadE.HAPPY_TALKING, "Do you want to flog the lot of 'em?")
					.addOptions(("Would you like to sell all your artefacts?"), option -> {

						option.add("Yes, show me the money.", new Dialogue()
								.addNext(() -> {
									addPlayer(HeadE.HAPPY_TALKING, "Yes, show me the money!");
									for (int artefactId : ARTEFACT_IDS) {
										int count = player.getInventory().getNumberOf(artefactId);
										if (count > 0) {
											int tradeInValue = PPArtefact.forId(artefactId).getTradeInValue();
											player.getInventory().removeItems(new Item(artefactId, count));
											player.getInventory().addCoins(count * tradeInValue);
											player.sendMessage("Simon buys " + count + " artefacts from you for " + count * tradeInValue + " coins.");
										}
									}
								}));

						option.add("No, let me think about it.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "No, let me think about it.")
								.addNPC(npcId, HeadE.HAPPY_TALKING, "No probs mate. Just give me the items you want to sell and we can do them one by one.")
						);
					});
		} else {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "Hi, what do you do here?"));
			addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "I'll buy any special artefacts you find here in the desert. If you happen to find any pyramid tops, I'll buy them for 10,000 gold each."));
			addNext(new PlayerStatement(HeadE.CHEERFUL, "Great, I'll be sure to come back if I find any."));
		}
	}

	public static ItemOnNPCHandler handleSimonTempletonArtefacts = new ItemOnNPCHandler(new Object[]{ npcId }, e -> {
		String itemName = e.getItem().getName();
		switch (itemName.toUpperCase()) {
			case "IVORY COMB", "POTTERY SCARAB", "POTTERY STATUETTE", "STONE SEAL", "STONE SCARAB", "STONE STATUETTE", "GOLD SEAL", "GOLD SCARAB", "GOLD STATUETTE", "JEWELLED GOLDEN STATUETTE", "JEWELLED DIAMOND STATUETTE" -> {
				Item item = e.getItem();
				PPArtefact artifact = PPArtefact.forId(item.getId());
				saleD(e.getPlayer(), item, artifact.getTradeInValue(), item.getName().toLowerCase());
			}
			default -> e.getPlayer().startConversation(new Dialogue()
					.addNPC(npcId, HeadE.SHAKING_HEAD, "I don't want to buy that mate.")
			);
		}
	});

	public static void saleD(Player player,Item item, int value, String name) {
		player.startConversation(new Dialogue()
				.addNPC(npcId, HeadE.HAPPY_TALKING, "You wanna sell that " + name + " mate?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "I'll give you " + value + " for it.")
				.addOptions(("Would you like to sell all your " + name + " artefact?"), option -> {
					option.add("Yes, show me the money.", new Dialogue()
							.addNPC(npcId, HeadE.HAPPY_TALKING, "Cheers Mate!")
							.addNext(() -> {
								player.getInventory().removeItems(new Item(item.getId(), 1));
								player.getInventory().addCoins(value);
								player.sendMessage("Simon buys the " + name + " artefact from you for " + value + " coins." );

							})
					);
					option.add("No, I think I'll keep it.", new Dialogue()
							.addPlayer(HeadE.SHAKING_HEAD, "No, I think I'll keep it.")
					);
				}));
	}
}

