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
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript.delayHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMagicHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMaxHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMeleeHit
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.player.Player
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.handlers.NPCInstanceHandler
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat
import com.rs.plugin.kts.onButtonClick
import com.rs.utils.WorldUtil
import java.util.ArrayList
import java.util.function.BiFunction

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
        val defs = npc.combatDefinitions
        val size = npc.size
        val possibleTargets = npc.possibleTargets
        var attackStyle = Utils.getRandomInclusive(4)

        if (Utils.getRandomInclusive(3) == 0 && npc.hitpoints < (npc.maxHitpoints / 2) && npc is CorporealBeast)
            npc.spawnDarkEnergyCore()

        //Melee
        if (attackStyle == 0 || attackStyle == 1) {
            val distanceX = target.x - npc.x
            val distanceY = target.y - npc.y
            if ((distanceX <= size) && (distanceX >= -1) && (distanceY <= size) && (distanceY >= -1)) {
                npc.anim(if (attackStyle == 0) defs.attackEmote else 10058)
                delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.maxHit, NPCCombatDefinitions.AttackStyle.MELEE, target)))
                return@npcCombat npc.attackSpeed
            }
            attackStyle = 2 + Utils.getRandomInclusive(2)
        }

        when(attackStyle) {
            //Magic single target
            2 -> {
                npc.anim(10410)
                delayHit(npc, World.sendProjectile(npc, target, 1825, 41 to 16, 10, 15, 16).taskDelay, target, getMagicHit(npc, getMaxHit(npc, 650, NPCCombatDefinitions.AttackStyle.MAGE, target)))
            }

            //Magic drain
            3 -> {
                npc.anim(10410)
                val delay = World.sendProjectile(npc, target, 1823, 41 to 16, 10, 15, 16).taskDelay
                delayHit(npc, delay, target, getMagicHit(npc, getMaxHit(npc, 550, NPCCombatDefinitions.AttackStyle.MAGE, target)))
                if (target is Player) {
                    var skill = Utils.getRandomInclusive(2)
                    skill =
                        if (skill == 0) Constants.MAGIC else (if (skill == 1) Constants.SUMMONING else Constants.PRAYER)
                    if (skill == Constants.PRAYER)
                        target.prayer.drainPrayer((10 + Utils.getRandomInclusive(40)).toDouble())
                    else {
                        var lvl = target.skills.getLevel(skill)
                        lvl -= 1 + Utils.getRandomInclusive(4)
                        target.skills.set(skill, if (lvl < 0) 0 else lvl)
                    }
                    target.sendMessage("Your " + Constants.SKILL_NAME[skill] + " has been slighly drained!")
                }
            }

            //Magic splash
            4 -> {
                npc.anim(10410)
                val tile = Tile.of(target.tile)
                npc.schedule {
                    wait(World.sendProjectile(npc, tile, 1824, 41 to 16, 0, 15, 16).taskDelay)
                    for (i in 0..5) {
                        val newTile = Tile.of(tile, 3)
                        if (!floorAndWallsFree(newTile, 1)) continue
                        for (t in possibleTargets) {
                            if (Utils.getDistance(newTile.x, newTile.y, t.x, t.y) > 1 || !t.lineOfSightTo(newTile, false)) continue
                            delayHit(npc, 0, t, getMagicHit(npc, getMaxHit(npc, 350, NPCCombatDefinitions.AttackStyle.MAGE, t)))
                        }
                        npc.schedule {
                            wait(World.sendProjectile(tile, newTile, 1824, 15 to 5, 0, 15, 30).taskDelay + 1)
                            sendSpotAnim(newTile, SpotAnim(1806))
                        }
                    }
                }
            }
        }
        return@npcCombat npc.attackSpeed
    }

    instantiateNpc(8133) { npcId, tile -> CorporealBeast(npcId, tile, false) }
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
        if (core != null) return
        core = DarkEnergyCore(this)
    }

    fun removeDarkEnergyCore() {
        if (core == null) return
        core!!.finish()
        core = null
    }

    override fun getPossibleTargets(): MutableList<Entity> {
        val targets = super.possibleTargets
        val hittableTargets: MutableList<Entity> = ArrayList<Entity>()
        for (t in targets) if (t.x > 2972) hittableTargets.add(t)
        return hittableTargets
    }

    override fun handlePreHit(hit: Hit) {
        if (hit.look == HitLook.CANNON_DAMAGE && hit.damage > 80) hit.setDamage(80)
        (hit.source as? Player)?.let { player ->
            if (player.equipment.getWeaponId() != -1 && !ItemDefinitions.getDefs(player.equipment.getWeaponId()).getName().contains(" spear") && (hit.look == HitLook.MELEE_DAMAGE || hit.look == HitLook.RANGE_DAMAGE))
                hit.setDamage(hit.damage / 2)
        }
        super.handlePreHit(hit)
    }

    override fun processNPC() {
        super.processNPC()
        if (isDead) return
        if (tickCounter % 3 == 0L) {
            val possibleTargets = getPossibleTargets()
            var stomp = false
            for (t in possibleTargets) if (WorldUtil.isInRange(this, t, -1)) {
                stomp = true
                t.applyHit(Hit(this, Utils.random(150, 513), HitLook.TRUE_DAMAGE), 0)
            }
            if (stomp)
                sync(10496, 1834)
        }
        if (attackedBy != null && lineOfSightTo(attackedBy, false)) setAttackedBy(null)
        val maxhp = maxHitpoints
        if (maxhp > hitpoints && getPossibleTargets().isEmpty() && attackedBy == null) {
            resetLevels()
            hitpoints = maxhp
        }
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        if (core != null) core!!.sendDeath(source)
    }

    override fun getMagePrayerMultiplier(): Double {
        return 0.6
    }
}
