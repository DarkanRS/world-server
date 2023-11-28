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
package com.rs.game.content.skills.dungeoneering.npcs;

import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class WorldGorgerShukarhazh extends DungeonBoss {

	private static final int[][] EYE_COORDINATES =
		{
				{ 0, 7 },
				{ 7, 15 },
				{ 15, 8 } };

	private final FamishedEye[] eyes;

	public WorldGorgerShukarhazh(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12478, 12492), manager.getBossLevel()), tile, manager, reference);
		eyes = new FamishedEye[3];
		for (int idx = 0; idx < eyes.length; idx++)
			eyes[idx] = new FamishedEye(this, 12436 + (idx * 15), manager.getTile(reference, EYE_COORDINATES[idx][0], EYE_COORDINATES[idx][1]), manager); //TODO scale to combat level
		refreshCapDamage();
	}

	@Override
	public void processHit(Hit hit) {
		for (FamishedEye eye : eyes)
			if (eye.getType() == hit.getLook().getMark()) {
				if (eye.isInactive())
					hit.setDamage((int) (hit.getDamage() * 0.1));
				break;
			}
		super.processHit(hit);
	}

	public void refreshCapDamage() {
		int inactiveCounter = 0;
		for (FamishedEye eye : eyes) {
			boolean inactive = eye.isInactive();
			if (inactive)
				inactiveCounter++;
		}
		setCapDamage(inactiveCounter == 0 ? 500 : inactiveCounter == 1 ? 1500 : -1);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;//reg
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 1.0;//prayer dun work m8t
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		for (FamishedEye eye : eyes)
			eye.finish();
	}

	public FamishedEye[] getFamishedEyes() {
		return eyes;
	}
}
