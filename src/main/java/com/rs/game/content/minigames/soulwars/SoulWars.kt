package com.rs.game.content.minigames.soulwars

import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSets

const val LOBBY_OVERLAY = 837
const val INGAME_OVERLAY = 836

const val GAME_ACTIVE_VARC = 632
const val BLUE_TEAM_SIZE_VARC = 633
const val RED_TEAM_SIZE_VARC = 634

const val LOBBY_MINUTES_PASSED_VARC = 635
const val GAME_MINUTES_PASSED_VARC = 636

const val BLUE_AVATAR_HEALTH_VARC = 639
const val RED_AVATAR_HEALTH_VARC = 640
const val BLUE_AVATAR_LEVEL_VARC = 641
const val RED_AVATAR_LEVEL_VARC = 642
const val BLUE_AVATAR_DEATH_VARC = 643
const val RED_AVATAR_DEATH_VARC = 644

const val MID_CLAIM_VARC = 645 //0-30  0 being blue owned 30 being red owned
const val MID_CLAIM_BAR_COMP = 47
const val EAST_CLAIM_VARC = 647 //0-30  0 being blue owned 30 being red owned
const val EAST_CLAIM_BAR_COMP = 49
const val WEST_CLAIM_VARC = 649 //0-30  0 being blue owned 30 being red owned
const val WEST_CLAIM_BAR_COMP = 50

const val PLAYER_ACTIVITY_BAR_VAR = 1380 //0-1000

const val REFRESH_RED_TEAM_COUNT_SCRIPT = 2075
const val REFRESH_BLUE_TEAM_COUNT_SCRIPT = 2076
const val REFRESH_TIME = 2077

const val SPIRIT_SHARD = 14646

val LOBBY_PLAYERS: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
val INGAME_PLAYERS: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
var ACTIVE_GAME: SoulWars? = null

@ServerStartupEvent
fun mapHandlers() {
    onObjectClick(42219) { (player) ->
        player.useStairs(-1, Tile.of(1886, 3178, 0), 0, 1)
    }

    onObjectClick(42220) { (player) ->
        player.useStairs(-1, Tile.of(3082, 3475, 0), 0, 1)
    }

    onObjectClick(42029) { (player) ->
        if (player.x < 1880) {
            player.passThrough(Tile.of(1880, 3162, 0))
            player.controllerManager.forceStop()
        } else {
            player.passThrough(Tile.of(1879, 3162, 0))
            player.controllerManager.startController(SoulWarsLobbyController())
        }
    }

    onObjectClick(42031) { (player) ->
        player.nextTile = Tile.of(1875, 3162, 0)
        player.controllerManager.startController(SoulWarsLobbyController())
    }

    onNpcClick(8527, 8525, options = arrayOf("Rewards")) { (player) ->
        player.interfaceManager.sendInterface(276)
    }

    onButtonClick(276) { (player, _, componentId, _, _, packet) ->
        when (componentId) {
            else -> player.sendMessage("soulrewardcomp: ${componentId} - ${packet}")
        }
    }

    WorldTasks.schedule(Ticks.fromMinutes(1), Ticks.fromMinutes(1)) {
        processLobbyMinute()
    }
}

fun processLobbyMinute() {

}

class SoulAvatar(redTeam: Boolean) : NPC(if (redTeam) 8596 else 8597, if (redTeam) Tile.of(1965, 3249, 0) else Tile.of(1805, 3208, 0)) {
    var level = 99

    override fun handlePreHit(hit: Hit?) {
        super.handlePreHit(hit)
        val player = hit?.source as? Player ?: return
        if (player.skills.getLevel(Skills.SLAYER) < level)
            hit.damage = 0
    }
}

class SoulWars {
    val redTeam: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
    val blueTeam: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
    var redAvatar = SoulAvatar(true)
    var blueAvatar = SoulAvatar(false)
    var redKills = 0
    var blueKills = 0
    var ticks = 0

    fun tick() {
        if (++ticks >= Ticks.fromMinutes(20)) {
            endGame()
            return
        }


    }

    fun endGame() {

    }
}

class SoulWarsLobbyController : Controller() {
    override fun start() {
        player.interfaceManager.sendOverlay(LOBBY_OVERLAY)
        LOBBY_PLAYERS.add(player)
    }

    override fun login(): Boolean {
        player.interfaceManager.sendOverlay(LOBBY_OVERLAY)
        LOBBY_PLAYERS.add(player)
        return false
    }

    override fun processItemTeleport(toTile: Tile?): Boolean { return false }
    override fun processMagicTeleport(toTile: Tile?): Boolean { return false }
    override fun processObjectTeleport(toTile: Tile?): Boolean { return false }

    override fun logout(): Boolean {
        LOBBY_PLAYERS.remove(player)
        return false
    }

    override fun onRemove() {
        LOBBY_PLAYERS.remove(player)
        player.interfaceManager.removeOverlay()
    }
}

class SoulWarsGameController : Controller() {
    override fun start() {
        player.interfaceManager.sendOverlay(INGAME_OVERLAY)
        INGAME_PLAYERS.add(player)
    }

    override fun login(): Boolean {
        player.interfaceManager.sendOverlay(INGAME_OVERLAY)
        INGAME_PLAYERS.add(player)
        return false
    }

    override fun processItemTeleport(toTile: Tile?): Boolean { return false }
    override fun processMagicTeleport(toTile: Tile?): Boolean { return false }
    override fun processObjectTeleport(toTile: Tile?): Boolean { return false }

    override fun logout(): Boolean {
        INGAME_PLAYERS.remove(player)
        return false
    }

    override fun onRemove() {
        INGAME_PLAYERS.remove(player)
        player.interfaceManager.removeOverlay()
    }
}