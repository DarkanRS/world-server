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
package com.rs.game.content

import com.rs.cache.loaders.ObjectType
import com.rs.engine.pathfinder.Direction
import com.rs.engine.pathfinder.Direction.Companion.forDelta
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.combat.calculateHit
import com.rs.game.content.combat.isRanging
import com.rs.game.content.world.areas.wilderness.WildernessController
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.OwnedObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.lib.util.Vec2
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onObjectClick

@JvmField
val CANNON_PIECES = arrayOf(
    intArrayOf(6, 8, 10, 12),
    intArrayOf(20494, 20495, 20496, 20497),
    intArrayOf(20498, 20499, 20500, 20501)
)
private val CANNON_OBJECTS = arrayOf(
    intArrayOf(7, 8, 9, 6),
    intArrayOf(29398, 29401, 29402, 29406),
    intArrayOf(29403, 29404, 29405, 29408)
)
private val CANNON_EMOTES = intArrayOf(303, 305, 307, 289, 184, 182, 178, 291)

@ServerStartupEvent
fun mapCannon() {
    onObjectClick("Dwarf multicannon", "Gold dwarf multicannon", "Royale dwarf multicannon") { (player, obj, option) ->
        if (obj is DwarfMultiCannon) {
            if (option == "Fire") obj.fire(player)
            else if (option == "Pick-up") obj.pickUp(player)
        }
    }

    onItemClick(6, 20494, 20498, options = arrayOf("Set-up")) { e -> setUp(e.player, if (e.item.id == 6) 0 else if (e.item.id == 20494) 1 else 2) }
}

class DwarfMultiCannon(player: Player, tile: Tile, private val cannonType: Int) : OwnedObject(player, CANNON_OBJECTS[cannonType][0], ObjectType.SCENERY_INTERACT, 0, tile) {
    private var balls = 0
    private var decay = 0
    private var spinRot = Direction.NORTH

    private enum class BannedArea(stopMes: String?, private vararg val regionIds: Int) {
        ABYSS(12108, 12109),
        ANCIENT_CAVERN(6995, 6994, 6738, 6482),
        AL_KHARID_PALACE(13105),
        BLACK_GUARD("It is not permitted to set up a cannon this close to the Dwarf Black Guard.", 11830, 11829, 12085, 12086),
        DWARVEN_MINE(11929, 12185, 12184),
        ENCHANTED_VALLEY(12102),
        ENTRANA(11060, 11316),
        FREM_SLAYER_DUNG("The air is too dank for you to set up a cannon here.", 11164, 10908, 10907),
        FELDIP_HILLS(10029),
        GRAND_EXCHANGE("The Grand Exchange staff prefer not to have heavy artillery operated around their premises.", 12598),
        KILLERWATT_PLANE("The electricity bursting through this plane would render the cannon useless.", 10577),
        TARNS_LAIR("This temple is ancient and would probably collapse if you started firing a cannon.", 12615, 12616),
        MORT_MYRE_SWAMP(13621, 13877, 14133, 13620, 13876, 13619, 13875, 13874, 13618, 13363, 14131, 14130),
        OURIANA_ALTAR(13131),
        SLAYER_TOWER(13623),
        REVENANT_CAVE(12446),
        WARRIORS_GUILD(11319),
        WYVERN_CAVE(12181),
        KALPHITE_QUEEN(13972),
        KING_BLACK_DRAGON(9033);

        val message: String = stopMes ?: "It is not permitted to set up a cannon here."

        constructor(vararg regionIds: Int) : this(null, *regionIds)

        companion object {
            internal val MAP: MutableMap<Int, BannedArea> = HashMap()

            init {
                for (area in entries) {
                    for (regionId in area.regionIds) MAP[regionId] = area
                }
            }
        }
    }

    val maxBalls: Int
        get() = when (cannonType) {
            2 -> 100
            1 -> 50
            else -> 30
        }

    fun fire(player: Player) {
        if (!ownedBy(player)) {
            player.sendMessage("This is not your cannon.")
            return
        }
        if (balls < 30) {
            var amount = player.inventory.getAmountOf(2)
            if (amount == 0) player.sendMessage("You need to load your cannon with cannon balls before firing it!")
            else {
                val add = 30 - balls
                if (amount > add) amount = add
                balls += amount
                player.inventory.deleteItem(2, amount)
                player.sendMessage("You load the cannon with $amount cannon balls.")
            }
        } else player.sendMessage("Your cannon is full.")
    }

    fun pickUp(player: Player) {
        if (!ownedBy(player)) {
            player.sendMessage("This is not your cannon.")
            return
        }
        val space = if (balls > 0 && !player.inventory.containsItem(2, 1)) 5 else 4
        if (player.inventory.freeSlots < space) {
            player.sendMessage("You need at least $space inventory spaces to pick up your cannon.")
            return
        }
        player.sendMessage("You pick up the cannon. It's really heavy.")
        for (i in CANNON_PIECES[cannonType].indices) player.inventory.addItem(CANNON_PIECES[cannonType][i], 1)
        if (balls > 0) {
            player.inventory.addItem(2, balls)
            balls = 0
        }
        player.placedCannon = 0
        destroy()
    }

    override fun tick(owner: Player?) {
        if (owner == null) {
            decay++
            if (decay >= 200) destroy()
            return
        }
        if (id != CANNON_OBJECTS[cannonType][3] || balls == 0) return
        spinRot = if (spinRot.ordinal + 1 == Direction.entries.size) Direction.NORTH
        else Direction.entries[spinRot.ordinal + 1]
        World.sendObjectAnimation(this, Animation(CANNON_EMOTES[spinRot.ordinal]))
        val cannonTile: Tile = this.tile.transform(1, 1, 0)
        for (npc in World.getNPCsInChunkRange(cannonTile.chunkId, 2)) {
            if (npc === owner.familiar || npc.isDead || !npc.definitions.hasAttackOption() || !owner.controllerManager.canHit(npc)) continue
            if (!npc.lineOfSightTo(cannonTile, false) || (!owner.isAtMultiArea && owner.inCombat() && owner.attackedBy !== npc)) continue
            if (!owner.isAtMultiArea && npc.attackedBy !== owner && npc.inCombat()) continue

            if (npc.withinDistance(cannonTile, 10) && getDirectionTo(npc) == spinRot) {
                val hit = calculateHit(owner, npc, 0, 300, owner.equipment.weaponId, owner.combatDefinitions.getAttackStyle(), isRanging(owner), true, 1.0)
                val proj = World.sendProjectile(Tile.of(x + 1, y + 1, plane), npc, 53, 38 to 38, 30, 2, 0)
                WorldTasks.schedule(proj.taskDelay) { npc.applyHit(Hit(owner, hit.damage, Hit.HitLook.CANNON_DAMAGE)) }
                owner.skills.addXp(Constants.RANGE, hit.damage.toDouble() / 5.0)
                balls--
                npc.setCombatTarget(owner)
                npc.setAttackedBy(owner)
                break
            }
        }
    }

    fun getDirectionTo(entity: Entity): Direction? {
        val to = entity.middleTileAsVector
        val from = Vec2(tile.transform(1, 1, 0))
        val sub = to.sub(from)
        sub.norm()
        val delta: Tile = sub.toTile()
        return forDelta(delta.x, delta.y)
    }
}

private enum class BannedArea(message: String?, private vararg val regionIds: Int) {
    ABYSS(12108, 12109),
    ANCIENT_CAVERN(6995, 6994, 6738, 6482),
    AL_KHARID_PALACE(13105),
    BLACK_GUARD("It is not permitted to set up a cannon this close to the Dwarf Black Guard.", 11830, 11829, 12085, 12086),
    DWARVEN_MINE(11929, 12185, 12184),
    ENCHANTED_VALLEY(12102),
    ENTRANA(11060, 11316),
    FREM_SLAYER_DUNG("The air is too dank for you to set up a cannon here.", 11164, 10908, 10907),
    FELDIP_HILLS(10029),
    GRAND_EXCHANGE("The Grand Exchange staff prefer not to have heavy artillery operated around their premises.", 12598),
    KILLERWATT_PLANE("The electricity bursting through this plane would render the cannon useless.", 10577),
    TARNS_LAIR("This temple is ancient and would probably collapse if you started firing a cannon.", 12615, 12616),
    MORT_MYRE_SWAMP(13621, 13877, 14133, 13620, 13876, 13619, 13875, 13874, 13618, 13363, 14131, 14130),
    OURIANA_ALTAR(13131),
    SLAYER_TOWER(13623),
    REVENANT_CAVE(12446),
    WARRIORS_GUILD(11319),
    WYVERN_CAVE(12181),
    KALPHITE_QUEEN(13972),
    KING_BLACK_DRAGON(9033);

    val message = message ?: "It is not permitted to set up a cannon here."

    constructor(vararg regionIds: Int) : this(null, *regionIds)

    companion object {
        val MAP: MutableMap<Int, BannedArea> = HashMap()

        init {
            for (area in entries) {
                for (regionId in area.regionIds) MAP[regionId] = area
            }
        }
    }
}


fun eligibleForCannonReplacement(player: Player): Boolean {
    return player.placedCannon > 0 && OwnedObject.getNumOwned(player, DwarfMultiCannon::class.java) == 0
}

private fun setUp(player: Player, cannonType: Int) {
    if (!player.isQuestComplete(Quest.DWARF_CANNON)) {
        player.sendMessage("You have no idea how to operate this machine.")
        return
    }
    if (OwnedObject.getNumOwned(player, DwarfMultiCannon::class.java) > 0) {
        player.sendMessage("You already have a cannon placed.")
        return
    }
    val controller = player.controllerManager.controller
    if (controller != null && controller !is WildernessController) {
        player.sendMessage("You can't place your cannon here.")
        return
    }
    val area = BannedArea.MAP[player.regionId]
    if (area != null) {
        player.sendMessage(area.message)
        return
    }
    var count = 0
    for (item in CANNON_PIECES[cannonType]) {
        if (!player.inventory.containsItem(item, 1)) break
        count++
    }
    if (count < 4) {
        player.sendMessage("You don't have all your cannon parts.")
        return
    }
    val pos = player.transform(-2, -3, 0)
    if (!World.floorAndWallsFree(pos, 3) || World.getObject(pos, ObjectType.SCENERY_INTERACT) != null) {
        player.sendMessage("There isn't enough space to set up here.")
        return
    }
    player.lock()
    player.nextFaceTile = pos
    val cannon = DwarfMultiCannon(player, pos, cannonType)
    player.schedule {
        player.anim(827)
        player.sendMessage("You place the cannon base on the ground.")
        cannon.createNoReplace()
        player.inventory.deleteItem(CANNON_PIECES[cannonType][0], 1)
        wait(2)
        player.sendMessage("You add the stand.")
        cannon.setId(CANNON_OBJECTS[cannonType][1])
        player.inventory.deleteItem(CANNON_PIECES[cannonType][1], 1)
        wait(2)
        player.sendMessage("You add the barrels.")
        cannon.setId(CANNON_OBJECTS[cannonType][2])
        player.inventory.deleteItem(CANNON_PIECES[cannonType][2], 1)
        wait(2)
        player.sendMessage("You add the furnace.")
        cannon.setId(CANNON_OBJECTS[cannonType][3])
        player.inventory.deleteItem(CANNON_PIECES[cannonType][3], 1)
        player.placedCannon = cannonType + 1
        wait(2)
        player.unlock()
    }
}