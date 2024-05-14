package com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager

class CommanderDenulthD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (val stage = player.questManager.getStage(Quest.DEATH_PLATEAU)) {

                STAGE_UNSTARTED -> {
                    player(HAPPY_TALKING, "Hello!")
                    npc(npc, HAPPY_TALKING, "Hello citizen, how can I help?")
                    label("initialOps")
                    options {
                        op("Do you have any quests for me?") {
                            npc(npc, SKEPTICAL, "We do have a quest, if you believe that you are ready for it.")
                            player(SKEPTICAL, "I was born ready. What do you need me to do?")
                            npc(npc, SKEPTICAL, "The trolls have taken up a position on Death Plateau, and we can't move them.")
                            npc(npc, SKEPTICAL, "As you know, the trolls have been pushing us hard, and we have to do something before they get too dug in and begin sending forces down the pass.")
                            npc(npc, SKEPTICAL, "They have stone throwers lining the only path up there that we know about, and the only entrance is narrow and easy to hold.")
                            npc(npc, VERY_FRUSTRATED, "If I didn't know they were pretty much mindless animals I would almost admire the position they have taken.")
                            player(SKEPTICAL_THINKING, "And you want me to go up there and kill them?")
                            npc(npc, SKEPTICAL, "Well, no. Sending you alone would be suicidal. What we need is to find another way up there.")
                            npc(npc, SKEPTICAL, "If we can find another route, then we can try and ambush them, or just spy on their movements.")
                            npc(npc, SKEPTICAL, "There is a cave beyond the defensive wall to the north west, at the foot of the plateau, where you will find a dwarf called Sabbot.")
                            npc(npc, SKEPTICAL, "He's a surly old geezer, but a good ally. He and his wife were surveying the mountain before the trolls arrived.")
                            npc(npc, SKEPTICAL, "To get there, go north-west from here, then west past the tower and the ruined wall. Then turn north and you can't miss it.")
                            npc(npc, SKEPTICAL, "If you go and speak to him, I'm sure that at the very least he'll be able to tell you another route up to the plateau.")
                            npc(npc, SKEPTICAL_THINKING, "We really need you to help us, with some urgency. It will be dangerous, and you may have to face some combat. Can you do it?")
                            questStart(Quest.DEATH_PLATEAU)
                            player(CALM_TALK, "This sounds like a pretty important job, I'll do it.") { player.questManager.setStage(Quest.DEATH_PLATEAU, STAGE_SPEAK_TO_SABBOT) }
                            npc(npc, CALM_TALK, "That's great news. May the gods grant you speed.")
                        }
                        op("What is this place?") {
                            npc(npc, CALM_TALK, "Welcome to the Principality of Burthorpe!")
                            npc(npc, CALM_TALK, "We are the Imperial Guard for his Royal Highness Prince Anlaf of Burthorpe.")
                            npc(npc, CALM_TALK, "Can I assist you with anything else?")
                            goto("initialOps")
                        }
                        op("You can't, thanks.") {
                            npc(npc, CALM_TALK, "Very well.")
                        }
                    }
                }

                in STAGE_SPEAK_TO_SABBOT..STAGE_ANGERED_TROLL -> {
                    player(CALM_TALK, "Hello!")
                    npc(npc, CALM_TALK, "Hello citizen, is there anything you'd like to know?")
                    label("initialOps")
                    options {
                        when (stage) {
                            STAGE_SPEAK_TO_SABBOT -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "I would start by speaking with Sabbot, the dwarf that lives in the cave not far from here. If you go north west of here, past the defensive wall, then his cave is to the north.")
                                    npc(npc, CALM_TALK, "It leads under the plateau. You can't really miss it. He and his wife were scouting the mountains for a suitable location for a trading post when the trolls arrived.")
                                    npc(npc, CALM_TALK, "To get there, go north-west from here, then west past the tower and the ruined wall. Then turn north and you can't miss it.")
                                    npc(npc, CALM_TALK, "It's very likely that he may have a good idea where to find a route up to the plateau.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_SPEAK_TO_FREDA -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "I spoke to Sabbot, and he told me to speak to his wife Freda. Does she live in town?")
                                    npc(npc, CALM_TALK, "No, she maintains a small cottage in the mountains to the west.")
                                    player(CALM_TALK, "Isn't that dangerous with trolls running around?")
                                    npc(npc, CALM_TALK, "Dangerous for the trolls maybe! She may barely come up to a troll's stomach, but she is very handy with an axe.")
                                    npc(npc, CALM_TALK, "Freda sometimes goes and scouts the area, reporting on the troll's movements and numbers.")
                                    npc(npc, CALM_TALK, "In exchange we send her shipments of food and deliver mail to Keldagrim for her.")
                                    npc(npc, CALM_TALK, "To be honest, I think she's starting to like this sort of life, instead of being a simple trader.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_TAKE_BOOTS_DUNSTAN -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "Where can I find Dunstan?")
                                    npc(npc, CALM_TALK, "Dunstan? The smith? What do you need to see him for?")
                                    player(CALM_TALK, "Freda asked me to run an errand while she makes a copy of a geological survey.")
                                    player(CALM_TALK, "Both she and Sabbot think there may be an entrance to underground tunnels that could be used to bypass the plateau.")
                                    npc(npc, CALM_TALK, "An interesting lead... But remember, the more you delay the more likely it is the trolls will attack. Dunstan is up in the north east of the town.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_RETURN_TO_FREDA -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "Well, it is not much of a question. I just thought I would let you know that Dunstan gave me Freda's boots back.")
                                    player(CALM_TALK, "By the time I deliver these, Freda will be finished with her transcription, and I'll be able to look for that secret tunnel.")
                                    npc(npc, CALM_TALK, "Boots...?")
                                    player(CALM_TALK, "Oh, yes, Freda asked me to run an errand for her while she transcribed a survey of the caves under Death Plateau.")
                                    player(CALM_TALK, "With any luck I might be able to find a route up there without the trolls seeing me.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_RECEIVED_SURVEY -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "Can you use this geological survey to get up to the plateau?")
                                    npc(npc, CALM_TALK, "Hmm....I don't think so.")
                                    player(CALM_TALK, "Oh well, I will take it to Sabbot's cave and see what I can do with it.")
                                    npc(npc, CALM_TALK, "Good luck.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_READ_SURVEY -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "Do you have a pickaxe I can borrow?")
                                    npc(npc, CALM_TALK, "Why would I have a pickaxe?")
                                    player(CALM_TALK, "You know, now I come to ask you I do think it would be odd if you had one.")
                                    npc(npc, CALM_TALK, "Why do you need a pickaxe, anyway?")
                                    player(CALM_TALK, "I have found a crack in the wall of Sabbot's cave that may lead to an underground tunnel.")
                                    if (player.containsTool(BRONZE_PICKAXE)) player(CALM_TALK, "Now that I think about it, I have a pickaxe on my tool belt. That should be sufficient to mine through the crack.")
                                    npc(npc, CALM_TALK, "That is interesting. Well, I'll let you get on with it. Remember, time is of the essence.")
                                    player(CALM_TALK, "I will.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_MINED_TUNNEL -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "Do you think you could spare some men to help me scout the tunnel I have found?")
                                    player(CALM_TALK, "I have found a tunnel, and I need to make sure it leads up to the plateau. But if I had some men to help then it would go quicker.")
                                    npc(npc, CALM_TALK, "Gladdened as I am by the news there is a tunnel, we can't spare anyone.")
                                    npc(npc, CALM_TALK, "We've had reports that trolls are slipping past the lines singly and in pairs, looking to mass in caves and crannies in Taverley for a strike behind our lines.")
                                    npc(npc, CALM_TALK, "We have everyone able to carry a sword keeping watch and turning over every rock they can find.")
                                    player(CALM_TALK, "That's pretty serious.")
                                    npc(npc, CALM_TALK, "That it is. It just makes it even more important that you find a way up to the plateau as soon as possible.")
                                    npc(npc, CALM_TALK, "As soon as we can begin launching assaults, the trolls will be on their back foot and we'll be able to cut the scouts off.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_FOUND_TROLL -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(SKEPTICAL_THINKING, "Can you use the tunnel I have found if there is a troll at the end?")
                                    npc(npc, SKEPTICAL, "Of course not. If there is a troll there then he might beat the guards as they emerge from the hole.")
                                    npc(npc, CALM_TALK, "And if he manages to warn other then the tunnel will be useless to us.")
                                    player(CALM_TALK, "Good point. I'll go take care of it.")
                                    goto("initialOps")
                                }
                            }
                            STAGE_ANGERED_TROLL -> {
                                op("Can I ask a question about the quest I am on?") {
                                    npc(npc, CALM_TALK, "Of course, what is it you need to know?")
                                    player(CALM_TALK, "Can you give me some tips on killing a troll?")
                                    npc(npc, CALM_TALK, "Always bring plenty of food, and try and keep them out of arm's reach if you can.")
                                    npc(npc, CALM_TALK, "If they do close with you, then don't hold back! Keep hacking until one of you is dead.")
                                    player(CALM_TALK, "Okay, I'll try that.")
                                    goto("initialOps")
                                }
                            }
                        }

                        op("I thought the White Knights controlled Asgarnia.") {
                            npc(npc, CALM_TALK, "You are right, citizen. The White Knights have taken advantage of the old and weak king. They control most of Asgarnia, including Falador, but they do not control Burthorpe!")
                            npc(npc, FRUSTRATED, "We are the prince's elite troops! We keep Burthorpe secure!")
                            npc(npc, SAD, "The White Knights have overlooked us, until now! They are pouring money into their war against the Black Knights, so they are looking for an excuse to stop our funding and I'm afraid they may have found it!")
                            npc(npc, FRUSTRATED, "If we can not destroy the troll camp on Death Plateau then the Imperial Guard will be disbanded and Burthorpe will come under control of the White Knights. We cannot let this happen!")
                            npc(npc, CALM_TALK, "Is there anything else you would like to know?")
                            goto("initialOps")

                        }
                        op("What is this place?") {
                            npc(npc, CALM_TALK, "Welcome to the Principality of Burthorpe!")
                            npc(npc, CALM_TALK, "We are the Imperial Guard for his Royal Highness Prince Anlaf of Burthorpe.")
                            npc(npc, CALM_TALK, "Can I assist you with anything else?")
                            goto("initialOps")
                        }
                        op("That's all, thanks.") {
                            npc(npc, CALM_TALK, "Good speed citizen.")
                        }
                    }
                }

                STAGE_KILLED_THE_MAP -> {
                    player(HAPPY_TALKING, "I've found a hidden overlook point over the route that leads up to Death Plateau!")
                    npc(npc, SKEPTICAL_THINKING, "Can we use it to get onto the plateau itself?")
                    player(SAD, "Not really... but it is a great place to attack the trolls from where they don't expect it.")
                    npc(npc, HAPPY_TALKING, "Ambush their reinforcements as they come through the choke point... I love it! We'll thin their numbers and give our troops on the front lines some respite.")
                    npc(npc, HAPPY_TALKING, "Come, let's plan a little surprise for the trolls. You've done great work today.")
                    if (player.inventory.freeSlots >= 3) {
                        exec {
                            player.fadeScreen {
                                player.interfaceManager.removeSubs(*InterfaceManager.Sub.ALL_GAME_TABS)
                                player.playPacketCutscene(10) {
                                    player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
                                    player.completeQuest(Quest.DEATH_PLATEAU)
                                    player.setCloseInterfacesEvent {
                                        player.setCloseInterfacesEvent(null)
                                        player.startConversation {
                                            npc(npc, HAPPY_TALKING, "That went very well.")
                                            npc(npc, CALM_TALK, "When you're ready, those archers will need some crates delivering to them.")
                                            npc(npc, CALM_TALK, "We have a number of reward lamps for you if you want them.")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        simple("You'll need at least 3 free inventory slots to receive your reward.")
                        player(CALM_TALK, "I'll go clear out my backpack first.")
                    }
                }

            }
        }
    }
}
