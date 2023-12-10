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
package com.rs.game.content.skills.herblore;

import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

import java.util.LinkedList;
import java.util.List;

public class HerbCleaning {

	public static enum Herbs {

		GUAM(199, 2.5, 1, 249),

		MARRENTILL(201, 3.8, 5, 251),

		TARROMIN(203, 5, 11, 253),

		HARRALANDER(205, 6.3, 20, 255),

		RANARR(207, 7.5, 25, 257),

		TOADFLAX(3049, 8, 30, 2998),

		SPIRIT_WEED(12174, 7.8, 35, 12172),

		IRIT(209, 8.8, 40, 259),

		WERGALI(14836, 9.5, 41, 14854),

		AVANTOE(211, 10, 48, 261),

		KWUARM(213, 11.3, 54, 263),

		SNAPDRAGON(3051, 11.8, 59, 3000),

		CADANTINE(215, 12.5, 65, 265),

		LANTADYME(2485, 13.1, 67, 2481),

		DWARF_WEED(217, 13.8, 70, 267),

		TORSTOL(219, 15, 75, 269),

		FELLSTALK(21626, 16.8, 91, 21624),

		SAGEWORT(17494, 2.1, 3, 17512),

		VALERIAN(17496, 3.2, 4, 17514),

		ALOE(17498, 4, 8, 17516),

		WORMWOOD_LEAF(17500, 7.2, 34, 17518),

		MAGEBANE(17502, 7.7, 37, 17520),

		FEATHERFOIL(17504, 8.6, 41, 17522),

		GRIMY_WINTERS_GRIP(17506, 12.7, 67, 17524),

		LYCOPUS(17508, 13.1, 70, 17526),

		BUCKTHORN(17510, 13.8, 74, 17528),

		ERZILLE(19984, 10, 54, 19989),

		ARGWAY(19985, 11.6, 57, 19990),

		UGUNE(19986, 11.5, 56, 19991),

		SHENGO(19987, 11.7, 58, 19992),

		SAMADEN(19988, 11.7, 59, 19993);

		private int herbId;
		private int level;
		private int cleanId;
		private double xp;

		Herbs(int herbId, double xp, int level, int cleanId) {
			this.herbId = herbId;
			this.xp = xp;
			this.level = level;
			this.cleanId = cleanId;
		}

		public int getHerbId() {
			return herbId;
		}

		public double getExperience() {
			return xp;
		}

		public int getLevel() {
			return level;
		}

		public int getCleanId() {
			return cleanId;
		}
	}

	public static Herbs getHerb(int id) {
		for (final Herbs herb : Herbs.values())
			if (herb.getHerbId() == id)
				return herb;
		return null;
	}

	public static boolean clean(final Player player, Item item, final int slotId) {
		final Herbs herb = getHerb(item.getId());
		if (herb == null)
			return false;
		if (player.getSkills().getLevel(Constants.HERBLORE) < herb.getLevel()) {
			player.sendMessage("You do not have the required level to clean this.", true);
			return true;
		}
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				Item i = player.getInventory().getItem(slotId);
				if ((i == null) || (i.getId() != herb.getHerbId()))
					return;
				i.setId(herb.getCleanId());
				player.getInventory().refresh(slotId);
				player.soundEffect(5153);
				player.getSkills().addXp(Constants.HERBLORE, herb.getExperience());
				player.sendMessage("You clean the herb.", true);
				player.incrementCount(item.getName() + " cleaned");
			}

		});
		return true;
	}

	public static List<Herbs> getHerbs() {
		List<Herbs> herbs = new LinkedList<>();
		for (Herbs herb : Herbs.values())
			if (herb.ordinal() < 17)
				herbs.add(herb);
		return herbs;
	}
}
