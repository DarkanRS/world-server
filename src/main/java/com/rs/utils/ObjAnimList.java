package com.rs.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.SpotAnimDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

public class ObjAnimList {
	
	private static Set<Integer> USED = new HashSet<>();
	private static List<Integer> OBJECT_ANIMS = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		
		init();
		
		System.out.println(OBJECT_ANIMS.size());
	}
	
	public static boolean inited() {
		return USED.size() > 0;
	}
	
	public static boolean isUsed(int anim) {
		return USED.contains(anim);
	}
	
	public static void init() {
		for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
			NPCDefinitions npcDef = NPCDefinitions.getDefs(npcId);
			if (npcDef.basId != -1) {
				if (BASDefinitions.getDefs(npcDef.basId).standAnimation != -1) {
					if (AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds != null) {
						int skeleton = AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds[0];
						for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
							AnimationDefinitions check = AnimationDefinitions.getDefs(i);
							if (check == null)
								continue;
							if (check.frameSetIds == null || check.frameSetIds[0] == -1)
								continue;
							if (check.frameSetIds[0] == skeleton) {
								USED.add(i);
							}
						}
					}
				}
			}
		}
		
		for (int spotAnimId = 0;spotAnimId < Utils.getSpotAnimDefinitionsSize();spotAnimId++) {
			SpotAnimDefinitions defs = SpotAnimDefinitions.getDefs(spotAnimId);
			if (defs.animationId != -1) {
				USED.add(defs.animationId);
			}
		}
		
		for (int objectId = 0;objectId < Utils.getObjectDefinitionsSize();objectId++) {
			ObjectDefinitions defs = ObjectDefinitions.getDefs(objectId);
			if (defs.animations != null) {
				for (int animId : defs.animations) {
					USED.add(animId);
				}
			}
		}
		
		for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
			if (!USED.contains(i)) {
				OBJECT_ANIMS.add(i);
			}
		}
	}

}
