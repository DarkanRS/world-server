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

import com.rs.cache.loaders.Bonus
import com.rs.engine.quest.Quest
import com.rs.game.content.minigames.disableMinigameRunes
import com.rs.game.content.minigames.enableMinigameRunes
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.AuraManager
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.lib.util.Logger

class CombatDefinitions {
    enum class Spellbook(@JvmField val interfaceId: Int) {
        MODERN(192),
        ANCIENT(193),
        LUNAR(430),
        DUNGEONEERING(950)
    }

    @Transient
    private lateinit var player: Player

    @Transient
    var isUsingSpecialAttack: Boolean = false
        private set

    @Transient
    private lateinit var bonuses: IntArray

    @Transient
    var isDungSpellbook: Boolean = false
        private set

    private var attackStyle: Byte = 0
    private var specialAttackPercentage: Byte = 100
    var isAutoRetaliate: Boolean = true
    private var sortSpellBook: Byte = 0
    private var showCombatSpells = true
    private var showSkillSpells = true
    private var showMiscSpells = true
    private var showTeleportSpells = true
    var isDefensiveCasting: Boolean = false
        private set
    private var spellbook: Spellbook = Spellbook.MODERN
    var autoCast: CombatSpell? = null
        private set

    val spell: CombatSpell?
        get() {
            val spell = player.tempAttribs.getO<CombatSpell>("manualCastSpell")
            if (spell != null) return spell
            return autoCast
        }

    fun hasManualCastQueued(): Boolean {
        return player.tempAttribs.getO<Any?>("manualCastSpell") != null
    }

    fun setManualCastSpell(spell: CombatSpell?) {
        player.tempAttribs.setO<Any>("manualCastSpell", spell)
    }

    fun clearManualCastSpell() {
        player.tempAttribs.removeO<Any>("manualCastSpell")
    }

    fun resetSpells(removeAutoSpell: Boolean) {
        clearManualCastSpell()
        if (removeAutoSpell) {
            setAutoCastSpell(null)
            refreshAutoCastSpell()
        }
    }

    fun setAutoCastSpell(spell: CombatSpell?) {
        autoCast = spell
        refreshAutoCastSpell()
    }

    fun refreshAutoCastSpell() {
        refreshAttackStyle()
        player.vars.setVar(108, spellAutoCastConfigValue)
    }

    private val spellAutoCastConfigValue: Int
        get() {
            if (autoCast == null) return 0
            when {
                isDungSpellbook -> return when (autoCast) {
                    CombatSpell.WIND_STRIKE -> 103
                    CombatSpell.WATER_STRIKE -> 105
                    CombatSpell.EARTH_STRIKE -> 107
                    CombatSpell.FIRE_STRIKE -> 109
                    CombatSpell.WIND_BOLT -> 111
                    CombatSpell.WATER_BOLT -> 113
                    CombatSpell.EARTH_BOLT -> 115
                    CombatSpell.FIRE_BOLT -> 117
                    CombatSpell.WIND_BLAST -> 119
                    CombatSpell.WATER_BLAST -> 121
                    CombatSpell.EARTH_BLAST -> 123
                    CombatSpell.FIRE_BLAST -> 125
                    CombatSpell.WIND_WAVE -> 127
                    CombatSpell.WATER_WAVE -> 129
                    CombatSpell.EARTH_WAVE -> 131
                    CombatSpell.FIRE_WAVE -> 133
                    CombatSpell.WIND_SURGE -> 135
                    CombatSpell.WATER_SURGE -> 137
                    CombatSpell.EARTH_SURGE -> 139
                    CombatSpell.FIRE_SURGE -> 141
                    else -> 0
                }

                spellbook == Spellbook.MODERN -> return when (autoCast) {
                    CombatSpell.WIND_STRIKE -> 3
                    CombatSpell.WATER_STRIKE -> 5
                    CombatSpell.EARTH_STRIKE -> 7
                    CombatSpell.FIRE_STRIKE -> 9
                    CombatSpell.WIND_BOLT -> 11
                    CombatSpell.WATER_BOLT -> 13
                    CombatSpell.EARTH_BOLT -> 15
                    CombatSpell.FIRE_BOLT -> 17
                    CombatSpell.CRUMBLE_UNDEAD -> 35
                    CombatSpell.WIND_BLAST -> 19
                    CombatSpell.WATER_BLAST -> 21
                    CombatSpell.EARTH_BLAST -> 23
                    CombatSpell.FIRE_BLAST -> 25
                    CombatSpell.IBAN_BLAST -> 45
                    CombatSpell.MAGIC_DART -> 37
                    CombatSpell.SARADOMIN_STRIKE -> 41
                    CombatSpell.CLAWS_OF_GUTHIX -> 39
                    CombatSpell.FLAMES_OF_ZAMORAK -> 43
                    CombatSpell.WIND_WAVE -> 27
                    CombatSpell.WATER_WAVE -> 29
                    CombatSpell.EARTH_WAVE -> 31
                    CombatSpell.FIRE_WAVE -> 33
                    CombatSpell.WIND_SURGE -> 47
                    CombatSpell.WATER_SURGE -> 49
                    CombatSpell.EARTH_SURGE -> 51
                    CombatSpell.FIRE_SURGE -> 53
                    CombatSpell.WIND_RUSH -> 143
                    CombatSpell.STORM_OF_ARMADYL -> 145
                    else -> 0
                }

                spellbook == Spellbook.ANCIENT -> return when (autoCast) {
                    CombatSpell.SMOKE_RUSH -> 63
                    CombatSpell.SHADOW_RUSH -> 65
                    CombatSpell.BLOOD_RUSH -> 67
                    CombatSpell.ICE_RUSH -> 69
                    CombatSpell.SMOKE_BURST -> 71
                    CombatSpell.SHADOW_BURST -> 73
                    CombatSpell.BLOOD_BURST -> 75
                    CombatSpell.ICE_BURST -> 77
                    CombatSpell.SMOKE_BLITZ -> 79
                    CombatSpell.SHADOW_BLITZ -> 81
                    CombatSpell.BLOOD_BLITZ -> 83
                    CombatSpell.ICE_BLITZ -> 85
                    CombatSpell.SMOKE_BARRAGE -> 87
                    CombatSpell.SHADOW_BARRAGE -> 89
                    CombatSpell.BLOOD_BARRAGE -> 91
                    CombatSpell.ICE_BARRAGE -> 93
                    CombatSpell.MIASMIC_RUSH -> 95
                    CombatSpell.MIASMIC_BURST -> 97
                    CombatSpell.MIASMIC_BLITZ -> 99
                    CombatSpell.MIASMIC_BARRAGE -> 101
                    else -> 0
                }

            }
            return 0
        }

    fun setSpellbook(book: Spellbook) {
        if (book == Spellbook.LUNAR && !player.isQuestComplete(
                Quest.LUNAR_DIPLOMACY,
                "to use the Lunar spellbook."
            )
        ) return
        if (book == Spellbook.ANCIENT && !player.isQuestComplete(
                Quest.DESERT_TREASURE,
                "to use the Ancient spellbook."
            )
        ) return
        if (book == Spellbook.DUNGEONEERING) isDungSpellbook = true
        else spellbook = book
        refreshSpellbook()
        player.interfaceManager.sendSubDefault(InterfaceManager.Sub.TAB_MAGIC)
    }

    fun getSpellbook(): Spellbook? {
        if (isDungSpellbook) return Spellbook.DUNGEONEERING
        return spellbook
    }

    fun switchShowCombatSpells() {
        showCombatSpells = !showCombatSpells
        refreshSpellbookSettings()
    }

    fun switchShowSkillSpells() {
        showSkillSpells = !showSkillSpells
        refreshSpellbookSettings()
    }

    fun switchShowMiscSpells() {
        showMiscSpells = !showMiscSpells
        refreshSpellbookSettings()
    }

    fun switchShowTeleportSkillSpells() {
        showTeleportSpells = !showTeleportSpells
        refreshSpellbookSettings()
    }

    fun switchDefensiveCasting() {
        isDefensiveCasting = !isDefensiveCasting
        refreshSpellbookSettings()
    }

    fun setSortSpellBook(sortId: Int) {
        sortSpellBook = sortId.toByte()
        refreshSpellbookSettings()
    }

    fun refreshSpellbookSettings() {
        player.vars.setVarBit(357, spellbook.ordinal)
        player.vars.setVarBit(5822, sortSpellBook.toInt())
        player.vars.setVarBit(5823, sortSpellBook.toInt())
        player.vars.setVarBit(5824, sortSpellBook.toInt())
        player.vars.setVarBit(7347, sortSpellBook.toInt())

        player.vars.setVarBit(6459, if (showCombatSpells) 0 else 1)
        player.vars.setVarBit(6466, if (showCombatSpells) 0 else 1)
        player.vars.setVarBit(6463, if (showCombatSpells) 0 else 1)
        player.vars.setVarBit(7348, if (showCombatSpells) 0 else 1)

        player.vars.setVarBit(6460, if (showSkillSpells) 0 else 1)
        player.vars.setVarBit(7349, if (showSkillSpells) 0 else 1)

        player.vars.setVarBit(6461, if (showMiscSpells) 0 else 1)
        player.vars.setVarBit(6464, if (showMiscSpells) 0 else 1)
        player.vars.setVarBit(7350, if (showMiscSpells) 0 else 1)

        player.vars.setVarBit(6462, if (showTeleportSpells) 0 else 1)
        player.vars.setVarBit(6467, if (showTeleportSpells) 0 else 1)
        player.vars.setVarBit(6465, if (showTeleportSpells) 0 else 1)
        player.vars.setVarBit(7351, if (showTeleportSpells) 0 else 1)

        player.vars.setVarBit(2668, if (isDefensiveCasting) 1 else 0)
    }

    fun setPlayer(player: Player) {
        this.player = player
        bonuses = IntArray(18)
    }

    fun getBonus(bonus: Bonus): Int {
        return bonuses[bonus.ordinal]
    }

    fun setBonus(bonus: Bonus, `val`: Int) {
        bonuses[bonus.ordinal] = `val`
    }

    fun refreshBonuses() {
        bonuses = IntArray(18)
        for (item in player.equipment.itemsCopy) {
            if (item == null) continue
            for (bonus in Bonus.entries) {
                if (bonus == Bonus.RANGE_STR && getBonus(Bonus.RANGE_STR) != 0) continue
                bonuses[bonus.ordinal] += Equipment.getBonus(player, item, bonus)
            }
        }
    }

    fun resetSpecialAttack() {
        drainSpec(0)
        specialAttackPercentage = 100
        refreshSpecialAttackPercentage()
    }

    fun setSpecialAttack(special: Int) {
        drainSpec(0)
        specialAttackPercentage = special.toByte()
        refreshSpecialAttackPercentage()
    }

    fun restoreSpecialAttack() {
        if (player.familiar != null) player.familiar.restoreSpecialAttack(15)
        if (specialAttackPercentage.toInt() == 100) return
        var toRestore = 10
        if (player.auraManager.isActivated(AuraManager.Aura.INVIGORATE)) toRestore = 12
        else if (player.auraManager.isActivated(AuraManager.Aura.GREATER_INVIGORATE)) toRestore = 15
        else if (player.auraManager.isActivated(AuraManager.Aura.MASTER_INVIGORATE)) toRestore = 17
        else if (player.auraManager.isActivated(AuraManager.Aura.SUPREME_INVIGORATE)) toRestore = 20
        restoreSpecialAttack(toRestore)
        if (specialAttackPercentage.toInt() == 100 || specialAttackPercentage.toInt() == 50) player.sendMessage(
            "<col=00FF00>Your special attack energy is now $specialAttackPercentage%.",
            true
        )
    }

    fun restoreSpecialAttack(percentage: Int) {
        if (specialAttackPercentage >= 100 || player.interfaceManager.containsScreenInter()) return
        specialAttackPercentage =
            (specialAttackPercentage + if (specialAttackPercentage > (100 - percentage)) 100 - specialAttackPercentage else percentage).toByte()
        refreshSpecialAttackPercentage()
    }

    fun init() {
        refreshUsingSpecialAttack()
        refreshSpecialAttackPercentage()
        refreshAutoRelatie()
        refreshAttackStyle()
        refreshSpellbook()
    }

    fun refreshSpellbook() {
        refreshSpellbookSettings()
        refreshAutoCastSpell()
        if (isDungSpellbook)
            disableMinigameRunes(player)
        else
            enableMinigameRunes(player)
        player.vars.syncVarsToClient()
        player.packets.sendRunScriptBlank(2057)
    }

    fun checkAttackStyle() {
        if (autoCast == null) setAttackStyle(attackStyle.toInt())
    }

    fun setAttackStyle(style: Int) {
        var finalStyle = style
        val styles = AttackStyle.getStyles(
            player.equipment.weaponId
        )
        if (finalStyle < 0) finalStyle = 0
        for (i in finalStyle downTo 0) {
            if (styles[i] != null) {
                finalStyle = i
                break
            }
        }
        if (finalStyle != attackStyle.toInt()) {
            attackStyle = finalStyle.toByte()
            if (autoCast != null) resetSpells(true)
            else refreshAttackStyle()
        } else if (autoCast != null) resetSpells(true)
    }

    fun refreshAttackStyle() {
        player.vars.setVar(43, (if (autoCast != null) 4 else attackStyle).toInt())
    }

    fun sendUnlockAttackStylesButtons() {
        for (componentId in 7..10) player.packets.setIFRightClickOps(884, componentId, -1, 0, 0)
    }

    fun switchUsingSpecialAttack() {
        isUsingSpecialAttack = !isUsingSpecialAttack
        refreshUsingSpecialAttack()
    }

    fun drainSpec(amount: Int) {
        var finalAmount = amount
        isUsingSpecialAttack = false
        refreshUsingSpecialAttack()
        if (player.nsv.getB("infSpecialAttack")) finalAmount = 0
        if (finalAmount > 0) {
            specialAttackPercentage = (specialAttackPercentage - finalAmount).toByte()
            refreshSpecialAttackPercentage()
        }
    }

    fun hasRingOfVigour(): Boolean {
        return player.equipment.ringId == 19669
    }

    fun getSpecialAttackPercentage(): Int {
        return specialAttackPercentage.toInt()
    }

    fun refreshUsingSpecialAttack() {
        player.vars.setVar(301, if (isUsingSpecialAttack) 1 else 0)
    }

    fun refreshSpecialAttackPercentage() {
        player.vars.setVar(300, specialAttackPercentage * 10)
    }

    fun switchAutoRetaliate() {
        isAutoRetaliate = !isAutoRetaliate
        refreshAutoRelatie()
    }

    fun refreshAutoRelatie() {
        player.vars.setVar(172, if (isAutoRetaliate) 0 else 1)
    }

    fun getAttackStyle(): AttackStyle {
        val styles = AttackStyle.getStyles(
            player.equipment.weaponId
        )
        var styleIndex = attackStyle.toInt()
        for (i in styleIndex downTo 0) {
            if (styles[i] != null) {
                styleIndex = i
                break
            }
        }
        if (styleIndex != attackStyle.toInt()) setAttackStyle(styleIndex)
        val style = styles[styleIndex]
        if (style != null)
            return style
        else {
            Logger.handle(CombatDefinitions::class.java, "getAttackStyle", Error("Invalid attack style for weapon ${player.equipment.weaponId} on style index $styleIndex"))
            return AttackStyle(0, "Punch", XPType.ACCURATE, AttackType.CRUSH)
        }
    }

    val currentAttackBonus: Bonus
        get() = getAttackStyle().attackType.attBonus

    val currentDefenseBonus: Bonus
        get() = getAttackStyle().attackType.defBonus

    fun removeDungeonneringBook() {
        if (isDungSpellbook) {
            isDungSpellbook = false
            player.interfaceManager.sendSubDefault(InterfaceManager.Sub.TAB_MAGIC)
        }
    }

    val attackStyleId: Int
        get() = attackStyle.toInt()
}
