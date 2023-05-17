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
package com.rs.tools;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.Logger;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.utils.json.ControllerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NPCCombatDefMerger {

	private final static String ORIGINAL_PATH = "data/npcs/combatdefs/";
	public static Map<File, NPCCombatDefinitions> ORIGINAL = new HashMap<>();

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
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());

		loadPackedCombatDefinitions();

		for (File file : ORIGINAL.keySet()) {
			NPCCombatDefinitions def = ORIGINAL.get(file);

			//TODO any bulk processing here
//			if (def.getAggroDistance() > 60) {
//				def.aggroDistance = def.getAttackStyle() == AttackStyle.MELEE ? 4 : def.getAttackRange() - 2;
//				System.out.println("Modified: " + file);
//			}
			
			JsonFileManager.saveJsonFile(def, file);
			System.out.println("Processed: " + file);
		}
	}

	private static void loadPackedCombatDefinitions() {
		try {
			File[] files = new File(ORIGINAL_PATH).listFiles();
			for (File f : files)
				loadFile(f, ORIGINAL);
		} catch (Throwable e) {
			Logger.handle(NPCCombatDefMerger.class, "loadPackedCombatDefinitions", e);
		}
	}

	private static void loadFile(File f, Map<File, NPCCombatDefinitions> map) throws JsonIOException, IOException {
		if (f.isDirectory()) {
			for (File dir : f.listFiles())
				loadFile(dir, map);
			return;
		}
		NPCCombatDefinitions defs = (NPCCombatDefinitions) JsonFileManager.loadJsonFile(f, NPCCombatDefinitions.class);
		if (defs != null)
			map.put(f, defs);
	}

}
