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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringFarming.Harvest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.OwnedObject;

public class DungFarmPatch extends OwnedObject {

	private int time;
	private int stage = 1;
	private final Harvest harvest;
	private final DungeonManager manager;

	public DungFarmPatch(Player player, Harvest harvest, GameObject basePatch, DungeonManager manager) {
		super(player, DungeonConstants.EMPTY_FARMING_PATCH + 1 + 1 + (harvest.ordinal() * 3), basePatch.getType(), basePatch.getRotation(), basePatch.getTile());
		this.harvest = harvest;
		this.manager = manager;
	}

	@Override
	public void onDestroy() {
		manager.getFarmingPatches().remove(this);
	}

	@Override
	public void tick(Player owner) {
		time++;
		if (time >= 50 && stage < 3) {
			time = 0;
			stage++;
			setId(DungeonConstants.EMPTY_FARMING_PATCH + 1 + stage + (harvest.ordinal() * 3));
		}
	}
}
