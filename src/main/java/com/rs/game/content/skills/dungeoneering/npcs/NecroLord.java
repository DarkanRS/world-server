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
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class NecroLord extends DungeonBoss {

	private int resetTicks;
	private List<SkeletalMinion> skeletons;

	public NecroLord(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(11737, 11751), manager.getBossLevel()), tile, manager, reference);
		setCantFollowUnderCombat(true); //force can't walk
		setLureDelay(Integer.MAX_VALUE);//doesn't stop focusing on target
		skeletons = new CopyOnWriteArrayList<>();
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!isUnderCombat() && skeletons != null && skeletons.size() > 0) {
			resetTicks++;
			if (resetTicks == 50) {
				resetSkeletons();
				resetTicks = 0;
				return;
			}
		}
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	public void addSkeleton(Tile tile) {
		SkeletalMinion npc = new SkeletalMinion(this, 11722, tile, getManager()); //TODO scale to level
		npc.setForceAgressive(true);
		skeletons.add(npc);
		World.sendSpotAnim(tile, new SpotAnim(2399));
	}

	public void resetSkeletons() {
		for (SkeletalMinion skeleton : skeletons)
			skeleton.sendDeath(this);
		skeletons.clear();
	}

	public void removeSkeleton(DungeonNPC sk) {
		skeletons.remove(sk);
	}

	/*
	 * because necrolord room has a safespot which shouldnt
	 */
	@Override
	public boolean lineOfSightTo(Object tile, boolean checkClose) {
		//because npc is under cliped data
		return getManager().isAtBossRoom(WorldUtil.targetToTile(tile));
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		resetSkeletons();
	}

	@Override
	public void sendDrop(Player player, Item item) {
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		player.getInventory().addItemDrop(item);
		player.sendMessage("<col=D2691E>You received: " + item.getAmount() + " " + item.getName() + ".");
		for (Player p2 : players) {
			if (p2 == player)
				continue;
			p2.sendMessage("<col=D2691E>" + player.getDisplayName() + " received: " + item.getAmount() + " " + item.getName() + ".");
		}
	}
}
