package com.rs.game.content.skills.crafting

import com.rs.engine.dialogue.startConversation
import com.rs.engine.dialogue.statements.MakeXStatement
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.net.decoders.handlers.InventoryOptionsHandler

class Craftables(private val craftable: Craftable, private var quantity: Int) : PlayerAction() {
    private var success = false

    enum class Craftable(val uncraftedItem: Int, val craftedItem: Int, val wasteItem: Int?, val requiredTool: Int, val successExperience: Double, val failExperience: Double?, val levelRequired: Int, val rate1: Int?, val rate99: Int?, val emote: Int) {
        LIMESTONE(3211, 3420, 968, 1755, 6.0, 1.5, 12, 80, 256, 1309)
    }

    private fun checkAll(player: Player): Boolean {
        return when {
            player.skills.getLevel(Constants.CRAFTING) < craftable.levelRequired -> {
                player.simpleDialogue("You need a crafting level of ${craftable.levelRequired} to craft the ${Item(craftable.uncraftedItem).name.lowercase()} into ${Utils.addArticle(Item(craftable.craftedItem).name.lowercase())}.")
                false
            }
            !player.inventory.containsOneItem(craftable.uncraftedItem) -> {
                player.simpleDialogue("You don't have any ${Item(craftable.uncraftedItem).name.lowercase()} to craft.")
                false
            }
            !hasRequiredTool(player) -> {
                player.simpleDialogue("You need ${Utils.addArticle(Item(craftable.requiredTool).name.lowercase())} to craft the ${Item(craftable.uncraftedItem).name.lowercase()}.")
                false
            }
            else -> true
        }
    }

    private fun hasRequiredTool(player: Player): Boolean {
        return player.inventory.containsOneItem(craftable.requiredTool) || player.containsTool(craftable.requiredTool)
    }

    fun rollSuccess(player: Player): Boolean {
        return if (craftable.failExperience != null && craftable.wasteItem != null && craftable.rate1 != null && craftable.rate99 != null) {
            val level = player.skills.getLevel(Constants.CRAFTING)
            val modifiedRate1 = modifyRate(craftable.rate1, level)
            val modifiedRate99 = modifyRate(craftable.rate99, level)
            if (modifiedRate1 != null && modifiedRate99 != null) {
                Utils.skillSuccess(level, modifiedRate1, modifiedRate99)
            } else {
                false
            }
        } else {
            true
        }
    }

    private fun modifyRate(rate: Int?, craftingLevel: Int): Int? {
        return if (rate != null && (craftable == Craftable.LIMESTONE && craftingLevel >= 40)) {
            // Adjust rate to 100% success if player level is 40 or higher for LIMESTONE
            256
        } else {
            rate
        }
    }

    override fun start(player: Player): Boolean {
        return if (checkAll(player)) {
            success = rollSuccess(player)
            setActionDelay(player, 1)
            player.anim(craftable.emote)
            true
        } else {
            false
        }
    }

    override fun process(player: Player): Boolean = checkAll(player)

    override fun processWithDelay(player: Player): Int {
        val success = rollSuccess(player)

        with(player.inventory) {
            deleteItem(craftable.uncraftedItem, 1)
            if (craftable.failExperience != null && craftable.wasteItem != null && !success) {
                addItem(craftable.wasteItem, 1)
                player.skills.addXp(Constants.CRAFTING, craftable.failExperience)
                player.sendMessage("You fail to craft the ${Item(craftable.uncraftedItem).name.lowercase()}.", true)
            } else {
                addItem(craftable.craftedItem, 1)
                player.skills.addXp(Constants.CRAFTING, craftable.successExperience)
                player.sendMessage("You successfully craft the ${Item(craftable.uncraftedItem).name.lowercase()} into ${Utils.addArticle(Item(craftable.craftedItem).name.lowercase())}.", true)
            }
        }

        quantity--
        return if (quantity <= 0) -1 else {
            player.anim(craftable.emote)
            Animation(craftable.emote).defs.emoteGameTicks
        }
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }

    companion object {

        @JvmStatic
        fun isCrafting(player: Player, item1: Item?, item2: Item?): Boolean {
            val craftableToolId = Craftable.entries.firstOrNull()?.requiredTool ?: return false
            val craftable = InventoryOptionsHandler.contains(craftableToolId, item1, item2) ?: return false
            return isCrafting(player, craftable.id)
        }


        private fun isCrafting(player: Player, craftableId: Int): Boolean {
            val craftable = Craftable.entries.find { it.uncraftedItem == craftableId }
            return if (craftable != null) {
                craft(player, craftable)
                true
            } else {
                false
            }
        }

        fun craft(player: Player, craftable: Craftable) {
            if (player.inventory.items.getNumberOf(Item(craftable.uncraftedItem, 1)) <= 1) {
                player.actionManager.setAction(Craftables(craftable, 1))
            } else {
                player.startConversation {
                    makeX(MakeXStatement.MakeXType.CRAFT,
                        "Choose how many you wish to craft,<br>then click on the item to begin.",
                        intArrayOf(craftable.uncraftedItem),
                        player.inventory.getAmountOf(craftable.uncraftedItem))
                    exec { player.actionManager.setAction(Craftables(craftable, MakeXStatement.getQuantity(player))) }
                }
            }
        }
    }
}
