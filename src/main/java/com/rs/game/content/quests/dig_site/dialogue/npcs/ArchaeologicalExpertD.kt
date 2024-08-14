package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item

class ArchaeologicalExpertD(val player: Player, val npc: NPC) {
    init {
        player.startConversation {
            when(player.getQuestStage(Quest.DIG_SITE)) {
                in STAGE_UNSTARTED..STAGE_BLOWN_UP_BRICKS -> {
                    player(CALM_TALK, "Hello. Who are you?")
                    npc(npc, CALM_TALK, "Good day to you. My name is Terry Balando, I am an expert archaeologist. I am employed by Varrock Museum to oversee all finds at this site. Anything you find must be reported to me.")
                    player(CALM_TALK, "Oh, okay. If I find anything of interest I will bring it here.")
                    npc(npc, CALM_TALK, "Can I help you at all?")
                    options {
                        op("I have something I need checking out.") {
                            player(CALM_TALK, "I have something I need checking out.")
                            npc(npc, CALM_TALK, "Okay, give it to me and I'll have a look for you.")
                        }
                        op("No thanks.") {
                            player(CALM_TALK, "No thanks.")
                            npc(npc, CALM_TALK, "Good, let me know if you find anything unusual.")
                        }
                        op("Can you tell me anything about the site?") {
                            player(CALM_TALK, "Can you tell me anything about the site?")
                            npc(npc, TALKING_ALOT, "Yes, indeed! I am studying the lives of the settlers. During the end of the Third Age, there used to be a great city at the site. Its inhabitants were humans, supporters of the god Saradomin.")
                            npc(npc, TALKING_ALOT, "It's not recorded what happened to the community here. I suspect nobody has lived here for over a millennium!")
                            options {
                                op("Can you tell me more about the tools an archaeologist uses?") {
                                    player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                                    goto("archaeologistTools")
                                }
                                op("Thank you!") {
                                    player(CALM_TALK, "Thank you!")
                                }
                            }
                        }
                        if (player.getQuestStage(Quest.DIG_SITE) == STAGE_RECEIVED_INVITATION && !player.inventory.containsOneItem(INVITATION_LETTER) && !player.bank.containsItem(INVITATION_LETTER))
                            op("I've lost the invitation letter you gave me.") {
                                player(CALM_TALK, "I've lost the invitation letter you gave me.")
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "Luckily I have another here for you. Make sure you don't lose it this time.") {
                                        player.inventory.addItem(INVITATION_LETTER)
                                    }
                                    player(CALM_TALK, "Thank you - I will try not to lose it again!")
                                } else {
                                    npc(npc, CALM_TALK, "I could give you another but you don't seem to have the room to carry it.")
                                    player(CALM_TALK, "Okay, I'll go empty my backpack first.")
                                }
                            }
                        op("Can you tell me more about the tools an archaeologist uses?") {
                            player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                            label("archaeologistTools")
                            npc(npc, CALM_TALK, "Of course! Let's see now...")
                            npc(npc, CALM_TALK, "Trowels are vital for fine digging work, so you can be careful to not damage or disturb any artefacts. Rock picks are for splitting rocks or scraping away soil.")
                            player(CALM_TALK, "What about specimen jars and brushes?")
                            npc(npc, CALM_TALK, "Those are essential for carefully cleaning and storing smaller samples.")
                            player(CALM_TALK, "Where can I get any of these things?")
                            npc(npc, CALM_TALK, "Well, we've come into a bit more funding of late, so there should be a stock of each of them in the Exam Centre's tools cupboard.")
                            npc(npc, CALM_TALK, "We also hand out relevant tools as students complete each level of their Earth Sciences exams.")
                            player(CALM_TALK, "Ah, okay, thanks.")
                        }
                    }
                }

                STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Hello again. I am now studying this mysterious altar and its inhabitants. The markings are strange...")
                    npc(npc, CALM_TALK, "It refers to a god I have never heard of before, named Zaros. It must be some pagan superstition.")
                    npc(npc, CALM_TALK, "That was a great find; who knows what other secrets lie buried beneath the surface of our land...")
                    npc(npc, CALM_TALK, "Can I help you at all?")
                    options {
                        op("I have something I need checking out.") {
                            player(CALM_TALK, "I have something I need checking out.")
                            npc(npc, CALM_TALK, "Okay, give it to me and I'll have a look for you.")
                        }
                        op("No thanks.") {
                            player(CALM_TALK, "No thanks.")
                            npc(npc, CALM_TALK, "Good, let me know if you find anything unusual.")
                        }
                        op("Can you tell me anything about the site?") {
                            player(CALM_TALK, "Can you tell me anything about the site?")
                            npc(npc, TALKING_ALOT, "Yes, indeed! I am studying the lives of the settlers. During the end of the Third Age, there used to be a great city at the site. Its inhabitants were humans, supporters of the god Saradomin.")
                            npc(npc, TALKING_ALOT, "It's not recorded what happened to the community here. I suspect nobody has lived here for over a millennium!")
                            options {
                                op("Can you tell me more about the tools an archaeologist uses?") {
                                    player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                                    goto("archaeologistTools")
                                }
                                op("Thank you!") {
                                    player(CALM_TALK, "Thank you!")
                                }
                            }
                        }
                        op("Can you tell me more about the tools an archaeologist uses?") {
                            player(CALM_TALK, "Can you tell me more about the tools an archaeologist uses?")
                            label("archaeologistTools")
                            npc(npc, CALM_TALK, "Of course! Let's see now...")
                            npc(npc, CALM_TALK, "Trowels are vital for fine digging work, so you can be careful to not damage or disturb any artefacts. Rock picks are for splitting rocks or scraping away soil.")
                            player(CALM_TALK, "What about specimen jars and brushes?")
                            npc(npc, CALM_TALK, "Those are essential for carefully cleaning and storing smaller samples.")
                            player(CALM_TALK, "Where can I get any of these things?")
                            npc(npc, CALM_TALK, "Well, we've come into a bit more funding of late, so there should be a stock of each of them in the Exam Centre's tools cupboard.")
                            npc(npc, CALM_TALK, "We also hand out relevant tools as students complete each level of their Earth Sciences exams.")
                            player(CALM_TALK, "Ah, okay, thanks.")
                        }
                    }
                }
            }
        }
    }

    fun identifyItem(item: Item) {
        player.startConversation {
            when (item.id) {
                UNIDENTIFIED_LIQUID -> {
                    player(SKEPTICAL_THINKING, "Do you know what this is?")
                    npc(npc, WORRIED, "Where did you get this?")
                    player(CALM_TALK, "From one of the barrels at the digsite.")
                    npc(npc, WORRIED, "This is a VERY dangerous liquid called nitroglycerin. Be careful how you handle it. Don't drop it or it will explode!") {
                        replaceAllItems(UNIDENTIFIED_LIQUID, NITROGLYCERIN)
                    }
                }

                NITROGLYCERIN -> {
                    player(CALM_TALK, "Can you tell me any more about this?")
                    npc(npc, WORRIED, "Nitroglycerin! This is a dangerous substance. This is normally mixed with other chemicals to produce a potent compound.")
                    npc(npc, WORRIED, "Be sure not to drop it! That stuff is highly volatile...")
                }

                CHEMICAL_POWDER, AMMONIUM_NITRATE -> {
                    if (item.id == CHEMICAL_POWDER) player(SKEPTICAL_THINKING, "Do you know what this powder is?") else player(CALM_TALK, "Have a look at this.")
                    npc(npc, WORRIED, "Really, you do find the most unusual items. I know what this is - it's a strong chemical called ammonium nitrate. Why you want this I'll never know...") {
                        if (item.id == CHEMICAL_POWDER) replaceAllItems(CHEMICAL_POWDER, AMMONIUM_NITRATE)
                    }
                }

                MIXED_CHEMICALS -> {
                    player(CALM_TALK, "Hey, look at this.")
                    npc(npc, CALM_TALK, "Hmm, that looks dangerous. Handle it carefully and don't drop it!")
                }

                MIXED_CHEMICALS_CHARCOAL -> {
                    player(CALM_TALK, "See what I have done with the compound now.")
                    npc(npc, CALM_TALK, "Seriously, I think you have a death wish! What on earth are you going to do with that stuff?")
                    player(CALM_TALK, "I'll find a use for it!")
                }

                CHEMICAL_COMPOUND -> {
                    player(CALM_TALK, "What do you think about this?")
                    npc(npc, CALM_TALK, "What have you concocted now? Just be careful when playing with chemicals!")
                }

                NUGGETS -> {
                    player(CALM_TALK, "I have these gold nuggets...")
                    if (player.inventory.getAmountOf(NUGGETS) < 3) {
                        npc(npc, CALM_TALK, "I can't do much with these nuggets yet. Come back when you have 3 and I will exchange them for you.")
                    } else if (player.inventory.getAmountOf(NUGGETS) > 3) {
                        if (player.inventory.hasFreeSlots()) {
                            npc(npc, CALM_TALK, "Good – that's three; I can exchange them for normal gold now. You can get this refined and make a profit!") {
                                player.inventory.deleteItem(NUGGETS, 3)
                                player.inventory.addItem(GOLD_ORE)
                            }
                            player(CALM_TALK, "Excellent!")
                        } else {
                            npc(npc, CALM_TALK, "Good – that's three; I can exchange them for normal gold now but you'll need a spare inventory slot first.")
                            player(CALM_TALK, "Excellent - I'll go empty my backpack first!")
                        }
                    } else if (player.inventory.getAmountOf(NUGGETS) == 3) {
                        npc(npc, CALM_TALK, "Good – that's three; I can exchange them for normal gold now. You can get this refined and make a profit!") {
                            player.inventory.deleteItem(NUGGETS, 3)
                            player.inventory.addItem(GOLD_ORE)
                        }
                        player(CALM_TALK, "Excellent!")
                    }
                }

                NEEDLE -> {
                    player(CALM_TALK, "I found a needle.")
                    npc(npc, CALM_TALK, "Hmm, yes; I wondered why this race were so well dressed! It looks like they had mastery of needlework.")
                }

                ROTTEN_APPLE -> {
                    player(CALM_TALK, "I found this...")
                    npc(npc, CALM_TALK, "Ew! Throw it away this instant!")
                }

                BROKEN_GLASS -> {
                    player(CALM_TALK, "Have a look at this glass.")
                    npc(npc, CALM_TALK, "Hey you should be careful of that. It might cut your fingers, throw it away!")
                }

                BROKEN_ARROW -> {
                    player(CALM_TALK, "Have a look at this arrow.")
                    npc(npc, CALM_TALK, "No doubt this arrow was shot by a strong warrior – it's split in half! It is not a valuable object though.")
                }

                EMPTY_PANNING_TRAY -> npc(npc, CALM_TALK, "I have no need for panning trays!")

                MUD_PANNING_TRAY, GOLD_PANNING_TRAY -> {
                    npc(npc, CALM_TALK, "Have you searched this tray yet?")
                    player(CALM_TALK, "Not that I remember...")
                    npc(npc, CALM_TALK, "It may contain something; I don't want to get my hands dirty.")
                    player.sendMessage("The expert hands the tray back to you.")
                }

                BONES -> {
                    player(CALM_TALK, "Have a look at these bones.")
                    npc(npc, CALM_TALK, "Ah, yes – a fine bone example... no noticeable fractures... and in good condition. These are common cow bones, however; they have no archaeological value.")
                }

                BUTTONS -> {
                    player(CALM_TALK, "I found these buttons.")
                    npc(npc, CALM_TALK, "Let's have a look. Ah, I think these are from the nobility, perhaps a royal servant?")
                }

                CRACKED_SAMPLE -> {
                    player(CALM_TALK, "I found this rock...")
                    npc(npc, CALM_TALK, "What a shame it's cracked; this looks like it would have been a good sample.")
                }

                OLD_TOOTH -> {
                    player(CALM_TALK, "Hey look at this.")
                    npc(npc, CALM_TALK, "Oh, an old tooth. It looks like it has come from a mighty being. Pity there are no tooth fairies around here!")
                }

                RUSTY_SWORD -> {
                    player(CALM_TALK, "I found an old sword.")
                    npc(npc, CALM_TALK, "Oh, it's very rusty isn't it? I'm not sure this sword belongs here, it looks very out of place.")
                }

                BROKEN_STAFF -> {
                    player(CALM_TALK, "Have a look at this staff.")
                    npc(npc, CALM_TALK, "Look at this... Interesting... This appears to belong to a cleric of some kind; certainly not a follower of Saradomin, however.")
                    npc(npc, CALM_TALK, "I wonder if there was another civilization here before the Saradominists?")
                }

                BROKEN_ARMOUR -> {
                    player(CALM_TALK, "I found some armour.")
                    npc(npc, CALM_TALK, "It looks like the wearer of this fought a mighty battle.")
                }

                DAMAGED_ARMOUR -> {
                    player(CALM_TALK, "I found some old armour.")
                    npc(npc, CALM_TALK, "How unusual. This armour doesn't seem to match with the other finds. Keep looking.")
                }

                CERAMIC_REMAINS -> {
                    player(CALM_TALK, "I found some pottery pieces.")
                    npc(npc, CALM_TALK, "Yes, many parts are discovered. The inhabitants of these parts were great potters.")
                    player(CALM_TALK, "You mean they were good at using potions?")
                    npc(npc, CALM_TALK, "No, no, silly. They were known for their skill with clay.")
                }

                BELT_BUCKLE -> {
                    player(CALM_TALK, "Have a look at this unusual item...")
                    npc(npc, CALM_TALK, "Let me see. This is a belt buckle. Not so unusual - I should imagine it came from a guard.")
                }

                ANIMAL_SKULL -> {
                    player(CALM_TALK, "Have a look at this.")
                    npc(npc, CALM_TALK, "Hmm, an interesting find; an animal skull for sure. Another student found one just like this today.")
                }

                SPECIAL_CUP -> {
                    player(CALM_TALK, "Have a look at this.")
                    npc(npc, CALM_TALK, "Looks like an award cup for some small find. Perhaps it belongs to one of the students?")
                }

                TEDDY -> {
                    player(CALM_TALK, "Have a look at this.")
                    npc(npc, CALM_TALK, "Why, it looks like a teddy bear to me. Perhaps someone's lucky mascot!")
                }

                OLD_BOOT -> {
                    player(CALM_TALK, "Have a look at this.")
                    npc(npc, CALM_TALK, "Ah yes, an old boot. Not really an ancient artefact, is it?")
                }

                ANCIENT_TALISMAN -> {
                    if (player.getQuestStage(Quest.DIG_SITE) == STAGE_COMPLETED_EXAMS) {
                        player(CALM_TALK, "Take a look at this talisman.")
                        npc(npc, CALM_TALK, "Unusual... This object doesn't appear right...")
                        npc(npc, CALM_TALK, "Hmmm...")
                        npc(npc, CALM_TALK, "From the markings on it, it seems to be a ceremonial ornament to a god named...")
                        npc(npc, CALM_TALK, "Zaros? I haven't heard much about him before. This is a great discovery; we know very little of the ancient gods that people worshipped.")
                        npc(npc, CALM_TALK, "There is some strange writing embossed upon it - it says, 'Zaros will return and wreak his vengeance upon Zamorak the pretender.'")
                        npc(npc, CALM_TALK, "Still, I wonder what this is doing around here. I'll tell you what; as you have found this, I will allow you to use the private dig shafts.")
                        npc(npc, CALM_TALK, "You obviously have a keen eye. Take this letter and give it to one of the winch operators at the north end of the dig, and they will allow you to use them."){
                            player.inventory.replace(ANCIENT_TALISMAN, INVITATION_LETTER)
                            player.setQuestStage(Quest.DIG_SITE, STAGE_RECEIVED_INVITATION)
                            player.sendMessage("The expert hands you a letter.")
                        }
                    } else if (player.getQuestStage(Quest.DIG_SITE) > STAGE_COMPLETED_EXAMS) {
                        player.sendMessage("The expert has already seen the talisman.")
                    }
                }

                INVITATION_LETTER -> npc(npc, CALM_TALK, "There's no point giving me this back!")

                STONE_TABLET -> {
                    if (!player.isQuestComplete(Quest.DIG_SITE)) {
                        player(CALM_TALK, "I found this in a hidden cavern beneath the site.")
                        npc(npc, CALM_TALK, "Incredible!")
                        player(CALM_TALK, "There is an altar down there. The place is crawling with skeletons!")
                        npc(npc, CALM_TALK, "Yuck! This is an amazing discovery! All this while we were convinced that no other race had lived here.")
                        npc(npc, CALM_TALK ,"It seems the followers of Saradomin have tried to cover up the evidence of this Zaros altar. This whole city must have been built over it!")
                        npc(npc, CALM_TALK, "Thanks for your help; your sharp eyes have spotted what many have missed. Here, take this gold as your reward.")
                        if (player.inventory.freeSlots >= 1) {
                            item(GOLD_BAR, "The expert gives you two gold bars as payment.")
                            exec { player.completeQuest(Quest.DIG_SITE) }
                        } else {
                            simple("You need at least 1 free inventory slot to receive your reward.")
                        }
                    } else {
                        player(CALM_TALK, "Have a look at this.")
                        npc(npc, CALM_TALK, "I don't need another tablet! One is enough, thank you.")
                    }
                }

                else -> {
                    player(CALM_TALK, "Have a look at this.")
                    npc(npc, CALM_TALK, "I don't think that has any archaeological significance.")
                }
            }
        }
    }

    private fun replaceAllItems(oldItem: Int, newItem: Int) {
        val amountOfOldItem = player.inventory.getAmountOf(oldItem)
        repeat(amountOfOldItem) {
            player.inventory.replace(oldItem, newItem)
        }
    }

}
