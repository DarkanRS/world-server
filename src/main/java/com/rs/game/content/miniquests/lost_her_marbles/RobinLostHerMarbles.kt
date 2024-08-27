package com.rs.game.content.miniquests.lost_her_marbles

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class RobinLostHerMarbles(p: Player, npc: NPC) {
    init {

        if (p.getMiniquestStage(Miniquest.LOST_HER_MARBLES) == p.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES)
                .getI("rewardClaimed")
        ) {
            giveRewards(p)
        } else if (countItemsInInventory(p) >= 8) {
            p.startConversation {
                npc(npc, CALM_TALK, "You have a full set of fragments! Well done, you should take these to Darren.")
                return@startConversation
            }
        } else {
            p.startConversation {
                player(
                    CALM_TALK,
                    "The Guildmaster mentioned that you have an idea who might have found these flame fragments..."
                )
                npc(
                    npc,
                    CALM_TALK,
                    "Yes, I have some idea. There was a shower of them around Lumbridge, so any man or woman may have stumbled across them. For that matter, those H.A.M. maniacs to the west might have one or two or six stashed away."
                )
                player(CALM_TALK, "Anyone else?")
                npc(
                    npc,
                    CALM_TALK,
                    "I'd be surprised if none had landed in a field; you might check the various farms hereabouts in case the farmers made a lucky find."
                )
                npc(
                    npc,
                    CALM_TALK,
                    "I suspect if any guards have found one lying around they won't have told anyone - I wouldn't either, on a guardsman's wage - and the same over the river in Al Kharid."
                )
                npc(
                    npc,
                    CALM_TALK,
                    "Oh, and there's a group of rogues in a castle deep in the Wilderness that might have 'acquired' some, but its a dangerous place."
                )
                npc(
                    npc,
                    CALM_TALK,
                    "Failing that, you could check in on the Dorgeshuun, they are always picking up strange rocks."
                ) //<- Creative liberty here, goblins aren't originally mentioned in the quest but do have a set.
                options {
                    op("How am I doing so far?") {
                        npc(npc, CALM_TALK, getMessageForPlayer(p))
                    }
                    op("I'll see what a few pockets can turn up, then.") {
                        player(CALM_TALK, "I'll see what a few pockets can turn up, then.")
                    }
                }
            }
        }
    }

    private fun getMessageForPlayer(player: Player): String {
        val message = StringBuilder("You'll still find fragments on")
        val fragmentGroups = arrayOf(
            "Citizen",
            "Farmer",
            "MasterFarmer",
            "HAM",
            "Warrior",
            "Guard",
            "Rouge",
            "Goblin"
        )
        val incompleteGroups = mutableListOf<String>()
        for (group in fragmentGroups) {
            if (!isGroupComplete(player, group)) {
                val groupName = when (group) {
                    "Citizen" -> "Citizens"
                    "Farmer" -> "Farmers"
                    "MasterFarmer" -> "Master Farmers"
                    "HAM" -> "H.A.M. Members"
                    "Warrior" -> "Warriors"
                    "Guard" -> "Guards"
                    "Rouge" -> "Rogues"
                    "Goblin" -> "Cave Goblins"
                    else -> ""
                }
                incompleteGroups.add(groupName)
            }
        }
        if (incompleteGroups.isNotEmpty()) {
            message.append(" ").append(incompleteGroups[0])
            for (i in 1 until incompleteGroups.size) {
                if (i == incompleteGroups.size - 1) {
                    message.append(", and ")
                } else {
                    message.append(", ")
                }
                message.append(incompleteGroups[i])
            }
            message.append(". Best of luck!")
        } else {
            message.setLength(0)
        }
        return message.toString()
    }
}
