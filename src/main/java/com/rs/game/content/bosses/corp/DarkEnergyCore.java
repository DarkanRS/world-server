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

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

import java.util.List;

public class DarkEnergyCore extends NPC {

	private CorporealBeast beast;
	private Entity target;

	public DarkEnergyCore(CorporealBeast beast) {
		super(8127, Tile.of(beast.getTile()), true);
		setForceMultiArea(true);
		setIgnoreDocile(true);
		this.beast = beast;
		changeTarget = 2;
	}

	private int changeTarget;
	private int sapTimer;
	private int delay;

	@Override
	public void processNPC() {
		if (isDead() || hasFinished())
			return;
		if (delay > 0) {
			delay--;
			return;
		}
		if (changeTarget > 0) {
			if (changeTarget == 1) {
				List<Entity> possibleTarget = beast.getPossibleTargets();
				if (possibleTarget.isEmpty()) {
					finish();
					beast.removeDarkEnergyCore();
					return;
				}
				target = possibleTarget.get(Utils.getRandomInclusive(possibleTarget.size() - 1));
				setNextTile(Tile.of(target.getTile()));
				delay += World.sendProjectile(this, target, 1828, 0, 0, 35, 1, 20, 0).getTaskDelay();
			}
			changeTarget--;
			return;
		}
		if (target == null || !WorldUtil.isInRange(this, target, 0)) {
			changeTarget = 5;
			return;
		}
		if (sapTimer-- <= 0) {
			int damage = Utils.getRandomInclusive(50) + 50;
			target.applyHit(new Hit(this, Utils.random(1, 131), HitLook.TRUE_DAMAGE));
			beast.heal(damage);
			delay = 2;
			if (target instanceof Player player)
				player.sendMessage("The dark core creature steals some life from you for its master.", true);
			sapTimer = getPoison().isPoisoned() ? 40 : 0;
		}
		delay = 2;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		beast.removeDarkEnergyCore();
	}

}
