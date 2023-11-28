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
package com.rs.game.content.skills.dungeoneering.npcs.bosses;

import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

import java.util.List;

public class DungeonBoss extends DungeonNPC {

	private RoomReference reference;

	public DungeonBoss(int id, Tile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager);
		setReference(reference);
		resetBonuses();
		setForceAgressive(true);
		setIntelligentRouteFinder(true);
		setLureDelay(3000);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		getManager().openStairs(getReference());
	}

	@Override
	public void processNPC() {
		super.processNPC();
		resetBonuses();
	}

	@Override
	public void drop() {
		DropTable[] drops = DropSets.getDropSet(getId()).getTables();
		if (drops == null || drops.length == 0)
			return;

		// TODO: Possible drop table mismatch with MEDIUM_DUNGEON?
		DropTable drop;
		if (getManager().getParty().getSize() == DungeonConstants.LARGE_DUNGEON)
			drop = drops[Utils.random(100) < 90 ? drops.length - 1 : Utils.random(drops.length)];
		else if (getManager().getParty().getSize() == DungeonConstants.LARGE_DUNGEON)
			drop = drops[Utils.random(100) < 60 ? drops.length - 1 : Utils.random(drops.length)];
		else
			drop = drops[Utils.random(drops.length)];
		List<Player> players = getManager().getParty().getTeam();
		if (players.isEmpty())
			return;
		Player killer = players.get(Utils.random(players.size()));
		if (drop != null)
			for (Item item : drop.toItemArr())
				sendDrop(killer, item);
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

	public RoomReference getReference() {
		return reference;
	}

	public void setReference(RoomReference reference) {
		this.reference = reference;
	}
}
