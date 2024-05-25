package com.rs.game.content.quests.troll_stronghold.dialogue.npcs.burthorpe

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player

class CommanderDenulthDTrollStronghold(player: Player, npc: NPC) {
    init {

        val hasClimbingBoots = player.containsOneItem(CLIMBING_BOOTS_POST_DEATH_PLATEAU) || player.equipment.getId(Equipment.FEET) == CLIMBING_BOOTS_POST_DEATH_PLATEAU
                || player.containsOneItem(ROCK_CLIMBING_BOOTS) || player.equipment.getId(Equipment.FEET) == ROCK_CLIMBING_BOOTS

        player.startConversation {
            when (player.questManager.getStage(Quest.TROLL_STRONGHOLD)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "How goes your fight with the trolls?")
                    npc(npc, CALM_TALK, "I'm afraid I have bad news. We made our attack as planned, but we met unexpected resistance.")
                    player(CALM_TALK, "What happened?")
                    npc(npc, CALM_TALK, "We were ambushed by trolls coming from the north. They captured Dunstan's son, Godric; we tried to follow, but we were repelled at the foot of their stronghold.")
                    options {
                        op("I'm sorry to hear that.") {
                            player(CALM_TALK, "I'm sorry to hear that.")
                        }
                        op("Is there anything I can do to help?") {
                            player(CALM_TALK, "Is there anything I can do to help?")
                            npc(npc, CALM_TALK, "The way to the stronghold is treacherous, friend. Even if you manage to climb your way up, there will be many trolls defending the stronghold.")
                            questStart(Quest.TROLL_STRONGHOLD)
                            player(CALM_TALK, "I'll get Godric back!")
                            npc(npc, CALM_TALK, "Saradomin be with you, friend! I would send some of my men with you, but none of them are brave enough to follow.") { player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_ACCEPTED_QUEST) }
                        }
                    }
                }

                in STAGE_ACCEPTED_QUEST..STAGE_ENGAGED_DAD -> {
                    npc(npc, CALM_TALK, "How are you getting on with rescuing Godric?")
                    if (hasClimbingBoots) {
                        player(CALM_TALK, "I have found a way up but I need to wear these climbing boots.")
                        npc(npc, CALM_TALK, "Then hurry, friend! What are you still doing here?")
                    } else {
                        player(CALM_TALK, "I haven't found a way to climb up yet.")
                        npc(npc, CALM_TALK, "Hurry, friend! Who knows what they'll do with Godric?")
                    }
                }

                STAGE_FINISHED_DAD -> {
                    player(CALM_TALK, "I've defeated the troll champion!")
                    npc(npc, CALM_TALK, "Hurry, friend! What are you still doing here?")
                }

                STAGE_UNLOCKED_PRISON_DOOR -> {
                    player(CALM_TALK, "I've found my way into the prison.")
                    npc(npc, CALM_TALK, "...and?")
                    player(CALM_TALK, "That's all.")
                    npc(npc, CALM_TALK, "Hurry, friend. Find a way to free Godric!")
                }

                STAGE_UNLOCKED_BOTH_CELLS -> {
                    player(CALM_TALK, "I have freed Godric!")
                    npc(npc, CALM_TALK, "Oh, what great news! You should hurry to tell Dunstan, he will be overjoyed!")
                }

            }
        }
    }
}
