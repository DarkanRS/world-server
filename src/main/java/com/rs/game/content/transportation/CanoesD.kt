package com.rs.game.content.transportation

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills

const val BARFY_BILL = 3331
const val TARQUIN = 3328
const val SIGURD = 3329
const val HARI = 3330

class CanoeD(player: Player, npc: NPC) : Conversation(player) {

    init {
        when (npc.id) {
            BARFY_BILL -> {
                player.startConversation(Dialogue()
                    .addNPC(BARFY_BILL, HeadE.CHEERFUL, "Oh! Hello there.")
                    .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
            }

            TARQUIN -> {
                player.startConversation(Dialogue()
                    .addNPC(TARQUIN, HeadE.CHEERFUL, "Hello old bean.")
                    .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
            }

            SIGURD -> {
                player.startConversation(Dialogue()
                    .addNPC(SIGURD, HeadE.CHEERFUL, "Ha Ha! Hello!")
                    .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
            }

            HARI -> {
                player.startConversation(Dialogue()
                    .addNPC(HARI, HeadE.CHEERFUL, "Hello.")
                    .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
            }
        }
        create()
    }

    private fun initialOptions(initialOps: Options, npc: NPC) {
        initialOps.add("Who are you?") {
            when (npc.id) {
                BARFY_BILL -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CALM, "Who are you?")
                        .addNext {
                            barfyBillWhoAreYou(player)
                        })
                }
                TARQUIN -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CALM, "Who are you?")
                        .addNext {
                            tarquinWhoAreYou(player)
                        })
                }
                SIGURD -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CALM, "Who are you?")
                        .addNext {
                            sigurdWhoAreYou(player)
                        })
                }
                HARI -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CALM, "Who are you?")
                        .addNext {
                            hariWhoAreYou(player)
                        })
                }
            }
        }

        initialOps.add("Can you teach me about canoeing?") {
            when (npc.id) {
                BARFY_BILL -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CHEERFUL, "Can you teach me about canoeing?")
                        .addNext {
                            barfyBillTeachCanoeing(player)
                        })
                }
                TARQUIN -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CHEERFUL, "Can you teach me about canoeing?")
                        .addNext {
                            tarquinTeachCanoeing(player)
                        })
                }
                SIGURD -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CHEERFUL, "Can you teach me about canoeing?")
                        .addNext {
                            sigurdTeachCanoeing(player)
                        })
                }
                HARI -> {
                    player.startConversation(Dialogue()
                        .addPlayer(HeadE.CHEERFUL, "Can you teach me about canoeing?")
                        .addNext {
                            hariTeachCanoeing(player)
                        })
                }
            }
        }
    }

    private fun barfyBillWhoAreYou(player: Player) {
        player.startConversation(Dialogue()
            .addNPC(BARFY_BILL, HeadE.CHEERFUL, "My name is Ex Sea Captain Barfy Bill.")
            .addPlayer(HeadE.CHEERFUL, "Ex sea captain?")
            .addNPC(BARFY_BILL, HeadE.CHEERFUL, "Yeah, I bought a lovely ship and was planning to make a fortune running her as a merchant vessel.")
            .addPlayer(HeadE.CHEERFUL, "Why are you not still sailing?")
            .addNPC(BARFY_BILL, HeadE.CHEERFUL, "Chronic sea sickness. My first and only voyage was spent dry heaving over the rails. If I had known about the sea sickness I could have saved myself a lot of money.")
            .addPlayer(HeadE.CHEERFUL, "What are you up to now then?")
            .addNPC(BARFY_BILL, HeadE.CHEERFUL, "Well my ship had a little fire related problem. Fortunately it was well insured. Anyway, I don't have to work anymore so I've taken to canoeing on the river. I don't get river sick! Would you like to know how to make a canoe?")
            .addOptions("What would you like to say?") { barfyBillOptions ->
                barfyBillOptions(barfyBillOptions, player)
            })
    }

    private fun barfyBillOptions(options: Options, player: Player) {
        options.add("Yes, please.") {
            barfyBillTeachCanoeing(player)
        }
        options.add("No, thank you.") {
            player.startConversation(
                Dialogue().addPlayer(HeadE.CONFUSED, "No, thank you."))
        }
    }

    private fun barfyBillTeachCanoeing(player: Player) {
        val wcLevel = player.skills.getLevel(Skills.WOODCUTTING)
        val dialogue = Dialogue()
            .addNPC(BARFY_BILL, HeadE.HAPPY_TALKING, "It's really quite simple. Just walk down to that tree on the bank and chop it down. When you have done that you can shape the log further with your hatchet to make a canoe.")

        when {
            wcLevel >= 57 -> {
                dialogue.addNPC(BARFY_BILL, HeadE.HAPPY_TALKING, "Hoo! You look like you know which end of a hatchet is which. You can easily build one of those Wakas. Be careful if you travel into the Wilderness though. I've heard tell of great evil in that blasted wasteland.")
                    .addPlayer(HeadE.HAPPY_TALKING, "Thanks for the warning Bill.")
            }

            wcLevel in 42..57 -> {
                dialogue.addNPC(BARFY_BILL, HeadE.HAPPY_TALKING, "The best canoe you can make is a Stable Dugout, one step beyond a normal Dugout. With a Stable Dugout you can travel to any place on the river.")
                    .addPlayer(HeadE.CONFUSED, "Even into the Wilderness?")
                    .addNPC(BARFY_BILL, HeadE.LAUGH, "Not likely! I've heard tell of a man up near Edgeville who claims he can use a Waka to get up into the Wilderness. I can't think why anyone would wish to venture into that hellish landscape though.")
            }

            wcLevel in 27..42 -> {
                dialogue.addNPC(BARFY_BILL, HeadE.HAPPY_TALKING, "With your skill in woodcutting you could make my favourite canoe, the Dugout. They might not be the best canoe on the river, but they get you where you're going.")
                    .addPlayer(HeadE.CONFUSED, "How far will I be able to go in a Dugout canoe?")
                    .addNPC(BARFY_BILL, HeadE.CALM, "You will be able to travel 2 stops on the river.")
            }

            wcLevel in 12..27 -> {
                dialogue.addNPC(BARFY_BILL, HeadE.LAUGH, "Hah! I can tell just by looking that you lack talent in woodcutting.")
                    .addPlayer(HeadE.CONFUSED, "What do you mean?")
                    .addNPC(BARFY_BILL, HeadE.LAUGH, "No Callouses! No Splinters! No camp fires littering the trail behind you. Anyway, the only 'canoe' you can make is a log. You'll be able to travel 1 stop along the river with a log canoe.")
            }

            else -> {
                dialogue.addNPC(BARFY_BILL, HeadE.SAD, "Well, you don't look like you have the skill to make a canoe. You need to have at least level 12 woodcutting. Once you are able to make a canoe it makes travel along the river much quicker!")
            }
        }

        player.startConversation(dialogue)
    }

    private fun tarquinWhoAreYou(player: Player) {
        player.startConversation(Dialogue()
            .addNPC(TARQUIN, HeadE.CHEERFUL, "My name is Tarquin Marjoribanks. I'd be surprised if you haven't already heard of me?")
            .addPlayer(HeadE.CHEERFUL, "Why would I have heard of you Mr. Marjoribanks?")
            .addNPC(TARQUIN, HeadE.CHEERFUL, "It's pronounced 'Marchbanks'! You should know of me because I am a member of the royal family of Misthalin!")
            .addPlayer(HeadE.CHEERFUL, "Are you related to King Roald?")
            .addNPC(TARQUIN, HeadE.CHEERFUL, "Oh yes! Quite closely actually. I'm his 4th cousin, once removed on his mothers side.")
            .addPlayer(HeadE.CHEERFUL, "Er... Okay. What are you doing here then?")
            .addNPC(TARQUIN, HeadE.CHEERFUL, "I'm canoeing on the river! It's enormous fun! Would you like to know how?")
            .addOptions("What would you like to say?") { tarquinOptions ->
                tarquinOptions(tarquinOptions, player)
            })
    }

    private fun tarquinOptions(options: Options, player: Player) {
        options.add("Yes, please.") {
            tarquinTeachCanoeing(player)
        }
        options.add("No, thank you.") {
            player.startConversation(
                Dialogue().addPlayer(HeadE.CONFUSED, "No, thank you."))
        }
    }

    private fun tarquinTeachCanoeing(player: Player) {
        val wcLevel = player.skills.getLevel(Skills.WOODCUTTING)
        val dialogue = Dialogue()
            .addNPC(TARQUIN, HeadE.HAPPY_TALKING, "It's really quite simple. Just walk down to that tree on the bank and chop it down. When you have done that you can shape the log further with your hatchet to make a canoe. My personal favourite is the Stable Dugout canoe. A finer craft you'll never see old bean!")
            .addNPC(TARQUIN, HeadE.HAPPY_TALKING, "A Stable Dugout canoe will take you pretty much the length of the Lum river. Of course there are other canoes.")

        when {
            wcLevel >= 57 -> {
                dialogue.addNPC(TARQUIN, HeadE.HAPPY_TALKING, "Well ... erm. You seem to be able to make a Waka!")
                    .addPlayer(HeadE.CONFUSED, "Sounds fun, what's a Waka?")
                    .addNPC(TARQUIN, HeadE.HAPPY_TALKING, "I've only ever seen one man on the river who uses a Waka. A big, fearsome looking fellow up near Edgeville. People say he was born in the Wilderness and that he is looking for a route back.")
                    .addPlayer(HeadE.AMAZED, "Is that true?!")
                    .addNPC(TARQUIN, HeadE.CALM, "How should I know? I would not consort with such a base fellow!")
            }

            wcLevel in 42..57 -> {
                dialogue.addNPC(TARQUIN, HeadE.HAPPY_TALKING, "Ah! Perfect! You can make a Stable Dugout canoe! One of those will carry you to any civilised place on the river. If you were of good pedigree I'd let you join my boat club. You seem to be one of those vagabond adventurers though.")
                    .addPlayer(HeadE.AMAZED_MILD, "Charming!")
                    .addNPC(TARQUIN, HeadE.LAUGH, "Be off with you rogue!")
            }

            wcLevel in 27..42 -> {
                dialogue.addNPC(TARQUIN, HeadE.HAPPY_TALKING, "You seem to be quite handy with a hatchet though! I'm sure you can build a Dugout canoe. Not as fine as a Stable Dugout but it will carry you 2 stops on the river. I should imagine it would suit your limited means.")
                    .addPlayer(HeadE.CONFUSED, "What do you mean when you say 'limited means'?")
                    .addNPC(TARQUIN, HeadE.CALM, "Well, you're just an itinerant adventurer! What possible reason would you have for cluttering up my river with your inferior water craft!")
            }

            wcLevel in 12..27 -> {
                dialogue.addNPC(TARQUIN, HeadE.LAUGH, "Further up river, near the Barbarian Village, I saw some darned fool 'canoeing' on a log! Unfortunately, you don't have the skill to create anything more than one of those logs. I dare say it will only get 1 stop down the river. Still, I'm sure it will satisfy one such as yourself.")
                    .addPlayer(HeadE.AMAZED_MILD, "What's that supposed to mean?!")
                    .addNPC(TARQUIN, HeadE.ANGRY, "Do not profane the royal house of Varrock by engaging me in further discourse you knave!")
                    .addPlayer(HeadE.LAUGH, "Pfft! I doubt he even knows the King!")
            }

            else -> {
                dialogue.addNPC(TARQUIN, HeadE.SAD, "Well, you don't look like you have the skill to make a canoe. You need to have at least level 12 woodcutting. Once you are able to make a canoe it makes travel along the river much quicker!")
            }
        }

        player.startConversation(dialogue)
    }

    private fun sigurdWhoAreYou(player: Player) {
        player.startConversation(Dialogue()
            .addNPC(SIGURD, HeadE.CHEERFUL, "I'm Sigurd the Great and Brainy.")
            .addPlayer(HeadE.CHEERFUL, "Why do they call you the Great and Brainy?")
            .addNPC(SIGURD, HeadE.CHEERFUL, "Because I invented the Log Canoe!")
            .addPlayer(HeadE.CHEERFUL, "Log Canoe?")
            .addNPC(SIGURD, HeadE.CHEERFUL, "Yeash! Me and my cousins were having a great party by the river when we decided to have a game of 'Smack The Tree'.")
            .addPlayer(HeadE.CHEERFUL, "Smack the Tree?")
            .addNPC(SIGURD, HeadE.CHEERFUL, "It's a game where you take it in turnsh shmacking a tree. First one to uproot the tree winsh! Anyway, I won the game with a flying tackle. The tree came loose and down the river bank I went, still holding the tree.")
            .addNPC(SIGURD, HeadE.CHEERFUL, "I woke up a few hours later and found myself several miles down river. And thatsh how I invented the log canoe!")
            .addPlayer(HeadE.CHEERFUL, "So you invented the 'Log Canoe' by falling into a river hugging a tree?")
            .addNPC(SIGURD, HeadE.CHEERFUL, "Well I refined the design from the original you know! I cut all the branches off to make it more comfortable. I could tell you how to if you like?")
            .addOptions("What would you like to say?") { sigurdOptions ->
                sigurdOptions(sigurdOptions, player)
            })
    }

    private fun sigurdOptions(options: Options, player: Player) {
        options.add("Yes, please.") {
            sigurdTeachCanoeing(player)
        }
        options.add("No, thank you.") {
            player.startConversation(
                Dialogue().addPlayer(HeadE.CONFUSED, "No, thank you."))
        }
    }

    private fun sigurdTeachCanoeing(player: Player) {
        val wcLevel = player.skills.getLevel(Skills.WOODCUTTING)
        val dialogue = Dialogue()
            .addNPC(SIGURD, HeadE.HAPPY_TALKING, "It's really quite simple. Just walk down to that tree on the bank and chop it down. Then take your hatchet to it and shape it how you like!")

        when {
            wcLevel >= 57 -> {
                dialogue.addNPC(SIGURD, HeadE.HAPPY_TALKING, "You look like you know your way around a tree, you can make a Waka canoe.")
                    .addPlayer(HeadE.CONFUSED, "What's a Waka?")
                    .addNPC(SIGURD, HeadE.CALM, "I've only ever seen Hari using them. People say he's found a way to canoe the river underground and into the Wilderness. Hari hangs around up near Edgeville. He's a nice bloke.")
            }

            wcLevel in 42..57 -> {
                dialogue.addNPC(SIGURD, HeadE.HAPPY_TALKING, "Well, you're pretty handy with an axe! You could make Stable Dugout canoes, like that snooty fella Tarquin. He reckons his canoes are better than mine. He's never said it to my face though.")
            }

            wcLevel in 27..42 -> {
                dialogue.addNPC(SIGURD, HeadE.HAPPY_TALKING, "You could make a Dugout canoe with your woodcutting skill, but I don't see why you would want to.")
            }

            wcLevel in 12..27 -> {
                dialogue.addNPC(SIGURD, HeadE.HAPPY_TALKING, "You can make a log canoe like mine! It'll get you 1 stop down the river. There's some snooty fella down near the Champions Guild who reckons his canoes are better than mine. He's never said it to my face though.")
            }

            else -> {
                dialogue.addNPC(SIGURD, HeadE.SAD, "Well, you don't look like you have the skill to make a canoe. You need to have at least level 12 woodcutting. Once you are able to make a canoe it makes travel along the river much quicker!")
            }
        }

        player.startConversation(dialogue)
    }

    private fun hariWhoAreYou(player: Player) {
        player.startConversation(Dialogue()
            .addNPC(HARI, HeadE.CHEERFUL, "My name is Hari.")
            .addPlayer(HeadE.CHEERFUL, "And what are you doing here Hari?")
            .addNPC(HARI, HeadE.CHEERFUL, "Like most people who come here to Edgeville, I am here to seek adventure in the Wilderness. I found a secret underground river that will take me quite a long way north.")
            .addPlayer(HeadE.CHEERFUL, "Underground river? Where does it come out?")
            .addNPC(HARI, HeadE.CHEERFUL, "It comes out in a pond located deep in the Wilderness.")
            .addNPC(HARI, HeadE.CHEERFUL, "I had to find a very special type of canoe to get me up the river though, would you like to know more?")
            .addOptions("What would you like to say?") { hariOptions ->
                hariOptions(hariOptions, player)
            })
    }

    private fun hariOptions(options: Options, player: Player) {
        options.add("Yes, please.") {
            hariTeachCanoeing(player)
        }
        options.add("No, thank you.") {
            player.startConversation(
                Dialogue().addPlayer(HeadE.CONFUSED, "No, thank you."))
        }
    }

    private fun hariTeachCanoeing(player: Player) {
        val wcLevel = player.skills.getLevel(Skills.WOODCUTTING)
        val dialogue = Dialogue()
            .addNPC(HARI, HeadE.HAPPY_TALKING, "It's really quite simple to make. Just walk down to that tree on the bank and chop it down. When you have done that you can shape the log further with your hatchet to make a canoe.")

        when {
            wcLevel >= 57 -> {
                dialogue.addNPC(HARI, HeadE.HAPPY_TALKING, "Your skills rival mine friend. You will certainly be able to build a Waka.")
                    .addPlayer(HeadE.CONFUSED, "A Waka? What's that?")
                    .addNPC(HARI, HeadE.HAPPY_TALKING, "A Waka is an invention of my people, it's an incredible strong and fast canoe and will carry you safely to any destination on the river.")
                    .addPlayer(HeadE.AMAZED, "Any destination?")
                    .addNPC(HARI, HeadE.CALM, "Yes, you can take a waka north through the underground portion of this river. It will bring you out at a pond in the heart of the Wilderness. Be careful up there, many have lost more than their lives in that dark and twisted place.")
            }

            wcLevel in 42..57 -> {
                dialogue.addNPC(HARI, HeadE.HAPPY_TALKING, "You seem to be an accomplished woodcutter. You will easily be able to make a Stable Dugout. They are reliable enough to get you anywhere on this river, except to the Wilderness of course. Only a Waka can take you there.")
                    .addPlayer(HeadE.AMAZED_MILD, "A Waka? What's that?")
                    .addNPC(HARI, HeadE.LAUGH, "Come and ask me when you have improved your skills as a woodcutter.")
            }

            wcLevel in 27..42 -> {
                dialogue.addNPC(HARI, HeadE.HAPPY_TALKING, "You are an average woodcutter. You should be able to make a Dugout canoe quite easily. It will take you 2 stops along the river.")
                    .addPlayer(HeadE.CONFUSED, "Can I take a dugout canoe to reach the Wilderness?")
                    .addNPC(HARI, HeadE.LAUGH, "You would never make it there alive.")
                    .addPlayer(HeadE.WORRIED, "Best not to try then.")
            }

            wcLevel in 12..27 -> {
                dialogue.addNPC(HARI, HeadE.LAUGH, "I can sense you're still a novice woodcutter, you will only be able to make a log canoe at present.")
                    .addPlayer(HeadE.CONFUSED, "Is that good?")
                    .addNPC(HARI, HeadE.CALM, "A log will take you one stop along the river. But you won't be able to travel into the Wilderness on it.")
            }

            else -> {
                dialogue.addNPC(HARI, HeadE.SAD, "Well, you don't look like you have the skill to make a canoe. You need to have at least level 12 woodcutting. Once you are able to make a canoe it makes travel along the river much quicker!")
            }
        }

        player.startConversation(dialogue)
    }
}
