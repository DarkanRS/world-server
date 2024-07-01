package com.rs.game.content.combat.special_attacks

import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.content.combat.*
import com.rs.game.content.combat.AmmoType.Companion.forId
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.interactions.PlayerCombatInteraction
import com.rs.game.model.entity.npc.NPC
import com.rs.engine.pathfinder.Direction
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.utils.ItemConfig
import com.rs.utils.Ticks
import com.rs.utils.closestOrNull
import java.util.*
import java.util.stream.Stream

private val specialAttacks: MutableMap<Int, SpecialAttack> = HashMap()

@ServerStartupEvent
fun mapSpecials() {
    fun Entity.isValidCombatTarget(player: Player): Boolean {
        return isAtMultiArea || player.isAtMultiArea || player.attackedBy !== this && player.inCombat() || this.attackedBy !== player && inCombat()
    }

    fun Entity.isInvalidMeleeTarget(player: Player): Boolean {
        return isDead || hasFinished() || !isValidCombatTarget(player) || !player.inMeleeRange(this)
    }

    /**
     * Instant Specials
     */
    //Granite maul/Granite mace
    addSpec(intArrayOf(4153, 13445, 14681, 14679), SpecialAttack(50) { player ->
        val target = player.interactionManager.interaction.let { interaction ->
            if (interaction is PlayerCombatInteraction) interaction.action.target else player.tempAttribs.getO("last_target")
        }

        if (target.isInvalidMeleeTarget(player)) return@SpecialAttack

        player.resetWalkSteps()
        if (player.interactionManager.interaction !is PlayerCombatInteraction || (player.interactionManager.interaction as PlayerCombatInteraction).action.target !== target) {
            player.interactionManager.setInteraction(PlayerCombatInteraction(player, target))
        }
        player.combatDefinitions.drainSpec(50)
        val animId = if (player.equipment.weaponId == 4153) 1667 else 10505
        player.anim(animId)
        if (player.equipment.weaponId == 4153) player.spotAnim(340, 0, 96 shl 16)
        delayNormalHit(target, calculateHit(player, target, false, true, 1.0, 1.0))
        player.soundEffect(target, 2715, true)
        return@SpecialAttack
    })

    //Dragon battleaxe
    addSpec(intArrayOf(1377, 13472), SpecialAttack(100) { player ->
        player.sync(1056, 246)
        player.forceTalk("Raarrrrrgggggghhhhhhh!")
        player.skills.adjustStat(0, -0.1, Skills.ATTACK, Skills.DEFENSE, Skills.RANGE, Skills.MAGIC)
        player.skills.adjustStat(0, 0.2, Skills.STRENGTH)
        player.combatDefinitions.drainSpec(100)
        player.soundEffect(2538, true)
    })


    //Excalibur
    addSpec(intArrayOf(35, 8280, 14632), SpecialAttack(100) { player ->
        player.sync(1168, 247)
        player.forceTalk("For Camelot!")
        val enhanced = player.equipment.weaponId == 14632
        player.skills.adjustStat(if (enhanced) 0 else 8, if (enhanced) 0.15 else 0.0, Skills.DEFENSE)
        player.addEffect(Effect.EXCALIBUR_HEAL, (if (enhanced) 70 else 35).toLong())
        player.combatDefinitions.drainSpec(100)
        player.soundEffect(2539, true)
    })


    //Staff of light
    addSpec(intArrayOf(15486, 15502, 22207, 22208, 22209, 22210, 22211, 22212, 22213, 22214), SpecialAttack(100) { player ->
        player.sync(12804, 2319)
        player.spotAnim(2321)
        player.addEffect(Effect.STAFF_OF_LIGHT_SPEC, Ticks.fromSeconds(60).toLong())
        player.combatDefinitions.drainSpec(100)
    })


    /**
     * MAGIC WEAPONS
     */
    //Mindspike
    addSpec(intArrayOf(23044, 23045, 23046, 23047), SpecialAttack(SpecialAttack.Type.MAGIC, 75) { player, target ->
        delayMagicHit(target, CombatSpell.WIND_RUSH.cast(player, target), Hit.magic(player, 50).setMaxHit(50), {
            target.spotAnim(CombatSpell.WIND_RUSH.hitSpotAnim)
        }, null, null)
        return@SpecialAttack 3
    })


    //Obliteration
    addSpec(intArrayOf(24457), SpecialAttack(SpecialAttack.Type.MAGIC, 20) { player, target ->
        player.sync(16960, 3189)
        val p = World.sendProjectile(player, target, 3188, 15 to 15, 15, 5, 0)
        for (dir in Direction.entries)
            World.sendProjectile(Tile.of(target.x + (dir.dx * 7), target.y + (dir.dy * 7), target.plane), target, 3188, 15 to 15, 15, 5, 0)
        val hit = calculateMagicHit(player, target, 500, true)
        delayMagicHit(target, p.taskDelay, hit, { target.spotAnim(CombatSpell.WIND_RUSH.hitSpotAnim) }, null, null)
        return@SpecialAttack 7
    })

    /**
     * RANGED WEAPONS
     */
    addSpec(RangedWeapon.QUICK_BOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 75) { player, target ->
        player.anim(1074)
        player.spotAnim(250, 10, 100)
        val p = World.sendProjectile(player, target, 249, 50, 5, 20)
        val p2 = World.sendProjectile(player, target, 249, 50, 5, 30)
        delayHit(target, p.taskDelay, Hit.range(player, 25).setMaxHit(25))
        delayHit(target, p2.taskDelay, Hit.range(player, 25).setMaxHit(25))
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.ZAMORAK_BOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 55) { player, target ->
        player.sync(426, 97)
        val p = World.sendProjectile(player, target, 100, 50, 5, 20)
        delayHit(target, p.taskDelay, calculateHit(player, target, true))
        dropAmmo(player, target, Equipment.AMMO, 1)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.GUTHIX_BOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 55) { player, target ->
        player.sync(426, 95)
        val p = World.sendProjectile(player, target, 98, 50, 5, 20)
        delayHit(target, p.taskDelay, calculateHit(player, target, true))
        dropAmmo(player, target, Equipment.AMMO, 1)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.SARADOMIN_BOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 55) { player, target ->
        player.sync(426, 96)
        val p = World.sendProjectile(player, target, 99, 50, 5, 20)
        delayHit(target, p.taskDelay, calculateHit(player, target, true))
        dropAmmo(player, target, Equipment.AMMO, 1)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.RUNE_THROWNAXE.ids, SpecialAttack(SpecialAttack.Type.RANGE, 20) { player, target -> //TODO test this heavily
        val maxHit = getMaxHit(player, target, true, 1.0)
        player.anim(9055)
        val projectile = World.sendProjectile(player, target, 258, 50, 5, 20)
        delayHit(target, projectile.taskDelay, calculateHit(player, target, 1, maxHit, true, true, 1.0))
        player.schedule {
            wait(projectile.taskDelay + 1)
            var lastTarget = target
            for (i in 1..5) {
                val nextTarget = getMultiAttackTargets(player, lastTarget, 4, 10, false).closestOrNull(lastTarget.tile) ?: return@schedule
                wait(World.sendProjectile(lastTarget, nextTarget, 258, 50, 5).taskDelay + 1)
                nextTarget.applyHit(calculateHit(player, nextTarget, 1, maxHit, true, true, 1.0))
                lastTarget = nextTarget
            }
        }
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(Stream.of(RangedWeapon.MAGIC_BOW.ids, RangedWeapon.MAGIC_LONGBOW.ids, RangedWeapon.MAGIC_COMP_BOW.ids).flatMapToInt(Arrays::stream).toArray(), SpecialAttack(SpecialAttack.Type.RANGE, 55) { player, target ->
        player.anim(1074)
        player.spotAnim(250, 10, 100)
        delayHit(target, World.sendProjectile(player, target, 249, 20, 5, 15).taskDelay, calculateHit(player, target, true))
        delayHit(target, World.sendProjectile(player, target, 249, 50, 5, 20).taskDelay, calculateHit(player, target, true))
        dropAmmo(player, target, Equipment.AMMO, 2)
        player.soundEffect(target, 2545, true)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.HAND_CANNON.ids, SpecialAttack(SpecialAttack.Type.RANGE, 50) { player, target ->
        player.sync(12175, 2138)
        delayHit(target, World.sendProjectile(player, target, 2143, 50, 5).taskDelay, calculateHit(player, target, true))
        return@SpecialAttack 1
    })

    addSpec(RangedWeapon.DORGESHUUN_CBOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 75) { player, target ->
        player.anim(ItemConfig.get(RangedWeapon.DORGESHUUN_CBOW.ids[0]).getAttackAnim(0))
        RangedWeapon.DORGESHUUN_CBOW.getAttackSpotAnim(player.equipment.ammoId).let {
            player.spotAnim(it)
        }
        val hit = calculateHit(player, target, true, true, 1.0, 1.3)
        if (hit.damage > 0)
            target.lowerStat(Skills.DEFENSE, hit.damage / 10, 0.0)
        delayHit(target, World.sendProjectile(player, target, 698, 50, 5, 20).taskDelay, hit)
        dropAmmo(player, target, Equipment.AMMO, 1)
        player.soundEffect(target, 1080, true)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.DARK_BOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 65) { player, target ->
        val ammoId = player.equipment.ammoId
        player.anim(ItemConfig.get(RangedWeapon.DARK_BOW.ids[0]).getAttackAnim(0))
        RangedWeapon.DARK_BOW.getAttackSpotAnim(player.equipment.ammoId).let {
            player.spotAnim(it)
        }
        if (ammoId == 11212) {
            val hit1 = calculateHit(player, target, true, true, 1.0, 1.5)
            if (hit1.damage < 80) hit1.setDamage(80)
            val hit2 = calculateHit(player, target, true, true, 1.0, 1.5)
            if (hit2.damage < 80) hit2.setDamage(80)
            delayHit(target, World.sendProjectile(player, target, 1099, 50, 5, 20) { _ ->
                target.spotAnim(1100, 0, 100)
            }.taskDelay, hit1)
            delayHit(target, World.sendProjectile(player, target, 1099, 50, 5, 30) { _ ->
                target.spotAnim(1100, 0, 100)
            }.taskDelay, hit2)
            player.soundEffect(target, 3736, true)
        } else {
            val hit1 = calculateHit(player, target, true, true, 1.0, 1.3)
            if (hit1.damage < 50) hit1.setDamage(50)
            val hit2 = calculateHit(player, target, true, true, 1.0, 1.3)
            if (hit2.damage < 50) hit2.setDamage(50)
            delayHit(target, World.sendProjectile(player, target, 1101, 50, 5, 20).taskDelay, hit1)
            delayHit(target, World.sendProjectile(player, target, 1101, 50, 5, 30).taskDelay, hit2)
            player.soundEffect(target, 3737, true)
        }
        dropAmmo(player, target, Equipment.AMMO, 2)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.ZANIKS_CROSSBOW.ids, SpecialAttack(SpecialAttack.Type.RANGE, 50) { player, target ->
        player.anim(ItemConfig.get(RangedWeapon.ZANIKS_CROSSBOW.ids[0]).getAttackAnim(0))
        player.spotAnim(1714)
        delayHit(target,
            World.sendProjectile(player, target, 2001, 50, 5, 20).taskDelay,
            Hit.range(player,
                calculateHit(player, target, true, true, 1.0, 1.0).damage + 30 + Utils.getRandomInclusive(120)))
        dropAmmo(player, target)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.MORRIGANS_JAVELIN.ids, SpecialAttack(SpecialAttack.Type.RANGE, 50) { player, target ->
        player.sync(10501, 1836)
        val hit = calculateHit(player, target, true, true, 1.0, 1.0)
        val proj = World.sendProjectile(player, target, 1837, 50, 5, 20)
        delayHit(target, proj.taskDelay, hit)
        if (hit.damage > 0) {
            target.schedule {
                var damage = hit.damage
                wait(proj.taskDelay + 2)
                while(damage > 0) {
                    target.applyHit(Hit.flat(player, if (damage < 50) damage % 50 else 50))
                    wait(2)
                    damage -= 50
                }
            }
        }
        dropAmmo(player, target, Equipment.WEAPON, 1)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.MORRIGANS_THROWING_AXE.ids, SpecialAttack(SpecialAttack.Type.RANGE, 50) { player, target ->
        player.sync(10504, 1838)
        delayHit(target,
            World.sendProjectile(player, target, 1839, 50, 5, 20).taskDelay,
            calculateHit(player, target, true, true, 1.0, 1.0))
        dropAmmo(player, target, Equipment.WEAPON, 1)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.SEERCULL.ids, SpecialAttack(SpecialAttack.Type.RANGE, 100) { player, target ->
        val hit = calculateHit(player, target, true, true, 1.0, 1.0)
        player.anim(ItemConfig.get(RangedWeapon.SEERCULL.ids[0]).getAttackAnim(0))
        player.spotAnim(472, 0, 100)
        delayHit(target, World.sendProjectile(player, target, 473, 50, 5, 20) { _ -> target.spotAnim(474) }.taskDelay, hit)
        if (hit.damage > 0)
            target.lowerStat(Skills.MAGIC, hit.damage / 10, 0.0)
        dropAmmo(player, target)
        player.soundEffect(target, 2546, true)
        return@SpecialAttack getRangeCombatDelay(player)
    })

    addSpec(RangedWeapon.DECIMATION.ids, SpecialAttack(SpecialAttack.Type.RANGE, 20) { player, target ->
        player.sync(16959, 3192)
        val p = World.sendProjectile(player, target, 3188, 100, 10, 20) { _ -> target.spotAnim(3191) }
        for (i in 0..3)
            delayHit(target, p.taskDelay, calculateHit(player, target, true, true, 1.0, 1.0))
        return@SpecialAttack 6
    })

    /**
     * MELEE WEAPONS
     */
    //Vine whip
    addSpec(intArrayOf(21371, 21372, 21373, 21374, 21375), SpecialAttack(SpecialAttack.Type.MELEE, 60) { player, target ->
        val attackStyle = player.combatDefinitions.getAttackStyle()
        val tile = Tile.of(target.tile)
        player.sync(11971, 476)
        player.schedule {
            for (i in 1..11) {
                World.sendSpotAnim(tile, SpotAnim(478))
                for (entity in getMultiAttackTargets(player, tile, 1, 9)) {
                    val hit = calculateHit(player, entity, 0, getMaxHit(player, target, 21371, attackStyle, false, 0.33), 21371, attackStyle, false, true, 1.25)
                    addXp(player, entity, attackStyle.xpType, hit)
                    if (hit.damage > 0 && Utils.getRandomInclusive(8) == 0) target.poison.makePoisoned(48)
                    entity.applyHit(hit)
                }
                wait(5)
            }
        }
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Abyssal whip
    addSpec(intArrayOf(4151, 13444, 15441, 15442, 15443, 15444, 23691), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.anim(11971)
        target.spotAnim(2108, 0, 100)
        if (target is Player)
            target.runEnergy = if (target.runEnergy > 25.0) target.runEnergy - 25.0 else 0.0
        delayNormalHit(target, calculateHit(player, target, false, true, 1.25, 1.0))
        player.soundEffect(target, 2713, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.getWeaponId())
    })

    //Saradomin sword
    addSpec(intArrayOf(11730, 13461, 23690), SpecialAttack(SpecialAttack.Type.MELEE, 100) { player, target ->
        player.sync(11993, 1194)
        delayNormalHit(target, Hit(player, 50 + Utils.getRandomInclusive(100), HitLook.MELEE_DAMAGE).setMaxHit(150))
        delayNormalHit(target, calculateHit(player, target, false, true, 2.0, 1.1))
        player.soundEffect(target, 3853, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon spear/Zamorakian spear
    addSpec(intArrayOf(1249, 1263, 3176, 5716, 5730, 11716, 13454, 13770, 13772, 13774, 13776, 23683), SpecialAttack(SpecialAttack.Type.MELEE, 25) { player, target ->
        player.anim(12017)
        player.stopAll()
        target.spotAnim(80, 5, 60)

        if (!target.addWalkSteps(target.x - player.x + target.x, target.y - player.y + target.y, 1))
            player.faceEntity(target)
        target.faceEntity(player)
        player.schedule {
            target.stopFaceEntity()
            player.stopFaceEntity()
        }
        if (target is Player) {
            target.lock()
            target.addFoodDelay(3000)
            target.setDisableEquip(true)
            target.schedule {
                wait(5)
                target.setDisableEquip(false)
                target.unlock()
            }
        } else if (target is NPC) {
            target.freeze(Ticks.fromSeconds(3), false)
            player.soundEffect(target, 2544, true)
        }
        return@SpecialAttack getMeleeCombatDelay(player.equipment.getWeaponId());
    })


    //Keenblade
    addSpec(intArrayOf(23042), SpecialAttack(SpecialAttack.Type.MELEE, 75) { player, target ->
        player.sync(16067, 2109)
        delayNormalHit(target, Hit(player, 50, HitLook.MELEE_DAMAGE).setMaxHit(50))
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })


    //Saradomin godsword
    addSpec(intArrayOf(11698, 13452, 23681), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.sync(12019, 2109)
        val hit = calculateHit(player, target, false, true, 2.0, 1.1)
        player.heal(hit.damage / 2)
        player.prayer.restorePrayer(hit.damage.toDouble() / 4)
        delayNormalHit(target, hit)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })


    //Bandos godsword
    addSpec(intArrayOf(11696, 13451, 23680), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.sync(11991, 2114)
        val hit = calculateHit(player, target, false, true, 2.0, 1.1)
        delayNormalHit(target, hit)
        var amountLeft = hit.damage / 10
        val skillsToDrain = listOf(Constants.DEFENSE, Constants.STRENGTH, Constants.PRAYER, Constants.ATTACK, Constants.MAGIC, Constants.RANGE)

        for (skill in skillsToDrain) {
            amountLeft = target.lowerStat(skill, amountLeft, 0.0)
            if (amountLeft <= 0) break
        }
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })


    //Ancient mace
    addSpec(intArrayOf(11061, 22406), SpecialAttack(SpecialAttack.Type.MELEE, 100) { player, target ->
        player.sync(6147, 1052)
        val hit = calculateHit(player, target, false, true, 1.0, 1.0)
        delayNormalHit(target, hit)
        if (target is Player)
            target.prayer.drainPrayer(hit.damage.toDouble())
        player.soundEffect(target, 3592, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Armadyl godsword
    addSpec(intArrayOf(11694, 13450, 23679), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.sync(11989, 2113)
        delayNormalHit(target, calculateHit(player, target, false, true, 2.0, 1.25))
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Vesta's longsword
    /**
     * Its special attack, Feint, inflicts 20% more damage and is harder to defend against while only draining 25% of the special bar,
     * making it deadly as it can be used four times in a row. When activated, the player thrusts the sword out with an exaggerated motion.
     * The power and accuracy of this attack made the Longsword the only piece of Ancient Warrior's equipment to retain a high price during
     * the excessive supply before the Anti-76k measures were put in place, due to its fatal capabilities. Only the non-corrupted version
     * features this attack. After the sword has been used in combat it will become untradeable.
     */
    addSpec(intArrayOf(13899, 13901), SpecialAttack(SpecialAttack.Type.MELEE, 25) { player, target ->
        player.anim(10502)
        delayNormalHit(target, calculateHit(player, target, false, true, 2.0, 1.20))
        player.soundEffect(target, 2529, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Statius' warhammer
    addSpec(intArrayOf(13902, 13904), SpecialAttack(SpecialAttack.Type.MELEE, 35) { player, target ->
        val hit = calculateHit(player, target, false, true, 1.0, 1.25)
        player.sync(10505, 1840)
        delayNormalHit(target, hit)
        if (hit.damage != 0)
            target.lowerStat(Skills.DEFENSE, 0.30, 0.0)
        player.soundEffect(target, 2520, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Vesta's spear
    addSpec(intArrayOf(13905, 13907), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        val attackStyle = player.combatDefinitions.getAttackStyle()
        player.sync(10499, 1835)
        player.addEffect(Effect.MELEE_IMMUNE, Ticks.fromSeconds(5).toLong())
        attackTarget(getMultiAttackTargets(player, target, 1, 20)) { next ->
            delayHit(next, 1, 13905, attackStyle, calculateHit(player, next, 13905, attackStyle, false, true, 1.0, 1.15))
            return@attackTarget true
        }
        player.soundEffect(target, 2529, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon 2h sword
    addSpec(intArrayOf(7158, 13430, 23696), SpecialAttack(SpecialAttack.Type.MELEE, 60) { player, target ->
        val attackStyle = player.combatDefinitions.getAttackStyle()
        player.sync(7078, 1225)
        attackTarget(getMultiAttackTargets(player, target, 1, 20)) { next ->
            delayHit(next, 1, 7158, attackStyle, calculateHit(player, next, 7158, attackStyle, false, true, 1.0, 1.2))
            return@attackTarget true
        }
        player.soundEffect(target, 2530, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Korasi's sword
    addSpec(intArrayOf(18786, 19780, 19784, 22401), SpecialAttack(SpecialAttack.Type.MELEE, 60) { player, target ->
        player.sync(14788, 1729)
        var damage = getMaxHit(player, target, false, 1.0).toDouble()
        val multiplier = Utils.random(if (!target.isAtMultiArea && !player.isAtMultiArea && !target.isForceMultiArea && !player.isForceMultiArea) 0.5 else 0.0, 1.5)
        damage *= multiplier
        delayNormalHit(target, Hit(player, damage.toInt(), HitLook.MAGIC_DAMAGE).setMaxHit(damage.toInt()))
        WorldTasks.schedule(0) { target.spotAnim(2795) }
        player.soundEffect(target, 3853, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Zamorak godsword
    addSpec(intArrayOf(11700, 13453, 23682), SpecialAttack(SpecialAttack.Type.MELEE, 100) { player, target ->
        val hit = calculateHit(player, target, false, true, 2.0, 1.1)
        player.sync(7070, 1221)
        if (hit.damage != 0 && target.size <= 1) {
            target.spotAnim(2104)
            target.freeze(Ticks.fromSeconds(18), false)
        }
        delayNormalHit(target, hit)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    fun clawSpec(player: Player, target: Entity, multipliers: DoubleArray) {
        var base = getMaxHit(player, target, false, multipliers[0]).let {
            calculateHit(player, target, it / 2, it, false, true, 1.0)
        }
        val hits = Array<Hit>(4) { Hit.miss(player) }
        if (base.damage > 0) {
            val damageHalved = base.damage / 2
            hits[0] = base
            hits[1] = Hit(player, damageHalved, HitLook.MELEE_DAMAGE)
            hits[2] = Hit(player, damageHalved / 2, HitLook.MELEE_DAMAGE)
            hits[3] = Hit(player, damageHalved - (damageHalved / 2), HitLook.MELEE_DAMAGE)
        } else {
            sequenceOf(multipliers[1], multipliers[2], multipliers[3]).forEachIndexed { index, multiplier ->
                base = calculateHit(player, target, false, true, multiplier, multiplier)
                if (base.damage > 0) {
                    if (index == 2) hits[3] = base
                    else hits.fill(Hit.miss(player), 0, index + 1)
                    return@forEachIndexed
                }
            }
            if (base.damage == 0) hits[3] = Hit(player, Utils.getRandomInclusive(7), HitLook.MELEE_DAMAGE)
        }
        hits.forEachIndexed { index, hit ->
            if (index > 1) delayHit(target, 1, hit)
            else delayNormalHit(target, hit)
        }
        sequenceOf(7464 to 0, 7465 to 25, 7466 to 35, 7467 to 50).forEach { (effect, delay) ->
            player.soundEffect(target, effect, delay, true)
        }
    }

    //Rune claws
    addSpec(intArrayOf(3101, 13764), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.anim(10961)
        clawSpec(player, target, doubleArrayOf(0.75, 0.75, 0.75, 1.25))
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon claws
    addSpec(intArrayOf(14484, 14486, 23695), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.sync(10961, 1950)
        clawSpec(player, target, doubleArrayOf(1.0, 1.0, 1.0, 1.5))
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Barrelchest anchor
    addSpec(intArrayOf(10887), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        player.sync(5870, 1027)
        val hit = calculateHit(player, target, false, true, 2.0, 1.1)
        delayNormalHit(target, hit)
        target.lowerStat(Skills.DEFENSE, hit.damage / 10, 0.0)
        player.soundEffect(target, 3481, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon longsword
    addSpec(intArrayOf(1305, 13475), SpecialAttack(SpecialAttack.Type.MELEE, 25) { player, target ->
        player.sync(12033, 2117)
        delayNormalHit(target, calculateHit(player, target, false, true, 1.0, 1.25))
        player.soundEffect(target, 2529, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon halberd
    addSpec(intArrayOf(3204, 13478), SpecialAttack(SpecialAttack.Type.MELEE, 30) { player, target ->
        player.sync(1665, 282)
        if (target.size > 3) {
            target.spotAnim(254, 0, 100)
            target.spotAnim(80)
        }
        delayNormalHit(target, calculateHit(player, target, false, true, 1.0, 1.1))
        if (target.size > 1) delayHit(target, 1, calculateHit(player, target, false, true, 1.0, 1.1))
        player.soundEffect(target, 2533, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon scimitar
    addSpec(intArrayOf(4587, 13477), SpecialAttack(SpecialAttack.Type.MELEE, 55) { player, target ->
        player.sync(12031, 2118)
        val hit = calculateHit(player, target, false, true, 1.25, 1.0)
        if (target is Player && hit.damage > 0)
            target.setProtectionPrayBlock(10)
        delayNormalHit(target, hit)
        player.soundEffect(target, 2540, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon daggers
    addSpec(intArrayOf(1215, 1231, 5680, 5698, 13465, 13466, 13467, 13468), SpecialAttack(SpecialAttack.Type.MELEE, 25) { player, target ->
        player.anim(1062)
        player.spotAnim(252, 0, 100)
        delayNormalHit(target, calculateHit(player, target, false, true, 1.15, 1.15))
        delayNormalHit(target, calculateHit(player, target, false, true, 1.15, 1.15))
        player.soundEffect(target, 2537, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon mace
    addSpec(intArrayOf(1434, 13479), SpecialAttack(SpecialAttack.Type.MELEE, 25) { player, target ->
        player.sync(1060, 251)
        delayNormalHit(target, calculateHit(player, target, false, true, 1.25, 1.5))
        player.soundEffect(target, 2541, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Rod of Ivandis, Ivandis flail
    addSpec(Stream.of(Utils.range(7639, 7648), Utils.range(13117, 13146)).flatMapToInt { array: IntArray? -> Arrays.stream(array) }.toArray(), SpecialAttack(SpecialAttack.Type.MELEE, 1) { player, target ->
        //TODO lmao
        player.sync(1060, 251)
        delayNormalHit(target, calculateHit(player, target, false, true, 1.0, 1.0))
        player.soundEffect(target, 2541, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Penance trident
    addSpec(intArrayOf(15438, 15485), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, _ ->
        player.anim(12804)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dwarven army axe
    addSpec(intArrayOf(21340), SpecialAttack(SpecialAttack.Type.MELEE, 100) { player, target ->
        player.anim(16308)
        //TODO proper anim
        delayHit(target, 0, Hit.melee(player, if (target is NPC) 20 else 10))
        delayHit(target, 0, Hit.melee(player, if (target is NPC) 40 else 25))
        delayHit(target, 0, Hit.melee(player, if (target is NPC) 30 else 20))
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Darklight
    addSpec(intArrayOf(6746), SpecialAttack(SpecialAttack.Type.MELEE, 50) { player, target ->
        val accuracyRoll = calculateHit(player, target, -1, AttackStyle(0, "stab", XPType.AGGRESSIVE, AttackType.STAB), false, true, 1.0, 1.0)
        player.sync(2890, 483)
        delayHit(target, 0, calculateHit(player, target, false, true, 1.0, 1.0))
        if (accuracyRoll.damage > 0) {
            target.lowerStat(Skills.ATTACK, 0.05, 0.0)
            target.lowerStat(Skills.STRENGTH, 0.05, 0.0)
            target.lowerStat(Skills.DEFENSE, 0.05, 0.0)
            player.soundEffect(target, 225, true)
        }
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon hatchet
    addSpec(intArrayOf(6739, 13470), SpecialAttack(SpecialAttack.Type.MELEE, 100) { player, target ->
        player.sync(2876, 479)
        val hit = calculateHit(player, target, false, true, 1.0, 1.0)
        delayHit(target, 0, hit)
        if (hit.damage > 0) {
            target.lowerStat(Skills.DEFENSE, hit.damage / 10, 0.0)
            target.lowerStat(Skills.MAGIC, hit.damage / 10, 0.0)
        }
        player.soundEffect(target, 2531, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Dragon pickaxe
    addSpec(intArrayOf(15259, 15261, 20786), SpecialAttack(SpecialAttack.Type.MELEE, 100) { player, target ->
        player.sync(2661, 2144)
        val hit = calculateHit(player, target, false, true, 1.0, 1.0)
        delayHit(target, 1, hit)
        if (hit.damage > 0) {
            target.lowerStat(Skills.ATTACK, 0.05, 0.0)
            target.lowerStat(Skills.MAGIC, 0.05, 0.0)
            target.lowerStat(Skills.RANGE, 0.05, 0.0)
        }
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Bone dagger
    addSpec(intArrayOf(8872, 8874, 8876, 8878), SpecialAttack(SpecialAttack.Type.MELEE, 75) { player, target ->
        player.sync(4198, 704)
        val hit = calculateHit(player, target, false, target.tempAttribs.getO<Any>("last_target") === player, 1.0, 1.0)
        delayHit(target, 0, hit)
        if (hit.damage > 0)
            target.lowerStat(Skills.DEFENSE, hit.damage / 10, 0.0)
        player.soundEffect(target, 1084, true)
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Brine sabre, Brackish blade
    addSpec(intArrayOf(11037, 20671, 20673), SpecialAttack(SpecialAttack.Type.MELEE, 75) { player, target ->
        player.anim(6118)
        player.spotAnim(1048, 0, 96)
        val hit = calculateHit(player, target, false, true, 2.0, 1.0)
        delayHit(target, 0, hit)
        if (hit.damage > 0) {
            player.skills.adjustStat(hit.damage / 40, 0.0, true, Skills.ATTACK)
            player.skills.adjustStat(hit.damage / 40, 0.0, true, Skills.STRENGTH)
            player.skills.adjustStat(hit.damage / 40, 0.0, true, Skills.DEFENSE)
        }
        return@SpecialAttack getMeleeCombatDelay(player.equipment.weaponId)
    })

    //Annihilation (16964 3193) obtaining
    //Absorbing essence into it? (16962 43)
    addSpec(intArrayOf(24455), SpecialAttack(SpecialAttack.Type.MELEE, 20) { player, target ->
        player.sync(16961, 44)
        delayNormalHit(target, calculateHit(player, target, false, true, 1.5, 1.5))
        return@SpecialAttack 5
    })
}

fun addSpec(itemIds: IntArray, spec: SpecialAttack) {
    for (itemId in itemIds) specialAttacks[itemId] = spec
}

fun addSpec(itemId: Int, spec: SpecialAttack) {
    specialAttacks[itemId] = spec
}

fun getSpec(itemId: Int): SpecialAttack? {
    return specialAttacks[itemId]
}

fun handleClick(player: Player) {
    val spec = getSpec(player.equipment.weaponId)
    if (spec == null) {
        player.sendMessage("This weapon has no special attack implemented yet.")
        return
    }
    if (spec.instant != null) {
        var specAmt = spec.energyCost.toDouble()
        if (player.combatDefinitions.hasRingOfVigour()) specAmt *= 0.9
        if (player.combatDefinitions.getSpecialAttackPercentage() < specAmt) {
            player.sendMessage("You don't have enough power left.")
            player.combatDefinitions.drainSpec(0)
            return
        }
        spec.instant.accept(player)
        return
    }
    player.combatDefinitions.switchUsingSpecialAttack()
}

fun execute(type: SpecialAttack.Type, player: Player, target: Entity?): Int {
    val spec = getSpec(player.equipment.weaponId)
    var cost = spec!!.energyCost.toDouble()
    if (spec.type != type) {
        player.combatDefinitions.drainSpec(0)
        return 3
    }
    if (player.combatDefinitions.hasRingOfVigour()) cost *= 0.9
    if (player.combatDefinitions.getSpecialAttackPercentage() < cost) {
        player.sendMessage("You don't have enough power left.")
        player.combatDefinitions.drainSpec(0)
        return 3
    }
    player.combatDefinitions.drainSpec(cost.toInt())
    return spec.execute?.apply(player, target!!) ?: 0
}