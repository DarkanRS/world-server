package com.rs.game.content.quests.druidic_ritual.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.Skillcapes
import com.rs.game.content.quests.druidic_ritual.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class KaqemeexD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DRUIDIC_RITUAL)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "What brings you to our holy monument?")
                    options {
                        op("Who are you?") {
                            player(CALM_TALK, "Who are you?")
                            npc(npc, CALM_TALK, "We are the druids of Guthix. We worship our god at our famous stone circles. You will find them located throughout these lands.")
                            options {
                                op("So what's so good about Guthix?") {
                                    player(CALM_TALK, "So what's so good about Guthix?")
                                    npc(npc, CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
                                    npc(npc, CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
                                    player(CALM_TALK, "He sounds kind of boring...")
                                    npc(npc, CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power.")
                                }
                                op("Well, I'll be on my way now.")
                            }
                        }
                        op("I'm in search of a quest.") {
                            player(CALM_TALK, "I'm in search of a quest.")
                            npc(npc, CALM_TALK, "Hmm. I think I may have a worthwhile quest for you actually. I don't know if you are familiar with the stone circle south of Varrock or not, but...")
                            player(CALM_TALK, "What about the stone circle full of dark wizards?")
                            npc(npc, CALM_TALK, "That used to be OUR stone circle. Unfortunately, many many years ago, dark wizards cast a wicked spell upon it so that they could corrupt its power for their own evil ends.")
                            npc(npc, CALM_TALK, "When they cursed the rocks for their rituals they made them useless to us and our magics. We require a brave adventurer to go on a quest for us to help purify the circle of Varrock.")
                            options {
                                op("Okay, I will try and help.") {
                                    player(CALM_TALK, "Okay, I will try and help.")
                                    npc(npc, CALM_TALK, "Excellent. Go to the village south of this place and speak to my fellow Sanfew who is working on the purification ritual. He knows better than I what is required to complete it.")
                                    player(CALM_TALK, "Will do.") { player.setQuestStage(Quest.DRUIDIC_RITUAL, STAGE_SPEAK_TO_SANFEW) }
                                }
                                op("No, that doesn't sound very interesting.")
                            }
                        }
                        opExec("What is that cape you're wearing?") { Skillcapes.Herblore.getOffer99CapeDialogue(player, npc.id) }
                    }

                }

                in STAGE_SPEAK_TO_SANFEW..STAGE_GATHER_MEATS -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "What brings you to our holy monument?")
                    options {
                        op("Who are you?") {
                            player(CALM_TALK, "Who are you?")
                            npc(npc, CALM_TALK, "We are the druids of Guthix. We worship our god at our famous stone circles. You will find them located throughout these lands.")
                            options {
                                op("So what's so good about Guthix?") {
                                    player(CALM_TALK, "So what's so good about Guthix?")
                                    npc(npc, CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
                                    npc(npc, CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
                                    player(CALM_TALK, "He sounds kind of boring...")
                                    npc(npc, CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power.")
                                }
                                op("Well, I'll be on my way now.")
                            }
                        }
                        op("About Druidic Ritual.") {
                            player(CALM_TALK, "What did you want me to do again?")
                            npc(
                                npc, CALM_TALK, "Go to the village south of this place and speak to my fellow " +
                                        "Sanfew who is working on the purification ritual. He knows better than I what is required to complete it."
                            )
                            player(CALM_TALK, "Will do.")
                        }
                        opExec("What is that cape you're wearing?") { Skillcapes.Herblore.getOffer99CapeDialogue(player, npc.id) }
                    }
                }

                STAGE_RETURN_TO_KAQEMEEX -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "I have word from Sanfew that you have been very helpful in assisting him with his preparations for the purification ritual. As promised I will now teach you the ancient arts of Herblore.")
                    exec { player.questManager.completeQuest(Quest.DRUIDIC_RITUAL) }
                }

            }
        }
    }
}
