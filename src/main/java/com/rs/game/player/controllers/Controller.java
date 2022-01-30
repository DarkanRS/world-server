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
package com.rs.game.player.controllers;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.Potions.Potion;
import com.rs.game.player.content.skills.cooking.Foods.Food;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

public abstract class Controller {

	protected transient Player player;

	public final void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public final void removeController() {
		player.getControllerManager().removeControllerWithoutCheck();
	}

	public abstract void start();

	public boolean canEat(Food food) {
		return true;
	}

	public boolean canTakeItem(GroundItem item) {
		return true;
	}

	public void processIncomingHit(final Hit hit) {

	}

    public boolean playAmbientMusic() {
        return true;
    }

    public Genre getGenre() {
        return Music.getGenre(player.getRegionId());
    }

    public boolean playMusicOnRegionEnter() {
        return true;
    }

	public void processOutgoingHit(final Hit hit, Entity target) {

	}

	public boolean processItemOnPlayer(Player p2, Item item, int slot) {
		return true;
	}

	public boolean canPot(Potion pot) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you attacking
	 */
	public boolean keepCombating(Entity target) {
		return true;
	}

	public boolean canEquip(int slotId, int itemId) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you start trying
	 * to attack
	 */
	public boolean canAttack(Entity target) {
		return true;
	}

	public void trackXP(int skillId, int addedXp) {

	}

	public boolean gainXP(int skillId, double exp) {
		return true;
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		return true;
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canPlayerOption1(Player target) {
		return true;
	}

	public boolean canPlayerOption2(Player target) {
		return true;
	}

	public boolean canPlayerOption3(Player target) {
		return true;
	}

	public boolean canPlayerOption4(Player target) {
		return true;
	}

	public void processNPCDeath(int id) {

	}

	/**
	 * hits as ice barrage and that on multi areas
	 */
	public boolean canHit(Entity entity) {
		return true;
	}

	/**
	 * processes every game ticket, usualy not used
	 */
	public void process() {

	}

	public void moved() {

	}

	/**
	 * called once teleport is performed
	 */
	public void magicTeleported(int type) {

	}

	public void sendInterfaces() {

	}

	/**
	 * return can use script
	 */
	public boolean useDialogueScript(Object key) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processItemTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processObjectTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick1(GameObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick2(GameObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick3(GameObject object) {
		return true;
	}

	public boolean processObjectClick5(GameObject object) {
		return true;
	}

	/**
	 * return let default death
	 */
	public boolean sendDeath() {
		return true;
	}

	/**
	 * return can move that step
	 */
	public boolean canMove(Direction dir) {
		return true;
	}

	/**
	 * return can set that step
	 */
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return true;
	}

	/**
	 * return remove controller
	 */
	public boolean login() {
		return true;
	}

	/**
	 * return remove controller
	 */
	public boolean logout() {
		return true;
	}

	public void forceClose() {
	}

	public boolean processItemOnObject(GameObject object, Item item) {
		return true;
	}
	public boolean processObjectClick4(GameObject object) {// TODO implement
		return true;
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}

	public boolean canDropItem(Item item) {
		return true;
	}

	public boolean canSummonFamiliar() {
		return true;
	}

	public void processNPCDeath(NPC npc) {

	}

	public boolean canTrade() {
		return true;
	}
}
