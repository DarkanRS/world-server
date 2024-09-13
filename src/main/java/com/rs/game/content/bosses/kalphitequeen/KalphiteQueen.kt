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
package com.rs.game.content.bosses.kalphitequeen

import com.rs.game.World.sendProjectile
import com.rs.game.content.combat.CombatStyle
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript.*
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler.getDefaultCombat
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.npc.combat.NPCCombatUtil.Companion.projectileBounce
import com.rs.game.model.entity.player.Player
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils.random
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat

class KalphiteQueen(id: Int, tile: Tile, spawned: Boolean) : NPC(id, tile, spawned) {
    init {
        isIgnoreNPCClipping = true
        lureDelay = 0
        isForceAgressive = true
        isIgnoreDocile = true
    }
    override fun sendDeath(source: Entity) {
        if (id == 1158) {
            resetWalkSteps()
            combat.removeTarget()
            anim(-1)
            schedule {
                anim(combatDefinitions.deathEmote)
                wait(combatDefinitions.deathDelay)
                isCantInteract = true
                transformIntoNPC(1160)
                sync(6270, 1055)
                schedule {
                    reset()
                    isCantInteract = false
                }
            }
            return
        }
        super.sendDeath(source)
        schedule {
            wait(combatDefinitions.deathDelay)
            transformIntoNPC(1158)
        }
    }
}

@ServerStartupEvent
fun mapKalphiteQueen() {
    instantiateNpc(1158, 1160) { id, tile -> KalphiteQueen(id, tile, false)}

    npcCombat(1158, 1160) { npc, target ->
        if (target.inMeleeRange(npc) && random(2) == 0)
            return@npcCombat getDefaultCombat().apply(npc, target)
        npc.anim(if (npc.id == 1158) 6240 else 6234)
        if (random(2) == 0) {
            //range
            sendProjectile(npc, target, 288, delay = 30, speed = 5, angle = 15) {
                delayHit(npc, 0, target, Hit.range(npc, getMaxHit(npc, npc.combatDefinitions.maxHit, CombatStyle.RANGE, target, 10000.0)))
                if (target is Player)
                    target.prayer.drainPrayer(10.0)
            }
            return@npcCombat npc.attackSpeed
        }
        //mage
        npc.spotAnim(if (npc.id == 1158) 278 else 279)
        projectileBounce(npc, target, mutableSetOf(target), 280, 281, 5) { nextTarget ->
            delayHit(npc, 0, nextTarget, Hit.magic(npc, getMaxHit(npc, npc.maxHit, CombatStyle.MAGE, nextTarget, 10000.0)))
        }
        return@npcCombat npc.attackSpeed
    }
}
