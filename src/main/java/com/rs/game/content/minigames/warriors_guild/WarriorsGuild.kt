package com.rs.game.content.minigames.warriors_guild

import com.rs.cache.loaders.ObjectType
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.engine.dialogue.startConversation
import com.rs.game.World
import com.rs.game.World.getObject
import com.rs.game.World.getPlayersInChunkRange
import com.rs.game.World.getSpawnedObject
import com.rs.game.World.isSpawnedObject
import com.rs.game.World.sendObjectAnimation
import com.rs.game.World.sendProjectile
import com.rs.game.World.spawnObjectTemporary
import com.rs.game.content.combat.AttackStyle
import com.rs.game.content.combat.AttackType
import com.rs.game.content.combat.XPType
import com.rs.game.content.combat.getWeaponAttackEmote
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.OwnedNPC
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile
import com.rs.lib.net.ClientPacket
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*
import kotlin.random.Random

private var killedCyclopses = 0
private var amountOfPlayers = 0
private var currentDummyTick = 0L
private var projectileType = 0

private val CATAPAULT_MINIGAME_INTERFACE = 411
private val CATAPULT_TARGET = Tile.of(2842, 3541, 1)
private val CATAPULT_PROJECTILE_BASE = Tile.of(2842, 3550, 1)
private val DEFENSIVE_ANIMATIONS = intArrayOf(4169, 4168, 4171, 4170)
private val CATAPULT = GameObject(15616, ObjectType.SCENERY_INTERACT, 0, 2840, 3548, 1)

private val DUMMY_LOCATIONS = arrayOf(Tile.of(2860, 3549, 0), Tile.of(2860, 3547, 0), Tile.of(2859, 3545, 0), Tile.of(2857, 3545, 0), Tile.of(2855, 3546, 0), Tile.of(2855, 3548, 0), Tile.of(2856, 3550, 0), Tile.of(2858, 3550, 0))
private val DUMMY_ROTATIONS = intArrayOf(1, 1, 2, 2, 3, 3, 0, 0)

private val SHOTPUT_FACE_18LB = Tile.of(2876, 3549, 1)
private val SHOTPUT_FACE_22LB = Tile.of(2876, 3543, 1)

private const val ALL = 0
private const val ATTACK = 1
private const val DEFENSE = 2
private const val STRENGTH = 3
private const val COMBAT = 4
private const val BALANCE = 5

private val CYCLOPS_LOBBY = Tile.of(2843, 3535, 2)
private val CYCLOPS_ROOM_SELECT_INTERFACE = 1058
private val DEFENDERS = intArrayOf(20072, 8850, 8849, 8848, 8847, 8846, 8845, 8844)

private fun canEnter(player: Player): Boolean {
    if (player.skills.getLevelForXp(STRENGTH) + player.skills.getLevelForXp(ATTACK) < 130 &&
        player.skills.getLevelForXp(ATTACK) != 99 && player.skills.getLevelForXp(STRENGTH) != 99) {
        player.sendMessage("The sum of your attack and strength level must be 130 or greater to enter the guild.")
        return false
    }
    player.controllerManager.startController(WarriorsGuildController())
    player.musicsManager.playSpecificAmbientSong(634, true)
    return true
}

@ServerStartupEvent
fun mapWarriorsGuild() {
    //Catapault and warrior dummy timer
    WorldTasks.scheduleTimer { tick ->
        if (tick % 14 == 0)
            tickDummies()
        if (tick % 8 == 0)
            tickDefenseMinigame()
        return@scheduleTimer true
    }

    onNpcClick(4287) { (player, npc) ->
        player.startConversation {
            if (player.inventory.containsItem(8856, 1)) {
                npc(npc.id, HeadE.HAPPY_TALKING, "You already have a shield. Good luck!")
                return@startConversation
            }
            npc(npc.id, HeadE.HAPPY_TALKING, "Good luck in there!")
            addItemToInv(player, 8856, "He hands you a shield.")
        }
    }

    onItemEquip(8856) { e ->
        if (e.equip()) {
            if (!inCatapultArea(e.player)) {
                e.player.sendMessage("You need to be near the target before you can equip this.")
                e.cancel()
                return@onItemEquip
            }
            sendShieldInterfaces(e.player)
        }
    }

    onObjectClick(15653) { (player, obj) ->
        if (isSpawnedObject(obj) || !canEnter(player)) return@onObjectClick
        player.lock(2)
        val opened = GameObject(obj.id, obj.type, obj.rotation - 1, obj.x, obj.y, obj.plane)
        spawnObjectTemporary(opened, 1)
        player.addWalkSteps(2876, 3542, 2, false)
    }

    onNpcDeath(4291, 4292, 6078, 6079, 6080, 6081) { (killer, npc) ->
        val player = killer as? Player ?: return@onNpcDeath
        val controller = player.controllerManager.controller as? WarriorsGuildController ?: return@onNpcDeath
        if (!controller.inCyclopsRoom) return@onNpcDeath player.sendMessage("Your time has expired and the cyclops will no longer drop defenders.")
        if (Utils.random(50) == 0)
            npc.sendDrop(player, Item(getBestDefender(player)))
        killedCyclopses++
    }

    onButtonClick(CATAPAULT_MINIGAME_INTERFACE) { (player, _, componentId) ->
        val controller = player.controllerManager.controller as? WarriorsGuildController ?: return@onButtonClick
        when(componentId) {
            13 -> controller.defensiveStyle = 0
            22 -> controller.defensiveStyle = 1
            31 -> controller.defensiveStyle = 2
            40 -> controller.defensiveStyle = 3
        }
    }

    onButtonClick(CYCLOPS_ROOM_SELECT_INTERFACE) { (player, _, componentId) ->
        val controller = player.controllerManager.controller as? WarriorsGuildController ?: return@onButtonClick
        when(componentId) {
            2 -> {
                var prevOp = Utils.clampI(player.vars.getVarBit(8668), 0, 5)
                if (prevOp == 0) {
                    prevOp = 1
                    player.vars.setVarBit(8668, prevOp)
                }
                controller.cyclopseOption = prevOp
            }
            3 -> {
                controller.cyclopseOption = ALL
            }
            22 -> {
                controller.cyclopseOption = BALANCE
                player.vars.setVarBit(8668, 5)
            }
            23 -> {
                controller.cyclopseOption = STRENGTH
                player.vars.setVarBit(8668, 3)
            }
            24 -> {
                controller.cyclopseOption = COMBAT
                player.vars.setVarBit(8668, 4)
            }
            25 -> {
                controller.cyclopseOption = ATTACK
                player.vars.setVarBit(8668, 1)
            }
            26 -> {
                controller.cyclopseOption = DEFENSE
                player.vars.setVarBit(8668, 2)
            }
            44 -> confirmAndEnterRoom(player, controller)
            else -> player.sendMessage("Unknown cyclops entrance interface component: $componentId")
        }
    }

    onItemOnObject(objectNamesOrIds = arrayOf(15621)) { (player, obj, item) ->
        if (player.tempAttribs.getB("animator_spawned")) {
            player.sendMessage("You are already in combat with an animation.")
            return@onItemOnObject
        }
        val realIndex: Int = getArmorIndex(player, item.id)
        if (realIndex == -1) return@onItemOnObject
        for (armor in ARMOR_SETS[realIndex]) player.inventory.deleteItem(armor, 1)
        player.anim(827)
        player.lock()
        player.schedule {
            wait(1)
            player.faceObject(obj)
            wait(2)
            player.simpleDialogue("The animator hums, something appears to be working.")
            wait(2)
            player.simpleDialogue("You stand back.")
            player.addWalkSteps(player.x, player.y + 3)
            wait(2)
            player.faceObject(obj)
            player.endConversation()
            wait(4)
            val npc = AnimatedArmor(player, 4278 + realIndex, obj.tile)
            npc.run = false
            npc.forceTalk("IM ALIVE!")
            npc.anim(4166)
            npc.addWalkSteps(player.x, player.y + 2)
            player.tempAttribs.setB("animator_spawned", true)
            npc.combat.target = player
            player.unlock()
            player.hintIconsManager.addHintIcon(npc, 0, -1, false)
        }
    }
}

class WarriorsGuildController(var inCyclopsRoom: Boolean = false, var cyclopseOption: Int = 0) : Controller() {
    @Transient
    var defensiveStyle = 0

    @Transient
    var lastDummy = 0L

    @Transient
    var kegCount = 0

    @Transient
    var kegTicks = 0

    override fun start() {
        sendInterfaces()
        amountOfPlayers++
    }

    override fun canAttack(target: Entity): Boolean {
        if (target is AnimatedArmor) if (player !== target.combat.target) return false
        return true
    }

    override fun canEquip(slot: Int, itemId: Int): Boolean {
        return !(slot == Equipment.HEAD && kegCount >= 1)
    }

    override fun processButtonClick(interfaceId: Int, componentId: Int, slotId: Int, slotId2: Int, packet: ClientPacket?): Boolean {
        if (interfaceId == 387 && kegCount >= 1) {
            if (componentId == 6) {
                player.sendMessage("You can't remove the kegs off your head.")
                return false
            }
        } else if (interfaceId == 750 && kegCount >= 1) {
            if (componentId == 4) {
                player.sendMessage("You cannot do this action while balancing the kegs on your head.")
                return false
            }
        } else if (interfaceId == 271 || interfaceId == 749 && componentId == 4) if (player.prayer.isCurses) {
            player.sendMessage("Harllaak frowns upon using curses in the Warrior's Guild.")
            return false
        }
        return true
    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        if (obj.id in 15624..15630) {
            if (lastDummy == currentDummyTick) {
                player.sendMessage("You have already tagged a dummy.")
                return false
            }
            submitDummyHit(player, this, obj)
            return false
        }
        if (obj.id == 15656) {
            player.interfaceManager.sendInterface(412)
            return false
        }
        if (obj.id == 66604) {
            player.interfaceManager.sendInterface(410)
            return false
        } else if (obj.id == 15664 || obj.id == 15665) {
            if (!hasEmptyHands(player)) {
                player.simpleDialogue("You must have both your hands free in order to throw a shotput.")
                return false
            }
            player.anim(827)
            player.sendOptionsDialogue {
                opExec("Standing Throw.") {
                    throwShotput(player, 0, obj.id == 15664)
                    player.anim(15079)
                }
                opExec("Step and throw.") {
                    throwShotput(player, 1, obj.id == 15664)
                    player.anim(15080)
                }
                opExec("Spin and throw.") {
                    throwShotput(player, 2, obj.id == 15664)
                    player.anim(15078)
                }
            }
            return false
        } else if (obj.id == 15647 || obj.id == 15641 || obj.id == 15644) {
            player.lock(2)
            val inLobby = player.y == obj.y
            if (obj.id == 15647) if (!inLobby) if (player.equipment.shieldId == 8856) {
                Equipment.remove(player, Equipment.SHIELD)
                closeShieldInterfaces(player)
            }
            player.addWalkSteps(obj.x, if (inLobby) obj.y + (if (obj.id == 15647) 1 else -1) else obj.y, 1, false)
            return false
        } else if (obj.id == 15658 || obj.id == 15660 || obj.id == 15653 || obj.id == 66758 && obj.x == 2861 && obj.y == 3538 && obj.plane == 1) {
            if (isSpawnedObject(obj)) return false
            if (obj.id == 15653) player.controllerManager.forceStop()
            else if (obj.id == 66758 && player.x == obj.x) resetKegBalance(player, this)
            player.lock(2)
            player.addWalkSteps(if (player.x == obj.x) obj.x + (if (obj.id == 66758) -1 else 1) else obj.x, obj.y, 1, false)
            return false
        } else if (obj.id in 15669..15673) {
            if (hasEmptyHands(player) && (player.equipment.hatId == -1 || kegCount >= 1))
                balanceKeg(player, this, obj)
            else if (kegCount == 0)
                player.simpleDialogue("You must have both your hands and head free to balance kegs.")
            return false
        } else if (obj.id == 66599 || obj.id == 66601) {
            player.nextFaceTile = CYCLOPS_LOBBY
            val withinArea = player.x == obj.x
            if (!withinArea) {
                Doors.handleDoubleDoor(player, obj)
                inCyclopsRoom = false
            } else player.startConversation {
                if (getBestDefender(player) == 8844)
                    npc(4289, HeadE.CALM_TALK, "It seems that you do not have a defender.")
                else
                    npc(4289, HeadE.CALM_TALK, "Ah, I see that you have one of the defenders already! Well done.")
                npc(4289, HeadE.CALM_TALK, "I'll release some cyclopses that might drop the next defender for you. Have fun in there.")
                npc(4289, HeadE.CALM_TALK, "Oh, and be careful; the cyclopses will occasionally summon a cyclossus. They are rather mean and can only be hurt with a rune or dragon defender.")
                exec { player.interfaceManager.sendInterface(1058) }
            }
            return false
        } else if (obj.id == 56887) {
            player.simpleDialogue("Kamfreena reports that $killedCyclopses cyclopes have been slain in the guild today. She hopes that warriors will step up and kill more!")
            return false
        }
        return true
    }

    override fun sendInterfaces() {
        if (inCatapultArea(player) && player.equipment.shieldId == 8856) sendShieldInterfaces(player)
        player.interfaceManager.sendOverlay(1057)
        for (i in player.warriorPoints.indices) player.refreshWarriorPoints(i)
    }

    override fun process() {
        tickKegMinigame(player, this)
        if (cyclopseOption != -1 && inCyclopsRoom)
            if (player.tickCounter % 100 == 0L)
                if (cyclopseOption == ALL)
                    for (index in player.warriorPoints.indices) player.setWarriorPoints(index, -3)
                else
                    player.setWarriorPoints(cyclopseOption, -10)
    }

    override fun login(): Boolean {
        start()
        return false
    }

    override fun logout(): Boolean {
        resetKegBalance(player, this)
        amountOfPlayers--
        return false
    }

    override fun onTeleported(teleType: TeleType?) {
        player.controllerManager.forceStop()
    }

    override fun forceClose() {
        resetKegBalance(player, this)
        inCyclopsRoom = false
        cyclopseOption = -1
        player.interfaceManager.removeOverlay(false)
        amountOfPlayers--
    }
}

/**
 * Armor minigame
 */
private val ARMOR_POINTS = intArrayOf(5, 10, 15, 20, 50, 60, 80)
private val ARMOR_SETS = arrayOf(intArrayOf(1155, 1117, 1075), intArrayOf(1153, 1115, 1067), intArrayOf(1157, 1119, 1069), intArrayOf(1165, 1125, 1077), intArrayOf(1159, 1121, 1071), intArrayOf(1161, 1123, 1073), intArrayOf(1163, 1127, 1079))
private val ARMOR_TYPES = arrayOf("Bronze", "Iron", "Steel", "Black", "Mithril", "Adamant", "Rune")

private fun getArmorIndex(player: Player, checkedId: Int): Int {
    for (i in ARMOR_SETS.indices) {
        for (j in ARMOR_SETS[i].indices) {
            if (ARMOR_SETS[i][j] == checkedId) {
                for (k in 0..2) if (!player.inventory.containsItem(ARMOR_SETS[i][k], 1)) {
                    player.sendMessage("You need a full set of ${ARMOR_TYPES[i]} to use the animator.")
                    return -1
                }
                return i
            }
        }
    }
    return -1
}

class AnimatedArmor(owner: Player, id: Int, tile: Tile) : OwnedNPC(owner, id, tile, false) {
    override fun finish() {
        if (hasFinished()) return
        super.finish()
        if (owner != null) {
            owner.tempAttribs.removeB("animator_spawned")
            owner.setWarriorPoints(3, ARMOR_POINTS[id - 4278])
            if (!isDead) for (item in ARMOR_SETS[id - 4278]) {
                if (item == -1) continue
                owner.inventory.addItemDrop(item, 1)
            }
        }
    }
}

/**
 * Keg minigame
 */
private fun tickKegMinigame(player: Player, controller: WarriorsGuildController) {
    if (controller.kegCount >= 1) {
        if (controller.kegCount == 5)
            controller.kegTicks++
        if (player.tickCounter % 15 == 0L)
            player.runEnergy -= 9
        player.drainRunEnergy(1.0)
        if ((player.runEnergy / 2.0) <= Math.random() || player.hasWalkSteps() && player.run)
            loseBalance(player, controller)
    }
}

private fun balanceKeg(player: Player, controller: WarriorsGuildController, obj: GameObject) {
    player.lock(4)
    player.anim(4180)
    player.schedule {
        wait(2)
        if (controller.kegCount == 0) player.appearance.setBAS(2671)
        controller.kegCount++
        player.vars.setVarBit(obj.definitions.varpBit, 1)
        player.equipment.setSlot(Equipment.HEAD, Item(8859 + controller.kegCount))
        player.equipment.refresh(Equipment.HEAD)
        player.appearance.generateAppearanceData()
    }
}


private fun loseBalance(player: Player, controller: WarriorsGuildController) {
    player.spotAnim(689 - controller.kegCount)
    player.lock(2)
    player.applyHit(Hit(null, Utils.random(20, 40), HitLook.TRUE_DAMAGE))
    player.sendMessage("You lose balance and the kegs fall onto your head.")
    player.forceTalk("Ouch!")
    if (controller.kegCount != 1) {
        player.skills.addXp(Constants.STRENGTH, 10.0 * controller.kegCount)
        player.setWarriorPoints(BALANCE, (10 * controller.kegCount) + (controller.kegTicks / 2))
    }
    resetKegBalance(player, controller)
}

private fun resetKegBalance(player: Player, controller: WarriorsGuildController) {
    if (controller.kegCount >= 1) {
        player.equipment.setSlot(Equipment.HEAD, null)
        player.equipment.refresh(Equipment.HEAD)
        player.appearance.generateAppearanceData()
        player.appearance.setBAS(-1)
    }
    controller.kegCount = 0
    controller.kegTicks = 0
    for (i in 0..5)
        player.vars.setVarBit(2252 + i, 0)
}

/**
 * Dummy minigame
 */
private fun tickDummies() {
    val index = Utils.random(DUMMY_LOCATIONS.size)
    spawnObjectTemporary(GameObject(Utils.random(15624, 15630), ObjectType.SCENERY_INTERACT, DUMMY_ROTATIONS[index], DUMMY_LOCATIONS[index]), 10)
    currentDummyTick = World.getServerTicks()
    sendObjectAnimation(CATAPULT, Animation(4164))
}

private fun submitDummyHit(player: Player, controller: WarriorsGuildController, obj: GameObject) {
    player.anim(getWeaponAttackEmote(player.equipment.getWeaponId(), player.combatDefinitions.getAttackStyle()))
    player.lock()
    player.schedule {
        if (isProperHit(player, obj)) {
            player.lock(2)
            player.skills.addXp(Constants.ATTACK, 15.0)
            player.setWarriorPoints(ATTACK, 5)
            player.sendMessage("You whack the dummy successfully!")
            controller.lastDummy = currentDummyTick
        } else {
            player.lock(5)
            player.applyHit(Hit(player, 10, HitLook.TRUE_DAMAGE))
            player.sync(Animation(424), SpotAnim(80, 5, 60))
            player.sendMessage("You whack the dummy whilst using the wrong attack style.")
        }
    }
}

private fun isProperHit(player: Player, obj: GameObject): Boolean {
    val style: AttackStyle = player.combatDefinitions.getAttackStyle()
    return when (obj.id) {
        15624 -> style.xpType == XPType.ACCURATE
        15625 -> style.attackType == AttackType.SLASH
        15626 -> style.xpType == XPType.AGGRESSIVE
        15627 -> style.xpType == XPType.CONTROLLED
        15628 -> style.attackType == AttackType.CRUSH
        15629 -> style.attackType == AttackType.STAB
        15630 -> style.xpType == XPType.DEFENSIVE
        else -> false
    }
}

/**
 * Defense minigame
 */
private fun tickDefenseMinigame() {
    projectileType = Utils.random(4)
    sendProjectile(CATAPULT_PROJECTILE_BASE, CATAPULT_TARGET, 679 + projectileType, 85, 15, 15, 0.2, 15) { projectile ->
        getPlayersInChunkRange(projectile.destination.chunkId, 1)
            .filter { inCatapultArea(it) && it.withinDistance(CATAPULT_TARGET, 0) }
            .forEach { player ->
                val controller = player.controllerManager.controller as? WarriorsGuildController ?: return@forEach
                if (controller.defensiveStyle + 679 == projectile.spotAnimId) {
                    player.skills.addXp(Constants.DEFENSE, 15.0)
                    player.setWarriorPoints(DEFENSE, 5)
                    player.anim(DEFENSIVE_ANIMATIONS[controller.defensiveStyle])
                    player.sendMessage("You deflect the incoming attack.")
                } else {
                    player.sendMessage("You fail to deflect the incoming attack.")
                    player.applyHit(Hit(player, Utils.random(10, 50), HitLook.TRUE_DAMAGE))
                }
            }
    }
}

private fun closeShieldInterfaces(player: Player) {
    player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
}

private fun sendShieldInterfaces(player: Player) {
    player.interfaceManager.sendSub(InterfaceManager.Sub.TAB_QUEST, 411)
    player.interfaceManager.removeSubs(InterfaceManager.Sub.TAB_COMBAT, InterfaceManager.Sub.TAB_ACHIEVEMENT, InterfaceManager.Sub.TAB_SKILLS, InterfaceManager.Sub.TAB_PRAYER, InterfaceManager.Sub.TAB_MAGIC, InterfaceManager.Sub.TAB_EMOTES)
    player.interfaceManager.openTab(InterfaceManager.Sub.TAB_QUEST)
}

private fun inCatapultArea(player: Player): Boolean {
    return player.withinArea(2837, 3538, 2847, 3552) && player.plane == 1
}

/**
 * Shotput minigame
 */
private fun hasEmptyHands(player: Player): Boolean {
    return player.equipment.getGlovesId() == -1 && player.equipment.getWeaponId() == -1 && player.equipment.getShieldId() == -1
}

private data class ShotPutResult(val success: Boolean, val distance: Int, val runEnergyCost: Double, val experience: Double, val tokens: Int)

private fun calculateShotPutResults(player: Player, throwingStyle: Int, is18LB: Boolean): ShotPutResult {
    val dustBonus = if (player.tempAttribs.getB("dustedHands")) 5 else 0
    val styleBonus = when (throwingStyle) {
        0 -> 30
        1 -> 20
        2 -> 10
        else -> 0
    }
    val x = player.skills.getLevel(STRENGTH) + player.runEnergy + styleBonus + dustBonus - (if (is18LB) 18 else 22)
    val thresholds = arrayOf(58, 71, 84, 97, 110, 124, 137, 150, 163, 176, 190, 203, 216)
    val distance = (thresholds.indexOfFirst { x < it } + 1).takeIf { it > 0 } ?: 14

    return ShotPutResult(
        success = Random.nextDouble(0.0, 1.0) < (x / 250.0),
        distance = distance,
        runEnergyCost = x * 0.1,
        experience = x * 0.7,
        tokens = if (is18LB) 1 + distance else 3 + distance
    )
}

fun throwShotput(player: Player, type: Int, is18LB: Boolean) {
    player.setNextFaceTile(if (is18LB) SHOTPUT_FACE_18LB else SHOTPUT_FACE_22LB)
    when(type) {
        0, 2 -> player.sendMessage("You take a deep breath and prepare yourself.")
        1 -> player.sendMessage("You take a step and throw the shot as hard as you can.")
    }
    val results = calculateShotPutResults(player, type, is18LB)
    player.drainRunEnergy(results.runEnergyCost)
    if (!results.success) {
        player.sendMessage("You fumble and drop the shot onto your toe. Ow!")
        player.applyHit(Hit(player, 10, HitLook.TRUE_DAMAGE))
        player.unlock()
        return
    }
    player.lock()
    player.schedule {
        player.anim(if (type == 0) 15079 else if (type == 1) 15080 else 15078)
        wait(1)
        wait(sendProjectile(player, Tile.of(player.x + results.distance, player.y, 1), 690, 50, 0, 30, 1.0, 15).taskDelay)
        player.skills.addXp(Constants.STRENGTH, results.experience)
        wait(1)
        when(Utils.random(3)) {
            0 -> player.sendMessage("The shot is perfectly thrown and gently drops to the floor.")
            1 -> player.sendMessage("The shot drops to the floor.")
            else -> player.sendMessage("The shot falls from the air like a brick, landing with a sickening thud.")
        }
        player.setWarriorPoints(STRENGTH, results.tokens)
        player.unlock()
    }
}

/**
 * Cyclops defender
 */
private fun getBestDefender(player: Player): Int {
    for (index in DEFENDERS.indices) if (player.equipment.shieldId == DEFENDERS[index] || player.inventory.containsItem(DEFENDERS[index], 1)) return DEFENDERS[if (index - 1 < 0) 0 else index - 1]
    return DEFENDERS[7]
}

private fun confirmAndEnterRoom(player: Player, controller: WarriorsGuildController) {
    if (controller.cyclopseOption == -1) {
        player.sendMessage("You must select an option before proceeding to the cyclopes room.")
        return
    }

    val hasEnoughPoints = if (controller.cyclopseOption == ALL)
        player.warriorPoints.all { it >= 30 }
    else
        player.warriorPoints[controller.cyclopseOption] >= 200

    if (!hasEnoughPoints) {
        player.sendMessage("You don't have enough points to complete this option.")
        return
    }

    if (getSpawnedObject(Tile.of(2846, 3535, 2)) != null) return

    player.lock(1)
    Doors.handleDoubleDoor(player, getObject(Tile.of(2846, 3535, 2)))
    player.closeInterfaces()
    controller.inCyclopsRoom = true
}

/**
 * Warriors guild extension functions for manipulating the array on the player file
 */
private fun Player.setWarriorPoints(index: Int, pointsDifference: Int) {
    if (warriorPoints == null || warriorPoints.size != 6)
        warriorPoints = IntArray(6)
    warriorPoints[index] += pointsDifference
    if (warriorPoints[index] < 0) {
        val controller = controllerManager.controller as? WarriorsGuildController ?: return
        controller.inCyclopsRoom = false
        tele(CYCLOPS_LOBBY)
        warriorPoints[index] = 0
    } else if (warriorPoints[index] > 65535)
        warriorPoints[index] = 65535
    refreshWarriorPoints(index)
}

private fun Player.refreshWarriorPoints(index: Int) {
    if (warriorPoints == null || warriorPoints.size != 6)
        warriorPoints = IntArray(6)
    vars.setVarBit(index + 8662, warriorPoints[index])
}