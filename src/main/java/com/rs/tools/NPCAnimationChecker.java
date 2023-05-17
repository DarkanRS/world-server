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

import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

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
			if (npcDef.basId != -1)
				if (BASDefinitions.getDefs(npcDef.basId).standAnimation != -1)
					if (AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds != null) {
						int frameSetId = AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds[0];
						HashSet<Integer> animsUsingSet = new HashSet<>();
						for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
							AnimationDefinitions check = AnimationDefinitions.getDefs(i);
							if ((check == null) || check.frameSetIds == null || check.frameSetIds[0] == -1)
								continue;
							if (check.frameSetIds[0] == frameSetId)
								animsUsingSet.add(i);
						}
						writer.append(npcId + " ("+npcDef.getName()+") set#" + frameSetId + ": " + animsUsingSet.toString());
						writer.newLine();
						writer.flush();
					}
		}

		writer.close();
	}

}
