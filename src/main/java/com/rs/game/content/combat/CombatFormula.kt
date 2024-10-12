package com.rs.game.content.combat

import com.rs.Settings
import com.rs.cache.loaders.Bonus
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.managers.AuraManager.Aura
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import kotlin.collections.mutableListOf
import kotlin.math.floor

private val combatPlugins = mutableListOf<(Entity, Entity, Bonus, CombatStyle) -> (CombatMod)>()

fun onCombatFormulaAdjust(plugin: (Entity, Entity, Bonus, CombatStyle) -> (CombatMod)) {
    combatPlugins.add(plugin)
}

fun hasFireCape(player: Player): Boolean {
    val capeId = player.equipment.capeId
    return capeId == 6570 || capeId == 20769 || capeId == 20771 || capeId == 23659
}

@JvmOverloads
fun calculateMagicHit(entity: Entity, target: Entity, baseDamage: Int, applyMageLevelBoost: Boolean = true): Hit {
    val hit = calculateHit(entity, target, 1, getMaxHit(entity, target, CombatStyle.MAGIC, Bonus.MAGIC_ATT, 1.0, baseDamage, applyMageLevelBoost), Bonus.MAGIC_ATT, CombatStyle.MAGIC, true, 1.0)
    if (hit.damage > 0) if (target is NPC) if (target.id == 9463 && entity is Player && hasFireCape(entity)) hit.setDamage(hit.damage + 40)
    return hit
}

@JvmOverloads
fun calculateHit(entity: Entity, target: Entity, attackBonus: Bonus, combatStyle: CombatStyle, calcDefense: Boolean = true, accuracyModifier: Double = 1.0, damageModifier: Double = 1.0): Hit {
    return calculateHit(entity, target, 1, getMaxHit(entity, target, combatStyle, attackBonus, damageModifier), attackBonus, combatStyle, calcDefense, accuracyModifier)
}

@JvmOverloads
fun calculateHit(player: Player, target: Entity,  combatStyle: CombatStyle, calcDefense: Boolean = true, accuracyModifier: Double = 1.0, damageModifier: Double = 1.0): Hit {
    return calculateHit(player, target, 1, getMaxHit(player, target, combatStyle, player.combatDefinitions.getAttackStyle().attackType.attBonus, damageModifier), combatStyle, calcDefense, accuracyModifier)
}

@JvmOverloads
fun calculateHit(entity: Entity, target: Entity, attackStyle: AttackStyle, combatStyle: CombatStyle, calcDefense: Boolean = true, accuracyModifier: Double = 1.0, damageModifier: Double = 1.0): Hit {
    return calculateHit(entity, target, 1, getMaxHit(entity, target, combatStyle, attackStyle.attackType.attBonus, damageModifier), combatStyle, calcDefense, accuracyModifier)
}

fun calculateHit(entity: Entity, target: Entity, combatStyle: CombatStyle): Hit {
    return calculateHit(entity, target, entity.currentOffensiveBonusForStyle, combatStyle, true, 1.0, 1.0)
}

fun calculateHit(entity: Entity, target: Entity, minHit: Int, maxHit: Int, combatStyle: CombatStyle, calcDefense: Boolean, accuracyModifier: Double): Hit {
    return calculateHit(entity, target, minHit, maxHit, entity.currentOffensiveBonusForStyle, combatStyle, calcDefense, accuracyModifier)
}

@JvmRecord
data class CombatMod(
    val accuracyLevel: Double = 1.0,
    val accuracy: Double = 1.0,
    val strengthLevel: Double = 1.0,
    val baseDamage: Double = 1.0,
    val defenseLevel: Double = 1.0,
    val defense: Double = 1.0,
    val overallDamage: Double = 1.0,
)

fun calculateHit(entity: Entity, target: Entity, minHit: Int, maxHit: Int, attackBonus: Bonus, combatStyle: CombatStyle, calcDefense: Boolean, accuracyModifier: Double): Hit {
    val combatModifiers = combatPlugins.map { it(entity, target, attackBonus, combatStyle) }

    val hit = Hit(entity, 0, when(combatStyle) {
        CombatStyle.MELEE -> HitLook.MELEE_DAMAGE
        CombatStyle.RANGE -> HitLook.RANGE_DAMAGE
        CombatStyle.MAGIC -> HitLook.MAGIC_DAMAGE
    })
    if (calcDefense) {
        val offensiveStat = when(combatStyle) {
            CombatStyle.MELEE -> Skills.ATTACK
            CombatStyle.RANGE -> Skills.RANGE
            CombatStyle.MAGIC -> Skills.MAGIC
        }
        val prayerAccuracyMultiplier = if (entity is Player) when(combatStyle) {
            CombatStyle.MELEE -> entity.prayer.attackMultiplier
            CombatStyle.RANGE -> entity.prayer.rangeMultiplier
            CombatStyle.MAGIC -> entity.prayer.mageMultiplier
        } else 1.0
        var atkLvl = floor(entity.getLevel(offensiveStat) * prayerAccuracyMultiplier)
        if (entity is Player) {
            val style = entity.combatDefinitions.getAttackStyle()
            if (style.attackType == AttackType.ACCURATE || style.xpType == XPType.ACCURATE)
                atkLvl += 3.0
            else if (style.xpType == XPType.CONTROLLED)
                atkLvl += 1.0
        }
        atkLvl += 8.0

        for (combatMod in combatModifiers)
            atkLvl *= combatMod.accuracyLevel

        var atkBonus = entity.getBonus(attackBonus).toDouble()

        var atk = floor(atkLvl * (atkBonus + 64))
        atk *= accuracyModifier

        for (combatMod in combatModifiers)
            atk *= combatMod.accuracy

        val defStat = if (combatStyle == CombatStyle.MAGIC) Skills.MAGIC else Skills.DEFENSE

        var defLvl = target.getLevel(defStat).toDouble() * ((target as? Player)?.prayer?.defenceMultiplier ?: 1.0)
        val defBonus = target.getBonus(attackBonus.invert()).toDouble()
        defLvl = target.getLevel(defStat).toDouble() * ((target as? Player)?.prayer?.defenceMultiplier ?: 1.0)
        if (target is Player) {
            val style = target.combatDefinitions.getAttackStyle()
            if (style.attackType == AttackType.LONG_RANGE || style.xpType == XPType.DEFENSIVE)
                atkLvl += 3.0
            else if (style.xpType == XPType.CONTROLLED)
                atkLvl += 1.0
        }
        defLvl += 8.0

        if (combatStyle == CombatStyle.MAGIC && entity is Player && target is Player) {
            defLvl *= 0.7
            var magLvl = floor(target.skills.getLevel(Skills.DEFENSE) * target.prayer.mageMultiplier)
            magLvl *= 0.3
            defLvl = defLvl + magLvl
        }

        for (combatMod in combatModifiers)
            defLvl *= combatMod.defenseLevel

        var def = floor(defLvl * (defBonus + 64))

        for (combatMod in combatModifiers)
            def *= combatMod.defense

        val prob = if (atk > def) (1 - (def + 2) / (2 * (atk + 1))) else (atk / (2 * (def + 1)))
        if (Settings.getConfig().isDebug && entity is Player && entity.nsv.getB("hitChance")) entity.sendMessage("Your hit chance: ${Utils.formatDouble(prob * 100.0)}%")
        if (prob <= Math.random()) return hit.setDamage(0)
    }
    var finalHit = Utils.random(minHit, maxHit).toDouble()
    for (combatMod in combatModifiers)
        finalHit *= combatMod.overallDamage
    hit.setMaxHit(maxHit)
    if (entity is Player && entity.auraManager.isActivated(Aura.EQUILIBRIUM)) {
        val perc25MaxHit = (maxHit * 0.25).toInt()
        finalHit -= perc25MaxHit
        hit.setMaxHit(maxHit - perc25MaxHit)
        if (finalHit < 0) finalHit = 0.0
        if (finalHit < perc25MaxHit) finalHit += perc25MaxHit
    }
    hit.setDamage(finalHit.toInt())
    return hit
}

private fun Bonus.invert(): Bonus = when(this) {
    Bonus.STAB_ATT -> Bonus.STAB_DEF
    Bonus.STAB_DEF -> Bonus.STAB_ATT
    Bonus.SLASH_ATT -> Bonus.SLASH_DEF
    Bonus.SLASH_DEF -> Bonus.SLASH_ATT
    Bonus.CRUSH_ATT -> Bonus.CRUSH_DEF
    Bonus.CRUSH_DEF -> Bonus.CRUSH_ATT
    Bonus.RANGE_ATT -> Bonus.RANGE_DEF
    Bonus.RANGE_DEF -> Bonus.RANGE_ATT
    Bonus.MAGIC_ATT -> Bonus.MAGIC_DEF
    Bonus.MAGIC_DEF -> Bonus.MAGIC_ATT
    else -> Bonus.STAB_DEF
}

fun getMagicBonusBoost(entity: Entity): Double = entity.getBonus(Bonus.MAGIC_STR) / 100.0 + 1.0

@JvmOverloads
fun getMaxHit(player: Player, target: Entity, combatStyle: CombatStyle, damageMultiplier: Double, spellBaseDamage: Int = -1, applyMageLevelBoost: Boolean = false): Int {
    return getMaxHit(player, target, combatStyle, player.combatDefinitions.getAttackStyle().attackType.attBonus, damageMultiplier, spellBaseDamage, applyMageLevelBoost)
}

fun getMaxHit(entity: Entity, target: Entity, combatStyle: CombatStyle, attackBonus: Bonus, damageMultiplier: Double, spellBaseDamage: Int = -1, applyMageLevelBoost: Boolean = false): Int {
    val combatModifiers = combatPlugins.map { it(entity, target, attackBonus, combatStyle) }

    val offensiveStat = when(combatStyle) {
        CombatStyle.MELEE -> Skills.STRENGTH
        CombatStyle.RANGE -> Skills.RANGE
        CombatStyle.MAGIC -> Skills.MAGIC
    }
    val prayerAccuracyMultiplier = if (entity is Player) when(combatStyle) {
        CombatStyle.MELEE -> entity.prayer.strengthMultiplier
        CombatStyle.RANGE -> entity.prayer.rangeMultiplier
        CombatStyle.MAGIC -> 1.0
    } else 1.0

    var strLvl = floor(entity.getLevel(offensiveStat) * prayerAccuracyMultiplier)
    strLvl += 8.0
    if (entity is Player) {
        val style = entity.combatDefinitions.getAttackStyle()
        if (style.attackType == AttackType.ACCURATE || style.xpType == XPType.AGGRESSIVE)
            strLvl += 3.0
        else if (style.xpType == XPType.CONTROLLED)
            strLvl += 1.0
    }

    for (combatMod in combatModifiers)
        strLvl *= combatMod.strengthLevel

    val strBonus = entity.getBonus(if (combatStyle == CombatStyle.MELEE) Bonus.MELEE_STR else Bonus.RANGE_STR).toDouble()

    var baseDamage = 5 + strLvl * (strBonus + 64) / 64

    if (combatStyle == CombatStyle.MAGIC)
        baseDamage = spellBaseDamage.toDouble()

    for (combatMod in combatModifiers)
        baseDamage *= combatMod.baseDamage

    var maxHit = floor(baseDamage * damageMultiplier)

    if (combatStyle == CombatStyle.MAGIC) {
        if (applyMageLevelBoost) {
            val boostedMageLevelBonus = 1 + ((entity.getLevel(Constants.MAGIC) - entity.getLevelForXp(Constants.MAGIC)) * 0.03)
            if (boostedMageLevelBonus > 1) maxHit = (maxHit * boostedMageLevelBonus)
        }
        maxHit = (maxHit * getMagicBonusBoost(entity))
    }

    if (Settings.getConfig().isDebug && (entity as? Player)?.nsv?.getB("hitChance") == true) entity.sendMessage("Your max hit: ${maxHit.toInt()}")
    return maxHit.toInt()
}