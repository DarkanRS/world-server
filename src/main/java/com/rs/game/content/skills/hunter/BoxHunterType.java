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

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BoxHunterType {

	CHINCHOMPA(5079, "hunter_chin", 53, 198.5, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(5184), new Animation(5185), 145, 268, null, null),

	CARNIVOROUS_CHINCHOMPA(5080, "hunter_red_chin", 63, 265, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(5184), new Animation(5185), 115, 228, null, null),

	GRENWALL(7010, 12535, "hunter_grenwall", 77, 1100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8602), new Animation(8602), 15, 130, null, null),

	PAWYA(7012, 5972, "hunter_pawya", 66, 400, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8615), new Animation(8611), 100, 215, null, null),

	WILD_KEBBIT(5089, "hunter_kebbitwild", 23, 128, BoxTrapType.DEAD_FALL, 19207, 19215, 19206, new Animation(5275), new Animation(5277), 105, 354, null, null),

	BARB_TAILED_KEBBIT(5088, "hunter_kebbitbarb", 33, 168, BoxTrapType.DEAD_FALL, 19218, 19217, 19206, new Animation(5275), new Animation(5277), 110, 326, null, null),

	PRICKLY_KEBBIT(5086, "hunter_kebbitprick", 37, 204, BoxTrapType.DEAD_FALL, 19208, 19218, 19206, new Animation(5275), new Animation(5277), 115, 298, null, null),

	SABRE_TOOTHED_KEBBIT(5087, "hunter_kebbitsabre", 51, 200, BoxTrapType.DEAD_FALL, 19209, 19216, 19206, new Animation(5275), new Animation(5277), 120, 268, null, null),

	FERRET(5081, "hunter_ferret", 27, 115.2, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(5191), new Animation(5192), 41, 255, List.of(Quest.EAGLES_PEAK), null),

//	GECKO(6916, new Item[] { new Item(12184) }, 27, 100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8362), new Animation(8361), 1, 255, null, Map.of(Skills.SUMMONING, 10)),
//
//	RACCOON(7272, new Item[] { new Item(12487) }, 27, 100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(7726), new Animation(7727), 1, 255, null, Map.of(Skills.SUMMONING, 80)),
//
//	BEIGE_MONKEY(7235, 1963, "hunter_monkey", 27, 100, BoxTrapType.BOX, 19188, 19189, 19192, new Animation(8349), new Animation(8350), 1, 255, null, Map.of(Skills.SUMMONING, 95)),

	SWAMP_LIZARD(5117, "hunter_salam_swamp", 29, 152, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1), -96, 433, null, null),

	ORANGE_SALAMANDER(5114, "hunter_salam_orange", 47, 224, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1), 17, 285, null, null),

	RED_SALAMANDER(5115, "hunter_salam_red", 59, 272, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1), 0, 240, null, null),

	BLACK_SALAMANDER(5116, "hunter_salam_black", 67, 319.2, BoxTrapType.TREE_NET, -1, -1, -1, new Animation(-1), new Animation(-1), -2, 212, null, null),

	CRIMSON_SWIFT(5073, "hunter_crimswift", 1, 34, BoxTrapType.BIRD_SNARE, 19179, 19180, 19174, new Animation(5171), new Animation(5172), 100, 420, null, null),

	GOLDEN_WARBLER(5075, "hunter_goldwarb", 5, 48, BoxTrapType.BIRD_SNARE, 19183, 19184, 19174, new Animation(5171), new Animation(5172), 92, 402, null, null),

	COPPER_LONGTAIL(5076, "hunter_copperlong", 9, 61, BoxTrapType.BIRD_SNARE, 19185, 19186, 19174, new Animation(5171), new Animation(5172), 84, 389, null, null),

	CERULEAN_TWITCH(5074, "hunter_cerutwitch", 11, 64.67, BoxTrapType.BIRD_SNARE, 19181, 19182, 19174, new Animation(5171), new Animation(5172), 82, 381, null, null),

	TROPICAL_WAGTAIL(5072, "hunter_tropwag", 19, 95.2, BoxTrapType.BIRD_SNARE, 19177, 19178, 19174, new Animation(5171), new Animation(5172), 74, 368, null, null),

	WIMPY_BIRD(7031, "hunter_wimpybird", 39, 167, BoxTrapType.BIRD_SNARE, 29164, 29165, 19174, new Animation(5171), new Animation(5172), 60, 321, null, null),

	IMP(708, "hunter_imp", 71, 450, BoxTrapType.MAGIC_BOX, 19225, 19226, 19224, new Animation(5218), new Animation(5285), 15, 130, null, null),

	COMMON_JADINKO(13119, "hunter_jadinko_common", 70, 350, BoxTrapType.MARASAMAW_PLANT, 56830, 56819, 56813, new Animation(3293), new Animation(3293), 30, 150, null, null),

	AMPHIBIOUS_JADINKO(13130, "hunter_jadinko_amphibious", 77, 485, BoxTrapType.MARASAMAW_PLANT, 56831, 56820, 56807, new Animation(3293), new Animation(3293), 15, 130, null, null),

	DRACONIC_JADINKO(13131, "hunter_jadinko_draconic", 80, 525, BoxTrapType.MARASAMAW_PLANT, 56832, 56821, 56812, new Animation(3293), new Animation(3293), 15, 130, null, null),

	AQUATIC_JADINKO(13142, "hunter_jadinko_aquatic", 76, 475, BoxTrapType.MARASAMAW_PLANT, 56833, 56822, 56808, new Animation(3293), new Animation(3293), 15, 135, null, null),

	IGNEOUS_JADINKO(13143, "hunter_jadinko_igneous", 74, 465, BoxTrapType.MARASAMAW_PLANT, 56834, 56823, 56815, new Animation(3293), new Animation(3293), 25, 145, null, null),

	CARRION_JADINKO(13154, "hunter_jadinko_carrion", 78, 505, BoxTrapType.MARASAMAW_PLANT, 56835, 56824, 56810, new Animation(3293), new Animation(3293), 15, 130, null, null),

	CANNIBAL_JADINKO(13155, "hunter_jadinko_cannibal", 75, 475, BoxTrapType.MARASAMAW_PLANT, 56836, 56825, 56809, new Animation(3293), new Animation(3293), 20, 140, null, null),

	GUTHIX_JADINKO(13164, "hunter_jadinko_guthix", 81, 600, BoxTrapType.MARASAMAW_PLANT, 56838, 56827, 56814, new Animation(3293), new Animation(3293), 15, 130, null, null),

	SARADOMIN_JADINKO(13163, "hunter_jadinko_saradomin", 81, 600, BoxTrapType.MARASAMAW_PLANT, 56837, 56826, 56817, new Animation(3293), new Animation(3293), 15, 130, null, null),

	ZAMORAK_JADINKO(13165, "hunter_jadinko_zamorak", 81, 600, BoxTrapType.MARASAMAW_PLANT, 56839, 56828, 56818, new Animation(3293), new Animation(3293), 15, 130, null, null);

	private final int npcId;
    private final int baitId;
    private final int level;
    private final int objectCatch;
    private final int objectSuccess;
    private final int objectFail;
    private final int rate1;
    private final int rate99;
	private final double xp;
	private final BoxTrapType hunter;
	private final Animation animSuccess;
    private final Animation animFail;
	private final String dropTable;
	private final List<Quest> questReqs;
	private final Map<Integer, Integer> skillReqs;

	public static final Map<Integer, BoxHunterType> ID_MAP = new HashMap<>();
	public static final Map<Integer, BoxHunterType> OBJECTID_MAP = new HashMap<>();

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

	BoxHunterType(int npcId, String dropTable, int level, double xp, BoxTrapType hunter, int objectCatch, int objectSuccess, int objectFail, Animation animSuccess, Animation animFail, int rate1, int rate99, List<Quest> questReqs, Map<Integer, Integer> skillReqs) {
		this(npcId, -1, dropTable, level, xp, hunter, objectCatch, objectSuccess, objectFail, animSuccess, animFail, rate1, rate99, questReqs, skillReqs);
	}

	BoxHunterType(int npcId, int baitId, String dropTable, int level, double xp, BoxTrapType hunter, int objectCatch, int objectSuccess, int objectFail, Animation animSuccess, Animation animFail, int rate1, int rate99, List<Quest> questReqs, Map<Integer, Integer> skillReqs) {
		this.npcId = npcId;
		this.baitId = baitId;
		this.dropTable = dropTable;
		this.level = level;
		this.xp = xp;
		this.hunter = hunter;
		this.objectCatch = objectCatch;
		this.objectSuccess = objectSuccess;
		this.objectFail = objectFail;
		this.animSuccess = animSuccess;
		this.animFail = animFail;
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.questReqs = questReqs;
		this.skillReqs = skillReqs;
	}

	public int getRate1() {
		return rate1;
	}

	public int getRate99() {
		return rate99;
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

	public Item[] getItems(Player player) {
		return DropTable.calculateDrops(player, DropSets.getDropSet(dropTable));
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

	public List<Quest> getQuestReqs() {
		return questReqs;
	}

	public Map<Integer, Integer> getSkillReqs() {
		return skillReqs;
	}
}
