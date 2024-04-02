package com.rs.engine.cutscenekt

import com.rs.engine.command.Commands
import com.rs.engine.dialogue.HeadE
import com.rs.game.model.entity.Entity.MoveType
import com.rs.lib.game.Rights
import com.rs.plugin.annotations.ServerStartupEvent

@ServerStartupEvent
fun mapCommand() {
    Commands.add(Rights.DEVELOPER, "examplecutscene", "starts the example kotlin cutscene") { p, _ ->
        p.cutscene {
            fadeIn()
            wait(5)
            dynamicRegion(player.tile, 178, 554, 4, 4)
            playerMove(15, 20, 0, MoveType.TELE)
            objCreate(67500, 0, 14, 23, 0)
            val manKbd = npcCreate(1, 14, 20, 0)
            fadeOut()
            wait(5)
            dialogue {
                npc(1, HeadE.SAD, "yee haw")
                player(HeadE.CHEERFUL, "yee haw!")
            }
            waitForDialogue()
            manKbd.transformIntoNPC(50)
            wait(5)
            manKbd.finish()
            wait(20)
        }
    }
}