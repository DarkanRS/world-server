package com.rs.game.content.quests.elderkiln

import com.rs.cache.loaders.Bonus
import com.rs.game.content.combat.AttackStyle.Companion.getStyles
import com.rs.game.content.combat.CombatSpell
import com.rs.game.content.combat.getWeaponAttackEmote
import com.rs.game.content.world.npcs.max.Max
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.NPCBodyMeshModifier
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.npc.combat.CombatScript.*
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.item.ItemsContainer
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat

const val NEXT_ATTACK_KEY = "currentFightPitAttack"

@ServerStartupEvent
fun mapFightpitsPlayerCombat() {
    npcCombat(LOLTHENKILL, ODISCHAMP, FIRECAPEZORZ, NOREMORSE77, FIGHTPITPKER) { npc, target ->
        if (npc is FightPitFakePlayer)
            npc.attack(target)
        else
            CombatScriptsHandler.getDefaultCombat().apply(npc, target)
    }
    instantiateNpc(15173) { npcId, tile -> FightPitFakePlayer(npcId, tile) }
}

private fun FightPitFakePlayer.attack(target: Entity): Int {
    val nextAttack = nsv.getO<FightPitFakePlayer.(Entity) -> Int>(NEXT_ATTACK_KEY) ?: return 0
    return nextAttack(target)
}

private fun NPC.setNextAttack(attackFunc: NPC.(Entity) -> Int) {
    nsv.setO<NPC.(Entity) -> Int>(NEXT_ATTACK_KEY, attackFunc)
}

class FightPitFakePlayer(npcId: Int, tile: Tile): NPC(npcId, tile) {
    val equipment = ItemsContainer<Item>(14, false)
    val skills = NPCCombatDefinitions.Skill.entries.associateWith { 99 }.toMutableMap()

    init {
        setPermName(randomUsername())
        combatLevel = Max.getCombatLevel(
            skills[NPCCombatDefinitions.Skill.ATTACK] ?: 1,
            skills[NPCCombatDefinitions.Skill.STRENGTH] ?: 1,
            skills[NPCCombatDefinitions.Skill.DEFENSE] ?: 1,
            skills[NPCCombatDefinitions.Skill.RANGE] ?: 1,
            skills[NPCCombatDefinitions.Skill.MAGE] ?: 1,
            99,
            99,
            99
        )
        updateAppearance()
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
    }

    private fun updateAppearance() {
        bodyMeshModifier = NPCBodyMeshModifier(definitions)
            .addModels(
                29055, //body 29055
                28987, //legs 28987
                28582, //head 28582
                4955, //feet 4955
                4953, //gloves 4953
                6232, //weapon 6232
                556, //shield 556
                60692, //amulet 60692
                248 //face 248
            )
    }

    override fun getLevel(skill: NPCCombatDefinitions.Skill): Int {
        return super.getLevel(skill)
    }

    override fun getBonus(bonus: Bonus): Int {
        return super.getBonus(bonus)
    }
}

private enum class Attack(val range: Int, val attackFunc: FightPitFakePlayer.(Entity) -> Int) {
    ICE_BARRAGE(10, { target -> CombatSpell.ICE_BARRAGE.cast(this, target) }),
    MELEE(0, attack@ { target ->
        val weaponId = equipment.get(Equipment.WEAPON)?.id ?: -1
        anim(getWeaponAttackEmote(weaponId, getStyles(weaponId)[1]!!))
        delayHit(this, 1, target, getMeleeHit(this, getMaxHit(this, 310, NPCCombatDefinitions.AttackStyle.MELEE, target)))
        return@attack 3
    })

}
