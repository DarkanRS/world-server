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
import com.rs.game.World;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.unorganized_dialogue.SimonTempletonD;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.ItemStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class AgilityPyramidController extends Controller {

	private boolean grabbedTop;

	private enum RollingBlock {
		A(1551, Tile.of(3354, 2841, 1), 1),
		B(1552, Tile.of(3368, 2849, 2), 2),
		C(1553, Tile.of(3374, 2835, 1), 3),
		D(1554, Tile.of(3048, 4699, 2), 3),
		E(1555, Tile.of(3044, 4699, 3), 2);

		private int configId;
		private Tile tile;
		private int rotation;

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
		for (RollingBlock block : RollingBlock.values())
			if (WorldUtil.collides(player.getTile(), block.tile, 1, 2) && !player.hasWalkSteps() && !player.isLocked()) {
				jumpRoller(block, false); //TODO fail calc
				return;
			}
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		int id = object.getId();
		if (id == 10857) {
			if (!Agility.hasLevel(player, 30))
				return false;
			if (player.getPlane() == 3)
				player.useStairs(-1, player.transform(-320, 1859, -1), 1, 1);
			else
				player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
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
				for (GameObject surr : World.getSurroundingObjects(object, 2))
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
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 3123)
			player.startConversation(new SimonTempletonD(player, npc.getId()));
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	public boolean failed() {
		return (player.getSkills().getLevel(Constants.AGILITY) / 75.0) < Math.random();
	}

	private void updateTop() {
		player.getVars().setVarBit(1556, grabbedTop ? 1 : 0);
	}

	public void finishCourse() {
		if (grabbedTop) {
			player.setNextTile(Tile.of(3364, 2830, 0));
			//player.getSkills().addXp(Constants.AGILITY, 300+(player.getSkills().getLevelForXp(Constants.AGILITY)*8)); //osrs rates?
			player.getSkills().addXp(Constants.AGILITY, 500);
			grabbedTop = false;
			updateTop();
			player.incrementCount("Agility Pyramid laps");
		} else
			player.startConversation(new Conversation(player, new Dialogue(new PlayerStatement(HeadE.CONFUSED, "I feel like I am forgetting something..."))));
	}

	private void grabTop(GameObject object) {
		player.setNextFaceTile(player.transform(1, 0, 0));
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			int ticks;
			@Override
			public void run() {
				if (ticks == 0)
					player.setNextAnimation(new Animation(3063));
				else if (ticks >= 2) {
					player.getInventory().addItemDrop(6970, 1);
					grabbedTop = true;
					updateTop();
					player.startConversation(new Conversation(player, new Dialogue(new ItemStatement(6970, "You find a pyramid top!"))));
					player.unlock();
					stop();
				}
				ticks++;
			}
		}, 0, 1);
	}

	//3056 fail
	public void shimmyHandholds(GameObject object) {
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
		WorldTasks.schedule(new WorldTask() {
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
						player.setNextTile(World.findClosestAdjacentFreeTile(player.transform(0, 0, -1), 2));
						player.applyHit(new Hit(null, 100, HitLook.TRUE_DAMAGE));
					} else {
						player.setNextAnimation(new Animation(endAnim));
						player.getSkills().addXp(Constants.AGILITY, 52);
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
			WorldTasks.schedule(new WorldTask() {
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
			player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 4, Direction.WEST));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextAnimation(new Animation(-1));
					player.setNextTile(toTile);
				}
			}, 3);
		}
	}

	public void walkLog(GameObject object) {
		final boolean running = player.getRun();
		final Tile toTile;
		if (object.getRotation() % 2 == 0)
			toTile = object.getTile().transform(object.getId() == 10867 ? 5 : -5, 0, 0);
		else
			toTile = object.getTile().transform(0, object.getId() == 10867 ? 5 : -5, 0);
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
		WorldTasks.schedule(new WorldTask() {
			boolean secondloop;
			@Override
			public void run() {
				if (!secondloop) {
					secondloop = true;
					player.getAppearance().setBAS(155);
				} else {
					player.getAppearance().setBAS(-1);
					player.setRunHidden(running);
					if (object.getId() == 10868)
						player.getSkills().addXp(Constants.AGILITY, 56.4);
					player.unlock();
					stop();
				}
			}
		}, 0, 5);
	}

	public void jumpGap(GameObject object) {
		Direction direction = Direction.NORTH;
		final Tile toTile;
		if (object.getRotation() % 2 == 0)
			toTile = player.transform(0, player.getY() < object.getY() ? 3 : -3, 0);
		else
			toTile = player.transform(player.getX() < object.getX() ? 3 : -3, 0, 0);
		if (player.getX() < toTile.getX())
			direction = Direction.EAST;
		else if (player.getX() > toTile.getX())
			direction = Direction.WEST;
		else if (player.getY() < toTile.getY())
			direction = Direction.NORTH;
		else if (player.getY() > toTile.getY())
			direction = Direction.SOUTH;
		player.lock();
		player.setNextAnimation(new Animation(3067));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 2, direction));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlock();
				player.setNextTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 22);
			}
		}, 1);
	}

	public void climbOver(GameObject object) {
		Direction direction = Direction.NORTH;
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
		if (player.getX() < toTile.getX())
			direction = Direction.EAST;
		else if (player.getX() > toTile.getX())
			direction = Direction.WEST;
		else if (player.getY() < toTile.getY())
			direction = Direction.NORTH;
		else if (player.getY() > toTile.getY())
			direction = Direction.SOUTH;
		player.lock();
		player.setNextAnimation(new Animation(1560));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 2, direction));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlock();
				player.setNextTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 8);
			}
		}, 1);
	}

	public void jumpRoller(RollingBlock block, boolean failed) {
		byte[] dir = Utils.getDirection(player.getFaceAngle());
		if (dir[0] != 0 && dir[1] != 0 || failed) {
			int x = 0, y = 0, z = -1;
			switch(block.rotation) {
			case 0:
				x = 0;
				y = -2;
				break;
			case 1:
				x = -2;
				y = 0;
				break;
			case 2:
				x = 0;
				y = 2;
				break;
			case 3:
				x = 2;
				y = 0;
				break;
			}
			if (block == RollingBlock.D) {
				x = 322;
				y = -1856;
				z = 1;
			}
			final Tile toTile = player.transform(x, y, z);
			player.lock();
			player.getVars().setVarBit(block.configId, 1);
			player.setNextAnimation(new Animation(3064));
			player.setNextForceMovement(new ForceMovement(player.getTile(), 0, Utils.getDistance(player.getTile(), toTile) > 50 ? player.transform(2, 0, -1) : toTile, 2, Direction.forDelta(toTile.getX() - player.getX(), toTile.getY() - player.getY())));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.unlock();
					player.getVars().setVarBit(block.configId, 0);
					player.setNextTile(toTile);
					player.applyHit(new Hit(null, 60, HitLook.TRUE_DAMAGE));
				}
			}, 2);
			return;
		}
		final Tile toTile = (player.transform(dir[0]*2, dir[1]*2, 0));
		player.lock();
		player.getVars().setVarBit(block.configId, 1);
		player.setNextAnimation(new Animation(1115));
		player.setNextForceMovement(new ForceMovement(player.getTile(), 0, toTile, 1, WorldUtil.getFaceDirection(toTile, player)));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getVars().setVarBit(block.configId, 0);
				player.setNextTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 12);
				player.unlock();
			}
		}, 3);
	}

}
