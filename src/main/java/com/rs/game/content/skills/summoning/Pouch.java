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
package com.rs.game.content.skills.summoning;

import com.google.common.collect.Streams;
import com.rs.game.World;
import com.rs.game.content.combat.XPType;
import com.rs.game.content.skills.fishing.Fishing;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.utils.DropSets;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Pouch {
	/**
	 * Hello leecher. Nice to see you here. You're welcome for doing all the hard work for you.
	 * I hope you'll notice the license above :)
	 */
	SPIRIT_WOLF(0, 6829, 6830, XPType.ACCURATE, 8298, 8532, Scroll.HOWL, 12047, 1, 0.1, 4.8, Ticks.fromMinutes(16), 1),
	DREADFOWL(2, 6825, 6826, XPType.MAGIC, 7807, 8555, Scroll.DREADFOWL_STRIKE, 12043, 4, 0.1, 9.3, Ticks.fromMinutes(16), 1),
	SPIRIT_SPIDER(0, 6841, 6842, XPType.CONTROLLED, 8163, 8544, Scroll.EGG_SPAWN, 12059, 10, 0.2, 12.6, Ticks.fromMinutes(16), 2, "forage_spirit_spider"),
	THORNY_SNAIL(0, 6806, 6807, XPType.RANGED, 8141, 8561, Scroll.SLIME_SPRAY, 12019, 13, 0.2, 12.6, Ticks.fromMinutes(16), 2, 3),
	GRANITE_CRAB(3, 6796, 6797, XPType.DEFENSIVE, 8108, 8541, Scroll.STONY_SHELL, 12009, 16, 0.2, 21.6, Ticks.fromMinutes(16), 2, "forage_granite_crab") {
		@Override
		public boolean canRollForager(Player player) {
			return player.getActionManager().doingAction(Fishing.class);
		}
	},
	SPIRIT_MOSQUITO(4, 7331, 7332, XPType.ACCURATE, 8037, 8915, Scroll.PESTER, 12778, 17, 0.2, 46.5, Ticks.fromMinutes(16), 2),
	DESERT_WYRM(0, 6831, 6832, XPType.AGGRESSIVE, 7794, 8537, Scroll.ELECTRIC_LASH, 12049, 18, 0.4, 31.2, Ticks.fromMinutes(16), 1),
	SPIRIT_SCORPION(8, 6837, 6838, XPType.CONTROLLED, 8127, 8546, Scroll.VENOM_SHOT, 12055, 19, 0.9, 83.2, Ticks.fromMinutes(16), 2),
	SPIRIT_TZ_KIH(32, 7361, 7362, XPType.MAGIC, 8260, 8927, Scroll.FIREBALL, 12808, 22, 1.1, 96.8, Ticks.fromMinutes(16), 3),
	ALBINO_RAT(7, 6847, 6848, XPType.ACCURATE, 16080, 16081, Scroll.CHEESE_FEAST, 12067, 23, 2.3, 202.4, Ticks.fromMinutes(16), 3, "forage_albino_rat"),
	SPIRIT_KALPHITE(8, 6994, 6995, XPType.DEFENSIVE, 8516, 8531, Scroll.SANDSTORM, 12063, 25, 2.5, 220, Ticks.fromMinutes(16), 3, 6),
	COMPOST_MOUND(70, 6871, 6872, XPType.AGGRESSIVE, 7773, 8540, Scroll.COMPOST_GENERATE, 12091, 28, 0.6, 49.8, Ticks.fromMinutes(32), 6, "forage_compost_mound"),
	GIANT_CHINCHOMPA(7, 7353, 7354, XPType.RANGED, 7754, 8922, Scroll.EXPLODE, 12800, 29, 2.5, 255.2, Ticks.fromMinutes(32), 1),
	VAMPYRE_BAT(4, 6835, 6836, XPType.CONTROLLED, 8279, 8563, Scroll.VAMPYRE_TOUCH, 12053, 31, 1.6, 136.0, Ticks.fromMinutes(32), 4),
	HONEY_BADGER(7, 6845, 6846, XPType.AGGRESSIVE, 7929, 8568, Scroll.INSANE_FEROCITY, 12065, 32, 1.6, 140.8, Ticks.fromMinutes(32), 4),
	BEAVER(7, 6808, -1, null, 7721, 8535, Scroll.MULTICHOP, 12021, 33, 0.7, 57.6, Ticks.fromMinutes(32), 4, "forage_beaver"),
	VOID_RAVAGER(7, 7370, 7371, XPType.AGGRESSIVE, 8091, 8929, Scroll.CALL_TO_ARMS, 12818, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4, "forage_void_ravager"),
	VOID_SPINNER(29, 7333, 7334, XPType.DEFENSIVE, 8174, 8920, Scroll.CALL_TO_ARMS, 12780, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4) {
		@Override
		public void tick(Player owner, Familiar familiar) {
			familiar.getAttribs().decI("healTicks");
			if (familiar.getAttribs().getI("healTicks") <= 0 && owner != null) {
				owner.heal(10);
				familiar.anim(8179);
				owner.setNextSpotAnim(new SpotAnim(1507));
				familiar.getAttribs().setI("healTicks", 25);
			}
		}
	},
	VOID_TORCHER(2, 7351, 7352, XPType.MAGIC, 8238, 8921, Scroll.CALL_TO_ARMS, 12798, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
	VOID_SHIFTER(30, 7367, 7368, XPType.ACCURATE, 8134, 8919, Scroll.CALL_TO_ARMS, 12814, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4) {
		@Override
		public void tick(Player owner, Familiar familiar) {
			if (owner.getHitpoints() <= (owner.getMaxHitpoints() * 0.1) && familiar.getTempAttribs().getL("lastTeleAtt") <= World.getServerTicks()) {
				familiar.getTempAttribs().setL("lastTeleAtt", World.getServerTicks() + Ticks.fromMinutes(1));
				if (Scroll.CALL_TO_ARMS.use(owner, familiar)) {
					owner.setHitpoints((int) (owner.getMaxHitpoints() * 0.2));
					owner.refreshHitPoints();
				}
			}
		}
	},
	BRONZE_MINOTAUR(57, 6853, 6854, XPType.DEFENSIVE, 8029, 8549, Scroll.BRONZE_BULL, 12073, 36, 2.4, 316.8, Ticks.fromMinutes(32), 9),
	IRON_MINOTAUR(57, 6855, 6856, XPType.DEFENSIVE, 8029, 8549, Scroll.IRON_BULL, 12075, 46, 4.6, 404.8, Ticks.fromMinutes(32), 9),
	STEEL_MINOTAUR(57, 6857, 6858, XPType.DEFENSIVE, 8029, 8549, Scroll.STEEL_BULL, 12077, 56, 5.6, 492.8, Ticks.fromMinutes(48), 9),
	MITHRIL_MINOTAUR(57, 6859, 6860, XPType.DEFENSIVE, 8029, 8549, Scroll.MITHRIL_BULL, 12079, 66, 6.6, 580.8, Ticks.fromMinutes(48), 9),
	ADAMANT_MINOTAUR(57, 6861, 6862, XPType.DEFENSIVE, 8029, 8549, Scroll.ADAMANT_BULL, 12081, 76, 8.6, 668.8, Ticks.fromMinutes(64), 9),
	RUNE_MINOTAUR(57, 6863, 6864, XPType.DEFENSIVE, 8029, 8549, Scroll.RUNE_BULL, 12083, 86, 8.6, 756.8, Ticks.fromMinutes(64), 9),
	BULL_ANT(8, 6867, 6868, XPType.CONTROLLED, 7894, 8554, Scroll.UNBURDEN, 12087, 40, 0.6, 52.8, Ticks.fromMinutes(32), 5, 9),
	MACAW(2, 6851, 6852, null, 8005, 8553, Scroll.HERBCALL, 12071, 41, 0.8, 72.4, Ticks.fromMinutes(32), 5, "forage_macaw"),
	EVIL_TURNIP(5, 6833, 6834, XPType.RANGED, 8252, 8250, Scroll.EVIL_FLAMES, 12051, 42, 2.1, 184.8, Ticks.fromMinutes(32), 5, "forage_evil_turnip"),
	SPIRIT_COCKATRICE(89, 6875, 6876, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12095, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	SPIRIT_GUTHATRICE(89, 6877, 6878, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12097, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	SPIRIT_SARATRICE(89, 6879, 6880, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12099, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	SPIRIT_ZAMATRICE(89, 6881, 6882, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12101, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	SPIRIT_PENGATRICE(89, 6883, 6884, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12103, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	SPIRIT_CORAXATRICE(89, 6885, 6886, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12105, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	SPIRIT_VULATRICE(89, 6887, 6888, XPType.MAGIC, 7765, 8551, Scroll.PETRIFYING_GAZE, 12107, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, "forage_cockatrice"),
	PYRELORD(23, 7377, 7378, XPType.AGGRESSIVE, 8081, 8930, Scroll.IMMENSE_HEAT, 12816, 46, 2.3, 202.4, Ticks.fromMinutes(32), 5),
	MAGPIE(2, 6824, -1, null, 8005, 8553, Scroll.THIEVING_FINGERS, 12041, 47, 0.9, 83.2, Ticks.fromMinutes(32), 5, "forage_magpie"),
	BLOATED_LEECH(14, 6843, 6844, XPType.ACCURATE, 7711, 8567, Scroll.BLOOD_DRAIN, 12061, 49, 2.4, 215.2, Ticks.fromMinutes(32), 5),
	SPIRIT_TERRORBIRD(2, 6794, 6795, XPType.CONTROLLED, 8231, 8557, Scroll.TIRELESS_RUN, 12007, 52, 0.7, 68.4, Ticks.fromMinutes(32), 6, 12),
	ABYSSAL_PARASITE(0, 6818, 6819, XPType.MAGIC, 7669, 8556, Scroll.ABYSSAL_DRAIN, 12035, 54, 1.1, 94.8, Ticks.fromMinutes(32), 6, 7),
	SPIRIT_JELLY(13, 6992, 6993, XPType.AGGRESSIVE, 8513, 7075, Scroll.DISSOLVE, 12027, 55, 5.5, 484.0, Ticks.fromMinutes(48), 6),
	IBIS(2, 6991, -1, null, 8202, 8552, Scroll.FISH_RAIN, 12531, 56, 1.1, 98.8, Ticks.fromMinutes(32), 6, "forage_ibis") {
		@Override
		public boolean canRollForager(Player player) {
			return player.getActionManager().doingAction(Fishing.class);
		}
	},
	SPIRIT_KYATT(7, 7365, 7366, XPType.ACCURATE, 7909, 8917, Scroll.AMBUSH, 12812, 57, 5.7, 501.6, Ticks.fromMinutes(48), 6),
	SPIRIT_LARUPIA(7, 7337, 7338, XPType.CONTROLLED, 7909, 8917, Scroll.RENDING, 12784, 57, 5.7, 501.6, Ticks.fromMinutes(48), 6),
	SPIRIT_GRAAHK(7, 7363, 7364, XPType.AGGRESSIVE, 7909, 8917, Scroll.GOAD, 12810, 57, 5.6, 501.6, Ticks.fromMinutes(48), 6),
	KARAMTHULU_OVERLORD(3, 6809, 6810, XPType.RANGED, 7969, 8543, Scroll.DOOMSPHERE, 12023, 58, 5.8, 510.4, Ticks.fromMinutes(48), 6),
	SMOKE_DEVIL(22, 6865, 6866, XPType.MAGIC, 7819, 8530, Scroll.DUST_CLOUD, 12085, 61, 3.1, 268.0, Ticks.fromMinutes(48), 7),
	ABYSSAL_LURKER(2, 6820, 6821, XPType.CONTROLLED, 7683, 8564, Scroll.ABYSSAL_STEALTH, 12037, 62, 1.9, 109.6, Ticks.fromMinutes(48), 7, 12),
	SPIRIT_COBRA(111, 6802, 6803, XPType.ACCURATE, 8157, 8550, Scroll.OPHIDIAN_INCUBATION, 12015, 63, 3.1, 276.8, Ticks.fromMinutes(48), 7),
	STRANGER_PLANT(3, 6827, 6828, XPType.CONTROLLED, 8216, 8533, Scroll.POISONOUS_BLAST, 12045, 64, 3.2, 281.6, Ticks.fromMinutes(48), 7, "forage_stranger_plant"),
	BARKER_TOAD(1, 6889, 6890, XPType.AGGRESSIVE, 7702, 8539, Scroll.TOAD_BARK, 12123, 66, 1.0, 87.0, Ticks.fromMinutes(48), 7),
	WAR_TORTOISE(2, 6815, 6816, XPType.DEFENSIVE, 8282, 8542, Scroll.TESTUDO, 12031, 67, 0.7, 58.6, Ticks.fromMinutes(48), 7, 18),
	BUNYIP(7, 6813, 6814, XPType.ACCURATE, 7736, 8547, Scroll.SWALLOW_WHOLE, 12029, 68, 1.4, 119.2, Ticks.fromMinutes(48), 7) {
		@Override
		public void tick(Player owner, Familiar familiar) {
			familiar.getAttribs().decI("healTicks");
			if (familiar.getAttribs().getI("healTicks") <= 0 && owner != null) {
				owner.heal((int) (owner.getMaxHitpoints()*0.02));
				owner.setNextSpotAnim(new SpotAnim(1507));
				familiar.getAttribs().setI("healTicks", 25);
			}
		}
	},
	FRUIT_BAT(7, 6817, -1, null, 8279, 8563, Scroll.FRUITFALL, 12033, 69, 1.4, 121.2, Ticks.fromMinutes(48), 7, "forage_fruit_bat"),
	RAVENOUS_LOCUST(6, 7372, 7373, XPType.ACCURATE, 7997, 8931, Scroll.FAMINE, 12820, 70, 1.5, 132.0, Ticks.fromMinutes(48), 4),
	ARCTIC_BEAR(7, 6839, 6840, XPType.CONTROLLED, 8522, 8566, Scroll.ARCTIC_BLAST, 12057, 71, 1.1, 93.2, Ticks.fromMinutes(48), 8),
	PHOENIX(3, 8575, 8576, XPType.MAGIC, 11095, 11096, Scroll.RISE_FROM_THE_ASHES, 14623, 72, 3.0, 301.0, Ticks.fromMinutes(48), 8),
	OBSIDIAN_GOLEM(28, 7345, 7346, XPType.AGGRESSIVE, 8049, 8924, Scroll.VOLCANIC_STRENGTH, 12792, 73, 7.3, 642.4, Ticks.fromMinutes(64), 8),
	GRANITE_LOBSTER(59, 6849, 6850, XPType.DEFENSIVE, 8122, 8548, Scroll.CRUSHING_CLAW, 12069, 74, 3.7, 325.6, Ticks.fromMinutes(64), 8, "forage_granite_lobster") {
		@Override
		public boolean canRollForager(Player player) {
			return player.getActionManager().doingAction(Fishing.class);
		}
	},
	PRAYING_MANTIS(9, 6798, 6799, XPType.ACCURATE, 8075, 8558, Scroll.MANTIS_STRIKE, 12011, 75, 3.6, 329.6, Ticks.fromMinutes(64), 8),
	FORGE_REGENT(34, 7335, 7336, XPType.RANGED, 7870, 8934, Scroll.INFERNO, 12782, 76, 1.5, 134.0, Ticks.fromMinutes(64), 9),
	TALON_BEAST(104, 7347, 7348, XPType.AGGRESSIVE, 8045, 8933, Scroll.DEADLY_CLAW, 12794, 77, 3.8, 1015.2, Ticks.fromMinutes(64), 9),
	GIANT_ENT(76, 6800, 6801, XPType.CONTROLLED, 7850, 8559, Scroll.ACORN_MISSILE, 12013, 78, 1.6, 136.8, Ticks.fromMinutes(64), 8, "forage_giant_ent"),
	HYDRA(19, 6811, 6812, XPType.RANGED, 7940, 8538, Scroll.REGROWTH, 12025, 80, 1.6, 140.8, Ticks.fromMinutes(64), 8),
	SPIRIT_DAGANNOTH(7, 6804, 6805, XPType.CONTROLLED, 7783, 8560, Scroll.SPIKE_SHOT, 12017, 83, 4.1, 364.8, Ticks.fromMinutes(64), 9),
	UNICORN_STALLION(107, 6822, 6823, XPType.CONTROLLED, 8266, 8565, Scroll.HEALING_AURA, 12039, 88, 1.8, 154.4, Ticks.fromMinutes(64), 9),
	WOLPERTINGER(7, 6869, 6870, XPType.MAGIC, 8309, 8534, Scroll.MAGIC_FOCUS, 12089, 92, 4.6, 404.8, Ticks.fromMinutes(64), 10),
	PACK_YAK(107, 6873, 6874, XPType.AGGRESSIVE, 8058, 8536, Scroll.WINTER_STORAGE, 12093, 96, 4.8, 422.2, Ticks.fromMinutes(64), 10, 30),
	FIRE_TITAN(23, 7355, 7356, XPType.MAGIC, 7829, 8925, Scroll.TITANS_CONSTITUTION, 12802, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
	MOSS_TITAN(27, 7357, 7358, XPType.AGGRESSIVE, 8188, 8926, Scroll.TITANS_CONSTITUTION, 12804, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
	ICE_TITAN(100, 7359, 7360, XPType.ACCURATE, 8188, 8926, Scroll.TITANS_CONSTITUTION, 12806, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
	LAVA_TITAN(27, 7341, 7342, XPType.AGGRESSIVE, 7987, 8928, Scroll.EBON_THUNDER, 12788, 83, 8.3, 730.4, Ticks.fromMinutes(64), 9),
	SWAMP_TITAN(104, 7329, 7330, XPType.ACCURATE, 8225, 8932, Scroll.SWAMP_PLAGUE, 12776, 85, 4.2, 373.6, Ticks.fromMinutes(64), 9),
	GEYSER_TITAN(27, 7339, 7340, XPType.RANGED, 7881, 7880, Scroll.BOIL, 12786, 89, 8.9, 783.2, Ticks.fromMinutes(64), 10),
	ABYSSAL_TITAN(127, 7349, 7350, XPType.ACCURATE, 8188, 8926, Scroll.ESSENCE_SHIPMENT, 12796, 93, 1.9, 163.2, Ticks.fromMinutes(64), 10, 20),
	IRON_TITAN(77, 7375, 7376, XPType.DEFENSIVE, 8188, 8926, Scroll.IRON_WITHIN, 12822, 95, 8.6, 417.6, Ticks.fromMinutes(64), 10),
	STEEL_TITAN(27, 7343, 7344, XPType.RANGED, 8188, 8926, Scroll.STEEL_OF_LEGENDS, 12790, 99, 4.9, 435.2, Ticks.fromMinutes(64), 10),
	
	MEERKATS(7, 11640, -1, null, 14316, 14317, Scroll.FETCH_CASKET, 19622, 4, 0.1, 0.0, Ticks.fromMinutes(48), 1),
	GHAST(36, 13979, 13980, XPType.PRAYER, 6782, 9467, Scroll.GHASTLY_ATTACK, 21444, 87, 3.0, 0.0, Ticks.fromMinutes(64), 1),
	
	BLOODRAGER_1(36, 11106, 11107, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_1, 17935, 1, 0.5, 5.0, Ticks.fromMinutes(64), 1),
	BLOODRAGER_2(36, 11108, 11109, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_2, 17936, 11, 1.0, 19.5, Ticks.fromMinutes(64), 1),
	BLOODRAGER_3(36, 11110, 11111, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_3, 17937, 21, 1.5, 43, Ticks.fromMinutes(64), 1),
	BLOODRAGER_4(36, 11112, 11113, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_4, 17938, 31, 2.0, 68.5, Ticks.fromMinutes(64), 1),
	BLOODRAGER_5(36, 11114, 11115, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_5, 17939, 41, 2.5, 99.5, Ticks.fromMinutes(64), 1),
	BLOODRAGER_6(36, 11116, 11117, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_6, 17940, 51, 3.0, 157, Ticks.fromMinutes(64), 1),
	BLOODRAGER_7(36, 11118, 11119, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_7, 17941, 61, 3.5, 220, Ticks.fromMinutes(64), 1),
	BLOODRAGER_8(36, 11120, 11121, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_8, 17942, 71, 4.0, 325, Ticks.fromMinutes(64), 1),
	BLOODRAGER_9(36, 11122, 11123, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_9, 17943, 81, 4.5, 517.5, Ticks.fromMinutes(64), 1),
	BLOODRAGER_10(36, 11124, 11125, XPType.AGGRESSIVE, 13684, 13685, Scroll.SUNDERING_STRIKE_10, 17944, 91, 5.0, 810, Ticks.fromMinutes(64), 1),
	
	STORMBRINGER_1(36, 11126, 11127, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_1, 17945, 3, 0.7, 6.4, Ticks.fromMinutes(64), 1),
	STORMBRINGER_2(36, 11128, 11129, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_2, 17946, 13, 1.2, 21.5, Ticks.fromMinutes(64), 1),
	STORMBRINGER_3(36, 11130, 11131, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_3, 17947, 23, 1.7, 45.8, Ticks.fromMinutes(64), 1),
	STORMBRINGER_4(36, 11132, 11133, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_4, 17948, 33, 2.2, 72.3, Ticks.fromMinutes(64), 1),
	STORMBRINGER_5(36, 11134, 11135, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_5, 17949, 43, 2.7, 104.5, Ticks.fromMinutes(64), 1),
	STORMBRINGER_6(36, 11136, 11137, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_6, 17950, 53, 3.2, 164, Ticks.fromMinutes(64), 1),
	STORMBRINGER_7(36, 11138, 11139, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_7, 17951, 63, 3.7, 229.2, Ticks.fromMinutes(64), 1),
	STORMBRINGER_8(36, 11140, 11141, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_8, 17952, 73, 4.2, 336.6, Ticks.fromMinutes(64), 1),
	STORMBRINGER_9(36, 11142, 11143, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_9, 17953, 83, 4.7, 531.7, Ticks.fromMinutes(64), 1),
	STORMBRINGER_10(36, 11144, 11145, XPType.MAGIC, 13676, 13677, Scroll.SNARING_WAVE_10, 17954, 93, 5.2, 827, Ticks.fromMinutes(64), 1),
	
	HOARDSTALKER_1(36, 11146, 11147, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_1, 17955, 5, 0.8, 7.1, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t1"),
	HOARDSTALKER_2(36, 11148, 11149, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_2, 17956, 15, 1.3, 22.5, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t2"),
	HOARDSTALKER_3(36, 11150, 11151, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_3, 17957, 25, 1.8, 47.2, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t3"),
	HOARDSTALKER_4(36, 11152, 11153, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_4, 17958, 35, 2.3, 74.2, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t4"),
	HOARDSTALKER_5(36, 11154, 11155, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_5, 17959, 45, 2.8, 107, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t5"),
	HOARDSTALKER_6(36, 11156, 11157, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_6, 17960, 55, 3.3, 167.5, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t6"),
	HOARDSTALKER_7(36, 11158, 11159, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_7, 17961, 65, 3.8, 233.8, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t7"),
	HOARDSTALKER_8(36, 11160, 11161, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_8, 17962, 75, 4.3, 342.4, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t8"),
	HOARDSTALKER_9(36, 11162, 11163, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_9, 17963, 85, 4.8, 538.8, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t9"),
	HOARDSTALKER_10(36, 11164, 11165, XPType.DEFENSIVE, 13684, 13685, Scroll.APTITUDE_10, 17964, 95, 5.3, 835.5, Ticks.fromMinutes(64), 1, "forage_hoardstalker_t10"),
	
	SKINWEAVER_1(36, 11166, 11167, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_1, 17965, 9, 1.0, 8.5, Ticks.fromMinutes(64), 1),
	SKINWEAVER_2(36, 11168, 11169, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_2, 17966, 19, 1.5, 24.5, Ticks.fromMinutes(64), 1),
	SKINWEAVER_3(36, 11170, 11171, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_3, 17967, 29, 2.0, 50, Ticks.fromMinutes(64), 1),
	SKINWEAVER_4(36, 11172, 11173, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_4, 17968, 39, 2.5, 78, Ticks.fromMinutes(64), 1),
	SKINWEAVER_5(36, 11174, 11175, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_5, 17969, 49, 3.0, 112, Ticks.fromMinutes(64), 1),
	SKINWEAVER_6(36, 11176, 11177, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_6, 17970, 59, 3.5, 174.5, Ticks.fromMinutes(64), 1),
	SKINWEAVER_7(36, 11178, 11179, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_7, 17971, 69, 4.0, 243, Ticks.fromMinutes(64), 1),
	SKINWEAVER_8(36, 11180, 11181, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_8, 17972, 79, 4.5, 354, Ticks.fromMinutes(64), 1),
	SKINWEAVER_9(36, 11182, 11183, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_9, 17973, 89, 5.0, 553, Ticks.fromMinutes(64), 1),
	SKINWEAVER_10(36, 11184, 11185, XPType.MAGIC, 13676, 13677, Scroll.GLIMMER_OF_LIGHT_10, 17974, 99, 5.5, 852.5, Ticks.fromMinutes(64), 1),
	
	WORLDBEARER_1(36, 11186, 11187, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_1, 17975, 7, 0.9, 7.8, Ticks.fromMinutes(64), 1, 12),
	WORLDBEARER_2(36, 11188, 11189, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_2, 17976, 17, 1.4, 23.5, Ticks.fromMinutes(64), 1, 14),
	WORLDBEARER_3(36, 11190, 11191, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_3, 17977, 27, 1.9, 48.6, Ticks.fromMinutes(64), 1, 16),
	WORLDBEARER_4(36, 11192, 11193, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_4, 17978, 37, 2.4, 76.1, Ticks.fromMinutes(64), 1, 18),
	WORLDBEARER_5(36, 11194, 11195, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_5, 17979, 47, 2.9, 109.5, Ticks.fromMinutes(64), 1, 20),
	WORLDBEARER_6(36, 11196, 11197, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_6, 17980, 57, 3.4, 171, Ticks.fromMinutes(64), 1, 22),
	WORLDBEARER_7(36, 11198, 11199, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_7, 17981, 67, 3.9, 238.4, Ticks.fromMinutes(64), 1, 24),
	WORLDBEARER_8(36, 11200, 11201, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_8, 17982, 77, 4.4, 348.2, Ticks.fromMinutes(64), 1, 26),
	WORLDBEARER_9(36, 11202, 11203, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_9, 17983, 87, 4.9, 545.9, Ticks.fromMinutes(64), 1, 28),
	WORLDBEARER_10(36, 11204, 11205, XPType.ACCURATE, 13684, 13685, Scroll.SECOND_WIND_10, 17984, 97, 5.4, 844, Ticks.fromMinutes(64), 1, 30),
	
	DEATHSLINGER_1(36, 11206, 11207, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_1, 17985, 2, 0.6, 5.7, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_2(36, 11208, 11209, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_2, 17986, 12, 1.1, 20.5, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_3(36, 11210, 11211, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_3, 17987, 22, 1.6, 44.4, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_4(36, 11212, 11213, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_4, 17988, 32, 2.1, 70.4, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_5(36, 11214, 11215, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_5, 17989, 42, 2.6, 102, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_6(36, 11216, 11217, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_6, 17990, 52, 3.1, 160.5, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_7(36, 11218, 11219, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_7, 17991, 62, 3.6, 224.6, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_8(36, 11220, 11221, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_8, 17992, 72, 4.1, 330.8, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_9(36, 11222, 11223, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_9, 17993, 82, 4.6, 524.6, Ticks.fromMinutes(64), 1),
	DEATHSLINGER_10(36, 11224, 11225, XPType.RANGED, 13684, 13685, Scroll.POISONOUS_SHOT_10, 17994, 92, 5.1, 818.5, Ticks.fromMinutes(64), 1),
	
	
	CLAY_POUCH_1(57, 8240, 8241, XPType.ACCURATE, 10595, 10593, Scroll.CLAY_DEPOSIT, 14422, 1, 0.0, 0.0, Ticks.fromMinutes(30), 1, 1),
	CLAY_POUCH_2(57, 8242, 8243, XPType.ACCURATE, 10595, 10593, Scroll.CLAY_DEPOSIT, 14424, 20, 0.0, 0.0, Ticks.fromMinutes(30), 1, 6),
	CLAY_POUCH_3(57, 8244, 8245, XPType.ACCURATE, 10595, 10593, Scroll.CLAY_DEPOSIT, 14426, 40, 0.0, 0.0, Ticks.fromMinutes(30), 1, 12),
	CLAY_POUCH_4(57, 8246, 8247, XPType.ACCURATE, 10595, 10593, Scroll.CLAY_DEPOSIT, 14428, 60, 0.0, 0.0, Ticks.fromMinutes(30), 1, 18),
	CLAY_POUCH_5(57, 8248, 8249, XPType.ACCURATE, 10595, 10593, Scroll.CLAY_DEPOSIT, 14430, 80, 0.0, 0.0, Ticks.fromMinutes(30), 1, 24),
	;
	

	private static final Map<Integer, Pouch> pouches = new HashMap<>();

	static {
		for (Pouch pouch : Pouch.values())
			pouches.put(pouch.id, pouch);
	}

	public static Pouch forId(int id) {
		return pouches.get(id);
	}
	
	private int headAnimIndex, baseNpc, pvpNpc, spawnAnim, despawnAnim;
	private XPType xpType;
	private Scroll scroll;
	private int id;
	private int level;
	private int summoningCost;
	private double summonXp;
	private double experience;
	private int pouchTime;
	private int bobSize;
	private String forageDropTable;
	
	private Pouch(int headAnimIndex, int baseNpc, int pvpNpc, XPType xpType, int spawnAnimation, int despawnAnim, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost) {
		this(headAnimIndex, baseNpc, pvpNpc, xpType, spawnAnimation, despawnAnim, scroll, realPouchId, level, minorExperience, experience, pouchTime, summoningCost, 0, null);
	}
	
	private Pouch(int headAnimIndex, int baseNpc, int pvpNpc, XPType xpType, int spawnAnimation, int despawnAnim, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost, String forageDropTable) {
		this(headAnimIndex, baseNpc, pvpNpc, xpType, spawnAnimation, despawnAnim, scroll, realPouchId, level, minorExperience, experience, pouchTime, summoningCost, forageDropTable != null ? 30 : 0, forageDropTable);
	}

	private Pouch(int headAnimIndex, int baseNpc, int pvpNpc, XPType xpType, int spawnAnimation, int despawnAnim, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost, int bobSize) {
		this(headAnimIndex, baseNpc, pvpNpc, xpType, spawnAnimation, despawnAnim, scroll, realPouchId, level, minorExperience, experience, pouchTime, summoningCost, bobSize, null);
	}
	
	private Pouch(int headAnimIndex, int baseNpc, int pvpNpc, XPType xpType, int spawnAnim, int despawnAnim, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost, int bobSize, String forageDropTable) {
		this.headAnimIndex = headAnimIndex;
		this.xpType = xpType;
		this.baseNpc = baseNpc;
		this.pvpNpc = pvpNpc;
		this.spawnAnim = spawnAnim;
		this.despawnAnim = despawnAnim;
		this.scroll = scroll;
		this.level = level;
		this.id = realPouchId;
		this.summonXp = minorExperience;
		this.experience = experience;
		this.summoningCost = summoningCost;
		this.pouchTime = pouchTime;
		this.bobSize = bobSize;
		this.forageDropTable = forageDropTable;
	}
	
	public Object[] getIdKeys() {
		int size = 1;
		if (pvpNpc != -1)
			size = 2;
		Object[] ids = new Object[size];
		ids[0] = baseNpc;
		if (pvpNpc != -1)
			ids[1] = pvpNpc;
		return ids;
	}
	
	public void tick(Player owner, Familiar familiar) {
		
	}
	
	public PouchMaterialList getMaterialList() {
		return PouchMaterialList.forId(id);
	}
	
	public XPType getXpType() {
		return xpType;
	}
	
	public int getDespawnAnim() {
		return despawnAnim;
	}
	
	public boolean isForager() {
		return forageDropTable != null;
	}
	
	public int getBaseNpc() {
		return baseNpc;
	}
	
	public int getPVPNpc() {
		return pvpNpc;
	}

	public int getLevel() {
		return level;
	}

	public int getId() {
		return id;
	}

	public int getSummoningCost() {
		return summoningCost;
	}

	public double getSummonXp() {
		return summonXp;
	}

	public double getExperience() {
		return experience;
	}

	public int getPouchTime() {
		return pouchTime;
	}

	public Scroll getScroll() {
		return scroll;
	}

	public int getBobSize() {
		return bobSize;
	}

	public int getSpawnAnim() {
		return spawnAnim;
	}

	public boolean isBob() {
		return bobSize > 0 && !isForager();
	}

	public static Object[] getAllNPCKeysWithInventory() {
		return Streams.concat(Arrays.stream(Pouch.values()).filter(p -> p.bobSize > 0).map(p -> p.getIdKeys()[0]).filter(i -> (int) i != -1), Arrays.stream(Pouch.values()).filter(p -> p.bobSize > 0).map(p -> p.getIdKeys().length <= 1 ? -1 : p.getIdKeys()[1]).filter(i -> (int) i != -1)).toArray();
	}
	
	public static Object[] getAllNPCIdKeys() {
		return Streams.concat(Arrays.stream(Pouch.values()).map(p -> p.getIdKeys()[0]).filter(i -> (int) i != -1), Arrays.stream(Pouch.values()).map(p -> p.getIdKeys().length <= 1 ? -1 : p.getIdKeys()[1]).filter(i -> (int) i != -1)).toArray();
	}

	public boolean isPassive() {
		return xpType == null;
	}
	
	public boolean canRollForager(Player player) {
		return true;
	}

	public void rollForage(Player player, ItemsContainer<Item> inv) {
		DropSet drop = DropSets.getDropSet(forageDropTable);
		if (drop == null || !canRollForager(player))
			return;
		inv.addAll(DropTable.calculateDrops(player, drop));
	}

	public int getHeadAnimIndex() {
		return headAnimIndex;
	}
}

