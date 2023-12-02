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

import com.rs.cache.loaders.ObjectType;
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
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;
import com.rs.utils.WorldUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Gravecreeper extends DungeonBoss {

	public static final int BURN_DELAY = 17;

	private final List<BurnTile> burnedTiles;
	private long specialDelay;
	private final int originalId;

	private final GameObject[][] plinths;
	private final boolean[][] triggeredPlinths;

	public Gravecreeper(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(new int[] { 532, 533, 11708, 11709, 11710, 11711, 11712, 11713, 11714, 11715, 11716, 11717, 11718, 11719, 11720 }, manager.getBossLevel()), tile, manager, reference);
		originalId = getId();
		burnedTiles = new CopyOnWriteArrayList<>();
		plinths = new GameObject[4][4];
		triggeredPlinths = new boolean[4][4];
		setHitpoints(getMaxHitpoints());
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6; // rs makes it always 0.6 99% time when partialy blocked duh
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void processNPC() {
		if (burnedTiles != null && !burnedTiles.isEmpty())
			processBurnAttack();
		super.processNPC();
	}

	public boolean removeBurnedTile(Tile center) {
		for (BurnTile bTile : burnedTiles)
			if (bTile.center.getX() == center.getX() && bTile.center.getY() == center.getY()) {
				burnedTiles.remove(bTile);
				return true;
			}
		return false;
	}

	private void processBurnAttack() {
		for (BurnTile bTile : burnedTiles) {
			bTile.cycles++;
			if (!bTile.permenant && bTile.cycles == BURN_DELAY) {
				burnedTiles.remove(bTile);
				continue;
			}
			if (bTile.cycles % 2 != 0)
				continue;
			bTile.sendGraphics();
			for (Entity t : getPossibleTargets()) {
				Player p2 = (Player) t;
				for (Tile tile : bTile.tiles) {
					if (p2.getX() != tile.getX() || p2.getY() != tile.getY())
						continue;
					p2.applyHit(new Hit(this, (int) Utils.random(getMaxHit() * .1, getMaxHit() * .25), HitLook.TRUE_DAMAGE));
					p2.getPrayer().drainPrayer(20);
					if (p2.getPrayer().hasPrayersOn())
						p2.getPrayer().closeAllPrayers();
				}
			}
		}
	}

	@Override
	public void sendDeath(final Entity source) {
		if (specialDelay != -2) {
			setHitpoints(1);
			specialDelay = -1;
			return;
		}
		burnedTiles.clear();
		super.sendDeath(source);
	}

	private static final String[] SPECIAL_SHOUTS = { "Burrrrrry", "Digggggg", "Brrainnns" };

	public void useSpecial() {
		Tile walkTo = getNearestPlinch();
		if (walkTo == null)
			return;
		getManager().setTemporaryBoss(this);
		setCantInteract(true);
		resetReceivedHits();
		setNextFaceEntity(null);
		setForceWalk(walkTo);
		setNextForceTalk(new ForceTalk(SPECIAL_SHOUTS[specialDelay == -1 ? 2 : Utils.random(SPECIAL_SHOUTS.length)]));
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				setNextAnimation(new Animation(14507));
				activatePlinths();
				activateTombs();
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						// finish();
						setNextNPCTransformation(1957);
						if (specialDelay == -1)
							specialDelay = -2;
						WorldTasks.schedule(new Task() {
							@Override
							public void run() {
								if (getManager().isDestroyed())
									return;
								setNextTile(getManager().getTile(getReference(), 3 + Utils.random(4) * 3, 3 + Utils.random(4) * 3));
								setNextNPCTransformation(originalId);
								setNextAnimation(new Animation(14506));
								WorldTasks.schedule(new Task() {
									@Override
									public void run() {
										if (getManager().isDestroyed())
											return;
										setCantInteract(false);
										triggerPlinths();
										getManager().setTemporaryBoss(null);
									}
								});

							}

						}, 8);
					}
				}, 1);
			}

		}, Utils.getDistanceI(getTile(), walkTo));
	}

	public Tile getNearestPlinch() {
		int distance = Integer.MAX_VALUE;
		GameObject p = null;
		for (int x = 0; x < plinths.length; x++)
			for (int y = 0; y < plinths[x].length; y++) {
				GameObject plinth = getManager().getObjectWithType(getReference(), ObjectType.GROUND_DECORATION, 3 + x * 3, 3 + y * 3);
				if (plinth == null)
					continue;
				int d = (int) Utils.getDistance(getTile(), plinth.getTile());
				if (d >= distance)
					continue;
				distance = d;
				p = plinth;
			}
		return p.getTile();
	}

	public void triggerPlinths() {
		List<Entity> possibleTargets = getPossibleTargets();
		for (int x = 0; x < plinths.length; x++)
			for (int y = 0; y < plinths[x].length; y++) {
				if (plinths[x][y] == null)
					continue;
				Tile altarLoc = getManager().getTile(getReference(), TOMB_LOC_POS_2[y][x][0], TOMB_LOC_POS_2[y][x][1]);
				World.sendSpotAnim(altarLoc, new SpotAnim(2751));
				if (!triggeredPlinths[x][y]) {
					triggeredPlinths[x][y] = true;
					createBurnTiles(plinths[x][y].getTile(), true);
					for (Entity t : possibleTargets)
						if (WorldUtil.isInRange(t.getX(), t.getY(), t.getSize(), altarLoc.getX(), altarLoc.getY(), 1, 2))
							t.applyHit(new Hit(this, Utils.random((int) (t.getMaxHitpoints() * 0.1)) + 1, HitLook.MAGIC_DAMAGE));
				}
			}
	}

	public boolean cleanseTomb(Player player, GameObject tomb) {
		int[] pos = getManager().getRoomPos(tomb.getTile());
		for (int x = 0; x < plinths.length; x++)
			for (int y = 0; y < plinths[x].length; y++)
				if (TOMB_LOC_POS_2[y][x][0] == pos[0] && TOMB_LOC_POS_2[y][x][1] == pos[1]) {
					player.lock(1);
					player.setNextAnimation(new Animation(645));
					player.sendMessage("Blessing the grave costs prayer points, but evil retreats.");
					player.getPrayer().drainPrayer(10);
					cleanseTomb(x, y);
					return false;
				}
		return true;
	}

	public void cleanseTomb(int x, int y) {
		if (plinths[x][y] != null) {
			World.removeObject(plinths[x][y]);
			World.sendSpotAnim(plinths[x][y].getTile(), new SpotAnim(2320));
			if (triggeredPlinths[x][y]) {
				removeBurnedTile(plinths[x][y].getTile());
				triggeredPlinths[x][y] = false;
			}
			plinths[x][y] = null;
		}
	}

	private static final int[][][] TOMB_LOC_POS_2 = { { { 2, 2 }, { 6, 2 }, { 9, 2 }, { 13, 2 } }, { { 2, 6 }, { 7, 7 }, { 8, 7 }, { 13, 6 } }, { { 2, 9 }, { 7, 8 }, { 8, 8 }, { 13, 9 } }, { { 2, 13 }, { 6, 13 }, { 9, 13 }, { 13, 13 } } };

	public void activateTombs() {
		for (int x = 0; x < plinths.length; x++)
			for (int y = 0; y < plinths[x].length; y++) {
				if (plinths[x][y] == null)
					continue;
				GameObject altar = getManager().getObjectWithType(getReference(), ObjectType.SCENERY_INTERACT, TOMB_LOC_POS_2[y][x][0], TOMB_LOC_POS_2[y][x][1]);
				if (altar == null)
					continue;
				GameObject activeAltar = new GameObject(altar);
				activeAltar.setId(altar.getId() + 1);
				World.spawnObjectTemporary(activeAltar, Ticks.fromSeconds(7));
				World.sendSpotAnim(activeAltar.getTile(), new SpotAnim(2752));
			}
	}

	public void activatePlinths() {
		for (int x = 0; x < plinths.length; x++)
			for (int y = 0; y < plinths[x].length; y++) {
				if (plinths[x][y] != null)
					continue;
				GameObject plinth = getManager().getObjectWithType(getReference(), ObjectType.GROUND_DECORATION, 3 + x * 3, 3 + y * 3);
				if (plinth == null)
					continue;
				if (plinths[x][y] == null && Utils.random(15) < getManager().getParty().getTeam().size() * 3) {
					GameObject activePlinth = new GameObject(plinth);
					activePlinth.setId(plinth.getId() + 1);
					World.spawnObject(activePlinth);
					plinths[x][y] = activePlinth;
				}

			}
	}

	public void createBurnTiles(Tile tile, boolean permenant) {
		burnedTiles.add(new BurnTile(tile, permenant));
	}

	public void createBurnTiles(Tile tile) {
		createBurnTiles(tile, false);
	}

	public long getSpecialDelay() {
		return specialDelay;
	}

	public void setSpecialDelay(long specialDelay) {
		this.specialDelay = specialDelay;
	}

	public static class BurnTile {
		private int cycles;
		private final boolean permenant;
		private final Tile center;
		private final Tile[] tiles;

		public BurnTile(Tile center, boolean permenant) {
			this.center = center;
			this.permenant = permenant;
			tiles = new Tile[9];
			int index = 0;
			for (int x = -1; x < 2; x++)
				for (int y = -1; y < 2; y++)
					tiles[index++] = center.transform(x, y, 0);
		}

		public void sendGraphics() {
			World.sendSpotAnim(center, new SpotAnim(133));
		}
	}
}
