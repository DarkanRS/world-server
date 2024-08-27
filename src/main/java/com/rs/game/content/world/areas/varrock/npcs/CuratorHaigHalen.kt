package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.dialogue.npcs.CuratorHaigHalenD
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.content.quests.shieldofarrav.MuseumCuratorArravD
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnNpc
import com.rs.plugin.kts.onNpcClick

class CuratorHaigHalen(player: Player, npc: NPC) {
    init {
        val hasAnyCert = player.inventory.containsOneItem(EXAM_1_CERT) || player.inventory.containsOneItem(EXAM_2_CERT) || player.inventory.containsOneItem(EXAM_3_CERT)
        player.startConversation {
            if (player.questManager.getStage(Quest.DIG_SITE) in STAGE_GET_LETTER_STAMPED..STAGE_RECEIVED_SEALED_LETTER) {
                exec { CuratorHaigHalenD(player, npc) }
            } else if (hasAnyCert) {
                exec { CuratorHaigHalenD(player, npc).handInCerts(null) }
            } else {
                npc(npc, CALM_TALK, "Welcome to the museum of Varrock.")
                options {
                    op("About Shield Of Arrav...") {
                        exec { player.startConversation(MuseumCuratorArravD(player)) }
                    }
                    op("Farewell.")
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapCuratorHaigHalen() {
    onNpcClick(646, options = arrayOf("Talk-to")) { (player, npc) -> CuratorHaigHalen(player, npc) }

    onNpcClick(646, options = arrayOf("Pickpocket")) { (player) -> player.sendMessage("The curator doesn't seem to have anything of value.") }

    onItemOnNpc(646) { (player, item, npc) ->
        when (item.id) {
            763, 765 -> player.startConversation(MuseumCuratorArravD(player, true).start)
            UNSTAMPED_LETTER -> CuratorHaigHalenD(player, npc)
            SEALED_LETTER -> player.startConversation { npc(npc, CALM_TALK, "No I don't want it back thank you, you'll need to take that back to the examiner at Varrock Dig Site.") }
            EXAM_1_CERT, EXAM_2_CERT, EXAM_3_CERT -> { CuratorHaigHalenD(player, npc).handInCerts(item) }
            else -> player.sendMessage("Nothing interesting happens.")
        }
    }
}
