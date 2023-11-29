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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.content.skills.util.CreateActionD;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;

public class DungeoneeringSmithing extends PlayerAction {

	private static final Item[][] materials = { { new Item(17630) }, { new Item(17632) }, { new Item(17634) }, { new Item(17636) }, { new Item(17638) }, { new Item(17640) }, { new Item(17642) }, { new Item(17644) }, { new Item(17646) }, { new Item(17648) } };
	private static final Item[][] products = { { new Item(17650) }, { new Item(17652) }, { new Item(17654) }, { new Item(17656) }, { new Item(17658) }, { new Item(17660) }, { new Item(17662) }, { new Item(17664) }, { new Item(17666) }, { new Item(17668) } };
	private static final int[] reqs = { 1, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
	private static final double[] xp = { 7.0, 13.0, 19.0, 25.0, 32.0, 38.0, 44.0, 51.0, 57.0, 63.0 };
	private static final int[] anims = { 3243, 3243, 3243, 3243, 3243, 3243, 3243, 3243, 3243, 3243 };

	private static final int[] BASE_COMPONENTS = { 113, 109, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105 };

	public static void openSmelting(Player player) {
		player.startConversation(new CreateActionD(player, materials, products, xp, anims, reqs, Constants.SMITHING, 3));
	}

	public static void openInterface(Player player, Item item) {
		if (item == null)
			return;
		DungSmithables[] prods = DungSmithables.forBar(item.getId());
		if (prods == null) {
			player.sendMessage("You can't smith that.");
			return;
		}
		player.getTempAttribs().setI("dungBar", item.getId());
		player.getInterfaceManager().sendInterface(934);
		for (int i = 0;i < prods.length;i++) {
			player.getPackets().setIFItem(934, BASE_COMPONENTS[i], prods[i].product.getId(), prods[i].product.getAmount());
			player.getPackets().setIFText(934, BASE_COMPONENTS[i] + 1, (prods[i].canMake(player) ? "<col=00FF00>" : "<col=FF0000>") + prods[i].product.getName());
			player.getPackets().setIFText(934, BASE_COMPONENTS[i] + 2, prods[i].materials[0].getAmount() + " " + prods[i].materials[0].getName());
			player.getPackets().sendRunScriptReverse(2263, prods[i].product.getName(), prods[i].product.getName(), prods[i].product.getAmount(), (934 << 16 | BASE_COMPONENTS[i] + 3));
		}
	}

	public static int getIndex(int component) {
		for (int i = 0;i < BASE_COMPONENTS.length;i++)
			if ((BASE_COMPONENTS[i]+3) == component)
				return i;
		return -1;
	}

	public static void handleButtons(Player player, ClientPacket packetId, int componentId) {
		if (player.getTempAttribs().getI("dungBar") == -1) {
			player.closeInterfaces();
			return;
		}
		int barId = player.getTempAttribs().getI("dungBar");
		DungSmithables[] prods = DungSmithables.forBar(barId);
		if (prods == null)
			return;

		int index = getIndex(componentId);
		if (index == -1 || index >= prods.length || prods[index] == null)
			return;
		if (packetId == ClientPacket.IF_OP1)
			player.getActionManager().setAction(new DungeoneeringSmithing(prods[index], 1));
		else if (packetId == ClientPacket.IF_OP2)
			player.getActionManager().setAction(new DungeoneeringSmithing(prods[index], 5));
		else if (packetId == ClientPacket.IF_OP3) {
			player.sendInputInteger("How many would you like to make?", number -> player.getActionManager().setAction(new DungeoneeringSmithing(prods[index], number)));
		} else
			player.getActionManager().setAction(new DungeoneeringSmithing(prods[index], 28));
	}

	private final DungSmithables bar;
	private int count;

	public DungeoneeringSmithing(DungSmithables bar, int count) {
		this.bar = bar;
		this.count = count;
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItem(17883, 1)) {
			player.sendMessage("You need a hammer in order to work with metals.");
			return false;
		}
		if (!bar.canMake(player)) {
			player.sendMessage("You need a smithing level of " + bar.req + " and " + bar.materials[0].getAmount() + " " + bar.materials[0].getName().toLowerCase() +"s to make that.");
			return false;
		}

		if (player.getInterfaceManager().containsScreenInter()) {
			player.getInterfaceManager().removeCentralInterface();
			return true;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(898));

		int numBars = bar.materials[0].getAmount();

		if (player.hasScrollOfEfficiency && numBars > 3)
			if (Utils.getRandomInclusive(15) == 5)
				numBars--;

		player.getInventory().deleteItem(bar.materials[0].getId(), numBars);
		player.getInventory().addItem(bar.product);
		player.getSkills().addXp(Constants.SMITHING, bar.xp);
		if (--count == 0)
			return -1;
		return 2;
	}

	@Override
	public boolean start(Player player) {
		if (!player.getInventory().containsItem(17883, 1)) {
			player.sendMessage("You need a hammer in order to work with metals.");
			return false;
		}
		if (!bar.canMake(player)) {
			player.sendMessage("You need a smithing level of " + bar.req + " and " + bar.materials[0].getAmount() + " " + bar.materials[0].getName().toLowerCase() +"s to make that.");
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 2);
	}

}
