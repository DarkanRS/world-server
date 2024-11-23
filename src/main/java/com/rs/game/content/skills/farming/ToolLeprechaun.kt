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
package com.rs.game.content.skills.farming

import com.rs.engine.dialogue.*
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.net.ClientPacket
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.ButtonClickEvent
import com.rs.plugin.events.ItemOnNPCEvent
import com.rs.plugin.events.NPCClickEvent
import com.rs.plugin.handlers.ButtonClickHandler
import com.rs.plugin.handlers.ItemOnNPCHandler
import com.rs.plugin.handlers.NPCClickHandler
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onItemOnNpc
import com.rs.plugin.kts.onNpcClick
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

@ServerStartupEvent
fun mapToolLeprechauns() {
    //inventory inter 74 = jadinko related storage
    fun openToolStorage(player: Player) {
        for (i in StorableItem.entries) i.updateVars(player)
        player.interfaceManager.sendInterface(125)
        player.interfaceManager.sendInventoryInterface(126)
    }

    fun storeTool(player: Player, itemId: Int, amount: Int) {
        val item = StorableItem.forId(itemId) ?: return
        player.storeLeprechaunItem(item, itemId, amount)
    }

    fun takeTool(player: Player, itemId: Int, amount: Int) {
        val item = StorableItem.forId(itemId) ?: return
        player.takeLeprechaunItem(item, amount)
    }

    onNpcClick("Teclyn") { e ->
        when (e.option) {
            "Exchange" -> openToolStorage(e.player)
            "Talk-to" -> e.player.startConversation {
                npc(e.npcId, HeadE.CHEERFUL, "Gosh, it's a nice day! Now, can I help you with tool storage, or a trip to Winkin's Farm, or something else?")
                options {
                    op("Tool storage.") {
                        npc(e.npcId, HeadE.CHEERFUL, "I can hold onto your rake, seed dibber, spade, secateurs, watering can and trowel - but not those trowels used for archaeology.")
                        npc(e.npcId, HeadE.CHEERFUL, "I can also store a few buckets and scarecrows for you, and even compost and supercompost. Of course, compost is essential for good Farming, so I can store lots of that for you.")
                        npc(e.npcId, HeadE.CHEERFUL, "Also, if you hand me your Farming produce, I might be able to change it into banknotes.")
                        npc(e.npcId, HeadE.CHEERFUL, "So, would you like me to look after your Farming tools?")
                        options {
                            opExec("Yes, please.") { openToolStorage(e.player) }
                            op("What do you do with the tools you're storing?") {
                                player(HeadE.CONFUSED, "What do you do with the tools you're storing? They can't all possibly fit in your pockets!")
                                npc(e.npcId, HeadE.HAPPY_TALKING, "Of course not. I have a toolshed. It is rather a curious shed, though. Sometimes, items just seem to appear in there.")
                            }
                            op("No thanks, I'll keep hold of my stuff.") {
                                player(HeadE.CHEERFUL,"No thanks, I'll keep hold of my stuff.")
                                npc(e.npcId, HeadE.CHEERFUL, "Well, I shall be here if you ever decide that I may be of service.")
                            }
                        }
                    }
                    op("Winkin's farm.") {
                        npc(e.npcId, HeadE.UPSET, "I'm sorry ${e.player.genderTerm("sir", "ma'am")}, I've been instructed that I'm not allowed to do that yet!")
                    }
                    op("Other topics.") {
                        options {
                            op("Aren't you a bit tall for a leprechaun?") {
                                player(HeadE.CONFUSED, "Aren't you a bit tall for a leprechaun?")
                                npc(e.npcId, HeadE.FRUSTRATED, "I'm not a leprechaun! Why would you ask such a strange question?")
                                player(HeadE.HAPPY_TALKING, "Well, you're looking after tools like leprechauns do.")
                                npc(e.npcId, HeadE.HAPPY_TALKING, "I'm of the Crwys clan. We have a special affinity with nature. It is my duty and pleasure to tend this farming patch.")
                            }
                            op("Actually, I'm fine.")
                        }
                    }
                }
            }
            "Teleport" -> e.player.sendMessage("Vinesweeper is not available yet.")
        }
    }
    onNpcClick("Tool leprechaun", "Tool Leprechaun") { e ->
        when (e.option) {
            "Exchange", "Exchange-tools", "Exchange-potions" -> openToolStorage(e.player)
            "Talk-to" -> e.player.startConversation {
                npc(e.npcId, HeadE.CHEERFUL, "Ah, 'tis a foine day, to be sure! Can I help ye with tool storage, or a trip to Winkin's Farm, or what?")
                options {
                    op("Tool storage.") {
                        npc(e.npcId, HeadE.CHEERFUL, "We'll hold onto yer rake, seed dibber, spade, secateurs, waterin' can and trowel - but mind it's not one of them fancy trowels only archaeologists use.")
                        npc(e.npcId, HeadE.CHEERFUL, "We'll take a few buckets an' scarecrows off yer hands too, and even yer compost and supercompost. There's room in our shed for plenty of compost, so bring it on.")
                        npc(e.npcId, HeadE.CHEERFUL, "Also, if ye hands us yer Farming produce, we might be able to change it into banknotes.")
                        npc(e.npcId, HeadE.CONFUSED, "So, do ye want to be using the store?")
                        options {
                            opExec("Yes, please.") { openToolStorage(e.player) }
                            op("Nevermind.")
                        }
                    }
                    op("Winkin's farm.") {
                        npc(e.npcId, HeadE.UPSET, "I'm sorry ${e.player.genderTerm("lad", "lass")}, I've been instructed that I'm not allowed to do that yet!")
                    }
                }
            }
            "Teleport" -> e.player.sendMessage("Vinesweeper is not available yet.")
        }
    }

    val noteableHerbs = arrayOf(249, 251, 253, 255, 257, 259, 261, 263, 265, 267, 269, 2481, 2998, 3000, 12172, 14854, 21624)

    onItemOnNpc("Tool leprechaun", "Tool Leprechaun", "Teclyn") { e ->
        val itemId = e.item.id
        // Duplicate npcid hack for miscellania cabbage noting
        if (e.npc.respawnTile.isAt(3056, 3310) && e.item.id in listOf(1965, 1967)) {
            e.player.startConversation {
                npc(e.npc.id, HeadE.SAD_MILD_LOOK_DOWN,"A cabbage? Don't ye go showing me yer cabbage; this whole farm stinks of the things! Take it away and get it exchanged somewhere else.")
            }
            return@onItemOnNpc
        }
        val produceType = ProduceType.forProduce(itemId)
        if ((produceType == null || e.item.definitions.getCertId() == -1) && !noteableHerbs.contains(itemId)) {
            e.player.npcDialogue(e.npc.id, HeadE.SAD_MILD_LOOK_DOWN,
                when (e.npc.id) {
                    2861 -> "Sorry, I can't turn that into a banknote."
                    else -> "Nay, I've got no banknotes to exchange for that item."
                })
            return@onItemOnNpc
        }
        val num = e.player.inventory.getNumberOf(itemId)
        e.player.inventory.deleteItem(itemId, num)
        e.player.inventory.addItem(Item(e.item.definitions.getCertId(), num))
        e.player.sendMessage(
            when (e.npc.id) {
                2861 -> "The elf exchanges your items for banknotes."
                else -> "The leprechaun exchanges your items for banknotes."
            }
        )
    }

    onButtonClick(125) { e ->
        when (e.packet) {
            ClientPacket.IF_OP1 -> takeTool(e.player, e.slotId2, 1)
            ClientPacket.IF_OP2 -> takeTool(e.player, e.slotId2, 5)
            ClientPacket.IF_OP3 -> takeTool(e.player, e.slotId2, Int.MAX_VALUE)
            ClientPacket.IF_OP4 -> e.player.sendInputInteger("How many would you like to take?") { num: Int -> takeTool(e.player, e.slotId2, num) }
            else -> {}
        }
    }

    onButtonClick(126) { e ->
        when (e.packet) {
            ClientPacket.IF_OP1 -> storeTool(e.player, e.slotId2, 1)
            ClientPacket.IF_OP2 -> storeTool(e.player, e.slotId2, 5)
            ClientPacket.IF_OP3 -> storeTool(e.player, e.slotId2, Int.MAX_VALUE)
            ClientPacket.IF_OP4 -> e.player.sendInputInteger("How many would you like to store?") { num: Int -> storeTool(e.player, e.slotId2, num) }
            else -> {}
        }
    }
}

fun Player.getNumInLeprechaun(item: StorableItem?) = getLeprechaunStorage()[item]?.amount ?: 0

private fun Player.storeLeprechaunItem(item: StorableItem, itemId: Int, amount: Int) {
    @Suppress("NAME_SHADOWING")
    var amount = amount
    val curr = leprechaunStorage[item]
    val maxStoreAmount = if (curr == null) minOf(item.maxAmount, inventory.getNumberOf(itemId))
    else minOf(item.maxAmount - curr.amount, inventory.getNumberOf(itemId))

    amount = minOf(amount, maxStoreAmount).coerceAtLeast(0)

    if (amount > 0) {
        if (curr == null)
            leprechaunStorage[item] = Item(itemId, amount)
        else
            curr.setAmount(curr.amount + amount)
        inventory.deleteItem(itemId, amount)
        item.updateVars(this)
    }
}

private fun Player.takeLeprechaunItem(item: StorableItem, amount: Int) {
    @Suppress("NAME_SHADOWING")
    var amount = amount
    val curr = leprechaunStorage[item] ?: return
    amount = minOf(amount, curr.amount, inventory.freeSlots).coerceAtLeast(0)

    if (amount > 0) {
        curr.setAmount(curr.amount - amount)
        inventory.addItem(curr.id, amount)
        if (curr.amount == 0) leprechaunStorage.remove(item)
        item.updateVars(this)
    }
}
