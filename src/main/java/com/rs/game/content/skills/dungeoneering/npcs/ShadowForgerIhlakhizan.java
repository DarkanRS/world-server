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

import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.List;

public final class ShadowForgerIhlakhizan extends DungeonBoss {

	public ShadowForgerIhlakhizan(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10143, 10156), manager.getBossLevel()), tile, manager, reference);
		setCantFollowUnderCombat(true); //force cant walk
	}

	@Override
	public void setNextFaceEntity(Entity entity) {
		//this boss doesnt face
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		for (Player player : getManager().getParty().getTeam()) {
			if (!getManager().isAtBossRoom(player.getTile()) || lineOfSightTo(player, false) || player.getTempAttribs().getB("SHADOW_FORGER_SHADOW"))
				continue;
			player.setNextSpotAnim(new SpotAnim(2378));
			player.getTempAttribs().setB("SHADOW_FORGER_SHADOW", true);
			player.applyHit(new Hit(this, Utils.random((int) (player.getMaxHitpoints() * 0.1)) + 1, HitLook.TRUE_DAMAGE));
		}
	}

	public void setUsedShadow() {
		for (Player player : getManager().getParty().getTeam())
			player.getTempAttribs().setB("SHADOW_FORGER_SHADOW", true);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
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
