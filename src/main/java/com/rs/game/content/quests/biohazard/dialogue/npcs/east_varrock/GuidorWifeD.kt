package com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player

class GuidorWifeD (player: Player, npc: NPC) {
    init {
        val wearingPriestTop = player.equipment.getId(Equipment.CHEST) == PRIEST_GOWN_TOP
        val wearingPriestBottom = player.equipment.getId(Equipment.LEGS) == PRIEST_GOWN_BOTTOM
        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_UNSTARTED..STAGE_FOUND_DISTILLATOR -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, SAD, "Oh hello, I can't chat now. I have to keep an eye on my husband. He's very ill!")
                    player(SAD, "I'm sorry to hear that!")
                }

                in STAGE_UNSTARTED..STAGE_RECEIVED_TOUCH_PAPER -> {
                    if (!wearingPriestTop || !wearingPriestBottom) {
                        player(CALM_TALK, "Hello, I'm a friend of Elena, here to see Guidor.")
                        npc(npc, SAD_CRYING, "I'm afraid...(she sobs)...that Guidor is not long for this world! So I'm not letting people see him now.")
                        player(SAD, "I'm really sorry to hear about Guidor. But I do have some very important business to attend to!")
                        npc(npc, FRUSTRATED, "You heartless rogue! What could be more important than Guidor's life? A life spent well, if not always wisely... I just hope that Saradomin shows mercy on his soul!")
                        player(CONFUSED, "Guidor is a religious man?")
                        npc(npc, SKEPTICAL_HEAD_SHAKE, "Oh god no! But I am! If only I could get him to see a priest!")
                    } else {
                        npc(npc, HAPPY_TALKING, "Father, thank heavens you're here! My husband is very ill! Perhaps you could read him his final rites?")
                        player(CALM_TALK, "I'll see what I can do.")
                    }
                }

                else -> {
                    player(CALM_TALK, "Hello again.")
                    npc(npc, CALM_TALK, "Hello there. I fear Guidor may not be long for this world!")
                }

            }
        }
    }
}
