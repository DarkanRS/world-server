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
package com.rs.game.content

import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.World.getServerTicks
import com.rs.game.content.world.areas.wilderness.WildernessController
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.Skills.*
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onItemOnItem
import com.rs.utils.Ticks
import java.util.*
import kotlin.math.floor

const val VIAL: Int = 229
const val FLASK: Int = 23191
const val JUJU_VIAL: Int = 19996
const val BEER_GLASS: Int = 1919
const val EMPTY_KEG: Int = 5769
private const val EMPTY_CUP = 4244
private const val BOWL = 1923
private const val EMPTY_JUG = 1935

@ServerStartupEvent
fun mapPotionOps() {
    onItemClick(*Potion.POTS.keys.toTypedArray(), options = arrayOf("Drink", "Empty")) { e ->
        val pot = Potion.forId(e.item.id) ?: return@onItemClick
        if (e.option == "Drink") pot.drink(e.player, e.item.id, e.item.slot)
        else if (e.option == "Empty" && pot.emptyId != -1) {
            e.item.id = pot.emptyId
            e.player.inventory.refresh(e.item.slot)
        }
    }

    onItemOnItem(used = Potion.POTS.keys.toIntArray()) { e ->
        fun getDoses(pot: Potion, item: Item): Int {
            for (i in pot.ids.indices.reversed()) if (pot.ids[i] == item.id) return pot.ids.size - i
            return 0
        }

        val fromItem = e.item1
        val toItem = e.item2
        val fromSlot = fromItem.slot
        val toSlot = toItem.slot
        if (fromItem.id == VIAL || toItem.id == VIAL) {
            val pot = Potion.forId(if (fromItem.id == VIAL) toItem.id else fromItem.id)
            if (pot == null || pot.emptyId == -1) return@onItemOnItem
            var doses = getDoses(pot, if (fromItem.id == VIAL) toItem else fromItem)
            if (doses == 1) {
                e.player.inventory.switchItem(fromSlot, toSlot)
                e.player.sendMessage("You combine the potions.", true)
                return@onItemOnItem
            }
            val vialDoses = doses / 2
            doses -= vialDoses
            e.player.inventory.items[if (fromItem.id == VIAL) toSlot else fromSlot] = Item(pot.getIdForDoses(doses), 1)
            e.player.inventory.items[if (fromItem.id == VIAL) fromSlot else toSlot] = Item(pot.getIdForDoses(vialDoses), 1)
            e.player.inventory.refresh(fromSlot)
            e.player.inventory.refresh(toSlot)
            e.player.sendMessage("You split the potion between the two vials.", true)
            return@onItemOnItem
        }
        val pot = Potion.forId(fromItem.id) ?: return@onItemOnItem
        var doses2 = getDoses(pot, toItem)
        if (doses2 == 0 || doses2 == pot.maxDoses) return@onItemOnItem
        var doses1 = getDoses(pot, fromItem)
        doses2 += doses1
        doses1 = if (doses2 > pot.maxDoses) doses2 - pot.maxDoses else 0
        doses2 -= doses1
        if (doses1 == 0 && pot.emptyId == -1) e.player.inventory.deleteItem(fromSlot, fromItem)
        else {
            e.player.inventory.items[fromSlot] = Item(if (doses1 > 0) pot.getIdForDoses(doses1) else pot.emptyId, 1)
            e.player.inventory.refresh(fromSlot)
        }
        e.player.inventory.items[toSlot] = Item(pot.getIdForDoses(doses2), 1)
        e.player.inventory.refresh(toSlot)
        e.player.sendMessage("You pour from one container into the other" + (if (pot.emptyId == -1 && doses1 == 0) " and the flask shatters to pieces." else "."))
    }
}

enum class Potion(val emptyId: Int, val ids: IntArray, val effect: (Player) -> Unit) {
    CHOCOLATEY_MILK(1925, 1977, { it.heal(40) }),
    BUCKET_OF_MILK(1925, 1927, { }),
    CUP_OF_TEA(1980, intArrayOf(712, 1978, 4242, 4243, 4245, 4246, 4838, 7730, 7731, 7733, 7734, 7736, 7737), { p ->
        p.heal(30)
        p.skills.adjustStat(3, 0.0, ATTACK)
        p.forceTalk("Aaah, nothing like a nice cuppa tea!")
    }),
    CUP_OF_TEA_CLAY(7728, 7730, { it.skills.adjustStat(1, 0.0, CONSTRUCTION) }),
    CUP_OF_TEA_CLAY_MILK(7728, 7731, { it.skills.adjustStat(1, 0.0, CONSTRUCTION) }),
    CUP_OF_TEA_PORCELAIN(7732, 7733, { it.skills.adjustStat(2, 0.0, CONSTRUCTION) }),
    CUP_OF_TEA_PORCELAIN_MILK(7732, 7734, { it.skills.adjustStat(2, 0.0, CONSTRUCTION) }),
    CUP_OF_TEA_GOLD(7735, 7736, { it.skills.adjustStat(3, 0.0, CONSTRUCTION) }),
    CUP_OF_TEA_GOLD_MILK(7735, 7737, { it.skills.adjustStat(3, 0.0, CONSTRUCTION) }),
    NETTLE_TEA_CUP(EMPTY_CUP, 4245, { p ->
        p.restoreRunEnergy(5.0)
        p.heal(30)
    }),
    NETTLE_TEA_CUP_MILK(EMPTY_CUP, 4246, { p ->
        p.restoreRunEnergy(5.0)
        p.heal(30)
    }),
    NETTLE_TEA_BOWL(BOWL, 4239, { p ->
        p.restoreRunEnergy(5.0)
        p.heal(30)
    }),
    NETTLE_TEA_BOWL_MILK(BOWL, 4240, { p ->
        p.restoreRunEnergy(5.0)
        p.heal(30)
    }),
    NETTLE_WATER(BOWL, 4237, { it.heal(10) }),
    ATTACK_POTION(VIAL, intArrayOf(2428, 121, 123, 125), { it.skills.adjustStat(3, 0.1, ATTACK) }),
    ATTACK_FLASK(-1, intArrayOf(23195, 23197, 23199, 23201, 23203, 23205), { it.skills.adjustStat(3, 0.1, ATTACK) }),
    ATTACK_MIX(VIAL, intArrayOf(11429, 11431), { p ->
        p.skills.adjustStat(3, 0.1, ATTACK)
        p.heal(30)
    }),

    STRENGTH_POTION(VIAL, intArrayOf(113, 115, 117, 119), { it.skills.adjustStat(3, 0.1, STRENGTH) }),
    STRENGTH_FLASK(-1, intArrayOf(23207, 23209, 23211, 23213, 23215, 23217), { it.skills.adjustStat(3, 0.1, STRENGTH) }),
    STRENGTH_MIX(VIAL, intArrayOf(11443, 11441), { p ->
        p.skills.adjustStat(3, 0.1, STRENGTH)
        p.heal(30)
    }),

    DEFENCE_POTION(VIAL, intArrayOf(2432, 133, 135, 137), { it.skills.adjustStat(3, 0.1, DEFENSE) }),
    DEFENCE_FLASK(-1, intArrayOf(23231, 23233, 23235, 23237, 23239, 23241), { it.skills.adjustStat(3, 0.1, DEFENSE) }),
    DEFENCE_MIX(VIAL, intArrayOf(11457, 11459), { p ->
        p.skills.adjustStat(3, 0.1, DEFENSE)
        p.heal(30)
    }),

    COMBAT_POTION(VIAL, intArrayOf(9739, 9741, 9743, 9745), { it.skills.adjustStat(3, 0.1, ATTACK, STRENGTH, DEFENSE) }),
    COMBAT_FLASK(-1, intArrayOf(23447, 23449, 23451, 23453, 23455, 23457), { it.skills.adjustStat(3, 0.1, ATTACK, STRENGTH, DEFENSE) }),
    COMBAT_MIX(VIAL, intArrayOf(11445, 11447), { p ->
        p.skills.adjustStat(3, 0.1, ATTACK, STRENGTH, DEFENSE)
        p.heal(30)
    }),

    SUPER_ATTACK(VIAL, intArrayOf(2436, 145, 147, 149), { it.skills.adjustStat(5, 0.15, ATTACK) }),
    SUPER_ATTACK_FLASK(-1, intArrayOf(23255, 23257, 23259, 23261, 23263, 23265), { it.skills.adjustStat(5, 0.15, ATTACK) }),
    CW_SUPER_ATTACK_POTION(-1, intArrayOf(18715, 18716, 18717, 18718), { p ->
        p.skills.adjustStat(5, 0.15, ATTACK, STRENGTH, DEFENSE)
        p.skills.adjustStat(4, 0.10, RANGE)
        p.skills.adjustStat(5, 0.0, MAGIC)
    }),
    SUPER_ATTACK_MIX(VIAL, intArrayOf(11469, 11471), { p ->
        p.skills.adjustStat(5, 0.15, ATTACK)
        p.heal(30)
    }),

    SUPER_STRENGTH(VIAL, intArrayOf(2440, 157, 159, 161), { it.skills.adjustStat(5, 0.15, STRENGTH) }),
    SUPER_STRENGTH_FLASK(-1, intArrayOf(23279, 23281, 23283, 23285, 23287, 23289), { it.skills.adjustStat(5, 0.15, STRENGTH) }),
    CW_SUPER_STRENGTH_POTION(-1, intArrayOf(18719, 18720, 18721, 18722), { it.skills.adjustStat(5, 0.15, STRENGTH) }),
    SUPER_STRENGTH_MIX(VIAL, intArrayOf(11485, 11487), { p ->
        p.skills.adjustStat(5, 0.15, STRENGTH)
        p.heal(30)
    }),

    SUPER_DEFENCE(VIAL, intArrayOf(2442, 163, 165, 167), { it.skills.adjustStat(5, 0.15, DEFENSE) }),
    SUPER_DEFENCE_FLASK(-1, intArrayOf(23291, 23293, 23295, 23297, 23299, 23301), { it.skills.adjustStat(5, 0.15, DEFENSE) }),
    CW_SUPER_DEFENCE_POTION(-1, intArrayOf(18723, 18724, 18725, 18726), { it.skills.adjustStat(5, 0.15, DEFENSE) }),
    SUPER_DEFENCE_MIX(VIAL, intArrayOf(11497, 11499), { p ->
        p.skills.adjustStat(5, 0.15, DEFENSE)
        p.heal(30)
    }),

    RANGING_POTION(VIAL, intArrayOf(2444, 169, 171, 173), { it.skills.adjustStat(4, 0.10, RANGE) }),
    RANGING_FLASK(-1, intArrayOf(23303, 23305, 23307, 23309, 23311, 23313), { it.skills.adjustStat(4, 0.10, RANGE) }),
    CW_SUPER_RANGING_POTION(-1, intArrayOf(18731, 18732, 18733, 18734), { it.skills.adjustStat(4, 0.10, RANGE) }),
    RANGING_MIX(VIAL, intArrayOf(11509, 11511), { p ->
        p.skills.adjustStat(4, 0.10, RANGE)
        p.heal(30)
    }),

    MAGIC_POTION(VIAL, intArrayOf(3040, 3042, 3044, 3046), { it.skills.adjustStat(5, 0.0, MAGIC) }),
    MAGIC_FLASK(-1, intArrayOf(23423, 23425, 23427, 23429, 23431, 23433), { it.skills.adjustStat(5, 0.0, MAGIC) }),
    CW_SUPER_MAGIC_POTION(-1, intArrayOf(18735, 18736, 18737, 18738), { it.skills.adjustStat(5, 0.0, MAGIC) }),
    MAGIC_MIX(VIAL, intArrayOf(11513, 11515), { p ->
        p.skills.adjustStat(5, 0.0, MAGIC)
        p.heal(30)
    }),

    RESTORE_POTION(VIAL, intArrayOf(2430, 127, 129, 131), { it.skills.adjustStat(10, 0.3, false, ATTACK, STRENGTH, DEFENSE, RANGE, MAGIC) }),
    RESTORE_FLASK(-1, intArrayOf(23219, 23221, 23223, 23225, 23227, 23229), { it.skills.adjustStat(10, 0.3, false, ATTACK, STRENGTH, DEFENSE, RANGE, MAGIC) }),
    RESTORE_MIX(VIAL, intArrayOf(11449, 11451), { p ->
        p.skills.adjustStat(10, 0.3, false, ATTACK, STRENGTH, DEFENSE, RANGE, MAGIC)
        p.heal(30)
    }),

    PRAYER_POTION(VIAL, intArrayOf(2434, 139, 141, 143), {
        it.prayer.restorePrayer((floor(it.skills.getLevelForXp(PRAYER) * 2.5) + 70).toInt().toDouble())
    }),
    PRAYER_FLASK(-1, intArrayOf(23243, 23245, 23247, 23249, 23251, 23253), {
        it.prayer.restorePrayer((floor(it.skills.getLevelForXp(PRAYER) * 2.5) + 70).toInt().toDouble())
    }),
    PRAYER_MIX(VIAL, intArrayOf(11465, 11467), { p ->
        p.prayer.restorePrayer((floor(p.skills.getLevelForXp(PRAYER) * 2.5) + 70).toInt().toDouble())
        p.heal(30)
    }),

    SUPER_RESTORE(VIAL, intArrayOf(3024, 3026, 3028, 3030), { p ->
        p.skills.adjustStat(true, 8, 0.25, false, *Utils.range(0, Skills.SIZE - 1))
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER) * 0.33 * 10).toInt().toDouble())
    }),
    SUPER_RESTORE_FLASK(-1, intArrayOf(23399, 23401, 23403, 23405, 23407, 23409), { p ->
        p.skills.adjustStat(true, 8, 0.25, false, *Utils.range(0, Skills.SIZE - 1))
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER) * 0.33 * 10).toInt().toDouble())
    }),
    DOM_SUPER_RESTORE(-1, intArrayOf(22379, 22380), { p ->
        p.skills.adjustStat(true, 8, 0.25, false, *Utils.range(0, Skills.SIZE - 1))
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER) * 0.33 * 10).toInt().toDouble())
    }),
    SUPER_RESTORE_MIX(VIAL, intArrayOf(11493, 11495), { p ->
        p.skills.adjustStat(true, 8, 0.25, false, *Utils.range(0, Skills.SIZE - 1))
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER) * 0.33 * 10).toInt().toDouble())
        p.heal(30)
    }),

    PRAYER_RENEWAL(VIAL, intArrayOf(21630, 21632, 21634, 21636), { it.addEffect(Effect.PRAYER_RENEWAL, 500) }),
    PRAYER_RENEWAL_FLASK(-1, intArrayOf(23609, 23611, 23613, 23615, 23617, 23619), { it.addEffect(Effect.PRAYER_RENEWAL, 500) }),

    ANTIPOISON(VIAL, intArrayOf(2446, 175, 177, 179), { it.addEffect(Effect.ANTIPOISON, Ticks.fromSeconds(90).toLong()) }),
    ANTIPOISON_FLASK(-1, intArrayOf(23315, 23317, 23319, 23321, 23323, 23325), { it.addEffect(Effect.ANTIPOISON, Ticks.fromSeconds(90).toLong()) }),
    ANTIPOISON_MIX(VIAL, intArrayOf(11433, 11435), { p ->
        p.addEffect(Effect.ANTIPOISON, Ticks.fromSeconds(90).toLong())
        p.heal(30)
    }),

    SUPER_ANTIPOISON(VIAL, intArrayOf(2448, 181, 183, 185), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6).toLong()) }),
    SUPER_ANTIPOISON_FLASK(-1, intArrayOf(23327, 23329, 23331, 23333, 23335, 23337), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6).toLong()) }),
    DOM_SUPER_ANTIPOISON(-1, intArrayOf(22377, 22378), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6).toLong()) }),
    ANTI_P_SUPERMIX(VIAL, intArrayOf(11473, 11475), { p ->
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6).toLong())
        p.heal(30)
    }),

    ANTIPOISONP(VIAL, intArrayOf(5943, 5945, 5947, 5949), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(9).toLong()) }),
    ANTIPOISONP_FLASK(-1, intArrayOf(23579, 23581, 23583, 23585, 23587, 23589), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(9).toLong()) }),
    ANTIDOTEP_MIX(VIAL, intArrayOf(11501, 11503), { p ->
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(9).toLong())
        p.heal(30)
    }),

    ANTIPOISONPP(VIAL, intArrayOf(5952, 5954, 5956, 5958), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(12).toLong()) }),
    ANTIPOISONPP_FLASK(-1, intArrayOf(23591, 23593, 23595, 23597, 23599, 23601), { it.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(12).toLong()) }),

    RELICYMS_BALM(VIAL, intArrayOf(4842, 4844, 4846, 4848), { /* TODO */ }),
    RELICYMS_BALM_FLASK(-1, intArrayOf(23537, 23539, 23541, 23543, 23545, 23547), { /* TODO */ }),
    RELICYMS_MIX(VIAL, intArrayOf(11437, 11439), { p ->
        //TODO
        p.heal(30)
    }),

    ZAMORAK_BREW(VIAL, intArrayOf(2450, 189, 191, 193), { p ->
        p.skills.adjustStat(2, 0.2, ATTACK)
        p.skills.adjustStat(2, 0.12, STRENGTH)
        p.skills.adjustStat(-2, -0.1, DEFENSE)
        p.applyHit(Hit(p, (p.hitpoints * 0.12).toInt(), HitLook.TRUE_DAMAGE))
    }),
    ZAMORAK_BREW_FLASK(-1, intArrayOf(23339, 23341, 23343, 23345, 23347, 23349), { p ->
        p.skills.adjustStat(2, 0.2, ATTACK)
        p.skills.adjustStat(2, 0.12, STRENGTH)
        p.skills.adjustStat(-2, -0.1, DEFENSE)
        p.applyHit(Hit(p, (p.hitpoints * 0.12).toInt(), HitLook.TRUE_DAMAGE))
    }),
    ZAMORAK_MIX(VIAL, intArrayOf(11521, 11523), { it.heal(30) }),

    ANTIFIRE(VIAL, intArrayOf(2452, 2454, 2456, 2458), { it.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(6).toLong()) }),
    ANTIFIRE_FLASK(-1, intArrayOf(23363, 23365, 23367, 23369, 23371, 23373), { it.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(6).toLong()) }),
    ANTIFIRE_MIX(VIAL, intArrayOf(11505, 11507), { p ->
        p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(6).toLong())
        p.heal(30)
    }),

    ENERGY_POTION(VIAL, intArrayOf(3008, 3010, 3012, 3014), { it.restoreRunEnergy(20.0) }),
    ENERGY_FLASK(-1, intArrayOf(23375, 23377, 23379, 23381, 23383, 23385), { it.restoreRunEnergy(20.0) }),
    ENERGY_MIX(VIAL, intArrayOf(11453, 11455), { p ->
        p.restoreRunEnergy(20.0)
        p.heal(30)
    }),

    SUPER_ENERGY(VIAL, intArrayOf(3016, 3018, 3020, 3022), { it.restoreRunEnergy(40.0) }),
    SUPER_ENERGY_FLASK(-1, intArrayOf(23387, 23389, 23391, 23393, 23395, 23397), { it.restoreRunEnergy(40.0) }),
    CW_SUPER_ENERGY_POTION(-1, intArrayOf(18727, 18728, 18729, 18730), { it.restoreRunEnergy(40.0) }),
    SUPER_ENERGY_MIX(VIAL, intArrayOf(11481, 11483), { p ->
        p.restoreRunEnergy(40.0)
        p.heal(30)
    }),

    GUTHIX_REST(VIAL, intArrayOf(4417, 4419, 4421, 4423), { p ->
        p.restoreRunEnergy(5.0)
        p.heal(50, 50)
        p.poison.lowerPoisonDamage(10)
    }),

    SARADOMIN_BREW(VIAL, intArrayOf(6685, 6687, 6689, 6691), { p ->
        val hpChange = (p.maxHitpoints * 0.15).toInt()
        p.heal(hpChange + 20, hpChange)
        p.skills.adjustStat(2, 0.2, DEFENSE)
        p.skills.adjustStat(-2, -0.1, ATTACK, STRENGTH, MAGIC, RANGE)
    }),
    SARADOMIN_BREW_FLASK(-1, intArrayOf(23351, 23353, 23355, 23357, 23359, 23361), { p ->
        val hpChange = (p.maxHitpoints * 0.15).toInt()
        p.heal(hpChange + 20, hpChange)
        p.skills.adjustStat(2, 0.2, DEFENSE)
        p.skills.adjustStat(-2, -0.1, ATTACK, STRENGTH, MAGIC, RANGE)
    }),
    DOM_SARADOMIN_BREW(-1, intArrayOf(22373, 22374), { p ->
        val hpChange = (p.maxHitpoints * 0.15).toInt()
        p.heal(hpChange + 20, hpChange)
        p.skills.adjustStat(2, 0.2, DEFENSE)
        p.skills.adjustStat(-2, -0.1, ATTACK, STRENGTH, MAGIC, RANGE)
    }),

    MAGIC_ESSENCE(VIAL, intArrayOf(9021, 9022, 9023, 9024), { it.skills.adjustStat(4, 0.0, MAGIC) }),
    MAGIC_ESSENCE_FLASK(-1, intArrayOf(23633, 23634, 23635, 23636, 23637, 23638), { it.skills.adjustStat(4, 0.0, MAGIC) }),
    MAGIC_ESSENCE_MIX(VIAL, intArrayOf(11489, 11491), { p ->
        p.skills.adjustStat(4, 0.0, MAGIC)
        p.heal(30)
    }),

    AGILITY_POTION(VIAL, intArrayOf(3032, 3034, 3036, 3038), { it.skills.adjustStat(3, 0.0, AGILITY) }),
    AGILITY_FLASK(-1, intArrayOf(23411, 23413, 23415, 23417, 23419, 23421), { it.skills.adjustStat(3, 0.0, AGILITY) }),
    AGILITY_MIX(VIAL, intArrayOf(11461, 11463), { p ->
        p.skills.adjustStat(3, 0.0, AGILITY)
        p.heal(30)
    }),

    FISHING_POTION(VIAL, intArrayOf(2438, 151, 153, 155), { it.skills.adjustStat(3, 0.0, FISHING) }),
    FISHING_FLASK(-1, intArrayOf(23267, 23269, 23271, 23273, 23275, 23277), { it.skills.adjustStat(3, 0.0, FISHING) }),
    FISHING_MIX(VIAL, intArrayOf(11477, 11479), { p ->
        p.skills.adjustStat(3, 0.0, FISHING)
        p.heal(30)
    }),

    HUNTER_POTION(VIAL, intArrayOf(9998, 10000, 10002, 10004), { it.skills.adjustStat(3, 0.0, HUNTER) }),
    HUNTER_FLASK(-1, intArrayOf(23435, 23437, 23439, 23441, 23443, 23445), { it.skills.adjustStat(3, 0.0, HUNTER) }),
    HUNTING_MIX(VIAL, intArrayOf(11517, 11519), { p ->
        p.skills.adjustStat(3, 0.0, HUNTER)
        p.heal(30)
    }),

    CRAFTING_POTION(VIAL, intArrayOf(14838, 14840, 14842, 14844), { it.skills.adjustStat(3, 0.0, CRAFTING) }),
    CRAFTING_FLASK(-1, intArrayOf(23459, 23461, 23463, 23465, 23467, 23469), { it.skills.adjustStat(3, 0.0, CRAFTING) }),

    FLETCHING_POTION(VIAL, intArrayOf(14846, 14848, 14850, 14852), { it.skills.adjustStat(3, 0.0, FLETCHING) }),
    FLETCHING_FLASK(-1, intArrayOf(23471, 23473, 23475, 23477, 23479, 23481), { it.skills.adjustStat(3, 0.0, FLETCHING) }),

    SANFEW_SERUM(VIAL, intArrayOf(10925, 10927, 10929, 10931), { p ->
        p.skills.adjustStat(8, 0.25, false, *Utils.range(0, Skills.SIZE - 1))
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER) * 0.33 * 10).toInt().toDouble())
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6).toLong())
    }),
    SANFEW_SERUM_FLASK(-1, intArrayOf(23567, 23569, 23571, 23573, 23575, 23577), { p ->
        p.skills.adjustStat(8, 0.25, false, *Utils.range(0, Skills.SIZE - 1))
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER) * 0.33 * 10).toInt().toDouble())
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6).toLong())
    }),

    SUMMONING_POTION(VIAL, intArrayOf(12140, 12142, 12144, 12146), { p ->
        p.skills.adjustStat(7, 0.25, false, SUMMONING)
        val familiar = p.familiar
        familiar?.restoreSpecialAttack(15)
    }),
    SUMMONING_FLASK(-1, intArrayOf(23621, 23623, 23625, 23627, 23629, 23631), { p ->
        p.skills.adjustStat(7, 0.25, false, SUMMONING)
        val familiar = p.familiar
        familiar?.restoreSpecialAttack(15)
    }),

    RECOVER_SPECIAL(VIAL, intArrayOf(15300, 15301, 15302, 15303), { p ->
        p.tempAttribs.setL("recSpecPot", getServerTicks())
        p.combatDefinitions.restoreSpecialAttack(25)
    }) {
        override fun canDrink(player: Player): Boolean {
            if (getServerTicks() - player.tempAttribs.getL("recSpecPot") < 50) {
                player.sendMessage("You may only use this pot every 30 seconds.")
                return false
            }
            return true
        }
    },
    RECOVER_SPECIAL_FLASK(-1, intArrayOf(23483, 23484, 23485, 23486, 23487, 23488), { p ->
        p.tempAttribs.setL("recSpecPot", getServerTicks())
        p.combatDefinitions.restoreSpecialAttack(25)
    }) {
        override fun canDrink(player: Player): Boolean {
            if (getServerTicks() - player.tempAttribs.getL("recSpecPot") < 50) {
                player.sendMessage("You may only use this pot every 30 seconds.")
                return false
            }
            return true
        }
    },

    SUPER_ANTIFIRE(VIAL, intArrayOf(15304, 15305, 15306, 15307), { it.addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromMinutes(6).toLong()) }),
    SUPER_ANTIFIRE_FLASK(-1, intArrayOf(23489, 23490, 23491, 23492, 23493, 23494), { it.addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromMinutes(6).toLong()) }),

    EXTREME_ATTACK(VIAL, intArrayOf(15308, 15309, 15310, 15311), { it.skills.adjustStat(5, 0.22, ATTACK) }),
    EXTREME_ATTACK_FLASK(-1, intArrayOf(23495, 23496, 23497, 23498, 23499, 23500), { it.skills.adjustStat(5, 0.22, ATTACK) }),

    EXTREME_STRENGTH(VIAL, intArrayOf(15312, 15313, 15314, 15315), { it.skills.adjustStat(5, 0.22, STRENGTH) }),
    EXTREME_STRENGTH_FLASK(-1, intArrayOf(23501, 23502, 23503, 23504, 23505, 23506), { it.skills.adjustStat(5, 0.22, STRENGTH) }),

    EXTREME_DEFENCE(VIAL, intArrayOf(15316, 15317, 15318, 15319), { it.skills.adjustStat(5, 0.22, DEFENSE) }),
    EXTREME_DEFENCE_FLASK(-1, intArrayOf(23507, 23508, 23509, 23510, 23511, 23512), { it.skills.adjustStat(5, 0.22, DEFENSE) }),

    EXTREME_MAGIC(VIAL, intArrayOf(15320, 15321, 15322, 15323), { it.skills.adjustStat(7, 0.0, MAGIC) }),
    EXTREME_MAGIC_FLASK(-1, intArrayOf(23513, 23514, 23515, 23516, 23517, 23518), { it.skills.adjustStat(7, 0.0, MAGIC) }),

    EXTREME_RANGING(VIAL, intArrayOf(15324, 15325, 15326, 15327), { it.skills.adjustStat(4, 0.2, RANGE) }),
    EXTREME_RANGING_FLASK(-1, intArrayOf(23519, 23520, 23521, 23522, 23523, 23524), { it.skills.adjustStat(4, 0.2, RANGE) }),

    SUPER_PRAYER(VIAL, intArrayOf(15328, 15329, 15330, 15331), {
        it.prayer.restorePrayer((70 + (it.skills.getLevelForXp(PRAYER) * 3.43)).toInt().toDouble())
    }),
    SUPER_PRAYER_FLASK(-1, intArrayOf(23525, 23526, 23527, 23528, 23529, 23530), {
        it.prayer.restorePrayer((70 + (it.skills.getLevelForXp(PRAYER) * 3.43)).toInt().toDouble())
    }),
    DOM_SUPER_PRAYER(-1, intArrayOf(22375, 22376), {
        it.prayer.restorePrayer((70 + (it.skills.getLevelForXp(PRAYER) * 3.43)).toInt().toDouble())
    }),

    OVERLOAD(VIAL, intArrayOf(15332, 15333, 15334, 15335), { p ->
        p.addEffect(Effect.OVERLOAD, 500)
        p.schedule {
            for(i in 0..4) {
                p.sync(3170, 560)
                p.applyHit(Hit(p, 100, HitLook.TRUE_DAMAGE))
                wait(2)
            }
        }
    }) {
        override fun canDrink(player: Player): Boolean {
            if (player.hasEffect(Effect.OVERLOAD)) {
                player.sendMessage("You are already under the effects of an overload potion.")
                return false
            }
            if (player.hitpoints <= 500) {
                player.sendMessage("You need more than 500 life points to survive the power of overload.")
                return false
            }
            return true
        }
    },
    OVERLOAD_FLASK(-1, intArrayOf(23531, 23532, 23533, 23534, 23535, 23536), { p ->
        p.addEffect(Effect.OVERLOAD, 500)
        p.schedule {
            for(i in 0..4) {
                p.sync(3170, 560)
                p.applyHit(Hit(p, 100, HitLook.TRUE_DAMAGE))
                wait(2)
            }
        }
    }) {
        override fun canDrink(player: Player): Boolean {
            if (player.hasEffect(Effect.OVERLOAD)) {
                player.sendMessage("You are already under the effects of an overload potion.")
                return false
            }
            if (player.hitpoints <= 500) {
                player.sendMessage("You need more than 500 life points to survive the power of overload.")
                return false
            }
            return true
        }
    },

    JUJU_MINING_POTION(JUJU_VIAL, intArrayOf(20003, 20004, 20005, 20006), { it.addEffect(Effect.JUJU_MINING, 500) }),
    JUJU_MINING_FLASK(-1, intArrayOf(23131, 23132, 23133, 23134, 23135, 23136), { it.addEffect(Effect.JUJU_MINING, 500) }),

    JUJU_COOKING_POTION(JUJU_VIAL, intArrayOf(20007, 20008, 20009, 20010), { /*TODO*/ }),
    JUJU_COOKING_FLASK(-1, intArrayOf(23137, 23138, 23139, 23140, 23141, 23142), { /*TODO*/ }),

    JUJU_FARMING_POTION(JUJU_VIAL, intArrayOf(20011, 20012, 20013, 20014), { it.addEffect(Effect.JUJU_FARMING, 500) }),
    JUJU_FARMING_FLASK(-1, intArrayOf(23143, 23144, 23145, 23146, 23147, 23148), { it.addEffect(Effect.JUJU_FARMING, 500) }),

    JUJU_WOODCUTTING_POTION(JUJU_VIAL, intArrayOf(20015, 20016, 20017, 20018), { it.addEffect(Effect.JUJU_WOODCUTTING, 500) }),
    JUJU_WOODCUTTING_FLASK(-1, intArrayOf(23149, 23150, 23151, 23152, 23153, 23154), { it.addEffect(Effect.JUJU_WOODCUTTING, 500) }),

    JUJU_FISHING_POTION(JUJU_VIAL, intArrayOf(20019, 20020, 20021, 20022), { it.addEffect(Effect.JUJU_FISHING, 500) }),
    JUJU_FISHING_FLASK(-1, intArrayOf(23155, 23156, 23157, 23158, 23159, 23160), { it.addEffect(Effect.JUJU_FISHING, 500) }),

    JUJU_HUNTER_POTION(JUJU_VIAL, intArrayOf(20023, 20024, 20025, 20026), { it.sendMessage("You decide that only plants are likely to enjoy the taste of this potion.") }),
    JUJU_HUNTER_FLASK(-1, intArrayOf(23161, 23162, 23163, 23164, 23165, 23166), { it.sendMessage("You decide that only plants are likely to enjoy the taste of this potion.") }),

    SCENTLESS_POTION(JUJU_VIAL, intArrayOf(20027, 20028, 20029, 20030), { it.addEffect(Effect.SCENTLESS, 500) }),
    SCENTLESS_FLASK(-1, intArrayOf(23167, 23168, 23169, 23170, 23171, 23172), { it.addEffect(Effect.SCENTLESS, 500) }),

    SARADOMINS_BLESSING(JUJU_VIAL, intArrayOf(20031, 20032, 20033, 20034), { it.addEffect(Effect.SARA_BLESSING, 500) }),
    SARADOMINS_BLESSING_FLASK(-1, intArrayOf(23173, 23174, 23175, 23176, 23177, 23178), { it.addEffect(Effect.SARA_BLESSING, 500) }),

    GUTHIXS_GIFT(JUJU_VIAL, intArrayOf(20035, 20036, 20037, 20038), { it.addEffect(Effect.GUTHIX_GIFT, 500) }),
    GUTHIXS_GIFT_FLASK(-1, intArrayOf(23179, 23180, 23181, 23182, 23183, 23184), { it.addEffect(Effect.GUTHIX_GIFT, 500) }),

    ZAMORAKS_FAVOUR(JUJU_VIAL, intArrayOf(20039, 20040, 20041, 20042), { it.addEffect(Effect.ZAMMY_FAVOR, 500) }),
    ZAMORAKS_FAVOUR_FLASK(-1, intArrayOf(23185, 23186, 23187, 23188, 23189, 23190), { it.addEffect(Effect.ZAMMY_FAVOR, 500) }),


    WEAK_MAGIC_POTION(17490, 17556, { it.skills.adjustStat(4, 0.1, MAGIC) }),
    WEAK_RANGED_POTION(17490, 17558, { it.skills.adjustStat(4, 0.1, RANGE) }),
    WEAK_MELEE_POTION(17490, 17560, { it.skills.adjustStat(4, 0.1, ATTACK, STRENGTH) }),
    WEAK_DEFENCE_POTION(17490, 17562, { it.skills.adjustStat(4, 0.1, DEFENSE) }),

    WEAK_GATHERERS_POTION(17490, 17574, { it.skills.adjustStat(3, 0.02, WOODCUTTING, MINING, FISHING) }),
    WEAK_ARTISANS_POTION(17490, 17576, { it.skills.adjustStat(3, 0.02, SMITHING, CRAFTING, FLETCHING, CONSTRUCTION, FIREMAKING) }),
    WEAK_NATURALISTS_POTION(17490, 17578, { it.skills.adjustStat(3, 0.02, COOKING, FARMING, HERBLORE, RUNECRAFTING) }),
    WEAK_SURVIVALISTS_POTION(17490, 17580, { it.skills.adjustStat(3, 0.02, AGILITY, HUNTER, THIEVING, SLAYER) }),

    MAGIC_POTION_D(17490, 17582, { it.skills.adjustStat(5, 0.14, MAGIC) }),
    RANGED_POTION(17490, 17584, { it.skills.adjustStat(5, 0.14, RANGE) }),
    MELEE_POTION(17490, 17586, { it.skills.adjustStat(5, 0.14, ATTACK, STRENGTH) }),
    DEFENCE_POTION_D(17490, 17588, { it.skills.adjustStat(5, 0.14, DEFENSE) }),

    GATHERERS_POTION(17490, 17598, { it.skills.adjustStat(4, 0.04, WOODCUTTING, MINING, FISHING) }),
    ARTISANS_POTION(17490, 17600, { it.skills.adjustStat(4, 0.04, SMITHING, CRAFTING, FLETCHING, CONSTRUCTION, FIREMAKING) }),
    NATURALISTS_POTION(17490, 17602, { it.skills.adjustStat(4, 0.04, COOKING, FARMING, HERBLORE, RUNECRAFTING) }),
    SURVIVALISTS_POTION(17490, 17604, { it.skills.adjustStat(4, 0.04, AGILITY, HUNTER, THIEVING, SLAYER) }),

    STRONG_MAGIC_POTION(17490, 17606, { it.skills.adjustStat(6, 0.2, MAGIC) }),
    STRONG_RANGED_POTION(17490, 17608, { it.skills.adjustStat(6, 0.2, RANGE) }),
    STRONG_MELEE_POTION(17490, 17610, { it.skills.adjustStat(6, 0.2, ATTACK, STRENGTH) }),
    STRONG_DEFENCE_POTION(17490, 17612, { it.skills.adjustStat(6, 0.2, DEFENSE) }),

    STRONG_GATHERERS_POTION(17490, 17622, { it.skills.adjustStat(6, 0.06, WOODCUTTING, MINING, FISHING) }),
    STRONG_ARTISANS_POTION(17490, 17624, { it.skills.adjustStat(6, 0.06, SMITHING, CRAFTING, FLETCHING, CONSTRUCTION, FIREMAKING) }),
    STRONG_NATURALISTS_POTION(17490, 17626, { it.skills.adjustStat(6, 0.06, COOKING, FARMING, HERBLORE, RUNECRAFTING) }),
    STRONG_SURVIVALISTS_POTION(17490, 17628, { it.skills.adjustStat(6, 0.06, AGILITY, HUNTER, THIEVING, SLAYER) }),

    WEAK_STAT_RESTORE_POTION(17490, 17564, { it.skills.adjustStat(5, 0.12, false, *Skills.allExcept(PRAYER, SUMMONING)) }),
    STAT_RESTORE_POTION(17490, 17590, { it.skills.adjustStat(7, 0.17, false, *Skills.allExcept(PRAYER, SUMMONING)) }),
    STRONG_STAT_RESTORE_POTION(17490, 17614, { it.skills.adjustStat(10, 0.24, false, *Skills.allExcept(PRAYER, SUMMONING)) }),

    WEAK_CURE_POTION(17490, 17568, { p ->
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(5).toLong())
        p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(5).toLong())
    }),
    CURE_POTION(17490, 17592, { p ->
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(10).toLong())
        p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(10).toLong())
    }),
    STRONG_CURE_POTION(17490, 17616, { p ->
        p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(20).toLong())
        p.addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromMinutes(20).toLong())
    }),

    WEAK_REJUVENATION_POTION(17490, 17570, { p ->
        p.skills.adjustStat(5, 0.10, false, SUMMONING)
        p.prayer.restorePrayer((p.skills.getLevelForXp(PRAYER).toDouble() + 50).toInt().toDouble())
    }),
    REJUVENATION_POTION(17490, 17594, { p ->
        p.skills.adjustStat(7, 0.15, false, SUMMONING)
        p.prayer.restorePrayer((floor(p.skills.getLevelForXp(PRAYER) * 1.5) + 70).toInt().toDouble())
    }),
    STRONG_REJUVENATION_POTION(17490, 17618, { p ->
        p.skills.adjustStat(10, 0.22, false, SUMMONING)
        p.prayer.restorePrayer((floor(p.skills.getLevelForXp(PRAYER) * 2.2) + 100).toInt().toDouble())
    }),

    POISON_CHALICE(2026, 197, { /* TODO */ }),

    STRANGE_FRUIT(-1, 464, { it.restoreRunEnergy(30.0) }),
    GORAJIAN_MUSHROOM(-1, 22446, { p ->
        p.heal((p.maxHitpoints * 0.1).toInt())
        p.tempAttribs.setB("gorajMush", true)
    }),

    KARAMJAN_RUM(-1, 431, { p ->
        p.skills.adjustStat(-4, 0.0, ATTACK)
        p.skills.adjustStat(-5, 0.0, STRENGTH)
        p.heal(50)
    }),
    BANDITS_BREW(BEER_GLASS, 4627, { p ->
        p.skills.adjustStat(1, 0.0, ATTACK, THIEVING)
        p.skills.adjustStat(-1, 0.0, STRENGTH)
        p.skills.adjustStat(-6, 0.0, DEFENSE)
        p.heal(10)
    }),
    GROG(BEER_GLASS, 1915, { p ->
        p.skills.adjustStat(3, 0.0, STRENGTH)
        p.skills.adjustStat(-6, 0.0, ATTACK)
        p.heal(30)
    }),
    BEER(BEER_GLASS, 1917, { p ->
        p.skills.adjustStat(0, 0.04, STRENGTH)
        p.skills.adjustStat(0, -0.07, ATTACK)
        p.heal(10)
    }),
    BEER_FREM(3805, 3803, { p ->
        p.skills.adjustStat(0, 0.04, STRENGTH)
        p.skills.adjustStat(0, -0.07, ATTACK)
        p.heal(10)
    }),
    BEER_POH(BEER_GLASS, 7740, { p ->
        p.skills.adjustStat(0, 0.04, STRENGTH)
        p.skills.adjustStat(0, -0.07, ATTACK)
        p.heal(10)
    }),

    ASGARNIAN_ALE(BEER_GLASS, 1905, { p ->
        p.skills.adjustStat(-2, 0.0, STRENGTH)
        p.skills.adjustStat(4, 0.0, ATTACK)
        p.heal(20)
    }),
    ASGARNIAN_ALE_POH(BEER_GLASS, 7744, { p ->
        p.skills.adjustStat(-2, 0.0, STRENGTH)
        p.skills.adjustStat(4, 0.0, ATTACK)
        p.heal(20)
    }),
    ASGARNIAN_ALE_KEG(EMPTY_KEG, intArrayOf(5779, 5781, 5783, 5785), { p ->
        p.skills.adjustStat(-2, 0.0, STRENGTH)
        p.skills.adjustStat(4, 0.0, ATTACK)
        p.heal(20)
    }),
    ASGARNIAN_ALE_M(BEER_GLASS, 5739, { p ->
        p.skills.adjustStat(-3, 0.0, STRENGTH)
        p.skills.adjustStat(6, 0.0, ATTACK)
        p.heal(20)
    }),
    ASGARNIAN_ALE_M_KEG(EMPTY_KEG, intArrayOf(5859, 5861, 5863, 5865), { p ->
        p.skills.adjustStat(-3, 0.0, STRENGTH)
        p.skills.adjustStat(6, 0.0, ATTACK)
        p.heal(20)
    }),

    MIND_BOMB(BEER_GLASS, 1907, { p ->
        p.skills.adjustStat(if (p.skills.getLevelForXp(MAGIC) >= 50) 3 else 2, 0.0, MAGIC)
        p.skills.adjustStat(-3, 0.0, ATTACK)
        p.skills.adjustStat(-4, 0.0, STRENGTH, DEFENSE)
    }),
    MIND_BOMB_KEG(EMPTY_KEG, intArrayOf(5795, 5797, 5799, 5801), { p ->
        p.skills.adjustStat(if (p.skills.getLevelForXp(MAGIC) >= 50) 3 else 2, 0.0, MAGIC)
        p.skills.adjustStat(-3, 0.0, ATTACK)
        p.skills.adjustStat(-4, 0.0, STRENGTH, DEFENSE)
    }),
    MIND_BOMB_M(BEER_GLASS, 5741, { p ->
        p.skills.adjustStat(if (p.skills.getLevelForXp(MAGIC) >= 50) 4 else 3, 0.0, MAGIC)
        p.skills.adjustStat(-5, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),
    MIND_BOMB_M_KEG(EMPTY_KEG, intArrayOf(5875, 5877, 5879, 5881), { p ->
        p.skills.adjustStat(if (p.skills.getLevelForXp(MAGIC) >= 50) 4 else 3, 0.0, MAGIC)
        p.skills.adjustStat(-5, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),

    GREENMANS_ALE(BEER_GLASS, 1909, { p ->
        p.skills.adjustStat(1, 0.0, HERBLORE)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),
    GREENMANS_ALE_POH(BEER_GLASS, 7746, { p ->
        p.skills.adjustStat(1, 0.0, HERBLORE)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),
    GREENMANS_ALE_KEG(EMPTY_KEG, intArrayOf(5787, 5789, 5791, 5793), { p ->
        p.skills.adjustStat(1, 0.0, HERBLORE)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),
    GREENMANS_ALE_M(BEER_GLASS, 5743, { p ->
        p.skills.adjustStat(2, 0.0, HERBLORE)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),
    GREENMANS_ALE_M_KEG(EMPTY_KEG, intArrayOf(5867, 5869, 5871, 5873), { p ->
        p.skills.adjustStat(2, 0.0, HERBLORE)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),

    DRAGON_BITTER(BEER_GLASS, 1911, { p ->
        p.skills.adjustStat(2, 0.0, STRENGTH)
        p.skills.adjustStat(-6, 0.0, ATTACK)
        p.heal(10)
    }),
    DRAGON_BITTER_POH(BEER_GLASS, 7748, { p ->
        p.skills.adjustStat(2, 0.0, STRENGTH)
        p.skills.adjustStat(-6, 0.0, ATTACK)
        p.heal(10)
    }),
    DRAGON_BITTER_KEG(EMPTY_KEG, intArrayOf(5803, 5805, 5807, 5809), { p ->
        p.skills.adjustStat(2, 0.0, STRENGTH)
        p.skills.adjustStat(-6, 0.0, ATTACK)
        p.heal(10)
    }),
    DRAGON_BITTER_M(BEER_GLASS, 5745, { p ->
        p.skills.adjustStat(2, 0.0, STRENGTH)
        p.skills.adjustStat(-4, 0.0, ATTACK)
        p.heal(20)
    }),
    DRAGON_BITTER_M_KEG(EMPTY_KEG, intArrayOf(5883, 5885, 5887, 5889), { p ->
        p.skills.adjustStat(2, 0.0, STRENGTH)
        p.skills.adjustStat(-4, 0.0, ATTACK)
        p.heal(20)
    }),
    DWARVEN_STOUT(BEER_GLASS, 1913, { p ->
        p.skills.adjustStat(1, 0.0, MINING, SMITHING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    DWARVEN_STOUT_KEG(EMPTY_KEG, intArrayOf(5771, 5773, 5775, 5777), { p ->
        p.skills.adjustStat(1, 0.0, MINING, SMITHING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    DWARVEN_STOUT_M(BEER_GLASS, 5747, { p ->
        p.skills.adjustStat(2, 0.0, MINING, SMITHING)
        p.skills.adjustStat(-7, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),
    DWARVEN_STOUT_M_KEG(EMPTY_KEG, intArrayOf(5851, 5853, 5855, 5857), { p ->
        p.skills.adjustStat(2, 0.0, MINING, SMITHING)
        p.skills.adjustStat(-7, 0.0, ATTACK, STRENGTH, DEFENSE)
        p.heal(10)
    }),

    MOONLIGHT_MEAD(BEER_GLASS, 2955, { it.heal(40) }),
    MOONLIGHT_MEAD_POH(BEER_GLASS, 7750, { it.heal(40) }),
    MOONLIGHT_MEAD_KEG(EMPTY_KEG, intArrayOf(5811, 5813, 5815, 5817), { it.heal(40) }),
    MOONLIGHT_MEAD_M(BEER_GLASS, 5749, { it.heal(60) }),
    MOONLIGHT_MEAD_M_KEG(EMPTY_KEG, intArrayOf(5891, 5893, 5895, 5897), { it.heal(60) }),

    AXEMANS_FOLLY(BEER_GLASS, 5751, { p ->
        p.skills.adjustStat(1, 0.0, WOODCUTTING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    AXEMANS_FOLLY_KEG(EMPTY_KEG, intArrayOf(5819, 5821, 5823, 5825), { p ->
        p.skills.adjustStat(1, 0.0, WOODCUTTING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    AXEMANS_FOLLY_M(BEER_GLASS, 5753, { p ->
        p.skills.adjustStat(2, 0.0, WOODCUTTING)
        p.skills.adjustStat(-4, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    AXEMANS_FOLLY_M_KEG(EMPTY_KEG, intArrayOf(5899, 5901, 5903, 5905), { p ->
        p.skills.adjustStat(2, 0.0, WOODCUTTING)
        p.skills.adjustStat(-4, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),

    CHEFS_DELIGHT(BEER_GLASS, 5755, { p ->
        p.skills.adjustStat(1, 0.05, COOKING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    CHEFS_DELIGHT_POH(BEER_GLASS, 7754, { p ->
        p.skills.adjustStat(1, 0.05, COOKING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    CHEFS_DELIGHT_KEG(EMPTY_KEG, intArrayOf(5827, 5829, 5831, 5833), { p ->
        p.skills.adjustStat(1, 0.05, COOKING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    CHEFS_DELIGHT_M(BEER_GLASS, 5757, { p ->
        p.skills.adjustStat(2, 0.05, COOKING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    CHEFS_DELIGHT_M_KEG(EMPTY_KEG, intArrayOf(5907, 5909, 5911, 5913), { p ->
        p.skills.adjustStat(2, 0.05, COOKING)
        p.skills.adjustStat(-3, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),

    SLAYERS_RESPITE(BEER_GLASS, 5759, { p ->
        p.skills.adjustStat(2, 0.0, SLAYER)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    SLAYERS_RESPITE_KEG(EMPTY_KEG, intArrayOf(5835, 5837, 5839, 5841), { p ->
        p.skills.adjustStat(2, 0.0, SLAYER)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    SLAYERS_RESPITE_M(BEER_GLASS, 5761, { p ->
        p.skills.adjustStat(4, 0.0, SLAYER)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),
    SLAYERS_RESPITE_M_KEG(EMPTY_KEG, intArrayOf(5915, 5917, 5919, 5921), { p ->
        p.skills.adjustStat(4, 0.0, SLAYER)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(10)
    }),

    CIDER(BEER_GLASS, 5763, { p ->
        p.skills.adjustStat(1, 0.0, FARMING)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    CIDER_POH(BEER_GLASS, 7752, { p ->
        p.skills.adjustStat(1, 0.0, FARMING)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    CIDER_KEG(EMPTY_KEG, intArrayOf(5843, 5845, 5847, 5849), { p ->
        p.skills.adjustStat(1, 0.0, FARMING)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    CIDER_M(BEER_GLASS, 5765, { p ->
        p.skills.adjustStat(2, 0.0, FARMING)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    CIDER_M_KEG(EMPTY_KEG, intArrayOf(5923, 5925, 5927, 5929), { p ->
        p.skills.adjustStat(2, 0.0, FARMING)
        p.skills.adjustStat(-2, 0.0, ATTACK, STRENGTH)
        p.heal(20)
    }),
    SERUM_207(VIAL, intArrayOf(3408, 3410, 3412, 3414), { }),
    SERUM_208(VIAL, intArrayOf(3416, 3417, 3418, 3419), { }),
    OLIVE_OIL(VIAL, intArrayOf(3422, 3424, 3426, 3428), { }),
    SACRED_OIL(VIAL, intArrayOf(3430, 3432, 3434, 3436), { }),

    JUG_OF_BAD_WINE(EMPTY_JUG, 1991, { it.skills.lowerStat(ATTACK, 3) }),
    JUG_OF_WINE(EMPTY_JUG, 1993, { p ->
        p.heal(110, 0)
        p.skills.lowerStat(ATTACK, 2)
    }),
    ;

    constructor(emptyId: Int, potionId: Int, effect: (Player) -> Unit) : this(emptyId, intArrayOf(potionId), effect)

    open fun canDrink(player: Player): Boolean {
        return true
    }

    internal fun drink(player: Player, itemId: Int, slot: Int) {
        if (player.inventory.getItem(slot) == null || player.inventory.getItem(slot).id != itemId || !player.canPot() || !player.controllerManager.canPot(this)) return
        if (wildernessBlacklisted.contains(this) && player.controllerManager.controller is WildernessController) {
            player.sendMessage("You cannot drink this potion here.")
            return
        }
        if (canDrink(player)) {
            var idIdx = -1
            for (i in 0 until ids.size - 1) if (ids[i] == itemId) {
                idIdx = i
                break
            }
            val newId = if (idIdx == -1) emptyId else ids[idIdx + 1]
            player.inventory.items[slot] = if (newId == -1) null else Item(newId, 1)
            player.inventory.refresh(slot)
            player.addPotionDelay(2)
            effect.invoke(player)
            player.anim(829)
            player.soundEffect(4580, false)
            player.sendMessage("You drink some of your " + ItemDefinitions.getDefs(itemId).name.lowercase(Locale.getDefault()).replace(" (1)", "").replace(" (2)", "").replace(" (3)", "").replace(" (4)", "").replace(" (5)", "").replace(" (6)", "") + ".", true)
        }
    }

    val maxDoses: Int
        get() = ids.size

    fun getIdForDoses(doses: Int): Int {
        return ids[ids.size - doses]
    }

    val isVial: Boolean
        get() = emptyId == VIAL || emptyId == JUJU_VIAL

    val isFlask: Boolean
        get() = emptyId == FLASK

    companion object {
        val wildernessBlacklisted = setOf(
            RECOVER_SPECIAL, RECOVER_SPECIAL_FLASK,
            EXTREME_ATTACK, EXTREME_ATTACK_FLASK,
            EXTREME_STRENGTH, EXTREME_STRENGTH_FLASK,
            EXTREME_DEFENCE, EXTREME_DEFENCE_FLASK,
            EXTREME_RANGING, EXTREME_RANGING_FLASK,
            EXTREME_MAGIC, EXTREME_MAGIC_FLASK,
        )

        @JvmField
        val POTS: MutableMap<Int, Potion> = HashMap()

        init {
            for (pot in entries) for (id in pot.ids) POTS[id] = pot
        }

        fun forId(itemId: Int): Potion? {
            return POTS[itemId]
        }


        @JvmStatic
        fun checkPVPPotionBoosts(player: Player) {
            var changed = false

            fun validateAndLowerSkill(skillId: Int, baseIncrease: Int, multiplier: Double) {
                val level = player.skills.getLevelForXp(skillId)
                val maxLevel = (level + baseIncrease + (level * multiplier)).toInt()
                if (maxLevel < player.skills.getLevel(skillId)) {
                    player.skills[skillId] = maxLevel
                    changed = true
                }
            }

            arrayOf(ATTACK, STRENGTH, DEFENSE).forEach { validateAndLowerSkill(it, 5, 0.15) }
            validateAndLowerSkill(RANGE, 5, 0.1)
            validateAndLowerSkill(MAGIC, 5, 0.0)
            if (changed) player.sendMessage("Your extreme potion bonus has been reduced.")
        }

        @JvmStatic
        fun applyOverloadEffect(player: Player) {
            if (player.hasEffect(Effect.OVERLOAD_PVP_REDUCTION)) {
                arrayOf(SUPER_ATTACK, SUPER_STRENGTH, SUPER_DEFENCE, RANGING_POTION, MAGIC_POTION).forEach {
                    it.effect.invoke(player)
                }
            } else {
                arrayOf(EXTREME_ATTACK, EXTREME_STRENGTH, EXTREME_DEFENCE, EXTREME_RANGING, EXTREME_MAGIC).forEach {
                    it.effect.invoke(player)
                }
            }
        }
    }
}