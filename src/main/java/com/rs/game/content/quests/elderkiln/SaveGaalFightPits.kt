package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val LOLTHENKILL = 15152
const val ODISCHAMP = 15153
const val FIRECAPEZORZ = 15154
const val NOREMORSE77 = 15155
const val FIGHTPITPKER = 15156
const val KET_HARDENED = 15168
const val XIL_HARDENED = 15169
const val GAAL_XOX = 15135

@ServerStartupEvent
fun mapSaveGaalFightPits() {
    onNpcClick(LOLTHENKILL) { it.player.npcDialogue(it.npc, HeadE.CONFUSED, "Yea wut?") }
    onNpcClick(ODISCHAMP) { it.player.npcDialogue(it.npc, HeadE.CALM_TALK, "Hurry up, let's just get in the Pits. I wanna go kill frosties.") }
    onNpcClick(FIRECAPEZORZ) { it.player.npcDialogue(it.npc, HeadE.CONFUSED, "These noobs don't stand a chance.") }
    onNpcClick(NOREMORSE77) { it.player.npcDialogue(it.npc, HeadE.CONFUSED, "Have you seen any dragon claws lying around? I can't go into the Pits without my claws!") }
    onNpcClick(FIGHTPITPKER) { (player, npc) ->
        player.startConversation {
            npc(npc, HeadE.CALM_TALK, "Ten points if you kill another person; twenty if you kill a TzHaar. A point each for the Ga'al; they're fodder, dude.")
            player(HeadE.CONFUSED, "What are you doing?")
            npc(npc, HeadE.CALM_TALK, "Just making this a bit more fun.")
        }
    }
    onNpcClick(KET_HARDENED) { (player, npc) ->
        player.startConversation {
            npc(npc, HeadE.T_CALM_TALK, "Are JalYt always this loud?")
            npc(npc, HeadE.T_CALM_TALK, "Speak to TzHaar-Mej-Ak if you want fight in Fight Pit.")
        }
    }
    onNpcClick(XIL_HARDENED) { (player, npc) ->
        player.startConversation {
            npc(npc, HeadE.T_CALM_TALK, "We here for fight, not talk.")
            npc(npc, HeadE.T_CALM_TALK, "Speak to TzHaar-Mej-Ak if you want fight in Fight Pit.")
        }
    }
    onNpcClick(GAAL_XOX) { (player, npc) ->
        player.startConversation {
            npc(npc, HeadE.T_CALM_TALK, "JalYt?")
            player(HeadE.CHEERFUL, "Wow...you can talk.")
            npc(npc, HeadE.T_CALM_TALK, "Ga'al-Xox been here for long time. Ga'al-Xox one of first Ga'al.")
            player(HeadE.CHEERFUL, "You've taught yourself to speak. That's impressive.")
            npc(npc, HeadE.T_CALM_TALK, "Ga'al-Xox speak good. Ga'al-Xox real TzHaar.")
            player(HeadE.CONFUSED, "What are you doing here?")
            npc(npc, HeadE.T_CALM_TALK, "Ga'al-Xox here to die with honor!")
            player(HeadE.CONFUSED, "Er-")
            npc(npc, HeadE.T_CALM_TALK, "Ga'al-Xox fight in Fight Pit with great honor!")
            label("ops")
            options {
                op("Can you help me convince that Ga'al to come with me?") {
                    npc(npc, HeadE.T_CALM_TALK, "No, no. Ga'al here to die with honor!")
                    player(HeadE.CALM_TALK, "But TzHaar-Mej-Jeh has a plan that will be able to get the Ga'al's memories back.")
                    npc(npc, HeadE.T_CALM_TALK, "Ga'al meant to stay here!")
                    goto("ops")
                }
                op("I need a Ga'al. Would you come with me? Jeh wouldn't know the difference.") {
                    npc(npc, HeadE.T_CALM_TALK, "Why JalYt need Ga'al?")
                    player(HeadE.SKEPTICAL, "TzHaar-Mej-Jeh said he wanted his Ga'al, but you would do just as well. I'm sure he wouldn't know the difference.")
                    player(HeadE.CALM_TALK, "So, Ga'al, will you come with me? TzHaar-Mej-Jeh thinks there is a way of giving you back your memories and turning you into a real TzHaar.")
                    npc(npc, HeadE.T_CALM_TALK, "Ga'al-Xox meant to stay here. Ga'al-Xox die with great honor!")
                    goto("ops")
                }
                op("I need to go.")
            }
        }
    }
}

fun saveGaalFightPitsAkDialogue(player: Player, npc: NPC) {
    player.startConversation {
        npc(npc.id, HeadE.T_CALM_TALK, "TzHaar! JalYt! Gather here for the Fight Pit.")
        npc(LOLTHENKILL, HeadE.CALM_TALK, "This is going to be sweet!")
        npc(NOREMORSE77, HeadE.CONFUSED, "Anyone seen my D claws?")
        npc(npc.id, HeadE.T_CALM_TALK, "Mighty warriors, prove your worth in fight to death!")
        npc(FIGHTPITPKER, HeadE.CALM_TALK, "So, are we all agreed on how many points you get per kill?")
        npc(ODISCHAMP, HeadE.CALM_TALK, "Nobody cares about your point system, Morean. It's stupid.")
        npc(FIGHTPITPKER, HeadE.ANGRY, "I'm not Morean, I'm FightpitPKer! Master of the bow, ruler of the P-")
        npc(KET_HARDENED, HeadE.T_ANGRY, "JalYt, be quiet!")
        npc(npc.id, HeadE.T_CALM_TALK, "Ah, you again. You want to fight in Fight Pit?")
        options {

        }
    }
}