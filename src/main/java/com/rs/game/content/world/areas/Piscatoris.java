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
package com.rs.game.content.world.areas;

import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.content.world.npcs.Banker;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Piscatoris {

	public static ObjectClickHandler handleColonyDoors = new ObjectClickHandler(new Object[] { 14929, 14931 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoubleDoors.handle(e);
		}
	};

	public static NPCClickHandler handleArnoldPiscatorisBanker = new NPCClickHandler(new Object[] { 3824 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("Talk-to"))
				e.getPlayer().startConversation(new Banker(e.getPlayer(), e.getNPC()));
			if(e.getOption().equalsIgnoreCase("Trade"))
				ShopsHandler.openShop(e.getPlayer(), "arnolds_eclectic_supplies");
			if(e.getOption().equalsIgnoreCase("Bank"))
				e.getPlayer().getBank().open();
			if(e.getOption().equalsIgnoreCase("Collect"))
				GE.openCollection(e.getPlayer());
		}
	};

	public static ObjectClickHandler handleColonyTunnels = new ObjectClickHandler(new Object[] { 14922 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!e.getPlayer().isQuestComplete(Quest.SWAN_SONG, "to enter the Piscatoris Fishing Colony."))
				return;
			final boolean isNorth = e.getPlayer().getY() > 3653;
			final WorldTile tile = isNorth ? new WorldTile(2344, 3650, 0) : new WorldTile(2344, 3655, 0);
			WorldTasks.schedule(new WorldTask() {
				int ticks = 0;

				@Override
				public void run() {
					e.getPlayer().lock();
					ticks++;
					if (ticks == 1) {
						e.getPlayer().setNextAnimation(new Animation(2589));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject(), 1, isNorth ? Direction.SOUTH : Direction.NORTH));
					} else if (ticks == 3) {
						e.getPlayer().setNextWorldTile(new WorldTile(2344, 3652, 0));
						e.getPlayer().setNextAnimation(new Animation(2590));
					} else if (ticks == 5)
						e.getPlayer().setNextAnimation(new Animation(2591));
					else if (ticks == 6) {
						e.getPlayer().setNextWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
						e.getPlayer().unlock();
						stop();
					}
				}
			}, 0, 0);
		}
	};

	public static ObjectClickHandler handleEaglesPeakShortcut = new ObjectClickHandler(new Object[] { 19849 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldObject obj = e.getObject();
			if (!Agility.hasLevel(p, 25)) {
				p.getPackets().sendGameMessage("You need level 25 agility to use this shortcut.");
				return;
			}
			if(obj.matches(new WorldTile(2323,3497, 0)))//above
				AgilityShortcuts.forceMovementInstant(p, new WorldTile(2322, 3502, 0), 2050, 1, 1, Direction.SOUTH);
			if(obj.matches(new WorldTile(2322,3501, 0)))//below
				AgilityShortcuts.forceMovementInstant(p, new WorldTile(2323, 3496, 0), 2049, 1, 1, Direction.SOUTH);

		}
	};
}
