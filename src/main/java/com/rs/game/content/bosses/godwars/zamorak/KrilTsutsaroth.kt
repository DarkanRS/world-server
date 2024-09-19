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
package com.rs.game.content.bosses.godwars.zamorak

import com.rs.game.World
import com.rs.game.content.bosses.godwars.GodWarMinion
import com.rs.game.content.combat.CombatStyle
import com.rs.game.model.entity.ForceTalk
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript.delayHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMaxHit
import com.rs.game.model.entity.player.Player
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Animation
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.lib.util.Utils.getRandomInclusive
import com.rs.lib.util.Utils.random
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.handlers.NPCInstanceHandler
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat
import java.util.function.BiFunction

private val QUOTES = arrayOf("Attack them, you dogs!", "Forward!", "Death to Saradomin's dogs!", "Kill them, you cowards!", "The Dark One will have their souls!", "Zamorak curse them!", "Rend them limb from limb!", "No retreat!", "Flay them all!")

@ServerStartupEvent
fun mapKrilTsutsaroth() {
    instantiateNpc(6203) { npcId, tile ->
        KrilTstsaroth(npcId, tile, false)
    }

    npcCombat(6203) { npc, target ->
        if (random(4) == 0)
            npc.forceTalk(QUOTES.random())
        val attackStyle = getRandomInclusive(2)
        when (attackStyle) {
            0 -> {
                npc.sync(14962, 1210)
                for (t in npc.possibleTargets) {
                    delayHit(npc, 1, t, Hit.magic(npc, getMaxHit(npc, 300, CombatStyle.MAGE, t)))
                    World.sendProjectile(npc, t, 1211, 41 to 16, 41, 5, 16)
                    if (getRandomInclusive(4) == 0) t.poison.makePoisoned(168)
                }
            }

            1, 2 -> {
                var damage = 300
                for (e in npc.possibleTargets) {
                    if (e is Player && e.prayer.isProtectingMelee && random(10) == 0) {
                        damage = 497
                        npc.forceTalk("YARRRRRRR!")
                        e.prayer.drainPrayer(e.prayer.points / 2)
                        e.sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.")
                    }
                    npc.anim(if (damage <= 463) 14963 else 14968)
                    if (damage <= 463) delayHit(npc, 0, e, Hit.melee(npc, getMaxHit(npc, damage, CombatStyle.MELEE, e)))
                    else delayHit(npc, 0, e, Hit.flat(npc, getMaxHit(npc, damage, CombatStyle.MELEE, e)))
                }
            }
        }
        return@npcCombat npc.attackSpeed
    }
}

class KrilTstsaroth(id: Int, tile: Tile, spawned: Boolean) : NPC(id, tile, spawned) {
    private val minions: Array<GodWarMinion?> = arrayOfNulls<GodWarMinion>(3)

    init {
        isIntelligentRouteFinder = true
        isIgnoreDocile = true
        setForceAggroDistance(64)
        minions[0] = GodWarMinion(6204, tile.transform(8, 4), true)
        minions[1] = GodWarMinion(6206, tile.transform(-6, 6), true)
        minions[2] = GodWarMinion(6208, tile.transform(-6, -2), true)
    }

    override fun onRespawn() {
        respawnMinions()
    }

    fun respawnMinions() {
        schedule("minionSpawn") {
            wait(2)
            minions.filterNotNull().forEach { minion ->
                if (minion.hasFinished() || minion.isDead)
                    minion.respawn()
            }
        }
    }
}
