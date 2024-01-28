package com.rs.game.content.dnds.eviltree

import com.google.common.collect.Iterators
import com.rs.cache.loaders.ObjectType
import com.rs.game.World
import com.rs.game.content.skills.woodcutting.Hatchet
import com.rs.game.content.skills.woodcutting.TreeType
import com.rs.game.model.entity.pathing.Direction
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Tile
import com.rs.lib.util.Vec2
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick
import java.util.*
import kotlin.math.ceil

private var currentTree: EvilTree? = null
private var LOCATIONS: List<Location> = ArrayList(Location.entries.toTypedArray().asList())
private var locIterator = Iterators.peekingIterator(LOCATIONS.iterator())

enum class Type(val wcType: TreeType, val wcReq: Int, val wcXp: Double, val farmReq: Int, val farmXp: Double, val fmReq: Int, val fmXp: Double, val healthyObj: Int, val deg1Obj: Int, val deg2Obj: Int, val deadObj: Int) {
    NORMAL(TreeType.NORMAL,1, 15.0, 1, 20.0, 1, 20.0, 11434, 11435, 11436, 14839),
    OAK(TreeType.OAK,15, 32.4, 7, 45.0, 15, 30.0, 11437, 11438, 11439, 14840),
    WILLOW(TreeType.WILLOW,30, 45.7, 15, 66.0, 30, 45.0, 11440, 11441, 11442, 14841),
    MAPLE(TreeType.MAPLE,45, 55.8, 22, 121.5, 45, 67.5, 11443, 11444, 11915, 14842),
    YEW(TreeType.YEW,60, 87.5, 30, 172.5, 60, 101.25, 11916, 11917, 11918, 14843),
    MAGIC(TreeType.MAGIC,75, 125.0, 37, 311.5, 75, 151.75, 11919, 11920, 11921, 14844),
    ELDER(TreeType.MAGIC,90, 162.5, 42, 730.0, 90, 260.5, 11922, 11923, 11924, 14845)
}

enum class Location(val tile: Tile, val desc: String) {
    SEERS(Tile(2703, 3445, 0), "south of Seer's Village."),
    RIMMINGTON(Tile(2982, 3204, 0), "east of Rimmington."),
    FALADOR(Tile(2991, 3407, 0), "north of Falador."),
    TOLNA_RIFT(Tile(3321, 3454, 0), "outside Tolna's Rift."),
}

@ServerStartupEvent
fun schedule() {
    LOCATIONS = LOCATIONS.shuffled()
    locIterator = Iterators.peekingIterator(LOCATIONS.iterator())
    WorldTasks.scheduleNthHourly(2, ::spawnTree)
}

fun spawnTree() {
    currentTree?.despawn()
    val loc = nextLocation()
    currentTree = EvilTree(Type.entries.random(), loc.tile)
    World.sendWorldMessage("<col=FF0000><shad=000000>A an evil tree has begun to sprout near ${loc.desc}!", false)

}

private fun nextLocation(): Location {
    if (!locIterator.hasNext()) locIterator =
        Iterators.peekingIterator(LOCATIONS.iterator())
    return locIterator.next()
}

class EvilTree(private val treeType: Type, private val centerTile: Tile) : GameObject(11391, ObjectType.SCENERY_INTERACT, 0, centerTile) {
    private var stage = 0
    private var stageProgress = 0
    private val fires = mapOf(
        Direction.WEST to GameObject(14887, ObjectType.STRAIGHT_INSIDE_WALL_DEC, 2, tile.transform(-2, 0, 0)),
        Direction.NORTH to GameObject(15253, ObjectType.STRAIGHT_INSIDE_WALL_DEC, 3, tile.transform(0, 2, 0)),
        Direction.EAST to GameObject(14888, ObjectType.STRAIGHT_INSIDE_WALL_DEC, 0, tile.transform(2, 0, 0)),
        Direction.SOUTH to GameObject(15254, ObjectType.STRAIGHT_INSIDE_WALL_DEC, 1, tile.transform(0, -2, 0)),
        Direction.NORTHWEST to GameObject(14889, ObjectType.DIAGONAL_INSIDE_WALL_DEC, 0, tile.transform(-1, 1, 0)),
        Direction.SOUTHEAST to GameObject(15255, ObjectType.DIAGONAL_INSIDE_WALL_DEC, 2, tile.transform(1, -1, 0)),
        Direction.NORTHEAST to GameObject(14890, ObjectType.DIAGONAL_INSIDE_WALL_DEC, 1, tile.transform(1, 1, 0)),
        Direction.SOUTHWEST to GameObject(15491, ObjectType.DIAGONAL_INSIDE_WALL_DEC, 3, tile.transform(-1, -1, 0)),
    )

    fun spawn() {
        World.spawnObject(this)
        fires.values.forEach { World.spawnObject(it) }
    }

    fun despawn() {
        World.removeObject(this)
        fires.values.forEach { World.removeObject(it) }
    }

    fun inspect(player: Player) {

    }

    fun nurture(player: Player) {
        if (player.skills.getLevel(Skills.FARMING) < treeType.farmReq) {
            player.sendMessage("You need a farming level of ${treeType.farmReq} to nurture this tree.")
            return
        }
        player.repeatAction(5) {
            player.faceTile(this.coordFace)
            player.anim(3114)
            incProgress()
            return@repeatAction stage < 5
        }
    }

    val ceilNegs: (Float) -> Float = { if (it < 0) ceil(it) - 1 else ceil(it) }

    fun lightFire(player: Player) {
        if (player.skills.getLevel(Skills.FIREMAKING) < treeType.fmReq) {
            player.sendMessage("You need a firemaking level of ${treeType.fmReq} to burn this tree.")
            return
        }
        if (!player.inventory.containsItem(14666)) {
            player.sendMessage("You need some wood chippings to light on fire.")
            return
        }
        val sub = player.getMiddleTileAsVector().sub(Vec2(centerTile))
        sub.norm()
        val fire = fires[Direction.forDelta(ceilNegs(sub.x).toInt(), ceilNegs(sub.y).toInt())]
        if (fire == null) {
            player.sendMessage("You can't light a fire here.")
            return
        }
        if (player.vars.getVarBit(fire.definitions.varpBit) != 0) {
            player.sendMessage("You've already lit a fire here.")
            return
        }
        player.anim(16700)
        player.skills.addXp(Skills.FIREMAKING, treeType.fmXp)
        player.inventory.deleteItem(14666, 1)
        player.vars.setVarBit(fire.definitions.varpBit, 1)
    }

    fun chop(player: Player) {
        if (player.skills.getLevel(Skills.WOODCUTTING) < treeType.wcReq) {
            player.sendMessage("You need a woodcutting level of ${treeType.wcReq} to chop this tree.")
            return
        }
        player.actionManager.setAction(object: PlayerAction() {
            val hatchet = Hatchet.getBest(player)

            override fun start(player: Player): Boolean {
                player.faceObject(this@EvilTree)
                if (hatchet == null) {
                    player.sendMessage("You do not have a hatchet that you have the woodcutting level to use.")
                    return false
                }
                return true
            }

            override fun process(player: Player): Boolean {
                player.faceObject(this@EvilTree)
                player.anim(hatchet.getAnim(TreeType.NORMAL))
                return this@EvilTree.getDefinitions(player).containsOption("Chop")
            }

            override fun processWithDelay(player: Player): Int {
                if (!treeType.wcType.rollSuccess(1.0, player.skills.getLevel(Skills.WOODCUTTING), hatchet)) return 3

                player.skills.addXp(Skills.WOODCUTTING, treeType.wcXp)
                player.inventory.addItemDrop(14666, 1)
                this@EvilTree.incProgress()
                return 3
            }

            override fun stop(player: Player) {
                player.anim(-1)
            }
        })
    }

    fun takeRewards(player: Player) {

    }

    private fun incProgress() {
        if (stage < 5) {
            if (++stageProgress >= 2) { //nurture stages
                incStage()
                stageProgress = 0
            }
        } else {
            if (++stageProgress >= 5) { //successful cut stages
                incStage()
                stageProgress = 0
            }
        }
    }

    private fun incStage() {
        if (stage >= 9)
            return
        if (++stage == 3) {
            World.removeObject(this)
            id += 1
            tile = tile.transform(-1, -1)
            World.spawnObject(this)
            return
        }
        if (stage == 8) {
            val varbits = fires.values.map { it.definitions.varpBit }
            fires.values.forEach { World.removeObject(it) }
            World.getPlayersInChunkRange(tile.chunkId, 1)
                .forEach { player -> varbits
                    .forEach { varbit ->
                        player.vars.setVarBit(varbit, 0)
                    }
                }
        }
        if (stage <= 4)
            setId(id + 1)
        else
            setId(when(stage) {
                5 -> treeType.healthyObj
                6 -> treeType.deg1Obj
                7 -> treeType.deg2Obj
                else -> treeType.deadObj
            })
    }
}

@ServerStartupEvent
fun mapEvilTrees() {
    onObjectClick(*(11391..11395).union(Type.entries.flatMap { listOf(it.healthyObj, it.deg1Obj, it.deg2Obj, it.deadObj) }).toTypedArray()) { (player, obj, option) ->
        if (obj !is EvilTree) return@onObjectClick

        when(option) {
            "Nurture" -> obj.nurture(player)
            "Chop" -> obj.chop(player)
            "Light fire" -> obj.lightFire(player)
            "Inspect" -> obj.inspect(player)
            "Take-rewards" -> obj.takeRewards(player)
        }
    }
}



