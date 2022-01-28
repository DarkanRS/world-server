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
package com.rs.game.player.content.skills.construction;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class SawmillOperator  {

	private static int[] logs = { 1511, 1521, 6333, 6332 };
	private static int[] planks = { 960, 8778, 8780, 8782 };
	private static int[] prices = { 100, 250, 500, 1500 };

	public static NPCInteractionDistanceHandler sawmillDistance = new NPCInteractionDistanceHandler(4250) {
		@Override
		public int getDistance(Player player, NPC npc) {
			return 1;
		}
	};

	public static NPCClickHandler handleOps = new NPCClickHandler(4250) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 1)
				e.getPlayer().getInterfaceManager().sendInterface(403);
			else if (e.getOpNum() == 3)
				e.getPlayer().getInterfaceManager().sendInterface(403);
			else if (e.getOpNum() == 4)
				ShopsHandler.openShop(e.getPlayer(), "construction_supplies");
		}
	};

	public static ButtonClickHandler handleSawmillInter = new ButtonClickHandler(403) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 12 && e.getComponentId() <= 15) {
				final int log = logs[e.getComponentId()-12];
				final int pricePer = prices[e.getComponentId()-12];
				final int plank = planks[e.getComponentId()-12];
				if (e.getPacket() == ClientPacket.IF_OP1)
					openAreYouSure(e.getPlayer(), log, 1, pricePer, plank);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					openAreYouSure(e.getPlayer(), log, 5, pricePer, plank);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					openAreYouSure(e.getPlayer(), log, 10, pricePer, plank);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().sendInputInteger("How many planks would you like to make?", (number) -> openAreYouSure(e.getPlayer(), log, number, pricePer, plank));
				else if (e.getPacket() == ClientPacket.IF_OP5)
					openAreYouSure(e.getPlayer(), log, 28, pricePer, plank);
			}
		}
	};

	public static void openAreYouSure(Player player, final int log, int amount, final int pricePer, final int plank) {
		if (amount > player.getInventory().getNumberOf(log))
			amount = player.getInventory().getNumberOf(log);
		if (amount == 0) {
			player.sendMessage("You don't have any logs to convert.");
			return;
		}
		final int finalAmount = amount;
		final int finalPrice = amount*pricePer;
		player.sendOptionDialogue("Pay "+finalPrice+" gold to make "+finalAmount+" planks?", new String[] {"Make "+finalAmount+" planks. ("+finalPrice+" coins)", "That's too much money."}, new DialogueOptionEvent() {

			@Override
			public void run(Player player) {
				if (getOption() == 1) {
					if (!player.getInventory().containsItem(log, finalAmount))
						return;
					if (!player.getInventory().containsItem(995, finalPrice)) {
						player.sendMessage("You don't have enough money to make the planks.");
						return;
					}
					player.getInventory().deleteItem(995, finalPrice);
					player.getInventory().deleteItem(log, finalAmount);
					player.getInventory().addItem(plank, finalAmount);
				}
			}

		});
	}

	public static int[] getPlankForLog(int item) {
		for (int i = 0;i < logs.length;i++)
			if (item == logs[i])
				return new int[] { planks[i], prices[i] };
		return null;
	}
}
