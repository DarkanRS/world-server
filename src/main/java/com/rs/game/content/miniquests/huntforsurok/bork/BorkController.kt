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
package com.rs.game.content.miniquests.huntforsurok.bork

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE
import com.rs.engine.pathfinder.Direction
import com.rs.game.World.spawnNPC
import com.rs.game.content.miniquests.huntforsurok.PortalPair
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.entity.player.managers.EmotesManager
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile

class BorkController(private val quest: Boolean) : InstancedController(Instance.of(PortalPair.BORK.tile1, 8, 8).setEntranceOffset(intArrayOf(43, 24, 0))) {
    override fun onBuildInstance() {
        player.lock()
        instance.copyMapAllPlanes(384, 688).thenAccept {
            player.cutscene {
                val instance = this@BorkController.instance ?: return@cutscene
                fadeInAndWait()
                hideMinimap()
                instance.teleportLocal(player, 43, 24, 0)
                player.setForceMultiArea(true)
                endTile = if (quest) Tile.of(getX(39), getY(25), 0) else Tile.of(getX(43), getY(24), 0)
                if (quest) {
                    val surok = npcCreate(7002, 39, 26, 0)
                    camLook(41, 24, 0)
                    camPos(33, 16, 12324)
                    camPos(29, 28, 3615, 0, 10)
                    fadeOutAndWait()
                    entityWalkTo(player, 39, 25)
                    wait(3)
                    surok.faceTile(tileFromLocal(39, 25))
                    player.faceTile(tileFromLocal(39, 26))
                    dialogue {
                        player(HeadE.FRUSTRATED, "It's a dead end, Surok. There's nowhere left to run.")
                        npc(7002, HeadE.FRUSTRATED, "You're wrong, " + player.displayName + ". I am right where I need to be.")
                        player(HeadE.FRUSTRATED, "What do you mean? You won't escape.")
                        npc(7002, HeadE.FRUSTRATED, "You cannot stop me, " + player.displayName + ". But just in case you try, allow me to introduce you to someone...")
                    }
                    waitForDialogue()
                    surok.faceDir(Direction.WEST)
                    surok.forceTalk("Bork! Kill the meddler!")
                    wait(1)
                    player.faceDir(Direction.WEST)
                    wait(2)
                    player.anim(EmotesManager.Emote.SCARED.anim)
                    wait(3)
                }
                player.interfaceManager.sendForegroundInterfaceOverGameWindow(692)
                wait(15)
                player.interfaceManager.closeInterfacesOverGameWindow()
                camPosResetSoft()
                fadeOutAndWait()
                unhideMinimap()
                spawnNPC(7134, Tile.of(getX(27), getY(33), 0), true, true).setForceMultiArea(true)
                player.resetReceivedHits()
                player.unlock()
            }
        }
    }

    override fun onDestroyInstance() {
        player.setForceMultiArea(false)
        player.unlock()
    }

    override fun onTeleported(type: TeleType) {
        player.controllerManager.forceStop()
    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        if (obj.id == 29537) {
            player.controllerManager.forceStop()
            player.spotAnim(110, 10, 96)
            player.useStairs(-1, instance.returnTo, 2, 3)
        }
        return true
    }
}
