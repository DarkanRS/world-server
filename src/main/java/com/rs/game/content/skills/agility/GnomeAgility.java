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
package com.rs.game.content.skills.agility;

import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class GnomeAgility {

	public static ObjectClickHandler handle = new ObjectClickHandler(false, new Object[] { 43529 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		int x = Utils.clampI(e.getObject().getX(), 2485, 2487);
		e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(x, 3419, 3), () -> {
			e.getPlayer().lock();
			WorldTasks.schedule(new WorldTask() {
				int stage = 0;

				@Override
				public void run() {
					if (stage == 0)
						e.getPlayer().faceObject(e.getObject());
					else if (stage == 1) {
						e.getPlayer().setNextAnimation(new Animation(11784));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, Tile.of(x, 3421, 3), 1));
					} else if (stage == 2) {
						e.getPlayer().setNextAnimation(new Animation(11785));
						e.getPlayer().setNextTile(Tile.of(x, 3421, 3));
						e.getPlayer().setNextForceMovement(new ForceMovement(Tile.of(x, 3421, 3), 0, Tile.of(x, 3425, 3), 1));
					} else if (stage == 3) {
						e.getPlayer().setNextTile(Tile.of(x, 3425, 3));
						e.getPlayer().setNextAnimation(new Animation(11789));
					} else if (stage == 6)
						e.getPlayer().setNextForceMovement(new ForceMovement(Tile.of(x, 3425, 3), 1, Tile.of(x, 3429, 3), 2));
					else if (stage == 11) {
						e.getPlayer().setNextTile(Tile.of(x, 3429, 3));
						e.getPlayer().setNextForceMovement(new ForceMovement(Tile.of(x, 3429, 3), 1, Tile.of(x, 3432, 3), 2));
					} else if (stage == 15) {
						e.getPlayer().setNextTile(Tile.of(x, 3432, 3));
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 25);
						if (getGnomeStage(e.getPlayer()) == 1)
							setGnomeStage(e.getPlayer(), 2);
						e.getPlayer().unlock();
						stop();
					}
					stage++;
				}
			}, 0, 0);
		}));
	});

	public static ObjectClickHandler handleBoard = new ObjectClickHandler(false, new Object[] { 69514 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(2476, 3418, 3), () -> {
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(2922));
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 1, Tile.of(2484, 3418, 3), 3, Direction.EAST));
			e.getPlayer().getSkills().addXp(Constants.AGILITY, 22);
			e.getPlayer().sendMessage("You skillfully run across the board", true);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().unlock();
					e.getPlayer().setNextTile(Tile.of(2484, 3418, 3));
					if (getGnomeStage(e.getPlayer()) == 0)
						setGnomeStage(e.getPlayer(), 1);
				}
			}, 2);
		}));
	});

	public static ObjectClickHandler handleTreeBranch2 = new ObjectClickHandler(new Object[] { 69506 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		e.getPlayer().sendMessage("You climb the tree...", true);
		e.getPlayer().useStairs(828, Tile.of(2472, 3419, 3), 1, 2, "... to the platform above.");
		if (getGnomeStage(e.getPlayer()) == 0)
			setGnomeStage(e.getPlayer(), 1);
		e.getPlayer().getSkills().addXp(Constants.AGILITY, 19);
	});

	public static ObjectClickHandler handleJumpDown = new ObjectClickHandler(new Object[] { 69389 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		e.getPlayer().lock();
		WorldTasks.schedule(new WorldTask() {
			int tick = 0;
			@Override
			public void run() {
				if (tick == 0) {
					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 1, Tile.of(2485, 3434, 3), 2));
					e.getPlayer().setNextAnimation(new Animation(2923));
				} else if (tick == 3) {
					e.getPlayer().setNextTile(Tile.of(2485, 3436, 0));
					e.getPlayer().setNextAnimation(new Animation(2924));
				} else if (tick == 5) {
					e.getPlayer().unlock();
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 25);
					if (getGnomeStage(e.getPlayer()) == 2) {
						e.getPlayer().incrementCount("Gnome advanced laps");
						removeGnomeStage(e.getPlayer());
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 605);
					}
					stop();
				}
				tick++;
			}
		}, 0, 0);
	});

	public static ObjectClickHandler handleLogWalk = new ObjectClickHandler(new Object[] { 69526 }, e -> {
		final boolean running = e.getPlayer().getRun();
		e.getPlayer().setRunHidden(false);
		e.getPlayer().lock();
		e.getPlayer().addWalkSteps(2474, 3429, -1, false);
		e.getPlayer().sendMessage("You walk carefully across the slippery log...", true);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					e.getPlayer().getAppearance().setBAS(155);
				} else {
					e.getPlayer().getAppearance().setBAS(-1);
					e.getPlayer().setRunHidden(running);
					setGnomeStage(e.getPlayer(), 0);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 7.5);
					e.getPlayer().sendMessage("... and make it safely to the other side.", true);
					e.getPlayer().unlock();
					stop();
				}
			}
		}, 0, 5);
	});

	public static ObjectClickHandler handleObstacleNet = new ObjectClickHandler(new Object[] { 69383 }, e -> {
		e.getPlayer().sendMessage("You climb the netting.", true);
		e.getPlayer().useStairs(828, Tile.of(e.getPlayer().getX(), 3423, 1), 1, 2);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(e.getPlayer()) == 0)
					setGnomeStage(e.getPlayer(), 1);
				e.getPlayer().getSkills().addXp(Constants.AGILITY, 7.5);
			}
		}, 1);
	});

	public static ObjectClickHandler handleTreeBranch = new ObjectClickHandler(new Object[] { 69508 }, e -> {
		e.getPlayer().sendMessage("You climb the tree...", true);
		e.getPlayer().useStairs(828, Tile.of(2473, 3420, 2), 1, 2, "... to the platform above.");
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(e.getPlayer()) == 1)
					setGnomeStage(e.getPlayer(), 2);
				e.getPlayer().getSkills().addXp(Constants.AGILITY, 5);
			}
		}, 1);
	});

	public static ObjectClickHandler handleWalkBackRope = new ObjectClickHandler(new Object[] { 4059 }, e -> e.getPlayer().sendMessage("I would keep moving forward on the course."));

	public static ObjectClickHandler handleRope = new ObjectClickHandler(new Object[] { 2312 }, e -> {
		final boolean running = e.getPlayer().getRun();
		e.getPlayer().setRunHidden(false);
		e.getPlayer().lock();
		e.getPlayer().addWalkSteps(2483, 3420, -1, false);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					e.getPlayer().getAppearance().setBAS(155);
				} else {
					e.getPlayer().getAppearance().setBAS(-1);
					e.getPlayer().setRunHidden(running);
					if (getGnomeStage(e.getPlayer()) == 2)
						setGnomeStage(e.getPlayer(), 3);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 7);
					e.getPlayer().sendMessage("You passed the obstacle succesfully.", true);
					stop();
					e.getPlayer().unlock();
				}
			}
		}, 0, 5);
	});

	public static ObjectClickHandler handleTreeBranch3 = new ObjectClickHandler(new Object[] { 69507 }, e -> {
		e.getPlayer().useStairs(828, Tile.of(2487, 3421, 0), 1, 2, "You climbed the tree branch succesfully.");
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getGnomeStage(e.getPlayer()) == 3)
					setGnomeStage(e.getPlayer(), 4);
				e.getPlayer().getSkills().addXp(Constants.AGILITY, 5);
			}
		}, 1);
	});

	public static ObjectClickHandler handleObstacleNet2 = new ObjectClickHandler(false, new Object[] { 69384 }, e -> {
		e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(Utils.clampI(e.getPlayer().getX(), 2483, 2488), e.getObject().getY()-1, 0), () -> {
			e.getPlayer().sendMessage("You climb the netting.", true);
			e.getPlayer().useStairs(828, Tile.of(e.getPlayer().getX(), e.getObject().getY()+1, 0), 1, 2);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if (getGnomeStage(e.getPlayer()) == 4)
						setGnomeStage(e.getPlayer(), 5);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 8);
				}
			}, 1);
		}));
	});

	public static ObjectClickHandler handlePipe = new ObjectClickHandler(new Object[] { 69377, 69378 }, e -> {
		final boolean running = e.getPlayer().getRun();
		e.getPlayer().setRunHidden(false);
		e.getPlayer().lock(8);
		e.getPlayer().addWalkSteps(e.getObject().getX(), e.getObject().getY() == 3431 ? 3437 : 3430, -1, false);
		e.getPlayer().sendMessage("You pulled yourself through the pipes.", true);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					e.getPlayer().getAppearance().setBAS(295);
				} else {
					e.getPlayer().getAppearance().setBAS(-1);
					e.getPlayer().setRunHidden(running);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 7);
					if (getGnomeStage(e.getPlayer()) == 5) {
						e.getPlayer().incrementCount("Gnome normal laps");
						removeGnomeStage(e.getPlayer());
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 39.5);

					}
					stop();
				}
			}
		}, 0, 6);
	});

	public static void removeGnomeStage(Player player) {
		player.getTempAttribs().removeI("GnomeCourse");
		player.getTempAttribs().removeI("GnomeCourseAdv");
	}

	public static void setGnomeStage(Player player, int stage) {
		player.getTempAttribs().setI("GnomeCourse", stage);
		player.getTempAttribs().setI("GnomeCourseAdv", stage);
	}

	public static int getGnomeStageAdv(Player player) {
		return player.getTempAttribs().getI("GnomeCourseAdv");
	}

	public static int getGnomeStage(Player player) {
		return player.getTempAttribs().getI("GnomeCourse");
	}
}
