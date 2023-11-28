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

import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class Cooking extends PlayerAction {

	public static ItemOnObjectHandler foodOnCookingObj = new ItemOnObjectHandler(new Object[] { "Fire", "Range", "Campfire", "Oven", "Cooking range", "Sulphur pit", "Stove", "Clay oven", "Clay Oven", "Fireplace" }, Arrays.stream(Cookables.values()).map(item -> item.raw.getId()).toArray(), e ->
			e.getPlayer().startConversation(new CookingD(e.getPlayer(), Cookables.forId(e.getItem().getId()), e.getObject())));

	public enum Cookables {
		SODA_ASH_1(401, 1781, 1781, 1, 0, 1),
		SODA_ASH_2(7516, 1781, 1781, 1, 0, 1),
		SODA_ASH_3(10978, 1781, 1781, 1, 0, 1),
		SINEW(2142, 9436, 9436, 1, 0, 3),
		NETTLE_TEA(4237, 4239, 4239, 1, 0, 61),
		KARAMBWAN(3142, 3148, 3144, 1, 20, 80),
		POISON_KARAMBWAN(3144, 3146, 3146, 1, 20, 1),
		RAW_MEAT(2132, 2146, 2142, 1, 34, 30),
		RAW_RAT(2134, 2146, 2142, 1, 34, 30),
		RAW_BEAR(2136, 2146, 2142, 1, 34, 30),
		RAW_RABBIT(3226, 7222, 3228, 1, 34, 30, false, true),
		RAW_CHICKEN(2138, 2144, 2140, 1, 34, 30),
		RAW_SHRIMP(317, 7954, 315, 1, 34, 30),
		RAW_ANCHOVIES(321, 323, 319, 1, 34, 30),
		RAW_CRAYFISH(13435, 13437, 13433, 1, 34, 30),
		RAW_MEAT_GHOST(4287, 2146, 2142, 1, 34, 30),
		RAW_CHICKEN_GHOST(4289, 2146, 2142, 1, 34, 30),
		BREAD(2307, 2311, 2309, 1, 37, 40),
		RAW_SARDINE(327, 369, 325, 1, 37, 40),
		RAW_UGTHANKI_MEAT(1859, 2146, 1861, 1, 100, 40),
		RAW_KARAMBWANJI(3150, 592, 3151, 1, 28, 10),
		RAW_HERRING(345, 357, 347, 5, 41, 50),
		RAW_POTATO(1942, 6699, 6701, 7, 41, 15),
		RAW_MACKEREL(353, 357, 355, 10, 45, 60),
		RAW_REDBERRY_PIE(2321, 2329, 2325, 10, 45, 78),
		RAW_BIRD_MEAT(9978, 9982, 23060, 11, 99, 62),
		RAW_BIRD_MEAT2(9984, 9982, 9980, 11, 99, 62, false, true),
		RAW_TROUT(335, 343, 333, 15, 49, 70),
		RAW_COD(341, 343, 339, 18, 51, 75),
		RAW_PIKE(349, 343, 351, 20, 53, 75),
		RAW_MEAT_PIE(2319, 2329, 2327, 20, 53, 110),
		RAW_CRAB_MEAT(7518, 7520, 7521, 21, 62, 100),
		RAW_BEAST_MEAT(9986, 9990, 9988, 21, 99, 82.5D, true, false),
		RAW_SALMON(331, 343, 329, 25, 58, 90),
		RAW_SLIMY_EEL(3379, 3383, 3381, 28, 60, 95),
		SWEETCORN(5986, 5990, 5988, 28, 53, 104),
		RAW_MUD_PIE(7168, 2329, 7170, 29, 63, 128),
		RAW_CHOMPY(2876, 2880, 2878, 30, 99, 140, true, true),
		RAW_TUNA(359, 367, 361, 30, 63, 100),
		RAW_APPLE_PIE(2317, 2329, 2323, 30, 63, 130),
		RAW_FISHCAKE(7529, 7531, 7530, 31, 62, 100),
		RAW_GARDEN_PIE(7176, 2329, 7178, 34, 78, 138),
		RAW_RAINBOW_FISH(10138, 10140, 10136, 35, 64, 110),
		RAW_CAVE_EEL(5001, 5006, 5003, 38, 74, 115),
		RAW_LOBSTER(377, 381, 379, 40, 74, 120),
		RAW_JUBBLY(7566, 7570, 7568, 41, 99, 160, true, false),
		RAW_BASS(363, 367, 365, 43, 79, 130),
		RAW_SWORDFISH(371, 375, 373, 45, 86, 140),
		RAW_FISH_PIE(7186, 2329, 7188, 47, 74, 164),
		RAW_LAVA_EEL(2148, -1, 2149, 53, 0, 30),
		RAW_MONKFISH(7944, 7948, 7946, 62, 92, 150),
		RAW_ADMIRAL_PIE(7196, 2329, 7198, 70, 94, 210),
		RAW_BARON_SHARK(19947, 387, 19948, 80, 95, 210),
		RAW_SHARK(383, 387, 385, 80, 100, 210),
		RAW_SEA_TURTLE(395, 399, 397, 82, 100, 211.3D),
//		HARDENED_STRAIT_ROOT(21349, -1, 21351, 83, 1, 379, false, true),
		RAW_WILD_PIE(7206, 2329, 7208, 85, 100, 240),
		RAW_CAVEFISH(15264, 15268, 15266, 88, 100, 214),
		RAW_MANTA_RAY(389, 393, 391, 91, 100, 216),
		RAW_ROCKTAIL(15270, 15274, 15272, 93, 100, 225),
		RAW_SUMMER_PIE(7216, 2329, 7218, 95, 100, 260),

		// Dungeoneering
		CAVE_POTATO(17817, -1, 18093, 1, 1, 9),
		HIEM_CRAB(17797, 18179, 18159, 1, 20, 22),
		RED_EYE(17799, 18181, 18161, 10, 30, 41),
		DUSK_EEL(17801, 18183, 18163, 20, 40, 61),
		GIANT_FLATFISH(17803, 18185, 18165, 30, 50, 82),
		SHORTFINNED_EEL(17805, 18187, 18167, 40, 60, 103),
		WEB_SNIPPER(17807, 18189, 18169, 50, 70, 124),
		BOULDABASS(17809, 18191, 18171, 60, 70, 146),
		SALVE_EEL(17811, 18193, 18173, 70, 70, 168),
		BLUE_CRAB(17813, 18195, 18175, 80, 70, 191),
		CAVE_MORAY(17815, 18197, 18177, 90, 70, 215);

		private final static Map<Integer, Cookables> ingredients = new HashMap<>();

		public static Cookables forId(int itemId) {
			return ingredients.get(itemId);
		}

		static {
			for (Cookables ingredient : Cookables.values())
				ingredients.put(ingredient.getRawItem().getId(), ingredient);
		}

		private final Item raw;
		private final int lvl;
		private final int burningLvl;
		private final double xp;
		private final Item burnt;
		private final Item product;
		private final boolean spitRoast;
		private final boolean fireOnly;

		Cookables(Item raw, Item burnt, Item product, int lvl, int burningLvl, double exp, boolean spitRoast, boolean fireOnly)
		{
			this.raw = raw;
			this.lvl = lvl;
			this.burningLvl = burningLvl;
			this.xp = exp;
			this.burnt = burnt;
			this.product = product;
			this.spitRoast = spitRoast;
			this.fireOnly = fireOnly;
		}

		Cookables(Item raw, Item burnt, Item product, int lvl, int burningLvl, double exp)
		{
			this(raw, burnt, product, lvl, burningLvl, exp, false, false);
		}

		Cookables(int rawId, int burntId, int productId, int lvl, int burningLvl, double exp, boolean spitRoast, boolean fireOnly) {
			this(new Item(rawId), new Item(burntId), new Item(productId), lvl, burningLvl, exp, spitRoast, fireOnly);
		}

		Cookables(int rawId, int burntId, int productId, int lvl, int burningLvl, double exp) {
			this(new Item(rawId), new Item(burntId), new Item(productId), lvl, burningLvl, exp);
		}

		public Item getRawItem() {
			return raw;
		}

		public int getLevel() {
			return lvl;
		}

		public Item getBurntItem() {
			return burnt;
		}

		public Item getProductItem() {
			return product;
		}

		public double getXp() {
			return xp;
		}

		public int getBurningLvl() {
			return burningLvl;
		}

		public boolean isSpitRoastRequired() {
			return spitRoast;
		}

		public boolean isFireOnly() {
			return fireOnly;
		}
	}

	private final Animation animation;
	private final String productName;

	private final GameObject gameObject;
	private final Cookables cookable;
	private int amount;

	public Cooking(Player player, GameObject gameObject, Cookables cookable, int amount) {
		this.gameObject = gameObject;
		this.amount = amount;
		this.cookable = cookable;
		if (gameObject.getDefinitions(player).getName().equals("Fire")) {
			this.animation = new Animation(897);
		} else {
			this.animation = new Animation(897);
		}
		this.productName = cookable.getProductItem().getName().toLowerCase();
	}

	private boolean rollIsBurnt(Cookables cook, Player player) {
		int level = player.getSkills().getLevel(Constants.COOKING);
		int burnLevel = cook.getBurningLvl();
		if (player.getEquipment().getGlovesId() == 775)
			burnLevel -= 6;
		if (level >= burnLevel)
			return false;

		double chance = ((double) level / (double) burnLevel);
		if (chance < 0.7)
			chance = 0.7;
		return chance < Math.random();
	}

	@Override
	public boolean start(Player player) {
		player.sendMessage("You attempt to cook the " + productName + ".", true);
		player.faceObject(gameObject);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (!ChunkManager.getChunk(gameObject.getTile().getChunkId()).objectExists(gameObject))
			return false;
		return player.getInventory().containsItem(cookable.getRawItem().getId(), 1);
	}

	@Override
	public int processWithDelay(Player player) {
		amount--;
		player.setNextAnimation(animation);
		if (rollIsBurnt(cookable, player)) {
			player.getInventory().deleteItem(cookable.getRawItem().getId(), 1);
			player.getInventory().addItem(cookable.getBurntItem().getId(), cookable.getBurntItem().getAmount());
			player.sendMessage("Oops! You accidentally burnt the " + productName + ".", true);
		} else {
			player.getInventory().deleteItem(cookable.getRawItem().getId(), 1);
			player.getInventory().addItem(cookable.getProductItem().getId(), cookable.getProductItem().getAmount());
			player.getSkills().addXp(Constants.COOKING, cookable.getXp());
			player.sendMessage("You successfully cook the " + productName + ".", true);
		}
		if (amount > 0) {
			player.sendMessage("You attempt to cook the " + productName + ".", true);
			return 3;
		}
		return -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

}
