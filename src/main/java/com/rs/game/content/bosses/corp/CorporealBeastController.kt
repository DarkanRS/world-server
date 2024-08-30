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
package com.rs.game.content.bosses.corp

import com.rs.Settings
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.model.entity.player.Controller
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.PluginEventHandler

@PluginEventHandler
class CorporealBeastController : Controller() {
    override fun start() {
    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        if (obj.getId() == 37929 || obj.getId() == 38811) {
            removeController()
            player.stopAll()
            player.tele(Tile.of(2970, 4384, player.plane))
            return false
        }
        return true
    }

    override fun process() {
        if (player.regionId != 11844) removeController()
    }

    override fun onTeleported(type: TeleType?) {
        removeController()
    }

    override fun sendDeath(): Boolean {
        WorldTasks.scheduleLooping(object : Task() {
            var loop: Int = 0

            override fun run() {
                if (loop == 0) player.setNextAnimation(Animation(836))
                else if (loop == 1) player.sendMessage("Oh dear, you have died.")
                else if (loop == 3) {
                    player.sendPVEItemsOnDeath(null, false)
                    player.reset()
                    player.tele(Tile.of(Settings.getConfig().playerRespawnTile))
                    player.setNextAnimation(Animation(-1))
                } else if (loop == 4) {
                    removeController()
                    player.jingle(90)
                    stop()
                }
                loop++
            }
        }, 0, 1)
        return false
    }

    override fun login(): Boolean {
        return false // so doesnt remove script
    }

    override fun logout(): Boolean {
        return false // so doesnt remove script
    }
}
