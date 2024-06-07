package com.rs.game.content.world.areas.burthorpe.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.Skillcapes
import com.rs.game.content.quests.druidic_ritual.dialogues.npcs.KaqemeexD
import com.rs.game.content.quests.druidic_ritual.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Kaqemeex(player: Player, npc: NPC) {
    init {
        val stage = player.getQuestStage(Quest.DRUIDIC_RITUAL)
        when (stage) {
            in STAGE_UNSTARTED..STAGE_RETURN_TO_KAQEMEEX -> {
                KaqemeexD(player, npc)
            }
            else -> {
                player.startConversation {
                    player(CALM_TALK, "Hello there.")
                    options {
                        op("Can you explain herblore?") {
                            npc(npc, CALM_TALK, "Herblore is the skill of working with herbs and other ingredients, to make useful potions and poison. ")
                            npc(npc, CALM_TALK, "First you will need a vial, which can be found or made with the crafting skill. Then you must gather the herbs needed to make the potion you want.")
                            npc(npc, CALM_TALK, "Refer to the Council's instructions in the Skills section of the website for the items needed to make a particular kind of potion. You must fill the vial with water and add the ingredients you need.")
                            npc(npc, CALM_TALK, "There are normally 2 ingredients to each type of potion. Bear in mind, you must first identify each herb, to see what it is. You may also have to grind some herbs before you can use them.")
                            npc(npc, CALM_TALK, "You will need a pestle and mortar in order to do this. Herbs can be found on the ground, and are also dropped by some monsters when you kill them. ")
                            npc(npc, CALM_TALK, "Let's try an example Attack potion: The first ingredient is Guam leaf; the next is Eye of Newt. Mix these in your water-filled vial and you will produce an Attack potion. ")
                            npc(npc, CALM_TALK, "Drink this potion to increase your Attack level. Different potions also require different Herblore levels before you can make them. ")
                            npc(npc, CALM_TALK, "Once again, check the instructions found on the Council's website for the levels needed to make a particular potion.Good luck with your Herblore practices, Good day Adventurer.")
                            player(CALM_TALK, "Thanks for your help.")
                        }
                        op("What is that cape you're wearing?") {
                            player(CALM_TALK, "What is that cape you're wearing?")
                            exec { Skillcapes.Herblore.getOffer99CapeDialogue(player, npc.id) }
                        }
                    }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapKaqemeex() {
    onNpcClick(455, options = arrayOf("Talk-to")) { (player, npc) -> Kaqemeex(player, npc) }
}
