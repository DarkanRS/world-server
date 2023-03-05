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

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.map.Chunk;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.Ticks;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public final class Stomp extends DungeonBoss {

	private static final int IVULNERABLE_TIMER = 37; // 16.5 sec
	private int stage;
	private int count;
	private int lodeStoneType;
	private boolean[] lodestones;

	private List<int[]> shadows;

	public Stomp(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9782, 9796), manager.getBossLevel()), tile, manager, reference);
		setCantFollowUnderCombat(true); // force cant walk
		freeze(5000000);
		lodestones = new boolean[2];
		shadows = new ArrayList<>();
	}
	
	@ServerStartupEvent
	public static void overrideLoS() {
		Entity.addLOSOverride((source, target, melee) -> {
			if (target instanceof Stomp s)
				return s.getManager().isAtBossRoom(source.getTile());
			return false;
		});
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public Tile getMiddleTile() {
		return this.getTile();
	}

	@Override
	public void processNPC() {
		if (getId() == 9781)
			setNextAnimation(new Animation(13460));
		else {
			if (count > 0) {
				if (count == IVULNERABLE_TIMER - 3) {
					List<Entity> possibleTargets = getPossibleTargets();
					for (int[] s : shadows) {
						GameObject object = getManager().spawnObjectTemporary(getReference(), 49268, ObjectType.SCENERY_INTERACT, 0, s[0], s[1], Ticks.fromSeconds(30));
						for (Entity target : possibleTargets)
							if (target.getX() == object.getX() && target.getY() == object.getY())
								target.applyHit(new Hit(this, 1 + Utils.random((int) (target.getMaxHitpoints() * 0.8)), HitLook.TRUE_DAMAGE));
					}
				}
				if (count == 1) {
					setCantInteract(false);
					if (lodestones[0] && lodestones[1]) {
						stage++;
						if (stage == 3) {
							setHitpoints(0);
							sendDeath(this);
							destroyExistingDebris();
						}
						for (Entity target : getPossibleTargets())
							if (target instanceof Player player)
								player.sendMessage("The portal weakens, harming Stomp!");
					} else
						heal((int) (getMaxHitpoints() * 0.25));
					lodestones[0] = lodestones[1] = false;
					refreshLodestones();
					removeCrystals();
				}

				count--;
				return;
			}

			super.processNPC();
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return stage == 2 ? 0.6 : 0;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return stage == 2 ? 0.6 : 0;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return stage == 2 ? 0.6 : 0;
	}

	public void refreshLodestones() {
		for (int i = 0; i < lodestones.length; i++)
			refreshLodestone(i);
	}

	private static final int[] CRYSTAL = { 15752, 15751, 15750 };

	public void refreshLodestone(int index) {

		int id = count == IVULNERABLE_TIMER ? (49274 + lodeStoneType * 2 + index) : lodestones[index] ? lodeStoneType == 0 ? 51099 : lodeStoneType == 1 ? 51601 : 51647 : (49270 + index);

		getManager().spawnObject(getReference(), id, ObjectType.SCENERY_INTERACT, 2, index == 1 ? 10 : 5, 10);
	}

	public void chargeLodeStone(Player player, int index) {
		if (lodestones[index] || count <= 1)
			return;
		if (player.getInventory().containsItem(CRYSTAL[lodeStoneType], 1)) {
			player.lock(1);
			player.setNextAnimation(new Animation(833));
			lodestones[index] = true;
			player.getInventory().deleteItem(CRYSTAL[lodeStoneType], 1);
			player.sendMessage("You place the crystal into the device and it powers up.");
			refreshLodestone(index);
			if (lodestones[0] && lodestones[1])
				for (Entity target : getPossibleTargets())
					if (target instanceof Player p2)
						p2.sendMessage("The lodestone has been fully activated.");
		} else
			player.sendMessage("You need a " + ItemDefinitions.getDefs(CRYSTAL[lodeStoneType]).getName().toLowerCase() + " to activate this lodestone.");

	}

	public void charge() {
		count = IVULNERABLE_TIMER;
		lodeStoneType = Utils.random(3);
		refreshLodestones();
		setNextAnimation(new Animation(13451));
		setNextSpotAnim(new SpotAnim(2407));
		setCantInteract(true);
		for (Entity target : getPossibleTargets())
			if (target instanceof Player player)
				player.sendMessage("Stomp enters a defensive stance. It is currently invulnerable, but no longer protecting the portal's lodestones!");
		destroyExistingDebris();
		for (int count = 0; count < 11; count++)
			l: for (int i = 0; i < DungeonConstants.SET_RESOURCES_MAX_TRY; i++) {
				int x = 3 + Utils.random(12);
				int y = 3 + Utils.random(9);
				if (containsShadow(x, y) || !getManager().isFloorFree(getReference(), x, y))
					continue;
				shadows.add(new int[] { x, y });
				getManager().spawnObject(getReference(), 49269, ObjectType.SCENERY_INTERACT, 0, x, y);
				break l;
			}

		for (int count = 0; count < 2; count++)
			l: for (int i = 0; i < DungeonConstants.SET_RESOURCES_MAX_TRY; i++) {
				int x = 3 + Utils.random(12);
				int y = 3 + Utils.random(9);
				if (containsShadow(x, y) || !getManager().isFloorFree(getReference(), x, y))
					continue;
				getManager().spawnItem(getReference(), new Item(CRYSTAL[lodeStoneType]), x, y);
				break l;
			}
	}

	/*
	 * if wasnt destroyed yet
	 */
	public void destroyExistingDebris() {
		for (int[] s : shadows)
			getManager().removeObject(getReference(), 49269, ObjectType.SCENERY_INTERACT, 0, s[0], s[1]);
		shadows.clear();
	}

	public void removeCrystals() {
		for (GroundItem item : World.getAllGroundItemsInChunkRange(getChunkId(), 2))
			if (item.getId() == CRYSTAL[lodeStoneType])
				World.removeGroundItem(item);
	}

	public boolean containsShadow(int x, int y) {
		for (int[] s : shadows)
			if (s[0] == x && s[1] == y)
				return true;
		return false;
	}

	@Override
	public void sendDeath(final Entity source) {
		if (stage != 3) {
			setHitpoints(1);
			return;
		}
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
				if (source instanceof Player player)
					player.getControllerManager().processNPCDeath(Stomp.this);
				drop();
				reset();
				setCantInteract(true);
				setNextNPCTransformation(9781);
				return false;
			}
			return true;
		});
		getManager().openStairs(getReference());
	}

	/*
	 * @Override public Item sendDrop(Player player, Drop drop) { Item item = new Item(drop.getItemId()); player.getInventory().addItemDrop(item.getId(), item.getAmount()); return item; }
	 */

	@Override
	public void setNextFaceEntity(Entity entity) {
		// this boss doesnt face
	}

	@Override
	public boolean lineOfSightTo(Object tile, boolean checkClose) {
		// because npc is under cliped data
		return getManager().isAtBossRoom(WorldUtil.targetToTile(tile));
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

}
