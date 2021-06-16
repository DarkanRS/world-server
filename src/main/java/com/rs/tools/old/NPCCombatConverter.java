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
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.util.Utils;

public final class NPCCombatConverter {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		createData2();
	}

	class CombatInfo {
		int hitpoints;
		int maxHit;
		int attackStyle;
		boolean aggro;
		int npcId;
		int[] levels;
	}

	private static Map<Integer, CombatInfo> beasts = new HashMap<Integer, CombatInfo>();

	private static void loadBeastData() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(new File("./testdefs.txt")));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				in.close();
				break;
			}
			if (line.startsWith("//"))
				continue;
			Gson gson = new GsonBuilder().create();
			CombatInfo bi = gson.fromJson(line, CombatInfo.class);
			beasts.put(bi.npcId, bi);
		}
	}

	@SuppressWarnings("unused")
	private static void createData() {
		try {
			loadBeastData();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("dumpedBonuses.txt")));
			try {
				for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++) {
					NPCDefinitions def = NPCDefinitions.getDefs(npcId);
					CombatInfo line = beasts.remove(npcId);
					if (line == null)
						continue;
					int stabatt = 0, slashatt = 0, crushatt = 0, magicatt = 0, rangeatt = 0, stabdef = 0, slashdef = 0, crushdef = 0, magicdef = 0, rangedef = 0;
					
					stabatt += def.getStabAtt() + line.levels[0];
					slashatt += def.getSlashAtt() + line.levels[0];
					crushatt += def.getCrushAtt() + line.levels[0];
					magicatt += def.getMagicAtt() + line.levels[4];
					magicdef += def.getMagicDef() + line.levels[4];
					rangeatt += def.getRangeAtt() + line.levels[3];
					rangedef += def.getRangeDef() + line.levels[2];
					crushdef += def.getCrushDef() + line.levels[2];
					slashdef += def.getSlashDef() + line.levels[2];
					stabdef += def.getStabDef() + line.levels[2];
					
					writer.write("//" + def.getName() + " (" + def.combatLevel + ")");
					writer.newLine();
					writer.write(npcId + " - " + stabatt + " " + slashatt + " " + crushatt + " " + magicatt + " " + rangeatt + " " + stabdef + " " + slashdef + " " + crushdef + " " + magicdef + " " + rangedef);
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
	
	private static void createData2() {
		try {
			loadBeastData();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("dumpedDefinitions.txt")));
			try {
				for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++) {
					NPCDefinitions def = NPCDefinitions.getDefs(npcId);
					CombatInfo line = beasts.remove(npcId);
					if (line == null)
						continue;
					String attackStyle = "MELEE";
					if (line.attackStyle == 1)
						attackStyle = "RANGE";
					if (line.attackStyle == 2)
						attackStyle = "MAGE";
					String aggro = line.aggro ? "AGRESSIVE" : "PASSIVE";
					writer.write("//" + def.getName() + " (" + def.combatLevel + ")");
					writer.newLine();
					writer.write(npcId + " - " + line.hitpoints + " " + -1 + " " + -1 + " " + -1 + " " + 1 + " " + 3 + " " + 40 + " " + line.maxHit + " " + attackStyle + " " + -1 + " " + -1 + " " + aggro);
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

	private NPCCombatConverter() {

	}

}
