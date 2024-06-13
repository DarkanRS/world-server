package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.quests.plague_city.utils.PlagueCityUtils
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Civilians(player: Player, npc: NPC) {
    init {
        if (PlagueCityUtils().isWearingGasMask(player)) gasmaskDialogue(player, npc)
        else {
            val randomDialogue = (1..3).random()
            when (randomDialogue) {
                1 -> normalDialogue1(player, npc)
                2 -> normalDialogue2(player, npc)
                3 -> normalDialogue3(player, npc)
                else -> normalDialogue1(player, npc)
            }
        }
    }
}

private fun gasmaskDialogue(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hello.")
        npc(npc, FRUSTRATED, "If you Mourners really wanna help, why don't you do something about these mice?!")
    }
}

private fun normalDialogue1(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hi there.")
        npc(npc, CALM_TALK, "Good day to you, traveller.")
        player(CALM_TALK, "What are you up to?")
        npc(npc, CALM_TALK, "Chasing mice as usual! It's all I seem to do nowadays.")
        player(CALM_TALK, "You must waste a lot of time?")
        npc(npc, CALM_TALK, "Yes, but what can you do? It's not like there's many cats around here!")
        player(CALM_TALK, "No, you're right, you don't see many around.")
    }
}

private fun normalDialogue2(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hello there.")
        npc(npc, CALM_TALK, "I'm a bit busy to talk right now, sorry.")
        player(CALM_TALK, "Why? What are you doing?")
        npc(npc, CALM_TALK, "Trying to kill these mice! What I really need is a cat!")
        player(CALM_TALK, "No, you're right, you don't see many around.")
    }
}

private fun normalDialogue3(player: Player, npc: NPC) {
    player.startConversation {
        player(CALM_TALK, "Hello there.")
        npc(npc, CALM_TALK, "Oh, hello. I'm sorry, I'm a bit worn out.")
        player(CALM_TALK, "Busy day?")
        npc(npc, CALM_TALK, "Oh, it's those mice! They're everywhere! What I really need is a cat, but they're hard to come by nowadays.")
        player(CALM_TALK, "No, you're right, you don't see many around.")
    }
}

@ServerStartupEvent
fun mapCivilians() {
    onNpcClick(785, 786, 787) { (player, npc) ->
        Civilians(player, npc)
    }
}
