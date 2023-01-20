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
package com.rs.game.content.achievements;

import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class AchievementInterface {

	//3483 enum for list of achievements

	public static void init(Player player) {
		player.getVars().setVarBit(8575, 1); //task set
		player.getVars().setVarBit(8582, 1); //task set
		player.getVars().setVarBit(8581, -1); //refresh?..
		player.getPackets().sendVarc(1423, 0); //completed in set
		player.getPackets().sendVarc(1424, 0); //total in set
		player.getVars().setVarBit(8601, 533); //total completed overall 534 unlocks taskmaster
		for (int i = 0;i < 6;i++)
			player.getVars().setVarBit(8587+i, 4095);
		player.getPackets().setIFHidden(1056, 117, true);
	}

	public static void openInterface(Player player) {
		player.getInterfaceManager().sendInterface(917);
		player.getPackets().setIFRightClickOps(917, 67, 0, 532, 0, 1);
		player.getPackets().setIFRightClickOps(917, 147, 0, 10, 0);
	}

	public static ButtonClickHandler handleTabButtons = new ButtonClickHandler(1056, e -> {
		switch (e.getComponentId()) {
		case 173:
			openInterface(e.getPlayer());
			break;
		default:
			e.getPlayer().sendMessage("Unhandled Achievement Tab button: " + e.getComponentId() + ", " + e.getSlotId());
			break;
		}
	});

	public static ButtonClickHandler handleInterfaceButtons = new ButtonClickHandler(917, e -> {
		switch (e.getComponentId()) {
		case 147:
			e.getPlayer().getVars().setVarBit(8582, e.getSlotId());
			e.getPlayer().getVars().setVarBit(8581, 0);
			break;
		case 160:
		case 161:
			e.getPlayer().getVars().setVarBit(8579, e.getComponentId() == 160 ? 0 : 1);
			break;
		case 162:
		case 163:
			e.getPlayer().getVars().setVarBit(8580, e.getComponentId() == 162 ? 0 : 1);
			break;
		default:
			e.getPlayer().sendMessage("Unhandled Achievement Interface button: " + e.getComponentId() + ", " + e.getSlotId());
			break;
		}
	});
}
