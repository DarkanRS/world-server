package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks

@ServerStartupEvent
fun mapMarketGuardPlugins() {
    onNpcClick(11269) { (player, npc) ->
        val miniquestManager = player.miniquestManager
        val distracted = miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).getB("UristDistracted")
        if (distracted) {
            distracted(player, npc)
            return@onNpcClick
        }
        if (player.isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            preQuest(player, npc)
            return@onNpcClick
        }
        stage1(player, npc)
    }
}

private fun preQuest(player: Player, npc: NPC) {
    player.startConversation {
        npc(npc, CALM_TALK, "Greetings, citizen.")
        player(CHEERFUL, "Good day to you.")
        options {
            op("How's everything going today?") {
                npc(npc, CALM, "Fairly uneventful so far. Varrock's not the hotbed of crime that Ardougne is, after all.")
                player(HAPPY_TALKING, "I'll let you get on with it then.")
                npc(npc, CALM, "Stay safe, citizen.")
            }
            op("Goodbye.") {
                player(HAPPY_TALKING, "Goodbye.")
                npc(npc, CALM, "Stay safe, citizen.")
            }
        }
    }
}

private fun stage1(player: Player, npc: NPC) {
    player.startConversation {
        npc(npc, CALM_TALK, "Greetings, citizen.")
        player(CHEERFUL, "Good day to you.")
        options {
            op("How's everything going today?") {
                npc(npc, CALM, "Fairly uneventful so far. Varrock's not the hotbed of crime that Ardougne is, after all.")
                player(HAPPY_TALKING, "I'll let you get on with it then.")
                npc(npc, CALM, "Stay safe, citizen.")
            }
            op("Who's the dwarf over there?") {
                player(SKEPTICAL, "Who's the dwarf over there?")
                npc(npc, SKEPTICAL, "Urist Loric. He's a craftsman from some fortress or other. Does some seriously delicate work, if the magnification on that monocle is anything to go by.")
                player(SKEPTICAL, "Do you know what he's working on?")
                npc(npc, CALM_TALK, "It's a toy dragon of some sort. I've been keeping an eye on him 'cos he's working with some seriously valuable materials, but nobody's tried anything yet. Nobody's going to either, I reckon. Must have heard I'm on the case.")
                player(CHEERFUL_EXPOSITION, "How's his dragon coming along?")
                npc(npc, CALM_TALK, "Looks like it's almost finished.")
                player(CHEERFUL, "He seems very focused.")
                npc(npc, CALM_TALK, "There's only two things he cares more about than his work, and he's had his morning booze.")
                player(SKEPTICAL, "What's the other thing?")
                npc(npc, CALM_TALK, "Oh, some sort of talisman he keeps in his back pocket. I've told him he shouldn't keep it there - it's asking to be stolen - but he won't listen.")
                player(HAPPY_TALKING, "I'll let you get on with it, then.")
                npc(npc, CALM_TALK, "Stay safe, citizen.")
            }
            op("Goodbye.") {
                player(HAPPY_TALKING, "Goodbye.")
                npc(npc, CALM, "Stay safe, citizen.")
            }
        }
    }
}

private fun distracted(player: Player, npc: NPC) {
    player.startConversation {
        player(CHEERFUL_EXPOSITION, "Gypsy Aris' sign is giving you the evil eye!")
        npc(npc, CONFUSED, "What? Not again!") {
            player.schedule {
                var count = 0
                while (true) {
                    val distracted = player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).getB("UristDistracted")
                    if (distracted) {
                        if (count == 0) {
                            npc.faceTile(Tile.of(3208, 3423, 0))
                            npc.forceTalk("Hmm, it looks innocuous to me.")
                            player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).setB("GuardDistracted", true)
                        } else {
                            npc.forceTalk("Hmm...")
                        }
                        count++
                        wait(Ticks.fromSeconds(1))
                    } else {
                        player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).setB("GuardDistracted", false)
                        npc.faceEast()
                        break
                    }
                }
            }
        }
    }
}
