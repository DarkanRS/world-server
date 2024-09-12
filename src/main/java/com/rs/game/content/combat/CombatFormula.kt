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
    val hit = getMagicMaxHit(entity, target, baseDamage, applyMageLevelBoost)
    hit.setDamage(Utils.random(1, hit.damage))
    if (hit.damage > 0) if (target is NPC) if (target.id == 9463 && hasFireCape(entity)) hit.setDamage(hit.damage + 40)
    return hit
}

@JvmOverloads
fun calculateHit(entity: Entity, target: Entity, weaponId: Int, attackStyle: AttackStyle, combatStyle: CombatStyle, calcDefense: Boolean = true, accuracyModifier: Double = 1.0, damageModifier: Double = 1.0): Hit {
    return calculateHit(entity, target, 1, getMaxHit(entity, target, weaponId, attackStyle, combatStyle, damageModifier), weaponId, attackStyle, combatStyle, calcDefense, accuracyModifier)
}

fun calculateHit(entity: Entity, target: Entity, combatStyle: CombatStyle, calcDefense: Boolean, accuracyModifier: Double, damageModifier: Double): Hit {
    return calculateHit(entity, target, 1, getMaxHit(entity, target, entity.equipment.weaponId, entity.combatDefinitions.getAttackStyle(), combatStyle, damageModifier), entity.equipment.weaponId, entity.combatDefinitions.getAttackStyle(), combatStyle, calcDefense, accuracyModifier)
}

fun calculateHit(entity: Entity, target: Entity, combatStyle: CombatStyle): Hit {
    return calculateHit(entity, target, entity.equipment.weaponId, entity.combatDefinitions.getAttackStyle(), combatStyle, true, 1.0, 1.0)
}

fun calculateHit(entity: Entity, target: Entity, minHit: Int, maxHit: Int, combatStyle: CombatStyle, calcDefense: Boolean, accuracyModifier: Double): Hit {
    return calculateHit(entity, target, minHit, maxHit, entity.equipment.weaponId, entity.combatDefinitions.getAttackStyle(), combatStyle, calcDefense, accuracyModifier)
}

data class CombatMod(
    val accuracyLevel: Double = 1.0,
    val accuracy: Double = 1.0,
    val maxhit: Double = 1.0,
    val damage: Double = 1.0,
    val defenseLevel: Double = 1.0,
    val defense: Double = 1.0,
)

val combatPlugins = listOf<(Entity, Entity, Bonus, CombatStyle) -> (CombatMod)>(
    voidPlugin@ { player, target, attackStyle, combatStyle ->
        if (player !is Player) return@voidPlugin CombatMod()
        return@voidPlugin when(combatStyle) {
            CombatStyle.MELEE -> if (fullVoidEquipped(player, 11665, 11676)) CombatMod(
                accuracyLevel = 1.1,
                maxhit = 1.1
            ) else null
            CombatStyle.RANGE -> if (fullVoidEquipped(player, 11664, 11675)) CombatMod(
                accuracyLevel = 1.1,
                maxhit = 1.1
            ) else null
            CombatStyle.MAGE -> if (fullVoidEquipped(player, 11663, 11674)) CombatMod(accuracyLevel = 1.3) else null
            else -> null
        } ?: CombatMod()
    },
    salveAmulet@ { player, target, attackStyle, combatStyle ->
        if (player !is Player) return@salveAmulet CombatMod()
        if (target !is NPC || !target.definitions.isUndead) return@salveAmulet CombatMod()
        return@salveAmulet when (player.equipment.salveAmulet) {
            0 -> CombatMod(accuracy = 1.15, maxhit = 1.15)
            1 -> CombatMod(accuracy = 1.20, maxhit = 1.20)
            else -> CombatMod()
        }
        return@salveAmulet CombatMod()
    },
    slayerHelmPlugin@ { player, target, attackStyle, combatStyle ->
        if (player !is Player) return@slayerHelmPlugin CombatMod()
        if (!player.hasSlayerTask() || target !is NPC || !player.slayer.isOnTaskAgainst(target))
            return@slayerHelmPlugin CombatMod()

        val mod = CombatMod(accuracy = 7.0 / 6.0, maxhit = 7.0 / 6.0)

        return@slayerHelmPlugin when (combatStyle) {
            CombatStyle.MELEE -> if (Slayer.hasBlackMask(player)) mod else CombatMod()
            CombatStyle.RANGE -> if (Slayer.hasFocusSight(player)) mod else CombatMod()
            CombatStyle.MAGE -> if (Slayer.hasHexcrest(player)) mod else CombatMod()
            else -> CombatMod()
        }
    },
    auraPlugin@ { player, target, attackStyle, combatStyle ->
        if (player !is Player) return@auraPlugin CombatMod()
        return@auraPlugin when(combatStyle) {
            CombatStyle.RANGE -> CombatMod(accuracyLevel = player.auraManager.rangeAcc)
            CombatStyle.MAGE -> CombatMod(accuracyLevel = player.auraManager.magicAcc)
            else -> null
        } ?: CombatMod()
    },
    ringKinshipPlugin@ { player, target, attackStyle, combatStyle ->
        if (player !is Player) return@ringKinshipPlugin CombatMod()
        if (combatStyle == CombatStyle.MELEE && attackStyle.xpType == XPType.ACCURATE && player.dungManager.activePerk == KinshipPerk.TACTICIAN && player.controllerManager.isIn(DungeonController::class.java))
            CombatMod(accuracy = 1.1 + (player.dungManager.getKinshipTier(KinshipPerk.TACTICIAN) * 0.01))
        else
            CombatMod()
    }
)

fun getMagicMaxHit(entity: Entity, target: Entity, spellBaseDamage: Int, applyMageLevelBoost: Boolean): Hit {
    var lvl = floor(entity.getLevel(Constants.MAGIC) * ((entity as? Player)?.prayer?.mageMultiplier ?: 1.0))
    lvl += 8.0
    val atkBonus = player.combatDefinitions.getBonus(Bonus.MAGIC_ATT).toDouble()

    var atk = floor(lvl * (atkBonus + 64))
    var maxHit = spellBaseDamage

    var def = 0.0
    if (target is Player) {
        var defLvl = floor(target.skills.getLevel(Constants.DEFENSE) * target.prayer.defenceMultiplier)
        defLvl += (if (target.combatDefinitions.getAttackStyle().attackType == AttackType.LONG_RANGE || target.combatDefinitions.getAttackStyle().xpType == XPType.DEFENSIVE) 3 else if (target.combatDefinitions.getAttackStyle().xpType == XPType.CONTROLLED) 1 else 0).toDouble()
        defLvl += 8.0
        defLvl *= 0.3
        var magLvl = floor(target.skills.getLevel(Constants.MAGIC) * target.prayer.mageMultiplier)
        magLvl *= 0.7

        val totalDefLvl = defLvl + magLvl

        val defBonus = target.combatDefinitions.getBonus(Bonus.MAGIC_DEF).toDouble()

        def = floor(totalDefLvl * (defBonus + 64))
    } else (target as? NPC)?.let { npc ->
        if (npc.name.startsWith("Vyre")) if (player.equipment.weaponId == 21580) {
            atk *= 1.5
            maxHit = (maxHit * 1.5).toInt()
        } else maxHit = 0
        if (npc.name == "Turoth" || npc.name == "Kurask") if (player.equipment.weaponId != 4170) maxHit = 0
        var defLvl = npc.magicLevel.toDouble()
        val defBonus = npc.definitions.magicDef.toDouble()
        defLvl += 8.0
        def = floor(defLvl * (defBonus + 64))
    }

    val prob = if (atk > def) (1 - (def + 2) / (2 * (atk + 1))) else (atk / (2 * (def + 1)))
    if (Settings.getConfig().isDebug && player.nsv.getB("hitChance")) player.sendMessage("Your hit chance: " + Utils.formatDouble(prob * 100.0) + "%")
    if (prob <= Math.random()) return Hit(player, 0, HitLook.MAGIC_DAMAGE)

    if (applyMageLevelBoost) {
        val boostedMageLevelBonus = 1 + ((player.skills.getLevel(Constants.MAGIC) - player.skills.getLevelForXp(Constants.MAGIC)) * 0.03)
        if (boostedMageLevelBonus > 1) maxHit = (maxHit * boostedMageLevelBonus).toInt()
    }
    maxHit = (maxHit * getMagicBonusBoost(player)).toInt()
    if (player.tempAttribs.getO<Any?>("spellcasterProc") != null) {
        if (spellBaseDamage > 60) {
            maxHit = (maxHit * 1.25).toInt()
            target.lowerStat(Skills.ATTACK, 0.1, 0.9)
            target.lowerStat(Skills.STRENGTH, 0.1, 0.9)
            target.lowerStat(Skills.DEFENSE, 0.1, 0.9)
            if (target is Player) target.sendMessage("Your melee skills have been drained.")
            player.sendMessage("Your spell weakened your enemy.")
            player.sendMessage("Your magic surged with extra power.")
        }
    }
    if (player.hasSlayerTask()) if (target is NPC && player.slayer.isOnTaskAgainst(target as NPC?)) if (player.equipment.wearingHexcrest() || player.equipment.wearingSlayerHelmet()) maxHit = (maxHit * 1.15).toInt()
    val finalMaxHit = maxHit.toDouble().toInt()
    if (Settings.getConfig().isDebug && player.nsv.getB("hitChance")) player.sendMessage("Your max hit: $finalMaxHit")
    return Hit(player, finalMaxHit, HitLook.MAGIC_DAMAGE).setMaxHit(finalMaxHit)
}

fun getMagicBonusBoost(entity: Entity): Double {
    return if (entity is Player)
        entity.combatDefinitions.getBonus(Bonus.MAGIC_STR) / 100.0 + 1.0
    else if (entity is NPC)
        entity.getBonus(Bonus.MAGIC_STR) / 100.0 + 1.0
    else
        1.0
}

fun calculateHit(entity: Entity, target: Entity, minHit: Int, maxHit: Int, attackBonus: Bonus, combatStyle: CombatStyle, calcDefense: Boolean, accuracyModifier: Double): Hit {
    val combatModifiers = combatPlugins.map { it(entity, target, attackBonus, combatStyle) }

    var finalMaxHit = maxHit
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

        var def = 0.0
        if (target is Player) {
            var defLvl = floor(target.skills.getLevel(Constants.DEFENSE) * target.prayer.defenceMultiplier)
            defLvl += (if (target.combatDefinitions.getAttackStyle().attackType == AttackType.LONG_RANGE || target.combatDefinitions.getAttackStyle().xpType == XPType.DEFENSIVE) 3 else if (target.combatDefinitions.getAttackStyle().xpType == XPType.CONTROLLED) 1 else 0).toDouble()
            defLvl += 8.0
            val defBonus = target.combatDefinitions.getDefenseBonusForStyle(entity.combatDefinitions.getAttackStyle()).toDouble()

            def = floor(defLvl * (defBonus + 64))

            if (!ranging) if (target.familiarPouch === Pouch.STEEL_TITAN) def *= 1.15
        } else (target as? NPC)?.let { npc ->
            val wId = player.equipment.weaponId
            if (wId == 15836 || wId == 17295 || wId == 21332) {
                val mageLvl = Utils.clampI(npc.magicLevel, 0, 350)
                if (player.controllerManager.isIn(DungeonController::class.java) && npc.combatDefinitions.attackStyle == CombatStyle.MAGE)
                    mageLvl * 2
                val atkMul = (140.0 + floor((3 * mageLvl.toDouble() - 10.0) / 100.0) - floor((0.3 * mageLvl.toDouble() - 100.0).pow(2.0) / 100.0)) / 100.0
                atk *= Utils.clampD(atkMul, 1.0, 3.0)
                val strMul = (250.0 + floor((3 * mageLvl.toDouble() - 14.0) / 100.0) - floor((0.3 * mageLvl.toDouble() - 140.0).pow(2.0) / 100.0)) / 100.0
                finalMaxHit = (finalMaxHit * Utils.clampD(strMul, 1.0, 3.0)).toInt()
            }
            if (npc.name.startsWith("Vyre")) {
                if (wId == 21581 || wId == 21582) {
                    atk *= 2.0
                    finalMaxHit *= 2
                } else if (!(wId == 6746 || wId == 2961 || wId == 2963 || wId == 2952 || wId == 2402 || (wId >= 7639 && wId <= 7648) || (wId >= 13117 && wId <= 13146))) finalMaxHit = 0
            }
            if (npc.name == "Turoth" || npc.name == "Kurask") {
                if (!(wId == 4158 || wId == 13290) && !(player.equipment.weaponName.contains("bow") && ItemDefinitions.getDefs(player.equipment.ammoId).name.lowercase(Locale.getDefault()).contains("broad"))) finalMaxHit = 0
            }
            val weapon = RangedWeapon.forId(weaponId)
            val ammo = AmmoType.forId(player.equipment.ammoId)
            if (ranging && weapon != null && weapon.ammos != null && weapon.ammos!!.contains(ammo)) {
                when (ammo) {
                    AmmoType.DRAGONBANE_ARROW, AmmoType.DRAGONBANE_BOLT -> {
                        if (npc.name.lowercase(Locale.getDefault()).contains("dragon")) {
                            atk *= 1.6
                            finalMaxHit = (finalMaxHit * 1.6).toInt()
                        }
                    }

                    AmmoType.ABYSSALBANE_ARROW, AmmoType.ABYSSALBANE_BOLT -> {
                        if (npc.name.lowercase(Locale.getDefault()).contains("abyssal")) {
                            atk *= 1.6
                            finalMaxHit = (finalMaxHit * 1.6).toInt()
                        }
                    }

                    AmmoType.BASILISKBANE_ARROW, AmmoType.BASILISKBANE_BOLT -> {
                        if (npc.name.lowercase(Locale.getDefault()).contains("basilisk")) {
                            atk *= 1.6
                            finalMaxHit = (finalMaxHit * 1.6).toInt()
                        }
                    }

                    AmmoType.WALLASALKIBANE_ARROW, AmmoType.WALLASALKIBANE_BOLT -> {
                        if (npc.name.lowercase(Locale.getDefault()).contains("wallasalki")) {
                            atk *= 1.6
                            finalMaxHit = (finalMaxHit * 1.6).toInt()
                        }
                    }

                    else -> {}
                }
            }
            var defLvl = npc.defenseLevel.toDouble()
            val defBonus = entity.combatDefinitions.getAttackStyle().attackType.getDefenseBonus(npc).toDouble()
            defLvl += 8.0
            def = floor(defLvl * (defBonus + 64))
        }
        if (finalMaxHit != 0 && fullVeracsEquipped(player) && Utils.random(4) == 0) veracsProc = true
        val prob = if (atk > def) (1 - (def + 2) / (2 * (atk + 1))) else (atk / (2 * (def + 1)))
        if (Settings.getConfig().isDebug && player.nsv.getB("hitChance")) player.sendMessage("Your hit chance: " + Utils.formatDouble(prob * 100.0) + "%")
        if (prob <= Math.random() && !veracsProc) return hit.setDamage(0)
    }
    if (Settings.getConfig().isDebug && player.nsv.getB("hitChance")) player.sendMessage("Modified max hit: $finalMaxHit")
    var finalHit = Utils.random(minHit, finalMaxHit)
    if (veracsProc) finalHit = (finalHit + 1.0).toInt()
    if (target is NPC) if (target.id == 9463 && hasFireCape(player)) finalHit += 40
    if (player.auraManager.isActivated(Aura.EQUILIBRIUM)) {
        val perc25MaxHit = (finalMaxHit * 0.25).toInt()
        finalHit -= perc25MaxHit
        finalMaxHit -= perc25MaxHit
        if (finalHit < 0) finalHit = 0
        if (finalHit < perc25MaxHit) finalHit += perc25MaxHit
    }
    hit.setMaxHit(finalMaxHit)
    hit.setDamage(finalHit)
    return hit
}

fun getMaxHit(player: Player, target: Entity?, combatStyle: CombatStyle, damageMultiplier: Double): Int {
    return getMaxHit(player, target, player.equipment.weaponId, player.combatDefinitions.getAttackStyle(), combatStyle, damageMultiplier)
}

fun getMaxHit(player: Player, target: Entity?, weaponId: Int, attackStyle: AttackStyle, combatStyle: CombatStyle, damageMultiplier: Double): Int {
    if (ranging) {
        if (target != null && weaponId == 24338 && target is Player) {
            player.sendMessage("The royal crossbow feels weak and unresponsive against other players.")
            return 60
        }
        var lvl = floor(player.skills.getLevel(Constants.RANGE) * player.prayer.rangeMultiplier)
        lvl += (if (attackStyle.attackType == AttackType.ACCURATE) 3 else 0).toDouble()
        lvl += 8.0
        if (fullVoidEquipped(player, 11664, 11675)) lvl = floor(lvl * 1.1)
        if (attackStyle.attackType == AttackType.RAPID && player.dungManager.activePerk == KinshipPerk.DESPERADO && player.controllerManager.isIn(DungeonController::class.java)) lvl = floor(lvl * 1.1 + (player.dungManager.getKinshipTier(KinshipPerk.DESPERADO) * 0.01))
        val str = player.combatDefinitions.getBonus(Bonus.RANGE_STR).toDouble()
        val baseDamage = 5 + lvl * (str + 64) / 64
        val maxHit = floor(baseDamage * damageMultiplier).toInt()
        if (Settings.getConfig().isDebug && player.nsv.getB("hitChance")) player.sendMessage("Your max hit: $maxHit")
        return maxHit
    }
    var lvl = floor(player.skills.getLevel(Constants.STRENGTH) * player.prayer.strengthMultiplier)
    lvl += (if (attackStyle.xpType == XPType.AGGRESSIVE) 3 else if (attackStyle.xpType == XPType.CONTROLLED) 1 else 0).toDouble()
    lvl += 8.0
    if (fullVoidEquipped(player, 11665, 11676)) lvl = floor(lvl * 1.1)
    if (attackStyle.xpType == XPType.AGGRESSIVE && player.dungManager.activePerk == KinshipPerk.BERSERKER && player.controllerManager.isIn(DungeonController::class.java)) lvl = floor(lvl * 1.1 + (player.dungManager.getKinshipTier(KinshipPerk.BERSERKER) * 0.01))
    var str = player.combatDefinitions.getBonus(Bonus.MELEE_STR).toDouble()
    if (weaponId == -2) str += 82.0
    var baseDamage = 5 + lvl * (str + 64) / 64

    when (weaponId) {
        6523, 6525, 6527, 6528 -> if (player.equipment.amuletId == 11128) baseDamage *= 1.2
        4718, 4886, 4887, 4888, 4889 -> if (fullDharokEquipped(player)) {
            val mul = 1.0 + (player.maxHitpoints - player.hitpoints) / 1000.0 * (player.maxHitpoints / 1000.0)
            baseDamage *= mul
        }

        10581, 10582, 10583, 10584 -> if (target != null && target is NPC) if (target.name.startsWith("Kalphite")) baseDamage *= if (Utils.random(51) == 0) 3.0
        else 4.0 / 3.0

        15403, 22405 -> if (target != null && target is NPC) if (target.name == "Dagannoth" || (target.name == "Wallasalki") || (target.name == "Dagannoth Supreme")) baseDamage *= 2.75
        6746 -> if (target != null && target is NPC) if (target.name.lowercase(Locale.getDefault()).contains("demon")) baseDamage *= 1.6
        else -> {}
    }
    //int multiplier = PluginManager.handle()
    val maxHit = floor(baseDamage * damageMultiplier).toInt()
    if (Settings.getConfig().isDebug && player.nsv.getB("hitChance")) player.sendMessage("Your max hit: $maxHit")
    return maxHit
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