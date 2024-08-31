// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.slayer

import com.rs.cache.loaders.NPCDefinitions
import com.rs.engine.dialogue.*
import com.rs.game.World.sendProjectile
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onNpcKillParticipation
import java.util.*

@ServerStartupEvent
fun mapReaperAssignments() {
    onItemClick(24806) { (player, _, option) ->
        when (option) {
            "Activate" -> talkToReaper(player)
            "Kills-left" -> if (player.bossTask != null)
                                player.sendMessage(player.bossTask.message)
                            else
                                player.sendMessage("You do not have a task assigned right now.")
            "Assignment" -> talkAboutAssignment(player)
        }
    }

    onNpcClick(15661) { (player, _, option) ->
        when (option) {
            "Talk-to" -> talkToReaper(player)
            "Get-task" -> talkAboutAssignment(player)
            "Rewards" -> player.sendMessage("Rewards are not implemented at the moment, but you can still gain points. Feel free to post suggestions for rewards in Discord.")
            "Reclaim-items" -> player.sendMessage("Possible future implementation, but not a fan of it.")
        }
    }

    BossTask.BossTasks.entries.forEach {
        onNpcKillParticipation(it.id) { (participant, npc) ->
            val player = participant as? Player ?: return@onNpcKillParticipation
            if (player.hasBossTask()) {
                val taskName: String = player.bossTask.name.lowercase(Locale.getDefault())
                if (npc.definitions.name.equals(taskName, ignoreCase = true)) player.bossTask.sendKill(player, npc)
            }
        }
    }
}

private fun talkAboutAssignment(player: Player) {
    if (player.bossTask == null) giveNewTask(player)
    else player.startConversation(Conversation(player).addNext(getRerollTaskDialogue(player)))
}

private fun giveNewTask(player: Player) {
    if (player.getDailyB("bossTaskCompleted")) {
        player.npcDialogue(15661, HeadE.CALM, "The imbalance has been corrected for today; your task is done. Visit me tomorrow for further instructions.")
        return
    }
    player.bossTask = BossTask.create()
    player.startConversation {
        npc(15661, HeadE.CALM, "I require you to collect " + player.bossTask.amount + " souls from the following battle: " + player.bossTask.name + ". Can I trust you with this task?")
        options {
            op("You certainly can. Thanks!")
            op("I'd like a different assignment.") { appendToCurrent(getRerollTaskDialogue(player)) }
        }
    }
}

private fun getRerollTaskDialogue(player: Player): Dialogue {
    return createDialogueSection {
        if (player.getDailyI("bossTaskRerolls") < 3)
            npc(15661, HeadE.CALM, "Do not think I will allow you to change your mind freely. I will only allow you to change it 3 times per day. ${(if (player.getDailyI("bossTaskRerolls") == 0) "" else "You've already used up " + player.getDailyI("bossTaskRerolls") + " of those.")}")
        options("Are you sure you want to reroll your task?") {
            opExec("Yes, I am sure.") {
                player.incDailyI("bossTaskRerolls")
                giveNewTask(player)
            }

            op("Nevermind, I will just do this task.")
        }
    }
}

private fun talkToReaper(player: Player) {
    player.startConversation {
        if (!player.getBool("learnedDeath")) {
            npc(15661, HeadE.CALM, "Hello mortal. I am in need of some assistance and I have an offer to propose to you. Listen closely.")
            npc(15661, HeadE.CALM, "As you are no doubt aware, I am Death. The Reaper, the Collector of Souls, or any other name civilizations have given me. My task is to retrieve the soul from a dying creature, enabling its passage to the underworld.")
            player(HeadE.CONFUSED, "So what do you need me for?")
            npc(15661, HeadE.CALM, "There is an imbalance in the harmony of life and death. There is far too much... life.")
            npc(15661, HeadE.CALM, ("My eyes have been on you in this age, " + player.displayName + ", as have the eyes of many others. You appear to have the skills I require to bring balance."))
            player(HeadE.CONFUSED, "So you want me to kill stuff?")
            npc(15661, HeadE.CALM, "If you wish to put it so indelicately, yes.")
            player(HeadE.HAPPY_TALKING, "Great! When do I start?")
            npc(15661, HeadE.CALM, "Immediately.")
            exec {
                player["learnedDeath"] = true
                player.inventory.addItemDrop(24806, 1)
                giveNewTask(player)
            }
            return@startConversation
        }

        npc(15661, HeadE.CALM, "What is it, mortal? Time is ticking.")
        options {
            op("I need an assignment.") { talkAboutAssignment(player) }

            op("I'd like another grim gem.") {
                item(24806, "You receive a grim gem.") { player.inventory.addItem(24806, 1) }
            }

            op("Are there any rewards for this?") {
                npc(15661, HeadE.CALM, "Not yet, mortal. I am still thinking about possible reward options. But I will still keep tally of your points regardless.")
            }
        }
    }
}

class BossTask(var task: BossTasks, var amount: Int) {
    enum class BossTasks(val id: Int, val minAmount: Int, val maxAmount: Int, val xp: Int) {
        GENERAL_GRAARDOR(6260, 5, 12, 10000),
        COMMANDER_ZILYANA(6247, 5, 12, 10000),
        KRIL_TSUTAROTH(6203, 5, 12, 10000),
        KREE_ARRA(6222, 5, 12, 10000),
        NEX(13447, 2, 4, 20000),
        KING_BLACK_DRAGON(50, 10, 15, 6000),
        KALPHITE_QUEEN(1158, 10, 12, 7000),
        CORPOREAL_BEAST(8133, 2, 5, 15000),
        DAGANNOTH_REX(2883, 8, 12, 10000),
        DAGANNOTH_PRIME(2882, 8, 12, 10000),
        DAGANNOTH_SUPREME(2881, 8, 12, 10000),
        CHAOS_ELEMENTAL(3200, 8, 12, 10000),
        TORMENTED_DEMON(8349, 8, 12, 10000);


        companion object {
            private val monsters: MutableMap<Int, BossTasks> = HashMap()

            fun forId(id: Int): BossTasks? {
                return monsters[id]
            }

            init {
                for (monster in entries) monsters[monster.id] = monster
            }

            val randomTask: BossTasks
                get() = monsters.values.toTypedArray()[Utils.random(monsters.values.size - 1)]
        }
    }

    val name: String
        get() = NPCDefinitions.getDefs(task.id).name

    private fun finishTask(player: Player) {
        player.incrementCount("Reaper assignments completed")
        player.sendMessage("You have completed your reaper assignment. You are rewarded with " + task.xp + " Slayer experience and 15 Reaper points.")
        player.skills.addXp(Constants.SLAYER, task.xp.toDouble())
        player.reaperPoints += 15
        player.setDailyB("bossTaskCompleted", true)
        player.bossTask = null
    }

    fun sendKill(player: Player, npc: NPC) {
        sendProjectile(npc.tile, player, 3060, Pair(18, 18), 15, 15, 20)
        if (amount >= 1) amount--
        if (amount <= 0) finishTask(player)
        else player.sendMessage("<col=ff0000>As " + npc.name + " dies, you absorb the soul. You now need " + amount + " more souls.")
    }

    val message: String
        get() = "You are currently assigned to collect souls from: $name. You must still retrieve $amount to complete your assignment."

    companion object {
        fun create(): BossTask {
            val info = BossTasks.randomTask
            val task = BossTask(info, Utils.random(info.minAmount, info.maxAmount))
            return task
        }
    }
}