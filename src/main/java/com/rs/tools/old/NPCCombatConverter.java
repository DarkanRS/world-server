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
package com.rs.tools.old;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.util.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

	private static Map<Integer, CombatInfo> beasts = new HashMap<>();

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
