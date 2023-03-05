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
package com.rs.engine.quest.data;

import java.util.HashMap;
import java.util.HashSet;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.StructDefinitions;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Tile;

public class QuestInformationParser {

	public static final long JOURNAL_SLOTID = 847;
	public static final long STARTLOC_PRE_HASH = 854;
	public static final long STARTLOC_HASH = 850;
	public static final long STEP2_LOC_HASH = 851;
	public static final long MEMBERS = 856;
	public static final long NAME = 845;
	public static final long NAME_SORT = 846;
	public static final long START_NPC = 691;
	public static final long REQUIRED_ITEMS = 949;
	public static final long REWARDS = 951;
	public static final long ENEMIES_TO_DEFEAT = 950;
	public static final long START_HINT = 948;
	public static final long DIFFICULTY = 848;

	public static final long USES_NEW_INTERFACE = 694;
	public static final long QUESTPOINT_REQ = 895;

	public static final long QUEST_REQ_START = 859;
	public static final long QUEST_REQ_END = 870;
	public static final long REQ_SKILL_START = 871;
	public static final long REQ_LEVEL_START = 872;
	public static final long REQ_SKILL_END = 883;
	public static final long REQ_LEVEL_END = 884;

	public static HashMap<Integer, QuestInformation> QUESTS_ID = new HashMap<>();
	public static HashMap<String, QuestInformation> QUESTS_NAME = new HashMap<>();
	public static HashMap<Integer, QuestInformation> QUESTS_SLOTID = new HashMap<>();

	public static void init() {
		EnumDefinitions questEnum = EnumDefinitions.getEnum(2252);
		for (long i : questEnum.getValues().keySet()) {
			Quest quest = Quest.forSlot((int) i);
			if (questEnum.getIntValue(i) != -1) {
				StructDefinitions detail = StructDefinitions.getStruct(questEnum.getIntValue(i));
				HashMap<Long, Object> map = detail.getValues();
				QuestInformation qi = new QuestInformation((int) i, (String) map.get(NAME), (Integer) map.get(JOURNAL_SLOTID));
				if (map.get(START_NPC) != null)
					qi.setStartNpc((Integer) map.get(START_NPC));
				else
					qi.setStartNpc(-1);
				if (map.get(QUESTPOINT_REQ) != null)
					qi.setQpReq((Integer) map.get(QUESTPOINT_REQ));
				else
					qi.setQpReq(0);
				if (map.get(STARTLOC_HASH) != null)
					qi.setStartLocation(Tile.of((Integer) map.get(STARTLOC_HASH)));
				if (map.get(QUEST_REQ_START) != null) {
					int numReqs = 0;
					for (long q = QUEST_REQ_START;q <= QUEST_REQ_END;q++)
						if (map.get(q) != null)
							numReqs++;
					int[] reqs = new int[numReqs];
					for (int q = 0;q < reqs.length;q++) {
						reqs[q] = (int) map.get(QUEST_REQ_START+q);
						qi.addPreReq(Quest.forSlot((int) map.get(QUEST_REQ_START+q)));
					}
				}
				if (map.get(REQ_SKILL_START) != null) {
					int numReqs = 0;
					for (long q = REQ_SKILL_START;q <= REQ_SKILL_END;q += 2)
						if (map.get(q) != null)
							numReqs++;
					int[][] reqs = new int[numReqs][2];
					for (int q = 0;q < reqs.length;q += 2) {
						reqs[q][0] = (int) map.get(REQ_SKILL_START+q);
						reqs[q][1] = (int) map.get(REQ_LEVEL_START+q);
						qi.addSkillReq((int) map.get(REQ_SKILL_START+q), (int) map.get(REQ_LEVEL_START+q));
						qi.addPreReqSkillReq((int) map.get(REQ_SKILL_START+q), (int) map.get(REQ_LEVEL_START+q));
					}
				}
				QUESTS_ID.put((int) i, qi);
				QUESTS_SLOTID.put(qi.getSlotId(), qi);
				QUESTS_NAME.put(qi.getName(), qi);
				QuestDefinitions.getQuestDefinitions(quest.getId()).setExtraInfo(qi);
			}
		}
		for (QuestInformation info : QUESTS_ID.values())
			for (Quest quest : new HashSet<>(info.getPreReqs()))
				addPreReqs(info, quest);
		Quest.RITUAL_OF_MAHJARRAT.getDefs().getExtraInfo().addSkillReq(Skills.MINING, 76);
		Quest.LUNAR_DIPLOMACY.getDefs().getExtraInfo().addSkillReq(Skills.MINING, 60);
		Quest.LUNAR_DIPLOMACY.getDefs().getExtraInfo().addSkillReq(Skills.MAGIC, 65);
		Quest.LUNAR_DIPLOMACY.getDefs().getExtraInfo().addSkillReq(Skills.WOODCUTTING, 55);
		Quest.VOID_STARES_BACK.getDefs().getExtraInfo().addSkillReq(Skills.SMITHING, 70);
		Quest.VOID_STARES_BACK.getDefs().getExtraInfo().addSkillReq(Skills.SUMMONING, 55);
		Quest.VOID_STARES_BACK.getDefs().getExtraInfo().addSkillReq(Skills.DEFENSE, 25);
	}

	public static void addPreReqs(QuestInformation info, Quest quest) {
		for (Quest q : new HashSet<>(quest.getDefs().getExtraInfo().getPreReqs()))
			addPreReqs(info, q);
		for (Quest preReq : new HashSet<>(quest.getDefs().getExtraInfo().getPreReqs()))
			info.addPreReq(preReq);
		for (int skill : quest.getDefs().getExtraInfo().getSkillReq().keySet())
			info.addPreReqSkillReq(skill, quest.getDefs().getExtraInfo().getSkillReq().get(skill));
	}

}
