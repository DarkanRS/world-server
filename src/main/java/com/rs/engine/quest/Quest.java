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
package com.rs.engine.quest;

import com.rs.engine.quest.data.QuestDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.util.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public enum Quest {
	COOKS_ASSISTANT(6, 1),
	DEMON_SLAYER(51, 2),
	DORICS_QUEST(99, 3),
	DRAGON_SLAYER(66, 4),
	ERNEST_CHICKEN(15, 5),
	GOBLIN_DIPLOMACY(137, 6),
	IMP_CATCHER(72, 7),
	KNIGHTS_SWORD(152, 8),
	PIRATES_TREASURE(60, 9),
	PRINCE_ALI_RESCUE(64, 10),
	RESTLESS_GHOST(27, 11),
	PRISONER_OF_GLOUPHRIE(186, 12),
	RUNE_MYSTERIES(55, 13),
	KING_OF_DWARVES(185, 14),
	SHIELD_OF_ARRAV(63, 15),
	VAMPYRE_SLAYER(132, 16),
	GUNNARS_GROUND(179, 17),
	ANIMAL_MAGNETISM(43, 18),
	BETWEEN_A_ROCK(57, 19),
	BIG_CHOMPY_BIRD_HUNTING(102, 20),
	BIOHAZARD(128, 21),
	CABIN_FEVER(0, 22),
	CLOCK_TOWER(44, 23),
	CONTACT(158, 24),
	ZOGRE_FLESH_EATERS(104, 25),
	CREATURE_OF_FENKENSTRAIN(92, 26),
	DARKNESS_OF_HALLOWVALE(26, 27),
	DEATH_TO_DORGESHUUN(13, 28),
	DEATH_PLATEAU(140, 29),
	DESERT_TREASURE(135, 30),
	DEVIOUS_MINDS(123, 31),
	DIG_SITE(69, 32),
	DRUIDIC_RITUAL(111, 33),
	DWARF_CANNON(34, 34),
	EADGARS_RUSE(75, 35),
	EAGLES_PEAK(149, 36),
	ELEMENTAL_WORKSHOP_I(8, 37),
	ELEMENTAL_WORKSHOP_II(94, 38),
	ENAKHRAS_LAMENT(97, 39),
	ENLIGHTENED_JOURNEY(107, 40),
	EYES_OF_GLOUPHRIE(143, 41),
	FAIRY_TALE_I_GROWING_PAINS(73, 42),
	FAIRY_TALE_II_CURE_A_QUEEN(154, 43),
	FAMILY_CREST(116, 44),
	THE_FEUD(136, 45),
	FIGHT_ARENA(106, 46),
	FISHING_CONTEST(76, 47),
	FORGETTABLE_TALE_OF_A_DRUNKEN_DWARF(98, 48),
	FREMENNIK_TRIALS(50, 49),
	WATERFALL_QUEST(93, 50),
	GARDEN_OF_TRANQUILITY(118, 51),
	GERTRUDES_CAT(138, 52),
	GHOSTS_AHOY(82, 53),
	GIANT_DWARF(134, 54),
	THE_GOLEM(3, 55),
	GRAND_TREE(65, 56),
	HAND_IN_SAND(150, 57),
	HAUNTED_MINE(120, 58),
	HAZEEL_CULT(125, 59),
	HEROES_QUEST(38, 60),
	HOLY_GRAIL(112, 61),
	HORROR_FROM_DEEP(105, 62),
	ICTHLARINS_LITTLE_HELPER(141, 63),
	IN_AID_OF_MYREQUE(21, 64),
	IN_SEARCH_OF_MYREQUE(19, 65),
	JUNGLE_POTION(59, 66),
	LEGENDS_QUEST(119, 67),
	LOST_CITY(42, 68),
	LOST_TRIBE(37, 69),
	LUNAR_DIPLOMACY(24, 70),
	MAKING_HISTORY(124, 71),
	MERLINS_CRYSTAL(108, 72),
	MONKEY_MADNESS(130, 73),
	MONKS_FRIEND(147, 74),
	MOUNTAIN_DAUGHTER(151, 75),
	MOURNINGS_ENDS_PART_I(156, 76),
	MOURNINGS_ENDS_PART_II(129, 77),
	MURDER_MYSTERY(28, 78),
	MY_ARMS_BIG_ADVENTURE(22, 79),
	NATURE_SPIRIT(133, 80),
	OBSERVATORY_QUEST(54, 81),
	ONE_SMALL_FAVOUR(146, 82),
	PLAGUE_CITY(58, 83),
	PRIEST_IN_PERIL(9, 84),
	RAG_AND_BONE_MAN(68, 85),
	RAT_CATCHERS(67, 86),
	RECIPE_FOR_DISASTER(30, 87),
	RECRUITMENT_DRIVE(62, 88),
	REGICIDE(100, 89),
	ROVING_ELVES(110, 90),
	ROYAL_TROUBLE(127, 91),
	A_RUM_DEAL(101, 92),
	SCORPION_CATCHER(2, 93),
	SEA_SLUG(121, 94),
	SLUG_MENACE(12, 95),
	SHADES_OF_MORTTON(10, 96),
	SHADOW_OF_STORM(71, 97),
	SHEEP_HERDER(39, 98),
	SHILO_VILLAGE(70, 99),
	A_SOULS_BANE(36, 100),
	SPIRITS_OF_ELID(46, 101),
	SWAN_SONG(48, 102),
	TAI_BWO_WANNAI_TRIO(89, 103),
	A_TAIL_OF_TWO_CATS(45, 104),
	TEARS_OF_GUTHIX(81, 105),
	TEMPLE_OF_IKOV(126, 106),
	THRONE_OF_MISCELLANIA(157, 107),
	TOURIST_TRAP(113, 108),
	WITCHS_HOUSE(7, 109),
	TREE_GNOME_VILLAGE(78, 110),
	TRIBAL_TOTEM(40, 111),
	TROLL_ROMANCE(90, 112),
	TROLL_STRONGHOLD(85, 113),
	UNDERGROUND_PASS(31, 114),
	WANTED(103, 115),
	WATCHTOWER(16, 116),
	COLD_WAR(145, 117),
	FREMENNIK_ISLES(148, 118),
	TOWER_OF_LIFE(1, 119),
	GREAT_BRAIN_ROBBERY(88, 120),
	WHAT_LIES_BELOW(144, 121),
	OLAFS_QUEST(153, 122),
	ANOTHER_SLICE_OF_HAM(155, 123),
	DREAM_MENTOR(79, 124),
	GRIM_TALES(32, 125),
	KINGS_RANSOM(61, 126),
	PATH_OF_GLOUPHRIE(5, 127),
	BACK_TO_MY_ROOTS(49, 128),
	LAND_OF_GOBLINS(91, 129),
	DEALING_WITH_SCABARAS(25, 130),
	WOLF_WHISTLE(52, 131),
	AS_A_FIRST_RESORT(41, 132),
	CATAPULT_CONSTRUCTION(23, 133),
	KENNITHS_CONCERNS(96, 134),
	LEGACY_OF_SEERGAZE(47, 135),
	PERILS_OF_ICE_MOUNTAIN(109, 136),
	TOKTZKETDILL(139, 137),
	SMOKING_KILLS(80, 138),
	ROCKING_OUT(4, 139),
	SPIRIT_OF_SUMMER(14, 140),
	MEETING_HISTORY(142, 141),
	ALL_FIRED_UP(29, 142),
	SUMMERS_END(95, 143),
	DEFENDER_OF_VARROCK(56, 144),
	WHILE_GUTHIX_SLEEPS(86, 145),
	IN_PYRE_NEED(87, 146),
	MYTHS_OF_WHITE_LANDS(74, 148),
	GLORIOUS_MEMORIES(115, 149),
	TALE_OF_MUSPAH(18, 150),
	HUNT_FOR_RED_RAKTUBER(122, 151),
	CHOSEN_COMMANDER(114, 152),
	SWEPT_AWAY(20, 153),
	FUR_N_SEEK(33, 154),
	MISSING_MY_MUMMY(77, 155),
	CURSE_OF_ARRAV(117, 156),
	TEMPLE_AT_SENNTISTEN(168, 157),
	FAIRY_TALE_III_BATTLE_AT_ORKS_RIFT(175, 158),
	BLACK_KNIGHTS_FORTRESS(53, 159),
	FORGIVENESS_OF_A_CHAOS_DWARF(35, 160),
	WITHIN_LIGHT(167, 161),
	NOMADS_REQUIEM(170, 162),
	BLOOD_RUNS_DEEP(169, 163),
	RUNE_MECHANICS(171, 165),
	BUYERS_AND_CELLARS(174, 167),
	LOVE_STORY(11, 168),
	BLOOD_PACT(172, 170),
	QUIET_BEFORE_SWARM(177, 171),
	ELEMENTAL_WORKSHOP_III(176, 172),
	A_VOID_DANCE(83, 173),
	VOID_STARES_BACK(180, 174),
	RITUAL_OF_MAHJARRAT(194, 176),
	CARNILLEAN_RISING(203, 178),
	DO_NO_EVIL(183, 179),
	ELEMENTAL_WORKSHOP_IV(187, 180),
	A_CLOCKWORK_SYRINGE(188, 181),
	DEADLIEST_CATCH(191, 182),
	SALT_IN_WOUND(192, 183),
	BRANCHES_OF_DARKMEYER(193, 184),
	ONE_PIERCING_NOTE(196, 187),
	LET_THEM_EAT_PIE(200, 188),
	ELDER_KILN(201, 190),
	FIREMAKERS_CURSE(199, 191),
	SONG_FROM_DEPTHS(202, 192)
	;

	private static HashMap<Integer, Quest> QUESTS_BY_ID = new HashMap<>();
	private static HashMap<Integer, Quest> QUESTS_BY_SLOTID = new HashMap<>();

	static {
		for (Quest quest : Quest.values()) {
			QUESTS_BY_ID.put(quest.id, quest);
			QUESTS_BY_SLOTID.put(quest.slotId, quest);
		}
		initializeHandlers();
	}

	public static void initializeHandlers() {
		try {
			List<Class<?>> classes = Utils.getClassesWithAnnotation("com.rs", QuestHandler.class);
			for (Class<?> clazz : classes) {
				QuestHandler handler = clazz.getAnnotation(QuestHandler.class);
				if (handler == null || clazz.getSuperclass() != QuestOutline.class)
					continue;
				handler.value().handler = (QuestOutline) clazz.getConstructor().newInstance();
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static Quest forId(int id) {
		return QUESTS_BY_ID.get(id);
	}

	public static Quest forSlot(int id) {
		return QUESTS_BY_SLOTID.get(id);
	}

	private int id, slotId;
	private QuestOutline handler;

	private Quest(int id, int slotId) {
		this(id, slotId, null);
	}

	private Quest(int id, int slotId, QuestOutline handler) {
		this.id = id;
		this.slotId = slotId;
		this.handler = handler;
	}

	public int getId() {
		return id;
	}

	public boolean isImplemented() {
		return handler != null;
	}

	public QuestOutline getHandler() {
		return handler;
	}

	public boolean meetsReqs(Player player) {
		return meetsReqs(player, null);
	}

	public boolean meetsReqs(Player player, String actionStr) {
		boolean meetsRequirements = true;
		for (Quest quest : getDefs().getExtraInfo().getPreReqs()) {
			if (!player.isQuestComplete(quest)) {
				if (actionStr != null && quest.isImplemented())
					player.sendMessage("You must have completed " + quest.getDefs().name + ".");
				meetsRequirements = false;
			}
		}
		for (int skillId : getDefs().getExtraInfo().getPreReqSkillReqs().keySet()) {
			if (player.getSkills().getLevelForXp(skillId) < getDefs().getExtraInfo().getPreReqSkillReqs().get(skillId)) {
				if (actionStr != null)
					player.sendMessage("You need a " + Skills.SKILL_NAME[skillId] + " level of " + getDefs().getExtraInfo().getPreReqSkillReqs().get(skillId)+".");
				meetsRequirements = false;
			}
		}
		if (!meetsRequirements && actionStr != null)
			player.sendMessage("You must meet the requirements for " + getDefs().name + " " + actionStr);
		return meetsRequirements;
	}

	public String getQuestPointRewardLine() {
		int qp = getDefs().questpointReward;
		return "<br>"+qp+" Quest point"+(qp > 1 ? "s" : "")+"<br>";
	}

	public void sendQuestCompleteInterface(Player player, int itemId, String... lines) {
		String line = "" + getQuestPointRewardLine();
		for (String l : lines)
			line += l + "<br>";

		//random quest jingle
		int jingleNum = Utils.random(0, 4);
		if(jingleNum == 3)
			jingleNum = 318;
		else
			jingleNum+=152;
		player.jingle(jingleNum);

		player.getInterfaceManager().sendInterface(1244);
		player.getPackets().setIFItem(1244, 24, itemId, 1);
		player.getPackets().setIFText(1244, 25, "You have completed "+getDefs().name+"!");
		player.getPackets().setIFText(1244, 26, line);
	}

	public QuestDefinitions getDefs() {
		return QuestDefinitions.getQuestDefinitions(id);
	}
}