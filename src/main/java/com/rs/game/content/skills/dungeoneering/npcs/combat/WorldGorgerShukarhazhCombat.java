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
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.npcs.WorldGorgerShukarhazh;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class WorldGorgerShukarhazhCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "World-gorger Shukarhazh" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final WorldGorgerShukarhazh boss = (WorldGorgerShukarhazh) npc;
		final DungeonManager manager = boss.getManager();

		boolean smash = false;
		for (Player player : manager.getParty().getTeam())
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				player.sendMessage("The creature crushes you as you move underneath it.");
				delayHit(npc, 0, player, Hit.flat(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MELEE, player)));
			}
		if (smash) {
			npc.setNextAnimation(new Animation(14894));
			return 6;
		}

		if (Utils.random(manager.getParty().getTeam().size() > 1 ? 20 : 5) == 0 && WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			npc.setNextAnimation(new Animation(14892));
			delayHit(npc, 0, target, Hit.melee(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MELEE, target)));
		} else {
			npc.setNextAnimation(new Animation(14893));
			npc.setNextSpotAnim(new SpotAnim(2846, 0, 100));
			target.setNextSpotAnim(new SpotAnim(2848, 75, 100));
			delayHit(npc, 2, target, Hit.magic(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MAGE, target)));
		}
		return 6;
	}
}
