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
package com.rs.game.content.bosses.godwars.zaros;

import com.rs.game.World;
import com.rs.game.content.bosses.godwars.zaros.Nex.Phase;
import com.rs.game.content.bosses.godwars.zaros.attack.*;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class NexCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Nex" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		if (!(npc instanceof Nex nex))
			return notNexAttack(npc, target);
		if (nex.getTempAttribs().getB("siphoning"))
			return 0;
		switch(nex.getPhase()) {
		case SMOKE:
			if (nex.getAttackCount() % 12 == 0)
				return nex.performAttack(target, Utils.random(4) == 0 ? new Drag() : new NoEscape());
			if (nex.getAttackCount() % 6 == 0)
				return nex.performAttack(target, new Virus());
			break;
		case SHADOW:
			if (nex.getAttackCount() == 0)
				return nex.performAttack(target, new EmbraceDarkness());
			if (nex.getAttackCount() % 5 == 0)
				return nex.performAttack(target, new ShadowTraps());
			break;
		case BLOOD:
			if (nex.getAttackCount() % 8 == 0)
				return nex.performAttack(target, new Siphon());
			if (nex.getAttackCount() % 4 == 0)
				return nex.performAttack(target, new BloodSacrifice());
			break;
		case ICE:
			if (nex.getAttackCount() % 8 == 0)
				return nex.performAttack(target, new ContainThis());
			if (nex.getAttackCount() % 4 == 0)
				return nex.performAttack(target, new IcePrison());
			break;
		default:
			break;
		}
		return autoAttack(nex, target);
	}

	public int autoAttack(Nex nex, Entity target) {
		if (nex.isFollowTarget()) {
			if (!nex.inMeleeRange(target)) {
				nex.calcFollow(target, true);
				if (nex.shouldStopMeleeing())
					nex.setFollowTarget(false);
				return 0;
			}
			nex.setFollowTarget(Utils.random(2) == 0);
			int damage = getMaxHit(nex, 360, AttackStyle.MELEE, target);
			delayHit(nex, 0, target, getMeleeHit(nex, damage));
			nex.setNextAnimation(new Animation(6354));
		} else {
			nex.setFollowTarget(Utils.random(2) == 0);
			switch (nex.getPhase()) {
			case SMOKE:
				nex.setNextAnimation(new Animation(6987));
				nex.setNextSpotAnim(new SpotAnim(1214));
				for (Entity t : nex.getPossibleTargets()) {
					int damage = getMaxHit(nex, 250, AttackStyle.MAGE, t);
					delayHit(nex, World.sendProjectile(nex, t, 306, 41, 16, 41, 1.6, 16, 0, p -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getMagicHit(nex, damage));
					if (damage > 0 && Utils.getRandomInclusive(5) == 0)
						t.getPoison().makePoisoned(88);
				}
				break;
			case SHADOW:
				nex.setNextAnimation(new Animation(6987));
				for (final Entity t : nex.getPossibleTargets()) {
					int distance = (int) Utils.getDistance(t.getX(), t.getY(), nex.getX(), nex.getY());
					if (distance <= 10) {
						int damage = 800 - (distance * 800 / 11);
						delayHit(nex, World.sendProjectile(nex, t, 380, 41, 16, 41, 1.6, 16, 0, p -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getRangeHit(nex, getMaxHit(nex, damage, AttackStyle.RANGE, t)));
					}
				}
				break;
			case BLOOD:
				nex.setNextAnimation(new Animation(6986));
				delayHit(nex, World.sendProjectile(nex, target, 374, 41, 16, 41, 1.6, 16, 0).getTaskDelay(), target, getMagicHit(nex, getMaxHit(nex, 250, AttackStyle.MAGE, target)));
				break;
			case ICE:
				nex.setNextAnimation(new Animation(6986));
				for (final Entity t : nex.getPossibleTargets()) {
					int damage = getMaxHit(nex, 250, AttackStyle.MAGE, t);
					delayHit(nex, World.sendProjectile(nex, t, 362, 41, 16, 41, 35, 16, 0).getTaskDelay(), t, getMagicHit(nex, damage));
					if (damage > 0 && Utils.getRandomInclusive(5) == 0) {
						if (t instanceof Player player)
							t.freeze(Ticks.fromSeconds(player.getPrayer().isProtectingMage() ? 3 : 18), true);
						t.setNextSpotAnim(new SpotAnim(369));
					}
				}
				break;
			case ZAROS:
				nex.setNextAnimation(new Animation(6987));
				for (Entity t : nex.getPossibleTargets()) {
					int damage = getMaxHit(nex, 350, AttackStyle.MAGE, t);
					delayHit(nex, World.sendProjectile(nex, t, 306, 41, 16, 41, 1.6, 16, 0, p -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getMagicHit(nex, damage));
				}
				break;
			}
		}
		if (nex.getPhase() == Phase.ZAROS && nex.getAttackCount() % 3 == 0)
			nex.switchPrayers();
		nex.incrementAttack();
		return nex.getAttackSpeed();
	}

	public int notNexAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(6987));
		npc.setNextSpotAnim(new SpotAnim(1214));
		for (Entity t : npc.getPossibleTargets()) {
			int damage = getMaxHit(npc, 250, AttackStyle.MAGE, t);
			delayHit(npc, World.sendProjectile(npc, t, 306, 41, 16, 41, 1.6, 16, 0, p -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.getRandomInclusive(5) == 0)
				t.getPoison().makePoisoned(88);
		}
		return npc.getAttackSpeed();
	}
}
