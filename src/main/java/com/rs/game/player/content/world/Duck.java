package com.rs.game.player.content.world;

import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Duck extends NPC {

	public Duck(int id, WorldTile tile) {
		super(id, tile);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		if (Utils.random(50) == 0)
			setNextForceTalk(new ForceTalk("Quack!"));
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler("Duck", "Drake") {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Duck(npcId, tile);
		}
	};
}
