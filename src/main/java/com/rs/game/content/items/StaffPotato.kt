package com.rs.game.content.items

import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.game.World.getServerTicks
import com.rs.game.content.Effect
import com.rs.game.content.Potion
import com.rs.game.content.combat.CombatDefinitions.Spellbook
import com.rs.game.content.minigames.barrows.BarrowsController
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager
import com.rs.game.content.skills.cooking.Foods
import com.rs.game.content.skills.magic.Magic
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.utils.Ticks

@ServerStartupEvent
fun mapStaffPotato() {
    onItemClick(5733, options = arrayOf("Eat", "Heal", "CM-Tool", "Commands", "Drop")) { e ->
        when (e.option) {
            "Drop" -> e.player.sendOptionsDialogue("Drop it? It will be destroyed.") {
                opExec("Yes, drop it.") { e.player.inventory.deleteItem(e.item) }
                op("Nevermind.")
            }

            "Eat" -> {
                if (!e.player.canEat()) return@onItemClick
                e.player.incrementCount("Food eaten")
                e.player.anim(Foods.EAT_ANIM)
                e.player.addFoodDelay(3)
                e.player.actionManager.actionDelay += 3
                e.player.heal(280, 100)
                arrayOf(
                    Effect.OVERLOAD, Effect.BONFIRE, Effect.ANTIPOISON, Effect.SUPER_ANTIFIRE,
                    Effect.JUJU_MINE_BANK, Effect.JUJU_WC_BANK, Effect.JUJU_FARMING, Effect.JUJU_HUNTER,
                    Effect.JUJU_FISHING, Effect.REV_IMMUNE, Effect.EVIL_TREE_WOODCUTTING_BUFF,
                    Effect.SHOOTING_STAR_MINING_BUFF, Effect.OOG_THERMAL_POOL, Effect.OOG_SALTWATER_POOL,
                    Effect.PATCH_BOMB
                ).forEach {
                    e.player.addEffect(it, Ticks.fromHours(1000).toLong())
                }
                arrayOf(
                    Potion.RECOVER_SPECIAL, Potion.SUPER_RESTORE, Potion.SUPER_ENERGY,
                    Potion.SUMMONING_POTION, Potion.STRONG_ARTISANS_POTION, Potion.STRONG_GATHERERS_POTION,
                    Potion.STRONG_NATURALISTS_POTION, Potion.STRONG_SURVIVALISTS_POTION
                ).forEach {
                    it.effect.invoke(e.player)
                }
            }

            "Heal" -> {
                val command = e.player.nsv.getO<Command>("lastPotatoCommand")
                command?.action?.invoke(e.player)
            }

            "Commands" -> e.player.sendOptionsDialogue {
                for (command in Command.entries)
                    opExec(command.cmdName) {
                        command.action.invoke(e.player)
                        e.player.nsv.setO<Any>("lastPotatoCommand", command)
                    }
            }

            "CM-Tool" -> e.player.sendOptionsDialogue("What would you like to do?") {
                val lastLoc = e.player.nsv.getO<Pair<Tile, Controller?>>("savedPotatoLoc")
                if (lastLoc != null)
                    opExec("Teleport to saved location.") {
                        Magic.sendNormalTeleportSpell(e.player, lastLoc.first) {
                            if (lastLoc.second != null) {
                                @Suppress("DEPRECATION")
                                e.player.controllerManager.controller = lastLoc.second
                                e.player.controllerManager.sendInterfaces()
                            }
                        }
                    }
                opExec("Save current location.") {
                    e.player.nsv.setO<Any>("savedPotatoLoc", Tile.of(e.player.tile) to e.player.controllerManager.controller)
                    e.player.sendMessage("Location saved.")
                }
            }
        }
    }

    onItemClick(25357, options = arrayOf("Rub")) { e ->
        val set = !e.player.nsv.getB("godMode")
        arrayOf("godMode", "infSpecialAttack", "infPrayer", "infRun", "infRunes")
            .forEach { e.player.nsv.setB(it, set) }
        e.player.anim(if (set) 529 else 528)
    }
}

private enum class Command(val cmdName: String, val action: (Player) -> Unit) {
    GOD("Toggle God", { p ->
        p.sync(361, 122)
        p.nsv.setB("godMode", !p.nsv.getB("godMode"))
        p.sendMessage("GODMODE: " + p.nsv.getB("godMode"))
    }),
    INF_PRAY("Toggle Infinite Prayer", { p ->
        p.sync(412, 121)
        p.nsv.setB("infPrayer", !p.nsv.getB("infPrayer"))
        p.sendMessage("INFINITE PRAYER: " + p.nsv.getB("infPrayer"))
    }),
    INF_SPEC("Toggle Infinite Spec", { p ->
        p.nsv.setB("infSpecialAttack", !p.nsv.getB("infSpecialAttack"))
        p.sendMessage("INFINITE SPECIAL ATTACK: " + p.nsv.getB("infSpecialAttack"))
    }),
    INF_RUN("Toggle Infinite Run", { p ->
        p.nsv.setB("infRun", !p.nsv.getB("infRun"))
        p.sendMessage("INFINITE RUN: " + p.nsv.getB("infRun"))
    }),
    INF_RUNES("Toggle Infinite Runes", { p ->
        p.nsv.setB("infRunes", !p.nsv.getB("infRunes"))
        p.sendMessage("INFINITE RUNES: " + p.nsv.getB("infRunes"))
    }),
    INVISIBILITY("Toggle Invisibility", { p ->
        p.appearance.isHidden = !p.appearance.isHidden
        p.sendMessage("HIDDEN: " + p.appearance.isHidden)
    }),
    BANK_DROPS("Send drops directly to bank until logout", { it.nsv.setB("sendingDropsToBank", true) }),
    RESET_TASK("Reset slayer task", { p ->
        p.slayer.removeTask()
        p.updateSlayerTask()
    }),
    BANK("Bank", { it.bank.open() }),
    MAGE_BOOK("Magic book", { p ->
        p.sendOptionsDialogue {
            opExec("Modern") { p.combatDefinitions.setSpellbook(Spellbook.MODERN) }
            opExec("Ancient") { p.combatDefinitions.setSpellbook(Spellbook.ANCIENT) }
            opExec("Lunar") { p.combatDefinitions.setSpellbook(Spellbook.LUNAR) }
            opExec("Dungeoneering") { p.combatDefinitions.setSpellbook(Spellbook.DUNGEONEERING) }
        }
    }),
    PRAY_BOOK("Prayer book", { p ->
        p.sendOptionsDialogue {
            opExec("Modern") { p.prayer.setPrayerBook(false) }
            opExec("Ancient curses") { p.prayer.setPrayerBook(true) }
        }
    }),
    LOOT_BARROWS("Loot a barrows chest", { p ->
        if (p.controllerManager.isIn(BarrowsController::class.java)) p.controllerManager.getController(BarrowsController::class.java).cheat()
        else p.sendMessage("You're not at barrows.")
    }),
    CLUES_TO_CASKETS("Turn clue scroll boxes to caskets", { p ->
        for (item in p.inventory.items.array()) {
            if (item == null) continue
            for (i in TreasureTrailsManager.SCROLL_BOXES.indices) {
                if (item.id == TreasureTrailsManager.SCROLL_BOXES[i]) item.id = TreasureTrailsManager.CASKETS[i]
            }
            p.inventory.refresh()
        }
    }),
    INSTA_FARM("Instant grow all farm patches", { for (i in 0..199) it.tickFarming() }),
    NEVER_LOG("Neverlog", { it.nsv.setB("idleLogImmune", true) }),
    AGGRO_POT("Aggro pot toggle", { p ->
        if (p.hasEffect(Effect.AGGRESSION_POTION)) p.removeEffect(Effect.AGGRESSION_POTION)
        else p.addEffect(Effect.AGGRESSION_POTION, Ticks.fromHours(1000).toLong())
        p.sendMessage("Aggression potion: " + p.hasEffect(Effect.AGGRESSION_POTION))
    }),
    NO_RANDOMS("Stop randoms for session", { it.nsv.setL("lastRandom", getServerTicks() + Ticks.fromHours(1000)) })
}
