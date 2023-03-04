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
import com.rs.game.content.skills.dungeoneering.npcs.Stomp;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class StompCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Stomp" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		Stomp stomp = (Stomp) npc;

		if (npc.getHitpoints() <= 10 || npc.getHitpoints() < (npc.getMaxHitpoints() * (2 - stomp.getStage()) * 0.33)) {
			stomp.charge();
			return npc.getAttackSpeed();
		}
		// 0 - first 33%
		// 1 - 66-33%
		// 2 - 33-0%
		// 3 - 0%

		if (stomp.getStage() > 1 && Utils.random(10) == 0) {
			final Tile tile = Tile.of(target.getTile());
			World.sendSpotAnim(tile, new SpotAnim(2400));
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					for (Entity target : npc.getPossibleTargets())
						if (target.getX() == tile.getX() && target.getY() == tile.getY())
							target.applyHit(new Hit(npc, (int) (target.getMaxHitpoints() * 0.25), HitLook.RANGE_DAMAGE));
				}
			}, 4);
		}

		int attackStyle = Utils.random(/* stomp.getStage() > 1 ? 4 : */stomp.getStage() > 0 ? 3 : 2);
		if (attackStyle == 0 && !WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			attackStyle = 1;

		switch (attackStyle) {
		case 0:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(13449));
			npc.setNextSpotAnim(new SpotAnim(2401));
			for (Entity t : npc.getPossibleTargets()) {
				World.sendProjectile(npc, t, 2402, 16, 16, 41, 30, 0, 0);
				t.setNextSpotAnim(new SpotAnim(2403, 70, 0));
				delayHit(npc, 1, t, getRangeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.RANGE, target)));
			}
			break;
		case 2:
			npc.setNextAnimation(new Animation(13450));
			npc.setNextSpotAnim(new SpotAnim(2404));
			World.sendProjectile(npc, target, 2405, 30, 16, 41, 65, 0, 0);
			target.setNextSpotAnim(new SpotAnim(2406, 120, 0));
			delayHit(npc, 2, target, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, target)));
			break;

		}
		return npc.getAttackSpeed();
	}
}
