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
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.utils.WorldUtil
import java.util.function.Consumer

class DarkEnergyCore(beast: CorporealBeast) : NPC(8127, Tile.of(beast.tile), true) {
    private val beast: CorporealBeast
    private var target: Entity? = null

    private var changeTarget: Int
    private var sapTimer = 0
    private var delay = 0

    init {
        setForceMultiArea(true)
        isIgnoreDocile = true
        this.beast = beast
        changeTarget = 2
    }

    override fun processNPC() {
        if (isDead || hasFinished()) return
        if (delay > 0) {
            delay--
            return
        }
        if (changeTarget > 0) {
            if (changeTarget == 1) {
                val possibleTarget: MutableList<Entity> = beast.getPossibleTargets()
                if (possibleTarget.isEmpty()) {
                    finish()
                    beast.removeDarkEnergyCore()
                    return
                }
                target = possibleTarget[Utils.getRandomInclusive(possibleTarget.size - 1)]
                isHidden = true
                delay += World.sendProjectile(this, target!!.tile, 1828, 0 to 0, 0, 10, 20, 0) {
                    tele(it.destination)
                    isHidden = false
                }.taskDelay
            }
            changeTarget--
            return
        }
        if (target == null || !WorldUtil.isInRange(this, target, 0)) {
            changeTarget = 5
            return
        }
        if (sapTimer-- <= 0) {
            val damage = Utils.getRandomInclusive(50) + 50
            target!!.applyHit(Hit(this, Utils.random(1, 131), HitLook.TRUE_DAMAGE))
            beast.heal(damage)
            delay = 2
            (target as? Player)?.sendMessage("The dark core creature steals some life from you for its master.", true)
            sapTimer = if (poison.isPoisoned) 40 else 0
        }
        delay = 2
    }

    override fun getMagePrayerMultiplier(): Double {
        return 0.6
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        beast.removeDarkEnergyCore()
    }
}
