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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.content.skills.summoning.familiars.*;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.Ticks;
import com.rs.utils.json.FamiliarAdapter;

@PluginEventHandler
public class Summoning {

	public enum Pouch {
		SPIRIT_WOLF(6829, 6830, Scroll.HOWL, 12047, 1, 0.1, 4.8, Ticks.fromMinutes(16), 1),
		DREADFOWL(6825, 6826, Scroll.DREADFOWL_STRIKE, 12043, 4, 0.1, 9.3, Ticks.fromMinutes(16), 1),
		SPIRIT_SPIDER(6841, 6842, Scroll.EGG_SPAWN, 12059, 10, 0.2, 12.6, Ticks.fromMinutes(16), 2, true),
		THORNY_SNAIL(6806, 6807, Scroll.SLIME_SPRAY, 12019, 13, 0.2, 12.6, Ticks.fromMinutes(16), 2, 3),
		GRANITE_CRAB(6796, 6797, Scroll.STONY_SHELL, 12009, 16, 0.2, 21.6, Ticks.fromMinutes(16), 2, true),
		SPIRIT_MOSQUITO(7331, 7332, Scroll.PESTER, 12778, 17, 0.2, 46.5, Ticks.fromMinutes(16), 2),
		DESERT_WYRM(6831, 6832, Scroll.ELECTRIC_LASH, 12049, 18, 0.4, 31.2, Ticks.fromMinutes(16), 1),
		SPIRIT_SCORPION(6837, 6838, Scroll.VENOM_SHOT, 12055, 19, 0.9, 83.2, Ticks.fromMinutes(16), 2),
		SPIRIT_TZ_KIH(7361, 7362, Scroll.FIREBALL, 12808, 22, 1.1, 96.8, Ticks.fromMinutes(16), 3),
		ALBINO_RAT(6847, 6848, Scroll.CHEESE_FEAST, 12067, 23, 2.3, 202.4, Ticks.fromMinutes(16), 3),
		SPIRIT_KALPHITE(6994, 6995, Scroll.SANDSTORM, 12063, 25, 2.5, 220, Ticks.fromMinutes(16), 3, 6),
		COMPOST_MOUND(6871, 6872, Scroll.COMPOST_GENERATE, 12091, 28, 0.6, 49.8, Ticks.fromMinutes(32), 6, true),
		GIANT_CHINCHOMPA(7353, 7354, Scroll.EXPLODE, 12800, 29, 2.5, 255.2, Ticks.fromMinutes(32), 1),
		VAMPYRE_BAT(6835, 6836, Scroll.VAMPYRE_TOUCH, 12053, 31, 1.6, 136.0, Ticks.fromMinutes(32), 4),
		HONEY_BADGER(6845, 6846, Scroll.INSANE_FEROCITY, 12065, 32, 1.6, 140.8, Ticks.fromMinutes(32), 4),
		BEAVER(6808, -1, Scroll.MULTICHOP, 12021, 33, 0.7, 57.6, Ticks.fromMinutes(32), 4, true),
		VOID_RAVAGER(7370, 7371, Scroll.CALL_TO_ARMS, 12818, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4, true),
		VOID_SPINNER(7333, 7334, Scroll.CALL_TO_ARMS, 12780, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		VOID_TORCHER(7351, 7352, Scroll.CALL_TO_ARMS, 12798, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		VOID_SHIFTER(7367, 7368, Scroll.CALL_TO_ARMS, 12814, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		BRONZE_MINOTAUR(6853, 6854, Scroll.BRONZE_BULL, 12073, 36, 2.4, 316.8, Ticks.fromMinutes(32), 9),
		IRON_MINOTAUR(6855, 6856, Scroll.IRON_BULL, 12075, 46, 4.6, 404.8, Ticks.fromMinutes(32), 9),
		STEEL_MINOTAUR(6857, 6858, Scroll.STEEL_BULL, 12077, 56, 5.6, 492.8, Ticks.fromMinutes(48), 9),
		MITHRIL_MINOTAUR(6859, 6860, Scroll.MITHRIL_BULL, 12079, 66, 6.6, 580.8, Ticks.fromMinutes(48), 9),
		ADAMANT_MINOTAUR(6861, 6862, Scroll.ADAMANT_BULL, 12081, 76, 8.6, 668.8, Ticks.fromMinutes(64), 9),
		RUNE_MINOTAUR(6863, 6864, Scroll.RUNE_BULL, 12083, 86, 8.6, 756.8, Ticks.fromMinutes(64), 9),
		BULL_ANT(6867, 6868, Scroll.UNBURDEN, 12087, 40, 0.6, 52.8, Ticks.fromMinutes(32), 5, 9),
		MACAW(6851, 6852, Scroll.HERBCALL, 12071, 41, 0.8, 72.4, Ticks.fromMinutes(32), 5, true),
		EVIL_TURNIP(6833, 6834, Scroll.EVIL_FLAMES, 12051, 42, 2.1, 184.8, Ticks.fromMinutes(32), 5, true),
		SPIRIT_COCKATRICE(6875, 6876, Scroll.PETRIFYING_GAZE, 12095, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		SPIRIT_GUTHATRICE(6877, 6878, Scroll.PETRIFYING_GAZE, 12097, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		SPIRIT_SARATRICE(6879, 6880, Scroll.PETRIFYING_GAZE, 12099, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		SPIRIT_ZAMATRICE(6881, 6882, Scroll.PETRIFYING_GAZE, 12101, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		SPIRIT_PENGATRICE(6883, 6884, Scroll.PETRIFYING_GAZE, 12103, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		SPIRIT_CORAXATRICE(6885, 6886, Scroll.PETRIFYING_GAZE, 12105, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		SPIRIT_VULATRICE(6887, 6888, Scroll.PETRIFYING_GAZE, 12107, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5, true),
		PYRELORD(7377, 7378, Scroll.IMMENSE_HEAT, 12816, 46, 2.3, 202.4, Ticks.fromMinutes(32), 5),
		MAGPIE(6824, -1, Scroll.THIEVING_FINGERS, 12041, 47, 0.9, 83.2, Ticks.fromMinutes(32), 5, true),
		BLOATED_LEECH(6843, 6844, Scroll.BLOOD_DRAIN, 12061, 49, 2.4, 215.2, Ticks.fromMinutes(32), 5),
		SPIRIT_TERRORBIRD(6794, 6795, Scroll.TIRELESS_RUN, 12007, 52, 0.7, 68.4, Ticks.fromMinutes(32), 6, 12),
		ABYSSAL_PARASITE(6818, 6819, Scroll.ABYSSAL_DRAIN, 12035, 54, 1.1, 94.8, Ticks.fromMinutes(32), 6, 7),
		SPIRIT_JELLY(6992, 6993, Scroll.DISSOLVE, 12027, 55, 5.5, 484.0, Ticks.fromMinutes(48), 6),
		IBIS(6991, -1, Scroll.FISH_RAIN, 12531, 56, 1.1, 98.8, Ticks.fromMinutes(32), 6, true),
		SPIRIT_KYATT(7365, 7366, Scroll.AMBUSH, 12812, 57, 5.7, 501.6, Ticks.fromMinutes(48), 6),
		SPIRIT_LARUPIA(7337, 7338, Scroll.RENDING, 12784, 57, 5.7, 501.6, Ticks.fromMinutes(48), 6),
		SPIRIT_GRAAHK(7363, 7364, Scroll.GOAD, 12810, 57, 5.6, 501.6, Ticks.fromMinutes(48), 6),
		KARAMTHULU_OVERLOAD(6809, 6810, Scroll.DOOMSPHERE, 12023, 58, 5.8, 510.4, Ticks.fromMinutes(48), 6),
		SMOKE_DEVIL(6865, 6866, Scroll.DUST_CLOUD, 12085, 61, 3.1, 268.0, Ticks.fromMinutes(48), 7),
		ABYSSAL_LURKER(6820, 6821, Scroll.ABYSSAL_STEALTH, 12037, 62, 1.9, 109.6, Ticks.fromMinutes(48), 7, 12),
		SPIRIT_COBRA(6802, 6803, Scroll.OPHIDIAN_INCUBATION, 12015, 63, 3.1, 276.8, Ticks.fromMinutes(48), 7),
		STRANGER_PLANT(6827, 6828, Scroll.POISONOUS_BLAST, 12045, 64, 3.2, 281.6, Ticks.fromMinutes(48), 7, true),
		BARKER_TOAD(6889, 6890, Scroll.TOAD_BARK, 12123, 66, 1.0, 87.0, Ticks.fromMinutes(48), 7),
		WAR_TORTOISE(6815, 6816, Scroll.TESTUDO, 12031, 67, 0.7, 58.6, Ticks.fromMinutes(48), 7, 18),
		BUNYIP(6813, 6814, Scroll.SWALLOW_WHOLE, 12029, 68, 1.4, 119.2, Ticks.fromMinutes(48), 7),
		FRUIT_BAT(6817, -1, Scroll.FRUITFALL, 12033, 69, 1.4, 121.2, Ticks.fromMinutes(48), 7, true),
		RAVENOUS_LOCUST(7372, 7373, Scroll.FAMINE, 12820, 70, 1.5, 132.0, Ticks.fromMinutes(48), 4),
		ARCTIC_BEAR(6839, 6840, Scroll.ARCTIC_BLAST, 12057, 71, 1.1, 93.2, Ticks.fromMinutes(48), 8),
		PHEONIX(8575, 8576, Scroll.RISE_FROM_THE_ASHES, 14623, 72, 3.0, 301.0, Ticks.fromMinutes(48), 8),
		OBSIDIAN_GOLEM(7345, 7346, Scroll.VOLCANIC_STRENGTH, 12792, 73, 7.3, 642.4, Ticks.fromMinutes(64), 8),
		GRANITE_LOBSTER(6849, 6850, Scroll.CRUSHING_CLAW, 12069, 74, 3.7, 325.6, Ticks.fromMinutes(64), 8, true),
		PRAYING_MANTIS(6798, 6799, Scroll.MANTIS_STRIKE, 12011, 75, 3.6, 329.6, Ticks.fromMinutes(64), 8),
		FORGE_REGENT(7335, 7336, Scroll.INFERNO, 12782, 76, 1.5, 134.0, Ticks.fromMinutes(64), 9),
		TALON_BEAST(7347, 7348, Scroll.DEADLY_CLAW, 12794, 77, 3.8, 1015.2, Ticks.fromMinutes(64), 9),
		GIANT_ENT(6800, 6801, Scroll.ACORN_MISSILE, 12013, 78, 1.6, 136.8, Ticks.fromMinutes(64), 8, true),
		HYDRA(6811, 6812, Scroll.REGROWTH, 12025, 80, 1.6, 140.8, Ticks.fromMinutes(64), 8),
		SPIRIT_DAGANNOTH(6804, 6805, Scroll.SPIKE_SHOT, 12017, 83, 4.1, 364.8, Ticks.fromMinutes(64), 9),
		UNICORN_STALLION(6822, 6823, Scroll.HEALING_AURA, 12039, 88, 1.8, 154.4, Ticks.fromMinutes(64), 9),
		WOLPERTINGER(6869, 6870, Scroll.MAGIC_FOCUS, 12089, 92, 4.6, 404.8, Ticks.fromMinutes(64), 10),
		PACK_YAK(6873, 6874, Scroll.WINTER_STORAGE, 12093, 96, 4.8, 422.2, Ticks.fromMinutes(64), 10, 30),
		FIRE_TITAN(7355, 7356, Scroll.TITANS_CONSTITUTION, 12802, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
		MOSS_TITAN(7357, 7358, Scroll.TITANS_CONSTITUTION, 12804, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
		ICE_TITAN(7359, 7360, Scroll.TITANS_CONSTITUTION, 12806, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
		LAVA_TITAN(7341, 7342, Scroll.EBON_THUNDER, 12788, 83, 8.3, 730.4, Ticks.fromMinutes(64), 9),
		SWAMP_TITAN(7329, 7330, Scroll.SWAMP_PLAGUE, 12776, 85, 4.2, 373.6, Ticks.fromMinutes(64), 9),
		GEYSER_TITAN(7339, 7340, Scroll.BOIL, 12786, 89, 8.9, 783.2, Ticks.fromMinutes(64), 10),
		ABYSSAL_TITAN(7349, 7350, Scroll.ESSENCE_SHIPMENT, 12796, 93, 1.9, 163.2, Ticks.fromMinutes(64), 10, 20),
		IRON_TITAN(7375, 7376, Scroll.IRON_WITHIN, 12822, 95, 8.6, 417.6, Ticks.fromMinutes(64), 10),
		STEEL_TITAN(7343, 7344, Scroll.STEEL_OF_LEGENDS, 12790, 99, 4.9, 435.2, Ticks.fromMinutes(64), 10),
		
		MEERKATS(11640, -1, Scroll.FETCH_CASKET, 19622, 4, 0.1, 0.0, Ticks.fromMinutes(48), 1),
		GHAST(13979, 13980, Scroll.GHASTLY_ATTACK, 21444, 87, 3.0, 0.0, Ticks.fromMinutes(64), 1),
		
		BLOODRAGER_1(11106, 11107, Scroll.SUNDERING_STRIKE_1, 17935, 1, 0.5, 5.0, Ticks.fromMinutes(64), 1),
		BLOODRAGER_2(11108, 11109, Scroll.SUNDERING_STRIKE_2, 17936, 11, 1.0, 19.5, Ticks.fromMinutes(64), 1),
		BLOODRAGER_3(11110, 11111, Scroll.SUNDERING_STRIKE_3, 17937, 21, 1.5, 43, Ticks.fromMinutes(64), 1),
		BLOODRAGER_4(11112, 11113, Scroll.SUNDERING_STRIKE_4, 17938, 31, 2.0, 68.5, Ticks.fromMinutes(64), 1),
		BLOODRAGER_5(11114, 11115, Scroll.SUNDERING_STRIKE_5, 17939, 41, 2.5, 99.5, Ticks.fromMinutes(64), 1),
		BLOODRAGER_6(11116, 11117, Scroll.SUNDERING_STRIKE_6, 17940, 51, 3.0, 157, Ticks.fromMinutes(64), 1),
		BLOODRAGER_7(11118, 11119, Scroll.SUNDERING_STRIKE_7, 17941, 61, 3.5, 220, Ticks.fromMinutes(64), 1),
		BLOODRAGER_8(11120, 11121, Scroll.SUNDERING_STRIKE_8, 17942, 71, 4.0, 325, Ticks.fromMinutes(64), 1),
		BLOODRAGER_9(11122, 11123, Scroll.SUNDERING_STRIKE_9, 17943, 81, 4.5, 517.5, Ticks.fromMinutes(64), 1),
		BLOODRAGER_10(11124, 11125, Scroll.SUNDERING_STRIKE_10, 17944, 91, 5.0, 810, Ticks.fromMinutes(64), 1),
		
		STORMBRINGER_1(11126, 11127, Scroll.SNARING_WAVE_1, 17945, 3, 0.7, 6.4, Ticks.fromMinutes(64), 1),
		STORMBRINGER_2(11128, 11129, Scroll.SNARING_WAVE_2, 17946, 13, 1.2, 21.5, Ticks.fromMinutes(64), 1),
		STORMBRINGER_3(11130, 11131, Scroll.SNARING_WAVE_3, 17947, 23, 1.7, 45.8, Ticks.fromMinutes(64), 1),
		STORMBRINGER_4(11132, 11133, Scroll.SNARING_WAVE_4, 17948, 33, 2.2, 72.3, Ticks.fromMinutes(64), 1),
		STORMBRINGER_5(11134, 11135, Scroll.SNARING_WAVE_5, 17949, 43, 2.7, 104.5, Ticks.fromMinutes(64), 1),
		STORMBRINGER_6(11136, 11137, Scroll.SNARING_WAVE_6, 17950, 53, 3.2, 164, Ticks.fromMinutes(64), 1),
		STORMBRINGER_7(11138, 11139, Scroll.SNARING_WAVE_7, 17951, 63, 3.7, 229.2, Ticks.fromMinutes(64), 1),
		STORMBRINGER_8(11140, 11141, Scroll.SNARING_WAVE_8, 17952, 73, 4.2, 336.6, Ticks.fromMinutes(64), 1),
		STORMBRINGER_9(11142, 11143, Scroll.SNARING_WAVE_9, 17953, 83, 4.7, 531.7, Ticks.fromMinutes(64), 1),
		STORMBRINGER_10(11144, 11145, Scroll.SNARING_WAVE_10, 17954, 93, 5.2, 827, Ticks.fromMinutes(64), 1),
		
		HOARDSTALKER_1(11146, 11147, Scroll.APTITUDE_1, 17955, 5, 0.8, 7.1, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_2(11148, 11149, Scroll.APTITUDE_2, 17956, 15, 1.3, 22.5, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_3(11150, 11151, Scroll.APTITUDE_3, 17957, 25, 1.8, 47.2, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_4(11152, 11153, Scroll.APTITUDE_4, 17958, 35, 2.3, 74.2, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_5(11154, 11155, Scroll.APTITUDE_5, 17959, 45, 2.8, 107, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_6(11156, 11157, Scroll.APTITUDE_6, 17960, 55, 3.3, 167.5, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_7(11158, 11159, Scroll.APTITUDE_7, 17961, 65, 3.8, 233.8, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_8(11160, 11161, Scroll.APTITUDE_8, 17962, 75, 4.3, 342.4, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_9(11162, 11163, Scroll.APTITUDE_9, 17963, 85, 4.8, 538.8, Ticks.fromMinutes(64), 1, true),
		HOARDSTALKER_10(11164, 11165, Scroll.APTITUDE_10, 17964, 95, 5.3, 835.5, Ticks.fromMinutes(64), 1, true),
		
		SKINWEAVER_1(11166, 11167, Scroll.GLIMMER_OF_LIGHT_1, 17965, 9, 1.0, 8.5, Ticks.fromMinutes(64), 1),
		SKINWEAVER_2(11168, 11169, Scroll.GLIMMER_OF_LIGHT_2, 17966, 19, 1.5, 24.5, Ticks.fromMinutes(64), 1),
		SKINWEAVER_3(11170, 11171, Scroll.GLIMMER_OF_LIGHT_3, 17967, 29, 2.0, 50, Ticks.fromMinutes(64), 1),
		SKINWEAVER_4(11172, 11173, Scroll.GLIMMER_OF_LIGHT_4, 17968, 39, 2.5, 78, Ticks.fromMinutes(64), 1),
		SKINWEAVER_5(11174, 11175, Scroll.GLIMMER_OF_LIGHT_5, 17969, 49, 3.0, 112, Ticks.fromMinutes(64), 1),
		SKINWEAVER_6(11176, 11177, Scroll.GLIMMER_OF_LIGHT_6, 17970, 59, 3.5, 174.5, Ticks.fromMinutes(64), 1),
		SKINWEAVER_7(11178, 11179, Scroll.GLIMMER_OF_LIGHT_7, 17971, 69, 4.0, 243, Ticks.fromMinutes(64), 1),
		SKINWEAVER_8(11180, 11181, Scroll.GLIMMER_OF_LIGHT_8, 17972, 79, 4.5, 354, Ticks.fromMinutes(64), 1),
		SKINWEAVER_9(11182, 11183, Scroll.GLIMMER_OF_LIGHT_9, 17973, 89, 5.0, 553, Ticks.fromMinutes(64), 1),
		SKINWEAVER_10(11184, 11185, Scroll.GLIMMER_OF_LIGHT_10, 17974, 99, 5.5, 852.5, Ticks.fromMinutes(64), 1),
		
		WORLDBEARER_1(11186, 11187, Scroll.SECOND_WIND_1, 17975, 7, 0.9, 7.8, Ticks.fromMinutes(64), 1, 12),
		WORLDBEARER_2(11188, 11189, Scroll.SECOND_WIND_2, 17976, 17, 1.4, 23.5, Ticks.fromMinutes(64), 1, 14),
		WORLDBEARER_3(11190, 11191, Scroll.SECOND_WIND_3, 17977, 27, 1.9, 48.6, Ticks.fromMinutes(64), 1, 16),
		WORLDBEARER_4(11192, 11193, Scroll.SECOND_WIND_4, 17978, 37, 2.4, 76.1, Ticks.fromMinutes(64), 1, 18),
		WORLDBEARER_5(11194, 11195, Scroll.SECOND_WIND_5, 17979, 47, 2.9, 109.5, Ticks.fromMinutes(64), 1, 20),
		WORLDBEARER_6(11196, 11197, Scroll.SECOND_WIND_6, 17980, 57, 3.4, 171, Ticks.fromMinutes(64), 1, 22),
		WORLDBEARER_7(11198, 11199, Scroll.SECOND_WIND_7, 17981, 67, 3.9, 238.4, Ticks.fromMinutes(64), 1, 24),
		WORLDBEARER_8(11200, 11201, Scroll.SECOND_WIND_8, 17982, 77, 4.4, 348.2, Ticks.fromMinutes(64), 1, 26),
		WORLDBEARER_9(11202, 11203, Scroll.SECOND_WIND_9, 17983, 87, 4.9, 545.9, Ticks.fromMinutes(64), 1, 28),
		WORLDBEARER_10(11204, 11205, Scroll.SECOND_WIND_10, 17984, 97, 5.4, 844, Ticks.fromMinutes(64), 1, 30),
		
		DEATHSLINGER_1(11206, 11207, Scroll.POISONOUS_SHOT_1, 17985, 2, 0.6, 5.7, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_2(11208, 11209, Scroll.POISONOUS_SHOT_2, 17986, 12, 1.1, 20.5, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_3(11210, 11211, Scroll.POISONOUS_SHOT_3, 17987, 22, 1.6, 44.4, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_4(11212, 11213, Scroll.POISONOUS_SHOT_4, 17988, 32, 2.1, 70.4, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_5(11214, 11215, Scroll.POISONOUS_SHOT_5, 17989, 42, 2.6, 102, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_6(11216, 11217, Scroll.POISONOUS_SHOT_6, 17990, 52, 3.1, 160.5, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_7(11218, 11219, Scroll.POISONOUS_SHOT_7, 17991, 62, 3.6, 224.6, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_8(11220, 11221, Scroll.POISONOUS_SHOT_8, 17992, 72, 4.1, 330.8, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_9(11222, 11223, Scroll.POISONOUS_SHOT_9, 17993, 82, 4.6, 524.6, Ticks.fromMinutes(64), 1),
		DEATHSLINGER_10(11224, 11225, Scroll.POISONOUS_SHOT_10, 17994, 92, 5.1, 818.5, Ticks.fromMinutes(64), 1),
		
		
		CLAY_POUCH_1(8240, 8241, Scroll.CLAY_DEPOSIT, 14422, 1, 0.0, 0.0, Ticks.fromMinutes(30), 1),
		CLAY_POUCH_2(8242, 8243, Scroll.CLAY_DEPOSIT, 14424, 20, 0.0, 0.0, Ticks.fromMinutes(30), 1),
		CLAY_POUCH_3(8244, 8245, Scroll.CLAY_DEPOSIT, 14426, 40, 0.0, 0.0, Ticks.fromMinutes(30), 1),
		CLAY_POUCH_4(8246, 8247, Scroll.CLAY_DEPOSIT, 14428, 60, 0.0, 0.0, Ticks.fromMinutes(30), 1),
		CLAY_POUCH_5(8248, 8249, Scroll.CLAY_DEPOSIT, 14430, 80, 0.0, 0.0, Ticks.fromMinutes(30), 1),
		;
		

		private static final Map<Integer, Pouch> pouches = new HashMap<>();

		static {
			for (Pouch pouch : Pouch.values())
				pouches.put(pouch.realPouchId, pouch);
		}

		public static Pouch forId(int id) {
			return pouches.get(id);
		}
		
		private int baseNpc, pvpNpc;
		private Scroll scroll;
		private int realPouchId;
		private int level;
		private int summoningCost;
		private double minorExperience;
		private double experience;
		private int pouchTime;
		private int bobSize;
		private boolean forager;
		
		private Pouch(int baseNpc, int pvpNpc, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost) {
			this(baseNpc, pvpNpc, scroll, realPouchId, level, minorExperience, experience, pouchTime, summoningCost, 0, false);
		}
		
		private Pouch(int baseNpc, int pvpNpc, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost, boolean forager) {
			this(baseNpc, pvpNpc, scroll, realPouchId, level, minorExperience, experience, pouchTime, summoningCost, forager ? 30 : 0, forager);
		}

		private Pouch(int baseNpc, int pvpNpc, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost, int bobSize) {
			this(baseNpc, pvpNpc, scroll, realPouchId, level, minorExperience, experience, pouchTime, summoningCost, bobSize, false);
		}
		
		private Pouch(int baseNpc, int pvpNpc, Scroll scroll, int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost, int bobSize, boolean forager) {
			this.baseNpc = baseNpc;
			this.pvpNpc = pvpNpc;
			this.scroll = scroll;
			this.level = level;
			this.realPouchId = realPouchId;
			this.minorExperience = minorExperience;
			this.experience = experience;
			this.summoningCost = summoningCost;
			this.pouchTime = pouchTime;
			this.bobSize = 0;
			this.forager = forager;
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

		public int getRealPouchId() {
			return realPouchId;
		}

		public int getSummoningCost() {
			return summoningCost;
		}

		public double getMinorExperience() {
			return minorExperience;
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
	}
	
	public static enum ScrollTarget {
		ITEM, ENTITY, CLICK, OBJECT
	}
	
	public enum Scroll {
		HOWL(12425, ScrollTarget.ENTITY, "Howl", "Scares non-player opponents, causing them to retreat. However, this lasts for only a few seconds.", 0.1, 3),
		DREADFOWL_STRIKE(12445, ScrollTarget.MEME, "Dreadfowl Strike", "", 0.1, 3),
		FETCH_CASKET(19621, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.1, 12),
		EGG_SPAWN(12428, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.2, 6),
		SLIME_SPRAY(12459, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.2, 3),
		STONY_SHELL(12533, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.2, 12),
		PESTER(12838, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.5, 3),
		ELECTRIC_LASH(12460, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.4, 6),
		VENOM_SHOT(12432, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.9, 6),
		FIREBALL(12839, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.1, 6),
		CHEESE_FEAST(12430, ScrollTarget.MEME, "NAME", "DESCRIPTION", 2.3, 6),
		SANDSTORM(12446, ScrollTarget.MEME, "NAME", "DESCRIPTION", 2.5, 6),
		COMPOST_GENERATE(12440, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.6, 12),
		EXPLODE(12834, ScrollTarget.MEME, "NAME", "DESCRIPTION", 2.9, 3),
		VAMPYRE_TOUCH(12477, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.5, 4),
		INSANE_FEROCITY(12433, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.6, 12),
		MULTICHOP(12429, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.7, 3),
		CALL_TO_ARMS(12443, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.7, 3),
		BRONZE_BULL(12461, ScrollTarget.MEME, "NAME", "DESCRIPTION", 3.6, 6),
		UNBURDEN(12431, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.6, 12),
		HERBCALL(12422, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.8, 12),
		EVIL_FLAMES(12448, ScrollTarget.MEME, "NAME", "DESCRIPTION", 2.1, 6),
		PETRIFYING_GAZE(12458, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.9, 3),
		IRON_BULL(12462, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.6, 6),
		IMMENSE_HEAT(12829, ScrollTarget.MEME, "NAME", "DESCRIPTION", 2.3, 6),
		THIEVING_FINGERS(12426, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.9, 12),
		BLOOD_DRAIN(12444, ScrollTarget.MEME, "NAME", "DESCRIPTION", 2.4, 6),
		TIRELESS_RUN(12441, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.8, 8),
		ABYSSAL_DRAIN(12454, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.1, 6),
		DISSOLVE(12453, ScrollTarget.MEME, "NAME", "DESCRIPTION", 5.5, 6),
		FISH_RAIN(12424, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.1, 12),
		STEEL_BULL(12463, ScrollTarget.MEME, "NAME", "DESCRIPTION", 5.6, 6),
		AMBUSH(12836, ScrollTarget.MEME, "NAME", "DESCRIPTION", 5.7, 3),
		RENDING(12840, ScrollTarget.MEME, "NAME", "DESCRIPTION", 5.7, 3),
		GOAD(12835, ScrollTarget.MEME, "NAME", "DESCRIPTION", 5.7, 6),
		DOOMSPHERE(12455, ScrollTarget.MEME, "NAME", "DESCRIPTION", 5.8, 3),
		DUST_CLOUD(12468, ScrollTarget.MEME, "NAME", "DESCRIPTION", 3, 6),
		ABYSSAL_STEALTH(12427, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.9, 20),
		OPHIDIAN_INCUBATION(12436, ScrollTarget.MEME, "NAME", "DESCRIPTION", 3.1, 3),
		POISONOUS_BLAST(12467, ScrollTarget.MEME, "NAME", "DESCRIPTION", 3.2, 6),
		MITHRIL_BULL(12464, ScrollTarget.MEME, "NAME", "DESCRIPTION", 6.6, 6),
		TOAD_BARK(12452, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1, 6),
		TESTUDO(12439, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.7, 20),
		SWALLOW_WHOLE(12438, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.4, 3),
		FRUITFALL(12423, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.4, 6),
		FAMINE(12830, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.5, 12),
		ARCTIC_BLAST(12451, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.1, 6),
		RISE_FROM_THE_ASHES(14622, ScrollTarget.MEME, "NAME", "DESCRIPTION", 8, 5),
		VOLCANIC_STRENGTH(12826, ScrollTarget.MEME, "NAME", "DESCRIPTION", 7.3, 12),
		MANTIS_STRIKE(12450, ScrollTarget.MEME, "NAME", "DESCRIPTION", 3.7, 6),
		CRUSHING_CLAW(12449, ScrollTarget.MEME, "NAME", "DESCRIPTION", 3.7, 6),
		INFERNO(12841, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.5, 6),
		ADAMANT_BULL(12465, ScrollTarget.MEME, "NAME", "DESCRIPTION", 7.6, 6),
		DEADLY_CLAW(12831, ScrollTarget.MEME, "NAME", "DESCRIPTION", 11.4, 6),
		ACORN_MISSILE(12457, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.6, 6),
		TITANS_CONSTITUTION(12824, ScrollTarget.MEME, "NAME", "DESCRIPTION", 7.9, 20),
		REGROWTH(12442, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.6, 6),
		SPIKE_SHOT(12456, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.1, 6),
		EBON_THUNDER(12837, ScrollTarget.MEME, "NAME", "DESCRIPTION", 8.3, 4),
		SWAMP_PLAGUE(12832, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.1, 6),
		RUNE_BULL(12466, ScrollTarget.MEME, "NAME", "DESCRIPTION", 8.6, 6),
		HEALING_AURA(12434, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.8, 20),
		BOIL(12833, ScrollTarget.MEME, "NAME", "DESCRIPTION", 8.9, 6),
		MAGIC_FOCUS(12437, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.6, 20),
		ESSENCE_SHIPMENT(12827, ScrollTarget.MEME, "NAME", "DESCRIPTION", 1.9, 6),
		IRON_WITHIN(12828, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.7, 12),
		WINTER_STORAGE(12435, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.8, 12),
		STEEL_OF_LEGENDS(12825, ScrollTarget.MEME, "NAME", "DESCRIPTION", 4.9, 12),
		GHASTLY_ATTACK(21453, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.9, 20),
		
		SUNDERING_STRIKE_1(18027, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_2(18028, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_3(18029, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_4(18030, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_5(18031, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_6(18032, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_7(18033, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_8(18034, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_9(18035, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SUNDERING_STRIKE_10(18036, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		
		POISONOUS_SHOT_1(18037, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_2(18038, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_3(18039, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_4(18040, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_5(18041, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_6(18042, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_7(18043, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_8(18044, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_9(18045, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		POISONOUS_SHOT_10(18046, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		
		SNARING_WAVE_1(18047, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_2(18048, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_3(18049, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_4(18050, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_5(18051, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_6(18052, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_7(18053, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_8(18054, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_9(18055, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SNARING_WAVE_10(18056, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		
		APTITUDE_1(18057, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_2(18058, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_3(18059, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_4(18060, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_5(18061, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_6(18062, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_7(18063, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_8(18064, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_9(18065, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		APTITUDE_10(18066, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		
		SECOND_WIND_1(18067, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_2(18068, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_3(18069, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_4(18070, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_5(18071, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_6(18072, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_7(18073, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_8(18074, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_9(18075, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		SECOND_WIND_10(18076, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		
		GLIMMER_OF_LIGHT_1(18077, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_2(18078, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_3(18079, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_4(18080, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_5(18081, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_6(18082, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_7(18083, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_8(18084, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_9(18085, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		GLIMMER_OF_LIGHT_10(18086, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 6),
		
		CLAY_DEPOSIT(14421, ScrollTarget.MEME, "NAME", "DESCRIPTION", 0.0, 12),
		;
		
		private ScrollTarget target;
		private String name;
		private String description;
		private int id;
		private double xp;
		private int pointCost;

		private Scroll(int scrollId, ScrollTarget target, String name, String description, double xp, int pointCost) {
			this.name = name;
			this.description = description;
			this.target = target;
			this.id = scrollId;
			this.xp = xp;
			this.pointCost = pointCost;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}

		public ScrollTarget getTarget() {
			return target;
		}
		
		public int getScrollId() {
			return id;
		}

		public double getExperience() {
			return xp;
		}
		
		public int getPointCost() {
			return pointCost;
		}
	}

	public static final int POUCHES_INTERFACE = 672, SCROLLS_INTERFACE = 666;
	private static final Animation SCROLL_INFUSIN_ANIMATION = new Animation(723);
	private static final Animation POUCH_INFUSION_ANIMATION = new Animation(725);
	private static final SpotAnim POUCH_INFUSION_GRAPHICS = new SpotAnim(1207);

	public static int getScrollId(int id) {
		return EnumDefinitions.getEnum(1283).getIntValue(id);
	}

	public static int getRequiredLevel(int id) {
		return EnumDefinitions.getEnum(1185).getIntValue(id);
	}

	public static boolean isFamiliar(int npcId) {
		return EnumDefinitions.getEnum(1320).getValues().containsValue(npcId);
	}

	public static boolean isFollower(int npcId) {
		return EnumDefinitions.getEnum(1279).getValues().containsKey((long) npcId);
	}

	public static String getRequirementsMessage(int id) {
		return EnumDefinitions.getEnum(1186).getStringValue(id);
	}

	public static void openInfusionInterface(Player player) {
		player.getInterfaceManager().sendInterface(POUCHES_INTERFACE);
		player.getPackets().sendPouchInfusionOptionsScript(POUCHES_INTERFACE, 16, 78, 8, 10, "Infuse<col=FF9040>", "Infuse-5<col=FF9040>", "Infuse-10<col=FF9040>", "Infuse-X<col=FF9040>", "Infuse-All<col=FF9040>", "List<col=FF9040>");
		player.getPackets().setIFEvents(new IFEvents(POUCHES_INTERFACE, 16, 0, 462).enableRightClickOptions(0,1,2,3,4,6));
		player.getTempAttribs().setB("infusing_scroll", false);
	}

	public static void openScrollInfusionInterface(Player player) {
		player.getInterfaceManager().sendInterface(SCROLLS_INTERFACE);
		player.getPackets().sendScrollInfusionOptionsScript(SCROLLS_INTERFACE, 16, 78, 8, 10, "Transform<col=FF9040>", "Transform-5<col=FF9040>", "Transform-10<col=FF9040>", "Transform-All<col=FF9040>", "Transform-X<col=FF9040>");
		player.getPackets().setIFEvents(new IFEvents(SCROLLS_INTERFACE, 16, 0, 462).enableRightClickOptions(0,1,2,3,4,5));
		player.getTempAttribs().setB("infusing_scroll", true);
	}

	public static int getPouchId(int grayId) {
		EnumDefinitions reals = EnumDefinitions.getEnum(1182);
		return reals.getIntValue((grayId-2) / 5 + 1);
	}
	
	public static ItemClickHandler handleSummonOps = new ItemClickHandler() {
		@Override
		public void handle(ItemClickEvent e) {
			Pouch pouches = Pouch.forId(e.getItem().getId());
			if (pouches != null) {
				if (e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING) >= pouches.getLevel())
					spawnFamiliar(e.getPlayer(), pouches);
				else
					e.getPlayer().sendMessage("You need a summoning level of " + pouches.getLevel() + " to summon this familiar.");
			}
		}
	};
	
	private static void spawnFamiliar(Player player, Pouch pouch) {
		if (player.getFamiliar() != null || player.getPet() != null) {
			player.sendMessage("You already have a follower.");
			return;
		}
		if (!player.getControllerManager().canSummonFamiliar())
			return;
		if (player.getSkills().getLevel(Constants.SUMMONING) < pouch.getSummoningCost()) {
			player.sendMessage("You do not have enought summoning points to spawn this.");
			return;
		}
		int levelReq = getRequiredLevel(pouch.getRealPouchId());
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < levelReq) {
			player.sendMessage("You need a summoning level of " + levelReq + " in order to use this pouch.");
			return;
		}
		WorldTile spawnTile = player.getNearestTeleTile(NPCDefinitions.getDefs(getNPCId(pouch.getRealPouchId()), player.getVars()).size);
		if (spawnTile == null) {
			player.sendMessage("Theres not enough space to summon your familiar here.");
			return;
		}
		final Familiar npc = createFamiliar(player, spawnTile, pouch);
		if (npc == null) {
			player.sendMessage("This familiar is not added yet.");
			return;
		}
		player.getInventory().deleteItem(pouch.getRealPouchId(), 1);
		player.getSkills().drainSummoning(pouch.getSummoningCost());
		player.setFamiliar(npc);
		npc.sendFollowerDetails();
	}

	public static Familiar createFamiliar(Player player, WorldTile tile, Pouch pouch) {
		try {
			if (pouch.clazz == null)
				return null;
			pouch
			return (Familiar) pouch.clazz.getConstructor(Player.class, Pouch.class, WorldTile.class, int.class, boolean.class).newInstance(player, pouch, tile, -1, true);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean hasPouch(Player player) {
		for (Pouch pouch : Pouch.values())
			if (player.getInventory().containsOneItem(pouch.getRealPouchId()))
				return true;
		return false;
	}

	public static ButtonClickHandler handlePouchButtons = new ButtonClickHandler(672) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 16) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), Integer.MAX_VALUE);
				else if (e.getPacket() == ClientPacket.IF_OP5) {
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 28);
					e.getPlayer().sendMessage("You currently need " + ItemDefinitions.getDefs(e.getSlotId2()).getCreateItemRequirements());
				}
			} else if (e.getComponentId() == 19 && e.getPacket() == ClientPacket.IF_OP1)
				openScrollInfusionInterface(e.getPlayer());
		}
	};

	public static ButtonClickHandler handleScrollButtons = new ButtonClickHandler(666) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 16) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					createScroll(e.getPlayer(), e.getSlotId2(), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					createScroll(e.getPlayer(), e.getSlotId2(), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					createScroll(e.getPlayer(), e.getSlotId2(), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					createScroll(e.getPlayer(), e.getSlotId2(), Integer.MAX_VALUE);
			} else if (e.getComponentId() == 18 && e.getPacket() == ClientPacket.IF_OP1)
				openInfusionInterface(e.getPlayer());
		}
	};

	public static void createScroll(Player player, int itemId, int amount) {
		Pouch pouch = Pouch.forId(itemId);
		if (pouch.scroll == null) {
			player.sendMessage("You do not have the pouch required to create this scroll.");
			return;
		}
		if (amount == 28 || amount > player.getInventory().getItems().getNumberOf(pouch.getRealPouchId()))
			amount = player.getInventory().getItems().getNumberOf(pouch.getRealPouchId());
		if (!player.getInventory().containsItem(pouch.getRealPouchId(), 1)) {
			player.sendMessage("You do not have enough " + ItemDefinitions.getDefs(pouch.getRealPouchId()).getName().toLowerCase() + "es to create " + amount + " " + ItemDefinitions.getDefs(pouch.scroll.id).getName().toLowerCase() + "s.");
			return;
		}
		if (player.getSkills().getLevel(Constants.SUMMONING) < pouch.getLevel()) {
			player.sendMessage("You need a summoning level of " + pouch.getLevel() + " to create " + amount + " " + ItemDefinitions.getDefs(pouch.scroll.id).getName().toLowerCase() + "s.");
			return;
		}
		player.getInventory().deleteItem(pouch.getRealPouchId(), amount);
		player.getInventory().addItem(pouch.scroll.id, amount * 10);
		player.getSkills().addXp(Constants.SUMMONING, pouch.scroll.xp);

		player.closeInterfaces();
		player.setNextAnimation(SCROLL_INFUSIN_ANIMATION);
	}


	public static void handlePouchInfusion(Player player, int pouchId, int creationCount) {
		Pouch pouch = Pouch.forId(pouchId);
		if (pouch == null)
			return;
		boolean infusingScroll = player.getTempAttribs().removeB("infusing_scroll"), hasRequirements = false;
		ItemDefinitions def = ItemDefinitions.getDefs(pouch.getRealPouchId());
		List<Item> itemReq = def.getCreateItemRequirements(infusingScroll);
		System.out.println(itemReq);
		int level = getRequiredLevel(pouch.getRealPouchId());
		if (itemReq != null)
			itemCount: for (int i = 0; i < creationCount; i++) {
				if (!player.getInventory().containsItems(itemReq)) {
					sendItemList(player, infusingScroll, creationCount, pouchId);
					break itemCount;
				}
				if (player.getSkills().getLevelForXp(Constants.SUMMONING) < level) {
					player.sendMessage("You need a summoning level of " + level + " to create this pouch.");
					break itemCount;
				}
				hasRequirements = true;
				player.getInventory().removeItems(itemReq);
				player.getInventory().addItem(new Item(infusingScroll ? getScrollId(pouch.getRealPouchId()) : pouch.getRealPouchId(), infusingScroll ? 10 : 1));
				player.getSkills().addXp(Constants.SUMMONING, infusingScroll ? pouch.getMinorExperience() : pouch.getExperience());
			}
		if (!hasRequirements) {
			player.getTempAttribs().setB("infusing_scroll", infusingScroll);
			return;
		}
		player.closeInterfaces();
		player.setNextAnimation(POUCH_INFUSION_ANIMATION);
		player.setNextSpotAnim(POUCH_INFUSION_GRAPHICS);
	}

	public static void switchInfusionOption(Player player) {
		if (player.getTempAttribs().getB("infusing_scroll"))
			openInfusionInterface(player);
		else
			openScrollInfusionInterface(player);
	}

	public static void sendItemList(Player player, boolean infusingScroll, int count, int pouchId) {
		Pouch pouch = Pouch.forId(pouchId);
		if (pouch == null)
			return;
		if (infusingScroll)
			player.sendMessage("This scroll requires 1 " + ItemDefinitions.getDefs(pouch.getRealPouchId()).name.toLowerCase() + ".");
		else
			player.sendMessage(getRequirementsMessage(pouch.getRealPouchId()));
	}
}
