package com.rs.utils;

public class DungTest {
	
	public static void main(String[] args) {
		for (int i = 1;i <= 60;i++) {
			printXP(i, 35);
		}
		System.out.println();
		printXP(1, 60);
	}
	
	public static void printXP(int floor, int prestige) {
		int baseSmall = getBaseXP(floor);
		int baseMed = getBaseXPMedium(floor);
		int baseLarge = getBaseXPLarge(floor);
		
		int presSmall = getPrestigeXP(floor, prestige);
		int presMed = getPrestigeXPMedium(floor, prestige);
		int presLarge = getPrestigeXPLarge(floor, prestige);
		
		int avgSmall = (int) ((baseSmall+presSmall) / 2);
		int avgMed = (int) ((baseMed+presMed) / 2);
		int avgLarge = (int) ((baseLarge+presLarge) / 2);
		
		System.out.println("~~~Experience calculated for floor " + floor + "~~~");
		System.out.println("Base XP: sm:" + baseSmall + " med:" + baseMed + " lg:" + baseLarge);
		System.out.println("Prestige " + prestige + " XP: sm:" + presSmall + " med:" + presMed + " lg:" + presLarge);
		System.out.println("Average XP: sm:" + avgSmall + " med:" + avgMed + " lg:" + avgLarge);
		System.out.println("Maximum possible XP for floor: " + ((int) (avgLarge * 1.56)));
	}
	
	public static int getBaseXP(int floor) {
		return (int) (0.16*(floor*floor*floor)+0.28*(floor*floor)+76.94*floor+23.0);
	}
	
	public static int getBaseXPMedium(int floor) {
		return getBaseXP(floor) * 2;
	}
	
	public static int getBaseXPLarge(int floor) {
		return (int) (getBaseXP(floor) * 3.5);
	}
	
	public static int getPrestigeXP(int floor, int prestige) {
		return (int) (27.52*prestige+getBaseXP(floor));
	}

	public static int getPrestigeXPMedium(int floor, int prestige) {
		return getPrestigeXP(floor, prestige) * 2;
	}
	
	public static int getPrestigeXPLarge(int floor, int prestige) {
		return (int) (getPrestigeXP(floor, prestige) * 3.5);
	}
}
