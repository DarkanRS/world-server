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
package com.rs.game.player.content.skills.hunter.traps;

import java.util.Arrays;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.npc.others.BoxHunterNPC;
import com.rs.game.object.OwnedObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.BoxHunterType;
import com.rs.game.player.content.skills.hunter.BoxTrapType;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;

public class BoxStyleTrap extends OwnedObject {

	private static final int[] VALID_BAITS = { 5972, 12535 };

	public enum Status {
		IDLE,
		CATCHING,
		SUCCESS,
		FAIL
	}

	protected BoxTrapType type;
	private int bait = -1;
	private int life = 0;
	private Status status = Status.IDLE;
	private BoxHunterType npcTrapped;

	public BoxStyleTrap(Player player, BoxTrapType type, WorldTile tile) {
		super(player, -1, ObjectType.SCENERY_INTERACT, 0, tile);
		id = type.getObjectId();
		this.type = type;
		if (type != BoxTrapType.DEAD_FALL)
			routeType = RouteType.WALK_ONTO;
	}

	@Override
	public void tick(Player player) {
		life++;
		if (life >= 100) {
			expire(player);
			destroy();
		}
	}

	public void expire(Player player) {
		World.addGroundItem(new Item(type.getId(), 1), new WorldTile(this), player, true, 60);
		if (bait != -1)
			World.addGroundItem(new Item(bait, 1), new WorldTile(this), player, true, 60);
	}

	public void handleCatch(BoxHunterNPC npc, boolean success) {
		if (type == BoxTrapType.BIRD_SNARE)
			setId(success ? npc.getType().getObjectCatch() : 19176);
		else if (type == BoxTrapType.DEAD_FALL)
			setId(success ? npc.getType().getObjectCatch() : 19219);
		else
			setId(npc.getType().getObjectCatch());
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (!success && type == BoxTrapType.DEAD_FALL) {
					destroy();
					return;
				}
				setNpcTrapped(npc.getType());
				setId(success ? npc.getType().getObjectSuccess() : npc.getType().getObjectFail());
				setStatus(success ? Status.SUCCESS : Status.FAIL);
				if (success) {
					npc.setNextAnimation(new Animation(-1));
					npc.setRespawnTask();
				}
			}
		}, 0);
	}

	public void dismantle(Player player) {
		destroy();
		player.setNextAnimation(getTrapType().getPickUpAnimation());
		if (getTrapType() == BoxTrapType.TREE_NET)
			player.getInventory().addItemDrop(954, 1);
		player.getInventory().addItemDrop(getTrapType().getId(), 1);
		if (bait != -1)
			player.getInventory().addItemDrop(bait, 1);
	}

	public void check(Player player) {
		destroy();
		player.incrementCount(NPCDefinitions.getDefs(getNpcTrapped().getNpcId()).getName()+" trapped");
		player.setNextAnimation(getTrapType().getPickUpAnimation());
		for (Item i : getNpcTrapped().getItems()) {
            i.setAmount(i.getAmount()*8);
            player.getInventory().addItemDrop(i);
        }
		if (getTrapType() != BoxTrapType.MAGIC_BOX && getTrapType() != BoxTrapType.DEAD_FALL)
			player.getInventory().addItemDrop(getTrapType().getId(), 1);
		if (getTrapType() == BoxTrapType.TREE_NET)
			player.getInventory().addItemDrop(954, 1);
		if (getTrapType() == BoxTrapType.MARASAMAW_PLANT)
			player.getSkills().addXp(Constants.HUNTER, getNpcTrapped().getXp() * player.getEquipment().getWitchDoctorBoost());
		else
			player.getSkills().addXp(Constants.HUNTER, getNpcTrapped().getXp());
		player.getInventory().refresh();
	}

	public BoxTrapType getTrapType() {
		return type;
	}

	public int getBait() {
		return bait;
	}

	public boolean tryBait(Player player, int bait) {
		if (Arrays.binarySearch(VALID_BAITS, bait) < 0) {
			player.sendMessage("That's not something you'd want to use to bait a trap with.");
			return false;
		}
		if (!player.getInventory().containsItem(bait))
			return false;
		if (this.bait != -1) {
			player.sendMessage("This trap has already been baited.");
			return false;
		}
		player.getInventory().deleteItem(bait, 1);
		this.bait = bait;
		player.sendMessage("You bait the trap with the " + ItemDefinitions.getDefs(bait).name.toLowerCase() + ".");
		return true;
	}

	public int getLife() {
		return life;
	}

	public void setStatus(Status status) {
		this.status = status;
		life = 0;
	}

	public Status getStatus() {
		return status;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public BoxHunterType getNpcTrapped() {
		return npcTrapped;
	}

	public void setNpcTrapped(BoxHunterType npcTrapped) {
		this.npcTrapped = npcTrapped;
	}
}
