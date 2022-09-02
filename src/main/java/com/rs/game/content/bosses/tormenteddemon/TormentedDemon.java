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
package com.rs.game.content.bosses.tormenteddemon;

import java.util.Set;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public final class TormentedDemon extends NPC {

	private int shieldTimer;
	private int prayer;
	private int combatStyleTimer;
	private int[] damageTaken = new int[3];
	private int combatStyle;

	public TormentedDemon(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		shieldTimer = 0;
		switchPrayers(0);
		combatStyleTimer = Utils.random(28);
		combatStyle = Utils.random(3);
	}

	public void switchPrayers(int type) {
		transformIntoNPC(8349 + type);
		prayer = type;
		damageTaken[type] = 0;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (shieldTimer > 0)
			shieldTimer--;
		combatStyleTimer--;
		if (combatStyleTimer <= 0) {
			combatStyleTimer = 28;
			combatStyle++;
			if (combatStyle == 3)
				combatStyle = 0;
			sendRandomProjectile();
		}
	}

	@Override
	public void handlePreHit(final Hit hit) {
		super.handlePreHit(hit);
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			if (player.getEquipment().getWeaponId() == 6746 && hit.getLook() == HitLook.MELEE_DAMAGE && hit.getDamage() > 0) {
				shieldTimer = 100;
				player.sendMessage("The demon is temporarily weakened by your weapon.");
			}
		}
		if (shieldTimer <= 0) {
			hit.setDamage((int) (hit.getDamage() * 0.25));
			setNextSpotAnim(new SpotAnim(1885));
		}
		if (hit.getLook() == HitLook.MELEE_DAMAGE) {
			if (prayer == 0)
				hit.setDamage(0);
			damageTaken[0] += hit.getDamage() == 0 ? 20 : hit.getDamage();
			if (damageTaken[0] >= 310)
				switchPrayers(0);
		} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
			if (prayer == 1)
				hit.setDamage(0);
			damageTaken[1] += hit.getDamage() == 0 ? 20 : hit.getDamage();
			if (damageTaken[1] >= 310)
				switchPrayers(1);
		} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
			if (prayer == 2)
				hit.setDamage(0);
			damageTaken[2] += hit.getDamage() == 0 ? 20 : hit.getDamage();
			if (damageTaken[2] >= 310)
				switchPrayers(2);
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		shieldTimer = 0;
		combatStyle = 0;
		damageTaken = new int[3];
		prayer = 0;
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
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

	private void sendRandomProjectile() {
		WorldTile tile = new WorldTile(getX() + Utils.random(7), getY() + Utils.random(7), getPlane());
		for (int regionId : getMapRegionsIds()) {
			Set<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null)
				for (int pid : playerIndexes) {
					Player player = World.getPlayers().get(pid);
					if (player != null && !player.isDead() && !player.hasFinished() && player.hasStarted() && player.withinDistance(getTile(), 7)) {
						tile = new WorldTile(player.getTile(), 2);
						break;
					}
				}
		}
		WorldTile finalTile = tile;
		setNextAnimation(new Animation(10917));
		World.sendProjectile(this, tile, 1884, 100, 16, 40, 0.6, 16, 0, p -> {
			World.sendSpotAnim(this, new SpotAnim(1883), finalTile);
			for (int regionId : getMapRegionsIds()) {
				Set<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes != null)
					for (int pid : playerIndexes) {
						Player player = World.getPlayers().get(pid);
						if (player == null || player.isDead() || player.hasFinished() || !player.hasStarted() || !player.withinDistance(finalTile, 1))
							continue;
						player.sendMessage("The demon's magical attack splashes on you.");
						player.applyHit(new Hit(this, 281, HitLook.MAGIC_DAMAGE, 1));
					}
			}
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
		CoresManager.schedule(() -> {
			try {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastRegionId(0);
				World.updateEntityRegion(npc);
				loadMapRegions();
				checkMultiArea();
				shieldTimer = 0;
			} catch (Throwable e) {
				Logger.handle(TormentedDemon.class, "setRespawnTask", e);
			}
		}, getCombatDefinitions().getRespawnDelay());
	}

	public static boolean atTD(WorldTile tile) {
		if ((tile.getX() >= 2560 && tile.getX() <= 2630) && (tile.getY() >= 5710 && tile.getY() <= 5753))
			return true;
		return false;
	}

	public int getCombatStyleTimer() {
		return combatStyleTimer;
	}

	public int getCombatStyle() {
		return combatStyle;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(8349, 8350, 8351) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new TormentedDemon(npcId, tile, false);
		}
	};
}
