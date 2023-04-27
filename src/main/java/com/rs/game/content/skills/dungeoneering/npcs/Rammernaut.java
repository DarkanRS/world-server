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
package com.rs.game.content.skills.dungeoneering.npcs;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class Rammernaut extends DungeonBoss {

	private Player chargeTarget;
	private int count;
	private boolean requestSpecNormalAttack;

	public Rammernaut(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9767, 9780), manager.getBossLevel()), tile, manager, reference);
		setForceFollowClose(true);
	}

	public void fail() {
		setNextAnimation(new Animation(13707));
		setNextForceTalk(new ForceTalk("Oooof!!"));
		count = -14;
	}

	public void sucess() {
		resetWalkSteps();
		setNextAnimation(new Animation(13698));
		applyStunHit(chargeTarget, (int) (chargeTarget.getMaxHitpoints() * 0.6));
		requestSpecNormalAttack = true;
		count = -12;
	}

	public void applyStunHit(final Entity entity, int maxHit) {
		entity.applyHit(new Hit(this, Utils.random(maxHit) + 1, HitLook.TRUE_DAMAGE));
		entity.freeze(2);
		if (entity instanceof Player player) {
			player.stopAll();
			player.sendMessage("You've been stunned.");
			player.freeze(2);
			if (player.getPrayer().hasProtectionPrayersOn()) {
				player.sendMessage("Your prayers have been disabled.");
				player.setProtectionPrayBlock(2);
			}
			WorldTasks.schedule(1, () -> {
				Tile tile = null;
				byte[] dirs = Utils.getDirection(getFaceAngle());
				for (int distance = 6; distance >= 0; distance--) {
					tile = Tile.of(Tile.of(entity.getX() + (dirs[0] * distance), entity.getY() + (dirs[1] * distance), entity.getPlane()));
					if (World.floorFree(tile.getPlane(), tile.getX(), tile.getY()) && getManager().isAtBossRoom(tile))
						break;
					if (distance == 0)
						tile = Tile.of(entity.getTile());
				}
				if (tile == null)
					return;
				entity.faceEntity(this);
				entity.forceMove(tile, 10070, 5, 60);
			});
		}
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (chargeTarget != null) {
			setNextFaceEntity(chargeTarget);
			if (count == 0) {
				setNextForceTalk(new ForceTalk("CHAAAAAARGE!"));
				setRun(true);
			} else if (count == -10) {
				setRun(false);
				resetWalkSteps();
				calcFollow(chargeTarget, true);
			} else if (count == -8)
				setChargeTarget(null);
			else if (count > 2) {
				resetWalkSteps();
				/*
				 * skip first step else it stucks ofc
				 */
				calcFollow(chargeTarget, true);

				if (count != 3 && !World.floorAndWallsFree(getTile(), getSize()))
					fail();
				else if (WorldUtil.isInRange(getX(), getY(), getSize(), chargeTarget.getX(), chargeTarget.getY(), chargeTarget.getSize(), 0))
					sucess();
				else if (!hasWalkSteps())
					fail();
			}
			count++;
			return;
		}
		super.processNPC();

		/*int chargeCount = getChargeCount(npc);
		if (chargeCount > 1) {
		    if (chargeCount == 2) {
			npc.setNextForceTalk(new ForceTalk("CHAAAAAARGE!"));
			npc.setRun(true);
			setChargeCount(npc, 3);
			return defs.getAttackDelay();
		    } else if (chargeCount == 3) {
			npc.calcFollow(target, true); //gotta be changed later
			if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			    npc.setNextAnimation(new Animation(13705));
			    setChargeCount(npc, 0);
			    return defs.getAttackDelay();
			}
		    }
		    return 0;
		}*/
	}

	public void setChargeTarget(Player target) {
		chargeTarget = target;
		getCombat().removeTarget();
		count = 0;
	}

	public boolean isRequestSpecNormalAttack() {
		return requestSpecNormalAttack;
	}

	public void setRequestSpecNormalAttack(boolean requestSpecNormalAttack) {
		this.requestSpecNormalAttack = requestSpecNormalAttack;
	}
}
