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
package com.rs.game.content.skills.fletching;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

import java.util.HashMap;
import java.util.Map;

public class Fletching extends PlayerAction {

	public static final int KNIFE = 946;
	public static final int CHISLE = 1755;
	public static final int BOW_STRING = 1777;
	public static final int CROSSBOW_STRING = 9438;
	public static final int DUNGEONEERING_KNIFE = 17754;

	public static enum Fletch {

		LOG(1511, 946, new int[] { 52, 50, 48, 9440 }, new int[] { 1, 1, 10, 9 }, new double[] { 0.33, 5, 10, 6 }, new Animation(6702)),

		STRUNG_SHORT_BOW(50, 1777, new int[] { 841 }, new int[] { 1 }, new double[] { 5 }, new Animation(6678)),

		STRUNG_LONG_BOW(48, 1777, new int[] { 839 }, new int[] { 10 }, new double[] { 10 }, new Animation(6684)),

		OAK_LOG(1521, 946, new int[] { 52, 54, 56, 9442 }, new int[] { 15, 20, 25, 24 }, new double[] { 0.375, 16.5, 25, 16 }, new Animation(6702)),

		STRUNG_OAK_SHORT_BOW(54, 1777, new int[] { 843 }, new int[] { 20 }, new double[] { 16.5 }, new Animation(6679)),

		STRUNG_OAK_LONG_BOW(56, 1777, new int[] { 845 }, new int[] { 25 }, new double[] { 25 }, new Animation(6685)),

		WILLOW_LOG(1519, 946, new int[] { 52, 60, 58, 9444 }, new int[] { 30, 35, 40, 39 }, new double[] { 0.4, 33.3, 41.5, 22 }, new Animation(6702)),

		STRUNG_WILLOW_SHORT_BOW(60, 1777, new int[] { 849 }, new int[] { 35 }, new double[] { 33.3 }, new Animation(6680)),

		STRUNG_WILLOW_LONG_BOW(58, 1777, new int[] { 847 }, new int[] { 40 }, new double[] { 41.5 }, new Animation(6686)),

		TEAK_LOG(6333, 946, new int[] { 9446 }, new int[] {46 }, new double[] { 27 }, new Animation(6702)),

		MAHOGANY_LOG (6332, 946, new int[] { 9450 }, new int[] { 61 }, new double[] { 41 }, new Animation(6702)),

		MAPLE_LOG(1517, 946, new int[] { 52, 64, 62, 9448 }, new int[] { 45, 50, 55, 54 }, new double[] { 0.416, 50, 58.3, 32 }, new Animation(6702)),

		STRUNG_MAPLE_SHORT_BOW(64, 1777, new int[] { 853 }, new int[] { 50 }, new double[] { 50 }, new Animation(6681)),

		STRUNG_MAPLE_LONG_BOW(62, 1777, new int[] { 851 }, new int[] { 55 }, new double[] { 58.3 }, new Animation(6687)),

		YEW_LOG(1515, 946, new int[] { 52, 68, 66, 9452 }, new int[] { 60, 65, 70, 69 }, new double[] { 0.428, 67.5, 75, 50 }, new Animation(6702)),

		STRUNG_YEW_SHORT_BOW(68, 1777, new int[] { 857 }, new int[] { 65 }, new double[] { 67.5 }, new Animation(6682)),

		STRUNG_YEW_LONG_BOW(66, 1777, new int[] { 855 }, new int[] { 70 }, new double[] { 75 }, new Animation(6688)),

		MAGIC_LOG(1513, 946, new int[] { 52, 72, 70 }, new int[] { 75, 80, 85 }, new double[] { 0.4375, 83.25, 91.5 }, new Animation(7211)),

		STRUNG_MAGIC_SHORT_BOW(72, 1777, new int[] { 861 }, new int[] { 80 }, new double[] { 83.25 }, new Animation(6683)),

		STRUNG_MAGIC_LONG_BOW(70, 1777, new int[] { 859 }, new int[] { 85 }, new double[] { 91.5 }, new Animation(6689)),

		/**
		 * Crossbows
		 */

		U_BRONZE_CBOW(9440, 9420, new int[] { 9454 }, new int[] { 9 }, new double[] { 6 }, new Animation(-1)),

		U_IRON_CBOW(9444, 9423, new int[] { 9457 }, new int[] { 39 }, new double[] { 22 }, new Animation(-1)),

		U_BLURITE_CBOW(9442, 9422, new int[] { 9456 }, new int[] { 24 }, new double[] { 16 }, new Animation(-1)),

		U_STEEL_CBOW(9446, 9425, new int[] { 9459 }, new int[] { 46 }, new double[] { 27 }, new Animation(-1)),

		U_MITHRIL_CBOW(9448, 9427, new int[] { 9461 }, new int[] { 54 }, new double[] { 32 }, new Animation(-1)),

		U_ADAMANT_CBOW(9450, 9429, new int[] { 9463 }, new int[] { 61 }, new double[] { 41 }, new Animation(-1)),

		U_RUNITE_CBOW(9452, 9431, new int[] { 9465 }, new int[] { 69 }, new double[] { 50 }, new Animation(-1)),

		BRONZE_CBOW(9454, 9438, new int[] { 9174 }, new int[] { 9 }, new double[] { 6.0 }, new Animation(6671)),

		IRON_CBOW(9457, 9438, new int[] { 9177 }, new int[] { 39 }, new double[] { 22 }, new Animation(6673)),

		STEEL_CBOW(9459, 9438, new int[] { 9179 }, new int[] { 46 }, new double[] { 27 }, new Animation(6674)),

		BLURITE_CBOW(9456, 9438, new int[] { 9176 }, new int[] { 24 }, new double[] { 16 }, new Animation(6672)),

		MITHRIL_CBOW(9461, 9438, new int[] { 9181 }, new int[] { 52 }, new double[] { 32 }, new Animation(6675)),

		ADAMANT_CBOW(9463, 9438, new int[] { 9183 }, new int[] { 61 }, new double[] { 41 }, new Animation(6676)),

		RUNITE_CBOW(9465, 9438, new int[] { 9185 }, new int[] { 69 }, new double[] { 50 }, new Animation(6677)),

		/**
		 * Arrows
		 */
		HEADLESS_ARROWS(52, 314, new int[] { 53 }, new int[] { 1 }, new double[] { 1 }, new Animation(-1)),

		BRONZE_ARROWS(39, 53, new int[] { 882 }, new int[] { 1 }, new double[] { 1.3 }, new Animation(-1)),

		IRON_ARROWS(40, 53, new int[] { 884 }, new int[] { 15 }, new double[] { 3.8 }, new Animation(-1)),

		STEEL_ARROWS(41, 53, new int[] { 886 }, new int[] { 30 }, new double[] { 6.3 }, new Animation(-1)),

		MITHRIL_ARROWS(42, 53, new int[] { 888 }, new int[] { 45 }, new double[] { 8.8 }, new Animation(-1)),

		ADAMANT_ARROWS(43, 53, new int[] { 890 }, new int[] { 60 }, new double[] { 11.3 }, new Animation(-1)),

		RUNITE_ARROWS(44, 53, new int[] { 892 }, new int[] { 75 }, new double[] { 13.8 }, new Animation(-1)),

		DRAGON_ARROWS(11237, 53, new int[] { 11212 }, new int[] { 90 }, new double[] { 16.3 }, new Animation(-1)),

		BROAD_BOLTS(13279, 314, new int[] { 13280 }, new int[] { 55 }, new double[] { 3 }, new Animation(-1)),

		BROAD_ARROW(13278, 53, new int[] { 4160 }, new int[] { 52 }, new double[] { 15 }, new Animation(-1)),
		DRAGONBANE_ARROW(21823, 53, new int[] { 21640 }, new int[] { 76 }, new double[] { 12.5 }, new Animation(-1)),
		ABYSSALBANE_ARROW(21838, 53, new int[] { 21655 }, new int[] { 76 }, new double[] { 12.5 }, new Animation(-1)),
		WALLASALKIBANE_ARROW(21828, 53, new int[] { 21645 }, new int[] { 76 }, new double[] { 12.5 }, new Animation(-1)),
		BASILISKBANE_ARROW(21833, 53, new int[] { 21650 }, new int[] { 76 }, new double[] { 12.5 }, new Animation(-1)),

		/**
		 * Bolts
		 */
		BRONZE_BOLT(9375, 314, new int[] { 877 }, new int[] { 9 }, new double[] { 0.5 }, new Animation(-1)),

		IRON_BOLT(9377, 314, new int[] { 9140 }, new int[] { 39 }, new double[] { 1.5 }, new Animation(-1)),

		STEEL_BOLT(9378, 314, new int[] { 9141 }, new int[] { 46 }, new double[] { 3.5 }, new Animation(-1)),

		MITHRIL_BOLT(9379, 314, new int[] { 9142 }, new int[] { 54 }, new double[] { 5 }, new Animation(-1)),

		ADAMANT_BOLT(9380, 314, new int[] { 9143 }, new int[] { 61 }, new double[] { 7 }, new Animation(-1)),

		RUNITE_BOLT(9381, 314, new int[] { 9144 }, new int[] { 69 }, new double[] { 10 }, new Animation(-1)),
		DRAGONBANE_BOLT(21843, 314, new int[] { 21660 }, new int[] { 80 }, new double[] { 7 }, new Animation(-1)),
		ABYSSALBANE_BOLT(21858, 314, new int[] { 21675 }, new int[] { 80 }, new double[] { 7 }, new Animation(-1)),
		WALLASALKIBANE_BOLT(21853, 314, new int[] { 21665 }, new int[] { 80 }, new double[] { 7 }, new Animation(-1)),
		BASILISKBANE_BOLT(21848, 314, new int[] { 21670 }, new int[] { 80 }, new double[] { 7 }, new Animation(-1)),

		OPAL_BOLTS(45, 877, new int[] { 879 }, new int[] { 11 }, new double[] { 1.6 }, new Animation(-1)),

		BLURITE_BOLTS(9376, 314, new int[] { 9139 }, new int[] { 24 }, new double[] { 1 }, new Animation(-1)),

		JADE_BOLTS(9187, 9376, new int[] { 9335 }, new int[] { 26 }, new double[] { 2.4 }, new Animation(-1)),

		PEARL_BOLTS(46, 9140, new int[] { 880 }, new int[] { 41 }, new double[] { 3.2 }, new Animation(-1)),

		SILVER_BOLTS(9382, 314, new int[] { 9145 }, new int[] { 43 }, new double[] { 2.5 }, new Animation(-1)),

		RED_TOPAZ_BOLTS(9188, 9141, new int[] { 9336 }, new int[] { 48 }, new double[] { 3.9 }, new Animation(-1)),

		BARBED_BOLTS(47, 877, new int[] { 881 }, new int[] { 51 }, new double[] { 9.5 }, new Animation(-1)),

		SAPPHIRE_BOLTS(9189, 9142, new int[] { 9337 }, new int[] { 56 }, new double[] { 2.4 }, new Animation(-1)),

		EMERALD_BOLTS(9190, 9142, new int[] { 9338 }, new int[] { 58 }, new double[] { 5.5 }, new Animation(-1)),

		RUBY_BOLTS(9191, 9143, new int[] { 9339 }, new int[] { 63 }, new double[] { 6.3 }, new Animation(-1)),

		DIAMOND_BOLTS(9192, 9143, new int[] { 9340 }, new int[] { 65 }, new double[] { 7 }, new Animation(-1)),

		DRAGON_BOLTS(9193, 9144, new int[] { 9341 }, new int[] { 71 }, new double[] { 8.2 }, new Animation(-1)),

		ONYX_BOLTS(9194, 9144, new int[] { 9342 }, new int[] { 73 }, new double[] { 9.4 }, new Animation(-1)),

		BRONZE_DART(819, 314, new int[] { 806 }, new int[] { 1 }, new double[] { 0.8 }, new Animation(-1)),

		/**
		 * Darts
		 */
		IRON_DART(820, 314, new int[] { 807 }, new int[] { 22 }, new double[] { 1 }, new Animation(-1)),

		STEEL_DART(821, 314, new int[] { 808 }, new int[] { 37 }, new double[] { 1.7 }, new Animation(-1)),

		MITHRIL_DART(822, 314, new int[] { 809 }, new int[] { 52 }, new double[] { 4 }, new Animation(-1)),

		ADAMANT_DART(823, 314, new int[] { 810 }, new int[] { 67 }, new double[] { 7.6 }, new Animation(-1)),

		RUNITE_DART(824, 314, new int[] { 811 }, new int[] { 81 }, new double[] { 12.2 }, new Animation(-1)),

		DRAGON_DART(11232, 314, new int[] { 11230 }, new int[] { 95 }, new double[] { 18.4 }, new Animation(-1)),

		BLISTERWOODS(21600, 946, new int[] { 21580, 21581, 21582 }, new int[] { 70, 70, 70 }, new double[] { 100, 10, 100 }, new Animation(-1));

		private static Map<Integer, Fletch> fletching = new HashMap<>();

		public static Fletch forId(int id) {
			return fletching.get(id);
		}

		static {
			for (Fletch fletch : Fletch.values())
				fletching.put(fletch.id, fletch);
		}

		private int[] product, level;
		private int id, selected;
		private double[] xp;
		private Animation anim;

		private Fletch(int id, int selected, int[] product, int level[], double[] xp, Animation anim) {
			this.id = id;
			this.product = product;
			this.selected = selected;
			this.xp = xp;
			this.anim = anim;
			this.level = level;
		}

		public int getId() {
			return id;
		}

		public int getSelected() {
			return selected;
		}

		public int[] getProduct() {
			return product;
		}

		public int[] getLevel() {
			return level;
		}

		public double[] getXp() {
			return xp;
		}

		public Animation getAnim() {
			return anim;
		}
	}

	private Fletch fletch;
	private int option;
	private int ticks;

	public Fletching(Fletch fletch, int option, int ticks) {
		this.fletch = fletch;
		this.option = option;
		this.ticks = ticks;
	}

	@Override
	public boolean start(Player player) {
		if (option >= fletch.getProduct().length)
			return false;
		if ((fletch == Fletch.BROAD_ARROW || fletch == Fletch.BROAD_BOLTS) && !player.isBroadFletching()) {
			player.sendMessage("You need to unlock the ability to fletch these from a slayer master.");
			return false;
		}
		if (!process(player))
			return false;
		player.sendMessage("You attempt to create a " + new Item(fletch.getProduct()[option]).getDefinitions().getName().replace("(u)", "") + "...", true);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if ((ticks <= 0) || !player.getInventory().containsItem(fletch.getId(), 1) || !player.getInventory().containsItem(fletch.getSelected(), 1))
			return false;
		if (player.getSkills().getLevel(Constants.FLETCHING) < fletch.getLevel()[option]) {
			player.simpleDialogue("You need a level of " + fletch.getLevel()[option] + " to fletch this.");
			return false;
		}
		return true;
	}

	public static boolean maxMakeQuantityTen(Fletch fletch) {
		return (fletch.getSelected() == 53 && fletch.getSelected() == 314) || ItemDefinitions.getDefs(fletch.getId()).getName().toLowerCase().contains("dart")
				|| ItemDefinitions.getDefs(fletch.getProduct()[0]).getName().toLowerCase().contains("bolt") || ItemDefinitions.getDefs(fletch.getProduct()[0]).getName().toLowerCase().contains(" stake") || ItemDefinitions.getDefs(fletch.getProduct()[0]).getName().toLowerCase().contains("dart") || ItemDefinitions.getDefs(fletch.getProduct()[0]).getName().toLowerCase().contains("arrow");
	}


	public static boolean maxMakeQuantityTen(Fletch fletch, int option) {
		return (fletch.getSelected() == 53 && fletch.getSelected() == 314) || ItemDefinitions.getDefs(fletch.getId()).getName().toLowerCase().contains("dart")
				|| ItemDefinitions.getDefs(fletch.getProduct()[option]).getName().toLowerCase().contains("bolt") || ItemDefinitions.getDefs(fletch.getProduct()[0]).getName().toLowerCase().contains(" stake") || ItemDefinitions.getDefs(fletch.getProduct()[option]).getName().toLowerCase().contains("dart") || ItemDefinitions.getDefs(fletch.getProduct()[option]).getName().toLowerCase().contains("arrow");
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		int amount = maxMakeQuantityTen(fletch, option) ? 15 : 1;
		if (amount > player.getInventory().getNumberOf(fletch.getId()))
			amount = player.getInventory().getNumberOf(fletch.getId());
		if (amount > player.getInventory().getNumberOf(fletch.getSelected()))
			amount = player.getInventory().getNumberOf(fletch.getSelected());

		if (fletch.getProduct()[option] == 52)
			amount = switch (fletch.getId()) {
			case 1511 -> 15;
			case 1521 -> 20;
			case 1519 -> 25;
			case 1517 -> 30;
			case 1515 -> 35;
			case 1513 -> 40;
			default -> 15;
			};

			if (fletch.getProduct()[option] == 21581)
				amount = 10;

			if (!player.getInventory().containsItem(fletch.getId(), amount) && (fletch.getProduct()[option] != 52) && (fletch.getProduct()[option] != 21581)) {
				player.sendMessage("You don't have enough of the supplies to make that many.");
				return -1;
			}
			player.setNextAnimation(fletch.getAnim());
			player.getInventory().deleteItem(fletch.getId(), fletch.getProduct()[option] == 52 || fletch.getProduct()[option] == 21581  ? 1 : amount);
			if (fletch.getSelected() != KNIFE && fletch.getSelected() != CHISLE)
				player.getInventory().deleteItem(fletch.getSelected(), amount);
			player.getInventory().addItem(fletch.getProduct()[option], amount);
			player.sendMessage("You create a " + new Item(fletch.getProduct()[option]).getDefinitions().getName().toLowerCase().replace(" (u)", "") + ".", true);
			player.getSkills().addXp(Constants.FLETCHING, fletch.getXp()[option] * amount);
			if (fletch.getSelected() == KNIFE)
				return 2;
			if (amount > 5)
				return 0;
			return 1;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	public static Fletch isFletching(Item first, Item second) {
		Fletch fletch = Fletch.forId(first.getId());
		int selected;
		if (fletch != null)
			selected = second.getId();
		else {
			fletch = Fletch.forId(second.getId());
			selected = first.getId();
		}
		return fletch != null && fletch.getSelected() == selected ? fletch : null;
	}
}
