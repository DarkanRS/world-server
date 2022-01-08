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

import com.rs.game.World;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.controllers.GodwarsController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Trollheim {

	public static LoginHandler unlockSabbotCavern = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(10762, 3); //1 = mineable, 2 = being mined, 3 = mined
		}
	};

	public static ObjectClickHandler handleTrollweissCaveEnter = new ObjectClickHandler(new Object[] { 5012 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2799, 10134, 0));
		}
	};

	public static ObjectClickHandler handleTrollweissCaveExit = new ObjectClickHandler(new Object[] { 5013 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2796, 3719, 0));
		}
	};

	public static ObjectClickHandler handleTrollheimCaveExits = new ObjectClickHandler(new Object[] { 3758 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.objectAt(2906, 10036))
				e.getPlayer().setNextWorldTile(new WorldTile(2922, 3658, 0));
			else if (e.objectAt(2906, 10017))
				e.getPlayer().setNextWorldTile(new WorldTile(2911, 3636, 0));
		}
	};

	public static ObjectClickHandler handleGodwarsEntrance = new ObjectClickHandler(new Object[] { 26342 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getControllerManager().getController() == null) {
				e.getPlayer().useStairs(828, new WorldTile(2881, 5310, 2), 0, 0);
				e.getPlayer().getControllerManager().startController(new GodwarsController());
			} else
				e.getPlayer().sendMessage("Invalid teleport.");
		}
	};

	public static ObjectClickHandler handleGodwarsBoulder = new ObjectClickHandler(new Object[] { 35390 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			boolean lift = e.getOpNum() == ClientPacket.OBJECT_OP1;
			if (e.getPlayer().getSkills().getLevel(lift ? Skills.STRENGTH : Skills.AGILITY) < 60) {
				e.getPlayer().sendMessage("You need a " + (lift ? "Strength" : "Agility") + " of 60 in order to " + (lift ? "lift" : "squeeze past") + " this boulder.");
				return;
			}
			boolean isReturning = e.getPlayer().getY() >= 3709;
			int liftAnimation = isReturning ? 3624 :3725;
			int squeezeAnimation = isReturning ? 3465 : 3466;
			WorldTile destination = new WorldTile(e.getPlayer().getX(), e.getPlayer().getY() + (isReturning ? -4 : 4), 0);
			WorldTasks.schedule(new WorldTask() {
				int stage = 0;

				@Override
				public void run() {
					if (stage == 0) {
						e.getPlayer().lock();

						e.getPlayer().faceTile(destination);
					} else if (stage == 1)
						e.getPlayer().setNextAnimation(lift ? new Animation(liftAnimation) : new Animation(squeezeAnimation));
					else if (stage == 3) {
						if (lift && isReturning)
							World.sendObjectAnimation(e.getObject(), new Animation(318));
					}  else if (stage == 4) {
						if (lift && !isReturning)
							World.sendObjectAnimation(e.getObject(), new Animation(318));
					} else if (stage == 6) {
						if (!lift) {
							e.getPlayer().setNextWorldTile(destination);
							e.getPlayer().unlock();
							stop();
						}
					} else if (stage == 8) {
						if (lift && isReturning) {
							e.getPlayer().setNextWorldTile(destination);
							e.getPlayer().unlock();
							stop();
						}
					} else if (stage == 11)
						if (lift && !isReturning) {
							e.getPlayer().setNextWorldTile(destination);
							e.getPlayer().unlock();
							stop();
						}
					stage++;
				}
			}, 0, 0);
		}
	};

	public static ObjectClickHandler handleTrollheimCaveEntrances = new ObjectClickHandler(new Object[] { 34395 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().isAt(2920, 3654))
				e.getPlayer().setNextWorldTile(new WorldTile(2907, 10035, 0));
			else if (e.getObject().isAt(2910, 3637))
				e.getPlayer().setNextWorldTile(new WorldTile(2907, 10019, 0));
			else if (e.getObject().isAt(2857, 3578))
				e.getPlayer().setNextWorldTile(new WorldTile(2269, 4752, 0));
			else if (e.getObject().isAt(2885, 3673))
				e.getPlayer().setNextWorldTile(new WorldTile(2893, 10074, 2));
			else if (e.getObject().isAt(2847, 3688))
				e.getPlayer().setNextWorldTile(new WorldTile(2837, 10090, 2));
			else if (e.getObject().isAt(2885, 3673))
				e.getPlayer().setNextWorldTile(new WorldTile(2893, 10074, 2));
			else if (e.getObject().isAt(2796, 3614))
				e.getPlayer().setNextWorldTile(new WorldTile(2808, 10002, 0));
			else
				e.getPlayer().sendMessage("Unhandled TrollheimMisc.handleTrollheimCaveEntrances()");
		}
	};

	public static ObjectClickHandler handleOtherCaveEntrances = new ObjectClickHandler(new Object[] { 32738, 18834, 18833, 4500, 3774 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getId() == 32738)
				e.getPlayer().setNextWorldTile(new WorldTile(2889, 3675, 0));
			else if (e.getObject().getId() == 18834)
				e.getPlayer().ladder(new WorldTile(2812, 3669, 0));
			else if (e.getObject().getId() == 18833)
				e.getPlayer().ladder(new WorldTile(2831, 10076, 2));
			else if (e.getObject().getId() == 4500)
				e.getPlayer().setNextWorldTile(new WorldTile(2795, 3615, 0));
			else if (e.getObject().getId() == 3774)
				e.getPlayer().setNextWorldTile(new WorldTile(2848, 3687, 0));
		}
	};

	public static ObjectClickHandler handleSabbottCaveShortcuts = new ObjectClickHandler(new Object[] { 67568, 67567, 67562, 67572, 66533, 67674, 67570 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getId() == 67568)
				e.getPlayer().setNextWorldTile(new WorldTile(2858, 3577, 0));
			else if (e.getObject().getId() == 67567)
				e.getPlayer().setNextWorldTile(new WorldTile(2267, 4758, 0));
			else if (e.getObject().getId() == 67562)
				e.getPlayer().setNextWorldTile(new WorldTile(3405, 4284, 2));
			else if (e.getObject().getId() == 67572)
				e.getPlayer().setNextWorldTile(new WorldTile(2858, 3577, 0));
			else if (e.getObject().getId() == 66533)
				e.getPlayer().setNextWorldTile(new WorldTile(2208, 4364, 0));
			else if (e.getObject().getId() == 67674) {
				if (e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2)
					Agility.handleObstacle(e.getPlayer(), 3382, 3, e.getPlayer().transform(-3, 0, -1), 1);
				else
					Agility.handleObstacle(e.getPlayer(), 3382, 3, e.getPlayer().transform(0, -3, -1), 1);
			} else if (e.getObject().getId() == 67570)
				if (e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2)
					Agility.handleObstacle(e.getPlayer(), 3381, 3, e.getPlayer().transform(3, 0, 1), 1);
				else
					Agility.handleObstacle(e.getPlayer(), 3381, 3, e.getPlayer().transform(0, 3, 1), 1);
		}
	};

	public static ObjectClickHandler handleCliffClimbs = new ObjectClickHandler(new Object[] { 35391, 3748, 34877, 34889, 9306, 9305, 3803, 9304, 9303 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getId() == 35391) {
				if (e.getObject().getRotation() == 3 || e.getObject().getRotation() == 1)
					Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 2 : -2, 0, 0), 1);
				else
					Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 2 : -2, 0), 1);
			} else if (e.getObject().getId() == 3748) {
				if (e.getObject().getRotation() == 3 || e.getObject().getRotation() == 1)
					Agility.handleObstacle(e.getPlayer(), 3377, 2, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 2 : -2, 0, 0), 1);
				else
					Agility.handleObstacle(e.getPlayer(), 3377, 2, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 2 : -2, 0), 1);
			} else if (e.getObject().getId() == 34877 || e.getObject().getId() == 34889) {
				if (e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2)
					Agility.handleObstacle(e.getPlayer(), e.getPlayer().getX() < e.getObject().getX() ? 3381 : 3382, 3, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 4 : -4, 0, 0), 1);
				else
					Agility.handleObstacle(e.getPlayer(), e.getPlayer().getY() < e.getObject().getY() ? 3381 : 3382, 3, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 4 : -4, 0), 1);
			} else if (e.getObject().getId() == 9306 || e.getObject().getId() == 9305) {
				if (e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2)
					Agility.handleObstacle(e.getPlayer(), e.getPlayer().getX() < e.getObject().getX() ? 3382 : 3381, 3, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 4 : -4, 0, 0), 1);
				else
					Agility.handleObstacle(e.getPlayer(), e.getPlayer().getY() < e.getObject().getY() ? 3382 : 3381, 3, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 4 : -4, 0), 1);
			} else if (e.getObject().getId() == 3803 || e.getObject().getId() == 9304 || e.getObject().getId() == 9303)
				if (e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2)
					Agility.handleObstacle(e.getPlayer(), e.getPlayer().getX() < e.getObject().getX() ? 3381 : 3382, 3, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 4 : -4, 0, 0), 1);
				else
					Agility.handleObstacle(e.getPlayer(), e.getPlayer().getY() < e.getObject().getY() ? 3381 : 3382, 3, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 4 : -4, 0), 1);
		}
	};

	public static ObjectClickHandler handleWildernessCliff = new ObjectClickHandler(false, new Object[] { 34878 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 64)) {
				e.getPlayer().sendMessage("You need 64 agility");
				return;
			}
			Player p = e.getPlayer();
			WorldObject obj = e.getObject();

			p.setRouteEvent(new RouteEvent(new WorldTile(2950, 3681, 0), () -> {
				if(obj.matches(new WorldTile(2951, 3681, 0)) && p.getX() < obj.getX()) {
					WorldTile destinationTile = new WorldTile(2954, 3682, 0);
					p.faceTile(destinationTile);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							p.setNextAnimation(new Animation(3382));
						}
					}, 1);

					p.lock();
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							p.setNextWorldTile(destinationTile);
							p.unlock();
						}
					}, 8);
				}
			}));

		}
	};

}
