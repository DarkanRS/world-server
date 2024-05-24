package com.rs.game.content.world.areas.burthorpe.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe.CommanderDenulthD
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class CommanderDenulth(player: Player, npc: NPC) {
    init {
        val supplyAndDemandComplete = player.getI(DEATH_PLATEAU_SUPPLY_TASKS) == 5

        if (player.getQuestStage(Quest.DEATH_PLATEAU) in STAGE_UNSTARTED..STAGE_KILLED_THE_MAP)
            CommanderDenulthD(player, npc)
        else
            player.startConversation {
                player(CALM_TALK, "Hello!")
                npc(npc, CALM_TALK, "Welcome back friend!")
                label("initialOps")
                options {
                    op("How goes your fight with the trolls?") {
                        player(CALM_TALK, "How goes your fight with the trolls?")
                        npc(npc, CALM_TALK, "We are busy preparing for an attack by night. Godric knows of a secret entrance to the stronghold. Once we destroy the stronghold Burthorpe will be safe! Friend, we are indebted to you!")
                        player(CALM_TALK, "Good luck!")
                        goto("initialOps")
                    }
                    if (player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_COMPLETE && !supplyAndDemandComplete)
                        op("Do you need any more help from me?") {
                            DeathPlateauUtils(player).supplyAndDemand(npc, this) }
                    op("I thought the White Knights controlled Asgarnia.") {
                        npc(npc, CALM_TALK, "You are right, citizen. The White Knights have taken advantage of the old and weak king. They control most of Asgarnia, including Falador, but they do not control Burthorpe!")
                        npc(npc, FRUSTRATED, "We are the prince's elite troops! We keep Burthorpe secure!")
                        npc(npc, SAD, "The White Knights have overlooked us, until now! They are pouring money into their war against the Black Knights, so they are looking for an excuse to stop our funding and I'm afraid they may have found it!")
                        npc(npc, FRUSTRATED, "If we can not destroy the troll camp on Death Plateau then the Imperial Guard will be disbanded and Burthorpe will come under control of the White Knights. We cannot let this happen!")
                        goto("initialOps")
                    }
                    op("See you about, Denulth!") {
                        npc(npc, CALM_TALK, "Saradomin be with you, friend!")
                    }
                }
            }
    }
}

@ServerStartupEvent
fun mapCommanderDenulth() {
    onNpcClick(1060) { (player, npc) -> CommanderDenulth(player, npc) }
}
