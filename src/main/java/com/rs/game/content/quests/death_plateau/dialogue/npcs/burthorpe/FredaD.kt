package com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.utils.shop.ShopsHandler

class FredaD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DEATH_PLATEAU)) {

                in STAGE_UNSTARTED..STAGE_SPEAK_TO_SABBOT -> {
                    player(HAPPY_TALKING, "Hello!")
                    npc(npc, HAPPY_TALKING, "Well hello there! My, you're a cheerful one! How can I help you?")
                    optionsDialogue(player, npc, this)
                }

                STAGE_SPEAK_TO_FREDA -> {
                    player(HAPPY_TALKING, "Hello there.")
                    npc(npc, HAPPY_TALKING, "Hello. How can I help you?")
                    player(SKEPTICAL_THINKING, "Can I see the survey you and your husband did on the cave under Death Plateau?")
                    npc(npc, CONFUSED, "What do you want that for?")
                    player(CALM_TALK, "Well, I am looking for another way to get onto Death Plateau so the Guard can ambush the trolls. Your husband told me that there could be caves under it that I could use.")
                    npc(npc, CHUCKLE, "Oh, he would go telling you that, the old duffer!")
                    player(CONFUSED, "You mean there aren't any?")
                    npc(npc, CALM_TALK, "There are some, but he doesn't understand that humans like you prefer running about and climbing over things instead of digging around.")
                    player(CALM_TALK, "He did seem a little...upset about the idea of rock climbing.")
                    npc(npc, CHUCKLE, "Yes, that sounds about right! He's always hated it ever since I managed to get from Rimmington to Taverley overground faster than he went underground!")
                    npc(npc, CALM_TALK, "He tried going overground himself next time, slipped on some snow and ended up bashing his shoulder on a rock, bless him.")
                    npc(npc, CALM_TALK, "Anyway, this survey. I can make a start on it after I get back from town. So you'll have to wait for me to be done with it.")
                    npc(npc, CALM_TALK, "The survey we made is written in some very technical terms, and I'll need to dig out all the information for ya.")
                    player(CALM_TALK, "Is there any chance you could do this and then go into town?")
                    npc(npc, SKEPTICAL, "Well I could, but I really need to get my climbing boots re-spiked. If I don't have that done I might run into trouble if the weather turns.")
                    player(CALM_TALK, "What if I went and got the boots fixed for you? Would you make a start on the survey then?")
                    npc(npc, CALM_TALK, "That sounds like a pretty good idea! You're on. I'll make a start while you go take care of this little job for me.")
                    npc(npc, CALM_TALK, "Just go and see Dunstan. He knows how I like them. Good smith, that man - for a human, obviously.")
                    player(CALM_TALK, "Where does he live?")
                    npc(npc, CALM_TALK, "In one of the houses to the east of Burthorpe castle. His is the one in the middle.")
                    npc(npc, CALM_TALK, "In fact, it would probably be quicker if you used the home teleport spell. It saves you running all the way there and then all the way back.")
                    player(CALM_TALK, "Thanks for the tip!")
                    if (player.inventory.hasFreeSlots()) {
                        item(CLIMBING_BOOTS, "Freda hands you some old climbing boots.") {
                            player.inventory.addItem(CLIMBING_BOOTS)
                            npc.faceEntityTile(player)
                            player.faceEntityTile(npc)
                            player.schedule {
                                npc.anim(ANIM_DWARF_GIVE_ITEM)
                                wait(2)
                                player.anim(ANIM_HUMAN_TAKE_ITEM)
                            }
                            player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_TAKE_BOOTS_DUNSTAN)
                        }
                    } else{
                        npc(npc, CALM_TALK, "Well will you look at that, you've got no space for these boots. Go and make some room. It'll give me a head start on this survey.")
                        player(CALM_TALK, "Oh, okay. I'll be right back.")
                    }
                }

                STAGE_TAKE_BOOTS_DUNSTAN -> {
                    if (player.inventory.containsOneItem(CLIMBING_BOOTS)) {
                        player(CALM_TALK, "Is the report ready?")
                        npc(npc, CALM_TALK, "You're pretty eager! No, I've barely started on it.")
                        player(CALM_TALK, "Awww...")
                        npc(npc, CALM_TALK, "It'll be ready in its own time.")
                        npc(npc, CALM_TALK, "Now, is there anything else you need?")
                        optionsDialogue(player, npc, this)
                    } else {
                        player(CALM_TALK, "I'm sorry. I lost those boots you gave me.")
                        npc(npc, CALM_TALK, "Aww, don't mope like that. They were old and ragged, that's why I wanted them repaired, remember?")
                        if (player.inventory.hasFreeSlots()) {
                            item(CLIMBING_BOOTS, "Freda hands you some old climbing boots.") {
                                player.inventory.addItem(CLIMBING_BOOTS)
                                npc.faceEntityTile(player)
                                player.faceEntityTile(npc)
                                player.schedule {
                                    npc.anim(ANIM_DWARF_GIVE_ITEM)
                                    wait(2)
                                    player.anim(ANIM_HUMAN_TAKE_ITEM)
                                }
                            }
                            npc(npc, CALM_TALK, "Here, go and take these ones into town instead. Just try and keep a better hold on them all right?")
                            player(CALM_TALK, "All right.")
                        } else {
                            npc(npc, CALM_TALK, "I've got plenty of other pairs you could go and fix up for me, but you don't seem like you've got the room to take any.")
                            npc(npc, CALM_TALK, "Why not go and clear some space in your backpack and the come see me again, okay?")
                            player(CALM_TALK, "That sounds like a good plan.")
                        }
                    }
                }

                STAGE_RETURN_TO_FREDA -> {
                    if (!player.inventory.containsOneItem(SPIKED_BOOTS)) {
                        player(CALM_TALK, "Is the report ready?")
                        npc(npc, SKEPTICAL_THINKING, "Not just yet. I need to polish it up a little. Have you got my boots?")
                        player(SHAKING_HEAD, "No. Maybe if I go and get them you'll be ready.")
                        npc(npc, CALM_TALK, "Aye, that sounds grand. I'll see you shortly.")
                    } else {
                        player(CALM_TALK, "Is the report ready?")
                        npc(npc, HAPPY_TALKING, "Aye, I just finished it about a minute before you walked in. You've got a good sense of timing.")
                        player(HAPPY_TALKING, "That's great! I have your boots too, by the way.")
                        npc(npc, CALM_TALK, "That's grand, I'll take them off your hands. Here's the report.")
                        item(SURVEY, "Freda hands you her fresh copy of the survey.") {
                            player.inventory.deleteItem(SPIKED_BOOTS, player.inventory.getAmountOf(SPIKED_BOOTS))
                            player.inventory.addItem(SURVEY)
                            npc.faceEntityTile(player)
                            player.faceEntityTile(npc)
                            player.schedule {
                                npc.anim(ANIM_DWARF_GIVE_ITEM)
                                wait(2)
                                player.anim(ANIM_HUMAN_TAKE_ITEM)
                            }
                            player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_RECEIVED_SURVEY)
                        }
                        player(CALM_TALK, "Thanks; I'll take this back to Sabbot's cave. Is there anything you want me to take to him?")
                        npc(npc, CALM_TALK, "Nah, you're alright. He'd not accept it anyway the stubborn goat that he is!")
                    }
                }

                STAGE_RECEIVED_SURVEY -> {
                    if (!player.inventory.containsOneItem(SURVEY)) {
                        player(SKEPTICAL, "Freda, do you have another copy of that survey?")
                        npc(npc, SKEPTICAL_THINKING, "Did you lose it, after all that effort I went to?")
                        if (player.inventory.hasFreeSlots()) {
                            npc(npc, CALM_TALK, "It's a good job I made a few extra copies, just in case you got hit by a troll or crushed in a landslide.") {
                                player.inventory.addItem(SURVEY)
                                npc.faceEntityTile(player)
                                player.faceEntityTile(npc)
                                player.schedule {
                                    npc.anim(ANIM_DWARF_GIVE_ITEM)
                                    wait(2)
                                    player.anim(ANIM_HUMAN_TAKE_ITEM)
                                }
                            }
                            player(CALM_TALK, "That's terrifyingly specific.")
                            npc(npc, CHUCKLE, "Well, you can't be too careful.")
                        } else {
                            npc(npc, CALM_TALK, "I have a couple more copies, now you ask, but you don't have the space to take them.")
                            player(CALM_TALK, "Good point. Wait here while I empty my backpack.")
                        }
                    } else {
                        player(CALM_TALK, "Hello, Freda.")
                        npc(npc, HAPPY_TALKING, "Hello. Is that survey coming in handy?")
                        player(CALM_TALK, "Well, I still need to take it along to Sabbot's cave and investigate the walls for tunnels.")
                        npc(npc, HAPPY_TALKING, "Aye? Well you have fun with that.")
                        npc(npc, CALM_TALK, "Is there anything else you need?")
                        exec { optionsDialogue(player, npc, this) }
                    }
                }

                STAGE_READ_SURVEY -> {
                    player(CALM_TALK, "Hello, Freda.")
                    npc(npc, HAPPY_TALKING, "Hello there. Is that survey coming in handy?")
                    player(CHUCKLE, "Well I took a look at it, and it seemed very simple. Was it really that complicated? I mean, now I know where to dig just by looking at the diagram you drew.")
                    npc(npc, HAPPY_TALKING, "Lucky you! I wish I had a decent bit of mining to help me unwind.")
                    npc(npc, CALM_TALK, "You have fun with that now! As for the survey, that was drawn by translating a stack of about two hundred pages of geological tests and observations.")
                    npc(npc, CALM_TALK, "Och, you think we used a picture of the cave? Bless you! We spent a week tracing every strata in that place!")
                    player(CALM_TALK, "That does sound quite complicated.")
                    npc(npc, CHUCKLE, "Don't you worry about it. I know you human types do like your nice, simple solutions. But we dwarves do things thoroughly!")
                    npc(npc, CALM_TALK, "Is there anything else you need?")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_MINED_TUNNEL -> {
                    player(CALM_TALK, "Hello, Freda.")
                    npc(npc, HAPPY_TALKING, "Hello there. Is that survey coming in handy?")
                    player(HAPPY_TALKING, "The survey was very useful! I found a secret tunnel, and it might well take me to Death Plateau!")
                    npc(npc, CALM_TALK, "Oh, I'm so glad it turned out to be handy. You watch out for trolls!")
                    npc(npc, CALM_TALK, "Is there anything else you need?")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_FOUND_TROLL -> {
                    player(CALM_TALK, "Hello, Freda.")
                    npc(npc, HAPPY_TALKING, "Hello there. Is that survey coming in handy?")
                    player(CALM_TALK, "Well the tunnel is useful, but there is a troll lurking at the other end.")
                    npc(npc, CALM_TALK, "Well you take care now. You only have one life, so don't waste it.")
                    player(CALM_TALK, "Uh, yeah, one life. Right.")
                    npc(npc, CALM_TALK, "Anyway, that path of yours will never be useful if there is a troll there. So I suggest you go and deal with it.")
                    npc(npc, CALM_TALK, "Give it the old one-two! And don't be afraid to go for the goolies. They hate that!")
                    player(MORTIFIED, "I'll try and keep that in mind.")
                    npc(npc, CALM_TALK, "Is there anything else you need?")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_ANGERED_TROLL -> {
                    player(CALM_TALK, "Hello, Freda.")
                    npc(npc, HAPPY_TALKING, "Hello there. Is that survey coming in handy?")
                    player(CALM_TALK, "Well the tunnel leads where I want to go, but I managed to enrage the troll at the other end.")
                    npc(npc, CALM_TALK, "That couldn't have taken much. These trolls are pretty wild.")
                    player(SKEPTICAL_THINKING, "Do you have any tips for fighting trolls?")
                    npc(npc, CALM_TALK, "Well, if you're an archer or a magic user then you'll want to keep it at arm's length.")
                    npc(npc, CALM_TALK, "Keep running, and if it gets too close then try and snare it, or just down some good, hot food and keep going.")
                    npc(npc, CALM_TALK, "If you're a melee fighter then get yourself a decent dwarven battle axe and go to town on the big, smelly blighter.")
                    npc(npc, HAPPY_TALKING, "Yes, it might be bigger than you, but you just give it a couple of whacks round the legs and see how it feels after that!")
                    npc(npc, CALM_TALK, "Is there anything else you need?")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_KILLED_THE_MAP -> {
                    player(CALM_TALK, "Hello, Freda.")
                    npc(npc, HAPPY_TALKING, "Hello there. Is that survey coming in handy?")
                    player(HAPPY_TALKING, "It's been fantastic! I took care of a troll at the end of the tunnel I found, and now it is perfect for the guard to start ambushing trolls on the plateau!")
                    npc(npc, HAPPY_TALKING, "Och, that's grand news! I'm sure the Guard will be thrilled to hear that!")
                    player(HAPPY_TALKING, "Yes, I'm just off to tell them about it now.")
                    npc(npc, CALM_TALK, "Is there anything else you need before you go?")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_COMPLETE -> {
                    player(CALM_TALK, "Hello, Freda.")
                    npc(npc, HAPPY_TALKING, "Hello there. I heard from some of the boys in town that you really gave those trolls a thrashing!")
                    npc(npc, HAPPY_TALKING, "You keep it up. Every little helps.")
                    player(HAPPY_TALKING, "I will!")
                    npc(npc, CALM_TALK, "Anything else you need?")
                    exec { optionsDialogue(player, npc, this) }
                }

            }
        }
    }

    private fun optionsDialogue(player: Player, npc: NPC, dialogue: DialogueBuilder) {
        val stage = player.questManager.getStage(Quest.DEATH_PLATEAU)
        dialogue.label("initialOps")
        dialogue.options {
            op("Who are you?") {
                npc(npc, HAPPY_TALKING, "I'm Freda, welcome to my humble home! My husband, Sabbot, and I came here to start a trading post.")
                npc(npc, CALM_TALK, "You know, selling dwarven arms to the Guard. Then the trolls came and took over the plateau before we could get established, so I came to live here.")
                player(CONFUSED, "What about your husband?")
                npc(npc, CALM_TALK, "That stubborn old mule won't leave the cave we were in! He'd rather die than let the trolls win. I send him soup and letters now and then, and those nice men in town make sure he's okay.")
                npc(npc, CALM_TALK, "Is there anything else you want?")
                goto("initialOps")
            }
            op("Aren't you afraid of the trolls up here?") {
                npc(npc, CALM_TALK, "Not at all. There used to be a few around here, but I soon dealt with them.")
                player(SKEPTICAL_THINKING, "How? Aren't trolls really fierce in combat?")
                npc(npc, CHUCKLE, "Yes, but I'm fiercer! I'll have you know I've not been a trader all my life. I do know which end of an axe will do the most damage to a troll.")
                npc(npc, CALM_TALK, "Besides, some of those nice young men from town give me a hand if the trolls come in numbers. But after the last time the trolls seem to have got the hint.")
                npc(npc, CALM_TALK, "It's astonishing what waving an axe at groin height will do to break the will of even the biggest troll!")
                player(SKEPTICAL_THINKING, "So why don't you live with your husband then?")
                npc(npc, CALM_TALK, "Well, I can take on the occasional wandering troll, but a whole horde of them is something else entirely. This place may not be that safe, but at least it's not the middle of a warzone.")
                npc(npc, CALM_TALK, "Anything else I can help you with?")
                goto("initialOps")
            }
            if (stage == STAGE_SPEAK_TO_SABBOT)
                op("Do you know of any other way through the mountains?") {
                    npc(npc, SKEPTICAL_THINKING, "I might do. Where are ya trying to get to?")
                    player(CALM_TALK, "Death Plateau. I have been asked by the commander of the guard to try and scout a new route up to attack the trolls.")
                    npc(npc, SKEPTICAL_HEAD_SHAKE, "Hmm...Death Plateau eh? I can't say I know another way up there. I do know a few secret goat paths and climbing spots out there, you know? But nothing that'd be useful for the guard.")
                    npc(npc, CALM_TALK, "I can get away with scrambling about on my own over the paths with my climbing boots. But you'd never get away with moving lots of men around. The trolls would see you coming from miles away.")
                    player(CALM_TALK, "Thanks. I guess I'll keep looking.")
                    npc(npc, HAPPY_TALKING, "You should go ask my husband, Sabbot. He's been living a lot closer to the plateau, and might have an idea.")
                    player(HAPPY_TALKING, "Thanks, I'll go check that out.")
                    npc(npc, CALM_TALK, "Is there anything else before ya go?")
                    goto("initialOps")
                }
            if (stage == STAGE_COMPLETE)
                op("Can I buy some Climbing boots?") {
                    npc(npc, HAPPY_TALKING, "I don't see why not. Let me see what I've got in your size.")
                    options {
                        op("Buy standard boots for 12gp.") {
                            if (player.inventory.hasFreeSlots()) {
                                if (player.inventory.hasCoins(12)) {
                                    item(CLIMBING_BOOTS, "You purchase the climbing boots.") {
                                        player.inventory.addItem(CLIMBING_BOOTS)
                                        player.inventory.removeCoins(12)
                                        npc.faceEntityTile(player)
                                        player.faceEntityTile(npc)
                                        player.schedule {
                                            npc.anim(ANIM_DWARF_GIVE_ITEM)
                                            wait(2)
                                            player.anim(ANIM_HUMAN_TAKE_ITEM)
                                        }
                                    }
                                } else {
                                    player(SAD, "I don't have 12 coins, sorry.")
                                }
                            } else {
                                player(SAD, "Actually, I don't have enough room to take them.")
                            }
                        }
                        op("See full range of stock.") {
                            exec { ShopsHandler.openShop(player, "fredas_boots") }
                        }
                    }
                }
            op("That's all, thanks.") {
                npc(npc, CALM_TALK, "Oh? Well you take care on your way back.")
            }
        }
    }

}
