package com.rs.game.content.world.areas.yanille.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.achievements.AchievementDef
import com.rs.game.content.achievements.AchievementSystemD
import com.rs.game.content.achievements.SetReward
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

const val WATCHTOWER_TELEPORT_LOCATION_KEY = "watchtowerTeleport"
const val DEFAULT_TELEPORT_LOCATION = false // false = Watchtower, true = Yanille

class Aleck(player: Player, npc: NPC) {
    private val hardRequirementMet = SetReward.ARDOUGNE_CLOAK.hasRequirements(player, AchievementDef.Area.ARDOUGNE, AchievementDef.Difficulty.HARD, false)

    init {
        player.startConversation {
            npc(npc, CHEERFUL, "Hello, what are you after?")
            options {
                opExec("Do you have anything for trade?") { ShopsHandler.openShop(player, "alecks_hunter_emporium") }
                opExec("About the Achievement System...") { AchievementSystemD(player, npc.id, SetReward.ARDOUGNE_CLOAK) }
                if (hardRequirementMet)
                    op("I'd like to change my Watchtower Teleport point.") {
                        player(CALM_TALK, "I'd like to change my Watchtower Teleport point.")
                        val currentLocation = getTeleportLocation(player)
                        val nextLocation = !currentLocation
                        options("Toggle Watchtower Teleport to ${if (!nextLocation) "Watchtower" else "Yanille"}?") {
                            opExec("Yes.") {
                                setTeleportLocation(player, nextLocation)
                                val locationMessage = if (!nextLocation) "Watchtower" else "centre of Yanille"
                                player.sendMessage("Watchtower Teleport will now teleport you to the $locationMessage.")
                            }
                            op("No.")
                        }
                    }
                op("Sorry, I was just leaving.")
            }
        }
    }

    private fun getTeleportLocation(player: Player): Boolean {
        return player.getBool(WATCHTOWER_TELEPORT_LOCATION_KEY)
    }

    private fun setTeleportLocation(player: Player, location: Boolean) {
        player.save(WATCHTOWER_TELEPORT_LOCATION_KEY, location)
    }
}

@ServerStartupEvent
fun mapAleckInteractions() {
    onNpcClick(5110, options = arrayOf("Talk-to")) { (player, npc) -> Aleck(player, npc) }
    onNpcClick(5110, options = arrayOf("Trade")) { (player) -> ShopsHandler.openShop(player, "alecks_hunter_emporium") }
}
