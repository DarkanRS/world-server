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
package com.rs.game.content.skills.cooking;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class Cooking extends PlayerAction {

	public enum Cookables {
		RAW_MEAT(new Item(2132, 1), 1, 5, 30, new Item(2146, 1), new Item(2142, 1), false, false),
		RAW_RAT(new Item(2134, 1), 1, 5, 30, new Item(2146, 1), new Item(2142, 1), false, false),
		RAW_BEAR(new Item(2136, 1), 1, 5, 30, new Item(2146, 1), new Item(2142, 1), false, false),
		RAW_MEAT_GHOST(new Item(4287, 1), 1, 5, 30, new Item(2146, 1), new Item(2142, 1), false, false),
		RAW_CHICKEN_GHOST(new Item(4289, 1), 1, 5, 30, new Item(2146, 1), new Item(2142, 1), false, false),
		BREAD(new Item(2307, 1), 1, 5, 40, new Item(2311, 1), new Item(2309, 1), false, false),
		SINEW(new Item(2142, 1), 1, 1, 1, new Item(9436, 1), new Item(9436, 1), false, false),
		RAW_CHICKEN(new Item(2138, 1), 1, 5, 30, new Item(2144, 1), new Item(2140, 1), false, false),
		RAW_UGTHANKI_MEAT(new Item(1859, 1), 1, 20, 30, new Item(2146, 1), new Item(1861, 1), false, false),
		RAW_RABBIT(new Item(3226, 1), 1, 20, 30, new Item(7222, 1), new Item(3228, 1), false, true),
		RAW_BIRD_MEAT(new Item(9978, 1), 11, 90, 62, new Item(9982, 1), new Item(23060, 1), false, false),
		RAW_BIRD_MEAT2(new Item(9984, 1), 11, 90, 62, new Item(9982, 1), new Item(9980, 1), false, true),
		RAW_CRAB_MEAT(new Item(7518, 1), 21, 90, 100, new Item(7520, 1), new Item(7521, 1), false, false),
		RAW_BEAST_MEAT(new Item(9986, 1), 21, 90, 82, new Item(9990, 1), new Item(9988, 1), true, false),
		RAW_CHOMPY(new Item(2876, 1), 30, 90, 140, new Item(2880, 1), new Item(2878, 1), false, true),
		RAW_JUBBLY(new Item(7566, 1), 41, 90, 160, new Item(7570, 1), new Item(7568, 1), true, false),
		RAW_CRAYFISH(new Item(13435, 1), 1, 32, 30, new Item(13437, 1), new Item(13433, 1), false, false),
		RAW_SHRIMP(new Item(317, 1), 1, 34, 30, new Item(7954, 1), new Item(315, 1), false, false),
		RAW_KARAMBWANJI(new Item(3150, 1), 1, 35, 10, new Item(3148, 1), new Item(3151, 1), false, false),
		RAW_SARDINE(new Item(327, 1), 1, 38, 40, new Item(369, 1), new Item(325, 1), false, false),
		RAW_ANCHOVIES(new Item(321, 1), 1, 34, 30, new Item(323, 1), new Item(319, 1), false, false),
		POISON_KARAMBWAN(new Item(3142, 1), 1, 20, 80, new Item(3148, 1), new Item(3151, 1), false, false),
		RAW_HERRING(new Item(345, 1), 5, 37, 50, new Item(357, 1), new Item(347, 1), false, false),
		RAW_MACKEREL(new Item(353, 1), 10, 45, 60, new Item(357, 1), new Item(355, 1), false, false),
		RAW_TROUT(new Item(335, 1), 15, 50, 70, new Item(343, 1), new Item(333, 1), false, false),
		RAW_COD(new Item(341, 1), 18, 39, 75, new Item(343, 1), new Item(339, 1), false, false),
		RAW_PIKE(new Item(349, 1), 20, 52, 80, new Item(343, 1), new Item(351, 1), false, false),
		RAW_SALMON(new Item(331, 1), 25, 58, 90, new Item(343, 1), new Item(329, 1), false, false),
		RAW_SLIMY_EEL(new Item(3379, 1), 28, 58, 95, new Item(3383, 1), new Item(3381, 1), false, false),
		RAW_TUNA(new Item(359, 1), 30, 63, 100, new Item(367, 1), new Item(361, 1), false, false),
		RAW_BARON_SHARK(new Item(19947, 1), 80, 95, 210, new Item(387, 1), new Item(19948, 1), false, false),
		RAW_RAINBOW_FISH(new Item(10138, 1), 35, 60, 110, new Item(10140, 1), new Item(10136, 1), false, false),
		RAW_CAVE_EEL(new Item(5001, 1), 38, 40, 115, new Item(5006, 1), new Item(5003, 1), false, false),
		RAW_LOBSTER(new Item(377, 1), 40, 66, 120, new Item(381, 1), new Item(379, 1), false, false),
		RAW_BASS(new Item(363, 1), 43, 80, 130, new Item(367, 1), new Item(365, 1), false, false),
		RAW_SWORDFISH(new Item(371, 1), 45, 86, 140, new Item(375, 1), new Item(373, 1), false, false),
		RAW_LAVA_EEL(new Item(2148, 1), 53, 53, 30, new Item(-1, 1), new Item(2149, 1), false, false),
		RAW_MONKFISH(new Item(7944, 1), 62, 90, 150, new Item(7948, 1), new Item(7946, 1), false, false),
		RAW_SHARK(new Item(383, 1), 80, 100, 210, new Item(387, 1), new Item(385, 1), false, false),
		RAW_SEA_TURTLE(new Item(395, 1), 82, 100, 212, new Item(399, 1), new Item(397, 1), false, false),
		RAW_CAVEFISH(new Item(15264, 1), 88, 100, 214, new Item(15268, 1), new Item(15266, 1), false, false),
		RAW_MANTA_RAY(new Item(389, 1), 91, 200, 216, new Item(393, 1), new Item(391, 1), false, false),
		SWEETCORN(new Item(5986, 1), 28, 54, 104, new Item(5990, 1), new Item(5988, 1), false, false),
		RAW_ROCKTAIL(new Item(15270, 1), 92, 100, 225, new Item(15274, 1), new Item(15272, 1), false, false),
		RAW_REDBERRY_PIE(new Item(2321, 1), 10, 35, 78, new Item(2329, 1), new Item(2325, 1), false, false),
		RAW_MEAT_PIE(new Item(2319, 1), 20, 59, 110, new Item(2329, 1), new Item(2327, 1), false, false),
		RAW_MUD_PIE(new Item(7168, 1), 29, 67, 128, new Item(2329, 1), new Item(7170, 1), false, false),
		RAW_APPLE_PIE(new Item(2317, 1), 30, 69, 130, new Item(2329, 1), new Item(2323, 1), false, false),
		RAW_GARDEN_PIE(new Item(7176, 1), 34, 78, 138, new Item(2329, 1), new Item(7178, 1), false, false),
		RAW_FISH_PIE(new Item(7186, 1), 47, 86, 164, new Item(2329, 1), new Item(7188, 1), false, false),
		RAW_ADMIRAL_PIE(new Item(7196, 1), 70, 95, 210, new Item(2329, 1), new Item(7198, 1), false, false),
		RAW_WILD_PIE(new Item(7206, 1), 85, 110, 240, new Item(2329, 1), new Item(7208, 1), false, false),
		RAW_SUMMER_PIE(new Item(7216, 1), 95, 120, 260, new Item(2329, 1), new Item(7218, 1), false, false),
		RAW_FISHCAKE(new Item(7529, 1), 31, 60, 100, new Item(7531, 1), new Item(7530, 1), false, false),
		RAW_POTATO(new Item(1942, 1), 7, 200, 15, new Item(6699, 1), new Item(6701, 1), false, false),

		HARDENED_STRAIT_ROOT(new Item(21349), 83, 1, 379, new Item(-1), new Item(21351), false, true),
		SODA_ASH_1(new Item(401), 1, 0, 3, new Item(1781), new Item(1781), false, false),
		SODA_ASH_2(new Item(7516), 1, 0, 3, new Item(1781), new Item(1781), false, false),
		SODA_ASH_3(new Item(10978), 1, 0, 3, new Item(1781), new Item(1781), false, false),

		CAVE_POTATO(new Item(17817), 1, 0, 9, new Item(-1), new Item(18093), false, false),
		HIEM_CRAB(new Item(17797), 1, 20, 22, new Item(18179), new Item(18159), false, false),
		RED_EYE(new Item(17799), 10, 30, 41, new Item(18181), new Item(18161), false, false),
		DUSK_EEL(new Item(17801), 20, 40, 61, new Item(18183), new Item(18163), false, false),
		GIANT_FLATFISH(new Item(17803), 30, 50, 82, new Item(18185), new Item(18165), false, false),
		SHORTFINNED_EEL(new Item(17805), 40, 60, 103, new Item(18187), new Item(18167), false, false),
		WEB_SNIPPER(new Item(17807), 50, 70, 124, new Item(18189), new Item(18169), false, false),
		BOULDABASS(new Item(17809), 60, 70, 146, new Item(18191), new Item(18171), false, false),
		SALVE_EEL(new Item(17811), 70, 70, 168, new Item(18193), new Item(18173), false, false),
		BLUE_CRAB(new Item(17813), 80, 70, 191, new Item(18195), new Item(18175), false, false),
		CAVE_MORAY(new Item(17815), 90, 70, 215, new Item(18197), new Item(18177), false, false);

		private static Map<Short, Cookables> ingredients = new HashMap<>();

		public static Cookables forId(short itemId) {
			return ingredients.get(itemId);
		}

		static {
			for (Cookables ingredient : Cookables.values())
				ingredients.put((short) ingredient.getRawItem().getId(), ingredient);
		}

		private Item raw;
		private int lvl;
		private int burningLvl;
		private int xp;
		private Item burnt;
		private Item total;
		private boolean spitRoast;
		private boolean fireOnly;

		private Cookables(Item raw, int lvl, int burningLvl, int exp, Item burnt, Item total, boolean spitRoast, boolean fireOnly) {
			this.raw = raw;
			this.lvl = lvl;
			this.burningLvl = burningLvl;
			xp = exp;
			this.burnt = burnt;
			this.total = total;
			this.spitRoast = spitRoast;
			this.fireOnly = fireOnly;
		}

		public Item getRawItem() {
			return raw;
		}

		public int getLvl() {
			return lvl;
		}

		public Item getBurntId() {
			return burnt;
		}

		public Item getProduct() {
			return total;
		}

		public int getXp() {
			return xp;
		}

		public int getBurningLvl() {
			return burningLvl;
		}

		public Item getTotal() {
			return total;
		}

		public boolean isSpitRoast() {
			return spitRoast;
		}

		public boolean isFireOnly() {
			return fireOnly;
		}
	}

	private int amount;
	private Cookables cook;
	private Item item;
	private GameObject object;
	private Animation FIRE_COOKING = new Animation(897), RANGE_COOKING = new Animation(897);

	public Cooking(GameObject object, Item item, int amount) {
		this.amount = amount;
		this.item = item;
		this.object = object;
	}

	@Override
	public boolean start(Player player) {
		if ((cook = Cookables.forId((short) item.getId())) == null)
			return false;
		if (cook.isFireOnly() && !object.getDefinitions(player).getName().equals("Fire"))
			player.simpleDialogue("You may only cook this on a fire.");
		else if (cook.isSpitRoast() && object.getId() != 11363) {
			player.simpleDialogue("You may only cook this on an iron spit.");
			return false;
		} else if (player.getSkills().getLevel(Constants.COOKING) < cook.getLvl()) {
			player.simpleDialogue("You need a cooking level of " + cook.getLvl() + " to cook this food.");
			return false;
		}
		player.sendMessage("You attempt to cook the " + cook.getProduct().getDefinitions().getName().toLowerCase() + ".", true);
		player.faceObject(object);
		return true;
	}

	private boolean isBurned(Cookables cook, Player player) {
		int level = player.getSkills().getLevel(Constants.COOKING);
		int burnLevel = cook.getBurningLvl();
		if (player.getEquipment().getGlovesId() == 775)
			burnLevel -= 6;

		double chance = ((double) level / (double) burnLevel);
		if (chance < 0.7)
			chance = 0.7;
		return chance < Math.random();
	}

	public static Cookables isCookingSkill(Item item) {
		return Cookables.forId((short) item.getId());
	}

	@Override
	public boolean process(Player player) {
		if (!World.getRegion(object.getTile().getRegionId()).objectExists(object) || !player.getInventory().containsItem(item.getId(), 1) || !player.getInventory().containsItem(cook.getRawItem().getId(), 1))
			return false;
		if (player.getSkills().getLevel(Constants.COOKING) < cook.getLvl()) {
			player.simpleDialogue("You need a level of " + cook.getLvl() + " to cook this.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		amount--;
		player.setNextAnimation(object.getDefinitions(player).getName().contains("fire") ? FIRE_COOKING : RANGE_COOKING);
		if ((player.getSkills().getLevel(Constants.COOKING) >= cook.getBurningLvl()) ? false : isBurned(cook, player)) {
			player.getInventory().deleteItem(item.getId(), 1);
			player.getInventory().addItem(cook.getBurntId().getId(), cook.getBurntId().getAmount());
			player.sendMessage("Oops! You accidently burnt the " + cook.getProduct().getDefinitions().getName().toLowerCase() + ".", true);
		} else {
			player.getInventory().deleteItem(item.getId(), 1);
			player.getInventory().addItem(cook.getProduct().getId(), cook.getProduct().getAmount());
			player.getSkills().addXp(Constants.COOKING, cook.getXp());
			player.sendMessage("You successfully cook the " + cook.getProduct().getDefinitions().getName().toLowerCase() + ".", true);
		}
		if (amount > 0) {
			player.sendMessage("You attempt to cook the " + cook.getProduct().getDefinitions().getName().toLowerCase() + ".", true);
			return 3;
		}
		return -1;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}
