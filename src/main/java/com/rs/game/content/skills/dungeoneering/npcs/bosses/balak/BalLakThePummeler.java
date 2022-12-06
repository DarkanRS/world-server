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
package com.rs.game.content.skills.dungeoneering.npcs.bosses.balak;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class BalLakThePummeler extends DungeonBoss {

	private boolean skip;
	private int barPercentage;

	private List<PoisionPuddle> puddles = new CopyOnWriteArrayList<>();

	public BalLakThePummeler(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10128, 10141), manager.getBossLevel()), tile, manager, reference);
		setLureDelay(6000); //this way you can lure him hehe, still not as much as outside dung npcs
		setHitpoints(getMaxHitpoints());
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		skip = !skip;
		if (!skip) {
			boolean reduced = false;
			for (PoisionPuddle puddle : puddles) {
				puddle.cycles++;
				if (puddle.canDestroyPoision()) {
					puddles.remove(puddle);
					continue;
				}
				List<Entity> targets = getPossibleTargets(true);
				if (WorldUtil.collides(getX(), getY(), getSize(), puddle.tile.getX(), puddle.tile.getY(), 1)) {
					barPercentage = barPercentage > 1 ? barPercentage - 2 : 0;
					sendDefenceBar();
					reduced = true;
				}
				for (Entity t : targets) {
					if (!t.withinDistance(puddle.tile, 1))
						continue;
					t.applyHit(new Hit(this, Utils.random((int) (t.getHitpoints() * 0.25)) + 1, HitLook.TRUE_DAMAGE));
				}
			}
			if (!reduced)
				if (!isUnderCombat()) {
					if (barPercentage > 0) {
						barPercentage--;
						sendDefenceBar();
					}
				} else if (barPercentage < 100) {
					barPercentage++;
					sendDefenceBar();
				}
		}
	}

	@Override
	public void processHit(Hit hit) {
		int damage = hit.getDamage();
		HitLook look = hit.getLook();
		if (damage > 0)
			if (look == HitLook.MELEE_DAMAGE || look == HitLook.RANGE_DAMAGE || look == HitLook.MAGIC_DAMAGE) {
				double multiplier = (100D - (barPercentage)) / 100D;
				hit.setDamage((int) (damage * multiplier));
			}
		super.processHit(hit);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		puddles.clear();
		sendDefenceBar();
	}

	private void sendDefenceBar() {
		if (isDead())
			getManager().hideBar(getReference());
		else
			getManager().showBar(getReference(), "Demon's Defence", barPercentage);
	}

	private static class PoisionPuddle {
		final WorldTile tile;
		int cycles;

		public PoisionPuddle(WorldTile tile, int barPercentage) {
			this.tile = tile;
		}

		public boolean canDestroyPoision() {
			return cycles == 15;
		}
	}

	public void addPoisionBubble(WorldTile centerTile) {
		puddles.add(new PoisionPuddle(centerTile, barPercentage));
		addPoisonBubbleSpotAnimations(centerTile);
	}

	private void addPoisonBubbleSpotAnimations(WorldTile centerTile) {
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX(), centerTile.getY(), centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX()+1, centerTile.getY(), centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX(), centerTile.getY()+1, centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX()+1, centerTile.getY()+1, centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX()-1, centerTile.getY(), centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX(), centerTile.getY()-1, centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX()-1, centerTile.getY()-1, centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX()-1, centerTile.getY()+1, centerTile.getPlane()));
		World.sendSpotAnim(this, new SpotAnim(2588), WorldTile.of(centerTile.getX()+1, centerTile.getY()-1, centerTile.getPlane()));
	}

	public List<PoisionPuddle> getPoisionPuddles() {
		return puddles;
	}
}
