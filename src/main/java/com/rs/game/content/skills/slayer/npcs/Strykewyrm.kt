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
package com.rs.game.content.skills.slayer.npcs

import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.content.combat.CombatSpell
import com.rs.game.content.combat.CombatStyle
import com.rs.game.content.skills.slayer.TaskMonster
import com.rs.game.content.skills.slayer.npcs.Strykewyrm.StrykewyrmType
import com.rs.game.content.skills.summoning.Familiar
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.npc.combat.CombatScript.delayHit
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.lib.util.Utils.getRandomInclusive
import com.rs.lib.util.Utils.random
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks

private val STRYKEWYRM_REQUIREMENTS = mapOf(
    9462 to StrykewyrmType(93, TaskMonster.ICE_STRYKEWYRMS),
    9463 to StrykewyrmType(93, TaskMonster.ICE_STRYKEWYRMS),
    9464 to StrykewyrmType(77, TaskMonster.DESERT_STRYKEWYRMS),
    9465 to StrykewyrmType(77, TaskMonster.DESERT_STRYKEWYRMS),
    9466 to StrykewyrmType(73, TaskMonster.JUNGLE_STRYKEWYRMS),
    9467 to StrykewyrmType(73, TaskMonster.JUNGLE_STRYKEWYRMS)
)

@ServerStartupEvent
fun mapStrykewyrms() {
    onNpcClick(9462, 9464, 9466) { event ->
        (event.npc as? Strykewyrm)?.let { handleStomping(event.player, it) }
    }

    instantiateNpc(9462, 9463, 9464, 9465, 9466, 9467) { id, tile -> Strykewyrm(id, tile) }

    npcCombat(9463, 9465, 9467) { npc, target ->
        handleStrykewyrmCombat(npc as Strykewyrm, target)
    }
}

private fun handleStrykewyrmCombat(npc: Strykewyrm, target: Entity): Int {
    val attackStyle = getRandomInclusive(20)

    return when {
        attackStyle <= 7 && npc.inMeleeRange(target) -> {
            npc.performMeleeAttack(target)
            npc.attackSpeed
        }
        attackStyle <= 19 -> {
            npc.performMagicAttack(target)
            npc.attackSpeed
        }
        attackStyle == 20 -> {
            npc.performBurrowAttack(target)
            npc.attackSpeed
        }
        else -> npc.attackSpeed
    }
}

class Strykewyrm(id: Int, tile: Tile?) : NPC(id, tile, false) {
    private val stompId: Int = id

    data class StrykewyrmType(
        val slayerLevel: Int,
        val taskMonster: TaskMonster
    )

    override fun processNPC() {
        super.processNPC()
        if (!isDead && id != stompId && !isCantInteract && !isUnderCombat) {
            transformToBase()
        }
    }

    private fun transformToBase() {
        anim(Animation(12796))
        setCantInteract(true)
        schedule {
            transformIntoNPC(stompId)
            wait(1)
            setCantInteract(false)
        }
    }

    override fun handlePreHit(hit: Hit) {
        val player = when (val source = hit.source) {
            is Player -> source
            is Familiar -> source.owner
            else -> return
        }

        if (!checkSlayerRequirements(player)) {
            hit.setDamage(0)
            capDamage = 0
            return
        }

        if (!player.equipment.hasFireCape() && !player.iceStrykeNoCape()) {
            player.sendMessage("The strykewyrm numbs your hands and freezes your attack.")
            hit.setDamage(0)
            capDamage = 0
            return
        }

        if (id in listOf(9462, 9463)) {
            handleIceStrykewyrmDamage(hit, player)
        }

        if (capDamage == 0) capDamage = -1
        super.handlePreHit(hit)
    }

    private fun checkSlayerRequirements(player: Player): Boolean {
        val requirements = STRYKEWYRM_REQUIREMENTS[id] ?: return true

        if (!player.isOnTask(requirements.taskMonster)) {
            player.sendMessage("You seem to be unable to damage it.")
            return false
        }

        if (player.skills.getLevel(Skills.SLAYER) < requirements.slayerLevel) {
            player.sendMessage("You need a Slayer level of at least ${requirements.slayerLevel} to fight this.")
            return false
        }

        return true
    }

    private fun handleIceStrykewyrmDamage(hit: Hit, player: Player) {
        if (player.equipment.hasFireCape() && hit.damage < 40)
            hit.setDamage(40)

        hit.getData("combatSpell", CombatSpell::class.java)?.takeIf {
            it.isFireSpell
        }?.let {
            hit.setDamage(hit.damage * 2)
        }
    }

    fun performMeleeAttack(target: Entity) {
        anim(combatDefinitions.attackEmote)

        if (id == 9467 && getRandomInclusive(10) == 0) {
            target.spotAnim(2309)
            target.poison.makePoisoned(44)
        }

        delayHit(this, 0, target, Hit.melee(this, CombatScript.getMaxHit(this, combatDefinitions.maxHit, CombatStyle.MAGIC, target)))
    }

    fun performMagicAttack(target: Entity) {
        anim(12794)
        val hit = Hit.magic(this, CombatScript.getMaxHit(this, combatDefinitions.maxHit, CombatStyle.MAGIC, target))
        delayHit(this, 1, target, hit)

        World.sendProjectile(this, target, combatDefinitions.attackProjectile, 41 to 16, 41, 5, 16)

        when (id) {
            9463 -> handleIceStrykewyrmMagic(target, hit)
            9467 -> handleJungleStrykewyrmMagic(target)
        }
    }

    private fun handleIceStrykewyrmMagic(target: Entity, hit: Hit) {
        schedule {
            wait(1)
            if (getRandomInclusive(10) == 0 && !target.hasEffect(Effect.FREEZE)) {
                target.freeze(Ticks.fromSeconds(3))
                target.spotAnim(369)
                if (target is Player) target.stopAll()
            } else if (hit.damage != 0) {
                target.spotAnim(2315)
            }
        }
    }

    private fun handleJungleStrykewyrmMagic(target: Entity) {
        if (getRandomInclusive(10) == 0) {
            target.spotAnim(2313)
            if (random(2) == 0) target.poison.makePoisoned(88)
        }
    }

    fun performBurrowAttack(target: Entity) {
        val tile = Tile.of(target.tile).transform(-1, -1, 0)

        anim(12796)
        setCantInteract(true)
        combat.removeTarget()
        lock()

        schedule {
            transformIntoNPC(stompId)
            setForceWalk(tile)
            wait { !hasForceWalk() }

            transformIntoNPC(stompId + 1)
            anim(12795)

            if (isInBurrowRange(target.x - x, target.y - y, size))
                dealBurrowDamage(target)

            wait(2)
            combat.combatDelay = attackSpeed
            setCombatTarget(target)
            setCantInteract(false)
            unlock()
        }
    }

    private fun isInBurrowRange(distanceX: Int, distanceY: Int, size: Int): Boolean =
        distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1

    private fun dealBurrowDamage(target: Entity) {
        delayHit(this, 0, target, Hit.flat(this, random(100, 300)))

        when (id) {
            9467 -> target.poison.makePoisoned(88)
            9465 -> {
                delayHit(this, 0, target, Hit.flat(this, random(100, 300)))
                target.spotAnim(2311)
            }
        }
    }

    override fun reset() {
        setNPC(stompId)
        super.reset()
    }

    fun performStomp(player: Player) {
        setAttackedBy(player)
        player.anim(4278)
        player.lock(1)
        setCantInteract(true)

        schedule {
            wait(2)
            anim(12795)
            transformIntoNPC(stompId + 1)
            wait(4)
            setCombatTarget(player)
            setAttackedBy(player)
            setCantInteract(false)
        }
    }
}

fun handleStomping(player: Player, npc: Strykewyrm) {
    if (npc.isCantInteract || !player.canAttackMulti(npc)) return

    val requirements = STRYKEWYRM_REQUIREMENTS[npc.id] ?: return

    if (!player.isOnTask(requirements.taskMonster)) {
        player.sendMessage("The mound doesn't respond.")
        return
    }

    if (player.skills.getLevel(Skills.SLAYER) < requirements.slayerLevel) {
        player.sendMessage("You need at least a slayer level of ${requirements.slayerLevel} to fight this.")
        return
    }

    npc.performStomp(player)
}