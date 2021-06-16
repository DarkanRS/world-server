package com.rs.game.npc.godwars.zaros.attack;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class Siphon implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.killBloodReavers();
		nex.setNextForceTalk(new ForceTalk("A siphon will solve this!"));
		nex.playSound(3317, 2);
		nex.setNextAnimation(new Animation(6948));
		nex.setNextSpotAnim(new SpotAnim(1201));
		nex.setTempB("siphoning", true);
		int bloodReaverSize = NPCDefinitions.getDefs(13458).size;
		int respawnedBloodReaverCount = 0;
		int maxMinions = Utils.getRandomInclusive(3);
		if (maxMinions != 0) {
			int[][] dirs = Utils.getCoordOffsetsNear(bloodReaverSize);
			for (int dir = 0; dir < dirs[0].length; dir++) {
				final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir], target.getY() + dirs[1][dir], target.getPlane()));
				if (World.floorAndWallsFree(tile, bloodReaverSize)) {
					nex.getBloodReavers()[respawnedBloodReaverCount++] = new NPC(13458, tile, true);
					if (respawnedBloodReaverCount == maxMinions)
						break;
				}
			}
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				nex.setTempB("siphoning", false);
			}
		}, 9);
		return nex.getAttackSpeed();
	}

}
