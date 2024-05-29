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
package com.rs.game.content.combat

import com.rs.game.World
import com.rs.game.model.WorldProjectile
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.SpotAnim
import java.util.*

enum class RangedWeapon {
    CROSSBOW(intArrayOf(767, 837, 11165, 11167), AmmoType.BRONZE_BOLTS),
    BRONZE_CROSSBOW(intArrayOf(9174), AmmoType.BRONZE_BOLTS),
    BLURITE_CROSSBOW(intArrayOf(9176), AmmoType.BRONZE_BOLTS, AmmoType.BLURITE_BOLTS),
    IRON_CROSSBOW(
        intArrayOf(9177),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS
    ),
    STEEL_CROSSBOW(
        intArrayOf(9179),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS
    ),
    BLACK_CROSSBOW(
        intArrayOf(13081),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS
    ),
    MITH_CROSSBOW(
        intArrayOf(9181),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS,
        AmmoType.MITHRIL_BOLTS
    ),
    ADAMANT_CROSSBOW(
        intArrayOf(9183),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS,
        AmmoType.MITHRIL_BOLTS,
        AmmoType.ADAMANT_BOLTS
    ),
    RUNE_CROSSBOW(
        intArrayOf(9185, 13530),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS,
        AmmoType.MITHRIL_BOLTS,
        AmmoType.ADAMANT_BOLTS,
        AmmoType.RUNITE_BOLTS,
        AmmoType.BROAD_TIPPED_BOLTS,
        AmmoType.WALLASALKIBANE_BOLT,
        AmmoType.ABYSSALBANE_BOLT,
        AmmoType.BASILISKBANE_BOLT,
        AmmoType.DRAGONBANE_BOLT,
        AmmoType.BAKRIMINEL_BOLTS
    ),
    ARMADYL_CROSSBOW(
        intArrayOf(25037, 25039),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS,
        AmmoType.MITHRIL_BOLTS,
        AmmoType.ADAMANT_BOLTS,
        AmmoType.RUNITE_BOLTS,
        AmmoType.BROAD_TIPPED_BOLTS,
        AmmoType.WALLASALKIBANE_BOLT,
        AmmoType.ABYSSALBANE_BOLT,
        AmmoType.BASILISKBANE_BOLT,
        AmmoType.DRAGONBANE_BOLT,
        AmmoType.BAKRIMINEL_BOLTS
    ),
    CHAOTIC_CROSSBOW(
        intArrayOf(18357),
        AmmoType.BRONZE_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS,
        AmmoType.MITHRIL_BOLTS,
        AmmoType.ADAMANT_BOLTS,
        AmmoType.RUNITE_BOLTS,
        AmmoType.BROAD_TIPPED_BOLTS,
        AmmoType.WALLASALKIBANE_BOLT,
        AmmoType.ABYSSALBANE_BOLT,
        AmmoType.BASILISKBANE_BOLT,
        AmmoType.DRAGONBANE_BOLT,
        AmmoType.BAKRIMINEL_BOLTS
    ),

    CORAL_CROSSBOW(intArrayOf(24303), AmmoType.CORAL_BOLTS),
    ROYAL_CROSSBOW(intArrayOf(24338, 24339), AmmoType.ROYAL_BOLTS),
    DORGESHUUN_CBOW(intArrayOf(8880), AmmoType.BONE_BOLTS),
    ZANIKS_CROSSBOW(
        intArrayOf(14684),
        AmmoType.BONE_BOLTS,
        AmmoType.BRONZE_BOLTS,
        AmmoType.SILVER_BOLTS,
        AmmoType.BLURITE_BOLTS,
        AmmoType.IRON_BOLTS,
        AmmoType.STEEL_BOLTS,
        AmmoType.BLACK_BOLTS,
        AmmoType.MITHRIL_BOLTS,
        AmmoType.ADAMANT_BOLTS
    ),
    KARILS_CROSSBOW(intArrayOf(4734, 4934, 4935, 4936, 4937), AmmoType.BOLT_RACK),

    HUNTERS_CROSSBOW(intArrayOf(10156), AmmoType.KEBBIT_BOLTS, AmmoType.LONG_KEBBIT_BOLTS),

    DOMINION_CROSSBOW(intArrayOf(22348), 27, 955),

    SHORTBOW(intArrayOf(841, 23818), AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW),
    QUICK_BOW(intArrayOf(23043), AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW),
    LONGBOW(intArrayOf(839), AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW),

    OAK_SHORTBOW(intArrayOf(843), AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW),
    OAK_LONGBOW(intArrayOf(845, 4236), AmmoType.BRONZE_ARROW, AmmoType.IRON_ARROW, AmmoType.STEEL_ARROW),

    WILLOW_BOW(
        intArrayOf(849),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW
    ),
    WILLOW_LONGBOW(
        intArrayOf(847),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW
    ),
    WILLOW_COMP_BOW(
        intArrayOf(10280, 13541),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW
    ),

    MAPLE_BOW(
        intArrayOf(853, 13524),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW
    ),
    MAPLE_LONGBOW(
        intArrayOf(851, 13523, 18331),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW
    ),

    YEW_BOW(
        intArrayOf(857, 13526),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.ICE_ARROWS
    ),
    YEW_LONGBOW(
        intArrayOf(855),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.ICE_ARROWS
    ),
    YEW_COMP_BOW(
        intArrayOf(10282, 13542),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.ICE_ARROWS
    ),

    MAGIC_BOW(
        intArrayOf(861, 13528),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.BROAD_ARROW,
        AmmoType.WALLASALKIBANE_ARROW,
        AmmoType.ABYSSALBANE_ARROW,
        AmmoType.BASILISKBANE_ARROW,
        AmmoType.DRAGONBANE_ARROW,
        AmmoType.SARADOMIN_ARROWS,
        AmmoType.GUTHIX_ARROWS,
        AmmoType.ZAMORAK_ARROWS,
        AmmoType.ICE_ARROWS
    ),
    MAGIC_LONGBOW(
        intArrayOf(859, 13543, 13527, 18332),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.BROAD_ARROW,
        AmmoType.WALLASALKIBANE_ARROW,
        AmmoType.ABYSSALBANE_ARROW,
        AmmoType.BASILISKBANE_ARROW,
        AmmoType.DRAGONBANE_ARROW,
        AmmoType.SARADOMIN_ARROWS,
        AmmoType.GUTHIX_ARROWS,
        AmmoType.ZAMORAK_ARROWS,
        AmmoType.ICE_ARROWS
    ),
    MAGIC_COMP_BOW(
        intArrayOf(10284, 13543),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.BROAD_ARROW,
        AmmoType.WALLASALKIBANE_ARROW,
        AmmoType.ABYSSALBANE_ARROW,
        AmmoType.BASILISKBANE_ARROW,
        AmmoType.DRAGONBANE_ARROW,
        AmmoType.SARADOMIN_ARROWS,
        AmmoType.GUTHIX_ARROWS,
        AmmoType.ZAMORAK_ARROWS,
        AmmoType.ICE_ARROWS
    ),

    DARK_BOW(
        intArrayOf(11235, 13405, 15701, 15702, 15703, 15704),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.DRAGON_ARROW,
        AmmoType.WALLASALKIBANE_ARROW,
        AmmoType.ABYSSALBANE_ARROW,
        AmmoType.BASILISKBANE_ARROW,
        AmmoType.DRAGONBANE_ARROW,
        AmmoType.SARADOMIN_ARROWS,
        AmmoType.GUTHIX_ARROWS,
        AmmoType.ZAMORAK_ARROWS,
        AmmoType.ICE_ARROWS
    ),

    SACRED_CLAY_BOW(
        intArrayOf(14121),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.BROAD_ARROW,
        AmmoType.WALLASALKIBANE_ARROW,
        AmmoType.ABYSSALBANE_ARROW,
        AmmoType.BASILISKBANE_ARROW,
        AmmoType.DRAGONBANE_ARROW,
        AmmoType.SARADOMIN_ARROWS,
        AmmoType.GUTHIX_ARROWS,
        AmmoType.ZAMORAK_ARROWS,
        AmmoType.ICE_ARROWS
    ),
    GRAVITE_SHORTBOW(
        intArrayOf(18373),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.BROAD_ARROW,
        AmmoType.ICE_ARROWS
    ),
    TRAINING_BOW(intArrayOf(9705), AmmoType.TRAINING_ARROWS),

    SEERCULL(
        intArrayOf(6724, 13529),
        AmmoType.BRONZE_ARROW,
        AmmoType.IRON_ARROW,
        AmmoType.STEEL_ARROW,
        AmmoType.MITHRIL_ARROW,
        AmmoType.ADAMANT_ARROW,
        AmmoType.RUNE_ARROW,
        AmmoType.BROAD_ARROW,
        AmmoType.WALLASALKIBANE_ARROW,
        AmmoType.ABYSSALBANE_ARROW,
        AmmoType.BASILISKBANE_ARROW,
        AmmoType.DRAGONBANE_ARROW,
        AmmoType.SARADOMIN_ARROWS,
        AmmoType.GUTHIX_ARROWS,
        AmmoType.ZAMORAK_ARROWS,
        AmmoType.ICE_ARROWS
    ),

    OGRE_BOW(
        intArrayOf(2883),
        AmmoType.OGRE_ARROW,
        AmmoType.BRONZE_BRUTAL,
        AmmoType.IRON_BRUTAL,
        AmmoType.STEEL_BRUTAL,
        AmmoType.BLACK_BRUTAL,
        AmmoType.MITHRIL_BRUTAL,
        AmmoType.ADAMANT_BRUTAL,
        AmmoType.RUNE_BRUTAL
    ),
    COMP_OGRE_BOW(
        intArrayOf(4827),
        AmmoType.BRONZE_BRUTAL,
        AmmoType.IRON_BRUTAL,
        AmmoType.STEEL_BRUTAL,
        AmmoType.BLACK_BRUTAL,
        AmmoType.MITHRIL_BRUTAL,
        AmmoType.ADAMANT_BRUTAL,
        AmmoType.RUNE_BRUTAL
    ),

    SARADOMIN_BOW(intArrayOf(19143, 19145), AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS),
    GUTHIX_BOW(intArrayOf(19146, 19148), AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS),
    ZAMORAK_BOW(intArrayOf(19149, 19151), AmmoType.SARADOMIN_ARROWS, AmmoType.GUTHIX_ARROWS, AmmoType.ZAMORAK_ARROWS),

    DECIMATION(intArrayOf(24456), 1066, 2962),  //3191 spec gfx
    BOOGIE_BOW(intArrayOf(24474), 3196, 3195),
    CRYSTAL_BOW(intArrayOf(4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223), 249, 250),
    SLING(intArrayOf(19830, 15597), 32, -1),
    ZARYTE_BOW(intArrayOf(20171, 20173), 1066, 2962),

    BRONZE_THROWNAXE(true, intArrayOf(800), 36),
    IRON_THROWNAXE(true, intArrayOf(801), 35),
    STEEL_THROWNAXE(true, intArrayOf(802), 37),
    MITHRIL_THROWNAXE(true, intArrayOf(803), 38),
    ADAMANT_THROWNAXE(true, intArrayOf(804), 39),
    RUNE_THROWNAXE(true, intArrayOf(805), 41),

    BRONZE_DART(true, intArrayOf(806, 812, 5628, 5635), 226, 232),
    IRON_DART(true, intArrayOf(807, 813, 5629, 5636), 227, 233),
    STEEL_DART(true, intArrayOf(808, 814, 5630, 5637), 228, 234),
    BLACK_DART(true, intArrayOf(3093, 3094, 5631, 5638), 34, 273),
    MITHRIL_DART(true, intArrayOf(809, 815, 5632, 5639), 229, 235),
    ADAMANT_DART(true, intArrayOf(810, 816, 5633, 5640), 230, 236),
    RUNE_DART(true, intArrayOf(811, 817, 5634, 5641), 231, 237),
    DRAGON_DART(true, intArrayOf(11230, 11231, 11233, 11234), 1122, 1123),

    BLISTERWOOD_STAKE(true, intArrayOf(21581), 732),

    DEATHTOUCHED_DART(true, intArrayOf(25202), 1122, 1123),

    BRONZE_JAVELIN(true, intArrayOf(825, 831, 5642, 5648), 200),
    IRON_JAVELIN(true, intArrayOf(826, 832, 5643, 5649), 201),
    STEEL_JAVELIN(true, intArrayOf(827, 833, 5644, 5650), 202),
    MITHRIL_JAVELIN(true, intArrayOf(828, 834, 5645, 5651), 203),
    ADAMANT_JAVELIN(true, intArrayOf(829, 835, 5646, 5652), 204),
    RUNE_JAVELIN(true, intArrayOf(830, 836, 5647, 5653), 205),

    BRONZE_KNIFE(true, intArrayOf(864, 870, 5654, 5661), 212),
    IRON_KNIFE(true, intArrayOf(863, 871, 5655, 5662), 213),
    STEEL_KNIFE(true, intArrayOf(865, 872, 5656, 5663), 214),
    BLACK_KNIFE(true, intArrayOf(869, 5658, 5665), 215),
    MITHRIL_KNIFE(true, intArrayOf(866, 873, 5657, 5664), 216),
    ADAMANT_KNIFE(true, intArrayOf(867, 875, 5659, 5666), 217),
    RUNE_KNIFE(true, intArrayOf(868, 876, 5660, 5667), 218),

    HOLY_WATER(true, intArrayOf(732), 192, 193),  //52/53 white holy water?

    TOKTZ_XIL_UL(true, intArrayOf(6522), 442),

    CHINCHOMPA(true, intArrayOf(10033), 908),
    RED_CHINCHOMPA(true, intArrayOf(10034), 909),

    SAGAIE(true, intArrayOf(21364), 466),
    BOLAS(true, intArrayOf(21365), 468),

    HAND_CANNON(intArrayOf(15241), AmmoType.HAND_CANNON_SHOT),

    SWAMP_LIZARD(intArrayOf(10149), AmmoType.GUAM_TAR),
    ORANGE_SALAMANDER(intArrayOf(10146), AmmoType.MARRENTILL_TAR),
    RED_SALAMANDER(intArrayOf(10147), AmmoType.TARROMIN_TAR),
    BLACK_SALAMANDER(intArrayOf(10148), AmmoType.HARRALANDER_TAR),

    PERFORMANCE_SHORTBOW(intArrayOf(13719), AmmoType.PERFORMANCE_ARROW),
    PERFORMANCE_KNIFE(true, intArrayOf(13720), 212),
    PERFORMANCE_THROWING_AXE(true, intArrayOf(13721), 36),

    MORRIGANS_JAVELIN(true, intArrayOf(13879, 13880, 13881, 13882, 13953, 13954, 13955, 13956), 1837),
    MORRIGANS_THROWING_AXE(true, intArrayOf(13883, 13957), 1839),

    BOW_CLASS_1(intArrayOf(14192), AmmoType.ARROWS_CLASS_1),
    BOW_CLASS_2(intArrayOf(14194), AmmoType.ARROWS_CLASS_1, AmmoType.ARROWS_CLASS_2),
    BOW_CLASS_3(intArrayOf(14196), AmmoType.ARROWS_CLASS_1, AmmoType.ARROWS_CLASS_2, AmmoType.ARROWS_CLASS_3),
    BOW_CLASS_4(
        intArrayOf(14198),
        AmmoType.ARROWS_CLASS_1,
        AmmoType.ARROWS_CLASS_2,
        AmmoType.ARROWS_CLASS_3,
        AmmoType.ARROWS_CLASS_4
    ),
    BOW_CLASS_5(
        intArrayOf(14200),
        AmmoType.ARROWS_CLASS_1,
        AmmoType.ARROWS_CLASS_2,
        AmmoType.ARROWS_CLASS_3,
        AmmoType.ARROWS_CLASS_4,
        AmmoType.ARROWS_CLASS_5
    ),

    TANGLE_GUM_SHORTBOW(intArrayOf(15775, 16867), AmmoType.NOVITE_ARROWS),
    TANGLE_GUM_LONGBOW(intArrayOf(15903, 16317), AmmoType.NOVITE_ARROWS),
    SEEPING_ELM_SHORTBOW(intArrayOf(15776, 16869), AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS),
    SEEPING_ELM_LONGBOW(intArrayOf(15904, 16319), AmmoType.NOVITE_ARROWS, AmmoType.BATHUS_ARROWS),
    BLOOD_SPINDLE_SHORTBOW(
        intArrayOf(15777, 16871),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS
    ),
    BLOOD_SPINDLE_LONGBOW(
        intArrayOf(15905, 16321),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS
    ),
    UTUKU_SHORTBOW(
        intArrayOf(15778, 16873),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS
    ),
    UTUKU_LONGBOW(
        intArrayOf(15906, 16323),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS
    ),
    SPINEBEAM_SHORTBOW(
        intArrayOf(15779, 16875),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS
    ),
    SPINEBEAM_LONGBOW(
        intArrayOf(15907, 16325),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS
    ),
    BOVISTRANGLER_SHORTBOW(
        intArrayOf(15780, 16877),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS
    ),
    BOVISTRANGLER_LONGBOW(
        intArrayOf(15908, 16327),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS
    ),
    THIGAT_SHORTBOW(
        intArrayOf(15781, 16879),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS
    ),
    THIGAT_LONGBOW(
        intArrayOf(15909, 16329),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS
    ),
    CORPSETHORN_SHORTBOW(
        intArrayOf(15782, 16881),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS
    ),
    CORPSETHORN_LONGBOW(
        intArrayOf(15910, 16331),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS
    ),
    ENTGALLOW_SHORTBOW(
        intArrayOf(15783, 16883),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS
    ),
    ENTGALLOW_LONGBOW(
        intArrayOf(15911, 16333),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS
    ),
    GRAVE_CREEPER_SHORTBOW(
        intArrayOf(15784, 16885),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS,
        AmmoType.PROMETHIUM_ARROWS
    ),
    GRAVE_CREEPER_LONGBOW(
        intArrayOf(15912, 16335),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS,
        AmmoType.PROMETHIUM_ARROWS
    ),
    SAGITTARIAN_SHORTBOW(
        intArrayOf(15785, 16887),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS,
        AmmoType.PROMETHIUM_ARROWS,
        AmmoType.SAGITTARIAN_ARROWS
    ),
    SAGITTARIAN_LONGBOW(
        intArrayOf(15913, 16337),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS,
        AmmoType.PROMETHIUM_ARROWS,
        AmmoType.SAGITTARIAN_ARROWS
    ),
    HEXHUNTER_BOW(
        intArrayOf(15836, 17295),
        AmmoType.NOVITE_ARROWS,
        AmmoType.BATHUS_ARROWS,
        AmmoType.MARMAROS_ARROWS,
        AmmoType.KRATONITE_ARROWS,
        AmmoType.FRACTITE_ARROWS,
        AmmoType.ZEPHYRIUM_ARROWS,
        AmmoType.ARGONITE_ARROWS,
        AmmoType.KATAGON_ARROWS,
        AmmoType.GORGONITE_ARROWS,
        AmmoType.PROMETHIUM_ARROWS,
        AmmoType.SAGITTARIAN_ARROWS
    ),

    OZANS_LONGBOW(intArrayOf(20859), AmmoType.OZANS_KRATONITE_ARROWS);

    var ammos: Set<AmmoType>? = null
        private set
    var isThrown: Boolean = false
        private set
    val ids: IntArray
    private val projAnim: Int
    private val drawbackSpotAnim: Int

    constructor(thrown: Boolean, itemIds: IntArray, projAnim: Int, drawbackSpotAnim: Int = -1) {
        this.isThrown = thrown
        this.ids = itemIds
        this.projAnim = projAnim
        this.drawbackSpotAnim = drawbackSpotAnim
    }

    constructor(itemIds: IntArray, projAnim: Int, drawbackSpotAnim: Int) {
        this.ids = itemIds
        this.projAnim = projAnim
        this.drawbackSpotAnim = drawbackSpotAnim
    }

    constructor(itemIds: IntArray, vararg ammos: AmmoType) : this(false, itemIds, -1, -1) {
        this.ammos = HashSet(listOf(*ammos))
    }

    fun properAmmo(player: Player, print: Boolean): Boolean {
        if (isThrown || ammos == null) return true
        val ammo = AmmoType.forId(player.equipment.ammoId) ?: run {
            if (print) player.sendMessage("You have no ammo in your quiver!")
            return false
        }
        if (!ammos!!.contains(ammo)) {
            if (print) player.sendMessage("You cannot use that ammo with this weapon.")
            return false
        }
        return true
    }

    fun getAttackSpotAnim(ammoId: Int): SpotAnim? {
        val ammo = AmmoType.forId(ammoId)
        when (this) {
            DARK_BOW -> {
                return SpotAnim(ammo!!.getDoubleDrawbackSpotAnim(ammoId), 0, 100)
            }

            HAND_CANNON -> {
                return SpotAnim(ammo!!.getDrawbackSpotAnim(ammoId))
            }

            else -> {
                if (isThrown || ammos == null) return SpotAnim(drawbackSpotAnim, 0, 100)
                if (ammo != null) return SpotAnim(ammo.getDrawbackSpotAnim(ammoId), 0, 100)
            }
        }
        return null
    }

    fun sendProjectile(player: Entity, target: Entity, attackSpeed: Int, ammoId: Int): WorldProjectile {
        when (this) {
            SLING -> return World.sendProjectile(player, target, projAnim, 30, 5, 20)
            else -> {
                if (isThrown) return World.sendProjectile(player, target, projAnim, 5 + (attackSpeed * 5), 5, 20)
                if (ammos == null) return World.sendProjectile(player, target, projAnim, 35, 5, 20)
                val ammo = AmmoType.forId(ammoId)!!
                return World.sendProjectile(player, target, ammo.getProjAnim(ammoId), 40, 5, 20)
            }
        }
    }

    companion object {
        private val MAP: Map<Int, RangedWeapon> = entries
            .flatMap { entry -> entry.ids.map { id -> id to entry } }
            .toMap()

        @JvmStatic
        fun forId(itemId: Int): RangedWeapon? = MAP[itemId]
    }
}
