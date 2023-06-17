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
package com.rs.game.content.skills.dungeoneering.rooms.puzzles;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.content.skills.dungeoneering.skills.DungHatchet;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

import java.util.HashSet;
import java.util.Set;

public class FlowerRootsRoom extends PuzzleRoom {

	//order: blue, purple, red, yellow, big plant 7 ticks, small plants 14 ticks

	//bprybpry
	//BbpprryY
	//pPRryybb
	//rryYBbpp
	//yybbpPRr


	private static final int[] DOOR_LEAVES =
		{ 35838, 35839, 35842, 35847, 35849 };
	private static final int[][][] PLANTS =
		{
				{
					//LARGE
					{ 35507, 35520 },
					{ 35523, 35525 },
					{ 35562, 35568 },
					{ 35569, 35576 } },
				{
						//SMALL 1
						{ 35577, 35588 },
						{ 35602, 35604 },
						{ 35606, 35609 },
						{ 35611, 35613 } },
				{
							//SMALL 2
							{ 35616, 35625 },
							{ 35655, 35685 },
							{ 35689, 35708 },
							{ 35709, 35712 } },
				{
								//SMALL 3
								{ 35715, 35718 },
								{ 35719, 35720 },
								{ 35734, 35739 },
								{ 35778, 35780 } },
				{
									//SMALL 4
									{ 35799, 35800 },
									{ 35804, 35808 },
									{ 35809, 35812 },
									{ 35830, 35835 } },

		};

	//TODO: need end animations: tested 14900-15200 14954 could be useable
	private static final int BIG_FLOWER_DESPAWN = 14954;
	private static final int SMALL_FLOWER_DESPAWN = 14954;
	private static final int LEAF_DESPAWN = 14954;

	private Plant[][] plants;
	private WorldTask colorTask;
	private WorldTask objectTask;
	private Plant bigPlant;
	private Set<GameObject> leaves;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
		leaves = new HashSet<>();
		plants = new Plant[16][16];
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++) {
				GameObject object = manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, x, y);
				if (object != null)
					for (int type = 0; type < 5; type++) {
						if (object.getId() == DOOR_LEAVES[type]) {
							leaves.add(object);
							break;
						}
						for (int color = 0; color < 4; color++)
							if (object.getId() == PLANTS[type][color][0]) {
								plants[x][y] = new Plant();
								plants[x][y].type = type;
								plants[x][y].currentColor = color;
								if (type == 0)
									bigPlant = plants[x][y];
							}
					}
			}
		colorTask = new ChangeColorTask();
		objectTask = new ChangeObjectTask();
		WorldTasks.schedule(colorTask, 0, 6);
		WorldTasks.schedule(objectTask, 4, 6); //color animation is 3 ticks
	}

	@Override
	public boolean processObjectClick1(final Player player, final GameObject object) {
		for (int type = 0; type < 5; type++)
			for (int color = 0; color < 4; color++)
				if (object.getId() == PLANTS[type][color][0] || object.getId() == PLANTS[type][color][1]) { //[1] is clickable for big plant aswell
					final int[] coords = manager.getRoomPos(object.getTile());
					if (type == 0) {
						if (!hasRequirement(player, Constants.FARMING)) {
							player.sendMessage("You need a farming level of " + getRequirement(Constants.FARMING) + " to uproot this plant.");
							return false;
						}
						giveXP(player, Constants.FARMING);
						player.lock(2);
						player.setNextAnimation(new Animation(3685));
						setComplete();
						return false;
					}
					if (plants[coords[0]][coords[1]].currentColor != bigPlant.currentColor) {
						player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * .15), HitLook.TRUE_DAMAGE));
						return false;
					}
					if (!hasRequirement(player, Constants.WOODCUTTING)) {
						player.sendMessage("You need a woodcutting level of " + getRequirement(Constants.WOODCUTTING) + " to chop down this plant.");
						return false;
					}
					DungHatchet defs = DungHatchet.getHatchet(player);
					if (defs == null) {
						player.sendMessage("You do not have a hatchet or do not have the required level to use the hatchet.");
						return false;
					}
					if (plants[coords[0]][coords[1]].locked)
						//already being used by other player
						return false;
					plants[coords[0]][coords[1]].locked = true;
					player.setNextAnimation(new Animation(defs.getEmoteId()));
					player.lock(4);
					for (Player team : manager.getParty().getTeam())
						team.getPackets().sendObjectAnimation(object, new Animation(SMALL_FLOWER_DESPAWN));
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							giveXP(player, Constants.WOODCUTTING);
							player.setNextAnimation(new Animation(-1));
							plants[coords[0]][coords[1]] = null;
							manager.spawnObject(reference, -1, ObjectType.SCENERY_INTERACT, 0, coords[0], coords[1]);

						}
					}, 3);
					return false;
				}
		return true;
	}

	@Override
	public void setComplete() {
		if (manager.isDestroyed())
			return;

		for (Player team : manager.getParty().getTeam()) {
			for (int x = 0; x < 16; x++)
				for (int y = 0; y < 16; y++) {
					Plant p = plants[x][y];
					if (p != null) {
						p.locked = true;
						if (p.type == 0)
							team.getPackets().sendObjectAnimation(manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, x, y), new Animation(BIG_FLOWER_DESPAWN));
						else
							team.getPackets().sendObjectAnimation(manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, x, y), new Animation(SMALL_FLOWER_DESPAWN));
					}
				}
			for (GameObject leaf : leaves)
				team.getPackets().sendObjectAnimation(leaf, new Animation(LEAF_DESPAWN));
		}
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int x = 0; x < 16; x++)
					for (int y = 0; y < 16; y++) {
						Plant p = plants[x][y];
						if (p != null) {
							plants[x][y] = null;
							manager.spawnObject(reference, -1, ObjectType.SCENERY_INTERACT, 0, x, y);
						}
					}
				for (GameObject leaf : leaves)
					World.removeObject(leaf);

			}
		}, 1);
		destroy();
		super.setComplete();
	}

	public class ChangeColorTask extends WorldTask {

		boolean odd;

		@Override
		public void run() {
			synchronized (manager) {
				if (manager.isDestroyed())
					return;
				for (int x = 0; x < 16; x++)
					for (int y = 0; y < 16; y++) {
						Plant p = plants[x][y];
						if (p != null && !p.locked)
							if (p.type == 0 || odd) {
								replaceObject(manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, x, y), PLANTS[p.type][p.currentColor][1]);
								p.currentColor++;
								p.currentColor &= 0x3;
							}
					}
			}
			odd = !odd;
		}

	}

	public class ChangeObjectTask extends WorldTask {

		boolean odd;

		@Override
		public void run() {
			synchronized (manager) {
				if (manager.isDestroyed()) {
					stop();
					return;
				}
				for (int x = 0; x < 16; x++)
					for (int y = 0; y < 16; y++) {
						Plant p = plants[x][y];
						if (p != null && !p.locked)
							if (p.type == 0 || odd)
								replaceObject(manager.getObjectWithType(reference, ObjectType.SCENERY_INTERACT, x, y), PLANTS[p.type][p.currentColor][0]);
					}
			}
			odd = !odd;
		}

	}

	public class Plant {

		public boolean locked;
		private int type;
		private int currentColor;

	}

	@Override
	public void destroy() {
		if (colorTask != null) {
			colorTask.stop();
			colorTask = null;
		}
		if (objectTask != null) {
			objectTask.stop();
			objectTask = null;
		}
	}

}
