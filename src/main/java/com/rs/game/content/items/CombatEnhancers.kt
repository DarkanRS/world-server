package com.rs.game.content.items

import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.content.combat.AmmoType
import com.rs.game.content.combat.AttackType
import com.rs.game.content.combat.CombatMod
import com.rs.game.content.combat.CombatSpell
import com.rs.game.content.combat.CombatStyle
import com.rs.game.content.combat.RangedWeapon
import com.rs.game.content.combat.XPType
import com.rs.game.content.combat.onCombatFormulaAdjust
import com.rs.game.content.skills.dungeoneering.DungeonController
import com.rs.game.content.skills.dungeoneering.KinshipPerk
import com.rs.game.content.skills.slayer.Slayer
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.util.Utils.clampD
import com.rs.lib.util.Utils.clampI
import com.rs.plugin.annotations.ServerStartupEvent
import kotlin.math.floor
import kotlin.math.pow

@ServerStartupEvent
fun mapCombatEnhancingItems() {
    onCombatFormulaAdjust berserkerNeck@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@berserkerNeck CombatMod()
        if (setOf(6523, 6525, 6527, 6528).contains(player.equipment.weaponId) && player.equipment.amuletId == 11128)
            return@berserkerNeck CombatMod(baseDamage = 1.2)
        return@berserkerNeck CombatMod()
    }

    onCombatFormulaAdjust dharoks@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@dharoks CombatMod()
        if (setOf(4718, 4886, 4887, 4888, 4889).contains(player.equipment.weaponId) && fullDharokEquipped(player))
            return@dharoks CombatMod(baseDamage = 1.0 + (player.maxHitpoints - player.hitpoints) / 1000.0 * (player.maxHitpoints / 1000.0))
        return@dharoks CombatMod()
    }

    onCombatFormulaAdjust salveAmulet@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@salveAmulet CombatMod()
        if (target !is NPC || !target.definitions.isUndead) return@salveAmulet CombatMod()
        return@salveAmulet when (player.equipment.salveAmulet) {
            0 -> CombatMod(accuracy = 1.15, baseDamage = 1.15)
            1 -> CombatMod(accuracy = 1.20, baseDamage = 1.20)
            else -> CombatMod()
        }
        return@salveAmulet CombatMod()
    }

    onCombatFormulaAdjust slayerHelmPlugin@ { player, target, offensiveBonus, combatStyle ->
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
    }

    onCombatFormulaAdjust auraPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player) return@auraPlugin CombatMod()
        return@auraPlugin when(combatStyle) {
            CombatStyle.RANGE -> CombatMod(accuracyLevel = player.auraManager.rangeAcc)
            CombatStyle.MAGE -> CombatMod(accuracyLevel = player.auraManager.magicAcc)
            else -> null
        } ?: CombatMod()
    }

    onCombatFormulaAdjust ringKinshipPlugin@ { player, target, offensiveBonus, combatStyle ->
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
    }

    onCombatFormulaAdjust spellcasterPlugin@ { entity, target, offensiveBonus, combatStyle ->
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
    }

    onCombatFormulaAdjust hexhunterBowPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player || target !is NPC) return@hexhunterBowPlugin CombatMod()
        if (player.equipment.weaponId == 15836 || player.equipment.weaponId == 17295 || player.equipment.weaponId == 21332) {
            val mageLvl = clampI(target.magicLevel, 0, 350)
            if (player.controllerManager.isIn(DungeonController::class.java) && target.combatDefinitions.attackStyle == CombatStyle.MAGE)
                mageLvl * 2
            val atkMul = (140.0 + floor((3 * mageLvl.toDouble() - 10.0) / 100.0) - floor((0.3 * mageLvl.toDouble() - 100.0).pow(2.0) / 100.0)) / 100.0
            val strMul = (250.0 + floor((3 * mageLvl.toDouble() - 14.0) / 100.0) - floor((0.3 * mageLvl.toDouble() - 140.0).pow(2.0) / 100.0)) / 100.0
            return@hexhunterBowPlugin CombatMod(
                accuracy = clampD(atkMul, 1.0, 3.0),
                baseDamage = clampD(strMul, 1.0, 3.0)
            )
        }

        return@hexhunterBowPlugin CombatMod()
    }

    onCombatFormulaAdjust broadsPlugin@ { player, target, offensiveBonus, combatStyle ->
        if (player !is Player || target !is NPC || (target.name != "Turoth" && target.name != "Kurask"))
            return@broadsPlugin CombatMod()
        when(combatStyle) {
            CombatStyle.RANGE -> {
                val weapon = RangedWeapon.forId(player.equipment.weaponId)
                val ammo = AmmoType.forId(player.equipment.ammoId)
                if (weapon?.ammos?.contains(ammo) != true || !setOf(AmmoType.BROAD_ARROW, AmmoType.BROAD_TIPPED_BOLTS).contains(ammo))
                    return@broadsPlugin CombatMod(overallDamage = 0.0)
            }
            CombatStyle.MELEE -> {
                if (player.equipment.weaponId != 4158 && player.equipment.weaponId != 13290)
                    return@broadsPlugin CombatMod(overallDamage = 0.0)
            }
            CombatStyle.MAGE -> {
                if (player.combatDefinitions.spell != CombatSpell.MAGIC_DART)
                    return@broadsPlugin CombatMod(overallDamage = 0.0)
            }
        }
        return@broadsPlugin CombatMod()
    }

    onCombatFormulaAdjust vyrePlugin@ { player, target, offensiveBonus, combatStyle ->
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
    }
}

private fun fullDharokEquipped(player: Player): Boolean {
    val helmId = player.equipment.hatId
    val chestId = player.equipment.chestId
    val legsId = player.equipment.legsId
    val weaponId = player.equipment.weaponId
    if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1) return false
    return (ItemDefinitions.getDefs(helmId).getName().contains("Dharok's") && ItemDefinitions.getDefs(chestId).getName().contains("Dharok's") && ItemDefinitions.getDefs(legsId).getName().contains("Dharok's")
            && ItemDefinitions.getDefs(weaponId).getName().contains("Dharok's"))
}