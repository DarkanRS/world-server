package com.rs.game.content.minigames.pyramidplunder

import com.rs.cache.loaders.ItemDefinitions
import com.rs.engine.dialogue.*
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnNpc
import com.rs.plugin.kts.onNpcClick
import kotlin.math.min

private const val MUMMY = 4476
private val PHARAOHS_SCEPTRE = intArrayOf(9050, 9048, 9046, 9044)

@ServerStartupEvent
fun mapMummyOps() {
    onItemOnNpc(MUMMY) { (player, item) ->
        if (item.name.contains("Pharaoh's sceptre")) rechargeDialogue(player)
    }

    onNpcClick(MUMMY) { (player, _, option) ->
        when(option) {
            "Start-minigame" -> player.controllerManager.startController(PyramidPlunderController())
            "Talk-to" -> player.startConversation {
                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "*sigh* Not another one.")
                player(HeadE.CALM_TALK, "Another what?")
                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "Another 'archaeologist'. I'm not going to let you plunder my master's tomb you know.")
                player(HeadE.HAPPY_TALKING, "That's a shame. Have you got anything else I could do while I'm here?")
                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "If it will keep you out of mischief I suppose I could set something up for you... I have a few rooms full of some things you humans might consider valuable, do you want to give it a go?")
                options {
                    op("Play Pyramid Plunder?") {
                        options {
                            op("That sounds like fun; what do I do?") {
                                player(HeadE.HAPPY_TALKING, "That sounds like fun; what do I do?")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "You have five minutes to explore the treasure rooms and collect as many artefacts as you can. The artefacts are in the urns, chests and sarcophagi found in each room.")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "There are eight treasure rooms, each subsequent room requires higher thieving skills to both enter the room and thieve from the urns and other containers")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "The rewards also become more lucrative the further into the tomb you go. You will also have to deactivate a trap in order to enter the main part of each room. ")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "When you want to move onto the next room you need to find the correct door first. There are four possible exits... you must open the door before finding out whether it is the exit or not.")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "Opening the doors require picking their locks. Having a lockpick will make this easier.")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "Do you want to do it?")
                                options {
                                    opExec("I am ready to give it a go now.") { player.controllerManager.startController(PyramidPlunderController()) }
                                    op("Not right now.") {
                                        player(HeadE.HAPPY_TALKING, "Not right now.")
                                        npc(MUMMY, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
                                    }
                                }
                            }

                            opExec("I know what I'm doing, so let's get on with it.") { player.controllerManager.startController(PyramidPlunderController()) }

                            op("Not right now.") {
                                player(HeadE.HAPPY_TALKING, "Not right now.")
                                npc(MUMMY, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
                            }
                        }
                    }
                    op("Not right now.") {
                        player(HeadE.HAPPY_TALKING, "Not right now.")
                        npc(MUMMY, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
                    }
                    if (player.hasSceptre())
                        opExec("I want to charge my sceptre") { rechargeDialogue(player) }
                    if (player.hasPPArtefacts())
                        opExec("I want to note my artefacts") { noteAllArtefacts(player) }
                }
            }
        }
    }
}

fun rechargeDialogue(player: Player) {
    player.startConversation {
        if (player.inventory.containsOneItem(PHARAOHS_SCEPTRE[3], PHARAOHS_SCEPTRE[2], PHARAOHS_SCEPTRE[1])) {
            npc(MUMMY, HeadE.CHILD_FRUSTRATED, "I'm not wasting the King's magic on a charged sceptre")
            return@startConversation
        }
        player(HeadE.CALM_TALK, "I want to charge my sceptre.")
        npc(MUMMY, HeadE.CHILD_ANGRY_HEADSHAKE, "You shouldn't have that thing in the first place, thief!")
        player(HeadE.SKEPTICAL, "Hmm... If I give you back some of the artefacts I've taken from the tomb, will you recharge the sceptre for me?")
        npc(MUMMY, HeadE.CHILD_ANGRY, "*sigh* Oh alright, but this is such a waste of the King's magic...")
        options("What artefact would you like to use?") {
            op("Pottery artefacts?") {
                player(HeadE.CALM, "I'd like to use some pottery artefacts.")
                exec { rechargeWithType(player, ArtefactTiers.TIER_POTTERY) }
            }
            op("Stone artefacts?") {
                player(HeadE.CALM, "I'd like to use some stone artefacts.")
                exec { rechargeWithType(player, ArtefactTiers.TIER_STONE) }
            }
            op("Gold artefacts?") {
                player(HeadE.CALM, "I'd like to use some gold artefacts.")
                exec { rechargeWithType(player, ArtefactTiers.TIER_GOLD) }
            }
            op("Jewelled artefacts?") {
                player(HeadE.CALM, "I'd like to use some jewelled artefacts.")
                exec { rechargeWithType(player, ArtefactTiers.TIER_JEWELED) }
            }
        }
    }
}

private fun noteAllArtefacts(player: Player) {
    PP_ARTEFACTS.keys
        .filter { itemId -> !ItemDefinitions.getDefs(itemId).noted }
        .forEach { itemId ->
            val defs = ItemDefinitions.getDefs(itemId)
            if (!defs.noted && defs.certId != -1 && player.inventory.containsItem(itemId)) {
                val amount = player.inventory.getAmountOf(itemId)
                if (amount > 0) {
                    player.inventory.deleteItem(itemId, amount)
                    player.inventory.addItemDrop(defs.certId, amount)
                }
            }
        }
}

private fun Player.hasSceptre(): Boolean = PHARAOHS_SCEPTRE.any { inventory.containsItem(it) }

private fun rechargeWithType(player: Player, type: ArtefactTier) {
    var numRequired = when(type) {
        ArtefactTiers.TIER_POTTERY -> 24
        ArtefactTiers.TIER_STONE -> 12
        ArtefactTiers.TIER_GOLD -> 6
        ArtefactTiers.TIER_JEWELED -> 1
        else -> return
    }

    val heldArtefactCounts = PP_ARTEFACTS.entries
        .filter { it.value.tier == type }
        .map { it.key to player.inventory.getAmountOf(it.key) }
        .filter { it.second > 0 }

    if (heldArtefactCounts.sumOf { it.second } < numRequired) {
        player.sendMessage("You do not have enough artefacts of the required type to recharge your sceptre.")
        return
    }

    for ((artefactId, count) in heldArtefactCounts) {
        if (numRequired <= 0) break
        val removeCount = min(count, numRequired)
        player.inventory.deleteItem(artefactId, removeCount)
        numRequired -= removeCount
    }

    player.inventory.removeItems(Item(PHARAOHS_SCEPTRE[0]), Item(PHARAOHS_SCEPTRE[1]), Item(PHARAOHS_SCEPTRE[2]))
    player.inventory.addItem(Item(PHARAOHS_SCEPTRE[3]))
    player.sendMessage("Your sceptre has been fully recharged.")
}