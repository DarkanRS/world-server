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
package com.rs.game.content.combat

import com.rs.cache.loaders.Bonus
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

enum class AttackType(private val attBonus: Bonus, private val defBonus: Bonus) {
    STAB(Bonus.STAB_ATT, Bonus.STAB_DEF),
    SLASH(Bonus.SLASH_ATT, Bonus.SLASH_DEF),
    CRUSH(Bonus.CRUSH_ATT, Bonus.CRUSH_DEF),

    ACCURATE(Bonus.RANGE_ATT, Bonus.RANGE_DEF),
    RAPID(Bonus.RANGE_ATT, Bonus.RANGE_DEF),
    LONG_RANGE(Bonus.RANGE_ATT, Bonus.RANGE_DEF),

    POLYPORE_ACCURATE(Bonus.MAGIC_ATT, Bonus.MAGIC_DEF),
    POLYPORE_LONGRANGE(Bonus.MAGIC_ATT, Bonus.MAGIC_DEF),

    MAGIC(Bonus.MAGIC_ATT, Bonus.MAGIC_DEF);

    fun getAttackBonus(player: Player): Int {
        return player.combatDefinitions.getBonus(attBonus)
    }

    fun getDefenseBonus(entity: Entity): Int {
        if (entity is Player) return entity.combatDefinitions.getBonus(defBonus)
        return (entity as NPC).getBonus(defBonus)
    }
}
