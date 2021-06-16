package com.rs.game.player.quests.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rs.game.player.quests.Quest;
import com.rs.lib.game.WorldTile;

public class QuestInformation {
	private int id;
	private String name;
	private int startNpc;
	private WorldTile startLocation;
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

	public WorldTile getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(WorldTile startLocation) {
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