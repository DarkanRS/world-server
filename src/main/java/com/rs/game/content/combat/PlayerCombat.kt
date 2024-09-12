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
package com.rs.game.content.combat

import com.rs.Settings
import com.rs.cache.loaders.Bonus
import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.content.combat.special_attacks.SpecialAttack
import com.rs.game.content.combat.special_attacks.execute
import com.rs.game.content.skills.dungeoneering.DungeonController
import com.rs.game.content.skills.dungeoneering.KinshipPerk
import com.rs.game.content.skills.summoning.Familiar
import com.rs.game.content.skills.summoning.Pouch
import com.rs.game.model.WorldProjectile
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.interactions.PlayerCombatInteraction
import com.rs.game.model.entity.npc.NPC
import com.rs.engine.pathfinder.Direction
import com.rs.game.content.skills.slayer.Slayer
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.model.entity.player.managers.AuraManager.Aura
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.utils.ItemConfig
import com.rs.utils.Ticks
import java.util.*
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

class PlayerCombat(@JvmField val target: Entity) : PlayerAction() {
    override fun start(player: Player): Boolean {
        player.actionManager.forceStop()
        player.faceEntity(target)
        if (!player.controllerManager.canAttack(target)) return false
        if (target is Player) {
            if (!player.isCanPvp || !target.isCanPvp) {
                player.sendMessage("You can only attack players in a player-vs-player area.")
                return false
            }
        }
        if (target is Familiar) {
            if (target === player.familiar) {
                player.sendMessage("You can't attack your own familiar.")
                return false
            }
            if (!target.canAttack(player)) {
                player.sendMessage("You can't attack them.")
                return false
            }
        }
        if (!player.canAttackMulti(target)) return false
        if (!checkAll(player)) return false
        if (target is NPC) player.lastNpcInteractedName = target.definitions.name
        return true
    }

    override fun process(player: Player): Boolean {
        return checkAll(player)
    }

    override fun processWithDelay(player: Player): Int {
        if (target is Player) if ((!target.attackedBy(player.username)) && (!player.attackedBy(target.username))) target.addToAttackedBy(player.username)

        var multiplier = 1.0
        if (player.hasEffect(Effect.MIASMIC_SLOWDOWN)) multiplier = 1.5
        if (!player.controllerManager.keepCombating(target)) return -1
        addAttackedByDelay(player, target)
        player.tempAttribs.setO<Any>("combatTarget", target)

        val spell = player.combatDefinitions.spell
        if (player.tempAttribs.getB("dfsActive")) {
            val shield = player.equipment[Equipment.SHIELD]
            player.faceEntity(target)
            if (shield == null || shield.getMetaDataI("dfsCharges") < 0) {
                player.tempAttribs.setB("dfsActive", false)
                player.sendMessage("Your shield was unable to be activated.")
                return 3
            }
            player.sync(6696, 1165)
            val p = World.sendProjectile(player, target, 1166, delay = 50, speed = 7, angle = 10)
            delayMagicHit(target, p.taskDelay, Hit(player, Utils.random(100, 250), HitLook.TRUE_DAMAGE), { target.spotAnim(1167, 0, 96) }, null, null)
            player.tempAttribs.setB("dfsActive", false)
            player.tempAttribs.setL("dfsCd", World.getServerTicks() + 200)
            shield.addMetaData("dfsCharges", shield.getMetaDataI("dfsCharges") - 1)
            player.combatDefinitions.refreshBonuses()
            return 3
        }
        if (spell == null && usingPolypore(player)) {
            player.faceEntity(target)
            player.sync(15448, 2034)
            drainCharge(player)
            val p = World.sendProjectile(player, target, 2035, 60 to 32, delay = 50, speed = 10)
            val hit = calculateMagicHit(player, target, (5 * player.skills.getLevel(Constants.MAGIC)) - 180, false)
            delayMagicHit(target, p.taskDelay, hit, {
                if (hit.damage > 0) target.spotAnim(2036, 0, 96)
                else {
                    target.spotAnim(85, 0, 96)
                    player.soundEffect(target, 227, true)
                }
            }, null, null)
            return 4
        }
        if (spell != null) {
            if (player.combatDefinitions.isUsingSpecialAttack) return execute(SpecialAttack.Type.MAGIC, player, target)

            val manualCast = player.combatDefinitions.hasManualCastQueued()
            val gloves = player.equipment.getItem(Equipment.HANDS)
            if (gloves != null && gloves.definitions.getName().contains("Spellcaster glove") && player.equipment.weaponId == -1 && Utils.random(20) == 0) player.tempAttribs.setO<Any>("spellcasterProc", spell)
            val delay = mageAttack(player, spell, !manualCast)
            if (player.nextAnimation != null && player.tempAttribs.getO<Any?>("spellcasterProc") != null) {
                player.anim(14339)
                player.tempAttribs.removeO<Any>("spellcasterProc")
            }
            return delay
        } else if (isRanging(player)) {
            val weapon = RangedWeapon.forId(player.equipment.weaponId) ?: return -1
            if (weapon.properAmmo(player, true)) return (rangeAttack(player) * multiplier).toInt()
            player.faceTile(target.tile)
            return -1
        } else return (meleeAttack(player) * multiplier).toInt()
    }

    fun mageAttack(player: Player, spell: CombatSpell?, autoCast: Boolean): Int {
        if (!autoCast) {
            player.combatDefinitions.resetSpells(false)
            player.stopAll(false)
        }
        if (spell == null) return -1
        if (!spell.canCast(player, target)) {
            if (autoCast) player.combatDefinitions.resetSpells(true)
            return -1
        }
        when (player.equipment.weaponId) {
            15486, 15502, 22207, 22209, 22211, 22213 -> if (Utils.random(8) != 0) spell.runeSet.deleteRunes(player)
            else player.sendMessage("Your spell draws its power completely from your weapon.", true)

            else -> spell.runeSet.deleteRunes(player)
        }
        val delay = spell.cast(player, target)
        val baseDamage = spell.getBaseDamage(player)
        player.skills.addXp(Constants.MAGIC, spell.splashXp)
        if (baseDamage < 0) {
            val hit = calculateMagicHit(player, target, 1000)
            if (hit.damage > 0) spell.onHit(player, target, hit)
            target.tasks.schedule(delay) {
                if (hit.damage > 0) {
                    target.spotAnim(spell.hitSpotAnim)
                    if (spell.landSound != -1) player.soundEffect(target, spell.landSound, true)
                } else {
                    target.spotAnim(85, 0, 96)
                    if (spell.splashSound != -1) player.soundEffect(target, spell.splashSound, true)
                    else player.soundEffect(target, 227, true)
                }
            }
        } else {
            val hit = castSpellAtTarget(player, target, spell, delay)
            if (spell.isAOE && hit) attackTarget(getMultiAttackTargets(player, target, 1, 9, false)) { nextTarget ->
                castSpellAtTarget(player, nextTarget, spell, delay)
                return@attackTarget true
            }
        }
        return spell.getCombatDelay(player)
    }

    fun castSpellAtTarget(player: Player, target: Entity, spell: CombatSpell, hitDelay: Int): Boolean {
        val hit = calculateMagicHit(player, target, spell.getBaseDamage(player))
        if (spell === CombatSpell.STORM_OF_ARMADYL && hit.damage > 0) {
            var minHit = (player.skills.getLevel(Constants.MAGIC) - 77) * 5
            minHit = (minHit * getMagicBonusBoost(player)).toInt()
            if (hit.damage < minHit) hit.setDamage(hit.damage + minHit)
        }
        hit.setData("combatSpell", spell)
        val sparkle = target.size >= 2 || target.hasEffect(Effect.FREEZE) || target.hasEffect(Effect.FREEZE_BLOCK)
        delayMagicHit(target, hitDelay, hit, {
            if (hit.damage > 0) when (spell) {
                CombatSpell.ICE_RUSH, CombatSpell.ICE_BURST, CombatSpell.ICE_BLITZ, CombatSpell.ICE_BARRAGE -> {
                    if (sparkle) target.spotAnim(1677, 0, 96)
                    else target.spotAnim(spell.hitSpotAnim)
                    if (spell.landSound != -1) player.soundEffect(target, spell.landSound, true)
                }

                else -> {
                    target.spotAnim(spell.hitSpotAnim)
                    if (spell.landSound != -1) player.soundEffect(target, spell.landSound, true)
                }
            } else {
                target.spotAnim(85, 0, 96)
                if (spell.splashSound != -1) player.soundEffect(target, spell.splashSound, true)
                else player.soundEffect(target, 227, true)
            }
        }, { spell.onHit(player, target, hit) }, null)
        return hit.damage > 0
    }

    private fun rangeAttack(player: Player): Int {
        val weaponId = player.equipment.weaponId
        val weapon = RangedWeapon.forId(weaponId) ?: return -1

        val attackStyle = player.combatDefinitions.getAttackStyle()
        val weaponConfig = ItemConfig.get(weaponId)
        var soundId = weaponConfig.getAttackSound(attackStyle.index)
        val ammo = AmmoType.forId(player.equipment.ammoId)
        var combatDelay = getRangeCombatDelay(weaponId, attackStyle)

        if (player.combatDefinitions.isUsingSpecialAttack) return execute(SpecialAttack.Type.RANGE, player, target)
        val p = weapon.sendProjectile(player, target, combatDelay, player.equipment.ammoId)
        when (weapon) {
            RangedWeapon.DEATHTOUCHED_DART -> {
                player.anim(weaponConfig.getAttackAnim(0))
                target.spotAnim(44)
                target.resetWalkSteps()
                if (target is NPC) {
                    target.tasks.schedule(p.taskDelay) {
                        target.capDamage = -1
                        target.applyHit(Hit(player, target.hitpoints, HitLook.TRUE_DAMAGE))
                    }
                    dropAmmo(player, target, Equipment.WEAPON, 1)
                    return 8
                } else return 0
            }

            RangedWeapon.CHINCHOMPA, RangedWeapon.RED_CHINCHOMPA -> { //TODO validate the logic here
                attackTarget(getMultiAttackTargets(player, target)) { nextTarget ->
                    val hit = calculateHit(player, nextTarget, weaponId, attackStyle, CombatStyle.RANGE, true, 1.0, if (weaponId == 10034) 1.2 else 1.0)
                    player.anim(2779)
                    nextTarget.tasks.schedule(p.taskDelay) { nextTarget.spotAnim(2739, 0, 96 shl 16) }
                    delayHit(nextTarget, p.taskDelay, weaponId, attackStyle, hit)
                    return@attackTarget hit.damage > 0
                }
                dropAmmo(player, target, Equipment.WEAPON, 1)
            }

            RangedWeapon.SWAMP_LIZARD, RangedWeapon.ORANGE_SALAMANDER, RangedWeapon.RED_SALAMANDER, RangedWeapon.BLACK_SALAMANDER -> {
                val hit = when (attackStyle.name) {
                    "Flare" -> calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE).setLook(HitLook.RANGE_DAMAGE)
                    "Blaze" -> calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE).setLook(HitLook.MAGIC_DAMAGE)
                    else -> calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE).setLook(HitLook.MELEE_DAMAGE)
                }
                delayHit(target, p.taskDelay, weaponId, attackStyle, hit)
                dropAmmo(player, target, Equipment.AMMO, 1)
                if (attackStyle.name == "Flare") combatDelay = 3
            }

            RangedWeapon.CROSSBOW, RangedWeapon.BRONZE_CROSSBOW, RangedWeapon.BLURITE_CROSSBOW, RangedWeapon.IRON_CROSSBOW, RangedWeapon.STEEL_CROSSBOW, RangedWeapon.BLACK_CROSSBOW, RangedWeapon.MITH_CROSSBOW, RangedWeapon.ADAMANT_CROSSBOW, RangedWeapon.RUNE_CROSSBOW, RangedWeapon.ARMADYL_CROSSBOW, RangedWeapon.CHAOTIC_CROSSBOW, RangedWeapon.ZANIKS_CROSSBOW -> {
                val hit: Hit
                var specced = false
                if (player.equipment.ammoId == 9241 && Utils.random(100) <= 55 && !target.poison.isPoisoned) {
                    target.spotAnim(752)
                    target.poison.makePoisoned(50)
                    specced = true
                }
                if (player.equipment.ammoId != -1 && Utils.getRandomInclusive(10) == 0) {
                    when (player.equipment.ammoId) {
                        9237 -> {
                            hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                            target.spotAnim(755)
                            if (target is Player) target.stopAll()
                            else if (target is NPC) target.combatTarget = null
                            soundId = 2914
                        }

                        9242 -> {
                            hit = Hit.range(player, (target.hitpoints * 0.2).toInt())
                            target.spotAnim(754)
                            player.applyHit(Hit(target, if (player.hitpoints > 20) (player.hitpoints * 0.1).toInt() else 1, HitLook.REFLECTED_DAMAGE))
                            soundId = 2912
                        }

                        9243 -> {
                            hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE, false, 1.0, 1.15)
                            target.spotAnim(758)
                            soundId = 2913
                        }

                        9244 -> {
                            hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE, false, 1.0, if (getAntifireLevel(target, true) > 0) 1.45 else 1.0)
                            target.spotAnim(756)
                            soundId = 2915
                        }

                        9245 -> {
                            hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE, false, 1.0, 1.15)
                            target.spotAnim(753)
                            player.heal((player.maxHitpoints * 0.25).toInt())
                            soundId = 2917
                        }

                        else -> hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                    }
                    specced = true
                } else {
                    hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                    checkSwiftGlovesEffect(player, p.taskDelay, attackStyle, weaponId, hit, p)
                }
                delayHit(target, p.taskDelay, weaponId, attackStyle, hit)
                if (specced) player.equipment.removeAmmo(Equipment.AMMO, 1)
                else dropAmmo(player, target, Equipment.AMMO, 1)
            }

            RangedWeapon.ROYAL_CROSSBOW -> {
                if (target is Player) player.sendMessage("The Royal crossbow seems unresponsive against this target.", true)
                else {
                    var stacks = player.tempAttribs.getI("rcbStacks", 0)
                    if (World.getServerTicks() < player.tempAttribs.getL("rcbLockOnTimer")) stacks++
                    else {
                        stacks = 1
                        player.sendMessage("Your crossbow loses focus on your target.")
                    }
                    if (stacks == 9) player.sendMessage("Your crossbow locks onto your target.")
                    val lockedOn = stacks >= 9
                    player.tempAttribs.setI("rcbStacks", stacks)
                    player.tempAttribs.setL("rcbLockOnTimer", World.getServerTicks() + 28)
                    val hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                    delayHit(target, p.taskDelay, weaponId, attackStyle, hit, null, {
                        if (weaponId == 24339 || target.isDead || target.hasFinished()) return@delayHit
                        val maxHit = getMaxHit(player, target, weaponId, attackStyle, CombatStyle.RANGE, 1.0)
                        val minBleed = (maxHit * (if (lockedOn) 0.25 else 0.20)).toInt()
                        val maxBleed = (minBleed * 0.70).toInt()
                        target.tasks.schedule(14) { delayHit(target, 0, weaponId, attackStyle, Hit.range(player, Utils.random(minBleed, maxBleed))) }
                        target.tasks.schedule(28) { delayHit(target, 0, weaponId, attackStyle, Hit.range(player, Utils.random(minBleed, maxBleed))) }
                    }, null)
                    checkSwiftGlovesEffect(player, p.taskDelay, attackStyle, weaponId, hit, p)
                }
                dropAmmo(player, target, Equipment.AMMO, 1)
            }

            RangedWeapon.HAND_CANNON -> {
                if (Utils.getRandomInclusive(player.skills.getLevel(Constants.FIREMAKING) shl 1) == 0) {
                    player.sync(12175, 2140)
                    player.equipment.deleteSlot(Equipment.WEAPON)
                    player.appearance.generateAppearanceData()
                    player.applyHit(Hit(player, Utils.getRandomInclusive(150) + 10, HitLook.TRUE_DAMAGE))
                    return combatDelay
                }
                delayHit(target, p.taskDelay, weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE))
                dropAmmo(player, target, Equipment.AMMO, 1)
            }

            RangedWeapon.SAGAIE -> {
                val damageMod = Utils.clampD((Utils.getDistanceI(player.tile, target.middleTile) / getAttackRange(player).toDouble()) * 0.70, 0.01, 1.0)
                val hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE, true, 1.0 - (damageMod * 0.95), 1.0 + damageMod)
                delayHit(target, p.taskDelay, weaponId, attackStyle, hit)
                checkSwiftGlovesEffect(player, p.taskDelay, attackStyle, weaponId, hit, p)
                dropAmmo(player, target, Equipment.WEAPON, 1)
            }

            RangedWeapon.BOLAS -> {
                var delay = Ticks.fromSeconds(15)
                if (target is Player) {
                    val t = target
                    var slashBased = t.equipment.getItem(3) != null
                    val slash = t.combatDefinitions.getBonus(Bonus.SLASH_ATT)
                    for (i in Bonus.STAB_ATT.ordinal..Bonus.RANGE_ATT.ordinal) if (t.combatDefinitions.getBonus(Bonus.entries[i]) > slash) {
                        slashBased = false
                        break
                    }
                    if (t.inventory.containsItem(946, 1) || slashBased) delay /= 2
                    if (t.prayer.isProtectingRange) delay /= 2
                    if (delay < Ticks.fromSeconds(5)) delay = Ticks.fromSeconds(5)
                }
                if (calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE).damage > 0) {
                    target.freeze(delay, true)
                    target.tasks.schedule(2) { target.spotAnim(469, 0, 96) }
                }
                player.soundEffect(target, soundId, true)
                player.equipment.removeAmmo(Equipment.WEAPON, 1)
            }

            RangedWeapon.DARK_BOW -> {
                val hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                delayHit(target, p.taskDelay, weaponId, attackStyle, hit)
                checkSwiftGlovesEffect(player, p.taskDelay, attackStyle, weaponId, hit, p)
                val p2 = AmmoType.forId(player.equipment.ammoId)?.let { World.sendProjectile(player, target, it.getProjAnim(player.equipment.ammoId), delay = 30, speed = 5, angle = 15) }
                delayHit(target, p2?.taskDelay ?: 1, weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE))
                dropAmmo(player, target, Equipment.AMMO, 2)
            }

            else -> {
                if (weapon.isThrown) {
                    val hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                    delayHit(target, p.taskDelay, weaponId, attackStyle, hit)
                    checkSwiftGlovesEffect(player, p.taskDelay, attackStyle, weaponId, hit, p)
                    dropAmmo(player, target, Equipment.WEAPON, 1)
                } else {
                    val hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE)
                    delayHit(target, p.taskDelay, weaponId, attackStyle, hit)
                    checkSwiftGlovesEffect(player, p.taskDelay, attackStyle, weaponId, hit, p)
                    if (weapon.ammos != null) dropAmmo(player, target)
                }
            }
        }
        player.anim(weaponConfig.getAttackAnim(attackStyle.index))
        val attackSpotAnim = weapon.getAttackSpotAnim(player.equipment.ammoId)
        if (attackSpotAnim != null) player.spotAnim(attackSpotAnim)
        player.soundEffect(target, soundId, true)
        return combatDelay
    }

    private fun checkSwiftGlovesEffect(player: Player, hitDelay: Int, attackStyle: AttackStyle, weaponId: Int, hit: Hit, p: WorldProjectile) {
        val gloves = player.equipment.getItem(Equipment.HANDS)
        if (gloves == null || !gloves.definitions.getName().contains("Swift glove")) return
        if (hit.damage != 0 && hit.damage < ((hit.maxHit / 3) * 2) || Random().nextInt(3) != 0) return
        player.sendMessage("You fired an extra shot.")
        World.sendProjectileAbsoluteSpeed(player, target, p.spotAnimId, p.startHeight - 5 to p.endHeight - 5, p.startDelayClientCycles, p.inAirClientCycles, p.angle)
        delayHit(target, hitDelay, weaponId, attackStyle, calculateHit(player, target, weaponId, attackStyle, CombatStyle.RANGE))
        if (hit.damage > (hit.maxHit - 10)) {
            target.freeze(Ticks.fromSeconds(10), false)
            target.spotAnim(181, 0, 96)
        }
    }

    @Suppress("unused")
    private fun getRangeHitDelay(player: Player): Int {
        return if (Utils.getDistance(player.x, player.y, target.x, target.y) >= 5) 2 else 1
    }

    private fun meleeAttack(player: Player): Int {
        if (player.hasEffect(Effect.FREEZE) && target.size == 1) {
            val dir = Direction.forDelta(target.x - player.x, target.y - player.y)
            if (dir != null) when (dir) {
                Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST -> {}
                else -> return 0
            }
        }
        var weaponId = player.equipment.weaponId
        val attackStyle = player.combatDefinitions.getAttackStyle()
        val weaponConfig = ItemConfig.get(weaponId)
        val combatDelay = getMeleeCombatDelay(weaponId)
        val soundId = weaponConfig.getAttackSound(attackStyle.index)
        if (weaponId == -1) {
            val gloves = player.equipment.getItem(Equipment.HANDS)
            if (gloves != null && gloves.definitions.getName().contains("Goliath gloves")) weaponId = -2
        }

        if (player.combatDefinitions.isUsingSpecialAttack) return execute(SpecialAttack.Type.MELEE, player, target)

        val hit = calculateHit(player, target, weaponId, attackStyle, CombatStyle.MELEE)

        if (weaponId == -2 && hit.damage <= 0) {
            if (Utils.random(10) == 0) {
                player.anim(14417)
                attackTarget(getMultiAttackTargets(player, target, 6, 10, true)) { nextTarget ->
                    nextTarget.freeze(Ticks.fromSeconds(10), true)
                    nextTarget.spotAnim(181, 0, 96)
                    nextTarget.tasks.schedule(1) { nextTarget.applyHit(calculateHit(player, nextTarget, -2, attackStyle, CombatStyle.MELEE, false, 1.0, 1.0).setLook(HitLook.TRUE_DAMAGE)) }
                    for (skill in Skills.COMBAT) nextTarget.lowerStat(skill, 7, 0.0)
                    if (nextTarget is Player) nextTarget.sendMessage("Your stats have been drained!")
                    return@attackTarget true
                }
                return combatDelay
            }
        }
        delayNormalHit(target, weaponId, attackStyle, hit)
        player.anim(getWeaponAttackEmote(weaponId, attackStyle))
        player.soundEffect(target, soundId, true)
        return combatDelay
    }

    override fun stop(player: Player) {
        player.stopFaceEntity()
        player.actionManager.forceStop()
        player.tempAttribs.removeO<Any>("combatTarget")
    }

    fun checkAll(player: Player): Boolean {
        if (target.isDead) return false
        if (!player.canAttackMulti(target) || !target.canAttackMulti(player)) return false
        if (target is Player) {
            if (!player.isCanPvp || !target.isCanPvp) return false
        } else (target as? NPC)?.let { npc ->
            if (npc.isCantInteract) return false
            if (npc is Familiar) {
                if (!npc.canAttack(target)) return false
            } else if (isAttackExeption(player, npc)) return false
        }
        if (player.hasEffect(Effect.STAFF_OF_LIGHT_SPEC) && !(player.equipment.weaponId == 15486 || player.equipment.weaponId == 22207 || player.equipment.weaponId == 22209 || player.equipment.weaponId == 22211 || player.equipment.weaponId == 22213)) player.removeEffect(Effect.STAFF_OF_LIGHT_SPEC)
        player.tempAttribs.setO<Any>("last_target", target)
        target.tempAttribs.setO<Any>("last_attacker", player)
        return true
    }

    private fun isAttackExeption(player: Player, n: NPC): Boolean {
        return !n.canBeAttackedBy(player)
    }
}

fun attackTarget(targets: Set<Entity>, perform: (Entity) -> Boolean) {
    for (target in targets) {
        if (!perform(target)) break
    }
}

fun addAttackedByDelay(player: Entity, target: Entity) {
    target.attackedBy = player
    target.setAttackedByDelay(System.currentTimeMillis() + 8000) // 8seconds
}

fun getRangeCombatDelay(weaponId: Int, attackStyle: AttackStyle): Int {
    var delay = ItemConfig.get(weaponId).attackDelay
    if (attackStyle.attackType == AttackType.RAPID) delay--
    else if (attackStyle.attackType == AttackType.LONG_RANGE) delay++
    return delay - 1
}

fun getMultiAttackTargets(entity: Entity, target: Entity): MutableSet<Entity> {
    return getMultiAttackTargets(entity, target, 1, 9)
}

fun getMultiAttackTargets(entity: Entity, tile: Tile?, maxDistance: Int, maxAmtTargets: Int): Array<Entity> {
    val possibleTargets: MutableList<Entity> = ArrayList()
    if (!entity.isAtMultiArea) {
        val target = entity.tempAttribs.getO<Entity>("last_target")
        if (target != null && !target.isDead && !target.hasFinished() && target.withinDistance(tile, maxDistance) && (target !is NPC || target.definitions.hasAttackOption())) possibleTargets.add(target)
        return possibleTargets.toTypedArray<Entity>()
    }

    for (p2 in entity.queryNearbyPlayersByTileRange(maxDistance) { p2: Player -> p2 !== entity && !p2.isDead && p2.isCanPvp && p2.isAtMultiArea && p2.withinDistance(tile, maxDistance) && (entity is Player && entity.controllerManager.canHit(p2)) }) {
        possibleTargets.add(p2)
        if (possibleTargets.size >= maxAmtTargets) break
    }
    if (possibleTargets.size < maxAmtTargets) {
        for (n in entity.queryNearbyNPCsByTileRange(maxDistance) { n: NPC -> (entity !is Player || n !== entity.familiar) && !n.isDead && n.definitions.hasAttackOption() && n.isAtMultiArea && n.withinDistance(tile, maxDistance) && (entity !is Player || entity.controllerManager.canHit(n)) }) {
            possibleTargets.add(n)
            if (possibleTargets.size >= maxAmtTargets) break
        }
    }
    return possibleTargets.toTypedArray<Entity>()
}

fun getMultiAttackTargets(entity: Entity, target: Entity, maxDistance: Int, maxAmtTargets: Int): MutableSet<Entity> {
    return getMultiAttackTargets(entity, target, maxDistance, maxAmtTargets, true)
}

fun getMultiAttackTargets(entity: Entity, target: Entity, maxDistance: Int, maxAmtTargets: Int, includeOriginalTarget: Boolean): MutableSet<Entity> {
    val possibleTargets: MutableList<Entity> = ArrayList()
    if (includeOriginalTarget)
        possibleTargets.add(target)
    if (!target.isAtMultiArea) return possibleTargets.toMutableSet()
    for (p2 in target.queryNearbyPlayersByTileRange(maxDistance) { p2: Player -> p2 !== entity && !p2.isDead && p2.isCanPvp && p2.isAtMultiArea && p2.withinDistance(target.tile, maxDistance) && (entity is Player && entity.controllerManager.canHit(p2)) }) {
        possibleTargets.add(p2)
        if (possibleTargets.size >= maxAmtTargets) break
    }
    if (possibleTargets.size < maxAmtTargets) {
        for (n in target.queryNearbyNPCsByTileRange(maxDistance) { n: NPC -> (entity !is Player || n !== entity.familiar) && !n.isDead && n.definitions.hasAttackOption() && n.isAtMultiArea && n.withinDistance(target.tile, maxDistance) && (entity !is Player || entity.controllerManager.canHit(n)) }) {
            possibleTargets.add(n)
            if (possibleTargets.size >= maxAmtTargets) break
        }
    }
    if (!includeOriginalTarget)
        possibleTargets.remove(target)
    return possibleTargets.toMutableSet()
}

fun getRangeCombatDelay(player: Player): Int {
    return getRangeCombatDelay(player.equipment.weaponId, player.combatDefinitions.getAttackStyle())
}

@JvmOverloads
fun dropAmmo(player: Player, target: Entity, slot: Int = Equipment.AMMO, quantity: Int = 1) {
    if (player.equipment.getItem(slot) == null) return
    val ammoId = player.equipment.getItem(slot).id
    when (ammoId) {
        15243, 25202, 10033, 10034, 19152, 19157, 19162, 10142, 10143, 10144, 10145 -> {
            player.equipment.removeAmmo(slot, quantity) //delete 100% of the time
            return
        }
    }
    when (player.equipment.capeId) {
        10498 -> {
            if (Utils.random(3) != 0) return
        }

        10499, 20068, 20769, 20771, 14641, 14642 -> {
            if (Utils.random(9) != 0) return
        }
    }
    player.equipment.removeAmmo(slot, quantity)
    if (Utils.random(5) == 0) //1/5 chance to just break the ammo entirely
        return
    World.addGroundItem(Item(ammoId, quantity), Tile.of(target.getCoordFaceX(target.size), target.getCoordFaceY(target.size), target.plane), player)
}

fun delayNormalHit(target: Entity, hit: Hit) {
    delayNormalHit(target, if (hit.source is Player) (hit.source as Player).equipment.weaponId else -1, if (hit.source is Player) (hit.source as Player).combatDefinitions.getAttackStyle() else null, hit, null, null, null)
}

@JvmOverloads
fun delayNormalHit(target: Entity, weaponId: Int, attackStyle: AttackStyle?, hit: Hit, afterDelay: Runnable? = null, hitSucc: Runnable? = null, hitFail: Runnable? = null) {
    delayHit(target, 0, weaponId, attackStyle, hit, afterDelay, hitSucc, hitFail)
}

fun delayMagicHit(target: Entity, delay: Int, hit: Hit, afterDelay: Runnable?, hitSucc: Runnable?, hitFail: Runnable?) {
    delayHit(target, delay, -1, null, hit, afterDelay, hitSucc, hitFail)
}

fun delayHit(target: Entity, delay: Int, hit: Hit) {
    delayHit(target, delay, if (hit.source is Player) (hit.source as Player).equipment.weaponId else -1, if (hit.source is Player) (hit.source as Player).combatDefinitions.getAttackStyle() else null, hit, null, null, null)
}

@JvmOverloads
fun delayHit(target: Entity, delay: Int, weaponId: Int, attackStyle: AttackStyle?, hit: Hit, afterDelay: Runnable? = null, hitSucc: Runnable? = null, hitFail: Runnable? = null) {
    val source = hit.source
    source?.let { addAttackedByDelay(source, target) }
    target.applyHit(hit, delay) {
        afterDelay?.run()
        val defenceEmote = getDefenceEmote(target)
        if (defenceEmote != -1)
            target.setNextAnimationNoPriority(Animation(getDefenceEmote(target)))
        if (target is NPC) target.soundEffect(source, target.combatDefinitions.defendSound, true)
        if (target is Player) {
            target.closeInterfaces()
            if (!target.isLocked && target.combatDefinitions.isAutoRetaliate && !target.actionManager.hasSkillWorking() && target.interactionManager.interaction == null && !target.hasWalkSteps()) target.interactionManager.setInteraction(PlayerCombatInteraction(target, source))
        } else (target as? NPC)?.let { npc ->
            if (!npc.isUnderCombat || npc.canBeAutoRetaliated())
                npc.combatTarget = source
        }
    }
    val damage = min(hit.damage.toDouble(), target.hitpoints.toDouble()).toInt()
    if (hit.maxHit > 0 && (damage >= hit.maxHit * 0.90) && (hit.look == HitLook.MAGIC_DAMAGE || hit.look == HitLook.RANGE_DAMAGE || hit.look == HitLook.MELEE_DAMAGE)) hit.setCriticalMark()
    if (damage > 0) {
        hitSucc?.run()
    } else hitFail?.run()
    (source as? Player)?.let { addXp(source, target, attackStyle?.xpType, hit) }

    // If player equips a (cross)bow, check poison on ammo rather than weapon.
    // This also prevents (the wrong) poison applying when using a melee or other weapon type with poison ammo equipped.
    var ammoId = -1
    if (weaponId != -1) {
        (source as? Player)?.let {
            val weaponName = ItemDefinitions.getDefs(weaponId).getName().lowercase()
            if (weaponName.contains("longbow") || weaponName.contains("shortbow") || weaponName.contains("crossbow"))
                ammoId = source.equipment.ammoId
        }
    }

    if (ammoId != -1)
        checkPoison(target, ammoId, hit)
    else
        checkPoison(target, weaponId, hit)
}

fun calculatePoisonStartDamage(weaponId: Int, hit: Hit, poisonLevel: Int): Int {
    // note that dungeoneering poison differences only matter for ammo and not melee weapons
    return when (poisonLevel) {
        // (p) or weak weapon poison (dung)
        0 -> {
            when (hit.look) {
                HitLook.RANGE_DAMAGE -> if (AmmoType.isDungAmmo(weaponId)) 48 else 28
                HitLook.MELEE_DAMAGE -> 48
                else -> 0
            }
        }
        // (p+) or weapon poison (dung)
        1 -> {
            when (hit.look) {
                HitLook.RANGE_DAMAGE -> if (AmmoType.isDungAmmo(weaponId)) 58 else 38
                HitLook.MELEE_DAMAGE -> 58
                else -> 0
            }
        }
        // (p++) or strong weapon poison (dung)
        2 -> {
            when (hit.look) {
                HitLook.RANGE_DAMAGE -> if (AmmoType.isDungAmmo(weaponId)) 68 else 58
                HitLook.MELEE_DAMAGE -> 68
                else -> 0
            }
        }
        else -> 0
    }
}

fun checkPoison(target: Entity, weaponId: Int, hit: Hit) {
    if (weaponId == -1) return

    val name = ItemDefinitions.getDefs(weaponId).getName()
    val poisonLevel = when {
        name.contains("(p++)") -> 2
        name.contains("(p+)") -> 1
        name.contains("(p)") -> 0
        else -> -1
    }
    if (poisonLevel == -1) return

    val poisonHit = Utils.getRandomInclusive(8) == 0
    if (!poisonHit) return

    if ((hit.look == HitLook.RANGE_DAMAGE || hit.look == HitLook.MELEE_DAMAGE) && hit.damage > 0)
        target.poison.makePoisoned(calculatePoisonStartDamage(weaponId, hit, poisonLevel))
}

fun addXpFamiliar(player: Player, target: Entity, xpType: XPType, hit: Hit) {
    if (hit.look != HitLook.MAGIC_DAMAGE && hit.look != HitLook.RANGE_DAMAGE && hit.look != HitLook.MELEE_DAMAGE) return
    val combatXp: Double
    val damage = Utils.clampI(hit.damage, 0, target.hitpoints)
    val hpXp = (damage / 7.5)
    if (hpXp > 0) player.skills.addXp(Constants.HITPOINTS, hpXp)
    when (xpType) {
        XPType.ACCURATE -> {
            combatXp = (damage / 2.5)
            player.skills.addXp(Constants.ATTACK, combatXp)
        }

        XPType.AGGRESSIVE -> {
            combatXp = (damage / 2.5)
            player.skills.addXp(Constants.STRENGTH, combatXp)
        }

        XPType.CONTROLLED -> {
            combatXp = (damage / 2.5)
            player.skills.addXp(Constants.ATTACK, combatXp / 3)
            player.skills.addXp(Constants.STRENGTH, combatXp / 3)
            player.skills.addXp(Constants.DEFENSE, combatXp / 3)
        }

        XPType.DEFENSIVE -> {
            combatXp = (damage / 2.5)
            player.skills.addXp(Constants.DEFENSE, combatXp)
        }

        XPType.MAGIC -> {
            combatXp = (damage / 2.5)
            if (combatXp > 0) player.skills.addXp(Constants.MAGIC, combatXp)
        }

        XPType.RANGED, XPType.RANGED_DEFENSIVE -> {
            combatXp = (damage / 2.5)
            if (xpType == XPType.RANGED_DEFENSIVE) {
                player.skills.addXp(Constants.RANGE, combatXp / 2)
                player.skills.addXp(Constants.DEFENSE, combatXp / 2)
            } else player.skills.addXp(Constants.RANGE, combatXp)
        }

        XPType.PRAYER -> {
            combatXp = (damage / 10.0)
            player.skills.addXp(Constants.PRAYER, combatXp)
        }

    }
}

fun addXp(player: Player, target: Entity, xpType: XPType?, hit: Hit) {
    var combatXp: Double
    val damage = Utils.clampI(hit.damage, 0, target.hitpoints)
    when (hit.look) {
        HitLook.MAGIC_DAMAGE -> {
            combatXp = (damage / 5.0)
            if (combatXp > 0) {
                if (player.combatDefinitions.isDefensiveCasting || (usingPolypore(player) && player.combatDefinitions.getAttackStyle().attackType == AttackType.POLYPORE_LONGRANGE)) {
                    val defenceXp = (damage / 7.5)
                    if (defenceXp > 0.0) {
                        combatXp -= defenceXp
                        player.skills.addXp(Constants.DEFENSE, defenceXp)
                    }
                }
                if (combatXp > 0.0) player.skills.addXp(Constants.MAGIC, combatXp)
                //				double hpXp = (hit.getDamage() / 7.5);
                //				if (hpXp > 0)
                //					player.getSkills().addXp(Constants.HITPOINTS, hpXp);
            }
        }

        HitLook.MELEE_DAMAGE -> {
            combatXp = (damage / 2.5)
            when (xpType) {
                XPType.ACCURATE -> player.skills.addXp(Constants.ATTACK, combatXp)
                XPType.AGGRESSIVE -> player.skills.addXp(Constants.STRENGTH, combatXp)
                XPType.CONTROLLED -> {
                    player.skills.addXp(Constants.ATTACK, combatXp / 3)
                    player.skills.addXp(Constants.STRENGTH, combatXp / 3)
                    player.skills.addXp(Constants.DEFENSE, combatXp / 3)
                }

                XPType.DEFENSIVE -> player.skills.addXp(Constants.DEFENSE, combatXp)
                else -> {}
            }
        }

        HitLook.RANGE_DAMAGE -> {
            combatXp = (damage / 2.5)
            if (xpType == XPType.RANGED_DEFENSIVE) {
                player.skills.addXp(Constants.RANGE, combatXp / 2)
                player.skills.addXp(Constants.DEFENSE, combatXp / 2)
            } else player.skills.addXp(Constants.RANGE, combatXp)
        }

        else -> {}
    }
    val hpXp = (damage / 7.5)
    if (hpXp > 0) player.skills.addXp(Constants.HITPOINTS, hpXp)
}

fun getWeaponAttackEmote(weaponId: Int, attackStyle: AttackStyle): Int {
    if (weaponId == -1) return if (attackStyle.index == 1) 423 else 422
    if (weaponId == -2) return if (attackStyle.index == 1) 14307 else 14393
    return ItemConfig.get(weaponId).getAttackAnim(attackStyle.index)
}

fun getMeleeCombatDelay(weaponId: Int): Int {
    if (weaponId != -1) return ItemConfig.get(weaponId).attackDelay
    return 3
}

fun isRanging(player: Player): Boolean {
    val weaponId = player.equipment.weaponId
    if (player.tempAttribs.getB("dfsActive") || (player.combatDefinitions.spell == null && usingPolypore(player))) return true
    if (weaponId == -1 && player.combatDefinitions.spell == null) return false
    return RangedWeapon.forId(weaponId) != null
}

fun isMeleeing(player: Player): Boolean {
    return !isRanging(player) && player.combatDefinitions.spell == null
}

fun isMaging(player: Player): Boolean {
    return !isMeleeing(player) && player.combatDefinitions.spell != null
}

fun getAttackRange(player: Player): Int {
    if (player.combatDefinitions.spell != null) return 10
    if (isRanging(player)) {
        if (player.tempAttribs.getB("dfsActive")) return 8
        var atkRange = ItemConfig.get(player.equipment.weaponId).attackRange
        if (player.combatDefinitions.getAttackStyle().attackType == AttackType.LONG_RANGE) atkRange += 2
        return Utils.clampI(atkRange, 0, 10)
    }
    return ItemConfig.get(player.equipment.weaponId).attackRange
}

fun chargeDragonfireShield(target: Entity) {
    if (target is Player) {
        val p = target
        val shield = p.equipment.shieldId
        if (shield == 11283 || shield == 11284) {
            if (shield == 11284) {
                p.equipment.replace(p.equipment.getItem(Equipment.SHIELD), Item(11283, 1).addMetaData("dfsCharges", 1))
                p.appearance.generateAppearanceData()
                p.equipment.refresh(Equipment.SHIELD)
            }
            val dfs = p.equipment.getItem(Equipment.SHIELD)
            if (dfs != null) {
                val charges = dfs.getMetaDataI("dfsCharges")
                if (charges < 50) {
                    p.equipment.getItem(Equipment.SHIELD).addMetaData("dfsCharges", charges + 1)
                    p.sync(6695, 1164)
                    p.sendMessage("Your shield becomes a little stronger as it absorbs the dragonfire.", true)
                    p.soundEffect(3740, true)
                    p.combatDefinitions.refreshBonuses()
                }
            }
        }
    }
}

fun getDefenceEmote(target: Entity): Int = when (target) {
    is NPC -> target.combatDefinitions.defenceEmote
    is Player -> ItemConfig.get(target.equipment.shieldId)?.defendAnim?.takeIf { it > 0 } ?: ItemConfig.get(target.equipment.weaponId)?.defendAnim?.takeIf { it > 0 } ?: 424
    else -> 424
}

fun getAntifireLevel(target: Entity?, prayerWorks: Boolean): Int {
    if (target !is Player) return 0
    var protection = 0
    if (target.hasEffect(Effect.SUPER_ANTIFIRE)) {
        target.sendMessage("Your potion heavily protects you from the dragon's fire.", true)
        protection = 2
        chargeDragonfireShield(target)
        return protection
    }
    val shieldId = target.equipment.shieldId
    if (shieldId == 1540 || shieldId == 11283 || shieldId == 11284 || shieldId == 16079 || shieldId == 16933) {
        protection++
        target.sendMessage("Your shield manages to block some of the dragon's breath.", true)
    }
    if (if (protection == 0 && prayerWorks) target.prayer.isProtectingMage else false) {
        target.sendMessage("Your prayers help resist some of the dragonfire!", true)
        protection++
    }
    if (target.hasEffect(Effect.ANTIFIRE)) {
        target.sendMessage("Your potion slightly protects you from the heat of the dragon's breath.", true)
        protection++
    }
    if (protection == 0) target.sendMessage("You are hit by the dragon's fiery breath.", true)
    chargeDragonfireShield(target)
    return protection
}