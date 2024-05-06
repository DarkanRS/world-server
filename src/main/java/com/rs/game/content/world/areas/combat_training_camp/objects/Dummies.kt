package com.rs.game.content.world.areas.combat_training_camp.objects

import com.rs.game.content.combat.XPType
import com.rs.game.content.combat.getWeaponAttackEmote
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class Dummies(player: Player) {
    init {
        run {
            if (player.skills.getLevelForXp(Constants.ATTACK) >= 8) {
                player.sendMessage("There is nothing more you can learn from hitting a dummy.")
                return@run
            }
            val type: XPType = player.combatDefinitions.getAttackStyle().xpType
            if (type != XPType.ACCURATE && type != XPType.AGGRESSIVE && type != XPType.CONTROLLED && type != XPType.DEFENSIVE) {
                player.sendMessage("You can't hit a dummy with that attack style.")
                return@run
            }
            player.anim(getWeaponAttackEmote(player.equipment.getWeaponId(), player.combatDefinitions.getAttackStyle()))
            player.lock(2)
            player.skills.addXp(Constants.ATTACK, 5.0)
        }
    }
}


@ServerStartupEvent
fun mapCombatTrainingCampDummies() {
    onObjectClick(2038) { (player, _) -> Dummies(player) }
}
