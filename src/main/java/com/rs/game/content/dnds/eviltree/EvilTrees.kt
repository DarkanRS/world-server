package com.rs.game.content.dnds.eviltree

import com.google.common.collect.Iterators
import com.rs.cache.loaders.ObjectType
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.engine.dialogue.startConversation
import com.rs.game.World
import com.rs.game.content.Effect
import com.rs.game.content.skills.woodcutting.Hatchet
import com.rs.game.content.skills.woodcutting.TreeType
import com.rs.game.content.transportation.SpiritTree
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.pathing.Direction
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.lib.util.Vec2
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks
import java.util.*
import kotlin.math.ceil

const val NURTURES_PER_STAGE = 25
const val CHOPS_PER_STAGE = 250

const val STAGE_NEW = 0
const val STAGE_NURTURED_LARGER = 3
const val STAGE_LAST_NURTURE = 4
const val STAGE_FULLY_GROWN = 5
const val STAGE_CHOPPED_UP_1 = 6
const val STAGE_CHOPPED_UP_2 = 7
const val STAGE_DEAD = 8

const val KINDLING = 14666

var currentTree: EvilTree? = null
var LOCATIONS: List<Location> = ArrayList(Location.entries.toTypedArray().asList())
var locIterator = Iterators.peekingIterator(LOCATIONS.iterator())

enum class Type(val wcType: TreeType, val wcReq: Int, val wcXp: Double, val farmReq: Int, val farmXp: Double, val fmReq: Int, val fmXp: Double, val healthyObj: Int, val deg1Obj: Int, val deg2Obj: Int, val dyingObj: Int, val deadObj: Int) {
    NORMAL(TreeType.NORMAL,1, 15.0, 1, 20.0, 1, 20.0, 11434, 11435, 11436, 11925, 14846),
    OAK(TreeType.OAK,15, 32.4, 7, 45.0, 15, 30.0, 11437, 11438, 11439, 11926, 14847),
    WILLOW(TreeType.WILLOW,30, 45.7, 15, 66.0, 30, 45.0, 11440, 11441, 11442, 11927, 14848),
    MAPLE(TreeType.WILLOW,45, 55.8, 22, 121.5, 45, 67.5, 11443, 11444, 11915, 11928, 14849),
    YEW(TreeType.WILLOW,60, 87.5, 30, 172.5, 60, 101.25, 11916, 11917, 11918, 11929, 14883),
    MAGIC(TreeType.TEAK,75, 125.0, 37, 311.5, 75, 151.75, 11919, 11920, 11921, 12711, 14884),
    ELDER(TreeType.MAPLE,85, 162.5, 42, 730.0, 85, 260.5, 11922, 11923, 11924, 12712, 14885)
}

enum class Location(val tile: Tile, val desc: String) {
    SEERS(Tile(2703, 3445, 0), "the south of Seer's Village"),
    RIMMINGTON(Tile(2982, 3204, 0), "the east of Rimmington"),
    FALADOR(Tile(2991, 3407, 0), "the north of Falador"),
    TOLNA_RIFT(Tile(3321, 3454, 0), "Tolna's Rift"),
    CATHERBY_WEST(Tile(2750, 3422, 0), "the west of Catherby"),
    SEERS_BANK(Tile(2708, 3507, 0), "the Seers Village bank"),
    LEGENDS_GUILD(Tile(2724, 3331, 0), "the Legends' Guild"),
    MOBILISING_ARMIES(Tile(2470, 2838, 0), "Mobilising Armies"),
    DRAYNOR(Tile(3098, 3228, 0), "Draynor Village"),
    GNOME_STRONGHOLD(Tile(2404, 3433, 0), "the Gnome Stronghold"),
    FALADOR_WEST(Tile(2925, 3378, 0), "the west of Falador"),
    EDGEVILLE(Tile(3050, 3458, 0), "Edgeville"),
    YANILLE_1(Tile(2604, 3119, 0), "Yanille"),
    YANILLE_2(Tile(2521, 3104, 0), "Yanille"),
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
    currentTree = EvilTree(Type.entries.random(), loc, loc.tile)
    currentTree!!.spawn()
    //World.sendWorldMessage("<col=FF0000><shad=000000>A an evil tree has begun to sprout near ${loc.desc}!", false)
}

private fun nextLocation(): Location {
    if (!locIterator.hasNext()) locIterator =
        Iterators.peekingIterator(LOCATIONS.iterator())
    return locIterator.next()
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

    onObjectClick(11428) { (player, obj) ->
        if (obj !is EvilTree.Root) return@onObjectClick

        player.faceObject(obj)
        if (player.skills.getLevel(Skills.WOODCUTTING) < obj.tree.treeType.wcReq) {
            player.sendMessage("You need a woodcutting level of ${obj.tree.treeType.wcReq} to chop this tree.")
            return@onObjectClick
        }
        val hatchet = Hatchet.getBest(player)
        if (hatchet == null) {
            player.sendMessage("You do not have a hatchet that you have the woodcutting level to use.")
            return@onObjectClick
        }
        player.repeatAction(3) {
            player.anim(hatchet.getAnim(TreeType.NORMAL))
            player.faceObject(obj)
            if (!obj.tree.treeType.wcType.rollSuccess(player.auraManager.woodcuttingMul, player.skills.getLevel(Skills.WOODCUTTING), hatchet))
                return@repeatAction obj.life >= 0

            player.skills.addXp(Skills.WOODCUTTING, obj.tree.treeType.wcXp / 3)
            player.inventory.addItemDrop(KINDLING, 1)
            player.incrementCount("Evil tree kindling chopped")
            obj.chopLife()
            return@repeatAction obj.life >= 0
        }
    }

    onNpcClick(418, 419) { (player, npc) ->
        player.startConversation {
            options {
                if (player.getDailyI("evilTreeChippings") >= 200) {
                    op("Are there any more rewards I can claim with my extra kindling?") {
                        player(HeadE.CONFUSED, "Are there any more rewards I can claim with my extra kindling?")
                        options {
                            opExec("[1000 kindling per hour] Buy more leprechaun banking magic time") {
                                player.sendInputInteger("How much kindling would you like to spend?") { num: Int ->
                                    player.sendOptionDialogue { conf: Options ->
                                        val adjusted = if (num > player.inventory.getNumberOf(KINDLING)) player.inventory.getNumberOf(KINDLING) else num
                                        conf.add("Spend ${Utils.formatNumber(adjusted)} kindling for ${Utils.ticksToTime((adjusted * 6).toDouble())}") {
                                            if (player.inventory.containsItem(KINDLING, adjusted)) {
                                                player.inventory.deleteItem(KINDLING, adjusted)
                                                player.extendEffect(Effect.EVIL_TREE_WOODCUTTING_BUFF, adjusted * 6L)
                                            }
                                        }
                                        conf.add("Nevermind. That's too expensive.")
                                    }
                                }
                            }
                            op("Buy lumberjack outfit pieces (2000 kindling each)") {
                                fun buyLumberjackItem(player: Player, itemId: Int) {
                                    if (!player.inventory.containsItems(KINDLING, 2000)) return player.sendMessage("You don't have enough kindling to buy that.")
                                    player.inventory.deleteItem(KINDLING, 2000)
                                    player.inventory.addItemDrop(itemId, 1)
                                }

                                options {
                                    op("Lumberjack hat") { exec { buyLumberjackItem(player, 10941) } }
                                    op("Lumberjack top") { exec { buyLumberjackItem(player, 10939) } }
                                    op("Lumberjack legs") { exec { buyLumberjackItem(player, 10940) } }
                                    op("Lumberjack boots") { exec { buyLumberjackItem(player, 10933) } }
                                }
                            }
                        }
                    }
                }
                op("Hello, there.") {
                    npc(npc, HeadE.CHEERFUL, if (npc.id == 418) "'Ello, 'ello, legs." else "Ain't you noticed the tree yet? Cut it down!")
                    player(HeadE.CONFUSED, "Who are you?")
                    npc(npc, HeadE.CONFUSED, "Me? Oo's askin'? I ain't nobody but a leprechaun, innit.")
                    player(HeadE.CONFUSED, "You sound a bit different from other leprechauns...")
                    npc(npc, HeadE.ANGRY, "That's bein' on account a' the fact I was raised by imps, innit. Luvverly chaps, imps. Right geezers.")
                    npc(npc, HeadE.CHEERFUL, "Anyways, if ya gimme a 'and, ${player.genderTerm("lad", "lass")}, I can return a favour.")
                    if (npc.id == 418) {
                        npc(npc, HeadE.CHEERFUL, "I come across this wild root on me wanderin', so I reckoned I'd stick about and get you geezers wif long legs to help nurture it.")
                        npc(npc, HeadE.CHEERFUL, "The wee thing's growin' fast, but ya'd speed up the process if ya gave it some encouragement.")
                    } else {
                        npc(npc, HeadE.CHEERFUL, "Anyways, I came across this 'ere root on me wanderin', so I got you geezers wif long legs to 'elp me out.")
                        npc(npc, HeadE.WORRIED, "Now look what I gots on me 'ands. Please, lad!")
                    }
                }
            }
        }
    }

    instantiateNpc(418, 419) { id, tile ->
        object : NPC(id, tile) {
            override fun processNPC() {
                super.processNPC()
                if (tickCounter % 30 == 0L && id == 419)
                    forceTalk(when(Utils.random(5)) {
                        0 -> "It's getting weaker!"
                        1 -> "Don't stop choppin'!"
                        2 -> "It's on its last legs! I mean... roots!"
                        3 -> "You can do it!"
                        else -> "The end is nigh!"
                    })
            }
        }
    }

    onLogin { (player) -> player.vars.setVarBit(1542, if (player.getDailyI("evilTreeChippings") >= 200) 0 else 1) }
}

class EvilTree(val treeType: Type, val location: Location, val centerTile: Tile) : GameObject(11391, ObjectType.SCENERY_INTERACT, 0, centerTile) {
    private var stage = STAGE_NEW
    private var stageProgress = 0
    private var leprechaun: NPC? = null
    private val roots: MutableMap<Direction, Root> = EnumMap(Direction::class.java)
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

    class Root(val tree: EvilTree, val dir: Direction, var life: Int = Utils.random(3, 8)) : GameObject(11427, ObjectType.SCENERY_INTERACT, 0, tree.centerTile.transform(dir.dx*2, dir.dy*2)) {
        fun chopLife() {
            if (life-- <= 0)
                kill()
        }

        override fun process(): Boolean {
            return true
        }

        fun kill() {
            unflagForProcess()
            World.removeObject(this)
            World.sendSpotAnim(this.tile, 315)
            tree.roots.remove(dir)
        }
    }

    override fun process(): Boolean {
        if (World.getServerTicks() % 10 == 0L)
            World.getPlayersInChunkRange(tile.chunkId, 4)
                .filter { !it.tempAttribs.getB("notified$treeType") }
                .forEach {
                    World.sendSpotAnim(it.tile, 314)
                    it.forceTalk("What was that??")
                    it.tempAttribs.setB("notified$treeType", true)
                }
        if (World.getServerTicks() % 2 == 0L)
            World.getPlayersInChunkRange(tile.chunkId, 1)
                .filter { player -> player.withinDistance(centerTile, 2) && roots.values.any { player.withinDistance(it.tile, 1) } }
                .forEach { knockAway(it) }

        if (World.getServerTicks() % 50 == 0L && stage >= 5 && stage < 8)
            spawnRandomRoot()
        return true
    }

    fun spawn() {
        World.spawnObject(this)
        flagForProcess()
        fires.values.forEach { World.spawnObject(it) }
        leprechaun = NPC(418, centerTile.transform(0, 3))
    }

    fun despawn() {
        leprechaun?.finish()
        World.removeObject(this)
        unflagForProcess()
        fires.values.forEach { World.removeObject(it) }
        roots.values.forEach { it.kill() }
    }

    fun spawnRandomRoot() {
        val freeDirections = Direction.entries.toTypedArray().filter { it !in roots.keys }
        if (freeDirections.isNotEmpty())
            spawnRoot(freeDirections[Utils.random(freeDirections.size)])
    }

    fun spawnRoot(dir: Direction) {
        val root = Root(this, dir)
        World.spawnObject(root)
        roots[dir] = root
        WorldTasks.schedule(2) { root.setId(11428) }
        root.flagForProcess()
        World.getPlayersInChunkRange(tile.chunkId, 1)
            .filter { it.withinDistance(centerTile, 2) && it.withinDistance(root.tile, 1) }
            .forEach { knockAway(it) }
    }

    fun inspect(player: Player) {
        fun stageDescText(): String {
            return when(stage) {
                0, 1, 2, 3, 4 -> "It is currently ${Utils.formatDouble((stageProgress.toDouble() / NURTURES_PER_STAGE.toDouble()) * 100.0)}% to the next stage."
                5, 6, 7 -> "It is currently ${Utils.formatDouble((stageProgress.toDouble() / CHOPS_PER_STAGE.toDouble()) * 100.0)}% to the next stage."
                else -> "It's dead."
            }
        }
        player.simpleDialogue("It's an evil ${treeType.name.lowercase()} tree${if(stage >= STAGE_FULLY_GROWN) "" else " sapling"}.<br>${stageDescText()}")
    }

    fun nurture(player: Player) {
        if (player.skills.getLevel(Skills.FARMING) < treeType.farmReq) {
            player.sendMessage("You need a farming level of ${treeType.farmReq} to nurture this tree.")
            return
        }
        player.repeatAction(5) {
            player.faceTile(this.coordFace)
            player.anim(3114)
            player.skills.addXp(Skills.FARMING, treeType.farmXp / 5.0)
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
        if (!player.inventory.containsItem(KINDLING)) {
            player.sendMessage("You need some wood chippings to light on fire.")
            return
        }
        val fire = fires[getDirectionFromTree(player)]
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
        player.inventory.deleteItem(KINDLING, 1)
        player.vars.setVarBit(fire.definitions.varpBit, 1)
    }

    private fun getDirectionFromTree(player: Player): Direction {
        val sub = player.getMiddleTileAsVector().sub(Vec2(centerTile))
        sub.norm()
        return Direction.forDelta(ceilNegs(sub.x).toInt(), ceilNegs(sub.y).toInt())
    }

    private fun knockAway(player: Player) {
        player.stopAll()
        val moveDir = getDirectionFromTree(player)
        player.forceMove(player.transform(moveDir.dx, moveDir.dy), Direction.rotateClockwise(moveDir, 4), 10070, 0, 60)
    }

    fun chop(player: Player) {
        if (player.skills.getLevel(Skills.WOODCUTTING) < treeType.wcReq) {
            player.sendMessage("You need a woodcutting level of ${treeType.wcReq} to chop this tree.")
            return
        }
        player.actionManager.setAction(object: PlayerAction() {
            val hatchet = Hatchet.getBest(player)

            override fun start(player: Player): Boolean {
                player.actionManager.actionDelay = 3
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
                if (!treeType.wcType.rollSuccess(player.auraManager.woodcuttingMul, player.skills.getLevel(Skills.WOODCUTTING), hatchet)) return 3

                player.skills.addXp(Skills.WOODCUTTING, treeType.wcXp)
                player.inventory.addItemDrop(KINDLING, 1)
                player.incrementCount("Evil tree kindling chopped")
                this@EvilTree.incProgress()
                return 3
            }

            override fun stop(player: Player) {
                player.anim(-1)
            }
        })
    }

    fun takeRewards(player: Player) {
        if (player.getDailyI("evilTreeChippings") >= 200) {
            player.sendMessage("You have already looted all you can from evil trees today.")
            return
        }
        if (player.inventory.containsItem(KINDLING, 1)) {
            val toHandIn = minOf(player.inventory.getNumberOf(KINDLING), 200 - player.getDailyI("evilTreeChippings"))
            if (toHandIn <= 0) return
            player.inventory.deleteItem(KINDLING, toHandIn)
            val coins = (when(treeType) {
                Type.OAK -> 652.0
                Type.WILLOW -> 2254.0
                Type.MAPLE -> 2560.0
                Type.YEW -> 4668.0
                Type.MAGIC -> 9474.0
                Type.ELDER -> 17388.0
                else -> 370.0
            } * (toHandIn.toDouble() / 200.0)).toInt()
            val logs = when(treeType) {
                Type.OAK -> Item(1522, (48.0 * (toHandIn.toDouble() / 200.0)).toInt())
                Type.WILLOW -> Item(1520, (452.0 * (toHandIn.toDouble() / 200.0)).toInt())
                Type.MAPLE -> Item(1518, (262.0 * (toHandIn.toDouble() / 200.0)).toInt())
                Type.YEW -> Item(1516, (34.0 * (toHandIn.toDouble() / 200.0)).toInt())
                Type.MAGIC -> Item(1514, (20.0 * (toHandIn.toDouble() / 200.0)).toInt())
                Type.ELDER -> when(Utils.random(4)) {
                    0 -> Item(1514, 36)
                    1 -> Item(1516, 118)
                    2 -> Item(8836, 204)
                    else -> Item(6334, 750)
                }
                else -> Item(1512, (24.0 * (toHandIn.toDouble() / 200.0)).toInt())
            }
            player.extendEffect(Effect.EVIL_TREE_WOODCUTTING_BUFF, (Ticks.fromMinutes(30).toDouble() * (toHandIn.toDouble() / 200.0)).toLong())
            if (coins > 0)
                player.inventory.addCoins(coins)
            if (logs.amount > 0)
                player.inventory.addItemDrop(logs)
            player.setDailyI("evilTreeChippings", player.getDailyI("evilTreeChippings", 0) + toHandIn)
            if (player.getDailyI("evilTreeChippings") >= 200)
                player.vars.setVarBit(1542, 1)
        }
    }

    private fun incProgress() {
        val threshold = if (stage < STAGE_FULLY_GROWN) NURTURES_PER_STAGE else CHOPS_PER_STAGE
        if (++stageProgress >= threshold) {
            incStage()
            stageProgress = 0
        }
    }

    private fun incStage() {
        if (stage >= STAGE_DEAD)
            return
        if (++stage == STAGE_NURTURED_LARGER) {
            World.removeObject(this)
            id += 1
            tile = tile.transform(-1, -1)
            World.spawnObject(this)
            World.getPlayersInChunkRange(tile.chunkId, 1)
                .filter { it.withinDistance(centerTile, 1) }
                .forEach { knockAway(it) }
            return
        }
        if (stage == STAGE_DEAD) {
            leprechaun?.transformIntoNPC(418)
            val varbits = fires.values.map { it.definitions.varpBit }
            fires.values.forEach { World.removeObject(it) }
            roots.values.forEach { it.kill() }
            World.getPlayersInChunkRange(tile.chunkId, 1)
                .forEach { player ->
                    varbits.forEach { varbit ->
                        player.vars.setVarBit(varbit, 0)
                    }
                }
            WorldTasks.schedule(10) { setId(treeType.deadObj) }
        }
        if (stage == STAGE_FULLY_GROWN) {
            leprechaun?.transformIntoNPC(419)
            Direction.entries.forEach { spawnRoot(it) }
        }
        if (stage <= STAGE_LAST_NURTURE)
            setId(id + 1)
        else
            setId(when(stage) {
                STAGE_FULLY_GROWN -> treeType.healthyObj
                STAGE_CHOPPED_UP_1 -> treeType.deg1Obj
                STAGE_CHOPPED_UP_2 -> treeType.deg2Obj
                else -> treeType.dyingObj
            })
    }
}


fun handleEvilTreeOps(player: Player, ops: Options, evilTreeHead: Int) {
    ops.add("Are there any Evil Trees you can help me find?") {
        player.startConversation {
            player(HeadE.CONFUSED, "Are there any Evil Trees you can help me find?")
            if (currentTree == null) {
                npc(evilTreeHead, HeadE.CALM_TALK, "The taint of the evil tree is not currently on the land. There won't be another for a little while.")
                return@startConversation
            }
            npc(evilTreeHead, HeadE.CALM_TALK, "An evil tree is currently growing near " + currentTree!!.location.desc + "! The taint of the next I sense near " + (if (!locIterator.hasNext()) LOCATIONS.first() else locIterator.peek()).desc + ".")
            npc(evilTreeHead, HeadE.CONFUSED, "Would you like me to teleport you directly there?")
            options {
                opExec("Yes, please.") { SpiritTree.sendTeleport(player, World.findClosestAdjacentFreeTile(currentTree!!.location.tile, 5)) }
                op("Nevermind.")
            }
        }
    }
}
