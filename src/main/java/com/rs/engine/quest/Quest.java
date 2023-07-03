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

import com.rs.Settings;
import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.StructDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.engine.quest.data.QuestDefinitions;
import com.rs.engine.quest.data.QuestInformation;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return qp+" Quest point"+(qp > 1 ? "s" : "")+"<br>";
	}

	public int getStructId() {
		return EnumDefinitions.getEnum(2252).getIntValue(slotId);
	}

	public StructDefinitions getStruct() {
		return StructDefinitions.getStruct(getStructId());
	}

	public void openQuestInfo(Player player, boolean promptStart) {
		player.getPackets().sendVarc(699, getStructId());
		player.getInterfaceManager().sendInterface(1243);
		if (promptStart) {
			player.getPackets().setIFHidden(1243, 45, false);
			player.getPackets().setIFHidden(1243, 57, true);
			player.getPackets().setIFHidden(1243, 56, true);
			player.getPackets().setIFEvents(new IFEvents(1243, 46, -1, -1).enableContinueButton());
			player.getPackets().setIFEvents(new IFEvents(1243, 51, -1, -1).enableContinueButton());
		} else
			player.getPackets().setIFHidden(1243, 58, false);
		String reqStr = getRequirementsString(player);
		player.getPackets().sendVarcString(359, reqStr);
		if (!isImplemented() || !getDefs().getExtraInfo().usesNewInterface()) {
			WorldTasks.schedule(0, () -> {
				int questState = player.isQuestStarted(this) ? 1 : 0;
				int height = 0;

				String startPointDesc = getStartLocationDescription(player);
				player.getPackets().sendRunScript(4249, 81461265, 81461266, 81461267, -1, -1, questState, height, "<col=ebe076>" + "Start point:" + "</col>", startPointDesc);
				height += Utils.getIFStringHeightAbs(startPointDesc, 495, 374);

				player.getPackets().sendRunScript(4249, 81461268, 81461269, 81461270, -1, -1, questState, height, "<col=ebe076>" + "Requirements:" + "</col>", reqStr);
				height += Utils.getIFStringHeightAbs(reqStr, 495, 357);

				String requiredItemsStr = getRequiredItemsString();
				player.getPackets().sendRunScript(4249, 81461271, 81461272, 81461273, 81461274, 81461279, questState, height, "<col=ebe076>" + "Required items:" + "</col>", requiredItemsStr);
				height += Utils.getIFStringHeightAbs(requiredItemsStr, 495, 351);

				String combatStuffStr = getCombatInformationString();
				player.getPackets().sendRunScript(4249, 81461280, 81461281, 81461282, -1, -1, questState, height, "<col=ebe076>" + "Combat:" + "</col>", combatStuffStr);
				height += Utils.getIFStringHeightAbs(combatStuffStr, 495, 393);

				String rewardsStr = getRewardsString();
				player.getPackets().sendRunScript(4249, 81461283, 81461284, 81461285, 81461286, 81461291, questState, height, "<col=ebe076>" + "Rewards:" + "</col>", rewardsStr);
				height += Utils.getIFStringHeightAbs(rewardsStr, 495, 388);

				player.getPackets().sendRunScript(5510, -1, 81461264, height, 0);
				player.getPackets().sendRunScript(31, 81461292, 81461264, 5666, 5663, 5664, 5665, 5686, 5685);
			});
		} else
			player.getPackets().sendVarcString(359, reqStr);
	}

	private String getStartLocationDescription(Player player) {
		QuestOutline handler = getHandler();
		if (handler == null)
			return "This quest is not yet implemented. Unimplemented quests will automatically complete upon all requirements being met for them.<br><br>" +
					"You currently have " + (player.isQuestComplete(this) ? "<col=00FF00>COMPLETED</col>" : "<col=FF0000>NOT COMPLETED</col>") + " this quest.<br> ";
		return getHandler().getStartLocationDescription() + (Settings.getConfig().isDebug() ? ("<br>[" + getDefs().getExtraInfo().getStartLocation().getX() + ", " + getDefs().getExtraInfo().getStartLocation().getY() + ", " + getDefs().getExtraInfo().getStartLocation().getPlane() + "]") : "");
	}

	private String getRequiredItemsString() {
		QuestOutline handler = getHandler();
		if (handler == null)
			return "None.";
		return getHandler().getRequiredItemsString();
	}

	private String getCombatInformationString() {
		QuestOutline handler = getHandler();
		if (handler == null)
			return "None.";
		return getHandler().getCombatInformationString();
	}

	private String getRewardsString() {
		QuestOutline handler = getHandler();
		if (handler == null)
			return getQuestPointRewardLine().replace("<br>", "");
		return getQuestPointRewardLine()+getHandler().getRewardsString();
	}

	public String getRequirementsString(Player player) {
		StringBuilder lines = new StringBuilder();
		QuestInformation info = getDefs().getExtraInfo();

		if (info.getPreReqs().size() > 0) {
			for (Quest preReq : info.getPreReqs())
				lines.append(Utils.strikeThroughIf(preReq.getDefs().getExtraInfo().getName(), () -> player.isQuestComplete(preReq)) + "<br>");
		}

		if (info.getSkillReq().size() > 0) {
			for (int skillId : info.getSkillReq().keySet()) {
				if (info.getSkillReq().get(skillId) == 0)
					continue;
				lines.append(Utils.strikeThroughIf(info.getSkillReq().get(skillId) + " " + Skills.SKILL_NAME[skillId], () -> player.getSkills().getLevelForXp(skillId) >= info.getSkillReq().get(skillId)) + "<br>");
			}
		}

		if (info.getQpReq() > 0)
			lines.append(info.getQpReq() + " quest points<br>");
		return lines.toString().isEmpty() ? "None." : lines.toString();
	}

	public QuestDefinitions getDefs() {
		return QuestDefinitions.getQuestDefinitions(id);
	}
}