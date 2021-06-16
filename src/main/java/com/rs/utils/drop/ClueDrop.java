package com.rs.utils.drop;

import java.util.HashSet;
import java.util.Set;

public class ClueDrop {
	
	private Set<Integer> combatLevels = new HashSet<>();
	private int weight;
	
	public ClueDrop(int weight, int... combatLevels) {
		this.weight = weight;
		for (int combatLevel : combatLevels)
			this.combatLevels.add(combatLevel);
	}
	
	public Set<Integer> getCombatLevels() {
		return combatLevels;
	}

	public int getWeight() {
		return weight;
	}

	public boolean validCombatLevel(int combatLevel) {
		if (combatLevels.isEmpty())
			return true;
		return combatLevels.contains(combatLevel);
	}

}
