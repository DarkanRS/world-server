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
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public final class NightGazerKhighorahk extends DungeonBoss {

	private boolean secondStage;
	private boolean usedSpecial;
	private int lightCount;

	public NightGazerKhighorahk(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9725, 9738), manager.getBossLevel()), tile, manager, reference);
		setCantFollowUnderCombat(true); //force cant walk
	}

	public boolean isSecondStage() {
		return secondStage;
	}

	@Override
	public void sendDeath(final Entity source) {
		if (!secondStage) {
			secondStage = true;
			setNextAnimation(new Animation(getCombatDefinitions().getDeathEmote()));
			setNextNPCTransformation(getId() + 14);
			setCombatLevel((int) (getCombatLevel() * 0.85)); //15% nerf
			setHitpoints(getMaxHitpoints());
			resetBonuses();
			return;
		}
		super.sendDeath(source);
	}

	public boolean isUsedSpecial() {
		return usedSpecial;
	}

	public void setUsedSpecial(boolean usedSpecial) {
		this.usedSpecial = usedSpecial;
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (!secondStage)
			reduceHit(hit);
		super.handlePreHit(hit);
	}

	public void reduceHit(Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		hit.setDamage((int) (hit.getDamage() * lightCount * 0.25));
	}

	public void lightPillar(Player player, GameObject object) {
		if (lightCount >= 4)
			return;
		if (!player.getInventory().containsOneItem(DungeonConstants.TINDERBOX)) {
			player.sendMessage("You need a tinderbox to do this.");
			return;
		}
		player.setNextAnimation(new Animation(833));
		final GameObject light = new GameObject(object);
		light.setId(object.getId() + 1);

		World.spawnObject(light);
		lightCount++;

		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				try {
					lightCount--;
					World.removeObject(light);
					for (Entity target : getPossibleTargets())
						if (target.withinDistance(light.getTile(), 2)) {
							target.applyHit(new Hit(NightGazerKhighorahk.this, Utils.random((int) (target.getMaxHitpoints() * 0.25)) + 1, HitLook.TRUE_DAMAGE));
							if (target instanceof Player player)
								player.sendMessage("You are damaged by the shadows engulfing the pillar of light.");
						}
				} catch (Throwable e) {
					Logger.handle(NightGazerKhighorahk.class, "lightPillar", e);
				}

			}

		}, Ticks.fromSeconds(30) - getManager().getParty().getSize() * Ticks.fromSeconds(3));

	}

	/*  @Override
	  public void sendDeath(final Entity source) {
	final NPCCombatDefinitions defs = getCombatDefinitions();
	resetWalkSteps();
	getCombat().removeTarget();
	setNextAnimation(null);
	WorldTasksManager.schedule(new WorldTask() {
	    int loop;

	    @Override
	    public void run() {
		if (loop == 0) {
		    setNextAnimation(new Animation(defs.getDeathEmote()));
		} else if (loop >= defs.getDeathDelay()) {
		    if (source instanceof Player)
			((Player) source).getControllerManager().processNPCDeath(NightGazerKhighorahk.this);
		    drop();
		    reset();
		    if (source.getAttackedBy() == NightGazerKhighorahk.this) { //no need to wait after u kill
			source.setAttackedByDelay(0);
			source.setAttackedBy(null);
			source.setFindTargetDelay(0);
		    }
		    setCantInteract(true);
		    setNextNPCTransformation(9781);
		    stop();
		}
		loop++;
	    }
	}, 0, 1);
	getManager().openStairs(getReference());
	  }*/

}
