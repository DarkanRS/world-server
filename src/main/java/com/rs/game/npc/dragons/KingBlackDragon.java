package com.rs.game.npc.dragons;

import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KingBlackDragon extends NPC {

	public KingBlackDragon(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setLureDelay(3000);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
	}

	public static boolean atKBD(WorldTile tile) {
		if ((tile.getX() >= 2250 && tile.getX() <= 2292) && (tile.getY() >= 4675 && tile.getY() <= 4710))
			return true;
		return false;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(50, 2642) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new KingBlackDragon(npcId, tile, false);
		}
	};
}
