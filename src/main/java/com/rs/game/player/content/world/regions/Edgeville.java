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
package com.rs.game.player.content.world.regions;

import com.rs.game.ForceMovement;
import com.rs.game.pathing.Direction;
import com.rs.game.player.controllers.WildernessController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Edgeville  {
	
	public static LoginHandler setSafetyStrongholdPosterPulled = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(6278, 1);
		}
	};
	
	public static ObjectClickHandler handleWildernessDitch = new ObjectClickHandler(new Object[] { 29319, 29320 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getY() <= 9917) {
				e.getPlayer().getControllerManager().startController(new WildernessController());
				e.getPlayer().handleOneWayDoor(e.getObject());
			} else {
				e.getPlayer().handleOneWayDoor(e.getObject());
			}
		}
	};
	
	public static ObjectClickHandler handleJailEntrance = new ObjectClickHandler(new Object[] { 29603 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(3082, 4229, 0), 0, 1);
		}
	};
	
	public static ObjectClickHandler handleJailExit = new ObjectClickHandler(new Object[] { 29602 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(3074, 3456, 0), 0, 1);
		}
	};
	
	public static ObjectClickHandler handlePosterEntrance = new ObjectClickHandler(new Object[] { 29735 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(3140, 4230, 2), 0, 1);
		}
	};
	
	public static ObjectClickHandler handlePosterExit = new ObjectClickHandler(new Object[] { 29623 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(3077, 4235, 0), 0, 1);
		}
	};
	
	public static ObjectClickHandler handleJailDoors = new ObjectClickHandler(new Object[] { 29624 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getRotation() == 0) {
				if (e.getPlayer().getPlane() == 0)
					e.getPlayer().useStairs(-1, e.getPlayer().transform(0, 3, 2), 0, 1);
				else
					e.getPlayer().useStairs(-1, e.getPlayer().transform(0, -3, -2), 0, 1);
			} else {
				if (e.getPlayer().getPlane() == 0)
					e.getPlayer().useStairs(-1, e.getPlayer().transform(-1, 2, 1), 0, 1);
				else
					e.getPlayer().useStairs(-1, e.getPlayer().transform(1, -2, -1), 0, 1);
			}
		}
	};
	
	public static ObjectClickHandler handleEdgevilleMonkeybars = new ObjectClickHandler(new Object[] { 29375 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			final boolean isNorth = e.getPlayer().getY() > 9964;
			final WorldTile tile = new WorldTile(e.getPlayer().getX(), e.getPlayer().getY() + (isNorth ? -7 : 7), 0);
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(745));
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 1, tile, 5, isNorth ? Direction.SOUTH : Direction.NORTH));
			WorldTasksManager.schedule(new WorldTask() {
				int ticks = 0;

				@Override
				public void run() {
					ticks++;
					if (ticks > 1)
						e.getPlayer().setNextAnimation(new Animation(744));
					if (ticks == 5) {
						e.getPlayer().setNextWorldTile(tile);
						e.getPlayer().unlock();
						stop();
						return;
					}
				}
			}, 0, 0);
		}
	};
}
