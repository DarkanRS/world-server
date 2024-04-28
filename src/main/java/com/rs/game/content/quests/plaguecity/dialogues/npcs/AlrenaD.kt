package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class AlrenaD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello Madam.")
                    npc(npc, WORRIED, "Oh, hello there.")
                    player(CONFUSED, "Are you ok?")
                    npc(npc, WORRIED, "Not too bad... I've just got some troubles on my mind...")
                }

                STAGE_SPEAK_TO_ALRENA -> {
                    player(HAPPY_TALKING, "Hello, Edmond has asked me to help find your daughter.")
                    npc(npc, CALM_TALK, "Yes he told me. I've begun making your special gas mask, but I need some dwellberries to finish it.")
                    if (player.inventory.containsItems(DWELLBERRIES)) {
                        player(HAPPY_TALKING, "Yes I've got some here.")
                        item(DWELLBERRIES, "You give the dwellberries to Alrena.")
                        simple("Alrena crushes the berries into a smooth paste. She then smears the paste over a strange mask.") { npc.anim(PESTLE_AND_MORTAR_ANIM) }
                        npc(npc, CALM_TALK, "There we go, all done. While in West Ardougne you must wear this at all times, or you could catch the plague.")
                        item(GAS_MASK, "Alrena gives you the mask.") {
                            player.inventory.deleteItem(DWELLBERRIES, 1)
                            player.inventory.addItem(GAS_MASK)
                            player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_RECEIVED_GAS_MASK)
                        }
                        npc(npc, CALM_TALK, "I'll make a spare mask. I'll hide it in the wardrobe in case the mourners come in.")
                    } else {
                        player(CALM_TALK, "I'll try to get some.")
                        npc(npc, CALM_TALK, "The best place to look is in McGrubor's Wood to the west of Seers village.")
                    }
                }

                STAGE_RECEIVED_GAS_MASK -> {
                    player(HAPPY_TALKING, "Hello Alrena.")
                    npc(npc, CALM_TALK, "Hello darling, I think Edmond had a good idea of how to get into West Ardougne, you should hear his idea.")
                    player(HAPPY_TALKING, "Alright I'll go and see him now.")
                }

                STAGE_PREPARE_TO_DIG,
                STAGE_CAN_DIG -> {
                    player(HAPPY_TALKING, "Hello Alrena.")
                    if (player.questManager.getAttribs(Quest.PLAGUE_CITY).getI(WATER_USED_ON_MUD) in 1..3) {
                        npc(npc, CALM_TALK, "Hello darling, how's that tunnel coming along?")
                        player(HAPPY_TALKING, "I just need to soften the soil a little more and then we'll start digging.")
                    } else if (player.questManager.getAttribs(Quest.PLAGUE_CITY).getI(WATER_USED_ON_MUD) == 4) {
                        npc(npc, CALM_TALK, "Hello darling, how's that tunnel coming along?")
                        player(HAPPY_TALKING, "I've soaked the soil with enough water.")
                        npc(npc, HAPPY_TALKING, "That's great, you should tell Edmond the news.")
                    } else {
                        player(HAPPY_TALKING, "I'm getting there.")
                        npc(npc, CALM_TALK, "One of the mourners has been sniffing around asking questions about you and Edmond, you should keep an eye out for him.")
                        player(CALM_TALK, "Ok, thanks for the warning.")
                        if (!player.inventory.containsOneItem(GAS_MASK) && !PlagueCityUtils().isWearingGasMask(player)) {
                            npc(npc, CALM_TALK, "I managed to finish the spare set of protective clothing, it's hidden in the wardrobe.")
                            player(CALM_TALK, "Great, thanks Alrena!")
                        }
                    }
                }

                in STAGE_UNCOVERED_SEWER_ENTRANCE..STAGE_SPOKEN_TO_JETHICK,
                in STAGE_GAVE_BOOK_TO_TED..STAGE_PERMISSION_TO_BRAVEK,
                in STAGE_GAVE_HANGOVER_CURE..STAGE_FREED_ELENA -> {
                    if (player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ENTERED_CITY)) {
                        player(HAPPY_TALKING, "Hello Alrena.")
                        npc(npc, CALM_TALK, "Hello, any word on Elena?")
                        player(SAD, "Not yet I'm afraid.")
                        npc(npc, CALM_TALK, "Is there anything else I can do to help?")
                        player(CALM_TALK, "It's alright, I'll get her back soon.")
                        npc(npc, CALM_TALK, "That's the spirit, dear.")
                    } else {
                        player(HAPPY_TALKING, "Hello Alrena.")
                        npc(npc, CALM_TALK, "Hi, have you managed to get through to West Ardougne?")
                        player(CALM_TALK, "Not yet, but I should be going soon.")
                        npc(npc, CALM_TALK, "Make sure you wear your mask while you're over there! I can't think of a worse way to die.")
                        player(CALM_TALK, "Ok, thanks for the warning.")
                    }
                }

                STAGE_GET_HANGOVER_CURE -> {
                    player(HAPPY_TALKING, "Hello Alrena.")
                    npc(npc, CALM_TALK, "Hello, any word on Elena?")
                    player(SAD, "Not yet I'm afraid, I need to find some Snape grass first, any idea where I'd find some?")
                    npc(npc, CALM_TALK, "It's not common round here, though I hear it's easy to find by the coast south west of Falador.")
                    player(HAPPY_TALKING, "Thanks, I'll go take a look.")
                    player(CALM_TALK, "I also need to get some chocolate powder for a hangover cure for the city warder.")
                    npc(npc, CALM_TALK, "Well I don't have any chocolate, but this may help.")
                    if (player.inventory.hasFreeSlots()) item(PESTLE_AND_MORTAR, "Alrena hands you a pestle and mortar.") { player.inventory.addItem(PESTLE_AND_MORTAR) }
                    else item(PESTLE_AND_MORTAR, "Alrena shows you a pestle and mortar, but you don't have room to take it from her.")
                    player(CALM_TALK, "Thanks.")
                }

                STAGE_COMPLETE -> {
                    npc(npc, HAPPY_TALKING, "Thank you for rescuing my daughter! Elena has told me of your bravery in entering a house that could have been plague infected. I can't thank you enough!")
                }

            }
        }
    }
}
