package com.rs.game.content.world.areas.runecrafting_guild.objects

import com.rs.cache.loaders.EnumDefinitions
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onObjectClick

enum class Talismans(val talismanId: Int) {
    AIR(1438),
    MIND(1448),
    WATER(1444),
    EARTH(1440),
    FIRE(1442),
    BODY(1446),
    COSMIC(1454),
    CHAOS(1452),
    NATURE(1462),
    LAW(1458),
    DEATH(1456),
    BLOOD(1450),
    ELEMENTAL(5516),
    OMNI(13649);
}

class MapTable(val player: Player, val item: Item?) {

    init {
        if (item != null && Talismans.entries.none { it.talismanId == item.id }) {
            player.sendMessage("You can only use an altar talisman on the map.")
        } else {
            player.interfaceManager.sendInterface(780)
        }
        if (item?.id != Talismans.OMNI.talismanId) {
            hideAllRuneAltarMapLocations()
            showRuneAltarLocationForItem(item?.id ?: -1)
        }
        updateAltarLocationText()
    }

    private fun hideAllRuneAltarMapLocations() {
        val enumValues = EnumDefinitions.getEnum(1681).values.values
        for (ifComp in enumValues) {
            val ifCompInt = ifComp as Int
            player.packets.setIFHidden(Utils.interfaceIdFromHash(ifCompInt), Utils.componentIdFromHash(ifCompInt), true)
        }
    }

    private fun showRuneAltarLocationForItem(itemId: Int) {
        val enumValues: Map<Int, Any> = EnumDefinitions.getEnum(1681).values.entries.associate { it.key.toInt() to it.value }
        val component = enumValues[itemId]

        if (component != null && component is Int)
            player.packets.setIFHidden(780, Utils.componentIdFromHash(component), false)
        else {
            if (itemId == Talismans.ELEMENTAL.talismanId) {
                // Show Air, Earth, Fire, and Water sprites for Elemental talisman
                val componentsToShow = listOf(Talismans.AIR, Talismans.EARTH, Talismans.FIRE, Talismans.WATER).mapNotNull { talisman -> enumValues[talisman.talismanId] as? Int }
                componentsToShow.forEach { componentId ->
                    player.packets.setIFHidden(780, Utils.componentIdFromHash(componentId), false)
                }
            }
        }
    }

    private fun updateAltarLocationText() {
        if (item != null) {
            val itemId = item.id
            val enumValues: Map<Int, Any> = EnumDefinitions.getEnum(1682).values.entries.associate { it.key.toInt() to it.value }
            val location = enumValues[itemId]
            player.packets.setIFText(780, 79, location.toString())
        } else {
            player.packets.setIFText(780, 79, "Use a talisman to display the altar location.")
        }
    }
}

@ServerStartupEvent
fun mapRunecraftingGuildMapTable() {
    onObjectClick(38315) { (player) -> MapTable(player, null) }
    onItemOnObject(objectNamesOrIds = arrayOf(38315)) { (player, _, item) -> MapTable(player, item) }
}
