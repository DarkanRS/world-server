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
package com.rs.game.content.bosses.corp

import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.World
import com.rs.game.World.floorAndWallsFree
import com.rs.game.World.sendSpotAnim
import com.rs.game.content.combat.CombatStyle
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript.delayHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMaxHit
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat
import com.rs.plugin.kts.onButtonClick
import com.rs.utils.WorldUtil

@ServerStartupEvent
fun mapCorporealBeastPlugins() {
    onButtonClick(650) { (player, _, componentId) ->
        when(componentId) {
            15 -> {
                player.stopAll()
                player.tele(Tile.of(2974, 4384, player.plane))
                player.controllerManager.startController(CorporealBeastController())
            }
            16 -> player.closeInterfaces()
        }
    }

    npcCombat(8133) { npc, target ->
        var attackStyle = Utils.getRandomInclusive(4)

        if (Utils.getRandomInclusive(3) == 0 && npc.hitpoints < (npc.maxHitpoints / 2) && npc is CorporealBeast)
            npc.spawnDarkEnergyCore()

        if (attackStyle == 0 || attackStyle == 1) {
            if (attackMelee(npc, target))
                return@npcCombat npc.attackSpeed
            attackStyle = 2 + Utils.getRandomInclusive(2)
        }

        when(attackStyle) {
            2 -> attackMagicSingleTarget(npc, target)
            3 -> attackMagicDrain(npc, target)
            4 -> attackMagicSplash(npc, target)
        }
        return@npcCombat npc.attackSpeed
    }

    instantiateNpc(8133) { npcId, tile -> CorporealBeast(npcId, tile, false) }
}

private fun attackMelee(npc: NPC, target: Entity): Boolean {
    val distanceX = target.x - npc.x
    val distanceY = target.y - npc.y
    if ((distanceX <= npc.size) && (distanceX >= -1) && (distanceY <= npc.size) && (distanceY >= -1)) {
        npc.anim(if (Utils.getRandomInclusive(1) == 0) npc.combatDefinitions.attackEmote else 10058)
        delayHit(npc, 0, target, Hit.magic(npc, getMaxHit(npc, npc.combatDefinitions.maxHit, CombatStyle.MELEE, target)))
        return true
    }
    return false
}

private fun attackMagicSingleTarget(npc: NPC, target: Entity) {
    npc.anim(10410)
    delayHit(npc, World.sendProjectile(npc, target, 1825, heights = 41 to 20, delay = 0, speed = 10, angle = 15).taskDelay, target, Hit.magic(npc, getMaxHit(npc, 650, CombatStyle.MAGIC, target)))
}

private fun attackMagicDrain(npc: NPC, target: Entity) {
    npc.anim(10410)
    val delay = World.sendProjectile(npc, target, 1823, heights = 41 to 20, delay = 0, speed = 10, angle = 15).taskDelay
    delayHit(npc, delay, target, Hit.magic(npc, getMaxHit(npc, 550, CombatStyle.MAGIC, target)))
    if (target is Player) {
        val skill = when (Utils.getRandomInclusive(2)) {
            0 -> Constants.MAGIC
            1 -> Constants.SUMMONING
            else -> Constants.PRAYER
        }
        if (skill == Constants.PRAYER) {
            target.prayer.drainPrayer((10 + Utils.getRandomInclusive(40)).toDouble())
        } else {
            val newLevel = target.skills.getLevel(skill) - (1 + Utils.getRandomInclusive(4))
            target.skills.set(skill, newLevel.coerceAtLeast(0))
        }
        target.sendMessage("Your ${Constants.SKILL_NAME[skill]} has been slightly drained!", true)
    }
}

fun attackMagicSplash(npc: NPC, target: Entity) {
    npc.anim(10410)
    val initialTile = Tile.of(target.tile)
    npc.schedule {
        wait(World.sendProjectile(npc, initialTile, 1824, heights = 41 to 5, delay = 0, speed = 15, angle = 15).taskDelay)
        repeat(6) {
            val newTile = Tile.of(initialTile, 3)
            if (floorAndWallsFree(newTile, 1)) {
                npc.possibleTargets.filter { Utils.getDistance(newTile.x, newTile.y, it.x, it.y) <= 1 && it.lineOfSightTo(newTile, false) }
                    .forEach { delayHit(npc, 0, it, Hit.magic(npc, getMaxHit(npc, 350, CombatStyle.MAGIC, it))) }
                npc.schedule {
                    wait(World.sendProjectile(initialTile, newTile, 1824, heights = 15 to 5, delay = 15, speed = 15, angle = 15).taskDelay + 1)
                    sendSpotAnim(newTile, SpotAnim(1806))
                }
            }
        }
    }
}

class CorporealBeast(id: Int, tile: Tile?, spawned: Boolean) : NPC(id, tile, spawned) {
    private var core: DarkEnergyCore? = null

    init {
        capDamage = 1000
        lureDelay = 3000
        setForceAggroDistance(64)
        isIntelligentRouteFinder = true
        isIgnoreDocile = true
    }

    fun spawnDarkEnergyCore() {
        core ?: run { core = DarkEnergyCore(this) }
    }

    fun removeDarkEnergyCore() {
        core?.finish()
        core = null
    }

    override fun getPossibleTargets(): MutableList<Entity> {
        return super.possibleTargets.filterTo(mutableListOf()) { it.x > 2972 }
    }

    override fun handlePreHit(hit: Hit) {
        if (hit.look == HitLook.CANNON_DAMAGE && hit.damage > 80) {
            hit.setDamage(80)
        }
        (hit.source as? Player)?.let { player ->
            if ((hit.look == HitLook.MELEE_DAMAGE || hit.look == HitLook.RANGE_DAMAGE) &&
                player.equipment.getWeaponId() != -1 && !ItemDefinitions.getDefs(player.equipment.getWeaponId()).getName().contains("spear"))
                    hit.setDamage(hit.damage / 2)

        }
        super.handlePreHit(hit)
    }

    override fun processNPC() {
        super.processNPC()
        if (isDead) return

        if (tickCounter % 3 == 0L) {
            val targetsInRange = possibleTargets.filter { WorldUtil.isInRange(this, it, -1) }
            if (targetsInRange.isNotEmpty()) {
                targetsInRange.forEach { it.applyHit(Hit(this, Utils.random(150, 513), HitLook.TRUE_DAMAGE), 0) }
                sync(10496, 1834)
            }
        }

        if (attackedBy != null && lineOfSightTo(attackedBy, false)) {
            setAttackedBy(null)
        }

        if (hitpoints < maxHitpoints && possibleTargets.isEmpty() && attackedBy == null) {
            resetLevels()
            hitpoints = maxHitpoints
        }
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        core?.sendDeath(source)
    }

    override fun getMagePrayerMultiplier(): Double = 0.6
}
