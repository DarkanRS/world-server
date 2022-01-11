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
package com.rs.game.player.content.skills.firemaking;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.controllers.DuelArenaController;
import com.rs.game.player.controllers.DuelController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;

public class Firemaking extends Action {

	public static enum Fire {
		NORMAL(1511, 1, 300, 70755, 40, 20),
		ACHEY(2862, 1, 300, 70756, 40, 1),
		OAK(1521, 15, 450, 70757, 60, 1),
		WILLOW(1519, 30, 450, 70758, 90, 1),
		TEAK(6333, 35, 450, 70759, 105, 1),
		ARCTIC_PINE(10810, 42, 500, 70760, 125, 1),
		MAPLE(1517, 45, 500, 70761, 135, 1),
		MAHOGANY(6332, 50, 700, 70762, 157.5, 1),
		EUCALYPTUS(12581, 58, 700, 70763, 193.5, 1),
		YEW(1515, 60, 800, 70764, 202.5, 1),
		MAGIC(1513, 75, 900, 70765, 303.8, 1),
		CURSED_MAGIC(13567, 82, 1000, 70766, 303.8, 1),

		TANGLE_GUM_BRANCHES(17682, 1, 300, 49940, 25, 1),
		SEEPING_ELM_BRANCHES(17684, 10, 375, 49941, 44.5, 1),
		BLOOD_SPINDLE_BRANCHES(17686, 20, 410, 49942, 65.6, 1),
		UTUKU_BRANCHES(17688, 30, 450, 49943, 88.3, 1),
		SPINEBEAM_BRANCHES(17690, 40, 500, 49944, 112.6, 1),
		BOVISTRANGLER_BRANCHES(17692, 50, 700, 49945, 138.5, 1),
		THIGAT_BRANCHES(17694, 60, 700, 49946, 166, 1),
		CORPSETHRON_BRANCHES(17696, 70, 850, 49947, 195.1, 1),
		ENTGALLOW_BRANCHES(17698, 80, 925, 49948, 225.8, 1),
		GRAVE_CREEPER_BRANCHES(17700, 90, 1000, 49949, 258.1, 1);

		private int logId;
		private int level;
		private int life;
		private int fireId;
		private int time;
		private double xp;

		Fire(int logId, int level, int life, int fireId, double xp, int time) {
			this.logId = logId;
			this.level = level;
			this.life = life;
			this.fireId = fireId;
			this.xp = xp;
			this.time = time;
		}

		public static Fire forId(int logId) {
			for (Fire fire : Fire.values())
				if (fire.getLogId() == logId)
					return fire;
			return null;
		}

		public int getLogId() {
			return logId;
		}

		public int getLevel() {
			return level;
		}

		public int getLife() {
			return life;
		}

		public int getFireId() {
			return fireId;
		}

		public double getExperience() {
			return xp;
		}

		public int getTime() {
			return time;
		}
	}

	private Fire fire;
	private GroundItem groundItem;

	public Firemaking(Fire fire) {
		this(fire, null);
	}

	public Firemaking(Fire fire, GroundItem groundItem) {
		this.fire = fire;
		this.groundItem = groundItem;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.resetWalkSteps();
		player.sendMessage("You attempt to light the logs.", true);
		if (groundItem == null) {
			player.getInventory().deleteItem(fire.getLogId(), 1);
			World.addGroundItem(new Item(fire.getLogId(), 1), new WorldTile(player), player, true, 180);
		}
		boolean quickFire = player.getTempAttribs().removeL("Fire") > System.currentTimeMillis();
		setActionDelay(player, quickFire ? 1 : 2);
		if (!quickFire)
			player.setNextAnimation(new Animation(16700));
		return true;
	}

	public static boolean isFiremaking(Player player, Item item1, Item item2) {
		Item log = InventoryOptionsHandler.contains(590, item1, item2);
		if (log == null)
			return false;
		return isFiremaking(player, log.getId());
	}

	public static boolean isFiremaking(Player player, int logId) {
		for (Fire fire : Fire.values())
			if (fire.getLogId() == logId) {
				player.getActionManager().setAction(new Firemaking(fire));
				return true;
			}
		return false;

	}

	public static void startFamiliarFire(Player player, Familiar familiar, Fire fire) {
		if (player.getFamiliar().getId() == 7378 || player.getFamiliar().getId() == 7377) {
		}
	}

	public boolean checkAll(Player player) {
		if (!player.getInventory().containsItem(590, 1) && (fire.ordinal() >= Fire.TANGLE_GUM_BRANCHES.ordinal() && !player.getInventory().containsItem(DungeonConstants.TINDERBOX))) {
			player.sendMessage("You do not have the required items to light this.");
			return false;
		}
		if (player.getSkills().getLevel(Constants.FIREMAKING) < fire.getLevel()) {
			player.sendMessage("You do not have the required level to light this.");
			return false;
		}
		if (!World.canLightFire(player.getPlane(), player.getX(), player.getY()) || World.getRegion(player.getRegionId()).getSpawnedObject(player) != null || player.getControllerManager().getController() instanceof DuelArenaController || player.getControllerManager().getController() instanceof DuelController) { // contains
			player.sendMessage("You can't light a fire here.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	public static double increasedExperience(Player player, double totalXp) {
		if (player.getEquipment().getGlovesId() == 13660)
			totalXp *= 1.025;
		if (player.getEquipment().getRingId() == 13659)
			totalXp *= 1.025;
		return totalXp;
	}

	@Override
	public int processWithDelay(final Player player) {
		final WorldTile tile = new WorldTile(player);
		if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
			if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
				if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
					player.addWalkSteps(player.getX(), player.getY() - 1, 1);
		player.sendMessage("The fire catches and the logs begin to burn.", true);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				final GroundItem item = groundItem != null ? groundItem : World.getRegion(tile.getRegionId()).getGroundItem(fire.getLogId(), tile, player);
				if ((item == null) || !World.removeGroundItem(player, item, false))
					return;
				World.spawnTempGroundObject(new GameObject(fire.getFireId(), ObjectType.SCENERY_INTERACT, 0, tile.getX(), tile.getY(), tile.getPlane()), 592, fire.getLife());
				player.getSkills().addXp(Constants.FIREMAKING, increasedExperience(player, fire.getExperience()));
				player.setNextFaceWorldTile(tile);
			}
		}, 1);
		player.getTempAttribs().setL("Fire", System.currentTimeMillis() + 1800);
		return -1;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

}
