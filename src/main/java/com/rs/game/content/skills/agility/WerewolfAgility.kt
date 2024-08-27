package com.rs.game.content.skills.agility

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.game.World
import com.rs.game.World.addGroundItem
import com.rs.game.World.sendProjectile
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*
import kotlin.math.abs

val WEREWOLF_AGILITY_CHECK: Array<IntArray> = arrayOf(intArrayOf(3521, 3584, 9859, 9918))

private const val LAST_VISITED_STONE_TILE_KEY = "lastVisitedStoneTile"

val steppingStones = listOf(
    Tile.of(3538, 9875, 0),
    Tile.of(3538, 9877, 0),
    Tile.of(3540, 9877, 0),
    Tile.of(3540, 9879, 0),
    Tile.of(3540, 9881, 0))

const val STICK = 4179

@ServerStartupEvent
fun mapWerewolfAgility() {
    instantiateNpc("Agility Trainer", "Agility Boss") { id, tile -> WolfAgilityTrainer(id, tile) }

    onNpcClick(1661) { (player, npc) ->
        player.startConversation {
            player(CONFUSED, "How do I use the agility course?")
            npc(npc, CALM_TALK, "I'll throw you a stick, which you need to fetch as quickly as possible, from the area beyond the pipes.")
            npc(npc, CALM_TALK, "Be wary of the deathslide - you must hang by your teeth, and if your strength is not up to the job you will fall into a pit of spikes. Also, I would advise not carrying too much extra weight.")
            npc(npc, CALM_TALK, "Bring the stick back to the werewolf waiting at the end of the death slide to get your agility bonus.")
            npc(npc, CALM_TALK, "I will throw your stick as soon as you jump onto the first stone.")
        }
    }

    onNpcClick(1664) { (player, npc) ->
        if (player.inventory.containsItem(STICK)) {
            val stickCount: Int = player.inventory.getAmountOf(STICK)
            player.inventory.removeItems(Item(STICK, stickCount))
            player.skills.addXp(Skills.AGILITY, (380 * stickCount).toDouble())
            player.sendMessage("You give the stick to the werewolf.")
            player.incrementCount("Werewolf agility sticks fetched")
        } else {
            player.startConversation {
                npc(npc, CALM_TALK, "You haven't got a stick to give me!")
            }
        }
    }

    onPickupItem(STICK) { e ->
        if (e.item.id == STICK && e.player.containsOneItem(STICK) && e.player.inventory.hasFreeSlots()) {
            e.cancelPickup()
            e.player.sendMessage("You can only carry 1 stick at a time.")
        }
    }

    // Stepping stone
    onObjectClick(35996, checkDistance = false) { (player, obj) ->
        if (!Agility.hasLevel(player, 60) || player.tile.y > 9880) return@onObjectClick
        if (obj.tile.matches(player.tile)) return@onObjectClick
        if (!obj.tile.withinDistance(player.tile, 100)) return@onObjectClick

        val lastVisitedStoneTile = player.tempAttribs.getO<Tile>(LAST_VISITED_STONE_TILE_KEY)
        val tileIndex = steppingStones.indexOf(obj.tile)

        if (tileIndex != -1) {
            val dx = abs(player.tile.x - obj.tile.x)
            val dy = abs(player.tile.y - obj.tile.y)

            if (obj.tile == Tile.of(3538, 9875, 0)) {
                if (player.tile.y < 9875) {
                    player.walkToAndExecute(Tile.of(3538, 9873, 0)) {
                        throwStick(player)
                        player.schedule {
                            wait(1)
                            steppingStone(player, obj)
                            player.tempAttribs.removeO<Tile>(LAST_VISITED_STONE_TILE_KEY)
                        }
                    }
                }
            } else if ((dx == 2 && dy == 0) || (dx == 0 && dy == 2)) {
                if (lastVisitedStoneTile == null || steppingStones.indexOf(lastVisitedStoneTile) < tileIndex) {
                    steppingStone(player, obj)
                    player.tempAttribs.setO<Tile>(LAST_VISITED_STONE_TILE_KEY, obj.tile)

                    if (tileIndex == steppingStones.size - 1) {
                        player.tempAttribs.removeO<Tile>(LAST_VISITED_STONE_TILE_KEY)
                    }
                }
            }
        }
    }

    // Hurdle
    onObjectClick(5133, 5134, 5135) { (player, obj) ->
        if (player.tile.y > obj.y) return@onObjectClick
        if (!Agility.hasLevel(player, 60)) return@onObjectClick
        player.skills.addXp(Skills.AGILITY, 20.0)
        player.forceMove(Tile.of(obj.x + (if (obj.id == 5134) 0 else 1), obj.y + 1, 0), 1603, 0, 45)
    }

    //Obstacle pipe
    onObjectClick(5152) { (player, obj) ->
        if (player.tile.y > obj.y) return@onObjectClick
        if (!Agility.hasLevel(player, 60)) return@onObjectClick
        player.lock(1)
        val toTile = Tile.of(obj.x, obj.y + 4, obj.plane)
        player.forceMove(toTile, 10580, 10, 60) { player.skills.addXp(Skills.AGILITY, 15.0) }
    }

    // Skull slope
    onObjectClick(5136) { (player, obj) ->
        if (player.x < obj.x) return@onObjectClick
        if (!Agility.hasLevel(player, 60)) return@onObjectClick
        player.forceMove(Tile.of(obj.x - 2, obj.y, 0), 2049, 0, 105) {
            player.anim(-1)
            player.skills.addXp(Skills.AGILITY, 25.0)
        }
    }

    // Zip line
    onObjectClick(5139, 5140, 5141) { (player) ->
        if (!Agility.hasLevel(player, 60)) return@onObjectClick
        val endTile: Tile = Tile.of(3528, 9873, 0)
        val midwayTile1: Tile = Tile.of(3528, 9890, 0)
        val midwayTile2: Tile = Tile.of(3528, 9882, 0)
        val failureLocation1: Tile = Tile.of(3526, 9887, 0)
        val failureLocation2: Tile = Tile.of(3526, 9879, 0)
        val playerWeight: Double = player.weight
        val agilityLevel: Int = player.skills.getLevel(Skills.AGILITY)
        val strengthLevel: Int = player.skills.getLevel(Skills.STRENGTH)

        val baseFailureChance = when {
            agilityLevel >= 80 && strengthLevel >= 80 && playerWeight < 2 -> 0.0
            playerWeight < -5 -> -30.0
            playerWeight < 0 -> -15.0
            playerWeight <= 0 -> 5.0
            playerWeight <= 2 -> 15.0
            playerWeight <= 5 -> 20.0
            playerWeight <= 10 -> 25.0
            playerWeight <= 20 -> 30.0
            playerWeight <= 30 -> 35.0
            playerWeight <= 40 -> 40.0
            playerWeight <= 50 -> 45.0
            playerWeight <= 60 -> 50.0
            else -> 80.0
        }

        val adjustedFailureChance = when {
            agilityLevel >= 80 && strengthLevel >= 80 && playerWeight < 2 -> 0.0
            agilityLevel >= 80 && strengthLevel >= 80 -> baseFailureChance * 0.5
            agilityLevel >= 70 && strengthLevel >= 70 -> baseFailureChance * 0.75
            agilityLevel >= 60 && strengthLevel >= 60 -> baseFailureChance * 0.9
            else -> baseFailureChance
        }

        val rate1 = if (agilityLevel >= 80 && strengthLevel >= 80 && playerWeight <= 2) 256 else (135 - adjustedFailureChance).coerceAtLeast(0.0).coerceAtMost(256.0).toInt()
        val rate99 = if (agilityLevel >= 80 && strengthLevel >= 80 && playerWeight <= 2) 256 else 250

        player.walkToAndExecute(Tile.of(3528, 9910, 0)) {
            player.lock()
            val agilityTrainer: List<NPC> = player.queryNearbyNPCsByTileRange(20) { npc: NPC -> npc.id == 1663 }
            if (agilityTrainer.isNotEmpty()) {
                when ((0..1).random()) {
                    0 -> agilityTrainer.first().forceTalk("Now a true test of teeth...")
                    1 -> agilityTrainer.first().forceTalk("Don't let the spikes or the blood put you off...")
                }
            }
            player.schedule {
                player.faceDir(Direction.SOUTH)
                wait(2)
                player.anim(1601)
                player.sendMessage("You bravely cling on to the death slide by your teeth ...")
                wait(2)
                player.forceTalk("WAAAAAARRRGGGHHH!!!!!!")
                player.incrementCount("Werewolf laps")

                if (Agility.rollSuccess(player, rate1, rate99)) {
                    player.forceMove(endTile, 1602, 0, 240) {
                        player.anim(-1)
                        player.skills.addXp(Skills.AGILITY, 200.0)
                        player.incrementCount("Werewolf agility laps completed")
                        player.sendMessage("... and land safely on your feet.")
                        val agilityTrainer2: List<NPC> = player.queryNearbyNPCsByTileRange(20) { npc: NPC -> npc.id == 1664 }
                        if (agilityTrainer2.isNotEmpty()) {
                            when ((0..1).random()) {
                                0 -> agilityTrainer2.first().forceTalk("Remember - no stick, no agility bonus!")
                                1 -> agilityTrainer2.first().forceTalk("Don't forget to give me the stick when you're done!")
                            }
                        }
                    }
                } else {
                    val randomFailure = Math.random()

                    val (failureTile, midwayTile, damage, xpReward, midwayDuration) = if (randomFailure < 0.5) {
                        FailureParams(failureLocation1, midwayTile1, (150..300).random(), 120.0, 180)
                    } else {
                        FailureParams(failureLocation2, midwayTile2, (120..200).random(), 140.0, 200)
                    }

                    player.forceMove(midwayTile, 1602, 0, midwayDuration) {
                        player.anim(-1)
                        player.forceMove(failureTile, 766, 0, 20) {
                            player.anim(-1)
                            player.sendMessage("... only to fall from a great height!")
                            player.applyHit(Hit.flat(player, damage))
                            player.skills.addXp(Skills.AGILITY, xpReward)
                        }
                    }
                }
                val groundItems = World.getAllGroundItemsInChunkRange(player.chunkId, 30)

                for (item in groundItems) {
                    if (item.id == STICK && item.creatorUsername != null && item.creatorUsername == player.username) {
                        World.removeGroundItem(item)
                    }
                }
            }
        }
    }

    // Ladder Down
    onObjectClick(5132) { (player) ->
        player.useLadder(Tile.of(3549, 9865, 0))
        player.controllerManager.startController(WerewolfAgilityController())
    }

    // Ladder Up
    onObjectClick(5130) { (player) ->
        player.useLadder(Tile.of(3543, 3463, 0))
        player.controllerManager.forceStop()
    }

    onLogin { (player) ->
        if (isInWerewolfAgility(player.tile)) player.controllerManager.startController(WerewolfAgilityController())
    }

}

private fun steppingStone(player: Player, obj: GameObject) {
    player.lock()
    player.skills.addXp(Skills.AGILITY, 10.0)
    player.forceMove(obj.tile, 741, 0, 25) { player.unlock() }
}

private fun throwStick(player: Player) {
    val agilityBoss: List<NPC> = player.queryNearbyNPCsByTileRange(8) { npc: NPC -> npc.id == 1661 }
    val groundItemsInRange = World.getAllGroundItemsInChunkRange(player.chunkId, 10)
    val playerHasStickInInventory = player.containsOneItem(STICK)
    var throwStickNeeded = true
    for (item in groundItemsInRange) {
        if (item.id == STICK && item.creatorUsername == player.username) {
            throwStickNeeded = false
            break
        }
    }
    if (throwStickNeeded) {
        if (!playerHasStickInInventory) {
            throwStickForPlayer(player, agilityBoss)
        }
    }
}

private fun throwStickForPlayer(player: Player, agilityBoss: List<NPC>) {
    if (agilityBoss.isNotEmpty()) {
        agilityBoss.first().faceDir(Direction.NORTH)
        agilityBoss.first().forceTalk("FETCH!!!!!")
        WorldTasks.schedule(2) {
            agilityBoss.first().anim(6547)
            sendProjectile(agilityBoss.first(), Tile.of(3540, 9911, 0), 1158, Pair(35, 0), 20, 10, 20, 0) {
                addGroundItem(Item(4179), Tile.of(3540, 9911, 0), player)
            }
        }
    }
}


private fun isInWerewolfAgility(tile: Tile): Boolean {
    for (range in WEREWOLF_AGILITY_CHECK) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
    return false
}

data class FailureParams<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

class WolfAgilityTrainer(id: Int, tile: Tile) : NPC(id, tile) {

    private val lastSpeakTick = mutableMapOf<Int, Long>()

    private val phrases = listOf(
        "Let the bloodlust take you!!",
        "You're the slowest wolf I've ever had the misfortune to witness",
        "I never really wanted to be an agility trainer",
        "Claws first - think later.",
        "When you're done there's a human with your name on it!!",
        "Get on with it - you need your whiskers plucking!!!!",
        "Remember - a slow wolf is a hungry wolf!!",
        "It'll be worth it when you hunt!!",
        "Let's see those powerful back legs at work!!",
        "Imagine the smell of blood in your nostrils!!!"
    )

    init {
        if (id == 1661)
            setRandomWalk(false)
    }

    override fun processNPC() {
        super.processNPC()
        lastSpeakTick.putIfAbsent(id, 0)
        val elapsedTime = tickCounter - lastSpeakTick[id]!!
        if (elapsedTime >= 20) {
            when (val option = Utils.random(20)) {
                in 0..9 -> {
                    val randomPhrase = phrases[option]
                    forceTalk(randomPhrase)
                }
                in 10..20 -> {
                }
            }
            lastSpeakTick[id] = tickCounter
        }
    }
}

class WerewolfAgilityController : Controller() {
    override fun start() {
        if (player.inventory.containsOneItem(STICK)) {
            player.inventory.deleteItem(STICK, player.inventory.getAmountOf(STICK))
        }
    }

    override fun login(): Boolean {
        player.tele(Tile.of(3549, 9865, 0))
        return true
    }

    override fun onTeleported(type: TeleType) {
        removeController()
    }

    override fun logout(): Boolean {
        player.tele(Tile.of(3549, 9865, 0))
        player.tile = Tile.of(3549, 9865, 0)
        if (player.inventory.containsOneItem(STICK)) {
            player.inventory.deleteItem(STICK, player.inventory.getAmountOf(STICK))
        }
        val groundItems = World.getAllGroundItemsInChunkRange(player.chunkId, 30)

        for (item in groundItems) {
            if (item.id == STICK && item.creatorUsername != null && item.creatorUsername == player.username) {
                World.removeGroundItem(item)
            }
        }
        return false
    }

    override fun onRemove() {
        super.onRemove()
        if (player.inventory.containsOneItem(STICK)) {
            player.inventory.deleteItem(STICK, player.inventory.getAmountOf(STICK))
        }
        val groundItems = World.getAllGroundItemsInChunkRange(player.chunkId, 30)

        for (item in groundItems) {
            if (item.id == STICK && item.creatorUsername != null && item.creatorUsername == player.username) {
                World.removeGroundItem(item)
            }
        }
    }

    override fun sendDeath(): Boolean {
        player.controllerManager.forceStop()
        return super.sendDeath()
    }
}
