package com.rs.utils;

import com.rs.lib.util.Utils;

public class EffigyDrop {
	
	private static final int COMBAT_LEVEL = 2;
	private static final int SAMPLE = 200_000_000;
	
	public static void main(String[] args) {
		int effigies = 0;
		for (int i = 0;i < SAMPLE;i++) {
			if (dropEffigy(COMBAT_LEVEL))
				effigies++;
		}
		System.out.println("Effigies: " + effigies);
		System.out.println("Simulated: " + (SAMPLE/effigies));
		System.out.println("Actual: " + getRate(COMBAT_LEVEL));
	}
	
	public static boolean dropEffigy(int combatLevel) {
		return Math.random() <= (1.0 / getRate(combatLevel));
	}
	
	public static double getRate(int combatLevel) {
		return Utils.clampD(169075.845*(Math.pow(0.9807522225, combatLevel)), 128.0, 1000000.0);
	}

}
