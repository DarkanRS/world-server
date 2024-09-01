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
package com.rs.game.content.bosses.qbd_trent

import com.rs.Settings
import com.rs.cache.loaders.ObjectType
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.Options
import com.rs.game.World
import com.rs.game.World.removeObject
import com.rs.game.World.spawnObject
import com.rs.game.content.bosses.qbd.npcs.QueenBlackDragon
import com.rs.game.content.death.DeathOfficeController
import com.rs.game.content.skills.magic.Magic
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.events.ObjectClickEvent
import com.rs.plugin.handlers.ObjectClickHandler
import java.util.function.Consumer

class QueenBlackDragonController : Controller() {
    private var platformStand = 0

    @Transient
    private var npc: QueenBlackDragon? = null
    private var bossRegion: Instance? = null
    private var bossBase: Tile? = null
    private var rewardRegion: Instance? = null
    private var rewardBase: Tile? = null

    override fun start() {
        player.lock()
        bossRegion = Instance.of(OUTSIDE, 8, 8)
        bossRegion!!.copyMapAllPlanes(176, 792).thenAccept(Consumer { e: Boolean? ->
            bossBase = bossRegion!!.getTileBase().transform(0, 0, 1)
            player.fadeScreen(Runnable {
                player.resetReceivedHits()
                npc = QueenBlackDragon(player, bossBase!!.transform(31, 37, 0), bossBase)
                player.tele(bossBase!!.transform(33, 28, 0))
                player.setLargeSceneView(true)
                player.setForceMultiArea(true)
                player.unlock()
                player.packets.sendVarc(184, 150)
                player.packets.sendVarc(1924, 0)
                player.packets.sendVarc(1925, 0)
                player.interfaceManager.sendSub(InterfaceManager.Sub.FULL_GAMESPACE_BG, 1285)
                player.musicsManager.playSongAndUnlock(1119) // AWOKEN
            })
        })
    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        if (npc == null) return true
        if (obj.getId() == 70790) {
            if (npc!!.phase < 5) return true
            player.lock()
            player.fadeScreen(Runnable {
                player.resetReceivedHits()
                player.sendMessage("You descend the stairs that appeared when you defeated the Queen Black Dragon.")
                player.packets.sendVarc(184, -1)
                npc!!.finish()
                rewardRegion = Instance.of(OUTSIDE, 8, 8)
                rewardRegion!!.copyMapAllPlanes(160, 760).thenAccept(Consumer { e: Boolean? ->
                    player.resetReceivedHits()
                    rewardBase = rewardRegion!!.tileBase.transform(0, 0, 0)
                    player.tele(rewardBase!!.transform(31, 36, 0))
                    player.setForceNextMapLoadRefresh(true)
                    player.loadMapRegions()
                    player.interfaceManager.removeSub(InterfaceManager.Sub.FULL_GAMESPACE_BG)
                    player.unlock()
                })
            })
            return false
        }
        if (obj.getId() == 70813) {
            Magic.sendObjectTeleportSpell(player, true, Tile.of(2994, 3233, 0))
            return false
        }
        if (obj.getId() == 70814) {
            player.sendMessage("The gate is locked.")
            return false
        }
        if (obj.getId() == 70815) {
            player.startConversation(
                Dialogue()
                    .addSimple("This strange device is covered in indecipherable script. It opens for you, displaying only a small sample of the objects it contains.")
                    .addNext(Runnable { npc!!.openRewardChest(true) })
            )
            return false
        }
        if (obj.getId() == 70817) {
            npc!!.openRewardChest(false)
            return false
        }
        if (obj.getId() == npc!!.activeArtifact.getId()) {
            player.musicsManager.playSongAndUnlock(1118) // QUEEN BLACK DRAGON
            npc!!.setSpawningWorms(false)
            npc!!.nextAttack = 20
            npc!!.activeArtifact = GameObject(
                obj.getId() + 1,
                ObjectType.SCENERY_INTERACT,
                0,
                obj.getTile()
            )
            npc!!.setHitpoints(npc!!.maxHitpoints)
            npc!!.setCantInteract(false)
            npc!!.setPhase(npc!!.getPhase() + 1)
            spawnObject(npc!!.getActiveArtifact())
            when (obj.getId()) {
                70777 -> {
                    player.getPackets().sendVarc(1924, 2)
                    spawnObject(
                        GameObject(
                            70843,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(24, 21, -1)
                        )
                    )
                }

                70780 -> {
                    player.getPackets().sendVarc(1924, 4)
                    spawnObject(
                        GameObject(
                            70845,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(24, 21, -1)
                        )
                    )
                }

                70783 -> {
                    player.getPackets().sendVarc(1924, 6)
                    World.spawnObject(
                        GameObject(
                            70847,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(24, 21, -1)
                        )
                    )
                }

                70786 -> {
                    player.getPackets().sendVarc(1924, 8)
                    player.getPackets().sendRemoveObject(
                        GameObject(
                            70849,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(24, 21, -1)
                        )
                    )
                    World.removeObject(
                        GameObject(
                            70778,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(33, 31, 0)
                        )
                    )
                    World.removeObject(
                        GameObject(
                            70776,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(33, 31, 0)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70790,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(31, 29, 0)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70775,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(31, 29, -1)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70849,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(24, 21, -1)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70837,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(22, 24, -1)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70840,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(34, 24, -1)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70822,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(21, 35, -1)
                        )
                    )
                    World.spawnObject(
                        GameObject(
                            70818,
                            ObjectType.SCENERY_INTERACT,
                            0,
                            bossBase!!.transform(39, 35, -1)
                        )
                    )
                }
            }
            return false
        }
        return true
    }

    override fun process() {
        if (npc == null) return
        if (player.getY() < bossBase!!.getY() + 28) {
            if (npc!!.hasFinished()) return
            if (platformStand++ == 6) {
                player.sendMessage("You are damaged for standing too long on the raw magical platforms.")
                player.applyHit(Hit(npc, 200, HitLook.TRUE_DAMAGE))
                platformStand = 0
            }
        } else platformStand = 0
    }

    override fun checkWalkStep(lastX: Int, lastY: Int, nextX: Int, nextY: Int): Boolean {
        if (npc != null && nextY < bossBase!!.getY() + 28) {
            if (npc!!.getPhase() > 1) {
                for (step in PLATFORM_STEPS[0]!!) if (bossBase!!.getX() + (step[0] - 64) == nextX && bossBase!!.getY() + (step[1] - 64) == nextY) return true
                if (npc!!.getPhase() > 2) {
                    for (step in PLATFORM_STEPS[1]!!) if (bossBase!!.getX() + (step[0] - 64) == nextX && bossBase!!.getY() + (step[1] - 64) == nextY) return true
                    if (npc!!.getPhase() > 3) for (step in PLATFORM_STEPS[2]!!) if (bossBase!!.getX() + (step[0] - 64) == nextX && bossBase!!.getY() + (step[1] - 64) == nextY) return true
                }
            }
            return false
        }
        return true
    }

    override fun onTeleported(type: TeleType?) {
        end(0)
    }

    override fun sendDeath(): Boolean {
        player.lock(7)
        player.stopAll()
        WorldTasks.scheduleLooping(object : Task() {
            var loop: Int = 0

            override fun run() {
                if (loop == 0) player.setNextAnimation(Animation(836))
                else if (loop == 1) player.sendMessage("Oh dear, you have died.")
                else if (loop == 3) {
                    end(0)
                    player.getControllerManager().startController(DeathOfficeController(OUTSIDE, player.hasSkull()))
                } else if (loop == 4) {
                    player.jingle(90)
                    stop()
                }
                loop++
            }
        }, 0, 1)
        return false
    }

    override fun logout(): Boolean {
        end(1)
        return false
    }

    override fun forceClose() {
        end(0)
    }

    private fun end(type: Int) {
        player.setForceMultiArea(false)
        player.setLargeSceneView(false)
        if (type == 0) {
            player.getInterfaceManager().removeSub(InterfaceManager.Sub.FULL_GAMESPACE_BG)
            player.getPackets().sendVarc(184, -1)
        } else player.setTile(OUTSIDE)
        removeController()
        if (npc != null) for (item in npc!!.getRewards().toArray()) player.getBank().addItem(item, true)
        bossRegion!!.destroy()
        if (rewardRegion != null) rewardRegion!!.destroy()
    }

    fun getBase(): Tile {
        return bossBase!!
    }

    fun getNpc(): QueenBlackDragon? {
        return npc
    }

    companion object {
        val OUTSIDE: Tile = Settings.getConfig().getPlayerRespawnTile()

        private val PLATFORM_STEPS = arrayOf<Array<IntArray>?>(
            arrayOf<IntArray>(
                intArrayOf(88, 86),
                intArrayOf(88, 87),
                intArrayOf(88, 88),
                intArrayOf(88, 89),
                intArrayOf(88, 90),
                intArrayOf(88, 91),
                intArrayOf(89, 91),
                intArrayOf(89, 90),
                intArrayOf(89, 89),
                intArrayOf(89, 88),
                intArrayOf(89, 87),
                intArrayOf(89, 86),
                intArrayOf(90, 86),
                intArrayOf(90, 87),
                intArrayOf(90, 88),
                intArrayOf(90, 89),
                intArrayOf(90, 90),
                intArrayOf(90, 91),
                intArrayOf(91, 91),
                intArrayOf(91, 90),
                intArrayOf(91, 89),
                intArrayOf(91, 88),
                intArrayOf(91, 87),
                intArrayOf(92, 87),
                intArrayOf(92, 88),
                intArrayOf(92, 89),
                intArrayOf(92, 90),
                intArrayOf(92, 91),
                intArrayOf(93, 91),
                intArrayOf(93, 90),
                intArrayOf(93, 89),
                intArrayOf(93, 88),
                intArrayOf(94, 88),
                intArrayOf(94, 89),
                intArrayOf(94, 90),
                intArrayOf(94, 91),
                intArrayOf(95, 91),
                intArrayOf(95, 90),
                intArrayOf(95, 89),
                intArrayOf(96, 89),
                intArrayOf(96, 90),
                intArrayOf(96, 91),
                intArrayOf(97, 91),
                intArrayOf(97, 90),
                intArrayOf(98, 90),
                intArrayOf(98, 91),
                intArrayOf(99, 91)
            ),
            arrayOf<IntArray>(
                intArrayOf(106, 91),
                intArrayOf(106, 90),
                intArrayOf(106, 89),
                intArrayOf(106, 88),
                intArrayOf(106, 87),
                intArrayOf(106, 86),
                intArrayOf(105, 86),
                intArrayOf(105, 87),
                intArrayOf(105, 88),
                intArrayOf(105, 89),
                intArrayOf(105, 90),
                intArrayOf(105, 91),
                intArrayOf(104, 91),
                intArrayOf(104, 90),
                intArrayOf(104, 89),
                intArrayOf(104, 88),
                intArrayOf(104, 87),
                intArrayOf(104, 86),
                intArrayOf(103, 87),
                intArrayOf(103, 88),
                intArrayOf(103, 89),
                intArrayOf(103, 90),
                intArrayOf(103, 91),
                intArrayOf(102, 91),
                intArrayOf(102, 90),
                intArrayOf(102, 89),
                intArrayOf(102, 88),
                intArrayOf(102, 87),
                intArrayOf(101, 88),
                intArrayOf(101, 89),
                intArrayOf(101, 90),
                intArrayOf(101, 91),
                intArrayOf(100, 91),
                intArrayOf(100, 90),
                intArrayOf(100, 89),
                intArrayOf(100, 88),
                intArrayOf(99, 88),
                intArrayOf(99, 89),
                intArrayOf(99, 90),
                intArrayOf(98, 89)
            ),
            arrayOf<IntArray>(
                intArrayOf(99, 90),
                intArrayOf(100, 90),
                intArrayOf(100, 89),
                intArrayOf(99, 89),
                intArrayOf(98, 89),
                intArrayOf(97, 89),
                intArrayOf(95, 88),
                intArrayOf(96, 88),
                intArrayOf(97, 88),
                intArrayOf(98, 88),
                intArrayOf(99, 88),
                intArrayOf(99, 87),
                intArrayOf(98, 87),
                intArrayOf(97, 87),
                intArrayOf(96, 87),
                intArrayOf(96, 86),
                intArrayOf(97, 86),
                intArrayOf(98, 86)
            )
        )


        var entrance: ObjectClickHandler = ObjectClickHandler(arrayOf<Any>(70812), Consumer { e: ObjectClickEvent ->
            if (e.getOption() == "Investigate") {
                e.getPlayer().startConversation(
                    Dialogue()
                        .addSimple("You will be sent to the heart of this cave complex - alone. There is no way out other than victory, teleportation, or death. Only those who can endure dangerous counters (level 110 or more) should proceed.")
                        .addOptions(Consumer { ops: Options? ->
                            ops!!.add("Proceed.", Runnable { enterPortal(e.getPlayer()) })
                            ops.add("Step away from the portal.")
                        })
                )
            } else if (e.getOption() == "Pass through") enterPortal(e.getPlayer())
        })

        private fun enterPortal(player: Player) {
            if (player.getSkills().getLevelForXp(Constants.SUMMONING) < 60) {
                player.sendMessage("You need a Summoning level of 60 to go through this portal.")
                return
            }
            player.lock()
            player.getControllerManager().startController(QueenBlackDragonController())
            player.setNextAnimation(Animation(16752))
        }
    }
}