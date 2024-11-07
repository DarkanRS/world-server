package com.rs.game.content.world.areas.ardougne.npcs.east_ardougne

import com.rs.db.WorldDB
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.dnds.penguins.*
import com.rs.game.content.items.Lamps.openPenguinHASExpReward
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Larry(val player: Player, val npc: NPC) {
    init {
        val unlockedPenguinHAS = player.getSavingAttributes().getOrDefault(UNLOCKED_PENGUIN_HAS, false) as Boolean
        if (!unlockedPenguinHAS) {
            player.startConversation {
                npc(npc, SECRETIVE, "What do you want?")
                player(CALM_TALK, "Uh, I just wanted to as-")
                npc(npc, SECRETIVE, "SHHHHHH! They're listening. Keep your voice down.")
                player(CONFUSED, "*whispers* Who's listening?")
                npc(npc, SECRETIVE, "Never mind. Are you the inquisitive sort? Are you willing to go on an expedition for me?")
                player(SKEPTICAL_THINKING, "What would I need to do on this expedition?")
                npc(npc, SECRETIVE, "The zoo has granted me permission to study penguins abroad. It's my, er...understanding that there are many penguins located around the world.")
                player(CONFUSED, "Why do you want to find penguins around the world?")
                npc(npc, FOGGY_STORYTELLING, "I need to see if they're organis-, I mean, if they're migrating or something like that.")
                player(CHUCKLE, "You think they're organised? They're just penguins!")
                npc(npc, SECRETIVE, "Do not underestimate them! They're clever and tricky and LISTENING! I know they're up to something. That's why I'm recruiting brave adventurers to find these penguins and tell me of their locations.")
                player(SKEPTICAL_HEAD_SHAKE, "Well, I don't think they're organised, but I do travel all over the world.")
                player(CALM_TALK, "I could give you a hand finding them. What do I need to do?")
                npc(npc, SECRETIVE, "Whenever you spot a penguin, spy on it. They're well trained and will change their positions every week, so keep your eyes peeled.")
                npc(npc, SECRETIVE, "I'll give you a spy notebook so that you can track the penguin spies. Penguins are worth between 1 and 2 Penguin Points depending on how difficult they are to find. Report back here and I will reward you for your efforts.")
                if (player.isQuestComplete(Quest.HUNT_FOR_RED_RAKTUBER))
                    npc(npc, SECRETIVE, "I also get a feeling that there might be a polar bear agent hiding around the world in a village well! I'll give you 1 Penguin Point, if you manage to find it.")
                player(CALM_TALK, "Great, I'll get started right away.")
                if (player.inventory.hasFreeSlots()) {
                    npc(npc, SECRETIVE, "Hold on, there. Take this before you go.")
                    item(13732, "Larry hands you a spy notebook.") {
                        player.inventory.addItem(13732)
                        player.getSavingAttributes()[UNLOCKED_PENGUIN_HAS] = true
                    }
                } else {
                    npc(npc, SECRETIVE, "You'll want to take this spy notebook, but you don't appear to have room for it.")
                    player(CALM_TALK, "I'll be back when I have room to take it.")
                }
            }
        } else {
            player.startConversation {
                val penguinsSpottedCount = WorldDB.getPenguinHAS().getPenguinsSpottedByPlayer(player.username)
                val penguinPoints = player.getI(PENGUIN_POINTS)
                npc(npc, SECRETIVE, "Do you have news? Have you found more?")
                label("initialOps")
                options {
                    op("I've found $penguinsSpottedCount ${if (penguinsSpottedCount == 1) "penguin" else "penguins"} this week.") {
                        if (penguinPoints > 0) {
                            npc(npc, CALM_TALK, "Great! You have $penguinPoints ${if (penguinPoints == 1) "point" else "points"} to spend. Perhaps you should think about claiming your reward.")
                            goto("initialOps")
                        } else {
                            if (penguinsSpottedCount > 1) npc(npc, CALM_TALK, "You've found a lot of spies. But, you have no Penguin Points saved up. Keep looking!")
                            else if (penguinsSpottedCount == 1) npc(npc, CALM_TALK, "You've barely found any spies. And, you have no Penguin Points saved up. Keep looking!")
                            else npc(npc, CALM_TALK, "You've not found any spies. And, you have no Penguin Points saved up. Keep looking!")
                            options {
                                op("I'm having trouble finding the penguins, can I have a hint?") {
                                    player(CALM_TALK, "I'm having trouble finding the penguins; can I have a hint?")
                                    goto("getHint")
                                }
                                op("What do I need to do again?") {
                                    player(WORRIED, "What do I need to do again?")
                                    npc(npc, FRUSTRATED, "Weren't you listening the first time? Fine, I'll explain it again, but pay attention this time.")
                                    npc(npc, SECRETIVE, "There are spies everywhere. I'm recruiting brave adventurers to find these penguins and tell me of their locations. Whenever you spot a penguin, spy on it.")
                                    npc(npc, SECRETIVE, "They're well trained and will change their positions every week, so keep your eyes peeled.")
                                    npc(npc, SECRETIVE, "I gave you a spy notebook so that you could track the penguin spies. Penguins are worth between 1 and 2 Penguin Points depending on how difficult they are to find.")
                                    if (player.isQuestComplete(Quest.HUNT_FOR_RED_RAKTUBER))
                                        npc(npc, SECRETIVE, "I also get a feeling that there might be a polar bear agent hiding around the world in a village well! I'll give you 1 Penguin Point, if you manage to find it.")
                                    npc(npc, SECRETIVE, "Report back here and I will reward you for your efforts.")
                                    goto("initialOps")
                                }
                                op("Never mind.") {
                                    player(CALM_TALK, "Never mind.")
                                    npc(npc, FRUSTRATED, "FINE. Be that way.")
                                }
                            }
                        }
                    }
                    opExec("I want to claim my reward.") {
                        LarryHideNSeek(player, npc)
                    }
                    op("I'm having trouble finding the penguins; can I have a hint?") {
                        player(CALM_TALK, "I'm having trouble finding the penguins; can I have a hint?")
                        label("getHint")
                        exec { getHint() }
                    }
                    op("I've lost my spy notebook. Can I have another please?") {
                        player(SAD, "I've lost my spy notebook. Can I have another please?")
                        if (player.inventory.containsOneItem(13732) || player.bank.containsItem(13732)) {
                            npc(npc, SKEPTICAL_HEAD_SHAKE, "Hold on, there. I reckon you might already have one somewhere...")
                        } else {
                            if (player.inventory.hasFreeSlots()) {
                                npc(npc, SECRETIVE, "Of course - take this one.")
                                item(13732, "Larry hands you another spy notebook.") {
                                    player.inventory.addItem(13732)
                                }
                            } else {
                                npc(npc, SECRETIVE, "Of course - but you don't appear to have room for it.")
                                player(CALM_TALK, "I'll be back when I have room to take it.")
                            }
                        }
                    }
                    op("Never mind.") {
                        player(CALM_TALK, "Never mind.")
                        npc(npc, FRUSTRATED, "FINE. Be that way.")
                    }
                }
            }
        }
    }
    fun getHint() {
        val hint = WorldDB.getPenguinHAS().getHintForPenguin(player.username)
        player.startConversation {
            if (hint != null) npc(npc, SECRETIVE, "I've heard there's a penguin $hint")
            else npc(npc, HAPPY_TALKING, "You've found them all this week. Well done!")
        }
    }
}

class LarryHideNSeek(val player: Player, val npc: NPC) {
    init {
        val penguinPoints = player.getI(PENGUIN_POINTS)
        val coinAmount = 6500 * penguinPoints
        player.startConversation {
            player(HAPPY_TALKING, "I want to claim my reward.")
            if (penguinPoints > 0) {
                val message = when (penguinPoints) {
                    1 -> "My, you have been working hard... You have 1 Penguin Point."
                    else -> "My, you have been working hard... You have $penguinPoints Penguin Points."
                }
                npc(npc, AMAZED, message)
                npc(npc, HAPPY_TALKING, "I can either reward you with coins or experience. Which would you prefer?")
                options {
                    op("Show me the money!") {
                        player(HAPPY_TALKING, "Show me the money!")
                        item(6964, "Larry hands you ${Utils.getFormattedNumber(coinAmount.toDouble(), ',')} coins in return for your $penguinPoints Penguin ${if (penguinPoints == 1) "Point" else "Points"}") {
                            player.inventory.addCoins(coinAmount)
                            player.set(PENGUIN_POINTS, 0)
                        }
                    }
                    op("Experience, all the way!") {
                        player(HAPPY_TALKING, "I want the experience reward.")
                        exec { openPenguinHASExpReward(player, player.getI(PENGUIN_POINTS)) }
                    }
                }
            } else {
                npc(npc, SAD, "You have no Penguin Points saved up. Keep looking!")
            }
        }
    }
}

@ServerStartupEvent
fun mapLarryEastArdougne() {
    onNpcClick(5424, options = arrayOf("Talk-to")) { (player, npc) -> Larry(player, npc) }
    onNpcClick(5424, options = arrayOf("Hide-n-Seek")) { (player, npc) ->
        val unlockedPenguinHAS = player.getSavingAttributes().getOrDefault(UNLOCKED_PENGUIN_HAS, false)
        when (unlockedPenguinHAS) {
            false -> Larry(player, npc)
            true -> LarryHideNSeek(player, npc)
        }
    }
}