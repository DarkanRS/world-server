package com.rs.game.content.world.areas.thieves_guild.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.quest.Quest
import com.rs.game.content.miniquests.from_tiny_acorns.DarrenFromTinyAcorns
import com.rs.game.content.miniquests.lost_her_marbles.DarrenLostHerMarbles
import com.rs.game.content.quests.buyersandcellars.npcs.DarrenBuyersAndCellars
import com.rs.game.content.skills.thieving.thievesGuild.PickPocketDummy
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapDarrenLightfingerGeneric() {
    onNpcClick(11273, options = arrayOf("Talk-to")) { (p, npc) ->
        if (!p.isQuestStarted(Quest.BUYERS_AND_CELLARS)) {
            DarrenBuyersAndCellars.preQuest(p, npc)
            return@onNpcClick
        }
        when (p.getQuestStage(Quest.BUYERS_AND_CELLARS)) {
            1 -> DarrenBuyersAndCellars.preQuest(p, npc)
            2 -> DarrenBuyersAndCellars.stage2(p, npc)
            else -> DarrenLightfingerGeneric(p, npc)
        }
    }
}

class DarrenLightfingerGeneric(p: Player, npc: NPC) {
    init {
        p.startConversation {
            exec { stage3(p, npc) }
        }
    }
    private fun stage3(p: Player, npc: NPC) {
        val thievingLevel = p.skills.getLevelForXp(Skills.THIEVING)
        val agilityLevel = p.skills.getLevelForXp(Skills.AGILITY)
        val herbloreLevel = p.skills.getLevelForXp(Skills.HERBLORE)

        p.startConversation {
            npc(npc, HAPPY_TALKING, "Greetings, my young recruit!")
            label("options")
            options {
                op("Can we try out that testing dummy again?") {
                    player(CALM_TALK, "Can we try out that testing dummy again?")
                    npc(npc, HAPPY_TALKING, "Of course!")
                    exec {
                        p.walkToAndExecute(Tile.of(4762, 5903, 0)) { p.actionManager.setAction(PickPocketDummy(GameObject(52316, 1, 4665, 5903, 0))) }
                    }
                }
                op("How's the guild coming along these days?") {
                    if (p.isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN, false)) {
                        //AGuildOfOurOwnTXT
                        goto("options")
                    }
                    if (p.isMiniquestComplete(Miniquest.LOST_HER_MARBLES)) {
                        //LostHerMarblesOptionsTXT
                        goto("options")
                    }
                    if (p.isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
                        player(CALM_TALK, "How's the guild coming along these days?")
                        npc(npc, HAPPY_TALKING, "A coshing tutor has moved in and we've now opened a store. We'll need more funds if we're to continue with renovations. Anything else I can do for you? ")
                        goto("options")
                    } else {
                        player(CALM_TALK, "How's the guild coming along these days?")
                        npc(npc, HAPPY_TALKING, "We're really only getting started at the moment. I've made a training dummy to practice on, but we'll need funds if we're to begin to command any respect. Anything else I can do for you?")
                    goto("options")
                    }
                }
                op("I'd like to talk about capers.") {
                    if (p.isQuestStarted(Quest.BUYERS_AND_CELLARS) && !p.isQuestComplete(Quest.BUYERS_AND_CELLARS)) {
                        exec { buyersAndCellarsOptions(p, npc) }
                    }
                    if (thievingLevel >= 24 && !p.isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
                        exec { DarrenFromTinyAcorns(p, npc) }
                    }
                    if (thievingLevel >= 41 && !p.isMiniquestComplete(Miniquest.LOST_HER_MARBLES)) {
                        exec { DarrenLostHerMarbles(p, npc) }
                    }
                    if (thievingLevel >= 62 && agilityLevel >= 40 && herbloreLevel >= 46 && !p.isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN)) {
                        //AGuildOfOurOwnOptions(player);
                    }
                    npc(npc, HAPPY_TALKING, "I don't have any capers for you at the moment, come back and see me in a little while.")
                }
                op("Sorry, I was just leaving.") { player(CALM, "Sorry, I was just leaving.") }
            }
        }
    }

    private fun buyersAndCellarsOptions(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, HAPPY_TALKING, "Have you retrieved the chalice?")
            when (p.getQuestStage(Quest.BUYERS_AND_CELLARS)) {
                3 -> {
                    player(CALM_TALK, "Not yet.")
                    npc(npc, HAPPY_TALKING, "Head to Lumbridge Castle as soon as you may, then; Robin will meet you there.")
                }

                4 -> {
                    player(CALM_TALK, "Not yet, but I'm on its trail")
                }

                5, 6 -> {
                    player(CALM_TALK, "I've tracked it down, but I've not yet retrieved it.")
                }

                7 -> {
                    player(CALM_TALK, "I have the key, but not the chalice.")
                    npc(npc, CALM_TALK, "You've used several keys in the past, I'm sure; one more should pose no difficulty.")
                }

                8 -> {
                    player(HAPPY_TALKING, "I have!")
                    npc(npc, HAPPY_TALKING, "Fantastic work! I knew I had chosen wisely when I recruited you. Now we can expand the guild and do some proper training around here.")
                    player(SKEPTICAL_HEAD_SHAKE, "Your buyer is still interested, I hope?")
                    npc(npc, CALM_TALK, "Yes, of course, why?")
                    player(CALM_TALK, "Well, the chalice wasn't where you said it was, nor was the owner; I just wanted to make sure you had something right in all of this.")
                    npc(npc, LAUGH, "Ha! I do appreciate a sense of humor in my members.")
                    player(CALM_TALK, "It wasn't actually a joke, to be honest.")
                    npc(npc, SKEPTICAL_HEAD_SHAKE, "To be honest? You don't want to be honest; you're a member of the illustrious Thieves' Guild! Now get out there and make me proud... and both of us rich!")
                    exec {
                        p.fadeScreen {
                            p.inventory.deleteItem(18648, 1)
                            p.tele(Tile.of(3223, 3269, 0))
                            p.vars.saveVarBit(7792, 10)
                            p.vars.setVarBit(7793, 0)
                            p.questManager.completeQuest(Quest.BUYERS_AND_CELLARS)
                        }
                    }
                }
            }
        }
    }
}
