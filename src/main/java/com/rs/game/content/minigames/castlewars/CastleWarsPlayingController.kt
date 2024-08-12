package com.rs.game.content.minigames.castlewars

import com.rs.engine.pathfinder.Direction
import com.rs.game.World.spawnObject
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.Teleport
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Inventory
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.net.ClientPacket

class CastleWarsPlayingController(private val team: Int) : Controller() {

    override fun start() {
        sendInterfaces()
    }

    override fun canMove(dir: Direction?): Boolean {
        val toTile = Tile.of(player.x + (dir?.dx ?: 0), player.y + (dir?.dy ?: 0), player.plane)
        return !CastleWars.isBarricadeAt(toTile)
    }

    override fun processNPCClick2(npc: NPC?): Boolean {
        if (npc?.id == 1532 && npc is CastleWarsBarricadeNpc) {
            if (!player.inventory.containsItem(590, 1)) {
                player.sendMessage("You do not have the required items to light this.")
                return false
            }
            npc.litFire()
            return false
        }
        return true
    }

    override fun processButtonClick(interfaceId: Int, componentId: Int, slotId: Int, slotId2: Int, packet: ClientPacket?): Boolean {
        when (interfaceId) {
            387 -> {
                when (componentId) {
                    9, 6 -> {
                        player.sendMessage("You can't remove your team's colours.")
                        return false
                    }
                    15 -> {
                        val weaponId = player.equipment.weaponId
                        if (weaponId == 4037 || weaponId == 4039) {
                            player.sendMessage("You can't remove enemy's flag.")
                            return false
                        }
                    }
                }
            }
            Inventory.INVENTORY_INTERFACE -> {
                val item = player.inventory.getItem(slotId)
                if (item != null) {
                    if (item.id == 4053) {
                        if (player.x == 2422 && player.y == 3076 || player.x == 2426 && player.y == 3080 || player.x == 2423 && player.y == 3076 || player.x == 2426 && player.y == 3081 || player.x == 2373
                                && player.y == 3127 || player.x == 2373 && player.y == 3126 || player.x == 2376 && player.y == 3131 || player.x == 2377 && player.y == 3131 || !CastleWars.isBarricadeAt(player.tile)) {
                            player.sendMessage("You cannot place a barricade here!")
                            return false
                        }
                        CastleWars.addBarricade(team, player)
                        return false
                    }
                    if (item.id == 4049 || item.id == 4050 || item.id == 12853 || item.id == 14640 || item.id == 14648) {
                        doBandageEffect(item)
                        return false
                    }
                }
            }
        }
        return true
    }
    
    private fun doBandageEffect(item: Item) {
        val gloves = player.equipment.glovesId
        player.heal((player.maxHitpoints * (if (gloves in 11079..11084) 0.15 else 0.10)).toInt())
        val restoredEnergy = (player.runEnergy * 1.3).toInt()
        player.runEnergy = restoredEnergy.coerceAtMost(100).toDouble()
        player.inventory.deleteItem(item)
    }

    override fun canEquip(slotId: Int, itemId: Int): Boolean {
        if (slotId == Equipment.CAPE || slotId == Equipment.HEAD) {
            player.sendMessage("You can't remove your team's colours.")
            return false
        }
        if (slotId == Equipment.WEAPON || slotId == Equipment.SHIELD) {
            val weaponId = player.equipment.weaponId
            if (weaponId == 4037 || weaponId == 4039) {
                player.sendMessage("You can't remove enemy's flag.")
                return false
            }
        }
        return true
    }

    override fun canAttack(target: Entity?): Boolean {
        if (target is Player) {
            if (canHit(target)) {
                return true
            }
            player.sendMessage("You can't attack your team.")
            return false
        }
        return true
    }

    override fun processItemOnNPC(npc: NPC?, item: Item?): Boolean {
        if (npc == null || item == null)
            return true
        if (npc.id == 1532 && npc is CastleWarsBarricadeNpc) {
            if (item.id == 590) {
                npc.litFire()
                return false
            }
            if (item.id == 4045) {
                player.inventory.deleteItem(item)
                npc.explode()
                return false
            }
        }
        return true
    }

    override fun canHit(entity: Entity?): Boolean {
        if (entity is NPC)
            return true
        return (entity as Player).equipment.capeId != player.equipment.capeId
    }
    
    fun leave() {
        player.interfaceManager.removeOverlay()
        CastleWars.removePlayingPlayer(player, team)
    }

    override fun sendInterfaces() {
        player.interfaceManager.sendOverlay(58)
    }

    override fun sendDeath(): Boolean {
        WorldTasks.scheduleLooping(object : Task() {
            var loop: Int = 0

            override fun run() {
                if (loop == 0) player.anim(836)
                else if (loop == 1) player.sendMessage("Oh dear, you have died.")
                else if (loop == 3) {
                    val weaponId = player.equipment.weaponId
                    if (weaponId == 4037 || weaponId == 4039) {
                        CastleWars.setWeapon(player, null)
                        CastleWars.dropFlag(player.tile, if (weaponId == 4037) CastleWars.SARADOMIN else CastleWars.ZAMORAK)
                    } else {
                        val killer = player.mostDamageReceivedSourcePlayer
                        killer?.increaseKillCount(player)
                    }

                    player.reset()
                    player.tele(Tile.of(if (team == CastleWars.ZAMORAK) CastleWars.ZAMO_BASE else CastleWars.SARA_BASE, 1))
                    player.anim(-1)
                } else if (loop == 4) {
                    player.jingle(90)
                    stop()
                }
                loop++
            }
        }, 0, 1)
        return false
    }

    override fun logout(): Boolean {
        player.tile = Tile.of(CastleWars.LOBBY, 2)
        return true
    }

    override fun processTeleport(tele: Teleport?): Boolean {
        player.simpleDialogue("You can't leave just like that!")
        return false
    }

    override fun processObjectClick1(obj: GameObject?): Boolean {
        if (obj == null)
            return true
        val id: Int = obj.id
        if (id == 4406 || id == 4407) {
            removeController()
            leave()
            return false
        }
        if ((id == 4469 && team == CastleWars.SARADOMIN) || (id == 4470 && team == CastleWars.ZAMORAK)) {
            passBarrier(obj)
            return false
        }
        if (id == 4377 || id == 4378) { // no flag anymore
            if (id == 4377 && team == CastleWars.SARADOMIN) {
                if (player.equipment.weaponId == 4039) {
                    CastleWars.addScore(player, team, CastleWars.ZAMORAK)
                    return false
                }
            } else if (id == 4378 && team == CastleWars.ZAMORAK) if (player.equipment.weaponId == 4037) {
                CastleWars.addScore(player, team, CastleWars.SARADOMIN)
                return false
            }
            player.sendMessage("You need to bring a flag back here!")
            return false
        } else if (id == 4902 || id == 4903) { // take flag
            if (id == 4902 && team == CastleWars.SARADOMIN) {
                if (player.equipment.weaponId == 4039) {
                    CastleWars.addScore(player, team, CastleWars.ZAMORAK)
                    return false
                }
                player.sendMessage("Saradomin won't let you take his flag!")
            } else if (id == 4903 && team == CastleWars.ZAMORAK) {
                if (player.equipment.weaponId == 4037) {
                    CastleWars.addScore(player, team, CastleWars.SARADOMIN)
                    return false
                }
                player.sendMessage("Zamorak won't let you take his flag!")
            } else  // take flag
                CastleWars.takeFlag(player, team, if (id == 4902) CastleWars.SARADOMIN else CastleWars.ZAMORAK, obj, false)
            return false
        } else if (id == 4900 || id == 4901) { // take dropped flag
            CastleWars.takeFlag(player, team, if (id == 4900) CastleWars.SARADOMIN else CastleWars.ZAMORAK, obj, true)
            return false
        } else if (id == 36579 || id == 36586) {
            player.inventory.addItem(Item(4049))
            return false
        } else if (id == 36575 || id == 36582) {
            player.inventory.addItem(Item(4053))
            return false
        } else if (id == 36577 || id == 36584) {
            player.inventory.addItem(Item(4045))
            return false
            // under earth from basess
        } else if (id == 4411) { // stepping stone
            if (obj.x == player.x && obj.y == player.y) return false
            player.lock(2)
            player.anim(741)
            player.addWalkSteps(obj.x, obj.y, -1, false)
        } else if (id == 36693) {
            player.useStairs(827, Tile.of(2430, 9483, 0), 1, 2)
            return false
        } else if (id == 36694) {
            player.useStairs(827, Tile.of(2369, 9524, 0), 1, 2)
            return false
        } else if (id == 36645) {
            player.useStairs(828, Tile.of(2430, 3081, 0), 1, 2)
            return false
        } else if (id == 36646) {
            player.useStairs(828, Tile.of(2369, 3126, 0), 1, 2)
            return false
        } else if (id == 4415) {
            if (obj.x == 2417 && obj.y == 3075 && obj.plane == 1) player.useStairs(-1, Tile.of(2417, 3078, 0), 0, 1)
            else if (obj.x == 2419 && obj.y == 3080 && obj.plane == 1) player.useStairs(-1, Tile.of(2419, 3077, 0), 0, 1)
            else if (obj.x == 2430 && obj.y == 3081 && obj.plane == 2) player.useStairs(-1, Tile.of(2427, 3081, 1), 0, 1)
            else if (obj.x == 2425 && obj.y == 3074 && obj.plane == 3) player.useStairs(-1, Tile.of(2425, 3077, 2), 0, 1)
            else if (obj.x == 2380 && obj.y == 3127 && obj.plane == 1) player.useStairs(-1, Tile.of(2380, 3130, 0), 0, 1)
            else if (obj.x == 2382 && obj.y == 3132 && obj.plane == 1) player.useStairs(-1, Tile.of(2382, 3129, 0), 0, 1)
            else if (obj.x == 2369 && obj.y == 3126 && obj.plane == 2) player.useStairs(-1, Tile.of(2372, 3126, 1), 0, 1)
            else if (obj.x == 2374 && obj.y == 3133 && obj.plane == 3) player.useStairs(-1, Tile.of(2374, 3130, 2), 0, 1)
            return false
        } else if (id == 36481) {
            player.useStairs(-1, Tile.of(2417, 3075, 0), 0, 1)
            return false
        } else if (id == 36495 && obj.plane == 0) {
            player.useStairs(-1, Tile.of(2420, 3080, 1), 0, 1)
            return false
        } else if (id == 36480 && obj.plane == 1) {
            player.useStairs(-1, Tile.of(2430, 3080, 2), 0, 1)
            return false
        } else if (id == 36484 && obj.plane == 2) {
            player.useStairs(-1, Tile.of(2426, 3074, 3), 0, 1)
            return false
        } else if (id == 36532 && obj.plane == 0) {
            player.useStairs(-1, Tile.of(2379, 3127, 1), 0, 1)
            return false
        } else if (id == 36540) {
            player.useStairs(-1, Tile.of(2383, 3132, 0), 0, 1)
            return false
        } else if (id == 36521 && obj.plane == 1) {
            player.useStairs(-1, Tile.of(2369, 3127, 2), 0, 1)
            return false
        } else if (id == 36523 && obj.plane == 2) {
            player.useStairs(-1, Tile.of(2373, 3133, 3), 0, 1)
            return false
        } else if (id == 36644) {
            if (obj.y == 9508) player.useStairs(828, Tile.of(2400, 3106, 0), 1, 2)
            else if (obj.y == 9499) player.useStairs(828, Tile.of(2399, 3100, 0), 1, 2)
            player.freeze(0)
            return false
        } else if (id == 36691) {
            if (obj.y == 3099) player.useStairs(827, Tile.of(2399, 9500, 0), 1, 2)
            else if (obj.y == 3108) player.useStairs(827, Tile.of(2400, 9507, 0), 1, 2)
            player.freeze(0)
            return false
        } /*
		 * else if (id == 4438) player.getActionManager().setSkill(new
		 * Mining(object, RockDefinitions.SMALLER_ROCKS)); else if (id == 4437)
		 * player.getActionManager().setSkill(new Mining(object,
		 * RockDefinitions.ROCKS ));
		 */
        else if (id == 4448) {
            for (players in CastleWars.getPlaying())
                for (player in players)
                    if (player.withinDistance(obj.tile, 1))
                        player.applyHit(Hit(player, player.hitpoints, HitLook.TRUE_DAMAGE))
            spawnObject(GameObject(4437, obj.type, obj.rotation, obj.x, obj.y, obj.plane))
        }
        return true
    }

    fun passBarrier(obj: GameObject) {
        if (obj.rotation == 0 || obj.rotation == 2) {
            if (player.y != obj.y) return
            player.lock(2)
            player.addWalkSteps(if (obj.x == player.x) obj.x + (if (obj.rotation == 0) -1 else +1) else obj.x, obj.y, -1, false)
        } else if (obj.rotation == 1 || obj.rotation == 3) {
            if (player.x != obj.x) return
            player.lock(2)
            player.addWalkSteps(obj.x, if (obj.y == player.y) obj.y + (if (obj.rotation == 3) -1 else +1) else obj.y, -1, false)
        }
    }

    override fun onTeleported(type: TeleType?) {
        removeController()
        leave()
    }

    override fun forceClose() {
        leave()
    }
    
}