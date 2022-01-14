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
package com.rs.game.player.content.skills.smithing;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class Smithing extends Action {

	public enum ForgingBar {

		ADAMANT(2361, 70, new Item[] { new Item(1211, 1), new Item(1357, 1), new Item(1430, 1), new Item(1145, 1), new Item(9380, 1), new Item(1287, 1), new Item(823, 1), new Item(4823, 1), new Item(-1, 1), new Item(-1, 1), new Item(-1, 1),
				new Item(43, 1), new Item(1331, 1), new Item(9429, 1), new Item(1301, 1), new Item(867, 1), new Item(1161, 1), new Item(1183, 1), new Item(-1, 1), new Item(-1, 1), new Item(1345, 1), new Item(1371, 1), new Item(1111, 1),
				new Item(1199, 1), new Item(3100, 1), new Item(1317, 1), new Item(1091, 1), new Item(1073, 1), new Item(1123, 1), new Item(1271, 1) }, new double[] { 62.5, 125, 187.5, 312.5 }, new int[] { 66, 210, 267 }),

		BRONZE(2349, 0, new Item[] { new Item(1205, 1), new Item(1351, 1), new Item(1422, 1), new Item(1139, 1), new Item(9375, 1), new Item(1277, 1), new Item(819, 1), new Item(4819, 1), new Item(1794, 1), new Item(-1, 1), new Item(-1, 1),
				new Item(39, 1), new Item(1321, 1), new Item(9420, 1), new Item(1291, 1), new Item(864, 1), new Item(1155, 1), new Item(1173, 1), new Item(-1, 1), new Item(-1, 1), new Item(1337, 1), new Item(1375, 1), new Item(1103, 1),
				new Item(1189, 1), new Item(3095, 1), new Item(1307, 1), new Item(1087, 1), new Item(1075, 1), new Item(1117, 1), new Item(1265, 1) }, new double[] { 12.5, 25, 37.5, 62.5 }, new int[] { 66, 82, 210, 267 }),

		IRON(2351, 15, new Item[] { new Item(1203, 1), new Item(1349, 1), new Item(1420, 1), new Item(1137, 1), new Item(9377, 1), new Item(1279, 1), new Item(820, 1), new Item(4820, 1), new Item(-1, 1), new Item(7225, 1), new Item(-1, 1),
				new Item(40, 1), new Item(1323, 1), new Item(9423, 1), new Item(1293, 1), new Item(863, 1), new Item(1153, 1), new Item(1175, 1), new Item(4540, 1), new Item(-1, 1), new Item(1335, 1), new Item(1363, 1), new Item(1101, 1),
				new Item(1191, 1), new Item(3096, 1), new Item(1309, 1), new Item(1081, 1), new Item(1067, 1), new Item(1115, 1), new Item(1267, 1) }, new double[] { 25, 50, 75, 125 }, new int[] { 66, 90, 162, 210, 267 }),

		MITHRIL(2359, 50, new Item[] { new Item(1209, 1), new Item(1355, 1), new Item(1428, 1), new Item(1143, 1), new Item(9379, 1), new Item(1285, 1), new Item(822, 1), new Item(4822, 1), new Item(-1, 1), new Item(-1, 1), new Item(-1, 1),
				new Item(42, 1), new Item(1329, 1), new Item(9427, 1), new Item(1299, 1), new Item(866, 1), new Item(1159, 1), new Item(1181, 1), new Item(-1, 1), new Item(9416, 1), new Item(1343, 1), new Item(1369, 1), new Item(1109, 1),
				new Item(1197, 1), new Item(3099, 1), new Item(1315, 1), new Item(1085, 1), new Item(1071, 1), new Item(1121, 1), new Item(1273, 1) }, new double[] { 50, 100, 150, 250 }, new int[] { 66, 170, 210, 267 }),

		RUNE(2363, 85, new Item[] { new Item(1213, 1), new Item(1359, 1), new Item(1432, 1), new Item(1147, 1), new Item(9381, 1), new Item(1289, 1), new Item(824, 1), new Item(4824, 1), new Item(-1, 1), new Item(-1, 1), new Item(-1, 1),
				new Item(44, 1), new Item(1333, 1), new Item(9431, 1), new Item(1303, 1), new Item(868, 1), new Item(1163, 1), new Item(1185, 1), new Item(-1, 1), new Item(-1, 1), new Item(1347, 1), new Item(1373, 1), new Item(1113, 1),
				new Item(1201, 1), new Item(3101, 1), new Item(1319, 1), new Item(1093, 1), new Item(1079, 1), new Item(1127, 1), new Item(1275, 1) }, new double[] { 75, 150, 225, 375 }, new int[] { 66, 210, 267 }),

		STEEL(2353, 30, new Item[] { new Item(1207, 1), new Item(1353, 1), new Item(1424, 1), new Item(1141, 1), new Item(9378, 1), new Item(1281, 1), new Item(821, 1), new Item(1539, 1), new Item(-1, 1), new Item(-1, 1), new Item(2370, 1),
				new Item(41, 1), new Item(1325, 1), new Item(9425, 1), new Item(1295, 1), new Item(865, 1), new Item(1157, 1), new Item(1177, 1), new Item(4544, 1), new Item(-1, 1), new Item(1339, 1), new Item(1365, 1), new Item(1105, 1),
				new Item(1193, 1), new Item(3097, 1), new Item(1311, 1), new Item(1083, 1), new Item(1069, 1), new Item(1119, 1), new Item(1269, 1) }, new double[] { 37.5, 75, 112.5, 187.5 }, new int[] { 66, 98, 162, 210, 267 });

		private static Map<Integer, ForgingBar> bars = new HashMap<>();

		static {
			for (ForgingBar bar : ForgingBar.values())
				bars.put(bar.getBarId(), bar);
		}

		public static ForgingBar forId(int id) {
			return bars.get(id);
		}

		public static ForgingBar getBar(Player player) {
			int smithLevel = player.getSkills().getLevel(Constants.SMITHING);
			for (ForgingBar bar : bars.values()) {
				if (smithLevel < bar.getLevel() || !player.getInventory().containsItem(bar.getBarId(), 1))
					continue;
				return bar;
			}
			return null;
		}

		private int barId;
		private int[] componentChilds;
		private double[] experience;
		private Item items[];
		private int level;

		private ForgingBar(int barId, int level, Item[] items, double[] experience, int[] componentChilds) {
			this.barId = barId;
			this.level = level;
			this.items = items;
			this.componentChilds = componentChilds;
			this.experience = experience;
		}

		public int getBarId() {
			return barId;
		}

		public int[] getComponentChilds() {
			return componentChilds;
		}

		public double[] getExperience() {
			return experience;
		}

		public Item[] getItems() {
			return items;
		}

		public int getLevel() {
			return level;
		}
	}

	public static final int HAMMER = 2347, DUNG_HAMMER = 17883;
	private ForgingBar bar;
	private int index;
	private int ticks;

	public Smithing(int ticks, int index) {
		this.index = index;
		this.ticks = ticks;
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItem(HAMMER, 1)) {
			player.sendMessage("You need a hammer in order to work with a bar of " + new Item(bar.getBarId(), 1).getDefinitions().getName().toLowerCase().replace(" bar", "") + ".");
			return false;
		}
		if (!player.getInventory().containsItem(bar.getBarId(), ForgingInterface.getActualAmount(bar.getLevel() + ForgingInterface.getFixedAmount(bar, bar.getItems()[index]), bar, bar.getItems()[index].getId()))) {
			player.sendMessage("You do not have sufficient bars!");
			return false;
		}
		if (player.getSkills().getLevel(Constants.SMITHING) < ForgingInterface.getLevels(bar, index, player)) {
			player.sendMessage("You need a Smithing level of " + ForgingInterface.getLevels(bar, index, player) + " to create this.");
			return false;
		}
		if (player.getInterfaceManager().containsScreenInter()) {
			player.getInterfaceManager().removeScreenInterface();
			return true;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		player.setNextAnimation(new Animation(898));
		int amount = ForgingInterface.getActualAmount(bar.getLevel() + ForgingInterface.getFixedAmount(bar, bar.getItems()[index]), bar, bar.getItems()[index].getId());

		if (player.hasScrollOfEfficiency && amount > 3)
			if (Utils.getRandomInclusive(15) == 5)
				amount--;

		player.getInventory().deleteItem(bar.getBarId(), amount);
		player.getInventory().addItem(bar.getItems()[index].getId(), ForgingInterface.getForgedAmount(bar.getItems()[index].getId()));
		player.getSkills().addXp(Constants.SMITHING, getExperience(player));
		if (ticks > 0)
			return 3;
		return -1;
	}

	private double getExperience(Player player) {
		int levelRequired = bar.getLevel() + ForgingInterface.getFixedAmount(bar, bar.getItems()[index]);
		int barAmount = ForgingInterface.getActualAmount(levelRequired, bar, bar.getItems()[index].getId());
		return bar.getExperience()[barAmount == 5 ? 3 : barAmount - 1];
	}

	@Override
	public boolean start(Player player) {
		if ((bar = player.getTempAttribs().getO("SmithingBar")) == null)
			return false;
		if (!player.getInventory().containsOneItem(HAMMER, bar.getBarId())) {
			player.sendMessage("You need a hammer in order to work with a bar of " + new Item(bar.getBarId(), 1).getDefinitions().getName().replace("Bar ", "") + ".");
			return false;
		}
		if (player.getSkills().getLevel(Constants.SMITHING) < ForgingInterface.getLevels(bar, index, player)) {
			player.sendMessage("You need a Smithing level of " + ForgingInterface.getLevels(bar, index, player) + " to create this.");
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
