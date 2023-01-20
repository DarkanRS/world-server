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
package com.rs.game.content.interfacehandlers;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.game.content.death.DeathOfficeController;
import com.rs.game.content.death.GraveStone;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class ItemsKeptOnDeath {

	public static ButtonClickHandler handleSwapWildy = new ButtonClickHandler(17, e -> {
		if (e.getComponentId() == 28)
			sendItemsKeptOnDeath(e.getPlayer(), !e.getPlayer().getTempAttribs().getB("wildy"));
	});

	public static void openItemsKeptOnDeath(Player player) {
		player.getInterfaceManager().sendInterface(17);
		sendItemsKeptOnDeath(player, false);
	}

	public static void sendItemsKeptOnDeath(Player player, boolean wilderness) {
		boolean skulled = player.hasSkull();
		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(player, wilderness, skulled, player.getPrayer().isProtectingItem());
		Item[][] items = GraveStone.getItemsKeptOnDeath(player, slots);
		long riskedWealth = 0;
		long carriedWealth = 0;
		for (Item item : items[1])
			carriedWealth = riskedWealth += item.getDefinitions().getValue() * item.getAmount();
		for (Item item : items[0])
			carriedWealth += item.getDefinitions().getValue() * item.getAmount();
		if (slots[0].length > 0) {
			for (int i = 0; i < slots[0].length; i++)
				player.getVars().setVarBit(9222 + i, slots[0][i]);
			player.getVars().setVarBit(9227, slots[0].length);
			player.save("protectSlots", slots[0].length);
		} else {
			player.getVars().setVarBit(9222, -1);
			player.getVars().setVarBit(9227, 1);
			player.save("protectSlots", 1);
		}
		player.getVars().setVarBit(9226, wilderness ? 1 : 0);
		player.getTempAttribs().setB("wildy", wilderness ? true : false);
		player.getVars().setVarBit(9229, skulled ? 1 : 0);
		StringBuffer text = new StringBuffer();
		text.append("The number of items kept on").append("<br>").append("death is normally 3.").append("<br>").append("<br>").append("<br>");
		if (wilderness)
			text.append("Your gravestone will not").append("<br>").append("appear.");
		else {
			int time = GraveStone.getMaximumTicks(player.getGraveStone());
			int seconds = (int) (time * 0.6);
			int minutes = seconds / 60;
			seconds -= minutes * 60;

			text.append("Gravestone:").append("<br>").append(EnumDefinitions.getEnum(1099).getStringValue(player.getGraveStone())).append("<br>").append("<br>").append("Initial duration:").append("<br>").append(minutes + ":" + (seconds < 10 ? "0" : "") + seconds).append("<br>");
		}
		text.append("<br>").append("<br>").append("Carried wealth:").append("<br>").append(carriedWealth > Integer.MAX_VALUE ? "Too high!" : Utils.getFormattedNumber((int) carriedWealth)).append("<br>").append("<br>").append("Risked wealth:").append("<br>").append(riskedWealth > Integer.MAX_VALUE ? "Too high!" : Utils.getFormattedNumber((int) riskedWealth)).append("<br>").append("<br>");
		if (wilderness)
			text.append("Your hub will be set to:").append("<br>").append("Edgeville.");
		else
			text.append("Current hub: " + EnumDefinitions.getEnum(3792).getStringValue(DeathOfficeController.getCurrentHub(player, player.getTile()).ordinal()));
		player.getPackets().sendVarcString(352, text.toString());
	}


}
