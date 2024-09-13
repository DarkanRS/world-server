package com.rs.game.content.combat

import com.rs.Settings
import com.rs.cache.loaders.Bonus
import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.content.skills.dungeoneering.DungeonController
import com.rs.game.content.skills.dungeoneering.KinshipPerk
import com.rs.game.content.skills.slayer.Slayer
import com.rs.game.content.skills.summoning.Pouch
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.managers.AuraManager.Aura
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import java.util.Locale
import kotlin.math.floor
import kotlin.math.pow

@JvmOverloads
fun calculateMagicHit(entity: Entity, target: Entity, baseDamage: Int, applyMageLevelBoost: Boolean = true): Hit {
    val hit = Hit.magic(entity, getMaxHit(entity, target, CombatStyle.MAGE, Bonus.MAGIC_ATT, 1.0, baseDamage, applyMageLevelBoost))
    hit.setDamage(Utils.random(1, hit.damage))
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

data class CombatMod(
    val accuracyLevel: Double = 1.0,
    val accuracy: Double = 1.0,
    val strengthLevel: Double = 1.0,
    val baseDamage: Double = 1.0,
    val defenseLevel: Double = 1.0,
    val defense: Double = 1.0,
    val overallDamage: Double = 1.0,
)

val combatPlugins = listOf<(Entity, Entity, Bonus, CombatStyle) -> (CombatMod)>(
    berserkerNeck@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@berserkerNeck CombatMod()
        if (setOf(6523, 6525, 6527, 6528).contains(player.equipment.weaponId) && player.equipment.amuletId == 11128)
            return@berserkerNeck CombatMod(baseDamage = 1.2)
        return@berserkerNeck CombatMod()
    },
    dharoks@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@dharoks CombatMod()
        if (setOf(4718, 4886, 4887, 4888, 4889).contains(player.equipment.weaponId) && fullDharokEquipped(player))
            return@dharoks CombatMod(baseDamage = 1.0 + (player.maxHitpoints - player.hitpoints) / 1000.0 * (player.maxHitpoints / 1000.0))
        return@dharoks CombatMod()
    },
    voidPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@voidPlugin CombatMod()
        return@voidPlugin when(combatStyle) {
            CombatStyle.MELEE -> if (fullVoidEquipped(player, 11665, 11676)) CombatMod(
                accuracyLevel = 1.1,
                strengthLevel = 1.1
            ) else null
            CombatStyle.RANGE -> if (fullVoidEquipped(player, 11664, 11675)) CombatMod(
                accuracyLevel = 1.1,
                strengthLevel = 1.1
            ) else null
            CombatStyle.MAGE -> if (fullVoidEquipped(player, 11663, 11674)) CombatMod(accuracyLevel = 1.3) else null
            else -> null
        } ?: CombatMod()
    },
    salveAmulet@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@salveAmulet CombatMod()
        if (target !is NPC || !target.definitions.isUndead) return@salveAmulet CombatMod()
        return@salveAmulet when (player.equipment.salveAmulet) {
            0 -> CombatMod(accuracy = 1.15, baseDamage = 1.15)
            1 -> CombatMod(accuracy = 1.20, baseDamage = 1.20)
            else -> CombatMod()
        }
        return@salveAmulet CombatMod()
    },
    slayerHelmPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@slayerHelmPlugin CombatMod()
        if (!player.hasSlayerTask() || target !is NPC || !player.slayer.isOnTaskAgainst(target))
            return@slayerHelmPlugin CombatMod()

        val mod = CombatMod(accuracy = 7.0 / 6.0, baseDamage = 7.0 / 6.0)

        return@slayerHelmPlugin when (combatStyle) {
            CombatStyle.MELEE -> if (Slayer.hasBlackMask(player)) mod else CombatMod()
            CombatStyle.RANGE -> if (Slayer.hasFocusSight(player)) mod else CombatMod()
            CombatStyle.MAGE -> if (Slayer.hasHexcrest(player)) mod else CombatMod()
            else -> CombatMod()
        }
    },
    auraPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@auraPlugin CombatMod()
        return@auraPlugin when(combatStyle) {
            CombatStyle.RANGE -> CombatMod(accuracyLevel = player.auraManager.rangeAcc)
            CombatStyle.MAGE -> CombatMod(accuracyLevel = player.auraManager.magicAcc)
            else -> null
        } ?: CombatMod()
    },
    ringKinshipPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player || !player.controllerManager.isIn(DungeonController::class.java)) return@ringKinshipPlugin CombatMod()
        when(combatStyle) {
            CombatStyle.MELEE -> {
                if (player.combatDefinitions.getAttackStyle().xpType == XPType.AGGRESSIVE && player.dungManager.activePerk == KinshipPerk.BERSERKER)
                    return@ringKinshipPlugin CombatMod(strengthLevel = 1.1 + (player.dungManager.getKinshipTier(KinshipPerk.BERSERKER) * 0.01))
                else if (player.combatDefinitions.getAttackStyle().xpType == XPType.ACCURATE && player.dungManager.activePerk == KinshipPerk.TACTICIAN)
                    return@ringKinshipPlugin CombatMod(accuracy = 1.1 + (player.dungManager.getKinshipTier(KinshipPerk.TACTICIAN) * 0.01))
            }
            CombatStyle.RANGE -> {
                if (player.combatDefinitions.getAttackStyle().attackType == AttackType.RAPID && player.dungManager.activePerk == KinshipPerk.DESPERADO)
                    return@ringKinshipPlugin CombatMod(strengthLevel = 1.1 + (player.dungManager.getKinshipTier(KinshipPerk.DESPERADO) * 0.01))
            }
            else -> {}
        }
        return@ringKinshipPlugin CombatMod()
    },
    steelTitanPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (target !is Player) return@steelTitanPlugin CombatMod()
        if (target.familiarPouch === Pouch.STEEL_TITAN) return@steelTitanPlugin CombatMod(defense = 1.15)
        return@steelTitanPlugin CombatMod()
    },
    spellcasterPlugin@ { entity, target, offensiveBonus, combatStyle ->
        if (entity.tempAttribs.getO<Any?>("spellcasterProc") != null) {
            target.lowerStat(Skills.ATTACK, 0.1, 0.9)
            target.lowerStat(Skills.STRENGTH, 0.1, 0.9)
            target.lowerStat(Skills.DEFENSE, 0.1, 0.9)
            if (target is Player) target.sendMessage("Your melee skills have been drained.")
            (entity as? Player)?.sendMessage("Your spell weakened your enemy.")
            (entity as? Player)?.sendMessage("Your magic surged with extra power.")
            return@spellcasterPlugin CombatMod(baseDamage = 1.25)
        }
        return@spellcasterPlugin CombatMod()
    },
    hexhunterBowPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player || target !is NPC) return@hexhunterBowPlugin CombatMod()
        if (player.equipment.weaponId == 15836 || player.equipment.weaponId == 17295 || player.equipment.weaponId == 21332) {
            val mageLvl = Utils.clampI(target.magicLevel, 0, 350)
            if (player.controllerManager.isIn(DungeonController::class.java) && target.combatDefinitions.attackStyle == CombatStyle.MAGE)
                mageLvl * 2
            val atkMul = (140.0 + floor((3 * mageLvl.toDouble() - 10.0) / 100.0) - floor((0.3 * mageLvl.toDouble() - 100.0).pow(2.0) / 100.0)) / 100.0
            val strMul = (250.0 + floor((3 * mageLvl.toDouble() - 14.0) / 100.0) - floor((0.3 * mageLvl.toDouble() - 140.0).pow(2.0) / 100.0)) / 100.0
            return@hexhunterBowPlugin CombatMod(
                accuracy = Utils.clampD(atkMul, 1.0, 3.0),
                baseDamage = Utils.clampD(strMul, 1.0, 3.0)
            )
        }

        return@hexhunterBowPlugin CombatMod()
    },
    vyrePlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player || target !is NPC || !target.name.startsWith("Vyre")) return@vyrePlugin CombatMod()
        when(combatStyle) {
            CombatStyle.MELEE, CombatStyle.RANGE -> {
                if (player.equipment.weaponId == 21581 || player.equipment.weaponId == 21582) {
                    return@vyrePlugin CombatMod(accuracy = 2.0, baseDamage = 2.0)
                } else if (!(player.equipment.weaponId == 6746 || player.equipment.weaponId == 2961 || player.equipment.weaponId == 2963 || player.equipment.weaponId == 2952 || player.equipment.weaponId == 2402 || (player.equipment.weaponId >= 7639 && player.equipment.weaponId <= 7648) || (player.equipment.weaponId >= 13117 && player.equipment.weaponId <= 13146)))
                    return@vyrePlugin CombatMod(baseDamage = 0.0)
            }
            CombatStyle.MAGE -> return@vyrePlugin if (player.equipment.weaponId == 21580)
                CombatMod(accuracy = 1.5, baseDamage = 1.5) else CombatMod(baseDamage = 0.0)
        }
        return@vyrePlugin CombatMod()
    },
    baneAmmo@{ player, target, offensiveBonus, combatStyle ->
        if (player !is Player || target !is NPC || combatStyle != CombatStyle.RANGE) return@baneAmmo CombatMod()

        val weapon = RangedWeapon.forId(player.equipment.weaponId)
        val ammo = AmmoType.forId(player.equipment.ammoId)
        if (weapon?.ammos?.contains(ammo) != true) return@baneAmmo CombatMod()

        val boosted = CombatMod(accuracy = 1.6, baseDamage = 1.6)
        val targetName = target.name.lowercase(Locale.getDefault())

        val matches = when (ammo) {
            AmmoType.DRAGONBANE_ARROW, AmmoType.DRAGONBANE_BOLT -> "dragon"
            AmmoType.ABYSSALBANE_ARROW, AmmoType.ABYSSALBANE_BOLT -> "abyssal"
            AmmoType.BASILISKBANE_ARROW, AmmoType.BASILISKBANE_BOLT -> "basilisk"
            AmmoType.WALLASALKIBANE_ARROW, AmmoType.WALLASALKIBANE_BOLT -> "wallasalki"
            else -> null
        }

        return@baneAmmo if (matches != null && targetName.contains(matches)) boosted else CombatMod()
    }
)

fun calculateHit(entity: Entity, target: Entity, minHit: Int, maxHit: Int, attackBonus: Bonus, combatStyle: CombatStyle, calcDefense: Boolean, accuracyModifier: Double): Hit {
    val combatModifiers = combatPlugins.map { it(entity, target, attackBonus, combatStyle) }

    val hit = Hit(entity, 0, when(combatStyle) {
        CombatStyle.MELEE -> HitLook.MELEE_DAMAGE
        CombatStyle.RANGE -> HitLook.RANGE_DAMAGE
        CombatStyle.MAGE -> HitLook.MAGIC_DAMAGE
    })
    if (calcDefense) {
        val offensiveStat = when(combatStyle) {
            CombatStyle.MELEE -> Skills.ATTACK
            CombatStyle.RANGE -> Skills.RANGE
            CombatStyle.MAGE -> Skills.MAGIC
        }
        val prayerAccuracyMultiplier = if (entity is Player) when(combatStyle) {
            CombatStyle.MELEE -> entity.prayer.attackMultiplier
            CombatStyle.RANGE -> entity.prayer.rangeMultiplier
            CombatStyle.MAGE -> entity.prayer.mageMultiplier
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

        var defLvl = entity.getLevel(Skills.DEFENSE).toDouble() * ((entity as? Player)?.prayer?.defenceMultiplier ?: 1.0)
        val defBonus = entity.getBonus(attackBonus.invert()).toDouble()
        defLvl = entity.getLevel(Skills.DEFENSE).toDouble() * ((entity as? Player)?.prayer?.defenceMultiplier ?: 1.0)
        if (target is Player) {
            val style = target.combatDefinitions.getAttackStyle()
            if (style.attackType == AttackType.LONG_RANGE || style.xpType == XPType.DEFENSIVE)
                atkLvl += 3.0
            else if (style.xpType == XPType.CONTROLLED)
                atkLvl += 1.0
        }
        defLvl += 8.0

        if (combatStyle == CombatStyle.MAGE && entity is Player && target is Player) {
            defLvl *= 0.3
            var magLvl = floor(target.skills.getLevel(Constants.MAGIC) * target.prayer.mageMultiplier)
            magLvl *= 0.7
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
        CombatStyle.MAGE -> Skills.MAGIC
    }
    val prayerAccuracyMultiplier = if (entity is Player) when(combatStyle) {
        CombatStyle.MELEE -> entity.prayer.strengthMultiplier
        CombatStyle.RANGE -> entity.prayer.rangeMultiplier
        CombatStyle.MAGE -> 1.0
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

    if (combatStyle == CombatStyle.MAGE)
        baseDamage = spellBaseDamage.toDouble()

    for (combatMod in combatModifiers)
        baseDamage *= combatMod.baseDamage

    var maxHit = floor(baseDamage * damageMultiplier)

    if (combatStyle == CombatStyle.MAGE) {
        if (applyMageLevelBoost) {
            val boostedMageLevelBonus = 1 + ((entity.getLevel(Constants.MAGIC) - entity.getLevelForXp(Constants.MAGIC)) * 0.03)
            if (boostedMageLevelBonus > 1) maxHit = (maxHit * boostedMageLevelBonus)
        }
        maxHit = (maxHit * getMagicBonusBoost(entity))
    }

    if (Settings.getConfig().isDebug && (entity as? Player)?.nsv?.getB("hitChance") == true) entity.sendMessage("Your max hit: ${maxHit.toInt()}")
    return maxHit.toInt()
}

fun hasFireCape(player: Player): Boolean {
    val capeId = player.equipment.capeId
    return capeId == 6570 || capeId == 20769 || capeId == 20771 || capeId == 23659
}

fun fullVeracsEquipped(player: Player): Boolean {
    val helmId = player.equipment.hatId
    val chestId = player.equipment.chestId
    val legsId = player.equipment.legsId
    val weaponId = player.equipment.weaponId
    if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1) return false
    return (ItemDefinitions.getDefs(helmId).getName().contains("Verac's") && ItemDefinitions.getDefs(chestId).getName().contains("Verac's") && ItemDefinitions.getDefs(legsId).getName().contains("Verac's")
            && ItemDefinitions.getDefs(weaponId).getName().contains("Verac's"))
}

fun fullDharokEquipped(player: Player): Boolean {
    val helmId = player.equipment.hatId
    val chestId = player.equipment.chestId
    val legsId = player.equipment.legsId
    val weaponId = player.equipment.weaponId
    if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1) return false
    return (ItemDefinitions.getDefs(helmId).getName().contains("Dharok's") && ItemDefinitions.getDefs(chestId).getName().contains("Dharok's") && ItemDefinitions.getDefs(legsId).getName().contains("Dharok's")
            && ItemDefinitions.getDefs(weaponId).getName().contains("Dharok's"))
}

fun fullVoidEquipped(player: Player, vararg helmid: Int): Boolean {
    var hasDeflector = player.equipment.shieldId == 19712
    if (player.equipment.glovesId != 8842) {
        if (!hasDeflector) return false
        hasDeflector = false
    }
    val legsId = player.equipment.legsId
    val hasLegs = legsId != -1 && (legsId == 8840 || legsId == 19786 || legsId == 19788 || legsId == 19790)
    if (!hasLegs) {
        if (!hasDeflector) return false
        hasDeflector = false
    }
    val torsoId = player.equipment.chestId
    val hasTorso = torsoId != -1 && (torsoId == 8839 || torsoId == 10611 || torsoId == 19785 || torsoId == 19787 || torsoId == 19789)
    if (!hasTorso && !hasDeflector)
        return false
    val helmId = player.equipment.hatId
    if (helmId == -1) return false
    var hasHelm = false
    for (id in helmid) if (helmId == id) {
        hasHelm = true
        break
    }
    return hasHelm
}