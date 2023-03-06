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
package com.rs.game.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.engine.quest.Quest;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.OwnedObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Vec2;
import com.rs.plugin.annotations.PluginEventHandler;
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
	private Direction spinRot = Direction.NORTH;

	private enum BannedArea {
		ABYSS(12108, 12109),
		ANCIENT_CAVERN(6995, 6994, 6738, 6482),
		AL_KHARID_PALACE(13105),
		BLACK_GUARD("It is not permitted to set up a cannon this close to the Dwarf Black Guard.", 11830, 11829, 12085, 12086),
		DWARVEN_MINE(11929, 12185, 12184),
		ENCHANTED_VALLEY(12102),
		ENTRANA(11060, 11316),
		FREM_SLAYER_DUNG("The air is too dank for you to set up a cannon here.", 11164, 10908, 10907),
		FELDIP_HILLS(10029),
		GRAND_EXCHANGE("The Grand Exchange staff prefer not to have heavy artillery operated around their premises.", 12598),
		KILLERWATT_PLANE("The electricity bursting through this plane would render the cannon useless.", 10577),
		TARNS_LAIR("This temple is ancient and would probably collapse if you started firing a cannon.", 12615, 12616),
		MORT_MYRE_SWAMP(13621, 13877, 14133, 13620, 13876, 13619, 13875, 13874, 13618, 13363, 14131, 14130),
		OURIANA_ALTAR(13131),
		SLAYER_TOWER(13623),
		REVENANT_CAVE(12446),
		WARRIORS_GUILD(11319),
		WYVERN_CAVE(12181),
		KALPHITE_QUEEN(13972),
		KING_BLACK_DRAGON(9033);

		private static Map<Integer, BannedArea> MAP = new HashMap<>();

		static {
			for (BannedArea area : values()) {
				for (int regionId : area.regionIds)
					MAP.put(regionId, area);
			}
		}

		private String message;
		private int[] regionIds;

		BannedArea(String message, int... regionIds) {
			this.message = message == null ? "It is not permitted to set up a cannon here." : message;
			this.regionIds = regionIds;
		}

		BannedArea(int... regionIds) {
			this(null, regionIds);
		}
	}

	public DwarfMultiCannon(Player player, Tile tile, int type) {
		super(player, CANNON_OBJECTS[type][0], ObjectType.SCENERY_INTERACT, 0, tile);
		this.type = type;
	}

	public static ItemClickHandler handlePlace = new ItemClickHandler(new Object[] { 6, 20494, 20498 }, new String[] { "Set-up" }, e -> {
		setUp(e.getPlayer(), e.getItem().getId() == 6 ? 0 : e.getItem().getId() == 20494 ? 1 : 2);
	});

	public static boolean canFreelyReplace(Player player) {
		return player.getPlacedCannon() > 0 && OwnedObject.getNumOwned(player, DwarfMultiCannon.class) == 0;
	}

	public static void setUp(Player player, int type) {
		if (!player.isQuestComplete(Quest.DWARF_CANNON)) {
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
		BannedArea area = BannedArea.MAP.get(player.getRegionId());
		if (area != null) {
			player.sendMessage(area.message);
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
		Tile pos = player.transform(-2, -3, 0);
		if (!World.floorAndWallsFree(pos, 3) || World.getObject(pos, ObjectType.SCENERY_INTERACT) != null) {
			player.sendMessage("There isn't enough space to set up here.");
			return;
		}
		player.lock();
		player.setNextFaceTile(pos);
		DwarfMultiCannon cannon = new DwarfMultiCannon(player, pos, type);
		WorldTasks.scheduleTimer(0, 0, stage -> {
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
				return false;
			}
			return true;
		});
	}

	public static ObjectClickHandler handleOptions = new ObjectClickHandler(new Object[] { "Dwarf multicannon", "Gold dwarf multicannon", "Royale dwarf multicannon" }, e -> {
		if (!(e.getObject() instanceof DwarfMultiCannon))
			return;
		DwarfMultiCannon cannon = (DwarfMultiCannon) e.getObject();
		if (e.getOption().equals("Fire"))
			cannon.fire(e.getPlayer());
		else if (e.getOption().equals("Pick-up"))
			cannon.pickUp(e.getPlayer(), e.getObject());
	});
	
	public int getMaxBalls() {
		return switch(type) {
			case 2 -> 100;
			case 1 -> 50;
			default -> 30;
		};
	}

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
		if (spinRot.ordinal() + 1 == Direction.values().length)
			spinRot = Direction.NORTH;
		else
			spinRot = Direction.values()[spinRot.ordinal() + 1];
		World.sendObjectAnimation(this, new Animation(CANNON_EMOTES[spinRot.ordinal()]));
		Tile cannonTile = this.tile.transform(1, 1, 0);
		for (NPC npc : World.getNPCsInChunkRange(cannonTile.getChunkId(), 2)) {
			if (npc == owner.getFamiliar() || npc.isDead() || !npc.getDefinitions().hasAttackOption() || !owner.getControllerManager().canHit(npc))
				continue;
			if (!npc.lineOfSightTo(cannonTile, false) || (!owner.isAtMultiArea() && owner.inCombat() && owner.getAttackedBy() != npc))
				continue;
			if (!owner.isAtMultiArea() && npc.getAttackedBy() != owner && npc.inCombat())
				continue;

			if (npc.withinDistance(cannonTile, 10) && getDirectionTo(npc) == spinRot) {
				Hit hit = PlayerCombat.calculateHit(owner, npc, 0, 300, owner.getEquipment().getWeaponId(), owner.getCombatDefinitions().getAttackStyle(), PlayerCombat.isRanging(owner), true, 1.0);
				WorldProjectile proj = World.sendProjectile(Tile.of(getX() + 1, getY() + 1, getPlane()), npc, 53, 38, 38, 30, 1, 0, 0);
				WorldTasks.schedule(proj.getTaskDelay(), () -> npc.applyHit(new Hit(owner, hit.getDamage(), HitLook.CANNON_DAMAGE)));
				owner.getSkills().addXp(Constants.RANGE, hit.getDamage() / 5);
				balls--;
				npc.setTarget(owner);
				npc.setAttackedBy(owner);
				break;
			}
		}
	}

	public Direction getDirectionTo(Entity entity) {
		Vec2 to = entity.getMiddleTileAsVector();
		Vec2 from = new Vec2(tile.transform(1, 1, 0));
		Vec2 sub = to.sub(from);
		sub.norm();
		Tile delta = sub.toTile();
		return Direction.forDelta(delta.getX(), delta.getY());
	}
}