package com.rs.game.content.world

import com.rs.game.content.skills.crafting.GOLD_BAR
import com.rs.game.content.skills.crafting.Silver
import com.rs.game.content.skills.crafting.Silver.SILVER_BAR
import com.rs.game.content.skills.crafting.openInterface
import com.rs.game.content.skills.smithing.MoltenGlassMaking
import com.rs.game.content.skills.smithing.SmeltingD
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapFurnaces() {
    onObjectClick("Furnace", "Small furnace", "Clay forge", "Lava Furnace") { e -> use(e.player, e.`object`) }
    onItemOnObject(arrayOf("Furnace", "Small furnace", "Clay forge", "Lava Furnace"), arrayOf(GOLD_BAR, SILVER_BAR)) { e -> useItem(e.player, e.`object`, e.item) }
}

fun use(player: Player, furnace: GameObject?) {
    if (player.inventory.containsItems(Item(MoltenGlassMaking.SODA_ASH), Item(MoltenGlassMaking.BUCKET_OF_SAND))) {
        MoltenGlassMaking.openDialogue(player)
        return
    }
    player.startConversation(SmeltingD(player, furnace))
}

fun useItem(player: Player, furnace: GameObject, item: Item) {
    if (item.id == GOLD_BAR) {
        openInterface(player, false)
        player.tempAttribs.setO<Any>("jewelryObject", furnace)
        return
    }

    if (item.id == SILVER_BAR) {
        Silver.openSilverInterface(player)
        player.tempAttribs.setO<Any>("silverObject", furnace)
        return
    }
}