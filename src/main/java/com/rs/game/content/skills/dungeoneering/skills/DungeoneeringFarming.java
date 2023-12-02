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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class DungeoneeringFarming {

	public enum Harvest {
		POTATO(17823, 1, 1.3, 17817),
		GISSEL(17824, 34, 3.5, 17819),
		EDICAP(17825, 68, 5.7, 17821),
		SAGEWORT(17826, 7, 15, 17494),
		VALERIAN(17827, 18, 18.1, 17496),
		ALOE(17828, 29, 21.9, 17498),
		WORMWOOD(17829, 40, 27.1, 17500),
		MAGEBANE(17830, 51, 34.4, 17502),
		FEATHERFOIL(17831, 62, 44.5, 17504),
		WINTERS_GRIP(17832, 73, 58.1, 17506),
		LYCOPUS(17833, 84, 75.9, 17508),
		BUCKTHORN(17834, 95, 98.6, 17510),
		SALVE_NETTLES(1, 6.1, 17448),
		WILDERCRESS(10, 9.2, 17450),
		BLIGHTLEAF(20, 12.8, 17452),
		ROSEBLOOD(30, 17.4, 17454),
		BRYLL(40, 23.5, 17456),
		DUSKWEED(50, 31.6, 17458),
		SOULBELL(60, 42.2, 17460),
		ECTOGRASS(70, 55.8, 17462),
		RUNELEAF(80, 72.9, 17464),
		SPIRITBLOOM(90, 94, 17466);

		private final int seed, lvl, product;
		private final double exp;

		Harvest(int seed, int lvl, double exp, int product) {
			this.seed = seed;
			this.lvl = lvl;
			this.exp = exp;
			this.product = product;
		}

		Harvest(int lvl, double exp, int product) {
			this(-1, lvl, exp, product);
		}

		public int getSeed() {
			return seed;
		}

		public int getLvl() {
			return lvl;
		}

		public double getExp() {
			return exp;
		}

		public int getProduct() {
			return product;
		}

		public boolean isTextile() {
			return seed == -1;
		}

		public static Harvest forSeed(int id) {
			for (Harvest harvest : Harvest.values())
				if (harvest.seed == id)
					return harvest;
			return null;
		}
	}

	public static int getHerbForLevel(int level) {
		for (int i = 10; i < Harvest.values().length; i++)
			if (Harvest.values()[i].lvl == level)
				return Harvest.values()[i].product;
		return 17448;
	}

	public static void initHarvest(final Player player, final Harvest harvest, final GameObject object) {
		int harvestCount = player.getTempAttribs().getI("HARVEST_COUNT");
		final boolean isTextile = harvest.isTextile();
		final String productName = ItemDefinitions.getDefs(harvest.product).getName().toLowerCase();

		DungFarmPatch patch = null;
		if (object instanceof DungFarmPatch p)
			patch = p;

		if (isTextile)
			if (player.getSkills().getLevel(Constants.FARMING) < harvest.lvl) {
				player.sendMessage("You need a Farming level of " + harvest.lvl + " in order to pick " + productName + ".");
				return;
			}

		if (harvestCount == -1)
			harvestCount = Utils.random(3, 6);
		harvestCount--;
		if (harvestCount == 0) {
			player.getTempAttribs().removeI("HARVEST_COUNT");
			if (isTextile)
				player.sendMessage("You have depleted this resource.");
			if (patch != null)
				patch.destroy();
			World.spawnObject(new GameObject(isTextile ? object.getId() + 1 : DungeonConstants.EMPTY_FARMING_PATCH, object.getType(), object.getRotation(), object.getTile()));
			return;
		}
		player.getTempAttribs().setI("HARVEST_COUNT", harvestCount);
		player.setNextAnimation(new Animation(3659));
		player.lock(2);
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				if (player.getInventory().addItemDrop(harvest.product, 1)) {
					player.sendMessage("You pick a " + productName + ".");
					player.getSkills().addXp(Constants.FARMING, harvest.getExp());
				}
			}
		}, 2);
	}

	public static void plantHarvest(Item item, final Player player, GameObject object, DungeonManager dungeon) {
		final Harvest harvest = Harvest.forSeed(item.getId());
		if (harvest == null)
			return;
		if (player.getSkills().getLevel(Constants.FARMING) < harvest.lvl) {
			player.sendMessage("You need a Farming level of " + harvest.lvl + " in order to plant a " + ItemDefinitions.getDefs(harvest.seed).getName().toLowerCase() + ".");
			return;
		}
		DungFarmPatch patch = new DungFarmPatch(player, harvest, object, dungeon);
		dungeon.getFarmingPatches().add(patch);
		patch.createReplace();
		player.lock(2);
		player.setNextAnimation(new Animation(3659));
		player.getInventory().deleteItem(harvest.seed, 1);
	}

	public static void clearHarvest(final Player player, final GameObject object) {
		player.setNextAnimation(new Animation(3659));
		player.lock(2);
		WorldTasks.scheduleTimer(2, (ticks) -> {
			World.spawnObject(new GameObject(DungeonConstants.EMPTY_FARMING_PATCH, object.getType(), object.getRotation(), object.getTile()));
			player.sendMessage("You empty the harvest patch.");
			return false;
		});
	}
}
