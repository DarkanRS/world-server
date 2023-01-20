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
package com.rs.game.content;

import java.util.Arrays;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class SkillCapeCustomizer {

	public static void resetSkillCapes(Player player) {
		player.setMaxedCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20767).originalModelColors, 4));
		player.setCompletionistCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20769).originalModelColors, 4));
	}

	public static void startCustomizing(Player player, int itemId) {
		int[] colors = itemId == 20767 ? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized();
		player.getTempAttribs().setO("scCustomColorArr", colors);
		player.getInterfaceManager().sendInterface(20);
		for (int i = 0; i < 4; i++)
			player.getVars().setVarBit(9254 + i, colors[i]);
		player.getPackets().setIFModel(20, 55, player.getAppearance().isMale() ? ItemDefinitions.getDefs(itemId).getMaleWornModelId1() : ItemDefinitions.getDefs(itemId).getFemaleWornModelId1());
	}

	public static void customizeSlot(Player player, int part) {
		int[] colors = player.getTempAttribs().getO("scCustomColorArr");
		if (colors == null) {
			player.closeInterfaces();
			return;
		}
		player.getInterfaceManager().sendInterface(19);
		player.getVars().setVar(2174, colors[part]);
		player.sendInputHSL(colorId -> {
			colors[part] = colorId;
			player.getVars().setVarBit(9254 + part, colorId);
			player.getInterfaceManager().sendInterface(20);
		});
	}

	public static ButtonClickHandler handleSkillCapeCustomizer = new ButtonClickHandler(20, e -> {
		int[] colors = e.getPlayer().getTempAttribs().getO("scCustomColorArr");
		if (colors == null) {
			e.getPlayer().closeInterfaces();
			return;
		}
		switch(e.getComponentId()) {
		case 58 -> {
			int[] orig = Arrays.copyOf(ItemDefinitions.getDefs(20767).originalModelColors, 4);
			for (int i = 0;i < orig.length;i++)
				colors[i] = orig[i];
			for (int i = 0; i < 4; i++)
				e.getPlayer().getVars().setVarBit(9254 + i, colors[i]);
		}
		case 34 -> customizeSlot(e.getPlayer(), 0);
		case 71 -> customizeSlot(e.getPlayer(), 1);
		case 83 -> customizeSlot(e.getPlayer(), 2);
		case 95 -> customizeSlot(e.getPlayer(), 3);
		case 114, 142 -> {
			e.getPlayer().getAppearance().generateAppearanceData();
			e.getPlayer().closeInterfaces();
		}
		}
	});
}
