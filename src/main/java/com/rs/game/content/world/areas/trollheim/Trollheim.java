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
package com.rs.game.content.world.areas.trollheim;

import com.rs.game.World;
import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Trollheim {

	public static LoginHandler unlockSabbotCavern = new LoginHandler(e -> {
		e.getPlayer().getVars().setVarBit(10762, 3); //1 = mineable, 2 = being mined, 3 = mined
	});

	public static ObjectClickHandler handleTrollweissCaveEnter = new ObjectClickHandler(new Object[] { 5012 }, e -> e.getPlayer().tele(Tile.of(2799, 10134, 0)));

	public static ObjectClickHandler handleTrollweissCaveExit = new ObjectClickHandler(new Object[] { 5013 }, e -> e.getPlayer().tele(Tile.of(2796, 3719, 0)));

	public static ObjectClickHandler handleTrollheimCaveExits = new ObjectClickHandler(new Object[] { 3758 }, e -> {
		if (e.objectAt(2906, 10036))
			e.getPlayer().tele(Tile.of(2922, 3658, 0));
		else if (e.objectAt(2906, 10017))
			e.getPlayer().tele(Tile.of(2911, 3636, 0));
	});

	public static ObjectClickHandler handleGodwarsEntrance = new ObjectClickHandler(new Object[] { 26342 }, e -> {
		if (e.getPlayer().getControllerManager().getController() == null) {
			e.getPlayer().useStairs(828, Tile.of(2881, 5310, 2), 0, 0);
			e.getPlayer().getControllerManager().startController(new GodwarsController());
		} else
			e.getPlayer().sendMessage("Invalid teleport.");
	});

	public static ObjectClickHandler handleGodwarsBoulder = new ObjectClickHandler(new Object[] { 35390 }, e -> {
		boolean lift = e.getOpNum() == ClientPacket.OBJECT_OP1;
		if (e.getPlayer().getSkills().getLevel(lift ? Skills.STRENGTH : Skills.AGILITY) < 60) {
			e.getPlayer().sendMessage("You need a " + (lift ? "Strength" : "Agility") + " of 60 in order to " + (lift ? "lift" : "squeeze past") + " this boulder.");
			return;
		}
		boolean isReturning = e.getPlayer().getY() >= 3709;
		int liftAnimation = isReturning ? 3624 :3725;
		int squeezeAnimation = isReturning ? 3465 : 3466;
		Tile destination = Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + (isReturning ? -4 : 4), 0);
		WorldTasks.scheduleLooping(new Task() {
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
						e.getPlayer().tele(destination);
						e.getPlayer().unlockNextTick();
						stop();
					}
				} else if (stage == 8) {
					if (lift && isReturning) {
						e.getPlayer().tele(destination);
						e.getPlayer().unlockNextTick();
						stop();
					}
				} else if (stage == 11)
					if (lift && !isReturning) {
						e.getPlayer().tele(destination);
						e.getPlayer().unlockNextTick();
						stop();
					}
				stage++;
			}
		}, 0, 0);
	});

	public static ObjectClickHandler handleTrollheimCaveEntrances = new ObjectClickHandler(new Object[] { 34395 }, e -> {
		if (e.getObject().getTile().isAt(2920, 3654))
			e.getPlayer().tele(Tile.of(2907, 10035, 0));
		else if (e.getObject().getTile().isAt(2910, 3637))
			e.getPlayer().tele(Tile.of(2907, 10019, 0));
		else if (e.getObject().getTile().isAt(2857, 3578))
			e.getPlayer().tele(Tile.of(2269, 4752, 0));
		else if (e.getObject().getTile().isAt(2885, 3673))
			e.getPlayer().tele(Tile.of(2893, 10074, 2));
		else if (e.getObject().getTile().isAt(2847, 3688))
			e.getPlayer().tele(Tile.of(2837, 10090, 2));
		else if (e.getObject().getTile().isAt(2885, 3673))
			e.getPlayer().tele(Tile.of(2893, 10074, 2));
		else if (e.getObject().getTile().isAt(2796, 3614))
			e.getPlayer().tele(Tile.of(2808, 10002, 0));
		else
			e.getPlayer().sendMessage("Unhandled TrollheimMisc.handleTrollheimCaveEntrances()");
	});

	public static ObjectClickHandler handleOtherCaveEntrances = new ObjectClickHandler(new Object[] { 32738, 18834, 18833, 4500, 3774 }, e -> {
		if (e.getObject().getId() == 32738)
			e.getPlayer().tele(Tile.of(2889, 3675, 0));
		else if (e.getObject().getId() == 18834)
			e.getPlayer().ladder(Tile.of(2812, 3669, 0));
		else if (e.getObject().getId() == 18833)
			e.getPlayer().ladder(Tile.of(2831, 10076, 2));
		else if (e.getObject().getId() == 4500)
			e.getPlayer().tele(Tile.of(2795, 3615, 0));
		else if (e.getObject().getId() == 3774)
			e.getPlayer().tele(Tile.of(2848, 3687, 0));
	});

	public static ObjectClickHandler sabbotNoDists = new ObjectClickHandler(false, new Object[] { 67752, 67679 }, e -> {
		if (e.getObjectId() == 67752) {
			e.getPlayer().setRouteEvent(new RouteEvent(e.getPlayer().getX() > e.getObject().getX() ? Tile.of(3434, 4261, 1) : Tile.of(3430, 4261, 1), () -> {
				e.getPlayer().lock();
				e.getPlayer().resetWalkSteps();
				World.sendObjectAnimation(e.getObject(), new Animation(497));
				e.getPlayer().forceMove(e.getPlayer().getX() < e.getObject().getX() ? Tile.of(3434, 4261, 1) : Tile.of(3430, 4261, 1), 751, 20, 75);
			}));
		} else {
			boolean goWest = e.getPlayer().getX() > 3419;
			e.getPlayer().setRouteEvent(new RouteEvent(goWest ? Tile.of(3423, 4260, 1) : Tile.of(3415, 4260, 1), () -> {
				e.getPlayer().lock();
				e.getPlayer().faceObject(e.getObject());
				for (int i = 0;i < 4;i++) {
					e.getPlayer().getTasks().schedule((i * 4) + 1, () -> {
						e.getPlayer().anim(13495);
						e.getPlayer().getTasks().schedule(3, () -> {
							e.getPlayer().anim(-1);
							e.getPlayer().tele(e.getPlayer().transform(goWest ? -2 : 2, 0));
						});
					});
				}
				e.getPlayer().getTasks().schedule(17, () -> e.getPlayer().unlock());
			}));
		}
	});


	public static ObjectClickHandler handleSabbottCaveShortcuts = new ObjectClickHandler(new Object[] { 67568, 67569, 67567, 67562, 67572, 67674, 67676, 67678, 67679, 67752, 67570 }, e -> {
		switch(e.getObjectId()) {
			case 67568 -> e.getPlayer().tele(Tile.of(2858, 3577, 0));
			case 67569 -> e.getPlayer().tele(Tile.of(2854, 3617, 0));
			case 67572 -> e.getPlayer().tele(Tile.of(3435, 4240, 2));
			case 67567 -> e.getPlayer().tele(Tile.of(2267, 4758, 0));
			case 67562 -> e.getPlayer().tele(Tile.of(3405, 4284, 2));
			case 67676 -> { //squeeze gaps
				int deltaX = e.getObject().getTile().isAt(3421, 4280) ? e.getPlayer().getX() > e.getObject().getX() ? -2 : 2 : 0;
				int deltaY = e.getObject().getTile().isAt(3421, 4280) ? 0 : e.getPlayer().getY() > e.getObject().getY() ? -2 : 2;
				e.getPlayer().lock();
				e.getPlayer().getTasks().schedule(1, () -> {
					e.getPlayer().anim(16025);
					e.getPlayer().getTasks().schedule(7, () -> {
						e.getPlayer().anim(-1);
						e.getPlayer().tele(e.getPlayer().transform(deltaX, deltaY));
						e.getPlayer().unlockNextTick();
					});
				});
			}
			case 67678 -> {
				e.getPlayer().lock();
				e.getPlayer().addWalkSteps(e.getObject().getTile(), 2, false);
				e.getPlayer().getTasks().schedule(1, () -> e.getPlayer().faceTile(e.getPlayer().transform(0, e.getObject().getTile().isAt(3434, 4275) ? 2 : -2)));
				e.getPlayer().getTasks().schedule(2, () -> e.getPlayer().anim(13495));
				e.getPlayer().getTasks().schedule(5, () -> {
					e.getPlayer().anim(-1);
					e.getPlayer().tele(e.getPlayer().transform(0, e.getObject().getTile().isAt(3434, 4275) ? 2 : -2));
					e.getPlayer().unlockNextTick();
				});
			}
			case 67674, 67570 -> {
				boolean horizontal = e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2;
				int dx = horizontal ? (e.getObject().getId() == 67674 ? -4 : 4) : 0;
				int dy = !horizontal ? (e.getObject().getId() == 67674 ? -4 : 4) : 0;
				int dz = e.getObject().getId() == 67674 ? -1 : 1;
				climbCliff(e.getPlayer(), e.getPlayer().getTile(), e.getPlayer().transform(dx, dy, dz), e.getObject().getId() == 67570);
			}
		}
	});

	public static ObjectClickHandler handleDadArena = new ObjectClickHandler(new Object[] { 34836, 34839 }, e -> {
		if (!ChunkManager.getChunk(e.getObject().getTile().getChunkId()).getBaseObjects().contains(e.getObject()))
			return;
		Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleCliffClimbs = new ObjectClickHandler(new Object[] { 35391, 3748, 34877, 34889, 9306, 9305, 3803, 9304, 9303 }, e -> {
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
	});

	public static ObjectClickHandler handleWildernessCliff = new ObjectClickHandler(new Object[] { 34878 }, e -> {
		if (e.getObject().getRotation() == 0 || e.getObject().getRotation() == 2)
			Agility.handleObstacle(e.getPlayer(), e.getPlayer().getX() < e.getObject().getX() ? 3381 : 3382, 3, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 4 : -4, 0, 0), 1);
		else
			Agility.handleObstacle(e.getPlayer(), e.getPlayer().getY() < e.getObject().getY() ? 3381 : 3382, 3, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 4 : -4, 0), 1);
	});

	public static void climbCliff(Player player, Tile start, Tile end, boolean up) {
		player.walkToAndExecute(start, () -> {
			player.lock();
			if (!up)
				player.addWalkSteps(end.x(), end.y(), 1, false);
			player.getTasks().schedule(1, () -> {
				player.faceTile(end);
				player.getTasks().schedule(1, () -> {
					player.anim(up ? 16031 : 16016);
					player.getTasks().schedule(up ? 5 : 2, () -> {
						player.anim(-1);
						player.tele(end);
						player.unlockNextTick();
					});
				});
			});
		});
	}
}
