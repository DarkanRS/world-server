package com.rs.game.content.transportation.canoes

import com.rs.cache.loaders.ObjectType
import com.rs.game.World
import com.rs.game.content.skills.woodcutting.Hatchet
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.pathing.RouteEvent
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub.ALL_GAME_TABS
import com.rs.game.model.`object`.GameObject
import com.rs.game.model.`object`.OwnedObject
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.ButtonClickEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onObjectClick
import kotlin.math.abs

enum class CanoeStationObjects(val objectId: Int, val varbitValue: Int) {
    TREE_STANDING(12144, 0),
    TREE_FALLING(12145, 9),
    TREE_FALLEN(12146, 10),
    TREE_SHAPED_LOG(12147, 1),
    TREE_SHAPED_DUGOUT(12148, 2),
    TREE_SHAPED_STABLE_DUGOUT(12149, 3),
    TREE_SHAPED_WAKA(12150, 4),
    CANOE_PUSHING_LOG(12151, 5),
    CANOE_PUSHING_DUGOUT(12152, 6),
    CANOE_PUSHING_STABLE_DUGOUT(12153, 7),
    CANOE_PUSHING_WAKA(12154, 8),
    CANOE_FLOATING_LOG(12155, 11),
    CANOE_FLOATING_DUGOUT(12156, 12),
    CANOE_FLOATING_STABLE_DUGOUT(12157, 13),
    CANOE_FLOATING_WAKA(12158, 14),
    CANOE_SINKING_LOG(12159, 0),
    CANOE_SINKING_DUGOUT(12160, 0),
    CANOE_SINKING_STABLE_DUGOUT(12161, 0),
    CANOE_SINKING_WAKA(12162, 0);
}

const val CANOE_STATION_VARBIT = "canoeStationVarbit"
const val SELECTED_CANOE = "selectedCanoe"

const val CANOE_SHAPING_INTERFACE = 52
const val CANOE_DESTINATION_INTERFACE = 53
const val CANOE_TRAVEL_INTERFACE = 758

val CANOE_SHAPING_SILHOUETTE = arrayOf(0, 9, 10, 8)
val CANOE_SHAPING_TEXT = arrayOf(0, 3, 2, 5)
val CANOE_SHAPING_BUTTONS = arrayOf(30, 31, 32, 33)

val CANOE_DESTINATION_BUTTONS = arrayOf(47, 48, 3, 6, 49)
val CANOE_DESTINATION_HIDE_ROW = arrayOf(10, 11, 12, 20, 18)
val CANOE_DESTINATION_YOU_ARE_HERE = arrayOf(25, 24, 23, 19, 0)

val CANOE_TREE_FALLING_ANIMATION = Animation(3304)
val CANOE_PLAYER_PUSHING_ANIMATION = Animation(3301)
val CANOE_PUSHING_ANIMATION = Animation(3304)
val CANOE_SINKING_ANIMATION = Animation(3305)

val CANOE_TRAVEL_INTERFACE_ANIMATIONS = arrayOf(
    arrayOf(0, 9890, 9889, 9888, 9887),
    arrayOf(9906, 0, 9893, 9892, 9891),
    arrayOf(9904, 9905, 0, 9895, 9894),
    arrayOf(9901, 9902, 9903, 0, 9896),
    arrayOf(9897, 9898, 9899, 9900, 0),
)

enum class CanoeStations(
    val stationRegion: Int,
    val stationVarbit: Int,
    val playerChopLocation: Tile,
    val playerFacingLocation: Tile,
    val playerFloatLocation: Tile,
    val canoeSinkLocation: Tile,
    val playerDestination: Tile,
    val canoeSinkRotation: Int,
    val locationName: String
) {
    LUMBRIDGE(
        12850,
        1839,
        Tile(3232, 3254, 0),
        Tile(3233, 3254, 0),
        Tile(3233, 3252, 0),
        Tile(3235, 3253, 0),
        Tile(3232, 3252, 0),
        1,
        "Lumbridge"
    ),
    CHAMPIONS(
        12852,
        1840,
        Tile(3204, 3343, 0),
        Tile(3204, 3342, 0),
        Tile(3202, 3343, 0),
        Tile(3200, 3340, 0),
        Tile(3202, 3343, 0),
        0,
        "the Champion's Guild"
    ),
    BARBARIAN(
        12341,
        1841,
        Tile(3112, 3409, 0),
        Tile(3111, 3409, 0),
        Tile(3112, 3411, 0),
        Tile(3109, 3410, 0),
        Tile(3112, 3411, 0),
        1,
        "the Barbarian Village"
    ),
    EDGEVILLE(
        12342,
        1842,
        Tile(3132, 3508, 0),
        Tile(3131, 3508, 0),
        Tile(3132, 3510, 0),
        Tile(3129, 3509, 0),
        Tile(3132, 3510, 0),
        1,
        "Edgeville"
    ),
    WILDERNESS(
        12603,
        0,
        Tile(0, 0, 0),
        Tile(0, 0, 0),
        Tile(0, 0, 0),
        Tile(3143, 3795, 0),
        Tile(3139, 3796, 0),
        2,
        "the Wilderness Pond"
    );

    companion object {
        private val stationRegionMap = entries.associateBy { it.stationRegion }

        @JvmStatic
        fun getCanoeStationByTile(location: Tile): CanoeStations {
            return stationRegionMap[location.regionId]!!
        }
    }
}

enum class Canoes(
    val level: Int,
    val experience: Double,
    val maxDistance: Int,
    val rate1: Int,
    val rate99: Int,
    val treeShaped: CanoeStationObjects,
    val canoePushing: CanoeStationObjects,
    val canoeFloating: CanoeStationObjects,
    val canoeSinking: CanoeStationObjects
) {
    LOG(12, 30.0, 1, 60, 70,
        CanoeStationObjects.TREE_SHAPED_LOG,
        CanoeStationObjects.CANOE_PUSHING_LOG,
        CanoeStationObjects.CANOE_FLOATING_LOG,
        CanoeStationObjects.CANOE_SINKING_LOG
    ),
    DUGOUT(27, 60.0, 2, 35, 65,
        CanoeStationObjects.TREE_SHAPED_DUGOUT,
        CanoeStationObjects.CANOE_PUSHING_DUGOUT,
        CanoeStationObjects.CANOE_FLOATING_DUGOUT,
        CanoeStationObjects.CANOE_SINKING_DUGOUT
    ),
    STABLE_DUGOUT(42, 90.0, 3, 10, 55,
        CanoeStationObjects.TREE_SHAPED_STABLE_DUGOUT,
        CanoeStationObjects.CANOE_PUSHING_STABLE_DUGOUT,
        CanoeStationObjects.CANOE_FLOATING_STABLE_DUGOUT,
        CanoeStationObjects.CANOE_SINKING_STABLE_DUGOUT
    ),
    WAKA(57, 150.0, 4, 6, 35,
        CanoeStationObjects.TREE_SHAPED_WAKA,
        CanoeStationObjects.CANOE_PUSHING_WAKA,
        CanoeStationObjects.CANOE_FLOATING_WAKA,
        CanoeStationObjects.CANOE_SINKING_WAKA
    );

    companion object {
        val indexMap = entries.associateBy { it.ordinal }
    }
}

@ServerStartupEvent
fun handleCanoes() {
    onObjectClick("Canoe station") { (player, obj, option) ->
        when (option) {
            "Chop-down" -> handleChopDown(player, obj)
            "Shape-canoe" -> handleShapeCanoe(player, obj)
            "Float Log", "Float Canoe" -> handleFloatCanoe(player, obj)
            "Paddle Log", "Paddle Canoe" -> handlePaddleCanoe(player, obj)
            else -> return@onObjectClick
        }
    }

    onButtonClick(CANOE_SHAPING_INTERFACE) { e ->
        handleCanoeShapingButtonClick(e)
    }

    onButtonClick(CANOE_DESTINATION_INTERFACE) { e ->
        handleDestinationButtonClick(e)
    }
}

private fun handleChopDown(player: Player, obj: GameObject) {
    val canoeStation = CanoeStations.getCanoeStationByTile(obj.tile)
    val hatchet: Hatchet? = Hatchet.getBest(player)
    player.setRouteEvent(RouteEvent(canoeStation.playerChopLocation) {
        player.faceObject(obj)
        if (hatchet == null) {
            player.sendMessage("You do not have a hatchet which you have the woodcutting level to use.")
            return@RouteEvent
        }
        if (player.skills.getLevel(Skills.WOODCUTTING) < 12) {
            player.sendMessage("You need a woodcutting level of at least 12 to chop down this tree.")
            return@RouteEvent
        }
        player.lock()
        player.faceTile(canoeStation.playerFacingLocation)
        player.schedule {
            player.anim(hatchet.animNormal())
            wait(3)
            player.anim(-1)
            player.vars.setVarBit(canoeStation.stationVarbit, CanoeStationObjects.TREE_FALLING.varbitValue)
            obj.animate(CANOE_TREE_FALLING_ANIMATION)
            wait(2)
            obj.refresh()
            player.vars.setVarBit(canoeStation.stationVarbit, CanoeStationObjects.TREE_FALLEN.varbitValue)
            player.unlock()
        }
    })
}

private fun handleShapeCanoe(player: Player, obj: GameObject) {
    val canoeStation = CanoeStations.getCanoeStationByTile(obj.tile)
    player.setRouteEvent(RouteEvent(canoeStation.playerFloatLocation) {
        player.faceObject(obj)
        player.tempAttribs.setI(CANOE_STATION_VARBIT, canoeStation.stationVarbit)
        Canoes.entries.forEach { canoe ->
            if (player.skills.getLevel(Skills.WOODCUTTING) >= canoe.level && canoe != Canoes.LOG) {
                player.packets.setIFHidden(CANOE_SHAPING_INTERFACE, CANOE_SHAPING_SILHOUETTE[canoe.ordinal], true)
                player.packets.setIFHidden(CANOE_SHAPING_INTERFACE, CANOE_SHAPING_TEXT[canoe.ordinal], false)
            }
        }
        player.interfaceManager.sendInterface(CANOE_SHAPING_INTERFACE)
    })
}

private fun handleFloatCanoe(player: Player, obj: GameObject) {
    val canoeStation = CanoeStations.getCanoeStationByTile(obj.tile)
    val canoe = Canoes.indexMap[player.tempAttribs.getI(SELECTED_CANOE)]!!
    player.setRouteEvent(RouteEvent(canoeStation.playerFloatLocation) {
        player.lock()
        player.faceObject(obj)
        player.anim(CANOE_PLAYER_PUSHING_ANIMATION.defs.id)
        player.soundEffect(2731, false)
        player.vars.setVarBit(canoeStation.stationVarbit, canoe.canoePushing.varbitValue)
        player.packets.sendObjectAnimation(obj, Animation(CANOE_PUSHING_ANIMATION.defs.id))
        player.schedule {
            wait(CANOE_PLAYER_PUSHING_ANIMATION.defs.emoteGameTicks)
            player.anim(-1)
            player.vars.setVarBit(canoeStation.stationVarbit, canoe.canoeFloating.varbitValue)
            player.unlock()
        }
    })
}

private fun handlePaddleCanoe(player: Player, obj: GameObject) {
    val canoeStation = CanoeStations.getCanoeStationByTile(obj.tile)
    val canoe = Canoes.indexMap[player.tempAttribs.getI(SELECTED_CANOE)]!!
    val origin = CanoeStations.getCanoeStationByTile(player.tile)
    for (i in CanoeStations.entries) {
        player.packets.setIFHidden(CANOE_DESTINATION_INTERFACE, CANOE_DESTINATION_HIDE_ROW[i.ordinal], true)
        if (i == CanoeStations.WILDERNESS) {
            if (canoe == Canoes.WAKA) {
                player.packets.setIFHidden(CANOE_DESTINATION_INTERFACE, 22, false)
                player.packets.setIFHidden(CANOE_DESTINATION_INTERFACE, CANOE_DESTINATION_HIDE_ROW[i.ordinal], false)
            }
        } else if (i.ordinal == origin.ordinal) {
            player.packets.setIFHidden(CANOE_DESTINATION_INTERFACE, CANOE_DESTINATION_YOU_ARE_HERE[i.ordinal], false)
        } else if (abs(i.ordinal - origin.ordinal) <= canoe.maxDistance) {
            player.packets.setIFHidden(CANOE_DESTINATION_INTERFACE, CANOE_DESTINATION_HIDE_ROW[i.ordinal], false)
        }
    }
    player.setRouteEvent(RouteEvent(canoeStation.playerFloatLocation) {
        player.interfaceManager.sendInterface(CANOE_DESTINATION_INTERFACE)
    })

}

private fun handleCanoeShapingButtonClick(e: ButtonClickEvent) {
    val canoe = Canoes.indexMap[CANOE_SHAPING_BUTTONS.indexOf(e.componentId)]!!
    val stationVarbit = e.player.tempAttribs.getI(CANOE_STATION_VARBIT)
    val hatchet: Hatchet? = Hatchet.getBest(e.player)
    if (hatchet == null) {
        e.player.sendMessage("You do not have a hatchet which you have the woodcutting level to use.")
        return
    }

    val level: Int = e.player.skills.getLevel(Constants.WOODCUTTING) + e.player.getInvisibleSkillBoost(Skills.WOODCUTTING)
    e.player.closeInterfaces()
    e.player.interfaceManager.removeOverlay(true)
    e.player.lock()
    e.player.tempAttribs.setI(SELECTED_CANOE, CANOE_SHAPING_BUTTONS.indexOf(e.componentId))
    e.player.repeatAction(2) {
        e.player.anim(hatchet.animCanoe())
        if (rollSuccess(e.player.auraManager.woodcuttingMul, level, hatchet, canoe)) {
            e.player.anim(-1)
            e.player.vars.setVarBit(stationVarbit, canoe.treeShaped.varbitValue)
            e.player.skills.addXp(Skills.WOODCUTTING, canoe.experience)
            e.player.unlock()
            return@repeatAction false
        } else {
            return@repeatAction true
        }
    }
}

private fun handleDestinationButtonClick(e: ButtonClickEvent) {
    val origin = CanoeStations.getCanoeStationByTile(e.player.tile)
    val destination = when (e.componentId) {
        CANOE_DESTINATION_BUTTONS[0] -> CanoeStations.LUMBRIDGE
        CANOE_DESTINATION_BUTTONS[1] -> CanoeStations.CHAMPIONS
        CANOE_DESTINATION_BUTTONS[2] -> CanoeStations.BARBARIAN
        CANOE_DESTINATION_BUTTONS[3] -> CanoeStations.EDGEVILLE
        CANOE_DESTINATION_BUTTONS[4] -> CanoeStations.WILDERNESS
        else -> CanoeStations.LUMBRIDGE
    }
    val interfaceAnimation = Animation(CANOE_TRAVEL_INTERFACE_ANIMATIONS[origin.ordinal][destination.ordinal])

    if (e.player.hasFamiliar()) {
        e.player.sendMessage("You can't take your follower on a canoe.")
        e.player.closeInterfaces()
        return
    }

    sendTravel(e.player, interfaceAnimation, origin, destination)
}


fun sendTravel(player: Player, interfaceAnimation: Animation, origin: CanoeStations, destination: CanoeStations) {

    player.interfaceManager.sendOverlay(115, true)

    val canoe = Canoes.indexMap[player.tempAttribs.getI(SELECTED_CANOE)]!!
    val sinkingCanoe = OwnedObject(
        player,
        canoe.canoeSinking.objectId,
        ObjectType.SCENERY_INTERACT,
        destination.canoeSinkRotation,
        destination.canoeSinkLocation
    )

    player.interfaceManager.sendInterface(CANOE_TRAVEL_INTERFACE)
    player.packets.setIFAnimation(interfaceAnimation.defs.id, CANOE_TRAVEL_INTERFACE, 3)
    player.lock()

    player.schedule {
        player.packets.setBlockMinimapState(2)
        player.interfaceManager.removeSubs(*ALL_GAME_TABS)

        wait(interfaceAnimation.defs.emoteGameTicks + 1)
        player.tele(destination.playerDestination)
        player.interfaceManager.sendOverlay(170, true)
        player.closeInterfaces()
        player.packets.setBlockMinimapState(0)
        player.interfaceManager.sendSubDefaults(*ALL_GAME_TABS)

        wait(1)
        World.spawnObjectTemporary(OwnedObject(player, sinkingCanoe), CANOE_SINKING_ANIMATION.defs.emoteGameTicks + 1)
        player.packets.sendObjectAnimation(sinkingCanoe, CANOE_SINKING_ANIMATION)

        player.sendMessage("You arrive at ${destination.locationName}.")
        player.sendMessage("Your canoe sinks into the water after the hard journey.")
        if (destination == CanoeStations.WILDERNESS) {
            player.sendMessage("There are no trees nearby to make a new canoe. Guess you're walking.")
        }
        player.unlock()
        player.vars.setVarBit(origin.stationVarbit, CanoeStationObjects.TREE_STANDING.varbitValue)
    }

}

fun rollSuccess(mul: Double, level: Int, hatchet: Hatchet, canoe: Canoes): Boolean {
    return Utils.skillSuccess((level * mul).toInt(), hatchet.toolMod, canoe.rate1, canoe.rate99)
}