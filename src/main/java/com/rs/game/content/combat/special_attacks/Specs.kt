package com.rs.game.content.combat.special_attacks

import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent

private val specialAttacks: MutableMap<Int, SpecialAttack> = HashMap()

@ServerStartupEvent
fun mapSpecials() {

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
    if (spec.isInstant) {
        var specAmt = spec.energyCost.toDouble()
        if (player.combatDefinitions.hasRingOfVigour()) specAmt *= 0.9
        if (player.combatDefinitions.getSpecialAttackPercentage() < specAmt) {
            player.sendMessage("You don't have enough power left.")
            player.combatDefinitions.drainSpec(0)
            return
        }
        spec.execute.apply(player, null)
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
    return spec.execute.apply(player, target!!)
}