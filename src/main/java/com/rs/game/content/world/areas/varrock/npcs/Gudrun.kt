package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val GUDRUN = 2869

@ServerStartupEvent
fun mapGudrunTalk() {
    onNpcClick(GUDRUN) { (player, npc) ->
        mapGudrunDialogue(player, npc)
    }
}

fun mapGudrunDialogue(player: Player, npc: NPC) {
    val dororanCraftingTasks: Int = player.getCounterValue(DORORAN_CRAFT_TASKS_KEY)

    player.startConversation {
        npc(npc, HAPPY_TALKING, "Hello!")
        options {
            op("I want to ask you something.") {
                npc(npc, HAPPY_TALKING, "Of course, what is it?")
                label("initialOptions")
                options {
                    op("How are things with Dororan?") {
                        npc(npc, HAPPY_TALKING, "I really like him. He's funny, vulnerable and nothing like my people.")
                        label("nothingLikeMyPeopleOptions")
                        options {
                            op("You're going to stay together then?") {
                                npc(npc, HAPPY_TALKING, "Of course!")
                                goto("nothingLikeMyPeopleOptions")
                            }
                            op("I want to ask about something else.") {
                                player(HAPPY_TALKING, "I want to ask about something else.")
                                goto("initialOptions")
                            }
                            op("Goodbye.") {
                                label("goodbye")
                                npc(npc, CALM_TALK, "Oh, goodbye!")
                            }
                        }
                    }
                    op("Where did this house come from?") {
                        npc(npc, CONFUSED, "I don't know. Papa said the previous owners left it to him. I don't know why they would do that.")
                        label("whyTheyWouldDoThatOptions")
                        options {
                            op("Do you have a theory?") {
                                npc(npc, SKEPTICAL_THINKING, "Gunnar always said 'A warrior does not barter; he simply takes!'. I think papa bought the house, but doesn't want anyone to know.")
                                goto("whyTheyWouldDoThatOptions")
                            }
                            op("I want to ask about something else.") {
                                player(HAPPY_TALKING, "I want to ask about something else.")
                                goto("initialOptions")
                            }
                            op("Goodbye.") {
                                player(HAPPY_TALKING, "Goodbye.")
                                goto("goodbye")
                            }
                        }
                    }
                    if (dororanCraftingTasks > 0)
                        op("Did you like your present?") {
                            when (dororanCraftingTasks) {
                                1 -> {
                                    npc(npc, HAPPY_TALKING, "Look at this ruby bracelet he got for me! 'With beauty blessed.' When I recited that line to papa, it took my breath away.")
                                }
                                2 -> {
                                    npc(npc, HAPPY_TALKING, "I love this dragonstone necklace he got for me! Isn't it wonderful? I might have it enchanted, so I can easily visit Al Kharid.")
                                }
                                3 -> {
                                    npc(npc, HAPPY_TALKING, "I love my new onyx amulet! Aren't these things really expensive? \"The most beautiful girl in the room.\" Dororan is so sweet! Now, whichever room I'm in, I know I'm the most beautiful!")
                                }
                            }
                            goto("initialOptions")
                        }
                    op("Goodbye.") {
                        player(HAPPY_TALKING, "Goodbye.")
                        goto("goodbye")
                    }
                }
            }
            op("Just passing through.")
        }
    }
}
