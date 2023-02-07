package com.rs.game.content

import com.rs.game.content.skills.dungeoneering.DungeonRewards.HerbicideSetting
import com.rs.game.content.skills.prayer.Burying
import com.rs.game.content.skills.prayer.Burying.Bone
import com.rs.game.content.skills.summoning.Pouch
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.Item
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.handlers.NPCDropHandler
import java.util.*

@PluginEventHandler
class DropCleaners {
    companion object {
        var bonecrusher = NPCDropHandler(null, Bone.values().filter { bone -> !listOf(Bone.ACCURSED_ASHES, Bone.IMPIOUS_ASHES, Bone.INFERNAL_ASHES).contains(bone) }.map { it.id }.toTypedArray()) { e ->
            if (bonecrush(e.player, e.item))
                e.deleteItem()
        }
        var herbicide = NPCDropHandler(null, HerbicideSetting.values().map { it.herb.herbId }.toTypedArray()) { e ->
            if (herbicide(e.player, e.item)) {
                e.deleteItem()
                return@NPCDropHandler
            }
            if (e.player.familiarPouch === Pouch.MACAW && e.player.familiar.inventory.freeSlot() > 0) {
                e.player.sendMessage("Your macaw picks up the " + e.item.name.lowercase(Locale.getDefault()) + " from the ground.", true)
                e.player.familiar.inventory.add(Item(e.item.id, e.item.amount))
                e.deleteItem()
            }
        }
        var charmingImp = NPCDropHandler(null, arrayOf(12158, 12159, 12160, 12161, 12162, 12163, 12168)) { e ->
            if (e.player.inventory.containsItem(25350, 1) && e.player.inventory.hasRoomFor(e.item)) {
                e.player.inventory.addItem(Item(e.item))
                e.deleteItem()
            }
        }
        var goldAccumulator = NPCDropHandler(null, arrayOf(995)) { e ->
            if (e.player.inventory.containsItem(25351, 1) && e.player.inventory.hasRoomFor(e.item)) {
                e.player.inventory.addCoins(e.item.amount)
                e.deleteItem()
            }
        }

        fun herbicide(player: Player, item: Item): Boolean {
            if (!player.inventory.containsItem(19675, 1)) return false
            val setting = HerbicideSetting.forGrimy(item.id)
            if (setting == null || !player.herbicideSettings.contains(setting)) return false
            if (player.skills.getLevel(Constants.HERBLORE) >= setting.herb.level) {
                player.skills.addXp(Constants.HERBLORE, setting.herb.experience * 2)
                return true
            }
            return false
        }

        fun bonecrush(player: Player, item: Item): Boolean {
            if (!player.inventory.containsItem(18337, 1)) return false
            val bone = Bone.forId(item.id)
            if (bone != null && bone != Bone.ACCURSED_ASHES && bone != Bone.IMPIOUS_ASHES && bone != Bone.INFERNAL_ASHES) {
                player.skills.addXp(Constants.PRAYER, bone.experience)
                Burying.handleNecklaces(player, bone.id)
                return true
            }
            return false
        }
    }
}
