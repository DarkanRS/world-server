package com.rs.game.content.world.areas.trollheim.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.trollheim.EadgarD
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class Eadgar(player: Player, npc: NPC) {
    val stage = player.getQuestStage(Quest.EADGARS_RUSE)

    init {
        if (player.isQuestStarted(Quest.EADGARS_RUSE) && stage > STAGE_SPEAK_TO_EADGAR && stage < STAGE_COMPLETE) {
            EadgarD(player, npc)
        } else {
            defaultEadgarOptions(player, npc)
        }
    }

    private fun defaultEadgarOptions(player: Player, npc: NPC) {
        player.startConversation {
            player(CALM_TALK, "Hi!")
            npc(npc, HAPPY_TALKING, "Welcome to Mad Eadgar's!<br>Happiness in a bowl!")
            npc(npc, HAPPY_TALKING, "Would you care to sample our delicious home cooking?")
            label("initialOps")
            options {
                if (stage == STAGE_SPEAK_TO_EADGAR)
                    op("I need to find some goutweed.") { exec { EadgarD(player, npc) } }
                op("Why do you live so close to the trolls?") {
                    player(CALM_TALK, "Why do you live so close to the trolls? Isn't it dangerous?")
                    npc(npc, CALM_TALK, "Well, I suppose I do keep getting captured by the trolls and thrown in prison... But they always release me in the end, I'm far too old and skinny for their tastes.")
                    npc(npc, CALM_TALK, "In any case, this is my home, and I'm not leaving it. And this area has the tastiest goats!")
                    goto("initialOps")
                }
                op("What do you have to offer?") {
                    player(CALM_TALK, "What do you have to offer?")
                    npc(npc, CALM_TALK, "The chef's recommendation for today is mountain goat stew. I'll give some stew in exchange for logs for my fire. They're hard to come by around here.")
                    if (player.inventory.containsOneItem(NORMAL_LOGS)) { // Contains regular logs
                        player(CALM_TALK, "Here's some logs.")
                        npc(npc, HAPPY_TALKING, "Thank you. Here's some mountain goat stew, as promised.") { player.inventory.replace(NORMAL_LOGS, STEW) }
                    } else {
                        player(CALM_TALK, "Thanks, I might come back with some logs.")
                    }
                }
                op("No thanks, Eadgar.") {
                    player(CALM_TALK, "No thanks, Eadgar.")
                    npc(npc, CALM_TALK, "Your loss!")
                }
            }
        }
    }
}
