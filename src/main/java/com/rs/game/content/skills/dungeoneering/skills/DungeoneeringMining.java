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
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class DungeoneeringMining extends PlayerAction {

	public enum DungeoneeringRocks {
		NOVITE_ORE(1, 15, 17630, 10, 1),
		BATHUS_ORE(10, 27.5, 17632, 15, 1),
		MARMAROS_ORE(20, 41, 17634, 25, 1),
		KRATONIUM_ORE(30, 56.5, 17636, 50, 5),
		FRACTITE_ORE(40, 71, 17638, 80, 10),
		ZEPHYRIUM_ORE(50, 85, 17640, 95, 10),
		AGRONITE_ORE(60, 100.5, 17642, 100, 15),
		KATAGON_ORE(70, 117, 17644, 110, 20),
		GORGONITE_ORE(80, 131, 17646, 123, 22),
		PROMETHIUM_ORE(90, 148, 17648, 130, 25);

		private final int level;
		private final double xp;
		private final int oreId;
		private final int oreBaseTime;
		private final int oreRandomTime;

		DungeoneeringRocks(int level, double xp, int oreId, int oreBaseTime, int oreRandomTime) {
			this.level = level;
			this.xp = xp;
			this.oreId = oreId;
			this.oreBaseTime = oreBaseTime;
			this.oreRandomTime = oreRandomTime;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}

		public int getOreId() {
			return oreId;
		}

		public int getOreBaseTime() {
			return oreBaseTime;
		}

		public int getOreRandomTime() {
			return oreRandomTime;
		}

	}

	private final GameObject rock;
	private final DungeoneeringRocks definitions;
	private DungPickaxe pick;

	public DungeoneeringMining(GameObject rock, DungeoneeringRocks definitions) {
		this.rock = rock;
		this.definitions = definitions;
	}

	@Override
	public boolean start(Player player) {
		pick = DungPickaxe.getBest(player);
		if (!checkAll(player))
			return false;
		player.sendMessage("You swing your pickaxe at the rock.");
		setActionDelay(player, getMiningDelay(player));
		return true;
	}

	private int getMiningDelay(Player player) {
		int mineTimer = definitions.getOreBaseTime() - player.getSkills().getLevel(Constants.MINING) + Utils.getRandomInclusive(pick.getTicks());
		if (mineTimer < 1 + definitions.getOreRandomTime())
			mineTimer = 1 + Utils.getRandomInclusive(definitions.getOreRandomTime());
		return mineTimer;
	}

	private boolean checkAll(Player player) {
		if (pick == null) {
			player.sendMessage("You do not have a pickaxe or do not have the required level to use the pickaxe.");
			return false;
		}
		if (!hasMiningLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("Not enough space in your inventory.");
			return false;
		}
		return true;
	}

	private boolean hasMiningLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(Constants.MINING)) {
			player.sendMessage("You need a mining level of " + definitions.getLevel() + " to mine this rock.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(pick.getAnimation());
		return checkRock(player);
	}

	@Override
	public int processWithDelay(Player player) {
		addOre(player);
		if (Utils.random(5) == 0) {
			World.spawnObject(new GameObject(rock.getId() + 1, rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane()));
			player.sendMessage("You have depleted this resource.");
			player.setNextAnimation(new Animation(-1));
			return -1;
		}
		if (!player.getInventory().hasFreeSlots() && definitions.getOreId() != -1) {
			player.setNextAnimation(new Animation(-1));
			player.sendMessage("Not enough space in your inventory.");
			return -1;
		}
		return getMiningDelay(player);
	}

	private void addOre(Player player) {
		double xpBoost = 1.0;
		player.getSkills().addXp(Constants.MINING, definitions.getXp() * xpBoost);
		player.getInventory().addItem(definitions.getOreId(), 1);
		String oreName = ItemDefinitions.getDefs(definitions.getOreId()).getName().toLowerCase();
		player.sendMessage("You mine some " + oreName + ".", true);
	}

	private boolean checkRock(Player player) {
		return World.containsObjectWithId(rock.getTile(), rock.getId());
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
