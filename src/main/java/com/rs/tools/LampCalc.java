package com.rs.tools;

import com.rs.game.player.Skills;
import com.rs.lib.Constants;

public class LampCalc {
	
	public enum Lamp {
		SMALL(2, 1700),
		MEDIUM(3, 2500),
		LARGE(8, 6400);
		
		private int mul, price;
		
		private Lamp(int mul, int price) {
			this.mul = mul;
			this.price = price;
		}
		
		public int getXp(int level) {
			if (level >= 99)
				level = 98;
			return (int) Math.floor(LAMP_XP_VALUES[level - 1] * mul / 4);
		}
	}

	public static int[] LAMP_XP_VALUES = { 250, 276, 308, 340, 373, 416, 492, 508, 777, 614, 680, 752, 822, 916, 1008, 1046, 1096, 1140, 1192, 1240, 1298, 1348, 1408, 1470, 1536, 1596, 1621, 1656, 1812, 1892, 1973, 2056, 2144, 2237, 2332, 2434, 2540, 2648, 2766, 2882, 3008, 3138, 3272, 3414, 3558, 3716, 3882, 4050, 4220, 4404, 4593, 4800, 4998, 5218, 5448, 5688, 5940, 6184, 6466, 6737, 7030, 7342, 7645, 8018, 8432, 8686, 9076, 9516, 9880, 10371, 10772, 11237, 11786, 12328, 12855, 13358, 13980, 14587, 15169, 15920, 16664, 17390, 18087, 19048, 19674, 20132, 21502, 22370, 23690, 24486, 25806, 26458, 27714, 28944, 30130, 32258, 33390, 34408 };
	public static int SMALL = 2, MEDIUM = 3, LARGE = 8;
	
	public static void main(String[] args) {
		Lamp lamp = Lamp.LARGE;
		int currentLevel = 50;
		int currentXp = 0;
		int targetLevel = 120;
		int lampsUsed = 0;
		
		if (currentLevel != -1)
			currentXp = Skills.getXPForLevel(currentLevel);
		
		while (Skills.getLevelForXp(Constants.DUNGEONEERING, currentXp) < targetLevel) {
			System.out.println("Lamp used at level " + Skills.getLevelForXp(Constants.DUNGEONEERING, currentXp) + " given " + lamp.getXp(Skills.getLevelForXp(Constants.DUNGEONEERING, currentXp)));
			currentXp += lamp.getXp(Skills.getLevelForXp(Constants.DUNGEONEERING, currentXp));
			lampsUsed++;
		}
		System.out.println("Lamps used: " + lampsUsed + " costing " + (lamp.price * lampsUsed) + " chimes.");
	}
}
