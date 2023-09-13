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
package com.rs.game.model.entity.npc.combat;

import com.rs.game.content.Effect;
import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Summoning.ScrollTarget;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public final class NPCCombat {

	private NPC npc;
	private int combatDelay;
	private Entity target;

	public NPCCombat(NPC npc) {
		this.npc = npc;
	}

	public int getCombatDelay() {
		return combatDelay;
	}

	/*
	 * returns if under combat
	 */
	public boolean process() {
		if (combatDelay > 0)
			combatDelay--;
		if (target != null) {
			if (!checkAll()) {
				removeTarget();
				return false;
			}
			if (combatDelay <= 0)
				combatDelay = combatAttack();
			return true;
		}
		return false;
	}

	/*
	 * return combatDelay
	 */
	private int combatAttack() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return 0;
		if (target instanceof Familiar familiar && familiar.getPouch().getScroll().getTarget() == ScrollTarget.COMBAT && Utils.random(3) == 0) {
			Player player = familiar.getOwner();
			if (player != null) {
				target = player;
				npc.setTarget(target);
				addAttackedByDelay(target);
			}
		}
		int maxDistance = npc.getCombatDefinitions().getAttackRange();
		if (!(npc instanceof Nex) && !npc.lineOfSightTo(target, maxDistance == 0))
			return npc.getAttackSpeed();
		boolean los = npc.lineOfSightTo(target, maxDistance == 0);
		boolean inRange = WorldUtil.isInRange(npc, target, maxDistance + (npc.hasWalkSteps() && target.hasWalkSteps() ? (npc.getRun() && target.getRun() ? 2 : 1) : 0));
		//boolean collidesCheck = !npc.isCantFollowUnderCombat() && WorldUtil.collides(npc, target);
		//add collision check here to enable jagex's cancer NPC walking mechanic
		if (!los || !inRange)
			return 0;
		addAttackedByDelay(target);
		return CombatScriptsHandler.attack(npc, target);
	}

	protected void doDefenceEmote(Entity target) {
		target.setNextAnimationNoPriority(new Animation(PlayerCombat.getDefenceEmote(target)));
	}

	public Entity getTarget() {
		return target;
	}

	public void addAttackedByDelay(Entity target) {
		target.setAttackedBy(npc);
		target.setAttackedByDelay(System.currentTimeMillis() + npc.getAttackSpeed() * 600 + 600); // 8seconds
	}

	public void setTarget(Entity target) {
		this.target = target;
		npc.setNextFaceEntity(target);
		if (!checkAll()) {
			removeTarget();
			return;
		}
	}

	public boolean checkAll() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return false;
		if (npc.isDead() || npc.hasFinished() || npc.isForceWalking() || target.isDead() || target.hasFinished() || npc.getPlane() != target.getPlane())
			return false;
		if (npc.hasEffect(Effect.FREEZE))
			return true; // if freeze cant move ofc
		int distanceX = npc.getX() - npc.getRespawnTile().getX();
		int distanceY = npc.getY() - npc.getRespawnTile().getY();
		int size = npc.getSize();
		int maxDistance;
		if (!npc.isNoDistanceCheck() && !npc.isCantFollowUnderCombat()) {
			maxDistance = npc.getCombatDefinitions().getMaxDistFromSpawn();
			if (!(npc instanceof Familiar))
				if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance) {
					npc.forceWalkRespawnTile();
					return false;
				}
			maxDistance = npc.getCombatDefinitions().getDeAggroDistance();
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
			if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
				return false; // if target distance higher 16
		}
		if (!npc.canAttackMulti(target) || !target.canAttackMulti(npc))
			return false;
		int targetSize = target.getSize();
		boolean colliding = WorldUtil.collides(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize);
		if (!npc.isCantFollowUnderCombat()) {
			if (colliding) {
				if (!npc.hasWalkSteps() && !target.hasWalkSteps()) {
					if (!npc.addWalkSteps(target.getX() - size, npc.getY())) { //check west
						npc.resetWalkSteps();
						if (!npc.addWalkSteps(target.getX() + targetSize, npc.getY())) { //check east
							npc.resetWalkSteps();
							if (!npc.addWalkSteps(npc.getX(), target.getY() - size)) { //check south
								npc.resetWalkSteps();
								if (!npc.addWalkSteps(npc.getX(), target.getY() + targetSize)) { //check north
									return true;
								}
							}
						}
					}
				}
				return true;
			}
			if (npc.getAttackStyle() == AttackStyle.MELEE && targetSize == 1 && size == 1 && Math.abs(npc.getX() - target.getX()) == 1 && Math.abs(npc.getY() - target.getY()) == 1 && !target.hasWalkSteps()) {
				if (!npc.addWalkSteps(target.getX(), npc.getY(), 1))
					npc.addWalkSteps(npc.getX(), target.getY(), 1);
				return true;
			}

			maxDistance = npc.isForceFollowClose() ? 0 : npc.getCombatDefinitions().getAttackRange();
			npc.resetWalkSteps();
			boolean los = npc.lineOfSightTo(target, maxDistance == 0);
			boolean inRange = WorldUtil.isInRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize, maxDistance);
			if (!los || !inRange) {
				npc.calcFollow(target, npc.getRun() ? 2 : 1, npc.isIntelligentRouteFinder());
				return true;
			}
		}
		return true;
	}

	public void addCombatDelay(int delay) {
		combatDelay += delay;
	}

	public void setCombatDelay(int delay) {
		combatDelay = delay;
	}

	public boolean hasTarget() {
		return target != null;
	}

	public void removeTarget() {
		target = null;
		npc.setNextFaceEntity(null);
	}

	public void reset() {
		combatDelay = 0;
		target = null;
	}

}