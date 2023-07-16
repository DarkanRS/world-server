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

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BASDumper {

	public static void main(String[] args) throws IOException {
		Cache.init(Settings.getConfig().getCachePath());

		File file = new File("basDump.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 727\n");
		writer.flush();

		Map<Integer, String> used = new HashMap<>();
		for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
			NPCDefinitions npcDef = NPCDefinitions.getDefs(npcId);
			if (npcDef.basId != -1) {
				BASDefinitions render = BASDefinitions.getDefs(npcDef.basId);
				if (render.standAnimation == -1 && render.walkAnimation == -1) {
					if (used.get(npcDef.basId) != null)
						used.put(npcDef.basId, used.get(npcDef.basId) + ", " + npcId + " (" + npcDef.getName() + ")" );
					else
						used.put(npcDef.basId, "["+AnimationDefinitions.getDefs(npcDef.basId).frameSetIds[0] + "] " + npcId + " (" + npcDef.getName() + ")");
					continue;
				}
				AnimationDefinitions check = AnimationDefinitions.getDefs(render.standAnimation == -1 ? render.walkAnimation : render.standAnimation);
				if (used.get(npcDef.basId) != null)
					used.put(npcDef.basId, used.get(npcDef.basId) + ", " + npcId + " (" + npcDef.getName() + ")" );
				else
					used.put(npcDef.basId, "["+AnimationDefinitions.getDefs(npcDef.basId).frameSetIds[0] + "] " + npcId + " (" + npcDef.getName() + ")");
				if (check.frameSetIds[0] == 2183 || check.frameSetIds[0] == 1843)
					used.put(npcDef.basId, "["+AnimationDefinitions.getDefs(npcDef.basId).frameSetIds[0] + "] Humanoid");
			}
		}

		Map<Integer, Integer> UNIDS = new HashMap<>();

		for (int i = 0; i < Utils.getBASAnimDefSize(); i++) {
			String name = used.get(i);
			if (name == null) {
				BASDefinitions bas = BASDefinitions.getDefs(i);
				if (bas.standAnimation != -1 || bas.walkAnimation != -1) {
					AnimationDefinitions check = AnimationDefinitions.getDefs(bas.standAnimation == -1 ? bas.walkAnimation : bas.standAnimation);
					name = switch (check.id) {
					case 2409, 2410, 2411, 2414, 2417, 2418, 2421, 2429, 2420 -> "Humanoid chathead";
					case 3102 -> "Troll/Tzhaar chathead";
					case 1815 -> "Child chathead";
					case 1981 -> "Werewolf/Cat chathead";
					case 1459 -> "Penguin chathead";
					case 81, 83 -> "Old NPC chathead";
					case 2183, 1843 -> "Humanoid";
					default -> {
						UNIDS.put(i, i);
						yield "[" + AnimationDefinitions.getDefs(i).frameSetIds[0] + "] Unknown";
					}
					};
				}
			}

			writer.append(i + ": " + name);
			writer.newLine();
			writer.flush();
		}

		writer.close();
	}

}
