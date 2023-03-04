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
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class LakkTheRiftSplitter extends DungeonBoss {

	private static final int[] RAIN_GRAPHICS =
		{ 2581, 2583, 2585 };

	private List<PortalCluster> clusters;

	public LakkTheRiftSplitter(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9898, 9911), manager.getBossLevel()), tile, manager, reference);
		clusters = new CopyOnWriteArrayList<>();
	}

	@Override
	public void processNPC() {
		if (isDead() || clusters == null)
			return;
		super.processNPC();
		for (PortalCluster cluster : clusters) {
			cluster.incrementCycle();
			if (cluster.getCycle() == 35) {
				clusters.remove(cluster);
				continue;
			}
			for (Entity t : getPossibleTargets()) {
				Player player = (Player) t;
				if (cluster.getCycle() < 1)
					continue;
				if (cluster.getCycle() % 2 == 0)
					for (Tile tile : cluster.getBoundary())
						if (player.getX() == tile.getX() && player.getY() == tile.getY()) {
							cluster.increaseEffectMultipier();
							int type = cluster.getType();
							double effectMultiplier = cluster.getEffectMultiplier();
							int maxHit = getMaxHit();

							if (type == 0)
								player.applyHit(new Hit(this, (int) (Utils.random(maxHit * .35, maxHit * .55) * effectMultiplier), HitLook.TRUE_DAMAGE));
							else if (type == 1)

								player.getPoison().makePoisoned((int) (Utils.random(maxHit * .10, maxHit * .30) * effectMultiplier));
							else {
								int skill = Utils.random(6);
								player.getSkills().drainLevel(skill == 3 ? Constants.MAGIC : skill, (int) (Utils.random(2, 3) * effectMultiplier));
							}
						}
			}
			if (cluster.getCycle() % 15 == 0)
				submitGraphics(cluster, this);
		}
	}

	@Override
	public void sendDeath(Entity killer) {
		super.sendDeath(killer);
		clusters.clear();
	}

	public void addPortalCluster(int type, Tile[] boundary) {
		PortalCluster cluster = new PortalCluster(type, boundary);
		submitGraphics(cluster, this);
		clusters.add(cluster);
	}

	public static void submitGraphics(PortalCluster cluster, NPC creator) {
		for (Tile tile : cluster.getBoundary())
			World.sendSpotAnim(tile, new SpotAnim((Utils.random(3) == 0 ? 1 : 0) + RAIN_GRAPHICS[cluster.getType()]));
	}

	private static class PortalCluster {

		private final int type;
		private final Tile[] boundary;
		private int cycle;
		private double effectMultiplier;

		public PortalCluster(int type, Tile[] boundary) {
			this.type = type;
			this.boundary = boundary;
			effectMultiplier = 0.5;
		}

		public Tile[] getBoundary() {
			return boundary;
		}

		public int getType() {
			return type;
		}

		public void incrementCycle() {
			cycle++;
		}

		public int getCycle() {
			return cycle;
		}

		public double getEffectMultiplier() {
			return effectMultiplier;
		}

		public void increaseEffectMultipier() {
			effectMultiplier += 0.5;
		}
	}

	public boolean doesBoundaryOverlap(List<Tile> boundaries) {
		for (PortalCluster cluster : clusters)
			for (Tile tile : cluster.getBoundary())
				for (Tile boundary : boundaries)
					if (tile.getX() == boundary.getX() && tile.getY() == boundary.getY())
						return true;
		return false;
	}
}
