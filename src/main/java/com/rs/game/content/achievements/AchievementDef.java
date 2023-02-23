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
package com.rs.game.content.achievements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.StructDefinitions;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.data.QuestDefinitions;
import com.rs.engine.quest.data.QuestInformationParser;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

@PluginEventHandler
public class AchievementDef {

	private static Map<Integer, AchievementDef> CACHE = new HashMap<>();
	private static Map<Area, Map<Difficulty, Set<AchievementDef>>> TASK_SETS = new HashMap<>();
	private static Map<Area, Map<Difficulty, Map<Integer, Integer>>> TASK_SET_LEVEL_REQS = new HashMap<>();

	public enum Area {
		//0=3000, 1=346, 2=426, 3=115, 4=221, 5=0, 6=182, 7=152, 8=531, 9=944, 10=995, 11=4091, 12=4091, 13=4091, 14=4091, 15=4091, 16=4091, 17=4091, 18=4091, 19=4091, 20=4091, 60=3522, 62=4091, 63=4090
		MINIGAMES(0),
		LUMBRIDGE(1),
		VARROCK(2),
		FALADOR(3),
		SEERS(4),
		ARDOUGNE(5),
		KARAMJA(6),
		FREMENNIK(7),
		BURTHORPE(8),
		MORYTANIA(9),
		LODESTONES(10),
		DESERT(11),
		ISLANDS(12),
		FELDIP(13),
		TIRANNWN(14),
		GNOME(15),
		WILDERNESS(16),
		KELDAGRIM(17),
		DORGESH_KAAN(18),
		OUTER_PLANES(19),
		PISCATORIS(20),
		SIR_VANT_TUTORIAL(60),
		UNNAMED1(61),
		DAMONHEIM(62),
		ELSEWHERE(63);

		private int id;

		Area(int id) {
			this.id = id;
		}

		public static Area forId(int id) {
			for (Area a : Area.values())
				if (a.id == id)
					return a;
			return null;
		}
	}

	public enum Difficulty {
		NONE, BEGINNER, EASY, MEDIUM, HARD, ELITE;
	}

	private int id;
	private int next;
	private String name;
	private Difficulty difficulty;
	private Area area;
	private Area taskSet;
	private Map<Integer, Integer> skillReqs;
	private Set<Quest> quests;

	private static final int TASK_SET = 1293;
	private static final int AREA = 1267;
	private static final int DIFFICULTY = 1272;
	private static final int NAME = 1266;
	private static final int ID = 1268;
	private static final int NEXT_ID = 1269;
	private static final int REQUIREMENTS_START = 1294;
	private static final int REQUIREMENTS_END = 1312;

	public AchievementDef(int id, StructDefinitions struct) {
		this.id = struct.getIntValue(ID);
		next = struct.getIntValue(NEXT_ID);
		name = struct.getStringValue(NAME);
		difficulty = Difficulty.values()[struct.getIntValue(DIFFICULTY)];
		area = Area.forId(struct.getIntValue(AREA));
		taskSet = Area.forId(struct.getIntValue(TASK_SET, 63));
		skillReqs = new HashMap<>();
		quests = new HashSet<>();
		for (int i = REQUIREMENTS_START;i <= REQUIREMENTS_END;i += 2)
			if (struct.getIntValue(i, -1) != -1 && struct.getIntValue(i+1, -1) != -1)
				if (struct.getIntValue(i, -1) >= 1 && struct.getIntValue(i, -1) <= 25)
					addSkillReq(toRealSkillId(struct.getIntValue(i, -1)), struct.getIntValue(i+1, -1));
				else if (struct.getIntValue(i, -1) == 61) {
					quests.add(Quest.forSlot(struct.getIntValue(i+1, -1)));
					for (int skillId : Quest.forSlot(struct.getIntValue(i+1, -1)).getDefs().getExtraInfo().getPreReqSkillReqs().keySet())
						addSkillReq(skillId, Quest.forSlot(struct.getIntValue(i+1, -1)).getDefs().getExtraInfo().getPreReqSkillReqs().get(skillId));
				}
	}

	public static boolean meetsRequirements(Player player, Area area, Difficulty difficulty) {
		return meetsRequirements(player, area, difficulty, true);
	}

	public static boolean meetsRequirements(Player player, Area area, Difficulty difficulty, boolean print) {
		boolean meetsRequirements = true;
		for (int skillId : TASK_SET_LEVEL_REQS.get(area).get(difficulty).keySet())
			if (player.getSkills().getLevelForXp(skillId) < TASK_SET_LEVEL_REQS.get(area).get(difficulty).get(skillId)) {
				if (print)
					player.sendMessage("You need a " + Constants.SKILL_NAME[skillId] + " level of " + TASK_SET_LEVEL_REQS.get(area).get(difficulty).get(skillId)+".");
				meetsRequirements = false;
			}
		return meetsRequirements;
	}

	public void addSkillReq(int skill, int level) {
		if (skillReqs.get(skill) == null || level > skillReqs.get(skill))
			skillReqs.put(skill, level);
	}

	public Area getTaskSet() {
		return taskSet;
	}

	@ServerStartupEvent(Priority.FILE_IO)
	public static void init() {
		QuestDefinitions.init();
		QuestInformationParser.init();
		EnumDefinitions list = EnumDefinitions.getEnum(3483);
		for (long id : list.getValues().keySet()) {
			StructDefinitions details = StructDefinitions.getStruct((int) list.getValue(id));
			AchievementDef cheevo = new AchievementDef((int) id, details);
			CACHE.put(cheevo.id, cheevo);
			if (cheevo.difficulty != Difficulty.NONE && cheevo.taskSet != Area.ELSEWHERE && cheevo.taskSet != Area.LODESTONES) {
				Map<Difficulty, Set<AchievementDef>> areaMap = TASK_SETS.get(cheevo.taskSet);
				if (areaMap == null)
					areaMap = new HashMap<>();
				Set<AchievementDef> taskSet = areaMap.get(cheevo.difficulty);
				if (taskSet == null)
					taskSet = new HashSet<>();
				taskSet.add(cheevo);
				areaMap.put(cheevo.difficulty, taskSet);
				TASK_SETS.put(cheevo.taskSet, areaMap);

				Map<Difficulty, Map<Integer, Integer>> diffMap = TASK_SET_LEVEL_REQS.get(cheevo.taskSet);
				if (diffMap == null)
					diffMap = new HashMap<>();
				Map<Integer, Integer> levelReqs = diffMap.get(cheevo.difficulty);
				if (levelReqs == null)
					levelReqs = new HashMap<>();
				for (int skillId : cheevo.skillReqs.keySet())
					if (levelReqs.get(skillId) == null || cheevo.skillReqs.get(skillId) > levelReqs.get(skillId))
						levelReqs.put(skillId, cheevo.skillReqs.get(skillId));
				diffMap.put(cheevo.difficulty, levelReqs);
				TASK_SET_LEVEL_REQS.put(cheevo.taskSet, diffMap);
			}
		}
		for (Area area : TASK_SET_LEVEL_REQS.keySet())
			for (Difficulty realDiff : TASK_SET_LEVEL_REQS.get(area).keySet()) {
				Map<Integer, Integer> reqs = TASK_SET_LEVEL_REQS.get(area).get(realDiff);
				if (reqs == null)
					continue;
				for (Difficulty diff : Difficulty.values()) {
					if (diff == realDiff)
						break;
					Map<Integer, Integer> oldReq = TASK_SET_LEVEL_REQS.get(area).get(diff);
					if (oldReq == null)
						continue;
					for (int skillId : oldReq.keySet())
						if (reqs.get(skillId) == null || oldReq.get(skillId) > reqs.get(skillId))
							reqs.put(skillId, oldReq.get(skillId));
				}
			}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public Area getArea() {
		return area;
	}

	private static int toRealSkillId(int skillId) {
		return switch(skillId) {
		case 1 -> 0;
		case 2 -> 2;
		case 3 -> 4;
		case 4 -> 6;
		case 5 -> 1;
		case 6 -> 3;
		case 7 -> 5;
		case 8 -> 16;
		case 9 -> 15;
		case 10 -> 17;
		case 11 -> 12;
		case 12 -> 20;
		case 13 -> 14;
		case 14 -> 13;
		case 15 -> 10;
		case 16 -> 7;
		case 17 -> 11;
		case 18 -> 8;
		case 19 -> 9;
		case 20 -> 18;
		case 21 -> 19;
		case 22 -> 22;
		case 23 -> 21;
		case 24 -> 23;
		case 25 -> 24;
		default -> 0;
		};
	}

	@Override
	public String toString() {
		return "{"+id+", \""+name+"\", " + difficulty.name()+"}";
	}

	public int getNext() {
		return next;
	}

	public static AchievementDef getDefs(int id) {
		return CACHE.get(id);
	}
}
