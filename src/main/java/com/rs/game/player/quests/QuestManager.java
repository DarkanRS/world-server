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
package com.rs.game.player.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.quests.data.QuestInformation;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class QuestManager {
	public static int MAX_QUESTPOINTS = 0;

	private transient Player player;

	private int sort;
	private boolean filter;
	private boolean hideDone;
	private Map<Integer, Integer> questStages;
	private Map<Integer, GenericAttribMap> questAttribs;

	public QuestManager() {
		questStages = new HashMap<>();
		questAttribs = new HashMap<>();
	}

	public static ButtonClickHandler handleQuestTabButtons = new ButtonClickHandler(190) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 38)
				e.getPlayer().getQuestManager().setSort(e.getSlotId());
			else if (e.getComponentId() == 3)
				e.getPlayer().getQuestManager().toggleFilter();
			else if (e.getComponentId() == 7)
				e.getPlayer().getQuestManager().toggleHideDone();
			else if (e.getComponentId() == 15) {
				Quest quest = Quest.forSlot(e.getSlotId());
				if (quest != null) {
					QuestInformation info = quest.getDefs().getExtraInfo();
					if (quest.isImplemented()) {
						ArrayList<String> lines = new ArrayList<>();
						if (e.getPlayer().getQuestManager().getStage(quest) > 0)
							for (int i = 0;i < e.getPlayer().getQuestManager().getStage(quest);i++)
								for (String line : quest.getHandler().getJournalLines(e.getPlayer(), i))
									lines.add("<str>" + line);
						lines.addAll(quest.getHandler().getJournalLines(e.getPlayer(), e.getPlayer().getQuestManager().getStage(quest)));


						e.getPlayer().getInterfaceManager().sendInterface(275);
						e.getPlayer().getPackets().sendRunScriptReverse(1207, lines.size());
						e.getPlayer().getPackets().setIFText(275, 1, info.getName());
						for (int i = 10; i < 289; i++)
							e.getPlayer().getPackets().setIFText(275, i, ((i - 10) >= lines.size() ? " " : lines.get(i - 10)));
					} else {
						ArrayList<String> lines = new ArrayList<>();

						lines.add("<col=00FF00><shad=000000>Requirements");
						if (info.getSkillReq().size() > 0)
							for (int skillId : info.getSkillReq().keySet()) {
								if (info.getSkillReq().get(skillId) == 0)
									continue;
								lines.add(info.getSkillReq().get(skillId) + " " + Skills.SKILL_NAME[skillId]);
							}
						else
							lines.add("None.");
						lines.add("");
						lines.add("<col=00FF00><shad=000000>True requirements");
						if (info.getPreReqSkillReqs().size() > 0)
							for (int skillId : info.getPreReqSkillReqs().keySet()) {
								if (info.getPreReqSkillReqs().get(skillId) == 0)
									continue;
								lines.add(info.getPreReqSkillReqs().get(skillId) + " " + Skills.SKILL_NAME[skillId]);
							}
						else
							lines.add("None.");
						lines.add("");
						lines.add("<col=00FF00><shad=000000>You must have completed the following quests");
						if (info.getPreReqs().size() > 0)
							for (Quest preReq : info.getPreReqs())
								lines.add(preReq.getDefs().getExtraInfo().getName());
						else
							lines.add("None.");
						lines.add("");
						lines.add("<col=00FF00><shad=000000>Quest point requirement");
						lines.add(info.getQpReq() + "");
						lines.add("");
						lines.add("<col=00FF00><shad=000000>Start NPC ");
						lines.add(info.getStartNpc() == -1 ? "NOT FOUND" : NPCDefinitions.getDefs(info.getStartNpc()).getName());
						lines.add("");
						String location = "NOT FOUND";
						if (info.getStartLocation() != null)
							location = "[" + info.getStartLocation().getX() + ", " + info.getStartLocation().getY() + ", " + info.getStartLocation().getPlane() + "]";
						lines.add("<col=00FF00><shad=000000>Coordinates of quest start");
						lines.add(location);
						lines.add("");
						lines.add("<col=ff0000><shad=000000>This is a test interface to showcase quest data.");
						lines.add("<col=ff0000><shad=000000>Quests currently implemented are:");
						for (Quest q : Quest.values())
							if (q.isImplemented())
								lines.add(q.name());
						e.getPlayer().getInterfaceManager().sendInterface(275);
						e.getPlayer().getPackets().sendRunScriptReverse(1207, lines.size());
						e.getPlayer().getPackets().setIFText(275, 1, info.getName());
						for (int i = 10; i < 289; i++)
							e.getPlayer().getPackets().setIFText(275, i, ((i - 10) >= lines.size() ? " " : lines.get(i - 10)));
					}
				}
			}
		}
	};

	public void unlockQuestTabOptions() {
		player.getPackets().setIFRightClickOps(190, 15, 0, 201, 0, 1, 2, 3);
		player.getPackets().setIFTargetParams(new IFTargetParams(190, 38, 0, 3).enableRightClickOption(0));
		updateOptions();
	}

	public int getStage(Quest quest) {
		if (questStages.get(quest.getId()) == null)
			return 0;
		return questStages.get(quest.getId());
	}

	public void setStage(Quest quest, int stage) {
		setStage(quest, stage, true);
	}

	public void setStage(Quest quest, int stage, boolean updateJournal) {
		if (!quest.isImplemented())
			return;
		questStages.put(quest.getId(), stage);
		if (updateJournal)
			sendQuestStage(quest, true);
	}

	public void completeQuest(Quest quest) {
		if (!quest.isImplemented())
			return;
		if (!isComplete(quest)) {
			setStage(quest, quest.getHandler().getCompletedStage());
			clearQuestAttributes(quest);
			quest.getHandler().complete(player);
			sendQuestStage(quest, true);
			sendQuestPoints();
		}
	}

	public void resetQuest(Quest quest) {
		if (!quest.isImplemented())
			return;
		clearQuestAttributes(quest);
		setStage(quest, 0);
	}

	private void clearQuestAttributes(Quest quest) {
		questAttribs.remove(quest.getId());
	}

	public GenericAttribMap getAttribs(Quest quest) {
		GenericAttribMap map = questAttribs.get(quest.getId());
		if (map == null) {
			map = new GenericAttribMap();
			questAttribs.put(quest.getId(), map);
		}
		return map;
	}

	public boolean completedAllQuests() {
		for (Quest quest : Quest.values()) {
			if (!quest.isImplemented())
				continue;
			if (!isComplete(quest))
				return false;
		}
		return true;
	}

	public boolean isComplete(Quest quest) {
		if (!quest.isImplemented())
			return true;
		return getStage(quest) == quest.getHandler().getCompletedStage();
	}

	public int getQuestPoints() {
		int points = 0;
		for (Quest quest : Quest.values()) {
			if (quest == null || !quest.isImplemented())
				continue;
			if (isComplete(quest))
				points += quest.getDefs().questpointReward;
		}
		return points;
	}

	public void sendQuestPoints() {
		player.getVars().setVar(101, getQuestPoints());
		player.getVars().setVar(904, MAX_QUESTPOINTS);
	}

	public void updateAllQuestStages() {
		for (Quest quest : Quest.values())
			sendQuestStage(quest, false);
	}

	public void sendQuestStage(Quest quest, boolean refresh) {
		if (isComplete(quest))
			quest.getDefs().sendCompleted(player);
		else if (getStage(quest) > 0)
			quest.getDefs().sendStarted(player);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (MAX_QUESTPOINTS == 0)
			for (Quest quest : Quest.values())
				if (quest.isImplemented())
					MAX_QUESTPOINTS += quest.getDefs().questpointReward;
	}

	public void updateOptions() {
		//4538 = free/members toggle which is first?
		player.getVars().setVarBit(4536, sort);
		player.getVars().setVarBit(4537, filter ? 0 : 1);
		player.getVars().setVarBit(7264, hideDone ? 0 : 1);
		player.getVars().syncVarsToClient();
		player.getPackets().sendRunScript(2160, 0);
	}

	public void setSort(int sort) {
		this.sort = sort;
		updateOptions();
	}

	public void toggleFilter() {
		filter = !filter;
		updateOptions();
	}

	public void toggleHideDone() {
		hideDone = !hideDone;
		updateOptions();
	}
}
