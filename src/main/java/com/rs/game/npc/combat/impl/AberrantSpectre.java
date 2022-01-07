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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.slayer.Slayer;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class AberrantSpectre extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Aberrant spectre" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		WorldProjectile p = World.sendProjectile(npc, target, def.getAttackProjectile(), 18, 18, 35, 1, 0, 0);
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		if (!Slayer.hasNosepeg(target)) {
			Player targetPlayer = (Player) target;
			if (!targetPlayer.getPrayer().isProtectingMage()) {
				int randomSkill = Utils.random(0, 6);
				if (randomSkill != Constants.HITPOINTS) {
					int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
					targetPlayer.getSkills().set(randomSkill, currentLevel < 5 ? 0 : currentLevel - 5);
					targetPlayer.sendMessage("The smell of the abberrant spectre make you feel slightly weaker.");
				}
			}
			delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, targetPlayer.getMaxHitpoints() / 10));
			// TODO player emote hands on ears
		} else
			delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
		return npc.getAttackSpeed();
	}
}
