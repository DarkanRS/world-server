package com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.trollheim

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class EadgarD(player: Player, npc: NPC) {
    init {
        val stage = player.getQuestStage(Quest.EADGARS_RUSE)
        val hasParrot = player.containsOneItem(DRUNK_PARROT) || player.bank.containsItem(DRUNK_PARROT)
        val hasFakeMan = player.containsOneItem(FAKE_MAN_ITEM) || player.bank.containsItem(FAKE_MAN_ITEM)

        player.startConversation {
            when (stage) {

                STAGE_SPEAK_TO_EADGAR -> {
                    player(CALM_TALK, "I need to find some goutweed. Sanfew said you might be able to help.")
                    npc(npc, SKEPTICAL, "Sanfew, you say? Ah, haven't seen him in a while.")
                    npc(npc, CALM_TALK, "Goutweed is used as an ingredient in troll cooking. You should ask one of their cooks.") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_SPEAK_TO_BURNTMEAT) }
                }

                STAGE_SPEAK_TO_BURNTMEAT -> {
                    npc(npc, CALM_TALK, "Oh, it's you. Have you talked to the troll cook yet?")
                    player(CALM_TALK, "Not yet.")
                    npc(npc, CALM_TALK, "Well, you should do that then.")
                    npc(npc, CALM_TALK, "Anything else I can do for you?")
                    exec { defaultEadgarOptions(player, npc) }
                }

                STAGE_BRING_HUMAN -> {
                    npc(npc, CALM_TALK, "Oh, it's you. Have you talked to the troll cook yet?")
                    player(TALKING_ALOT, "I talked to the troll cook but he wouldn't tell me anything, and now he wants me to find him some tasty human for his stew.")
                    npc(npc, SAD, "Oh dear, that's no good. You can't just go hand over a human to those trolls...")
                    npc(npc, HAPPY_TALKING, "Aha! I have a plan!")
                    player(CALM_TALK, "Really?")
                    npc(npc, HAPPY_TALKING, "Yes! It's bound to work. First of all, I will need a parrot!")
                    player(CONFUSED, "A PARROT? Where am I going to find one of those?")
                    npc(npc, HAPPY_TALKING, "At the zoo, where else?") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_GET_PARROT) }
                    npc(npc, CALM_TALK, "Anything else I can do for you?")
                    exec { defaultEadgarOptions(player, npc) }
                }

                STAGE_GET_PARROT -> {
                    npc(npc, HAPPY_TALKING, "Oh, it's you. Have you got me a parrot yet?")
                    if (!player.containsOneItem(DRUNK_PARROT)) {
                        player(SAD_MILD_LOOK_DOWN, "I haven't been able to find one yet.")
                        npc(npc, CALM_TALK, "Well, go see if the zoo have one.")
                        options {
                            op("Where's the zoo?") {
                                player(CONFUSED, "Where's the zoo?")
                                npc(npc, CALM_TALK, "It's in Ardougne, south west of here.")
                                player(CALM_TALK, "Okay, I'll be right back.")
                                npc(npc, CALM_TALK, "Anything else I can do for you?")
                                exec { defaultEadgarOptions(player, npc) }
                            }
                            op("Okay, I'll be right back.") {
                                player(CALM_TALK, "Okay, I'll be right back.")
                                npc(npc, CALM_TALK, "Anything else I can do for you?")
                                exec { defaultEadgarOptions(player, npc) }
                            }
                        }
                    } else {
                        npc(PARROT, T_CALM_TALK, "Raaawk! Polly wanna cracker!")
                        player(TALKING_ALOT, "Here it is! Now are you going to explain your plan?")
                        npc(npc, HAPPY_TALKING, "Yes, yes, of course. It's quite ingenious really!")
                        npc(npc, CALM_TALK, "What we need is something that looks like a human, sounds like a human, smells like a human and tastes like a human.")
                        player(SKEPTICAL_THINKING, "How are we going to make it look like a human?")
                        npc(npc, CALM_TALK, "We'll just make a scarecrow. We'll need logs, and 10 sheaves of wheat for stuffing.")
                        player(SKEPTICAL_THINKING, "How are we going to make it sound like a human?")
                        npc(npc, HAPPY_TALKING, "That's what the parrot's for, of course.")
                        npc(PARROT, T_CALM_TALK, "Who's a pretty boy then?")
                        npc(npc, CALM_TALK, "Although the trolls might get suspicious if it doesn't say what they expect a human to say...")
                        npc(npc, CALM_TALK, "You should hide it for a while somewhere it can pick up some more appropriate catchphrases.")
                        player(SKEPTICAL_THINKING, "How are we going to make it smell like a human?")
                        npc(npc, CALM_TALK, "Well, we'll need to dress it up anyway; if we use dirty clothes it'll smell human to the trolls.")
                        npc(npc, CALM_TALK, "I'm a man of few clothes myself, so you might want to ask old Sanfew about that one.")
                        player(CALM_TALK, "And how are we going to make it taste like a human?")
                        npc(npc, HAPPY_TALKING, "That's easy, we'll just stuff it with a few chickens. Everything tastes like chicken! Five raw chickens should be enough.")
                        npc(npc, CALM_TALK, "That's the plan. Anything else I can do for you?") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_NEED_TO_HIDE_PARROT) }
                        exec { defaultEadgarOptions(player, npc) }
                    }
                }

                STAGE_NEED_TO_HIDE_PARROT -> {
                    player(CALM_TALK, "What should I do with this parrot?")
                    npc(npc, CALM_TALK, "I told you, put it somewhere it'll learn to say the sort of thing the trolls will expect it to say.")
                    npc(npc, CALM_TALK, "This is likely to be screaming. There's bound to be a good spot somewhere in the troll prison.")
                    npc(npc, CALM_TALK, "Anything else I can do for you?")
                    label("parrotOps")
                    options {
                        op("How are we going to make it look like a human?") {
                            player(CALM_TALK, "How are we going to make it look like a human?")
                            npc(npc, CALM_TALK, "We'll just make a scarecrow. We'll need logs, and 10 sheaves of wheat for stuffing.")
                            npc(npc, CALM_TALK, "Anything else I can do for you?")
                            goto("parrotOps")
                        }
                        op("How are we going to make it sound like a human?") {
                            player(CALM_TALK, "How are we going to make it sound like a human?")
                            npc(npc, HAPPY_TALKING, "That's what the parrot's for, of course. You should hide it for a while somewhere it can pick up some more appropriate catchphrases.")
                            npc(npc, CALM_TALK, "Anything else I can do for you?")
                            goto("parrotOps")
                        }
                        op("How are we going to make it smell like a human?") {
                            player(CALM_TALK, "How are we going to make it smell like a human?")
                            npc(npc, HAPPY_TALKING, "Well, we'll need to dress it up anyway; if we use dirty clothes it'll smell human to the trolls.")
                            npc(npc, CALM_TALK, "I'm a man of few clothes myself, so you might want to ask old Sanfew about that one.")
                            npc(npc, CALM_TALK, "Anything else I can do for you?")
                            goto("parrotOps")
                        }
                        op("How are we going to make it taste like a human?") {
                            player(CALM_TALK, "And how are we going to make it taste like a human?")
                            npc(npc, HAPPY_TALKING, "That's easy, we'll just stuff it with a few chickens. Everything tastes like chicken! Five raw chickens should be enough.")
                            npc(npc, CALM_TALK, "Anything else I can do for you?")
                            goto("parrotOps")
                        }
                        op("More") {
                            exec { defaultEadgarOptions(player, npc) }
                        }
                    }
                }

                STAGE_HIDDEN_PARROT -> {
                    val logsCount = player.inventory.getAmountOf(NORMAL_LOGS)
                    val chickensCount = player.inventory.getAmountOf(RAW_CHICKEN)
                    val wheatCount = player.inventory.getAmountOf(WHEAT)
                    val clothesCount = player.inventory.getAmountOf(DIRTY_ROBE)

                    val questAttribs = player.questManager.getAttribs(Quest.EADGARS_RUSE)

                    val handedInLogs = questAttribs.getI(NORMAL_LOGS.toString())
                    val handedInChickens = questAttribs.getI(RAW_CHICKEN.toString())
                    val handedInWheat = questAttribs.getI(WHEAT.toString())
                    val handedInClothes = questAttribs.getI(DIRTY_ROBE.toString())

                    val logsDescription = if (logsCount >= 1) "the logs" else "no logs"
                    val chickensDescription = if (chickensCount >= 5) "five chickens" else "no chickens"
                    val wheatDescription = if (wheatCount >= 10) "ten bundles of wheat" else "no wheat"
                    val clothesDescription = if (clothesCount >= 1) "the dirty clothes" else "no dirty clothes"

                    val allItemsHandedIn = handedInLogs >= 1 && handedInChickens >= 5 && handedInWheat >= 10 && handedInClothes >= 1

                    if (allItemsHandedIn) {
                        npc(npc, CALM_TALK, "That's everything!")
                        npc(npc, HAPPY_TALKING, "Good, good, everything is almost finished.")
                        npc(npc, CALM_TALK, "Of course, we can't just give him the dummy and expect to get anything useful out of him.")
                        player(CALM_TALK, "Oh?")
                        npc(npc, CALM_TALK, "No, of course not. We need to make sure he'll tell you what you need to know.")
                        player(SKEPTICAL_THINKING, "And how do we do that?")
                        npc(npc, TALKING_ALOT, "I happen to know that the trolls are susceptible to a certain kind of plant that grows around this mountain. I call it Troll Thistle. If properly prepared, it can be made into a sort of Troll truth potion.")
                        player(SKEPTICAL_THINKING, "How do you prepare it?")
                        npc(npc, CALM_TALK, "You'll have to dry it in a fire, then grind it and mix it into a potion with Ranarr weed.")
                        player(CALM_TALK, "Okay, I'll be back with that soon.") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_NEED_TROLL_POTION) }
                        npc(npc, CALM_TALK, "Anything else I can do for you?")
                        exec { defaultEadgarOptions(player, npc) }
                    } else {
                        if (!questAttribs.getB(HIDDEN_PARROT_EADGAR_CHAT)) {
                            player(CALM_TALK, "I hid the parrot under the rack in the troll prison.") {
                                questAttribs.setB(HIDDEN_PARROT_EADGAR_CHAT, true)
                            }
                            npc(npc, HAPPY_TALKING, "Good work. How about those other items?")
                        } else {
                            npc(npc, HAPPY_TALKING, "How are you getting on?")
                        }

                        player(CALM_TALK, "I have $logsDescription, $chickensDescription, $wheatDescription, and $clothesDescription.") {
                            if (logsCount > 0 && handedInLogs < 1) {
                                EadgarsRuseUtils(player).handInItems(NORMAL_LOGS, logsCount)
                                questAttribs.setI(NORMAL_LOGS.toString(), handedInLogs + logsCount)
                            }
                            if (chickensCount >= 5 && handedInChickens < 5) {
                                EadgarsRuseUtils(player).handInItems(RAW_CHICKEN, chickensCount)
                                questAttribs.setI(RAW_CHICKEN.toString(), handedInChickens + chickensCount)
                            }
                            if (wheatCount >= 10 && handedInWheat < 10) {
                                EadgarsRuseUtils(player).handInItems(WHEAT, wheatCount)
                                questAttribs.setI(WHEAT.toString(), handedInWheat + wheatCount)
                            }
                            if (clothesCount > 0 && handedInClothes < 1) {
                                EadgarsRuseUtils(player).handInItems(DIRTY_ROBE, clothesCount)
                                questAttribs.setI(DIRTY_ROBE.toString(), handedInClothes + clothesCount)
                            }
                        }

                        exec {
                            player.startConversation {
                                val updatedHandedInLogs = questAttribs.getI(NORMAL_LOGS.toString())
                                val updatedHandedInChickens = questAttribs.getI(RAW_CHICKEN.toString())
                                val updatedHandedInWheat = questAttribs.getI(WHEAT.toString())
                                val updatedHandedInClothes = questAttribs.getI(DIRTY_ROBE.toString())

                                val allItemsHandedInNow = updatedHandedInLogs >= 1 && updatedHandedInChickens >= 5 && updatedHandedInWheat >= 10 && updatedHandedInClothes >= 1

                                if (!allItemsHandedInNow) {
                                    val logsDescriptionNeeded = if (updatedHandedInLogs >= 1) "no logs" else "the logs"
                                    val chickensDescriptionNeeded = if (updatedHandedInChickens >= 5) "no chickens" else "five chickens"
                                    val wheatDescriptionNeeded = if (updatedHandedInWheat >= 10) "no wheat" else "ten bundles of wheat"
                                    val clothesDescriptionNeeded = if (updatedHandedInClothes >= 1) "no dirty clothes" else "the dirty clothes"
                                    npc(npc, CALM_TALK, "You now need $logsDescriptionNeeded, $chickensDescriptionNeeded, $wheatDescriptionNeeded, and $clothesDescriptionNeeded.")
                                } else {
                                    npc(npc, CALM_TALK, "That's everything!")
                                    npc(npc, HAPPY_TALKING, "Good, good, everything is almost finished.")
                                    npc(npc, CALM_TALK, "Of course, we can't just give him the dummy and expect to get anything useful out of him.")
                                    player(CALM_TALK, "Oh?")
                                    npc(npc, CALM_TALK, "No, of course not. We need to make sure he'll tell you what you need to know.")
                                    player(SKEPTICAL_THINKING, "And how do we do that?")
                                    npc(npc, TALKING_ALOT, "I happen to know that the trolls are susceptible to a certain kind of plant that grows around this mountain. I call it Troll Thistle. If properly prepared, it can be made into a sort of Troll truth potion.")
                                    player(SKEPTICAL_THINKING, "How do you prepare it?")
                                    npc(npc, CALM_TALK, "You'll have to dry it in a fire, then grind it and mix it into a potion with Ranarr weed.")
                                    player(CALM_TALK, "Okay, I'll be back with that soon.") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_NEED_TROLL_POTION) }
                                }
                                npc(npc, CALM_TALK, "Anything else I can do for you?")
                                exec { defaultEadgarOptions(player, npc) }
                            }
                        }
                    }
                }

                STAGE_NEED_TROLL_POTION -> {
                    if (player.containsOneItem(TROLL_POTION)) {
                        player(CALM_TALK, "I've got the troll truth potion.")
                        npc(npc, HAPPY_TALKING, "Excellent, hand it here. Now just go fetch that poor parrot back, it's probably had enough by now.") {
                            player.inventory.deleteItem(TROLL_POTION, 1)
                            player.setQuestStage(Quest.EADGARS_RUSE, STAGE_FETCH_PARROT)
                        }
                    } else {
                        player(CALM_TALK, "How do I make the troll truth potion again?")
                        npc(npc, CALM_TALK, "Troll Thistle grows around this mountain. If properly prepared, it can be made into a sort of Troll truth potion.")
                        npc(npc, CALM_TALK, "Anything else I can do for you?")
                        exec { defaultEadgarOptions(player, npc) }
                    }
                }

                STAGE_FETCH_PARROT -> {
                    npc(npc, HAPPY_TALKING, "Did you get that parrot back?")
                    player(CALM_TALK, "Not yet.")
                    npc(npc, CALM_TALK, "Hurry up then.")
                }

                STAGE_RETRIEVED_PARROT -> {
                    if (!hasParrot) {
                        player(SAD, "I lost the parrot after I retrieved it from the Troll prison.")
                        npc(npc, CALM_TALK, "Luckily, it flew into my cave.")
                        if (player.inventory.hasFreeSlots()) {
                            item(DRUNK_PARROT, "Eadgar hands you the parrot back.") { player.inventory.addItem(DRUNK_PARROT) }
                            player(HAPPY_TALKING, "Thank you Eadgar.")
                        } else {
                            simple("You need at least 1 free inventory slot to take the parrot.")
                        }
                    } else {
                        player(HAPPY_TALKING, "I've brought the parrot.")
                        npc(npc, HAPPY_TALKING, "Hand it here...there we go! Can you tell this isn't a bona fide human being? I sure can't!")
                        npc(FAKE_MAN_NPC, NONE, "What am I doing here? Ow! Where's the rest of the Guard? Agh! I won't tell you anything!")
                        exec {
                            player.inventory.replace(DRUNK_PARROT, FAKE_MAN_ITEM)
                            player.setQuestStage(Quest.EADGARS_RUSE, STAGE_RECEIVED_FAKE_MAN)
                            defaultEadgarOptions(player, npc)
                        }
                    }
                }

                STAGE_RECEIVED_FAKE_MAN -> {
                    npc(npc, HAPPY_TALKING, "Have you given the fake man to the troll cook yet?")
                    if (hasFakeMan) {
                        player(CALM_TALK, "Not yet.")
                        npc(npc, CALM_TALK, "Go on then, give it to the cook! Anything else I can do for you?")
                        exec { defaultEadgarOptions(player, npc) }
                    } else {
                        player(SAD, "No. I lost it.")
                        npc(npc, SKEPTICAL_THINKING, "You bumbling imbecile! Never mind, here's one I prepared earlier.")
                        if (player.inventory.hasFreeSlots()) {
                            simple("Eadgar hands you the fake man.") { player.inventory.addItem(FAKE_MAN_ITEM) }
                        } else {
                            simple("You need at least 1 free inventory slot to take the fake man.")
                        }
                        npc(npc, CALM_TALK, "Anything else I can do for you?")
                        exec { defaultEadgarOptions(player, npc) }
                    }
                }

                in STAGE_GAVE_FAKE_MAN_TO_BURNTMEAT..STAGE_DISCOVERED_KEY_LOCATION -> {
                    npc(npc, HAPPY_TALKING, "Have you given the fake man to the troll cook yet?")
                    player(SKEPTICAL_THINKING, "Yes, I just need to find a way to get into the storeroom now.")
                    npc(npc, HAPPY_TALKING, "I'd start by questioning the troll cook.")
                    npc(npc, CALM_TALK, "Anything else I can do for you?")
                    exec { defaultEadgarOptions(player, npc) }
                }

                STAGE_UNLOCKED_STOREROOM -> {
                    npc(npc, HAPPY_TALKING, "Well? Did the plan work?")
                    player(HAPPY_TALKING, "Yes! I can get into the storeroom now.")
                    npc(npc, HAPPY_TALKING, "Glad to hear it!")
                    npc(npc, CALM_TALK, "Anything else I can do for you?")
                    exec { defaultEadgarOptions(player, npc) }
                }

            }
        }
    }

    private fun defaultEadgarOptions(player: Player, npc: NPC) {
        player.startConversation {
            label("initialOps")
            options {
                op("Why do you live so close to the trolls?") {
                    player(CALM_TALK, "Why do you live so close to the trolls? Isn't it dangerous?")
                    npc(npc, CALM_TALK, "Well, I suppose I do keep getting captured by the trolls and thrown in prison... But they always release me in the end, I'm far too old and skinny for their tastes.")
                    npc(npc, CALM_TALK, "In any case, this is my home, and I'm not leaving it. And this area has the tastiest goats!")
                    goto("initialOps")
                }
                op("What do you have to offer?") {
                    player(CALM_TALK, "What do you have to offer?")
                    npc(npc, CALM_TALK, "The chef's recommendation for today is mountain goat stew. I'll give some stew in exchange for logs for my fire. They're hard to come by around here.")
                    if (player.inventory.containsOneItem(NORMAL_LOGS)) { // Contains regular logs
                        player(CALM_TALK, "Here's some logs.")
                        npc(npc, HAPPY_TALKING, "Thank you. Here's some mountain goat stew, as promised.") { player.inventory.replace(NORMAL_LOGS, STEW) }
                    } else {
                        player(CALM_TALK, "Thanks, I might come back with some logs.")
                    }
                }
                op("No thanks, Eadgar.") {
                    player(CALM_TALK, "No thanks, Eadgar.")
                    npc(npc, CALM_TALK, "Your loss!")
                }
            }
        }
    }
}
