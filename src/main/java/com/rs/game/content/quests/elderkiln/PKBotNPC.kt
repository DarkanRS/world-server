package com.rs.game.content.quests.elderkiln

import com.rs.cache.loaders.Bonus
import com.rs.game.content.Effect
import com.rs.game.content.combat.AttackStyle.Companion.getStyles
import com.rs.game.content.combat.CombatSpell
import com.rs.game.content.combat.attackTarget
import com.rs.game.content.combat.getMultiAttackTargets
import com.rs.game.content.combat.getWeaponAttackEmote
import com.rs.game.content.world.npcs.max.Max
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.NPCBodyMeshModifier
import com.rs.game.model.entity.npc.combat.CombatScript.*
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.npc.combat.NPCCombatUtil.Companion.castSpellAtTarget
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.item.ItemsContainer
import com.rs.lib.game.GroundItem
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat

@ServerStartupEvent
fun mapPKBotCombat() {
    npcCombat(4336) { npc, target ->
        if (npc is PKBotNPC)
            npc.attack.attackFunc(npc, target)
        else
            CombatScriptsHandler.getDefaultCombat().apply(npc, target)
    }
    instantiateNpc(4336) { _, tile -> PKBotNPC(tile) }
}

private fun PKBotNPC.attack(target: Entity): Int {
    return attack.attackFunc(this, target)
}

private val RENDER_SLOTS = intArrayOf(
    Equipment.HEAD, Equipment.NECK, Equipment.CHEST,
    Equipment.LEGS, Equipment.WEAPON, Equipment.HANDS,
    Equipment.FEET, Equipment.SHIELD, Equipment.CAPE
)

private val DEFAULT_MODELS = intArrayOf(230, 176, 517, 250, 181, 265, 296, 5434, 5430)

class PKBotNPC(tile: Tile): NPC(4336, tile) {
    val equipment = ItemsContainer<Item>(14, false)
    val skills = NPCCombatDefinitions.Skill.entries.associateWith { 99 }.toMutableMap()
    private var hpLevel = 99
    private var equipmentHpIncrease = 0
    var bonuses = IntArray(18)
    var attack = Attack.ICE_BARRAGE
        set(value) {
            field = value
            attackRange = value.range
        }

    init {
        isIntelligentRouteFinder = true
        run = true
        combatLevel = Max.getCombatLevel(
            skills[NPCCombatDefinitions.Skill.ATTACK] ?: 1,
            skills[NPCCombatDefinitions.Skill.STRENGTH] ?: 1,
            skills[NPCCombatDefinitions.Skill.DEFENSE] ?: 1,
            skills[NPCCombatDefinitions.Skill.RANGE] ?: 1,
            skills[NPCCombatDefinitions.Skill.MAGE] ?: 1,
            hpLevel,
            99,
            99
        )
        wear(Equipment.WEAPON, 15486)
        wear(Equipment.SHIELD, 13738)
        wear(Equipment.HEAD, 20159)
        wear(Equipment.CHEST, 20163)
        wear(Equipment.LEGS, 20167)
        wear(Equipment.FEET, 24986)
        wear(Equipment.HANDS, 22366)
        wear(Equipment.NECK, 18335)
        wear(Equipment.CAPE, 20771)
        updateAppearance()
        hitpoints = maxHitpoints
        setPermName("<col=FFFFFF>${randomUsername()}")
        attack = Attack.ICE_BARRAGE
    }

    private fun randomUsername() =
        listOf(
            "Cool", "Smart", "Lucky",
            "Lol", "PkBoy", "Bad"
        ).random() +
        listOf(
            "Wizard", "Rider", "Hawk",
            "Brother", "Zonked", "Champ"
        ).random() + (100..999).random()

    override fun processNPC() {
        super.processNPC()
        if (isDead || isLocked) return
        updateAppearance()
    }

    fun wear(slot: Int, itemId: Int) {
        equipment.set(slot, Item(itemId))
        updateAppearance()
        refreshBonuses()
    }

    private fun refreshBonuses() {
        val items = equipment.itemsCopy
        bonuses = IntArray(18)
        for (item in items) {
            if (item == null) continue
            for (bonus in Bonus.entries) {
                if (bonus == Bonus.RANGE_STR && getBonus(Bonus.RANGE_STR) != 0) continue
                bonuses[bonus.ordinal] += Equipment.getBonus(item, bonus)
            }
        }
        var hpIncrease = 0.0
        items.filterNotNull().forEach {
            when (it.id) {
                20135, 20137, 20147, 20149, 20159, 20161 -> hpIncrease += 66.0
                20139, 20141, 20151, 20153, 20163, 20165 -> hpIncrease += 200.0
                20143, 20145, 20155, 20157, 20167, 20169 -> hpIncrease += 134.0
                24974, 24975, 24977, 24978, 24980, 24981, 24983, 24984, 24986, 24987, 24989, 24990, 25058, 25060, 25062, 25064, 25066, 25068 -> hpIncrease += 25.0
            }
        }
        val maxHp: Int = hpLevel * 10
        if (hasEffect(Effect.BONFIRE)) hpIncrease += (maxHp + hpIncrease.toInt()) * 0.1
        if (hasEffect(Effect.OOG_THERMAL_POOL)) hpIncrease += (maxHp + hpIncrease.toInt()) * 0.03
        if (hpIncrease != equipmentHpIncrease.toDouble())
            equipmentHpIncrease = hpIncrease.toInt()
    }

    override fun drop(killer: Player?) {
        killer?.let {
            equipment.array().forEach { item ->
                if (item == null) return@forEach
                killer.packets.sendGroundItem(GroundItem(item, Tile.of(this.tile)))
            }
        }
    }

    override fun getMaxHitpoints(): Int {
        return hpLevel * 10 + equipmentHpIncrease
    }

    private fun updateAppearance() {
        val models = RENDER_SLOTS.mapIndexed { index, slot ->
            equipment[slot]?.definitions?.maleEquip1 ?: DEFAULT_MODELS[index]
        }.toMutableList()
        if (equipment.get(Equipment.HEAD)?.definitions?.faceMask() == true)
            models.add(252)
        models.padEnd(12, -1)
        bas = equipment.get(Equipment.WEAPON)?.definitions?.renderAnimId ?: 1426
        bodyMeshModifier = NPCBodyMeshModifier(definitions).addModels(*models.toIntArray())
    }

    override fun getLevel(skill: NPCCombatDefinitions.Skill): Int {
        return skills[skill] ?: 1
    }

    override fun getBonus(bonus: Bonus): Int {
        return bonuses[bonus.ordinal]
    }

    private fun <T> MutableList<T>.padEnd(size: Int, element: T) {
        if (this.size >= size) return
        while (this.size < size) {
            this.add(element)
        }
    }
}

enum class Attack(val range: Int, val attackFunc: PKBotNPC.(Entity) -> Int) {
    ICE_BARRAGE(10, { target ->
        val spell = CombatSpell.ICE_BARRAGE
        val delay = spell.cast(this, target)
        val baseDamage = spell.getBaseDamage(this)
        if (baseDamage < 0) {
            val hit = getMagicHit(this, getMaxHit(this, NPCCombatDefinitions.AttackStyle.MAGE, target))
            if (hit.damage > 0) spell.onHit(this, target, hit)
            target.tasks.schedule(delay) {
                if (hit.damage > 0) {
                    target.spotAnim(spell.hitSpotAnim)
                    if (spell.landSound != -1) this.soundEffect(target, spell.landSound, true)
                } else {
                    target.spotAnim(85, 0, 96)
                    if (spell.splashSound != -1) this.soundEffect(target, spell.splashSound, true)
                    else this.soundEffect(target, 227, true)
                }
            }
        } else {
            val hit = castSpellAtTarget(this, target, spell, delay)
            if (spell.isAOE && hit) attackTarget(getMultiAttackTargets(this, target, 1, 9, false)) { nextTarget ->
                castSpellAtTarget(this, nextTarget, spell, delay)
                return@attackTarget true
            }
        }
        CombatSpell.ICE_BARRAGE.getCombatDelay(this)
    }),
    MELEE(0, attack@ { target ->
        val weaponId = equipment.get(Equipment.WEAPON)?.id ?: -1
        anim(getWeaponAttackEmote(weaponId, getStyles(weaponId)[1]!!))
        delayHit(this, 1, target, getMeleeHit(this, getMaxHit(this, 310, NPCCombatDefinitions.AttackStyle.MELEE, target)))
        return@attack 3
    })

}
