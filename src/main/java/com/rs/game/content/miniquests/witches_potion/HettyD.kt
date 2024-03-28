package com.rs.game.content.miniquests.witches_potion

import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.miniquest.Miniquest
import com.rs.lib.game.Item

class HettyD(p: Player, npc: NPC) {
    init {
        when(p.getMiniquestStage(Miniquest.WITCHES_POTION)) {
            WitchesPotion.NOT_STARTED -> {
                mapNotStarted(p, npc)
            }
            WitchesPotion.NEED_INGREDIENTS -> {
                mapNeedIngredients(p, npc)
            }
            WitchesPotion.HANDED_IN_INGREDIENTS -> {
                mapHandedInIngredients(p, npc)
            }
            WitchesPotion.COMPLETED -> {
                mapCompleted(p, npc)
            }
        }
    }

    private fun mapNotStarted(p: Player, npc: NPC) {
        p.startConversation {
            label("startOfNotStarted")
            npc(npc, CONFUSED, "What could you want with an old woman like me?")
            options {
                op("You look like a witch.") {
                    player(CHEERFUL, "You look like a witch.")
                    npc(npc, WORRIED, "Yes, I suppose I'm not being very subtle about it. I fear I may get a visit from the witch hungers of Falador before long.")
                    goto("startOfNotStarted")
                }
                op("Nothing, thanks.") { player(CALM, "Nothing, thanks") }
                op("I'm looking for work.") {
                    player(CHEERFUL, "I'm looking for work.")
                    npc(npc, SECRETIVE, "Hmmm... Perhaps you could do something that would help both of us.")
                    npc(npc, SECRETIVE, "Would you like to become more proficient in the dark arts?")
                    options {
                        op("No, I have my principles and honour.") {
                            player(FRUSTRATED, "No, I have my principles and honour.")
                            npc(npc, LAUGH, "Suit yourself, but you're missing out.")
                        }
                        op("What, you mean improve my magic?") {
                            player(LAUGH, "What, you mean improve my magic?")
                            simple("The witch sighs.")
                            npc(npc, FRUSTRATED, "Yes, improve your magic... Do you have no sense of drama?")
                            options {
                                op("Yes, I'd like to improve my magic.") {
                                    player(CHEERFUL, "Yes, I'd like to improve my magic.")
                                    simple("The witch sighs.")
                                    exec { mapStartMiniquest(p, npc) }
                                }
                                op("No, I'm not interested.") {
                                    player(CALM, "No, I'm not interested.")
                                    npc(npc, LAUGH, "Many aren't, at first.")
                                    simple("The witch smiles mysteriously.")
                                    npc(npc, LAUGH, "But I think you'll be drawn back to this place.")
                                }
                                op("Show me the mysteries of the dark arts...") {
                                    exec { mapStartMiniquest(p, npc) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mapStartMiniquest(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, HAPPY_TALKING, "OK, I'm going to make a potion to help bring out your darker self. You will need certain ingredients.") {
                p.miniquestManager.setStage(Miniquest.WITCHES_POTION, WitchesPotion.NEED_INGREDIENTS)
            }
            label("startOptions")
            options {
                op("What do I need?") {
                    player(CHEERFUL, "What do I need?")
                    npc(npc, CHEERFUL, "You need an eye of newt, a rat's tail, an onion... Oh, and a piece of burnt meat.")
                    goto("startOptions")
                }
                op("Where can I find a rat's tail?") {
                    player(CONFUSED, "Where can I find a rat's tail?")
                    npc(npc, LAUGH, "On a rat! There are lots of rodents around here; try killing one.")
                    goto("startOptions")
                }
                op("Where can I find burnt meat?") {
                    player(CONFUSED, "Where can I find burnt meat?")
                    npc(npc, CALM, "Find yourself a piece of meat and cook it twice. It'll burn.")
                    goto("startOptions")
                }
                op("Where can I find an onion?") {
                    player(CONFUSED, "Where can I find an onion?")
                    npc(npc, CALM, "There's a field of onions just north of here. No-one will mind if you borrow a few.")
                    goto("startOptions")
                }
                op("Where can I find an eye of newt?") {
                    player(CONFUSED, "Where can I find an eye of newt?")
                    npc(npc, CALM, "There's a shop in Port Sarim, east of here. You can buy newt eyes there.")
                    goto("startOptions")
                }
                op("Okay, I'll be off.") {
                    player(CHEERFUL, "Okay, I'll be off.")
                }
            }
        }
    }

    private fun mapNeedIngredients(p: Player, npc: NPC) {
        val reqItems = listOf(Item(WitchesPotion.ONION_ID), Item(WitchesPotion.RATS_TAIL_ID), Item(WitchesPotion.EYE_OF_NEWT_ID), Item(WitchesPotion.BURNT_MEAT_ID))
        p.startConversation {
            npc(npc, CHEERFUL, "So, have you found the things for the potion?")
            if (p.inventory.containsItems(reqItems)) {
                player(CHEERFUL, "Yes, I have everything!")
                npc(npc, CALM, "Excellent, can I have them then?")
                simple("You pass the ingredients to Hetty and she puts them all into her cauldron. Hetty closes her eyes and begins to chant. The cauldron bubbles mysteriously.")
                player(CONFUSED, "Well, is it ready?")
                npc(npc, EVIL_LAUGH, "Ok, now drink from the cauldron.") {
                    p.inventory.removeItems(reqItems)
                    p.miniquestManager.setStage(Miniquest.WITCHES_POTION, WitchesPotion.HANDED_IN_INGREDIENTS)
                }
            } else {
                player(SAD, "No, not yet.")
                npc(npc, CALM, "Well, remember you need to get an eye of newt, a rat's tail, some burnt meat and an onion.")
            }
        }
    }

    private fun mapHandedInIngredients(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, VERY_FRUSTRATED, "Well, are you going to drink the potion or not?")
        }
    }

    private fun mapCompleted(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, HAPPY_TALKING, "How's your magic coming along?")
            player(CHEERFUL, "I'm practicing and slowly getting better.")
            npc(npc, CALM, "Good, good.")
        }
    }
}
