package com.rs.game.player.content.skills.hunter;

import java.util.HashMap;
import java.util.Map;

import com.rs.lib.game.Animation;

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
