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
package com.rs.game.content.skills.prayer.cremation

import com.rs.cache.loaders.ItemDefinitions
import com.rs.game.World.addGroundItem
import com.rs.game.World.getClosestObjectByObjectId
import com.rs.game.World.sendSpotAnim
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.OwnedNPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.game.model.`object`.OwnedObject
import com.rs.lib.Constants
import com.rs.lib.game.Item
import com.rs.lib.game.Tile

class Pyre(player: Player?, obj: GameObject?, val log: PyreLog, private val isShadePyre: Boolean) : OwnedObject(player, GameObject(obj, if (isShadePyre) log.shadeNoCorpse else log.vyreNoCorpse)) {
    private var corpse: Corpse? = null
    private var life = 50
    private var lit = false

    override fun tick(owner: Player) {
        if (life-- <= 0) destroy()
    }

    fun setCorpse(corpse: Corpse): Boolean {
        if (!log.validCorpse(corpse) || (corpse == Corpse.VYRE && isShadePyre)) return false
        this.corpse = corpse
        setId(if (isShadePyre) log.shadeCorpse else log.vyreCorpse)
        life = 50
        return true
    }

    override fun onDestroy() {
        if (lit) return
        addGroundItem(Item(log.itemId), coordFace, owner)
        if (corpse != null) addGroundItem(Item(corpse!!.itemIds[0]), coordFace, owner)
    }

    fun light(player: Player): Boolean {
        if (corpse == null) return false
        life = 50
        lit = true
        player.lock()
        player.schedule {
            player.anim(16700)
            wait(1)
            sendSpotAnim(coordFace, 357)
            ReleasedSpirit(player, coordFace, isShadePyre)
            player.skills.addXp(Constants.FIREMAKING, log.xp)
            player.skills.addXp(Constants.PRAYER, corpse!!.xp)
            wait(2)
            this@Pyre.destroy()
            wait(1)
            player.incrementCount(ItemDefinitions.getDefs(corpse!!.itemIds[0]).name + " cremated")
            player.unlock()
            val stand: GameObject = getClosestObjectByObjectId(if (isShadePyre) 4065 else 30488, coordFace) ?: return@schedule
            sendSpotAnim(stand.tile, 1605)
            for (item in corpse!!.getKeyDrop(player, log)) if (item != null) addGroundItem(item, stand.tile)
        }
        return true
    }

    private class ReleasedSpirit(owner: Player?, tile: Tile?, shade: Boolean) : OwnedNPC(owner, if (shade) 1242 else 7687, tile, false) {
        private var life = if (shade) 6 else 12

        override fun processNPC() {
            if (life-- <= 0) finish()
        }
    }
}
