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

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.dungeoneering.KinshipPerk;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Foods {

	public static enum Food {

		ACAI(20270,50),
		ADMIRAL_PIE(7198, 80),
		ADMIRAL_PIE_FULL(7198, 80, 7200, Effect.ADMIRAL_PIE),
		ADMIRAL_PIE_HALF(7200, 80, 2313, Effect.ADMIRAL_PIE),
		AMPHIBIOUS_FRUIT(21381,150),
		ANCHOVIE(319, 10),
		ANCHOVIE_PIZZA_FULL(2297, 90, 2299),
		ANCHOVIE_PIZZA_HALF(2299, 90,2299),
		ANCHOVY_PIZZA(2297, 90, 2299),
		APPLE_PIE(2323, 70,2335),
		APPLE_PIE_FULL(2323, 70, 2335),
		APPLE_PIE_HALF(2335, 70, 2313),
		AQUATIC_FRUIT(21380,150),
		BAGUETTE(6961,60),
		BAKED_POTATO(6701, 40),
		BANANA(1963, 20),
		BANANA_STEW(4016, 110),
		BARON_SHARK(19948, 0,0,Effect.BARON_SHARK_EFFECT),
		BASS(365, 130),
		BAT_SHISH(10964, 20),
		BISCUITS(19467, 20),
		BLACK_MUSHROOM(4620, 0, Effect.BLACK_MUSHROOM_EFFECT),
		BLUE_CRAB(18175, 220),
		BLUE_SWEETS(4558, 2),
		BLURBERRY_SPECIAL(2064, 2),
		BOULDABASS(18171, 170),
		BREAD(2309, 50),
		BUTTON_MUSHROOM(13563,10),
		CABBAGE(1965, 10, Effect.CABAGE_MESSAGE),
		CAKE(1891, 120, 1893),
		CAMOUFLAGED_FRUIT(21384, 150),
		CANNIBAL_FRUIT(21379, 150),
		CARRION_FRUIT(21382, 150),
		CAVEFISH(15266, 220),
		CAVE_EEL(5003, 110),
		CAVE_MORAY(18177, 250),
		CAVE_NIGHTSHADE(2398, 0, Effect.CAVE_NIGHTSHADE_EFFECT),
		CAVIAR(11326, 50),
		CELEBRATION_CAKE(20179, 20181, 20182),
		CHEESE(1985,20),
		CHEESEPTOM_BATTA(2259, 70),
		CHEESE_AND_TOMATO_BATTA(9535, 110),
		CHEESE_WHEEL(18789, 20),
		CHICKEN(2140, 30),
		CHILLI_CON_CARNIE(7062, 50, 1923),
		CHILLI_POTATO(7054, 140),
		CHOCCHIP_CRUNCHIES(2209, 70),
		CHOCOLATEY_MILK(1977, 40),
		CHOCOLATE_BAR(1973, 30),
		CHOCOLATE_BOMB(2185, 150),
		CHOCOLATE_CAKE(1897, 150, 1899),
		CHOCOLATE_DROP(14083, 30),
		CHOCOLATE_EGG(12646, 10, 12648),
		CHOCOLATE_KEBBIT(11026, 0, Effect.REFUSE_EFFECT),
		CHOCOLATE_SLICE(1901, 120),
		CHOCOTREAT(24148, 8),
		CHOC_ICE(6794, 70),
		CHOC_SATURDAY(2074, 50),
		CHOMPY(2878, 60),
		CHOPPED_ONION(1871,50),
		CHOPPED_TOMATO(1869,50),
		CHOPPED_TUNA(7086,50),
		COATED_FROGS_LEGS(10963, 20),
		COD(339,180),
		COMMON_FRUIT(21376,150),
		COOKED_CHICKEN(2140, 30),
		COOKED_CHOMPY(2878, 30),
		COOKED_CRAB_MEAT_1(7521,10),
		COOKED_CRAB_MEAT_2(7523, 10),
		COOKED_CRAB_MEAT_3(7524, 10),
		COOKED_CRAB_MEAT_4(7525,10),
		COOKED_CRAB_MEAT_5(7526, 10),
		COOKED_FISHCAKE(7530,110),
		COOKED_JUBBLY(7568,150),
		COOKED_KARAMBWAN(3144,180),
		COOKED_MEAT(2142, 30),
		COOKED_OOMLIE_WRAP(2343,140),
		COOKED_RABBIT(3228,20),
		COOKED_SLIMY_EEL(3381,70),
		COOKED_SWEETCORN(5988,0, Effect.SWEETCORN_EFFECT),
		COOKED_TURKEY(14540,30),
		COOKED_TURKEY_DRUMSTICK(14543,20),
		CORONATION_CHICKEN_SANDWICH(24398,20),
		CRAB_MEAT(7521, 0, Effect.REFUSE_EFFECT),
		CRAYFISH(13433,20),
		CREAM_TEA(24396,20),
		CRUMBLY_BITS(24179,0),
		CRUNCHY_RUNE_ROCKS(20838,0),
		CURRY(2011, 190, 1923),
		DEEP_BLUE_SWEETS(4559,20),
		DISEASED_FRUIT(21383,100),
		DOUGHNUT(14665,20),
		DRACONIC_FRUIT(21385,100),
		DRUGGED_MEAT(15277,0, Effect.REFUSE_EFFECT),
		DUSK_EEL(18163, 70),
		DWARVEN_ROCK_CAKE(7509, 0),
		DWELLBERRIES(2126,20),
		EASTER_EGG(1961, 20),
		EASTER_EGG1(7928, 20),
		EASTER_EGG2(7929, 20),
		EASTER_EGG3(7930, 20),
		EASTER_EGG4(7931, 20),
		EASTER_EGG5(7932, 20),
		EASTER_EGG6(7933, 20),
		EASTER_EGG7( 12644, 20),
		EASTER_EGG8( 12643, 20),
		EASTER_EGG9(1961, 20),
		EASTER_EGG10( 12642, 20),
		EASTER_EGG11( 12641,20),
		EASTER_EGG12( 12640, 20),
		EASTER_EGG13( 12639, 20),
		EASTER_EGG14(1961, 20),
		EASTER_EGG15(1961, 20),
		EASTER_EGG16(1961, 20),
		EASTER_EGG17(1961, 20),
		EASTER_EGG18(1961,20),
		EDIBLE_SEAWEED(403, 20),
		EELSUSHI(10971, 20),
		EGG_AND_TOMATO(7064, 80, 1923),
		EGG_POTATO(7056, 160),
		EQUA_LEAVES(2128,10),
		EVIL_DRUMSTICK(24147, 10),
		EVIL_TURNIP(12134, 60, 12136),
		FAT_SNAIL_MEAT(3373, 80 + Utils.random(2)),
		FIELD_RATION(7934, 50),
		FILLETS(10969, 25),
		FINGERS(10965, 20),
		FISHCAKE(7530, 110),
		FISH_LIKE_THING(6202, 0,Effect.REFUSE_EFFECT),
		FISH_N_CHIPS(2440,50),
		FISH_PIE_FULL(7188, 120, 7190, Effect.FISH_PIE),
		FISH_PIE_HALF(7188, 120, 2313, Effect.FISH_PIE),
		FOOD_CLASS_1(14162,20),
		FOOD_CLASS_2(14164,20),
		FOOD_CLASS_3(14166,20),
		FOOD_CLASS_4(14168,20),
		FOOD_CLASS_5(1417,20),
		FRESH_MONKFISH(7943,100),
		FRIED_MUSHROOMS(7082, 50, 1923),
		FRIED_ONIONS(7084, 50, 1923),
		FROGBURGER(10962,20),
		FROGSPAWN_GUMBO(10961,20),
		FROG_SPAWN(5004,20),
		FRUIT_BATTA(2277, 110),
		FULL_BREAKFAST(24404,100),
		FURY_SHARK(20429,280),
		GARDEN_PIE_FULL(7178, 60, 7180, Effect.GARDEN_PIE),
		GARDEN_PIE_HALF(7180, 60, 2313, Effect.GARDEN_PIE),
		GIANT_CARP(337,60),
		GIANT_FLATFISH(18165, 100),
		GIANT_FROG_LEGS(4517,60),
		GOUT_TUBER(6311,10,Effect.GOUT_EFFECT),
		GREEN_GLOOP_SOUP(10960,20),
		GREEN_SWEETS(4563,20),
		GRUBS_A_LA_MODE(10966,25),
		GUTHIX_FRUIT(21387,200),
		HALF_WINE_JUG(1989,87),
		HEIM_CRAB(18159, 20),
		HERRING(347, 20),
		HUMBLE_PIE(18767,0, Effect.REFUSE_EFFECT), //TODO
		IGNEOUS_FRUIT(21378,150),
		JANGERBERRIES(247,20),
		JUBBLY(7568, 150),
		JUJU_GUMBO(19949,320,Effect.JUJU_GUMBO_EFFECT),
		KARAMBWANI(3144, 30),
		KARAMBWANJI(3151, 30),
		KEBAB(1971, 0,Effect.KEBAB_EFFECT),
		KING_WORM(2162,20),
		LAVA_EEL(2149, 110),
		LEAN_SNAIL_MEAT(3371, 80),
		LEMON(2102,20),
		LEMON_CHUNKS(2104,2),
		LEMON_SLICES(2106,2),
		LIME(2120,20),
		LIME_CHUNKS(2122,2),
		LIME_SLICES(2124,2),
		LOACH(10970, 30),
		LOBSTER(379, 120),
		LOCUST_MEAT(9052,20),
		MACKEREL(355, 60),
		MAGIC_EGG(11023,20),
		MANTA(391, 220),
		MEAT(2142, 3), // TODO
		MEAT_PIE_FULL(2327, 60, 2331),
		MEAT_PIE_HALF(2331, 60, 2313),
		MEAT_PIZZA_FULL(2293, 160, 2295),
		MEAT_PIZZA_HALF(2295, 160, 2295),
		MINCED_MEAT(707,25,434),
		MINT_CAKE(9475,0,Effect.MINT_CAKE_EFFECT),
		MONKEY_BAR(4014,90),
		MONKEY_NUTS(4012,20),
		MONKFISH(7946, 160),
		MUSHROOMS(10968, 25),
		MUSHROOM_AND_ONIONS(7066, 110, 1923),
		MUSHROOM_AND_ONION_POTATO(7058, 200, 1923),
		NOT_MEAT(20837,20),
		ODD_CRUNCHIES(2197,1),
		OKTOBERFEST_PRETZEL(19778,20),
		ONE_THIRD_EVIL_TURNIP(12138, 6),
		ONION(1957, 1, Effect.ONION_MESSAGE),
		ONION_AND_TOMATO(1875,20,1923),
		OOMILE(2343, 14),
		ORANGE(2108,20),
		ORANGE_CHUNKS(211,2),
		ORANGE_SLICES(2112,2),
		PAPAYA(5972, 8, Effect.PAPAYA),
		PEACH(6883,20),
		PIKE(351, 80),
		PINEAPPLE_CHUNKS(2116,2),
		PINEAPPLE_PIZZA_FULL(2301, 81, 2303),
		PINEAPPLE_PIZZA_HALF(2303, 81),
		PINEAPPLE_RING(2118,2),
		PINK_SWEETS(4564,20),
		PLAIN_PIZZA_FULL(2289, 110, 2291),
		PLAIN_PIZZA_HALF(2291, 110),
		PLANT_BITS(2418,0, Effect.REFUSE_EFFECT),
		PM_CHEESE_AND_TOMATO_BATTA(2223, 110),
		PM_CHOCOCHIP_CRUNCHIES(2239, 50),
		PM_CHOCOLATE_BOMB(2229, 15),
		PM_FRUIT_BATTA(2225, 70),
		PM_SPICY_CRUNCHIES(2241, 50),
		PM_TANGLED_TOAD_LEGS(2231, 15),
		PM_TOAD_BATTA(2221, 70),
		PM_TOAD_CRUNCHIES(2243, 50),
		PM_VEGETABLE_BATTA(2227, 70),
		PM_WORM_BATTA(2219, 70),
		PM_WORM_CRUNCHIES(2237, 50),
		PM_WORM_HOLE(2233, 20),
		POISONED_CHEESE(6768,0, Effect.REFUSE_EFFECT),
		POISON_KARAMBWAN(3146,0, Effect.POISION_KARMAMWANNJI_EFFECT),
		POISON_KARAMBWANJI(3146, 0, Effect.POISION_KARMAMWANNJI_EFFECT),
		POORLY_COOKED_BEAST_MEAT(23062,20),
		POORLY_COOKED_BIRD_MEAT(2306,20),
		POPCORN_BALL(14082,3),
		POTATO_WITH_BUTTER(6703, 97),
		POTATO_WITH_CHEESE(6705, 160),
		POT_OF_CREAM(2130, 20),
		PUMPKIN(1959,20),
		PUNCH(22329,20),
		PURPLE_SWEETS(4561, 20, 0, null, 0, 20),
		RABBIT_SANDWICH(23065,42),
		RABIT(3228, 50),
		RAINBOW_FISH(10136, 110),
		REDBERRY_PIE_FULL(2325, 100, 2333),
		REDBERRY_PIE_HALF(2333,100 , 2313),
		RED_BANANA(7572,50),
		RED_EYE(18161, 50),
		RED_SWEETS(4562,20),
		ROASTED_BEAST_MEAT(9988, 42),
		ROASTED_BIRD_MEAT(9980, 42),
		ROAST_BEAST_MEAT(9988,42),
		ROAST_BIRD_MEAT(998,60),
		ROAST_FROG(10967,50),
		ROAST_POTATOES(15429, 100),
		ROAST_RABBIT(7223,70),
		ROCKTAIL(15272, 100,230),
		ROCK_CAKE(2379,0, Effect.ROCK_CAKE_EFFECT),
		ROE(11324,30),
		ROLL(6963,20),
		ROTTEN_APPLE(1984,0, Effect.REFUSE_EFFECT),
		ROTTEN_POTATO(5733,0, Effect.REFUSE_EFFECT),
		SALMON(329, 90),
		SALVE_EEL(18173, 200),
		SARADOMIN_FRUIT(21386,200),
		SARDINE(325, 40),
		SCORPION_MEAT(22342,80),
		SCRAMBLED_EGG(7078, 32, 1923),
		SEASONED_LEGS(2158,1),
		SEAWEED_SANDWICH(3168,0, Effect.REFUSE_EFFECT),
		SEA_MEAT(20831,250, 24182),
		SEA_TURTLE(397,200),
		SHADOW_FRUIT(21377,150),
		SHARK(385, 200),
		SHORT_FINNED_EEL(18167, 120),
		SHRIMP(315, 30),
		SHRUNK_OGLEROOT(11205,20),
		SKEWERED_KEBAB(15123,90),
		SLICED_BANANA(3162,20),
		SLICED_RED_BANANA(7574,20),
		SLICE_OF_CAKE(1895, 120),
		SLIMY_EEL(3381, 70),
		SPICEY_SAUCE(7072, 20, 1923),
		SPICY_CRUNCHIES(2213, 20),
		SPICY_MINCED_MEAT(9996,20),
		SPICY_SAUCE(7072,20),
		SPICY_STEW(7513, 20, 1923, Effect.SPICY_STEW_EFFECT),
		SPICY_TOADS_LEGS(2156,20),
		SPICY_TOMATO(9994,20),
		SPICY_WORM(216,20),
		SPIDER_ON_SHAFT(6299,20),
		SPIDER_ON_STICK(6297,20),
		SPINACH_ROLL(1969, 2),
		SQUARE_SANDWICH(6965,25),
		STEAK_AND_KIDNEY_PIE(24402,50),
		STEW(2003, 110, 1923),
		STRAWBERRY(5504, 6),
		STUFFED_SNAKE(7579,200),
		SUMMER_PIE_FULL(7218, 110, 7220, Effect.SUMMER_PIE),
		SUMMER_PIE_HALF(7220, 110, 2313, Effect.SUMMER_PIE),
		SUMMER_SQIRKJUICE(10849, 150, Effect.SQIRKJUICE_EFFECT),
		SUPER_KEBAB(4608, 0,Effect.KEBAB_EFFECT),
		SWORDFISH(373, 140),
		TANGLED_TOAD_LEGS(2187, 150),
		TCHIKI_MONKEY_NUTS(7573, 2),
		TCHIKI_NUT_PASTE(7575, 2),
		TEA(1978, 20, 1980, Effect.TEA_MESSAGE),
		TEA_FLASK(10859, 20),
		TENTH_ANNIVERSARY_CAKE(20111, 20),
		THIN_SNAIL_MEAT(3369, 30),
		THOK_RUNE(20841, 20),
		TIGER_SHARK(21521, 100),
		TOADS_LEGS(2152, 20),
		TOAD_BATTA(2255, 110),
		TOAD_CRUNCHIES(2217, 20),
		TOMATO(1982, 20),
		TRIANGLE_SANDWICH(6962, 20),
		TROUT(333, 70),
		TRUFFLE(12132, 20),
		TUNA(361, 100),
		TUNA_AND_CORN(7068, 220, 1923),
		TUNA_POTATO(7060, 220, 1923),
		TURKEY_DRUMSTICK(15428, 20),
		TURTLE(397, 210),
		TWO_THIRDS_CAKE(1893, 124, 1895),
		TWO_THIRDS_CHOCOLATE_CAKE(1899, 124, 1901),
		TWO_THIRDS_EVIL_TURNIP(12136, 6, 12138),
		UGTHANKI_KEBAB(1883,0, 1885,Effect.UGTHANKI_KEBAB_EFFECT),
		UGTHANKI_KEBAB_1(1883, 0, 1885,Effect.UGTHANKI_KEBAB_EFFECT),
		UGTHANKI_KEBAB_2(1885, 0, 1885,Effect.UGTHANKI_KEBAB_EFFECT),
		UGTHANKI_MEAT(1861, 20),
		UNFINISHED_BATTA(2261,  20),
		UNFINISHED_BATTA1( 2263,  20),
		UNFINISHED_BATTA2(2265,  20),
		UNFINISHED_BATTA3( 2267, 20),
		UNFINISHED_BATTA4( 2269,  20),
		UNFINISHED_BATTA5( 2271,  20),
		UNFINISHED_BATTA6( 2273,  20),
		VEGETABLE_BATTA7(2281, 11),
		VEG_BALL(2195, 12),
		WATERMELON_SLICE(5984, 20),
		WEB_SNIPER(18169, 150),
		WHITE_PEARL(4485, 20),
		WHITE_SWEETS(456, 20),
		WHITE_TREE_FRUIT(6469, 20),
		WILD_PIE_FULL(7208, 110, 7210, Effect.WILD_PIE),
		WILD_PIE_HALF(7210, 110, 2313, Effect.WILD_PIE),
		WORM_BATTA(2253, 110),
		WORM_CRUNCHIES(2205, 20),
		WORM_HOLE(2191, 20),
		WRAPPED_CANDY(14084, 20),
		YULE_LOG(1543, 20),
		YULE_LOGS(15430, 1, 20),
		ZAMORAK_FRUIT(21388, 20);

		/**
		 * The food id
		 */
		private int id;

		/**
		 * The healing health
		 */
		private int heal;

		/**
		 * The new food id if needed
		 */
		private int newId;

		private int extraHP;

		private int restoreRun;

		/**
		 * Our effect
		 */
		private Effect effect;

		/**
		 * A map of object ids to foods.
		 */
		private static Map<Integer, Food> foods = new HashMap<>();

		/**
		 * Gets a food by an object id.
		 *
		 * @param itemId
		 *            The object id.
		 * @return The food, or <code>null</code> if the object is not a food.
		 */
		public static Food forId(int itemId) {
			return foods.get(itemId);
		}

		/**
		 * Populates the tree map.
		 */
		static {
			for (final Food food : Food.values())
				foods.put(food.id, food);
		}

		/**
		 * Represents a food being eaten
		 *
		 * @param id
		 *            The food id
		 * @param heal
		 *            The healing health received
		 */
		private Food(int id, int heal) {
			this.id = id;
			this.heal = heal;
		}

		/**
		 * Represents a part of a food item being eaten (example: cake)
		 *
		 * @param id
		 *            The food id
		 * @param heal
		 *            The heal amount
		 * @param newId
		 *            The new food id
		 */
		private Food(int id, int heal, int newId) {
			this(id, heal, newId, null);
		}

		private Food(int id, int heal, int newId, Effect effect) {
			this.id = id;
			this.heal = heal;
			this.newId = newId;
			this.effect = effect;
		}

		private Food(int id, int heal, int newId, Effect effect, int extraHP, int restoreRun) {
			this.id = id;
			this.heal = heal;
			this.newId = newId;
			this.effect = effect;
			this.extraHP = extraHP;
			this.restoreRun = restoreRun;
		}

		private Food(int id, int heal, Effect effect) {
			this(id, heal, 0, effect);
		}

		/**
		 * Gets the id.
		 *
		 * @return The id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the exp amount.
		 *
		 * @return The exp amount.
		 */
		public int getHeal() {
			return heal;
		}

		/**
		 * Gets the new food id
		 *
		 * @return The new food id.
		 */
		public int getNewId() {
			return newId;
		}

		public int getExtraHP() {
			return extraHP;
		}
		public int getRestoreRun() {
			return restoreRun;
		}
	}

	public static enum Effect {
		SUMMER_PIE {

			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int runEnergy = (int) (player.getRunEnergy() * 1.1);
				if (runEnergy > 100)
					runEnergy = 100;
				player.setRunEnergy(runEnergy);
				int level = player.getSkills().getLevel(Constants.AGILITY);
				int realLevel = player.getSkills().getLevelForXp(Constants.AGILITY);
				player.getSkills().set(Constants.AGILITY, level >= realLevel ? realLevel + 5 : level + 5);
			}

		},

		GARDEN_PIE {

			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int level = player.getSkills().getLevel(Constants.FARMING);
				int realLevel = player.getSkills().getLevelForXp(Constants.FARMING);
				player.getSkills().set(Constants.FARMING, level >= realLevel ? realLevel + 3 : level + 3);
			}

		},

		FISH_PIE {

			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int level = player.getSkills().getLevel(Constants.FISHING);
				int realLevel = player.getSkills().getLevelForXp(Constants.FISHING);
				player.getSkills().set(Constants.FISHING, level >= realLevel ? realLevel + 3 : level + 3);
			}
		},

		ADMIRAL_PIE {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int level = player.getSkills().getLevel(Constants.FISHING);
				int realLevel = player.getSkills().getLevelForXp(Constants.FISHING);
				player.getSkills().set(Constants.FISHING, level >= realLevel ? realLevel + 5 : level + 5);
			}
		},

		WILD_PIE {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int level = player.getSkills().getLevel(Constants.SLAYER);
				int realLevel = player.getSkills().getLevelForXp(Constants.SLAYER);
				player.getSkills().set(Constants.SLAYER, level >= realLevel ? realLevel + 4 : level + 4);
				int level2 = player.getSkills().getLevel(Constants.RANGE);
				int realLevel2 = player.getSkills().getLevelForXp(Constants.RANGE);
				player.getSkills().set(Constants.RANGE, level2 >= realLevel2 ? realLevel2 + 4 : level2 + 4);
			}
		},

		SPICY_STEW_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				if (Utils.random(100) > 5) {
					int level = player.getSkills().getLevel(Constants.COOKING);
					int realLevel = player.getSkills().getLevelForXp(Constants.COOKING);
					player.getSkills().set(Constants.COOKING, level >= realLevel ? realLevel + 6 : level + 6);
				} else {
					int level = player.getSkills().getLevel(Constants.COOKING);
					player.getSkills().set(Constants.COOKING, level <= 6 ? 0 : level - 6);
				}
			}

		},

		CABAGE_MESSAGE {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.sendMessage("You don't really like it much.", true);
			}
		},

		PAPAYA {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int restoredEnergy = (int) (player.getRunEnergy() + 5);
				player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
			}
		},

		TEA_MESSAGE {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.setNextForceTalk(new ForceTalk("Aaah, nothing like a nice cuppa tea!"));
				player.removeEffect(com.rs.game.content.Effect.AGGRESSION_POTION);
			}
		},

		ONION_MESSAGE {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.sendMessage("It hurts to see a grown " + (player.getAppearance().isMale() ? "male" : "female") + "cry.");
			}
		},

		SQIRKJUICE_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.getSkills().adjustStat(2, 0.1, true, Constants.THIEVING);

			}
		},

		BARON_SHARK_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.addEffect(com.rs.game.content.Effect.BARON_SHARK, Ticks.fromSeconds(12));
			}
		},
		REFUSE_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.sendMessage("I'm not going to eat that!");
			}
		},
		BLACK_MUSHROOM_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.sendMessage("Eugh! It tastes horrible, and stains your fingers black.");
			}
		},


		ANTIPOISION_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.addEffect(com.rs.game.content.Effect.ANTIPOISON, Ticks.fromSeconds(90));
			}
		},
		SWEETCORN_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int hp = (int) Math.round(player.getMaxHitpoints()*0.10);
				player.heal(hp);
			}
		},
		GOUT_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int run = player.getSkills().getLevel(Constants.AGILITY) + 100;
				player.restoreRunEnergy(run);
			}
		},
		JUJU_GUMBO_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.addEffect(com.rs.game.content.Effect.BARON_SHARK, Ticks.fromSeconds(10));
			}
		},
		KEBAB_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int roll = Utils.random(100);
				if(roll>=95){
					player.sendMessage("Wow, that was an amazing kebab! You feel really invigorated.");
					int healChance = Utils.random(26,32);
					int hp = (int) Math.round(player.getMaxHitpoints()*healChance);
					player.heal(hp);
					player.getSkills().adjustStat(2, 0.1, true, Constants.ATTACK);
					player.getSkills().adjustStat(2, 0.1, true, Constants.STRENGTH);
					player.getSkills().adjustStat(2, 0.1, true, Constants.DEFENSE);
				}
				if(roll >= 90 && roll <= 94){
					player.sendMessage("That tasted very dodgy. You feel very ill. Eating the kebab has done damage to some of your stats.");
					player.getSkills().adjustStat(-3, 0.1, true, Constants.ATTACK);
					player.getSkills().adjustStat(-3, 0.1, true, Constants.STRENGTH);
					player.getSkills().adjustStat(-3, 0.1, true, Constants.DEFENSE);
				}
				if(roll >= 40 && roll <= 89){
					player.sendMessage("It restores some life points.");
					double healChance = Utils.random(7.3,10.0);
					int hp = (int) Math.round(player.getMaxHitpoints()*healChance);
					player.heal(hp);
				}
				if(roll >= 25 && roll <= 39){
					player.sendMessage("That kebab didn't seem to do a lot.");
				}
				if(roll >= 10 && roll <= 24){
					player.sendMessage("That was a good kebab. You feel a lot better.");
					double healChance = Utils.random(14.6,20.0);
					int hp = (int) Math.round(player.getMaxHitpoints()*healChance);
					player.heal(hp);
				}
				if(roll >= 0 && roll <= 9){
					int skill = Utils.random(0,25);
					player.sendMessage("That tasted very dodgy. You feel very ill. Eating the kebab has done damage to some of your " + Constants.SKILL_NAME[skill] + " stats.");
					player.getSkills().adjustStat(-3, 0.1, true, skill);
				}
			}
		},
		MINT_CAKE_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				int run = player.getSkills().getLevel(Constants.AGILITY) + 100;
				player.restoreRunEnergy(run);
			}
		},
		UGTHANKI_KEBAB_EFFECT{
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
					player.heal(190);
					player.sendMessage("Yum!");
			}
		},
		POISION_KARMAMWANNJI_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.applyHit(new Hit(player, 50, HitLook.POISON_DAMAGE));
			}
		},
		CAVE_NIGHTSHADE_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.applyHit(new Hit(player, 15, HitLook.POISON_DAMAGE));
				player.sendMessage("Ahhhh! What have I done");
			}
		},
		ROCK_CAKE_EFFECT {
			@Override
			public void effect(Object object) {
				Player player = (Player) object;
				player.applyHit(new Hit(player, 5, HitLook.POISON_DAMAGE));
				player.sendMessage("Ow! I nearly broke a tooth!");
			}
		};
		public void effect(Object object) {
		}
	}

	public static final Animation EAT_ANIM = new Animation(829);

	public static boolean eat(final Player player, Item item, int slot) {
		return eat(player, item, slot, null);
	}

	public static boolean eat(final Player player, Item item, int slot, Player givenFrom) {
		Food food = Food.forId(item.getId());
		if (food == null)
			return false;
		if (!player.canEat() || !player.getControllerManager().canEat(food))
			return true;
		String name = ItemDefinitions.getDefs(food.getId()).getName().toLowerCase();
		player.sendMessage("You eat the " + name + ".");
		if (Settings.getConfig().isDebug())
			player.sendMessage("You gain " + food.getHeal()  + " HP and " + food.getExtraHP() + " bonus HP.");
		player.incrementCount("Food eaten");
		player.setNextAnimation(EAT_ANIM);
		int foodDelay = name.contains("half") ? 2 : 3;
		player.addFoodDelay(foodDelay);
		player.getActionManager().setActionDelay(player.getActionManager().getActionDelay() + 3);
		player.getInventory().getItems().set(slot, food.getNewId() == 0 ? null : new Item(food.getNewId(), 1));
		player.getInventory().refresh(slot);
		int hp = player.getHitpoints();
		if (ItemConstants.isDungItem(item.getId())) {
			int healed = food.getHeal()/10;
			if (givenFrom != null && givenFrom.getDungManager().getActivePerk() == KinshipPerk.MEDIC)
				healed *= 1.2 + (givenFrom.getDungManager().getKinshipTier(KinshipPerk.MEDIC) * 0.03);
			player.applyHit(new Hit(player, healed, HitLook.HEALED_DAMAGE));
		} else
			player.heal(food.getHeal()/10, food.getExtraHP());
		if(food.restoreRun != 0)
			player.restoreRunEnergy(getRestoreRun(food));
		if (player.getHitpoints() > hp)
			player.sendMessage("It heals some health.");
		player.getInventory().refresh();
		if (food.effect != null)
			food.effect.effect(player);
		return true;
	}

	public static boolean isConsumable(Item item) {
		Food food = Food.forId(item.getId());
		if (food == null)
			return false;
		return true;
	}
	public static int getRestoreRun(Food item) {
		Food food = Food.forId(item.getId());
		if ((food.getRestoreRun() >= 0))
			return food.getRestoreRun();
		else
			return 0;
	}
}