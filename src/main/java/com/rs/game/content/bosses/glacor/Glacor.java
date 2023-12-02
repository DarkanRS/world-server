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
package com.rs.game.content.bosses.glacor;

import com.rs.game.World;
import com.rs.game.content.combat.CombatSpell;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.TimerBar;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Glacor extends NPC {

	public enum InheritedType {
		ENDURING, SAPPING, UNSTABLE;
	}

	public enum Stage {
		FIRST, MINIONS, FINAL;
	}

	private boolean minionsSpawned = false;
	private InheritedType minionType = null;
	private Stage stage = Stage.FIRST;

	public UnstableMinion unstable = null;
	public SappingMinion sapping = null;
	public EnduringMinion enduring = null;

	public boolean startedTimer = false;
	public boolean hasExploded = false;

	public NPC thisNpc = this;

	public Player lastAttacked = null;

	public Glacor(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceMultiAttacked(true);
	}

	public boolean minionsKilled() {
		if (minionsSpawned)
			if (unstable.defeated && sapping.defeated && enduring.defeated)
				return true;
		return false;
	}

	public void resetNpcs() {
		if (unstable != null)
			unstable.finish();
		if (sapping != null)
			sapping.finish();
		if (enduring != null)
			enduring.finish();
		setStage(Stage.FIRST);
		setHitpoints(5000);
		minionsSpawned = false;
		minionType = null;
		setAttackedBy(null);
		setCapDamage(-1);
		setMinionType(null);
		startedTimer = false;
		hasExploded = false;
		lastAttacked = null;
	}

	public void deathReset() {
		if (unstable != null)
			unstable.finish();
		if (sapping != null)
			sapping.finish();
		if (enduring != null)
			enduring.finish();
		minionsSpawned = false;
		minionType = null;
		setAttackedBy(null);
		setStage(Stage.FIRST);
		setCapDamage(-1);
		setMinionType(null);
		startedTimer = false;
		hasExploded = false;
		lastAttacked = null;
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (getMinionType() == InheritedType.ENDURING)
			hit.setDamage((int) (hit.getDamage() * .40));
		if (hit.getData("combatSpell") != null && hit.getData("combatSpell", CombatSpell.class).isFireSpell())
			hit.setDamage(hit.getDamage() * 2);
		if (!isMinionsSpawned() && getHitpoints() < 2500) {
			spawnMinions();
			unstable.setTarget(lastAttacked);
			sapping.setTarget(lastAttacked);
		}
		super.handlePreHit(hit);
	}

	@Override
	public void processEntity() {
		super.processEntity();

		if (stage == Stage.MINIONS && minionsKilled()) {
			setStage(Stage.FINAL);
			setCapDamage(-1);
		}

		if (lastAttacked != null && (!lastAttacked.withinDistance(getTile(), 40) || lastAttacked.isDead())) {
			resetNpcs();
			return;
		}

		if (getMinionType() == null) {
			if (unstable == null && sapping == null && enduring == null)
				return;
			if (unstable.defeated && enduring.defeated && !sapping.defeated)
				setMinionType(InheritedType.SAPPING);
			else if (unstable.defeated && !enduring.defeated && sapping.defeated)
				setMinionType(InheritedType.ENDURING);
			else if (!unstable.defeated && enduring.defeated && sapping.defeated)
				setMinionType(InheritedType.UNSTABLE);
		}

		if (getMinionType() == InheritedType.UNSTABLE && unstable.defeated)
			if (!startedTimer && !hasExploded) {
				getNextHitBars().add(new TimerBar(700));
				startedTimer = true;
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						if (thisNpc.getHitpoints() <= 0 || thisNpc.isDead())
							return;
						for (Player player : World.getPlayersInChunkRange(getChunkId(), 1))
							if (Utils.getDistance(thisNpc.getX(), thisNpc.getY(), player.getX(), player.getY()) < 3)
								player.applyHit(new Hit(player, player.getHitpoints() / 2, HitLook.TRUE_DAMAGE));
						thisNpc.applyHit(new Hit(thisNpc, (int) (thisNpc.getHitpoints() * 0.80), HitLook.TRUE_DAMAGE));
						thisNpc.setNextSpotAnim(new SpotAnim(739));
						hasExploded = true;
					}
				}, 25);
			}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		deathReset();
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
				resetNpcs();
				drop();
				reset();
				setLocation(getRespawnTile());
				finish();
				setRespawnTask();
				return false;
			}
			return true;
		});
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		WorldTasks.schedule(getCombatDefinitions().getRespawnDelay(), () -> {
			try {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastChunkId(0);
				ChunkManager.updateChunks(npc);
				loadMapRegions();
				checkMultiArea();
			} catch (Throwable e) {
				Logger.handle(Glacor.class, "setRespawnTask", e);
			}
		});
	}

	public void spawnMinions() {
		setNextAnimation(new Animation(9964));
		setNextSpotAnim(new SpotAnim(635));
		unstable = new UnstableMinion(14302, Tile.of(getX() + 1, getY() + 1, getPlane()), -1, true, true, this);
		sapping = new SappingMinion(14303, Tile.of(getX() + 1, getY(), getPlane()), -1, true, true, this);
		enduring = new EnduringMinion(14304, Tile.of(getX() + 1, getY() - 1, getPlane()), -1, true, true, this);
		World.sendProjectile(this, unstable, 634, 60, 32, 50, 0.7, 0, 0);
		World.sendProjectile(this, sapping, 634, 60, 32, 50, 0.7, 0, 0);
		World.sendProjectile(this, enduring, 634, 60, 32, 50, 0.7, 0, 0);
		minionsSpawned = true;
		setStage(Stage.MINIONS);
		setCapDamage(0);
	}

	public InheritedType getMinionType() {
		return minionType;
	}

	public void setMinionType(InheritedType minionType) {
		this.minionType = minionType;
	}

	public boolean isMinionsSpawned() {
		return minionsSpawned;
	}

	public void setMinionsSpawned(boolean minionsSpawned) {
		this.minionsSpawned = minionsSpawned;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(14301, (npcId, tile) -> new Glacor(npcId, tile, false));
}
