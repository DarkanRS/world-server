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
package com.rs.game.npc.dungeoneering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class Dreadnaut extends DungeonBoss {

	private List<GassPuddle> puddles;

	private int ticks;
	private boolean reduceMagicLevel;

	public Dreadnaut(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12848, 12862), manager.getBossLevel()), tile, manager, reference);
		setForceFollowClose(true);
		setRun(true);
		setHitpoints(getMaxHitpoints());
		setLureDelay(6000);//6 seconds
		puddles = new CopyOnWriteArrayList<>();
	}

	@Override
	public void processNPC() {
		if (puddles == null) //still loading
			return;
		super.processNPC();
		if (!reduceMagicLevel) {
			if (isUnderCombat())
				for (Entity t : getPossibleTargets())
					if (!t.withinDistance(this, 1)) {
						ticks++;
						break;
					}
			if (ticks == 25) {
				reduceMagicLevel = true;
				setNextForceTalk(new ForceTalk("You cannot run from me forever!"));
			}
		}

		for (GassPuddle puddle : puddles) {
			puddle.cycles++;
			if (puddle.canDestroyPuddle()) {
				puddles.remove(puddle);
				continue;
			}
			if (puddle.cycles % 2 != 0)
				continue;
			if (puddle.cycles % 2 == 0)
				puddle.refreshGraphics();
			List<Entity> targets = getPossibleTargets(true);
			for (Entity t : targets) {
				if (!t.matches(puddle.tile))
					continue;
				t.applyHit(new Hit(this, Utils.random((int) (t.getHitpoints() * 0.25)) + 1, HitLook.TRUE_DAMAGE));
			}
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.60;
	}

	public boolean canReduceMagicLevel() {
		return reduceMagicLevel;
	}

	public void setReduceMagicLevel(boolean reduceMagicLevel) {
		this.reduceMagicLevel = reduceMagicLevel;
	}

	public void addSpot(WorldTile tile) {
		GassPuddle puddle = new GassPuddle(this, tile);
		puddle.refreshGraphics();
		puddles.add(puddle);
	}

	private static class GassPuddle {
		final Dreadnaut boss;
		final WorldTile tile;
		int cycles;

		public GassPuddle(Dreadnaut boss, WorldTile tile) {
			this.tile = tile;
			this.boss = boss;
		}

		public void refreshGraphics() {
			World.sendSpotAnim(boss, new SpotAnim(2859, 0, 10), tile);
		}

		public boolean canDestroyPuddle() {
			return cycles == 50;
		}
	}
}
