package com.rs.game.content.world

import com.rs.game.content.skills.smithing.MoltenGlassMaking
import com.rs.game.content.skills.smithing.SmeltingD
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Item
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick
import java.util.function.Consumer

@ServerStartupEvent
fun mapFurnaces() {
    onObjectClick("Furnace", "Small furnace", "Clay forge", "Lava Furnace") { e -> use(e.player, e.getObject()) }
}

fun use(player: Player, furnace: GameObject?) {
    if (player.inventory.containsItems(Item(MoltenGlassMaking.SODA_ASH), Item(MoltenGlassMaking.BUCKET_OF_SAND))) {
        MoltenGlassMaking.openDialogue(player)
        return
    }
    player.startConversation(SmeltingD(player, furnace))
}