package com.rs.game.content.quests.death_plateau.objects

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.content.skills.mining.Pickaxe
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject

class SabbotCaveMining(player: Player, obj: GameObject) {
    init {
        run {
            val pick: Pickaxe? = Pickaxe.getBest(player)
            if (pick == null) {
                player.sendMessage("You do not have a pickaxe which you have the mining level to use.")
                return@run
            }
            player.faceObject(obj)
            player.cutscene {
                player.anim(pick.animId)
                wait(6)
                player.vars.setVarBit(SABBOT_CAVE_VB, 2)
                wait(9)
                player.anim(-1)
                obj.refresh()
                player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_MINED_TUNNEL)
                player.questManager.getAttribs(Quest.DEATH_PLATEAU).setB(RECEIVED_SANDWICHES, false)
                dialogue {
                    npc(SABBOT, FRUSTRATED, "Hey! Watch it! You humans don't know how to mine cleanly at all, do ya?")
                    player(CALM_TALK, "Well it is pretty messy work.")
                    npc(SABBOT, CALM_TALK, "Excuses, excuses...")
                }
            }
        }
    }
}
