package com.rs.game.content.quests.naturespirit

import com.rs.engine.dialogue.Options
import com.rs.engine.dialogue.startConversation
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.content.canSpeakWithGhosts
import com.rs.game.content.world.areas.morytania.npcs.DREZEL
import com.rs.game.model.entity.player.Player

fun drezelNatureSpiritOptions(player: Player, options: Options) {
    if (!Quest.NATURE_SPIRIT.meetsReqs(player))
        return

    when(player.getQuestStage(Quest.NATURE_SPIRIT)) {
        STAGE_UNSTARTED -> options.add("Is there anything else interesting to do around here?") {
            player.startConversation {
                player(CONFUSED, "Is there anything else interesting to do around here?")
                npc(DREZEL, CALM_TALK, "Well, not a great deal... but there is something you could do for me if you're interested. Though it is quite dangerous.")
                questStart(Quest.NATURE_SPIRIT)
                player(CONFUSED, "Well, what is it, I may be able to help?")
                npc(DREZEL, CALM_TALK, "There's a man called Filliman who lives in Mort Myre, I wonder if you could look for him? The swamps of Mort Myre are dangerous, though. They're infested with Ghasts!")
                label("startOps")
                options {
                    op("Who is this Filliman?") {
                        player(CONFUSED, "Who is this Filliman?")
                        npc(DREZEL, CALM_TALK, "Filliman Tarlock is his full name and he's a Druid. He lives in Mort Myre much like a hermit, but there's many a traveller who he's helped.")
                        npc(DREZEL, CHEERFUL, "Most people that come his way tell stories of when they were lost and paths that just seemed to 'open up' before them! I think it was Filliman Tarlock helping out.")
                        goto("startOps")
                    }
                    op("Where's Mort Myre?") {
                        player(CONFUSED, "Where's Mort Myre?")
                        npc(DREZEL, CALM_TALK, "Mort Myre is a decayed and dangerous swamp to the south. It was once a beautiful forest but has since become filled with vile emanations from within Morytania.")
                        npc(DREZEL, CALM_TALK, "The swamp decays everything. We put a fence around it to stop unwary travellers going in. Anyone who dies in the swamp is forever cursed to haunt it as a Ghast. Ghasts attack travellers, turning food to rotten filth.")
                        goto("startOps")
                    }
                    op("What's a Ghast?") {
                        player(CONFUSED, "What's a Ghast?")
                        npc(DREZEL, CALM_TALK, "A Ghast is a poor soul who died in Mort Myre. They're undead of a special class, they're untouchable as far as I'm aware!")
                        npc(DREZEL, CALM_TALK, "Filliman knew how to tackle them, but I've not heard from him in a long time. Ghasts, when they attack, will devour any food you have. If you have no food, they'll draw their nourishment from you!")
                        goto("startOps")
                    }
                    op("Yes, I'll go and look for him.") {
                        player(CHEERFUL, "Yes, I'll go and look for him.")
                        npc(DREZEL, CONFUSED, "That's great, but it's very dangerous. Are you sure you want to do this?")
                        options {
                            op("Yes I'm sure.") {
                                player(CHEERFUL, "Yes, I'm sure")
                                npc(DREZEL, CHEERFUL, "That's great! Many thanks! Now then, please be aware of the Ghasts, you cannot attack them. Only Filliman knew how to take them on.")
                                npc(DREZEL, CHEERFUL, "Just run from them if you can. If you start to get lost, try to make your way back to the temple.")
                                item(2327, "The cleric hands you some food.")
                                npc(DREZEL, CHEERFUL, "Please take this food to Filliman, he'll probably appreciate a bit of cooked food. He used to live to the South of the swamps. Search for him won't you?") {
                                    player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_FIND_FILLIMAN)
                                    player.inventory.addItemDrop(2327, 3)
                                    player.inventory.addItemDrop(2323, 3)
                                }
                                player(CHEERFUL, "I'll do my best, don't worry, if he's in there and he's still alive I'll definitely find him.")
                                npc(DREZEL, CONFUSED, "I saved a few runes to allow me to travel quickly to Filliman's old camp, however, I was never able to use them, and now of course I cannot leave. If you can return to me and confirm he is safe, I will gladly allow you to use them.")

                            }
                            op("Sorry, I don't think I can help.")
                        }
                    }
                    op("Sorry, I don't think I can help.")
                }
            }
        }
        STAGE_FIND_FILLIMAN -> options.add("Talk about Nature Spirit") {
            player.startConversation {
                player(CHEERFUL, "Hello again!")
                npc(DREZEL, CONFUSED, "Have you managed to find Filliman yet?")
                player(SAD, "No, not yet.")
                npc(DREZEL, WORRIED, "Please go and look for him, I would appreciate it!")
                label("startOps")
                options {
                    op("Where should I look for Filliman Tarlock again?") {
                        player(CONFUSED, "Where should I look for Filliman Tarlock again?")
                        npc(DREZEL, CALM_TALK, "Search to the south of Mort Myre. Remember that he's a druid so he can conceal himself within nature quite well. My guess is that he lives in the southern area of the swamp, though he could be anywhere.")
                        goto("startOps")
                    }
                    op("Explain to me what a Ghast is again.") {
                        player(CONFUSED, "Explain to me what a Ghast is again.")
                        npc(DREZEL, CALM_TALK, "A Ghast is a poor soul who died in Mort Myre. They're undead of a special class, they're untouchable without special druidic items.")
                        npc(DREZEL, CALM_TALK, "Filliman knew how to tackle them, but I've not heard from him in a long time. Ghasts, when they attack, will devour any food you have. If you have no food, they'll draw their nourishment from you!")
                        goto("startOps")
                    }
                    op("What's the story with Mort Myre?") {
                        player(CONFUSED, "What's the story with Mort Myre?")
                        npc(DREZEL, CALM_TALK, "Mort Myre was once a beautiful forest by the name of Humblethorn until the evil denizens of Morytania descended. Now their evil emanations have putrified and diseased the forest into a decaying swamp of death.")
                        goto("startOps")
                    }
                    op("What's the story with Filliman Tarlock?") {
                        player(CONFUSED, "What's the story with Filliman Tarlock?")
                        npc(DREZEL, CALM_TALK, "Filliman is a druid of some considerable power. He helped many people in Morytania escape when the evil descended upon the land. His knowledge of plants and nature was exceptional.")
                        npc(DREZEL, CALM_TALK, "But one day, he was betrayed by some of the people who he had tried to help. This naturally made him more careful when dealing with strangers again and so, instead of showing himself, he would follow them.")
                        npc(DREZEL, CALM_TALK, "He would follow them at a distance and make the path clear for them, showing them to the temple and to salvation. Saradomin bless him! He is a good man.")
                        goto("startOps")
                    }
                    op("Ok, thanks.") {
                        npc(DREZEL, CHEERFUL, "Many thanks to you, ${player.genderTerm("brother", "sister")}!")
                    }
                }
            }
        }
    }
}

fun fillimanDialogue(player: Player) {
    when(player.getQuestStage(Quest.NATURE_SPIRIT)) {
        STAGE_FIND_FILLIMAN, STAGE_PROVE_GHOST -> player.startConversation {
            if (!player.canSpeakWithGhosts()) {
                npc(FILLIMAN, CONFUSED, "Cannot wake up... Where am I?")
                player(CONFUSED, "Huh? What's this?")
                npc(FILLIMAN, CONFUSED, "What did I write down now? Put it in the knot hole.")
                npc(FILLIMAN, SAD, "Ahhrs Ooooooh arhhhhAHhhh.")
                player(CONFUSED, "Huh! Now you're just not making any sense at all! I just cannot understand you!")
                return@startConversation
            }
            if (player.getQuestStage(Quest.NATURE_SPIRIT) == STAGE_PROVE_GHOST) {
                player(CHEERFUL, "Hello again!")
                npc(FILLIMAN, SAD, "Oh, hello there, do you still think I'm dead? It's hard to see how I could be dead when I'm still in the world. I can see everything quite clearly. And nothing of what you say reflects the truth.")
                if (player.inventory.containsItem(2966)) {
                    item(2966, "You use the mirror on the spirit of the dead Filliman Tarlock.")
                    player(CALM_TALK, "Here take a look at this, perhaps you can see that you're utterly transparent now!")
                    item(2966, "The spirit of Filliman reaches forwards and takes the mirror.")
                    npc(FILLIMAN, CONFUSED, "Well, that is the most peculiar thing I've ever experienced. This mirror must somehow be dysfunctional. Strange how well it reflects the stagnant swamp behind me, but there is nothing of my own visage apparent.")
                    player(AMAZED_MILD, "That's because you're dead! Dead as a door nail... Deader in fact... You bear a remarkable resemblance to wormbait! Err... No offence...")
                    npc(FILLIMAN, SAD, "I think you might be right my friend, though I still feel very much alive. It is strange how I still come to be here and yet I've not turned into a Ghast. It must be a sign... Yes a sign... I must try to find out what it means. Now, where did I put my journal?")
                    if (player.inventory.containsItem(2967)) {
                        item(2967, "You give the journal to Filliman Tarlock.")
                        player(CONFUSED, "Here, I found this, maybe you can use it?")
                        npc(FILLIMAN, CHEERFUL, "My journal! That should help to collect my thoughts.")
                        item(2967, "~ The spirit starts leafing through the journal ~<br>~ He seems quite distant as he regards the pages ~<br>~ After some time the druid faces you again ~")
                        npc(FILLIMAN, SAD, "It's all coming back to me now. It looks like I came to a violent and bitter end but that's not important now. I just have to figure out what I am going to do now?")
                        exec {
                            player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_GAVE_JOURNAL)
                            player.inventory.deleteItem(2967, 1)
                        }
                    } else {
                        player(CONFUSED, "Where did you put it?")
                        npc(FILLIMAN, CONFUSED, "Well, if I knew that, I wouldn't still be looking for it. However, I do remember something about a knot? Perhaps I was meant to tie a knot or something?")
                    }
                }
            } else {
                player(CONFUSED, "Hello?")
                npc(FILLIMAN, AMAZED_MILD, "Oh, I understand you! At last, someone who doesn't just mumble. I understand what you're saying!")
            }
            label("startOptions")
            options {
                op("I'm wearing an amulet of ghost speak!") {
                    player(CHEERFUL, "I'm wearing an amulet of ghost speak!")
                    npc(FILLIMAN, SAD, "Why you poor fellow, have you passed away and you want to send a message back to a loved one?")
                    player(CONFUSED, "Err.. Not exactly...")
                    npc(FILLIMAN, SAD, "You have come to haunt my dreams until I pass on your message to a dearly loved one. I understand. Pray, tell me who would you like me to pass a message on to?")
                    player(CONFUSED, "Ermm, you don't understand... It's just that..")
                    npc(FILLIMAN, SAD, "Yes!")
                    player(CONFUSED, "Well, please don't be upset or anything... But you're the ghost!")
                    npc(FILLIMAN, AMAZED_MILD, "Don't be silly now! That in no way reflects the truth!") { if (player.getQuestStage(Quest.NATURE_SPIRIT) < STAGE_PROVE_GHOST) player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_PROVE_GHOST) }
                    goto("startOptions")
                }
                op("How long have you been a ghost?") {
                    player(CONFUSED, "How long have you been a ghost?")
                    npc(FILLIMAN, AMAZED_MILD, "What?! Don't be preposterous! I'm not a ghost! How could you say something like that?")
                    player(CONFUSED, "But it's true, you're a ghost... well, at least that is to say, you're sort of not alive anymore.")
                    npc(FILLIMAN, SAD, "Don't be silly, I can see you. I can see that tree. If I were dead, I wouldn't be able to see anything. What you say just doesn't reflect the truth. You'll have to try harder to put one over on me!") { if (player.getQuestStage(Quest.NATURE_SPIRIT) < STAGE_PROVE_GHOST) player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_PROVE_GHOST) }
                    goto("startOptions")
                }
                op("What's it like being a ghost?") {
                    player(CONFUSED, "What's it like being a ghost?")
                    npc(FILLIMAN, SAD, "Oh, it's quite.... Oh... Trying to catch me out were you! Anyone can clearly see that I am not a ghost!")
                    player(CONFUSED, "But you are a ghost, look at yourself! I can see straight through you! You're as dead as this swamp! Err... No offence or anything...")
                    npc(FILLIMAN, SAD, "No I won't take offence because I'm not dead and I'm afraid you'll have to come up with some pretty conclusive proof before I believe it. What a strange dream this is.") { if (player.getQuestStage(Quest.NATURE_SPIRIT) < STAGE_PROVE_GHOST) player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_PROVE_GHOST) }
                    goto("startOptions")
                }
                op("Ok, thanks.")
            }
        }

        STAGE_GAVE_JOURNAL -> player.startConversation {
            if (!player.canSpeakWithGhosts()) {
                npc(FILLIMAN, SAD, "Ahhrs Ooooooh arhhhhAHhhh.")
                return@startConversation
            }
            npc(FILLIMAN, SAD, "Thanks for the journal, I've been reading it. It looks like I came to a violent and bitter end but that's not really important. I just have to figure out what I am going to do now?")
            label("startOptions")
            options {
                op("Being dead, what options do you think you have?") {
                    player(CONFUSED, "Being dead, what options do you think you have? I'm not trying to be rude or anything, but it's not like you have many options is it? I mean, it's either up or down for you isn't it?")
                    npc(FILLIMAN, SAD, "Hmm, well you're a poetic one aren't you. Your material world logic stands you in good stead... If you're standing in the material world...")
                    goto("startOptions")
                }
                op("So, what's your plan?") {
                    player(CONFUSED, "So, what's your plan?")
                    npc(FILLIMAN, CHEERFUL, "In my former incarnation I was Filliman Tarlock, a great druid of some power. I spent many years in this place, which was once a forest and I would wish to protect it as a nature spirit.")
                    goto("startOptions")
                }
                op("Well, good luck with that.") {
                    player(SKEPTICAL, "Well, good luck with that.")
                    npc(FILLIMAN, SAD, "Won't you help me to become a nature spirit? I could really use your help!")
                    goto("startOptions")
                }
                op("How can I help?") {
                    player(CONFUSED, "How can I help?")
                    npc(FILLIMAN, CONFUSED, "Will you help me to become a nature spirit? The directions for becoming one are a bit vague, I need three things but I know how to get one of them. Perhaps you can help collect the rest?")
                    player(SKEPTICAL, "I might be interested, what's involved?")
                    npc(FILLIMAN, SKEPTICAL_THINKING, "Well, the book says, that I need, and I quote:- 'Something with faith', 'something from nature' and the 'spirit-to-become' freely given'. Hmm, I know how to get something from nature.")
                    player(SKEPTICAL_THINKING, "Well, that does seem a bit vague.")
                    npc(FILLIMAN, SAD, "Hmm, it does and I could understand if you didn't want to help. However, if you could perhaps at least get the item from nature, that would be a start. Perhaps we can figure out the rest as we go along.")
                    item(2968, "The druid produces a small sheet of papyrus with some writing on it.")
                    npc(FILLIMAN, CHEERFUL, "This spell needs to be cast in the swamp after you have been blessed. I'm afraid you'll need to go to the temple to the North and ask a member of the clergy to bless you.")
                    player(CONFUSED, "Blessed, what does that do?")
                    npc(FILLIMAN, CHEERFUL, "It is required if you're to cast this druid spell. Once you've cast the spell, you should find something from nature. Bring it back to me and then we'll try to figure out the other things we need.") {
                        player.inventory.addItem(2968)
                        player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_GET_BLESSED)
                    }
                }
                op("Ok, thanks.")
            }
        }
    }
}