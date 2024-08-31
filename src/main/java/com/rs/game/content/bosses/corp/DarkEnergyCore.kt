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

import com.rs.game.World
import com.rs.game.model.WorldProjectile
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.utils.WorldUtil
import java.util.function.Consumer

class DarkEnergyCore(private val beast: CorporealBeast) : NPC(8127, Tile.of(beast.tile), true) {

    init {
        setForceMultiArea(true)
        isIgnoreDocile = true
    }

    override fun processNPC() {
        if (isDead || hasFinished()) return

        if (!isLocked && !isHidden && !poison.isPoisoned && tickCounter % 2L == 0L) {
            val target = beast.possibleTargets.filter { it is Player && it.withinDistance(this.tile, 1) }.firstOrNull()
            if (target == null)
                jump()
            else
                sapLife()
        }
    }

    private fun jump() {
        val target = beast.possibleTargets.takeIf { it.isNotEmpty() }?.let { it[Utils.getRandomInclusive(it.size - 1)] }
        if (target == null) {
            finish()
            beast.removeDarkEnergyCore()
            return
        }
        lock()
        schedule {
            val start = this@DarkEnergyCore.tile
            val destination = Tile.of(target.tile)
            val distance = Utils.getDistance(start, destination)
            isHidden = true
            val speed = when {
                distance <= 2 -> 30
                distance <= 5 -> 60
                else -> 90
            }
            val travelTime = World.sendProjectileAbsoluteSpeed(start, destination, 1828, heights = 15 to 15, delay = 0, speed, offset = 20, angle = 30).taskDelay;
            wait(travelTime+1)
            tele(destination)
            isHidden = false
            wait(2)
            unlock()
        }
    }

    private fun sapLife() {
        beast.possibleTargets.filter { it is Player && it.withinDistance(this.tile, 1) }.forEach { target ->
            val damage = Utils.random(50, 100)
            target.applyHit(Hit(this, damage, HitLook.TRUE_DAMAGE))
            beast.heal(damage)
            (target as? Player)?.sendMessage("The dark core creature steals some life from you for its master.", true)
        }
    }

    override fun getMagePrayerMultiplier() = 0.6

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        beast.removeDarkEnergyCore()
    }
}