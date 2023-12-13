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
package com.rs.game.content;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.utils.Ticks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@PluginEventHandler
public class Potions {

	public static final int VIAL = 229;
	public static final int JUJU_VIAL = 19996;
	public static final int BEER_GLASS = 1919;
	public static final int EMPTY_KEG = 5769;
	private static int EMPTY_CUP = 4244;
	private static int BOWL = 1923;
	private static int EMPTY_JUG = 1935;

	public enum Potion {
		CUP_OF_TEA(1980, new int[] { 712, 1978, 4242, 4243, 4245, 4246, 4838, 7730, 7731, 7733, 7734, 7736, 7737 }, p -> {
			p.heal(30);
			p.getSkills().adjustStat(3, 0, Constants.ATTACK);
			p.setNextForceTalk(new ForceTalk("Aaah, nothing like a nice cuppa tea!"));
		}),
		CUP_OF_TEA_CLAY(7728, 7730, p -> {
			p.getSkills().adjustStat(1, 0, Constants.CONSTRUCTION);
		}),
		CUP_OF_TEA_CLAY_MILK(7728, 7731, p -> {
			p.getSkills().adjustStat(1, 0, Constants.CONSTRUCTION);
		}),
		CUP_OF_TEA_PORCELAIN(7732, 7733, p -> {
			p.getSkills().adjustStat(2, 0, Constants.CONSTRUCTION);
		}),
		CUP_OF_TEA_PORCELAIN_MILK(7732, 7734, p -> {
			p.getSkills().adjustStat(2, 0, Constants.CONSTRUCTION);
		}),
		CUP_OF_TEA_GOLD(7735, 7736, p -> {
			p.getSkills().adjustStat(3, 0, Constants.CONSTRUCTION);
		}),
		CUP_OF_TEA_GOLD_MILK(7735, 7737, p -> {
			p.getSkills().adjustStat(3, 0, Constants.CONSTRUCTION);
		}),
		NETTLE_TEA_CUP(EMPTY_CUP, 4245, p -> {
			p.restoreRunEnergy(5);
			p.heal(30);
		}),
		NETTLE_TEA_CUP_MILK(EMPTY_CUP, 4246, p -> {
			p.restoreRunEnergy(5);
			p.heal(30);
		}),
		NETTLE_TEA_BOWL(BOWL, 4239, p -> {
			p.restoreRunEnergy(5);
			p.heal(30);
		}),
		NETTLE_TEA_BOWL_MILK(BOWL, 4240, p -> {
			p.restoreRunEnergy(5);
			p.heal(30);
		}),
		NETTLE_WATER(BOWL, 4237, p -> p.heal(10)),
		ATTACK_POTION(VIAL, new int[] { 2428, 121, 123, 125 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.ATTACK)),
		ATTACK_FLASK(-1, new int[] { 23195, 23197, 23199, 23201, 23203, 23205 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.ATTACK)),
		ATTACK_MIX(VIAL, new int[] { 11429, 11431 }, p -> {
			p.getSkills().adjustStat(3, 0.1, Constants.ATTACK);
			p.heal(30);
		}),

		STRENGTH_POTION(VIAL, new int[] { 113, 115, 117, 119 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.STRENGTH)),
		STRENGTH_FLASK(-1, new int[] { 23207, 23209, 23211, 23213, 23215, 23217 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.STRENGTH)),
		STRENGTH_MIX(VIAL, new int[] { 11443, 11441 }, p -> {
			p.getSkills().adjustStat(3, 0.1, Constants.STRENGTH);
			p.heal(30);
		}),

		DEFENCE_POTION(VIAL, new int[] { 2432, 133, 135, 137 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.DEFENSE)),
		DEFENCE_FLASK(-1, new int[] { 23231, 23233, 23235, 23237, 23239, 23241 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.DEFENSE)),
		DEFENCE_MIX(VIAL, new int[] { 11457, 11459 }, p -> {
			p.getSkills().adjustStat(3, 0.1, Constants.DEFENSE);
			p.heal(30);
		}),

		COMBAT_POTION(VIAL, new int[] { 9739, 9741, 9743, 9745 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE)),
		COMBAT_FLASK(-1, new int[] { 23447, 23449, 23451, 23453, 23455, 23457 }, p -> p.getSkills().adjustStat(3, 0.1, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE)),
		COMBAT_MIX(VIAL, new int[] { 11445, 11447 }, p -> {
			p.getSkills().adjustStat(3, 0.1, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(30);
		}),

		SUPER_ATTACK(VIAL, new int[] { 2436, 145, 147, 149 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.ATTACK)),
		SUPER_ATTACK_FLASK(-1, new int[] { 23255, 23257, 23259, 23261, 23263, 23265 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.ATTACK)),
		CW_SUPER_ATTACK_POTION(-1, new int[] { 18715, 18716, 18717, 18718 }, p -> {
			p.getSkills().adjustStat(5, 0.15, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.getSkills().adjustStat(4, 0.10, Constants.RANGE);
			p.getSkills().adjustStat(5, 0, Constants.MAGIC);
		}),
		SUPER_ATTACK_MIX(VIAL, new int[] { 11469, 11471 }, p -> {
			p.getSkills().adjustStat(5, 0.15, Constants.ATTACK);
			p.heal(30);
		}),

		SUPER_STRENGTH(VIAL, new int[] { 2440, 157, 159, 161 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.STRENGTH)),
		SUPER_STRENGTH_FLASK(-1, new int[] { 23279, 23281, 23283, 23285, 23287, 23289 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.STRENGTH)),
		CW_SUPER_STRENGTH_POTION(-1, new int[] { 18719, 18720, 18721, 18722 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.STRENGTH)),
		SUPER_STRENGTH_MIX(VIAL, new int[] { 11485, 11487 }, p -> {
			p.getSkills().adjustStat(5, 0.15, Constants.STRENGTH);
			p.heal(30);
		}),

		SUPER_DEFENCE(VIAL, new int[] { 2442, 163, 165, 167 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.DEFENSE)),
		SUPER_DEFENCE_FLASK(-1, new int[] { 23291, 23293, 23295, 23297, 23299, 23301 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.DEFENSE)),
		CW_SUPER_DEFENCE_POTION(-1, new int[] { 18723, 18724, 18725, 18726 }, p -> p.getSkills().adjustStat(5, 0.15, Constants.DEFENSE)),
		SUPER_DEFENCE_MIX(VIAL, new int[] { 11497, 11499 }, p -> {
			p.getSkills().adjustStat(5, 0.15, Constants.DEFENSE);
			p.heal(30);
		}),

		RANGING_POTION(VIAL, new int[] { 2444, 169, 171, 173 }, p -> p.getSkills().adjustStat(4, 0.10, Constants.RANGE)),
		RANGING_FLASK(-1, new int[] { 23303, 23305, 23307, 23309, 23311, 23313 }, p -> p.getSkills().adjustStat(4, 0.10, Constants.RANGE)),
		CW_SUPER_RANGING_POTION(-1, new int[] { 18731, 18732, 18733, 18734 }, p -> p.getSkills().adjustStat(4, 0.10, Constants.RANGE)),
		RANGING_MIX(VIAL, new int[] { 11509, 11511 }, p -> {
			p.getSkills().adjustStat(4, 0.10, Constants.RANGE);
			p.heal(30);
		}),

		MAGIC_POTION(VIAL, new int[] { 3040, 3042, 3044, 3046 }, p -> p.getSkills().adjustStat(5, 0, Constants.MAGIC)),
		MAGIC_FLASK(-1, new int[] { 23423, 23425, 23427, 23429, 23431, 23433 }, p -> p.getSkills().adjustStat(5, 0, Constants.MAGIC)),
		CW_SUPER_MAGIC_POTION(-1, new int[] { 18735, 18736, 18737, 18738 }, p -> p.getSkills().adjustStat(5, 0, Constants.MAGIC)),
		MAGIC_MIX(VIAL, new int[] { 11513, 11515 }, p -> {
			p.getSkills().adjustStat(5, 0, Constants.MAGIC);
			p.heal(30);
		}),

		RESTORE_POTION(VIAL, new int[] { 2430, 127, 129, 131 }, p -> p.getSkills().adjustStat(10, 0.3, false, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE, Constants.RANGE, Constants.MAGIC)),
		RESTORE_FLASK(-1, new int[] { 23219, 23221, 23223, 23225, 23227, 23229 }, p -> p.getSkills().adjustStat(10, 0.3, false, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE, Constants.RANGE, Constants.MAGIC)),
		RESTORE_MIX(VIAL, new int[] { 11449, 11451 }, p -> {
			p.getSkills().adjustStat(10, 0.3, false, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE, Constants.RANGE, Constants.MAGIC);
			p.heal(30);
		}),

		PRAYER_POTION(VIAL, new int[] { 2434, 139, 141, 143 }, p -> p.getPrayer().restorePrayer(((int) (Math.floor(p.getSkills().getLevelForXp(Constants.PRAYER) * 2.5) + 70)))),
		PRAYER_FLASK(-1, new int[] { 23243, 23245, 23247, 23249, 23251, 23253 }, p -> p.getPrayer().restorePrayer(((int) (Math.floor(p.getSkills().getLevelForXp(Constants.PRAYER) * 2.5) + 70)))),
		PRAYER_MIX(VIAL, new int[] { 11465, 11467 }, p -> {
			p.getPrayer().restorePrayer(((int) (Math.floor(p.getSkills().getLevelForXp(Constants.PRAYER) * 2.5) + 70)));
			p.heal(30);
		}),

		SUPER_RESTORE(VIAL, new int[] { 3024, 3026, 3028, 3030 }, p -> {
			p.getSkills().adjustStat(8, 0.25, false, Utils.range(0, Skills.SIZE-1));
			p.getPrayer().restorePrayer(((int) (p.getSkills().getLevelForXp(Constants.PRAYER) * 0.33 * 10)));
		}),
		SUPER_RESTORE_FLASK(-1, new int[] { 23399, 23401, 23403, 23405, 23407, 23409 }, p -> {
			p.getSkills().adjustStat(8, 0.25, false, Utils.range(0, Skills.SIZE-1));
			p.getPrayer().restorePrayer(((int) (p.getSkills().getLevelForXp(Constants.PRAYER) * 0.33 * 10)));
		}),
		DOM_SUPER_RESTORE(-1, new int[] { 22379, 22380 }, p -> {
			p.getSkills().adjustStat(8, 0.25, false, Utils.range(0, Skills.SIZE-1));
			p.getPrayer().restorePrayer(((int) (p.getSkills().getLevelForXp(Constants.PRAYER) * 0.33 * 10)));
		}),
		SUPER_RESTORE_MIX(VIAL, new int[] { 11493, 11495 }, p -> {
			p.getSkills().adjustStat(8, 0.25, false, Utils.range(0, Skills.SIZE-1));
			p.getPrayer().restorePrayer(((int) (p.getSkills().getLevelForXp(Constants.PRAYER) * 0.33 * 10)));
			p.heal(30);
		}),

		PRAYER_RENEWAL(VIAL, new int[] { 21630, 21632, 21634, 21636 }, p -> p.addEffect(Effect.PRAYER_RENEWAL, 500)),
		PRAYER_RENEWAL_FLASK(-1, new int[] { 23609, 23611, 23613, 23615, 23617, 23619 }, p -> p.addEffect(Effect.PRAYER_RENEWAL, 500)),

		ANTIPOISON(VIAL, new int[] { 2446, 175, 177, 179 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromSeconds(90))),
		ANTIPOISON_FLASK(-1, new int[] { 23315, 23317, 23319, 23321, 23323, 23325 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromSeconds(90))),
		ANTIPOISON_MIX(VIAL, new int[] { 11433, 11435 }, p -> {
			p.addEffect(Effect.ANTIPOISON, Ticks.fromSeconds(90));
			p.heal(30);
		}),

		SUPER_ANTIPOISON(VIAL, new int[] { 2448, 181, 183, 185 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6))),
		SUPER_ANTIPOISON_FLASK(-1, new int[] { 23327, 23329, 23331, 23333, 23335, 23337 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6))),
		DOM_SUPER_ANTIPOISON(-1, new int[] { 22377, 22378 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6))),
		ANTI_P_SUPERMIX(VIAL, new int[] { 11473, 11475 }, p -> {
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6));
			p.heal(30);
		}),

		ANTIPOISONP(VIAL, new int[] { 5943, 5945, 5947, 5949 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(9))),
		ANTIPOISONP_FLASK(-1, new int[] { 23579, 23581, 23583, 23585, 23587, 23589 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(9))),
		ANTIDOTEP_MIX(VIAL, new int[] { 11501, 11503 }, p -> {
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(9));
			p.heal(30);
		}),

		ANTIPOISONPP(VIAL, new int[] { 5952, 5954, 5956, 5958 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(12))),
		ANTIPOISONPP_FLASK(-1, new int[] { 23591, 23593, 23595, 23597, 23599, 23601 }, p -> p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(12))),

		RELICYMS_BALM(VIAL, new int[] { 4842, 4844, 4846, 4848 }, p -> { /*TODO*/ }),
		RELICYMS_BALM_FLASK(-1, new int[] { 23537, 23539, 23541, 23543, 23545, 23547 }, p -> { /*TODO*/ }),
		RELICYMS_MIX(VIAL, new int[] { 11437, 11439 }, p -> {
			//TODO
			p.heal(30);
		}),

		ZAMORAK_BREW(VIAL, new int[] { 2450, 189, 191, 193 }, p -> {
			p.getSkills().adjustStat(2, 0.2, Constants.ATTACK);
			p.getSkills().adjustStat(2, 0.12, Constants.STRENGTH);
			p.getSkills().adjustStat(-2, -0.1, Constants.DEFENSE);
			p.applyHit(new Hit(null, (int) (p.getHitpoints()*0.12), HitLook.TRUE_DAMAGE));
		}),
		ZAMORAK_BREW_FLASK(-1, new int[] { 23339, 23341, 23343, 23345, 23347, 23349 }),
		ZAMORAK_MIX(VIAL, new int[] { 11521, 11523 }, p -> {
			p.heal(30);
		}),

		ANTIFIRE(VIAL, new int[] { 2452, 2454, 2456, 2458 }, p -> p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(6))),
		ANTIFIRE_FLASK(-1, new int[] { 23363, 23365, 23367, 23369, 23371, 23373 }, p -> p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(6))),
		ANTIFIRE_MIX(VIAL, new int[] { 11505, 11507 }, p -> {
			p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(6));
			p.heal(30);
		}),

		ENERGY_POTION(VIAL, new int[] { 3008, 3010, 3012, 3014 }, p -> p.restoreRunEnergy(20)),
		ENERGY_FLASK(-1, new int[] { 23375, 23377, 23379, 23381, 23383, 23385 }, p -> p.restoreRunEnergy(20)),
		ENERGY_MIX(VIAL, new int[] { 11453, 11455 }, p -> {
			p.restoreRunEnergy(20);
			p.heal(30);
		}),

		SUPER_ENERGY(VIAL, new int[] { 3016, 3018, 3020, 3022 }, p -> p.restoreRunEnergy(40)),
		SUPER_ENERGY_FLASK(-1, new int[] { 23387, 23389, 23391, 23393, 23395, 23397 }, p -> p.restoreRunEnergy(40)),
		CW_SUPER_ENERGY_POTION(-1, new int[] { 18727, 18728, 18729, 18730 }, p -> {
			p.restoreRunEnergy(40);
		}),
		SUPER_ENERGY_MIX(VIAL, new int[] { 11481, 11483 }, p -> {
			p.restoreRunEnergy(40);
			p.heal(30);
		}),

		GUTHIX_REST(VIAL, new int[] { 4417, 4419, 4421, 4423 }, p -> {
			p.restoreRunEnergy(5);
			p.heal(50, 50);
			p.getPoison().lowerPoisonDamage(10);
		}),

		SARADOMIN_BREW(VIAL, new int[] { 6685, 6687, 6689, 6691 }, p -> {
			int hpChange = (int) (p.getMaxHitpoints() * 0.15);
			p.heal(hpChange + 20, hpChange);
			p.getSkills().adjustStat(2, 0.2, Constants.DEFENSE);
			p.getSkills().adjustStat(-2, -0.1, Constants.ATTACK, Constants.STRENGTH, Constants.MAGIC, Constants.RANGE);
		}),
		SARADOMIN_BREW_FLASK(-1, new int[] { 23351, 23353, 23355, 23357, 23359, 23361 }, p -> {
			int hpChange = (int) (p.getMaxHitpoints() * 0.15);
			p.heal(hpChange + 20, hpChange);
			p.getSkills().adjustStat(2, 0.2, Constants.DEFENSE);
			p.getSkills().adjustStat(-2, -0.1, Constants.ATTACK, Constants.STRENGTH, Constants.MAGIC, Constants.RANGE);
		}),
		DOM_SARADOMIN_BREW(-1, new int[] { 22373, 22374 }, p -> {
			int hpChange = (int) (p.getMaxHitpoints() * 0.15);
			p.heal(hpChange + 20, hpChange);
			p.getSkills().adjustStat(2, 0.2, Constants.DEFENSE);
			p.getSkills().adjustStat(-2, -0.1, Constants.ATTACK, Constants.STRENGTH, Constants.MAGIC, Constants.RANGE);
		}),

		MAGIC_ESSENCE(VIAL, new int[] { 9021, 9022, 9023, 9024 }, p -> p.getSkills().adjustStat(4, 0, Constants.MAGIC)),
		MAGIC_ESSENCE_FLASK(-1, new int[] { 23633, 23634, 23635, 23636, 23637, 23638 }, p -> p.getSkills().adjustStat(4, 0, Constants.MAGIC)),
		MAGIC_ESSENCE_MIX(VIAL, new int[] { 11489, 11491 }, p -> {
			p.getSkills().adjustStat(4, 0, Constants.MAGIC);
			p.heal(30);
		}),

		AGILITY_POTION(VIAL, new int[] { 3032, 3034, 3036, 3038 }, p -> p.getSkills().adjustStat(3, 0, Constants.AGILITY)),
		AGILITY_FLASK(-1, new int[] { 23411, 23413, 23415, 23417, 23419, 23421 }, p -> p.getSkills().adjustStat(3, 0, Constants.AGILITY)),
		AGILITY_MIX(VIAL, new int[] { 11461, 11463 }, p -> {
			p.getSkills().adjustStat(3, 0, Constants.AGILITY);
			p.heal(30);
		}),

		FISHING_POTION(VIAL, new int[] { 2438, 151, 153, 155 }, p -> p.getSkills().adjustStat(3, 0, Constants.FISHING)),
		FISHING_FLASK(-1, new int[] { 23267, 23269, 23271, 23273, 23275, 23277 }, p -> p.getSkills().adjustStat(3, 0, Constants.FISHING)),
		FISHING_MIX(VIAL, new int[] { 11477, 11479 }, p -> {
			p.getSkills().adjustStat(3, 0, Constants.FISHING);
			p.heal(30);
		}),

		HUNTER_POTION(VIAL, new int[] { 9998, 10000, 10002, 10004 }, p -> p.getSkills().adjustStat(3, 0, Constants.HUNTER)),
		HUNTER_FLASK(-1, new int[] { 23435, 23437, 23439, 23441, 23443, 23445 }, p -> p.getSkills().adjustStat(3, 0, Constants.HUNTER)),
		HUNTING_MIX(VIAL, new int[] { 11517, 11519 }, p -> {
			p.getSkills().adjustStat(3, 0, Constants.HUNTER);
			p.heal(30);
		}),

		CRAFTING_POTION(VIAL, new int[] { 14838, 14840, 14842, 14844 }, p -> p.getSkills().adjustStat(3, 0, Constants.CRAFTING)),
		CRAFTING_FLASK(-1, new int[] { 23459, 23461, 23463, 23465, 23467, 23469 }, p -> p.getSkills().adjustStat(3, 0, Constants.CRAFTING)),

		FLETCHING_POTION(VIAL, new int[] { 14846, 14848, 14850, 14852 }, p -> p.getSkills().adjustStat(3, 0, Constants.FLETCHING)),
		FLETCHING_FLASK(-1, new int[] { 23471, 23473, 23475, 23477, 23479, 23481 }, p -> p.getSkills().adjustStat(3, 0, Constants.FLETCHING)),

		SANFEW_SERUM(VIAL, new int[] { 10925, 10927, 10929, 10931 }, p -> {
			p.getSkills().adjustStat(8, 0.25, false, Utils.range(0, Skills.SIZE-1));
			p.getPrayer().restorePrayer(((int) (p.getSkills().getLevelForXp(Constants.PRAYER) * 0.33 * 10)));
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6));
		}),
		SANFEW_SERUM_FLASK(-1, new int[] { 23567, 23569, 23571, 23573, 23575, 23577 }, p -> {
			p.getSkills().adjustStat(8, 0.25, false, Utils.range(0, Skills.SIZE-1));
			p.getPrayer().restorePrayer(((int) (p.getSkills().getLevelForXp(Constants.PRAYER) * 0.33 * 10)));
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(6));
		}),

		SUMMONING_POTION(VIAL, new int[] { 12140, 12142, 12144, 12146 }, p -> {
			p.getSkills().adjustStat(7, 0.25, false, Constants.SUMMONING);
			Familiar familiar = p.getFamiliar();
			if (familiar != null)
				familiar.restoreSpecialAttack(15);
		}),
		SUMMONING_FLASK(-1, new int[] { 23621, 23623, 23625, 23627, 23629, 23631 }, p -> {
			p.getSkills().adjustStat(7, 0.25, false, Constants.SUMMONING);
			Familiar familiar = p.getFamiliar();
			if (familiar != null)
				familiar.restoreSpecialAttack(15);
		}),

		RECOVER_SPECIAL(VIAL, new int[] { 15300, 15301, 15302, 15303 }, true, p -> {
			p.getTempAttribs().setL("recSpecPot", World.getServerTicks());
			p.getCombatDefinitions().restoreSpecialAttack(25);
		}) {
			@Override
			public boolean canDrink(Player player) {
				if (World.getServerTicks() - player.getTempAttribs().getL("recSpecPot") < 50) {
					player.sendMessage("You may only use this pot every 30 seconds.");
					return false;
				}
				return true;
			}
		},
		RECOVER_SPECIAL_FLASK(-1, new int[] { 23483, 23484, 23485, 23486, 23487, 23488 }, true, p -> {
			p.getTempAttribs().setL("recSpecPot", World.getServerTicks());
			p.getCombatDefinitions().restoreSpecialAttack(25);
		}) {
			@Override
			public boolean canDrink(Player player) {
				if (World.getServerTicks() - player.getTempAttribs().getL("recSpecPot") < 50) {
					player.sendMessage("You may only use this pot every 30 seconds.");
					return false;
				}
				return true;
			}
		},

		SUPER_ANTIFIRE(VIAL, new int[] { 15304, 15305, 15306, 15307 }, true, p -> p.addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromMinutes(6))),
		SUPER_ANTIFIRE_FLASK(-1, new int[] { 23489, 23490, 23491, 23492, 23493, 23494 }, true, p -> p.addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromMinutes(6))),

		EXTREME_ATTACK(VIAL, new int[] { 15308, 15309, 15310, 15311 }, true, p -> p.getSkills().adjustStat(5, 0.22, Constants.ATTACK)),
		EXTREME_ATTACK_FLASK(-1, new int[] { 23495, 23496, 23497, 23498, 23499, 23500 }, true, p -> p.getSkills().adjustStat(5, 0.22, Constants.ATTACK)),

		EXTREME_STRENGTH(VIAL, new int[] { 15312, 15313, 15314, 15315 }, true, p -> p.getSkills().adjustStat(5, 0.22, Constants.STRENGTH)),
		EXTREME_STRENGTH_FLASK(-1, new int[] { 23501, 23502, 23503, 23504, 23505, 23506 }, true, p -> p.getSkills().adjustStat(5, 0.22, Constants.STRENGTH)),

		EXTREME_DEFENCE(VIAL, new int[] { 15316, 15317, 15318, 15319 }, true, p -> p.getSkills().adjustStat(5, 0.22, Constants.DEFENSE)),
		EXTREME_DEFENCE_FLASK(-1, new int[] { 23507, 23508, 23509, 23510, 23511, 23512 }, true, p -> p.getSkills().adjustStat(5, 0.22, Constants.DEFENSE)),

		EXTREME_MAGIC(VIAL, new int[] { 15320, 15321, 15322, 15323 }, true, p -> p.getSkills().adjustStat(7, 0, Constants.MAGIC)),
		EXTREME_MAGIC_FLASK(-1, new int[] { 23513, 23514, 23515, 23516, 23517, 23518 }, true, p -> p.getSkills().adjustStat(7, 0, Constants.MAGIC)),

		EXTREME_RANGING(VIAL, new int[] { 15324, 15325, 15326, 15327 }, true, p -> p.getSkills().adjustStat(4, 0.2, Constants.RANGE)),
		EXTREME_RANGING_FLASK(-1, new int[] { 23519, 23520, 23521, 23522, 23523, 23524 }, true, p -> p.getSkills().adjustStat(4, 0.2, Constants.RANGE)),

		SUPER_PRAYER(VIAL, new int[] { 15328, 15329, 15330, 15331 }, p -> p.getPrayer().restorePrayer(((int) (70 + (p.getSkills().getLevelForXp(Constants.PRAYER) * 3.43))))),
		SUPER_PRAYER_FLASK(-1, new int[] { 23525, 23526, 23527, 23528, 23529, 23530 }, p -> p.getPrayer().restorePrayer(((int) (70 + (p.getSkills().getLevelForXp(Constants.PRAYER) * 3.43))))),
		DOM_SUPER_PRAYER(-1, new int[] { 22375, 22376 }, p -> p.getPrayer().restorePrayer(((int) (70 + (p.getSkills().getLevelForXp(Constants.PRAYER) * 3.43))))),

		OVERLOAD(VIAL, new int[] { 15332, 15333, 15334, 15335 }, true, p -> {
			p.addEffect(Effect.OVERLOAD, 500);
			WorldTasks.schedule(new Task() {
				int count = 4;

				@Override
				public void run() {
					if (count == 0)
						stop();
					p.setNextAnimation(new Animation(3170));
					p.setNextSpotAnim(new SpotAnim(560));
					p.applyHit(new Hit(p, 100, HitLook.TRUE_DAMAGE, 0));
					count--;
				}
			}, 0, 2);
		}) {
			@Override
			public boolean canDrink(Player player) {
				if (player.hasEffect(Effect.OVERLOAD)) {
					player.sendMessage("You are already under the effects of an overload potion.");
					return false;
				}
				if (player.getHitpoints() <= 500) {
					player.sendMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}
		},
		OVERLOAD_FLASK(-1, new int[] { 23531, 23532, 23533, 23534, 23535, 23536 }, true, p -> {
			p.addEffect(Effect.OVERLOAD, 500);
			WorldTasks.schedule(new Task() {
				int count = 4;

				@Override
				public void run() {
					if (count == 0)
						stop();
					p.setNextAnimation(new Animation(3170));
					p.setNextSpotAnim(new SpotAnim(560));
					p.applyHit(new Hit(p, 100, HitLook.TRUE_DAMAGE, 0));
					count--;
				}
			}, 0, 2);
		}) {
			@Override
			public boolean canDrink(Player player) {
				if (player.hasEffect(Effect.OVERLOAD)) {
					player.sendMessage("You are already under the effects of an overload potion.");
					return false;
				}
				if (player.getHitpoints() <= 500) {
					player.sendMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}
		},

		JUJU_MINING_POTION(JUJU_VIAL, new int[] { 20003, 20004, 20005, 20006 }, p -> p.addEffect(Effect.JUJU_MINING, 500)),
		JUJU_MINING_FLASK(-1, new int[] { 23131, 23132, 23133, 23134, 23135, 23136 }, p -> p.addEffect(Effect.JUJU_MINING, 500)),

		JUJU_COOKING_POTION(JUJU_VIAL, new int[] { 20007, 20008, 20009, 20010 }),
		JUJU_COOKING_FLASK(-1, new int[] { 23137, 23138, 23139, 23140, 23141, 23142 }),

		JUJU_FARMING_POTION(JUJU_VIAL, new int[] { 20011, 20012, 20013, 20014 }, p -> p.addEffect(Effect.JUJU_FARMING, 500)),
		JUJU_FARMING_FLASK(-1, new int[] { 23143, 23144, 23145, 23146, 23147, 23148 }, p -> p.addEffect(Effect.JUJU_FARMING, 500)),

		JUJU_WOODCUTTING_POTION(JUJU_VIAL, new int[] { 20015, 20016, 20017, 20018 }, p -> p.addEffect(Effect.JUJU_WOODCUTTING, 500)),
		JUJU_WOODCUTTING_FLASK(-1, new int[] { 23149, 23150, 23151, 23152, 23153, 23154 }, p -> p.addEffect(Effect.JUJU_WOODCUTTING, 500)),

		JUJU_FISHING_POTION(JUJU_VIAL, new int[] { 20019, 20020, 20021, 20022 }, p -> p.addEffect(Effect.JUJU_FISHING, 500)),
		JUJU_FISHING_FLASK(-1, new int[] { 23155, 23156, 23157, 23158, 23159, 23160 }, p -> p.addEffect(Effect.JUJU_FISHING, 500)),

		JUJU_HUNTER_POTION(JUJU_VIAL, new int[] { 20023, 20024, 20025, 20026 }),
		JUJU_HUNTER_FLASK(-1, new int[] { 23161, 23162, 23163, 23164, 23165, 23166 }),

		SCENTLESS_POTION(JUJU_VIAL, new int[] { 20027, 20028, 20029, 20030 }, p -> p.addEffect(Effect.SCENTLESS, 500)),
		SCENTLESS_FLASK(-1, new int[] { 23167, 23168, 23169, 23170, 23171, 23172 }, p -> p.addEffect(Effect.SCENTLESS, 500)),

		SARADOMINS_BLESSING(JUJU_VIAL, new int[] { 20031, 20032, 20033, 20034 }, p -> p.addEffect(Effect.SARA_BLESSING, 500)),
		SARADOMINS_BLESSING_FLASK(-1, new int[] { 23173, 23174, 23175, 23176, 23177, 23178 }, p -> p.addEffect(Effect.SARA_BLESSING, 500)),

		GUTHIXS_GIFT(JUJU_VIAL, new int[] { 20035, 20036, 20037, 20038 }, p -> p.addEffect(Effect.GUTHIX_GIFT, 500)),
		GUTHIXS_GIFT_FLASK(-1, new int[] { 23179, 23180, 23181, 23182, 23183, 23184 }, p -> p.addEffect(Effect.GUTHIX_GIFT, 500)),

		ZAMORAKS_FAVOUR(JUJU_VIAL, new int[] { 20039, 20040, 20041, 20042 }, p -> p.addEffect(Effect.ZAMMY_FAVOR, 500)),
		ZAMORAKS_FAVOUR_FLASK(-1, new int[] { 23185, 23186, 23187, 23188, 23189, 23190 }, p -> p.addEffect(Effect.ZAMMY_FAVOR, 500)),


		WEAK_MAGIC_POTION(17490, 17556, p -> p.getSkills().adjustStat(4, 0.1, Constants.MAGIC)),
		WEAK_RANGED_POTION(17490, 17558, p -> p.getSkills().adjustStat(4, 0.1, Constants.RANGE)),
		WEAK_MELEE_POTION(17490, 17560, p -> p.getSkills().adjustStat(4, 0.1, Constants.ATTACK, Constants.STRENGTH)),
		WEAK_DEFENCE_POTION(17490, 17562, p -> p.getSkills().adjustStat(4, 0.1, Constants.DEFENSE)),

		WEAK_GATHERERS_POTION(17490, 17574, p -> p.getSkills().adjustStat(3, 0.02, Constants.WOODCUTTING, Constants.MINING, Constants.FISHING)),
		WEAK_ARTISANS_POTION(17490, 17576, p -> p.getSkills().adjustStat(3, 0.02, Constants.SMITHING, Constants.CRAFTING, Constants.FLETCHING, Constants.CONSTRUCTION, Constants.FIREMAKING)),
		WEAK_NATURALISTS_POTION(17490, 17578, p -> p.getSkills().adjustStat(3, 0.02, Constants.COOKING, Constants.FARMING, Constants.HERBLORE, Constants.RUNECRAFTING)),
		WEAK_SURVIVALISTS_POTION(17490, 17580, p -> p.getSkills().adjustStat(3, 0.02, Constants.AGILITY, Constants.HUNTER, Constants.THIEVING, Constants.SLAYER)),

		MAGIC_POTION_D(17490, 17582, p -> p.getSkills().adjustStat(5, 0.14, Constants.MAGIC)),
		RANGED_POTION(17490, 17584, p -> p.getSkills().adjustStat(5, 0.14, Constants.RANGE)),
		MELEE_POTION(17490, 17586, p -> p.getSkills().adjustStat(5, 0.14, Constants.ATTACK, Constants.STRENGTH)),
		DEFENCE_POTION_D(17490, 17588, p -> p.getSkills().adjustStat(5, 0.14, Constants.DEFENSE)),

		GATHERERS_POTION(17490, 17598, p -> p.getSkills().adjustStat(4, 0.04, Constants.WOODCUTTING, Constants.MINING, Constants.FISHING)),
		ARTISANS_POTION(17490, 17600, p -> p.getSkills().adjustStat(4, 0.04, Constants.SMITHING, Constants.CRAFTING, Constants.FLETCHING, Constants.CONSTRUCTION, Constants.FIREMAKING)),
		NATURALISTS_POTION(17490, 17602, p -> p.getSkills().adjustStat(4, 0.04, Constants.COOKING, Constants.FARMING, Constants.HERBLORE, Constants.RUNECRAFTING)),
		SURVIVALISTS_POTION(17490, 17604, p -> p.getSkills().adjustStat(4, 0.04, Constants.AGILITY, Constants.HUNTER, Constants.THIEVING, Constants.SLAYER)),

		STRONG_MAGIC_POTION(17490, 17606, p -> p.getSkills().adjustStat(6, 0.2, Constants.MAGIC)),
		STRONG_RANGED_POTION(17490, 17608, p -> p.getSkills().adjustStat(6, 0.2, Constants.RANGE)),
		STRONG_MELEE_POTION(17490, 17610, p -> p.getSkills().adjustStat(6, 0.2, Constants.ATTACK, Constants.STRENGTH)),
		STRONG_DEFENCE_POTION(17490, 17612, p -> p.getSkills().adjustStat(6, 0.2, Constants.DEFENSE)),

		STRONG_GATHERERS_POTION(17490, 17622, p -> p.getSkills().adjustStat(6, 0.06, Constants.WOODCUTTING, Constants.MINING, Constants.FISHING)),
		STRONG_ARTISANS_POTION(17490, 17624, p -> p.getSkills().adjustStat(6, 0.06, Constants.SMITHING, Constants.CRAFTING, Constants.FLETCHING, Constants.CONSTRUCTION, Constants.FIREMAKING)),
		STRONG_NATURALISTS_POTION(17490, 17626, p -> p.getSkills().adjustStat(6, 0.06, Constants.COOKING, Constants.FARMING, Constants.HERBLORE, Constants.RUNECRAFTING)),
		STRONG_SURVIVALISTS_POTION(17490, 17628, p -> p.getSkills().adjustStat(6, 0.06, Constants.AGILITY, Constants.HUNTER, Constants.THIEVING, Constants.SLAYER)),

		WEAK_STAT_RESTORE_POTION(17490, 17564, p -> p.getSkills().adjustStat(5, 0.12, false, Skills.allExcept(Constants.PRAYER, Constants.SUMMONING))),
		STAT_RESTORE_POTION(17490, 17590, p -> p.getSkills().adjustStat(7, 0.17, false, Skills.allExcept(Constants.PRAYER, Constants.SUMMONING))),
		STRONG_STAT_RESTORE_POTION(17490, 17614, p -> p.getSkills().adjustStat(10, 0.24, false, Skills.allExcept(Constants.PRAYER, Constants.SUMMONING))),

		WEAK_CURE_POTION(17490, 17568, p -> {
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(5));
			p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(5));
		}),
		CURE_POTION(17490, 17592, p -> {
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(10));
			p.addEffect(Effect.ANTIFIRE, Ticks.fromMinutes(10));
		}),
		STRONG_CURE_POTION(17490, 17616, p -> {
			p.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(20));
			p.addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromMinutes(20));
		}),

		WEAK_REJUVENATION_POTION(17490, 17570, p -> {
			p.getSkills().adjustStat(5, 0.10, false, Constants.SUMMONING);
			p.getPrayer().restorePrayer(((int) (Math.floor(p.getSkills().getLevelForXp(Constants.PRAYER)) + 50)));
		}),
		REJUVENATION_POTION(17490, 17594, p -> {
			p.getSkills().adjustStat(7, 0.15, false, Constants.SUMMONING);
			p.getPrayer().restorePrayer(((int) (Math.floor(p.getSkills().getLevelForXp(Constants.PRAYER) * 1.5) + 70)));
		}),
		STRONG_REJUVENATION_POTION(17490, 17618, p -> {
			p.getSkills().adjustStat(10, 0.22, false, Constants.SUMMONING);
			p.getPrayer().restorePrayer(((int) (Math.floor(p.getSkills().getLevelForXp(Constants.PRAYER) * 2.2) + 100)));
		}),

		POISON_CHALICE(2026, 197, p -> {
			//TODO
		}),

		STRANGE_FRUIT(-1, 464, p -> p.restoreRunEnergy(30)),
		GORAJIAN_MUSHROOM(-1, 22446, p -> {
			p.heal((int) (p.getMaxHitpoints()*0.1));
			p.getTempAttribs().setB("gorajMush", true);
		}),

		KARAMJAN_RUM(-1, 431, p -> {
			p.getSkills().adjustStat(-4, 0, Constants.ATTACK);
			p.getSkills().adjustStat(-5, 0, Constants.STRENGTH);
			p.heal(50);
		}),
		BANDITS_BREW(BEER_GLASS, 4627, p -> {
			p.getSkills().adjustStat(1, 0, Constants.ATTACK, Constants.THIEVING);
			p.getSkills().adjustStat(-1, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-6, 0, Constants.DEFENSE);
			p.heal(10);
		}),
		GROG(BEER_GLASS, 1915, p -> {
			p.getSkills().adjustStat(3, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-6, 0, Constants.ATTACK);
			p.heal(30);
		}),
		BEER(BEER_GLASS, 1917, p -> {
			p.getSkills().adjustStat(0, 0.04, Constants.STRENGTH);
			p.getSkills().adjustStat(0, -0.07, Constants.ATTACK);
			p.heal(10);
		}),
		BEER_FREM(3805, 3803, p -> {
			p.getSkills().adjustStat(0, 0.04, Constants.STRENGTH);
			p.getSkills().adjustStat(0, -0.07, Constants.ATTACK);
			p.heal(10);
		}),
		BEER_POH(BEER_GLASS, 7740, p -> {
			p.getSkills().adjustStat(0, 0.04, Constants.STRENGTH);
			p.getSkills().adjustStat(0, -0.07, Constants.ATTACK);
			p.heal(10);
		}),

		ASGARNIAN_ALE(BEER_GLASS, 1905, p -> {
			p.getSkills().adjustStat(-2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(4, 0, Constants.ATTACK);
			p.heal(20);
		}),
		ASGARNIAN_ALE_POH(BEER_GLASS, 7744, p -> {
			p.getSkills().adjustStat(-2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(4, 0, Constants.ATTACK);
			p.heal(20);
		}),
		ASGARNIAN_ALE_KEG(EMPTY_KEG, new int[] { 5779, 5781, 5783, 5785 }, p -> {
			p.getSkills().adjustStat(-2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(4, 0, Constants.ATTACK);
			p.heal(20);
		}),
		ASGARNIAN_ALE_M(BEER_GLASS, 5739, p -> {
			p.getSkills().adjustStat(-3, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(6, 0, Constants.ATTACK);
			p.heal(20);
		}),
		ASGARNIAN_ALE_M_KEG(EMPTY_KEG, new int[] { 5859, 5861, 5863, 5865 }, p -> {
			p.getSkills().adjustStat(-3, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(6, 0, Constants.ATTACK);
			p.heal(20);
		}),

		MIND_BOMB(BEER_GLASS, 1907, p -> {
			p.getSkills().adjustStat(p.getSkills().getLevelForXp(Constants.MAGIC) >= 50 ? 3 : 2, 0, Constants.MAGIC);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK);
			p.getSkills().adjustStat(-4, 0, Constants.STRENGTH, Constants.DEFENSE);
		}),
		MIND_BOMB_KEG(EMPTY_KEG, new int[] { 5795, 5797, 5799, 5801 }, p -> {
			p.getSkills().adjustStat(p.getSkills().getLevelForXp(Constants.MAGIC) >= 50 ? 3 : 2, 0, Constants.MAGIC);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK);
			p.getSkills().adjustStat(-4, 0, Constants.STRENGTH, Constants.DEFENSE);
		}),
		MIND_BOMB_M(BEER_GLASS, 5741, p -> {
			p.getSkills().adjustStat(p.getSkills().getLevelForXp(Constants.MAGIC) >= 50 ? 4 : 3, 0, Constants.MAGIC);
			p.getSkills().adjustStat(-5, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),
		MIND_BOMB_M_KEG(EMPTY_KEG, new int[] { 5875, 5877, 5879, 5881 }, p -> {
			p.getSkills().adjustStat(p.getSkills().getLevelForXp(Constants.MAGIC) >= 50 ? 4 : 3, 0, Constants.MAGIC);
			p.getSkills().adjustStat(-5, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),

		GREENMANS_ALE(BEER_GLASS, 1909, p -> {
			p.getSkills().adjustStat(1, 0, Constants.HERBLORE);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),
		GREENMANS_ALE_POH(BEER_GLASS, 7746, p -> {
			p.getSkills().adjustStat(1, 0, Constants.HERBLORE);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),
		GREENMANS_ALE_KEG(EMPTY_KEG, new int[] { 5787, 5789, 5791, 5793 }, p -> {
			p.getSkills().adjustStat(1, 0, Constants.HERBLORE);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),
		GREENMANS_ALE_M(BEER_GLASS, 5743, p -> {
			p.getSkills().adjustStat(2, 0, Constants.HERBLORE);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),
		GREENMANS_ALE_M_KEG(EMPTY_KEG, new int[] { 5867, 5869, 5871, 5873 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.HERBLORE);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),

		DRAGON_BITTER(BEER_GLASS, 1911, p -> {
			p.getSkills().adjustStat(2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-6, 0, Constants.ATTACK);
			p.heal(10);
		}),
		DRAGON_BITTER_POH(BEER_GLASS, 7748, p -> {
			p.getSkills().adjustStat(2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-6, 0, Constants.ATTACK);
			p.heal(10);
		}),
		DRAGON_BITTER_KEG(EMPTY_KEG, new int[] { 5803, 5805, 5807, 5809 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-6, 0, Constants.ATTACK);
			p.heal(10);
		}),
		DRAGON_BITTER_M(BEER_GLASS, 5745, p -> {
			p.getSkills().adjustStat(2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-4, 0, Constants.ATTACK);
			p.heal(20);
		}),
		DRAGON_BITTER_M_KEG(EMPTY_KEG, new int[] { 5883, 5885, 5887, 5889 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.STRENGTH);
			p.getSkills().adjustStat(-4, 0, Constants.ATTACK);
			p.heal(20);
		}),
		DWARVEN_STOUT(BEER_GLASS, 1913, p -> {
			p.getSkills().adjustStat(1, 0, Constants.MINING, Constants.SMITHING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		DWARVEN_STOUT_KEG(EMPTY_KEG, new int[] { 5771, 5773, 5775, 5777 }, p -> {
			p.getSkills().adjustStat(1, 0, Constants.MINING, Constants.SMITHING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		DWARVEN_STOUT_M(BEER_GLASS, 5747, p -> {
			p.getSkills().adjustStat(2, 0, Constants.MINING, Constants.SMITHING);
			p.getSkills().adjustStat(-7, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),
		DWARVEN_STOUT_M_KEG(EMPTY_KEG, new int[] { 5851, 5853, 5855, 5857 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.MINING, Constants.SMITHING);
			p.getSkills().adjustStat(-7, 0, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
			p.heal(10);
		}),

		MOONLIGHT_MEAD(BEER_GLASS, 2955, p -> p.heal(40)),
		MOONLIGHT_MEAD_POH(BEER_GLASS, 7750, p -> p.heal(40)),
		MOONLIGHT_MEAD_KEG(EMPTY_KEG, new int[] { 5811, 5813, 5815, 5817 }, p -> p.heal(40)),
		MOONLIGHT_MEAD_M(BEER_GLASS, 5749, p -> p.heal(60)),
		MOONLIGHT_MEAD_M_KEG(EMPTY_KEG, new int[] { 5891, 5893, 5895, 5897 }, p -> p.heal(60)),

		AXEMANS_FOLLY(BEER_GLASS, 5751, p -> {
			p.getSkills().adjustStat(1, 0, Constants.WOODCUTTING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		AXEMANS_FOLLY_KEG(EMPTY_KEG, new int[] { 5819, 5821, 5823, 5825 }, p -> {
			p.getSkills().adjustStat(1, 0, Constants.WOODCUTTING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		AXEMANS_FOLLY_M(BEER_GLASS, 5753, p -> {
			p.getSkills().adjustStat(2, 0, Constants.WOODCUTTING);
			p.getSkills().adjustStat(-4, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		AXEMANS_FOLLY_M_KEG(EMPTY_KEG, new int[] { 5899, 5901, 5903, 5905 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.WOODCUTTING);
			p.getSkills().adjustStat(-4, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),

		CHEFS_DELIGHT(BEER_GLASS, 5755, p -> {
			p.getSkills().adjustStat(1, 0.05, Constants.COOKING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		CHEFS_DELIGHT_POH(BEER_GLASS, 7754, p -> {
			p.getSkills().adjustStat(1, 0.05, Constants.COOKING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		CHEFS_DELIGHT_KEG(EMPTY_KEG, new int[] { 5827, 5829, 5831, 5833 }, p -> {
			p.getSkills().adjustStat(1, 0.05, Constants.COOKING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		CHEFS_DELIGHT_M(BEER_GLASS, 5757, p -> {
			p.getSkills().adjustStat(2, 0.05, Constants.COOKING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		CHEFS_DELIGHT_M_KEG(EMPTY_KEG, new int[] { 5907, 5909, 5911, 5913 }, p -> {
			p.getSkills().adjustStat(2, 0.05, Constants.COOKING);
			p.getSkills().adjustStat(-3, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),

		SLAYERS_RESPITE(BEER_GLASS, 5759, p -> {
			p.getSkills().adjustStat(2, 0, Constants.SLAYER);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		SLAYERS_RESPITE_KEG(EMPTY_KEG, new int[] { 5835, 5837, 5839, 5841 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.SLAYER);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		SLAYERS_RESPITE_M(BEER_GLASS, 5761, p -> {
			p.getSkills().adjustStat(4, 0, Constants.SLAYER);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),
		SLAYERS_RESPITE_M_KEG(EMPTY_KEG, new int[] { 5915, 5917, 5919, 5921 }, p -> {
			p.getSkills().adjustStat(4, 0, Constants.SLAYER);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(10);
		}),

		CIDER(BEER_GLASS, 5763, p -> {
			p.getSkills().adjustStat(1, 0, Constants.FARMING);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		CIDER_POH(BEER_GLASS, 7752, p -> {
			p.getSkills().adjustStat(1, 0, Constants.FARMING);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		CIDER_KEG(EMPTY_KEG, new int[] { 5843, 5845, 5847, 5849 }, p -> {
			p.getSkills().adjustStat(1, 0, Constants.FARMING);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		CIDER_M(BEER_GLASS, 5765, p -> {
			p.getSkills().adjustStat(2, 0, Constants.FARMING);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		CIDER_M_KEG(EMPTY_KEG, new int[] { 5923, 5925, 5927, 5929 }, p -> {
			p.getSkills().adjustStat(2, 0, Constants.FARMING);
			p.getSkills().adjustStat(-2, 0, Constants.ATTACK, Constants.STRENGTH);
			p.heal(20);
		}),
		SERUM_207(VIAL, new int[] { 3408, 3410, 3412, 3414 }),
		SERUM_208(VIAL, new int[] { 3416, 3417, 3418, 3419 }),
		OLIVE_OIL(VIAL, new int[] { 3422, 3424, 3426, 3428 }),
		SACRED_OIL(VIAL, new int[] { 3430, 3432, 3434, 3436 }),

		JUG_OF_BAD_WINE(EMPTY_JUG, 1991, p -> {
			p.getSkills().lowerStat(Constants.ATTACK, 3);
		}),
		JUG_OF_WINE(EMPTY_JUG, 1993, p -> {
			p.heal(110, 0);
			p.getSkills().lowerStat(Constants.ATTACK, 2);
		}),

		;

		public static Map<Integer, Potion> POTS = new HashMap<>();

		static {
			for (Potion pot : Potion.values())
				for (int id : pot.ids)
					POTS.put(id, pot);
		}

		public static Potion forId(int itemId) {
			return POTS.get(itemId);
		}

		private int emptyId;
		private Consumer<Player> effect;
		private int[] ids;
		private boolean isOP;

		private Potion(int emptyId, int[] ids, boolean isOP, Consumer<Player> effect) {
			this.emptyId = emptyId;
			this.ids = ids;
			this.effect = effect;
		}

		private Potion(int emptyId, int[] ids, Consumer<Player> effect) {
			this(emptyId, ids, false, effect);
		}

		private Potion(int emptyId, int id, Consumer<Player> effect) {
			this(emptyId, new int[] { id }, false, effect);
		}

		private Potion(int emptyId, int[] ids) {
			this(emptyId, ids, null);
		}

		public boolean canDrink(Player player) {
			return true;
		}

		private final void drink(Player player, int itemId, int slot) {
			if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getId() != itemId || !player.canPot() || !player.getControllerManager().canPot(this))
				return;
			if (effect == null) {
				player.sendMessage("You wouldn't want to drink that.");
				return;
			}
			if (isOP && player.getControllerManager().getController() instanceof WildernessController) {
				player.sendMessage("You cannot drink this potion here.");
				return;
			}
			if (canDrink(player)) {
				int idIdx = -1;
				for (int i = 0;i < ids.length-1;i++)
					if (ids[i] == itemId) {
						idIdx = i;
						break;
					}
				int newId = idIdx == -1 ? emptyId : ids[idIdx+1];
				player.getInventory().getItems().set(slot, newId == -1 ? null : new Item(newId, 1));
				player.getInventory().refresh(slot);
				player.addPotionDelay(2);
				effect.accept(player);
				player.setNextAnimation(new Animation(829));
				player.soundEffect(4580);
				player.sendMessage("You drink some of your " + ItemDefinitions.getDefs(itemId).name.toLowerCase().replace(" (1)", "").replace(" (2)", "").replace(" (3)", "").replace(" (4)", "").replace(" (5)", "").replace(" (6)", "") + ".", true);
			}
		}
		
		public Consumer<Player> getEffect() {
			return effect;
		}

		public int[] getIds() {
			return ids;
		}

		public int getMaxDoses() {
			return ids.length;
		}

		public int getIdForDoses(int doses) {
			return ids[ids.length-doses];
		}

		public boolean isVial() {
			return emptyId == VIAL || emptyId == JUJU_VIAL;
		}
	}

	public static ItemClickHandler clickOps = new ItemClickHandler(Potion.POTS.keySet().toArray(), new String[] { "Drink", "Empty" }, e -> {
		Potion pot = Potion.forId(e.getItem().getId());
		if (pot == null)
			return;
		if (e.getOption().equals("Drink"))
			pot.drink(e.getPlayer(), e.getItem().getId(), e.getItem().getSlot());
		else if (e.getOption().equals("Empty") && pot.emptyId != -1) {
			e.getItem().setId(pot.emptyId);
			e.getPlayer().getInventory().refresh(e.getItem().getSlot());
		}
	});

	public static int getDoses(Potion pot, Item item) {
		for (int i = pot.ids.length - 1; i >= 0; i--)
			if (pot.ids[i] == item.getId())
				return pot.ids.length - i;
		return 0;
	}

	public static ItemOnItemHandler mixPotions = new ItemOnItemHandler(Potion.POTS.keySet().stream().mapToInt(i->i).toArray(), e -> {
		Item fromItem = e.getItem1();
		Item toItem = e.getItem2();
		int fromSlot = fromItem.getSlot();
		int toSlot = toItem.getSlot();
		if (fromItem.getId() == VIAL || toItem.getId() == VIAL) {
			Potion pot = Potion.forId(fromItem.getId() == VIAL ? toItem.getId() : fromItem.getId());
			if (pot == null || pot.emptyId == -1)
				return;
			int doses = getDoses(pot, fromItem.getId() == VIAL ? toItem : fromItem);
			if (doses == 1) {
				e.getPlayer().getInventory().switchItem(fromSlot, toSlot);
				e.getPlayer().sendMessage("You combine the potions.", true);
				return;
			}
			int vialDoses = doses / 2;
			doses -= vialDoses;
			e.getPlayer().getInventory().getItems().set(fromItem.getId() == VIAL ? toSlot : fromSlot, new Item(pot.getIdForDoses(doses), 1));
			e.getPlayer().getInventory().getItems().set(fromItem.getId() == VIAL ? fromSlot : toSlot, new Item(pot.getIdForDoses(vialDoses), 1));
			e.getPlayer().getInventory().refresh(fromSlot);
			e.getPlayer().getInventory().refresh(toSlot);
			e.getPlayer().sendMessage("You split the potion between the two vials.", true);
			return;
		}
		Potion pot = Potion.forId(fromItem.getId());
		if (pot == null)
			return;
		int doses2 = getDoses(pot, toItem);
		if (doses2 == 0 || doses2 == pot.getMaxDoses())
			return;
		int doses1 = getDoses(pot, fromItem);
		doses2 += doses1;
		doses1 = doses2 > pot.getMaxDoses() ? doses2 - pot.getMaxDoses() : 0;
		doses2 -= doses1;
		if (doses1 == 0 && pot.emptyId == -1)
			e.getPlayer().getInventory().deleteItem(fromSlot, fromItem);
		else {
			e.getPlayer().getInventory().getItems().set(fromSlot, new Item(doses1 > 0 ? pot.getIdForDoses(doses1) : pot.emptyId, 1));
			e.getPlayer().getInventory().refresh(fromSlot);
		}
		e.getPlayer().getInventory().getItems().set(toSlot, new Item(pot.getIdForDoses(doses2), 1));
		e.getPlayer().getInventory().refresh(toSlot);
		e.getPlayer().sendMessage("You pour from one container into the other" + (pot.emptyId == -1 && doses1 == 0 ? " and the flask shatters to pieces." : "."));
	});
	
	public static void checkOverloads(Player player) {
		boolean changed = false;
		int level = player.getSkills().getLevelForXp(Constants.ATTACK);
		int maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Constants.ATTACK)) {
			player.getSkills().set(Constants.ATTACK, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.STRENGTH);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Constants.STRENGTH)) {
			player.getSkills().set(Constants.STRENGTH, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.DEFENSE);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Constants.DEFENSE)) {
			player.getSkills().set(Constants.DEFENSE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.RANGE);
		maxLevel = (int) (level + 5 + (level * 0.1));
		if (maxLevel < player.getSkills().getLevel(Constants.RANGE)) {
			player.getSkills().set(Constants.RANGE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Constants.MAGIC);
		maxLevel = level + 5;
		if (maxLevel < player.getSkills().getLevel(Constants.MAGIC)) {
			player.getSkills().set(Constants.MAGIC, maxLevel);
			changed = true;
		}
		if (changed)
			player.sendMessage("Your extreme potion bonus has been reduced.");
	}

	public static void applyOverLoadEffect(Player player) {
		if (player.hasEffect(Effect.OVERLOAD_PVP_REDUCTION)) {
			int actualLevel = player.getSkills().getLevel(Constants.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Constants.ATTACK);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.ATTACK, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Constants.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Constants.STRENGTH);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.STRENGTH, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Constants.DEFENSE);
			realLevel = player.getSkills().getLevelForXp(Constants.DEFENSE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.DEFENSE, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Constants.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Constants.MAGIC);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.MAGIC, level + 5);

			actualLevel = player.getSkills().getLevel(Constants.RANGE);
			realLevel = player.getSkills().getLevelForXp(Constants.RANGE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.RANGE, (int) (level + 5 + (realLevel * 0.1)));
		} else {
			int actualLevel = player.getSkills().getLevel(Constants.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Constants.ATTACK);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.ATTACK, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Constants.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Constants.STRENGTH);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.STRENGTH, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Constants.DEFENSE);
			realLevel = player.getSkills().getLevelForXp(Constants.DEFENSE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.DEFENSE, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Constants.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Constants.MAGIC);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.MAGIC, level + 7);

			actualLevel = player.getSkills().getLevel(Constants.RANGE);
			realLevel = player.getSkills().getLevelForXp(Constants.RANGE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.RANGE, (int) (level + 4 + (Math.floor(realLevel / 5.2))));
		}
	}
}
