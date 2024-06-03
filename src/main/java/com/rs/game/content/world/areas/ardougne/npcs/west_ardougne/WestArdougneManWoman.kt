package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plague_city.utils.STAGE_GAVE_HANGOVER_CURE
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class WestArdougneManWoman (player: Player, npc: NPC) {
    init {
        val randomDialogue = (1..5).random()
        when (randomDialogue) {
            1 -> dialogue1(player, npc)
            2 -> dialogue2(player, npc)
            3 -> dialogue3(player, npc)
            4 -> dialogue4(player, npc)
            5 -> dialogue5(player, npc)
            else -> dialogue1(player, npc)
        }
    }
}

private fun dialogue1(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Good day.")
        npc(npc, AMAZED_MILD, "An outsider! Can you get me out of this hell hole?")
        player(CALM_TALK, "Sorry, that's not what I'm here to do.")
    }
}

private fun dialogue2(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hi there.")
        npc(npc, FRUSTRATED, "Go away. People from the outside shut us in like animals. I have nothing to say to you.")
    }
}

private fun dialogue3(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hello, how's it going?")
        npc(npc, SAD, "Life is tough.")
        options {
            op("Yes, living in a plague city must be hard.") {
                player(SAD, "Yes, living in a plague city must be hard.")
                npc(npc, LAUGH, "Plague? Pah, that's no excuse for the treatment we've received. It's obvious pretty quickly if someone has the plague. I'm thinking about making a break for it. I'm perfectly healthy, not gonna infect anyone.")
            }
            op("I'm sorry to hear that.") {
                player(SAD, "I'm sorry to hear that.")
                npc(npc, SAD, "Well, ain't much either you or me can do about it.")
            }
            if (player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GAVE_HANGOVER_CURE)
                op("I'm looking for a lady called Elena.") {
                    player(SKEPTICAL_THINKING, "I'm looking for a lady called Elena.")
                    npc(npc, FRUSTRATED, "I've not heard of her. Old Jethick knows a lot of people, maybe he'll know where you can find her.")
                }
        }
    }
}

private fun dialogue4(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hello, how's it going?")
        npc(npc, FRUSTRATED, "Bah, those mourners... they're meant to be helping us, but I think they're doing more harm here than good. They won't even let me send a letter out to my family.")
        options {
            if (player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GAVE_HANGOVER_CURE)
                op("Have you seen a lady called Elena around here?") {
                    player(SKEPTICAL_THINKING, "Have you seen a lady called Elena around here?")
                    npc(npc, CALM_TALK, "Yes, I've seen her. Very helpful person. Not for the last few days though... I thought maybe she'd gone home.")
                }
            op("You should stand up to them more.") {
                player(CALM_TALK, "You should stand up to them more.")
                npc(npc, CALM_TALK, "Oh I'm not one to cause a fuss.")
            }
        }
    }
}

private fun dialogue5(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Good day.")
        npc(npc, FRUSTRATED, "We don't have good days here anymore. Curse King Tyras.")
        options {
            op("Oh ok, bad day then.") { player(CALM_TALK, "Oh ok, bad day then.") }
            op("Why, what has he done?") {
                player(CALM_TALK, "Why, what has he done?")
                npc(npc, FRUSTRATED, "His army curses our city with this plague then wanders off again, leaving us to clear up the pieces.")
            }
            if (player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GAVE_HANGOVER_CURE)
                op("I'm looking for a woman called Elena.") {
                    player(SKEPTICAL_THINKING, "I'm looking for a woman called Elena.")
                    npc(npc, CALM_TALK, "Not heard of her.")
                }
        }
    }
}

@ServerStartupEvent
fun mapWestArdougneManWoman() {
    onNpcClick(351, 352, 353, 354, 360, 361, 362, 363, 729, 728, options = arrayOf("Talk-to")) { (player, npc) ->
        WestArdougneManWoman(player, npc)
    }
}
