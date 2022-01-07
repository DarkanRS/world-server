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
package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.WorldGorgerShukarhazh;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
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
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHit(npc, AttackStyle.MELEE, player)));
			}
		if (smash) {
			npc.setNextAnimation(new Animation(14894));
			return 6;
		}

		if (Utils.random(manager.getParty().getTeam().size() > 1 ? 20 : 5) == 0 && WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			npc.setNextAnimation(new Animation(14892));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
		} else {
			npc.setNextAnimation(new Animation(14893));
			npc.setNextSpotAnim(new SpotAnim(2846, 0, 100));
			target.setNextSpotAnim(new SpotAnim(2848, 75, 100));
			delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)));
		}
		return 6;
	}
}
