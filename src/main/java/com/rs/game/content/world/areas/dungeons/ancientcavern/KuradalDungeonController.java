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
package com.rs.game.content.world.areas.dungeons.ancientcavern;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class KuradalDungeonController extends Controller {

	public static ItemClickHandler handleFerociousRings = new ItemClickHandler(Utils.range(15398, 15402), new String[] { "Rub", "Kuradal" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (Magic.sendItemTeleportSpell(e.getPlayer(), true, 9603, 1684, 3, WorldTile.of(1739, 5312, 1)))
				if (e.getItem().getId() == 15402) {
					if (e.isEquipped())
						e.getPlayer().getEquipment().deleteSlot(Equipment.RING);
					else
						e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
				} else {
					e.getItem().setId(e.getItem().getId()+1);
					e.getPlayer().getInventory().refresh();
					e.getPlayer().getEquipment().refresh(Equipment.RING);
				}
		}
	};

	public KuradalDungeonController() {}

	@Override
	public void start() {
		player.setNextWorldTile(WorldTile.of(1661, 5257, 0));
	}

	@Override
	public boolean canAttack(Entity target) {
		if (player.getSlayer().isOnTaskAgainst((NPC)target))
			return true;
		player.sendMessage("This creature is not assigned to you.");
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (player.getSlayer().isOnTaskAgainst((NPC)target))
			return true;
		player.sendMessage("This creature is not assigned to you.");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 47231) {
			player.setNextWorldTile(WorldTile.of(1735, 5313, 1));
			removeController();
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
	}

	@Override
	public void moved() {
		if (player.getRegionId() != 6482)
			removeController();
	}

	@Override
	public void processOutgoingHit(Hit hit, Entity target) {
		if (target != player && hit.getDamage() > 0 && player.getEquipment().getRingId() >= 15398 && player.getEquipment().getRingId() <= 15402)
			hit.setDamage(hit.getDamage()+40);
	}

	@Override
	public void processNPCDeath(NPC npc) {
		if (!player.getSlayer().isOnTaskAgainst(npc))
			return;
		if (Utils.random(128) == 0)
			npc.sendDrop(player, new Item(15398));
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

}
