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
package com.rs.game.content.world.areas.dungeons;

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class TaverlyDungeon {

	public static ObjectClickHandler handleVSBSecretLocation = new ObjectClickHandler(new Object[] { 11901, 11902 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getObjectId() == 11901 ? new WorldTile(4498, 5680, 0) : new WorldTile(2915, 9673, 0));
		}
	};

	public static ObjectClickHandler handlePipeSqueeze = new ObjectClickHandler(new Object[] { 9293 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSkills().getLevel(Constants.AGILITY) < 70) {
				e.getPlayer().sendMessage("You need an agility level of 70 to use this obstacle.", true);
				return;
			}
			int x = e.getPlayer().getX() == 2886 ? 2892 : 2886;
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().setNextAnimation(new Animation(10580));
				}
			}, 0);
			e.getPlayer().setNextForceMovement(new ForceMovement(new WorldTile(x, 9799, 0), 3, e.getPlayer().getX() == 2886 ? Direction.WEST : Direction.EAST));
			e.getPlayer().useStairs(-1, new WorldTile(x, 9799, 0), 3, 4);
		}
	};

	public static ObjectClickHandler handleStrangeFloor = new ObjectClickHandler(new Object[] { 9294 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 80))
				return;
			final boolean isRunning = e.getPlayer().getRun();
			final boolean isSouth = e.getPlayer().getY() > 9812;
			final WorldTile tile = isSouth ? new WorldTile(2878, 9812, 0) : new WorldTile(2881, 9814, 0);
			e.getPlayer().setRun(true);
			e.getPlayer().addWalkSteps(isSouth ? 2881 : 2877, isSouth ? 9814 : 9812);
			WorldTasks.schedule(new WorldTask() {
				int ticks = 0;

				@Override
				public void run() {
					ticks++;
					if (ticks == 2)
						e.getPlayer().setNextFaceWorldTile(e.getObject());
					else if (ticks == 3) {
						e.getPlayer().setNextAnimation(new Animation(1995));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, tile, 4, Utils.getAngleTo(e.getObject().getX() - e.getPlayer().getX(), e.getObject().getY() - e.getPlayer().getY())));
					} else if (ticks == 4)
						e.getPlayer().setNextAnimation(new Animation(1603));
					else if (ticks == 7) {
						e.getPlayer().setNextWorldTile(tile);
						e.getPlayer().setRun(isRunning);
						stop();
						return;
					}
				}
			}, 0, 0);
		}
	};

	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 66991, 66992 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getObjectId() == 66991 ? new WorldTile(2885, 9795, 0) : new WorldTile(2885, 3395, 0));
		}
	};

}
