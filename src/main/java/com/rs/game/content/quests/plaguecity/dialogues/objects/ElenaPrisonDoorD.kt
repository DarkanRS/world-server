package com.rs.game.content.quests.plaguecity.dialogues.objects

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.player.Player

class ElenaPrisonDoorD (player: Player) {
    private val elena = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == ELENA_PRISON }
    init {
        if (player.questManager.getStage(Quest.PLAGUE_CITY) >= STAGE_GAVE_HANGOVER_CURE) {
            player.startConversation {
                simple("The door is locked.")
                npc(ELENA_PRISON, WORRIED, "Hey get me out of here please!") { faceEachOther(player) }
                player(WORRIED, "I would do but I don't have a key.")
                npc(ELENA_PRISON, SKEPTICAL_THINKING, "I think there may be one around somewhere. I'm sure I heard them stashing it somewhere.") { player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(ATTEMPTED_TO_FREE_ELENA, true) }
                label("initialOps")
                options {
                    op("Have you caught the plague?") {
                        player(SKEPTICAL, "Have you caught the plague?")
                        npc(ELENA_PRISON, CALM_TALK, "No, I have none of the symptoms.")
                        player(CONFUSED, "Strange, I was told this house was plague infected.")
                        npc(ELENA_PRISON, CALM_TALK, "I suppose that was a cover up by the kidnappers.")
                        goto("initialOps")
                    }
                    op("Ok, I'll look for it.") { player(CALM_TALK, "Ok, I'll look for it.") }
                }
            }
        } else {
            player.sendMessage("You see no benefit to going in the cell...")
        }
    }

    private fun faceEachOther(player: Player) {
        elena?.faceEntityTile(player)
        player.faceEntityTile(elena)
    }
}
