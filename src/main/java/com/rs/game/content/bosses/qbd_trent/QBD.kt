package com.rs.game.content.bosses.qbd_trent

import com.rs.cache.loaders.Bonus
import com.rs.cache.loaders.ObjectType
import com.rs.engine.pathfinder.Direction
import com.rs.game.World
import com.rs.game.content.combat.getAntifireLevel
import com.rs.game.content.items.LootInterface
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.NPC.yellDrop
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.npc.combat.CombatScript.delayHit
import com.rs.game.model.entity.npc.combat.CombatScript.getMeleeHit
import com.rs.game.model.entity.npc.combat.CombatScript.getRangeHit
import com.rs.game.model.entity.npc.combat.CombatScriptsHandler
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle
import com.rs.game.model.entity.player.Player
import com.rs.game.model.item.ItemsContainer
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils.add
import com.rs.lib.util.Utils.addArticle
import com.rs.lib.util.Utils.clampI
import com.rs.lib.util.Utils.random
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.DropSets
import com.rs.utils.WorldUtil.gsonTreeMapToItemContainer
import com.rs.utils.drop.DropTable
import java.util.stream.IntStream.range
import kotlin.math.abs
import kotlin.streams.toList

private const val ANIM_MELEE_LEFT = 16743
private const val ANIM_MELEE_CENTER = 16717
private const val ANIM_MELEE_RIGHT = 16744
private const val ANIM_RANGE = 16720
private const val ANIM_FIRE_BREATH = 16721
private const val ANIM_SPAWN_SOULS = 16738
private const val ANIM_SIPHON_SOULS_ANIM = 16739
private const val ANIM_SPAWN_GROTWORMS = 16722
private const val ANIM_SPAWN_GROTWORMS_STOP = 16723
private const val ANIM_EXTREMELY_HOT_FLAMES = 16745
private const val ANIM_FIRE_WALLS = 16746
private const val ANIM_FIRE_WALL_CONTINUE = 16747
private const val ANIM_FIRE_WALL_STOP = 16748
private const val ANIM_WAKE_UP = 16714

@ServerStartupEvent
fun mapQbdFeatures() {
    instantiateNpc(15454, 15506, 15507, 15508, 15509) { npcId, tile -> QBD(npcId, tile) }

    npcCombat(15454, 15506, 15507, 15508, 15509) { npc, target ->
        val qbd = npc as? QBD
        val player = target as? Player
        if (qbd == null || player == null) return@npcCombat CombatScriptsHandler.getDefaultCombat().apply(npc, target)
        if (qbd.artefact != null) return@npcCombat 0
        return@npcCombat qbd.getNextAttack().func.invoke(qbd, player)
    }

    onObjectClick(70777, 70780, 70783, 70786) { (player, obj) ->
        if (obj !is Artefact) return@onObjectClick
        player.anim(832)
        obj.qbd.phase()
    }

    onObjectClick(70816) { (player) ->
        val items = gsonTreeMapToItemContainer(player["qbdLootChest"])
        if (items == null) {
            player.sendMessage("The coffer is empty.")
            return@onObjectClick
        }
        LootInterface.open("Dragonkin Coffer", player, items)
    }
}

class Artefact(id: Int, tile: Tile, val qbd: QBD) : GameObject(id, ObjectType.SCENERY_INTERACT, 0, tile)

class QBD(npcId: Int, tile: Tile) : NPC(npcId, tile, Direction.SOUTH, true) {
    var woke = false
    var lastSpec = 0
    var phase = 0
    var artefact: Artefact? = null
    var lastHitpoints = 0
    val souls = mutableListOf<TorturedSoul>()

    init {
        setRandomWalk(false)
        isForceMultiArea = true
        isForceMultiAttacked = true
        isCantFollowUnderCombat = true
        isIgnoreDocile = true
        isCantInteract = true
        capDamage = 1000
    }

    fun wake() {
        woke = true
        schedule {
            anim(ANIM_WAKE_UP)
            wait(28)
            isCantInteract = false
            transformIntoNPC(15454)
            capDamage = 1000
            hitpoints = maxHitpoints
        }
    }

    override fun getMaxHitpoints() = 7500

    override fun setHitpoints(hitpoints: Int) {
        super.setHitpoints(hitpoints)
        if (lastHitpoints != hitpoints) {
            queryNearbyPlayersByTileRange(20) { !it.isDead }.forEach { it.packets.sendVarc(1923, getMaxHitpoints() - hitpoints) }
            lastHitpoints = hitpoints;
        }
    }

    override fun sendDeath(source: Entity?) {
        unguardArtefact()
    }

    val platformObjTile
        get() = middleTile.transform(-9, -17, -1)
    val cofferTile
        get() = middleTile.transform(-1, -8, 0)
    val artefact1Tile
        get() = middleTile.transform(0, -7, 0)
    val artefact2Tile
        get() = middleTile.transform(-9, -17, 0)
    val artefact3Tile
        get() = middleTile.transform(9, -17, 0)
    val artefact4Tile
        get() = middleTile.transform(0, -17, 0)

    fun unguardArtefact() {
        transformIntoNPC(15454)
        World.spawnObject(GameObject(70822, ObjectType.SCENERY_INTERACT, 0, middleTile.transform(-12, -3, -1)))
        World.spawnObject(GameObject(70818, ObjectType.SCENERY_INTERACT, 0, middleTile.transform(6, -3, -1)))
        capDamage = 1000
        isCantInteract = true
        val newArtefact = when(phase) {
            0 -> {
                possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
                    .forEach {
                        it.sendMessage("The Queen Black Dragon's concentration wavers; the first artefact is now unguarded.")
                        it.packets.sendVarc(1924, 1)
                    }
                Artefact(70777, artefact1Tile, this)
            }
            1 -> {
                possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
                    .forEach {
                        it.sendMessage("The Queen Black Dragon's concentration wavers; the second artefact is now unguarded.")
                        it.packets.sendVarc(1924, 3)
                    }
                World.spawnObject(GameObject(70844, ObjectType.SCENERY_INTERACT, 0, platformObjTile))
                Artefact(70780, artefact2Tile, this)
            }
            2 -> {
                possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
                    .forEach {
                        it.sendMessage("The Queen Black Dragon's concentration wavers; the third artefact is now unguarded.")
                        it.packets.sendVarc(1924, 5)
                    }
                World.spawnObject(GameObject(70846, ObjectType.SCENERY_INTERACT, 0, platformObjTile))
                Artefact(70783, artefact3Tile, this)
            }
            else -> {
                possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
                    .forEach {
                        it.sendMessage("The Queen Black Dragon's concentration wavers; the last artefact is now unguarded.")
                        it.packets.sendVarc(1924, 7)
                    }
                World.spawnObject(GameObject(70848, ObjectType.SCENERY_INTERACT, 0, platformObjTile))
                Artefact(70786, artefact4Tile, this)
            }
        }
        artefact = newArtefact
        World.spawnObject(newArtefact)
        clearPendingTasks()
        schedule {
            anim(ANIM_SPAWN_GROTWORMS)
            wait(7)
            while(artefact != null) {
                anim(ANIM_SPAWN_GROTWORMS)
                val aliveWorms = queryNearbyNPCsByTileRange(20) { it.id == 15464 && !it.isDead }
                if (aliveWorms.size >= 11) {
                    queryNearbyPlayersByTileRange(20) { !it.isDead }.forEach {
                        it.sendMessage("The power of the unprotected artefact leaks out and damages you!")
                        it.applyHit(Hit.flat(this@QBD, 200))
                    }
                    wait(4)
                    continue
                }
                val destination = Tile.of(middleTile.transform(0, -7, 0), 5)
                wait(World.sendProjectileHalfSqAbsoluteSpeed(this@QBD.middleTile.transform(0, 3, 0), destination, 3141, endXyHalves = 1 to 1, heights = 255 to 0, delay = 0, speed = 90, angle = 5).taskDelay)
//                World.sendSpotAnim(destination, 3142)
//                wait(2)
                val worm = NPC(15464, destination, true)
                worm.anim(16800)
                worm.lock(2)
                wait(2)
                worm.isForceMultiArea = true
                worm.isForceMultiAttacked = true
                worm.isForceAgressive = true
            }
        }
    }

    fun phase() {
        artefact?.let { World.spawnObject(GameObject(it.id+1, ObjectType.SCENERY_INTERACT, 0, Tile.of(it.tile))) }
        World.getObject(platformObjTile)?.let { World.spawnObject(GameObject(it.id+1, ObjectType.SCENERY_INTERACT, 0, platformObjTile)) }
        artefact = null
        isCantInteract = false
        hitpoints = maxHitpoints
        clearPendingTasks()
        if (phase >= 3) {
            transformIntoNPC(15509)
            isCantInteract = false
            anim(16742)
            World.spawnObject(GameObject(-1, ObjectType.SCENERY_INTERACT, 0, artefact1Tile))
            World.spawnObject(GameObject(70816, ObjectType.SCENERY_INTERACT, 0, cofferTile))
            souls.forEach { it.finish() }
            souls.clear()
            queryNearbyNPCsByTileRange(20) { it.id == 15464 }.forEach { it.finish() }
            repeat(100) {
                recievedDamageEntities.filterIsInstance<Player>().forEach { generateAndAddDropToChest(it) }
            }
            schedule {
                wait(100)
                resetAll()
            }
            return
        }
        anim(ANIM_SPAWN_GROTWORMS_STOP)
        combat.addCombatDelay(10)
        phase++
    }

    fun resetAll() {
        phase = 0
        World.getObject(cofferTile)?.let { World.removeObject(it) }
        World.getObject(platformObjTile)?.let { World.removeObject(it) }
        World.spawnObject(GameObject(70776, ObjectType.SCENERY_INTERACT, 0, artefact1Tile))
        World.spawnObject(GameObject(70779, ObjectType.SCENERY_INTERACT, 0, artefact2Tile))
        World.spawnObject(GameObject(70782, ObjectType.SCENERY_INTERACT, 0, artefact3Tile))
        World.spawnObject(GameObject(70785, ObjectType.SCENERY_INTERACT, 0, artefact4Tile))
        artefact?.let { World.removeObject(it) }
        clearPendingTasks()
        woke = false
        transformIntoNPC(15509)
        reset()
        souls.forEach { it.finish() }
        souls.clear()
        queryNearbyNPCsByTileRange(20) { it.id == 15464 }.forEach { it.finish() }
    }

    override fun processNPC() {
        super.processNPC()
        faceDir(Direction.SOUTH)
        if (id != 15509 && queryNearbyPlayersByTileRange(20) { true }.isEmpty() && woke)
            resetAll()
        if (!queryNearbyPlayersByTileRange(20) { !it.isDead }.isEmpty() && !woke)
            wake()
    }

    fun spawnSoul(target: Player) {
        souls.add(TorturedSoul(this, target, Tile.of(tile)))
    }
}

fun generateAndAddDropToChest(player: Player) {
    val currentLoot = gsonTreeMapToItemContainer(player["qbdLootChest"]) ?: ItemsContainer<Item?>(28, true)
    val drops = genDrop(player)
    drops.forEach { item ->
        if (yellDrop(item.id))
            World.broadcastLoot("${player.displayName} has just received ${addArticle(item.name)} from the Queen Black Dragon!")
        player.incrementCount(item.name + " drops earned", item.getAmount())
        if (!currentLoot.add(item))
            player.inventory.addItemDrop(item)
    }
    player["qbdLootChest"] = currentLoot
}

fun genDrop(killer: Player): MutableList<Item> {
    val drops = mutableListOf<Item>()
    add(drops, DropTable.calculateDrops(killer, DropSets.getDropSet("QBDMain")))
    add(drops, DropTable.calculateDrops(killer, DropSets.getDropSet("QBDSupply")))
    if (random(128) == 0)
        repeat(2) { add(drops, DropTable.calculateDrops(killer, DropSets.getDropSet("rdt_standard"))) }
    return drops
}

private val possibleOffsets = listOf(1 to 1, -1 to 1, -1 to -1, 1 to -1, 1 to 0, -1 to 0, 0 to -1, 0 to 1)

class TorturedSoul(val qbd: QBD, val player: Player, tile: Tile) : NPC(15510, tile, true) {
    private val messages = setOf("NO MORE! RELEASE ME, MY QUEEN! I BEG YOU!", "We lost our free will long ago...", "How long has it been since I was taken...", "The cycle is never ending, mortal...")

    init {
        hitpoints = 500
        combatDefinitions.hitpoints = 500
        isForceMultiArea = true
        isForceMultiAttacked = true
        setRandomWalk(false)
        combat.target = player
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        qbd.souls.remove(this)
    }

    fun spec(target: Player) {
        val specTile = possibleOffsets
            .map { offset -> target.tile.transform(offset.first, offset.second, 0) }
            .firstOrNull { newTile ->
                qbd.souls.none { it.tile.matches(newTile) || it.moveTile?.matches(newTile) == true }
            } ?: target.tile
        sync(16861, 3147)
        tele(specTile)
        combat.reset()
        combat.addCombatDelay(10)
        faceTile(player.tile)
        schedule {
            wait(1)
            forceTalk(messages.random())
            sync(16864, 3145)
            combat.target = player
            var currTile = getStartTile()
            while(true) {
                World.sendSpotAnim(currTile, 3146)
                val hittableTarget = queryHittableTargets(currTile)
                if (hittableTarget != null) {
                    hittableTarget.applyHit(Hit.flat(qbd, random(200, 260)))
                    break
                }
                currTile = when {
                    currTile.x > player.x && currTile.y > player.y -> currTile.transform(-1, -1, 0)
                    currTile.x > player.x && currTile.y < player.y -> currTile.transform(-1, 1, 0)
                    currTile.x < player.x && currTile.y > player.y -> currTile.transform(1, -1, 0)
                    currTile.x < player.x && currTile.y < player.y -> currTile.transform(1, 1, 0)
                    currTile.x > player.x -> currTile.transform(-1, 0, 0)
                    currTile.x < player.x -> currTile.transform(1, 0, 0)
                    currTile.y > player.y -> currTile.transform(0, -1, 0)
                    else -> currTile.transform(0, 1, 0)
                }
                wait(1)
            }
        }
    }

    private fun queryHittableTargets(tile: Tile): Entity? {
        return queryNearbyNPCsByTileRange(20) { !it.isDead && it.tile.matches(tile) }.firstOrNull()
            ?: queryNearbyPlayersByTileRange(20) { !it.isDead && it.tile.matches(tile) }.firstOrNull()
    }

    private fun getStartTile(): Tile {
        val (offsetX, offsetY) = when {
            abs(x - player.x) > abs(y - player.y) -> if (x < player.x) -1 to 0 else 1 to 0
            else -> if (y < player.y) 0 to -1 else 0 to 1
        }

        val (finalOffsetX, finalOffsetY) = if (player.transform(offsetX, offsetY, 0).matches(tile))
            -offsetX to -offsetY
        else
            offsetX to offsetY

        return Tile.of(player.x + finalOffsetX, player.y + finalOffsetY, player.plane)
    }
}

private fun QBD.getNextAttack(): QBDAttack {
    if (lastSpec++ >= 2 && random(3) == 0) {
        lastSpec = 0
        val possibleSpecs = mutableListOf(QBDAttack.FIRE_WALL)
        if (phase > 0) {
            if (!souls.isEmpty())
                possibleSpecs.add(QBDAttack.SIPHON_SOULS)
            else
                possibleSpecs.add(QBDAttack.SPAWN_SOULS)
            possibleSpecs.add(QBDAttack.CHANGE_ARMOR)
        }
        if (phase >= 3)
            possibleSpecs.add(QBDAttack.EXTREMELY_HOT_FLAMES)
        return possibleSpecs.random()
    }
    return listOf(
        QBDAttack.BASIC,
        QBDAttack.BASIC,
        QBDAttack.BASIC,
        QBDAttack.FIRE_BREATH,
    ).random()
}

private enum class QBDAttack(val func: (QBD, Player) -> Int) {
    BASIC({ qbd, player ->
        if ((qbd.y - player.y) <= 4 && random(2) == 0) {
            val xDiff = player.x - qbd.middleTile.x
            qbd.anim(when {
                xDiff <= -3 -> ANIM_MELEE_LEFT
                xDiff >= 3 -> ANIM_MELEE_RIGHT
                else -> ANIM_MELEE_CENTER
            })
            delayHit(qbd, 1, player, getMeleeHit(qbd, CombatScript.getMaxHit(qbd, 475, Bonus.SLASH_ATT, AttackStyle.MELEE, player)));
        } else {
            qbd.anim(ANIM_RANGE)
            delayHit(qbd, 1, player, getRangeHit(qbd, CombatScript.getMaxHit(qbd, 525, AttackStyle.RANGE, player)));
        }
        6
    }),
    FIRE_BREATH({ qbd, player ->
        qbd.sync(ANIM_FIRE_BREATH, 3143)
        qbd.schedule {
            wait(1)
            player.applyHit(Hit.flat(qbd, when(getAntifireLevel(player, false)) {
                2 -> random(190, 210)
                1 -> random(250, 300)
                else -> random(750, 950)
            }))
        }
        6
    }),
    SPAWN_SOULS({ qbd, player ->
        val count = (qbd.phase+1) - qbd.souls.size
        player.sendMessage(if (count < 2) "<col=9900CC>The Queen Black Dragon summons one of her captive, tortured souls.</col>" else "<col=9900CC>The Queen Black Dragon summons several of her captive, tortured souls.</col>")
        qbd.schedule {
            qbd.anim(ANIM_SPAWN_SOULS)
            repeat(count) {
                qbd.spawnSoul(player)
            }
            qbd.souls.forEach { it.spec(player) }
        }
        10
    }),
    SIPHON_SOULS({ qbd, player ->
        if (qbd.souls.isEmpty()) 0
        val targets = qbd.possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
        targets.forEach { it.sendMessage("<col=9900CC>The Queen Black Dragon starts to siphon the energy of her mages.</col>") }
        qbd.anim(ANIM_SIPHON_SOULS_ANIM)
        qbd.schedule {
            while(!qbd.souls.isEmpty()) {
                qbd.souls.forEach { soul ->
                    soul.spotAnim(3148)
                    soul.applyHit(Hit.flat(qbd, 20))
                    qbd.applyHit(Hit.heal(qbd, 40))
                }
                wait(1)
            }
        }
        10
    }),
    CHANGE_ARMOR(changeArmor@ { qbd, player ->
        if (qbd.id != 15454) return@changeArmor 0
        qbd.anim(ANIM_SPAWN_SOULS)
        val targets = qbd.possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
        if (random(2) == 1) {
            targets.forEach { it.sendMessage("<col=66FFFF>The Queen Black Dragon takes the consistency of crystal; she is more resistant to</col>") }
            targets.forEach { it.sendMessage("<col=66FFFF>magic, but weaker to physical damage.</col>") }
            qbd.transformIntoNPC(15506)
            World.spawnObject(GameObject(70823, ObjectType.SCENERY_INTERACT, 0, qbd.middleTile.transform(-12, -3, -1)))
            World.spawnObject(GameObject(70819, ObjectType.SCENERY_INTERACT, 0, qbd.middleTile.transform(6, -3, -1)))
            qbd.capDamage = 1000
        } else {
            targets.forEach { it.sendMessage("<col=669900>The Queen Black Dragon hardens her carapace; she is more resistant to physical</col>") }
            targets.forEach { it.sendMessage("<col=669900>damage, but more vulnerable to magic.</col>") }
            qbd.transformIntoNPC(15507)
            World.spawnObject(GameObject(70824, ObjectType.SCENERY_INTERACT, 0, qbd.middleTile.transform(-12, -3, -1)))
            World.spawnObject(GameObject(70820, ObjectType.SCENERY_INTERACT, 0, qbd.middleTile.transform(6, -3, -1)))
            qbd.capDamage = 1000
        }
        qbd.schedule {
            wait(40)
            qbd.transformIntoNPC(15454)
            World.spawnObject(GameObject(70822, ObjectType.SCENERY_INTERACT, 0, qbd.middleTile.transform(-12, -3, -1)))
            World.spawnObject(GameObject(70818, ObjectType.SCENERY_INTERACT, 0, qbd.middleTile.transform(6, -3, -1)))
        }
        return@changeArmor 6
    }),
    EXTREMELY_HOT_FLAMES({ qbd, player ->
        fun QBD.baseDamage(target: Entity): Int {
            val distanceFromCenter = abs(target.x - middleTile.x)
            return when {
                distanceFromCenter <= 2 -> {
                    target.tempAttribs.setB("canBrandish", true)
                    random(695, 774)
                }
                distanceFromCenter <= 6 -> random(490, 520)
                else -> random(275, 350)
            }
        }

        val targets = qbd.possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
        targets.forEach { it.sendMessage("<col=FFCC00>The Queen Black Dragon gathers her strength to breath extremely hot flames.</col>") }
        qbd.sync(ANIM_EXTREMELY_HOT_FLAMES, 3152)
        qbd.schedule {
            wait(5)
            repeat(3) { num ->
                qbd.possibleTargets.filter { it is Player && !it.isDead }.forEach {
                    it.applyHit(Hit.flat(qbd, when (getAntifireLevel(it, false)) {
                        1 -> qbd.baseDamage(it) / 2
                        2 -> qbd.baseDamage(it) / 3
                        else -> qbd.baseDamage(it)
                    }))
                    if (num == 2) it.tempAttribs.removeB("canBrandish")
                }
                wait(2)
            }
        }
        13
    }),
    FIRE_WALL({ qbd, player ->
        val numWalls = clampI(qbd.phase+1, 0, 3)
        val targets = qbd.possibleTargets.filterIsInstance<Player>().filterNot { it.isDead }
        targets.forEach { it.sendMessage("<col=FF9900>The Queen Black Dragon takes a huge breath.</col>") }
        qbd.schedule {
            qbd.sync(ANIM_FIRE_WALLS, 3155)
            wait(6)
            qbd.anim(ANIM_FIRE_WALL_CONTINUE)
            val variants = range(0, 3).toList().shuffled()
            for (i in 1..numWalls) {
                qbd.sendFirewall(variants[i-1])
                if (i != numWalls)
                    wait(6)
            }
            wait(2)
            qbd.anim(ANIM_FIRE_WALL_STOP)
        }
        (numWalls) * 6 + 7
    })
}

private fun QBD.sendFirewall(variant: Int) {
    val targets = possibleTargets.filter { it is Player && !it.isDead }
    val spotAnim = 3158+variant
    var offset = 0
    spotAnim(3156)
    World.sendProjectile(tile.transform(2, 2), tile.transform(2, -18), spotAnim, 0 to 0, 0, 30, 0, 0)
    targets.forEach { target ->
        target.tasks.scheduleTimer(0, 0) { tick ->
            when(tick) {
                18 -> return@scheduleTimer false
                else -> {
                    offset++
                    val baseTile = tile.transform(2, 2-offset)
                    for (x in -10..10) {
                        when(variant) {
                            0 -> if (x == -5) continue
                            1 -> if (x == 4) continue
                            2 -> if (x == -1) continue
                        }
                        val danger1 = baseTile.transform(x, 0)
                        val danger2 = baseTile.transform(x, -1)
                        targets.filter { !it.isDead && (it.isAt(danger1.x, danger1.y) || it.isAt(danger2.x, danger2.y)) }.forEach {
                            it.applyHit(Hit.flat(this, when(getAntifireLevel(it, false)) {
                                2 -> random(190, 210)
                                1 -> random(250, 300)
                                else -> random(450, 550)
                            }))
                        }
//                    if (Settings.getConfig().isDebug) {
//                        World.sendSpotAnim(baseTile.transform(x, 0), 502)
//                        World.sendSpotAnim(baseTile.transform(x, -1), 502)
//                    }
                    }
                }
            }
            return@scheduleTimer true
        }
    }
}