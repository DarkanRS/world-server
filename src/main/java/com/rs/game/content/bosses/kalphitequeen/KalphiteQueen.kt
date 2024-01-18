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
import com.rs.game.model.entity.Entity
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
        resetWalkSteps()
        combat.removeTarget()
        anim(-1)
        WorldTasks.scheduleTimer { loop: Int ->
            if (loop == 0) anim(combatDefinitions.deathEmote)
            else if (loop >= combatDefinitions.deathDelay) {
                if (id == 1158) {
                    isCantInteract = true
                    transformIntoNPC(1160)
                    sync(6270, 1055)
                    tasks.schedule("kqDeath") {
                        reset()
                        isCantInteract = false
                    }
                } else {
                    drop()
                    reset()
                    setLocation(respawnTile)
                    finish()
                    if (!isSpawned) setRespawnTask()
                    transformIntoNPC(1158)
                }
                return@scheduleTimer false
            }
            return@scheduleTimer true
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
            sendProjectile(npc, target, 288, 30, 15, 1.8) {
                delayHit(npc, 0, target, getRangeHit(npc, getMaxHit(npc, npc.combatDefinitions.maxHit, NPCCombatDefinitions.AttackStyle.RANGE, target, 10000.0)))
                if (target is Player)
                    target.prayer.drainPrayer(10.0)
            }
            return@npcCombat npc.attackSpeed
        }
        //mage
        npc.spotAnim(if (npc.id == 1158) 278 else 279)
        projectileBounce(npc, target, mutableSetOf(target), 280, 281, 1.8) { nextTarget ->
            delayHit(npc, 0, nextTarget, getMagicHit(npc, getMaxHit(npc, npc.maxHit, NPCCombatDefinitions.AttackStyle.MAGE, nextTarget, 10000.0)))
        }
        return@npcCombat npc.attackSpeed
    }
}
