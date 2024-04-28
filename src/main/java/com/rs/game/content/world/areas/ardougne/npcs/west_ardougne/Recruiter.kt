package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Recruiter (player: Player, npc: NPC) {
    private var man1 =  728
    private var man2 = 729

    init {
        player.startConversation {
            npc(npc, CALM_TALK, "Citizens of West Ardougne. Who will join the Royal Army of Ardougne? It is a very noble cause. Fight alongside King Tyras, crusading in the darklands of the west!")
            npc(man1, ANGRY, "Plague bringer!")
            npc(man2, ANGRY, "King Tyras is scum!")
            npc(npc, ANGRY, "Tyras will be informed of these words of treason!")
        }
    }
}

@ServerStartupEvent
fun mapWestArdougneRecruiter() {
    onNpcClick(720, options = arrayOf("Talk-to")) { (player, npc) ->
        Recruiter(player, npc)
    }
}
