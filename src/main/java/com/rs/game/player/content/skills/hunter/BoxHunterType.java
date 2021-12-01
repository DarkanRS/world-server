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
package com.rs.game.player.content.skills.hunter;

import java.util.HashMap;
import java.util.Map;

import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public enum BoxHunterType {
	
	CHINCHOMPA(5079, new Item[] { new Item(10033, 1) }, 53, 198.5, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(5184), new Animation(5185)),

	CARNIVOROUS_CHINCHOMPA(5080, new Item[] { new Item(10034, 1) }, 63, 265, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(5184), new Animation(5185)),

	GRENWALL(7010, 12535, new Item[] { new Item(12539, 18) }, 77, 1100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8602), new Animation(8602)),
	
	PAWYA(7012, 5972, new Item[] { new Item(526, 1), new Item(12535, 1) }, 66, 400, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8615), new Animation(8611)),

	WILD_KEBBIT(5089, new Item[] { new Item(526, 1), new Item(10113) }, 23, 128, BoxTrapType.DEAD_FALL, 19207, 19215, 19206, new Animation(5275), new Animation(5277)),
	
	BARB_TAILED_KEBBIT(5088, new Item[] { new Item(526, 1), new Item(10129) }, 33, 168, BoxTrapType.DEAD_FALL, 19218, 19217, 19206, new Animation(5275), new Animation(5277)),
	
	PRICKLY_KEBBIT(5086, new Item[] { new Item(526, 1), new Item(10105) }, 37, 204, BoxTrapType.DEAD_FALL, 19208, 19218, 19206, new Animation(5275), new Animation(5277)),
	
	SABRE_TOOTHED_KEBBIT(5087, new Item[] { new Item(526, 1), new Item(10109) }, 51, 200, BoxTrapType.DEAD_FALL, 19209, 19216, 19206, new Animation(5275), new Animation(5277)),
	
	FERRET(5081, new Item[] { new Item(10092) }, 27, 115, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(5191), new Animation(5192)),

	GECKO(6916, new Item[] { new Item(12184) }, 27, 100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8362), new Animation(8361)),

	RACCOON(7272, new Item[] { new Item(12487) }, 27, 100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(7726), new Animation(7727)),

	SWAMP_LIZARD(5117, new Item[] { new Item(10149) }, 29, 152, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1)),
	
	ORANGE_SALAMANDER(5114, new Item[] { new Item(10146) }, 47, 224, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1)),
	
	RED_SALAMANDER(5115, new Item[] { new Item(10147) }, 59, 272, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1)),
	
	BLACK_SALAMANDER(5116, new Item[] { new Item(10148) }, 67, 319.2, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1)),
	
	CRIMSON_SWIFT(5073, new Item[] { new Item(10088), new Item(526, 1), new Item(9978, 1) }, 1, 34, BoxTrapType.BIRD_SNARE, 19179, 19180, 19174, new Animation(6775), new Animation(6774)),

	GOLDEN_WARBLER(5075, new Item[] { new Item(1583), new Item(526, 1), new Item(9978, 1) }, 5, 48, BoxTrapType.BIRD_SNARE, 19183, 19184, 19174, new Animation(6775), new Animation(6774)),

	COPPER_LONGTAIL(5076, new Item[] { new Item(10091), new Item(526, 1), new Item(9978, 1) }, 9, 61, BoxTrapType.BIRD_SNARE, 19185, 19186, 19174, new Animation(6775), new Animation(6774)),

	CERULEAN_TWITCH(5074, new Item[] { new Item(10089), new Item(526, 1), new Item(9978, 1) }, 11, 64.67, BoxTrapType.BIRD_SNARE, 19181, 19182, 19174, new Animation(6775), new Animation(6774)),

	TROPICAL_WAGTAIL(5072, new Item[] { new Item(10087), new Item(526, 1), new Item(9978, 1) }, 19, 95.2, BoxTrapType.BIRD_SNARE, 19177, 19178, 19174, new Animation(6775), new Animation(6774)),

	WIMPY_BIRD(7031, new Item[] { new Item(11525, 1), new Item(526, 1), new Item(9978, 1) }, 39, 167, BoxTrapType.BIRD_SNARE, 29164, 29165, 19174, new Animation(6775), new Animation(6774)),

	IMP(708,new Item[] { new Item(10027) }, 71, 450, BoxTrapType.MAGIC_BOX, 19225, 19226, 19224, new Animation(5218), new Animation(5285)),
	
	COMMON_JADINKO(13119, new Item[] { new Item(19971, 1) }, 70, 350, BoxTrapType.MARASAMAW_PLANT, 56830, 56819, 56813, new Animation(3293), new Animation(3293)),

	AMPHIBIOUS_JADINKO(13130, new Item[] { new Item(19972, 1) }, 77, 485, BoxTrapType.MARASAMAW_PLANT, 56831, 56820, 56807, new Animation(3293), new Animation(3293)),

	DRACONIC_JADINKO(13131, new Item[] { new Item(19973, 1) }, 80, 525, BoxTrapType.MARASAMAW_PLANT, 56832, 56821, 56812, new Animation(3293), new Animation(3293)),

	AQUATIC_JADINKO(13142, new Item[] { new Item(19976, 1) }, 76, 475, BoxTrapType.MARASAMAW_PLANT, 56833, 56822, 56808, new Animation(3293), new Animation(3293)),

	INDIGINOUS_JADINKO(13143, new Item[] { new Item(19980, 1) }, 74, 465, BoxTrapType.MARASAMAW_PLANT, 56834, 56823, 56815, new Animation(3293), new Animation(3293)),

	CARRION_JADINKO(13154, new Item[] { new Item(19974, 1) }, 78, 505, BoxTrapType.MARASAMAW_PLANT, 56835, 56824, 56810, new Animation(3293), new Animation(3293)),

	CANNIBAL_JADINKO(13155, new Item[] { new Item(19975, 1) }, 75, 475, BoxTrapType.MARASAMAW_PLANT, 56836, 56825, 56809, new Animation(3293), new Animation(3293)),

	GUTHIX_JADINKO(13164, new Item[] { new Item(19982, 1) }, 81, 600, BoxTrapType.MARASAMAW_PLANT, 56838, 56827, 56814, new Animation(3293), new Animation(3293)),

	SARADOMIN_JADINKO(13163, new Item[] { new Item(19981, 1) }, 81, 600, BoxTrapType.MARASAMAW_PLANT, 56837, 56826, 56817, new Animation(3293), new Animation(3293)),

	ZAMORAK_JADINKO(13165, new Item[] { new Item(19983, 1) }, 81, 600, BoxTrapType.MARASAMAW_PLANT, 56839, 56828, 56818, new Animation(3293), new Animation(3293));
	
	private int npcId, baitId, level, objectCatch, objectSuccess, objectFail;
	private Item[] item;
	private double xp;
	private BoxTrapType hunter;
	private Animation animSuccess, animFail;

	public static final Map<Integer, BoxHunterType> ID_MAP = new HashMap<Integer, BoxHunterType>();
	public static final Map<Integer, BoxHunterType> OBJECTID_MAP = new HashMap<Integer, BoxHunterType>();

	public static BoxHunterType forId(int id) {
		return ID_MAP.get(id);
	}

	static {
		for (BoxHunterType type : BoxHunterType.values()) {
			ID_MAP.put(type.npcId, type);
			OBJECTID_MAP.put(type.objectSuccess, type);
		}
	}

	public static BoxHunterType forObjectId(int id) {
		return OBJECTID_MAP.get(id);
	}
	
	private BoxHunterType(int npcId, Item[] item, int level, double xp, BoxTrapType hunter, int objectCatch, int objectSuccess, int objectFail, Animation animSuccess, Animation animFail) {
		this(npcId, -1, item, level, xp, hunter, objectCatch, objectSuccess, objectFail, animSuccess, animFail);
	}

	private BoxHunterType(int npcId, int baitId, Item[] item, int level, double xp, BoxTrapType hunter, int objectCatch, int objectSuccess, int objectFail, Animation animSuccess, Animation animFail) {
		this.npcId = npcId;
		this.baitId = baitId;
		this.item = item;
		this.level = level;
		this.xp = xp;
		this.hunter = hunter;
		this.objectCatch = objectCatch;
		this.objectSuccess = objectSuccess;
		this.objectFail = objectFail;
		this.animSuccess = animSuccess;
		this.animFail = animFail;
	}
	
	public int getRate1() {
		return 40;
	}
	
	public int getRate99() {
		return 200;
	}

	public int getLevel() {
		return level;
	}

	public int getNpcId() {
		return npcId;
	}

	public double getXp() {
		return xp;
	}

	public Item[] getItems() {
		return item;
	}

	public BoxTrapType getTrap() {
		return hunter;
	}
	
	public int getObjectCatch() {
		return objectCatch;
	}
	
	public int getObjectSuccess() {
		return objectSuccess;
	}
	
	public int getObjectFail() {
		return objectFail;
	}
	
	public Animation getAnimSuccess() {
		return animSuccess;
	}
	
	public Animation getAnimFail() {
		return animFail;
	}

	public int getBaitId() {
		return baitId;
	}
}
