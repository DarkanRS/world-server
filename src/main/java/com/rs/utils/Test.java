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
package com.rs.utils;

import java.io.IOException;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.interfaces.IComponentDefinitions;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.utils.json.ControllerAdapter;

public class Test {

	public static int HOURS_TO_SAMPLE = 300;
	
	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());

		Settings.getConfig();
		Cache.init(Settings.getConfig().getCachePath());
		
		IComponentDefinitions[] summ = IComponentDefinitions.getInterface(672);
		for (int i = 0;i < summ.length;i++)
			System.out.println(summ[i]);
	}

//	public static void main(String[] args) throws IOException {
//		int numKills = 1000000;
//
//		JsonFileManager.setGSON(new GsonBuilder()
//				.registerTypeAdapter(Controller.class, new ControllerAdapter())
//				.registerTypeAdapter(Date.class, new DateAdapter())
//				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
//				.registerTypeAdapter(Packet.class, new PacketAdapter())
//				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
//				.disableHtmlEscaping()
//				.setPrettyPrinting()
//				.create());
//
//		Settings.getConfig();
//		Cache.init(Settings.getConfig().getCachePath());
//		DropSets.init();
//
//		ItemsContainer<Item> items = new ItemsContainer<>(500, true);
//
//		DropList table = DropSets.getDropSet(6260).createDropList();
//		for (int i = 0;i < numKills;i++)
//			items.addAll(table.genDrop());
//
//		List<Item> sorted = new ArrayList<>();
//
//		for (Item item : items.array()) {
//			if (item == null)
//				continue;
//			sorted.add(item);
//		}
//
//		sorted.sort((o1, o2) -> o1.getId() - o2.getId());
//
//		for (Item item : sorted)
//			Logger.debug(item.getName() + " - " + item.getAmount() + " Rate: 1/" + (numKills / item.getAmount()));
//	}

	//		for (int i = 0;i < 2000;i++) {
	//			int musicIndex = (int) EnumDefinitions.getEnum(1351).getKeyForValue(i);
	//			if (musicIndex != -1) {
	//				String musicName = EnumDefinitions.getEnum(1345).getStringValue(musicIndex);
	//				Logger.debug(i + " - " + musicName + " - musicIndex: " + musicIndex);
	//			}
	//		}

	//		for (NPCDirection dir : NPCDirection.values()) {
	//			Logger.debug(dir.name() + " - " + dir.getFaceDirection());
	//			int d = ((int) (Math.atan2(dir.getDx(), dir.getDy()) * 2607.5945876176133));
	//			double ang = d / 2607.5945876176133;
	//			Logger.debug(Math.round(Math.sin(ang)) + ", " + Math.round(Math.cos(ang)));
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
	//						Logger.debug(average);
	//				}
	//			}
	//			Logger.debug();
	//		}

	//		for (int i = 1; i <= 99; i += 1) {
	//			if (i % 5 == 0 || i == rock.getLevel() || i == 99) {
	//				String average = getMiningAverage(rock, i, pick);
	//				if (!average.isEmpty())
	//					Logger.debug(average);
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
