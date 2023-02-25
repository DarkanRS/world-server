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

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;

public class NexMinion extends NPC {

	private NexArena arena;

	public NexMinion(NexArena arena, int id, Tile tile) {
		super(id, tile, true);
		this.arena = arena;
		setCantFollowUnderCombat(true);
		setCapDamage(0);
		setIgnoreDocile(true);
	}

	public void breakBarrier() {
		setCapDamage(-1);
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (!getCombat().process())
			checkAggressivity();
	}

	@Override
	public void handlePreHit(Hit hit) {
		super.handlePreHit(hit);
		if (hit.getLook() != HitLook.RANGE_DAMAGE)
			hit.setDamage(0);
		if (getCapDamage() != -1)
			setNextSpotAnim(new SpotAnim(1549));
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		arena.moveNextStage();
	}

}
