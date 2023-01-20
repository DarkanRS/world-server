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
package com.rs.game.content.world;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class GraveStoneSelection {

	public static void openSelectionInterface(Player player) {
		player.getInterfaceManager().sendInterface(652);
		player.getPackets().setIFRightClickOps(652, 31, 0, 78, 0, 1);
		player.getPackets().setIFRightClickOps(652, 34, 0, 13, 0, 1);
		player.getVars().setVar(1146, player.getGraveStone() | 262112);
	}

	public static ButtonClickHandler handleSelectionInterface = new ButtonClickHandler(652, e -> {
		if (e.getComponentId() == 31)
			e.getPlayer().getTempAttribs().setI("graveSelection", e.getSlotId());
		else if (e.getComponentId() == 34)
			confirmSelection(e.getPlayer());
	});

	private static String getStoneName(int slot) {
		return EnumDefinitions.getEnum(1099).getStringValue(slot);
	}

	private static int getStonePrice(int slot) {
		return EnumDefinitions.getEnum(1101).getIntValue(slot);
	}

	public static void confirmSelection(Player player) {
		int slot = player.getTempAttribs().getI("graveSelection", -1) / 6;
		int price = getStonePrice(slot);
		String name = getStoneName(slot);
		if (slot != -1) {
			if (!player.getInventory().hasCoins(price)) {
				player.sendMessage("You need " + Utils.formatNumber(price) + " coins to purchase " + Utils.addArticle(name) + ".");
				return;
			}
			player.getInventory().removeCoins(price);
			player.setGraveStone(slot);
			player.closeInterfaces();
		}
	}
}