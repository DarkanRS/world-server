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

import com.rs.cache.loaders.ItemDefinitions

enum class AmmoType(
    private val itemIds: IntArray,
    private val projAnim: Int,
    private val drawbackSpotAnim: Int = -1,
    private val doubleDrawbackSpotAnim: Int = -1
) {
    ICE_ARROWS(intArrayOf(78), 16, 25, 1110),
    BRONZE_ARROW(intArrayOf(882, 883, 5616, 5622, 22336, 598, 942), 10, 19, 1104),
    IRON_ARROW(intArrayOf(884, 885, 5617, 5623, 2532, 2533), 9, 18, 1105),
    STEEL_ARROW(intArrayOf(886, 887, 5618, 5624, 2534, 2535), 11, 20, 1106),
    MITHRIL_ARROW(intArrayOf(888, 889, 5619, 5625, 2536, 2537), 12, 21, 1107),
    ADAMANT_ARROW(intArrayOf(890, 891, 5620, 5626, 2538, 2539), 13, 22, 1108),
    RUNE_ARROW(intArrayOf(892, 893, 5621, 5627, 22337, 2540, 2541), 15, 24, 1109),
    DRAGON_ARROW(intArrayOf(11212, 11227, 11228, 11229, 11217, 11222), 1120, 1116, 1111),
    BROAD_ARROW(intArrayOf(4160), 326, 325, 1112),

    ABYSSALBANE_ARROW(intArrayOf(21655, 21733, 21734, 21735), 12, 21, 1107),
    DRAGONBANE_ARROW(intArrayOf(21640, 21712, 21713, 21714), 12, 21, 1107),
    BASILISKBANE_ARROW(intArrayOf(21650, 21719, 21720, 21721), 12, 21, 1107),
    WALLASALKIBANE_ARROW(intArrayOf(21645, 21726, 21727, 21728), 12, 21, 1107),

    SARADOMIN_ARROWS(intArrayOf(19152), 99, 96, 125),
    GUTHIX_ARROWS(intArrayOf(19157), 98, 95, 124),
    ZAMORAK_ARROWS(intArrayOf(19162), 100, 97, 126),

    BRONZE_BOLTS(intArrayOf(877, 878, 879, 881, 6061, 6062, 9236), 27, 955),
    BLURITE_BOLTS(intArrayOf(9139, 9237, 9286, 9293, 9300, 9335), 27, 955),
    SILVER_BOLTS(intArrayOf(9145, 9292, 9299, 9306), 27, 955),
    IRON_BOLTS(intArrayOf(9140, 880, 9238, 9287, 9294, 9301), 27, 955),
    STEEL_BOLTS(intArrayOf(9141, 9239, 9288, 9295, 9302, 9336), 27, 955),
    BLACK_BOLTS(intArrayOf(13083, 13084, 13085, 13086), 27, 955),
    MITHRIL_BOLTS(intArrayOf(9142, 9240, 9241, 9289, 9296, 9303, 9337, 9338), 27, 955),
    ADAMANT_BOLTS(intArrayOf(9143, 9242, 9243, 9290, 9297, 9304, 9339, 9340), 27, 955),
    RUNITE_BOLTS(intArrayOf(9144, 9244, 9245, 9291, 9298, 9305, 9341, 9342), 27, 955),
    BROAD_TIPPED_BOLTS(intArrayOf(13280), 27, 955),

    ABYSSALBANE_BOLT(intArrayOf(21675, 21701, 21702, 21703), 27, 955),
    DRAGONBANE_BOLT(intArrayOf(21660, 21680, 21681, 21682), 27, 955),
    BASILISKBANE_BOLT(intArrayOf(21670, 21687, 21688, 21689), 27, 955),
    WALLASALKIBANE_BOLT(intArrayOf(21665, 21694, 21695, 21696), 27, 955),

    BAKRIMINEL_BOLTS(intArrayOf(24116), 3023),

    OGRE_ARROW(intArrayOf(2866), 242, 243),
    BRONZE_BRUTAL(intArrayOf(4773), 404, 403),
    IRON_BRUTAL(intArrayOf(4778), 404, 403),
    STEEL_BRUTAL(intArrayOf(4783), 404, 403),
    BLACK_BRUTAL(intArrayOf(4788), 404, 403),
    MITHRIL_BRUTAL(intArrayOf(4793), 404, 403),
    ADAMANT_BRUTAL(intArrayOf(4798), 404, 403),
    RUNE_BRUTAL(intArrayOf(4803), 404, 403),

    GUAM_TAR(intArrayOf(10142), -1, 952),
    MARRENTILL_TAR(intArrayOf(10143), -1, 952),
    TARROMIN_TAR(intArrayOf(10144), -1, 952),
    HARRALANDER_TAR(intArrayOf(10145), -1, 952),

    BOLT_RACK(intArrayOf(4740), 27, 955),

    BONE_BOLTS(intArrayOf(8882), 696, 697),

    TRAINING_ARROWS(intArrayOf(9706), 805, 806),

    KEBBIT_BOLTS(intArrayOf(10158), 27, 955),
    LONG_KEBBIT_BOLTS(intArrayOf(10159), 27, 955),

    PERFORMANCE_ARROW(intArrayOf(13722), 1802, 1801),

    ARROWS_CLASS_1(intArrayOf(14202), 1877, 1872),
    ARROWS_CLASS_2(intArrayOf(14203), 1878, 1873),
    ARROWS_CLASS_3(intArrayOf(14204), 1879, 1874),
    ARROWS_CLASS_4(intArrayOf(14205), 1880, 1875),
    ARROWS_CLASS_5(intArrayOf(14206), 1881, 1876),

    HAND_CANNON_SHOT(intArrayOf(15243), 2143, 2137),

    NOVITE_ARROWS(intArrayOf(15947, 15958, 15969, 15980, 16427, 16482, 16537, 16592), 2467, 2466),
    BATHUS_ARROWS(intArrayOf(15948, 15959, 15970, 15981, 16432, 16487, 16542, 16597), 2469, 2468),
    MARMAROS_ARROWS(intArrayOf(15949, 15960, 15971, 15982, 16437, 16492, 16547, 16602), 2471, 2470),
    KRATONITE_ARROWS(intArrayOf(15950, 15961, 15972, 15983, 16442, 16497, 16552, 16607), 2473, 2472),
    FRACTITE_ARROWS(intArrayOf(15951, 15962, 15973, 15984, 16447, 16502, 16557, 16612), 2475, 2474),
    ZEPHYRIUM_ARROWS(intArrayOf(15952, 15963, 15974, 15985, 16452, 16507, 16562, 16617), 2477, 2476),
    ARGONITE_ARROWS(intArrayOf(15953, 15964, 15975, 15986, 16457, 16512, 16567, 16622), 2479, 2478),
    KATAGON_ARROWS(intArrayOf(15954, 15965, 15976, 15987, 16462, 16517, 16572, 16627), 2481, 2480),
    GORGONITE_ARROWS(intArrayOf(15955, 15966, 15977, 15988, 16467, 16522, 16577, 16632), 2483, 2482),
    PROMETHIUM_ARROWS(intArrayOf(15956, 15967, 15978, 15989, 16472, 16527, 16582, 16637), 2485, 2484),
    SAGITTARIAN_ARROWS(intArrayOf(15957, 15968, 15979, 15990, 16477, 16532, 16587, 16642), 2487, 2486),

    OZANS_KRATONITE_ARROWS(intArrayOf(20858), 2473, 2472),

    CORAL_BOLTS(intArrayOf(24304), 3172, 3171),
    ROYAL_BOLTS(intArrayOf(24336), 3173, 3170);

    fun getProjAnim(itemId: Int): Int {
        when (this) {
            NOVITE_ARROWS, BATHUS_ARROWS, MARMAROS_ARROWS, KRATONITE_ARROWS, FRACTITE_ARROWS, ZEPHYRIUM_ARROWS, ARGONITE_ARROWS, KATAGON_ARROWS, GORGONITE_ARROWS, PROMETHIUM_ARROWS, SAGITTARIAN_ARROWS -> {
                if (ItemDefinitions.getDefs(itemId).name.contains("(p")) return projAnim + 20
                if (ItemDefinitions.getDefs(itemId).name.contains("fire")) return 17
            }

            BRONZE_ARROW, IRON_ARROW, STEEL_ARROW, MITHRIL_ARROW, ADAMANT_ARROW, RUNE_ARROW, DRAGON_ARROW ->
                if (ItemDefinitions.getDefs(itemId).name.contains("fire")) return 17 else -> {}
        }
        return projAnim
    }

    fun getDrawbackSpotAnim(itemId: Int): Int {
        when (this) {
            NOVITE_ARROWS, BATHUS_ARROWS, MARMAROS_ARROWS, KRATONITE_ARROWS, FRACTITE_ARROWS, ZEPHYRIUM_ARROWS, ARGONITE_ARROWS, KATAGON_ARROWS, GORGONITE_ARROWS, PROMETHIUM_ARROWS, SAGITTARIAN_ARROWS -> {
                if (ItemDefinitions.getDefs(itemId).name.contains("(p")) return drawbackSpotAnim + 20
                if (ItemDefinitions.getDefs(itemId).name.contains("fire")) return 26
            }

            BRONZE_ARROW, IRON_ARROW, STEEL_ARROW, MITHRIL_ARROW, ADAMANT_ARROW, RUNE_ARROW, DRAGON_ARROW ->
                if (ItemDefinitions.getDefs(itemId).name.contains("fire")) return 26 else -> {}
        }
        return drawbackSpotAnim
    }

    fun getDoubleDrawbackSpotAnim(itemId: Int): Int {
        when (this) {
            BRONZE_ARROW, IRON_ARROW, STEEL_ARROW, MITHRIL_ARROW, ADAMANT_ARROW, RUNE_ARROW, DRAGON_ARROW ->
                if (ItemDefinitions.getDefs(itemId).name.contains("fire")) return 1113 else -> {}
        }
        return doubleDrawbackSpotAnim
    }

    companion object {
        private val MAP: Map<Int, AmmoType> = entries.flatMap { entry -> entry.itemIds.map { itemId -> itemId to entry } }.toMap()

        @JvmStatic
        fun forId(itemId: Int): AmmoType? = MAP[itemId]
    }
}
