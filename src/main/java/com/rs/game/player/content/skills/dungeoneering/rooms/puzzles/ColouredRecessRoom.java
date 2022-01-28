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
package com.rs.game.player.content.skills.dungeoneering.rooms.puzzles;

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.dungeoneering.DungeonNPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.player.dialogues.ColouredRecessShelvesD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class ColouredRecessRoom extends PuzzleRoom {

	public static final int[] SHELVES =
		{ 35243, 35242, 35241, 35245, 35246 };

	//+1-4 for colors
	public static final int[] BASE_BLOCKS =
		{ 13024, 13029, 13034, 13039, 13044 };

	public static final int[][] LOCATIONS =
		{
				{ 5, 10 },
				{ 10, 10 },
				{ 10, 5 },
				{ 5, 5 }, };

	private Block[] blocks;
	private boolean[] used;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
		blocks = new Block[4];
		used = new boolean[4];
		for (int i = 0; i < blocks.length; i++)
			while_: while (true) {
				WorldTile tile = manager.getTile(reference, 4 + Utils.random(8), 4 + Utils.random(8));
				if (!World.floorFree(tile, 1))
					continue;
				for (int j = 0; j < i; j++)
					if (blocks[j].matches(tile))
						continue while_;
				blocks[i] = new Block(tile);
				break;
			}

	}

	public void checkComplete() {
		if(isComplete())
			return;
		outer: for (Block block : blocks) {
			for (int tileColor = 0; tileColor < LOCATIONS.length; tileColor++) {
				int[] location = LOCATIONS[tileColor];
				if (manager.getTile(reference, location[0], location[1]).matches(block)) {
					int color = block.getId() - BASE_BLOCKS[type] - 1;
					if (color == tileColor)
						continue outer;
					return;

				}
			}
			return;
		}
		setComplete();
	}

	public class Block extends DungeonNPC {

		public Block(WorldTile tile) {
			super(BASE_BLOCKS[type], tile, manager);
		}

		public void handle(final Player player, final boolean push) {
			//TODO: make sure 2 players can't move 2 statues ontop of eachother in the same tick? although it doesn't really matter
			boolean pull = !push;

			int[] nPos = manager.getRoomPos(this);
			int[] pPos = manager.getRoomPos(player);

			final int dx = push ? getX() - player.getX() : player.getX() - getX();
			final int dy = push ? getY() - player.getY() : player.getY() - getY();
			final int ldx = push ? nPos[0] - pPos[0] : pPos[0] - nPos[0];
			final int ldy = push ? nPos[1] - pPos[1] : pPos[1] - nPos[1];

			if (nPos[0] + ldx < 4 || nPos[0] + ldx > 11 || nPos[1] + ldy < 4 || nPos[1] + ldy > 11) {
				player.sendMessage("You cannot push the block there.");
				return;
			}
			final WorldTile nTarget = transform(dx, dy, 0);
			final WorldTile pTarget = player.transform(dx, dy, 0);

			if (!World.floorFree(nTarget, 1) || !World.floorFree(pTarget, 1)) {
				player.sendMessage("Something is blocking the way.");
				return;
			}
			if (!ColouredRecessRoom.this.canMove(null, nTarget) || (pull && !ColouredRecessRoom.this.canMove(null, pTarget))) {
				player.sendMessage("A block is blocking the way.");
				return;
			}

			for (Player team : manager.getParty().getTeam())
				if (team != player && team.matches(nTarget)) {
					player.sendMessage("A party member is blocking the way.");
					return;
				}

			player.lock(2);
			WorldTasks.schedule(new WorldTask() {

				private boolean moved;

				@Override
				public void run() {
					if (!moved) {
						moved = true;
						addWalkSteps(getX() + dx, getY() + dy);
						WorldTile fromTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
						player.setNextWorldTile(pTarget);
						player.setNextForceMovement(new ForceMovement(fromTile, 0, pTarget, 1, WorldUtil.getFaceDirection(Block.this, player)));
						player.setNextAnimation(new Animation(push ? 3065 : 3065));
					} else {
						checkComplete();
						stop();
					}
				}
			}, 0, 0);

		}

		public boolean useItem(Player player, Item item) {
			int color = (item.getId() - 19869) / 2;
			if (color < 0 || color > 3 || (getId() != BASE_BLOCKS[type]) || used[color])
				return true;
			used[color] = true;
			player.getInventory().deleteItem(item);
			player.setNextAnimation(new Animation(832));
			setNextNPCTransformation(getId() + color + 1);
			checkComplete();
			return false;
		}

	}

	@Override
	public boolean canMove(Player player, WorldTile to) {
		for (WorldTile block : blocks)
			if (to.matches(block))
				return false;
		return true;
	}

	@Override
	public boolean processObjectClick1(Player p, GameObject object) {
		if (object.getId() == SHELVES[type]) {
			p.getDialogueManager().execute(new ColouredRecessShelvesD(), (Object[]) null);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(Player player, NPC npc) {
		if (npc instanceof Block block) {
			block.handle(player, true);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(Player player, NPC npc) {
		if (npc instanceof Block block) {
			block.handle(player, false);
			return false;
		}
		return true;
	}

}
