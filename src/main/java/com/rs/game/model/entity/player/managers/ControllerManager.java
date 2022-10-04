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
package com.rs.game.model.entity.player.managers;

import java.lang.SuppressWarnings;

import com.rs.game.content.Potions.Potion;
import com.rs.game.content.skills.cooking.Foods.Food;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;

public final class ControllerManager {

	private transient Player player;
	private transient boolean inited;

	private Controller controller;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Controller getController() {
		return controller;
	}

	public void startController(Controller controller) {
		if (controller != null)
			forceStop();
		this.controller = controller;
		if (controller == null)
			return;
		controller.setPlayer(player);
		controller.start();
		inited = true;
	}

	public void login() {
		if (controller == null) {
			forceStop();
			return;
		}
		controller.setPlayer(player);
		if (controller.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controller == null)
			return;
		if (controller.logout())
			forceStop();
	}

	public boolean canMove(Direction dir) {
		if (controller == null || !inited)
			return true;
		return controller.canMove(dir);
	}

	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (controller == null || !inited)
			return true;
		return controller.checkWalkStep(lastX, lastY, nextX, nextY);
	}

	public boolean keepCombating(Entity target) {
		if (controller == null || !inited)
			return true;
		return controller.keepCombating(target);
	}

	public boolean canEquip(int slotId, int itemId) {
		if (controller == null || !inited)
			return true;
		return controller.canEquip(slotId, itemId);
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canAddInventoryItem(itemId, amount);
	}

	public void trackXP(int skillId, int addedXp) {
		if (controller == null || !inited)
			return;
		controller.trackXP(skillId, addedXp);
	}

	public boolean gainXP(int skillId, double exp) {
		if (controller == null || !inited)
			return true;
		return controller.gainXP(skillId, exp);
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canDeleteInventoryItem(itemId, amount);
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (controller == null || !inited)
			return true;
		return controller.canUseItemOnItem(itemUsed, usedWith);
	}

	public boolean canAttack(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canAttack(entity);
	}

	public boolean canPlayerOption1(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption1(target);
	}

	public boolean canPlayerOption2(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption2(target);
	}

	public boolean canPlayerOption3(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption3(target);
	}

	public boolean canPlayerOption4(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption4(target);
	}

	public boolean canHit(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canHit(entity);
	}

	public void moved() {
		if (controller == null || !inited)
			return;
		controller.moved();
	}

	public boolean canTakeItem(GroundItem item) {
		if (controller == null || !inited)
			return true;
		return controller.canTakeItem(item);
	}

	public void processNPCDeath(int id) {
		if (controller == null || !inited)
			return;
		controller.processNPCDeath(id);
	}

	public void processOutgoingHit(Hit hit, Entity target) {
		if (controller == null || !inited)
			return;
		controller.processOutgoingHit(hit, target);
	}

	public void processIncomingHit(Hit hit) {
		if (controller == null || !inited)
			return;
		controller.processIncomingHit(hit);
	}

	public void processNPCDeath(NPC id) {
		if (controller == null || !inited)
			return;
		controller.processNPCDeath(id.getId());
		controller.processNPCDeath(id);
	}

	public void magicTeleported(int type) {
		if (controller == null || !inited)
			return;
		controller.magicTeleported(type);
	}

	public void sendInterfaces() {
		if (controller == null || !inited)
			return;
		controller.sendInterfaces();
	}

	public void process() {
		if (controller == null || !inited)
			return;
		controller.process();
	}

	public boolean sendDeath() {
		if (controller == null || !inited)
			return true;
		return controller.sendDeath();
	}

	public boolean canEat(Food food) {
		if (controller == null || !inited)
			return true;
		return controller.canEat(food);
	}

	public boolean canPot(Potion pot) {
		if (controller == null || !inited)
			return true;
		return controller.canPot(pot);
	}

	public boolean useDialogueScript(Object key) {
		if (controller == null || !inited)
			return true;
		return controller.useDialogueScript(key);
	}

	public boolean processMagicTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processMagicTeleport(toTile);
	}

	public boolean processItemTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processItemTeleport(toTile);
	}

	public boolean processObjectTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectTeleport(toTile);
	}

	public boolean processObjectClick1(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick1(object);
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (controller == null || !inited)
			return true;
		return controller.processButtonClick(interfaceId, componentId, slotId, slotId2, packet);
	}

	public boolean processNPCClick1(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick1(npc);
	}

	public boolean canSummonFamiliar() {
		if (controller == null || !inited)
			return true;
		return controller.canSummonFamiliar();
	}

	public boolean processNPCClick2(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick2(npc);
	}

	public boolean processNPCClick3(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick3(npc);
	}

	public boolean processObjectClick2(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick2(object);
	}

	public boolean processObjectClick3(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick3(object);
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnNPC(npc, item);
	}

	public boolean canDropItem(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.canDropItem(item);
	}

	public void forceStop() {
		if (controller != null) {
			controller.onRemove();
			controller.forceClose();
			controller = null;
		}
		inited = false;
	}

	public void removeControllerWithoutCheck() {
		if (controller != null)
			controller.onRemove();
		controller = null;
		inited = false;
	}

	public boolean processObjectClick4(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick4(object);
	}

	public boolean processObjectClick5(GameObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick5(object);
	}

	public boolean processItemOnPlayer(Player p2, Item item, int slot) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnPlayer(p2, item, slot);
	}

	public boolean handleItemOnObject(GameObject object, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnObject(object, item);
	}

	public boolean canTrade() {
		if (controller == null || !inited)
			return true;
		return controller.canTrade();
	}

	public boolean isIn(Class<?> type) {
		if (controller == null)
			return false;
		return controller.getClass().isAssignableFrom(type);
	}
	
	/**
	 * Very unsafe to use this. Do not please.
	 * @param controller
	 */
	@Deprecated
	public void setController(Controller controller) {
		this.controller = controller;
	}

	@SuppressWarnings("unchecked")
	public <T extends Controller> T getController(Class<T> clazz) {
		if (controller == null || !controller.getClass().isAssignableFrom(clazz))
			return null;
		return (T) controller;
	}
}
