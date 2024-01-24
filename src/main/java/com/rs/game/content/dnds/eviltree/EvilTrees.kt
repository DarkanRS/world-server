package com.rs.game.content.dnds.eviltree

import com.rs.cache.loaders.ObjectType
import com.rs.game.World
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

enum class TreeType(val wcReq: Int, val wcXp: Double, val farmReq: Int, val farmXp: Double, val fmReq: Int, val fmXp: Double, val healthyObj: Int, val deg1Obj: Int, val deg2Obj: Int, val deadObj: Int) {
    NORMAL(1, 15.0, 1, 20.0, 1, 200.0, 11434, 11435, 11436, 14839),
    OAK(15, 32.4, 7, 45.0, 15, 300.0, 11437, 11438, 11439, 14840),
    WILLOW(30, 45.7, 15, 66.0, 30, 450.0, 11440, 11441, 11442, 14841),
    MAPLE(45, 55.8, 22, 121.5, 45, 675.0, 11443, 11444, 11915, 14842),
    YEW(60, 87.5, 30, 172.5, 60, 1012.5, 11916, 11917, 11918, 14843),
    MAGIC(75, 125.0, 37, 311.5, 75, 1517.5, 11919, 11920, 11921, 14844),
    ELDER(90, 162.5, 42, 730.0, 90, 2600.5, 11922, 11923, 11924, 14845)
}

class EvilTree(private val treeType: TreeType, tile: Tile) : GameObject(11391, ObjectType.SCENERY_INTERACT, 0, tile) {
    private var stage = 0
    private var stageProgress = 0

    fun spawn() {
        World.spawnObject(this)
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
            if (++stageProgress >= 2) {
                incStage()
                stageProgress = 0
            }
            if (stage >= 5)
                return@repeatAction false
            return@repeatAction true
        }
    }

    fun lightFire(player: Player) {
        if (player.skills.getLevel(Skills.FIREMAKING) < treeType.fmReq) {
            player.sendMessage("You need a firemaking level of ${treeType.fmReq} to burn this tree.")
            return
        }
        // 11425 fire 0, 4
    }

    fun chop(player: Player) {
        if (player.skills.getLevel(Skills.WOODCUTTING) < treeType.wcReq) {
            player.sendMessage("You need a woodcutting level of ${treeType.wcReq} to chop this tree.")
            return
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
    onObjectClick(*(11391..11395).union(TreeType.entries.flatMap { listOf(it.healthyObj, it.deg1Obj, it.deg2Obj, it.deadObj) }).toTypedArray()) { (player, obj, option) ->
        if (obj !is EvilTree) return@onObjectClick

        when(option) {
            "Nurture" -> obj.nurture(player)
            "Chop" -> obj.chop(player)
            "Light fire" -> obj.lightFire(player)
            "Inspect" -> obj.inspect(player)
        }
    }
}



