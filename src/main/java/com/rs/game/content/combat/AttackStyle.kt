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

data class AttackStyle(@JvmField val index: Int, @JvmField val name: String, @JvmField val xpType: XPType, @JvmField val attackType: AttackType) {
    companion object {
        val UNARMED: Map<Int, AttackStyle> = mapOf(
            0 to AttackStyle(0, "Punch", XPType.ACCURATE, AttackType.CRUSH),
            1 to AttackStyle(1, "Kick", XPType.AGGRESSIVE, AttackType.CRUSH),
            2 to AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
        )
        private val ATTACK_STYLES: Map<Int, Map<Int, AttackStyle>?> = mapOf(
            1 to mapOf(
                0 to AttackStyle(0, "Bash", XPType.ACCURATE, AttackType.CRUSH),
                1 to AttackStyle(1, "Pound", XPType.AGGRESSIVE, AttackType.CRUSH),
                2 to AttackStyle(2, "Focus", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            2 to mapOf(
                0 to AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Hack", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Smash", XPType.AGGRESSIVE, AttackType.CRUSH),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
            ),
            3 to mapOf(
                0 to AttackStyle(0, "Bash", XPType.ACCURATE, AttackType.CRUSH),
                1 to AttackStyle(1, "Pound", XPType.AGGRESSIVE, AttackType.CRUSH),
                2 to AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            4 to mapOf(
                0 to AttackStyle(0, "Spike", XPType.ACCURATE, AttackType.STAB),
                1 to AttackStyle(1, "Impale", XPType.AGGRESSIVE, AttackType.STAB),
                2 to AttackStyle(2, "Smash", XPType.AGGRESSIVE, AttackType.CRUSH),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
            ),
            5 to mapOf(
                0 to AttackStyle(0, "Stab", XPType.ACCURATE, AttackType.STAB),
                1 to AttackStyle(1, "Lunge", XPType.AGGRESSIVE, AttackType.STAB),
                2 to AttackStyle(2, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
            ),
            6 to mapOf(
                0 to AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Lunge", XPType.CONTROLLED, AttackType.STAB),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
            ),
            7 to mapOf(
                0 to AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Smash", XPType.AGGRESSIVE, AttackType.CRUSH),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
            ),
            8 to mapOf(
                0 to AttackStyle(0, "Pound", XPType.ACCURATE, AttackType.CRUSH),
                1 to AttackStyle(1, "Pummel", XPType.AGGRESSIVE, AttackType.CRUSH),
                2 to AttackStyle(2, "Spike", XPType.CONTROLLED, AttackType.STAB),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            9 to mapOf(
                0 to AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Lunge", XPType.CONTROLLED, AttackType.STAB),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
            ),
            10 to mapOf(
                0 to AttackStyle(0, "Pound", XPType.ACCURATE, AttackType.CRUSH),
                1 to AttackStyle(1, "Pummel", XPType.AGGRESSIVE, AttackType.CRUSH),
                2 to AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            11 to mapOf(
                0 to AttackStyle(0, "Flick", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Lash", XPType.CONTROLLED, AttackType.SLASH),
                2 to AttackStyle(2, "Deflect", XPType.DEFENSIVE, AttackType.SLASH)
            ),
            12 to mapOf(
                0 to AttackStyle(0, "Pound", XPType.ACCURATE, AttackType.CRUSH),
                1 to AttackStyle(1, "Pummel", XPType.AGGRESSIVE, AttackType.CRUSH),
                2 to AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            13 to mapOf(
                0 to AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
                1 to AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
                2 to AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
            ),
            14 to mapOf(
                0 to AttackStyle(0, "Lunge", XPType.CONTROLLED, AttackType.STAB),
                1 to AttackStyle(1, "Swipe", XPType.CONTROLLED, AttackType.SLASH),
                2 to AttackStyle(2, "Pound", XPType.CONTROLLED, AttackType.CRUSH),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
            ),
            15 to mapOf(
                0 to AttackStyle(0, "Jab", XPType.CONTROLLED, AttackType.STAB),
                1 to AttackStyle(1, "Swipe", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Fend", XPType.DEFENSIVE, AttackType.STAB)
            ),
            16 to mapOf(
                0 to AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
                1 to AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
                2 to AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
            ),
            17 to mapOf(
                0 to AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
                1 to AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
                2 to AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
            ),
            18 to mapOf(
                0 to AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
                1 to AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
                2 to AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
            ),
            19 to mapOf(
                0 to AttackStyle(0, "Short fuse", XPType.RANGED, AttackType.ACCURATE),
                1 to AttackStyle(1, "Medium fuse", XPType.RANGED, AttackType.RAPID),
                2 to AttackStyle(2, "Long fuse", XPType.RANGED, AttackType.LONG_RANGE)
            ),
            20 to mapOf(
                0 to AttackStyle(0, "Aim and fire", XPType.RANGED, AttackType.ACCURATE),
                2 to AttackStyle(2, "Kick", XPType.AGGRESSIVE, AttackType.CRUSH)
            ),
            21 to mapOf(
                0 to AttackStyle(0, "Scorch", XPType.AGGRESSIVE, AttackType.SLASH),
                1 to AttackStyle(1, "Flare", XPType.RANGED, AttackType.ACCURATE),
                2 to AttackStyle(2, "Blaze", XPType.MAGIC, AttackType.MAGIC)
            ),
            22 to mapOf(
                0 to AttackStyle(0, "Reap", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Chop", XPType.AGGRESSIVE, AttackType.STAB),
                2 to AttackStyle(2, "Jab", XPType.AGGRESSIVE, AttackType.CRUSH),
                3 to AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
            ),
            23 to mapOf(
                0 to AttackStyle(0, "Slash", XPType.ACCURATE, AttackType.SLASH),
                1 to AttackStyle(1, "Crush", XPType.AGGRESSIVE, AttackType.CRUSH),
                2 to AttackStyle(2, "Slash", XPType.DEFENSIVE, AttackType.SLASH)
            ),
            24 to mapOf(
                0 to AttackStyle(0, "Sling", XPType.RANGED, AttackType.ACCURATE),
                1 to AttackStyle(1, "Chuck", XPType.RANGED, AttackType.RAPID),
                2 to AttackStyle(2, "Lob", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
            ),
            25 to mapOf(
                0 to AttackStyle(0, "Jab", XPType.ACCURATE, AttackType.STAB),
                1 to AttackStyle(1, "Swipe", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Fend", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            26 to mapOf(
                0 to AttackStyle(0, "Jab", XPType.ACCURATE, AttackType.STAB),
                1 to AttackStyle(1, "Swipe", XPType.AGGRESSIVE, AttackType.SLASH),
                2 to AttackStyle(2, "Fend", XPType.DEFENSIVE, AttackType.CRUSH)
            ),
            27 to mapOf(
                0 to AttackStyle(0, "Hack!", XPType.CONTROLLED, AttackType.SLASH),
                1 to AttackStyle(1, "Gouge!", XPType.CONTROLLED, AttackType.STAB),
                2 to AttackStyle(2, "Smash!", XPType.CONTROLLED, AttackType.CRUSH)
            ),
            28 to mapOf(
                0 to AttackStyle(0, "Accurate", XPType.MAGIC, AttackType.POLYPORE_ACCURATE),
                2 to AttackStyle(2, "Long range", XPType.MAGIC, AttackType.POLYPORE_LONGRANGE)
            ),
        )

        fun getStyles(itemId: Int): Map<Int, AttackStyle> {
            val defs = ItemDefinitions.getDefs(itemId)
            val weaponType = defs.getParamVal(686)
            return ATTACK_STYLES[weaponType] ?: UNARMED
        }
    }
}
