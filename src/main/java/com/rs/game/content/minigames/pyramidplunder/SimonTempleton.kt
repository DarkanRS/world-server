// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option.add) any later version.
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
package com.rs.game.content.minigames.pyramidplunder

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.minigames.pyramidplunder.ArtefactTiers.TIER_DIAMOND
import com.rs.game.content.minigames.pyramidplunder.ArtefactTiers.TIER_GOLD
import com.rs.game.content.minigames.pyramidplunder.ArtefactTiers.TIER_JEWELED
import com.rs.game.content.minigames.pyramidplunder.ArtefactTiers.TIER_POTTERY
import com.rs.game.content.minigames.pyramidplunder.ArtefactTiers.TIER_STONE
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnNpc
import com.rs.plugin.kts.onNpcClick

const val SIMON = 3123

typealias ArtefactTier = Int
object ArtefactTiers {
    const val TIER_POTTERY = 0
    const val TIER_STONE = 1
    const val TIER_GOLD = 2
    const val TIER_JEWELED = 3
    const val TIER_DIAMOND = 4
}

data class ArtefactInfo(val tier: ArtefactTier, val value: Int)

private val groupedArtefacts = mapOf(
    setOf(9026, 9027) to ArtefactInfo(TIER_POTTERY, 50),
    setOf(9032, 9033) to ArtefactInfo(TIER_POTTERY, 75),
    setOf(9036, 9037) to ArtefactInfo(TIER_POTTERY, 100),
    setOf(9042, 9043) to ArtefactInfo(TIER_STONE, 150),
    setOf(9030, 9031) to ArtefactInfo(TIER_STONE, 175),
    setOf(9038, 9039) to ArtefactInfo(TIER_STONE, 200),
    setOf(9040, 9041) to ArtefactInfo(TIER_GOLD, 750),
    setOf(9028, 9029) to ArtefactInfo(TIER_GOLD, 1000),
    setOf(9034, 9035) to ArtefactInfo(TIER_GOLD, 1250),
    setOf(20661, 20669) to ArtefactInfo(TIER_JEWELED, 7500)
)

val PP_ARTEFACTS = groupedArtefacts.flatMap { entry ->
    entry.key.map { it to entry.value } }.toMap() + (21570 to ArtefactInfo(TIER_DIAMOND, 12500))

@ServerStartupEvent
fun mapSimon() {
    onNpcClick(SIMON, options = arrayOf("Talk-to")) { (player) ->
        player.startConversation {
            if (player.inventory.containsItem(6970)) {
                player(HeadE.CHEERFUL, "I have a pyramid top I can sell you!")
                npc(SIMON, HeadE.CHEERFUL, "Excellent job mate! Here's your money.") {
                    val totalMoney = player.inventory.getAmountOf(6970) * 10000
                    player.inventory.deleteItem(6970, Int.MAX_VALUE)
                    player.inventory.addCoins(totalMoney)
                }
                return@startConversation
            }
            if (player.hasPPArtefacts()) {
                player(HeadE.CHEERFUL, "I have some interesting artefacts I'd like you to look at.")
                npc(SIMON, HeadE.HAPPY_TALKING, "Bonzer! Let's have a Butcher's mate.")
                npc(SIMON, HeadE.HAPPY_TALKING, "Do you want to flog the lot of 'em?")
                options("Sell all your artefacts?") {
                    opExec("Yes, please.") { player.sellAllArtefacts() }
                    op("Nevermind.")
                }
                return@startConversation
            }
            player(HeadE.CHEERFUL, "Hi, what do you do here?")
            npc(SIMON, HeadE.CHEERFUL, "I'll buy any special artefacts you find here in the desert. If you happen to find any pyramid tops, I'll buy them for 10,000 gold each.")
            player(HeadE.CHEERFUL, "Great, I'll be sure to come back if I find any.")
        }
    }

    onItemOnNpc(SIMON) { (player, item) -> player.sellItem(item) }
}

fun Player.hasPPArtefacts(): Boolean =
    PP_ARTEFACTS.keys.any { inventory.containsItem(it) }

private fun Player.sellAllArtefacts() {
    var totalValue = 0
    PP_ARTEFACTS.keys.filter { inventory.containsItem(it) }
        .forEach { artefactId ->
            val artefact = PP_ARTEFACTS[artefactId]
            val amount = inventory.getAmountOf(artefactId)
            totalValue += artefact?.value?.times(amount) ?: 0
            inventory.deleteItem(artefactId, amount)
        }
    if (totalValue <= 0) return sendMessage("You don't have any artefacts to sell.")
    inventory.addCoins(totalValue)
    sendMessage("All your artefacts have been sold for ${Utils.formatNumber(totalValue)} coins.")
}

private fun Player.sellItem(item: Item) {
    val artefact = PP_ARTEFACTS[item.id] ?: return sendMessage("You can't sell that item.")
    sendOptionsDialogue("Are you sure you'd like to sell your ${item.definitions.name} for ${Utils.formatNumber(artefact.value)}?") {
        opExec("Yes, I'm sure.") {
            if (inventory.containsItem(item)) {
                inventory.deleteItem(item)
                inventory.addCoins(artefact.value * item.amount)
            }
        }
        op("Nevermind.")
    }
}

