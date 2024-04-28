package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ClerkD (player: Player, npc: NPC) {
    init {
        var hasAttemptedPrisonHouse = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ATTEMPTED_PRISON_HOUSE_DOORS)
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                in STAGE_UNSTARTED..STAGE_SPOKEN_TO_MILLI,
                in STAGE_GAVE_HANGOVER_CURE..STAGE_COMPLETE -> {
                    npc(npc, HAPPY_TALKING, "Hello, welcome to the Civic Office of West Ardougne. How can I help you?")
                    options {
                        if (hasAttemptedPrisonHouse && player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GAVE_HANGOVER_CURE) {
                            op("I need permission to enter a plague house.") {
                                player(CALM_TALK, "I need permission to enter a plague house.")
                                npc(npc, CALM_TALK, "Rather you than me! The mourners normally deal with that stuff, you should speak to them. Their headquarters are right near the city gate.")
                                options {
                                    op("I'll try asking them then.") { player(CALM_TALK, "I'll try asking them then.") }
                                    op("Surely you don't let them run everything for you?") {
                                        player(CALM_TALK, "Surely you don't let them run everything for you?")
                                        npc(npc, CALM_TALK, "Well, they do know what they're doing here. If they did start doing something badly Bravek, the city warder, would have the power to override them. I can't see that happening though.")
                                        options {
                                            op("I'll try asking them then.") { player(CALM_TALK, "I'll try asking them then.") }
                                            op("Can I speak to Bravek anyway?") {
                                                player(CALM_TALK, "Can I speak to Bravek anyway?")
                                                npc(npc, CALM_TALK, "He has asked not to be disturbed.")
                                                options {
                                                    op("This is urgent though!") {
                                                        player(FRUSTRATED, "This is urgent though!")
                                                        goto("thisIsUrgent")
                                                    }
                                                    op("Ok, I'll leave him alone.") { player(CALM_TALK, "Ok, I'll leave him alone.") }
                                                    op("Do you know when he will be available?") {
                                                        player(CALM_TALK, "Do you know when he will be available?")
                                                        goto("whenHeWillBeAvailable")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    op("This is urgent though!") {
                                        player(FRUSTRATED, "This is urgent though!")
                                        label("thisIsUrgent")
                                        npc(npc, CALM_TALK, "I'll see what I can do I suppose.")
                                        npc(npc, CALM_TALK, "Mr Bravek, there's a man here who really needs to speak to you.")
                                        npc(BRAVEK, CALM_TALK, "I suppose they can come in then. If they keep it short.") {
                                            player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_PERMISSION_TO_BRAVEK)
                                        }
                                    }
                                }
                            }
                        }
                        op("Who is through that door?") {
                            player(CALM_TALK, "Who is through that door?")
                            npc(npc, CALM_TALK, "The city warder Bravek is in there.")
                            player(CALM_TALK, "Can I go in?")
                            npc(npc, CALM_TALK, "He has asked not to be disturbed.")
                            options {
                                if (hasAttemptedPrisonHouse) {
                                    op("This is urgent though!") {
                                        player(FRUSTRATED, "This is urgent though!")
                                        goto("thisIsUrgent")
                                    }
                                }
                                op("Ok, I'll leave him alone.") { player(CALM_TALK, "Ok, I'll leave him alone.") }
                                op("Do you know when he will be available?") {
                                    player(CALM_TALK, "Do you know when he will be available?")
                                    label("whenHeWillBeAvailable")
                                    npc(npc, CALM_TALK, "Oh I don't know, an hour or so maybe.")

                                }
                            }
                        }
                        op("I'm just looking thanks.") {
                            player(CALM_TALK, "I'm just looking thanks.")
                        }
                    }
                }

                STAGE_PERMISSION_TO_BRAVEK -> {
                    npc(npc, HAPPY_TALKING, "Bravek will see you now but keep it short!")
                    player(CALM_TALK, "Thanks, I won't take much of his time.")
                }

            }
        }
    }
}
