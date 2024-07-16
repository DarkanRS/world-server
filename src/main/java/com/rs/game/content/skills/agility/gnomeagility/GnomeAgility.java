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
package com.rs.game.content.skills.agility.gnomeagility;

import com.rs.engine.pathfinder.RouteEvent;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class GnomeAgility {

	private static void initAdvancedCourse(final Player player) {
		boolean[] previousStages = Agility.getStages(player, Agility.GNOME_COURSE);
		Agility.initStages(player, Agility.GNOME_COURSE, AdvancedObstacle.getEntries().size());
		// the log balance, obstacle net and tree branch carry over from the normal course to the advanced course
		if (previousStages != null) {
			Agility.setStageProgress(player, Agility.GNOME_COURSE, AdvancedObstacle.LOG_BALANCE.ordinal(), previousStages[NormalObstacle.LOG_BALANCE.ordinal()]);
			Agility.setStageProgress(player, Agility.GNOME_COURSE, AdvancedObstacle.OBSTACLE_NET.ordinal(), previousStages[NormalObstacle.OBSTACLE_NET.ordinal()]);
			Agility.setStageProgress(player, Agility.GNOME_COURSE, AdvancedObstacle.TREE_BRANCH.ordinal(), previousStages[NormalObstacle.TREE_BRANCH.ordinal()]);
		}
	}

	public static ObjectClickHandler handle = new ObjectClickHandler(false, new Object[] { 43529 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		int x = Utils.clampI(e.getObject().getX(), 2485, 2487);
		e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(x, 3419, 3), () -> {
			e.getPlayer().lock();
			WorldTasks.scheduleTimer(tick -> {
				switch(tick) {
					case 0 -> e.getPlayer().faceObject(e.getObject());
					case 1 -> e.getPlayer().forceMoveVisually(Tile.of(x, 3421, 3), 11784, 0, 30);
					case 2 -> {
						e.getPlayer().tele(Tile.of(x, 3421, 3));
						e.getPlayer().forceMoveVisually(Tile.of(x, 3421, 3), Tile.of(x, 3425, 3), 11785, 0, 30);
					}
					case 3 -> {
						e.getPlayer().anim(11789);
						e.getPlayer().tele(Tile.of(x, 3425, 3));
					}
					case 6 -> e.getPlayer().forceMoveVisually(Tile.of(x, 3429, 3), -1, 20, 60);
					case 11 -> {
						e.getPlayer().tele(Tile.of(x, 3429, 3));
						e.getPlayer().forceMoveVisually(Tile.of(x, 3429, 3), Tile.of(x, 3432, 3), -1, 20, 60);
					}
					case 15 -> {
						e.getPlayer().tele(Tile.of(x, 3432, 3));
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 25);
						Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, AdvancedObstacle.POLE_SWING.ordinal(), true);
						e.getPlayer().unlock();
						return false;
					}
				}
				return true;
			});
		}));
	});

	public static ObjectClickHandler handleBoard = new ObjectClickHandler(false, new Object[] { 69514 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(2476, 3418, 3), () -> e.getPlayer().forceMove(Tile.of(2484, 3418, 3), 2922, 25, 90, () -> {
            e.getPlayer().getSkills().addXp(Constants.AGILITY, 22);
			Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, AdvancedObstacle.RUN_BOARD.ordinal(), true);
        })));
	});

	public static ObjectClickHandler handleTreeBranch2 = new ObjectClickHandler(new Object[] { 69506 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		initAdvancedCourse(e.getPlayer());
		Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, AdvancedObstacle.CLIMB_TREE.ordinal(), true);
		e.getPlayer().sendMessage("You climb the tree...", true);
		e.getPlayer().useStairs(828, Tile.of(2472, 3419, 3), 1, 2, "... to the platform above.");
		e.getPlayer().getSkills().addXp(Constants.AGILITY, 19);
	});

	public static ObjectClickHandler handleJumpDown = new ObjectClickHandler(new Object[] { 69389 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 85))
			return;
		e.getPlayer().lock();
		WorldTasks.scheduleLooping(new Task() {
			int tick = 0;
			@Override
			public void run() {
				if (tick == 0) {
					e.getPlayer().forceMove(Tile.of(2485, 3434, 3), 2923, 25, 60, false);
				} else if (tick == 3) {
					e.getPlayer().setNextAnimation(new Animation(2924));
					e.getPlayer().tele(Tile.of(2485, 3436, 0));
				} else if (tick == 5) {
					e.getPlayer().unlock();
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 25);
					if (Agility.completedCourse(e.getPlayer(), Agility.GNOME_COURSE)) {
						e.getPlayer().incrementCount("Gnome advanced laps");
						Agility.removeStage(e.getPlayer(), Agility.GNOME_COURSE);
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 605);
					}
					stop();
				}
				tick++;
			}
		}, 0, 0);
	});

	public static ObjectClickHandler handleLogWalk = new ObjectClickHandler(new Object[] { 69526 }, e -> {
		Agility.initStages(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.getEntries().size());
		final boolean running = e.getPlayer().getRun();
		e.getPlayer().setRunHidden(false);
		e.getPlayer().lock();
		e.getPlayer().addWalkSteps(2474, 3429, -1, false);
		e.getPlayer().sendMessage("You walk carefully across the slippery log...", true);
		WorldTasks.scheduleLooping(new Task() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					e.getPlayer().getAppearance().setBAS(155);
				} else {
					e.getPlayer().getAppearance().setBAS(-1);
					e.getPlayer().setRunHidden(running);
					Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.LOG_BALANCE.ordinal(), true);
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
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.OBSTACLE_NET.ordinal(), true);
				e.getPlayer().getSkills().addXp(Constants.AGILITY, 7.5);
			}
		}, 1);
	});

	public static ObjectClickHandler handleTreeBranch = new ObjectClickHandler(new Object[] { 69508 }, e -> {
		e.getPlayer().sendMessage("You climb the tree...", true);
		e.getPlayer().useStairs(828, Tile.of(2473, 3420, 2), 1, 2, "... to the platform above.");
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.TREE_BRANCH.ordinal(), true);
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
		WorldTasks.scheduleLooping(new Task() {
			boolean secondloop;

			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					e.getPlayer().getAppearance().setBAS(155);
				} else {
					e.getPlayer().getAppearance().setBAS(-1);
					e.getPlayer().setRunHidden(running);
					Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.ROPE.ordinal(), true);
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
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.TREE_BRANCH.ordinal(), true);
				e.getPlayer().getSkills().addXp(Constants.AGILITY, 5);
			}
		}, 1);
	});

	public static ObjectClickHandler handleObstacleNet2 = new ObjectClickHandler(false, new Object[] { 69384 }, e -> e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(Utils.clampI(e.getPlayer().getX(), 2483, 2488), e.getObject().getY()-1, 0), () -> {
        e.getPlayer().sendMessage("You climb the netting.", true);
        e.getPlayer().useStairs(828, Tile.of(e.getPlayer().getX(), e.getObject().getY()+1, 0), 1, 2);
        WorldTasks.schedule(new Task() {
            @Override
            public void run() {
				Agility.setStageProgress(e.getPlayer(), Agility.GNOME_COURSE, NormalObstacle.OBSTACLE_NET.ordinal(), true);
                e.getPlayer().getSkills().addXp(Constants.AGILITY, 8);
            }
        }, 1);
    })));

	public static ObjectClickHandler handlePipe = new ObjectClickHandler(new Object[] { 69377, 69378 }, e -> {
		final boolean running = e.getPlayer().getRun();
		e.getPlayer().setRunHidden(false);
		e.getPlayer().lock(8);
		e.getPlayer().addWalkSteps(e.getObject().getX(), e.getObject().getY() == 3431 ? 3437 : 3430, -1, false);
		e.getPlayer().sendMessage("You pulled yourself through the pipes.", true);
		WorldTasks.scheduleLooping(new Task() {
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
					if (Agility.completedCourse(e.getPlayer(), Agility.GNOME_COURSE)) {
						e.getPlayer().incrementCount("Gnome normal laps");
						Agility.removeStage(e.getPlayer(), Agility.GNOME_COURSE);
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 39.5);

					}
					stop();
				}
			}
		}, 0, 6);
	});
}
