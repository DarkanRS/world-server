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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions.dungeons;

import com.rs.game.player.content.skills.slayer.TaskMonster;
import com.rs.game.player.controllers.KuradalDungeonController;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class KuradalsDungeon {
	
	public static ObjectClickHandler handleBarriers = new ObjectClickHandler(new Object[] { 47236 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getRotation() == 2) {
				e.getPlayer().walkOneStep(e.getPlayer().getX() > e.getObject().getX() ? -1 : 1, 0, false);
			} else {
				e.getPlayer().walkOneStep(0, e.getPlayer().getY() == e.getObject().getY() ? -1 : 1, false);
			}
		}
	};
	
	public static ObjectClickHandler handleEntrances = new ObjectClickHandler(new Object[] { 47232 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSlayer().getTask() != null) {
				TaskMonster currentTask = e.getPlayer().getSlayer().getTask().getMonster();
				switch (currentTask) {
					case HELLHOUNDS:
					case GREATER_DEMONS:
					case BLUE_DRAGONS:
					case GARGOYLES:
					case ABYSSAL_DEMONS:
					case DARK_BEASTS:
					case IRON_DRAGONS:
					case STEEL_DRAGONS:
						e.getPlayer().getControllerManager().startController(new KuradalDungeonController());
						return;
					default:
				}
			}
			e.getPlayer().sendMessage("Sorry, this dungeon is exclusive only to those who need to go in there.");
		}
	};
}
