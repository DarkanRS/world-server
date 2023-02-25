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
package com.rs.game.content.skills.slayer.npcs.combat;

import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.slayer.npcs.Strykewyrm;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;
import com.rs.utils.WorldUtil;

public class StrykewyrmCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 9463, 9465, 9467 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandomInclusive(20);
		if (attackStyle <= 7 && WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) { // melee
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			if (npc.getId() == 9467) {
				if (Utils.getRandomInclusive(10) == 0) {
					target.setNextSpotAnim(new SpotAnim(2309));
					target.getPoison().makePoisoned(44);
				}
			}
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target)));
			return npc.getAttackSpeed();
		}
		if (attackStyle <= 9) { // mage
			npc.setNextAnimation(new Animation(12794));
			final Hit hit = getMagicHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target));
			delayHit(npc, 1, target, hit);
			World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 30, 16, 0);
			if (npc.getId() == 9463)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (Utils.getRandomInclusive(10) == 0 && !target.hasEffect(Effect.FREEZE)) {
							target.freeze(Ticks.fromSeconds(3));
							target.setNextSpotAnim(new SpotAnim(369));
							if (target instanceof Player player)
								player.stopAll();
						} else if (hit.getDamage() != 0)
							target.setNextSpotAnim(new SpotAnim(2315));
					}
				}, 1);
			else if (npc.getId() == 9467)
				if (Utils.getRandomInclusive(10) == 0) {
					target.setNextSpotAnim(new SpotAnim(2313));
					if (Utils.random(2) == 0)
						target.getPoison().makePoisoned(88);
				}
		} else if (attackStyle == 20) { // bury
			final Tile tile = Tile.of(target.getTile()).transform(-1, -1, 0);
			npc.setNextAnimation(new Animation(12796));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			WorldTasks.schedule(new WorldTask() {

				int count;

				@Override
				public void run() {
					if (count == 0) {
						npc.transformIntoNPC(((Strykewyrm) npc).getStompId());
						npc.setForceWalk(tile);
						count++;
					} else if (count == 1 && !npc.hasForceWalk()) {
						npc.transformIntoNPC(((Strykewyrm) npc).getStompId() + 1);
						npc.setNextAnimation(new Animation(12795));
						int distanceX = target.getX() - npc.getX();
						int distanceY = target.getY() - npc.getY();
						int size = npc.getSize();
						if (distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1) {
							delayHit(npc, 0, target, new Hit(npc, 300, HitLook.TRUE_DAMAGE));
							if (npc.getId() == 9467)
								target.getPoison().makePoisoned(88);
							else if (npc.getId() == 9465) {
								delayHit(npc, 0, target, new Hit(npc, 300, HitLook.TRUE_DAMAGE));
								target.setNextSpotAnim(new SpotAnim(2311));
							}
						}
						count++;
					} else if (count == 2) {
						WorldTasks.schedule(new WorldTask() {
							@Override
							public void run() {
								npc.getCombat().setCombatDelay(npc.getAttackSpeed());
								npc.setTarget(target);
								npc.setCantInteract(false);
							}
						});
						stop();
					}
				}
			}, 1, 1);
		}
		return npc.getAttackSpeed();
	}
}
