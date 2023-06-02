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
package com.rs.game.content.skills.herblore;

import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class WeaponPoison {

	public static enum PoisonableItem {
		BRONZE_DART(806, 812, 5628, 5635),
		IRON_DART(807, 813, 5629, 5636),
		STEEL_DART(808, 814, 5630, 5637),
		MITHRIL_DART(809, 815, 5632, 5639),
		ADAMANT_DART(810, 816, 5633, 5640),
		RUNE_DART(811, 817, 5634, 5641),
		IRON_JAVELIN(826, 832, 5643, 5649),
		STEEL_JAVELIN(827, 833, 5644, 5650),
		MITHRIL_JAVELIN(828, 834, 5645, 5651),
		ADAMANT_JAVELIN(829, 835, 5646, 5652),
		RUNE_JAVELIN(830, 836, 5647, 5653),
		IRON_KNIFE(863, 871, 5655, 5662),
		BRONZE_KNIFE(864, 870, 5654, 5661),
		STEEL_KNIFE(865, 872, 5656, 5663),
		MITHRIL_KNIFE(866, 873, 5657, 5664),
		ADAMANT_KNIFE(867, 875, 5659, 5666),
		RUNE_KNIFE(868, 876, 5660, 5667),
		BLACK_KNIFE(869, 874, 5658, 5665),
		BRONZE_BOLTS(877, 878, 6061, 6062),
		BRONZE_ARROW(882, 883, 5616, 5622),
		IRON_ARROW(884, 885, 5617, 5623),
		STEEL_ARROW(886, 887, 5618, 5624),
		MITHRIL_ARROW(888, 889, 5619, 5625),
		ADAMANT_ARROW(890, 891, 5620, 5626),
		RUNE_ARROW(892, 893, 5621, 5627),
		IRON_DAGGER(1203, 1219, 5668, 5686),
		BRONZE_DAGGER(1205, 1221, 5670, 5688),
		STEEL_DAGGER(1207, 1223, 5672, 5690),
		MITHRIL_DAGGER(1209, 1225, 5674, 5692),
		ADAMANT_DAGGER(1211, 1227, 5676, 5694),
		RUNE_DAGGER(1213, 1229, 5678, 5696),
		DRAGON_DAGGER(1215, 1231, 5680, 5698),
		BLACK_DAGGER(1217, 1233, 5682, 5700),
		BRONZE_SPEAR(1237, 1251, 5704, 5718, 3170),
		IRON_SPEAR(1239, 1253, 5706, 5720, 3171),
		STEEL_SPEAR(1241, 1255, 5708, 5722, 3172),
		MITHRIL_SPEAR(1243, 1257, 5710, 5724, 3173),
		ADAMANT_SPEAR(1245, 1259, 5712, 5726, 3174),
		RUNE_SPEAR(1247, 1261, 5714, 5728, 3175),
		DRAGON_SPEAR(1249, 1263, 5716, 5730, 3176),
		BLACK_DART(3093, 3094, 5631, 5638),
		BLACK_SPEAR(4580, 4582, 5734, 5736, 4584),
		WHITE_DAGGER(6591, 6593, 6595, 6597),
		BONE_DAGGER(8872, 8874, 8876, 8878),
		BLURITE_BOLTS(9139, 9286, 9293, 9300),
		IRON_BOLTS(9140, 9287, 9294, 9301),
		STEEL_BOLTS(9141, 9288, 9295, 9302),
		MITHRIL_BOLTS(9142, 9289, 9296, 9303),
		ADAMANT_BOLTS(9143, 9290, 9297, 9304),
		RUNITE_BOLTS(9144, 9291, 9298, 9305),
		SILVER_BOLTS(9145, 9292, 9299, 9306),
		KERIS(10581, 10582, 10583, 10584),
		DRAGON_ARROW(11212, 11227, 11228, 11229),
		DRAGON_DART(11230, 11231, 11233, 11234),
		BRONZE_HASTA(11367, 11379, 11382, 11384, 11381),
		IRON_HASTA(11369, 11386, 11389, 11391, 11388),
		STEEL_HASTA(11371, 11393, 11396, 11398, 11395),
		MITHRIL_HASTA(11373, 11400, 11403, 11405, 11402),
		ADAMANT_HASTA(11375, 11407, 11410, 11412, 11409),
		RUNE_HASTA(11377, 11414, 11417, 11419, 11416),
		BLACK_BOLTS(13083, 13084, 13085, 13086),
		MORRIGANS_JAVELIN(13879, 13880, 13881, 13882),
		NOVITE_ARROWS(16427, -1, -1, -1, -1, 16482, 16537, 16592),
		BATHUS_ARROWS(16432, -1, -1, -1, -1, 16487, 16542, 16597),
		MARMAROS_ARROWS(16437, -1, -1, -1, -1, 16492, 16547, 16602),
		KRATONITE_ARROWS(16442, -1, -1, -1, -1, 16497, 16552, 16607),
		FRACTITE_ARROWS(16447, -1, -1, -1, -1, 16502, 16557, 16612),
		ZEPHYRIUM_ARROWS(16452, -1, -1, -1, -1, 16507, 16562, 16617),
		ARGONITE_ARROWS(16457, -1, -1, -1, -1, 16512, 16567, 16622),
		KATAGON_ARROWS(16462, -1, -1, -1, -1, 16517, 16572, 16627),
		GORGONITE_ARROWS(16467, -1, -1, -1, -1, 16522, 16577, 16632),
		PROMETHIUM_ARROWS(16472, -1, -1, -1, -1, 16527, 16582, 16637),
		SAGITTARIAN_ARROWS(16477, -1, -1, -1, -1, 16532, 16587, 16642),
		NOVITE_DAGGER(16757, -1, -1, -1, -1, 16759, 16761, 16763),
		BATHUS_DAGGER(16765, -1, -1, -1, -1, 16767, 16769, 16771),
		MARMAROS_DAGGER(16773, -1, -1, -1, -1, 16775, 16777, 16779),
		KRATONITE_DAGGER(16781, -1, -1, -1, -1, 16783, 16785, 16787),
		FRACTITE_DAGGER(16789, -1, -1, -1, -1, 16791, 16793, 16795),
		ZEPHYRIUM_DAGGER(16797, -1, -1, -1, -1, 16799, 16801, 16803),
		ARGONITE_DAGGER(16805, -1, -1, -1, -1, 16807, 16809, 16811),
		KATAGON_DAGGER(16813, -1, -1, -1, -1, 16815, 16817, 16819),
		GORGONITE_DAGGER(16821, -1, -1, -1, -1, 16823, 16825, 16827),
		PROMETHIUM_DAGGER(16829, -1, -1, -1, -1, 16831, 16833, 16835),
		PRIMAL_DAGGER(16837, -1, -1, -1, -1, 16839, 16841, 16843),
		NOVITE_SPEAR(17063, -1, -1, -1, -1, 17065, 17067, 17069),
		BATHUS_SPEAR(17071, -1, -1, -1, -1, 17073, 17075, 17077),
		MARMAROS_SPEAR(17079, -1, -1, -1, -1, 17081, 17083, 17085),
		KRATONITE_SPEAR(17087, -1, -1, -1, -1, 17089, 17091, 17093),
		FRACTITE_SPEAR(17095, -1, -1, -1, -1, 17097, 17099, 17101),
		ZEPHYRIUM_SPEAR(17103, -1, -1, -1, -1, 17105, 17107, 17109),
		ARGONITE_SPEAR(17111, -1, -1, -1, -1, 17113, 17115, 17117),
		KATAGON_SPEAR(17119, -1, -1, -1, -1, 17121, 17123, 17125),
		GORGONITE_SPEAR(17127, -1, -1, -1, -1, 17129, 17131, 17133),
		PROMETHIUM_SPEAR(17135, -1, -1, -1, -1, 17137, 17139, 17141),
		PRIMAL_SPEAR(17143, -1, -1, -1, -1, 17145, 17147, 17149);

		private static Map<Integer, Integer> POISON_TO_BASE_IDS = new HashMap<>();
		private static Map<Integer, PoisonableItem> BASE_IDS_TO_WEAPON = new HashMap<>();
		static {
			for (PoisonableItem wep : PoisonableItem.values()) {
				BASE_IDS_TO_WEAPON.put(wep.id, wep);
				for (int poisoned : wep.poisonIds) {
					if (poisoned != -1)
						POISON_TO_BASE_IDS.put(poisoned, wep.id);
				}
			}
		}

		public static int getBaseFromPoisoned(int poisoned) {
			return POISON_TO_BASE_IDS.get(poisoned);
		}

		public static PoisonableItem forId(int baseId) {
			return BASE_IDS_TO_WEAPON.get(baseId);
		}

		private int id;
		private int[] poisonIds;

		private PoisonableItem(int id, int... poisonIds) {
			this.id = id;
			this.poisonIds = poisonIds;
		}
	}

	public static int[] POISONS = { 187, 5937, 5940, 3153, 17572, 17596, 17620 };

	public static ItemOnItemHandler handlePoison = new ItemOnItemHandler(Arrays.stream(PoisonableItem.values()).mapToInt(wep -> wep.id).toArray(), POISONS, e -> {
		Item targetItem = e.getUsedWith(POISONS);
		Item poison = e.getUsedWith(targetItem.getId());
		if (targetItem == null || poison == null)
			return;
		PoisonableItem def = PoisonableItem.forId(targetItem.getId());
		if (def == null)
			return;
		int poisonType = -1;
		for (int i = 0;i < POISONS.length;i++) {
			if (poison.getId() == POISONS[i])
				poisonType = i;
		}
		if (poisonType < 0 || poisonType >= def.poisonIds.length || def.poisonIds[poisonType] == -1)
			return;
		int amt = targetItem.getAmount() > 15 ? 15 : targetItem.getAmount();
		if (amt > 1 && !e.getPlayer().getInventory().hasFreeSlots()) {
			e.getPlayer().sendMessage("Not enough space in your inventory.");
			return;
		}
		if (poisonType == 3)
			e.getPlayer().getInventory().deleteItem(poison);
		else {
			poison.setId(poisonType > 4 ? 17490 : 229);
			e.getPlayer().getInventory().refresh(poison.getSlot());
		}
		if (amt < targetItem.getAmount()) {
			e.getPlayer().getInventory().deleteItem(targetItem.getId(), amt);
			e.getPlayer().getInventory().addItem(def.poisonIds[poisonType], amt);
		} else {
			targetItem.setId(def.poisonIds[poisonType]);
			e.getPlayer().getInventory().refresh(targetItem.getSlot());
		}
		e.getPlayer().sendMessage("You dip the tip of the " + targetItem.getName().toLowerCase() + " in the poison.");
	});

	public static ItemOnItemHandler handleClean = new ItemOnItemHandler(PoisonableItem.POISON_TO_BASE_IDS.keySet().stream().mapToInt(key -> key.intValue()).toArray(), 3188, e -> {
		Item targetItem = e.getUsedWith(3188);
		if (targetItem == null)
			return;
		int base = PoisonableItem.getBaseFromPoisoned(targetItem.getId());
		if (base <= 0)
			return;
		targetItem.setId(base);
		e.getPlayer().getInventory().refresh(targetItem.getSlot());
		e.getPlayer().sendMessage("You clean the poison off the " + targetItem.getName().toLowerCase() + ".");
	});

}
