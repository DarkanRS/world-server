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
package com.rs.game.content.minigames.pest;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.quest.Quest;
import com.rs.game.content.skills.herblore.HerbCleaning.Herbs;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class CommendationExchange {
	private static final int INTERFACE = 1011;

	public static void openExchangeShop(Player player) {
		player.getInterfaceManager().sendInterface(INTERFACE);
		player.getVars().setVar(1875, 1250);
		refreshPoints(player);
	}

	public static NPCClickHandler tyrKorasiBuy = new NPCClickHandler(new Object[] { 11681 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.VOID_STARES_BACK, "to buy Korasi's sword"))
			return;
		e.getPlayer().sendOptionDialogue("Would you like to buy Korasi's sword for 200,000 coins?", ops -> {
			ops.add("Yes, that sounds like a fair price.", () -> {
				if (e.getPlayer().getInventory().getCoinsAsInt() < 200_000) {
					e.getPlayer().sendMessage("You don't have enough money for that.");
					return;
				}
				e.getPlayer().getInventory().removeCoins(200_000);
				e.getPlayer().getInventory().addItemDrop(19784, 1);
			});
			ops.add("Nevermind.");
		});
	});

	public static ItemOnNPCHandler handleEliteUpgrade = new ItemOnNPCHandler(new Object[] { 11681 }, e -> {
		if (e.getItem().getId() == 10611 || e.getItem().getId() == 8840) {
			if (!e.getPlayer().isQuestComplete(Quest.VOID_STARES_BACK, "to upgrade void knight armor"))
				return;
			if (e.getPlayer().getPestPoints() < 100) {
				e.getPlayer().sendMessage("You need 100 Void Knight Commendations to upgrade void knight armor.");
				return;
			}
			e.getPlayer().sendOptionDialogue("Upgrade this piece to elite for 100 commendations?", ops -> {
				ops.add("Yes, upgrade my armor.", () -> {
					if (e.getPlayer().getPestPoints() >= 100) {
						e.getPlayer().setPestPoints(e.getPlayer().getPestPoints()-100);
						e.getItem().setId(e.getItem().getId() == 10611 ? 19785 : 19786);
					}
				});
				ops.add("Nevermind.");
			});
		}
	});

	public static NPCClickHandler handleVoidExchange = new NPCClickHandler(new Object[] { 12195, "Void Knight" }, new String[] { "Exchange" }, e ->
			openExchangeShop(e.getPlayer()));

	private static void refreshPoints(Player player) {
		player.getVars().setVarBit(2086, player.getPestPoints());
	}

	public static void confirmBuy(Player player) {
		player.getPackets().setIFHidden(INTERFACE, 71, true);
		Reward reward = player.getTempAttribs().removeO("pcShopBuy");
		if (reward == null)
			return;
		int currentPoints = player.getPestPoints();
		if (!player.hasRights(Rights.DEVELOPER) && currentPoints - reward.cost < 0) {
			player.sendMessage("You don't have enough Commendations remaining to complete this exchange.");
			return;
		}
		player.setPestPoints(currentPoints - reward.cost);
		refreshPoints(player);
		reward.give.run();
	}

	public static void buy(Player player, String reward, int cost, Runnable give) {
		player.getPackets().setIFText(1011, 380, "Are you sure you wish to exchange "+cost+" Commendations in return for a "+reward+"?");
		player.getPackets().setIFHidden(INTERFACE, 71, false);
		player.getTempAttribs().setO("pcShopBuy", new Reward(cost, give));
	}

	private record Reward(int cost, Runnable give) { }

	static final int[] COMPS = {15, 196, 208, 220, 232, 244, 256, 268, 280};
	static final int[] VOIDS = {11665, 11664, 11663, 10611, 8840, 8842, 8841, 19712, 11666};
	static final int[] COSTS = {200, 200, 200, 250, 250, 150, 250, 150, 10};

	public static void buySkillXp(Player player, int skillId, int componentId) {
		if (player.getSkills().getLevelForXp(skillId) < 25) {
			player.sendMessage("You need 25 " + Skills.SKILL_NAME[skillId] + " to purchase experience.");
			return;
		}
		double multiplier = switch(skillId) {
			case Skills.PRAYER -> 18.0;
			case Skills.MAGIC, Skills.RANGE -> 32.0;
			default -> 35.0;
		};
		double baseXp = (player.getSkills().getLevelForXp(skillId) * player.getSkills().getLevelForXp(skillId)) / 600.0;
		final double xp = Math.floor(baseXp * multiplier) * getXpCost(componentId);
		buy(player, Utils.formatNumber((int) xp) + " " + Skills.SKILL_NAME[skillId] + " XP", getXpCost(componentId), () -> player.getSkills().addXp(skillId, xp));
	}

	private static int getXpCost(int componentId) {
		return switch(componentId) {
			case 68, 100, 116, 132, 148, 164, 180 -> 1;
			case 86, 102, 118, 134, 150, 166, 182 -> 10;
			case 88, 104, 120, 136, 152, 168, 184 -> 100;
			default -> 1;
		};
	}

	public static ButtonClickHandler handleButtonOptions = new ButtonClickHandler(1011, e -> {
		switch(e.getComponentId()) {
			case 15, 196, 208, 220, 232, 244, 256, 268, 280 -> {
				if (!e.getPlayer().getSkills().hasRequirements(Constants.ATTACK, 42, Constants.STRENGTH, 42, Constants.DEFENSE, 42, Constants.HITPOINTS, 42, Constants.RANGE, 42, Constants.MAGIC, 42, Constants.PRAYER, 22)) {
					e.getPlayer().sendMessage("You need level 42 Attack, Strength, Defence, Constitution, Ranged, Magic, and 22 Prayer in to purchase void equipment.");
					return;
				}
				int search = -1;
				for (int i = 0;i < COMPS.length;i++)
					if (COMPS[i] == e.getComponentId())
						search = i;
				final int slot = search;
				buy(e.getPlayer(), ItemDefinitions.getDefs(VOIDS[slot]).name, COSTS[slot], () -> e.getPlayer().getInventory().addItemDrop(VOIDS[slot], 1));
			}

			case 68, 86, 88 -> buySkillXp(e.getPlayer(), Skills.ATTACK, e.getComponentId());
			case 100, 102, 104 -> buySkillXp(e.getPlayer(), Skills.STRENGTH, e.getComponentId());
			case 116, 118, 120 -> buySkillXp(e.getPlayer(), Skills.DEFENSE, e.getComponentId());
			case 132, 134, 136 -> buySkillXp(e.getPlayer(), Skills.HITPOINTS, e.getComponentId());
			case 148, 150, 152 -> buySkillXp(e.getPlayer(), Skills.RANGE, e.getComponentId());
			case 164, 166, 168 -> buySkillXp(e.getPlayer(), Skills.MAGIC, e.getComponentId());
			case 180, 182, 184 -> buySkillXp(e.getPlayer(), Skills.PRAYER, e.getComponentId());

			case 291 -> {
				if (e.getPlayer().getSkills().getLevelForXp(Constants.HERBLORE) < 25) {
					e.getPlayer().sendMessage("You need an Herblore level of 25 in order to purchase an herblore pack.");
					return;
				}
				buy(e.getPlayer(), "herb pack", 30, () -> {
					for (int i = 0;i < 5;i++) {
						Item[] herb = DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("herb"));
						if (herb.length <= 0)
							continue;
						e.getPlayer().getInventory().addItemDrop(herb[0].getId()+1, 1);
					}
				});
			}

			case 302 -> {
				if (e.getPlayer().getSkills().getLevelForXp(Constants.MINING) < 25) {
					e.getPlayer().sendMessage("You need an Mining level of 25 in order to purchase a mineral pack.");
					return;
				}
				buy(e.getPlayer(), "mineral pack", 15, () -> {
					e.getPlayer().getInventory().addItem(441, Utils.random(20), true);
					e.getPlayer().getInventory().addItem(454, Utils.random(30), true);
				});
			}

			case 313 -> {
				if (e.getPlayer().getSkills().getLevelForXp(Constants.FARMING) < 25) {
					e.getPlayer().sendMessage("You need an Farming level of 25 in order to purchase a seed pack.");
					return;
				}
				buy(e.getPlayer(), "seed pack", 15, () -> {
					for (int i = 0;i < 6;i++)
						for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_shit_seed")))
							e.getPlayer().getInventory().addItemDrop(rew);
				});
			}

			case 324, 326, 328 -> e.getPlayer().sendInputInteger("How many would you like to buy?", amt -> buy(e.getPlayer(), Utils.formatNumber(amt) + " spinner charms", amt*2, () -> e.getPlayer().getInventory().addItemDrop(12166, amt)));
			case 339, 341, 343 -> e.getPlayer().sendInputInteger("How many would you like to buy?", amt -> buy(e.getPlayer(), Utils.formatNumber(amt) + " torcher charms", amt*2, () -> e.getPlayer().getInventory().addItemDrop(12167, amt)));
			case 354, 356, 358 -> e.getPlayer().sendInputInteger("How many would you like to buy?", amt -> buy(e.getPlayer(), Utils.formatNumber(amt) + " ravager charms", amt*2, () -> e.getPlayer().getInventory().addItemDrop(12164, amt)));
			case 369, 371, 373 -> e.getPlayer().sendInputInteger("How many would you like to buy?", amt -> buy(e.getPlayer(), Utils.formatNumber(amt) + " shifter charms", amt*2, () -> e.getPlayer().getInventory().addItemDrop(12165, amt)));


			case 383 -> confirmBuy(e.getPlayer());
			case 385 -> {
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 71, true);
				e.getPlayer().getTempAttribs().removeO("pcShopBuy");
			}

			case 20, 73 -> {
				//experience
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 70, true);
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 69, true);
			}
			case 24, 31 -> {
				//consumables
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 70, false);
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 69, true);
			}
			case 29, 75 -> {
				//equipment
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 70, true);
				e.getPlayer().getPackets().setIFHidden(INTERFACE, 69, false);
			}

			default -> e.getPlayer().sendMessage("Component: " + e.getComponentId());
		}
	});
}