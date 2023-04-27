package com.rs.game.content.skills.summoning;

import com.rs.lib.game.Item;

import java.util.HashMap;
import java.util.Map;

public enum PouchMaterialList {
	SPIRIT_TERRORBIRD_POUCH(12007, new Item[] { new Item(12183, 12), new Item(12158, 1), new Item(12155, 1), new Item(9978, 1) }),
	GRANITE_CRAB_POUCH(12009, new Item[] { new Item(12183, 7), new Item(12158, 1), new Item(12155, 1), new Item(440, 1) }),
	PRAYING_MANTIS_POUCH(12011, new Item[] { new Item(12183, 168), new Item(12160, 1), new Item(12155, 1), new Item(2466, 1) }),
	GIANT_ENT_POUCH(12013, new Item[] { new Item(12183, 124), new Item(12159, 1), new Item(12155, 1), new Item(5933, 1) }),
	SPIRIT_COBRA_POUCH(12015, new Item[] { new Item(12183, 116), new Item(12160, 1), new Item(12155, 1), new Item(0, 1) }),
	SPIRIT_DAGANNOTH_POUCH(12017, new Item[] { new Item(12183, 1), new Item(12160, 1), new Item(12155, 1), new Item(6155, 1) }),
	THORNY_SNAIL_POUCH(12019, new Item[] { new Item(12183, 9), new Item(12158, 1), new Item(12155, 1), new Item(3363, 1) }),
	BEAVER_POUCH(12021, new Item[] { new Item(12183, 72), new Item(12159, 1), new Item(12155, 1), new Item(1519, 1) }),
	KARAM_OVERLORD_POUCH(12023, new Item[] { new Item(12183, 144), new Item(12163, 1), new Item(12155, 1), new Item(6667, 1) }),
	HYDRA_POUCH(12025, new Item[] { new Item(12183, 128), new Item(12159, 1), new Item(12155, 1), new Item(571, 1) }),
	SPIRIT_JELLY_POUCH(12027, new Item[] { new Item(12183, 151), new Item(12163, 1), new Item(12155, 1), new Item(1937, 1) }),
	BUNYIP_POUCH(12029, new Item[] { new Item(12183, 110), new Item(12159, 1), new Item(12155, 1), new Item(383, 1) }),
	WAR_TORTOISE_POUCH(12031, new Item[] { new Item(12183, 1), new Item(12158, 1), new Item(12155, 1), new Item(7939, 1) }),
	FRUIT_BAT_POUCH(12033, new Item[] { new Item(12183, 130), new Item(12159, 1), new Item(12155, 1), new Item(1963, 1) }),
	ABYSSAL_PARASITE_POUCH(12035, new Item[] { new Item(12183, 106), new Item(12159, 1), new Item(12155, 1), new Item(12161, 1) }),
	ABYSSAL_LURKER_POUCH(12037, new Item[] { new Item(12183, 119), new Item(12159, 1), new Item(12155, 1), new Item(12161, 1) }),
	UNICORN_STALLION_POUCH(12039, new Item[] { new Item(12183, 140), new Item(12159, 1), new Item(12155, 1), new Item(237, 1) }),
	MAGPIE_POUCH(12041, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(1635, 1) }),
	DREADFOWL_POUCH(12043, new Item[] { new Item(12183, 8), new Item(12158, 1), new Item(12155, 1), new Item(2138, 1) }),
	STRANGER_PLANT_POUCH(12045, new Item[] { new Item(12183, 128), new Item(12160, 1), new Item(12155, 1), new Item(8431, 1) }),
	SPIRIT_WOLF_POUCH(12047, new Item[] { new Item(12183, 7), new Item(12158, 1), new Item(12155, 1), new Item(2859, 1) }),
	DESERT_WYRM_POUCH(12049, new Item[] { new Item(12183, 45), new Item(12159, 1), new Item(12155, 1), new Item(1783, 1) }),
	EVIL_TURNIP_POUCH(12051, new Item[] { new Item(12183, 104), new Item(12160, 1), new Item(12155, 1), new Item(12153, 1) }),
	VAMPYRE_BAT_POUCH(12053, new Item[] { new Item(12183, 81), new Item(12160, 1), new Item(12155, 1), new Item(3325, 1) }),
	SPIRIT_SCORPION_POUCH(12055, new Item[] { new Item(12183, 57), new Item(12160, 1), new Item(12155, 1), new Item(3095, 1) }),
	ARCTIC_BEAR_POUCH(12057, new Item[] { new Item(12183, 14), new Item(12158, 1), new Item(12155, 1), new Item(10117, 1) }),
	SPIRIT_SPIDER_POUCH(12059, new Item[] { new Item(12183, 8), new Item(12158, 1), new Item(12155, 1), new Item(6291, 1) }),
	BLOATED_LEECH_POUCH(12061, new Item[] { new Item(12183, 117), new Item(12160, 1), new Item(12155, 1), new Item(2132, 1) }),
	SPIRIT_KALPHITE_POUCH(12063, new Item[] { new Item(12183, 51), new Item(12163, 1), new Item(12155, 1), new Item(3138, 1) }),
	HONEY_BADGER_POUCH(12065, new Item[] { new Item(12183, 84), new Item(12160, 1), new Item(12155, 1), new Item(12156, 1) }),
	ALBINO_RAT_POUCH(12067, new Item[] { new Item(12183, 75), new Item(12163, 1), new Item(12155, 1), new Item(2134, 1) }),
	GRANITE_LOBSTER_POUCH(12069, new Item[] { new Item(12183, 166), new Item(12160, 1), new Item(12155, 1), new Item(6979, 1) }),
	MACAW_POUCH(12071, new Item[] { new Item(12183, 78), new Item(12159, 1), new Item(12155, 1), new Item(249, 1) }),
	BRONZE_MINOTAUR_POUCH(12073, new Item[] { new Item(12183, 102), new Item(12163, 1), new Item(12155, 1), new Item(2349, 1) }),
	IRON_MINOTAUR_POUCH(12075, new Item[] { new Item(12183, 125), new Item(12163, 1), new Item(12155, 1), new Item(2351, 1) }),
	STEEL_MINOTAUR_POUCH(12077, new Item[] { new Item(12183, 141), new Item(12163, 1), new Item(12155, 1), new Item(2353, 1) }),
	MITHRIL_MINOTAUR_POUCH(12079, new Item[] { new Item(12183, 152), new Item(12163, 1), new Item(12155, 1), new Item(2359, 1) }),
	ADAMANT_MINOTAUR_POUCH(12081, new Item[] { new Item(12183, 144), new Item(12163, 1), new Item(12155, 1), new Item(2361, 1) }),
	RUNE_MINOTAUR_POUCH(12083, new Item[] { new Item(12183, 1), new Item(12163, 1), new Item(12155, 1), new Item(2363, 1) }),
	SMOKE_DEVIL_POUCH(12085, new Item[] { new Item(12183, 141), new Item(12160, 1), new Item(12155, 1), new Item(9736, 1) }),
	BULL_ANT_POUCH(12087, new Item[] { new Item(12183, 11), new Item(12158, 1), new Item(12155, 1), new Item(6010, 1) }),
	WOLPERTINGER_POUCH(12089, new Item[] { new Item(12183, 203), new Item(12160, 1), new Item(12155, 1), new Item(2859, 1), new Item(3226, 1) }),
	COMPOST_MOUND_POUCH(12091, new Item[] { new Item(12183, 47), new Item(12159, 1), new Item(12155, 1), new Item(6032, 1) }),
	PACK_YAK_POUCH(12093, new Item[] { new Item(12183, 211), new Item(12160, 1), new Item(12155, 1), new Item(10818, 1) }),
	SP_COCKATRICE_POUCH(12095, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12109, 1) }),
	SP_GUTHATRICE_POUCH(12097, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12111, 1) }),
	SP_SARATRICE_POUCH(12099, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12113, 1) }),
	SP_ZAMATRICE_POUCH(12101, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12115, 1) }),
	SP_PENGATRICE_POUCH(12103, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12117, 1) }),
	SP_CORAXATRICE_POUCH(12105, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12119, 1) }),
	SP_VULATRICE_POUCH(12107, new Item[] { new Item(12183, 88), new Item(12159, 1), new Item(12155, 1), new Item(12121, 1) }),
	BARKER_TOAD_POUCH(12123, new Item[] { new Item(12183, 11), new Item(12158, 1), new Item(12155, 1), new Item(2150, 1) }),
	IBIS_POUCH(12531, new Item[] { new Item(12183, 109), new Item(12159, 1), new Item(12155, 1), new Item(311, 1) }),
	SWAMP_TITAN_POUCH(12776, new Item[] { new Item(12183, 150), new Item(12160, 1), new Item(12155, 1), new Item(10149, 1) }),
	SPIRIT_MOSQUITO_POUCH(12778, new Item[] { new Item(12183, 1), new Item(12158, 1), new Item(12155, 1), new Item(6319, 1) }),
	VOID_SPINNER_POUCH(12780, new Item[] { new Item(12183, 74), new Item(12163, 1), new Item(12155, 1), new Item(12166, 1) }),
	FORGE_REGENT_POUCH(12782, new Item[] { new Item(12183, 141), new Item(12159, 1), new Item(12155, 1), new Item(10020, 1) }),
	SPIRIT_LARUPIA_POUCH(12784, new Item[] { new Item(12183, 155), new Item(12163, 1), new Item(12155, 1), new Item(10095, 1) }),
	GEYSER_TITAN_POUCH(12786, new Item[] { new Item(12183, 222), new Item(12163, 1), new Item(12155, 1), new Item(1444, 1) }),
	LAVA_TITAN_POUCH(12788, new Item[] { new Item(12183, 219), new Item(12163, 1), new Item(12155, 1), new Item(12168, 1) }),
	STEEL_TITAN_POUCH(12790, new Item[] { new Item(12183, 178), new Item(12160, 1), new Item(12155, 1), new Item(1119, 1) }),
	OBSIDIAN_GOLEM_POUCH(12792, new Item[] { new Item(12183, 195), new Item(12163, 1), new Item(12155, 1), new Item(12168, 1) }),
	TALON_BEAST_POUCH(12794, new Item[] { new Item(12183, 174), new Item(12160, 1), new Item(12155, 1), new Item(12162, 1) }),
	ABYSSAL_TITAN_POUCH(12796, new Item[] { new Item(12183, 113), new Item(12159, 1), new Item(12155, 1), new Item(12161, 1) }),
	VOID_TORCHER_POUCH(12798, new Item[] { new Item(12183, 74), new Item(12163, 1), new Item(12155, 1), new Item(12167, 1) }),
	GIANT_CHINCHOMPA_POUCH(12800, new Item[] { new Item(12183, 84), new Item(12163, 1), new Item(12155, 1), new Item(10033, 1) }),
	FIRE_TITAN_POUCH(12802, new Item[] { new Item(12183, 198), new Item(12163, 1), new Item(12155, 1), new Item(1442, 1) }),
	MOSS_TITAN_POUCH(12804, new Item[] { new Item(12183, 202), new Item(12163, 1), new Item(12155, 1), new Item(1440, 1) }),
	ICE_TITAN_POUCH(12806, new Item[] { new Item(12183, 198), new Item(12163, 1), new Item(12155, 1), new Item(1438, 1), new Item(1444, 1) }),
	SPIRIT_TZ_KIH_POUCH(12808, new Item[] { new Item(12183, 64), new Item(12160, 1), new Item(12155, 1), new Item(12168, 1) }),
	SPIRIT_GRAAHK_POUCH(12810, new Item[] { new Item(12183, 154), new Item(12163, 1), new Item(12155, 1), new Item(10099, 1) }),
	SPIRIT_KYATT_POUCH(12812, new Item[] { new Item(12183, 153), new Item(12163, 1), new Item(12155, 1), new Item(10103, 1) }),
	VOID_SHIFTER_POUCH(12814, new Item[] { new Item(12183, 74), new Item(12163, 1), new Item(12155, 1), new Item(12165, 1) }),
	PYRELORD_POUCH(12816, new Item[] { new Item(12183, 111), new Item(12160, 1), new Item(12155, 1), new Item(590, 1) }),
	VOID_RAVAGER_POUCH(12818, new Item[] { new Item(12183, 74), new Item(12159, 1), new Item(12155, 1), new Item(12164, 1) }),
	RAVENOUS_LOCUST_POUCH(12820, new Item[] { new Item(12183, 79), new Item(12160, 1), new Item(12155, 1), new Item(1933, 1) }),
	IRON_TITAN_POUCH(12822, new Item[] { new Item(12183, 198), new Item(12160, 1), new Item(12155, 1), new Item(1115, 1) }),
	PHOENIX_POUCH(14623, new Item[] { new Item(12183, 165), new Item(12160, 1), new Item(12155, 1), new Item(14616, 1) }),
	CUB_BLOODRAGER_POUCH(17935, new Item[] { new Item(18017, 1), new Item(17630, 1) }),
	LITTLE_BLOODRAGER_POUCH(17936, new Item[] { new Item(18017, 1), new Item(17632, 1) }),
	NAIVE_BLOODRAGER_POUCH(17937, new Item[] { new Item(18017, 1), new Item(17634, 1) }),
	KEEN_BLOODRAGER_POUCH(17938, new Item[] { new Item(18018, 1), new Item(17636, 1) }),
	BRAVE_BLOODRAGER_POUCH(17939, new Item[] { new Item(18018, 1), new Item(17638, 1) }),
	BRAH_BLOODRAGER_POUCH(17940, new Item[] { new Item(18018, 1), new Item(17640, 1) }),
	NAABE_BLOODRAGER_POUCH(17941, new Item[] { new Item(18019, 1), new Item(17642, 1) }),
	WISE_BLOODRAGER_POUCH(17942, new Item[] { new Item(18019, 1), new Item(17644, 1) }),
	ADEPT_BLOODRAGER_POUCH(17943, new Item[] { new Item(18020, 1), new Item(17646, 1) }),
	SACHEM_BLOODRAGER_POUCH(17944, new Item[] { new Item(18020, 1), new Item(17648, 1) }),
	CUB_STORMBRINGER_POUCH(17945, new Item[] { new Item(18017, 1), new Item(17448, 1) }),
	LITTLE_STORMBRINGER_POUCH(17946, new Item[] { new Item(18017, 1), new Item(17450, 1) }),
	NAIVE_STORMBRINGER_POUCH(17947, new Item[] { new Item(18017, 1), new Item(17452, 1) }),
	KEEN_STORMBRINGER_POUCH(17948, new Item[] { new Item(18018, 1), new Item(17454, 1) }),
	BRAVE_STORMBRINGER_POUCH(17949, new Item[] { new Item(18018, 1), new Item(17456, 1) }),
	BRAH_STORMBRINGER_POUCH(17950, new Item[] { new Item(18018, 1), new Item(17458, 1) }),
	NAABE_STORMBRINGER_POUCH(17951, new Item[] { new Item(18019, 1), new Item(17460, 1) }),
	WISE_STORMBRINGER_POUCH(17952, new Item[] { new Item(18019, 1), new Item(17462, 1) }),
	ADEPT_STORMBRINGER_POUCH(17953, new Item[] { new Item(18020, 1), new Item(17464, 1) }),
	SACHEM_STORMBRINGER_POUCH(17954, new Item[] { new Item(18020, 1), new Item(17466, 1) }),
	CUB_HOARDSTALKER_POUCH(17955, new Item[] { new Item(18017, 1), new Item(17424, 1) }),
	LITTLE_HOARDSTALKER_POUCH(17956, new Item[] { new Item(18017, 1), new Item(17426, 1) }),
	NAIVE_HOARDSTALKER_POUCH(17957, new Item[] { new Item(18017, 1), new Item(17428, 1) }),
	KEEN_HOARDSTALKER_POUCH(17958, new Item[] { new Item(18018, 1), new Item(17430, 1) }),
	BRAVE_HOARDSTALKER_POUCH(17959, new Item[] { new Item(18018, 1), new Item(17432, 1) }),
	BRAH_HOARDSTALKER_POUCH(17960, new Item[] { new Item(18018, 1), new Item(17434, 1) }),
	NAABE_HOARDSTALKER_POUCH(17961, new Item[] { new Item(18019, 1), new Item(17436, 1) }),
	WISE_HOARDSTALKER_POUCH(17962, new Item[] { new Item(18019, 1), new Item(17438, 1) }),
	ADEPT_HOARDSTALKER_POUCH(17963, new Item[] { new Item(18020, 1), new Item(17440, 1) }),
	SACHEM_HOARDSTALKER_POUCH(17964, new Item[] { new Item(18020, 1), new Item(17442, 1) }),
	CUB_SKINWEAVER_POUCH(17965, new Item[] { new Item(18017, 1), new Item(18159, 2) }),
	LITTLE_SKINWEAVER_POUCH(17966, new Item[] { new Item(18017, 1), new Item(18161, 2) }),
	NAIVE_SKINWEAVER_POUCH(17967, new Item[] { new Item(18017, 1), new Item(18163, 2) }),
	KEEN_SKINWEAVER_POUCH(17968, new Item[] { new Item(18018, 1), new Item(18165, 2) }),
	BRAVE_SKINWEAVER_POUCH(17969, new Item[] { new Item(18018, 1), new Item(18167, 2) }),
	BRAH_SKINWEAVER_POUCH(17970, new Item[] { new Item(18018, 1), new Item(18169, 2) }),
	NAABE_SKINWEAVER_POUCH(17971, new Item[] { new Item(18019, 1), new Item(18171, 2) }),
	WISE_SKINWEAVER_POUCH(17972, new Item[] { new Item(18019, 1), new Item(18173, 2) }),
	ADEPT_SKINWEAVER_POUCH(17973, new Item[] { new Item(18020, 1), new Item(18175, 2) }),
	SACHEM_SKINWEAVER_POUCH(17974, new Item[] { new Item(18020, 1), new Item(18177, 2) }),
	CUB_WORLDBEARER_POUCH(17975, new Item[] { new Item(18017, 1), new Item(17995, 1) }),
	LITTLE_WORLDBEARER_POUCH(17976, new Item[] { new Item(18017, 1), new Item(17997, 1) }),
	NAIVE_WORLDBEARER_POUCH(17977, new Item[] { new Item(18017, 1), new Item(17999, 1) }),
	KEEN_WORLDBEARER_POUCH(17978, new Item[] { new Item(18018, 1), new Item(18001, 1) }),
	BRAVE_WORLDBEARER_POUCH(17979, new Item[] { new Item(18018, 1), new Item(18003, 1) }),
	BRAH_WORLDBEARER_POUCH(17980, new Item[] { new Item(18018, 1), new Item(18005, 1) }),
	NAABE_WORLDBEARER_POUCH(17981, new Item[] { new Item(18019, 1), new Item(18007, 1) }),
	WISE_WORLDBEARER_POUCH(17982, new Item[] { new Item(18019, 1), new Item(18009, 1) }),
	ADEPT_WORLDBEARER_POUCH(17983, new Item[] { new Item(18020, 1), new Item(18011, 1) }),
	SACHEM_WORLDBEARER_POUCH(17984, new Item[] { new Item(18020, 1), new Item(18013, 1) }),
	CUB_DEATHSLINGER_POUCH(17985, new Item[] { new Item(18017, 1), new Item(17682, 2) }),
	LITTLE_DEATHSLINGER_POUCH(17986, new Item[] { new Item(18017, 1), new Item(17684, 2) }),
	NAIVE_DEATHSLINGER_POUCH(17987, new Item[] { new Item(18017, 1), new Item(17686, 2) }),
	KEEN_DEATHSLINGER_POUCH(17988, new Item[] { new Item(18018, 1), new Item(17688, 2) }),
	BRAVE_DEATHSLINGER_POUCH(17989, new Item[] { new Item(18018, 1), new Item(17690, 2) }),
	BRAH_DEATHSLINGER_POUCH(17990, new Item[] { new Item(18018, 1), new Item(17692, 2) }),
	NAABE_DEATHSLINGER_POUCH(17991, new Item[] { new Item(18019, 1), new Item(17694, 2) }),
	WISE_DEATHSLINGER_POUCH(17992, new Item[] { new Item(18019, 1), new Item(17696, 2) }),
	ADEPT_DEATHSLINGER_POUCH(17993, new Item[] { new Item(18020, 1), new Item(17698, 2) }),
	SACHEM_DEATHSLINGER_POUCH(17994, new Item[] { new Item(18020, 1), new Item(17700, 2) });
	
	private static Map<Integer, PouchMaterialList> MAP = new HashMap<>();
	
	static {
		for (PouchMaterialList l : PouchMaterialList.values())
			MAP.put(l.pouchId, l);
	}
	
	public static PouchMaterialList forId(int pouchId) {
		return MAP.get(pouchId);
	}
	
	private int pouchId;
	private Item[] materials;
	
	private PouchMaterialList(int pouchId, Item[] materials) {
		this.pouchId = pouchId;
		this.materials = materials;
	}

	public Item[] get() {
		return materials;
	}

	public int getPouchId() {
		return pouchId;
	}

}
