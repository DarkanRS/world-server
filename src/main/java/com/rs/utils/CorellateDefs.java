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
