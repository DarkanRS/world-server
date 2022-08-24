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
package com.rs.game.content.miniquests;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class MiniquestManager {
	private transient Player player;

	private Map<Miniquest, Integer> questStages;
	private Map<Miniquest, GenericAttribMap> questAttribs;

	public MiniquestManager() {
		questStages = new HashMap<>();
		questAttribs = new HashMap<>();
	}

	public int getStage(Miniquest quest) {
		if (questStages.get(quest) == null)
			return 0;
		return questStages.get(quest);
	}

	public void setStage(Miniquest quest, int stage) {
		setStage(quest, stage, true);
	}

	public void setStage(Miniquest quest, int stage, boolean updateJournal) {
		if (!quest.isImplemented())
			return;
		questStages.put(quest, stage);
		if (updateJournal)
			sendStage(quest);
	}

	public void complete(Miniquest quest) {
		if (!quest.isImplemented())
			return;
		if (!isComplete(quest)) {
			setStage(quest, quest.getHandler().getCompletedStage());
			clearQuestAttributes(quest);
			quest.getHandler().complete(player);
			sendStage(quest);
		}
	}

	public void reset(Miniquest quest) {
		if (!quest.isImplemented())
			return;
		clearQuestAttributes(quest);
		setStage(quest, 0);
	}

	private void clearQuestAttributes(Miniquest quest) {
		questAttribs.remove(quest);
	}

	public GenericAttribMap getAttribs(Miniquest quest) {
		GenericAttribMap map = questAttribs.get(quest);
		if (map == null) {
			map = new GenericAttribMap();
			questAttribs.put(quest, map);
		}
		return map;
	}

	public boolean completedAll() {
		for (Miniquest quest : Miniquest.values()) {
			if (!quest.isImplemented())
				continue;
			if (!isComplete(quest))
				return false;
		}
		return true;
	}

	public boolean isComplete(Miniquest quest, String actionForUnimplemented) {
		if (!quest.isImplemented())
			return quest.meetsReqs(player, actionForUnimplemented);
		if (getStage(quest) == quest.getHandler().getCompletedStage())
			return true;
		if (actionForUnimplemented != null)
			player.sendMessage("You must have completed " + quest.getName() + " " + actionForUnimplemented);
		return false;
	}
	
	public boolean isComplete(Miniquest quest) {
		return isComplete(quest, null);
	}

	public void updateAllStages() {
		for (Miniquest quest : Miniquest.values())
			sendStage(quest);
	}

	public void sendStage(Miniquest quest) {
		if (quest.getHandler() != null)
			quest.getHandler().updateStage(player);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
