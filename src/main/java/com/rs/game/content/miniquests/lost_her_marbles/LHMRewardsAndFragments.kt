package com.rs.game.content.miniquests.lost_her_marbles

import com.rs.Launcher
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.content.skills.thieving.PickPocketAction
import com.rs.game.content.skills.thieving.PickPocketableNPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.util.Logger
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onXpDrop
import java.util.*

private var npcID: Int = 11279
private const val Citizen = 18653
private const val HAM = 18654
private const val Rouge = 18655
private const val MasterFarmer = 18656
private const val Farmer = 18657
private const val Guard = 18658
private const val Warrior = 18659
private const val Goblin = 18660

@ServerStartupEvent
fun handleFragmentChecks() {
    onItemClick(
        Citizen,
        HAM,
        Rouge,
        MasterFarmer,
        Farmer,
        Guard,
        Warrior,
        Goblin,
        options = arrayOf("Check")
    ) { e ->
        val itemToGroupMap = mapOf(
            Citizen to "Citizen",
            HAM to "HAM",
            Rouge to "Rogue",
            MasterFarmer to "MasterFarmer",
            Farmer to "Farmer",
            Guard to "Guard",
            Warrior to "Warrior",
            Goblin to "Goblin"
        )
        var group = itemToGroupMap[e.item.id]
        if (group != null) {
            val charges = getGroupFragments(e.player, group)
            if(group == "MasterFarmer")
                group = "Master Farmer";
            if(group == "HAM")
                group = "H.A.M. Member";
            e.player.sendMessage("You have collected $charges/6 fragments from $group's")
        }
        e.player.sendMessage("You have collected a total of ${getTotalFragments(e.player)} out of 32 fragments.")
    }
}

@ServerStartupEvent
fun giveFragments() {
    onXpDrop { e ->
        if(!e.player.isMiniquestStarted(Miniquest.LOST_HER_MARBLES) || e.player.isMiniquestComplete(Miniquest.LOST_HER_MARBLES))
            return@onXpDrop
        if(e.skillId != Skills.THIEVING)
            return@onXpDrop
        val action = e.player.actionManager.action as? PickPocketAction ?: return@onXpDrop
        if (action.npcData == PickPocketableNPC.MAN || action.npcData == PickPocketableNPC.FARMER || action.npcData == PickPocketableNPC.MASTER_FARMER || action.npcData == PickPocketableNPC.CAVE_GOBLIN || action.npcData == PickPocketableNPC.MALE_HAM || action.npcData == PickPocketableNPC.FEMALE_HAM || action.npcData == PickPocketableNPC.HAM_GUARD || action.npcData == PickPocketableNPC.GUARD || action.npcData == PickPocketableNPC.ROGUE || action.npcData == PickPocketableNPC.WARRIOR) {
            val group: String = when (action.npcData) {
                PickPocketableNPC.MAN -> "Citizen"
                PickPocketableNPC.FARMER -> "Farmer"
                PickPocketableNPC.MASTER_FARMER -> "MasterFarmer"
                PickPocketableNPC.CAVE_GOBLIN -> "Goblin"
                PickPocketableNPC.MALE_HAM -> "HAM"
                PickPocketableNPC.FEMALE_HAM -> "HAM"
                PickPocketableNPC.HAM_GUARD -> "HAM"
                PickPocketableNPC.GUARD -> "Guard"
                PickPocketableNPC.ROGUE -> "Rogue"
                PickPocketableNPC.WARRIOR -> "Warrior"
                else -> return@onXpDrop
            }
            val groupToItemMap = mapOf(
                "Citizen" to Citizen,
                "Farmer" to Farmer,
                "MasterFarmer" to MasterFarmer,
                "HAM" to HAM,
                "Warrior" to Warrior,
                "Guard" to Guard,
                "Rouge" to Rouge,
                "Goblin" to Goblin
            )
            val itemId = groupToItemMap[group] ?: return@onXpDrop
            if(!e.player.inventory.containsItem(itemId) && !e.player.inventory.hasFreeSlots())
                return@onXpDrop
            if (getTotalFragments(e.player) >= 32)
                return@onXpDrop
            else if(e.player.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES).getI("LHM_$group") <= 5 || !hasItems(e.player, group))
                if (Utils.random(0, 100) > 33) {
                    e.player.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES).incI("LHM_$group")
                    e.player.inventory.addItem(itemId, 1)
                    e.player.sendMessage("You find a small fragment of solidified elemental fire.")
                }
        }
    }
}

private fun hasItems(player: Player, group: String): Boolean {
    var itemID = 0
    when (group) {
        "Citizen" -> itemID = 18653
        "HAM" -> itemID = 18654
        "Rouge" -> itemID = 18655
        "MasterFarmer" -> itemID = 18656
        "Farmer" -> itemID = 18657
        "Guard" -> itemID = 18658
        "Warrior" -> itemID = 18659
        "Goblin" -> itemID = 18660
    }
    return player.inventory.containsItems(intArrayOf(itemID), intArrayOf(6))
}
fun giveRewards(player: Player) {
    val stage = player.miniquestManager.getStage(Miniquest.LOST_HER_MARBLES)
    if (player.inventory.freeSlots < 2) {
        player.npcDialogue(
            npcID,
            HeadE.SHAKING_HEAD,
            "You don't have enough space in your inventory to claim a reward."
        )
        return
    }

    when (stage) {
        1 -> if (getTotalFragments(player) >= 8) {
            addItemToInventory(player, Rewards.TEAK_LOGS, Rewards.MAPLE_LOGS)
            player.miniquestManager.setStage(Miniquest.LOST_HER_MARBLES, 2)
            player.npcDialogue(
                npcID,
                HeadE.HAPPY_TALKING,
                "Got the word to split a chunk of our takings with you as a bonus."
            )
        }

        2 -> if (getTotalFragments(player) >= 16) {
            addItemToInventory(player, Rewards.COAL_ORE, Rewards.GOLD_ORE)
            player.miniquestManager.setStage(Miniquest.LOST_HER_MARBLES, 3)
            player.npcDialogue(
                npcID,
                HeadE.HAPPY_TALKING,
                "Note's come from the boss to pass on another chunk of our swag."
            )
        }

        3 -> if (getTotalFragments(player) >= 24) {
            addItemToInventory(player, Rewards.RAW_LOBSTER, Rewards.RAW_SWORDFISH)
            player.miniquestManager.setStage(Miniquest.LOST_HER_MARBLES, 4)
            player.npcDialogue(npcID, HeadE.HAPPY_TALKING, "Darren says you've earned yourself another cut.")
        }

        4 -> if (getTotalFragments(player) >= 32) {
            addItemToInventory(player, Rewards.NATURE_RUNE, Rewards.LAW_RUNE)
            player.startConversation {
                npc(
                    npcID,
                    HeadE.HAPPY_TALKING,
                    "I'm to give you your cut and some pointers in advanced pickpocketing, yes?"
                )
                player(HeadE.HAPPY_TALKING, "That's right.")
                npc(
                    npcID,
                    HeadE.CALM_TALK,
                    "Right then. Advanced pickpocketing, or 'How to Pick a Rich Target'. Here's how it's done, " + player.displayName + "..."
                ) {
                    player.miniquestManager.complete(Miniquest.LOST_HER_MARBLES)
                    player.fadeScreen {
                        player.tele(3223, 3269, 0)

                    }
                }
            }
        }
    }
}

private fun addItemToInventory(player: Player, vararg rewards: Rewards) {
    for (reward in rewards) {
        player.inventory.addItem(reward.itemID, Utils.random(reward.minRoll, reward.maxRoll))
    }
}

fun removeItems(player: Player): Boolean {
    val totalItemsToRemove = 8
    val itemIdsToRemove = intArrayOf(Citizen, HAM, Rouge, Farmer, MasterFarmer, Goblin, Guard, Warrior)
    val amountsToRemove = IntArray(itemIdsToRemove.size)
    Arrays.fill(amountsToRemove, 0)
    for (item in player.inventory.items.array()) {
        if (item == null)
            continue
        val itemId = item.id
        for (i in itemIdsToRemove.indices) {
            if (itemIdsToRemove[i] != itemId)
                continue;
            if (itemIdsToRemove[i] == itemId) {
                val remainingToRemove = totalItemsToRemove - amountsToRemove.sum()
                val canRemove = minOf(remainingToRemove, item.amount)
                amountsToRemove[i] += canRemove
                if (amountsToRemove.sum() == totalItemsToRemove) {
                    break
                }
            }
        }
        if (amountsToRemove.sum() == totalItemsToRemove) {
            break
        }
    }
    if (amountsToRemove.sum() != totalItemsToRemove) {
        Logger.info(
            Launcher::class.java,
            "main",
            "Return False"
        )
        return false
    }
    return player.inventory.removeItems(itemIdsToRemove, amountsToRemove)
}

fun countItemsInInventory(player: Player): Int {
    var total = 0
    total = player.inventory.getTotalNumberOf(18653, 18654, 18655, 18656, 18657, 18658, 18659, 18660)
    return total
}

internal enum class Rewards(val itemID: Int, val minRoll: Int, val maxRoll: Int) {
    COAL_ORE(454, 46, 49),
    GOLD_ORE(445, 46, 46),
    TEAK_LOGS(6334, 136, 136),
    MAPLE_LOGS(1518, 255, 255),
    RAW_LOBSTER(378, 37, 37),
    RAW_SWORDFISH(372, 19, 19),
    NATURE_RUNE(561, 43, 55),
    LAW_RUNE(563, 45, 45)
}

fun getGroupFragments(player: Player, group: String): Int {
    val fragments: Int = player.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES).getI("LHM_$group")
    return fragments
}

fun isGroupComplete(player: Player, group: String): Boolean {
    val fragments = getGroupFragments(player, group)
    return fragments >= 6
}

fun getTotalFragments(player: Player): Int {
    val fragmentGroups = arrayOf(
        "LHM_Citizen",
        "LHM_Farmer",
        "LHM_MasterFarmer",
        "LHM_HAM",
        "LHM_Warrior",
        "LHM_Guard",
        "LHM_Rouge",
        "LHM_Goblin"
    )
    var total = 0
    for (group in fragmentGroups) {
        total += player.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES).getI(group)
    }
    return total
}
