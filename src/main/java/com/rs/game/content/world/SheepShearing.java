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

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class SheepShearing {

	public static ItemOnNPCHandler handleShearsOnSheep = new ItemOnNPCHandler(new Object[] { 5157, 1765, 43, 5160, 5161, 5156 }, e -> {
		final int npcId = e.getNPC().getId();
		if (Utils.getRandomInclusive(2) == 0) {
			e.getNPC().setNextForceTalk(new ForceTalk("Baa!"));
			e.getNPC().soundEffect(756);
			e.getNPC().addWalkSteps(npcId, npcId, 4, true);
			e.getNPC().setRun(true);
			e.getPlayer().sendMessage("The sheep runs away from you.");
		} else if (e.getPlayer().getInventory().containsItem(1735, 1)) {
			e.getPlayer().soundEffect(761);
			e.getPlayer().getInventory().addItem(1737, 1);
			e.getPlayer().sendMessage("You shear the sheep of it's fleece.");
			e.getPlayer().setNextAnimation(new Animation(893));
			e.getNPC().transformIntoNPC(5149);
			WorldTasks.delay(Ticks.fromSeconds(10), () -> e.getNPC().transformIntoNPC(npcId));
		} else
			e.getPlayer().sendMessage("You need a pair of shears to shear the sheep.");
	});

	public static NPCClickHandler handleShearOption = new NPCClickHandler(new Object[] { 5157, 1765, 43, 5160, 5161, 5156 }, e -> {
		final int npcId = e.getNPC().getId();
		if (Utils.getRandomInclusive(2) == 0) {
			e.getNPC().setNextForceTalk(new ForceTalk("Baa!"));
			e.getNPC().soundEffect(756);
			e.getNPC().addWalkSteps(npcId, npcId, 4, true);
			e.getNPC().setRun(true);
			e.getPlayer().sendMessage("The sheep runs away from you.");
		} else if (e.getPlayer().getInventory().containsItem(1735, 1)) {
			e.getPlayer().soundEffect(761);
			e.getPlayer().getInventory().addItem(1737, 1);
			e.getPlayer().sendMessage("You shear the sheep of it's fleece.");
			e.getPlayer().setNextAnimation(new Animation(893));
			e.getNPC().transformIntoNPC(5149);
			WorldTasks.delay(Ticks.fromSeconds(10), () -> e.getNPC().transformIntoNPC(npcId));
		} else
			e.getPlayer().sendMessage("You need a pair of shears to shear the sheep.");
	});

}
