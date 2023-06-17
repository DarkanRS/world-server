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
package com.rs.game.content.skills.farming;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

import java.util.HashMap;
import java.util.Map;

public enum ProduceType {
	Potato(5318, 1, new Item(1942), 6, 8, 9, 4, PatchType.ALLOTMENT, 100, 192, new Item(6032, 2)),
	Onion(5319, 5, new Item(1957), 13, 9.5, 10.5, 4, PatchType.ALLOTMENT, 95, 192, new Item(5438, 1)),
	Cabbage(5324, 7, new Item(1965), 20, 10, 11.5, 4, PatchType.ALLOTMENT, 90, 192, new Item(5458, 1)),
	Tomato(5322, 12, new Item(1982), 27, 12.5, 14, 4, PatchType.ALLOTMENT, 85, 192, new Item(5478, 2)),
	Sweetcorn(5320, 20, new Item(5986), 34, 17, 19, 6, PatchType.ALLOTMENT, 80, 192, new Item(5931, 10)),
	Strawberry(5323, 31, new Item(5504), 43, 26, 29, 6, PatchType.ALLOTMENT, 75, 192, new Item(5386, 1)),
	Watermelon(5321, 47, new Item(5982), 52, 48.5, 54.5, 8, PatchType.ALLOTMENT, 70, 192, new Item(5970, 10)),

	Guam(5291, 9, new Item(199), 4, 11, 12.5, 4, PatchType.HERB, 25, 80),
	Marrentill(5292, 14, new Item(201), 4, 13.5, 15, 4, PatchType.HERB, 28, 80),
	Tarromin(5293, 19, new Item(203), 4, 16, 18, 4, PatchType.HERB, 31, 80),
	Harralander(5294, 26, new Item(205), 4, 21.5, 24, 4, PatchType.HERB, 36, 80),
	Rannar(5295, 32, new Item(207), 4, 27, 30.5, 4, PatchType.HERB, 39, 80),
	Spirit_weed(12176, 36, new Item(12174), 4, 32, 36, 4, PatchType.HERB, 40, 80),
	Toadflax(5296, 38, new Item(3049), 4, 34, 38.5, 4, PatchType.HERB, 43, 80),
	Irit(5297, 44, new Item(209), 4, 43, 48.5, 4, PatchType.HERB, 46, 80),
	Avantoe(5298, 50, new Item(211), 4, 54.4, 61.5, 4, PatchType.HERB, 50, 80),
	Kwuarm(5299, 56, new Item(213), 4, 69, 78, 4, PatchType.HERB, 54, 80),
	Snapdragon(5300, 62, new Item(3051), 4, 87.5, 98.5, 4, PatchType.HERB, 57, 80),
	Cadantine(5301, 67, new Item(215), 4, 106.5, 120, 4, PatchType.HERB, 60, 80),
	Lantadyme(5302, 73, new Item(2485), 4, 134.5, 151.5, 4, PatchType.HERB, 64, 80),
	Dwarf_weed(5303, 79, new Item(217), 4, 170.5, 192, 4, PatchType.HERB, 67, 80),
	Torstol(5304, 85, new Item(219), 4, 199.5, 224.5, 4, PatchType.HERB, 71, 80),
	Fellstalk(21621, 91, new Item(21626), 4, 225, 315.6, 4, PatchType.HERB, 15, 90),
	Wergali(14870, 46, new Item(14836), 4, 52.8, 52.8, 4, PatchType.HERB, 48, 80),
	Gout(6311, 65, new Item(3261), 110, 4, 45, 4, PatchType.HERB, 57, 80),

	Marigold(5096, 2, new Item(6010), 8, 8.5, 47, 4, PatchType.FLOWER),
	Rosemary(5097, 11, new Item(6014), 13, 12, 66.5, 4, PatchType.FLOWER),
	Nasturtium(5098, 24, new Item(6012), 18, 19.5, 111, 4, PatchType.FLOWER),
	Woad(5099, 25, new Item(1793), 23, 20.5, 115.5, 4, PatchType.FLOWER),
	Limpwurt(5100, 26, new Item(225), 28, 21.5, 120, 4, PatchType.FLOWER),
	Scarecrow(6059, 23, new Item(6059), 36, 0, 0, 0, PatchType.FLOWER),
	White_lily(14589, 52, new Item(14583), 37, 70, 250, 4, PatchType.FLOWER),

	Barley(5305, 3, new Item(6006), 49, 8.5, 9.5, 4, PatchType.HOP, 95, 185, new Item(6032, 3)),
	Hammerstone(5307, 4, new Item(5994), 4, 9, 10, 4, PatchType.HOP, 95, 185, new Item(6010, 1)),
	Asgarnian(5308, 8, new Item(5996), 11, 10.9, 12, 5, PatchType.HOP, 95, 185, new Item(5458, 1)),
	Jute(5306, 13, new Item(5931), 56, 13, 14.5, 5, PatchType.HOP, 95, 185, new Item(6008, 6)),
	Yanillian(5309, 16, new Item(5998), 19, 14.5, 16, 6, PatchType.HOP, 95, 185, new Item(5968, 1)),
	Krandorian(5310, 21, new Item(6000), 28, 17.5, 19.5, 7, PatchType.HOP, 95, 185, new Item(5478, 3)),
	Wildblood(5311, 28, new Item(6002), 38, 23, 26, 8, PatchType.HOP, 95, 185, new Item(6012, 1)),

	Oak(5370, 15, new Item(6043), 8, 14, 467.3, 4, PatchType.TREE, new Item(5968, 1)),
	Willow(5371, 30, new Item(6045), 15, 25, 1456.5, 6, PatchType.TREE, new Item(5386, 1)),
	Maple(5372, 45, new Item(6047), 24, 45, 3403.4, 8, PatchType.TREE, new Item(5396, 1)),
	Yew(5373, 60, new Item(6049), 35, 81, 7069.9, 10, PatchType.TREE, new Item(6016, 10)),
	Magic(5374, 75, new Item(6051), 48, 145.5, 13768.3, 12, PatchType.TREE, new Item(5974, 25)),

	Apple(5496, 27, new Item(1955), 8, 22, 1199.5, 6, PatchType.FRUIT_TREE, new Item(5986, 9)),
	Banana(5497, 33, new Item(1963), 35, 28, 1841.5, 6, PatchType.FRUIT_TREE, new Item(5386, 4)),
	Orange(5498, 39, new Item(2108), 72, 35.5, 2470.2, 6, PatchType.FRUIT_TREE, new Item(5406, 3)),
	Curry(5499, 42, new Item(5970), 99, 40, 2906.9, 6, PatchType.FRUIT_TREE, new Item(5416, 5)),
	Pineapple(5500, 51, new Item(2114), 136, 57, 4605.7, 6, PatchType.FRUIT_TREE, new Item(5982, 10)),
	Papaya(5501, 57, new Item(5972), 163, 72, 6146.4, 6, PatchType.FRUIT_TREE, new Item(2114, 10)),
	Palm(5502, 68, new Item(5974), 200, 110.5, 10150.1, 6, PatchType.FRUIT_TREE, new Item(5972, 15)),

	Redberry(5101, 10, new Item(1951), 5, 11.5, 64, 5, PatchType.BUSH, new Item(5478, 4)),
	Cadavaberry(5102, 22, new Item(753), 15, 18, 102.5, 6, PatchType.BUSH, new Item(5968, 3)),
	Dwellberry(5103, 36, new Item(2126), 26, 31.5, 177.5, 7, PatchType.BUSH, new Item(5406, 3)),
	Jangerberry(5104, 48, new Item(247), 38, 50.5, 284.5, 8, PatchType.BUSH, new Item(5982, 6)),
	Whiteberry(5105, 59, new Item(239), 51, 78, 437.5, 8, PatchType.BUSH, new Item(6004, 8)),
	Poison_ivy(5106, 70, new Item(6018), 197, 120, 675, 8, PatchType.BUSH),

	Bittercap(5282, 53, new Item(6004, 6), 4, 61.5, 57.7, 6, PatchType.MUSHROOM),
	Morchella(21620, 74, new Item(21622, 9), 26, 22, 77.7, 6, PatchType.MUSHROOM),
	
	Evil_turnip(12148, 42, new Item(12134, 1), 4, 41, 46, 1, PatchType.EVIL_TURNIP),

	Belladonna(5281, 63, new Item(2398), 4, 91, 512, 4, PatchType.BELLADONNA),

	Calquat(5503, 72, new Item(5980, 6), 4, 110.5, 12096, 8, PatchType.CALQUAT, new Item(6018, 8)),

	Cactus(5280, 55, new Item(6016), 8, 66.5, 374, 7, PatchType.CACTUS, new Item(753, 6)),

	Erzille(19897, 58, new Item(19984), 4, 87, 87, 4, PatchType.VINE_HERB, 67, 80),
	Argway(19907, 65, new Item(19985), 4, 110, 125, 4, PatchType.VINE_HERB, 67, 80),
	Ugune(19902, 70, new Item(19986), 4, 135, 152, 4, PatchType.VINE_HERB, 67, 80),
	Shengo(19912, 76, new Item(19987), 4, 140.5, 160, 4, PatchType.VINE_HERB, 67, 80),
	Samaden(19917, 80, new Item(19988), 4, 170, 190, 4, PatchType.VINE_HERB, 67, 80),

	Red_blossom(19922, 54, new Item(19962), 4, 52, 255, 4, PatchType.VINE_FLOWER),
	Blue_blossom(19927, 54, new Item(19963), 9, 52, 255, 4, PatchType.VINE_FLOWER),
	Green_blossom(19932, 54, new Item(19964), 14, 52, 255, 4, PatchType.VINE_FLOWER),

	Lergberry(19937, 61, new Item(19969, 4), 4, 145.5, 236.3, 6, PatchType.VINE_BUSH, new Item(19962, 3)),
	Kalferberry(19942, 77, new Item(19970, 4), 15, 220, 375, 6, PatchType.VINE_BUSH, new Item(19969, 3)),

	Compost(-1, 1, new Item(6032), 0, 0.0, 8.5, 1, PatchType.COMPOST),
	Supercompost(-2, 1, new Item(6034), 0, 0.0, 8.5, 2, PatchType.COMPOST),

	Spirit_tree(5375, 83, null, 8, 199.5, 19301.8, 12, PatchType.SPIRIT);


	private static Map<Integer, ProduceType> MAP = new HashMap<>();

	static {
		for (ProduceType product : ProduceType.values())
			MAP.put(product.seedId, product);
	}

	public static ProduceType forSeed(int itemId) {
		return MAP.get(itemId);
	}

	public static boolean isProduce(int itemId) {
		for (ProduceType prod : ProduceType.values()) {
			if (prod.productId == null)
				continue;
			if (prod.productId.getId() == itemId)
				return true;
		}
		return false;
	}

	public int seedId;
	public int level;
	public Item productId;
	public int varBitPlanted;
	public PatchType type;
	public int stages;
	public double experience, plantingExperience;
	public int rate1 = -1, rate99 = -1;
	public Item protection;

	private ProduceType(int seedId, int level, Item productId, int varBitPlanted, double plantingExperience, double experience, int stages, PatchType type, int rate1, int rate99, Item protection) {
		this.seedId = seedId;
		this.level = level;
		this.productId = productId;
		this.varBitPlanted = varBitPlanted;
		this.plantingExperience = plantingExperience;
		this.experience = experience;
		this.stages = stages;
		this.type = type;
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.protection = protection;
	}

	private ProduceType(int seedId, int level, Item productId, int varBitPlanted, double plantingExperience, double experience, int stages, PatchType type, int rate1, int rate99) {
		this(seedId, level, productId, varBitPlanted, plantingExperience, experience, stages, type, rate1, rate99, null);
	}

	private ProduceType(int seedId, int level, Item productId, int varBitPlanted, double plantingExperience, double experience, int stages, PatchType type, Item protection) {
		this(seedId, level, productId, varBitPlanted, plantingExperience, experience, stages, type, -1, -1, protection);
	}

	private ProduceType(int seedId, int level, Item productId, int varBitPlanted, double plantingExperience, double experience, int stages, PatchType type) {
		this(seedId, level, productId, varBitPlanted, plantingExperience, experience, stages, type, -1, -1, null);
	}

	public static boolean isProduct(Item item) {
		for (ProduceType info : ProduceType.values())
			if (info.productId.getId() == item.getId())
				return true;
		return false;
	}

	public boolean decLife(Player player) {
		if (rate1 == -1)
			return true;
		return !Utils.skillSuccess(player.getSkills().getLevel(Constants.FARMING), player.getInventory().containsItem(7409) ? 1.1 : 1.0, rate1, rate99);
	}

	public ProduceType getFlowerProtection() {
		switch(this) {
		case Potato:
		case Onion:
		case Tomato:
			return ProduceType.Marigold;
		case Cabbage:
			return ProduceType.Rosemary;
		case Sweetcorn:
			return ProduceType.Scarecrow;
		case Watermelon:
			return ProduceType.Nasturtium;
		default:
			return null;
		}
	}
}
