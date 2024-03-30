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
package com.rs.game.content.minigames.barrows.npcs;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class AhrimCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2025 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target);
		if (damage != 0 && target instanceof Player player && Utils.random(3) == 0) {
			target.setNextSpotAnim(new SpotAnim(400, 0, 100));
			int currentLevel = player.getSkills().getLevel(Constants.STRENGTH);
			player.getSkills().set(Constants.STRENGTH, currentLevel < 5 ? 0 : currentLevel - 5);
		}
		World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 35, 16);
		npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
