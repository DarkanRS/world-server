package com.rs.game.content.world.areas.thieves_guild.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.quest.Quest
import com.rs.game.content.miniquests.from_tiny_acorns.DarrenFromTinyAcorns
import com.rs.game.content.miniquests.from_tiny_acorns.RobinFromTinyAcorns
import com.rs.game.content.miniquests.lost_her_marbles.DarrenLostHerMarbles
import com.rs.game.content.miniquests.lost_her_marbles.RobinLostHerMarbles
import com.rs.game.content.quests.buyersandcellars.npcs.RobinCastleKt
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapRobinGeneric() {
    onNpcClick(7955, 11268, 11279, 11280, options = arrayOf("Talk-to")) { (p, npc) ->
        val questStage = p.getQuestStage(Quest.BUYERS_AND_CELLARS)
        when (questStage) {
            0 -> RobinCastleKt.preQuest(p, npc)
            1, 2 -> RobinCastleKt.someExtraHelp(p, npc)
            3, 4 -> if (npc.tile == Tile.of(4762, 5904, 0)) {
                RobinCastleKt.someExtraHelp(p, npc)
            } else RobinCastleKt.stage3(p, npc)
            5, 6 -> RobinCastleKt.stage5(p, npc)
            7 -> RobinCastleKt.stage7(p, npc)
            8 -> if (npc.tile == Tile.of(4762, 5904, 0)) {
                RobinCastleKt.someExtraHelp(p, npc)
            } else RobinCastleKt.stage8(p, npc)

            else -> postQuest(p, npc)
        }
    }
}

fun postQuest(p: Player, npc: NPC) {
    p.startConversation {
        player(CALM_TALK, "Hello there.")
        npc(
            npc,
            CALM_TALK,
            "The Guildmaster wanted me to be on hand in case you needed some more hints on picking pockets. Now, what can I do for you?"
        )
        options {
            op("I'm always willing to learn.") {
                player(CALM_TALK, "I'm always willing to learn.")
                npc(
                    npc,
                    CALM_TALK,
                    "When you're on the prowl for pickpocketing targets, it should be fairly obvious who's not paying enough attention to the world around them."
                )
                npc(
                    npc,
                    CALM_TALK,
                    "Just saunter up to them all casual-like, then dip your hand into their wallets as gently and as neatly as you can."
                )
                npc(
                    npc,
                    CALM_TALK,
                    "If you succeed, you'll get some of the contents of their pockets; it not, they'll likely punch you in the face, so be warned."
                )
                npc(npc, CALM_TALK, "It stings, and you'll need a moment to gather your wits.")
                player(CALM_TALK, "Thanks, Robin.")
                npc(
                    npc,
                    CALM_TALK,
                    "You can use the training dummy if you'd like, but after a while you'll need to switch to real marks if you want to improve."
                )
            }
            if (p.questManager.isComplete(Quest.BUYERS_AND_CELLARS)) {
                op("I'd like to talk about capers.") {
                    if (p.isMiniquestStarted(Miniquest.FROM_TINY_ACORNS) && !p.isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
                        exec { RobinFromTinyAcorns(p, npc) }
                    }
                    if (p.isMiniquestStarted(Miniquest.LOST_HER_MARBLES) && !p.isMiniquestComplete(Miniquest.LOST_HER_MARBLES)) {
                        exec { RobinLostHerMarbles(p, npc) }
                    }
                    if (p.isMiniquestStarted(Miniquest.A_GUILD_OF_OUR_OWN) && !p.isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN)) {
                        //AGuildOfOurOwnOptions(player);
                    }
                    npc(
                        npc,
                        HAPPY_TALKING,
                        "You aren't running a caper at the moment, Darren may have something for you to do."
                    )
                }
                op("I've got it, thanks.") {
                    player(CALM_TALK, "I’ve got it, thanks.")
                    npc(
                        npc,
                        CALM_TALK,
                        "You can use the training dummy if you'd like, but after a while you'll need to switch to real marks if you want to improve."
                    )
                }
                op("Bye for now.") { player(CALM_TALK, "Bye for now.") }
            }
        }
    }
}
