package com.rs.game.content.world.areas.desert

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.shop.ShopsHandler

enum class CarpetLocation(val npcId: Int, val tile: Tile) {
    SHANTAY_PASS(2291, Tile.of(3308, 3109, 0)),
    BEDABIN_CAMP(2292, Tile.of(3180, 3045, 0)),
    S_POLLNIVNEACH(2293, Tile.of(3351, 2942, 0)),
    N_POLLNIVNEACH(2294, Tile.of(3349, 3003, 0)),
    UZER(2295, Tile.of(3469, 3113, 0)),
    SOPHANEM(2297, Tile.of(3285, 2813, 0)),
    MENAPHOS(2299, Tile.of(3245, 2813, 0)),
    NARDAH(3020, Tile.of(3401, 2916, 0)),
    MONKEY_COLONY(13237, Tile.of(3227, 2988, 0));

}

fun forId(npcId: Int): CarpetLocation? {
    for (loc in CarpetLocation.entries) if (loc.npcId == npcId) return loc
    return null
}

@ServerStartupEvent
fun mapDesertInteractions() {

    // Magic Carpets
    onNpcClick(2291, 2292, 2293, 2294, 2295, 2297, 2299, 3020, 13237) { (player, npc) ->
        player.startConversation {
            when (forId(npc.id)) {
                CarpetLocation.SHANTAY_PASS ->
                    options("Where would you like to travel?") {
                        opExec("Pollnivneach") { player.tele(CarpetLocation.N_POLLNIVNEACH.tile) }
                        opExec("Bedabin Camp") { player.tele(CarpetLocation.BEDABIN_CAMP.tile) }
                        opExec("Uzer") { player.tele(CarpetLocation.UZER.tile) }
                        opExec("Monkey Colony") { player.tele(CarpetLocation.MONKEY_COLONY.tile) }
                        op("Nevermind")
                    }
                CarpetLocation.N_POLLNIVNEACH, CarpetLocation.UZER, CarpetLocation.BEDABIN_CAMP, CarpetLocation.MONKEY_COLONY ->
                    options("Where would you like to travel?") {
                        opExec("Shantay Pass") { player.tele(CarpetLocation.SHANTAY_PASS.tile) }
                        op("Nevermind")
                    }
                CarpetLocation.S_POLLNIVNEACH ->
                    options("Where would you like to travel?") {
                        opExec("Nardah") { player.tele(CarpetLocation.NARDAH.tile) }
                        opExec("Sophanem Camp") { player.tele(CarpetLocation.SOPHANEM.tile) }
                        opExec("Menaphos") { player.tele(CarpetLocation.MENAPHOS.tile) }
                        op("Nevermind")
                    }
                CarpetLocation.NARDAH, CarpetLocation.SOPHANEM, CarpetLocation.MENAPHOS ->
                    options("Where would you like to travel?") {
                        opExec("Pollnivneach") { player.tele(CarpetLocation.S_POLLNIVNEACH.tile) }
                        op("Nevermind")
                    }
                else -> {}
            }
        }
    }

    // Monkey Colony Rug Merchant
    onLogin { (player) ->
        player.vars.setVarBit(8628, 1)
        player.vars.setVarBit(8628, 1)
        player.vars.setVarBit(8628, 1)
        player.vars.setVarBit(395, 1)
    }

    // Spirit Waterfall
    onObjectClick(10417, 63173) { (player, obj) ->
        if (obj.id == 63173) player.useStairs(Tile.of(3348, 9535, 0))
        else player.useStairs(Tile.of(3370, 3129, 0))
    }

    // Curtain Doors
    onObjectClick(1528) { (player, obj) ->
        if (obj.rotation == 2 || obj.rotation == 0) player.walkOneStep(if (player.x > obj.x) -1 else 1, 0, false)
        else player.walkOneStep(0, if (player.y == obj.y) -1 else 1, false)
    }

    // Tourist Trap Mine Entrance & Exit
    onObjectClick(2675, 2676, 2690, 2691) { (player, obj) ->
        when (obj.id) {
            2675, 2676 -> player.useStairs(Tile.of(3279, 9427, 0))
            2690, 2691 -> player.useStairs(Tile.of(3301, 3036, 0))
        }
    }

    // Bandit Camp Bartender
    onNpcClick(1921) { (player) -> ShopsHandler.openShop(player, "the_big_heist_lodge") }

    // Ali Snake Charmer
    onNpcClick(1872) { (player, npc) ->
        player.startConversation {
            player(HAPPY_TALKING, "Hello...")
            npc(npc, FRUSTRATED, "What do you want " + player.getPronoun("sir", "ma'am") + "?")
            options {
                op("Would you like some money?") {
                    player(CHEERFUL, "Would you like some money?")
                    npc(npc, FRUSTRATED, "Why else would I sit here with a dangerous snake?")
                    options("Give money?") {
                        if (player.inventory.hasCoins(1)) op("Yes.") {
                            item(995, "You give the charmer 1 coin") { player.inventory.removeCoins(1) }
                            npc(npc, CHEERFUL, "Oh thank you so much! Please come again.")
                            simple("You feel swindled...")
                        }
                        else op("I would give you if I had some...") {
                            player(CHEERFUL, "I would give you money if I had it.")
                            npc(npc, FRUSTRATED, "Leave me alone.")
                        }
                        op("No.")
                    }
                }
                op("Does the snake ever bite?") {
                    player(CHEERFUL, "Does the snake ever bite?")
                    npc(npc, SECRETIVE, "It's trained not to.")
                    player(AMAZED, "That's cheating isn't it?")
                    npc(npc, CALM_TALK, "Please, leave me alone.")
                }
                op("Can I try your flute?") {
                    player(CHEERFUL, "Can I try your flute?")
                    simple("He looks upset...")
                    npc(npc, FRUSTRATED, "Will it get you off my back?")
                    player(SECRETIVE, "Umm, sure.")
                    simple("He pulls out a set of flutes...")
                    npc(npc, FRUSTRATED, "I will give you one, just leave me alone.")
                    item(4605, "He gives you a flute.") { player.inventory.addItem(4605, 1) }
                }
            }
        }
    }

    // Ancient Spellbook Pyramid Rear Entrance
    onObjectClick(6481) { (player) -> player.tele(Tile.of(3233, 9310, 0)) }

    // Pyramid Sarcophagi
    onObjectClick(6516) { (player, obj) ->
        if (obj.tile.isAt(3233, 9309)) player.tele(Tile.of(3233, 2887, 0))
        else player.sendMessage("You search the sarcophagus but find nothing.")
    }

    // Granite Splitting
    onItemClick(6979, 6981, 6983, options = arrayOf("Craft")) { (player, item)->
        if (!player.inventory.containsItem(1755, 1, true)) {
            player.sendMessage("You must have a chisel in order to craft granite.")
            return@onItemClick
        }
        if (player.inventory.freeSlots < 3) {
            player.sendMessage("You do not have enough room in your inventory to split the granite.")
            return@onItemClick
        }
        player.lock(2)
        when (item.id) {
            6983 -> { // 5kg - splits into 2x 2kg and 2x 500g
                player.inventory.deleteItem(6983, 1)
                player.inventory.addItem(6981, 2)
                player.inventory.addItem(6979, 2)
            }
            6981 -> { // 2kg - splits into 4x 500g
                player.inventory.deleteItem(6981, 1)
                player.inventory.addItem(6979, 4)
            }
            6979 -> { // 500g
                player.sendMessage("This block of granite is too small to craft into anything.")
                return@onItemClick
            }
        }
        player.anim(11146)
    }

    // Sandstone Splitting
    onItemClick(6973, 6975, 6977, options = arrayOf("Craft")) { (player, item)->
        if (!player.inventory.containsItem(1755, 1, true)) {
            player.sendMessage("You must have a chisel in order to craft sandstone.")
            return@onItemClick
        }
        if (player.inventory.freeSlots < (if (item.id == 6975) 2 else 1)) {
            player.sendMessage("You do not have enough room in your inventory to split the sandstone.")
            return@onItemClick
        }
        player.lock(2)
        when (item.id) {
            6977 -> { // 10kg - splits into 2x 5kg
                player.inventory.deleteItem(6977, 1)
                player.inventory.addItem(6975, 2)
            }
            6975 -> { // 5kg - splits into 2x 2kg and 1x 1kg
                player.inventory.deleteItem(6975, 1)
                player.inventory.addItem(6973, 2)
                player.inventory.addItem(6971, 1)
            }
            6973 -> { // 2kg - splits into 2x 1kg
                player.inventory.deleteItem(6973, 1)
                player.inventory.addItem(6971, 2)
            }
        }
        player.anim(11146)
    }

}