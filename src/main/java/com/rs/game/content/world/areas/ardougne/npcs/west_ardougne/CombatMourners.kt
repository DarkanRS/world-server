package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class CombatMourners(player: Player, npc: NPC) {
    init {
        player.startConversation {
            val isInMournerHQ = BiohazardUtils(player).isInMournerHQ(npc.tile)
            val isInMournerHQGarden = BiohazardUtils(player).isInMournerHQGarden(npc.tile)
            val isPlayerWearingDoctorsGown = player.equipment.getId(Equipment.CHEST) == DOCTORS_GOWN
            val gotDistillator = player.questManager.getAttribs(Quest.BIOHAZARD).getB(GOT_DISTILLATOR)
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_APPLE_IN_CAULDRON..STAGE_SPEAK_TO_KING -> {
                    if (npc.tile.plane == 1 && isInMournerHQ) {
                        if (!isPlayerWearingDoctorsGown) {
                            player.sendMessage("The mourner doesn't feel like talking.")
                        } else {
                            player(CALM_TALK, "Hello there.")
                            npc(npc, WORRIED, "You're here at last! I don't know what I've eaten but I feel like I'm on death's door.")
                            player(CALM_TALK, "Hmm... interesting, sounds like food poisoning.")
                            npc(npc, WORRIED, "Yes, I'd figured that out already. What can you give me to help?")
                            options {
                                op("Just hold your breath and count to ten.") {
                                    player(CALM_TALK, "Just hold your breath and count to ten.")
                                    npc(npc, CONFUSED, "What? How will that help? What kind of doctor are you?")
                                    player(CALM_TALK, "Erm... I'm new, I just started.")
                                    npc(npc, FRUSTRATED, "You're no doctor!")
                                    exec { npc.combatTarget = player }
                                }
                                op("The best I can do is pray for you.") {
                                    player(CALM_TALK, "The best I can do is pray for you.")
                                    npc(npc, FRUSTRATED, "Pray for me? You're no doctor... You're an imposter!")
                                    exec { npc.combatTarget = player }
                                }
                                op("There's nothing I can do, it's fatal.") {
                                    player(CALM_TALK, "There's nothing I can do, it's fatal.")
                                    npc(npc, SAD, "No, I'm too young to die! I've never even had a girlfriend.")
                                    player(CALM_TALK, "That's life for you.")
                                    npc(npc, CONFUSED, "Wait a minute, where's your equipment?")
                                    player(WORRIED, "It's erm... at home.")
                                    npc(npc, FRUSTRATED, "You're no doctor!")
                                    exec { npc.combatTarget = player }
                                }
                            }
                        }
                    } else if (npc.tile.plane == 0 && isInMournerHQ || isInMournerHQGarden) {
                        if (isPlayerWearingDoctorsGown && !gotDistillator) {
                            player(CALM_TALK, "Hello there.")
                            npc(npc, WORRIED, "Oh dear oh dear. I feel terrible, I think it was the stew.")
                            player(CALM_TALK, "You should be more careful with your ingredients.")
                            npc(npc, WORRIED, "There is one mourner, who's really sick, resting upstairs. You should see to him first.")
                            player(CALM_TALK, "Ok, I'll see what I can do.")
                        } else if (isPlayerWearingDoctorsGown && gotDistillator) {
                            player(CALM_TALK, "Hello.")
                            npc(npc, WORRIED, "Hello Doc, I feel terrible. I think it was the stew.")
                            player(CALM_TALK, "Be more careful with your ingredients next time.")
                        } else {
                            player(CALM_TALK, "Hello there.")
                            npc(npc, WORRIED, "Oh dear oh dear. I feel terrible, I think it was the stew.")
                            player(CALM_TALK, "You should be more careful with your ingredients.")
                            npc(npc, FRUSTRATED, "I need a doctor. The nurse's hut is to the south west. Go now and bring us a doctor, that's an order.")
                        }
                    } else if (!isInMournerHQ) {
                        npc(npc, FRUSTRATED, "Stand back citizen, do not approach me.")
                    }
                }

                else -> npc(npc, FRUSTRATED, "Stand back citizen, do not approach me.")

            }
        }
    }
}

@ServerStartupEvent
fun mapCombatMourners() {
    onNpcClick(347, 348, 357, 369, 370, 371, options = arrayOf("Talk-to")) { (player, npc) ->
        CombatMourners(player, npc)
    }
}
