package com.rs.game.content.items

import com.rs.game.content.skills.magic.Magic
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onItemClick

val resources = mapOf(
    /**
     * Tier 1
     */
    29 to Resource(1, arrayOf( //Copper
        Tile.of(3227, 3148, 0),
        Tile.of(3285, 3366, 0),
    )),
    30 to Resource(1, arrayOf( //Tin
        Tile.of(3227, 3148, 0),
        Tile.of(3285, 3366, 0),
    )),
    31 to Resource(1, arrayOf( //Iron
        Tile.of(3179, 3367, 0),
        Tile.of(3285, 3366, 0),
    )),
    13 to Resource(1, arrayOf( //Oak trees
        Tile.of(3257, 3363, 0),
    )),
    14 to Resource(1, arrayOf( //Willow trees
        Tile.of(3090, 3232, 0),
    )),
    2 to Resource(1, arrayOf( //Herblore secondaries 1
        //Tile.of(),
    )),
    6 to Resource(1, arrayOf( //Fish 1
        Tile.of(3090, 3232, 0),
    )),
    /**
     * Tier 2
     */
    32 to Resource(2, arrayOf( //Silver
        Tile.of(3179, 3367, 0),
    )),
    33 to Resource(2, arrayOf( //Clay
        Tile.of(3179, 3367, 0),
    )),
    15 to Resource(2, arrayOf( //Maple trees
        //Tile.of(),
    )),
    16 to Resource(2, arrayOf( //Special logs
        //Tile.of(),
    )),
    /**
     * Tier 3
     */
    34 to Resource(3, arrayOf( //Gold
        //Tile.of(),
    )),
    35 to Resource(3, arrayOf( //Mithril
        //Tile.of(),
    )),
    17 to Resource(3, arrayOf( //Yew trees
        Tile.of(3257, 3363, 0),
    )),
    4 to Resource(3, arrayOf( //Herblore secondaries 2
        //Tile.of(),
    )),
    8 to Resource(3, arrayOf( //Fish 2
        //Tile.of(),
    )),
    /**
     * Tier 4
     */
    36 to Resource(3, arrayOf( //Adamant
        //Tile.of(),
    )),
    37 to Resource(3, arrayOf( //Runite
        //Tile.of(),
    )),
    18 to Resource(3, arrayOf( //Magic trees
        //Tile.of(),
    )),
)

typealias Resource = Pair<Int, Array<Tile>>

@ServerStartupEvent
fun mapResourceLocators() {
    onButtonClick(844) { (player, _, componentId) ->
        val resource = resources[componentId] ?: return@onButtonClick
        val locator = player.nsv.getO<Item>("locatorOpened") ?: return@onButtonClick
        if (player.inventory.getItem(locator.slot) == null) return@onButtonClick
        val tier = locator.id - 15004
        if (resource.first != tier) return@onButtonClick
        if (Magic.sendItemTeleportSpell(player, true,11871, 2061, 11885, -1, 5, resource.second.random())) {
            val chargesLeft = locator.decMetaDataI("locateCharges")
            if (chargesLeft <= 0) {
                player.inventory.deleteItem(locator.slot, locator)
                player.sendMessage("Your locator has degraded into dust.")
                return@onButtonClick
            }
            player.sendMessage("Your locator has $chargesLeft charges left.")
        }
    }

    onItemClick(15005, 15006, 15007, 15008, options = arrayOf("Locate", "Check-Charges")) { (player, item, option) ->
        when(option) {
            "Check-Charges" -> player.sendMessage("This locator has ${item.getMetaDataI("locateCharges", 20)} charges left.")
            "Locate" -> {
                val tier = item.id - 15004
                player.packets.sendVarc(826, item.getMetaDataI("locateCharges", 20))
                player.packets.sendVarc(827, tier)
                player.nsv.setO<Item>("locatorOpened", item)
                player.interfaceManager.sendInterface(844)
            }
        }
    }
}
