package com.rs.game.content.world.areas.desert

import com.rs.game.World
import com.rs.game.content.items.EnchantedWaterTiara
import com.rs.game.content.skills.cooking.Foods
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onChunkEnter
import com.rs.plugin.kts.onItemEquip
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Areas
import com.rs.utils.Ticks

// Var Keys
const val DESERT_HEAT_AREA = "desert_heat_effect"
const val DESERT_HEAT_EFFECT_DELAY_ATTR = "desert_heat_effect_delay"

// Item Configs
private val WATER_SKINS = arrayOf(Item(1823), Item(1825), Item(1827), Item(1829), Item(1831))
private val WATER_CONTAINERS = arrayOf(intArrayOf(1937, 1935), intArrayOf(1929, 1925), intArrayOf(1921, 1923), intArrayOf(227, 229))
private val CHOC_ICE = Item(6794)

// Male Dervish items
private val dervishHeadM = getDervishItemDelays(20970, 20978, 6) // Dervish Head Wraps (Male)
private val dervishTopM = getDervishItemDelays(20980, 20988, 12) // Dervish Robes (Male)
private val dervishLegsM = getDervishItemDelays(20990, 20998, 12) // Dervish Trousers (Male)
private val dervishFeetM = getDervishItemDelays(21000, 21008, 6) // Dervish Shoes (Male)
// Female Dervish items
private val dervishHeadF = getDervishItemDelays(21010, 21018, 6) // Dervish Hoods (Female)
private val dervishTopF = getDervishItemDelays(21020, 21028, 12) // Dervish Robes (Female)
private val dervishLegsF = getDervishItemDelays(21030, 21038, 12) // Dervish Trousers (Female)
private val dervishFeetF = getDervishItemDelays(21040, 21048, 6) // Dervish Shoes (Female)

// Combat Equipment Slots
const val HEAD_SLOT = Equipment.HEAD
const val CHEST_SLOT = Equipment.CHEST
const val LEGS_SLOT = Equipment.LEGS
const val FEET_SLOT = Equipment.FEET
const val HANDS_SLOT = Equipment.HANDS

// Combat Equipment Penalties
private val slotPenalties = mapOf(
    HEAD_SLOT to 6,
    CHEST_SLOT to 24,
    LEGS_SLOT to 18,
    FEET_SLOT to 6,
    HANDS_SLOT to 6
)

// Equipment Delays - Item ID & time to add on.
private val EQUIPMENT_DELAYS = arrayOf(
    Pair(1833, 12), // Desert Shirt
    Pair(1835, 12), // Desert Robe
    Pair(1837, 6),  // Desert Boots
    Pair(6384, 12), // Desert Top (Shirt)
    Pair(6386, 12), // Desert Robes
    Pair(6388, 12), // Desert Top (Coat)
    Pair(3690, 12), // Desert Legs
    Pair(6392, 12), // Menap Headgear (Purple)
    Pair(6400, 12), // Menap Headgear (Red)
    Pair(1844, 6), // Slave Shirt
    Pair(1845, 6), // Slave Robe
    Pair(1846, 3), // Slave Boots
).plus(
    dervishHeadM + dervishTopM + dervishLegsM + dervishFeetM +
            dervishHeadF + dervishTopF + dervishLegsF + dervishFeetF
)

// Equipment IDs for unequip handling
val equipmentIds = EQUIPMENT_DELAYS.map { it.first }.toTypedArray()

@ServerStartupEvent
fun mapDesertHeatEffect() {

    // Apply heat delay effect to player as the enter chunks
    onChunkEnter { (entity, _) ->
        val player = entity as? Player ?: return@onChunkEnter
        if (Areas.withinArea(DESERT_HEAT_AREA, player.chunkId)) {
            val currentDelay = player.tempAttribs.getI(DESERT_HEAT_EFFECT_DELAY_ATTR)
            if (currentDelay <= 0) {
                player.tempAttribs.setI(DESERT_HEAT_EFFECT_DELAY_ATTR, getHeatEffectDelay(player))
            }
        }
    }

    // Apply heat effect to players in DESERT_HEAT_AREA chunks
    WorldTasks.scheduleTimer(0, 1) {
        World.players.forEach { player ->
            if (Areas.withinArea(DESERT_HEAT_AREA, player.chunkId)) {
                val noInterfaceOpen = !player.interfaceManager.containsChatBoxInter() &&
                        !player.interfaceManager.containsScreenInter() &&
                        player.interfaceManager.topInterface != 755

                if (noInterfaceOpen) {
                    var heatEffectDelay = player.tempAttribs.getI(DESERT_HEAT_EFFECT_DELAY_ATTR)
                    if (heatEffectDelay <= 1) {
                        applyEffect(player)
                        heatEffectDelay = getHeatEffectDelay(player)
                    } else {
                        heatEffectDelay -= 1
                    }
                    player.tempAttribs.setI(DESERT_HEAT_EFFECT_DELAY_ATTR, heatEffectDelay)
                }
            }
        }
        return@scheduleTimer true
    }

    // Refill waterskins from Kharidian Cacti
    onObjectClick(2670) { (player, obj) -> cutCactus(player, obj) }
    onItemOnObject(objectNamesOrIds = arrayOf(2670), itemNamesOrIds = arrayOf(946)) { (player, obj) -> cutCactus(player, obj) }

    // Increase/Decrease player's heatEffectDelay by Item's amount if equipped/unequipped
    onItemEquip(*equipmentIds) { e ->
        if (Areas.withinArea(DESERT_HEAT_AREA, e.player.chunkId)) {
            var heatEffectDelay = e.player.tempAttribs.getI(DESERT_HEAT_EFFECT_DELAY_ATTR)

            // Apply equipment delay increments
            EQUIPMENT_DELAYS.forEach { (equipmentId, delayIncrement) ->
                if (e.item.id == equipmentId) {
                    if (e.equip()) {
                        heatEffectDelay += delayIncrement
                    } else if (e.dequip()) {
                        heatEffectDelay -= delayIncrement
                        heatEffectDelay = heatEffectDelay.coerceAtLeast(0) // Ensure delay doesn't drop below 0
                    }
                }
            }

            e.player.tempAttribs.setI(DESERT_HEAT_EFFECT_DELAY_ATTR, heatEffectDelay)
        }
    }

}

private fun cutCactus(player: Player, obj: GameObject): Boolean {
    if (!player.inventory.containsItem(946)) {
        player.sendMessage("You need a knife to cut this cactus.")
        return false
    }
    player.lock(1)
    player.anim(911)
    val successfulCut = Utils.skillSuccess(player.skills.getLevel(Constants.WOODCUTTING), player.auraManager.woodcuttingMul, 30, 252, 255)
    if (!successfulCut) {
        player.sendMessage("You fail to cut the cactus correctly and it gives you no water this time.")
        player.skills.addXp(Skills.WOODCUTTING, 0.4)
    } else {
        val hasRefilled = refillWaterskin(player)
        obj.setIdTemporary(obj.id + 1, Ticks.fromMinutes(1))
        if (hasRefilled) {
            player.sendMessage("You top up your skin with water from the cactus.")
            player.skills.addXp(Skills.WOODCUTTING, 10.0)
        } else {
            player.sendMessage("You have no empty waterskins to put the water in.")
        }
    }
    return true
}

private fun refillWaterskin(player: Player): Boolean {
    for (i in 1 until WATER_SKINS.size) {
        val itemId = WATER_SKINS[i].id
        if (player.inventory.containsOneItem(itemId)) {
            player.inventory.deleteItem(Item(itemId))
            player.inventory.addItem(Item(itemId - 2))
            return true
        }
    }
    return false
}

private fun getHeatEffectDelay(player: Player): Int {
    var heatEffectDelay = Utils.randomInclusive(61, 91)

    // Iterate through the slots and apply the corresponding penalties if the item has combat stats
    slotPenalties.forEach { (slot, penalty) ->
        val item = player.equipment.getItem(slot)
        if (hasCombatStats(item)) {
            heatEffectDelay -= penalty
        }
    }

    // Apply equipment delays
    EQUIPMENT_DELAYS.forEach { (itemId, delayIncrement) ->
        if (player.equipment.containsOneItem(itemId))
            heatEffectDelay += delayIncrement
    }

    // Ensure delay doesn't drop below 0
    return heatEffectDelay.coerceAtLeast(0)
}

private fun checkAndDrinkWater(player: Player): Boolean {
    // Try Enchanted Water Tiara first
    if (EnchantedWaterTiara(player).depleteEnchantedWaterTiara()) {
        return true
    }

    // If no enchanted water tiara with enough charges, try to consume a waterskin charge
    val sortedWaterskins = WATER_SKINS.sortedByDescending { it.id }
    for (waterskin in sortedWaterskins) {
        if (waterskin.id != 1831 && player.inventory.containsOneItem(waterskin.id)) {
            val newWaterskinId = waterskin.id + 2
            player.inventory.deleteItem(Item(waterskin.id))
            player.inventory.addItem(Item(newWaterskinId))
            player.anim(829)
            player.sendMessage("You take a drink of water from the waterskin.")
            player.soundEffect(2401, false)
            return true
        }
    }

    // If no waterskin is available, try to consume a choc-ice
    if (player.inventory.containsOneItem(CHOC_ICE.id)) {
        Foods.eat(player, CHOC_ICE, player.inventory.getItemById(CHOC_ICE.id).slot, null)
        player.sendMessage("And it cools you down.")
        return true
    }

    val message = if (player.inventory.containsOneItem(1831)) "Perhaps you should fill up one of your empty waterskins."
    else "You should get a waterskin for any travelling in the desert."
    player.sendMessage(message, true)
    return false
}

private fun evaporateWater(player: Player) {
    WATER_CONTAINERS.forEach { (fullContainerId, emptyContainerId) ->
        if (player.inventory.containsOneItem(fullContainerId)) {
            player.inventory.deleteItem(Item(fullContainerId))
            player.inventory.addItem(Item(emptyContainerId))
            val containerName = Item(fullContainerId).name.lowercase().replace("of water", "").trim()
            player.sendMessage("The water in your $containerName evaporates in the desert heat.")
        }
    }
}

private fun applyEffect(player: Player) {
    player.tempAttribs.setI(DESERT_HEAT_EFFECT_DELAY_ATTR, getHeatEffectDelay(player))
    evaporateWater(player)
    if (checkAndDrinkWater(player)) return
    val hitAmount = if (player.y < 2990) Utils.random(10, 120) else Utils.random(10, 80)
    player.applyHit(Hit(player, hitAmount, Hit.HitLook.TRUE_DAMAGE))
    player.sendMessage("You start dying of thirst while you're in the desert.")
}

private fun getDervishItemDelays(startId: Int, endId: Int, delay: Int): List<Pair<Int, Int>> {
    return (startId..endId step 2).map { it to delay }
}

private fun hasCombatStats(item: Item?): Boolean {
    return item?.definitions?.bonuses?.any { it > 0 } ?: false
}
