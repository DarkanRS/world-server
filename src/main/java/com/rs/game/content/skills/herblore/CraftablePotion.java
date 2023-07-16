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

import java.util.HashMap;
import java.util.Map;

public enum CraftablePotion {

	GUAM_POTION_UNF(new Item(91, 1), 1, 1.0, new Item[] { new Item(227, 1), new Item(249, 1) }),
	MARRENTILL_POTION_UNF(new Item(93, 1), 5, 1.0, new Item[] { new Item(227, 1), new Item(251, 1) }),
	TARROMIN_POTION_UNF(new Item(95, 1), 12, 1.0, new Item[] { new Item(227, 1), new Item(253, 1) }),
	HARRALANDER_POTION_UNF(new Item(97, 1), 22, 1.0, new Item[] { new Item(227, 1), new Item(255, 1) }),
	RANARR_POTION_UNF(new Item(99, 1), 30, 1.0, new Item[] { new Item(227, 1), new Item(257, 1) }),
	IRIT_POTION_UNF(new Item(101, 1), 45, 1.0, new Item[] { new Item(227, 1), new Item(259, 1) }),
	AVANTOE_POTION_UNF(new Item(103, 1), 50, 1.0, new Item[] { new Item(227, 1), new Item(261, 1) }),
	KWUARM_POTION_UNF(new Item(105, 1), 55, 1.0, new Item[] { new Item(227, 1), new Item(263, 1) }),
	CADANTINE_POTION_UNF(new Item(107, 1), 66, 1.0, new Item[] { new Item(227, 1), new Item(265, 1) }),
	DWARF_WEED_POTION_UNF(new Item(109, 1), 72, 1.0, new Item[] { new Item(227, 1), new Item(267, 1) }),
	TORSTOL_POTION_UNF(new Item(111, 1), 78, 1.0, new Item[] { new Item(227, 1), new Item(269, 1) }),
	LANTADYME_POTION_UNF(new Item(2483, 1), 69, 1.0, new Item[] { new Item(227, 1), new Item(2481, 1) }),
	TOADFLAX_POTION_UNF(new Item(3002, 1), 34, 1.0, new Item[] { new Item(227, 1), new Item(2998, 1) }),
	SNAPDRAGON_POTION_UNF(new Item(3004, 1), 63, 1.0, new Item[] { new Item(227, 1), new Item(3000, 1) }),
	ROGUES_PURSE_POTION_UNF(new Item(4840, 1), 8, 1.0, new Item[] { new Item(227, 1), new Item(1534, 1) }),
	SPIRIT_WEED_POTION_UNF(new Item(12181, 1), 40, 1.0, new Item[] { new Item(227, 1), new Item(12172, 1) }),
	WERGALI_POTION_UNF(new Item(14856, 1), 42, 1.0, new Item[] { new Item(227, 1), new Item(14854, 1) }),
	FELLSTALK_POTION_UNF(new Item(21628, 1), 94, 1.0, new Item[] { new Item(227, 1), new Item(21624, 1) }),

	SAGEWORT_POTION_UNF(new Item(17538, 1), 3, 0.0, new Item[] { new Item(17492, 1), new Item(17512, 1) }),
	VALERIAN_POTION_UNF(new Item(17540, 1), 4, 0.0, new Item[] { new Item(17492, 1), new Item(17514, 1) }),
	ALOE_POTION_UNF(new Item(17542, 1), 8, 0.0, new Item[] { new Item(17492, 1), new Item(17516, 1) }),
	WORMWOOD_POTION_UNF(new Item(17544, 1), 34, 0.0, new Item[] { new Item(17492, 1), new Item(17518, 1) }),
	MAGEBANE_POTION_UNF(new Item(17546, 1), 37, 0.0, new Item[] { new Item(17492, 1), new Item(17520, 1) }),
	FEATHERFOIL_POTION_UNF(new Item(17548, 1), 41, 0.0, new Item[] { new Item(17492, 1), new Item(17522, 1) }),
	WINTERS_GRIP_POTION_UNF(new Item(17550, 1), 67, 0.0, new Item[] { new Item(17492, 1), new Item(17524, 1) }),
	LYCOPUS_POTION_UNF(new Item(17552, 1), 70, 0.0, new Item[] { new Item(17492, 1), new Item(17526, 1) }),
	BUCKTHORN_POTION_UNF(new Item(17554, 1), 74, 0.0, new Item[] { new Item(17492, 1), new Item(17528, 1) }),

	ERZILLE_POTION_UNF(new Item(19998, 1), 54, 1.0, new Item[] { new Item(19994, 1), new Item(19989, 1) }),
	UGUNE_POTION_UNF(new Item(19999, 1), 56, 1.0, new Item[] { new Item(19994, 1), new Item(19991, 1) }),
	ARGWAY_POTION_UNF(new Item(20000, 1), 57, 1.0, new Item[] { new Item(19994, 1), new Item(19990, 1) }),
	SHENGO_POTION_UNF(new Item(20001, 1), 58, 1.0, new Item[] { new Item(19994, 1), new Item(19992, 1) }),
	SAMADEN_POTION_UNF(new Item(20002, 1), 59, 1.0, new Item[] { new Item(19994, 1), new Item(19993, 1) }),

	ATTACK_POTION(new Item(121, 1), 1, 25.0, new Item[] { new Item(91, 1), new Item(221, 1) }),
	ANTIPOISON(new Item(175, 1), 5, 37.5, new Item[] { new Item(93, 1), new Item(235, 1) }),
	RELICYMS_BALM(new Item(4844, 1), 8, 40.0, new Item[] { new Item(4840, 1), new Item(1526, 1) }),
	STRENGTH_POTION(new Item(115, 1), 12, 50.0, new Item[] { new Item(95, 1), new Item(225, 1) }),
	SERUM_207(new Item(3410, 1), 15, 50.0, new Item[] { new Item(95, 1), new Item(592, 1) }),
	GUTHIX_REST(new Item(4419, 1), 18, 59.5, new Item[] { new Item(97, 1), new Item(251, 1) }),
	RESTORE_POTION(new Item(127, 1), 22, 62.0, new Item[] { new Item(97, 1), new Item(223, 1) }),
	ENERGY_POTION(new Item(3010, 1), 26, 67.0, new Item[] { new Item(97, 1), new Item(1975, 1) }),
	DEFENCE_POTION(new Item(133, 1), 30, 75.0, new Item[] { new Item(93, 1), new Item(948, 1) }),
	PRAYER_POTION(new Item(139, 1), 38, 87.0, new Item[] { new Item(99, 1), new Item(231, 1) }),
	SUPER_ATTACK(new Item(145, 1), 45, 100.0, new Item[] { new Item(101, 1), new Item(221, 1) }),
	FISHING_POTION(new Item(151, 1), 50, 112.0, new Item[] { new Item(103, 1), new Item(231, 1) }),
	SUPER_STRENGTH(new Item(157, 1), 55, 125.0, new Item[] { new Item(105, 1), new Item(225, 1) }),
	SUPER_DEFENCE(new Item(163, 1), 66, 150.0, new Item[] { new Item(107, 1), new Item(239, 1) }),
	SUPER_RANGING_POTION(new Item(169, 1), 72, 162.0, new Item[] { new Item(109, 1), new Item(245, 1) }),
	SUPER_ANTIPOISON(new Item(181, 1), 48, 106.0, new Item[] { new Item(101, 1), new Item(235, 1) }),
	ZAMORAK_BREW(new Item(189, 1), 78, 175.0, new Item[] { new Item(111, 1), new Item(247, 1) }),
	ANTIFIRE(new Item(2454, 1), 69, 157.0, new Item[] { new Item(2483, 1), new Item(241, 1) }),
	SUPER_ENERGY(new Item(3018, 1), 52, 117.0, new Item[] { new Item(103, 1), new Item(2970, 1) }),
	SUPER_RESTORE(new Item(3026, 1), 63, 142.0, new Item[] { new Item(3004, 1), new Item(223, 1) }),
	AGILITY_POTION(new Item(3034, 1), 34, 80.0, new Item[] { new Item(3002, 1), new Item(2152, 1) }),
	SUPER_MAGIC_POTION(new Item(3042, 1), 76, 172.0, new Item[] { new Item(2483, 1), new Item(3138, 1) }),
	SARADOMIN_BREW(new Item(6687, 1), 81, 180.0, new Item[] { new Item(3002, 1), new Item(6693, 1) }),
	MAGIC_ESSENCE_UNF(new Item(9019, 1), 57, 1.0, new Item[] { new Item(227, 1), new Item(9017, 1) }),
	MAGIC_ESSENCE(new Item(9022, 1), 57, 130.0, new Item[] { new Item(9019, 1), new Item(9018, 1) }),
	COMBAT_POTION(new Item(9741, 1), 36, 84.0, new Item[] { new Item(97, 1), new Item(9736, 1) }),
	HUNTER_POTION(new Item(10000, 1), 53, 120.0, new Item[] { new Item(103, 1), new Item(10111, 1) }),
	MIXTURE__STEP_1(new Item(10911, 1), 65, 47.0, new Item[] { new Item(3026, 1), new Item(235, 1) }),
	MIXTURE__STEP_2(new Item(10919, 1), 65, 52.0, new Item[] { new Item(1526, 1), new Item(10911, 1) }),
	SANFEW_SERUM(new Item(10927, 1), 65, 60.0, new Item[] { new Item(10937, 1), new Item(10919, 1) }),
	SUMMONING_POTION(new Item(12142, 1), 40, 92.0, new Item[] { new Item(12181, 1), new Item(12109, 1) }),
	SUPER_FISHING_EXPLOSIVE(new Item(12633, 1), 31, 55.0, new Item[] { new Item(91, 1), new Item(12630, 1) }),
	CRAFTING_POTION(new Item(14840, 1), 42, 95.0, new Item[] { new Item(14856, 1), new Item(5004, 1) }),
	FLETCHING_POTION(new Item(14848, 1), 58, 132.0, new Item[] { new Item(14856, 1), new Item(11525, 1) }),
	RESTORE_SPECIAL(new Item(15301, 1), 84, 200.0, new Item[] { new Item(3018, 1), new Item(5972, 1) }),
	SUPER_ANTIFIRE(new Item(15305, 1), 85, 210.0, new Item[] { new Item(2454, 1), new Item(4621, 1) }),
	EXTREME_ATTACK(new Item(15309, 1), 88, 220.0, new Item[] { new Item(145, 1), new Item(261, 1) }),
	EXTREME_STRENGTH(new Item(15313, 1), 89, 230.0, new Item[] { new Item(157, 1), new Item(267, 1) }),
	EXTREME_DEFENCE(new Item(15317, 1), 90, 240.0, new Item[] { new Item(163, 1), new Item(2481, 1) }),
	EXTREME_MAGIC(new Item(15321, 1), 91, 250.0, new Item[] { new Item(3042, 1), new Item(9594, 1) }),
	EXTREME_RANGING(new Item(15325, 1), 92, 260.0, new Item[] { new Item(169, 1), new Item(12539, 5) }),
	SUPER_PRAYER(new Item(15329, 1), 94, 270.0, new Item[] { new Item(139, 1), new Item(6810, 1) }),
	OVERLOAD(new Item(15333, 1), 96, 1000.0, new Item[] { new Item(269, 1), new Item(15309, 1), new Item(15313, 1), new Item(15317, 1), new Item(15321, 1), new Item(15325, 1)}),
	PRAYER_RENEWAL(new Item(21632, 1), 94, 190.0, new Item[] { new Item(21628, 1), new Item(21622, 1) }),

	COCONUT_MILK(new Item(5935, 1), 1, 1.0, new Item[] { new Item(229, 1), new Item(5976, 1) }),
	WEAPON_POISON_P_UNF(new Item(5936, 1), 73, 1.0, new Item[] { new Item(5935, 1), new Item(6016, 1) }),
	WEAPON_POISON_P(new Item(5937, 1), 73, 165.0, new Item[] { new Item(5936, 1), new Item(223, 1) }),
	WEAPON_POISON_PP_UNF(new Item(5939, 1), 82, 1.0, new Item[] { new Item(5935, 1), new Item(2398, 1) }),
	WEAPON_POISON_PP(new Item(5940, 1), 82, 190.0, new Item[] { new Item(5939, 1), new Item(6018, 1) }),
	ANTIPOISON_P_UNF(new Item(5942, 1), 68, 1.0, new Item[] { new Item(5935, 1), new Item(2998, 1) }),
	ANTIPOISON_P(new Item(5943, 1), 68, 155.0, new Item[] { new Item(5942, 1), new Item(6049, 1) }),
	ANTIPOISON_PP_UNF(new Item(5951, 1), 79, 1.0, new Item[] { new Item(5935, 1), new Item(259, 1) }),
	ANTIPOISON_PP(new Item(5952, 1), 79, 177.0, new Item[] { new Item(5951, 1), new Item(6051, 1) }),

	GUTHIX_BALANCE_UNF(new Item(7652, 1), 22, 25.0, new Item[] { new Item(2430, 1), new Item(1550, 1) }),
	GUTHIX_BALANCE_UNF2(new Item(7654, 1), 22, 25.0, new Item[] { new Item(127, 1), new Item(1550, 1) }),
	GUTHIX_BALANCE_UNF3(new Item(7656, 1), 22, 25.0, new Item[] { new Item(129, 1), new Item(1550, 1) }),
	GUTHIX_BALANCE_UNF4(new Item(7658, 1), 22, 25.0, new Item[] { new Item(131, 1), new Item(1550, 1) }),
	GUTHIX_BALANCE_4(new Item(7660, 1), 22, 25.0, new Item[] { new Item(7652, 1), new Item(7650, 1) }),
	GUTHIX_BALANCE_3(new Item(7662, 1), 22, 25.0, new Item[] { new Item(7654, 1), new Item(7650, 1) }),
	GUTHIX_BALANCE_2(new Item(7664, 1), 22, 25.0, new Item[] { new Item(7656, 1), new Item(7650, 1) }),
	GUTHIX_BALANCE_1(new Item(7666, 1), 22, 25.0, new Item[] { new Item(7658, 1), new Item(7650, 1) }),

	GUAM_TAR(new Item(10142, 15), 19, 30.0, new Item[] { new Item(1939, 15), new Item(249, 1) }),
	MARRENTILL_TAR(new Item(10143, 15), 31, 42.0, new Item[] { new Item(1939, 15), new Item(251, 1) }),
	TARROMIN_TAR(new Item(10144, 15), 39, 55.0, new Item[] { new Item(1939, 15), new Item(253, 1) }),
	HARRALANDER_TAR(new Item(10145, 15), 44, 72.0, new Item[] { new Item(1939, 15), new Item(255, 1) }),

	SNAKEWEED_MIXTURE(new Item(737, 1), 3, 1.0, new Item[] { new Item(227, 1), new Item(1526, 1) }),
	ARDRIGAL_MIXTURE(new Item(738, 1), 3, 1.0, new Item[] { new Item(227, 1), new Item(1528, 1) }),
	BRAVERY_POTION(new Item(739, 1), 1, 0.0, new Item[] { new Item(738, 1), new Item(1526, 1) }),
	BLAMISH_OIL(new Item(1582, 1), 25, 80.0, new Item[] { new Item(97, 1), new Item(1581, 1) }),

	ANCHOVY_OIL(new Item(11264, 1), 3, 0.0, new Item[] { new Item(229, 1), new Item(11266, 8) }),
	IMP_REPLLENT_MARIGOLDS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(6010, 1) }),
	IMP_REPLLENT_ROSEMARY(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(6014, 1) }),
	IMP_REPLLENT_NASTURTIUMS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(6012, 1) }),
	IMP_REPLLENT_PASTEL_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2460, 1) }),
	IMP_REPLLENT_RED_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2462, 1) }),
	IMP_REPLLENT_BLUE_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2464, 1) }),
	IMP_REPLLENT_YELLOW_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2466, 1) }),
	IMP_REPLLENT_PURPLE_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2468, 1) }),
	IMP_REPLLENT_ORANGE_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2470, 1) }),
	IMP_REPLLENT_RGB_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2472, 1) }),
	IMP_REPLLENT_WHITE_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2474, 1) }),
	IMP_REPLLENT_BLACK_FLOWERS(new Item(11262, 1), 3, 5.0, new Item[] { new Item(11264, 1), new Item(2476, 1) }),

	JUJU_MINING_POTION(new Item(20004, 1), 74, 168.0, new Item[] { new Item(20002, 1), new Item(19973, 1) }),
	JUJU_COOKING_POTION(new Item(20008, 1), 67, 152.0, new Item[] { new Item(20001, 1), new Item(19975, 1) }),
	JUJU_FARMING_POTION(new Item(20012, 1), 64, 146.0, new Item[] { new Item(19999, 1), new Item(19980, 1) }),
	JUJU_WOODCUTTING_POTION(new Item(20016, 1), 71, 160.0, new Item[] { new Item(20002, 1), new Item(19972, 1) }),
	JUJU_FISHING_POTION(new Item(20020, 1), 70, 158.0, new Item[] { new Item(20001, 1), new Item(19976, 1) }),
	JUJU_HUNTER_POTION(new Item(20024, 1), 54, 123.0, new Item[] { new Item(19998, 1), new Item(19979, 1) }),
	SCENTLESS_POTION(new Item(20028, 1), 59, 135.0, new Item[] { new Item(20000, 1), new Item(19977, 1) }),
	SARADOMINS_BLESSING(new Item(20032, 1), 75, 179.0, new Item[] { new Item(20002, 1), new Item(19981, 1) }),
	GUTHIXS_GIFT(new Item(20036, 1), 75, 179.0, new Item[] { new Item(20002, 1), new Item(19982, 1) }),
	ZAMORAKS_FAVOUR(new Item(20040, 1), 75, 179.0, new Item[] { new Item(20002, 1), new Item(19983, 1) }),

	ATTACK_MIX_2(new Item(11429, 1), 4, 8.0, new Item[] { new Item(123, 1), new Item(11326, 1) }),
	ANTIPOISON_MIX_2(new Item(11433, 1), 6, 1.0, new Item[] { new Item(177, 1), new Item(11326, 1) }),
	RELICYMS_MIX_2(new Item(11437, 1), 9, 14.0, new Item[] { new Item(4846, 1), new Item(11326, 1) }),
	STRENGTH_MIX_2(new Item(11443, 1), 14, 17.0, new Item[] { new Item(117, 1), new Item(11326, 1) }),
	COMBAT_MIX_2(new Item(11445, 1), 40, 28.0, new Item[] { new Item(9743, 1), new Item(11326, 1) }),
	RESTORE_MIX_2(new Item(11449, 1), 24, 21.0, new Item[] { new Item(129, 1), new Item(11326, 1) }),
	ENERGY_MIX_2(new Item(11453, 1), 29, 23.0, new Item[] { new Item(3012, 1), new Item(11326, 1) }),
	DEFENCE_MIX_2(new Item(11457, 1), 33, 25.0, new Item[] { new Item(135, 1), new Item(11326, 1) }),
	AGILITY_MIX_2(new Item(11461, 1), 37, 27.0, new Item[] { new Item(3036, 1), new Item(11326, 1) }),
	PRAYER_MIX_2(new Item(11465, 1), 42, 29.0, new Item[] { new Item(141, 1), new Item(11326, 1) }),
	SUPER_ATTACK_MIX_2(new Item(11469, 1), 47, 33.0, new Item[] { new Item(147, 1), new Item(11326, 1) }),
	ANTIP_SUPERMIX_2(new Item(11473, 1), 51, 35.0, new Item[] { new Item(183, 1), new Item(11326, 1) }),
	FISHING_MIX_2(new Item(11477, 1), 53, 38.0, new Item[] { new Item(153, 1), new Item(11326, 1) }),
	SUPER_ENERGY_MIX_2(new Item(11481, 1), 56, 39.0, new Item[] { new Item(3020, 1), new Item(11326, 1) }),
	SUPER_STRENGTH_MIX_2(new Item(11485, 1), 59, 42.0, new Item[] { new Item(159, 1), new Item(11326, 1) }),
	MAGIC_ESSENCE_MIX_2(new Item(11489, 1), 61, 43.0, new Item[] { new Item(9023, 1), new Item(11326, 1) }),
	SUPER_RESTORE_MIX_2(new Item(11493, 1), 67, 48.0, new Item[] { new Item(3028, 1), new Item(11326, 1) }),
	SUPER_DEFENCE_MIX_2(new Item(11497, 1), 71, 50.0, new Item[] { new Item(165, 1), new Item(11326, 1) }),
	ANTIDOTE_MIX_2(new Item(11501, 1), 74, 52.0, new Item[] { new Item(5947, 1), new Item(11326, 1) }),
	ANTIFIRE_MIX_2(new Item(11505, 1), 75, 53.0, new Item[] { new Item(2456, 1), new Item(11326, 1) }),
	SUPER_RANGING_MIX_2(new Item(11509, 1), 80, 54.0, new Item[] { new Item(171, 1), new Item(11326, 1) }),
	SUPER_MAGIC_MIX_2(new Item(11513, 1), 83, 57.0, new Item[] { new Item(3044, 1), new Item(11326, 1) }),
	HUNTING_MIX_2(new Item(11517, 1), 58, 40.0, new Item[] { new Item(10002, 1), new Item(11326, 1) }),
	ZAMORAK_MIX_2(new Item(11521, 1), 85, 58.0, new Item[] { new Item(191, 1), new Item(11326, 1) }),
	GOBLIN_POTION(new Item(11810, 1), 37, 85.0, new Item[] { new Item(3002, 1), new Item(11807, 1) }),

	WEAK_MAGIC_POTION(new Item(17556, 1), 3, 21.0, new Item[] { new Item(17538, 1), new Item(17530, 1) }),
	WEAK_RANGED_POTION(new Item(17558, 1), 5, 34.0, new Item[] { new Item(17540, 1), new Item(17530, 1) }),
	WEAK_MELEE_POTION(new Item(17560, 1), 7, 37.0, new Item[] { new Item(17540, 1), new Item(17532, 1) }),
	WEAK_DEFENCE_POTION(new Item(17562, 1), 9, 41.0, new Item[] { new Item(17542, 1), new Item(17530, 1) }),
	WEAK_STAT_RESTORE_POTION(new Item(17564, 1), 12, 47.0, new Item[] { new Item(17542, 1), new Item(17534, 1) }),
	WEAK_CURE_POTION(new Item(17568, 1), 15, 53.0, new Item[] { new Item(17542, 1), new Item(17536, 1) }),
	WEAK_REJUVENATION_POTION(new Item(17570, 1), 18, 57.0, new Item[] { new Item(17542, 1), new Item(17532, 1) }),
	WEAK_WEAPON_POISON(new Item(17572, 1), 21, 61.0, new Item[] { new Item(17538, 1), new Item(17536, 1) }),
	WEAK_GATHERERS_POTION(new Item(17574, 1), 24, 65.0, new Item[] { new Item(17538, 1), new Item(17534, 1) }),
	WEAK_ARTISANS_POTION(new Item(17576, 1), 27, 68.0, new Item[] { new Item(17540, 1), new Item(17534, 1) }),
	WEAK_NATURALISTS_POTION(new Item(17578, 1), 30, 72.0, new Item[] { new Item(17538, 1), new Item(17532, 1) }),
	WEAK_SURVIVALISTS_POTION(new Item(17580, 1), 33, 75.0, new Item[] { new Item(17540, 1), new Item(17536, 1) }),
	MAGIC_POTION(new Item(17582, 1), 36, 79.0, new Item[] { new Item(17544, 1), new Item(17530, 1) }),
	RANGED_POTION(new Item(17584, 1), 38, 83.0, new Item[] { new Item(17546, 1), new Item(17530, 1) }),
	MELEE_POTION(new Item(17586, 1), 40, 86.0, new Item[] { new Item(17546, 1), new Item(17532, 1) }),
	DEFENCE_POTION_D(new Item(17588, 1), 42, 89.0, new Item[] { new Item(17548, 1), new Item(17530, 1) }),
	STAT_RESTORE_POTION(new Item(17590, 1), 45, 93.0, new Item[] { new Item(17548, 1), new Item(17534, 1) }),
	CURE_POTION(new Item(17592, 1), 48, 98.0, new Item[] { new Item(17548, 1), new Item(17536, 1) }),
	REJUVENATION_POTION(new Item(17594, 1), 51, 105.0, new Item[] { new Item(17548, 1), new Item(17532, 1) }),
	WEAPON_POISON(new Item(17596, 1), 54, 114.0, new Item[] { new Item(17544, 1), new Item(17536, 1) }),
	GATHERERS_POTION(new Item(17598, 1), 57, 123.0, new Item[] { new Item(17544, 1), new Item(17534, 1) }),
	ARTISANS_POTION(new Item(17600, 1), 60, 131.0, new Item[] { new Item(17546, 1), new Item(17534, 1) }),
	NATURALISTS_POTION(new Item(17602, 1), 63, 139.0, new Item[] { new Item(17544, 1), new Item(17532, 1) }),
	SURVIVALISTS_POTION(new Item(17604, 1), 66, 147.0, new Item[] { new Item(17546, 1), new Item(17536, 1) }),
	STRONG_MAGIC_POTION(new Item(17606, 1), 69, 155.0, new Item[] { new Item(17550, 1), new Item(17530, 1) }),
	STRONG_RANGED_POTION(new Item(17608, 1), 71, 160.0, new Item[] { new Item(17552, 1), new Item(17530, 1) }),
	STRONG_MELEE_POTION(new Item(17610, 1), 73, 164.0, new Item[] { new Item(17552, 1), new Item(17532, 1) }),
	STRONG_DEFENCE_POTION(new Item(17612, 1), 75, 170.0, new Item[] { new Item(17554, 1), new Item(17530, 1) }),
	STRONG_STAT_RESTORE_POTION(new Item(17614, 1), 78, 173.0, new Item[] { new Item(17554, 1), new Item(17534, 1) }),
	STRONG_CURE_POTION(new Item(17616, 1), 81, 178.0, new Item[] { new Item(17554, 1), new Item(17536, 1) }),
	STRONG_REJUVENATION_POTION(new Item(17618, 1), 84, 189.0, new Item[] { new Item(17554, 1), new Item(17532, 1) }),
	STRONG_WEAPON_POISON(new Item(17620, 1), 87, 205.0, new Item[] { new Item(17550, 1), new Item(17536, 1) }),
	STRONG_GATHERERS_POTION(new Item(17622, 1), 90, 234.0, new Item[] { new Item(17550, 1), new Item(17534, 1) }),
	STRONG_ARTISANS_POTION(new Item(17624, 1), 93, 253.0, new Item[] { new Item(17552, 1), new Item(17534, 1) }),
	STRONG_NATURALISTS_POTION(new Item(17626, 1), 96, 279.0, new Item[] { new Item(17550, 1), new Item(17532, 1) }),
	STRONG_SURVIVALISTS_POTION(new Item(17628, 1), 99, 315.0, new Item[] { new Item(17552, 1), new Item(17536, 1) }),
	NETTLE_WATER(new Item(4237, 1), 1, 1.0, new Item[] { new Item(1921, 1), new Item(4241, 1) });

	private Item product;
	private int req;
	private double xp;
	private Item primary;
	private Item[] secondaries;

	public static Map<Integer, CraftablePotion> MAP = new HashMap<>();

	static {
		for (CraftablePotion p : CraftablePotion.values())
			for (Item item : p.getSecondaries()) {
				MAP.put((p.getPrimary().getId() << 16) + item.getId(), p);
				MAP.put((item.getId() << 16) + p.getPrimary().getId(), p);
			}
	}

	public static CraftablePotion forCombo(int itemId, int itemId2) {
		return MAP.get((itemId << 16) + itemId2);
	}

	private CraftablePotion(Item product, int req, double xp, Item[] materials) {
		this.product = product;
		this.req = req;
		this.xp = xp;
		primary = materials[0];
		secondaries = new Item[materials.length-1];
		for (int i = 1;i < materials.length;i++)
			secondaries[i-1] = materials[i];
	}

	public Item getProduct() {
		return product;
	}

	public int getReq() {
		return req;
	}

	public double getXp() {
		return xp;
	}

	public Item getPrimary() {
		return primary;
	}

	public Item[] getSecondaries() {
		return secondaries;
	}
}