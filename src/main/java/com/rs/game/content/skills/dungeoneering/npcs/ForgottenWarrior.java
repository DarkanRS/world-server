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

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class ForgottenWarrior extends Guardian {

	public ForgottenWarrior(int id, Tile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
	}

	@Override
	public void drop() {
		super.drop();
		GuardianMonster m = GuardianMonster.forId(getId());
		if (m == null)
			return;
		int size = getSize();
		ArrayList<Item> drops = new ArrayList<>();
		if (m.name().contains("WARRIOR"))
			drops.add(new Item(DungeonUtils.getRandomMeleeGear(Math.min(1+Utils.random(getTier()), 10))));
		else if (m.name().contains("MAGE"))
			drops.add(new Item(DungeonUtils.getRandomMagicGear(Math.min(1+Utils.random(getTier()), 10))));
		else
			drops.add(new Item(DungeonUtils.getRandomRangeGear(Math.min(1+Utils.random(getTier()), 10))));
		for (Item item : drops)
			World.addGroundItem(item, Tile.of(getCoordFaceX(size), getCoordFaceY(size), getPlane()));
	}

}
