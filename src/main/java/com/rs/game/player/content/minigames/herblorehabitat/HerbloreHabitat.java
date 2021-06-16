package com.rs.game.player.content.minigames.herblorehabitat;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class HerbloreHabitat {
	//inter 72 jadinko methods
	
	public static final int REGION_ID = 11821;

	@ServerStartupEvent
	public static void initUpdateTask() {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				for (Player player : World.getPlayersInRegion(REGION_ID)) {
					if (player.hasStarted() && !player.hasFinished()) {
						JadinkoType.updateGroup(player, JadinkoType.COMMON);
						JadinkoType.updateGroup(player, JadinkoType.IGNEOUS, JadinkoType.AQUATIC);
						JadinkoType.updateGroup(player, JadinkoType.CANNIBAL, JadinkoType.CARRION);
						JadinkoType.updateGroup(player, JadinkoType.AMPHIBIOUS, JadinkoType.DRACONIC);
						JadinkoType.updateGroup(player, JadinkoType.SARADOMIN, JadinkoType.GUTHIX, JadinkoType.ZAMORAK);
					}
				}
			}
		}, 25, 25);
	}
	
}
