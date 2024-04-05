package com.rs.engine.cutscenekt

import com.rs.engine.command.Commands
import com.rs.engine.dialogue.HeadE
import com.rs.game.model.entity.Entity.MoveType
import com.rs.lib.game.Rights
import com.rs.plugin.annotations.ServerStartupEvent

const val MAN = 1
const val KBD = 50

@ServerStartupEvent
fun mapCommand() {
    Commands.add(Rights.DEVELOPER, "examplecutscene", "starts the example kotlin cutscene") { p, _ ->
        p.cutscene {

            //Prepare and set up the scene
            fadeInAndWait()
            dynamicRegion(player.tile, 178, 554, 4, 4)
            entityTeleTo(player, 15, 20)
            objCreate(67500, 0, 14, 23, 0)
            val wereKbd = npcCreate(MAN, 14, 20, 0)
            fadeOutAndWait()

            //Play the content of the scene
            dialogue {
                npc(MAN, HeadE.SAD, "Sir, I don't feel so good..")
                player(HeadE.CONFUSED, "What's wrong?")
            }
            waitForDialogue()

            wereKbd.transformIntoNPC(KBD)
            wait(5)
            wereKbd.finish()
            wait(20)
        }
    }
}