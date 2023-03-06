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
import java.util.Map;
import java.util.Set;

import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;

public class QuestInformation {
	private int id;
	private String name;
	private int startNpc;
	private Tile startLocation;
	private Set<Quest> preReqs = new HashSet<>();
	private Map<Integer, Integer> skillReqs = new HashMap<>();
	private Map<Integer, Integer> preReqSkillReqs = new HashMap<>();
	private int qpReq;
	private int slotId;

	public QuestInformation(int id, String name, int slotId) {
		this.id = id;
		this.name = name;
		this.slotId = slotId;
	}

	public int getId() {
		return id;
	}

	public int getStartNpc() {
		return startNpc;
	}

	public void setStartNpc(int startNpc) {
		this.startNpc = startNpc;
	}

	public Tile getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Tile startLocation) {
		this.startLocation = startLocation;
	}

	public Set<Quest> getPreReqs() {
		return preReqs;
	}

	public void addPreReq(Quest quest) {
		preReqs.add(quest);
	}

	public Map<Integer, Integer> getSkillReq() {
		return skillReqs;
	}

	public void addSkillReq(int skillId, int level) {
		if (skillReqs.get(skillId) == null || level > skillReqs.get(skillId))
			skillReqs.put(skillId, level);
	}

	public int getQpReq() {
		return qpReq;
	}

	public void setQpReq(int qpReq) {
		this.qpReq = qpReq;
	}

	public String getName() {
		return name;
	}

	public int getSlotId() {
		return slotId;
	}

	public Map<Integer, Integer> getPreReqSkillReqs() {
		return preReqSkillReqs;
	}

	public void addPreReqSkillReq(int skillId, int level) {
		if (preReqSkillReqs.get(skillId) == null || level > preReqSkillReqs.get(skillId))
			preReqSkillReqs.put(skillId, level);
	}
}