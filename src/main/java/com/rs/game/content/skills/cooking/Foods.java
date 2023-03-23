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
import java.util.function.Consumer;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.Effect;
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
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Foods {

    public static final Animation EAT_ANIM = new Animation(829);

    public static ItemClickHandler eat = new ItemClickHandler(Food.foods.keySet().toArray(), new String[] { "Eat" }, e -> eat(e.getPlayer(), e.getItem(), e.getSlotId(), null));

    public static boolean eat(final Player player, Item item, int slot, Player givenFrom) {
        Food food = Food.forId(item.getId());
        if (food == null)
            return false;
        if (!player.canEat() || !player.getControllerManager().canEat(food))
            return true;
        if (food.heal < 0) {
            player.sendMessage("I'm not going to eat that!");
            return true;
        }
        player.sendMessage("You eat the " + item.getName().toLowerCase() + ".", true);
        player.incrementCount("Food eaten");
        player.setNextAnimation(EAT_ANIM);
        player.addFoodDelay(food.ids.length > 1 ? 2 : 3);
        player.getActionManager().setActionDelay(player.getActionManager().getActionDelay() + 3);
        Item replace = new Item(item.getId(), item.getAmount());
        if (replace.getDefinitions().isStackable())
            replace.setAmount(replace.getAmount()-1);
        else
            replace.setId(food.getReplaceIdFor(item.getId()));
        player.getInventory().getItems().set(slot, replace.getId() <= 0 || replace.getAmount() <= 0 ? null : replace);
        player.getInventory().refresh(slot);
        int hp = player.getHitpoints();
        if (ItemConstants.isDungItem(item.getId())) {
            int healed = food.heal;
            if (givenFrom != null && givenFrom.getDungManager().getActivePerk() == KinshipPerk.MEDIC)
                healed *= 1.2 + (givenFrom.getDungManager().getKinshipTier(KinshipPerk.MEDIC) * 0.03);
            player.applyHit(new Hit(player, healed, HitLook.HEALED_DAMAGE));
        } else
            player.heal(food.heal);
        if (player.getHitpoints() > hp)
            player.sendMessage("It heals some health.");
        player.getInventory().refresh();
        if (food.effect != null)
            food.effect.accept(player);
        return true;
    }

    public static boolean isConsumable(Item item) {
        Food food = Food.forId(item.getId());
        if (food == null)
            return false;
        return true;
    }

    public static enum Food {
        ACAI(20270, 50),
        ADMIRAL_PIE(new int[] { 7198, 7200 }, 2313, 80, p -> p.getSkills().adjustStat(5, 0.0, Constants.FISHING)),
        AMPHIBIOUS_FRUIT(21381, 150),
        ANCHOVY(319, 10),
        ANCHOVY_PIZZA(new int[] { 2297, 2299 }, 90),
        APPLE_PIE(new int[] { 2323, 2335 }, 2313, 70),
        AQUATIC_FRUIT(21380, 150),
        BAGUETTE(6961, 60),
        BAKED_POTATO(6701, 40),
        BANANA(1963, 20),
        BANANA_STEW(4016, 110),
        BARON_SHARK(19948, 200, p -> p.addEffect(Effect.BARON_SHARK, Ticks.fromSeconds(12))),
        BASS(365, 130),
        BAT_SHISH(10964, 20),
        BISCUITS(19467, 20),
        BLACK_MUSHROOM(4620, 0, p -> p.sendMessage("Eugh! It tastes horrible, and stains your fingers black.")),
        BLUE_CRAB(18175, 220),
        BLUE_SWEETS(4558, 2),
        BLURBERRY_SPECIAL(2064, 2),
        BOULDABASS(18171, 170),
        BREAD(2309, 50),
        BUTTON_MUSHROOM(13563, 10),
        CABBAGE(1965, 10, p -> p.sendMessage("You don't really like it much.", true)),
        CAMOUFLAGED_FRUIT(21384, 150),
        CANNIBAL_FRUIT(21379, 150),
        CARRION_FRUIT(21382, 150),
        CAVEFISH(15266, 220),
        CAVE_EEL(5003, 110),
        CAVE_MORAY(18177, 250),
        CAVE_NIGHTSHADE(2398, 0, player -> {
            player.applyHit(new Hit(player, 15, HitLook.POISON_DAMAGE));
            player.sendMessage("Ahhhh! What have I done");
        }),
        CAVIAR(11326, 50),
        CELEBRATION_CAKE_1(20179, 20),
        CELEBRATION_CAKE_2(20181, 20),
        CELEBRATION_CAKE_3(20182, 20),
        CHEESE(1985, 20),
        CHEESEPTOM_BATTA(2259, 70),
        CHEESE_AND_TOMATO_BATTA(9535, 110),
        CHEESE_WHEEL(18789, 20),
        CHICKEN(2140, 30),
        CHILLI_CON_CARNIE(new int[] { 7062 }, 1923, 50),
        CHILLI_POTATO(7054, 140),
        CHOCCHIP_CRUNCHIES(2209, 70),
        CHOCOLATEY_MILK(1977, 40),
        CHOCOLATE_BAR(1973, 30),
        CHOCOLATE_BOMB(2185, 150),
        CHOCOLATE_CAKE(new int[] { 1897, 1899, 1901 }, 50),
        CAKE(new int[] { 1891, 1893, 1895 }, 40),
        CHOCOLATE_DROP(14083, 30),
        CHOCOLATE_EGG_1(12646, 10),
        CHOCOLATE_EGG(12648, 10),
        CHOCOLATE_KEBBIT(11026, -1),
        CHOCOTREAT(24148, 80),
        CHOC_ICE(6794, 70),
        CHOC_SATURDAY(2074, 50),
        CHOMPY(2878, 60),
        CHOPPED_ONION(new int[] { 1871 }, 1923, 50),
        CHOPPED_TOMATO(new int[] { 1869 }, 1923, 50),
        CHOPPED_TUNA(new int[] { 7086 }, 1923, 50),
        COATED_FROGS_LEGS(10963, 20),
        COD(339, 180),
        COMMON_FRUIT(21376, 150),
        COOKED_CHICKEN(2140, 30),
        COOKED_CHOMPY(2878, 30),
        COOKED_CRAB_MEAT_1(7521, 10),
        COOKED_CRAB_MEAT_2(7523, 10),
        COOKED_CRAB_MEAT_3(7524, 10),
        COOKED_CRAB_MEAT_4(7525, 10),
        COOKED_CRAB_MEAT_5(7526, 10),
        COOKED_FISHCAKE(7530, 110),
        COOKED_JUBBLY(7568, 150),
        COOKED_KARAMBWAN(3144, 180),
        COOKED_MEAT(2142, 30),
        COOKED_OOMLIE_WRAP(2343, 140),
        COOKED_RABBIT(3228, 20),
        COOKED_SLIMY_EEL(3381, 70),
        COOKED_SWEETCORN(5988, 0, p -> p.heal((int) Math.round(p.getMaxHitpoints() * 0.10))),
        COOKED_TURKEY(14540, 30),
        COOKED_TURKEY_DRUMSTICK(14543, 20),
        CORONATION_CHICKEN_SANDWICH(24398, 20),
        CRAB_MEAT(7521, -1),
        CRAYFISH(13433, 20),
        CREAM_TEA(24396, 20),
        CRUMBLY_BITS(24179, 0),
        CRUNCHY_RUNE_ROCKS(20838, 0),
        CURRY(new int[] { 2011 }, 1923, 190),
        DEEP_BLUE_SWEETS(4559, 20),
        DISEASED_FRUIT(21383, 100),
        DOUGHNUT(14665, 20),
        DRACONIC_FRUIT(21385, 100),
        DRUGGED_MEAT(15277, -1),
        DUSK_EEL(18163, 70),
        DWELLBERRIES(2126, 20),
        EASTER_EGG(1961, 20),
        EASTER_EGG1(7928, 20),
        EASTER_EGG2(7929, 20),
        EASTER_EGG3(7930, 20),
        EASTER_EGG4(7931, 20),
        EASTER_EGG5(7932, 20),
        EASTER_EGG6(7933, 20),
        EASTER_EGG7(12644, 20),
        EASTER_EGG8(12643, 20),
        EASTER_EGG9(1961, 20),
        EASTER_EGG10(12642, 20),
        EASTER_EGG11(12641, 20),
        EASTER_EGG12(12640, 20),
        EASTER_EGG13(12639, 20),
        EASTER_EGG14(1961, 20),
        EASTER_EGG15(1961, 20),
        EASTER_EGG16(1961, 20),
        EASTER_EGG17(1961, 20),
        EASTER_EGG18(1961, 20),
        EDIBLE_SEAWEED(403, 20),
        EELSUSHI(10971, 20),
        EGG_AND_TOMATO(new int[] { 7064 }, 1923, 80),
        EGG_POTATO(7056, 160),
        EQUA_LEAVES(2128, 10),
        EVIL_DRUMSTICK(24147, 10),
        EVIL_TURNIP(new int[] { 12134, 12136, 12138 }, 60),
        FAT_SNAIL_MEAT(3373, 80, p -> p.heal(Utils.random(20))),
        FIELD_RATION(7934, 50),
        FILLETS(10969, 25),
        FINGERS(10965, 20),
        FISHCAKE(7530, 110),
        FISH_LIKE_THING(6202, -1),
        FISH_N_CHIPS(24400, 50),
        FISH_PIE(new int[] { 7188, 7190 }, 2313, p -> p.getSkills().adjustStat(3, 0.0, Constants.FISHING)),
        FOOD_CLASS_1(14162, 40),
        FOOD_CLASS_2(14164, 80),
        FOOD_CLASS_3(14166, 120),
        FOOD_CLASS_4(14168, 160),
        FOOD_CLASS_5(1417, 200),
        FRESH_MONKFISH(7943, 100),
        FRIED_MUSHROOMS(new int[] { 7082 }, 1923, 50),
        FRIED_ONIONS(new int[] { 7084 }, 1923, 50),
        FROGBURGER(10962, 20),
        FROGSPAWN_GUMBO(10961, 20),
        FROG_SPAWN(5004, 20),
        FRUIT_BATTA(2277, 110),
        FULL_BREAKFAST(24404, 100),
        FURY_SHARK(20429, 280),
        GARDEN_PIE(new int[] { 7178, 7180 }, 2313, p -> p.getSkills().adjustStat(3, 0.0, Constants.FARMING)),
        GIANT_CARP(337, -1),
        GIANT_FLATFISH(18165, 100),
        GIANT_FROG_LEGS(4517, 60),
        GOUT_TUBER(6311, 10, p -> p.restoreRunEnergy(100)),
        GREEN_GLOOP_SOUP(10960, 20),
        GREEN_SWEETS(4563, 20),
        GRUBS_A_LA_MODE(10966, 25),
        GUTHIX_FRUIT(21387, 200),
        HALF_WINE_JUG(1989, 87),
        HEIM_CRAB(18159, 20),
        HERRING(347, 20),
        HUMBLE_PIE(18767, -1),
        IGNEOUS_FRUIT(21378, 150),
        JANGERBERRIES(247, 20),
        JUBBLY(7568, 150),
        JUJU_GUMBO(19949, 320, p -> p.addEffect(Effect.BARON_SHARK, Ticks.fromSeconds(12))),
        KARAMBWANI(3144, 30),
        KARAMBWANJI(3151, 30),
        KEBAB(1971, 0, KEBAB_EFFECT),
        KING_WORM(2162, 20),
        LAVA_EEL(2149, 110),
        LEAN_SNAIL_MEAT(3371, 80),
        LEMON(2102, 20),
        LEMON_CHUNKS(2104, 20),
        LEMON_SLICES(2106, 20),
        LIME(2120, 20),
        LIME_CHUNKS(2122, 20),
        LIME_SLICES(2124, 20),
        LOACH(10970, 30),
        LOBSTER(379, 120),
        LOCUST_MEAT(9052, 20),
        MACKEREL(355, 60),
        MAGIC_EGG(11023, 20),
        MANTA(391, 220),
        MEAT(2142, 30),
        MEAT_PIE(new int[] { 2327, 2331 }, 2313, 60),
        MEAT_PIZZA(new int[] { 2293, 2295 }, 80),
        MINCED_MEAT(new int[] { 7070 }, 1923, 2),
        MINT_CAKE(9475, 0, p -> p.restoreRunEnergy(100)),
        MONKEY_BAR(4014, 90),
        MONKEY_NUTS(4012, 20),
        MONKFISH(7946, 160),
        MUSHROOMS(10968, 25),
        MUSHROOM_AND_ONIONS(new int[] { 7066 }, 1923, 110),
        MUSHROOM_AND_ONION_POTATO(7058, 200),
        NOT_MEAT(20837, 20),
        ODD_CRUNCHIES(2197, 10),
        OKTOBERFEST_PRETZEL(19778, 20),
        ONION(1957, 10, p -> p.sendMessage("It hurts to see a grown " + (p.getAppearance().isMale() ? "man" : "woman") + "cry.")),
        ONION_AND_TOMATO(new int[] { 1875 }, 1923, 20),
        OOMILE(2343, 140),
        ORANGE(2108, 20),
        ORANGE_CHUNKS(211, 20),
        ORANGE_SLICES(2112, 20),
        PAPAYA(5972, 80, p -> p.restoreRunEnergy(5)),
        PEACH(6883, 80),
        PIKE(351, 80),
        PINEAPPLE_CHUNKS(2116, 20),
        PINEAPPLE_PIZZA(new int[] { 2301, 2303 }, 110),
        PINEAPPLE_RING(2118, 20),
        PINK_SWEETS(4564, 20),
        PLAIN_PIZZA_FULL(new int[] { 2289, 2291 }, 70),
        PLANT_BITS(2418, -1),
        PM_CHEESE_AND_TOMATO_BATTA(2223, 110),
        PM_CHOCOCHIP_CRUNCHIES(2239, 70),
        PM_CHOCOLATE_BOMB(2229, 150),
        PM_FRUIT_BATTA(2225, 110),
        PM_SPICY_CRUNCHIES(2241, 70),
        PM_TANGLED_TOAD_LEGS(2231, 150),
        PM_TOAD_BATTA(2221, 110),
        PM_TOAD_CRUNCHIES(2243, 80),
        PM_VEGETABLE_BATTA(2227, 110),
        PM_WORM_BATTA(2219, 110),
        PM_WORM_CRUNCHIES(2237, 80),
        PM_WORM_HOLE(2233, 120),
        POISONED_CHEESE(6768, -1),
        POISON_KARAMBWAN(3146, 0, p -> p.applyHit(new Hit(p, 50, HitLook.POISON_DAMAGE))),
        POORLY_COOKED_BEAST_MEAT(23062, 20),
        POORLY_COOKED_BIRD_MEAT(23060, 20),
        POPCORN_BALL(14082, 30),
        POTATO_WITH_BUTTER(6703, 140),
        POTATO_WITH_CHEESE(6705, 160),
        POT_OF_CREAM(2130, 20),
        PUMPKIN(1959, 20),
        PUNCH(22329, 20),
        PURPLE_SWEETS(4561, 0, p -> {
            p.heal(Utils.random(10, 30));
            p.restoreRunEnergy(10);
        }),
        PURPLE_SWEETS2(10476, 0, p -> {
            p.heal(Utils.random(10, 30));
            p.restoreRunEnergy(10);
        }),
        RABBIT_SANDWICH(23065, 42),
        RABIT(3228, 50),
        RAINBOW_FISH(10136, 110),
        REDBERRY_PIE(new int[] { 2325, 2333 }, 2313, 50),
        RED_BANANA(7572, 50),
        RED_EYE(18161, 50),
        RED_SWEETS(4562, 20),
        ROASTED_BEAST_MEAT(9988, 42),
        ROASTED_BIRD_MEAT(9980, 42),
        ROAST_BEAST_MEAT(9988, 42),
        ROAST_BIRD_MEAT(998, 60),
        ROAST_FROG(10967, 50),
        ROAST_POTATOES(15429, 100),
        ROAST_RABBIT(7223, 70),
        ROCKTAIL(15272, 0, p -> p.heal(230, 100)),
        ROE(11324, 30),
        ROLL(6963, 20),
        ROTTEN_APPLE(1984, -1),
        SALMON(329, 90),
        SALVE_EEL(18173, 200),
        SARADOMIN_FRUIT(21386, 200),
        SARDINE(325, 40),
        SCORPION_MEAT(22342, 80),
        SCRAMBLED_EGG(new int[] { 7078 }, 1923, 50),
        SEASONED_LEGS(2158, 1),
        SEAWEED_SANDWICH(3168, -1),
        SEA_MEAT_1(20831, 250),
        SEA_MEAT_2(24182, 250),
        SEA_TURTLE(397, 210),
        SHADOW_FRUIT(21377, 150),
        SHARK(385, 200),
        SHORT_FINNED_EEL(18167, 120),
        SHRIMP(315, 30),
        SHRUNK_OGLEROOT(11205, 20),
        SKEWERED_KEBAB(15123, 90),
        SLICED_BANANA(3162, 20),
        SLICED_RED_BANANA(7574, 20),
        SLIMY_EEL(3381, 70),
        SPICEY_SAUCE(new int[] { 7072 }, 1923, 20),
        SPICY_CRUNCHIES(2213, 20),
        SPICY_MINCED_MEAT(9996, 20),
        SPICY_SAUCE(7072, 20),
        SPICY_TOADS_LEGS(2156, 20),
        SPICY_TOMATO(9994, 20),
        SPICY_WORM(216, 20),
        SPIDER_ON_SHAFT(6299, 20),
        SPIDER_ON_STICK(6297, 20),
        SPINACH_ROLL(1969, 2),
        SQUARE_SANDWICH(6965, 25),
        STEAK_AND_KIDNEY_PIE(24402, 50),
        STEW(new int[] { 2003 }, 1923, 110),
        STRAWBERRY(5504, 10, p -> p.heal((int) (p.getMaxHitpoints() * 0.06))),
        STUFFED_SNAKE(7579, 200),
        SUMMER_PIE_FULL(new int[] { 7218, 7220 }, 2313, 110, p -> {
            p.getSkills().adjustStat(5, 0.0, Constants.AGILITY);
            p.restoreRunEnergy(10);
        }),
        SUMMER_SQIRKJUICE(10849, 150, p -> p.getSkills().adjustStat(2, 0.1, true, Constants.THIEVING)),
        SUPER_KEBAB(4608, 0, KEBAB_EFFECT),
        SWORDFISH(373, 140),
        TANGLED_TOAD_LEGS(2187, 150),
        TCHIKI_MONKEY_NUTS(7573, 2),
        TCHIKI_NUT_PASTE(7575, 2),
        TEA(new int[] { 1978 }, 1980, 20, player -> {
            player.setNextForceTalk(new ForceTalk("Aaah, nothing like a nice cuppa tea!"));
            player.removeEffect(Effect.AGGRESSION_POTION);
        }),
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
        TUNA_AND_CORN(new int[] { 7068 }, 1923, 130),
        TUNA_POTATO(7060, 220),
        TURKEY_DRUMSTICK(15428, 20),
        UGTHANKI_KEBAB_1(1883, 190, p -> p.forceTalk("Yum!")),
        UGTHANKI_KEBAB_2(1885, 190, p -> p.forceTalk("Yum!")),
        UGTHANKI_MEAT(1861, 20),
        UNFINISHED_BATTA(2261, 20),
        UNFINISHED_BATTA1(2263, 20),
        UNFINISHED_BATTA2(2265, 20),
        UNFINISHED_BATTA3(2267, 20),
        UNFINISHED_BATTA4(2269, 20),
        UNFINISHED_BATTA5(2271, 20),
        UNFINISHED_BATTA6(2273, 20),
        VEGETABLE_BATTA7(2281, 20),
        VEG_BALL(2195, 120),
        WATERMELON_SLICE(5984, 20),
        WEB_SNIPER(18169, 150),
        WHITE_PEARL(4485, 20),
        WHITE_SWEETS(456, 20),
        WHITE_TREE_FRUIT(6469, 20),
        WILD_PIE(new int[] { 7208, 7210 }, 2313, 110, p -> {
            p.getSkills().adjustStat(4, 0.0, Constants.RANGE);
            p.getSkills().adjustStat(5, 0.0, Constants.SLAYER);
        }),
        WORM_BATTA(2253, 110),
        WORM_CRUNCHIES(2205, 80),
        WORM_HOLE(2191, 120),
        WRAPPED_CANDY(14084, 20),
        YULE_LOG(1543, 20),
        YULE_LOGS(15430, 20),
        ZAMORAK_FRUIT(21388, 200);

        private static Map<Integer, Food> foods = new HashMap<>();

        static {
            for (final Food food : Food.values())
                for (int id : food.ids)
                    foods.put(id, food);
        }

        private int[] ids;
        private int container;
        private int heal;

        private Consumer<Player> effect;

        private Food(int[] ids, int container, int heal, Consumer<Player> effect) {
            this.ids = ids;
            this.container = container;
            this.heal = heal;
            this.effect = effect;
        }

        private Food(int[] ids, int heal, Consumer<Player> effect) {
            this(ids, -1, heal, effect);
        }

        private Food(int id, int heal, Consumer<Player> effect) {
            this(new int[] { id }, -1, heal, effect);
        }

        private Food(int id, int heal) {
            this.ids = new int[] { id };
            this.heal = heal;
        }

        private Food(int[] ids, int container, int heal) {
            this.ids = ids;
            this.container = container;
            this.heal = heal;
        }

        private Food(int[] ids, int heal) {
            this(ids, -1, heal);
        }

        public static Food forId(int itemId) {
            return foods.get(itemId);
        }

        public int getReplaceIdFor(int id) {
            if (ids.length <= 1)
                return -1;
            int index = 0;
            for (int i = 0;i < ids.length;i++) {
                if (ids[i] == id)
                    index = i;
            }
            index++;
            if (index >= ids.length)
                return container <= 0 ? -1 : container;
            return ids[index];
        }
    }

    private static Consumer<Player> KEBAB_EFFECT = player -> {
        int roll = Utils.random(100);
        if (roll >= 95) {
            player.sendMessage("Wow, that was an amazing kebab! You feel really invigorated.");
            int healChance = Utils.random(26, 32);
            int hp = (int) Math.round(player.getMaxHitpoints() * healChance);
            player.heal(hp);
            player.getSkills().adjustStat(2, 0.1, true, Constants.ATTACK);
            player.getSkills().adjustStat(2, 0.1, true, Constants.STRENGTH);
            player.getSkills().adjustStat(2, 0.1, true, Constants.DEFENSE);
        } else if (roll >= 90 && roll <= 94) {
            player.sendMessage("That tasted very dodgy. You feel very ill. Eating the kebab has done damage to some of your stats.");
            player.getSkills().adjustStat(-3, 0.1, true, Constants.ATTACK);
            player.getSkills().adjustStat(-3, 0.1, true, Constants.STRENGTH);
            player.getSkills().adjustStat(-3, 0.1, true, Constants.DEFENSE);
        } else if (roll >= 40 && roll <= 89) {
            player.sendMessage("It restores some life points.");
            double healChance = Utils.random(7.3, 10.0);
            int hp = (int) Math.round(player.getMaxHitpoints() * healChance);
            player.heal(hp);
        } else if (roll >= 25 && roll <= 39) {
            player.sendMessage("That kebab didn't seem to do a lot.");
        } else if (roll >= 10 && roll <= 24) {
            player.sendMessage("That was a good kebab. You feel a lot better.");
            double healChance = Utils.random(14.6, 20.0);
            int hp = (int) Math.round(player.getMaxHitpoints() * healChance);
            player.heal(hp);
        } else if (roll >= 0 && roll <= 9) {
            int skill = Utils.random(0, 25);
            player.sendMessage("That tasted very dodgy. You feel very ill. Eating the kebab has done damage to some of your " + Constants.SKILL_NAME[skill] + " stats.");
            player.getSkills().adjustStat(-3, 0.1, true, skill);
        }
    };
}