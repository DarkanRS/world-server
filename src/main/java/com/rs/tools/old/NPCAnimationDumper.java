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

import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NPCAnimationDumper {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		File file = new File("npcAnimations.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.flush();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions npc = NPCDefinitions.getDefs(id);
			if (npc.basId <= 0)
				continue;
			BASDefinitions render = BASDefinitions.getDefs(npc.basId);
			AnimationDefinitions anim = AnimationDefinitions.getDefs(render.standAnimation);
			if (anim.anIntArray5923 == null)
				continue;
			ArrayList<AnimationDefinitions> relatedDefs = new ArrayList<>();
			for (int i = 0;i < Utils.getAnimationDefinitionsSize();i++) {
				AnimationDefinitions def = AnimationDefinitions.getDefs(i);
				if (def.anIntArray5923 != null && def.anIntArray5923[0] == anim.anIntArray5923[0])
					relatedDefs.add(def);
			}
			writer.append(id + " (" + npc.getName() + ") - " + Arrays.toString(relatedDefs.stream().map(def -> def.id + " (" + (def.getEmoteTime()/1000.0) + " secs)").toArray()));
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}

}
