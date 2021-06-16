package com.rs.game.player.content.world;

import com.rs.Settings;
import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class TownCrier extends NPC {

	public TownCrier(int id, WorldTile tile) {
		super(id, tile);
	}
	
	@Override
	public void processNPC() {
		if (Settings.getConfig().getLoginMessage() != null && Utils.random(100) == 0)
			setNextForceTalk(new ForceTalk(Settings.getConfig().getLoginMessage()));
		super.processNPC();
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6135, 6136, 6137, 6138, 6139) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new TownCrier(npcId, tile);
		}
	};
}
