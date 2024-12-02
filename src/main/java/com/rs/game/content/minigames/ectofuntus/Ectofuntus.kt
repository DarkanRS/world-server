package com.rs.game.content.minigames.ectofuntus

import com.rs.cache.loaders.ItemDefinitions
import com.rs.cache.loaders.ObjectDefinitions
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.skills.agility.Agility
import com.rs.game.content.skills.magic.Magic
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.content.skills.prayer.Burying
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onObjectClick

class Ectofuntus {

    companion object {
        const val EMPTY_POT = 1931
        const val EMPTY_BUCKET = 1925
        const val ECTO_TOKEN = 4278
        const val BUCKET_OF_SLIME = 4286
        const val FULL_ECTOPHIAL = 4251
        const val EMPTY_ECTOPHIAL = 4252
        const val ECTOFUNTUS = 5282

        @JvmStatic
        fun handleItemOnObject(player: Player, itemId: Int, objectId: Int): Boolean {
            val objectDefs = ObjectDefinitions.getDefs(objectId)
            val itemDefs = ItemDefinitions.getDefs(itemId)
            if (itemId == EMPTY_BUCKET && objectDefs.name == "Pool of Slime") {
                player.actionManager.setAction(SlimeBucketFill())
                return true
            }
            if (itemDefs.name.lowercase().contains("bone") && objectId == 11162) {
                player.actionManager.setAction(EctoBones(itemId))
                return true
            }
            return false
        }
    }

}

@ServerStartupEvent
fun mapEctofuntusInteractions() {

    onObjectClick(5268) { (player) -> player.useLadder(Tile.of(3669, 9888, 3)) } // Entrance to Ectofuntus Pool
    onObjectClick(5264) { (player) -> player.useLadder(Tile.of(3654, 3519, 0)) } // Exit from Ectofuntus Pool

    onObjectClick(9307, 9308) { (player, obj) ->  // Ectofuntus Pool Shortcuts
        when (obj.id) {
            9307 -> {
                if (!Agility.hasLevel(player, 53)) return@onObjectClick
                player.useLadder(Tile.of(3670, 9888, 3))
            }

            9308 -> {
                if (!Agility.hasLevel(player, 53)) return@onObjectClick
                player.useLadder(Tile.of(3671, 9888, 2))
            }
        }
    }

    onObjectClick(5262, 5263) { (player, obj) ->  // Ectofuntus Pool Stairs
        when (obj.id) {
            5262 -> {
                when (player.plane) {
                    2 -> player.tele(Tile.of(3692, 9888, 3))
                    1 -> player.tele(Tile.of(3671, 9888, 2))
                    0 -> player.tele(Tile.of(3687, 9888, 1))
                }
            }

            5263 -> {
                when (player.plane) {
                    3 -> player.tele(Tile.of(3688, 9888, 2))
                    2 -> player.tele(Tile.of(3675, 9887, 1))
                    1 -> player.tele(Tile.of(3683, 9888, 0))
                }
            }
        }
    }

    onObjectClick(Ectofuntus.ECTOFUNTUS) { (player) ->  // Ectofuntus Worship
        if (!player.inventory.containsItem(Ectofuntus.BUCKET_OF_SLIME, 1)) {
            player.sendMessage("You need a bucket of slime before you can worship at the ectofuntus.")
            return@onObjectClick
        }

        var hasBonemeal = false
        for (item in player.inventory.items.toArray()) {
            if (item == null) continue

            val bone = BoneMeal.forMealId(item.id)
            if (bone != null) {
                hasBonemeal = true
                break
            }
        }

        if (!hasBonemeal) {
            player.sendMessage("You need some bonemeal in order to worship at the ectofuntus.")
            return@onObjectClick
        }

        for (item in player.inventory.items.toArray()) {
            if (item == null) continue

            val bone = BoneMeal.forMealId(item.id)
            if (bone != null) {
                val boneData = Burying.Bone.forId(bone.boneId)
                if (boneData == null) {
                    player.sendMessage("Error - bone not added... Please submit a Bug Report on Discord with the bone type you tried to add.")
                    return@onObjectClick
                }

                player.incrementCount("${ItemDefinitions.getDefs(bone.boneId).name} offered at ectofuntus")
                player.anim(1651)
                player.inventory.deleteItem(bone.boneMealId, 1)
                player.inventory.deleteItem(Ectofuntus.BUCKET_OF_SLIME, 1)
                player.inventory.addItem(Ectofuntus.EMPTY_POT, 1)
                player.inventory.addItem(Ectofuntus.EMPTY_BUCKET, 1)
                player.skills.addXp(Constants.PRAYER, boneData.experience * 4)
                player.unclaimedEctoTokens += 5
                break
            }
        }
    }

    onObjectClick(37454) { (player, obj) -> player.useStairs(-1, Tile.of(player.x, obj.y + 5, player.plane + 1), 0, 1) } // Ectofuntus Grinder Stairs Up
    onObjectClick(5281) { (player, obj) -> player.useStairs(-1, Tile.of(player.x, obj.y - 4, player.plane - 1), 0, 1) } // Ectofuntus Grinder Stairs Down

    onObjectClick(11163) { (player) -> grinder(player) } // Bone Grinder
    onObjectClick(11164) { (player) -> bin(player) } // Bone Grinder Bin

    onItemClick(Ectofuntus.FULL_ECTOPHIAL) { (player) -> // Empty Ectophial (Teleport)
        if (!player.isQuestComplete(Quest.GHOSTS_AHOY, "to use the ectophial.")) return@onItemClick

        player.schedule {
            player.anim(9609)
            player.spotAnim(1688)
            player.sendMessage("You empty your ectophial on the floor...", true)
            player.lock(3)
            wait(5)
            Magic.sendTeleportSpell(
                player, 8939, 8941, 1678, 1679, 0, 0.0, Tile.of(3659, 3523, 0), 3, true, TeleType.MAGIC,
                Runnable {
                    player.lock()
                    player.soundEffect(4580, true)
                },
                Runnable {
                    player.unlock()
                    player.sendMessage("Your ectophial magically fills itself from the nearby ectofuntus.", true)
                    /*val obj = World.getObject(Tile.of(3658, 3518, 0))
                    if (obj != null) {
                        player.inventory.replace(Ectofuntus.FULL_ECTOPHIAL, Ectofuntus.EMPTY_ECTOPHIAL)
                        player.walkToAndExecute(World.findClosestAdjacentFreeTile(Tile.of(3659, 3521, 0), 1)) {
                            player.schedule {
                                wait(1)
                                player.faceTile(obj.tile)
                                wait(1)
                                player.anim(833)
                                player.inventory.replace(Ectofuntus.EMPTY_ECTOPHIAL, Ectofuntus.FULL_ECTOPHIAL)
                                player.sendMessage("You refill your ectophial from the ectofuntus.")
                            }
                        }
                    }*/
                }
            )
        }
    }

    onItemOnObject(arrayOf(Ectofuntus.ECTOFUNTUS), arrayOf(Ectofuntus.EMPTY_ECTOPHIAL, Ectofuntus.FULL_ECTOPHIAL)) { e -> // Refill ectophial
        when (e.item.id) {
            Ectofuntus.EMPTY_ECTOPHIAL -> {
                e.player.anim(833)
                e.player.inventory.replace(Ectofuntus.EMPTY_ECTOPHIAL, Ectofuntus.FULL_ECTOPHIAL);
                e.player.sendMessage("You refill your ectophial from the ectofuntus.");
            }
            Ectofuntus.FULL_ECTOPHIAL -> e.player.sendMessage("Your ectophial is already full.")
        }
    }

}

fun hopper(player: Player, itemId: Int): Boolean {
    if (player.boneType != -1 && player.boneType != 0) {
        player.sendMessage("You already have some bones in the hopper.")
        return false
    }
    if (!player.inventory.containsItem(itemId, 1)) {
        return false
    }
    val meal = BoneMeal.forBoneId(itemId)
    if (meal != null) {
        player.boneType = meal.boneId
        player.sendMessage("You put the bones in the hopper.", true)
        player.anim(1649)
        player.inventory.deleteItem(meal.boneId, 1)
        return true
    }
    player.boneType = -1
    return false
}

fun grinder(player: Player): Boolean {
    return if (player.boneType != -1 && !player.bonesGrinded) {
        player.sendMessage("You turn the grinder, some crushed bones fall into the bin.", true)
        player.anim(1648)
        player.bonesGrinded = true
        true
    } else {
        player.anim(1648)
        false
    }
}

fun bin(player: Player): Boolean {
    if (player.boneType == -1) {
        player.sendMessage("You need to put some bones in the hopper and grind them first.")
        return false
    }
    if (!player.bonesGrinded) {
        player.sendMessage("You need to grind the bones by turning the grinder first.")
        return false
    }
    if (player.boneType != -1 && player.bonesGrinded) {
        val meal = BoneMeal.forBoneId(player.boneType)
        if (meal != null) {
            player.sendMessage("You fill an empty pot with bones.", true)
            player.anim(1650)
            player.inventory.deleteItem(Ectofuntus.EMPTY_POT, 1)
            player.inventory.addItem(meal.boneMealId, 1)
            player.boneType = -1
            player.bonesGrinded = false
            return true
        }
        player.boneType = -1
    }
    return false
}

enum class BoneMeal(val boneId: Int, val boneMealId: Int) {
    BONES(526, 4255),
    BAT_BONES(530, 4256),
    BIG_BONES(532, 4257),
    BABY_DRAGON_BONES(534, 4260),
    DRAGON_BONES(536, 4261),
    DAGANNOTH_BONES(6729, 6728),
    WYVERN_BONES(6812, 6810),
    OURG_BONES(4834, 4855),
    OURG_BONES_2(14793, 4855),
    FROST_BONES(18832, 18834),
    IMPIOUS_ASHES(20264, 20264),
    ACCURSED_ASHES(20266, 20266),
    INFERNAL_ASHES(20268, 20268);

    companion object {
        private val bonemeals = BoneMeal.entries.associateBy { it.boneId }
        private val bones = BoneMeal.entries.associateBy { it.boneMealId }

        fun forBoneId(itemId: Int): BoneMeal? = bonemeals[itemId]
        fun forMealId(itemId: Int): BoneMeal? = bones[itemId]
    }
}
