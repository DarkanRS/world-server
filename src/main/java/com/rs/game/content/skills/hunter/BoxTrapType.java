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

import com.rs.lib.game.Animation;

import java.util.HashMap;
import java.util.Map;

public enum BoxTrapType {

	BIRD_SNARE(10006, 19175, new Animation(5207), 1),
	DEAD_FALL(1511, 19206, new Animation(451), 23),
	BOX(10008, 19187, new Animation(451), 27),
	TREE_NET(303, -1, new Animation(5207), 29),
	MARASAMAW_PLANT(19965, 56806, new Animation(451), 70),
	MAGIC_BOX(10025, 19223, new Animation(451), 71);

	private int itemId, objectId, baseLevel;
	private Animation pickUpAnimation;

	private static Map<Integer, BoxTrapType> TRAPS_BY_ITEM = new HashMap<>();

	static {
		for (BoxTrapType t : BoxTrapType.values())
			TRAPS_BY_ITEM.put(t.itemId, t);
	}

	public static BoxTrapType forId(int id) {
		return TRAPS_BY_ITEM.get(id);
	}

	private BoxTrapType(int itemId, int objectId, Animation pickUpAnimation, int baseLevel) {
		this.itemId = itemId;
		this.objectId = objectId;
		this.pickUpAnimation = pickUpAnimation;
		this.baseLevel = baseLevel;
	}

	public int getId() {
		return itemId;
	}

	public int getObjectId() {
		return objectId;
	}

	public Animation getPickUpAnimation() {
		return pickUpAnimation;
	}

	public int getBaseLevel() {
		return baseLevel;
	}
}
