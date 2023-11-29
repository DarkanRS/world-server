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
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.List;

public class Sagittare extends DungeonBoss {

	private final int stage;
	private boolean special;

	public Sagittare(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9753, 9766), manager.getBossLevel()), tile, manager, reference);
		setCantFollowUnderCombat(true);
		this.stage = -1;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		//		int max_hp = getMaxHitpoints();
		//		int current_hp = getHitpoints();

		//		if ((current_hp == 1 || current_hp < max_hp * (.25 * stage)) && !special) {
		//			special = true;
		//			stage--;
		//		}
	}

	@Override
	public void processHit(Hit hit) {
		int damage = hit.getDamage();
		if (damage > 0)
			if (hit.getLook() == HitLook.RANGE_DAMAGE)
				hit.setDamage((int) (damage * .4));
		super.processHit(hit);
	}

	public boolean isUsingSpecial() {
		return special;
	}

	public void setUsingSpecial(boolean special) {
		this.special = special;
	}

	public int getStage() {
		return stage;
	}

	@Override
	public void sendDeath(final Entity source) {
		if (stage != -1) {
			setHitpoints(1);
			return;
		}
		super.sendDeath(source);
	}

	@Override
	public void sendDrop(Player player, Item item) {
		List<Player> players = getManager().getParty().getTeam();
		if (players.isEmpty())
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
