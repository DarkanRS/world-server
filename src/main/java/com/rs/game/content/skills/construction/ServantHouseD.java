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
package com.rs.game.content.skills.construction;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;

public class ServantHouseD extends Conversation {

	public ServantHouseD(Player player, ServantNPC servant, boolean bankOps) {
		super(player);
		servant.setFollowing(true);
		if (player.getHouse().getPaymentStage() == 10) {
			addNPC(servant.getId(), HeadE.CALM_TALK, "Excuse me, but before I can continue working you must pay my fee.");
			addOptions("Would you you like to pay the fee of " + servant.getServantData().getCost() + "?", ops -> {
				ops.add("Yes.", () -> {
					int cost = servant.getServantData().getCost();
					if (player.getInventory().getNumberOf(995) < cost) {
						player.npcDialogue(servant.getId(), HeadE.UPSET, "You do not have enough coins to cover up my cost.");
						return;
					}
					player.getInventory().deleteItem(995, cost);
					player.getHouse().resetPaymentStage();
					player.npcDialogue(servant.getId(), HeadE.CHEERFUL, "Thank you!");
				});
				ops.add("No.");
				ops.add("Fire.", () -> fireServant(player, servant));
			});
			return;
		}
		
		if (bankOps) {
			addNext(getBankOptions(player, servant));
			return;
		}
			
		addNPC(servant.getId(), HeadE.CALM_TALK, "I am at thy command, my master");
		addOptions(ops -> {
			ops.add("Go to the bank/sawmill...")
				.addNext(getBankOptions(player, servant));
			
			ops.add("Misc...")
				.addOptions(misc -> {
					misc.add("Make tea")
						.addNPC(servant.getId(), HeadE.CALM_TALK, "Thou shall taste the very tea of the Demon Lords themselves!")
						.addNext(() -> servant.makeFood(HouseConstants.TEA_BUILDS));
					misc.add("Serve dinner")
						.addNPC(servant.getId(), HeadE.CALM_TALK, "I shall prepare thee a banquet fit for the lords of Pandemonium!")
						.addNext(() -> servant.makeFood(HouseConstants.DINNER_BUILDS));
					misc.add("Serve drinks")
						.addPlayer(HeadE.CALM_TALK, "Serve drinks please.")
						.addNext(() -> servant.makeFood(HouseConstants.TEA_BUILDS));
					misc.add("Greet guests")
						.addPlayer(HeadE.CALM_TALK, "Stay at the entrance and greet guests.")
						.addNext(() -> {
							servant.setGreetGuests(true);
							servant.setFollowing(false);
							servant.setNextWorldTile(servant.getRespawnTile());
						});
				});
			
			ops.add("Stop following me", () -> servant.setFollowing(false));
			ops.add("You're fired", () -> fireServant(player, servant));
		});
	}
	
	private Dialogue getBankOptions(Player player, ServantNPC servant) {
		return new Dialogue().addOptions(ops -> {
			ops.add("Take something to the bank").
				addNPC(servant.getId(), HeadE.CALM_TALK, "Give any item to me and I shall take it swiftly to the bank where it will be safe from thieves and harm.");
			
			ops.add("Bring something from the bank")
				.addOptions(bank -> {
					for (int itemId : HouseConstants.BANKABLE_ITEMS) {
						bank.option(ItemDefinitions.getDefs(itemId).name, () -> {
							player.sendInputInteger("How many would you like?", amount -> {
								if (!player.getHouse().isLoaded() || !player.getHouse().getPlayers().contains(player))
									return;
								player.getHouse().getServantInstance().requestType(itemId, amount, (byte) 0);
							});
						});
					}
				});
			
			if (servant.getServantData().isSawmill()) {
				ops.add("Take something to the sawmill")
						.addNPC(servant.getId(), HeadE.CALM_TALK, "Give me some logs and I will return as fast as possible.")
						.addOptions("Choose logs to send...", option ->{
							if(player.getInventory().containsItem(1511))
								option.add("Logs", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Take these logs to the sawmill...", ()->{
									int logAmount = player.getInventory().getAmountOf(1511);
									int coinAmount = player.getInventory().getAmountOf(995);
									int cost = logAmount*100;
									if(coinAmount > cost) {
										int previousID = player.getHouse().getServantInstance().getId();
										player.getHouse().getServantInstance().transformIntoNPC(1957);
										player.lock(10);
										WorldTasks.schedule(9, () -> {
											player.getHouse().getServantInstance().transformIntoNPC(previousID);
											player.getInventory().removeItems(new Item(995, cost));
											player.getInventory().removeItems(new Item(1511, logAmount));
											player.getInventory().addItem(new Item(960, logAmount));
										});
										return;
									}
									player.sendMessage("You didn't have enough coins for your inventory!");
								}));
							if(player.getInventory().containsItem(1521))
								option.add("Oak Logs", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Take these oak logs to the sawmill...", ()->{
									int logAmount = player.getInventory().getAmountOf(1521);
									int coinAmount = player.getInventory().getAmountOf(995);
									int cost = logAmount*250;
									if(coinAmount > cost) {
										int previousID = player.getHouse().getServantInstance().getId();
										player.getHouse().getServantInstance().transformIntoNPC(1957);
										player.lock(10);
										WorldTasks.schedule(9, () -> {
											player.getHouse().getServantInstance().transformIntoNPC(previousID);
											player.getInventory().removeItems(new Item(995, cost));
											player.getInventory().removeItems(new Item(1521, logAmount));
											player.getInventory().addItem(new Item(8778, logAmount));
										});
										return;
									}
									player.sendMessage("You didn't have enough coins for your inventory!");
								}));
							if(player.getInventory().containsItem(6333))
								option.add("Teak Logs", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Take these teak logs to the sawmill...", ()->{
									int logAmount = player.getInventory().getAmountOf(6333);
									int coinAmount = player.getInventory().getAmountOf(995);
									int cost = logAmount*500;
									if(coinAmount > cost) {
										int previousID = player.getHouse().getServantInstance().getId();
										player.getHouse().getServantInstance().transformIntoNPC(1957);
										player.lock(10);
										WorldTasks.schedule(9, () -> {
											player.getHouse().getServantInstance().transformIntoNPC(previousID);
											player.getInventory().removeItems(new Item(995, cost));
											player.getInventory().removeItems(new Item(6333, logAmount));
											player.getInventory().addItem(new Item(8780, logAmount));
										});
										return;
									}
									player.sendMessage("You didn't have enough coins for your inventory!");
								}));
							if(player.getInventory().containsItem(6332))
								option.add("Mahogany Logs", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Take these mahogany logs to the sawmill...", ()->{
									int logAmount = player.getInventory().getAmountOf(6332);
									int coinAmount = player.getInventory().getAmountOf(995);
									int cost = logAmount*1500;
									if(coinAmount > cost) {
										int previousID = player.getHouse().getServantInstance().getId();
										player.getHouse().getServantInstance().transformIntoNPC(1957);
										player.lock(10);
										WorldTasks.schedule(9, () -> {
											player.getHouse().getServantInstance().transformIntoNPC(previousID);
											player.getInventory().removeItems(new Item(995, cost));
											player.getInventory().removeItems(new Item(6332, logAmount));
											player.getInventory().addItem(new Item(8782, logAmount));
										});
										return;
									}
									player.sendMessage("You didn't have enough coins for your inventory!");
								}));
							option.add("Nevermind", new Dialogue().addPlayer(HeadE.CALM_TALK, "Nevermind..."));
						});
			}
		});
	}
	
	private void fireServant(Player player, ServantNPC servant) {
		player.sendOptionDialogue("Do you really want to fire your servant?", ops -> {
			ops.add("Yes.", () -> {
				player.playerDialogue(HeadE.CALM_TALK, "You are dismissed...");
				servant.fire();
			});
			ops.add("No.");
		});
	}
}
