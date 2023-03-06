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

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.npcs.Dreadnaut;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class DreadnautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Dreadnaut" };// GFX 2859 Poop bubbles that drain prayer
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Dreadnaut boss = (Dreadnaut) npc;

		if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			return 0;

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(14982));
			npc.setNextSpotAnim(new SpotAnim(2865));
			int damage = getMaxHit(boss, boss.getMaxHit(), AttackStyle.MELEE, target);
			if (damage > 0) {
				target.setNextSpotAnim(new SpotAnim(2866, 75, 0));
				sendReductionEffect(boss, target, damage);
			}
			if (target instanceof Player player) {
				player.sendMessage("You have been injured and are unable to use protection prayers.");
				player.setProtectionPrayBlock(12);
			}
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		} else {
			npc.setNextAnimation(new Animation(14973));
			npc.setNextSpotAnim(new SpotAnim(2856));

			for (Entity t : boss.getPossibleTargets()) {
				if (!t.withinDistance(target.getTile(), 2))
					continue;
				int damage = getMaxHit(boss, boss.getMaxHit(), AttackStyle.MELEE, t);
				World.sendProjectile(boss, t, 2857, 30, 30, 25, 35, 15, 1);
				if (damage > 0) {
					sendReductionEffect(boss, t, damage);
					boss.addSpot(Tile.of(t.getTile()));
				} else
					t.setNextSpotAnim(new SpotAnim(2858, 75, 0));
				delayHit(npc, 1, t, getMeleeHit(npc, damage));
			}
		}
		return 5;
	}

	private void sendReductionEffect(Dreadnaut boss, Entity target, int damage) {
		if (!boss.canReduceMagicLevel() || !(target instanceof Player player))
			return;
		player.getSkills().set(Constants.MAGIC, (int) (player.getSkills().getLevel(Constants.MAGIC) - (damage * .10)));
	}
}
