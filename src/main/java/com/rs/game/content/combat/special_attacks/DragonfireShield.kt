package com.rs.game.content.combat.special_attacks

import com.rs.game.World
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick

@ServerStartupEvent
fun handleDFSSpec() {
    onItemClick("Dragonfire shield", options = arrayOf("Inspect", "Activate", "Empty")) { e ->
        when (e.option) {
            "Inspect" -> {
                if (e.item.id == 11284) e.player.sendMessage("The shield is empty and unresponsive.")
                else e.player.sendMessage("The shield contains " + e.item.getMetaDataI("dfsCharges") + " charges.")
            }

            "Activate" -> {
                if (e.item.getMetaDataI("dfsCharges") > 0) {
                    if (World.getServerTicks() > e.player.tempAttribs.getL("dfsCd")) {
                        e.player.tempAttribs.setB("dfsActive", !e.player.tempAttribs.getB("dfsActive"))
                        e.player.sendMessage("You have " + (if (e.player.tempAttribs.getB("dfsActive")) "activated" else "deactivated") + " the shield.")
                    } else e.player.sendMessage("The dragonfire shield is still pretty hot from its last activation.")
                } else e.player.sendMessage("The shield is empty and unable to be activated.")
            }

            "Empty" -> {
                if (e.item.id == 11284 || e.item.getMetaDataI("dfsCharges") < 0) e.player.sendMessage("The shield is already empty.")
                else e.player.sendOptionDialogue("Are you sure you would like to empty the " + e.item.getMetaDataI("dfsCharges") + " charges?") { ops ->
                    ops.add("Yes, I understand the shield will lose all its stats.") {
                        e.item.deleteMetaData()
                        e.item.id = 11284
                        e.player.inventory.refresh()
                    }
                    ops.add("No, I want to keep them.")
                }
            }
        }
    }
}