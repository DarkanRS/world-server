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
package com.rs.game.content.skills.hunter;

import com.rs.game.World;
import com.rs.game.content.skills.hunter.traps.BoxStyleTrap;
import com.rs.game.content.skills.hunter.traps.DeadfallTrap;
import com.rs.game.content.skills.hunter.traps.MarasamawPlant;
import com.rs.game.content.skills.hunter.traps.NetTrap;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.OwnedObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;

public class BoxAction extends PlayerAction {

	private BoxTrapType type;
	private BoxStyleTrap trap;
	private GroundItem groundItem;
	private GameObject obj;
	private WorldTile tile;

	public BoxAction(BoxTrapType type, GroundItem groundItem, GameObject obj) {
		this.type = type;
		this.groundItem = groundItem;
		this.obj = obj;
	}

	public BoxAction(BoxTrapType type, GroundItem groundItem) {
		this(type, groundItem, null);
	}

	public BoxAction(BoxTrapType type, GameObject obj) {
		this(type, null, obj);
	}

	public BoxAction(BoxTrapType type) {
		this(type, null, null);
	}

	@Override
	public boolean start(Player player) {
		player.resetWalkSteps();
		tile = WorldTile.of(player.getTile());
		if (type == BoxTrapType.MARASAMAW_PLANT)
			trap = new MarasamawPlant(player, WorldTile.of(player.getTile()));
		else if (type == BoxTrapType.TREE_NET)
			trap = new NetTrap(player, WorldTile.of(player.getTile()), obj);
		else if (type == BoxTrapType.DEAD_FALL)
			trap = new DeadfallTrap(player, obj);
		else
			trap = new BoxStyleTrap(player, type, WorldTile.of(player.getTile()));
		if (!checkAll(player))
			return false;
		if (groundItem == null && type != BoxTrapType.TREE_NET) {
			player.getInventory().deleteItem(type.getId(), 1);
			World.addGroundItem(new Item(type.getId(), 1), WorldTile.of(player.getTile()), player, true, 180);
		}
		player.sendMessage("You start setting up the trap..");
		player.setNextAnimation(type == BoxTrapType.TREE_NET ? new Animation(5215) : new Animation(5208));
		setActionDelay(player, type == BoxTrapType.TREE_NET ? 1 : 2);
		return true;
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (type != BoxTrapType.DEAD_FALL && !player.addWalkSteps(player.getX() - 1, player.getY(), 1))
			if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
				if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
					player.addWalkSteps(player.getX(), player.getY() - 1, 1);
		boolean created = false;
		if (type == BoxTrapType.DEAD_FALL) {
			trap.createReplace();
			created = true;
		} else
			created = trap.createNoReplace();
		if (created && type.getId() != -1)
			if (type == BoxTrapType.TREE_NET) {
				player.getInventory().deleteItem(954, 1);
				player.getInventory().deleteItem(303, 1);
			} else {
				GroundItem item = groundItem != null ? groundItem : World.getRegion(tile.getRegionId()).getGroundItem(type.getId(), tile, player);
				if (item != null)
					World.removeGroundItem(player, item, false);
			}
		return -1;
	}

	@Override
	public void stop(final Player player) {
		player.unlock();
		setActionDelay(player, 3);
	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Constants.HUNTER) < type.getBaseLevel()) {
			player.sendMessage("You need a Hunter level of " + type.getBaseLevel() + " to place this kind of trap.");
			return false;
		}
		if (type == BoxTrapType.TREE_NET && (!player.getInventory().containsItem(954, 1, false) || !player.getInventory().containsItem(303, 1, false))) {
			player.sendMessage("You need a small fishing net and some rope to set this trap.");
			return false;
		}
		int trapAmt = getTrapAmount(player);
		int numberSetup = OwnedObject.getNumOwned(player, BoxStyleTrap.class);
		if (numberSetup >= trapAmt) {
			player.sendMessage("You can't setup more than " + trapAmt + " traps.");
			return false;
		}
		if (type != BoxTrapType.DEAD_FALL && trap.overlapsExisting()) {
			player.sendMessage("You can't place a trap here.");
			return false;
		}
		return true;
	}

	public int getTrapAmount(Player player) {
		return 1 + (int) Math.floor(player.getSkills().getLevel(Constants.HUNTER) / 20);
	}
}