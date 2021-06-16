package com.rs.tools.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.util.Utils;

public final class BeastiaryInfo {

	public static void main(String[] args) throws IOException {
		Cache.init(Settings.getConfig().getCachePath());
		createData();
	}

	class Animations {
		int death;
		int attack;
	}

	class BestiaryInfo {
		int magic;
		int defence;
		int level;
		int ranged;
		int attack;
		int size;
		int lifepoints;
		double xp;
		int id;
		Animations animations;
		boolean poisonous;
		boolean members;
		boolean aggressive;
		String weakness;
		boolean attackable;
		String description;
		String name;
		String[] areas;
	}

	private static Map<Integer, BestiaryInfo> beasts = new HashMap<Integer, BestiaryInfo>();

	private static void loadBeastData() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(new File("./beasts.txt")));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				in.close();
				break;
			}
			Gson gson = new GsonBuilder().create();
			BestiaryInfo bi = gson.fromJson(line, BestiaryInfo.class);
			if (bi.id == 0)
				continue;
			beasts.put(bi.id, bi);
		}
	}

	private static void createData() {
		try {
			loadBeastData();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("backupAnimations.txt")));
			try {
				for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++) {
					NPCDefinitions def = NPCDefinitions.getDefs(npcId);
					BestiaryInfo line = beasts.remove(npcId);
					if (line == null || !def.getName().equals(line.name) || (line.animations.attack == 0 && line.animations.death == 0))
						continue;
					writer.write("//" + def.getName() + " (" + def.combatLevel + ")");
					writer.newLine();
					writer.write(npcId + " - " + (line.animations.attack > Utils.getAnimationDefinitionsSize() ? -1 : line.animations.attack) + " " + (line.animations.death > Utils.getAnimationDefinitionsSize() ? -1 : line.animations.death));
					writer.newLine();
					writer.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BeastiaryInfo() {

	}

}
