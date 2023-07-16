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
package com.rs.game.content.bosses.corp;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.WorldUtil;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class CorporealBeast extends NPC {

	private DarkEnergyCore core;

	public CorporealBeast(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setCapDamage(1000);
		setLureDelay(3000);
		setForceAggroDistance(64);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
	}

	public void spawnDarkEnergyCore() {
		if (core != null)
			return;
		core = new DarkEnergyCore(this);
	}

	public void removeDarkEnergyCore() {
		if (core == null)
			return;
		core.finish();
		core = null;
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = super.getPossibleTargets();
		List<Entity> hittableTargets = new ArrayList<>();
		for (Entity t : targets)
			if (t.getX() > 2972)
				hittableTargets.add(t);
		return hittableTargets;
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (hit.getLook() == HitLook.CANNON_DAMAGE && hit.getDamage() > 80)
			hit.setDamage(80);
		if (hit.getSource() instanceof Player player)
			if (player.getEquipment().getWeaponId() != -1)
				if (!ItemDefinitions.getDefs(player.getEquipment().getWeaponId()).getName().contains(" spear"))
					if (hit.getLook() == HitLook.MELEE_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE)
						hit.setDamage(hit.getDamage() / 2);
		super.handlePreHit(hit);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (getTickCounter() % 3 == 0) {
			final List<Entity> possibleTargets = getPossibleTargets();
			boolean stomp = false;
			for (Entity t : possibleTargets)
				if (WorldUtil.isInRange(this, t, -1)) {
					stomp = true;
					t.applyHit(new Hit(this, Utils.random(150, 513), HitLook.TRUE_DAMAGE), 0);
				}
			if (stomp) {
				setNextAnimation(new Animation(10496));
				setNextSpotAnim(new SpotAnim(1834));
			}
		}
		if (getAttackedBy() != null && lineOfSightTo(getAttackedBy(), false))
			setAttackedBy(null);
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty() && getAttackedBy() == null) {
			resetLevels();
			setHitpoints(maxhp);
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (core != null)
			core.sendDeath(source);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(8133, (npcId, tile) -> new CorporealBeast(npcId, tile, false));
}
