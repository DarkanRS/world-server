package com.rs.game.player.content.skills.slayer;

import java.util.HashMap;
import java.util.Map;

public enum Master {
	Turael(8480, 3, 1, 1, 1, 3, 10), 
	Mazchna(8481, 20, 1, 2, 2, 5, 15), 
	Vannaka(1597, 40, 1, 3, 4, 20, 60), 
	Chaeldar(1598, 70, 1, 4, 10, 50, 150), 
	Sumona(7779, 85, 35, 5, 12, 60, 180), 
	Duradel(8466, 100, 50, 6, 15, 75, 225), 
	Kuradal(9085, 110, 75, 7, 18, 90, 270);

	public static final Map<Integer, Master> SLAYER_MASTERS = new HashMap<Integer, Master>();

	public static Master getMaster(int id) {
		return SLAYER_MASTERS.get(id);
	}
	
	public static Master getMasterForId(int npcId) {
		for (Master master : Master.values()) {
			if (master != null && master.npcId == npcId)
				return master;
		}
		return null;
	}

	static {
		for (Master master : Master.values()) {
			SLAYER_MASTERS.put(master.npcId, master);
		}
	}
	
	public int npcId;
	public int requiredCombatLevel;
	public int reqSlayerLevel;
	public int masterID;
	private int points, points10, points50;

	private Master(int npcId, int requiredCombatLevel, int requiredSlayerLevel, int slayerMasterID, int points, int points10, int points50) {
		this.npcId = npcId;
		this.requiredCombatLevel = requiredCombatLevel;
		this.reqSlayerLevel = requiredSlayerLevel;
		this.masterID = slayerMasterID;
		this.points = points;
		this.points10 = points10;
		this.points50 = points50;
	}
	
	public int getPoints() {
		return points;
	}

	public int getPoints10() {
		return points10;
	}

	public int getPoints50() {
		return points50;
	}
}