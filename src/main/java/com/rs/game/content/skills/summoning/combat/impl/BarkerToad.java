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
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.skills.summoning.combat.FamiliarCombatScript;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;

@PluginEventHandler
public class BarkerToad extends FamiliarCombatScript {
	
	public static ItemOnNPCHandler load = new ItemOnNPCHandler(Pouch.BARKER_TOAD.getIdKeys(), e -> {
		if (e.getItem().getId() != 2) {
			e.getPlayer().sendMessage("You can only load the toad with cannonballs.");
			return;
		}
		if (e.getNPC() instanceof Familiar f) {
			if (f.getAttribs().getB("storedCannonball")) {
				e.getPlayer().sendMessage("The toad already has a cannonball stored in it.");
				return;
			}
			f.getAttribs().setB("storedCannonball", true);
			e.getPlayer().getInventory().deleteItem(2, 1);
			f.sync(7704, 1400);
		}
	});

	@Override
	public Object[] getKeys() {
		return Pouch.BARKER_TOAD.getIdKeys();
	}
	
	@Override
	public int alternateAttack(final NPC npc, final Entity target) {
		if (!(npc instanceof Familiar f) || !f.getAttribs().getB("storedCannonball"))
			return CANCEL;
		shootCannonball(f, target);
		return f.getAttackSpeed();
	}
	
	public static void shootCannonball(Familiar f, Entity target) {
		f.getAttribs().removeB("storedCannonball");
		f.sync(7703, 1401);
		delayHit(f, 1, target, PlayerCombat.calculateHit(f.getOwner(), target, 0, 300, f.getOwner().getEquipment().getWeaponId(), f.getOwner().getCombatDefinitions().getAttackStyle(), PlayerCombat.isRanging(f.getOwner()), true, 1.0)).setSource(f);
		World.sendProjectile(f, target, 1402, 10, 16, 30, 1.5, 16, 0);
	}
}
