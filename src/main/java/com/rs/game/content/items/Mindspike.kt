package com.rs.game.content.items

import com.rs.engine.dialogue.startConversation
import com.rs.engine.dialogue.statements.Statement
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick

@ServerStartupEvent
fun mapMindspikeChange() {
    onItemClick(23044, 23045, 23046, 23047, options = arrayOf("Change element")) { e ->
        e.player.startConversation {
            statement(object : Statement {
                override fun send(player: Player) {
                    player.interfaceManager.sendChatBoxInterface(1235)
                }

                override fun getOptionId(componentId: Int) = when (componentId) {
                    2 -> 0
                    5 -> 1
                    8 -> 2
                    11 -> 3
                    else -> -1
                }

                override fun close(player: Player) {
                    player.interfaceManager.closeChatBoxInterface()
                }
            })
            intArrayOf(23044, 23045, 23046, 23047).forEach {
                appendToCurrent {
                    e.item.id = it
                    e.player.inventory.refresh(e.item.slot)
                }
            }
        }
    }
}
