package com.rs.game.content.items

import com.rs.game.content.skills.magic.Magic
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Inventory
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onItemClick

const val DEFAULT_CHARGES = 50

//TODO please add way more locations
val RESOURCE_MAP = mapOf(
    /**
     * Tier 1
     */
    29 to Resource(1, arrayOf( //Copper
        Tile.of(3227, 3148, 0),
        Tile.of(3285, 3366, 0),
        Tile.of(3041, 9782, 0),
        Tile.of(3024, 9801, 0),
        Tile.of(2968, 3233, 0),
    )),
    30 to Resource(1, arrayOf( //Tin
        Tile.of(3227, 3148, 0),
        Tile.of(3285, 3366, 0),
        Tile.of(3051, 9779, 0),
        Tile.of(2977, 3232, 0),
    )),
    31 to Resource(1, arrayOf( //Iron
        Tile.of(3179, 3367, 0),
        Tile.of(3285, 3366, 0),
        Tile.of(3039, 9775, 0),
        Tile.of(2711, 3330, 0),
        Tile.of(2977, 3232, 0),
    )),
    13 to Resource(1, arrayOf( //Oak trees
        Tile.of(3257, 3363, 0),
        Tile.of(2706, 3317, 0),
        Tile.of(2783, 3439, 0),
        Tile.of(2751, 3463, 0),
        Tile.of(3157, 3228, 0),
    )),
    14 to Resource(1, arrayOf( //Willow trees
        Tile.of(3090, 3232, 0),
        Tile.of(2718, 3324, 0),
        Tile.of(2783, 3429, 0),
        Tile.of(2714, 3510, 0),
    )),
    2 to Resource(1, arrayOf( //Herblore secondaries 1
        Tile.of(2709, 3671, 0),
        Tile.of(2786, 3467, 0),
        Tile.of(3210, 3110, 0),
        Tile.of(3411, 2887, 0),
        Tile.of(3167, 9893, 0),
        Tile.of(3121, 9956, 0),
        Tile.of(2419, 3508, 0),
        Tile.of(3177, 3174, 0),
    )),
    6 to Resource(1, arrayOf( //Fish 1
        Tile.of(3090, 3232, 0),
        Tile.of(3243, 3155, 0),
        Tile.of(3273, 3144, 0),
        Tile.of(3241, 3250, 0),
        Tile.of(3104, 3431, 0),
        Tile.of(2718, 3532, 0),
        Tile.of(2990, 3174, 0),
    )),
    /**
     * Tier 2
     */
    32 to Resource(2, arrayOf( //Silver
        Tile.of(3179, 3367, 0),
        Tile.of(3299, 3314, 0),
        Tile.of(4621, 5091, 0),
    )),
    33 to Resource(2, arrayOf( //Clay
        Tile.of(3179, 3367, 0),
        Tile.of(3028, 9810, 0),
        Tile.of(2977, 3232, 0),
    )),
    15 to Resource(2, arrayOf( //Maple trees
        Tile.of(2751, 3463, 0),
        Tile.of(2722, 3469, 0),
        Tile.of(2728, 3502, 0),
    )),
    16 to Resource(2, arrayOf( //Special logs
        Tile.of(2465, 2860, 0),
        Tile.of(2606, 2975, 0),
        Tile.of(2337, 3050, 0),
        Tile.of(3059, 3073, 0),
    )),
    /**
     * Tier 3
     */
    34 to Resource(3, arrayOf( //Gold
        Tile.of(4621, 5091, 0),
        Tile.of(2733, 3225, 0),
        Tile.of(3322, 2874, 0),
        Tile.of(2977, 3232, 0),
    )),
    35 to Resource(3, arrayOf( //Mithril
        Tile.of(3024, 3802, 0),
        Tile.of(3055, 3942, 0),
        Tile.of(3039, 9768, 0),
    )),
    17 to Resource(3, arrayOf( //Yew trees
        Tile.of(3257, 3363, 0),
        Tile.of(2711, 3462, 0),
        Tile.of(2757, 3431, 0),
        Tile.of(3087, 3476, 0),
        Tile.of(3157, 3228, 0),
        Tile.of(3212, 3502, 0),
    )),
    4 to Resource(3, arrayOf( //Herblore secondaries 2
        Tile.of(3216, 3815, 0),
        Tile.of(2909, 9804, 0),
        Tile.of(3465, 9482, 2),
        Tile.of(2949, 3473, 0),
        Tile.of(2906, 3293, 0),
        Tile.of(2543, 3763, 0),
        Tile.of(2512, 3085, 0),
    )),
    8 to Resource(3, arrayOf( //Fish 2
        Tile.of(2924, 3173, 0),
        Tile.of(2841, 3433, 0),
    )),
    /**
     * Tier 4
     */
    36 to Resource(4, arrayOf( //Adamant
        Tile.of(3300, 3314, 0),
        Tile.of(3300, 3314, 0),
        Tile.of(3018, 3797, 0),
        Tile.of(3055, 3942, 0),
        Tile.of(3039, 9768, 0),
    )),
    37 to Resource(4, arrayOf( //Runite
        Tile.of(3059, 3884, 0),
        Tile.of(3045, 10263, 0),
    )),
    18 to Resource(4, arrayOf( //Magic trees
        Tile.of(2693, 3425, 0),
        Tile.of(2702, 3397, 0),
        Tile.of(3370, 3314, 0),
        Tile.of(2429, 3411, 0),
    )),
)

typealias Resource = Pair<Int, Array<Tile>>

@ServerStartupEvent
fun mapResourceLocators() {
    onButtonClick(844) { (player, _, componentId) ->
        val resource = RESOURCE_MAP[componentId] ?: return@onButtonClick
        val locator = player.nsv.getO<Item>("locatorOpened") ?: return@onButtonClick player.closeInterfaces()
        val container: Any = if (player.nsv.getB("locatorOpenedFromEquipment")) player.equipment else player.inventory
        val inventory = container as? Inventory
        val equipment = container as? Equipment
        if (inventory?.getItem(locator.slot) == null && equipment?.getItem(locator.slot) == null) return@onButtonClick player.closeInterfaces()
        val tier = locator.id - 15004
        if (resource.first > tier) return@onButtonClick player.sendMessage("This locator isn't powerful enough to attune to that resource.")
        if (Magic.sendItemTeleportSpell(player, true, 11871, 2061, 11885, -1, 4, resource.second.random())) {
            val chargesLeft = locator.getMetaDataI("locateCharges", DEFAULT_CHARGES) - 1
            locator.addMetaData("locateCharges", chargesLeft)
            if (chargesLeft <= 0) {
                inventory?.deleteItem(locator.slot, locator)
                equipment?.deleteSlot(locator.slot)
                player.sendMessage("Your locator has degraded into dust.")
                return@onButtonClick
            }
            player.sendMessage("Your locator has $chargesLeft charges left.")
        }
    }

    onItemClick(15005, 15006, 15007, 15008, options = arrayOf("Locate", "Check-Charges")) { e ->
        when(e.option) {
            "Check-Charges" -> e.player.sendMessage("This locator has ${e.item.getMetaDataI("locateCharges", DEFAULT_CHARGES)} charges left.")
            "Locate" -> {
                val tier = e.item.id - 15004
                e.player.packets.sendVarc(826, e.item.getMetaDataI("locateCharges", DEFAULT_CHARGES))
                e.player.packets.sendVarc(827, tier)
                e.player.nsv.setO<Item>("locatorOpened", e.item)
                e.player.nsv.setB("locatorOpenedFromEquipment", e.isEquipped)
                e.player.interfaceManager.sendInterface(844)
            }
        }
    }
}
