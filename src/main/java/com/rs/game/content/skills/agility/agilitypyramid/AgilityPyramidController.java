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
package com.rs.game.content.skills.agility.agilitypyramid;

import com.rs.cache.loaders.ObjectType;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.ItemStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.World;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.magic.TeleType;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

import java.util.Arrays;

public class AgilityPyramidController extends Controller {

	/**
	 * ALL AGILITY PYRAMID STAGES (the indices of the obstacles at each plane/level)
	 *
	 * Plane 1:
	 * JUMP ROLLER: 0
	 * CLIMB OVER: 1
	 * SHIMMY SIDEWAYS: 2
	 * WALK LOG: 3
	 * JUMP ROLLER: 4
	 * SHIMMY HANDHOLDS: 5
	 * SHIMMY SIDEWAYS: 6
	 *
	 * Plane 2:
	 * SHIMMY HANDHOLDS: 7
	 * JUMP GAP: 8
	 * SHIMMY HANDHOLDS: 9
	 * JUMP ROLLER: 10
	 * SHIMMY SIDEWAYS: 11
	 * CLIMB OVER: 12
	 * JUMP GAP: 13
	 *
	 * Plane 3:
	 * CLIMB OVER: 14
	 * SHIMMY SIDEWAYS: 15
	 * JUMP GAP: 16
	 * WALK LOG: 17
	 *
	 * Plane 4:
	 * JUMP GAP: 18
	 * CLIMB OVER: 19
	 * JUMP ROLLER: 20
	 * JUMP GAP: 21
	 * CLIMB OVER: 22
	 *
	 * Plane 5:
	 * JUMP ROLLER: 23
	 * JUMP GAP: 24
	 */

	private boolean grabbedTop;
	private final int N_STAGES = 25;
	private final String MAX_OBSTACLE_IDX_KEY = "AgilityPyramidMaxObstacleIdx";
	private final String VIRTUAL_PLANE_KEY = "AgilityPyramidVirtualPlane";
	private final int[][] jumpRollerStages = {
			{0,4}, 	// plane 1
			{10},	// plane 2
			{-1},	// plane 3 (none)
			{20},	// plane 4
			{23}	// plane 5
	};

	private final int[][] climbOverStages = {
			{1}, 		// plane 1
			{12},		// plane 2
			{14},		// plane 3
			{19,22},	// plane 4
			{-1}		// plane 5 (none)
	};

	private final int[][] jumpGapStages = {
			{-1},		// plane 1 (none)
			{8,13},		// plane 2
			{16},		// plane 3
			{18,21},	// plane 4
			{24}		// plane 5
	};

	private final int[][] walkLogStages = {
			{3},	// plane 1
			{-1},	// plane 2 (none)
			{17},	// plane 3
			{-1},	// plane 4 (none)
			{-1}	// plane 5 (none)
	};

	private final int[][] shimmySidewaysStages = {
			{2,6},		// plane 1
			{11},		// plane 2
			{15},		// plane 3
			{-1},		// plane 4 (none)
			{-1}		// plane 5 (none)
	};

	private final int[][] shimmyHandholdsStages = {
			{5},	// plane 1
			{7,9},	// plane 2
			{-1},	// plane 3 (none)
			{-1},	// plane 4 (none)
			{-1}	// plane 5 (none)
	};

	private int[] getObstacleIndicesAtPlane(final Obstacle obstacle) {
		int virtualPlane = player.getTempAttribs().getI(VIRTUAL_PLANE_KEY);

		// This only applies when a player logs in while already on the pyramid.
		// However, player.getPlane() is not entirely accurate so this is a "best guess."
		// Usually, above plane/level 3 the plane resets to 0 so things can get a little weird there...
		if (virtualPlane < 1) {
			virtualPlane = player.getPlane() == 0 ? 1 : player.getPlane();
			player.getTempAttribs().setI(VIRTUAL_PLANE_KEY, virtualPlane);
		}

		return switch (obstacle) {
			case Obstacle.JUMP_ROLLER -> jumpRollerStages[virtualPlane - 1];
			case Obstacle.CLIMB_OVER -> climbOverStages[virtualPlane - 1];
			case Obstacle.JUMP_GAP -> jumpGapStages[virtualPlane - 1];
			case Obstacle.SHIMMY_HANDHOLDS -> shimmyHandholdsStages[virtualPlane - 1];
			case Obstacle.SHIMMY_SIDEWAYS -> shimmySidewaysStages[virtualPlane - 1];
			case Obstacle.WALK_LOG -> walkLogStages[virtualPlane - 1];
		};
	}

	/*
	 * Agility pyramid's state management is a little complex.
	 * Instead of trying to pinpoint every exact object clicked,
	 * we instead maintain the "maximum" index reached based off
	 * the object ID and current plane the player is on. When a
	 * player encounters a new obstacle type, we check if the index
	 * (according to ID and plane) is greater than our current max.
	 * If so, then this is a "new" obstacle. Otherwise, we have already
	 * encountered it (e.g., getting pushed down a level.) This will
	 * help in determining whether to apply the aura effect
	 * since it should not trigger for already completed obstacles.
	 */

	private void updateMaxObstacleIdx(final Obstacle obstacle) {
		Agility.initStagesIfNotAlready(player, Agility.AGILITY_PYRAMID, N_STAGES);

		int[] stageIndices = getObstacleIndicesAtPlane(obstacle);

		int maxObstacleIdx = player.getTempAttribs().getI(MAX_OBSTACLE_IDX_KEY);
        for (int stageIdx : stageIndices) {
			if (stageIdx != -1 && maxObstacleIdx == stageIdx - 1) {
				player.getTempAttribs().setI(MAX_OBSTACLE_IDX_KEY, stageIdx);
				break;
			}
		}
	}

	private enum RollingBlock {
		A(1551, Tile.of(3354, 2841, 1), 1),
		B(1552, Tile.of(3368, 2849, 2), 2),
		C(1553, Tile.of(3374, 2835, 1), 3),
		D(1554, Tile.of(3048, 4699, 2), 3),
		E(1555, Tile.of(3044, 4699, 3), 2);

		private final int configId;
		private final Tile tile;
		private final int rotation;

		private RollingBlock(int configId, Tile tile, int rotation) {
			this.configId = configId;
			this.tile = tile;
			this.rotation = rotation;
		}
	}

	@Override
	public void start() {
		updateTop();
	}

	@Override
	public void process() {
		for (RollingBlock block : RollingBlock.values()) {
			if (WorldUtil.collides(player.getTile(), block.tile, 1, 2) && !player.hasWalkSteps() && !player.isLocked()) {
				jumpRoller(block);
				return;
			}
		}
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		int id = object.getId();
		if (id == 10857) {
			if (!Agility.hasLevel(player, 30))
				return false;
			if (player.getPlane() == 3) {
				player.useStairs(-1, player.transform(-320, 1859, -1), 1, 1);
				player.getTempAttribs().setI(VIRTUAL_PLANE_KEY, 4);
			}
			else {
				player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
				int virtualPlane = player.getTempAttribs().getI(VIRTUAL_PLANE_KEY);
				player.getTempAttribs().setI(VIRTUAL_PLANE_KEY, virtualPlane + 1);
			}
		} else if (id == 10858) {
			if (player.getY() > 4000 && player.getPlane() == 2)
				player.useStairs(-1, player.transform(320, -1859, 1), 1, 1);
			else
				player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
		} else if (id == 10865)
			climbOver(object);
		else if (id == 10859)
			jumpGap(object);
		else if (id == 10868 || id == 10867)
			walkLog(object);
		else if (object.getDefinitions(player).getFirstOption().equals("Cross") && object.getDefinitions(player).getName().equals("Ledge"))
			shimmySideways(object);
		else if (object.getDefinitions(player).getFirstOption().equals("Cross") && object.getDefinitions(player).getName().equals("Gap")) {
			if (object.getType() == ObjectType.STRAIGHT_INSIDE_WALL_DEC) {
				for (GameObject surr : World.getSurroundingBaseObjects(object, 2))
					if (surr.getDefinitions(player).getFirstOption() != null && surr.getDefinitions(player).getFirstOption().equals("Cross") && surr.getDefinitions(player).getName().equals("Gap") && surr.getType() == ObjectType.SCENERY_INTERACT)
						shimmyHandholds(new GameObject(surr.getId(), surr.getType(), surr.getRotation(), surr.getX(), surr.getY(), surr.getPlane()));
			} else
				shimmyHandholds(object);
		} else if (id == 10851) {
			if (!grabbedTop)
				grabTop(object);
		} else if (id == 10855 || id == 10856)
			finishCourse();
		else if (id == 16536)
			climbRocks(player, object);
		else if (id == 16535) {
			forceClose();
			climbRocks(player, object);
		}
		return false;
	}

	@Override
	public void onTeleported(TeleType type) {
		removeController();
	}

	@Override
	public boolean login() { return false; }

	@Override
	public boolean logout() { return false; }

	public boolean failed() {
		int agilityLevel = player.getSkills().getLevel(Constants.AGILITY);
		double successProbability = Math.min((double)agilityLevel / 75.0, 1.0);
		int maxObstacleIdx = player.getTempAttribs().getI(MAX_OBSTACLE_IDX_KEY);
		return !Agility.rollSuccess(player, successProbability, Agility.AGILITY_PYRAMID, maxObstacleIdx);
	}

	private void updateTop() {
		player.getVars().setVarBit(1556, grabbedTop ? 1 : 0);
	}

	public void finishCourse() {
		if (grabbedTop) {
			player.tele(Tile.of(3364, 2830, 0));
			//player.getSkills().addXp(Constants.AGILITY, 300+(player.getSkills().getLevelForXp(Constants.AGILITY)*8)); //osrs rates?
			player.getSkills().addXp(Constants.AGILITY, 500);
			grabbedTop = false;
			updateTop();
			player.incrementCount("Agility Pyramid laps");
			Agility.removeStage(player, Agility.AGILITY_PYRAMID);
			player.getTempAttribs().removeI(VIRTUAL_PLANE_KEY);
			player.getTempAttribs().removeI(MAX_OBSTACLE_IDX_KEY);
		} else
			player.startConversation(new Conversation(player, new Dialogue(new PlayerStatement(HeadE.CONFUSED, "I feel like I am forgetting something..."))));
	}

	private void grabTop(GameObject object) {
		player.setNextFaceTile(player.transform(1, 0, 0));
		player.lock();
		player.getTasks().schedule(0, () -> player.anim(3063));
		player.getTasks().schedule(2, () -> {
			player.getInventory().addItemDrop(6970, 1);
			grabbedTop = true;
			updateTop();
			player.startConversation(new Conversation(player, new Dialogue(new ItemStatement(6970, "You find a pyramid top!"))));
			player.unlock();
		});
	}

	//3056 fail
	public void shimmyHandholds(GameObject object) {
		updateMaxObstacleIdx(Obstacle.SHIMMY_HANDHOLDS);
		int startAnim = 3057; //3053 alt
		int renderEmote = 387;
		int endAnim = 3058;
		if (object.getId() == 10883 || object.getId() == 10885 || object.getId() == 10862) {
			startAnim -= 4;
			renderEmote -= 1;
			endAnim = 3055;
		} else if (failed())
			endAnim= 3056;
		if (object.getRotation() % 2 == 0)
			shimmy(object.getTile().transform(object.getRotation() == 0 ? 1 : 0, player.getY() < object.getY() ? 4 : -4, 0), startAnim, renderEmote, endAnim, endAnim == 3055 || endAnim == 3056);
		else
			shimmy(object.getTile().transform(player.getX() < object.getX() ? 4 : -4, object.getRotation() == 3 ? 1 : 0, 0), startAnim, renderEmote, endAnim, endAnim == 3055 || endAnim == 3056);
	}

	//760 761 fail
	public void shimmySideways(GameObject object) {
		updateMaxObstacleIdx(Obstacle.SHIMMY_SIDEWAYS);
		int startAnim = 752;
		int renderEmote = 156;
		int endAnim = 758;
		boolean failed = failed();
		if (object.getId() == 10886 || (object.getId() == 10860 && (object.getTile().isAt(3372, 2839) || object.getTile().isAt(3364, 2851))) || object.getId() == 10888) {
			startAnim++;
			renderEmote++;
			endAnim++;
			if (failed)
				endAnim = 761;
		} else {
			endAnim = 760;
			failed = true;
		}
		if (object.getRotation() % 2 == 0)
			shimmy(object.getTile().transform(object.getRotation() == 0 ? 1 : 0, player.getY() < object.getY() ? 4 : -4, 0), startAnim, renderEmote, endAnim, failed);
		else
			shimmy(object.getTile().transform(player.getX() < object.getX() ? 4 : -4, object.getRotation() == 3 ? 1 : 0, 0), startAnim, renderEmote, endAnim, failed);
	}

	public void shimmy(final Tile toTile, final int startAnim, final int renderEmote, final int endAnim, final boolean fail) {
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		WorldTasks.scheduleLooping(new Task() {
			int ticks;
			@Override
			public void run() {
				if (ticks == 0) {
					player.setNextAnimation(new Animation(startAnim));
					player.getAppearance().setBAS(renderEmote);
				} else if (ticks == 1)
					player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
				else if (ticks == 3 && fail) {
					player.setNextAnimation(new Animation(endAnim));
					player.resetWalkSteps();
				} else if (ticks >= 4) {
					if (fail) {
						player.tele(World.findClosestAdjacentFreeTile(player.transform(0, 0, -1), 2));
						player.applyHit(new Hit(null, 100, HitLook.TRUE_DAMAGE));
						int virtualPlane = player.getTempAttribs().getI(VIRTUAL_PLANE_KEY);
						player.getTempAttribs().setI(VIRTUAL_PLANE_KEY, virtualPlane - 1);
					} else {
						player.setNextAnimation(new Animation(endAnim));
						player.getSkills().addXp(Constants.AGILITY, 52);
						int maxObstacleIdx = player.getTempAttribs().getI(MAX_OBSTACLE_IDX_KEY);
						Agility.setStageProgress(player, Agility.AGILITY_PYRAMID, maxObstacleIdx, true);
					}
					player.getAppearance().setBAS(-1);
					player.setRunHidden(running);
					player.unlock();
					stop();
				}
				ticks++;
			}
		}, 0, 1);
	}

	public static void climbRocks(Player player, GameObject object) {
		if (player.getX() > object.getX()) {
			final boolean running = player.getRun();
			player.setRunHidden(false);
			player.lock();
			player.addWalkSteps(player.transform(-4, 0, 0), -1, false);
			player.getControllerManager().forceStop();
			WorldTasks.scheduleLooping(new Task() {
				boolean secondloop;
				@Override
				public void run() {
					if (!secondloop) {
						secondloop = true;
						player.getAppearance().setBAS(0);
					} else {
						player.getAppearance().setBAS(-1);
						player.setRunHidden(running);
						player.unlock();
						stop();
					}
				}
			}, 0, 3);
		} else {
			final Tile toTile = player.transform(4, 0, 0);
			player.setNextAnimation(new Animation(740));
			player.forceMove(toTile, 20, 120, () -> player.setNextAnimation(new Animation(-1)));
		}
	}

	public void walkLog(GameObject object) {
		updateMaxObstacleIdx(Obstacle.WALK_LOG);
		final boolean running = player.getRun();
		final Tile toTile;
		if (object.getRotation() % 2 == 0)
			toTile = object.getTile().transform(object.getId() == 10867 ? 5 : -5, 0, 0);
		else
			toTile = object.getTile().transform(0, object.getId() == 10867 ? 5 : -5, 0);
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
		WorldTasks.scheduleLooping(new Task() {
			boolean secondloop;
			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearance().setBAS(155);
				} else {
					player.getAppearance().setBAS(-1);
					player.setRunHidden(running);
					if (object.getId() == 10868) {
						player.getSkills().addXp(Constants.AGILITY, 56.4);
						int maxObstacleIdx = player.getTempAttribs().getI(MAX_OBSTACLE_IDX_KEY);
						Agility.setStageProgress(player, Agility.AGILITY_PYRAMID, maxObstacleIdx, true);
					}
					player.unlock();
					stop();
				}
			}
		}, 0, 5);
	}

	public void jumpGap(GameObject object) {
		updateMaxObstacleIdx(Obstacle.JUMP_GAP);
		final Tile toTile;
		if (object.getRotation() % 2 == 0)
			toTile = player.transform(0, player.getY() < object.getY() ? 3 : -3, 0);
		else
			toTile = player.transform(player.getX() < object.getX() ? 3 : -3, 0, 0);
		player.lock();
		player.setNextAnimation(new Animation(3067));
		player.forceMove(toTile, 5, 60, () -> player.getSkills().addXp(Constants.AGILITY, 22));
		int maxObstacleIdx = player.getTempAttribs().getI(MAX_OBSTACLE_IDX_KEY);
		Agility.setStageProgress(player, Agility.AGILITY_PYRAMID, maxObstacleIdx, true);
	}

	public void climbOver(GameObject object) {
		updateMaxObstacleIdx(Obstacle.CLIMB_OVER);
		final Tile toTile;
		if (failed()) {
			player.applyHit(new Hit(null, 40, HitLook.TRUE_DAMAGE));
			player.setNextForceTalk(new ForceTalk("Ouch!"));
			player.lock(3);
			return;
		}
		if (object.getRotation() % 2 == 0)
			toTile = player.transform(player.getX() < object.getX() ? 2 : -2, 0, 0);
		else
			toTile = player.transform(0, player.getY() < object.getY() ? 2 : -2, 0);
		player.lock();
		player.setNextAnimation(new Animation(1560));
		player.forceMove(toTile, 5, 60, () -> player.getSkills().addXp(Constants.AGILITY, 8));
		int maxObstacleIdx = player.getTempAttribs().getI(MAX_OBSTACLE_IDX_KEY);
		Agility.setStageProgress(player, Agility.AGILITY_PYRAMID, maxObstacleIdx, true);
	}

	public void jumpRoller(RollingBlock block) {
		updateMaxObstacleIdx(Obstacle.JUMP_ROLLER);
		int virtualPlane = player.getTempAttribs().getI(VIRTUAL_PLANE_KEY);
		virtualPlane -= 1;
		boolean failed = failed();
		byte[] dir = Utils.getDirection(player.getFaceAngle());
		if (dir[0] != 0 && dir[1] != 0 || failed) {
			int x = 0, y = 0, z = -1;
            y = switch (block.rotation) {
                case 0 -> {
                    x = 0;
                    yield -2;
                }
                case 1 -> {
                    x = -2;
                    yield 0;
                }
                case 2 -> {
                    x = 0;
                    yield 2;
                }
                case 3 -> {
                    x = 2;
                    yield 0;
                }
                default -> y;
            };
			if (block == RollingBlock.D) {
				x = 322;
				y = -1856;
				z = 1;
			}
			player.lock();
			player.getVars().setVarBit(block.configId, 1);
			player.setNextAnimation(new Animation(3064));
			if (failed)
				player.getTempAttribs().setI(VIRTUAL_PLANE_KEY, virtualPlane);
			player.forceMove(player.transform(x, y, z), 10, 60, () -> {
				player.getVars().setVarBit(block.configId, 0);
				player.applyHit(new Hit(null, 60, HitLook.TRUE_DAMAGE));
			});
			return;
		}
		player.lock();
		player.getVars().setVarBit(block.configId, 1);
		player.setNextAnimation(new Animation(1115));
		player.forceMove(player.transform(dir[0]*2, dir[1]*2, 0), 5, 50, () -> {
			player.getVars().setVarBit(block.configId, 0);
			player.getSkills().addXp(Constants.AGILITY, 12);
		});
	}
}
