package com.rs.game.content.minigames.soulwars

import com.rs.cache.loaders.ObjectType
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.World
import com.rs.game.content.minigames.MinigameUtil
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.MapUtils
import com.rs.lib.util.MapUtils.Area
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*
import com.rs.utils.Ticks
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSets
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sign

const val LOBBY_OVERLAY = 837
const val INGAME_OVERLAY = 836

const val GAME_ACTIVE_VARC = 632
const val PLAYERS_NEEDED_BLUE_VARC = 633
const val PLAYERS_NEEDED_RED_VARC = 634

const val LOBBY_MINUTES_PASSED_VARC = 635
const val GAME_MINUTES_PASSED_VARC = 636

const val BLUE_TEAM_SIZE_VARC = 637
const val RED_TEAM_SIZE_VARC = 638

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

const val BONES = 14638
const val SOUL_FRAGMENT = 14646
const val RED_CAPE = 14641
const val BLUE_CAPE = 14642

const val PLAYER_MINIMUM = 4
const val TICKS_BETWEEN_GAMES = 300

val RED_AVATAR_DANGER_CHUNKS = setOf(504215, 502167, 502166, 504214, 504213, 502165)
val BLUE_AVATAR_DANGER_CHUNKS = setOf(463248, 461200, 461201, 463249, 463250, 461202)

val BLUE_EXIT_AREA = Area(MapUtils.Structure.TILE, 1885, 3166, 3, 7)
val RED_EXIT_AREA = Area(MapUtils.Structure.TILE, 1893, 3166, 3, 7)

val BLUE_RESPAWN_AREA = Area(MapUtils.Structure.TILE, 1817, 3222, 5, 6)
val RED_RESPAWN_AREA = Area(MapUtils.Structure.TILE, 1952, 3236, 5, 6)
val EAST_RESPAWN_AREA = Area(MapUtils.Structure.TILE, 1932, 3244, 2, 1)
val WEST_RESPAWN_AREA = Area(MapUtils.Structure.TILE, 1841, 3218, 2, 1)

val MID_CAP_ZONE = Area(MapUtils.Structure.TILE, 1879, 3224, 15, 15)
val MID_CAP_CHUNKS = intArrayOf(481684, 479636, 479635, 481683, 483731, 483732)
val EAST_GRAVEYARD_CAP_ZONE = Area(MapUtils.Structure.TILE, 1928, 3240, 10, 10)
val EAST_CAP_CHUNKS = intArrayOf(493973, 493974, 496022, 496021)
val WEST_GRAVEYARD_CAP_ZONE = Area(MapUtils.Structure.TILE, 1837, 3213, 10, 10)
val WEST_CAP_CHUNKS = intArrayOf(471442, 469394, 469393, 471441)

val LOBBY_PLAYERS: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
val INGAME_PLAYERS: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
var ACTIVE_GAME: SoulWars? = null
var LOBBY_TICKS = 0

@ServerStartupEvent
fun mapSoulwars() {
    onObjectClick(42219) { (player) ->
        player.useStairs(-1, Tile.of(1886, 3178, 0), 0, 1)
    }

    onObjectClick(42220) { (player) ->
        player.useStairs(-1, Tile.of(3082, 3475, 0), 0, 1)
    }

    onObjectClick(42029) { (player) ->
        when {
            player.x < 1880 -> {
                player.passThrough(Tile.of(1880, 3162, 0))
                player.controllerManager.forceStop()
            }

            player.equipment.get(Equipment.CAPE) != null || player.inventory.items.array().any { it?.definitions?.equipSlot == Equipment.CAPE } ->
                player.sendMessage("You cannot enter the lobby with a cape.")

            else -> {
                player.passThrough(Tile.of(1879, 3162, 0))
                player.controllerManager.startController(SoulWarsLobbyController())
            }
        }
    }

    onObjectClick(42031) { (player) ->
        player.nextTile = Tile.of(1875, 3162, 0)
        player.controllerManager.startController(SoulWarsLobbyController())
    }

    onItemClick(BONES, options = arrayOf("Bury")) { (player, item) ->
        val ctrl = player.controllerManager.getController(SoulWarsGameController::class.java)
        if (ctrl != null) {
            if (ctrl.game?.bury(player, ctrl.redTeam) == true) {
                ctrl.activity += 100
                player.inventory.deleteItem(item)
                player.anim(827)
            }
        }
    }

    onItemEquip(RED_CAPE, BLUE_CAPE) { e ->
        e.apply {
            if (dequip()) {
                if (!player.controllerManager.isIn(SoulWarsGameController::class.java)) {
                    player.equipment.setNoPluginTrigger(Equipment.CAPE, null)
                    player.equipment.refresh(Equipment.CAPE)
                    player.appearance.generateAppearanceData()
                }
                cancel()
            }
        }
    }

    onChunkEnter { (entity, chunkId) ->
        (entity as? Player)?.let { player ->
            fun setBarsVisibility(mid: Boolean, east: Boolean, west: Boolean) {
                player.packets.apply {
                    setIFHidden(INGAME_OVERLAY, MID_CLAIM_BAR_COMP, !mid)
                    setIFHidden(INGAME_OVERLAY, EAST_CLAIM_BAR_COMP, !east)
                    setIFHidden(INGAME_OVERLAY, WEST_CLAIM_BAR_COMP, !west)
                }
            }

            when {
                MID_CAP_CHUNKS.contains(chunkId) -> setBarsVisibility(mid = true, east = false, west = false)
                EAST_CAP_CHUNKS.contains(chunkId) -> setBarsVisibility(mid = false, east = true, west = false)
                WEST_CAP_CHUNKS.contains(chunkId) -> setBarsVisibility(mid = false, east = false, west = true)
            }
        }
    }

    WorldTasks.schedule(1, 0) {
        processSoulwars()
    }
}

fun processSoulwars() {
    ACTIVE_GAME?.tick()
    LOBBY_TICKS++
    if (LOBBY_TICKS % TICKS_BETWEEN_GAMES == 0) {
        attemptStartGame()
        LOBBY_TICKS = 0
    }
    if (LOBBY_TICKS % 5 == 0) {
        for (player in LOBBY_PLAYERS)
            updateLobbyVars(player)
        for (player in INGAME_PLAYERS)
            updateIngameVars(player)
    }
}

fun updateLobbyVars(player: Player) {
    player.packets.apply {
        sendVarc(GAME_ACTIVE_VARC, if (ACTIVE_GAME == null) 0 else 1)

        sendVarc(PLAYERS_NEEDED_BLUE_VARC, ACTIVE_GAME?.redTeam?.size ?: (LOBBY_PLAYERS.count() + (10 - PLAYER_MINIMUM)))
        sendVarc(PLAYERS_NEEDED_RED_VARC, ACTIVE_GAME?.redTeam?.size ?: (LOBBY_PLAYERS.count() + (10 - PLAYER_MINIMUM)))

        sendVarc(BLUE_TEAM_SIZE_VARC, ACTIVE_GAME?.blueTeam?.size ?: (LOBBY_PLAYERS.count() + (10 - PLAYER_MINIMUM)))
        sendVarc(RED_TEAM_SIZE_VARC, ACTIVE_GAME?.redTeam?.size ?: (LOBBY_PLAYERS.count() + (10 - PLAYER_MINIMUM)))

        sendVarc(LOBBY_MINUTES_PASSED_VARC, LOBBY_TICKS / 100)
        sendVarc(GAME_MINUTES_PASSED_VARC, ACTIVE_GAME?.ticks?.div(100) ?: 0)
    }
}

fun updateIngameVars(player: Player) {
    updateLobbyVars(player)
    player.packets.apply {
        sendVarc(RED_AVATAR_HEALTH_VARC, ACTIVE_GAME?.redAvatar?.getHealthVarc() ?: 0)
        sendVarc(RED_AVATAR_LEVEL_VARC, ACTIVE_GAME?.redAvatar?.level ?: 0)
        sendVarc(RED_AVATAR_DEATH_VARC, ACTIVE_GAME?.redDeaths ?: 0)

        sendVarc(BLUE_AVATAR_HEALTH_VARC, ACTIVE_GAME?.blueAvatar?.getHealthVarc() ?: 0)
        sendVarc(BLUE_AVATAR_LEVEL_VARC, ACTIVE_GAME?.blueAvatar?.level ?: 0)
        sendVarc(BLUE_AVATAR_DEATH_VARC, ACTIVE_GAME?.blueDeaths ?: 0)

        sendVarc(MID_CLAIM_VARC, ACTIVE_GAME?.midCapVal ?: 15)
        sendVarc(EAST_CLAIM_VARC, ACTIVE_GAME?.eastCapVal ?: 15)
        sendVarc(WEST_CLAIM_VARC, ACTIVE_GAME?.westCapVal ?: 15)
    }
}

fun attemptStartGame() {
    if (ACTIVE_GAME != null)
        return
    if (LOBBY_PLAYERS.count() >= PLAYER_MINIMUM)
        ACTIVE_GAME = SoulWars()
}

class SoulAvatar(val redTeam: Boolean, val game: SoulWars) : NPC(if (redTeam) 8596 else 8597, if (redTeam) Tile.of(1965, 3249, 0) else Tile.of(1805, 3208, 0)) {
    var level = 100

    init {
        capDamage = 700
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        level = 100
        if (redTeam)
            game.redDeaths++
        else
            game.blueDeaths++
    }

    override fun handlePreHit(hit: Hit?) {
        super.handlePreHit(hit)
        val player = hit?.source as? Player ?: return
        if (player.skills.getLevelForXp(Skills.SLAYER) < level)
            hit.damage = 0
    }

    override fun canBeAttackedBy(player: Player): Boolean {
        return if (redTeam)
            game.blueTeam.contains(player)
        else
            game.redTeam.contains(player)
    }

    override fun getPossibleTargets(): MutableList<Entity> {
        return if (redTeam)
            game.blueTeam.filter { RED_AVATAR_DANGER_CHUNKS.contains(it.chunkId) }.toMutableList()
        else
            game.redTeam.filter { BLUE_AVATAR_DANGER_CHUNKS.contains(it.chunkId) }.toMutableList()
    }

    fun getHealthVarc(): Int {
        return takeUnless { it.isDead || it.hasFinished() }
            ?.hitpoints
            ?.let { ceil(it.toDouble() * 100.0 / 15000.0).toInt() }
            ?: 0
    }
}

class SoulWars {
    val redTeam: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
    val blueTeam: ObjectSet<Player> = ObjectSets.synchronize(ObjectOpenHashSet())
    var redAvatar = SoulAvatar(true, this)
    var blueAvatar = SoulAvatar(false, this)
    var redDeaths = 0
    var blueDeaths = 0
    var midCapVal = 15
    var eastCapVal = 15
    var westCapVal = 15
    var ticks = 0

    init {
        val sortedPlayers = LOBBY_PLAYERS.toList().sortedBy { it.skills.combatLevel }
        var redTotalCombatLevel = 0
        var blueTotalCombatLevel = 0

        for (player in sortedPlayers) {
            if (redTotalCombatLevel <= blueTotalCombatLevel) {
                player.controllerManager.startController(SoulWarsGameController(true, this))
                redTeam.add(player)
                redTotalCombatLevel += player.skills.combatLevel
            } else {
                player.controllerManager.startController(SoulWarsGameController(false, this))
                blueTeam.add(player)
                blueTotalCombatLevel += player.skills.combatLevel
            }
            LOBBY_PLAYERS.remove(player)
        }
    }

    private fun getObelisk(): GameObject {
        return World.getObjectWithType(Tile.of(1886, 3231, 0), ObjectType.SCENERY_INTERACT)
    }

    private fun getEastBarrier(): GameObject {
        return World.getObjectWithType(Tile.of(1933, 3243, 0), ObjectType.WALL_STRAIGHT)
    }

    private fun getWestBarrier(): GameObject {
        return World.getObjectWithType(Tile.of(1842, 3220, 0), ObjectType.WALL_STRAIGHT)
    }

    fun closestRespawnPoint(redTeam: Boolean, tile: Tile): Tile {
        val possibleTiles = ArrayList<Tile>()
        possibleTiles.add(if (redTeam) RED_RESPAWN_AREA.randomTile else BLUE_RESPAWN_AREA.randomTile)
        if (if (redTeam) eastCapVal >= 25 else eastCapVal <= 5)
            possibleTiles.add(EAST_RESPAWN_AREA.randomTile)
        if (if (redTeam) westCapVal >= 25 else westCapVal <= 5)
            possibleTiles.add(WEST_RESPAWN_AREA.randomTile)
        return possibleTiles.minByOrNull { Utils.getDistance(it, tile) }!!
    }

    fun bury(player: Player, redTeam: Boolean): Boolean {
        val avatar = if (redTeam) redAvatar else blueAvatar
        if (avatar.isDead || avatar.hasFinished())
            return false
        if (avatar.level >= 100) {
            player.sendMessage("Your avatar is already a high enough level.")
            return false
        }
        avatar.level++
        return true
    }

    fun tick() {
        if (++ticks >= Ticks.fromMinutes(20)) return endGame()
        if (ticks % 5 == 0) {
            listOf(EAST_CAP_CHUNKS to EAST_GRAVEYARD_CAP_ZONE, MID_CAP_CHUNKS to MID_CAP_ZONE, WEST_CAP_CHUNKS to WEST_GRAVEYARD_CAP_ZONE).forEach { (chunks, zone) ->
                val side = World.getPlayersInChunks(*chunks).count { it.isCanPvp && zone.within(it.tile) && redTeam.contains(it) } - World.getPlayersInChunks(*chunks).count { it.isCanPvp && zone.within(it.tile) && blueTeam.contains(it) }
                when (zone) {
                    EAST_GRAVEYARD_CAP_ZONE -> eastCapVal = Utils.clampI(eastCapVal + side.sign, 0, 30)
                    MID_CAP_ZONE -> midCapVal = Utils.clampI(midCapVal + side.sign, 0, 30)
                    WEST_GRAVEYARD_CAP_ZONE -> westCapVal = Utils.clampI(westCapVal + side.sign, 0, 30)
                }
            }
            updateObjectTeam(getObelisk(), 42012, 42011, 42010, midCapVal)?.let { color ->
                INGAME_PLAYERS.forEach { it.sendMessage("The $color team has taken control of the soul obelisk.") }
            }
            updateObjectTeam(getEastBarrier(), 42016, 42013, 42019, eastCapVal)?.let { color ->
                INGAME_PLAYERS.forEach { it.sendMessage("The $color team has taken control of the eastern graveyard.") }
            }
            updateObjectTeam(getWestBarrier(), 42017, 42014, 42020, westCapVal)?.let { color ->
                INGAME_PLAYERS.forEach { it.sendMessage("The $color team has taken control of the western graveyard.") }
            }
        }
    }

    private fun updateObjectTeam(obj: GameObject, redId: Int, blueId: Int, neutralId: Int, capVal: Int): String? {
        val newId = when {
            capVal >= 25 -> redId
            capVal <= 5 -> blueId
            else -> neutralId
        }
        if (obj.id != newId) {
            obj.setId(newId)
            return if (newId == redId) "<col=A31818>red</col>" else "<col=3232D1>blue</col>"
        }
        return null
    }

    private fun endGame() {
        val winningTeam = if (redDeaths == blueDeaths) null else if (redDeaths > blueDeaths) blueTeam else redTeam
        arrayOf(blueAvatar, redAvatar).forEach { avatar ->
            avatar.finish()
            avatar.cancelRespawnTask()
        }
        arrayOf(redTeam to RED_EXIT_AREA, blueTeam to BLUE_EXIT_AREA).forEach { (team, exitArea) ->
            team.forEach { player ->
                player.nextTile = exitArea.randomTile
                player.controllerManager.forceStop()
                val zeal = if (winningTeam == null) 2 else if (winningTeam == team) 3 else 1
                player.soulWarsZeal += zeal
                player.incrementCount("Soul Wars Zeal earned", zeal)
                when (zeal) {
                    1 -> player.sendMessage("Your team lost. You are awarded 1 Zeal. You now have ${Utils.formatNumber(player.soulWarsZeal)} Zeal.")
                    2 -> player.sendMessage("The game was a draw. You are awarded 2 Zeal. You now have ${Utils.formatNumber(player.soulWarsZeal)} Zeal.")
                    3 -> player.sendMessage("Your team won. You are awarded 3 Zeal. You now have ${Utils.formatNumber(player.soulWarsZeal)} Zeal.")
                }
            }
        }
        ACTIVE_GAME = null
    }
}

class SoulWarsLobbyController : Controller() {
    override fun start() {
        player.interfaceManager.sendOverlay(LOBBY_OVERLAY)
        LOBBY_PLAYERS.add(player)
        updateLobbyVars(player)
    }

    override fun login(): Boolean {
        player.nextTile = Tile.of(1880, 3162, 0)
        player.controllerManager.forceStop()
        return true
    }

    override fun processItemTeleport(toTile: Tile?): Boolean { return false }
    override fun processMagicTeleport(toTile: Tile?): Boolean { return false }
    override fun processObjectTeleport(toTile: Tile?): Boolean { return false }

    override fun logout(): Boolean {
        LOBBY_PLAYERS.remove(player)
        player.nextTile = Tile.of(1880, 3162, 0)
        player.tile = Tile.of(1880, 3162, 0)
        return false
    }

    override fun onRemove() {
        LOBBY_PLAYERS.remove(player)
        player.interfaceManager.removeOverlay()
    }
}

class SoulWarsGameController(val redTeam: Boolean, @Transient val game: SoulWars?) : Controller() {
    @Transient
    var activity: Int = 1000

    override fun processObjectClick1(obj: GameObject): Boolean {
        fun checkPositionAndPassThrough(check: Boolean, deltaX: Int, deltaY: Int) {
            if (check) {
                player.sendMessage("Onward! You have no reason to go back inside.")
                return
            }
            player.passThrough(obj.tile.transform(deltaX, deltaY, 0))
            player.isCanPvp = true
        }

        when (obj.id) {
            42021, 42022 -> player.sendOptionDialogue {
                it.add("Yes, leave. (You won't receive any rewards for doing so)") { player.controllerManager.forceStop() }
                it.add("Nevermind.")
            }
            42023, 42024 -> MinigameUtil.giveFoodAndPotions(player)
            42015 -> checkPositionAndPassThrough(player.x < obj.x, -1, 0)
            42018 -> checkPositionAndPassThrough(player.x >= obj.x, 0, 0)
            in arrayOf(42016, 42013, 42019) -> checkPositionAndPassThrough(player.y > obj.y, 0, -1)
            in arrayOf(42017, 42014, 42020) -> checkPositionAndPassThrough(player.y >= obj.y, 0, 0)
        }
        return false
    }

    override fun processItemOnObject(obj: GameObject, item: Item): Boolean {
        if (item.id == SOUL_FRAGMENT && (obj.id == 42010 || obj.id == 42011 || obj.id == 42012)) {
            val isCorrectTeamControl = (redTeam && obj.id == 42012) || (!redTeam && obj.id == 42011)
            if (!isCorrectTeamControl) {
                player.sendMessage("The obelisk is unresponsive as your team is not in control of it.")
                return false
            }

            val avatarLevel = (if (redTeam) game?.blueAvatar else game?.redAvatar)?.level ?: -1
            val numToUse = min(item.amount, avatarLevel)

            if (numToUse <= 0) {
                player.sendMessage("Your opponent's avatar cannot be weakened any further.")
                return false
            }

            player.inventory.deleteItem(SOUL_FRAGMENT, numToUse)
            if (redTeam)
                game?.blueAvatar?.let { avatar -> avatar.level -= numToUse }
            else
                game?.redAvatar?.let { avatar -> avatar.level -= numToUse }
        }
        return false
    }

    override fun sendDeath(): Boolean {
        player.lock(8)
        player.stopAll()
        WorldTasks.scheduleTimer { loop: Int ->
            when (loop) {
                0 -> player.anim(836)
                1 -> player.sendMessage("Oh dear, you have died.")
                4 -> {
                    val killer = player.mostDamageReceivedSourcePlayer
                    if (killer != null) {
                        killer.removeDamage(player)
                        killer.increaseKillCount(player)
                    }
                    val soulFrags = player.inventory.getItemById(SOUL_FRAGMENT);
                    if (soulFrags != null) {
                        World.addGroundItem(soulFrags, Tile.of(player.tile))
                        player.inventory.deleteItem(soulFrags)
                    }
                    World.addGroundItem(Item(BONES, 1), Tile.of(player.tile))
                    player.reset()
                    player.nextTile = game?.closestRespawnPoint(redTeam, player.tile) ?: player.tile
                    player.isCanPvp = false
                    player.anim(-1)
                    activity = 1000
                }
                5 -> {
                    player.jingle(90)
                    return@scheduleTimer false
                }
            }
            true
        }
        return false
    }

    override fun process() {
        if (player.inCombat())
            activity = Utils.clampI(activity + 10, 0, 1000);
        else
            activity -= 2
        player.vars.setVar(PLAYER_ACTIVITY_BAR_VAR, activity)
        if (activity <= 0) {
            player.nextTile = RED_EXIT_AREA.randomTile
            player.controllerManager.forceStop()
        }
    }

    override fun start() {
        player.interfaceManager.sendOverlay(INGAME_OVERLAY)
        INGAME_PLAYERS.add(player)
        updateIngameVars(player)
        player.nextTile = if (redTeam) RED_RESPAWN_AREA.randomTile else BLUE_RESPAWN_AREA.randomTile
        player.equipment.setNoPluginTrigger(Equipment.CAPE, Item(if (redTeam) RED_CAPE else BLUE_CAPE, 1))
        player.equipment.refresh(Equipment.CAPE)
        player.appearance.generateAppearanceData()
        player.isCanPvp = false
        player.startConversation {
            npc(8528, HeadE.EVIL_LAUGH, "The time is now! Crush their souls!")
        }
    }

    override fun login(): Boolean {
        player.isCanPvp = false
        MinigameUtil.checkAndDeleteFoodAndPotions(player)
        player.nextTile = Tile.of(1886, 3172, 0)
        player.equipment.setNoPluginTrigger(Equipment.CAPE, null)
        player.equipment.refresh(Equipment.CAPE)
        player.appearance.generateAppearanceData()
        player.controllerManager.forceStop()
        return false
    }

    override fun processItemTeleport(toTile: Tile?): Boolean { return false }
    override fun processMagicTeleport(toTile: Tile?): Boolean { return false }
    override fun processObjectTeleport(toTile: Tile?): Boolean { return false }

    override fun logout(): Boolean {
        player.isCanPvp = false
        MinigameUtil.checkAndDeleteFoodAndPotions(player)
        INGAME_PLAYERS.remove(player)
        if (redTeam)
            game?.redTeam?.remove(player)
        else
            game?.blueTeam?.remove(player)
        player.nextTile = Tile.of(1886, 3172, 0)
        player.equipment.setNoPluginTrigger(Equipment.CAPE, null)
        player.equipment.refresh(Equipment.CAPE)
        player.appearance.generateAppearanceData()
        player.tile = Tile.of(1886, 3172, 0)
        return false
    }

    override fun onRemove() {
        player.isCanPvp = false
        MinigameUtil.checkAndDeleteFoodAndPotions(player)
        player.equipment.setNoPluginTrigger(Equipment.CAPE, null)
        player.equipment.refresh(Equipment.CAPE)
        player.appearance.generateAppearanceData()
        player.inventory.items.array().map {
            if (it != null && (it.id == BONES || it.id == SOUL_FRAGMENT))
                player.inventory.deleteItem(it)
        }
        INGAME_PLAYERS.remove(player)
        player.interfaceManager.removeOverlay()
        if (redTeam)
            game?.redTeam?.remove(player)
        else
            game?.blueTeam?.remove(player)
    }
}

fun getQuickchatVar(varId: Int): Int {
    return when(varId) {
        850 -> ACTIVE_GAME?.blueAvatar?.level ?: 0
        851 -> ACTIVE_GAME?.redAvatar?.level ?: 0
        866 -> ACTIVE_GAME?.blueAvatar?.getHealthVarc() ?: 0
        867 -> ACTIVE_GAME?.redAvatar?.getHealthVarc() ?: 0
        else -> 0
    }
}