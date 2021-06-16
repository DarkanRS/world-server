package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

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
					switch(check.id) {
					case 2409:
					case 2410:
					case 2411:
					case 2414:
					case 2417:
					case 2418:
					case 2421:
					case 2429:
					case 2420:
						name = "Humanoid chathead";
						break;
					case 3102:
						name = "Troll/Tzhaar chathead";
						break;
					case 1815:
						name = "Child chathead";
						break;
					case 1981:
						name = "Werewolf/Cat chathead";
						break;
					case 1459:
						name = "Penguin chathead";
						break;
					case 81:
					case 83:
						name = "Old NPC chathead";
						break;
					case 2183:
					case 1843:
						name = "Humanoid";
						break;
					default:
						UNIDS.put(i, i);
						name = "[" + AnimationDefinitions.getDefs(i).frameSetIds[0] + "] Unknown";
						break;
					}
				}
			}
			
			writer.append(i + ": " + name);
			writer.newLine();
			writer.flush();
		}
		
		writer.close();
	}

}
