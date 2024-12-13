package com.rs.game.content.randomevents

import com.rs.Launcher
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.World.getNPCsInChunkRange
import com.rs.game.World.spawnNPC
import com.rs.game.model.entity.npc.NPC
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Tile
import com.rs.lib.util.Logger
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val postiePeteID = 3805
private var finishedTalking = false
private var isFinishing = false

enum class PostiePeteLocation(val peteTile: Tile, val npcId: Int) {
    VARROCK_LIBRARY(Tile.of(3209, 3496, 0), 647),
    NORTH_PORT_PHASMATYS(Tile.of(3679, 3536, 0), 2825),
    BANDIT_CAMP(Tile.of(3025, 3700, 0), 597),
    LAUGHING_MINER(Tile.of(2916, 10191, 0), 2178),
    CASTLE_WARS(Tile.of(2446, 3097, 0), 1526),
    PARTY_ROOM(Tile.of(3054, 3373, 0), 659),
    ARDOUGNE_ZOO(Tile.of(2614, 3284, 0), 1216),
    ICE_MOUNTAIN(Tile.of(3011, 3501, 0), 746)
}

class PostiePeteWorldTalk {
    companion object {

        private val conversations = mapOf(
            PostiePeteLocation.VARROCK_LIBRARY to listOf(
                "Postie Pete: Reldo, have you found that book I was looking for yet?",
                "Reldo: No, not yet... but I know I have seen it somewhere...",
                "Reldo: ... come back later.",
                "Postie Pete: Sure, no problems. See you later.",
                "Reldo: Bye! Say hi to the other Petes for me.",
                "Postie Pete: Will do. Farewell!"
            ),
            PostiePeteLocation.BANDIT_CAMP to listOf(
                "Postie Pete: Psssst! How is the plan going?",
                "Noterazzo: Perfectly. Everyone is prepared.",
                "Postie Pete: Great. Now don't forget - nobody does anything until I give the word! OK?",
                "Noterazzo: Sure no problem. We'll be waiting.",
                "Postie Pete: 'Till next time. Farewell."
            ),
            PostiePeteLocation.LAUGHING_MINER to listOf(
                "Postie Pete: 4 Pints of your finest please.",
                "Barmaid: 4 Pints coming right up. Are you expecting guests?",
                "Postie Pete: Just a few old friends.",
                "Barmaid: You want me to put it on your tab?",
                "Postie Pete: That would be great, thanks. Oh cripes! I've left a fire burning! I'll be back in a mo."
            ),
            PostiePeteLocation.CASTLE_WARS to listOf(
                "Postie Pete: Fight! Fight!.. Fight!!",
                "Lanthus: Easy Pete, calm down. Don't lose your head!",
                "Postie Pete: Cheeky!! If you're not careful I'll get you cursed too!",
                "Lanthus: You wouldn't catch me double-crossing a witch!",
                "Postie Pete: But which witch is which! Anyway, I'm late for an appointment with the oracle.",
                "Lanthus: Take care and say hi to the kids for me."
            ),
            PostiePeteLocation.PARTY_ROOM to listOf(
                "Postie Pete: Hey, bro! How's the party business?",
                "Party Pete: Great, thanks!",
                "Party Pete: Celebrating weddings and drop partys...",
                "Party Pete: ...and getting paid for it!",
                "Postie Pete: Nice. Well, I'm here to talk to some White Knights.",
                "Party Pete: Want to take some cake with you?",
                "Postie Pete: No thanks, I'm still dieting!",
                "Postie Pete: Just seafood for me."
            ),
            PostiePeteLocation.ARDOUGNE_ZOO to listOf(
                "Postie Pete: Hi Pete!",
                "Postie Pete: Mum wants to know if you're coming round for tea.",
                "Parroty Pete: No, I've got another late night here.",
                "Parroty Pete: Ol' bluebeak's got the flu again!",
                "Postie Pete: Oh dear. Nothing too serious I hope.",
                "Parroty Pete: Well, as long as he hasn't caught anything from that evil chicken.",
                "Postie Pete: Got a taste for parrots now too has he?",
                "Postie Pete: *Sigh.* Will nothing sacred be safe?"
            ),
            PostiePeteLocation.NORTH_PORT_PHASMATYS to listOf(
                "Postie Pete: Hey Pete. Any news from the fleet?",
                "Pirate Pete: No yo ho bruv.",
                "Pirate Pete: 'Tis all quiet on the eastern front.",
                "Postie Pete: Good good.",
                "Postie Pete: Well, you know what to do if something goes wrong.",
                "Pirate Pete: Aye, that I do bruv.",
                "Pirate Pete: Holler like a bosun without a bottle.",
                "Postie Pete: That's it brother, and we'll come a running!"
            ),
            PostiePeteLocation.ICE_MOUNTAIN to listOf(
                "Postie Pete: Well, I'm back. And he said no. Again.",
                "Oracle: Lemons? Both of them?",
                "Postie Pete: Lemons? What are you talking about?",
                "Oracle: Fragile! Do not bend!",
                "Postie Pete: I swear you get stranger every time.",
                "Postie Pete: Ok, I'll ask again."
            )
        )

        fun spawnPostiePete(location: PostiePeteLocation) {
            val spawnedPete = spawnNPC(postiePeteID, location.peteTile)
            val targetNpc = getNPCsInChunkRange(spawnedPete.chunkId, 1).find { it.id == location.npcId }
            if (targetNpc != null) {
                spawnedPete.spotAnim(86)
                spawnedPete.forceTalk("Anyone got post?")
                finishedTalking = false
                isFinishing = false
                doWorldTalk(spawnedPete, targetNpc, conversations[location] ?: emptyList())
            }
            else {
                spawnedPete.finish()
                //Logger.info(Launcher::class.java, "PostiePeteWorldTalk", "Pete tried to talk to ${location.npcId} at ${location.peteTile} but the npc was not spawned. Killing Pete.")
            }
        }

        private fun doWorldTalk(pete: NPC, npc: NPC, conversation: List<String>) {
            if (conversation.isEmpty()) {
                Logger.info(Launcher::class.java, "PostiePeteWorldTalk", "Pete tried to talk to ${npc.id} at ${npc.tile} but the conversation is empty. Killing Pete.")
                pete.finish()
                return
            }
            pete.faceEntity(npc)
            WorldTasks.scheduleTimer(4) { loop ->
                val line = loop / 4
                if (finishedTalking && loop >= 100) {
                    when (loop) {
                        100 -> pete.forceTalk("I'd better head off.")
                        101 -> isFinishing = true //Stops it triggering with line == conversation.size??
                        in 102..103 -> return@scheduleTimer true
                        104 -> pete.forceTalk("Now, how's this thing work again...?")
                        in 105 ..107 -> return@scheduleTimer true
                        else -> {
                            pete.spotAnim(86)
                            pete.finish()
                            if(pete.hasFinished()) {
                                return@scheduleTimer false
                            }
                        }
                    }
                }
                else
                    when {
                        line < conversation.size -> {
                            val message = conversation[line]
                            if (loop % 4 == 0) {
                                if (message.startsWith("Postie Pete:")) {
                                    pete.forceTalk(message.substringAfter("Postie Pete: "))
                                }
                                else
                                    npc.forceTalk(message.substringAfter(": "))
                            }
                        }
                        line == conversation.size -> {
                            finishedTalking = true
                        }
                    }
                return@scheduleTimer true
            }
        }
    }
}

object PostiePeteStartup {
    @JvmStatic
    @ServerStartupEvent
    fun initializePostiePete() {
        var lastLocation: PostiePeteLocation? = null
        WorldTasks.scheduleTimer { tick: Int ->
            if (tick % 120 == 0) {
                val location = PostiePeteLocation.entries[Utils.random(PostiePeteLocation.entries.size)]
                if (location != lastLocation) {
                    //Logger.info(Launcher::class.java, "PostiePeteWorldTalk", "Trying to spawn pete at ${location.name}.")
                    PostiePeteWorldTalk.spawnPostiePete(location)
                    lastLocation = location
                }
            }
            true
        }
    }

    @JvmStatic
    @ServerStartupEvent
    fun mapPostiePeteClick() {
        onNpcClick(postiePeteID) { (player, npc) ->
            if (!finishedTalking) {
                player.sendMessage("Postie Pete is talking to someone - don't you know it's rude to interrupt?")
                return@onNpcClick
            }
            if (isFinishing) {
                player.sendMessage("Postie Pete is preparing to teleport somewhere else, distracting him could have some terrible side effects.")
                return@onNpcClick
            }
            when (npc.tile) {
                PostiePeteLocation.ARDOUGNE_ZOO.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "Hey Pete, what you doing here?")
                        npc(npc.id, HeadE.CALM_TALK, "Just chatting to my brother Pete. He keeps Parrots.")
                        player(HeadE.CALM_TALK, "Doesn't sound too tough. What's the pay like?")
                        npc(npc.id, HeadE.CALM_TALK, "Not very good I'm afraid. His bosses are pretty cheep.")
                        player(HeadE.CALM_TALK, "Groan. Don't give up the day job Pete.")
                    }
                }
                PostiePeteLocation.CASTLE_WARS.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "Hey Pete, what you doing here?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Just doing a quick survey on famous people and their favourite colour. It's a special feature for 'West Wyverns Women's Weekly'."
                        )
                        player(HeadE.CALM_TALK, "Can I get a copy?")
                        npc(npc.id, HeadE.CALM_TALK, "Can you speak Wyvern?")
                        player(HeadE.CALM_TALK, "No. Can you?")
                        npc(npc.id, HeadE.CALM_TALK, "Well obviously.")
                        player(HeadE.CALM_TALK, "Go on then.")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Here! Are you mad? No, 'fraid I can't do that. Could cause all sorts of trouble."
                        )
                        player(HeadE.CALM_TALK, "You're a bit mad really aren't you?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "So would you be if you could speak over 1000 languages. I bet it's bliss being ignorant of this world's trouble! I'm off!"
                        )
                    }
                }
                PostiePeteLocation.ICE_MOUNTAIN.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "Hey Postie Pete how's it going?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "I was just trying to get some wise words of wisdom from the Oracle, You know, she's been here a long time"
                        )
                        player(HeadE.CALM_TALK, "Yes, I thought as much, what did she have to say?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "First she said, \"I am the Oracle. And when I open my lips, let no dog bark\"!"
                        )
                        player(HeadE.CALM_TALK, "I see. And then what?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "I didn't know she was familiar with the works of Shakespeare. So I said 'Et tu, brute?'"
                        )
                        player(HeadE.CALM_TALK, "And then?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Well then she started pointing to Ardougne and rambling on about betrayal!"
                        )
                    }
                }
                PostiePeteLocation.PARTY_ROOM.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "Hi Pete, how's things?")
                        npc(npc.id, HeadE.CALM_TALK, "Just chatting to my big brother Pete. Hee heee!!")
                        player(HeadE.CALM_TALK, "What's so funny?")
                        npc(npc.id, HeadE.CALM_TALK, "Just a joke that he told me. Care to hear it?")
                        player(HeadE.CALM_TALK, "Yes, go on then.")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "2 Parrots sit on a perch. One says to the other 'Can you smell fish'? Hee heheee he ho ho."
                        )
                        player(HeadE.CALM_TALK, "And so what did the other one say?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Sigh. 2 Parrots sit on a perch. One says to the other 'Can you smell fish'? Hee heheee he ho ho."
                        )
                        player(HeadE.CALM_TALK, "I see. So... what did the other one say?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "You're serious aren't you. Listen again. 2 Parrots sit on a perch. One says to the other 'Can you smell fish'."
                        )
                        player(HeadE.CALM_TALK, "Fish, right. So, what was the reply?")
                        npc(npc.id, HeadE.CALM_TALK, "ARGH!! 2 PARROTS. ON A PERCH. SMELLS FISHY.")
                        player(HeadE.CALM_TALK, "No, still lost. Is one of them a penguin?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "You're doing it on purpose now aren't you? Please leave me alone!"
                        )
                    }
                }
                PostiePeteLocation.LAUGHING_MINER.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "Hey Postie. Hows tricks? Who are you interviewing here?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Everyone deserves a hic break you know. It gets pretty tiring lugging burp letters about all day."
                        )
                        player(HeadE.CALM_TALK, "I see. Can I join you?")
                        npc(npc.id, HeadE.CALM_TALK, "Why, am I hic coming apart?")
                        player(HeadE.CALM_TALK, "You've been here a while, haven't you?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Maybe I haves, maybe I haves. It all goes straight to my head you see."
                        )
                        player(HeadE.CALM_TALK, "Yes... I can see that. Bye Pete!")
                    }
                }
                PostiePeteLocation.NORTH_PORT_PHASMATYS.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "Hey Pete, what you doing here?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Arrrr. Avast shipmate. Arrr. I Likes the cut of your gibb. Arrr. That it be."
                        )
                        player(HeadE.CALM_TALK, "Arrr?")
                        npc(npc.id, HeadE.CALM_TALK, "Arrrr.")
                        player(HeadE.CALM_TALK, "I have a strange feeling of Deja Vu. I must go now.")
                    }
                }
                PostiePeteLocation.VARROCK_LIBRARY.peteTile -> {
                    player.startConversation {
                        player(HeadE.CALM_TALK, "HEY PETE, WHAT ARE YOU DOING HERE?")
                        npc(npc.id, HeadE.CALM_TALK, "Shhhh! It's a library you know!")
                        player(HeadE.CALM_TALK, "Oh, sorry! So, what's new?")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Not much, just doing some research on Dwarvern Culture. Did you know that Keldagrim was originally constructed by a long lost clan who embraced the power of magic, and used it to aid with glorious buildings like the royal palace?"
                        )
                        player(HeadE.CALM_TALK, "Really??")
                        npc(
                            npc.id,
                            HeadE.CALM_TALK,
                            "Yes. They even stayed down there for 1000 years or more, in a time they call 'The age of Kings'."
                        )
                        player(HeadE.CALM_TALK, "WOW!")
                        npc(npc.id, HeadE.CALM_TALK, "Shhhh! That's enough history for one day I think!")
                    }
                }
            }
        }
    }
}