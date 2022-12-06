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

import com.rs.lib.game.WorldTile;

public class WerewolveAgility {

	public static final int
	WEREWOLF_STEPPING_STONE_OBJECT = 5138,
	WEREWOLF_HURDLE_OBJECT1 = 5133,
	WEREWOLF_HURDLE_OBJECT2 = 5134,
	WEREWOLF_HURDLE_OBJECT3 = 5135,
	WEREWOLF_PIPES_OBJECT = 5152,
	WEREWOLF_SKULL_OBJECT = 5136,
	WEREWOLF_SLING_OBJECT = 5141,

	WEREWOLF_STEPPING_STONE_ANIM = 3067,
	WEREWOLF_HURDLE_ANIM = 2750,
	WEREWOLF_PIPES_ANIM = 844,
	WEREWOLF_SKULL_ANIM = 1148,
	WEREWOLF_SLING_ANIM = 5141;

	public static final WorldTile
	EXIT_COORDS = WorldTile.of(3543, 3463, 0),
	ENTRANCE_COORDS = WorldTile.of(3549, 9865, 0);
	//
	//		case WerewolfAgility.WEREWOLF_PIPES_OBJECT:
	//			return PIPES_EMOTE;
	//		case WerewolfAgility.WEREWOLF_STEPPING_STONE_OBJECT:
	//			return 3067;
	//		case WerewolfAgility.WEREWOLF_SKULL_OBJECT:
	//			return 1148;
	//		case WerewolfAgility.WEREWOLF_SLING_OBJECT:
	//			return 744;
	//		case WerewolfAgility.WEREWOLF_HURDLE_OBJECT1:
	//		case WerewolfAgility.WEREWOLF_HURDLE_OBJECT2:
	//		case WerewolfAgility.WEREWOLF_HURDLE_OBJECT3:
	//			return 2750;
	//		}
	//		return -1;
	//
	//
	//
	//	@ObjectClickHandler(ids = { 5138 }, checkDistance = false)
	//	public static void handleSteppingStone(ObjectClickEvent e) {
	//
	//	}
	//
	//
	//
	//
	//







	//	@ObjectClickHandler(ids = { 43529 }, checkDistance = false)
	//	public static void handleSwing(ObjectClickEvent e) {
	//		if (!Agility.hasLevel(e.getPlayer(), 85))
	//			return;
	//		int x = Utils.clampI(e.getObject().getX(), 2485, 2487);
	//		e.getPlayer().setRouteEvent(new RouteEvent(WorldTile.of(x, 3419, 3), () -> {
	//			e.getPlayer().lock();
	//			WorldTasksManager.schedule(new WorldTask() {
	//				int stage = 0;
	//
	//				@Override
	//				public void run() {
	//					if (stage == 0) {
	//						e.getPlayer().faceObject(e.getObject());
	//					} else if (stage == 1) {
	//						e.getPlayer().setNextAnimation(new Animation(11784));
	//						e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, WorldTile.of(x, 3421, 3), 1));
	//					} else if (stage == 2) {
	//						e.getPlayer().setNextAnimation(new Animation(11785));
	//						e.getPlayer().setNextWorldTile(WorldTile.of(x, 3421, 3));
	//						e.getPlayer().setNextForceMovement(new ForceMovement(WorldTile.of(x, 3421, 3), 0, WorldTile.of(x, 3425, 3), 1));
	//					} else if (stage == 3) {
	//						e.getPlayer().setNextWorldTile(WorldTile.of(x, 3425, 3));
	//						e.getPlayer().setNextAnimation(new Animation(11789));
	//					} else if (stage == 6) {
	//						e.getPlayer().setNextForceMovement(new ForceMovement(WorldTile.of(x, 3425, 3), 1, WorldTile.of(x, 3429, 3), 2));
	//					} else if (stage == 11) {
	//						e.getPlayer().setNextWorldTile(WorldTile.of(x, 3429, 3));
	//						e.getPlayer().setNextForceMovement(new ForceMovement(WorldTile.of(x, 3429, 3), 1, WorldTile.of(x, 3432, 3), 2));
	//					} else if (stage == 15) {
	//						e.getPlayer().setNextWorldTile(WorldTile.of(x, 3432, 3));
	//						e.getPlayer().getSkills().addXp(Constants.AGILITY, 25);
	//						if (getGnomeStage(e.getPlayer()) == 1)
	//							setGnomeStage(e.getPlayer(), 2);
	//						e.getPlayer().unlock();
	//						stop();
	//					}
	//					stage++;
	//				}
	//			}, 0, 0);
	//		}));
	//	}
	//
	//	@ObjectClickHandler(ids = { 69514 }, checkDistance = false)
	//	public static void handleBoard(ObjectClickEvent e) {
	//		if (!Agility.hasLevel(e.getPlayer(), 85))
	//			return;
	//		e.getPlayer().setRouteEvent(new RouteEvent(WorldTile.of(2476, 3418, 3), () -> {
	//			e.getPlayer().lock();
	//			e.getPlayer().setNextAnimation(new Animation(2922));
	//			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 1, WorldTile.of(2484, 3418, 3), 3, ForceMovement.EAST));
	//			e.getPlayer().getSkills().addXp(Constants.AGILITY, 22);
	//			e.getPlayer().sendMessage("You skillfully run across the board", true);
	//			WorldTasksManager.schedule(new WorldTask() {
	//				@Override
	//				public void run() {
	//					e.getPlayer().unlock();
	//					e.getPlayer().setNextWorldTile(WorldTile.of(2484, 3418, 3));
	//					if (getGnomeStage(e.getPlayer()) == 0)
	//						setGnomeStage(e.getPlayer(), 1);
	//				}
	//			}, 2);
	//		}));
	//	}
	//
	//
	//	@ObjectClickHandler(ids = { 69506 })
	//	public static void handleTreeBranch2(ObjectClickEvent e) {
	//		if (!Agility.hasLevel(e.getPlayer(), 85))
	//			return;
	//		e.getPlayer().sendMessage("You climb the tree...", true);
	//		e.getPlayer().useStairs(828, WorldTile.of(2472, 3419, 3), 1, 2, "... to the platform above.");
	//		if (getGnomeStage(e.getPlayer()) == 0)
	//			setGnomeStage(e.getPlayer(), 1);
	//		e.getPlayer().getSkills().addXp(Constants.AGILITY, 19);
	//	}
	//
	//	@ObjectClickHandler(ids = { 69389 })
	//	public static void handleJumpDown(ObjectClickEvent e) {
	//		if (!Agility.hasLevel(e.getPlayer(), 85))
	//			return;
	//		e.getPlayer().lock();
	//		WorldTasksManager.schedule(new WorldTask() {
	//			int tick = 0;
	//			@Override
	//			public void run() {
	//				if (tick == 0) {
	//					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 1, WorldTile.of(2485, 3434, 3), 2));
	//					e.getPlayer().setNextAnimation(new Animation(2923));
	//				} else if (tick == 3) {
	//					e.getPlayer().setNextWorldTile(WorldTile.of(2485, 3436, 0));
	//					e.getPlayer().setNextAnimation(new Animation(2924));
	//				} else if (tick == 5) {
	//					e.getPlayer().unlock();
	//					e.getPlayer().getSkills().addXp(Constants.AGILITY, 25);
	//					if (getGnomeStage(e.getPlayer()) == 2) {
	//						e.getPlayer().incrementCount("Gnome advanced laps");
	//						removeGnomeStage(e.getPlayer());
	//						e.getPlayer().getSkills().addXp(Constants.AGILITY, 605);
	//					}
	//					stop();
	//				}
	//				tick++;
	//			}
	//		}, 0, 0);
	//	}
	//
	//	@ObjectClickHandler(ids = { 69526 })
	//	public static void handleLogWalk(ObjectClickEvent e) {
	//		final boolean running = e.getPlayer().getRun();
	//		e.getPlayer().setRunHidden(false);
	//		e.getPlayer().lock();
	//		e.getPlayer().addWalkSteps(2474, 3429, -1, false);
	//		e.getPlayer().sendMessage("You walk carefully across the slippery log...", true);
	//		WorldTasksManager.schedule(new WorldTask() {
	//			boolean secondloop;
	//
	//			@Override
	//			public void run() {
	//				if (!secondloop) {
	//					secondloop = true;
	//					e.getPlayer().getAppearance().setBAS(155);
	//				} else {
	//					e.getPlayer().getAppearance().setBAS(-1);
	//					e.getPlayer().setRunHidden(running);
	//					setGnomeStage(e.getPlayer(), 0);
	//					e.getPlayer().getSkills().addXp(Constants.AGILITY, 7.5);
	//					e.getPlayer().sendMessage("... and make it safely to the other side.", true);
	//					e.getPlayer().unlock();
	//					stop();
	//				}
	//			}
	//		}, 0, 5);
	//	}
	//
	//	@ObjectClickHandler(ids = { 69383 })
	//	public static void handleObstacleNet(ObjectClickEvent e) {
	//		e.getPlayer().sendMessage("You climb the netting.", true);
	//		e.getPlayer().useStairs(828, WorldTile.of(e.getPlayer().getX(), 3423, 1), 1, 2);
	//		WorldTasksManager.schedule(new WorldTask() {
	//			@Override
	//			public void run() {
	//				if (getGnomeStage(e.getPlayer()) == 0)
	//					setGnomeStage(e.getPlayer(), 1);
	//				e.getPlayer().getSkills().addXp(Constants.AGILITY, 7.5);
	//			}
	//		}, 1);
	//	}
	//
	//	@ObjectClickHandler(ids = { 69508 })
	//	public static void handleTreeBranch(ObjectClickEvent e) {
	//		e.getPlayer().sendMessage("You climb the tree...", true);
	//		e.getPlayer().useStairs(828, WorldTile.of(2473, 3420, 2), 1, 2, "... to the platform above.");
	//		WorldTasksManager.schedule(new WorldTask() {
	//			@Override
	//			public void run() {
	//				if (getGnomeStage(e.getPlayer()) == 1)
	//					setGnomeStage(e.getPlayer(), 2);
	//				e.getPlayer().getSkills().addXp(Constants.AGILITY, 5);
	//			}
	//		}, 1);
	//	}
	//
	//	@ObjectClickHandler(ids = { 4059 })
	//	public static void handleWalkBackRope(ObjectClickEvent e) {
	//		e.getPlayer().sendMessage("I would keep moving forward on the course.");
	//	}
	//
	//	@ObjectClickHandler(ids = { 2312 })
	//	public static void handleRope(ObjectClickEvent e) {
	//		final boolean running = e.getPlayer().getRun();
	//		e.getPlayer().setRunHidden(false);
	//		e.getPlayer().lock();
	//		e.getPlayer().addWalkSteps(2483, 3420, -1, false);
	//		WorldTasksManager.schedule(new WorldTask() {
	//			boolean secondloop;
	//
	//			@Override
	//			public void run() {
	//				if (!secondloop) {
	//					secondloop = true;
	//					e.getPlayer().getAppearance().setBAS(155);
	//				} else {
	//					e.getPlayer().getAppearance().setBAS(-1);
	//					e.getPlayer().setRunHidden(running);
	//					if (getGnomeStage(e.getPlayer()) == 2)
	//						setGnomeStage(e.getPlayer(), 3);
	//					e.getPlayer().getSkills().addXp(Constants.AGILITY, 7);
	//					e.getPlayer().sendMessage("You passed the obstacle succesfully.", true);
	//					stop();
	//					e.getPlayer().unlock();
	//				}
	//			}
	//		}, 0, 5);
	//	}
	//
	//	@ObjectClickHandler(ids = { 69507 })
	//	public static void handleTreeBranch3(ObjectClickEvent e) {
	//		e.getPlayer().useStairs(828, WorldTile.of(2487, 3421, 0), 1, 2, "You climbed the tree branch succesfully.");
	//		WorldTasksManager.schedule(new WorldTask() {
	//			@Override
	//			public void run() {
	//				if (getGnomeStage(e.getPlayer()) == 3)
	//					setGnomeStage(e.getPlayer(), 4);
	//				e.getPlayer().getSkills().addXp(Constants.AGILITY, 5);
	//			}
	//		}, 1);
	//	}
	//
	//	@ObjectClickHandler(ids = { 69384 }, checkDistance = false)
	//	public static void handleObstacleNet2(ObjectClickEvent e) {
	//		e.getPlayer().setRouteEvent(new RouteEvent(WorldTile.of(Utils.clampI(e.getPlayer().getX(), 2483, 2488), e.getObject().getY()-1, 0), () -> {
	//			e.getPlayer().sendMessage("You climb the netting.", true);
	//			e.getPlayer().useStairs(828, WorldTile.of(e.getPlayer().getX(), e.getObject().getY()+1, 0), 1, 2);
	//			WorldTasksManager.schedule(new WorldTask() {
	//				@Override
	//				public void run() {
	//					if (getGnomeStage(e.getPlayer()) == 4)
	//						setGnomeStage(e.getPlayer(), 5);
	//					e.getPlayer().getSkills().addXp(Constants.AGILITY, 8);
	//				}
	//			}, 1);
	//		}));
	//	}
	//
	//	@ObjectClickHandler(ids = { 69377, 69378 })
	//	public static void handlePipe(ObjectClickEvent e) {
	//		final boolean running = e.getPlayer().getRun();
	//		e.getPlayer().setRunHidden(false);
	//		e.getPlayer().lock(8);
	//		e.getPlayer().addWalkSteps(e.getObject().getX(), e.getObject().getY() == 3431 ? 3437 : 3430, -1, false);
	//		e.getPlayer().sendMessage("You pulled yourself through the pipes.", true);
	//		WorldTasksManager.schedule(new WorldTask() {
	//			boolean secondloop;
	//
	//			@Override
	//			public void run() {
	//				if (!secondloop) {
	//					secondloop = true;
	//					e.getPlayer().getAppearance().setBAS(295);
	//				} else {
	//					e.getPlayer().getAppearance().setBAS(-1);
	//					e.getPlayer().setRunHidden(running);
	//					e.getPlayer().getSkills().addXp(Constants.AGILITY, 7);
	//					if (getGnomeStage(e.getPlayer()) == 5) {
	//						e.getPlayer().incrementCount("Gnome normal laps");
	//						removeGnomeStage(e.getPlayer());
	//						e.getPlayer().getSkills().addXp(Constants.AGILITY, 39.5);
	//
	//					}
	//					stop();
	//				}
	//			}
	//		}, 0, 6);
	//	}
	//
	//	public static void removeGnomeStage(Player player) {
	//		player.getTemporaryAttributes().remove("GnomeCourse");
	//		player.getTemporaryAttributes().remove("GnomeCourseAdv");
	//	}
	//
	//	public static void setGnomeStage(Player player, int stage) {
	//		player.getTemporaryAttributes().put("GnomeCourse", stage);
	//		player.getTemporaryAttributes().put("GnomeCourseAdv", stage);
	//	}
	//
	//	public static int getGnomeStageAdv(Player player) {
	//		Integer stage = (Integer) player.getTemporaryAttributes().get("GnomeCourseAdv");
	//		if (stage == null)
	//			return -1;
	//		return stage;
	//	}
	//
	//	public static int getGnomeStage(Player player) {
	//		Integer stage = (Integer) player.getTemporaryAttributes().get("GnomeCourse");
	//		if (stage == null)
	//			return -1;
	//		return stage;
	//	}
}
