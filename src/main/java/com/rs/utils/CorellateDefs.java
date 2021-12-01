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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils;

import java.io.IOException;

import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;

public class CorellateDefs {
	
	public static void main(String[] args) throws IOException {
		//Cache.init();

		NPCDefinitions npc = NPCDefinitions.getDefs(13456);
		BASDefinitions render = BASDefinitions.getDefs(npc.basId);
		AnimationDefinitions anim = AnimationDefinitions.getDefs(render.standAnimation);
		System.out.println(npc);
		System.out.println(render);
		System.out.println(anim);
//		ArrayList<AnimationDefinitions> relatedDefs = new ArrayList<AnimationDefinitions>();
//		anim: for (int i = 0;i < Utils.getAnimationDefinitionsSize();i++) {
//			for (int remId = 0;remId < Utils.getRenderAnimDefinitionsSize();remId++) {
//				RenderAnimDefinitions rdef = RenderAnimDefinitions.getRenderAnimDefinitions(remId);
//				if (rdef != null && rdef.containsAnimation(i))
//					continue anim;
//			}
//			AnimationDefinitions def = AnimationDefinitions.getAnimationDefinitions(i);
//			if (def.anIntArray5923 != null && def.anIntArray5923[0] == anim.anIntArray5923[0]) {
//				relatedDefs.add(def);
//			}
//		}
//		System.out.println(relatedDefs);
//		System.out.println("Found " + relatedDefs.size() + " animations for " + npc.name + " (" + npc.id + ")");
//		System.out.println(Arrays.toString(relatedDefs.stream().map(def -> def.id + " (" + (def.getEmoteTime()/1000.0) + " secs)").toArray()));
	}

}
