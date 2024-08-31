// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game

import com.rs.Launcher
import com.rs.Settings
import com.rs.cache.loaders.ObjectType
import com.rs.cache.loaders.map.ClipFlag
import com.rs.db.WorldDB
import com.rs.engine.pathfinder.*
import com.rs.engine.pathfinder.Direction.Companion.forDelta
import com.rs.engine.pathfinder.WorldCollision.allFlags
import com.rs.engine.pathfinder.WorldCollision.getFlags
import com.rs.engine.pathfinder.collision.CollisionStrategyType
import com.rs.engine.pathfinder.reach.DefaultReachStrategy.reached
import com.rs.engine.thread.AsyncTaskExecutor
import com.rs.engine.thread.WorldThread
import com.rs.game.content.ItemConstants
import com.rs.game.content.world.areas.wilderness.WildernessController
import com.rs.game.map.Chunk
import com.rs.game.map.ChunkManager
import com.rs.game.model.WorldProjectile
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.EntityList
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.*
import com.rs.lib.net.packets.encoders.Sound
import com.rs.lib.net.packets.encoders.updatezone.AddObject
import com.rs.lib.net.packets.encoders.updatezone.CustomizeObject
import com.rs.lib.util.Logger
import com.rs.lib.util.MapUtils
import com.rs.lib.util.Utils
import com.rs.plugin.PluginManager
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.NPCInstanceEvent
import com.rs.utils.*
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

@ServerStartupEvent
fun addSaveFilesTask() {
    AsyncTaskExecutor.schedule({ Launcher.saveFilesAsync() }, 0, Ticks.fromSeconds(30))
}

@PluginEventHandler
object World {
    @JvmField
	var SYSTEM_UPDATE_DELAY: Int = -1
    private var SYSTEM_UPDATE_START: Long = 0

    @JvmStatic
    val players: EntityList<Player> = EntityList(Settings.PLAYERS_LIMIT)
    private val PLAYER_MAP_USERNAME: MutableMap<String, Player> = ConcurrentHashMap()
    private val PLAYER_MAP_DISPLAYNAME: MutableMap<String, Player> = ConcurrentHashMap()

    @JvmStatic
    val NPCs: EntityList<NPC> = EntityList(Settings.NPCS_LIMIT)

    private val GAMEOBJECT_ROUTE_TYPE_MAPPINGS = Int2ObjectOpenHashMap<GameObject.RouteType>()

    @JvmStatic
	fun addPlayer(player: Player) {
        players.add(player)
        PLAYER_MAP_USERNAME[player.username] = player
        PLAYER_MAP_DISPLAYNAME[player.displayName] = player
        if (player.session != null && !player.username.contains("cli_bot")) AccountLimiter.add(player.session.ip)
    }

    @JvmStatic
	fun removePlayer(player: Player) {
        players.remove(player)
        PLAYER_MAP_USERNAME.remove(player.username, player)
        PLAYER_MAP_DISPLAYNAME.remove(player.displayName, player)
    }

    @JvmStatic
	fun addNPC(npc: NPC) {
        if (!NPCs.contains(npc)) NPCs.add(npc)
    }

    @JvmStatic
	fun removeNPC(npc: NPC?) {
        NPCs.remove(npc)
    }

    @JvmStatic
    fun spawnNPC(id: Int, tile: Tile?, direction: Direction?, permaDeath: Boolean, withFunction: Boolean, customName: String?): NPC {
        val n: NPC
        if (withFunction) {
            val fObj = PluginManager.getObj(NPCInstanceEvent(id, tile, permaDeath))
            n = if (fObj != null) fObj as NPC
            else NPC(id, tile, direction, permaDeath)
        } else n = NPC(id, tile, direction, permaDeath)
        if (customName != null)
            n.setPermName(customName)
        return n
    }

    @JvmStatic
    fun spawnNPC(id: Int, tile: Tile?, permaDeath: Boolean, withFunction: Boolean, customName: String?): NPC {
        return spawnNPC(id, tile, Direction.SOUTH, permaDeath, withFunction, customName)
    }


    @JvmOverloads
    @JvmStatic
    fun spawnNPC(id: Int, tile: Tile?, permaDeath: Boolean = false, withFunction: Boolean = true): NPC {
        return spawnNPC(id, tile, permaDeath, withFunction, null)
    }

    @JvmStatic
	fun canLightFire(plane: Int, x: Int, y: Int): Boolean {
        return !ClipFlag.flagged(getClipFlags(plane, x, y), ClipFlag.UNDER_ROOF) && getClipFlags(plane, x, y) and 2097152 == 0 && getObjectWithSlot(Tile.of(x, y, plane), 2) == null
    }

    @JvmStatic
	fun floorAndWallsFree(plane: Int, x: Int, y: Int, size: Int): Boolean {
        return floorAndWallsFree(Tile.of(x, y, plane), size)
    }

    @JvmStatic
    fun floorAndWallsFree(tile: Tile, size: Int): Boolean {
        for (x in 0 until size) for (y in 0 until size) if (!floorFree(tile.transform(x, y)) || !wallsFree(tile.transform(x, y))) return false
        return true
    }

    @JvmStatic
    fun floorFree(tile: Tile, size: Int): Boolean {
        for (x in 0 until size) for (y in 0 until size) if (!floorFree(tile.transform(x, y))) return false
        return true
    }

    @JvmStatic
	fun floorFree(plane: Int, x: Int, y: Int, size: Int): Boolean {
        return floorFree(Tile.of(x, y, plane), size)
    }

    @JvmStatic
    private fun floorFree(tile: Tile): Boolean {
        return !ClipFlag.flagged(getClipFlags(tile), ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.BW_FULL)
    }

    @JvmStatic
	fun floorFree(plane: Int, x: Int, y: Int): Boolean {
        return floorFree(Tile.of(x, y, plane))
    }

    @JvmStatic
    private fun wallsFree(tile: Tile): Boolean {
        return !ClipFlag.flagged(getClipFlags(tile), ClipFlag.BW_NE, ClipFlag.BW_NW, ClipFlag.BW_SE, ClipFlag.BW_SW, ClipFlag.BW_E, ClipFlag.BW_N, ClipFlag.BW_S, ClipFlag.BW_W)
    }

    @JvmStatic
	fun wallsFree(plane: Int, x: Int, y: Int): Boolean {
        return wallsFree(Tile.of(x, y, plane))
    }

    @JvmStatic
	fun getClipFlags(plane: Int, x: Int, y: Int): Int {
        return getFlags(Tile.of(x, y, plane))
    }

    @JvmStatic
    private fun getClipFlags(tile: Tile): Int {
        return getFlags(tile)
    }

    @JvmStatic
    fun hasLineOfSight(src: Tile, srcSize: Int, dst: Tile, dstSize: Int): Boolean {
        if (src.plane != dst.plane) return false
        return LineValidator(DEFAULT_SEARCH_MAP_SIZE, allFlags).hasLineOfSight(src.x().toInt(), src.y().toInt(), src.plane().toInt(), dst.x().toInt(), dst.y().toInt(), srcSize, dstSize, dstSize)
    }

    @JvmStatic
	fun checkMeleeStep(from: Any?, fromSize: Int, to: Any?, toSize: Int): Boolean {
        val fromTile: Tile = WorldUtil.targetToTile(from)
        var toTile: Tile = WorldUtil.targetToTile(to)
        (to as? Entity)?.let { toTile = it.lastTile ?: it.tile }
        if (fromTile.plane != toTile.plane) return false
        return reached(allFlags, fromTile.x().toInt(), fromTile.y().toInt(), fromTile.plane().toInt(), toTile.x().toInt(), toTile.y().toInt(), toSize, toSize, fromSize, 0, -2, 0)
    }

    @JvmStatic
	fun containsPlayer(username: String): Boolean {
        for (p2 in players) {
            if (p2 == null) continue
            if (p2.username == username) return true
        }
        return false
    }

    @JvmStatic
    fun getFreeTile(center: Tile, distance: Int): Tile {
        var tile: Tile
        for (i in 0..9) {
            tile = Tile.of(center, distance)
            if (floorAndWallsFree(tile, 1)) return tile
        }
        return center
    }

    @JvmStatic
	fun getPlayerByUsername(username: String): Player? {
        return PLAYER_MAP_USERNAME[username]
    }

    @JvmStatic
	fun getPlayerByDisplay(displayName: String?): Player? {
        return PLAYER_MAP_DISPLAYNAME[Utils.formatPlayerNameForDisplay(displayName)]
    }

    @JvmStatic
	fun forceGetPlayerByDisplay(displayName: String, result: Consumer<Player?>) {
        var displayVar = displayName
        displayVar = Utils.formatPlayerNameForDisplay(displayVar)
        val player = getPlayerByDisplay(displayVar)
        if (player != null) {
            result.accept(player)
            return
        }
        WorldDB.getPlayers().getByUsername(Utils.formatPlayerNameForProtocol(displayVar)) { t: Player? -> result.accept(t) }
    }

    @JvmStatic
	val ticksTillUpdate: Long
        get() {
            if (SYSTEM_UPDATE_START == 0L) return -1
            return (SYSTEM_UPDATE_DELAY - (getServerTicks() - SYSTEM_UPDATE_START))
        }

    @JvmStatic
	fun safeShutdown(delay: Int) {
        if (SYSTEM_UPDATE_START != 0L) return
        SYSTEM_UPDATE_START = getServerTicks()
        SYSTEM_UPDATE_DELAY = delay
        for (player in players) {
            if (player == null || !player.hasStarted() || player.hasFinished()) continue
            player.packets.sendSystemUpdate(delay)
        }
        WorldTasks.schedule(delay) {
            try {
                for (player in players) {
                    if (player == null || !player.hasStarted()) continue
                    player.packets.sendLogout(true)
                    player.realFinish()
                    WorldDB.getPlayers().saveSync(player)
                }
                WorldPersistentData.save()
                Launcher.shutdown()
            } catch (e: Throwable) {
                Logger.handle(World::class.java, "safeShutdown", e)
            }
        }
    }

    @JvmStatic
	val data: WorldPersistentData
        get() = WorldPersistentData.get()

    @JvmStatic
    fun isSpawnedObject(obj: GameObject): Boolean {
        return ChunkManager.getChunk(obj.tile.chunkId).spawnedObjects.contains(obj)
    }

    @JvmStatic
    fun spawnObject(obj: GameObject) {
        ChunkManager.getChunk(obj.tile.chunkId).spawnObject(obj, true)
    }

    @JvmStatic
    fun spawnObject(obj: GameObject, clip: Boolean) {
        ChunkManager.getChunk(obj.tile.chunkId).spawnObject(obj, clip)
    }

    @JvmStatic
    fun removeObject(obj: GameObject) {
        ChunkManager.getChunk(obj.tile.chunkId).removeObject(obj)
    }

    @JvmOverloads
    @JvmStatic
    fun spawnObjectTemporary(obj: GameObject, ticks: Int, clip: Boolean = true) {
        spawnObject(obj, clip)
        WorldTasks.schedule(Utils.clampI(ticks - 1, 0, Int.MAX_VALUE), Runnable {
            try {
                if (!isSpawnedObject(obj)) return@Runnable
                removeObject(obj)
            } catch (e: Throwable) {
                Logger.handle(World::class.java, "spawnObjectTemporary", e)
            }
        })
    }

    @JvmStatic
    fun removeObjectTemporary(obj: GameObject?, ticks: Int): Boolean {
        if (obj == null) return false
        removeObject(obj)
        WorldTasks.schedule(object : Task() {
            override fun run() {
                try {
                    spawnObject(obj)
                } catch (e: Throwable) {
                    Logger.handle(World::class.java, "removeObjectTemporary", e)
                }
            }
        }, Utils.clampI(ticks, 0, Int.MAX_VALUE))
        return true
    }

    @JvmStatic
    fun spawnTempGroundObject(obj: GameObject, replaceId: Int, ticks: Int) {
        spawnObject(obj)
        WorldTasks.schedule(object : Task() {
            override fun run() {
                try {
                    removeObject(obj)
                    addGroundItem(Item(replaceId), obj.tile, null, false, 180)
                } catch (e: Throwable) {
                    Logger.handle(World::class.java, "spawnTempGroundObject", e)
                }
            }
        }, Utils.clampI(ticks - 1, 0, Int.MAX_VALUE))
    }

    @JvmStatic
	fun allPlayers(func: Consumer<Player?>) {
        synchronized(players) {
            for (p in players) func.accept(p)
        }
    }

    private fun <T> getObjectsInChunkRange(chunkId: Int, chunkRadius: Int, objectFetcher: (Chunk) -> List<T?>, filter: (T) -> Boolean = { true }): List<T> {
        return getChunkRadius(chunkId, chunkRadius)
            .asSequence()
            .mapNotNull { ChunkManager.getChunk(it) }
            .flatMap { objectFetcher(it).asSequence() }
            .filterNotNull()
            .filter(filter)
            .toList()
    }

    @JvmStatic
    fun getSpawnedObjectsInChunkRange(chunkId: Int, chunkRadius: Int): List<GameObject> =
        getObjectsInChunkRange(chunkId, chunkRadius, { it.spawnedObjects })

    @JvmStatic
    fun getAllObjectsInChunkRange(chunkId: Int, chunkRadius: Int): List<GameObject> =
        getObjectsInChunkRange(chunkId, chunkRadius, { it.getAllObjects() })

    @JvmStatic
    fun getBaseObjectsInChunkRange(chunkId: Int, chunkRadius: Int): List<GameObject> =
        getObjectsInChunkRange(chunkId, chunkRadius, { it.getBaseObjects() })

    @JvmStatic
    fun getNPCsInChunkRange(chunkId: Int, chunkRadius: Int): List<NPC> =
        getObjectsInChunkRange(chunkId, chunkRadius, { chunk ->
            chunk.npCsIndexes.map { NPCs[it] }
        }) { !it.hasFinished() }

    @JvmStatic
    fun getPlayersInChunkRange(chunkId: Int, chunkRadius: Int): List<Player> =
        getObjectsInChunkRange(chunkId, chunkRadius, { chunk ->
            chunk.playerIndexes.map { players[it] }
        }) { it.hasStarted() && !it.hasFinished() }

    @JvmStatic
    fun getPlayersInChunks(vararg chunkIds: Int): List<Player> =
        chunkIds.asSequence()
            .mapNotNull { ChunkManager.getChunk(it) }
            .flatMap { chunk -> chunk.playerIndexes.map { players[it] }.asSequence() }
            .filterNotNull()
            .filter { it.hasStarted() && !it.hasFinished() }
            .toList()

    @JvmStatic
    fun getAllGroundItemsInChunkRange(chunkId: Int, chunkRadius: Int): List<GroundItem> =
        getObjectsInChunkRange(chunkId, chunkRadius, { it.allGroundItems })

    @JvmStatic
    fun getChunkRadius(chunkId: Int, radius: Int): Set<Int> {
        val chunksXYLoop: MutableSet<Int> = IntOpenHashSet()
        for (cx in -radius..radius) {
            for (cy in -radius..radius) {
                chunksXYLoop.add(chunkId + cx * Chunk.X_INC + cy)
            }
        }
        return chunksXYLoop
    }

    @JvmStatic
    fun mapRegionIdsToChunks(mapRegionsIds: Set<Int>, plane: Int? = null): Set<Int> {
        return mapRegionsIds.flatMap { regionId ->
            val rCoords = MapUtils.decode(MapUtils.Structure.REGION, regionId)
            val cX = rCoords[0] shl 3
            val cY = rCoords[1] shl 3
            generateChunkIds(cX, cY, plane)
        }.toSet()
    }

    @JvmStatic
    fun regionIdToChunkSet(regionId: Int): Set<Int> {
        val rCoords = MapUtils.decode(MapUtils.Structure.REGION, regionId)
        val cX = rCoords[0] shl 3
        val cY = rCoords[1] shl 3
        return generateChunkIds(cX, cY, null)
    }

    private fun generateChunkIds(cX: Int, cY: Int, plane: Int?): Set<Int> {
        val chunkIds: MutableSet<Int> = IntOpenHashSet()
        val planes = plane?.let { listOf(it) } ?: (0..3)
        for (pl in planes) {
            for (x in 0..7) {
                for (y in 0..7) {
                    chunkIds.add(MapUtils.encode(MapUtils.Structure.CHUNK, cX + x, cY + y, pl))
                }
            }
        }
        return chunkIds
    }

    @JvmStatic
    fun refreshObject(obj: GameObject) {
        ChunkManager.getChunk(obj.tile.chunkId).addChunkUpdate(AddObject(obj.tile.chunkLocalHash, obj))
        if (obj.meshModifier != null)
            ChunkManager.getChunk(obj.tile.chunkId).addChunkUpdate(CustomizeObject(obj.tile.chunkLocalHash, obj, obj.meshModifier.modelIds, obj.meshModifier.modifiedColors, obj.meshModifier.modifiedTextures))
    }

    @JvmStatic
    fun getObject(tile: Tile): GameObject? {
        return ChunkManager.getChunk(tile.chunkId).getObject(tile)
    }

    @JvmStatic
    fun getBaseObjects(tile: Tile): Array<GameObject> {
        return ChunkManager.getChunk(tile.chunkId).getBaseObjects(tile)
    }

    @JvmStatic
    fun getSpawnedObject(tile: Tile): GameObject? {
        return ChunkManager.getChunk(tile.chunkId).getSpawnedObject(tile)
    }

    @JvmStatic
    fun getObject(tile: Tile, type: ObjectType?): GameObject? {
        return ChunkManager.getChunk(tile.chunkId).getObject(tile, type)
    }

    @JvmStatic
    fun setObjectRouteType(id: Int, routeType: GameObject.RouteType) {
        GAMEOBJECT_ROUTE_TYPE_MAPPINGS[id] = routeType
    }

    @JvmStatic
	fun getRouteType(id: Int): GameObject.RouteType {
        return GAMEOBJECT_ROUTE_TYPE_MAPPINGS[id] ?: GameObject.RouteType.NORMAL
    }

    @Deprecated("")
    @JvmStatic
    fun addGroundItemForever(item: Item?, tile: Tile, respawnTicks: Int) {
        val groundItem: GroundItem = GroundItem(item, tile, GroundItem.GroundItemType.FOREVER).setRespawnTicks(respawnTicks)
        if (groundItem.id == -1) return
        ChunkManager.getChunk(tile.chunkId).addGroundItem(groundItem)
    }

    @JvmStatic
    @JvmOverloads
    fun addGroundItemNoExpire(item: Item, tile: Tile, owner: Player? = null): GroundItem? {
        return addGroundItem(item, tile, owner, owner != null, -1, World.DropMethod.NORMAL, -1)
    }

    @JvmOverloads
    @JvmStatic
    fun addGroundItem(item: Item, tile: Tile, owner: Player? = null, invisible: Boolean = true, hiddenTime: Int = 60, type: DropMethod = DropMethod.NORMAL, deleteTime: Int = 150): GroundItem? {
        if ((item.id == -1) || (owner != null && owner.rights == Rights.ADMIN)) return null
        if (type != DropMethod.NORMAL) if (type == DropMethod.TURN_UNTRADEABLES_TO_COINS && !ItemConstants.isTradeable(item)) {
            val price = item.definitions.getValue()
            if (price <= 0) return null
            item.id = 995
            item.setAmount(price)
        }
        val floorItem = GroundItem(item, tile, owner?.username, if (invisible) GroundItem.GroundItemType.INVISIBLE else GroundItem.GroundItemType.NORMAL)
        if (floorItem.amount > 1 && !item.definitions.isStackable && floorItem.metaData == null) for (i in 0 until floorItem.amount) {
            val oneItem = Item(item.id, 1)
            val newItem = GroundItem(oneItem, tile, owner?.username, if (invisible) GroundItem.GroundItemType.INVISIBLE else GroundItem.GroundItemType.NORMAL)
            finalizeGroundItem(newItem, tile, owner, hiddenTime, deleteTime)
        }
        else finalizeGroundItem(floorItem, tile, owner, hiddenTime, deleteTime)
        return floorItem
    }

    @JvmStatic
    private fun finalizeGroundItem(item: GroundItem, tile: Tile, owner: Player?, hiddenSeconds: Int, lifeSeconds: Int) {
        if ((item.id == -1) || (owner != null && owner.rights == Rights.ADMIN)) return
        if (ChunkManager.getChunk(tile.chunkId).addGroundItem(item)) {
            if (lifeSeconds != -1) item.deleteTime = Ticks.fromSeconds(lifeSeconds + hiddenSeconds)
            if (item.isInvisible) if (hiddenSeconds != -1) item.privateTime = Ticks.fromSeconds(hiddenSeconds)
        }
    }

    @JvmStatic
    fun removeGroundItem(groundItem: GroundItem): Boolean {
        return ChunkManager.getChunk(groundItem.tile.chunkId).deleteGroundItem(groundItem)
    }

    @JvmStatic
    fun removeGroundItem(player: Player, floorItem: GroundItem): Boolean {
        return removeGroundItem(player, floorItem, true)
    }

    @JvmStatic
    fun removeGroundItem(player: Player, groundItem: GroundItem, add: Boolean): Boolean {
        if (groundItem.id == -1) return false
        val chunk: Chunk = ChunkManager.getChunk(groundItem.tile.chunkId)
        if (!chunk.itemExists(groundItem)) return false
        if (player.isIronMan && groundItem.sourceId != 0 && groundItem.sourceId != player.uuid) {
            player.sendMessage("You may not pick up other players items as an ironman.")
            return false
        }
        if (add && !player.inventory.hasRoomFor(groundItem)) {
            player.sendMessage("Not enough space in your inventory.")
            return false
        }
        if (chunk.deleteGroundItem(groundItem)) {
            if (add) {
                if (!player.inventory.addItem(Item(groundItem.id, groundItem.amount, groundItem.metaData))) return false
                if (groundItem.sourceId != 0 && groundItem.sourceId != player.uuid) WorldDB.getLogs().logPickup(player, groundItem)
            }
            if (groundItem.isRespawn) WorldTasks.schedule(groundItem.respawnTicks) {
                try {
                    @Suppress("DEPRECATION")
                    addGroundItemForever(groundItem, groundItem.tile, groundItem.respawnTicks)
                } catch (e: Throwable) {
                    Logger.handle(World::class.java, "removeGroundItem", e)
                }
            }
            return true
        }
        return false
    }

    @JvmStatic
    fun sendObjectAnimation(obj: GameObject?, animation: Animation?) {
        if (obj == null) return
        ChunkManager.getChunk(obj.tile.chunkId).addObjectAnim(obj, animation)
    }

    @JvmStatic
    fun sendObjectAnimation(obj: GameObject?, animation: Int) {
        sendObjectAnimation(obj, Animation(animation))
    }

    @JvmStatic
    fun sendSpotAnim(tile: Tile, anim: SpotAnim?) {
        ChunkManager.getChunk(tile.chunkId).addSpotAnim(tile, anim)
    }

    @JvmStatic
    fun sendSpotAnim(tile: Tile, anim: Int) {
        ChunkManager.getChunk(tile.chunkId).addSpotAnim(tile, SpotAnim(anim))
    }

    /**
     * Sends a projectile from the specified source to the target with a given graphic ID, delay, speed, and optional parameters.
     * This function uses default values for angle and offset if they are not provided.
     *
     * @param from The source from which the projectile is sent. Can be any object representing a position or entity.
     * @param to The target to which the projectile is sent. Can be any object representing a position or entity.
     * @param spotAnimId The ID of the spotanim to use for the projectile.
     * @param delay The delay before the projectile is sent, in client frames.
     * @param speed The speed of the projectile. This is measured in client frames spent in the air per tile of distance. So a lower number is a faster projectile.
     * @param angle (Optional) The angle at which the projectile is sent. Default is 0.
     * @param offset (Optional) The offset of the projectile's trajectory. Default is 0.
     * @param task (Optional) A task to be executed when the projectile hits the target.
     * @return The `WorldProjectile` object representing the sent projectile.
     */
    @JvmStatic
    @JvmOverloads
    fun sendProjectile(from: Any, to: Any, spotAnimId: Int, delay: Int, speed: Int, angle: Int = 0, offset: Int = 0, task: Consumer<WorldProjectile>? = null): WorldProjectile {
        return sendProjectile(from, to, spotAnimId, 28 to 28, delay, speed, angle, offset, task)
    }

    @JvmStatic
    @JvmOverloads
    fun sendProjectile(from: Any, to: Any, spotAnimId: Int, heights: Pair<Int, Int>, delay: Int, speed: Int, angle: Int = 0, offset: Int = 0, task: Consumer<WorldProjectile>? = null): WorldProjectile {
        val sourceTile = getTargetTile(from)
        val targetTile = getTargetTile(to)
        return sendProjectileAbsoluteSpeed(from, to, spotAnimId, heights, delay, Utils.getDistanceI(sourceTile, targetTile) * speed, angle, offset, task)
    }

    /**
     * Sends a projectile from the specified source to the target with a given graphic ID, heights, delay, speed, and optional parameters.
     * This function calculates the speed based on the distance between the source and the target.
     *
     * @param from The source from which the projectile is sent. Can be any object representing a position or entity.
     * @param to The target to which the projectile is sent. Can be any object representing a position or entity.
     * @param spotAnimId The ID of the spotanim to use for the projectile.
     * @param heights A pair representing the start and end heights of the projectile.
     * @param speed The speed of the projectile. This is measured in client frames spent in the air per tile of distance. So a lower number is a faster projectile.
     * @param angle (Optional) The angle at which the projectile is sent. Default is 0.
     * @param offset (Optional) The offset of the projectile's trajectory. Default is 0.
     * @param task (Optional) A task to be executed when the projectile hits the target.
     * @return The `WorldProjectile` object representing the sent projectile.
     */
    @JvmStatic
    fun sendProjectile(from: Any, to: Any, spotAnimId: Int, heights: Pair<Int, Int>, speed: Int, angle: Int = 0, offset: Int = 0, task: Consumer<WorldProjectile>? = null): WorldProjectile {
        val sourceTile = getTargetTile(from)
        val targetTile = getTargetTile(to)
        return sendProjectileAbsoluteSpeed(from, to, spotAnimId, heights, 0, Utils.getDistanceI(sourceTile, targetTile) * speed, angle, offset, task)
    }

    /**
     * Sends a projectile from the specified source to the target with a given graphic ID, heights, delay, speed, and optional parameters.
     * This function uses an absolute speed value and includes calculations for trajectory offset.
     *
     * @param from The source from which the projectile is sent. Can be any object representing a position or entity.
     * @param to The target to which the projectile is sent. Can be any object representing a position or entity.
     * @param spotAnimId The ID of the spotanim to use for the projectile.
     * @param heights A pair representing the start and end heights of the projectile.
     * @param delay (Optional) The delay before the projectile is sent, in ticks. Default is 0.
     * @param speed The absolute speed of the projectile. This is measured in client frames it will take to reach the target. So a lower number is a faster projectile.
     * @param angle The angle at which the projectile is sent.
     * @param offset (Optional) The offset of the projectile's trajectory. Default is 0.
     * @param task (Optional) A task to be executed when the projectile hits the target.
     * @return The `WorldProjectile` object representing the sent projectile.
     */
    @JvmStatic
    @JvmOverloads
    fun sendProjectileAbsoluteSpeed(from: Any, to: Any, spotAnimId: Int, heights: Pair<Int, Int>, delay: Int = 0, speed: Int, angle: Int, offset: Int = 0, task: Consumer<WorldProjectile>? = null): WorldProjectile {
        val (fromSizeX, _) = getTargetSize(from)
        val calcedOffset = fromSizeX * 32 + offset
        val projectile = WorldProjectile(from, to, spotAnimId, heights.first, heights.second, delay, speed, calcedOffset, angle, task)
        if (spotAnimId != -1) {
            val chunkId = getTargetChunkId(from)
            ChunkManager.getChunk(chunkId).addProjectile(projectile)
        }
        return projectile
    }

    @JvmStatic
    private fun getTargetSize(target: Any): Pair<Int, Int> = when (target) {
        is Entity -> target.size to target.size
        is GameObject -> {
            val defs = target.definitions
            defs.getSizeX() to defs.getSizeY()
        }
        else -> 1 to 1
    }

    @JvmStatic
    private fun getTargetTile(obj: Any): Tile = when (obj) {
        is Tile -> obj
        is Entity -> obj.middleTile
        is GameObject -> obj.tile
        else -> throw RuntimeException("Invalid target type. $obj")
    }

    @JvmStatic
    private fun getTargetChunkId(obj: Any): Int = getTargetTile(obj).chunkId

    @JvmStatic
    fun isMultiArea(tile: Tile): Boolean {
        val chunkId = MapUtils.encode(MapUtils.Structure.CHUNK, tile.chunkX, tile.chunkY)
        return Areas.withinArea("multi", chunkId) || (ChunkManager.getChunk(chunkId)?.isMulticombat ?: false)
    }

    @JvmStatic
	fun isPvpArea(player: Player): Boolean {
        return WildernessController.isAtWild(player.tile)
    }

    @JvmStatic
    fun jingle(source: Tile, jingleId: Int, delay: Int) {
        sound(source, jingleId, delay, Sound.SoundType.JINGLE)
    }

    @JvmStatic
    fun jingle(source: Tile, jingleId: Int) {
        sound(source, jingleId, 0, Sound.SoundType.JINGLE)
    }

    @JvmStatic
    fun musicTrack(source: Tile, trackId: Int, delay: Int, volume: Int) {
        sound(source, trackId, delay, Sound.SoundType.MUSIC).volume(volume)
    }

    @JvmOverloads
    @JvmStatic
    fun musicTrack(source: Tile, trackId: Int, delay: Int = 100) {
        sound(source, trackId, delay, Sound.SoundType.MUSIC)
    }

    @JvmStatic
    fun sound(tile: Tile, sound: Sound): Sound {
        ChunkManager.getChunk(tile.chunkId).addSound(tile, sound)
        return sound
    }

    @JvmStatic
    fun sound(source: Tile, soundId: Int, delay: Int, type: Sound.SoundType?): Sound {
        return sound(source, Sound(soundId, delay, type))
    }

    @JvmOverloads
    @JvmStatic
    fun soundEffect(source: Tile, soundId: Int, delay: Int = 0) {
        sound(source, soundId, delay, Sound.SoundType.EFFECT).radius(10)
    }

    @JvmOverloads
    @JvmStatic
    fun voiceEffect(source: Tile, voiceId: Int, delay: Int = 0) {
        sound(source, voiceId, delay, Sound.SoundType.VOICE)
    }

    @JvmStatic
    fun getClosestObject(tile: Tile, matchCondition: (GameObject) -> Boolean): GameObject? {
        for (dist in 0..15) {
            for (dx in -dist..dist) {
                for (dy in listOf(-dist, dist)) {
                    getObject(tile.transform(dx, dy))?.takeIf(matchCondition)?.let { return it }
                }
            }
            for (dy in -dist + 1 until dist) {
                for (dx in listOf(-dist, dist)) {
                    getObject(tile.transform(dx, dy))?.takeIf(matchCondition)?.let { return it }
                }
            }
        }
        return null
    }

    @JvmStatic
    fun getClosestObjectByObjectId(objectId: Int, tile: Tile): GameObject? =
        getClosestObject(tile) { it.id == objectId }

    @JvmStatic
    fun getClosestObjectByType(type: ObjectType, tile: Tile): GameObject? =
        getClosestObject(tile) { it.type == type }

    @JvmStatic
    fun getClosestObjectByTypeAndObjectId(type: ObjectType?, objectId: Int, tile: Tile): GameObject? =
        getClosestObject(tile) { it.id == objectId && (type == null || it.type == type) }

    @JvmStatic
    fun getClosestObjectByName(name: String, tile: Tile): GameObject? =
        getClosestObject(tile) { it.definitions.name == name }

    @JvmStatic
    fun getClosestObject(name: String, tile: Tile, range: Int): GameObject? {
        var closest: GameObject? = null
        var closestDist = Double.MAX_VALUE
        for (x in -range until range) {
            for (y in -range until range) {
                val obj: GameObject? = getObject(tile.transform(x, y))
                if (obj != null && obj.definitions.name == name) {
                    val newDist = Utils.getDistance(obj.coordFace, tile)
                    if (newDist < closestDist) {
                        closest = obj
                        closestDist = newDist
                    }
                }
            }
        }
        return closest
    }

    @JvmStatic
    fun getObjectWithType(tile: Tile, type: ObjectType?): GameObject? {
        return ChunkManager.getChunk(tile.chunkId).getObjectWithType(tile, type)
    }

    @JvmStatic
    private fun getObjectWithSlot(tile: Tile, slot: Int): GameObject? {
        return ChunkManager.getChunk(tile.chunkId).getObjectWithSlot(tile, slot)
    }

    @JvmStatic
    fun containsObjectWithId(tile: Tile, id: Int): Boolean {
        return ChunkManager.getChunk(tile.chunkId).containsObjectWithId(tile, id)
    }

    @JvmStatic
    fun getObjectWithId(tile: Tile, id: Int): GameObject? {
        return ChunkManager.getChunk(tile.chunkId).getObjectWithId(tile, id)
    }

    @JvmStatic
	fun sendWorldMessage(message: String?, forStaff: Boolean) {
        for (p in players) {
            if (p == null || !p.isRunning || p.isYellOff || (forStaff && !p.hasRights(Rights.MOD))) continue
            p.sendMessage(message)
        }
    }

    @JvmStatic
    fun findAdjacentFreeTile(tile: Tile, vararg blacklistedDirections: Direction): Tile? {
        val step = StepValidator(allFlags)
        val unchecked = Direction.entries.filterNot { it in blacklistedDirections }.toMutableList()
        while (unchecked.isNotEmpty()) {
            val curr = unchecked.random()
            if (step.canTravel(tile.plane().toInt(), tile.x().toInt(), tile.y().toInt(), curr.dx, curr.dy, 1, 0, CollisionStrategyType.NORMAL.strategy))
                return tile.transform(curr.dx, curr.dy)
            unchecked.remove(curr)
        }
        return null
    }

    @JvmStatic
    fun checkWalkStep(tile: Tile, dir: Direction, size: Int): Boolean {
        val step = StepValidator(allFlags)
        return step.canTravel(tile.plane().toInt(), tile.x().toInt(), tile.y().toInt(), dir.dx, dir.dy, size, 0, CollisionStrategyType.NORMAL.strategy)
    }

    @JvmStatic
    fun checkWalkStep(tile: Tile, toTile: Tile, size: Int): Boolean {
        val dir = forDelta(toTile.x() - tile.x(), toTile.y() - tile.y()) ?: return false
        val step = StepValidator(allFlags)
        return step.canTravel(tile.plane().toInt(), tile.x().toInt(), tile.y().toInt(), dir.dx, dir.dy, size, 0, CollisionStrategyType.NORMAL.strategy)
    }

    /**
     * Please someone refactor this. This is beyond disgusting and definitely can be done better.
     */
    @JvmStatic
    fun findAdjacentFreeSpace(origin: Tile, size: Int): Tile? {
        if (size == 1) return findAdjacentFreeTile(origin)

        val stepValidator = StepValidator(allFlags)
        val directions = mutableListOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)

        while (directions.isNotEmpty()) {
            val currDir = directions.removeAt(Utils.random(directions.size))
            val isHorizontal = currDir.dy == 0
            val offsetDir = if (isHorizontal) Direction.NORTH else Direction.WEST

            var failed = false
            for (i in 0 until size) {
                for (row in 0 until size) {
                    if (!stepValidator.canTravel(origin.plane().toInt(), origin.x, origin.y, currDir.dx, currDir.dy, size, 0, CollisionStrategyType.NORMAL.strategy) ||
                        (row < size - 1 && !stepValidator.canTravel(origin.plane().toInt(), origin.x, origin.y, offsetDir.dx, offsetDir.dy, size, 0, CollisionStrategyType.NORMAL.strategy))) {
                        failed = true
                        break
                    }
                }
                if (failed) break
            }

            if (!failed) {
                val deltaX = if (currDir.dx < 0) -size + 1 else currDir.dx
                val deltaY = if (currDir.dy < 0) -size + 1 else currDir.dy
                return origin.transform(deltaX, deltaY)
            }
        }

        return null
    }

    @JvmStatic
    fun findClosestAdjacentFreeTile(tile: Tile, dist: Int): Tile {
        //Checks outward - Northeast
        for (x in 0..dist) for (y in 0..dist) if (floorFree(tile.plane, tile.x + x, tile.y + y)) return tile.transform(x, y, 0)
        //Checks outward - Southeast
        for (x in 0..dist) for (y in 0 downTo -dist) if (floorFree(tile.plane, tile.x + x, tile.y + y)) return tile.transform(x, y, 0)
        //Checks outward - Southwest
        for (x in 0 downTo -dist) for (y in 0 downTo -dist) if (floorFree(tile.plane, tile.x + x, tile.y + y)) return tile.transform(x, y, 0)
        //Checks outward - Northwest
        for (x in 0 downTo -dist) for (y in 0..dist) if (floorFree(tile.plane, tile.x + x, tile.y + y)) return tile.transform(x, y, 0)
        return tile.transform(0, 0, 0)
    }

    @JvmStatic
    fun getServerTicks() = WorldThread.WORLD_CYCLE

    @JvmStatic
    fun getSurroundingBaseObjects(base: GameObject, radius: Int): List<GameObject> {
        val objects: ArrayList<GameObject> = ArrayList<GameObject>()
        for (obj in ChunkManager.getChunk(base.tile.chunkId).getBaseObjects()) {
            if (obj?.definitions == null) continue
            if (Utils.getDistance(base.tile, obj.tile) <= radius) objects.add(obj)
        }
        return objects
    }

    @JvmStatic
	fun broadcastLoot(message: String) {
        sendWorldMessage("<img=4><shad=000000><col=00FF00>$message", false)
    }

    @JvmStatic
	fun processEntityLists() {
        players.processPostTick()
        NPCs.processPostTick()
    }

    @JvmStatic
    fun getRandomWalkableTileInChunk(chunk: Chunk, maxAttempts: Int = 64): Tile? {
        repeat(maxAttempts) {
            val x = chunk.baseX + Utils.random(8)
            val y = chunk.baseY + Utils.random(8)
            Tile.of(x, y, 0).takeIf { World.floorAndWallsFree(it, 1) }?.let { return it }
        }
        return null
    }

    enum class DropMethod {
        NORMAL, TURN_UNTRADEABLES_TO_COINS
    }
}
