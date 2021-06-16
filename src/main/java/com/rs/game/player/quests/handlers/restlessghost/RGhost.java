package com.rs.game.player.quests.handlers.restlessghost;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class RGhost extends NPC {

	public RGhost(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 3 && super.withinDistance(tile, distance);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(457) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new RGhost(npcId, tile);
		}
	};
}
