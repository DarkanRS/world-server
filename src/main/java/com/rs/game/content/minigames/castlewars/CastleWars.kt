package com.rs.game.content.minigames.castlewars

import com.rs.cache.loaders.ObjectType
import com.rs.game.World
import com.rs.game.content.skills.cooking.Foods.Food
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks

@ServerStartupEvent
fun mapCastleWarsCastle() {

    // scoreboard
    onObjectClick(4484) {
        CastleWars.viewScoreBoard(it.player)
    }

    // join zamorak
    onObjectClick(4388) {
        CastleWars.joinPortal(it.player, CastleWars.ZAMORAK)
    }

    // join guthix
    onObjectClick(4408) {
        CastleWars.joinPortal(it.player, CastleWars.GUTHIX)
    }

    // join saradomin
    onObjectClick(4387) {
        CastleWars.joinPortal(it.player, CastleWars.SARADOMIN)
    }

}

class PlayingGame : Task() {

    var minutesLeft: Int = 0
    var score: MutableList<Int> = mutableListOf()
    var flagStatus: MutableList<Int> = mutableListOf()
    var barricadesCount: MutableList<Int> = mutableListOf()

    val spawnedObjects: MutableList<GameObject> = mutableListOf()
    val barricades: MutableList<CastleWarsBarricadeNpc> = mutableListOf()

    init {
        reset()
    }

    fun reset() {
        minutesLeft = 5 // temp testing else 5
        score = mutableListOf(0, 0)
        flagStatus = mutableListOf(0, 0)
        barricadesCount = mutableListOf(0, 0)
        for (obj in spawnedObjects) {
            World.removeObject(obj)
        }
        spawnedObjects.clear()
        for (npc in barricades)
            npc.finish()
        barricades.clear()
    }

    fun isBarricadeAt(tile: Tile): Boolean {
        val iterator = barricades.iterator()
        while (iterator.hasNext()) {
            val npc = iterator.next()
            if (npc.isDead || npc.hasFinished()) {
                iterator.remove()
                continue
            }
            if (npc.x == tile.x && npc.y == tile.y && npc.plane == tile.plane) {
                return true
            }
        }
        return false
    }

    fun addBarricade(team: Int, player: Player) {
        if (barricadesCount[team] >= 10) {
            player.sendMessage("Each team in the activity can have a maximum of 10 barricades set up.")
            return
        }
        player.inventory.deleteItem(Item(4053, 1))
        barricadesCount[team]++
        barricades.add(CastleWarsBarricadeNpc(team, Tile.of(player.tile)))
    }

    fun removeBarricade(team: Int, npc: CastleWarsBarricadeNpc) {
        barricadesCount[team]--
        barricades.remove(npc)
    }

    fun takeFlag(player: Player, team: Int, flagTeam: Int, obj: GameObject, dropped: Boolean) {
        if ((!dropped && team == flagTeam) || (dropped && flagStatus[flagTeam] != DROPPED))
            return
        if (!dropped && flagStatus[flagTeam] != SAFE)
            return

        if (flagTeam != team && (player.equipment.weaponId != -1 || player.equipment.shieldId != -1)) {
            // TODO no space message
            player.sendMessage("You can't take flag while wearing something in your hands.")
            return
        }
        if (!dropped) {
            val flagStand = GameObject(if (flagTeam == CastleWars.SARADOMIN) 4377 else 4378, obj.type, obj.rotation, obj.x, obj.y, obj.plane)
            spawnedObjects.add(flagStand)
            World.spawnObject(flagStand)
        } else {
            spawnedObjects.remove(obj)
            World.removeObject(obj)
            if (flagTeam == team) {
                makeSafe(flagTeam)
                return
            }
        }
        CastleWars.addHintIcon(flagTeam, player)
        flagStatus[flagTeam] = TAKEN
        CastleWars.setWeapon(player, Item(if (flagTeam == CastleWars.SARADOMIN) 4037 else 4039, 1))
    }

    fun addScore(player: Player, team: Int, flagTeam: Int) {
        CastleWars.setWeapon(player, null)
        score[team]++
        makeSafe(flagTeam)
    }

    private fun makeSafe(flagTeam: Int) {
        var flagStand: GameObject? = null
        for (obj in spawnedObjects) {
            if (obj.id == (if (flagTeam == CastleWars.SARADOMIN) 4377 else 4378)) {
                flagStand = obj
                break
            }
        }
        if (flagStand == null)
            return
        World.removeObject(flagStand)
        flagStatus[flagTeam] = DROPPED
        CastleWars.refreshAllPlayersPlaying()
    }

    fun dropFlag(tile: Tile, flagTeam: Int) {
        CastleWars.removeHintIcon(flagTeam)
        val flagDropped = GameObject(if (flagTeam == CastleWars.SARADOMIN) 4900 else 4901, ObjectType.SCENERY_INTERACT, 0,tile.x, tile.y, tile.plane)
        spawnedObjects.add(flagDropped)
        World.spawnObject(flagDropped)
        flagStatus[flagTeam] = DROPPED
        CastleWars.refreshAllPlayersPlaying()
    }

    fun refresh(player: Player) {
        player.apply {
            vars.setVarBit(143, flagStatus[CastleWars.SARADOMIN])
            vars.setVarBit(145, score[CastleWars.SARADOMIN])
            vars.setVarBit(153, flagStatus[CastleWars.ZAMORAK])
            vars.setVarBit(155, score[CastleWars.ZAMORAK])
        }
    }

    override fun run() {
        minutesLeft--
        if (minutesLeft == 5) {
            CastleWars.endGame(if (score[CastleWars.SARADOMIN] == score[CastleWars.ZAMORAK]) -2 else if (score[CastleWars.SARADOMIN] > score[CastleWars.ZAMORAK]) CastleWars.SARADOMIN else CastleWars.ZAMORAK)
            reset()
        } else if (minutesLeft == 0) {
            minutesLeft = 25
            CastleWars.startGame()
        } else if (minutesLeft > 6) { // adds ppl waiting on lobby
            CastleWars.startGame()
        }
        CastleWars.refreshAllPlayersTime()
    }

    companion object {
        const val SAFE = 0
        const val TAKEN = 1
        const val DROPPED = 2
    }

}

class CastleWars {

    companion object {

        private val waiting: MutableList<MutableList<Player>> = mutableListOf(mutableListOf(), mutableListOf())
        private val playing: MutableList<MutableList<Player>> = mutableListOf(mutableListOf(), mutableListOf())

        private val seasonWins: MutableList<Int> = MutableList(2) { 0 }

        val LOBBY = Tile.of(2442, 3090, 0)
        private val SARA_WAITING = Tile.of(2381, 9489, 0)
        private val ZAMO_WAITING = Tile.of(2421, 9523, 0)
        val SARA_BASE = Tile.of(2426, 3076, 1)
        internal val ZAMO_BASE = Tile.of(2373, 3131, 1)

        const val CW_TICKET = 4067
        const val SARADOMIN = 0
        const val ZAMORAK = 1
        const val GUTHIX = 2

        private var playingGame: PlayingGame? = null

        fun viewScoreBoard(player: Player) {
            player.apply {
                interfaceManager.sendChatBoxInterface(55)
                packets.setIFText(55, 1, "Saradomin: " + seasonWins[SARADOMIN])
                packets.setIFText(55, 2, "Zamorak: " + seasonWins[ZAMORAK])
            }
        }

        fun getPowerfullestTeam(): Int {
            val zamorak = waiting[ZAMORAK].size + playing[ZAMORAK].size
            val saradomin = waiting[SARADOMIN].size + playing[SARADOMIN].size
            return when {
                zamorak > saradomin -> ZAMORAK
                saradomin > zamorak -> SARADOMIN
                else -> GUTHIX
            }
        }

        fun joinPortal(player: Player, team: Int) {
            var team = team // apparently kotlin parameters cant be reassigned...
            if (player.equipment.hatId != -1 || player.equipment.capeId != -1) {
                player.sendMessage("You cannot wear hats, capes, or helms in the arena.")
                return
            }
            for (item in player.inventory.items.array()) {
                if (item == null)
                    continue
                if (Food.forId(item.id) != null) {
                    player.sendMessage("You cannot bring food into the arena.")
                    return
                }
            }
            val powerfullestTeam = getPowerfullestTeam()
            if (team == GUTHIX) {
                team = if (powerfullestTeam == ZAMORAK) SARADOMIN else ZAMORAK
            } else if (team == powerfullestTeam) {
                if (team == ZAMORAK) {
                    player.sendMessage("The Zamorak team is powerful enough already! Guthix demands balance - join the Saradomin team instead!")
                } else {
                    player.sendMessage("The Saradomin team is powerful enough already! Guthix demands balance - join the Zamorak team instead!")
                }
                return
            }
            player.lock(2)
            waiting[team].add(player)
            setCape(player, Item(if (team == ZAMORAK) 4042 else 4041))
            setHood(player, Item(if (team == ZAMORAK) 4515 else 4513))
            player.controllerManager.startController(CastleWarsWaitingController(team))
            player.tele((Tile.of(if (team == ZAMORAK) ZAMO_WAITING else SARA_WAITING, 1)))
            if (playingGame == null && waiting[team].size >= 5) {
                createPlayingGame()
            } else {
                refreshTimeLeft(player)
            }
            // TODO You cannot take non-combat items into the arena
        }

        fun setHood(player: Player, hood: Item?) {
            player.apply {
                equipment.setSlot(Equipment.HEAD, hood)
                equipment.refresh(Equipment.HEAD)
                // TODO: is generating appearance data already handled??
                appearance.generateAppearanceData()
            }
        }

        fun setCape(player: Player, cape: Item?) {
            player.apply {
                equipment.setSlot(Equipment.CAPE, cape)
                equipment.refresh(Equipment.CAPE)
                // TODO: is generating appearance data already handled??
                appearance.generateAppearanceData()
            }
        }

        fun setWeapon(player: Player, weapon: Item?) {
            player.apply {
                equipment.setSlot(Equipment.WEAPON, weapon)
                equipment.refresh(Equipment.WEAPON)
                appearance.generateAppearanceData()
            }
        }

        fun createPlayingGame() {
            playingGame = PlayingGame()
            WorldTasks.scheduleLooping(playingGame, Ticks.fromMinutes(1), Ticks.fromMinutes(1))
            refreshAllPlayersTime()
        }

        fun destroyPlayingGame() {
            playingGame?.stop()
            playingGame = null
            refreshAllPlayersTime()
            leavePlayersSafely()
        }

        fun leavePlayersSafely() {
            leavePlayersSafely(-1)
        }

        fun leavePlayersSafely(winner: Int) {
            for (element in playing) {
                for (player in element) {
                    player.lock(7)
                    player.stopAll()
                }
            }
            // TODO: What is going on here???
            WorldTasks.schedule(6) {
                for (i in 0..playing.size) {
                    for (player in playing[i].toTypedArray()) {
                        forceRemovePlayingPlayer(player)
                        if (winner != -1) {
                            if (winner == -2) {
                                player.sendMessage("You draw.")
                                player.inventory.addItem(CW_TICKET, 1)
                            } else if (winner == i) {
                                player.sendMessage("You won.")
                                player.inventory.addItem(CW_TICKET, 2)
                            } else {
                                player.sendMessage("You lost.")
                            }
                        }
                    }
                }
            }
        }

        fun forceRemoveWaitingPlayer(player: Player) {
            player.controllerManager.forceStop()
        }

        fun removeWaitingPlayer(player: Player, team: Int) {
            waiting[team].remove(player)
            setCape(player, null)
            setHood(player, null)
            player.tele(Tile.of(LOBBY, 2))
            // if no players are left, destroy game
            if (playingGame != null && waiting[team].isEmpty() && playing[team].isEmpty())
                destroyPlayingGame()
        }

        fun refreshTimeLeft(player: Player) {
            player.vars.setVar(380, if (playingGame == null) 0 else playingGame!!.minutesLeft - if (player.controllerManager.getController() is CastleWarsPlayingController) 5 else 0)
        }

        fun startGame() {
            for (i in 0..waiting.size) {
                for (player in waiting[i].toTypedArray()) {
                    joinPlayingGame(player, i)
                }
            }
        }

        fun forceRemovePlayingPlayer(player: Player) {
            player.controllerManager.forceStop()
        }

        fun removePlayingPlayer(player: Player, team: Int) {
            player.apply {
                playing[team].remove(player)
                reset()
                isCanPvp = false
                setCape(player, null)
                setHood(player, null)
                val weaponId = equipment.weaponId
                if (weaponId == 4037 || weaponId == 4039) {
                    setWeapon(player, null)
                    dropFlag(player.lastTile, if (weaponId == 4037) SARADOMIN else ZAMORAK)
                }
                closeInterfaces()
                inventory.deleteItem(4049, Integer.MAX_VALUE) // bandages
                inventory.deleteItem(4053, Integer.MAX_VALUE) // barricades
                hintIconsManager.removeUnsavedHintIcon()
                musicsManager.reset()
                tele(Tile.of(LOBBY, 2))
                // if no players are left, destroy game
                if (playingGame != null && waiting[team].isEmpty() && playing[team].isEmpty())
                    destroyPlayingGame()
            }
        }

        fun joinPlayingGame(player: Player, team: Int) {
            player.apply {
                playingGame?.refresh(player)
                waiting[team].remove(player)
                controllerManager.removeControllerWithoutCheck()
                interfaceManager.removeOverlay()
                playing[team].add(player)
                isCanPvp = true
                controllerManager.startController(CastleWarsPlayingController(team))
                tele(Tile.of(if (team == ZAMORAK) ZAMO_BASE else SARA_BASE, 1))
            }
        }

        fun endGame(winner: Int) {
            if (winner != -2)
                seasonWins[winner]++
            leavePlayersSafely(winner)
        }

        fun refreshAllPlayersTime() {
            for (element in waiting) {
                for (player in element) {
                    refreshTimeLeft(player)
                }
            }
            for (i in 0..playing.size) {
                for (player in playing[i]) {
                    player.musicsManager.playSongAndUnlock(if (i == ZAMORAK) 845 else 314)
                }
            }
        }

        fun refreshAllPlayersPlaying() {
            for (element in playing) {
                for (player in element)
                    playingGame?.refresh(player)
            }
        }

        fun addHintIcon(team: Int, target: Player) {
            for (player in playing[team]) {
                player.hintIconsManager.addHintIcon(target, 0, -1, false)
            }
        }

        fun removeHintIcon(team: Int) {
            for (player in playing[team]) {
                player.hintIconsManager.removeUnsavedHintIcon()
            }
        }

        fun addScore(player: Player, team: Int, flagTeam: Int) {
            if (playingGame == null)
                return
            playingGame!!.addScore(player, team, flagTeam)
        }

        fun takeFlag(player: Player, team: Int, flagTeam: Int, obj: GameObject, dropped: Boolean) {
            if (playingGame == null)
                return
            playingGame!!.takeFlag(player, team, flagTeam, obj, dropped)
        }

        fun dropFlag(tile: Tile, flagTeam: Int) {
            if (playingGame == null)
                return
            playingGame!!.dropFlag(tile, flagTeam)
        }

        fun removeBarricade(team: Int, npc: CastleWarsBarricadeNpc) {
            if (playingGame == null)
                return
            playingGame!!.removeBarricade(team, npc)
        }

        fun addBarricade(team: Int, player: Player) {
            if (playingGame == null)
                return
            playingGame!!.addBarricade(team, player)
        }

        fun isBarricadeAt(tile: Tile): Boolean {
            if (playingGame == null)
                return false
            return playingGame!!.isBarricadeAt(tile)
        }

        fun handleInterfaces(player: Player, interfaceId: Int, componentId: Int) {
            if (interfaceId == 55) {
                if (componentId == 9) {

                    player.closeInterfaces()
                }
            }
        }

        fun getPlaying(): MutableList<MutableList<Player>> {
            return playing
        }

    }

}