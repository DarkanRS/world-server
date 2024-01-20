package com.rs.game.content.quests.naturespirit

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.Options
import com.rs.engine.dialogue.startConversation
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.canSpeakWithGhosts
import com.rs.game.content.skills.magic.Magic
import com.rs.game.content.world.areas.morytania.npcs.DREZEL
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile

fun drezelNatureSpiritOptions(player: Player, npc: NPC, options: Options) {
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
                label("options")
                options {
                    op("Who is this Filliman?") {
                        player(CONFUSED, "Who is this Filliman?")
                        npc(DREZEL, CALM_TALK, "Filliman Tarlock is his full name and he's a Druid. He lives in Mort Myre much like a hermit, but there's many a traveller who he's helped.")
                        npc(DREZEL, CHEERFUL, "Most people that come his way tell stories of when they were lost and paths that just seemed to 'open up' before them! I think it was Filliman Tarlock helping out.")
                        goto("options")
                    }
                    op("Where's Mort Myre?") {
                        player(CONFUSED, "Where's Mort Myre?")
                        npc(DREZEL, CALM_TALK, "Mort Myre is a decayed and dangerous swamp to the south. It was once a beautiful forest but has since become filled with vile emanations from within Morytania.")
                        npc(DREZEL, CALM_TALK, "The swamp decays everything. We put a fence around it to stop unwary travellers going in. Anyone who dies in the swamp is forever cursed to haunt it as a Ghast. Ghasts attack travellers, turning food to rotten filth.")
                        goto("options")
                    }
                    op("What's a Ghast?") {
                        player(CONFUSED, "What's a Ghast?")
                        npc(DREZEL, CALM_TALK, "A Ghast is a poor soul who died in Mort Myre. They're undead of a special class, they're untouchable as far as I'm aware!")
                        npc(DREZEL, CALM_TALK, "Filliman knew how to tackle them, but I've not heard from him in a long time. Ghasts, when they attack, will devour any food you have. If you have no food, they'll draw their nourishment from you!")
                        goto("options")
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
                label("options")
                options {
                    op("Where should I look for Filliman Tarlock again?") {
                        player(CONFUSED, "Where should I look for Filliman Tarlock again?")
                        npc(DREZEL, CALM_TALK, "Search to the south of Mort Myre. Remember that he's a druid so he can conceal himself within nature quite well. My guess is that he lives in the southern area of the swamp, though he could be anywhere.")
                        goto("options")
                    }
                    op("Explain to me what a Ghast is again.") {
                        player(CONFUSED, "Explain to me what a Ghast is again.")
                        npc(DREZEL, CALM_TALK, "A Ghast is a poor soul who died in Mort Myre. They're undead of a special class, they're untouchable without special druidic items.")
                        npc(DREZEL, CALM_TALK, "Filliman knew how to tackle them, but I've not heard from him in a long time. Ghasts, when they attack, will devour any food you have. If you have no food, they'll draw their nourishment from you!")
                        goto("options")
                    }
                    op("What's the story with Mort Myre?") {
                        player(CONFUSED, "What's the story with Mort Myre?")
                        npc(DREZEL, CALM_TALK, "Mort Myre was once a beautiful forest by the name of Humblethorn until the evil denizens of Morytania descended. Now their evil emanations have putrified and diseased the forest into a decaying swamp of death.")
                        goto("options")
                    }
                    op("What's the story with Filliman Tarlock?") {
                        player(CONFUSED, "What's the story with Filliman Tarlock?")
                        npc(DREZEL, CALM_TALK, "Filliman is a druid of some considerable power. He helped many people in Morytania escape when the evil descended upon the land. His knowledge of plants and nature was exceptional.")
                        npc(DREZEL, CALM_TALK, "But one day, he was betrayed by some of the people who he had tried to help. This naturally made him more careful when dealing with strangers again and so, instead of showing himself, he would follow them.")
                        npc(DREZEL, CALM_TALK, "He would follow them at a distance and make the path clear for them, showing them to the temple and to salvation. Saradomin bless him! He is a good man.")
                        goto("options")
                    }
                    op("Ok, thanks.") {
                        npc(DREZEL, CHEERFUL, "Many thanks to you, ${player.genderTerm("brother", "sister")}!")
                    }
                }
            }
        }

        STAGE_PROVE_GHOST, STAGE_GAVE_JOURNAL -> options.add("Talk about Nature Spirit") {
            player.startConversation {
                npc(DREZEL, CHEERFUL, "Greetings again adventurer. How go your travels in Morytania? Is it as evil as I have heard?")
                player(SAD_MILD, "I've found Filliman and you should prepare for some sad news.")
                npc(DREZEL, CONFUSED, "You mean... he's dead?")
                player(SAD_MILD, "Yes, but I am still in the process of helping him with something.")
                npc(DREZEL, CONFUSED, "Hmmm, that's very interesting, please do let me know how you get on with it.")
            }
        }

        STAGE_GET_BLESSED -> options.add("Talk about Nature Spirit") {
            player.startConversation {
                npc(DREZEL, CHEERFUL, "Greetings again adventurer. How go your travels in Morytania? Is it as evil as I have heard?")
                player(CHEERFUL, "Hello again! I'm helping Filliman, he plans to become a nature spirit. I have a spell to cast but first I need to be blessed. Can you bless me?")
                npc(DREZEL, CHEERFUL, "But you haven't sneezed!")
                player(CHEERFUL, "You're so funny! But can you bless me?")
                npc(DREZEL, CHEERFUL, "Very well my friend, prepare yourself for the blessings of Saradomin. Here we go!") {
                    npc.anim(811)
                    player.anim(645)
                    npc.forceTalk("Ashustru, blessidium, adverturasi, fidum!")
                    World.sendProjectile(npc, player, 268, 15, 15, 0.6) { player.spotAnim(259) }
                    player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_CAST_BLOOM)
                }
                npc(DREZEL, CHEERFUL, "There you go my friend, you're now blessed. It's funny, now I look at you, there seems to be something of the faith about you. It sounds like you should get back to Filliman without delay. Would you like me to use my runes to teleport you?")
                options {
                    opExec("Yes, please.") {
                        npc.anim(811)
                        Magic.sendNormalTeleportSpell(player, Tile.of(3439, 3335, 0))
                    }
                    op("No thank you.") {
                        npc(DREZEL, CHEERFUL, "I will keep them then, unless you manage to find our own way there.")
                    }
                }
            }
        }

        STAGE_CAST_BLOOM, STAGE_PUZZLING_IT_OUT, STAGE_MEET_IN_GROTTO -> options.add("Talk about Nature Spirit") {
            player.startConversation {
                npc(DREZEL, CONFUSED, "How's life been treating you since you got blessed?")
                player(CHEERFUL, "No so bad!")
                //Didn't read and take the teleport earlier? Get bent idiot.
            }
        }

        STAGE_BRING_SICKLE -> options.add("Talk about Nature Spirit") {
            player.startConversation {
                npc(DREZEL, CHEERFUL, "Greetings again adventurer. How go your travels in Morytania? Is it as evil as I have heard?")
                player(CHEERFUL, "Hi again. Filliman has turned into the Nature Spirit. I now need to get a silver sickle so that I can try and defeat the Ghasts. Do you know where I could get something like that?")
                npc(DREZEL, CHEERFUL, "Well, let me think now. Aha, yes, if you're making something from silver, you'll more than likely need a mould, most crafting shops sell them, you could get one from Al Kharid. You can mine silver from a silver rock, you can find some on the way to Al Kharid.")
            }
        }

        STAGE_KILL_GHASTS -> options.add("Talk about Nature Spirit") {
            player.startConversation {
                player(CHEERFUL, "Hiya, I'm a mighty Ghast killer!")
                npc(DREZEL, CHEERFUL, "That's great! How many did Filliman ask you kill?")
                player(CHEERFUL, "He asked me to kill 3!")
                npc(DREZEL, CHEERFUL, "Better kill them then go and tell him, I'll bet he'll be pleased.")
            }
        }
    }
}

fun fillimanDialogue(player: Player, npc: NPC) {
    fun checkGhostSpeak(player: Player, builder: DialogueBuilder): Boolean {
        if (!player.canSpeakWithGhosts()) {
            builder.npc(FILLIMAN, SAD, "Ahhrs Ooooooh arhhhhAHhhh.")
            return false
        }
        return true
    }

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
                            player.inventory.deleteItem(2967, 1)
                            player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_GAVE_JOURNAL)
                        }
                    } else {
                        player(CONFUSED, "Where did you put it?")
                        npc(FILLIMAN, CONFUSED, "Well, if I knew that, I wouldn't still be looking for it. However, I do remember something about a knot? Perhaps I was meant to tie a knot or something?")
                    }
                }
                return@startConversation
            } else {
                player(CONFUSED, "Hello?")
                npc(FILLIMAN, AMAZED_MILD, "Oh, I understand you! At last, someone who doesn't just mumble. I understand what you're saying!")
            }
            label("options")
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
                    goto("options")
                }
                op("How long have you been a ghost?") {
                    player(CONFUSED, "How long have you been a ghost?")
                    npc(FILLIMAN, AMAZED_MILD, "What?! Don't be preposterous! I'm not a ghost! How could you say something like that?")
                    player(CONFUSED, "But it's true, you're a ghost... well, at least that is to say, you're sort of not alive anymore.")
                    npc(FILLIMAN, SAD, "Don't be silly, I can see you. I can see that tree. If I were dead, I wouldn't be able to see anything. What you say just doesn't reflect the truth. You'll have to try harder to put one over on me!") { if (player.getQuestStage(Quest.NATURE_SPIRIT) < STAGE_PROVE_GHOST) player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_PROVE_GHOST) }
                    goto("options")
                }
                op("What's it like being a ghost?") {
                    player(CONFUSED, "What's it like being a ghost?")
                    npc(FILLIMAN, SAD, "Oh, it's quite.... Oh... Trying to catch me out were you! Anyone can clearly see that I am not a ghost!")
                    player(CONFUSED, "But you are a ghost, look at yourself! I can see straight through you! You're as dead as this swamp! Err... No offence or anything...")
                    npc(FILLIMAN, SAD, "No I won't take offence because I'm not dead and I'm afraid you'll have to come up with some pretty conclusive proof before I believe it. What a strange dream this is.") { if (player.getQuestStage(Quest.NATURE_SPIRIT) < STAGE_PROVE_GHOST) player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_PROVE_GHOST) }
                    goto("options")
                }
                op("Ok, thanks.")
            }
        }

        STAGE_GAVE_JOURNAL -> player.startConversation {
            if (!checkGhostSpeak(player, this)) return@startConversation

            npc(FILLIMAN, SAD, "Thanks for the journal, I've been reading it. It looks like I came to a violent and bitter end but that's not really important. I just have to figure out what I am going to do now?")
            label("options")
            options {
                op("Being dead, what options do you think you have?") {
                    player(CONFUSED, "Being dead, what options do you think you have? I'm not trying to be rude or anything, but it's not like you have many options is it? I mean, it's either up or down for you isn't it?")
                    npc(FILLIMAN, SAD, "Hmm, well you're a poetic one aren't you. Your material world logic stands you in good stead... If you're standing in the material world...")
                    goto("options")
                }
                op("So, what's your plan?") {
                    player(CONFUSED, "So, what's your plan?")
                    npc(FILLIMAN, CHEERFUL, "In my former incarnation I was Filliman Tarlock, a great druid of some power. I spent many years in this place, which was once a forest and I would wish to protect it as a nature spirit.")
                    goto("options")
                }
                op("Well, good luck with that.") {
                    player(SKEPTICAL, "Well, good luck with that.")
                    npc(FILLIMAN, SAD, "Won't you help me to become a nature spirit? I could really use your help!")
                    goto("options")
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

        STAGE_GET_BLESSED -> player.startConversation {
            if (!checkGhostSpeak(player, this)) return@startConversation

            npc(FILLIMAN, CONFUSED, "Hello there, have you been blessed yet?")
            player(CALM_TALK, "No, not yet.")
            npc(FILLIMAN, CHEERFUL, "Well, hurry up!")
            if (!player.containsItem(2968)) {
                player(CONFUSED, "Could I have another bloom scroll please?")
                npc(FILLIMAN, CALM_TALK, "Sure, but please look after this one.")
                item(2968, "The spirit of Filliman Tarlock gives you another bloom spell.") { player.inventory.addItem(2968) }
            }
        }

        STAGE_CAST_BLOOM -> player.startConversation {
            if (!checkGhostSpeak(player, this)) return@startConversation

            if (player.questManager.getAttribs(Quest.NATURE_SPIRIT).getB("castedBloom") && player.inventory.containsItem(2970)) {
                npc(FILLIMAN, CONFUSED, "Did you manage to get something from nature?")
                item(2970, "You show the fungus to Filliman")
                player(CHEERFUL, "Yes, I have a fungus here that I picked.")
                npc(FILLIMAN, CHEERFUL, "Wonderful, the mushroom represents 'something from nature'. Now we need to work out what the other components of the spell are!") {
                    player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_PUZZLING_IT_OUT)
                }
                return@startConversation
            }
            player(CONFUSED, "Hello, I've been blessed but I don't know what to do now.")
            npc(FILLIMAN, CALM_TALK, "Well, you need to bring 'something from nature', 'something from faith', and 'something of the spirit-to-become freely given'.")
            player(CONFUSED, "Yeah, but what does that mean?")
            npc(FILLIMAN, CONFUSED, "Hmm, it is a conundrum, however, if you use that spell I gave you, you should be able to get from nature. Once you have that, we may be able to puzzle the rest out.")
            if (!player.containsItem(2968)) {
                player(CONFUSED, "Could I have another bloom scroll please?")
                npc(FILLIMAN, CALM_TALK, "Sure, but please look after this one.")
                item(2968, "The spirit of Filliman Tarlock gives you another bloom spell.") { player.inventory.addItem(2968) }
            }
        }

        STAGE_PUZZLING_IT_OUT -> player.startConversation {
            if (!checkGhostSpeak(player, this)) return@startConversation

            npc(FILLIMAN, CHEERFUL, "Hello again! I don't suppose you've found out what the other components of the Nature spell are have you?")
            label("options")
            options {
                op("What are the things that are needed?") {
                    player(CONFUSED, "What are the things that are needed again?")
                    npc(FILLIMAN, CHEERFUL, "The three things are: 'Something with faith', 'something from nature' and 'something of the spirit-to-become freely given'.")
                    player(CONFUSED, "Ok, and 'something from nature' is the mushroom from the bloom spell you gave me?")
                    npc(FILLIMAN, CHEERFUL, "Yes, that's correct, that seems right to me. The other things we need are 'something with faith' and 'something of the spirit-to-become freely given.")
                    player(CONFUSED, "Do you have any ideas what those things are?")
                    npc(FILLIMAN, CHEERFUL, "I'm sorry my friend, but I do not.")
                    goto("options")
                }
                op("What should I do when I have those things?") {
                    player(CONFUSED, "What should we do when we have those things?")
                    npc(FILLIMAN, CHEERFUL, "Ah yes, I looked this up. It says,.. 'to arrange upon three rocks around the spirit-to-become...'. Then I must cast a spell. As you can see, I've already placed the rocks. I must have planned to do this before I died!")
                    player(CONFUSED, "Can we just place the components on any rock?")
                    npc(FILLIMAN, CHEERFUL, "Well, the only thing the journal says is that 'something with faith stands south of the spirit-to-become', but I'm so confused now I don't really know what that means. Oh, if only I had all my faculties!")
                    goto("options")
                }
                op("I think I've solved the puzzle!") {
                    player(CONFUSED, "I think I've solved the puzzle!")
                    npc(FILLIMAN, CHEERFUL, "Oh really.. Have you placed all the items on the stones? Ok, well, lets try!<br>~ The druid attempts to cast a spell. ~")
                    exec {
                        npc.anim(811)
                        val fungus = World.getAllGroundItemsInChunkRange(player.chunkId, 1).firstOrNull { it.id == 2970 && it.tile.isAt(3439, 3336, 0) }
                        val spell = World.getAllGroundItemsInChunkRange(player.chunkId, 1).firstOrNull { it.id == 2969 && it.tile.isAt(3441, 3336, 0) }
                        val success = player.tile.isAt(3440, 3335, 0) && (fungus != null || player.questManager.getAttribs(Quest.NATURE_SPIRIT).getB("placedFungus")) && (spell != null || player.questManager.getAttribs(Quest.NATURE_SPIRIT).getB("placedSpell"))
                        if (success) {
                            fungus.let { World.removeGroundItem(it) }
                            spell.let { World.removeGroundItem(it) }
                            World.sendProjectile(player, npc, 268, 15, 15, 0.6) { player.spotAnim(259) }
                            World.sendProjectile(Tile.of(3439, 3336, 0), npc, 268, 15, 15, 0.6)
                            World.sendProjectile(Tile.of(3441, 3336, 0), npc, 268, 15, 15, 0.6)
                            player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_MEET_IN_GROTTO)
                            player.npcDialogue(FILLIMAN, CHEERFUL, "Aha, everything seems to be in place! You can come through now into the grotto for the final section of my transformation.")
                        } else
                            player.npcDialogue(FILLIMAN, CONFUSED, "Hmm, something still doesn't seem right. I think we need something more before we can continue.")
                    }
                }
                if (!player.containsItem(2968)) {
                    op("Could I have another bloom scroll please?") {
                        player(CONFUSED, "Could I have another bloom scroll please?")
                        npc(FILLIMAN, CALM_TALK, "Sure, but please look after this one.")
                        item(2968, "The spirit of Filliman Tarlock gives you another bloom spell.") {
                            player.inventory.addItem(2968)
                        }
                        goto("options")
                    }
                }
                op("Ok, thanks.")
            }
        }

        STAGE_MEET_IN_GROTTO -> player.startConversation {
            if (!checkGhostSpeak(player, this)) return@startConversation

            npc(FILLIMAN, CHEERFUL, "Well, hello there again, I was just enjoying the grotto. Many thanks for your help, I couldn't have become a Spirit of nature without you.")
            npc(FILLIMAN, CHEERFUL, "I must complete the transformation now. Just stand there and watch the show. Apparently it's quite good.")
            simple("~The ritual begins~") {
                World.sendProjectile(npc.tile.transform(-3, -3), npc.tile, 268, 30, 0, 0.6)
                World.sendProjectile(npc.tile.transform(3, 3), npc.tile, 268, 30, 0, 0.6)
                World.sendProjectile(npc.tile.transform(-2, -2), npc.tile, 268, 30, 0, 0.6)
                World.sendProjectile(npc.tile.transform(2, 2), npc.tile, 268, 30, 0, 0.6)
                World.sendProjectile(npc.tile.transform(-2, 2), npc.tile, 268, 30, 0, 0.6)
                World.sendSpotAnim(npc.tile, SpotAnim(259, 50))
                player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_BRING_SICKLE)
            }
            npc(NATURE_SPIRIT, CHEERFUL, "Hmmm, good, the transformation is complete. Now, my friend, in return for your assistance, I will help you to kill the Ghasts. First bring to me a silver sickle so that I can bless it for you.")
            player(CONFUSED, "A silver sickle? What's that?")
            npc(NATURE_SPIRIT, CHEERFUL, "The sickle is the symbol and weapon of the Druid, you need to construct one of silver so that I can bless it, with its powers you will be able to defeat the Ghasts of Mort Myre.")
        }

        STAGE_BRING_SICKLE -> player.startConversation {
            npc(NATURE_SPIRIT, CONFUSED, "Have you brought me the silver sickle?")
            if (player.inventory.containsOneItem(2961, 2963)) {
                player(CHEERFUL, "Yes, here it is. What are you going to do with it?")
                npc(NATURE_SPIRIT, CHEERFUL, "My friend, I will bless it for you and you will then be able to accomplish great things. Now then, I must cast the enchantment. You can bless a new sickle by dipping it in the holy water of the grotto.") {
                    World.sendProjectile(Tile.of(2271, 5341, 0), player, 268, 30, 0, 0.6)
                    World.sendProjectile(Tile.of(2271, 5340, 0), player, 268, 30, 0, 0.6)
                    World.sendProjectile(Tile.of(2272, 5340, 0), player, 268, 30, 0, 0.6)
                    if (player.inventory.containsItem(2961)) {
                        player.inventory.deleteItem(2961, 1)
                        player.inventory.addItem(2963, 1)
                    }
                }
                simple("~ Your sickle has been blessed! You can bless ~<br>~ a new sickle by dipping it in the grotto waters. ~")
                npc(NATURE_SPIRIT, CHEERFUL, "Now you can go forth and make the swamp bloom. Collect nature's bounty to fill a druids pouch. So armed will the Ghasts be bound to you until, you flee or they are defeated.")
                npc(NATURE_SPIRIT, CHEERFUL, "Before I can make this grotto into an Altar of Nature, I need to be sure that the Ghasts will be kept at bay. Go forth into Mort Myre and slay three Ghasts. You'll be releasing their souls from Mort Myre.")
                item(2957, "The nature spirit gives you an empty druid pouch.") { player.inventory.addItem(2957) }
                npc(NATURE_SPIRIT, CHEERFUL, "You'll need this in order to collect together nature's bounty. When it contains items, it will bind the Ghast to you until you flee or it is defeated.") {
                    player.setQuestStage(Quest.NATURE_SPIRIT, STAGE_KILL_GHASTS)
                }
                return@startConversation
            }
            player(AMAZED_MILD, "No sorry, not yet!")
            npc(NATURE_SPIRIT, CHEERFUL, "Well, come to me when you have it.")
            label("options")
            options {
                op("Where would I get a silver sickle?") {
                    player(CONFUSED, "Where would I get a silver sickle?")
                    npc(NATURE_SPIRIT, CHEERFUL, "You could make one yourself if you're artisan enough. I've heard of a distant sandy place where you can buy the mould that you require, it's similar in many respects to the creating of a holy symbol.")
                    goto("options")
                }
                op("What will you do to the silver sickle?") {
                    player(CONFUSED, "What will you do to the silver sickle?")
                    npc(NATURE_SPIRIT, CHEERFUL, "Why, I will give it my blessings so that the very swamp in which you stand will blossom and bloom!")
                    goto("options")
                }
                op("How can a blessed sickle help me to defeat the Ghasts?") {
                    player(CONFUSED, "How can a blessed sickle help me to defeat the Ghasts?")
                    npc(NATURE_SPIRIT, CHEERFUL, "My blessings will entice nature to bloom in Mort Myre! And then with nature's harvest you can fill a druids pouch and release the Ghasts from their torment.")
                    goto("options")
                }
                op("Ok, thanks.")
            }
        }

        STAGE_KILL_GHASTS -> player.startConversation {
            npc(NATURE_SPIRIT, CONFUSED, "Hello again, my friend. Have you defeated three ghasts as I asked you?")
            if (player.questManager.getAttribs(Quest.NATURE_SPIRIT).getI("ghastsKilled") >= 3) {
                player(CHEERFUL, "Yes, I've killed all three and their spirits have been released!")
                npc(NATURE_SPIRIT, CHEERFUL, "Many thanks my friend, you have completed your quest! I can now change this place into a holy sanctuary! And forever will it now be an Altar of Nature!")
                npc(NATURE_SPIRIT, CHEERFUL, "Welcome to my Altar to Nature! Farewell my friend, and keep those ghasts at bay!") {
                    player.tele(Tile.of(2271, 5334, 1))
                    player.completeQuest(Quest.NATURE_SPIRIT)
                }
                return@startConversation
            }
            player(CALM_TALK, "Not yet.")
            npc(NATURE_SPIRIT, CHEERFUL, "Well, when you do, please come to me and I'll reward you!")
            options {
                op("How do I get to attack the Ghasts?") {
                    player(CONFUSED, "How do I get to attack the Ghasts?")
                    npc(NATURE_SPIRIT, CHEERFUL, "Go forth and with the sickle make the swamp bloom. Collect natures bounty to fill a druids pouch. So armed will the Ghasts be bound to you until, you flee or they are defeated.")
                }
                op("What's this pouch for?") {
                    player(CONFUSED, "What's this pouch for?")
                    npc(NATURE_SPIRIT, CHEERFUL, "It is for collecting natures bounty, once it contains the blossomed items of the swamp, it will make the Ghasts appear and you can attack them.")
                }
                op("What can I do with this sickle?") {
                    player(CONFUSED, "What can I do with this sickle?")
                    npc(NATURE_SPIRIT, CHEERFUL, "You may use it wisely within the area of Mort Myre to affect nature's balance and bring forth a bounty of natures harvest. Once collected into the druid pouch will the Ghast be apparent.")
                }
                op("I've lost my sickle.") {
                    player(SAD, "I've lost my sickle.")
                    npc(NATURE_SPIRIT, CHEERFUL, "If you should lose the blessed sickle, simply bring another to my altar of nature and refresh it in the grotto waters.")
                }
                op("Ok, thanks.")
            }
        }
    }
}