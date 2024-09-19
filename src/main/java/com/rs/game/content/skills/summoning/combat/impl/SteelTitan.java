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
package com.rs.game.content.skills.summoning.combat.impl;

import com.rs.game.World;
import com.rs.game.content.combat.CombatFormulaKt;
import com.rs.game.content.combat.CombatMod;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.skills.summoning.combat.FamiliarCombatScript;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import kotlin.Pair;

@PluginEventHandler
public class SteelTitan extends FamiliarCombatScript {

	@Override
	public Object[] getKeys() {
		return Pouch.STEEL_TITAN.getIdKeys();
	}
	
	@Override
	public int alternateAttack(final NPC npc, final Entity target) {
		if (npc.inMeleeRange(target)) {
			npc.anim(8183);
			delayHit(npc, 1, target, Hit.melee(npc, getMaxHit(npc, 244, CombatStyle.MELEE, target)));
		} else {
			switch (Utils.getRandomInclusive(1)) {
			case 0:
				npc.sync(7694, 1451);
				delayHit(npc, World.sendProjectile(npc, target, 1453, new Pair<>(34, 16), 30, 5, 16).getTaskDelay(), target, Hit.magic(npc, getMaxHit(npc, 255, CombatStyle.MAGE, target)));
				break;
			case 1:
				npc.sync(8190, 1444);
				delayHit(npc, World.sendProjectile(npc, target, 1445, new Pair<>(34, 16), 30, 5, 16).getTaskDelay(), target, Hit.range(npc, getMaxHit(npc, 244, CombatStyle.RANGE, target)));
				break;
			}
		}
		return npc.getAttackSpeed();
	}

	@ServerStartupEvent
	public static void addDefenseBoost() {
		CombatFormulaKt.onCombatFormulaAdjust((_, target, _, _) -> {
			if (!(target instanceof Player pTarget)) return new CombatMod();
			if (pTarget.getFamiliarPouch() == Pouch.STEEL_TITAN) return new CombatMod(1.0, 1.0, 1.0, 1.0, 1.0, 1.15, 1.0);
			return new CombatMod();
		});
	}
}
