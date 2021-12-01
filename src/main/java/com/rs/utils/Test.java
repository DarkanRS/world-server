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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.game.WorldTile;

public class Test {
	
	public static int HOURS_TO_SAMPLE = 300;

	@SuppressWarnings("unchecked")
public static void main(String[] args) throws IOException, ParseException {
		//Cache.init();
		
		final String CURRENT_SPAWNS = "C:\\Users\\Devin\\Desktop\\Games\\RSPS\\724\\darkan-game-server\\data\\npcs\\spawns\\_spawns.json";
		final String SHAUNY_SPAWNS = "C:\\Users\\Devin\\Desktop\\Games\\RSPS\\724\\darkan-game-server\\data\\npcs\\spawns\\_shaunySpawns.json";
		final String NEW_SPAWNS = "C:\\Users\\Devin\\Desktop\\Games\\RSPS\\724\\darkan-game-server\\data\\npcs\\spawns\\newSpawns.json";		

		JSONParser jsonParser = new JSONParser();
		JSONArray shaunySpawns = (JSONArray) jsonParser.parse(new FileReader(SHAUNY_SPAWNS));
		JSONArray oldSpawns = (JSONArray) jsonParser.parse(new FileReader(CURRENT_SPAWNS));

		JSONArray newSpawns = new JSONArray();

		oldSpawns.forEach(spawn -> {
			if (!isSurfaceRegion(getRegionId((JSONObject) spawn)) || (isSurfaceRegion(getRegionId((JSONObject) spawn)) && hasGraphicallyUpdated(getRegionId((JSONObject) spawn))) || isTransformableNPC((JSONObject) spawn)) {
				newSpawns.add(spawn);
			}
		});
		
		shaunySpawns.forEach(spawn -> {
			if (!hasGraphicallyUpdated(getRegionId((JSONObject) spawn))) {
				newSpawns.add(spawn);
			}
		});
		
		System.out.println("shauny: " + shaunySpawns.size());
		System.out.println("current: " + oldSpawns.size());
		System.out.println("new: " + newSpawns.size());
		
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyPrintString = gson.toJson(newSpawns);
		
		FileWriter fw = new FileWriter(NEW_SPAWNS);
		
	    try {
			fw.write(prettyPrintString);
	    }catch (Exception E) {
	        E.printStackTrace();
	    } finally {
	        fw.flush();
	        fw.close();
	    }
	}

	public static int getRegionId(JSONObject o) {
		JSONObject tile = (JSONObject) o.get("tile");
		Object x = tile.get("x");
		Object y = tile.get("y");
		Object z = tile.get("plane");
		if (x != null && y != null && z != null) {
			WorldTile wt = new WorldTile(Integer.parseInt(x.toString()), Integer.parseInt(y.toString()), Integer.parseInt(z.toString()));
			return wt.getRegionId();
		}
		return -1;
	}

	public static boolean isTransformableNPC(JSONObject o) {
		Object id = o.get("npcId");
		if (id != null) {
			int n = Integer.parseInt(id.toString());
			if (NPCDefinitions.getDefs(n).transformTo != null && NPCDefinitions.getDefs(n).transformTo.length > 0) {
				System.out.println("NPC " + n + " transforms with vars... Keeping spawn.");
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasGraphicallyUpdated(int regionId) {
		switch (regionId) {
		case 12850:
		case 12593:
		case 12849:
		case 13105:
		case 12843:
		case 13099:
		case 13365:
		case 13364:
		case 13623:
		case 12337:
		case 12336:
		case 12592:
		case 11318:
		case 11317:
		case 11061:
		case 10548:
		case 11575:
		case 11574:
		case 11573:
		case 11821:
		case 13622:
		case 12086:
		case 12087:
		case 13362:
		case 13363:
			System.out.println("Region Id " + regionId + " has been graphically updated, skipping.");
			return true;
		}
		return false;
	}
	
	public static boolean isSurfaceRegion(int regionId) {
		if ((regionId >= 6961 && regionId <= 6963) ||
		(regionId >= 7216 && regionId <= 7219) ||
		(regionId >= 7472 && regionId <= 7475) ||
		(regionId >= 7728 && regionId <= 7731) ||
		(regionId >= 7984 && regionId <= 7987) ||
		(regionId >= 8240 && regionId <= 8256) ||
		(regionId >= 8496 && regionId <= 8512) ||
		(regionId >= 8752 && regionId <= 8768) ||
		(regionId >= 9008 && regionId <= 9024) ||
		(regionId >= 9262 && regionId <= 9280) ||
		(regionId >= 9516 && regionId <= 9536) ||
		(regionId >= 9772 && regionId <= 9792) ||
		(regionId >= 10023 && regionId <= 10048) ||
		(regionId >= 10279 && regionId <= 10304) ||
		(regionId >= 10535 && regionId <= 10560) ||
		(regionId >= 10791 && regionId <= 10816) ||
		(regionId >= 11047 && regionId <= 11072) ||
		(regionId >= 11303 && regionId <= 11328) ||
		(regionId >= 11559 && regionId <= 11584) ||
		(regionId >= 11815 && regionId <= 11840) ||
		(regionId >= 12071 && regionId <= 12096) ||
		(regionId >= 12333 && regionId <= 12352) ||
		(regionId >= 12589 && regionId <= 12608) ||
		(regionId >= 12843 && regionId <= 12864) ||
		(regionId >= 13099 && regionId <= 13120) ||
		(regionId >= 13354 && regionId <= 13376) ||
		(regionId >= 13610 && regionId <= 13632) ||
		(regionId >= 13866 && regionId <= 13883) ||
		(regionId >= 14127 && regionId <= 14138) ||
		(regionId >= 14379 && regionId <= 14392) ||
		(regionId >= 14635 && regionId <= 14648) ||
		(regionId >= 14891 && regionId <= 14904) ||
		(regionId >= 15147 && regionId <= 15160) ||
		(regionId >= 15403 && regionId <= 15416)) {
			return true;
		}
		
		System.out.println("Region Id " + regionId + " is not a surface map, skipping.");
		return false;
	}


	
	
//		for (int i = 0;i < 2000;i++) {
//			int musicIndex = (int) EnumDefinitions.getEnum(1351).getKeyForValue(i);
//			if (musicIndex != -1) {
//				String musicName = EnumDefinitions.getEnum(1345).getStringValue(musicIndex);
//				System.out.println(i + " - " + musicName + " - musicIndex: " + musicIndex);
//			}
//		}

	
//		for (NPCDirection dir : NPCDirection.values()) {
//			System.out.println(dir.name() + " - " + dir.getFaceDirection());
//			int d = ((int) (Math.atan2(dir.getDx(), dir.getDy()) * 2607.5945876176133));
//			double ang = d / 2607.5945876176133;
//			System.out.println(Math.round(Math.sin(ang)) + ", " + Math.round(Math.cos(ang)));
//		}
//		
//		
//		RockType rock = RockType.GEM;
//		Pickaxe pick = Pickaxe.RUNE;
		
//		for (int pickIdx = Pickaxe.BRONZE.ordinal();pickIdx <= Pickaxe.DRAGON.ordinal();pickIdx += 2) {
//			for (int i = 1;i <= 99;i += 1) {
//				if (i % 5 == 0 || i == rock.getLevel() || i == 99) {
//					String average = getMiningAverage(rock, i, Pickaxe.values()[pickIdx]);
//					if (!average.isEmpty())
//						System.out.println(average);
//				}
//			}
//			System.out.println();
//		}
		
//		for (int i = 1; i <= 99; i += 1) {
//			if (i % 5 == 0 || i == rock.getLevel() || i == 99) {
//				String average = getMiningAverage(rock, i, pick);
//				if (!average.isEmpty())
//					System.out.println(average);
//			}
//		}
//	}
//
//	private static String getFishingAverage(FishingSpot spot, int level) {
//		if (level < spot.getLevel())
//			return "";
//		ItemsContainer<Item> fishContainer = new ItemsContainer<>(10, true);
//
//		int attemptsPerHour = (60*60*1000)/(5*600);
//		int xp = 0;
//		for (int hours = 0;hours < HOURS_TO_SAMPLE;hours++) {
//			for (int i = 0;i < attemptsPerHour;i++) {
//				for (Fish f : spot.getFish()) {
//					if (level >= f.getLevel() && f.rollSuccess(level)) {
//						fishContainer.add(new Item(f.getId(), 1));
//						xp += f.getXp();
//						break;
//					}
//				}
//			}
//		}
//
//		String fishCounts = "";
//		List<Item> sorted = fishContainer.asList();
//		sorted.sort((o1, o2) -> {
//			return o1.getName().compareTo(o2.getName());
//		});
//		for (Item f : sorted) {
//			if (f != null)
//				fishCounts += (int) ((double) f.getAmount() / (double) HOURS_TO_SAMPLE) + " " + f.getName() + ", ";
//		}
//
//		return spot.name() + " -> Level: " + level + " Caught: " + fishCounts + " XP: " + Utils.formatNumber((int) (xp / HOURS_TO_SAMPLE));
//	}
//	
//	private static String getWoodcuttingAverage(TreeType tree, int level, Hatchet hatchet) {
//		if (level < tree.getLevel())
//			return "";
//		ItemsContainer<Item> wood = new ItemsContainer<>(10, true);
//
//		int attemptsPerHour = (60*60*1000)/(4*600);
//		int xp = 0;
//		for (int hours = 0;hours < HOURS_TO_SAMPLE;hours++) {
//			for (int i = 0;i < attemptsPerHour;i++) {
//				if (tree.rollSuccess(level, hatchet)) {
//					wood.add(new Item(tree.getLogsId()[0], 1));
//					xp += tree.getXp();
//				}
//			}
//		}
//
//		String logCounts = "";
//		List<Item> sorted = wood.asList();
//		sorted.sort((o1, o2) -> {
//			return o1.getName().compareTo(o2.getName());
//		});
//		for (Item f : sorted) {
//			if (f != null)
//				logCounts += (int) ((double) f.getAmount() / (double) HOURS_TO_SAMPLE) + " " + f.getName() + ", ";
//		}
//
//		return "Tree: " + tree.name() + " Hatchet: " + hatchet.name() + " Level: " + level + " Cut: " + logCounts + " XP: " + Utils.formatNumber((int) (xp / HOURS_TO_SAMPLE));
//	}
//	
//	private static String getMiningAverage(RockType rock, int level, Pickaxe pick) {
//		if (level < rock.getLevel() || level < pick.getLevel())
//			return "";
//		ItemsContainer<Item> ores = new ItemsContainer<>(10, true);
//
//		int totalTicks = 6000 * HOURS_TO_SAMPLE;
//		int usedTicks = 0;
//		int xp = 0;
//		int tries = 0;
//		while (usedTicks < totalTicks) {
//			boolean success = false;
//			tries++;
//			for (Ore ore : rock.getOres()) {
//				if (level >= ore.getLevel() && ore.rollSuccess(level)) {
//					ores.add(new Item(ore.getId(), 1));
//					xp += ore.getXp();
//					success = true;
//					break;
//				}
//			}
//			if (success) {
//				if ((pick == Pickaxe.DRAGON || pick == Pickaxe.DRAGON_G) && Utils.random(2) == 0)
//					usedTicks += tries <= 1 ? 2 : pick.getTicks()-1;
//				else
//					usedTicks += tries <= 1 ? 3 : pick.getTicks();
//				tries = 0;
//			} else {
//				if ((pick == Pickaxe.DRAGON || pick == Pickaxe.DRAGON_G) && Utils.random(2) == 0)
//					usedTicks += pick.getTicks()-1;
//				else
//					usedTicks += pick.getTicks();
//			}
//		}
//
//		String logCounts = "";
//		List<Item> sorted = ores.asList();
//		sorted.sort((o1, o2) -> {
//			return o1.getName().compareTo(o2.getName());
//		});
//		for (Item f : sorted) {
//			if (f != null)
//				logCounts += (int) ((double) f.getAmount() / (double) HOURS_TO_SAMPLE) + " " + f.getName() + ", ";
//		}
//
//		return "Rock: " + rock.name() + " Pickaxe: " + pick.name() + " Level: " + level + " Mined: " + logCounts + " XP: " + Utils.formatNumber((int) (xp / HOURS_TO_SAMPLE));
//	}
//
//	private static String getJagAverageFor(int ticksPerAction, int level, int sucLvl1, int sucLvl99, double xp) {
//		return getJagAverageFor(ticksPerAction, level, 0, sucLvl1, sucLvl99, xp);
//	}
//	
//	private static String getJagAverageFor(int ticksPerAction, int level, int toolBonus, int sucLvl1, int sucLvl99, double xp) {
//		int attemptsPerHour = (60*60*1000)/(ticksPerAction*600);
//		int caught = 0;
//		for (int hours = 0;hours < HOURS_TO_SAMPLE;hours++) {
//			for (int i = 0;i < attemptsPerHour;i++) {
//				if (Utils.skillSuccess(level, toolBonus, sucLvl1, sucLvl99))
//					caught++;
//			}
//		}
//		
//		return "Level: " + level + " Tool: "+toolBonus+" Items: " + Utils.formatNumber(caught / HOURS_TO_SAMPLE) + " XP: " + Utils.formatNumber((int) (xp * (caught / HOURS_TO_SAMPLE)));
//	}
}
