package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.minigames.fightkiln.FightKilnController
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapHandlers() {
    onObjectClick(68107) { (player, _, option) ->
        when(option) {
            "Enter" ->
                if (player.isQuestComplete(Quest.ELDER_KILN))
                    FightKilnController.enterFightKiln(player, false)
                else
                    enterQuestKiln(player)
            "Quick-Play Fight Kiln" ->
                if (player.isQuestComplete(Quest.ELDER_KILN))
                    FightKilnController.enterFightKiln(player, true)
        }
    }
}

private fun enterQuestKiln(player: Player) {
    if (player.getQuestStage(Quest.ELDER_KILN) < STAGE_GO_TO_KILN) {
        player.npcDialogue(15194, HeadE.T_CALM_TALK, "No one allowed to pass through here. Much too dangerous for human and is sacred ground for TzHaar.")
        return
    }

    when(player.getQuestStage(Quest.ELDER_KILN)) {
        STAGE_GO_TO_KILN -> player.startConversation {
            npc(15194, HeadE.T_CALM_TALK, "TzHaar-Mej-Jeh say you may pass through here. This go to ancient Kiln. Be careful, loud noises through there, sound like wild Ket-Zek. Take pickaxe too. No one been through for long time.")
            exec {
                player.playCutscene {
                    //Ga'al-Xox:
                    //Ga'al-Xox and [Player] go now?
                    //Player:
                    //Are you sure about this?
                    //Ga'al-Xox:
                    //Ga'al-Xox must be good TzHaar.
                    //
                }
            }
        }


    }
}