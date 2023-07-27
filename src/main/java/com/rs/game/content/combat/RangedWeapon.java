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
package com.rs.game.content.combat;

import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

import java.util.*;

public enum RangedWeapon {

	CROSSBOW(new Animation(4230), 6, new int[] { 767, 837, 11165, 11167 }, AmmoType.BRONZE_BOLTS),
	BRONZE_CROSSBOW(new Animation(4230), 6, new int[] { 9174 }, AmmoType.BRONZE_BOLTS),
	BLURITE_CROSSBOW(new Animation(4230), 6, new int[] { 9176 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS),
	IRON_CROSSBOW(new Animation(4230), 6, new int[] { 9177 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS),
	STEEL_CROSSBOW(new Animation(4230), 6, new int[] { 9179 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS),
	BLACK_CROSSBOW(new Animation(4230), 6, new int[] { 13081 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS),
	MITH_CROSSBOW(new Animation(4230), 6, new int[] { 9181 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS, AmmoType.MITHRIL_BOLTS),
	ADAMANT_CROSSBOW(new Animation(4230), 6, new int[] { 9183 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS, AmmoType.MITHRIL_BOLTS, AmmoType.ADAMANT_BOLTS),
	RUNE_CROSSBOW(new Animation(4230), 6, new int[] { 9185, 13530 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS, AmmoType.MITHRIL_BOLTS, AmmoType.ADAMANT_BOLTS, AmmoType.RUNITE_BOLTS, AmmoType.BROAD_TIPPED_BOLTS, AmmoType.WALLASALKIBANE_BOLT, AmmoType.ABYSSALBANE_BOLT, AmmoType.BASILISKBANE_BOLT, AmmoType.DRAGONBANE_BOLT, AmmoType.BAKRIMINEL_BOLTS),
	ARMADYL_CROSSBOW(new Animation(4230), 6, new int[] { 25037, 25039 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS, AmmoType.MITHRIL_BOLTS, AmmoType.ADAMANT_BOLTS, AmmoType.RUNITE_BOLTS, AmmoType.BROAD_TIPPED_BOLTS, AmmoType.WALLASALKIBANE_BOLT, AmmoType.ABYSSALBANE_BOLT, AmmoType.BASILISKBANE_BOLT, AmmoType.DRAGONBANE_BOLT, AmmoType.BAKRIMINEL_BOLTS),
	CHAOTIC_CROSSBOW(new Animation(4230), 6, new int[] { 18357 }, AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS, AmmoType.MITHRIL_BOLTS, AmmoType.ADAMANT_BOLTS, AmmoType.RUNITE_BOLTS, AmmoType.BROAD_TIPPED_BOLTS, AmmoType.WALLASALKIBANE_BOLT, AmmoType.ABYSSALBANE_BOLT, AmmoType.BASILISKBANE_BOLT, AmmoType.DRAGONBANE_BOLT, AmmoType.BAKRIMINEL_BOLTS),

	CORAL_CROSSBOW(new Animation(16836), 6, new int[] { 24303 }, AmmoType.CORAL_BOLTS),
	ROYAL_CROSSBOW(new Animation(16929), 6, new int[] { 24338, 24339 }, AmmoType.ROYAL_BOLTS),
	DORGESHUUN_CBOW(new Animation(4230), 5, new int[] { 8880 }, AmmoType.BONE_BOLTS),
	ZANIKS_CROSSBOW(new Animation(4230), 5, new int[] { 14684 }, AmmoType.BONE_BOLTS, AmmoType.BRONZE_BOLTS, AmmoType.SILVER_BOLTS, AmmoType.BLURITE_BOLTS, AmmoType.IRON_BOLTS, AmmoType.STEEL_BOLTS, AmmoType.BLACK_BOLTS, AmmoType.MITHRIL_BOLTS, AmmoType.ADAMANT_BOLTS),
	KARILS_CROSSBOW(new Animation(2075), 4, new int[] { 4734, 4934, 4935, 4936, 4937 }, AmmoType.BOLT_RACK),

	HUNTERS_CROSSBOW(new Animation(4230), 4, new int[] { 10156 }, AmmoType.KEBBIT_BOLTS, AmmoType.LONG_KEBBIT_BOLTS),

	DOMINION_CROSSBOW(new Animation(4230), 6, new int[] { 22348 }, 27, 955),

	SHORTBOW(new Animation(426), 4, new int[] { 841, 23818 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW),
	QUICK_BOW(new Animation(426), 4, new int[] { 23043 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW),
	LONGBOW(new Animation(426), 6, new int[] { 839 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW),

	OAK_SHORTBOW(new Animation(426), 4, new int[] { 843 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW),
	OAK_LONGBOW(new Animation(426), 6, new int[] { 845, 4236 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW),

	WILLOW_BOW(new Animation(426), 4, new int[] { 849 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW),
	WILLOW_LONGBOW(new Animation(426), 6, new int[] { 847 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW),
	WILLOW_COMP_BOW(new Animation(426), 5, new int[] { 10280, 13541 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW),

	MAPLE_BOW(new Animation(426), 4, new int[] { 853, 13524 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW),
	MAPLE_LONGBOW(new Animation(426), 6, new int[] { 851, 13523, 18331 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW),

	YEW_BOW(new Animation(426), 4, new int[] { 857, 13526 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.ICE_ARROWS),
	YEW_LONGBOW(new Animation(426), 6, new int[] { 855 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.ICE_ARROWS),
	YEW_COMP_BOW(new Animation(426), 5, new int[] { 10282, 13542 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.ICE_ARROWS),

	MAGIC_BOW(new Animation(426), 4, new int[] { 861, 13528 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.BROAD_ARROW, AmmoType.WALLASALKIBANE_ARROW, AmmoType.ABYSSALBANE_ARROW, AmmoType.BASILISKBANE_ARROW, AmmoType.DRAGONBANE_ARROW, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS, AmmoType.ICE_ARROWS),
	MAGIC_LONGBOW(new Animation(426), 6, new int[] { 859, 13543, 13527, 18332 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.BROAD_ARROW, AmmoType.WALLASALKIBANE_ARROW, AmmoType.ABYSSALBANE_ARROW, AmmoType.BASILISKBANE_ARROW, AmmoType.DRAGONBANE_ARROW, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS, AmmoType.ICE_ARROWS),
	MAGIC_COMP_BOW(new Animation(426), 5, new int[] { 10284, 13543 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.BROAD_ARROW, AmmoType.WALLASALKIBANE_ARROW, AmmoType.ABYSSALBANE_ARROW, AmmoType.BASILISKBANE_ARROW, AmmoType.DRAGONBANE_ARROW, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS, AmmoType.ICE_ARROWS),

	DARK_BOW(new Animation(426), 9, new int[] { 11235, 13405, 15701, 15702, 15703, 15704 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.DRAGON_ARROW, AmmoType.WALLASALKIBANE_ARROW, AmmoType.ABYSSALBANE_ARROW, AmmoType.BASILISKBANE_ARROW, AmmoType.DRAGONBANE_ARROW, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS, AmmoType.ICE_ARROWS),

	SACRED_CLAY_BOW(new Animation(426), 4, new int[] { 14121 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.BROAD_ARROW, AmmoType.WALLASALKIBANE_ARROW, AmmoType.ABYSSALBANE_ARROW, AmmoType.BASILISKBANE_ARROW, AmmoType.DRAGONBANE_ARROW, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS, AmmoType.ICE_ARROWS),
	GRAVITE_SHORTBOW(new Animation(426), 4, new int[] { 18373 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.BROAD_ARROW, AmmoType.ICE_ARROWS),
	TRAINING_BOW(new Animation(426), 4, new int[] { 9705 }, AmmoType.TRAINING_ARROWS),

	SEERCULL(new Animation(426), 4, new int[] { 6724, 13529 }, AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW, AmmoType.MITHRIL_ARROW, AmmoType.ADAMANT_ARROW, AmmoType.RUNE_ARROW, AmmoType.BROAD_ARROW, AmmoType.WALLASALKIBANE_ARROW, AmmoType.ABYSSALBANE_ARROW, AmmoType.BASILISKBANE_ARROW, AmmoType.DRAGONBANE_ARROW, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS, AmmoType.ICE_ARROWS),

	OGRE_BOW(new Animation(426), 8, new int[] { 2883 }, AmmoType.OGRE_ARROW, AmmoType.BRONZE_BRUTAL, AmmoType.IRON_BRUTAL, AmmoType.STEEL_BRUTAL, AmmoType.BLACK_BRUTAL, AmmoType.MITHRIL_BRUTAL, AmmoType.ADAMANT_BRUTAL, AmmoType.RUNE_BRUTAL),
	COMP_OGRE_BOW(new Animation(426), 5, new int[] { 4827 }, AmmoType.BRONZE_BRUTAL, AmmoType.IRON_BRUTAL, AmmoType.STEEL_BRUTAL, AmmoType.BLACK_BRUTAL, AmmoType.MITHRIL_BRUTAL, AmmoType.ADAMANT_BRUTAL, AmmoType.RUNE_BRUTAL),

	SARADOMIN_BOW(new Animation(426), 6, new int[] { 19143, 19145 }, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS),
	GUTHIX_BOW(new Animation(426), 6, new int[] { 19146, 19148 }, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS),
	ZAMORAK_BOW(new Animation(426), 6, new int[] { 19149, 19151 }, AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS),

	DECIMATION(new Animation(426), 4, new int[] { 24456 }, 1066, 2962), //3191 spec gfx
	BOOGIE_BOW(new Animation(426), 4, new int[] { 24474 }, 3196, 3195),
	CRYSTAL_BOW(new Animation(426), 5, new int[] { 4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223 }, 249, 250),
	SLING(new Animation(3128), 4, new int[] { 19830, 15597 }, 32, -1),
	ZARYTE_BOW(new Animation(426), 5, new int[] { 20171, 20173 }, 1066, 2962),

	BRONZE_THROWNAXE(new Animation(9055), 5, true, new int[] { 800 }, 36),
	IRON_THROWNAXE(new Animation(9055), 5, true, new int[] { 801 }, 35),
	STEEL_THROWNAXE(new Animation(9055), 5, true, new int[] { 802 }, 37),
	MITHRIL_THROWNAXE(new Animation(9055), 5, true, new int[] { 803 }, 38),
	ADAMANT_THROWNAXE(new Animation(9055), 5, true, new int[] { 804 }, 39),
	RUNE_THROWNAXE(new Animation(9055), 5, true, new int[] { 805 }, 41),

	BRONZE_DART(new Animation(582), 3, true, new int[] { 806, 812, 5628, 5635 }, 226, 232),
	IRON_DART(new Animation(582), 3, true, new int[] { 807, 813, 5629, 5636 }, 227, 233),
	STEEL_DART(new Animation(582), 3, true, new int[] { 808, 814, 5630, 5637 }, 228, 234),
	BLACK_DART(new Animation(582), 3, true, new int[] { 3093, 3094, 5631, 5638 }, 34, 273),
	MITHRIL_DART(new Animation(582), 3, true, new int[] { 809, 815, 5632, 5639 }, 229, 235),
	ADAMANT_DART(new Animation(582), 3, true, new int[] { 810, 816, 5633, 5640 }, 230, 236),
	RUNE_DART(new Animation(582), 3, true, new int[] { 811, 817, 5634, 5641 }, 231, 237),
	DRAGON_DART(new Animation(582), 3, true, new int[] { 11230, 11231, 11233, 11234 }, 1122, 1123),

	BLISTERWOOD_STAKE(new Animation(582), 3, true, new int[] { 21581 }, 732),

	DEATHTOUCHED_DART(new Animation(582), 3, true, new int[] { 25202 }, 1122, 1123),

	BRONZE_JAVELIN(new Animation(10501), 6, true, new int[] { 825, 831, 5642, 5648 }, 200),
	IRON_JAVELIN(new Animation(10501), 6, true, new int[] { 826, 832, 5643, 5649 }, 201),
	STEEL_JAVELIN(new Animation(10501), 6, true, new int[] { 827, 833, 5644, 5650 }, 202),
	MITHRIL_JAVELIN(new Animation(10501), 6, true, new int[] { 828, 834, 5645, 5651 }, 203),
	ADAMANT_JAVELIN(new Animation(10501), 6, true, new int[] { 829, 835, 5646, 5652 }, 204),
	RUNE_JAVELIN(new Animation(10501), 6, true, new int[] { 830, 836, 5647, 5653 }, 205),

	BRONZE_KNIFE(new Animation(929), 3, true, new int[] { 864, 870, 5654, 5661 }, 212),
	IRON_KNIFE(new Animation(929), 3, true, new int[] { 863, 871, 5655, 5662 }, 213),
	STEEL_KNIFE(new Animation(929), 3, true, new int[] { 865, 872, 5656, 5663 }, 214),
	BLACK_KNIFE(new Animation(929), 3, true, new int[] { 869, 5658, 5665 }, 215),
	MITHRIL_KNIFE(new Animation(929), 3, true, new int[] { 866, 873, 5657, 5664 }, 216),
	ADAMANT_KNIFE(new Animation(929), 3, true, new int[] { 867, 875, 5659, 5666 }, 217),
	RUNE_KNIFE(new Animation(929), 3, true, new int[] { 868, 876, 5660, 5667 }, 218),

	HOLY_WATER(new Animation(9055), 4, true, new int[] { 732 }, 192, 193), //52/53 white holy water?

	TOKTZ_XIL_UL(new Animation(10501), 4, true, new int[] { 6522 }, 442),

	CHINCHOMPA(new Animation(2779), 4, true, new int[] { 10033 }, 908),
	RED_CHINCHOMPA(new Animation(2779), 4, true, new int[] { 10034 }, 909),

	SAGAIE(new Animation(3236), 6, true, new int[] { 21364 }, 466),
	BOLAS(new Animation(3128), 6, true, new int[] { 21365 }, 468),

	HAND_CANNON(new Animation(12174), 7, new int[] { 15241 }, AmmoType.HAND_CANNON_SHOT),

	SWAMP_LIZARD(new Animation(5247), 5, new int[] { 10149 }, AmmoType.GUAM_TAR),
	ORANGE_SALAMANDER(new Animation(5247), 5, new int[] { 10146 }, AmmoType.MARRENTILL_TAR),
	RED_SALAMANDER(new Animation(5247), 5, new int[] { 10147 }, AmmoType.TARROMIN_TAR),
	BLACK_SALAMANDER(new Animation(5247), 5, new int[] { 10148 }, AmmoType.HARRALANDER_TAR),

	PERFORMANCE_SHORTBOW(new Animation(426), 4, new int[] { 13719 }, AmmoType.PERFORMANCE_ARROW),
	PERFORMANCE_KNIFE(new Animation(929), 3, true, new int[] { 13720 }, 212),
	PERFORMANCE_THROWING_AXE(new Animation(9055), 5, true, new int[] { 13721 }, 36),

	MORRIGANS_JAVELIN(new Animation(10501), 6, true, new int[] { 13879, 13880, 13881, 13882, 13953, 13954, 13955, 13956 }, 1837),
	MORRIGANS_THROWING_AXE(new Animation(10504), 5, true, new int[] { 13883, 13957 }, 1839),

	BOW_CLASS_1(new Animation(426), 5, new int[] { 14192 }, AmmoType.ARROWS_CLASS_1),
	BOW_CLASS_2(new Animation(426), 5, new int[] { 14194 }, AmmoType.ARROWS_CLASS_1, AmmoType.ARROWS_CLASS_2),
	BOW_CLASS_3(new Animation(426), 5, new int[] { 14196 }, AmmoType.ARROWS_CLASS_1, AmmoType.ARROWS_CLASS_2, AmmoType.ARROWS_CLASS_3),
	BOW_CLASS_4(new Animation(426), 5, new int[] { 14198 }, AmmoType.ARROWS_CLASS_1, AmmoType.ARROWS_CLASS_2, AmmoType.ARROWS_CLASS_3, AmmoType.ARROWS_CLASS_4),
	BOW_CLASS_5(new Animation(426), 5, new int[] { 14200 }, AmmoType.ARROWS_CLASS_1, AmmoType.ARROWS_CLASS_2, AmmoType.ARROWS_CLASS_3, AmmoType.ARROWS_CLASS_4, AmmoType.ARROWS_CLASS_5),

	TANGLE_GUM_SHORTBOW(new Animation(426), 4, new int[] { 15775, 16867 }, AmmoType.NOVITE_ARROWS),
	TANGLE_GUM_LONGBOW(new Animation(426), 6, new int[] { 15903, 16317 }, AmmoType.NOVITE_ARROWS),
	SEEPING_ELM_SHORTBOW(new Animation(426), 4, new int[] { 15776, 16869 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS),
	SEEPING_ELM_LONGBOW(new Animation(426), 6, new int[] { 15904, 16319 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS),
	BLOOD_SPINDLE_SHORTBOW(new Animation(426), 4, new int[] { 15777, 16871 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS),
	BLOOD_SPINDLE_LONGBOW(new Animation(426), 6, new int[] { 15905, 16321 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS),
	UTUKU_SHORTBOW(new Animation(426), 4, new int[] { 15778, 16873 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS),
	UTUKU_LONGBOW(new Animation(426), 6, new int[] { 15906, 16323 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS),
	SPINEBEAM_SHORTBOW(new Animation(426), 4, new int[] { 15779, 16875 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS),
	SPINEBEAM_LONGBOW(new Animation(426), 6, new int[] { 15907, 16325 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS),
	BOVISTRANGLER_SHORTBOW(new Animation(426), 4, new int[] { 15780, 16877 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS),
	BOVISTRANGLER_LONGBOW(new Animation(426), 6, new int[] { 15908, 16327 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS),
	THIGAT_SHORTBOW(new Animation(426), 4, new int[] { 15781, 16879 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS),
	THIGAT_LONGBOW(new Animation(426), 6, new int[] { 15909, 16329 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS),
	CORPSETHORN_SHORTBOW(new Animation(426), 4, new int[] { 15782, 16881 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS),
	CORPSETHORN_LONGBOW(new Animation(426), 6, new int[] { 15910, 16331 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS),
	ENTGALLOW_SHORTBOW(new Animation(426), 4, new int[] { 15783, 16883 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS),
	ENTGALLOW_LONGBOW(new Animation(426), 6, new int[] { 15911, 16333 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS),
	GRAVE_CREEPER_SHORTBOW(new Animation(426), 4, new int[] { 15784, 16885 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS, AmmoType.PROMETHIUM_ARROWS),
	GRAVE_CREEPER_LONGBOW(new Animation(426), 6, new int[] { 15912, 16335 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS, AmmoType.PROMETHIUM_ARROWS),
	SAGITTARIAN_SHORTBOW(new Animation(426), 4, new int[] { 15785, 16887 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS, AmmoType.PROMETHIUM_ARROWS, AmmoType.SAGITTARIAN_ARROWS),
	SAGITTARIAN_LONGBOW(new Animation(426), 6, new int[] { 15913, 16337 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS, AmmoType.PROMETHIUM_ARROWS, AmmoType.SAGITTARIAN_ARROWS),
	HEXHUNTER_BOW(new Animation(426), 4, new int[] { 15836, 17295 }, AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS, AmmoType.MARMAROS_ARROWS, AmmoType.KRATONITE_ARROWS, AmmoType.FRACTITE_ARROWS, AmmoType.ZEPHYRIUM_ARROWS, AmmoType.ARGONITE_ARROWS, AmmoType.KATAGON_ARROWS, AmmoType.GORGONITE_ARROWS, AmmoType.PROMETHIUM_ARROWS, AmmoType.SAGITTARIAN_ARROWS),

	OZANS_LONGBOW(new Animation(426), 6, new int[] { 20859 }, AmmoType.OZANS_KRATONITE_ARROWS);

	private Animation attackAnim;
	private int attackSpeed = 6;
	private Set<AmmoType> ammos;
	private boolean thrown;
	private int[] itemIds;
	private int projAnim;
	private int drawbackSpotAnim;

	private static Map<Integer, RangedWeapon> MAP = new HashMap<>();

	static {
		for (RangedWeapon t : RangedWeapon.values())
			for (int id : t.itemIds)
				MAP.put(id, t);
	}

	public static RangedWeapon forId(int itemId) {
		return MAP.get(itemId);
	}

	private RangedWeapon(Animation attackAnim, int attackSpeed, boolean thrown, int[] itemIds, int projAnim, int drawbackSpotAnim) {
		this.attackSpeed = attackSpeed;
		this.attackAnim = attackAnim;
		this.thrown = thrown;
		this.itemIds = itemIds;
		this.projAnim = projAnim;
		this.drawbackSpotAnim = drawbackSpotAnim;
	}

	private RangedWeapon(Animation attackAnim, int attackSpeed, boolean thrown, int[] itemIds, int projAnim) {
		this(attackAnim, attackSpeed, thrown, itemIds, projAnim, -1);
	}

	private RangedWeapon(Animation attackAnim, int attackSpeed, int[] itemIds, int projAnim, int drawbackSpotAnim) {
		this.attackSpeed = attackSpeed;
		this.attackAnim = attackAnim;
		this.itemIds = itemIds;
		this.projAnim = projAnim;
		this.drawbackSpotAnim = drawbackSpotAnim;
	}

	private RangedWeapon(Animation attackAnim, int attackSpeed, int[] itemIds, AmmoType... ammos) {
		this(attackAnim, attackSpeed, false, itemIds, -1, -1);
		this.ammos = new HashSet<>(Arrays.asList(ammos));
	}

	public boolean properAmmo(Player player, boolean print) {
		if (thrown || ammos == null)
			return true;
		AmmoType ammo = AmmoType.forId(player.getEquipment().getAmmoId());
		if (ammo == null) {
			if (print)
				player.sendMessage("You have no ammo in your quiver!");
			return false;
		}
		if (!ammos.contains(ammo)) {
			if (print)
				player.sendMessage("You cannot use that ammo with this weapon.");
			return false;
		}
		return true;
	}

	public Animation getAttackAnimation() {
		return attackAnim;
	}

	public SpotAnim getAttackSpotAnim(Player player, AmmoType ammo) {
		switch(this) {
		case DARK_BOW -> {
			return new SpotAnim(ammo.getDoubleDrawbackSpotAnim(player.getEquipment().getAmmoId()), 0, 100);
		}
		case HAND_CANNON -> {
			return new SpotAnim(ammo.getDrawbackSpotAnim(player.getEquipment().getAmmoId()));
		}
		default -> {
			if (thrown || ammos == null)
				return new SpotAnim(drawbackSpotAnim, 0, 100);
			if (ammo != null)
				return new SpotAnim(ammo.getDrawbackSpotAnim(player.getEquipment().getAmmoId()), 0, 100);
		}
		}
		return null;
	}

	public WorldProjectile getProjectile(Player player, Entity target) {
		double speed = 8.0 / ((double) attackSpeed);
		return switch(this) {
			case SLING -> World.sendProjectile(player, target, projAnim, 20, 30, speed);
			default -> {
				if (thrown)
					yield World.sendProjectile(player, target, projAnim, 20, 5 + (attackSpeed * 5), speed);
				if (ammos == null)
					yield World.sendProjectile(player, target, projAnim, 20, 35, speed);
				AmmoType ammo = AmmoType.forId(player.getEquipment().getAmmoId());
				yield World.sendProjectile(player, target, ammo.getProjAnim(player.getEquipment().getAmmoId()), 20, 40, speed);
			}
		};
	}

	public int[] getIds() {
		return itemIds;
	}

	public boolean isThrown() {
		return thrown;
	}

	public Set<AmmoType> getAmmos() {
		return ammos;
	}

	public int getAttackDelay() {
		return attackSpeed;
	}
}
