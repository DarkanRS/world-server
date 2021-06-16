package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

public class NPCAnimationChecker {
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		
		File file = new File("npcAnimationsCompat.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 727\n");
		writer.flush();
		
		for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
			NPCDefinitions npcDef = NPCDefinitions.getDefs(npcId);
			if (npcDef.basId != -1) {
				if (BASDefinitions.getDefs(npcDef.basId).standAnimation != -1) {
					if (AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds != null) {
						int frameSetId = AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds[0];
						HashSet<Integer> animsUsingSet = new HashSet<Integer>();
						for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
							AnimationDefinitions check = AnimationDefinitions.getDefs(i);
							if (check == null)
								continue;
							if (check.frameSetIds == null || check.frameSetIds[0] == -1)
								continue;
							if (check.frameSetIds[0] == frameSetId)
								animsUsingSet.add(i);
						}
						writer.append(npcId + " ("+npcDef.getName()+") set#" + frameSetId + ": " + animsUsingSet.toString());
						writer.newLine();
						writer.flush();
					}
				}
			}
		}
		
		writer.close();
	}

}
