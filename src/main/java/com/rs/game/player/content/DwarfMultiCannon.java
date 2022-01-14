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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content;

import java.util.Set;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.object.OwnedObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.WildernessController;
import com.rs.game.player.quests.Quest;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DwarfMultiCannon extends OwnedObject {

	public static int[][] CANNON_PIECES = { { 6, 8, 10, 12 }, { 20494, 20495, 20496, 20497 }, { 20498, 20499, 20500, 20501 } };
	private static int[][] CANNON_OBJECTS = { { 7, 8, 9, 6 }, { 29398, 29401, 29402, 29406 }, { 29403, 29404, 29405, 29408 } };
	private static int[] CANNON_EMOTES = { 303, 305, 307, 289, 184, 182, 178, 291 };

	private int type;
	private int balls = 0;
	private int decay = 0;
	private int spinRot = 0;

	public DwarfMultiCannon(Player player, WorldTile tile, int type) {
		super(player, CANNON_OBJECTS[type][0], ObjectType.SCENERY_INTERACT, 0, tile);
		this.type = type;
	}

	public static ItemClickHandler handlePlace = new ItemClickHandler(new Object[] { 6, 20494, 20498 }, new String[] { "Set-up" }) {
		@Override
		public void handle(ItemClickEvent e) {
			setUp(e.getPlayer(), e.getItem().getId() == 6 ? 0 : e.getItem().getId() == 20494 ? 1 : 2);
		}
	};

	public static boolean canFreelyReplace(Player player) {
		return player.getPlacedCannon() > 0 && OwnedObject.getNumOwned(player, DwarfMultiCannon.class) == 0;
	}

	public static void setUp(Player player, int type) {
		if (!player.getQuestManager().isComplete(Quest.DWARF_CANNON)) {
			player.sendMessage("You have no idea how to operate this machine.");
			return;
		}
		if (OwnedObject.getNumOwned(player, DwarfMultiCannon.class) > 0) {
			player.sendMessage("You already have a cannon placed.");
			return;
		}
		Controller controller = player.getControllerManager().getController();
		if (controller != null && !(controller instanceof WildernessController)) {
			player.sendMessage("You can't place your cannon here.");
			return;
		}
		int count = 0;
		for (int item : CANNON_PIECES[type]) {
			if (!player.getInventory().containsItem(item, 1))
				break;
			count++;
		}
		if (count < 4) {
			player.sendMessage("You don't have all your cannon parts.");
			return;
		}
		WorldTile pos = player.transform(-2, -3, 0);
		if (!World.floorAndWallsFree(pos, 3) || World.getObject(pos, ObjectType.SCENERY_INTERACT) != null) {
			player.sendMessage("There isn't enough space to set up here.");
			return;
		}
		player.lock();
		player.setNextFaceWorldTile(pos);
		DwarfMultiCannon cannon = new DwarfMultiCannon(player, pos, type);
		WorldTasks.schedule(new WorldTask() {
			int stage = 0;

			@Override
			public void run() {
				player.setNextAnimation(new Animation(827));
				if (stage == 0) {
					player.sendMessage("You place the cannon base on the ground.");
					cannon.createNoReplace();
					player.getInventory().deleteItem(CANNON_PIECES[type][0], 1);
				} else if (stage == 2) {
					player.sendMessage("You add the stand.");
					cannon.setId(CANNON_OBJECTS[type][1]);
					player.getInventory().deleteItem(CANNON_PIECES[type][1], 1);
				} else if (stage == 4) {
					player.sendMessage("You add the barrels.");
					cannon.setId(CANNON_OBJECTS[type][2]);
					player.getInventory().deleteItem(CANNON_PIECES[type][2], 1);
				} else if (stage == 6) {
					player.sendMessage("You add the furnance.");
					cannon.setId(CANNON_OBJECTS[type][3]);
					player.getInventory().deleteItem(CANNON_PIECES[type][3], 1);
					player.setPlacedCannon(type+1);
				} else if (stage == 8) {
					player.unlock();
					stop();
					return;
				}
				stage++;
			}
		}, 0, 0);
	}

	public static ObjectClickHandler handleOptions = new ObjectClickHandler(new Object[] { "Dwarf multicannon", "Gold dwarf multicannon", "Royale dwarf multicannon" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!(e.getObject() instanceof DwarfMultiCannon))
				return;
			DwarfMultiCannon cannon = (DwarfMultiCannon) e.getObject();
			if (e.getOption().equals("Fire"))
				cannon.fire(e.getPlayer());
			else if (e.getOption().equals("Pick-up"))
				cannon.pickUp(e.getPlayer(), e.getObject());
		}
	};

	public void fire(Player player) {
		if (!ownedBy(player)) {
			player.sendMessage("This is not your cannon.");
			return;
		}
		if (balls < 30) {
			int amount = player.getInventory().getAmountOf(2);
			if (amount == 0)
				player.sendMessage("You need to load your cannon with cannon balls before firing it!");
			else {
				int add = 30 - balls;
				if (amount > add)
					amount = add;
				balls += amount;
				player.getInventory().deleteItem(2, amount);
				player.sendMessage("You load the cannon with " + amount + " cannon balls.");
			}
		} else
			player.sendMessage("Your cannon is full.");
	}

	public void pickUp(Player player, GameObject object) {
		if (!ownedBy(player)) {
			player.sendMessage("This is not your cannon.");
			return;
		}
		int space = balls > 0 ? 5 : 4;
		if (player.getInventory().getFreeSlots() < space) {
			player.sendMessage("You need at least " + space + " inventory spaces to pick up your cannon.");
			return;
		}
		player.sendMessage("You pick up the cannon. It's really heavy.");
		for (int i = 0; i < CANNON_PIECES[type].length; i++)
			player.getInventory().addItem(CANNON_PIECES[type][i], 1);
		if (balls > 0) {
			player.getInventory().addItem(2, balls);
			balls = 0;
		}
		player.setPlacedCannon(0);
		destroy();
	}

	@Override
	public void tick(Player owner) {
		if (owner == null) {
			decay++;
			if (decay >= 200)
				destroy();
			return;
		}
		if (id != CANNON_OBJECTS[type][3] || balls == 0)
			return;
		spinRot++;
		if (spinRot == CANNON_EMOTES.length)
			spinRot = 0;
		World.sendObjectAnimation(null, this, new Animation(CANNON_EMOTES[spinRot]));
		Set<Integer> npcIndexes = World.getRegion(getRegionId()).getNPCsIndexes();
		if (npcIndexes == null)
			return;
		for (int npcIndex : npcIndexes) {
			NPC npc = World.getNPCs().get(npcIndex);
			if (npc == null || npc == owner.getFamiliar() || npc.isDead() || npc.hasFinished() || !npc.getDefinitions().hasAttackOption() || !owner.getControllerManager().canHit(npc))
				continue;
			if (!owner.lineOfSightTo(npc, true) || (!owner.isAtMultiArea() && owner.inCombat() && owner.getAttackedBy() != npc))
				continue;
			if (!owner.isAtMultiArea() && npc.getAttackedBy() != owner && npc.inCombat())
				continue;
			int distanceX = npc.getX() - getX() + 1;
			int distanceY = npc.getY() - getY() + 1;

			boolean hit = false;
			switch (spinRot) {
			case 0: // North
				if ((distanceY <= 8 && distanceY >= 0) && (distanceX >= -1 && distanceX <= 1))
					hit = true;
				break;
			case 1: // North East
				if ((distanceY <= 8 && distanceY >= 0) && (distanceX <= 8 && distanceX >= 0))
					hit = true;
				break;
			case 2: // East
				if ((distanceY <= 1 && distanceY >= -1) && (distanceX <= 8 && distanceX >= 0))
					hit = true;
				break;
			case 3: // South East
				if ((distanceY >= -8 && distanceY <= 0) && (distanceX <= 8 && distanceX >= 0))
					hit = true;
				break;
			case 4: // South
				if ((distanceY >= -8 && distanceY <= 0) && (distanceX <= 1 && distanceX >= -1))
					hit = true;
				break;
			case 5: // South West
				if ((distanceY >= -8 && distanceY <= 0) && (distanceX >= -8 && distanceX <= 0))
					hit = true;
				break;
			case 6: // West
				if ((distanceY >= -1 && distanceY <= 1) && (distanceX >= -8 && distanceX <= 0))
					hit = true;
				break;
			case 7: // North West
				if ((distanceY <= 8 && distanceY >= 0) && (distanceX >= -8 && distanceX <= 0))
					hit = true;
				break;
			default:
				hit = false;
				break;
			}

			if (hit) {
				int damage = PlayerCombat.getRandomMaxHit(owner, npc, 300, owner.getEquipment().getWeaponId(), owner.getCombatDefinitions().getAttackStyle(), PlayerCombat.isRanging(owner), true, 1.0);
				World.sendProjectile(new WorldTile(getX() + 1, getY() + 1, getPlane()), npc, 53, 38, 38, 30, 1, 0, 0);
				npc.applyHit(new Hit(owner, damage, HitLook.CANNON_DAMAGE));
				owner.getSkills().addXp(Constants.RANGE, damage / 5);
				balls--;
				npc.setTarget(owner);
				npc.setAttackedBy(owner);
				break;
			}
		}
	}
}