package com.rs.game.content.minigames.soulwars

import com.rs.game.model.entity.player.Controller
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick

val LOBBY_OVERLAY = 837
val INGAME_OVERLAY = 836

val GAME_ACTIVE_VARC = 632
val BLUE_TEAM_SIZE_VARC = 633
val RED_TEAM_SIZE_VARC = 634

val LOBBY_MINUTES_PASSED_VARC = 635
val GAME_MINUTES_PASSED_VARC = 636

val BLUE_AVATAR_HEALTH_VARC = 639
val RED_AVATAR_HEALTH_VARC = 640
val BLUE_AVATAR_LEVEL_VARC = 641
val RED_AVATAR_LEVEL_VARC = 642
val BLUE_AVATAR_DEATH_VARC = 643
val RED_AVATAR_DEATH_VARC = 644

val MID_CLAIM_VARC = 645 //0-30  0 being blue owned 30 being red owned
val MID_CLAIM_BAR_COMP = 47
val EAST_CLAIM_VARC = 647 //0-30  0 being blue owned 30 being red owned
val EAST_CLAIM_BAR_COMP = 49
val WEST_CLAIM_VARC = 649 //0-30  0 being blue owned 30 being red owned
val WEST_CLAIM_BAR_COMP = 50

val PLAYER_ACTIVITY_BAR_VAR = 1380 //0-1000

val REFRESH_RED_TEAM_COUNT_SCRIPT = 2075
val REFRESH_BLUE_TEAM_COUNT_SCRIPT = 2076
val REFRESH_TIME = 2077


@ServerStartupEvent
fun mapHandlers() {
    onObjectClick(42219) { e ->
        e.player.useStairs(-1, Tile.of(1886, 3178, 0), 0, 1)
        e.player.controllerManager.startController(SoulWarsLobbyController())
    }

    onObjectClick(42220) { e ->
        e.player.useStairs(-1, Tile.of(3082, 3475, 0), 0, 1)
        e.player.controllerManager.forceStop()
    }

    onNpcClick(8527, 8525, options = arrayOf("Rewards")) { e ->
        e.player.interfaceManager.sendInterface(276)
    }

    onButtonClick(276) { e ->
        when (e.componentId) {
            else -> e.player.sendMessage("soulrewardcomp: ${e.componentId} - ${e.packet}")
        }
    }
}

class SoulWars {

}

class SoulWarsLobbyController : Controller() {
    override fun start() {
        player.interfaceManager.sendOverlay(LOBBY_OVERLAY)
    }

    override fun login(): Boolean {
        player.interfaceManager.sendOverlay(LOBBY_OVERLAY)
        return false
    }

    override fun logout(): Boolean { return false }

    override fun onRemove() {
        player.interfaceManager.removeOverlay()
    }
}

class SoulWarsGameController : Controller() {
    override fun start() {
        player.interfaceManager.sendOverlay(INGAME_OVERLAY)
    }

    override fun login(): Boolean {
        player.interfaceManager.sendOverlay(INGAME_OVERLAY)
        return false
    }

    override fun logout(): Boolean { return false }

    override fun onRemove() {
        player.interfaceManager.removeOverlay()
    }
}