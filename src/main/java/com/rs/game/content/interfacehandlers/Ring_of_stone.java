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

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Ring_of_stone {

	public static ItemClickHandler handleItemOption = new ItemClickHandler(new Object[] { "Ring of stone", }, new String[] { "Wear" }, e -> {
		if (e.getPlayer().inCombat(10000) || e.getPlayer().hasBeenHit(10000)) {
			e.getPlayer().sendMessage("You wouldn't want to use that right now.");
			return;
		}
		if (e.getItem().getName().equals("Ring of stone"))
			transformInto(e.getPlayer(), 2626);

	});




	public static void transformInto(Player player, int npcId) {
		player.stopAll(true, true, true);
		player.lock();
		player.getAppearance().transformIntoNPC(npcId);
		player.getInterfaceManager().sendSub(Sub.TAB_INVENTORY, 375);
		player.getTempAttribs().setB("TransformationRing", true);
	}

	public static void deactivateTransformation(Player player) {
		player.getTempAttribs().removeB("TransformationRing");
		player.unlock();
		player.setNextAnimation(new Animation(14884));
		player.getAppearance().transformIntoNPC(-1);
		player.getInterfaceManager().sendSubDefault(Sub.TAB_INVENTORY);
	}

	public static void triggerDeactivation(Player player) {
		if (player.getTempAttribs().getB("TransformationRing"))
			deactivateTransformation(player);
	}

}
