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
package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public final class DungeoneeringWoodcutting extends Action {

	private GameObject treeObj;
	private DungTree type;
	private DungHatchet hatchet;

	public DungeoneeringWoodcutting(GameObject treeObj, DungTree type) {
		this.treeObj = treeObj;
		this.type = type;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.sendMessage("You swing your hatchet at the tree...", true);
		setActionDelay(player, 4);
		return true;
	}

	private boolean checkAll(Player player) {
		hatchet = DungHatchet.getHatchet(player);
		if (hatchet == null) {
			player.sendMessage("You dont have the required level to use that axe or you don't have a hatchet.");
			return false;
		}
		if (!hasWoodcuttingLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("Not enough space in your inventory.");
			return false;
		}
		return true;
	}

	private boolean hasWoodcuttingLevel(Player player) {
		if (type.getLevel() > player.getSkills().getLevel(8)) {
			player.sendMessage("You need a woodcutting level of " + type.getLevel() + " to chop down this tree.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(hatchet.getEmoteId()));
		return checkTree(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (type.rollSuccess(player.getSkills().getLevel(Constants.WOODCUTTING), hatchet)) {
			type.giveLog(player);
			if (Utils.random(8) == 0) {
				World.spawnObject(new GameObject(treeObj.getId() + 1, treeObj.getType(), treeObj.getRotation(), treeObj));
				player.sendMessage("You have depleted this resource.");
				return -1;
			}
		}
		return 4;
	}

	private boolean checkTree(Player player) {
		return World.getRegion(treeObj.getRegionId()).objectExists(treeObj);
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 4);
	}
}
