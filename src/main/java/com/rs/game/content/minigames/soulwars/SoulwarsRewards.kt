package com.rs.game.content.minigames.soulwars

import com.rs.cache.loaders.ItemDefinitions
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.ItemConstants
import com.rs.game.content.skills.summoning.Summoning
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.net.ClientPacket
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onItemOnNpc
import com.rs.plugin.kts.onNpcClick
import com.rs.tools.old.CharmDrop
import com.rs.utils.DropSets
import com.rs.utils.drop.DropTable
import kotlin.math.floor

enum class Imbue(val itemId: Int, val imbuedItemId: Int, val zealCost: Int) {
    GOLD_RING(1635, 15009, 2),
    SAPPHIRE_RING(1637, 15010, 5),
    EMERALD_RING(1639, 15011, 6),
    RUBY_RING(1641, 15012, 10),
    DIAMOND_RING(1643, 15013, 15),
    DRAGONSTONE_RING(1645, 15014, 20),
    LUNAR_RING(9104, 15015, 8),
    RING_OF_CHAROS(6465, 15016, 4),
    ONYX_RING(6575, 15017, 25),
    SEERS_RING(6731, 15018, 25),
    ARCHERS_RING(6733, 15019, 25),
    WARRIOR_RING(6735, 15020, 25),
    BERSERKER_RING(6737, 15220, 25)
}

val COMP_TO_PET = mapOf(
    6 to Triple(7975, 14652, 5),
    14 to Triple(7976, 14653, 25),
    10 to Triple(7977, 14654, 40),
    12 to Triple(7978, 14655, 70),
    16 to Triple(7979, 14651, 85),
    81 to Triple(6570, 21512, 100)
)
val COMP_TO_CHARM = mapOf(
    27 to Pair(ItemConstants.CHARM_IDS[0], 4),
    26 to Pair(ItemConstants.CHARM_IDS[1], 5),
    25 to Pair(ItemConstants.CHARM_IDS[2], 12),
    24 to Pair(ItemConstants.CHARM_IDS[3], 30)
)
val COMP_TO_SKILL = mapOf(
    37 to Skills.ATTACK,
    35 to Skills.STRENGTH,
    34 to Skills.DEFENSE,
    33 to Skills.HITPOINTS,
    36 to Skills.RANGE,
    39 to Skills.MAGIC,
    38 to Skills.PRAYER,
    32 to Skills.SLAYER
)

@ServerStartupEvent
fun mapRewardsPlugins() {
    onNpcClick(8527, 8525, options = arrayOf("Rewards")) { (player) ->
        player.interfaceManager.sendInterface(276)
        player.vars.setVarBit(5827, player.soulWarsZeal)
    }

    onButtonClick(276) { (player, _, componentId, _, _, packet) ->
        when(packet) {
            ClientPacket.IF_OP1 -> {
                when (componentId) {
                    32, 33, 34, 35, 36, 37, 38, 39 -> COMP_TO_SKILL[componentId]?.let { player.sendMessage("You will receive ${Utils.formatNumber(getXpPerZeal(player.skills.getLevelForXp(it), it))} ${Skills.SKILL_NAME[it]} experience per Zeal exchanged.") }
                    24, 25, 26, 27 -> COMP_TO_CHARM[componentId]?.let { (charmId, cost) ->
                        player.sendMessage("You need $cost Zeal to buy ${getCharmAmount(player, charmId)} ${ItemDefinitions.getDefs(charmId).name.lowercase()}s.") }
                    6, 14, 10, 12, 16, 81 -> COMP_TO_PET[componentId]?.let { (item, petId, cost) ->
                        player.sendMessage("You need a ${ItemDefinitions.getDefs(item).name.lowercase()} and $cost Zeal to buy a ${ItemDefinitions.getDefs(petId).name}.") }
                    8 -> player.sendMessage("You need 2 Zeal to gamble.")
                    else -> player.sendMessage("soulrewardcomp: $componentId - $packet")
                }
                return@onButtonClick
            }
            ClientPacket.IF_OP2, ClientPacket.IF_OP3, ClientPacket.IF_OP4 -> {
                when (componentId) {
                    32, 33, 34, 35, 36, 37, 38, 39 -> COMP_TO_SKILL[componentId]?.let { buyXp(player, it, packet) }
                    24, 25, 26, 27 -> COMP_TO_CHARM[componentId]?.let { (charmId, cost) ->
                        if (player.soulWarsZeal < cost) {
                            player.sendMessage("You need $cost Zeal to buy ${getCharmAmount(player, charmId)} ${ItemDefinitions.getDefs(charmId).name.lowercase()}s.")
                            return@onButtonClick
                        }
                        player.soulWarsZeal -= cost
                        player.inventory.addItemDrop(charmId, getCharmAmount(player, charmId))
                    }
                    6, 14, 10, 12, 16, 81 -> {
                        COMP_TO_PET[componentId]?.let { (item, petId, cost) ->
                            if (player.soulWarsZeal < cost || !player.inventory.containsItem(item)) {
                                player.sendMessage("You need a ${ItemDefinitions.getDefs(item).name.lowercase()} and $cost Zeal to buy a ${ItemDefinitions.getDefs(petId).name}.")
                                return@onButtonClick
                            }
                            player.soulWarsZeal -= cost
                            player.inventory.deleteItem(item, 1)
                            player.inventory.addItemDrop(petId, 1)
                        }
                    }
                    8 -> {
                        if (player.soulWarsZeal < 2) {
                            player.sendMessage("You need 2 Zeal to gamble.")
                            return@onButtonClick
                        }
                        player.soulWarsZeal -= 2;
                        DropTable.calculateDrops(player, DropSets.getDropSet("sw_gamble")).forEach { player.inventory.addItemDrop(it) }
                    }

                    else -> player.sendMessage("soulrewardcomp: $componentId - $packet")
                }
            }
            else -> {}
        }
        player.vars.setVarBit(5827, player.soulWarsZeal)
    }

    onItemOnNpc(8527, 8525) { e ->
        e.apply {
            if (!Imbue.entries.any { e.item.id == it.itemId || e.item.id == it.imbuedItemId }) {
                player.startConversation { npc(npc.id, HeadE.CALM_TALK, "I can't imbue that. Bring me a ring to imbue. I can also un-imbue a ring for you if you bring me an imbued ring.") }
                return@onItemOnNpc
            }
            val imbue = Imbue.entries.first { e.item.id == it.itemId || e.item.id == it.imbuedItemId }
            if (imbue.imbuedItemId == e.item.id) {
                player.startConversation {
                    npc(npc.id, HeadE.CALM_TALK, "Are you sure you'd like me to remove your enchantment? You won't be refunded any Zeal for this process.")
                    options {
                        option("Yes, please restore my ring to its original state.") {
                            exec {
                                item.id = imbue.itemId
                                player.inventory.refresh(item.slot)
                            }
                        }
                        option("Nevermind.")
                    }
                }
            }
            if (imbue.itemId == e.item.id) {
                player.startConversation {
                    npc(npc.id, HeadE.CALM_TALK, "I can imbue that ring for you for " + imbue.zealCost + " Zeal. Is that alright with you?")
                    options {
                        option("Yes, please imbue my ring.") {
                            if (player.soulWarsZeal >= imbue.zealCost)
                                exec {
                                    if (player.soulWarsZeal >= imbue.zealCost) {
                                        player.soulWarsZeal -= imbue.zealCost
                                        item.id = imbue.imbuedItemId
                                        player.inventory.refresh(item.slot)
                                    }
                                }
                            else
                                npc(npc.id, HeadE.CALM_TALK, "You don't have enough Zeal.")
                        }
                        option("Nevermind.")
                    }
                }
            }
        }
    }
}

fun buyXp(player: Player, skillId: Int, packet: ClientPacket) {
    val zealCost = when(packet) {
        ClientPacket.IF_OP3 -> 10
        ClientPacket.IF_OP4 -> 100
        else -> 1
    }
    if (player.soulWarsZeal < zealCost) {
        player.sendMessage("You don't have enough Zeal.")
        return
    }
    player.soulWarsZeal -= zealCost
    player.skills.addXp(skillId, getXpPerZeal(player.skills.getLevelForXp(skillId), skillId).toDouble() * zealCost)
}

fun getXpPerZeal(level: Int, skillId: Int): Int {
    return when(skillId) {
        Skills.ATTACK, Skills.STRENGTH, Skills.DEFENSE, Skills.HITPOINTS -> floor((level*level) / 600.0).toInt() * 525
        Skills.RANGE, Skills.MAGIC -> floor((level*level) / 600.0).toInt() * 480
        Skills.PRAYER -> floor((level*level) / 600.0).toInt() * 270
        Skills.SLAYER -> floor((level*level) / 349.0).toInt() * 45
        else -> 1
    }
}

fun getCharmAmount(player: Player, charmId: Int): Int {
    return when(charmId) {
        ItemConstants.CHARM_IDS[0] -> linearInterp(3.0, 6.0, 138.0, 52.0, player.skills.combatLevelWithSummoning.toDouble())
        ItemConstants.CHARM_IDS[1], ItemConstants.CHARM_IDS[2] -> linearInterp(3.0, 4.0, 138.0, 34.0, player.skills.combatLevelWithSummoning.toDouble())
        ItemConstants.CHARM_IDS[3] -> linearInterp(3.0, 5.0, 138.0, 45.0, player.skills.combatLevelWithSummoning.toDouble())
        else -> 1
    }.toInt()
}

fun linearInterp(x1: Double, y1: Double, x2: Double, y2: Double, x: Double): Double {
    if (x1 == x2)
        throw IllegalArgumentException("x1 and x2 cannot be the same value")
    return ((y2 - y1) / (x2 - x1)) * (x - x1) + y1
}