package com.rs.game.npc.combat;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
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
			if (combatDelay <= 0) {
				combatDelay = combatAttack();
			}
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
		if (target instanceof Familiar && Utils.random(3) == 0) {
			Familiar familiar = (Familiar) target;
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
		if (!npc.lineOfSightTo(target, maxDistance == 0))
			return 0;
		if (!WorldUtil.isInRange(npc, target, maxDistance + (npc.hasWalkSteps() && target.hasWalkSteps() ? (npc.getRun() && target.getRun() ? 2 : 1) : 0)))
			return 0;
		if ((!npc.isCantFollowUnderCombat() && WorldUtil.collides(npc, target)))
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
		if (npc.isFrozen())
			return true; // if freeze cant move ofc
		int distanceX = npc.getX() - npc.getRespawnTile().getX();
		int distanceY = npc.getY() - npc.getRespawnTile().getY();
		int size = npc.getSize();
		int maxDistance;
		if (!npc.isNoDistanceCheck() && !npc.isCantFollowUnderCombat()) {
			maxDistance = npc.getCombatDefinitions().getMaxDistFromSpawn();
			if (!(npc instanceof Familiar)) {
				if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance) {
					npc.forceWalkRespawnTile();
					return false;
				}
			}
			maxDistance = npc.getCombatDefinitions().getDeAggroDistance();
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
			if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
				return false; // if target distance higher 16
		} else {
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
		}
		// checks for no multi area :)
		if (npc instanceof Familiar) {
			Familiar familiar = (Familiar) npc;
			if (!familiar.canAttack(target))
				return false;
		} else {
			if (!npc.isForceMultiAttacked()) {
				if (!target.isAtMultiArea() || !npc.isAtMultiArea()) {
					if (npc.getAttackedBy() != target && npc.inCombat())
						return false;
					if (target.getAttackedBy() != npc && target.inCombat())
						return false;
				}
			}
		}
		if (!npc.isCantFollowUnderCombat()) {
			int targetSize = target.getSize();
			if (!target.hasWalkSteps() && WorldUtil.collides(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize)) {
				npc.resetWalkSteps();
				if (!npc.addWalkSteps(target.getX() + targetSize, npc.getY())) {
					npc.resetWalkSteps();
					if (!npc.addWalkSteps(target.getX() - size, npc.getY())) {
						npc.resetWalkSteps();
						if (!npc.addWalkSteps(npc.getX(), target.getY() + targetSize)) {
							npc.resetWalkSteps();
							if (!npc.addWalkSteps(npc.getX(), target.getY() - size)) {
								return true;
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
			if ((!npc.lineOfSightTo(target, maxDistance == 0)) || !WorldUtil.isInRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize, maxDistance)) {
				npc.calcFollow(target, npc.getRun() ? 2 : 1, true, npc.isIntelligentRouteFinder());
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
		this.target = null;
		npc.setNextFaceEntity(null);
	}

	public void reset() {
		combatDelay = 0;
		target = null;
	}

}