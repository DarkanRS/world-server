package com.rs.game.content.world.areas.kethsi

import com.rs.engine.pathfinder.Direction
import com.rs.game.content.combat.AmmoType
import com.rs.game.content.combat.CombatMod
import com.rs.game.content.combat.CombatStyle
import com.rs.game.content.combat.RangedWeapon
import com.rs.game.content.combat.onCombatFormulaAdjust
import com.rs.game.content.skills.agility.Agility
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.ItemOnObjectEvent
import com.rs.plugin.events.ObjectClickEvent
import com.rs.plugin.handlers.ItemOnObjectHandler
import com.rs.plugin.handlers.ObjectClickHandler
import com.rs.plugin.kts.onItemOnObject
import com.rs.plugin.kts.onObjectClick
import java.util.Locale
import java.util.function.Consumer

@ServerStartupEvent
fun mapKethsiEffects() {
    onCombatFormulaAdjust baneAmmo@ { player, target, offensiveBonus, combatStyle ->
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

    onObjectClick(61584) { (player) ->
        player.tele(player.transform(if (player.x == 4206) -1 else 1, 0, 0))
    }

    onObjectClick(6655) { (player) ->
        if (player.vars.getVarBit(9833) == 1 || player.inventory.containsItem(21797)) {
            player.simpleDialogue("You search the rubble, but find nothing of interest.")
            return@onObjectClick
        }
        player.itemDialogue(21797, "You find a statue arm.")
        player.inventory.addItem(21797, 1)
    }

    onItemOnObject(objectNamesOrIds = arrayOf(10466), itemNamesOrIds = arrayOf(21797)) { (player) ->
        if (player.vars.getVarBit(9833) == 1) {
            player.simpleDialogue("It doesn't look like the arm will fit.")
            return@onItemOnObject
        }
        player.simpleDialogue("You attach the missing arm to the statue and notice it doing a very vertraut pose. A ramp falls down nearby.")
        player.inventory.deleteItem(21797, 1)
        player.vars.saveVarBit(9833, 1)
    }

    onObjectClick(6754, 6755, 6753) { (player, obj) ->
        val up = obj.id == 6753
        player.useLadder(
            player.transform(
                if (obj.rotation == 1) (if (up) 2 else -2) else if (obj.rotation == 3) (if (up) -2 else 2) else 0,
                if (obj.rotation == 2) (if (up) -2 else 2) else if (obj.rotation == 0) (if (up) 2 else -2) else 0, if (up) 1 else -1)
        )
    }

    onObjectClick(10372, 10390) { (player, obj) ->
        player.walkToAndExecute(obj.tile) {
            player.forceMove(player.transform(if (obj.id == 10372) -3 else 3, 0, 0), 11729, 20, 60)
        }
    }

    onObjectClick(10456) { (player) ->
        Agility.walkToAgility(player, 155, if (player.y < 5709) Direction.NORTH else Direction.SOUTH, 6, 6)
    }

    onObjectClick(6751, 6752) { (player, obj) -> player.useStairs(player.transform(0, if (obj.id == 6751) -5 else 5, if (obj.id == 6751) 1 else -1)) }
}
