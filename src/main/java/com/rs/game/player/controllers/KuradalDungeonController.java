package com.rs.game.player.controllers;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Equipment;
import com.rs.game.player.content.skills.magic.Magic;
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
			if (Magic.sendItemTeleportSpell(e.getPlayer(), true, 9603, 1684, 3, new WorldTile(1739, 5312, 1))) {
				if (e.getItem().getId() == 15402) {
					if (e.isEquipped()) {
						e.getPlayer().getEquipment().set(Equipment.RING, null);
						e.getPlayer().getEquipment().refresh(Equipment.RING);
					} else {
						e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
					}
				} else {
					e.getItem().setId(e.getItem().getId()+1);
					e.getPlayer().getInventory().refresh();
					e.getPlayer().getEquipment().refresh(Equipment.RING);
				}
			}
		}
	};
	
	public KuradalDungeonController() {}
	
	@Override
	public void start() {
		player.setNextWorldTile(new WorldTile(1661, 5257, 0));
	}
	
	@Override
	public boolean canAttack(Entity target) {
		if (player.getSlayer().isOnTaskAgainst((NPC)target)) {
			return true;
		}
		player.sendMessage("This creature is not assigned to you.");
		return false;
	}
	
	@Override
	public boolean keepCombating(Entity target) {
		if (player.getSlayer().isOnTaskAgainst((NPC)target)) {
			return true;
		}
		player.sendMessage("This creature is not assigned to you.");
		return false;
	}
	
	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 47231) {
			player.setNextWorldTile(new WorldTile(1735, 5313, 1));
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
		if (player.getRegionId() != 6482) {
			removeController();
		}
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
